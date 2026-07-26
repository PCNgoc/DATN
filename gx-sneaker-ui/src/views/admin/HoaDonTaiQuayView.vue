<script setup>
import { onMounted, ref } from "vue";
import {exportPdf, getHoaDonTaiQuay} from "@/services/hoaDonService"
import { getChiTietHoaDon } from "@/services/hoaDonService";
const hoaDons = ref([]);

const keyword = ref("");
const loadHoaDon = async () => {

  const res = await getHoaDonTaiQuay(
    keyword.value,
    trangThaiThanhToan.value,
    tuNgay.value,
    denNgay.value

  );

  hoaDons.value = res.data;

}
const selectedHoaDon = ref(null);

const chiTietHoaDon = ref([]);
const xemChiTiet = async (hd) => {


  selectedHoaDon.value = hd;

  const res = await getChiTietHoaDon(hd.id);

  chiTietHoaDon.value = res.data;

}
const formatDate = (date) => {

  if (!date) return "";

  return new Date(date).toLocaleString("vi-VN");

}

const formatMoney = (money) => {

  if (!money) return "0 đ";

  return new Intl.NumberFormat("vi-VN").format(money) + " đ";

}

const inHoaDon = async () => {

  if (!selectedHoaDon.value) {
    alert("Vui lòng chọn hóa đơn");
    return;
  }

  try {

    const response = await exportPdf(selectedHoaDon.value.id);

    const blob = new Blob(
      [response.data],
      { type: "application/pdf" }
    );

    const url = window.URL.createObjectURL(blob);

    window.open(url);

  } catch (e) {

    console.log(e);

    alert("Không thể in hóa đơn");

  }

}
const isRefreshing = ref(false)
const refreshHoaDon = async () => {

  isRefreshing.value = true

  // Xóa tìm kiếm
  keyword.value = ""

  // Xóa trạng thái thanh toán
  trangThaiThanhToan.value = ""

  // Xóa khoảng ngày
  tuNgay.value = ""
  denNgay.value = ""

  // Bỏ chọn hóa đơn
  selectedHoaDon.value = null

  // Xóa chi tiết hóa đơn
  chiTietHoaDon.value = []

  // Load lại danh sách
  await loadHoaDon()

  setTimeout(() => {
    isRefreshing.value = false
  }, 300)

}


const trangThaiThanhToan = ref("")
const tuNgay = ref("")
const denNgay = ref("")

onMounted(() => {

  loadHoaDon();

});

</script>
<template>

  <div class="container-fluid">

    <div class="row">

      <!-- DANH SÁCH HÓA ĐƠN -->
      <div class="col-lg-8">

        <!-- Tiêu đề -->
        <h3 class="mb-3">
          Quản lý hóa đơn bán tại quầy
        </h3>

        <!-- Thanh công cụ -->
        <div class="toolbar">
        <div class="d-flex align-items-center gap-2 mb-4">

          <!-- Ô tìm kiếm -->
          <input
            v-model="keyword"
            class="form-control"
            placeholder="Tìm theo mã HĐ / khách hàng / SĐT..."
            style="width:300px"
            @keyup.enter="loadHoaDon"
          >

          <!--          Trạng thái-->
          <select
            v-model="trangThaiThanhToan"
            class="form-select"
            style="width:220px"
            @change="loadHoaDon"
          >
            <option value="">Tất cả</option>
            <option value="CHUA_THANH_TOAN">Chưa thanh toán</option>
            <option value="DA_THANH_TOAN">Đã thanh toán</option>
          </select>



          <!-- Nút tìm kiếm -->
          <button
            class="btn btn-primary"
            @click="loadHoaDon"
          >
            <i class="bi bi-search"></i>
            Tìm kiếm
          </button>



          <!-- Nút làm mới -->
          <button
            class="btn btn-outline-secondary"
            @click="refreshHoaDon"
            title="Làm mới"
          >
            <i
              class="bi bi-arrow-clockwise"
              :class="{ rotate: isRefreshing }"
            ></i>
          </button>

        </div>
        <div class="d-flex gap-2 mt-2">

          <input
            type="date"
            class="form-control"
            v-model="tuNgay"
            @change="loadHoaDon"
          />

          <input
            type="date"
            class="form-control"
            v-model="denNgay"
            @change="loadHoaDon"
          />

        </div>
        </div>


        <table class="table table-bordered table-hover align-middle">

          <thead class="table-dark">

          <tr>

            <th>Mã HĐ</th>

            <th>Khách hàng</th>

            <th>Số Điện thoại</th>

            <th>Ngày tạo</th>

            <th>Tổng tiền</th>

            <th>Thanh toán</th>

            <th width="100"></th>

          </tr>

          </thead>

          <tbody>

          <tr
            v-for="hd in hoaDons"
            :key="hd.id"
            :class="{ 'table-primary': selectedHoaDon?.id === hd.id }"
          >

            <td>{{ hd.maHoaDon }}</td>

            <td>
              {{ hd.tenKhachHang || "Khách lẻ" }}
            </td>

            <td>
              {{ hd.soDienThoai || "Không có " }}
            </td>

            <td>
              {{ formatDate(hd.ngayTao) }}
            </td>

            <td class="fw-bold text-danger">
              {{ formatMoney(hd.tongTienThanhToan) }}
            </td>

            <td>
  <span
    :class="
      hd.trangThaiThanhToan === 'DA_THANH_TOAN'
        ? 'status-paid'
        : 'status-unpaid'
    "
  >
    {{
      hd.trangThaiThanhToan === 'DA_THANH_TOAN'
        ? 'Đã thanh toán'
        : 'Chưa thanh toán'
    }}
  </span>
            </td>

            <td>

              <button
                class="btn btn-primary btn-sm"
                @click="xemChiTiet(hd)"
              >

                Chi tiết

              </button>

            </td>

          </tr>

          </tbody>

        </table>

      </div>

      <!-- CHI TIẾT -->
      <div class="col-lg-4">

        <div class="card shadow">

          <div class="card-header fw-bold">

            Chi tiết hóa đơn

          </div>

          <div class="card-body">

            <template v-if="selectedHoaDon">

              <div class="text-center mb-3">

                <h5 class="fw-bold mb-0">
                  HÓA ĐƠN BÁN TẠI QUẦY
                </h5>

              </div>

              <div class="mb-2">

                <strong>Mã hóa đơn:</strong>

                {{ selectedHoaDon.maHoaDon }}

              </div>

              <div class="mb-2">

                <strong>Ngày tạo:</strong>

                {{ formatDate(selectedHoaDon.ngayTao) }}

              </div>

              <div class="mb-2">

                <strong>Khách hàng:</strong>

                {{ selectedHoaDon.tenKhachHang || "Khách lẻ" }}

              </div>

              <div class="mb-2">

                <strong>Số điện thoại:</strong>

                {{ selectedHoaDon.soDienThoai || "Không có" }}

              </div>

              <div class="mb-3">

                <strong>Người tạo:</strong>

                {{ selectedHoaDon.tenNhanVien || selectedHoaDon.nguoiTao || "Nhân viên" }}

              </div>

              <hr>

              <table class="table table-bordered table-sm">

                <thead class="table-light">

                <tr>

                  <th>Sản phẩm</th>

                  <th width="55">SL</th>

                  <th width="120">Đơn giá</th>

                  <th width="130">Thành tiền</th>

                </tr>

                </thead>

                <tbody>

                <tr
                  v-for="ct in chiTietHoaDon"
                  :key="ct.id"
                >

                  <td>

                    {{ ct.tenSanPham }}

                    <br>

                    <small class="text-secondary">

                      {{ ct.mauSac }}

                      - Size {{ ct.size }}

                    </small>

                  </td>

                  <td class="text-center">

                    {{ ct.soLuong }}

                  </td>

                  <td class="text-end">

                    {{ formatMoney(ct.donGia) }}

                  </td>

                  <td class="text-end text-danger fw-bold">

                    {{ formatMoney(ct.thanhTien) }}

                  </td>

                </tr>

                </tbody>

              </table>

              <hr>

              <div class="d-flex justify-content-between mb-2">

                <span>Tổng tiền hàng</span>

                <strong>

                  {{ formatMoney(selectedHoaDon.tongTienHang) }}

                </strong>

              </div>

              <div class="d-flex justify-content-between mb-2">

                <span>Giảm giá</span>

                <strong>

                  {{ formatMoney(selectedHoaDon.soTienGiam) }}

                </strong>

              </div>

              <div class="d-flex justify-content-between border-top pt-3 fs-5">

                <strong>Thành tiền</strong>

                <strong class="text-danger">

                  {{ formatMoney(selectedHoaDon.tongTienThanhToan) }}

                </strong>

              </div>

              <hr>

              <div class="mb-3">

                <strong>Trạng thái:</strong>

                <span
                  :style="{
      backgroundColor:
        selectedHoaDon.trangThaiThanhToan === 'DA_THANH_TOAN'
          ? '#198754'
          : '#ffc107',
      color:
        selectedHoaDon.trangThaiThanhToan === 'DA_THANH_TOAN'
          ? '#fff'
          : '#000',
      padding: '4px 8px',
      borderRadius: '20px',
      fontWeight: '600',
      marginLeft: '10px',
      display: 'inline-block'
    }"
                >
    {{
                    selectedHoaDon.trangThaiThanhToan === "DA_THANH_TOAN"
                      ? "Đã thanh toán"
                      : "Chưa thanh toán"
                  }}
  </span>

              </div>

              <div class="mb-3">

                <strong>Phương thức:</strong>

                <span
                  :style="{
      backgroundColor:
        selectedHoaDon.phuongThucThanhToan === 'TIEN_MAT'
          ? '#0d6efd'
          : '#6f42c1',
      color: '#fff',
      padding: '4px 8px',
      borderRadius: '20px',
      fontWeight: '600',
      marginLeft: '10px',
      display: 'inline-block'
    }"
                >
    {{
                    selectedHoaDon.phuongThucThanhToan === "TIEN_MAT"
                      ? "Tiền mặt"
                      : selectedHoaDon.phuongThucThanhToan === "VNPAY"
                        ? "Thanh toán QR VNPay"
                        : "Chưa chọn"
                  }}
  </span>

              </div>

              <button
                class="btn btn-success w-100"
                @click="inHoaDon"
              >

                <i class="bi bi-printer me-2"></i>

                In hóa đơn

              </button>

            </template>

            <template v-else>

              <div class="text-center text-secondary py-5">

                Chọn một hóa đơn để xem chi tiết

              </div>

            </template>

          </div>

        </div>

      </div>

    </div>

  </div>

</template>

<style scoped>
.rotate{
  animation: spin .5s linear;
}

@keyframes spin{
  from{
    transform: rotate(0deg);
  }
  to{
    transform: rotate(360deg);
  }
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  margin-bottom: 15px;
}
.toolbar input[type="text"],
.toolbar .form-control {
  height: 42px;
  border-radius: 10px;
}

.toolbar .form-select {
  height: 42px;
  border-radius: 10px;
}
.toolbar .btn {
  height: 42px;
  border-radius: 10px;
  padding: 0 18px;
}

</style>

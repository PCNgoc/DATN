<script setup>
import { onMounted, ref } from "vue";
import { getHoaDonTaiQuay } from "@/services/hoaDonService"
import { getChiTietHoaDon } from "@/services/hoaDonService";
const hoaDons = ref([]);

const loadHoaDon = async () => {

  const res = await getHoaDonTaiQuay();

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
onMounted(() => {

  loadHoaDon();

});

</script>
<template>

  <div class="container-fluid">

    <div class="row">

      <!-- DANH SÁCH HÓA ĐƠN -->
      <div class="col-lg-8">

        <h3 class="mb-4">
          Quản lý hóa đơn bán tại quầy
        </h3>

        <table class="table table-bordered table-hover align-middle">

          <thead class="table-dark">

          <tr>

            <th>Mã HĐ</th>

            <th>Khách hàng</th>

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
              {{ formatDate(hd.ngayTao) }}
            </td>

            <td class="fw-bold text-danger">
              {{ formatMoney(hd.tongTienThanhToan) }}
            </td>

            <td>

              <span
                class="badge"
                :class="hd.trangThaiThanhToan === 'DA_THANH_TOAN'
                  ? 'bg-success'
                  : 'bg-warning text-dark'"
              >

                {{ hd.trangThaiThanhToan }}

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

                {{ selectedHoaDon.soDienThoai || "Null" }}

              </div>

              <div class="mb-3">

                <strong>Nhân viên:</strong>

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

              <div class="mb-2">

                <strong>Trạng thái:</strong>

                <span
                  class="badge ms-2"
                  :class="selectedHoaDon.trangThaiThanhToan==='DA_THANH_TOAN'
                    ? 'bg-success'
                    : 'bg-warning text-dark'"
                >

                  {{
                    selectedHoaDon.trangThaiThanhToan === 'DA_THANH_TOAN'
                      ? 'Đã thanh toán'
                      : 'Chưa thanh toán'
                  }}

                </span>

              </div>

              <div class="mb-3">

                <strong>Phương thức:</strong>

                {{
                  selectedHoaDon.phuongThucThanhToan === "TIEN_MAT"
                    ? "Tiền mặt"
                    : selectedHoaDon.phuongThucThanhToan
                }}

              </div>

              <button
                class="btn btn-success w-100"
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

</style>

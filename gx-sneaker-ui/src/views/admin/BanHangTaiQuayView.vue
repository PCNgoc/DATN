<script setup>
import { ref, onMounted, computed,watch } from "vue";

import {
  taoHoaDonCho,
  getHoaDonCho,
  themSanPham,
  getChiTietHoaDon,
  doiKhachHang
} from "@/services/hoaDonService.js";

import {
  getBanTaiQuay
} from "@/services/chiTietSanPhamService";

import {

  capNhatSoLuong

} from "@/services/hoaDonService.js";

import {
  getKhachHangBanTaiQuay
} from "@/services/khachHangService";

import {

  xoaSanPham

} from "@/services/hoaDonService";
import {

  thanhToanTienMat

} from "@/services/hoaDonService";

//=====================
// HÓA ĐƠN CHỜ
//=====================

const hoaDonCho = ref([]);
const selectedHoaDon = ref(null);

const loadHoaDonCho = async () => {
  try {
    const res = await getHoaDonCho();
    hoaDonCho.value = res.data;
  } catch (e) {
    console.log(e);
  }
};

const taoHoaDon = async () => {
  await taoHoaDonCho();
  await loadHoaDonCho();
};

//=====================
// DANH SÁCH SẢN PHẨM
//=====================

const sanPham = ref([]);
const keyword = ref("");

const loadSanPham = async () => {
  try {
    const res = await getBanTaiQuay();
    sanPham.value = res.data;
  } catch (e) {
    console.log(e);
  }
};

const sanPhamFilter = computed(() => {

  if (!keyword.value) {
    return sanPham.value;
  }

  return sanPham.value.filter(sp =>
    sp.sanPham?.tenSanPham
      ?.toLowerCase()
      .includes(keyword.value.toLowerCase())
  );

});

//=====================
// GIỎ HÀNG
//=====================

const gioHang = ref([]);

const loadGioHang = async () => {

  if (!selectedHoaDon.value) {

    gioHang.value = [];

    return;

  }

  const res = await getChiTietHoaDon(selectedHoaDon.value.id);

  gioHang.value = res.data;

}

const chonHoaDon = async (hd) => {

  selectedHoaDon.value = hd;

  await loadGioHang();

};

//=====================
// THÊM SẢN PHẨM
//=====================

const themSanPhamVaoHoaDon = async (sp) => {

  if (!selectedHoaDon.value) {

    alert("Vui lòng chọn hóa đơn trước!");

    return;

  }

  try {

    await themSanPham(selectedHoaDon.value.id, {

      chiTietSanPhamId: sp.id,

      soLuong: 1

    });

    // Load lại giỏ hàng
    await loadGioHang();

    // Load lại danh sách hóa đơn
    await reloadHoaDonDangChon();

    // Load lại tồn kho
    await loadSanPham();

  } catch (e) {

    console.error(e);

    alert("Không thể thêm sản phẩm");

  }

};

//=====================
// FORMAT
//=====================

const formatMoney = (money) => {

  if (!money) return "0 đ";

  return new Intl.NumberFormat("vi-VN").format(money) + " đ";

};

const tangSoLuong = async (item) => {

  await capNhatSoLuong(
    item.id,
    item.soLuong + 1
  );

  await loadGioHang();

  await reloadHoaDonDangChon();

  await loadSanPham();

}

const giamSoLuong = async (item) => {

  if(item.soLuong <= 1) return;

  await capNhatSoLuong(
    item.id,
    item.soLuong - 1
  );

  await loadGioHang();

  await reloadHoaDonDangChon();

  await loadSanPham();

}

const reloadHoaDonDangChon = async () => {

  await loadHoaDonCho();

  selectedHoaDon.value =
    hoaDonCho.value.find(
      hd => hd.id === selectedHoaDon.value.id
    );

}

const khachHangs = ref([]);

const selectedKhachHang = ref(null);

const loadKhachHang = async () => {

  const res = await getKhachHangBanTaiQuay();

  khachHangs.value = res.data;

}

watch(selectedKhachHang, async (val) => {

  if (!selectedHoaDon.value) return;

  await doiKhachHang(
    selectedHoaDon.value.id,
    val?.id
  );

  await loadHoaDonCho();

  selectedHoaDon.value =
    hoaDonCho.value.find(
      h => h.id === selectedHoaDon.value.id
    );

});

const xoaKhoiGio = async (item) => {

  if (!confirm("Xóa sản phẩm khỏi hóa đơn?")) return;

  await xoaSanPham(item.id);

  await loadGioHang();

  await loadSanPham();

  await loadHoaDonCho();

  selectedHoaDon.value =
    hoaDonCho.value.find(
      h => h.id === selectedHoaDon.value.id
    );

}

const showThanhToan = ref(false);

const tienKhachDua = ref(0);

const tienThua = computed(() => {

  if (!selectedHoaDon.value) return 0;

  return Math.max(

    0,

    tienKhachDua.value -

    (selectedHoaDon.value.tongTienThanhToan || 0)

  );

});

const xacNhanThanhToan = async () => {

  if (!selectedHoaDon.value) return;

  try {

    await thanhToanTienMat(

      selectedHoaDon.value.id,

      tienKhachDua.value

    );

    alert("Thanh toán thành công");

    showThanhToan.value = false;

    tienKhachDua.value = 0;

    selectedHoaDon.value = null;

    gioHang.value = [];

    await loadHoaDonCho();

    await loadSanPham();

  }

  catch(e){

    alert(e.response?.data?.message || "Thanh toán thất bại");

  }

}

//=====================

onMounted(async () => {

  await loadHoaDonCho();

  await loadSanPham();
  await loadKhachHang();

});
</script>

<template>




  <div class="banhang">

    <!-- Hóa đơn -->

    <div class="invoice-bar">

      <button
        class="btn btn-primary"
        @click="taoHoaDon"
      >
        + Hóa đơn mới
      </button>

      <div class="invoice-list">

        <div
          v-for="hd in hoaDonCho"
          :key="hd.id"
          class="invoice-item"
          :class="{active:selectedHoaDon?.id===hd.id}"
          @click="chonHoaDon(hd)"
        >

          {{ hd.maHoaDon }}

        </div>

      </div>

    </div>

    <div class="main-content">

      <!-- LEFT -->

      <div class="left-panel">


        <div class="product-header">

          <h4>Danh sách sản phẩm</h4>

          <input
            v-model="keyword"
            class="form-control"
            placeholder="Tìm sản phẩm..."
          >

        </div>

        <!-- Card -->

        <div class="product-grid">

          <!-- để nguyên v-for sản phẩm của bạn -->



            <div
              class="product-card"
              v-for="sp in sanPhamFilter"
              :key="sp.id"
            >

              <img
                class="img-fluid rounded mb-3"
                :src="sp.sanPham?.anhDaiDien
      ? '/images/' + sp.sanPham.anhDaiDien
      : 'https://placehold.co/250x250'"
              />

              <h6 class="fw-bold mb-2">
                {{ sp.sanPham?.tenSanPham }}
              </h6>

              <small>

                {{ sp.mauSac?.ten }}

                -

                Size {{ sp.kichThuoc?.size }}

              </small>

              <div class="price text-danger fw-bold fs-5">

                {{ formatMoney(sp.giaBan) }}

              </div>

              <div class="stock text-success mb-2">

                Còn {{ sp.soLuongTon }}

              </div>

              <button
                class="btn btn-success w-100"
                @click="themSanPhamVaoHoaDon(sp)"
              >
                Thêm
              </button>

            </div>



        </div>


      </div>

      <!-- RIGHT -->



      <div class="right-panel">

        <h4>Giỏ hàng</h4>
        <div class="mb-3">

          <label class="fw-bold">

            Khách hàng

          </label>

          <select
            v-model="selectedKhachHang"
            class="form-select"
          >

            <option :value="null">

              Khách lẻ

            </option>

            <option
              v-for="kh in khachHangs"
              :key="kh.id"
              :value="kh"
            >

              {{ kh.hoTen }}

              -

              {{ kh.soDienThoai }}

            </option>

          </select>

        </div>

        <table class="table table-hover align-middle mb-0">

          <thead>

          <tr>

            <th style="width:70px"></th>

            <th style="width:45%">Tên sản phẩm</th>

            <th style="width:70px" class="text-center">
              SL
            </th>

            <th style="width:120px">
              Đơn giá
            </th>

            <th style="width:140px">
              Thành tiền
            </th>
            <th style="width:60px">Xóa</th>

          </tr>

          </thead>

          <tbody>

          <tr
            v-for="item in gioHang"
            :key="item.id"
          >

            <!-- Ảnh -->

            <td>

              <img
                :src="'/images/' + item.anh"
                style="width:55px;height:55px;object-fit:cover"
                class="rounded border"
              >

            </td>

            <!-- Tên -->

            <td>

              <div class="fw-bold">

                {{ item.tenSanPham }}

              </div>

              <div class="text-secondary small">

                {{ item.mauSac }}

                -

                Size {{ item.size }}

              </div>

            </td>

            <!-- SL -->

            <td>

              <div class="quantity-box">

                <button

                  class="btn btn-sm btn-outline-secondary"

                  @click="giamSoLuong(item)"

                >

                  -

                </button>

                <span>

            {{ item.soLuong }}

        </span>

                <button

                  class="btn btn-sm btn-outline-secondary"

                  @click="tangSoLuong(item)"

                >

                  +

                </button>

              </div>

            </td>

            <!-- Đơn giá -->

            <td>

              {{ formatMoney(item.donGia) }}

            </td>

            <!-- Thành tiền -->

            <td class="text-danger fw-bold">

              {{ formatMoney(item.thanhTien) }}

            </td>
            <td>

              <button

                class="btn btn-danger btn-sm"

                @click="xoaKhoiGio(item)"

              >

                🗑

              </button>

            </td>

          </tr>

          </tbody>

        </table>

        <!-- bảng -->

        <!-- table hiện tại -->

        <!-- tổng tiền -->

        <div class="bill-total">

          <div class="d-flex justify-content-between">

            <span>Tạm tính</span>

            <span>
                        {{ formatMoney(selectedHoaDon?.tongTienHang) }}
                    </span>

          </div>

          <div class="d-flex justify-content-between">

            <span>Giảm giá</span>

            <span>
                        {{ formatMoney(selectedHoaDon?.soTienGiam) }}
                    </span>

          </div>

          <div class="d-flex justify-content-between fw-bold fs-5 text-danger">

            <span>Thành tiền</span>

            <span>

                        {{ formatMoney(selectedHoaDon?.tongTienThanhToan) }}

                    </span>

          </div>

        </div>

        <button
          class="btn btn-success w-100 mt-3"
          @click="showThanhToan=true"
        >

          Thanh toán

        </button>

      </div>

    </div>

  </div>

  <div
    v-if="showThanhToan"
    class="popup-bg"
  >

    <div class="popup">

      <h4>Thanh toán tiền mặt</h4>

      <div class="mb-3">

        <label>Tổng tiền</label>

        <input
          class="form-control"
          :disabled="true"
          :value="formatMoney(selectedHoaDon?.tongTienThanhToan)"
        >

      </div>

      <div class="mb-3">

        <label>Khách đưa</label>

        <input
          type="number"
          v-model="tienKhachDua"
          class="form-control"
        >

      </div>

      <div class="mb-3">

        <label>Tiền thừa</label>

        <input
          class="form-control"
          :disabled="true"
          :value="formatMoney(tienThua)"
        >

      </div>

      <div class="d-flex gap-2">

        <button
          class="btn btn-secondary flex-fill"
          @click="showThanhToan=false"
        >

          Hủy

        </button>

        <button
          class="btn btn-success flex-fill"
          @click="xacNhanThanhToan"
        >

          Xác nhận

        </button>

      </div>

    </div>

  </div>

</template>

<style scoped>

.banhang{

  display:flex;

  flex-direction:column;

  height:100%;

  gap:15px;

}

/* hóa đơn */

.invoice-bar{

  background:white;

  padding:15px;

  border-radius:12px;

  box-shadow:0 2px 8px rgba(0,0,0,.08);

}

.invoice-list{

  display:flex;

  gap:10px;

  overflow-x:auto;

  margin-top:12px;

}

.invoice-item{

  min-width:140px;

  text-align:center;

  padding:10px;

  border-radius:10px;

  background:#f4f4f4;

  cursor:pointer;

  font-weight:600;

}

.invoice-item.active{

  background:#0d6efd;

  color:white;

}

/* nội dung */

.main-content{

  display:flex;

  gap:15px;

  flex:1;

}

.left-panel{

  flex:1;

  background:white;

  border-radius:12px;

  padding:15px;

}

.right-panel{

  width:560px;

  background:white;

  border-radius:12px;

  padding:15px;

}

/* header */

.product-header{

  display:flex;

  justify-content:space-between;

  align-items:center;

  margin-bottom:20px;

}

.product-header input{

  width:280px;

}

/* sản phẩm */

.product-grid{

  display:grid;

  grid-template-columns: repeat(2, 1fr);
  gap:18px;

}

/* card */

.product-card{

  transition:.2s;

}

.product-card:hover{

  transform:translateY(-4px);

  box-shadow:0 6px 18px rgba(0,0,0,.12);

}

/* tổng tiền */

.bill-total{

  margin-top:20px;

  padding-top:20px;

  border-top:1px solid #ddd;

}

.product-card{

  border-radius:12px;

  border:1px solid #eee;

  padding:15px;

  background:white;

}

.product-card img{

  width:100%;

  height:170px;

  object-fit:cover;

}

.product-card:hover{

  box-shadow:0 8px 18px rgba(0,0,0,.12);

}

.invoice-item{

  transition:.2s;

}

.invoice-item:hover{

  background:#dbeafe;

}

.right-panel table td{

  vertical-align:middle;

}

.quantity-box{

  display:flex;

  align-items:center;

  justify-content:center;

  gap:8px;

}

.quantity-box span{

  width:30px;

  text-align:center;

  font-weight:bold;

}

.left-panel{

  display:flex;

  flex-direction:column;

  height:calc(100vh - 220px);

}

.product-grid{

  flex:1;

  overflow-y:auto;

  display:grid;

  grid-template-columns:repeat(2,1fr);

  gap:18px;

  padding-right:8px;

}

.popup-bg{

  position:fixed;

  top:0;

  left:0;

  right:0;

  bottom:0;

  background:rgba(0,0,0,.45);

  display:flex;

  justify-content:center;

  align-items:center;

  z-index:999;

}

.popup{

  width:420px;

  background:white;

  padding:25px;

  border-radius:12px;

}

</style>

<script setup>
import { ref, onMounted } from "vue";
import { getHoaDonByKhachHang } from "@/services/HoaDonService";
import { huyHoaDon } from "@/services/HoaDonService";
import Swal from "sweetalert2";

const orders = ref([]);

const formatMoney = (value) =>
  Number(value).toLocaleString("vi-VN") + " đ";

const formatDate = (value) =>
  new Date(value).toLocaleString("vi-VN");

const getStatusText = (status) => {
  const map = {
    CHO_XAC_NHAN: "Chờ xác nhận",
    DA_XAC_NHAN: "Đã xác nhận",
    CHO_THANH_TOAN: "Chờ thanh toán",
    CHUA_THANH_TOAN: "Chưa thanh toán",
    DA_THANH_TOAN: "Đã thanh toán",
    DANG_GIAO: "Đang giao",
    HOAN_THANH: "Hoàn thành",
    DA_HUY: "Đã hủy",
  }

  return map[status] || status
}

const getStatusClass = (status) => {
  const map = {
    CHO_XAC_NHAN: "warning",
    DA_XAC_NHAN: "primary",
    CHO_THANH_TOAN: "secondary",
    CHUA_THANH_TOAN: "dark",
    DA_THANH_TOAN: "info",
    DANG_GIAO: "success",
    HOAN_THANH: "completed",
    DA_HUY: "danger",
  }

  return map[status] || "secondary"
}

const getStatusIcon = (status) => {
  const map = {
    CHO_XAC_NHAN: "bi bi-hourglass-split",
    DA_XAC_NHAN: "bi bi-check2-circle",
    CHO_THANH_TOAN: "bi bi-wallet2",
    CHUA_THANH_TOAN: "bi bi-cash-stack",
    DA_THANH_TOAN: "bi bi-credit-card-2-front",
    DANG_GIAO: "bi bi-truck",
    HOAN_THANH: "bi bi-patch-check-fill",
    DA_HUY: "bi bi-x-circle-fill",
  }

  return map[status] || "bi bi-circle"
}
const huyDon = async (id) => {

  const { value: lyDo } = await Swal.fire({
    title: "Lý do hủy đơn",
    input: "select",
    inputOptions: cancelReasons.reduce((obj, item) => {
      obj[item] = item;
      return obj;
    }, {}),
    inputPlaceholder: "Chọn lý do",
    showCancelButton: true,
    confirmButtonText: "Tiếp tục",
    cancelButtonText: "Đóng"
  });

  if (!lyDo) return;

  let finalReason = lyDo;

  // Nếu chọn lý do khác thì nhập thêm
  if (lyDo === "Lý do khác") {

    const { value: text } = await Swal.fire({
      title: "Nhập lý do hủy đơn",
      input: "textarea",
      inputPlaceholder: "Vui lòng nhập lý do...",
      showCancelButton: true,
      confirmButtonText: "Xác nhận",
      cancelButtonText: "Đóng"
    });

    if (!text || !text.trim()) {
      Swal.fire(
        "Thông báo",
        "Vui lòng nhập lý do hủy đơn!",
        "warning"
      );
      return;
    }

    finalReason = text.trim();
  }

  try {

    await huyHoaDon(id, {
      nguoiThucHien: "Khách hàng",
      ghiChu: finalReason
    });

    const currentUser = JSON.parse(localStorage.getItem("user"));

    const res = await getHoaDonByKhachHang(currentUser.id);

    orders.value = res.data;

    Swal.fire({
      icon: "success",
      title: "Thành công",
      text: "Đơn hàng đã được hủy.",
      timer: 1500,
      showConfirmButton: false
    });

  } catch (e) {

    console.error(e);

    Swal.fire({
      icon: "error",
      title: "Không thể hủy đơn",
      text:
        e.response?.data?.message ||
        e.response?.data ||
        "Đã xảy ra lỗi."
    });

  }

}

const cancelReasons = [
  "Đặt nhầm sản phẩm",
  "Muốn đổi sản phẩm khác",
  "Muốn thay đổi địa chỉ nhận hàng",
  "Muốn thay đổi phương thức thanh toán",
  "Tìm được giá tốt hơn",
  "Không còn nhu cầu mua",
  "Lý do khác"
];

onMounted(async () => {
  try {
    const user = JSON.parse(localStorage.getItem("user"));

    if (!user) {
      console.log("Chưa đăng nhập");
      return;
    }

    console.log("User hiện tại:", user);
    console.log("ID khách hàng:", user.id);

    const res = await getHoaDonByKhachHang(user.id);

    orders.value = res.data;

    console.log("Danh sách hóa đơn:", orders.value);

  } catch (e) {
    console.log(e);
  }
});
</script>
<template>
  <div class="order-page">

    <h1 class="title">📦 Đơn hàng của tôi</h1>

    <div
      v-if="orders.length"
      v-for="order in orders"
      :key="order.id"
      class="order-card"
    >

      <!-- Header -->
      <div class="top">

        <div>
          <h3>{{ order.maHoaDon }}</h3>
          <span class="date">
            {{ formatDate(order.ngayDatHang) }}
          </span>
        </div>

        <span
          class="status-pill"
          :class="getStatusClass(order.trangThai)"
        >
    <i
      :class="getStatusIcon(order.trangThai)"
      class="me-1"
    ></i>

    {{ getStatusText(order.trangThai) }}
</span>

      </div>

      <div class="divider"></div>

      <!-- Danh sách sản phẩm -->
      <div
        class="product"
        v-for="item in order.items"
        :key="item.chiTietSanPhamId"
      >

        <img
          :src="'/images/' + item.image"
          alt=""
        >

        <div class="info">

          <h3>{{ item.productName }}</h3>

          <p>
            {{ item.color }} |
            Size {{ item.size }}
          </p>

          <p>Số lượng: x{{ item.quantity }}</p>

          <span class="item-price">
            {{ formatMoney(item.price) }}
          </span>

        </div>

      </div>

      <div class="divider"></div>

      <!-- Footer -->
      <div class="bottom">

        <div>

          <span class="label">
            Tổng tiền
          </span>

          <div class="price">
            {{ formatMoney(order.tongTien) }}
          </div>

        </div>

        <router-link
          :to="`/orders/${order.id}`"
          class="btn-detail"
        >
          Xem chi tiết
        </router-link>

        <button
          v-if="order.trangThai==='CHO_XAC_NHAN'"
          class="btn-cancel"
          @click="huyDon(order.id)"
        >
          Hủy đơn
        </button>

      </div>

    </div>

    <div
      v-else
      class="empty"
    >
      📦 Bạn chưa có đơn hàng nào.
    </div>

  </div>
</template>
<style scoped>

.order-page{
  max-width:1100px;
  margin:auto;
  padding:40px 20px;
  background:#f7f8fa;
  min-height:100vh;
}

.title{
  font-size:32px;
  font-weight:bold;
  margin-bottom:30px;
  color:#111827;
}

.order-card{
  background:#fff;
  border-radius:18px;
  padding:25px;
  margin-bottom:25px;
  transition:.3s;
  box-shadow:0 5px 15px rgba(0,0,0,.08);
}

.order-card:hover{
  transform:translateY(-4px);
  box-shadow:0 15px 35px rgba(0,0,0,.12);
}

.top{
  display:flex;
  justify-content:space-between;
  align-items:center;
}

.top h3{
  margin:0;
  font-size:22px;
}

.date{
  color:#888;
  font-size:14px;
}

.divider{
  height:1px;
  background:#eee;
  margin:20px 0;
}

.product{
  display:flex;
  align-items:center;
  margin-bottom:18px;
}

.product img{
  width:110px;
  height:110px;
  object-fit:cover;
  border-radius:12px;
  border:1px solid #eee;
  margin-right:20px;
}

.info{
  flex:1;
}

.info h3{
  margin-bottom:8px;
  font-size:20px;
}

.info p{
  margin:4px 0;
  color:#666;
}

.item-price{
  color:#e53935;
  font-size:18px;
  font-weight:bold;
}

.bottom{
  display:flex;
  justify-content:space-between;
  align-items:center;
}

.label{
  color:#666;
  font-size:15px;
}

.price{
  color:#e53935;
  font-size:28px;
  font-weight:bold;
  margin-top:6px;
}

.btn-detail{
  padding:12px 25px;
  background:#111827;
  color:#fff;
  text-decoration:none;
  border-radius:12px;
  transition:.3s;
  font-weight:bold;
}

.btn-detail:hover{
  background:#ef4444;
}

.status{
  padding:8px 18px;
  border-radius:30px;
  font-weight:bold;
  font-size:14px;
}

/* Trạng thái */

.status.CHO_XAC_NHAN,
.status.Chờ\ xác\ nhận{
  background:#fff7e6;
  color:#fa8c16;
}

.status.DANG_GIAO,
.status.Đang\ giao{
  background:#e6f4ff;
  color:#1677ff;
}

.status.HOAN_THANH,
.status.HOÀN\ THÀNH,
.status.Hoàn\ thành{
  background:#f6ffed;
  color:#52c41a;
}

.status.DA_HUY,
.status.Đã\ hủy{
  background:#fff1f0;
  color:#ff4d4f;
}

.empty{
  text-align:center;
  padding:120px;
  font-size:24px;
  color:#777;
}

@media(max-width:768px){

  .product{
    flex-direction:column;
    text-align:center;
  }

  .product img{
    margin:0 0 15px;
  }

  .bottom{
    flex-direction:column;
    gap:20px;
  }

  .top{
    flex-direction:column;
    gap:10px;
  }

}


.status-pill{
  display:inline-flex;
  align-items:center;
  gap:6px;
  padding:6px 12px;
  border-radius:20px;
  font-size:13px;
  font-weight:600;
}

.status-pill.warning{
  background:#fff3cd;
  color:#856404;
}

.status-pill.primary{
  background:#cfe2ff;
  color:#084298;
}

.status-pill.secondary{
  background:#e2e3e5;
  color:#41464b;
}

.status-pill.dark{
  background:#d3d3d4;
  color:#1f1f1f;
}

.status-pill.info{
  background:#cff4fc;
  color:#055160;
}

.status-pill.success{
  background:#d1e7dd;
  color:#0f5132;
}

.status-pill.completed{
  background:#198754;
  color:white;
}

.status-pill.danger{
  background:#f8d7da;
  color:#842029;
}


.btn-cancel{
  background:#dc3545;
  color:#fff;
  border:none;
  padding:10px 18px;
  border-radius:8px;
  margin-left:10px;
  cursor:pointer;
  transition:.2s;
}

.btn-cancel:hover{
  background:#bb2d3b;
}


</style>

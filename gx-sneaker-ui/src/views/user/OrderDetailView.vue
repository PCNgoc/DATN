<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { getHoaDonById } from "@/services/HoaDonService";



const route = useRoute();

const order = ref(null);

const loading = ref(true);
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
const formatMoney = (money) => {
  return Number(money).toLocaleString("vi-VN");
};

const formatDate = (date) => {
  if (!date) return "";

  return new Date(date).toLocaleString("vi-VN");
};
const orderSteps = [
  {
    key: "CHO_XAC_NHAN",
    title: "Chờ xác nhận",
    icon: "fas fa-receipt"
  },
  {
    key: "DA_XAC_NHAN",
    title: "Đã xác nhận",
    icon: "fas fa-check-circle"
  },
  {
    key: "DANG_GIAO",
    title: "Đang giao",
    icon: "fas fa-truck"
  },
  {
    key: "HOAN_THANH",
    title: "Hoàn thành",
    icon: "fas fa-box-open"
  }
];

const getCurrentStep = () => {

  if (!order.value) return 0;

  switch (order.value.trangThai) {

    case "CHO_XAC_NHAN":
      return 0;

    case "DA_XAC_NHAN":
      return 1;

    case "DANG_GIAO":
      return 2;

    case "HOAN_THANH":
      return 3;

    default:
      return 0;
  }

};

onMounted(async () => {
  try {
    const id = route.params.id;

    const res = await getHoaDonById(id);

    order.value = res.data;
  } catch (e) {
    console.error(e);
    order.value = null;
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="order-detail">

    <h2>Chi tiết đơn hàng</h2>

    <div v-if="loading">
      Đang tải...
    </div>

    <div v-else-if="order">

      <div class="info">

        <p>
          <b>Mã đơn:</b>
          {{ order.maHoaDon }}
        </p>

        <p>
          <b>Mã vận đơn:</b>
          {{ order.maVanDon || "Chưa có" }}
        </p>

        <p>
          <b>Loại đơn:</b>
          {{ order.loaiDon }}
        </p>

        <p>
          <b>Phương thức thanh toán:</b>
          {{ order.phuongThucThanhToan }}
        </p>

        <p>
          <b>Trạng thái thanh toán:</b>
          {{ getStatusText(order.trangThaiThanhToan) }}
        </p>

        <p v-if="order.maPhieuGiamGia">
          <b>Mã giảm giá:</b>
          {{ order.maPhieuGiamGia }}
        </p>

        <p v-if="order.tenPhieuGiamGia">
          <b>Tên phiếu:</b>
          {{ order.tenPhieuGiamGia }}
        </p>

        <p>
          <b>Ngày đặt:</b>
          {{ formatDate(order.ngayDatHang) }}
        </p>

        <p>
          <b>Trạng thái:</b>

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
        </p>

        <p>
          <b>Người nhận:</b>
          {{ order.tenNguoiNhan }}
        </p>

        <p>
          <b>Số điện thoại:</b>
          {{ order.soDienThoai }}
        </p>

        <p>
          <b>Địa chỉ:</b>
          {{ order.diaChi }}
        </p>

      </div>


      <!-- Timeline trạng thái -->
      <div
        v-if="order.trangThai !== 'DA_HUY'"
        class="order-timeline"
      >
        <div
          v-for="(step,index) in orderSteps"
          :key="step.key"
          class="timeline-item"
          :class="{ active: index <= getCurrentStep() }"
        >

          <div class="timeline-circle">
            <i :class="step.icon"></i>
          </div>

          <div class="timeline-title">
            {{ step.title }}
          </div>

        </div>
      </div>

      <!-- Nếu đơn đã hủy -->
      <div
        v-else
        class="cancel-box"
      >
        <i class="fas fa-ban me-2 text-danger"></i>

        <b>Đơn hàng đã hủy</b>

        <div class="mt-2">
          <strong>Lý do:</strong>

          {{ order.lyDoHuy }}
        </div>
      </div>

      <table class="table">

        <thead>

        <tr>

          <th>Ảnh</th>

          <th>Sản phẩm</th>

          <th>Màu</th>

          <th>Size</th>

          <th>Số lượng</th>

          <th>Đơn giá</th>

          <th>Thành tiền</th>

        </tr>

        </thead>

        <tbody>

        <tr
          v-for="item in order.items"
          :key="item.chiTietSanPhamId"
        >

          <td>

            <img
              :src="`/images/${item.image}`"
              width="80"
            />

          </td>

          <td>{{ item.productName }}</td>

          <td>{{ item.color }}</td>

          <td>{{ item.size }}</td>

          <td>{{ item.quantity }}</td>

          <td>{{ formatMoney(item.price) }} đ</td>

          <td>{{ formatMoney(item.total) }} đ</td>

        </tr>

        </tbody>

      </table>

      <div class="invoice-summary">

        <div class="summary-row">
          <span>Tổng tiền hàng</span>
          <span>{{ formatMoney(order.tongTienHang) }} đ</span>
        </div>

        <div
          class="summary-row"
          v-if="order.maPhieuGiamGia"
        >
    <span>
      Giảm giá ({{ order.maPhieuGiamGia }})
    </span>

          <span class="discount">
      - {{ formatMoney(order.soTienGiam) }} đ
    </span>
        </div>

        <div
          class="summary-row"
          v-else
        >
          <span>Giảm giá</span>
          <span>0 đ</span>
        </div>

        <div class="summary-row">
          <span>Phí vận chuyển</span>
          <span>{{ formatMoney(order.phiVanChuyen) }} đ</span>
        </div>

        <hr>

        <div class="summary-total">
          <span>Tổng thanh toán</span>

          <span>
      {{ formatMoney(order.tongTien) }} đ
    </span>
        </div>

      </div>

    </div>

    <div v-else>

      Không tìm thấy đơn hàng.

    </div>

  </div>
</template>

<style scoped>

.order-detail{
  max-width:1100px;
  margin:auto;
  padding:30px;
}

.info{
  background:#f8f8f8;
  padding:20px;
  border-radius:10px;
  margin-bottom:25px;
}

.info p{
  margin:8px 0;
}

.table{
  width:100%;
  border-collapse:collapse;
}

.table th,
.table td{
  border:1px solid #ddd;
  padding:10px;
  text-align:center;
}

.table th{
  background:#f5f5f5;
}

.total{
  margin-top:20px;
  text-align:right;
  color:red;
}

img{
  border-radius:8px;
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


.invoice-summary {
  width: 420px;
  margin-left: auto;
  margin-top: 25px;
  padding: 20px;
  background: #fff;
  border: 1px solid #e5e5e5;
  border-radius: 10px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 10px 0;
  font-size: 16px;
}

.summary-row span:first-child {
  color: #666;
}

.summary-row span:last-child {
  font-weight: 600;
}

.discount {
  color: #e53935;
}

.invoice-summary hr {
  margin: 18px 0;
  border: none;
  border-top: 1px dashed #ccc;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 28px;
  font-weight: bold;
  color: #e60023;
}

.order-timeline{

  display:flex;

  justify-content:space-between;

  margin:35px 0;

  position:relative;

}

.order-timeline::before{

  content:"";

  position:absolute;

  top:24px;

  left:8%;

  width:84%;

  height:4px;

  background:#ddd;

  z-index:0;

}

.timeline-item{

  flex:1;

  text-align:center;

  position:relative;

  z-index:1;

}

.timeline-circle{

  width:50px;

  height:50px;

  border-radius:50%;

  background:#ddd;

  color:white;

  display:flex;

  align-items:center;

  justify-content:center;

  margin:auto;

  font-size:22px;

  transition:.3s;

}

.timeline-title{

  margin-top:10px;

  font-weight:600;

  color:#999;

}

.timeline-item.active .timeline-circle{

  background:#28a745;

}

.timeline-item.active .timeline-title{

  color:#28a745;

}

.cancel-order-box{

  display:flex;

  align-items:center;

  gap:15px;

  background:#fff4f4;

  border-left:5px solid #dc3545;

  padding:18px;

  border-radius:10px;

  margin:30px 0;

}

.cancel-order-box i{

  font-size:45px;

  color:#dc3545;

}

</style>

```vue
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
const router = useRouter()
const dashboard = ref({
  tongSoDon: 0,
  tongDoanhThu: 0,
  soDonChoXacNhan: 0,
  soDonDaXacNhan: 0,
  soDonDangGiao: 0,
  soDonHoanThanh: 0,
  soDonDaHuy: 0,
})
const xemTatCaDonHang = () => {
  router.push('/hoa-don')
}
const loadDashboard = async () => {
  try {
    const response = await axios.get(
      'http://localhost:8080/api/hoa-don/thong-ke/dashboard'
    )

    dashboard.value = response.data
  } catch (error) {
    console.error('Lỗi tải dashboard:', error)
  }
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN').format(value || 0) + ' VNĐ'
}

onMounted(() => {
  loadDashboard()
})
</script>

<template>
  <div class="dashboard-container">
    <h1 class="dashboard-title">Dashboard Thống Kê GX Sneaker</h1>

    <div class="dashboard-grid">
      <div class="card" @click="xemTatCaDonHang">
        <h3>Tổng đơn hàng</h3>
        <p>{{ dashboard.tongSoDon }}</p>
      </div>

      <div class="card">
        <h3>Tổng doanh thu</h3>
        <p>{{ formatCurrency(dashboard.tongDoanhThu) }}</p>
      </div>

      <div class="card">
        <h3>Chờ xác nhận</h3>
        <p>{{ dashboard.soDonChoXacNhan }}</p>
      </div>

      <div class="card">
        <h3>Đã xác nhận</h3>
        <p>{{ dashboard.soDonDaXacNhan }}</p>
      </div>

      <div class="card">
        <h3>Đang giao</h3>
        <p>{{ dashboard.soDonDangGiao }}</p>
      </div>

      <div class="card">
        <h3>Hoàn thành</h3>
        <p>{{ dashboard.soDonHoanThanh }}</p>
      </div>

      <div class="card">
        <h3>Đã hủy</h3>
        <p>{{ dashboard.soDonDaHuy }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.dashboard-title {
  margin-bottom: 24px;
  color: #333;
  font-size: 32px;
  font-weight: bold;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.card h3 {
  margin-bottom: 12px;
  color: #666;
  font-size: 16px;
}

.card p {
  font-size: 28px;
  font-weight: bold;
  color: #111;
}
</style>
```

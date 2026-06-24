<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  CategoryScale,
  LinearScale,
  ArcElement,
  BarElement
} from 'chart.js'

import {
  Line,
  Pie,
  Bar
} from 'vue-chartjs'

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  CategoryScale,
  LinearScale,
  ArcElement,
  BarElement
)
const router = useRouter()

const dashboard = ref({
  tongSoDon: 0,
  tongDoanhThu: 0,
  soDonChoXacNhan: 0,
  soDonDaXacNhan: 0,
  soDonDangGiao: 0,
  soDonHoanThanh: 0,
  soDonDaHuy: 0
})

const revenueChartData = ref({
  labels: [],
  datasets: []
})

const chartOptions = {
  responsive: true,

  plugins: {
    legend: {
      position: 'top'
    }
  },

  scales: {
    y: {
      ticks: {
        callback: function (value) {
          return new Intl.NumberFormat('vi-VN').format(value) + ' đ'
        }
      }
    }
  }
}

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
const selectedYear = ref(new Date().getFullYear())

const loadRevenueChart = async () => {
  try {

    const response = await axios.get(
      `http://localhost:8080/api/hoa-don/thong-ke/bieu-do-doanh-thu-thang?year=${selectedYear.value}`
    )

    revenueChartData.value = {
      labels: response.data.map(item => `Tháng ${item.thang}`),

      datasets: [
        {
          label: `Doanh thu năm ${selectedYear.value}`,
          data: response.data.map(item => item.doanhThu),

          borderColor: '#0d6efd',
          backgroundColor: '#0d6efd',

          tension: 0.4,
          fill: false
        }
      ]
    }

  } catch (error) {
    console.error('Lỗi tải biểu đồ doanh thu:', error)
  }
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN').format(value || 0) + ' VNĐ'
}

const statusChartData = ref({
  labels: [],
  datasets: []
})

const pieOptions = {
  responsive: true,
  maintainAspectRatio: false
}

const handleYearChange = () => {
  loadRevenueChart()
  loadStatusChart()
}

const loadStatusChart = async () => {
  try {

    const response = await axios.get(
      `http://localhost:8080/api/hoa-don/thong-ke/trang-thai-don-hang?year=${selectedYear.value}`
    )

    statusChartData.value = {
      labels: response.data.map(item => item.trangThai),

      datasets: [
        {
          data: response.data.map(item => item.soLuong),

          backgroundColor: [
            '#198754',
            '#0d6efd',
            '#ffc107',
            '#dc3545',
            '#6c757d'
          ]
        }
      ]
    }

  } catch (error) {
    console.error(error)
  }
}

const tongTrangThai = computed(() => {
  return statusChartData.value?.datasets?.[0]?.data?.reduce(
    (tong, item) => tong + item,
    0
  ) || 0
})

const topSanPhamData = ref([])

const loadTopSanPham = async () => {
  try {

    const res = await axios.get(
      `http://localhost:8080/api/hoa-don/top-5-san-pham?year=${selectedYear.value}`
    )

    topSanPhamData.value = res.data

  } catch (error) {
    console.error("Lỗi tải top sản phẩm:", error)
  }
}
const topSanPhamChartData = computed(() => ({
  labels: topSanPhamData.value.map(
    item => item.tenSanPham
  ),

  datasets: [
    {
      label: "Số lượng bán",

      data: topSanPhamData.value.map(
        item => item.tongSoLuongBan
      ),

      backgroundColor: [
        "#ef4444",
        "#3b82f6",
        "#f59e0b",
        "#14b8a6",
        "#8b5cf6"
      ],

      borderRadius: 12,

      barThickness: 30,
      maxBarThickness: 40,

      categoryPercentage: 0.4,
      barPercentage: 0.7
    }
  ]
}))

const topSanPhamOptions = {
  responsive: true,
  maintainAspectRatio: false,

  plugins: {
    legend: {
      display: false
    },

    tooltip: {
      backgroundColor: "#111827",
      padding: 12,
      titleColor: "#fff",
      bodyColor: "#fff",

      callbacks: {
        label: function (context) {
          return `Số lượng bán: ${context.raw}`
        }
      }
    }
  },

  scales: {
    x: {
      offset: true,

      grid: {
        display: false
      },

      border: {
        display: false
      },

      ticks: {
        color: "#374151",

        font: {
          size: 13,
          weight: "600"
        },

        maxRotation: 0,
        minRotation: 0
      }
    },

    y: {
      beginAtZero: true,

      ticks: {
        precision: 0,
        stepSize: 1,
        color: "#6B7280"
      },

      grid: {
        color: "#f3f4f6"
      },

      border: {
        display: false
      }
    }
  },

  layout: {
    padding: {
      top: 10,
      bottom: 10,
      left: 40,
      right: 40
    }
  }
}

onMounted(() => {
  loadDashboard()
  loadRevenueChart()
  loadTopSanPham()
})
</script>

<template>
  <div class="dashboard-container">
    <h1 class="dashboard-title">
      Dashboard Thống Kê GX Sneaker
    </h1>


    <div class="dashboard-grid">

      <div class="card" @click="xemTatCaDonHang">
        <h3>Tổng đơn hàng</h3>
        <p>{{ dashboard.tongSoDon }}</p>
      </div>

      <div class="card">
        <h3>Doanh thu lũy kế</h3>
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

    <div class="year-filter mt-4 mb-4">
      <label class="form-label">Chọn năm</label>

      <select
        class="form-select year-select"
        v-model="selectedYear"
        @change="handleYearChange"
      >
        <option :value="2024">2024</option>
        <option :value="2025">2025</option>
        <option :value="2026">2026</option>
      </select>
    </div>

    <div class="chart-grid">

      <!-- Biểu đồ doanh thu -->
      <div class="chart-card">
        <h3 class="chart-title">
          📈 Biểu đồ doanh thu theo tháng năm {{ selectedYear }}
        </h3>

        <Line :data="revenueChartData" />
      </div>

      <!-- Biểu đồ trạng thái -->
      <div class="chart-card">
        <h3 class="chart-title">
          Tỷ lệ trạng thái đơn hàng năm {{ selectedYear }}
        </h3>

        <div class="pie-chart-container">

          <div v-if="tongTrangThai === 0" class="no-data">
            Không có dữ liệu đơn hàng trong năm {{ selectedYear }}
          </div>

          <Pie
            v-else
            :data="statusChartData"
            :options="pieOptions"
          />

        </div>
      </div>

    </div>

    <div class="chart-card top-product mt-4">
      <h3 class="chart-title">
        Top 5 sản phẩm bán chạy năm {{ selectedYear }}
      </h3>
      <div class="top-product-chart">
        <Bar
          :data="topSanPhamChartData"
          :options="topSanPhamOptions"
        />
      </div>
    </div>


  </div>
</template>
<style scoped>
.dashboard-container {
  padding: 30px;
  background: #f5f7fb;
  min-height: 100vh;
}

.dashboard-title {
  font-size: 34px;
  font-weight: 700;
  margin-bottom: 30px;
  color: #111827;
}

/* KPI Cards */

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit,minmax(250px,1fr));
  gap: 20px;
}

.card {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 24px rgba(0,0,0,.08);
  transition: all .3s ease;
  border: none;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0,0,0,.12);
}

.card h3 {
  font-size: 15px;
  color: #6b7280;
  margin-bottom: 12px;
}

.card p {
  font-size: 30px;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

/* Filter */

.year-filter {
  margin-top: 30px;
  margin-bottom: 30px;
}

.form-label {
  font-weight: 600;
  margin-bottom: 8px;
}

.year-select {
  width: 180px;
  border-radius: 12px;
  border: 1px solid #dbe1ea;
  padding: 10px 14px;
  transition: .3s;
}

.year-select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59,130,246,.15);
}

/* Chart Layout */

.chart-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.chart-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 24px rgba(0,0,0,.08);
}

.chart-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 20px;
  color: #111827;
}

/* Pie Chart */

.pie-chart-container {
  height: 320px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.no-data {
  height: 320px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #9ca3af;
  font-size: 15px;
  font-weight: 500;
}

/* Top Product Chart */

.mt-4 {
  margin-top: 24px;
}

/* Responsive */

@media (max-width: 992px) {

  .chart-grid {
    grid-template-columns: 1fr;
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }

  .chart-card.top-product {
    height: 450px;
  }



  .top-product-chart {
    height: 350px;
    width: 75%;
    margin: 0 auto;
  }
}
</style>


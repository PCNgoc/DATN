<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import '@/api/authApi.js'

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
  BarElement,
} from 'chart.js'

import { Line, Pie, Bar } from 'vue-chartjs'

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  CategoryScale,
  LinearScale,
  ArcElement,
  BarElement,
)
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

const revenueChartData = ref({
  labels: [],
  datasets: [],
})

const chartOptions = {
  responsive: true,

  plugins: {
    legend: {
      position: 'top',
    },
  },


  scales: {
    y: {
      ticks: {
        callback: function (value) {
          return new Intl.NumberFormat('vi-VN').format(value) + ' đ'
        },
      },
    },
  },
}
const currentYear = new Date().getFullYear()

const years = Array.from(
  { length: 7 },
  (_, i) => currentYear - 3 + i
)

const selectedYear = ref(currentYear)

const tuNgay = ref('')
const denNgay = ref('')

const loadDashboard = async () => {
  try {
    const response = await axios.get(
      'http://localhost:8080/api/hoa-don/thong-ke/dashboard',
      {
        params: {
          tuNgay: tuNgay.value || null,
          denNgay: denNgay.value || null
        }
      }
    )

    dashboard.value = response.data

  } catch (error) {
    console.error('Lỗi tải dashboard:', error)
  }
}
const handleDateFilter = async () => {
  if (tuNgay.value && denNgay.value) {
    if (tuNgay.value > denNgay.value) {
      alert('Từ ngày không được lớn hơn đến ngày')
      return
    }
  }

  await Promise.all([
    loadDashboard(),
    loadRevenueChart(),
    loadStatusChart(),
    loadTopSanPham()
  ])
}

const handleDateFilter1 = async () => {
  if (tuNgay.value && denNgay.value) {
    if (tuNgay.value > denNgay.value) {
      alert('Từ ngày không được lớn hơn đến ngày')
      return
    }
  }

  await Promise.all([
    loadDashboard(),

  ])
}

const resetDateFilter = async () => {
  tuNgay.value = ''
  denNgay.value = ''

  await Promise.all([
    loadRevenueChart(),
    loadStatusChart(),
    loadTopSanPham(),
    loadDashboard()
  ])
}




const loadRevenueChart = async () => {
  try {

    // =========================================
    // CÓ LỌC THEO KHOẢNG NGÀY
    // =========================================
    if (tuNgay.value || denNgay.value) {

      const params = {}

      // Trường hợp chỉ chọn TỪ NGÀY
      // => Từ ngày đó đến hôm nay
      if (tuNgay.value && !denNgay.value) {

        params.tuNgay = tuNgay.value

        params.denNgay = new Date()
          .toISOString()
          .slice(0, 10)
      }

        // Trường hợp chỉ chọn ĐẾN NGÀY
      // => Từ đầu năm đến ngày đó
      else if (!tuNgay.value && denNgay.value) {

        params.tuNgay = `${selectedYear.value}-01-01`

        params.denNgay = denNgay.value
      }

      // Trường hợp chọn CẢ HAI
      else {

        params.tuNgay = tuNgay.value
        params.denNgay = denNgay.value
      }

      // Kiểm tra khoảng ngày
      if (
        params.tuNgay &&
        params.denNgay &&
        params.tuNgay > params.denNgay
      ) {

        alert('Từ ngày không được lớn hơn đến ngày')

        return
      }

      const response = await axios.get(
        'http://localhost:8080/api/hoa-don/thong-ke/bieu-do-doanh-thu-khoang-ngay',
        {
          params
        }
      )

      revenueChartData.value = {

        labels: response.data.map(item => {

          const date = new Date(item.ngay)

          return date.toLocaleDateString('vi-VN')
        }),

        datasets: [
          {
            label: `Doanh thu từ ${params.tuNgay} đến ${params.denNgay}`,

            data: response.data.map(
              item => item.doanhThu
            ),

            borderColor: '#0d6efd',

            backgroundColor: 'rgba(13, 110, 253, 0.15)',

            tension: 0.4,

            fill: true,

            pointRadius: 4,

            pointHoverRadius: 6,
          },
        ],
      }

      return
    }


    // =========================================
    // KHÔNG LỌC NGÀY
    // => GIỮ BIỂU ĐỒ 12 THÁNG
    // =========================================

    const response = await axios.get(
      `http://localhost:8080/api/hoa-don/thong-ke/bieu-do-doanh-thu-thang?year=${selectedYear.value}`,
    )

    revenueChartData.value = {

      labels: response.data.map(
        item => `Tháng ${item.thang}`
      ),

      datasets: [
        {
          label: `Doanh thu năm ${selectedYear.value}`,

          data: response.data.map(
            item => item.doanhThu
          ),

          borderColor: '#0d6efd',

          backgroundColor: 'rgba(13, 110, 253, 0.15)',

          tension: 0.4,

          fill: true,

          pointRadius: 4,

          pointHoverRadius: 6,
        },
      ],
    }

  } catch (error) {

    console.error(
      'Lỗi tải biểu đồ doanh thu:',
      error
    )

  }
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN').format(value || 0) + ' VNĐ'
}

const statusChartData = ref({
  labels: [],
  datasets: [],
})

const statusChartKey = ref(0)

const pieOptions = {
  responsive: true,
  maintainAspectRatio: false,
}

const handleYearChange = () => {
  loadRevenueChart()
  loadStatusChart()
  loadTopSanPham()
}

const loadStatusChart = async () => {
  try {

    let response

    // ==========================================
    // CÓ LỌC THEO NGÀY
    // ==========================================

    if (tuNgay.value || denNgay.value) {

      const params = {}

      // ------------------------------------------
      // Chỉ chọn TỪ NGÀY
      // => Từ ngày đó đến hôm nay
      // ------------------------------------------

      if (tuNgay.value && !denNgay.value) {

        params.tuNgay = tuNgay.value

        params.denNgay = new Date()
          .toISOString()
          .slice(0, 10)
      }

        // ------------------------------------------
        // Chỉ chọn ĐẾN NGÀY
        // => Từ đầu năm đến ngày đó
      // ------------------------------------------

      else if (!tuNgay.value && denNgay.value) {

        params.tuNgay = `${selectedYear.value}-01-01`

        params.denNgay = denNgay.value
      }

        // ------------------------------------------
        // Chọn CẢ HAI
        // => Từ ngày đến ngày
      // ------------------------------------------

      else {

        params.tuNgay = tuNgay.value

        params.denNgay = denNgay.value
      }

      console.log('STATUS FILTER:', params)

      response = await axios.get(
        'http://localhost:8080/api/hoa-don/thong-ke/trang-thai-don-hang-khoang-ngay',
        {
          params
        }
      )

    }

      // ==========================================
      // KHÔNG LỌC NGÀY
      // => DÙNG NĂM
    // ==========================================

    else {

      response = await axios.get(
        `http://localhost:8080/api/hoa-don/thong-ke/trang-thai-don-hang?year=${selectedYear.value}`
      )

    }

    // ==========================================
    // CẬP NHẬT DỮ LIỆU BIỂU ĐỒ
    // ==========================================

    console.log('STATUS RESPONSE:', response.data)

    statusChartData.value = {

      labels: response.data.map(
        item => item.trangThai
      ),

      datasets: [
        {
          data: response.data.map(
            item => item.soLuong
          ),

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

    // ==========================================
    // ÉP PIE CHART RENDER LẠI
    // ==========================================

    statusChartKey.value++

  } catch (error) {

    console.error(
      'Lỗi tải thống kê trạng thái:',
      error
    )

  }
}

const tongTrangThai = computed(() => {
  return statusChartData.value?.datasets?.[0]?.data?.reduce((tong, item) => tong + item, 0) || 0
})

const topSanPhamData = ref([])

const loadTopSanPham = async () => {
  try {

    // ==========================================
    // CÓ LỌC THEO NGÀY
    // ==========================================
    if (tuNgay.value || denNgay.value) {

      const params = {}

      // Chỉ chọn TỪ NGÀY
      // => Từ ngày đó đến hôm nay
      if (tuNgay.value && !denNgay.value) {

        params.tuNgay = tuNgay.value

        params.denNgay = new Date()
          .toISOString()
          .slice(0, 10)
      }

        // Chỉ chọn ĐẾN NGÀY
      // => Đầu năm đến ngày đó
      else if (!tuNgay.value && denNgay.value) {

        params.tuNgay = `${selectedYear.value}-01-01`

        params.denNgay = denNgay.value
      }

      // Chọn CẢ HAI
      else {

        params.tuNgay = tuNgay.value
        params.denNgay = denNgay.value
      }

      const res = await axios.get(
        'http://localhost:8080/api/hoa-don/thong-ke/top-5-san-pham-ban-chay-khoang-ngay',
        {
          params
        }
      )

      topSanPhamData.value = res.data

      return
    }

    // ==========================================
    // KHÔNG LỌC NGÀY
    // => DÙNG NĂM
    // ==========================================

    const res = await axios.get(
      `http://localhost:8080/api/hoa-don/thong-ke/top-5-san-pham-ban-chay?year=${selectedYear.value}`
    )

    topSanPhamData.value = res.data

  } catch (error) {

    console.error(
      'Lỗi tải top sản phẩm:',
      error
    )

  }
}
const topSanPhamChartData = computed(() => ({
  labels: topSanPhamData.value.map((item) => item.tenSanPham),

  datasets: [
    {
      label: 'Số lượng bán',

      data: topSanPhamData.value.map((item) => item.tongSoLuongBan),

      backgroundColor: ['#ef4444', '#3b82f6', '#f59e0b', '#14b8a6', '#8b5cf6'],

      borderRadius: 12,

      barThickness: 30,
      maxBarThickness: 40,

      categoryPercentage: 0.4,
      barPercentage: 0.7,
    },
  ],
}))

const topSanPhamOptions = {
  responsive: true,
  maintainAspectRatio: false,

  plugins: {
    legend: {
      display: false,
    },

    tooltip: {
      backgroundColor: '#111827',
      padding: 12,
      titleColor: '#fff',
      bodyColor: '#fff',

      callbacks: {
        label: function (context) {
          return `Số lượng bán: ${context.raw}`
        },
      },
    },
  },

  scales: {
    x: {
      offset: true,

      grid: {
        display: false,
      },

      border: {
        display: false,
      },

      ticks: {
        color: '#374151',

        font: {
          size: 13,
          weight: '600',
        },

        maxRotation: 0,
        minRotation: 0,
      },
    },

    y: {
      beginAtZero: true,

      ticks: {
        precision: 0,
        stepSize: 1,
        color: '#6B7280',
      },

      grid: {
        color: '#f3f4f6',
      },

      border: {
        display: false,
      },
    },
  },

  layout: {
    padding: {
      top: 10,
      bottom: 10,
      left: 40,
      right: 40,
    },
  },
}

const tonKhoData = ref([])
const loadTopTonKho = async () => {

  try {

    const res = await axios.get(
      "http://localhost:8080/api/hoa-don/thong-ke/top-ton-kho"
    )

    tonKhoData.value = res.data

  } catch (e) {

    console.log(e)

  }

}

const tonKhoChartData = computed(() => ({

  labels: tonKhoData.value.map(item => item.tenSanPham),

  datasets: [
    {

      label: "Số lượng tồn",

      data: tonKhoData.value.map(item => item.soLuongTon),

      backgroundColor: [
        "#3b82f6",
        "#22c55e",
        "#f59e0b",
        "#ef4444",
        "#8b5cf6"
      ],

      borderRadius: 12,

      barThickness: 30

    }
  ]

}))

onMounted(() => {
  loadDashboard()
  loadRevenueChart()
  loadStatusChart()
  loadTopSanPham()
  loadTopTonKho()
})
</script>

<template>
  <div class="dashboard-container">
    <h1 class="dashboard-title">Dashboard Thống Kê GX Sneaker</h1>

    <div class="date-filter-card">
      <div class="date-filter-title">
        <i class="bi bi-calendar3"></i>
        <span> Bộ lọc thời gian</span>
      </div>

      <div class="date-filter-form">

        <div class="date-filter-item">
          <label>Từ ngày</label>

          <input
            v-model="tuNgay"
            type="date"
            class="form-control"
          />
        </div>

        <div class="date-filter-item">
          <label>Đến ngày</label>

          <input
            v-model="denNgay"
            type="date"
            class="form-control"
          />
        </div>

        <button
          class="btn btn-primary date-filter-btn"
          @click="handleDateFilter1"
        >
          <i class="bi bi-funnel-fill me-2"></i>
          Lọc
        </button>

        <button
          class="btn btn-outline-secondary date-reset-btn"
          @click="resetDateFilter"
        >
          <i class="bi bi-arrow-counterclockwise me-2"></i>
          Đặt lại
        </button>

      </div>

      <small class="date-filter-hint">
        Nếu chỉ chọn ngày bắt đầu, hệ thống sẽ lấy dữ liệu từ ngày đó đến hôm nay.
      </small>
    </div>

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
        <option
          v-for="year in years"
          :key="year"
          :value="year"
        >
          {{ year }}
        </option>
      </select>
    </div>

    <div class="date-filter mt-3 mb-4">

      <div class="date-filter-item">
        <label class="form-label">
          Từ ngày
        </label>

        <input
          type="date"
          v-model="tuNgay"
          class="form-control"
        />
      </div>

      <div class="date-filter-item">
        <label class="form-label">
          Đến ngày
        </label>

        <input
          type="date"
          v-model="denNgay"
          class="form-control"
        />
      </div>

      <button
        type="button"
        class="btn btn-primary"
        @click="handleDateFilter"
      >
        <i class="bi bi-funnel-fill me-2"></i>
        Lọc doanh thu
      </button>

      <button
        type="button"
        class="btn btn-outline-secondary"
        @click="
      tuNgay = '';
      denNgay = '';
      loadRevenueChart();
    "
      >
        <i class="bi bi-arrow-counterclockwise me-2"></i>
        Xóa lọc
      </button>

    </div>

    <div class="chart-grid">
      <!-- Biểu đồ doanh thu -->
      <div class="chart-card">
        <h3 class="chart-title">📈 Biểu đồ doanh thu theo tháng năm {{ selectedYear }}</h3>

        <Line :data="revenueChartData" />
      </div>

      <!-- Biểu đồ trạng thái -->
      <div class="chart-card">
        <h3 class="chart-title">Tỷ lệ trạng thái đơn hàng năm {{ selectedYear }}</h3>

        <div class="pie-chart-container">
          <div v-if="tongTrangThai === 0" class="no-data">
            Không có dữ liệu đơn hàng trong năm {{ selectedYear }}
          </div>

          <Pie
            v-else
            :key="statusChartKey"
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

      <div
        v-if="topSanPhamData.length === 0"
        class="empty-chart"
      >
        <i class="bi bi-box-seam-fill empty-icon"></i>

        <h5>Chưa có dữ liệu</h5>

        <p>
          Không có sản phẩm nào được bán trong năm {{ selectedYear }}
        </p>
      </div>

      <div
        v-else
        class="top-product-chart"
      >
        <Bar
          :data="topSanPhamChartData"
          :options="topSanPhamOptions"
        />
      </div>
    </div>

    <div class="chart-card top-product mt-4">

      <h3 class="chart-title">
        Top 5 sản phẩm tồn kho nhiều
      </h3>

      <div class="top-product-chart">

        <Bar
          :data="tonKhoChartData"
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
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.card {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: none;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.12);
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
  transition: 0.3s;
}

.year-select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.15);
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
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
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

.empty-chart{
  height:350px;
  display:flex;
  flex-direction:column;
  justify-content:center;
  align-items:center;
  text-align:center;
  color:#9ca3af;
}

.empty-icon{
  font-size:60px;
  color:#d1d5db;
  margin-bottom:15px;
}

.empty-chart h5{
  color:#374151;
  font-weight:700;
  margin-bottom:8px;
}

.empty-chart p{
  margin:0;
  font-size:15px;
}

.date-filter {
  display: flex;
  align-items: end;
  gap: 16px;
  padding: 20px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.date-filter-item {
  min-width: 220px;
}

.date-filter-item .form-label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: #374151;
}

.date-filter-item input {
  height: 42px;
  border-radius: 10px;
}

.date-filter .btn {
  height: 42px;
  border-radius: 10px;
  font-weight: 600;
}




/* =========================================
   BỘ LỌC DOANH THU THEO KHOẢNG NGÀY
========================================= */

.date-filter-form {
  display: flex;
  align-items: flex-end;
  gap: 16px;

  width: 100%;

  padding: 20px 24px;

  background: #ffffff;

  border: 1px solid #e5e7eb;
  border-radius: 16px;

  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.05);

  margin-top: 16px;
}


/* =========================================
   Ô NGÀY
========================================= */

.date-filter-item {
  display: flex;
  flex-direction: column;

  gap: 7px;

  min-width: 210px;
}

.date-filter-item label {
  font-size: 14px;

  font-weight: 600;

  color: #374151;

  margin: 0;
}


/* Input date */

.date-filter-item .form-control {
  height: 42px;

  padding: 8px 12px;

  border: 1px solid #d1d5db;

  border-radius: 10px;

  background: #ffffff;

  color: #374151;

  font-size: 14px;

  transition: all 0.2s ease;
}


/* Hover */

.date-filter-item .form-control:hover {
  border-color: #9ca3af;
}


/* Focus */

.date-filter-item .form-control:focus {
  border-color: #0d6efd;

  box-shadow:
    0 0 0 3px rgba(13, 110, 253, 0.12);

  outline: none;
}


/* =========================================
   NÚT LỌC
========================================= */

.date-filter-btn {
  height: 42px;

  padding: 0 20px;

  border-radius: 10px;

  font-size: 14px;

  font-weight: 600;

  white-space: nowrap;

  transition: all 0.2s ease;
}

.date-filter-btn:hover {
  transform: translateY(-1px);

  box-shadow:
    0 5px 12px rgba(13, 110, 253, 0.25);
}


/* =========================================
   NÚT ĐẶT LẠI
========================================= */

.date-reset-btn {
  height: 42px;

  padding: 0 18px;

  border-radius: 10px;

  font-size: 14px;

  font-weight: 600;

  white-space: nowrap;

  transition: all 0.2s ease;
}

.date-reset-btn:hover {
  transform: translateY(-1px);
}


/* =========================================
   GỢI Ý
========================================= */

.date-filter-hint {
  display: block;

  margin-top: 8px;
  margin-left: 4px;

  color: #6b7280;

  font-size: 13px;

  line-height: 1.5;
}


/* =========================================
   RESPONSIVE
========================================= */

@media (max-width: 900px) {

  .date-filter-form {
    flex-wrap: wrap;

    align-items: stretch;
  }

  .date-filter-item {
    flex: 1;

    min-width: 200px;
  }

  .date-filter-btn,
  .date-reset-btn {
    flex: 1;
  }
}


@media (max-width: 576px) {

  .date-filter-form {
    flex-direction: column;

    align-items: stretch;

    padding: 16px;
  }

  .date-filter-item {
    width: 100%;

    min-width: 0;
  }

  .date-filter-btn,
  .date-reset-btn {
    width: 100%;
  }
}

</style>

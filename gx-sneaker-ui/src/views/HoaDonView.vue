<template>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

  <div class="container-fluid custom-layout bg-light min-vh-100">
    <div class="card hoa-don-card shadow border-0 w-100" style="max-width: 1300px;">

      <div class="card-header bg-white border-0 pt-5 pb-3 px-4">
        <div class="text-center">
          <h1 class="main-invoice-title fw-bold text-dark mb-4">
            DANH SÁCH HÓA ĐƠN ({{ hoaDons.length }})
          </h1>

          <div class="search-box-container mx-auto my-4">
            <div class="row g-2 align-items-center m-0">
              <div class="col-md-5 px-1">
                <div class="input-group search-input">
                  <span class="input-group-text bg-white border-end-0 text-muted">
                    <i class="bi bi-search"></i>
                  </span>
                  <input
                    v-model="searchMaHD"
                    type="text"
                    class="form-control border-start-0"
                    placeholder="Nhập mã hóa đơn..."
                    @keyup.enter="handleSearch"
                  />
                </div>
              </div>

              <div class="col-md-4 px-1">
                <select v-model="searchStatus" class="form-select search-select" @change="handleSearch">
                  <option value="">-- Tất cả trạng thái --</option>
                  <option value="CHO_XAC_NHAN">CHỜ XÁC NHẬN</option>
                  <option value="DA_XAC_NHAN">ĐÃ XÁC NHẬN</option>
                  <option value="DANG_GIAO">ĐANG GIAO</option>
                  <option value="HOAN_THANH">HOÀN THÀNH</option>
                  <option value="DA_HUY">ĐÃ HỦY</option>
                </select>
              </div>

              <div class="col-md-3 px-1 d-flex gap-2">
                <button @click="handleSearch" class="btn btn-filter d-flex align-items-center gap-2 text-nowrap w-100 justify-content-center">
                  <i class="bi bi-funnel-fill"></i> Lọc
                </button>
                <button @click="handleReset" class="btn btn-outline-secondary btn-reset d-flex align-items-center justify-content-center" title="Xóa bộ lọc">
                  <i class="bi bi-arrow-counterclockwise"></i>
                </button>
              </div>
            </div>
          </div>

          <div class="text-start px-2 mb-2">
            <button class="btn btn-export-lg d-inline-flex align-items-center gap-2">
              <i class="bi bi-download"></i> Xuất Excel
            </button>
          </div>
        </div>
      </div>

      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table mb-0 align-middle">
            <thead>
            <tr>
              <th style="width: 80px;" class="text-center">#</th>
              <th>Mã hóa đơn</th>
              <th>Người nhận</th>
              <th>SĐT</th>
              <th>Tổng thanh toán</th>
              <th>Trạng thái</th>
              <th>Ngày đặt</th>
              <th class="text-center" style="width: 220px;">Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(hd, index) in hoaDons" :key="hd.id">
              <td class="text-center text-muted fw-semibold">{{ index + 1 }}</td>
              <td><span class="ma-hd">{{ hd.maHoaDon }}</span></td>
              <td class="text-secondary fw-semibold">{{ hd.tenNguoiNhan }}</td>
              <td class="text-secondary">{{ hd.soDienThoaiNguoiNhan }}</td>
              <td><span class="tien">{{ formatMoney(hd.tongTienThanhToan) }}</span></td>
              <td>
                  <span class="status-pill" :class="getStatusClass(hd.trangThai)">
                    <i :class="getStatusIcon(hd.trangThai)" class="me-1"></i>
                    {{ getStatusText(hd.trangThai) }}
                  </span>
              </td>
              <td>
                <div class="date-time">
                  <div class="fw-semibold text-secondary">{{ formatDate(hd.ngayDatHang) }}</div>
                  <div class="text-muted small mt-1 text-black-50">{{ formatTime(hd.ngayDatHang) }}</div>
                </div>
              </td>
              <td>
                <div class="d-flex gap-4 justify-content-center">
                  <button @click="openDetailModal(hd)" class="btn-action btn-view" title="Chi tiết">
                    <i class="bi bi-eye"></i>
                  </button>
                  <button class="btn-action btn-edit" title="Sửa"><i class="bi bi-pencil-square"></i></button>
                  <button class="btn-action btn-delete" title="Xóa"><i class="bi bi-trash"></i></button>
                </div>
              </td>
            </tr>
            <tr v-if="hoaDons.length === 0">
              <td colspan="8" class="text-center py-5 text-muted fw-medium">
                Không tìm thấy dữ liệu hóa đơn nào phù hợp.
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="custom-modal-overlay" @click.self="closeModal">
      <div class="modal-content-card animate-slide-up">

        <div class="modal-header-pro">
          <div>
            <h4 class="m-0 fw-bold text-dark">CHI TIẾT ĐƠN HÀNG</h4>
            <small class="text-muted">Mã số: <span class="text-danger fw-bold">{{ selectedHoaDon?.maHoaDon }}</span></small>
          </div>
          <button @click="closeModal" class="btn-close-pro"><i class="bi bi-x-lg"></i></button>
        </div>

        <div class="modal-body-pro">
          <div class="info-grid mb-4">
            <div class="info-item">
              <label>Người nhận</label>
              <span>{{ selectedHoaDon?.tenNguoiNhan }}</span>
            </div>
            <div class="info-item">
              <label>Số điện thoại</label>
              <span>{{ selectedHoaDon?.soDienThoaiNguoiNhan }}</span>
            </div>
            <div class="info-item">
              <label>Ngày đặt</label>
              <span>{{ formatDate(selectedHoaDon?.ngayDatHang) }} {{ formatTime(selectedHoaDon?.ngayDatHang) }}</span>
            </div>
            <div class="info-item">
              <label>Trạng thái</label>
              <div>
                <span class="status-pill m-0" :class="getStatusClass(selectedHoaDon?.trangThai)">
                  {{ getStatusText(selectedHoaDon?.trangThai) }}
                </span>
              </div>
            </div>
          </div>

          <div class="product-table-wrapper">
            <table class="table table-borderless align-middle">
              <thead class="table-light">
              <tr>
                <th>Sản phẩm</th>
                <th class="text-center">Size/Màu</th>
                <th class="text-center">Số lượng</th>
                <th class="text-end">Đơn giá</th>
                <th class="text-end">Thành tiền</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="item in chiTietList" :key="item.id">
                <td>
                  <div class="d-flex align-items-center">
                    <div class="product-img-mini">GX</div>
                    <div class="ms-3">
                      <div class="fw-bold text-dark">
                        {{ item.chiTietSanPham?.sanPham?.tenSanPham || 'Giày Sneaker Cao Cấp GX' }}
                      </div>
                      <small class="text-muted">Mã SKU: {{ item.chiTietSanPham?.maSku || 'SKU-UNKNOWN' }}</small>
                    </div>
                  </div>
                </td>
                <td class="text-center text-secondary">
                  {{ item.chiTietSanPham?.kichCo || '41' }} / {{ item.chiTietSanPham?.mauSac || 'Đen' }}
                </td>
                <td class="text-center fw-bold">x{{ item.soLuong }}</td>
                <td class="text-end text-secondary">{{ formatMoney(item.donGia) }}</td>
                <td class="text-end fw-bold text-danger">{{ formatMoney(item.soLuong * item.donGia) }}</td>
              </tr>
              <tr v-if="chiTietList.length === 0">
                <td colspan="5" class="text-center py-4 text-muted">
                  Đang tải dữ liệu sản phẩm chi tiết...
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="modal-footer-pro">
          <div class="total-section">
            <span class="text-muted me-2">TỔNG CỘNG:</span>
            <span class="total-price">{{ formatMoney(selectedHoaDon?.tongTienThanhToan) }}</span>
          </div>
          <button @click="closeModal" class="btn btn-dark px-5 fw-bold rounded-3">ĐÓNG</button>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios'
import { ref, onMounted } from 'vue'

const hoaDons = ref([])
const searchMaHD = ref('')
const searchStatus = ref('')

const showModal = ref(false)
const selectedHoaDon = ref(null)
const chiTietList = ref([])

const loadHoaDon = async (maHD = '', status = '') => {
  try {
    const response = await axios.get('http://localhost:8080/api/hoa-don/search', {
      params: { maHoaDon: maHD, trangThai: status }
    })
    hoaDons.value = response.data
  } catch (error) {
    console.error('Lỗi API:', error)
  }
}

const openDetailModal = async (hd) => {
  selectedHoaDon.value = hd
  showModal.value = true

  try {
    const response = await axios.get('http://localhost:8080/api/hoa-don-chi-tiet')
    chiTietList.value = response.data.filter(item => item.hoaDon?.id === hd.id || item.idHoaDon === hd.id)
  } catch (error) {
    console.error('Lỗi khi tải chi tiết hóa đơn:', error)
  }
}

const closeModal = () => {
  showModal.value = false
  selectedHoaDon.value = null
  chiTietList.value = []
}

const handleSearch = () => { loadHoaDon(searchMaHD.value, searchStatus.value) }
const handleReset = () => { searchMaHD.value = ''; searchStatus.value = ''; loadHoaDon() }

const formatMoney = (v) => v ? Number(v).toLocaleString('vi-VN') + ' đ' : '0 đ'
const formatDate = (s) => s ? `${String(new Date(s).getDate()).padStart(2,'0')}/${String(new Date(s).getMonth()+1).padStart(2,'0')}/${new Date(s).getFullYear()}` : ''
const formatTime = (s) => s ? `${String(new Date(s).getHours()).padStart(2,'0')}:${String(new Date(s).getMinutes()).padStart(2,'0')}` : ''

const getStatusText = (s) => {
  const m = { 'CHO_XAC_NHAN': 'CHỜ XÁC NHẬN', 'DA_XAC_NHAN': 'ĐÃ XÁC NHẬN', 'DANG_GIAO': 'ĐANG GIAO', 'HOAN_THANH': 'HOÀN THÀNH', 'DA_HUY': 'ĐÃ HỦY' }
  return m[s] || s
}
const getStatusClass = (s) => {
  switch (s) {
    case 'CHO_XAC_NHAN': return 'bg-warning-custom'
    case 'DA_XAC_NHAN': return 'bg-primary-custom'
    case 'DANG_GIAO': return 'bg-info-custom'
    case 'HOAN_THANH': return 'bg-success-custom'
    case 'DA_HUY': return 'bg-danger-custom'
    default: return 'bg-secondary-custom'
  }
}
const getStatusIcon = (s) => {
  switch (s) {
    case 'CHO_XAC_NHAN': return 'bi bi-clock-history'
    case 'DA_XAC_NHAN': return 'bi bi-check2-all'
    case 'DANG_GIAO': return 'bi bi-truck'
    case 'HOAN_THANH': return 'bi bi-check-circle'
    case 'DA_HUY': return 'bi bi-x-circle'
    default: return 'bi bi-question-circle'
  }
}

onMounted(() => { loadHoaDon() })
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

* { font-family: 'Inter', sans-serif; }

.custom-layout {
  padding: 60px 70px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.hoa-don-card {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 35px rgba(0, 0, 0, 0.05) !important;
  background-color: #ffffff;
}

.main-invoice-title { font-size: 38px; letter-spacing: -0.5px; }

.search-box-container {
  background: #f8f9fa;
  padding: 16px 20px;
  border-radius: 12px;
  border: 1px solid #eef2f5;
  max-width: 850px !important;
  width: 100%;
}

/* ==================== ĐỊNH DẠNG MODAL CAO CẤP MỚI ==================== */
.custom-modal-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100vw; height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999 !important;
}

.modal-content-card {
  background: #ffffff;
  width: 900px;
  max-width: 95%;
  max-height: 88vh;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
}

.modal-header-pro {
  padding: 24px 32px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn-close-pro {
  border: none;
  background: none;
  font-size: 22px;
  color: #bfbfbf;
  cursor: pointer;
}
.btn-close-pro:hover { color: #000000; }

.modal-body-pro {
  padding: 32px;
  overflow-y: auto;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
}

.info-item label {
  display: block;
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 4px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-item span {
  font-weight: 600;
  color: #262626;
  font-size: 14.5px;
}

.product-table-wrapper {
  margin-top: 24px;
}

.product-img-mini {
  width: 45px; height: 45px;
  background: #1a1a1a; color: #ffffff;
  display: flex; align-items: center; justify-content: center;
  border-radius: 8px; font-weight: 800; font-size: 11px;
}

.modal-footer-pro {
  padding: 24px 32px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fafafa;
}

.total-price {
  font-size: 28px;
  font-weight: 900;
  color: #e04646;
}

.animate-slide-up {
  animation: slideUp 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes slideUp {
  from { transform: translateY(30px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

/* ==================== ĐỊNH DẠNG TABLE & BUTTON DANH SÁCH ==================== */
.search-input .form-control, .search-select, .btn-filter, .btn-reset { height: 46px !important; }
.search-input .input-group-text { border-radius: 8px 0 0 8px !important; border: 1px solid #ced4da; background-color: #ffffff; }
.search-input .form-control { border-radius: 0 8px 8px 0 !important; border: 1px solid #ced4da; }
.search-select { border-radius: 8px !important; border: 1px solid #ced4da; }

.btn-filter {
  padding: 0 24px; font-size: 14px; font-weight: 600; border-radius: 8px;
  background-color: #1a1a1a; color: #ffffff; border: none; transition: all 0.2s ease;
}
.btn-filter:hover { background-color: #000000; }

.btn-reset { width: 46px; border-radius: 8px; background-color: #ffffff; border: 1px solid #ced4da; color: #6c757d; }
.btn-reset:hover { background-color: #f8f9fa; }

.text-nowrap { white-space: nowrap !important; }

.btn-export-lg {
  padding: 10px 22px; font-size: 14px; font-weight: 600; border-radius: 6px;
  color: #434343; background-color: #ffffff; border: 1px solid #d9d9d9;
}
.btn-export-lg:hover { background-color: #f5f5f5; }

.table thead th { background: #f8f9fa; color: #262626; font-weight: 600; font-size: 14px; padding: 18px 24px; border-bottom: 2px solid #edf2f7; }
.table tbody td { padding: 20px 24px; border-bottom: 1px solid #f0f0f0; font-size: 14.5px; }

.ma-hd, .tien { color: #e04646; font-weight: 600; }

.status-pill { padding: 6px 14px; border-radius: 6px; font-size: 12px; font-weight: 600; display: inline-flex; align-items: center; }

.bg-warning-custom { background: #fff7e6 !important; color: #fa8c16 !important; border: 1px solid #ffe7ba; }
.bg-primary-custom { background: #e6f4ff !important; color: #1677ff !important; border: 1px solid #bae0ff; }
.bg-info-custom { background: #f0f5ff !important; color: #2f54eb !important; border: 1px solid #adc6ff; }
.bg-success-custom { background: #f6ffed !important; color: #52c41a !important; border: 1px solid #b7eb8f; }
.bg-danger-custom { background: #fff1f0 !important; color: #ff4d4f !important; border: 1px solid #ffa39e; }

.btn-action {
  width: 38px; height: 38px; border-radius: 8px; display: inline-flex; align-items: center;
  justify-content: center; border: 1px solid; background: #ffffff; font-size: 16px; transition: all 0.2s ease;
}
.btn-view { border-color: #1677ff; color: #1677ff; }
.btn-view:hover { background: #e6f4ff; transform: translateY(-2px); }
.btn-edit { border-color: #52c41a; color: #52c41a; }
.btn-edit:hover { background: #f6ffed; transform: translateY(-2px); }
.btn-delete { border-color: #ff4d4f; color: #ff4d4f; }
.btn-delete:hover { background: #fff1f0; transform: translateY(-2px); }

.date-time { line-height: 1.4; }
</style>

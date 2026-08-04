<script setup>
import { ref, onMounted, onUnmounted, computed, watch, nextTick } from 'vue'

// import {
//   taoHoaDonCho,
//   getHoaDonCho,
//   themSanPham,
//   getChiTietHoaDon,
//   doiKhachHang,
// } from '@/services/hoaDonService.js'

import {
  taoHoaDonCho,
  getHoaDonCho,
  themSanPham,
  getChiTietHoaDon,
  doiKhachHang,
  thanhToanVnpayTaiQuay,
  apDungVoucherTaiQuay,
  xoaVoucherTaiQuay
} from '@/services/hoaDonService.js'

import { getBanTaiQuay } from '@/services/chiTietSanPhamService'

import { capNhatSoLuong } from '@/services/hoaDonService.js'

import { getKhachHangBanTaiQuay } from '@/services/khachHangService'

import { xoaSanPham } from '@/services/hoaDonService'
import { thanhToanTienMat } from '@/services/hoaDonService'
import {
  exportPdf
} from "@/services/hoaDonService";

import { getAll as getVouchers } from "@/services/phieuGiamGiaService";

import * as bootstrap from 'bootstrap'

//=====================
// HÓA ĐƠN CHỜ
//=====================

const hoaDonCho = ref([])
const selectedHoaDon = ref(null)

const loadHoaDonCho = async () => {
  try {
    const res = await getHoaDonCho()
    hoaDonCho.value = res.data
  } catch (e) {
    console.log(e)
  }
}

const taoHoaDon = async () => {
  await taoHoaDonCho()
  await loadHoaDonCho()
}

const maVoucherInput = ref("");


const applyVoucher = async () => {
  if (!selectedHoaDon.value) return;
  if (!maVoucherInput.value.trim()) {
    alert("Vui lòng nhập mã giảm giá");
    return;
  }
  
  try {
    await apDungVoucherTaiQuay(selectedHoaDon.value.id, maVoucherInput.value.trim());
    alert("Áp dụng mã giảm giá thành công!");
    await reloadHoaDonDangChon(); // Reload để lấy lại tổng tiền và thông tin voucher
  } catch (e) {
    const errorMsg = e.response?.data?.message || "Lỗi khi áp dụng mã giảm giá";
    alert(errorMsg);
  }
};

const removeVoucher = async () => {
  if (!selectedHoaDon.value || !selectedHoaDon.value.idPhieuGiamGia) return;
  try {
    await xoaVoucherTaiQuay(selectedHoaDon.value.id);
    alert("Đã gỡ mã giảm giá!");
    maVoucherInput.value = "";
    await reloadHoaDonDangChon();
  } catch (e) {
    const errorMsg = e.response?.data?.message || "Lỗi khi gỡ mã giảm giá";
    alert(errorMsg);
  }
};

//=====================
// DANH SÁCH SẢN PHẨM
//=====================

const sanPham = ref([])
const keyword = ref('')

const loadSanPham = async () => {
  try {
    const res = await getBanTaiQuay()
    sanPham.value = res.data
  } catch (e) {
    console.log(e)
  }
}

const sanPhamFilter = computed(() => {
  if (!keyword.value) {
    return sanPham.value
  }

  return sanPham.value.filter((sp) =>
    sp.sanPham?.tenSanPham?.toLowerCase().includes(keyword.value.toLowerCase()),
  )
})

//=====================
// GIỎ HÀNG
//=====================

const gioHang = ref([])

const loadGioHang = async () => {
  if (!selectedHoaDon.value) {
    gioHang.value = []

    return
  }

  const res = await getChiTietHoaDon(selectedHoaDon.value.id)

  gioHang.value = res.data
}

const chonHoaDon = async (hd) => {
  selectedHoaDon.value = hd

  await loadGioHang()
}

//=====================
// THÊM SẢN PHẨM
//=====================

const themSanPhamVaoHoaDon = async (sp) => {
  if (!selectedHoaDon.value) {
    alert('Vui lòng chọn hóa đơn trước!')

    return
  }

  try {
    await themSanPham(selectedHoaDon.value.id, {
      chiTietSanPhamId: sp.id,

      soLuong: 1,
    })

    // Load lại giỏ hàng
    await loadGioHang()

    // Load lại danh sách hóa đơn
    await reloadHoaDonDangChon()

    // Load lại tồn kho
    await loadSanPham()
  } catch (e) {
    console.error(e)

    alert('Không thể thêm sản phẩm')
  }
}

//=====================
// FORMAT
//=====================

const formatMoney = (money) => {
  if (!money) return '0 đ'

  return new Intl.NumberFormat('vi-VN').format(money) + ' đ'
}

const tangSoLuong = async (item) => {
  await capNhatSoLuong(item.id, item.soLuong + 1)

  await loadGioHang()

  await reloadHoaDonDangChon()

  await loadSanPham()
}

const giamSoLuong = async (item) => {
  if (item.soLuong <= 1) return

  await capNhatSoLuong(item.id, item.soLuong - 1)

  await loadGioHang()

  await reloadHoaDonDangChon()

  await loadSanPham()
}

const reloadHoaDonDangChon = async () => {
  await loadHoaDonCho()

  selectedHoaDon.value = hoaDonCho.value.find((hd) => hd.id === selectedHoaDon.value.id)
}

const khachHangs = ref([]);
const danhSachVoucher = ref([]);
const searchVoucherText = ref("");
const isVoucherDropdownOpen = ref(false);
const voucherSearchInput = ref(null);

const formatKieuPhieu = (kieuPhieu, dieuKienHang) => {
  if (!kieuPhieu) return 'Công khai';
  switch (kieuPhieu) {
    case 'PUBLIC': return 'Công khai';
    case 'HOLIDAY': return 'Ngày lễ';
    case 'MEMBER_ONLY': return `Hội viên${dieuKienHang ? ' (' + dieuKienHang + ')' : ''}`;
    case 'PERSONAL': return 'Cá nhân';
    case 'NEW_CUSTOMER': return 'Khách mới';
    default: return kieuPhieu;
  }
};

const filteredVouchers = computed(() => {
  if (!searchVoucherText.value) return danhSachVoucher.value;
  const query = searchVoucherText.value.toLowerCase().trim();
  return danhSachVoucher.value.filter(v => 
    (v.maPhieu && v.maPhieu.toLowerCase().includes(query)) ||
    (v.tenPhieu && v.tenPhieu.toLowerCase().includes(query))
  );
});

const currentAppliedVoucher = computed(() => {
  if (!selectedHoaDon.value || !selectedHoaDon.value.idPhieuGiamGia) return null;
  return danhSachVoucher.value.find(v => v.id === selectedHoaDon.value.idPhieuGiamGia);
});

watch(() => selectedHoaDon.value?.idPhieuGiamGia, (newId) => {
  if (newId) {
    const found = danhSachVoucher.value.find(v => v.id === newId);
    if (found) {
      maVoucherInput.value = found.maPhieu;
    }
  } else {
    maVoucherInput.value = "";
  }
}, { immediate: true });

const toggleVoucherDropdown = async () => {
  isVoucherDropdownOpen.value = !isVoucherDropdownOpen.value;
  if (isVoucherDropdownOpen.value) {
    searchVoucherText.value = "";
    await nextTick();
    if (voucherSearchInput.value) {
      voucherSearchInput.value.focus();
    }
  }
};

const selectVoucher = (v) => {
  maVoucherInput.value = v.maPhieu;
  isVoucherDropdownOpen.value = false;
};

const closeVoucherDropdownOnClickOutside = (e) => {
  const dropdownEl = document.getElementById('voucher-combobox');
  if (dropdownEl && !dropdownEl.contains(e.target)) {
    isVoucherDropdownOpen.value = false;
  }
};

const loadDanhSachVoucher = async () => {
  try {
    const res = await getVouchers();
    if (res.data) {
      const dataArr = Array.isArray(res.data) ? res.data : (res.data.content || res.data.data || []);
      // Lọc voucher đang hoạt động và số lượng > 0
      danhSachVoucher.value = dataArr.filter(v => v.trangThai === true && v.soLuong > 0);
    }
  } catch (err) {
    console.error("Lỗi khi tải mã giảm giá", err);
  }
};

const selectedKhachHang = ref(null)

const loadKhachHang = async () => {
  const res = await getKhachHangBanTaiQuay()

  khachHangs.value = res.data
}

watch(selectedKhachHang, async (val) => {
  if (!selectedHoaDon.value) return

  await doiKhachHang(selectedHoaDon.value.id, val?.id)

  await loadHoaDonCho()

  selectedHoaDon.value = hoaDonCho.value.find((h) => h.id === selectedHoaDon.value.id)
})

const xoaKhoiGio = async (item) => {
  if (!confirm('Xóa sản phẩm khỏi hóa đơn?')) return

  await xoaSanPham(item.id)

  await loadGioHang()

  await loadSanPham()

  await loadHoaDonCho()

  selectedHoaDon.value = hoaDonCho.value.find((h) => h.id === selectedHoaDon.value.id)
}

const showThanhToan = ref(false)

const tienKhachDua = ref(0)

const phuongThucThanhToan = ref('TIEN_MAT')
const vnpayCheckoutUrl = ref('')

const vnpayQrUrl = computed(() => {
  if (!vnpayCheckoutUrl.value) return ''

  return `https://api.qrserver.com/v1/create-qr-code/?size=260x260&data=${encodeURIComponent(
    vnpayCheckoutUrl.value,
  )}`
})

const tienThua = computed(() => {
  if (!selectedHoaDon.value) return 0

  return Math.max(
    0,

    tienKhachDua.value - (selectedHoaDon.value.tongTienThanhToan || 0),
  )
})

const xacNhanThanhToan = async () => {
  if (!selectedHoaDon.value) return

  if (!gioHang.value || gioHang.value.length === 0) {
    alert('Hóa đơn chưa có sản phẩm')
    return
  }

  try {
    if (phuongThucThanhToan.value === 'VNPAY') {

      const res = await thanhToanVnpayTaiQuay(selectedHoaDon.value.id)

      vnpayCheckoutUrl.value = res.data?.checkoutUrl

      if (!vnpayCheckoutUrl.value) {
        alert("Không nhận được link thanh toán VNPAY")
        return
      }

      window.open(vnpayCheckoutUrl.value, "_blank")
      return
    }

    

    await thanhToanTienMat(
      selectedHoaDon.value.id,
      {
        tienKhachDua: tienKhachDua.value,
        phuongThucThanhToan: phuongThucThanhToan.value
      }
    )

    alert('Thanh toán thành công')

    showThanhToan.value = false
    tienKhachDua.value = 0
    phuongThucThanhToan.value = 'TIEN_MAT'
    vnpayCheckoutUrl.value = ''
    selectedHoaDon.value = null
    gioHang.value = []

    await loadHoaDonCho()
    await loadSanPham()
  } catch (e) {
    alert(e.response?.data?.message || 'Thanh toán thất bại')
  }
}

const hoanTatThanhToanVnpay = async () => {
  showThanhToan.value = false
  tienKhachDua.value = 0
  phuongThucThanhToan.value = 'TIEN_MAT'
  vnpayCheckoutUrl.value = ''
  selectedHoaDon.value = null
  gioHang.value = []

  await loadHoaDonCho()
  await loadSanPham()
}


//=====================

onMounted(async () => {
  document.addEventListener('click', closeVoucherDropdownOnClickOutside)
  await loadHoaDonCho()
  await loadSanPham()
  await loadKhachHang()
  await loadDanhSachVoucher()
})

onUnmounted(() => {
  document.removeEventListener('click', closeVoucherDropdownOnClickOutside)
})
</script>

<template>
  <div class="banhang">
    <!-- Hóa đơn -->

    <div class="invoice-bar">
      <button class="btn btn-primary" @click="taoHoaDon">+ Hóa đơn mới</button>

      <div class="invoice-list">
        <div
          v-for="hd in hoaDonCho"
          :key="hd.id"
          class="invoice-item"
          :class="{ active: selectedHoaDon?.id === hd.id }"
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

          <input v-model="keyword" class="form-control" placeholder="Tìm sản phẩm..." />
        </div>

        <!-- Card -->

        <div class="product-grid">
          <!-- để nguyên v-for sản phẩm của bạn -->

          <div class="product-card" v-for="sp in sanPhamFilter" :key="sp.id">
            <img
              class="img-fluid rounded mb-3"
              :src="
                sp.sanPham?.anhDaiDien
                  ? '/images/' + sp.sanPham.anhDaiDien
                  : 'https://placehold.co/250x250'
              "
            />

            <h6 class="fw-bold mb-2">
              {{ sp.sanPham?.tenSanPham }}
            </h6>

            <small>
              {{ sp.mauSac?.ten }}

              - Size {{ sp.kichThuoc?.size }}
            </small>

            <div class="price text-danger fw-bold fs-5">
              {{ formatMoney(sp.giaBan) }}
            </div>

            <div class="stock text-success mb-2">Còn {{ sp.soLuongTon }}</div>

            <button class="btn btn-success w-100" @click="themSanPhamVaoHoaDon(sp)">Thêm</button>
          </div>
        </div>
      </div>

      <!-- RIGHT -->

      <div class="right-panel">
        <h4>Giỏ hàng</h4>
        <div class="mb-3">
          <label class="fw-bold"> Khách hàng </label>

          <select v-model="selectedKhachHang" class="form-select">
            <option :value="null">Khách lẻ</option>

            <option v-for="kh in khachHangs" :key="kh.id" :value="kh">
              {{ kh.hoTen }} - {{ kh.soDienThoai }} ({{ kh.hangThanhVien || 'BRONZE' }})
            </option>
          </select>
        </div>

        <table class="table table-hover align-middle mb-0">
          <thead>
            <tr>
              <th style="width: 70px"></th>

              <th style="width: 45%">Tên sản phẩm</th>

              <th style="width: 70px" class="text-center">SL</th>

              <th style="width: 120px">Đơn giá</th>

              <th style="width: 140px">Thành tiền</th>
              <th style="width: 60px">Xóa</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="item in gioHang" :key="item.id">
              <!-- Ảnh -->

              <td>
                <img
                  :src="'/images/' + item.anh"
                  style="width: 55px; height: 55px; object-fit: cover"
                  class="rounded border"
                />
              </td>

              <!-- Tên -->

              <td>
                <div class="fw-bold">
                  {{ item.tenSanPham }}
                </div>

                <div class="text-secondary small">
                  {{ item.mauSac }}

                  - Size {{ item.size }}
                </div>
              </td>

              <!-- SL -->

              <td>
                <div class="quantity-box">
                  <button class="btn btn-sm btn-outline-secondary" @click="giamSoLuong(item)">
                    -
                  </button>

                  <span>
                    {{ item.soLuong }}
                  </span>

                  <button class="btn btn-sm btn-outline-secondary" @click="tangSoLuong(item)">
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
                <button class="btn btn-danger btn-sm" @click="xoaKhoiGio(item)">🗑</button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- bảng -->

        <!-- table hiện tại -->

        <!-- Mã giảm giá (Custom Combobox) -->
        <div class="voucher-section mt-3 mb-3 p-3 border rounded bg-white shadow-sm" v-if="selectedHoaDon">
          <label class="form-label fw-bold mb-2 text-secondary" style="font-size: 0.9rem;">Phiếu giảm giá:</label>
          <div class="d-flex gap-2 position-relative" id="voucher-combobox">
            <div class="flex-grow-1 position-relative">
              <div 
                class="form-control d-flex justify-content-between align-items-center bg-white"
                style="cursor: pointer; user-select: none;"
                @click="toggleVoucherDropdown"
              >
                <span :class="{'text-muted': !maVoucherInput, 'fw-medium': !!maVoucherInput}">
                  {{ maVoucherInput || 'Chọn mã giảm giá...' }}
                </span>
                <span class="ms-2 text-muted" style="font-size: 0.8rem;">▼</span>
              </div>

              <!-- Menu xổ xuống -->
              <div 
                v-if="isVoucherDropdownOpen" 
                class="position-absolute start-0 end-0 bg-white border rounded shadow-lg p-2 mt-1" 
                style="z-index: 1050; max-height: 300px; overflow: hidden; display: flex; flex-direction: column;"
              >
                <div class="p-1 mb-2 border-bottom">
                  <div class="input-group input-group-sm">
                    <span class="input-group-text bg-light border-end-0">
                      <i class="fas fa-search text-muted"></i>
                    </span>
                    <input 
                      type="text" 
                      class="form-control border-start-0 ps-1" 
                      placeholder="Tìm mã code..." 
                      v-model="searchVoucherText" 
                      ref="voucherSearchInput"
                    >
                  </div>
                </div>
                <div class="list-group list-group-flush flex-grow-1" style="max-height: 220px; overflow-y: auto; overflow-x: hidden;">
                  <button 
                    v-for="v in filteredVouchers" 
                    :key="'dropdown-voucher-'+v.id"
                    type="button" 
                    class="list-group-item list-group-item-action text-start py-2 px-2 border-0 rounded mb-1"
                    :class="{'active': maVoucherInput === v.maPhieu}"
                    @click="selectVoucher(v)"
                  >
                    <div class="fw-bold" style="font-size: 0.85rem;">
                      <span>{{ v.maPhieu }}</span>
                      <span class="ms-1" :class="maVoucherInput === v.maPhieu ? 'text-warning' : 'text-danger'">
                        (-{{ v.loaiGiamGia ? v.giaTriGiam + '%' : formatMoney(v.giaTriGiam) }})
                      </span>
                    </div>
                    <div class="small" :class="maVoucherInput === v.maPhieu ? 'text-white-50' : 'text-muted'" style="font-size: 0.75rem;">
                      {{ v.tenPhieu || 'Mã giảm giá' }} • {{ formatKieuPhieu(v.kieuPhieu, v.dieuKienHangThanhVien) }}
                    </div>
                  </button>

                  <div v-if="filteredVouchers.length === 0" class="text-center text-muted py-3 small">
                    Không tìm thấy phiếu giảm giá nào
                  </div>
                </div>
              </div>
            </div>

            <button class="btn btn-primary px-3 fw-medium" type="button" @click="applyVoucher">
              Áp dụng
            </button>
          </div>

          <div class="mt-2 p-2 bg-success-subtle border border-success-subtle rounded d-flex justify-content-between align-items-center" v-if="selectedHoaDon.idPhieuGiamGia">
            <span class="text-success small">
              ✓ Đang áp dụng: <strong>{{ currentAppliedVoucher?.tenPhieu || currentAppliedVoucher?.maPhieu || 'Phiếu giảm giá' }}</strong> 
              (Mã: <strong>{{ currentAppliedVoucher?.maPhieu || maVoucherInput }}</strong> 
              <span v-if="selectedHoaDon.soTienGiam" class="text-danger fw-bold ms-1">- Giảm {{ formatMoney(selectedHoaDon.soTienGiam) }}</span>)
            </span>
            <button class="btn btn-sm btn-outline-danger border-0 py-0 px-2 fw-bold" @click="removeVoucher" title="Gỡ mã">✕</button>
          </div>
        </div>

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

        <button class="btn btn-success w-100 mt-3" @click="showThanhToan = true">Thanh toán</button>
      </div>
    </div>
  </div>

  <div v-if="showThanhToan" class="popup-bg">
    <div class="popup">
      <h4>Thanh toán hóa đơn</h4>

      <div class="mb-3">
        <label>Tổng tiền</label>
        <input
          class="form-control"
          :disabled="true"
          :value="formatMoney(selectedHoaDon?.tongTienThanhToan)"
        />
      </div>

      <div class="mb-3">
        <label class="fw-bold">Phương thức thanh toán</label>

        <div class="payment-methods">
          <label class="payment-option">
            <input type="radio" value="TIEN_MAT" v-model="phuongThucThanhToan" />
            <span>Tiền mặt</span>
          </label>

          <label class="payment-option">
            <input type="radio" value="VNPAY" v-model="phuongThucThanhToan" />
            <span>VNPAY QR</span>
          </label>
        </div>
      </div>

      <div v-if="phuongThucThanhToan === 'TIEN_MAT'">
        <div class="mb-3">
          <label>Khách đưa</label>
          <input type="number" v-model="tienKhachDua" class="form-control" />
        </div>

        <div class="mb-3">
          <label>Tiền thừa</label>
          <input class="form-control" :disabled="true" :value="formatMoney(tienThua)" />
        </div>
      </div>

      <div v-if="phuongThucThanhToan === 'VNPAY'" class="vnpay-box">
        <p class="text-secondary mb-2">Bấm xác nhận để tạo mã QR VNPAY cho hóa đơn này.</p>

        <div v-if="vnpayCheckoutUrl" class="qr-wrapper">
          <img :src="vnpayQrUrl" alt="VNPAY QR" class="vnpay-qr" />

          <a :href="vnpayCheckoutUrl" target="_blank" class="btn btn-primary mt-3">
            Mở trang thanh toán VNPAY
          </a>

          <button class="btn btn-success mt-2" @click="hoanTatThanhToanVnpay">
            Tôi đã thanh toán xong
          </button>
        </div>
      </div>

      <div class="d-flex gap-2">
        <button class="btn btn-secondary flex-fill" @click="showThanhToan = false">Hủy</button>

        <button class="btn btn-success flex-fill" @click="xacNhanThanhToan">
          {{ phuongThucThanhToan === 'VNPAY' ? 'Tạo QR VNPAY' : 'Xác nhận' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.banhang {
  display: flex;

  flex-direction: column;

  height: 100%;

  gap: 15px;
}

/* hóa đơn */

.invoice-bar {
  background: white;

  padding: 15px;

  border-radius: 12px;

  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.invoice-list {
  display: flex;

  gap: 10px;

  overflow-x: auto;

  margin-top: 12px;
}

.invoice-item {
  min-width: 140px;

  text-align: center;

  padding: 10px;

  border-radius: 10px;

  background: #f4f4f4;

  cursor: pointer;

  font-weight: 600;
}

.invoice-item.active {
  background: #0d6efd;

  color: white;
}

/* nội dung */

.main-content {
  display: flex;

  gap: 15px;

  flex: 1;
}

.left-panel {
  flex: 1;

  background: white;

  border-radius: 12px;

  padding: 15px;
}

.right-panel {
  width: 560px;

  background: white;

  border-radius: 12px;

  padding: 15px;
}

/* header */

.product-header {
  display: flex;

  justify-content: space-between;

  align-items: center;

  margin-bottom: 20px;
}

.product-header input {
  width: 280px;
}

/* sản phẩm */

.product-grid {
  display: grid;

  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

/* card */

.product-card {
  transition: 0.2s;
}

.product-card:hover {
  transform: translateY(-4px);

  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
}

/* tổng tiền */

.bill-total {
  margin-top: 20px;

  padding-top: 20px;

  border-top: 1px solid #ddd;
}

.product-card {
  border-radius: 12px;

  border: 1px solid #eee;

  padding: 15px;

  background: white;
}

.product-card img {
  width: 100%;

  height: 170px;

  object-fit: cover;
}

.product-card:hover {
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.12);
}

.invoice-item {
  transition: 0.2s;
}

.invoice-item:hover {
  background: #dbeafe;
}

.right-panel table td {
  vertical-align: middle;
}

.quantity-box {
  display: flex;

  align-items: center;

  justify-content: center;

  gap: 8px;
}

.quantity-box span {
  width: 30px;

  text-align: center;

  font-weight: bold;
}

.left-panel {
  display: flex;

  flex-direction: column;

  height: calc(100vh - 220px);
}

.product-grid {
  flex: 1;

  overflow-y: auto;

  display: grid;

  grid-template-columns: repeat(2, 1fr);

  gap: 18px;

  padding-right: 8px;
}

.popup-bg {
  position: fixed;

  top: 0;

  left: 0;

  right: 0;

  bottom: 0;

  background: rgba(0, 0, 0, 0.45);

  display: flex;

  justify-content: center;

  align-items: center;

  z-index: 999;
}

.popup {
  width: 420px;

  background: white;

  padding: 25px;

  border-radius: 12px;
}

.payment-methods {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.payment-option {
  flex: 1;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  background: #f9fafb;
}

.payment-option:hover {
  border-color: #0d6efd;
  background: #eff6ff;
}

.vnpay-box {
  margin-top: 12px;
  padding: 14px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1px dashed #94a3b8;
  text-align: center;
}

.qr-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.vnpay-qr {
  width: 260px;
  height: 260px;
  object-fit: contain;
  background: white;
  padding: 10px;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
}
</style>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getMeApi } from '@/api/authApi'
import { update as updateProfile } from '@/services/khachHangService'
import {
  getByKhachHangId as getAddresses,
  create as createAddress,
  update as updateAddress,
  remove as removeAddress,
} from '@/services/diaChiService'

const user = ref(null)
const errorMessage = ref('')
const addressList = ref([])
const showForm = ref(false)

const showProfileForm = ref(false)
const profileForm = ref({
  hoTen: '',
  soDienThoai: '',
  gioiTinh: true,
  ngaySinh: ''
})

const editProfile = () => {
  profileForm.value = {
    hoTen: user.value.hoTen || '',
    soDienThoai: user.value.soDienThoai || '',
    gioiTinh: user.value.gioiTinh !== false, // default true
    ngaySinh: user.value.ngaySinh ? new Date(user.value.ngaySinh).toISOString().split('T')[0] : ''
  }
  showProfileForm.value = true
}

const cancelEditProfile = () => {
  showProfileForm.value = false
}

const saveProfile = async () => {
  if (!profileForm.value.hoTen || !profileForm.value.hoTen.trim()) {
    alert('Họ và tên không được để trống!')
    return
  }
  try {
    const data = {
      ...user.value,
      hoTen: profileForm.value.hoTen,
      soDienThoai: profileForm.value.soDienThoai,
      gioiTinh: profileForm.value.gioiTinh,
      ngaySinh: profileForm.value.ngaySinh || null
    }
    await updateProfile(user.value.id, data)
    alert('Cập nhật hồ sơ thành công!')
    showProfileForm.value = false
    
    // Update local storage user data
    const localUserStr = localStorage.getItem('user')
    if (localUserStr) {
      const localUser = JSON.parse(localUserStr)
      localUser.hoTen = data.hoTen
      localUser.soDienThoai = data.soDienThoai
      localUser.gioiTinh = data.gioiTinh
      localUser.ngaySinh = data.ngaySinh
      localStorage.setItem('user', JSON.stringify(localUser))
    }
    
    await loadProfileAndAddresses()
  } catch (error) {
    console.error(error)
    alert('Lỗi khi cập nhật hồ sơ! ' + (error.response?.data?.message || ''))
  }
}

const addressForm = ref({
  id: null,
  hoTenNguoiNhan: '',
  soDienThoai: '',
  tinhThanh: '',
  quanHuyen: '',
  phuongXa: '',
  diaChiChiTiet: '',
})

const loadProfileAndAddresses = async () => {
  try {
    const response = await getMeApi()
    user.value = response.data

    if (user.value && user.value.id) {
      const addrRes = await getAddresses(user.value.id)
      addressList.value = addrRes.data || []
    }
  } catch (error) {
    errorMessage.value = 'Không thể lấy thông tin tài khoản hoặc địa chỉ'
    console.error(error)
  }
}

const formatCurrency = (value) => {
  if (!value) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

const tierProgress = computed(() => {
  if (!user.value) return { currentSpent: 0, currentTier: 'BRONZE', nextTier: 'SILVER', max: 5000000, percent: 0, remaining: 5000000 }
  const spent = user.value.tongChiTieu || 0
  let max = 5000000
  let currentTier = 'BRONZE'
  let nextTier = 'SILVER'
  let min = 0

  if (spent < 5000000) {
    max = 5000000
    currentTier = 'BRONZE'
    nextTier = 'SILVER'
    min = 0
  } else if (spent < 15000000) {
    max = 15000000
    currentTier = 'SILVER'
    nextTier = 'GOLD'
    min = 5000000
  } else if (spent < 30000000) {
    max = 30000000
    currentTier = 'GOLD'
    nextTier = 'DIAMOND'
    min = 15000000
  } else {
    return { currentSpent: spent, currentTier: 'DIAMOND', nextTier: 'MAX', max: spent, percent: 100, remaining: 0 }
  }

  const percent = Math.min(((spent - min) / (max - min)) * 100, 100)
  const remaining = max - spent

  return { currentSpent: spent, currentTier, nextTier, max, percent, remaining }
})

onMounted(loadProfileAndAddresses)

const resetAddressForm = () => {
  addressForm.value = {
    id: null,
    hoTenNguoiNhan: '',
    soDienThoai: '',
    tinhThanh: '',
    quanHuyen: '',
    phuongXa: '',
    diaChiChiTiet: '',
  }
  showForm.value = false
}

const editAddress = (addr) => {
  addressForm.value = { ...addr }
  showForm.value = true
}

const saveAddress = async () => {
  if (!addressForm.value.hoTenNguoiNhan || !addressForm.value.hoTenNguoiNhan.trim()) {
    alert('Tên người nhận không được để trống!')
    return
  }
  if (!addressForm.value.soDienThoai || !addressForm.value.soDienThoai.trim()) {
    alert('Số điện thoại không được để trống!')
    return
  }
  if (!addressForm.value.tinhThanh || !addressForm.value.tinhThanh.trim()) {
    alert('Tỉnh / Thành phố không được để trống!')
    return
  }
  if (!addressForm.value.phuongXa || !addressForm.value.phuongXa.trim()) {
    alert('Phường / Xã không được để trống!')
    return
  }
  if (!addressForm.value.diaChiChiTiet || !addressForm.value.diaChiChiTiet.trim()) {
    alert('Địa chỉ chi tiết không được để trống!')
    return
  }

  try {
    const data = {
      ...addressForm.value,
      idKhachHang: user.value.id,
    }
    if (addressForm.value.id) {
      await updateAddress(addressForm.value.id, data)
    } else {
      await createAddress(data)
    }
    resetAddressForm()
    await loadProfileAndAddresses()
    alert('Lưu địa chỉ thành công!')
  } catch (err) {
    console.error(err)
    alert('Lỗi khi lưu địa chỉ!')
  }
}

const deleteAddress = async (addrId) => {
  if (!confirm('Bạn có chắc muốn xóa địa chỉ này?')) return
  try {
    await removeAddress(addrId)
    await loadProfileAndAddresses()
  } catch (error) {
    console.error(error)

    localStorage.removeItem('token')
    localStorage.removeItem('user')

    alert('Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại')
    router.push('/login')
  }
}
</script>

<template>
  <div class="profile-container">
    <div class="profile-header">
      <h2>👤 Hồ sơ tài khoản</h2>
      <p class="subtitle">Quản lý thông tin cá nhân và sổ địa chỉ nhận hàng của bạn.</p>
    </div>

    <p v-if="errorMessage" class="error-msg">⚠️ {{ errorMessage }}</p>

    <div class="profile-grid" v-if="user">
      <!-- Cột trái: Thông tin tài khoản -->
      <div class="profile-card info-section">
        <div class="section-header">
          <h3>Thông tin cá nhân</h3>
          <button v-if="!showProfileForm" class="btn-add" @click="editProfile">
            ✏️ Chỉnh sửa
          </button>
        </div>
        <div class="info-group">
          <label>Mã khách hàng:</label>
          <span>{{ user.maKhachHang }}</span>
        </div>
        <div class="info-group">
          <label>Hạng hội viên:</label>
          <span :class="['badge-hang', user.hangThanhVien]">{{ user.hangThanhVien || 'BRONZE' }}</span>
        </div>
        
        <!-- Thanh tiến trình hạng hội viên -->
        <div class="tier-progress-box" v-if="user">
          <div class="progress-labels">
            <span>Đã chi tiêu: <strong>{{ formatCurrency(tierProgress.currentSpent) }}</strong></span>
            <span v-if="tierProgress.nextTier !== 'MAX'">Cần <strong>{{ formatCurrency(tierProgress.remaining) }}</strong> để lên <strong>{{ tierProgress.nextTier }}</strong></span>
            <span v-else>Đạt cấp độ cao nhất</span>
          </div>
          <div class="progress-bar-bg">
            <div class="progress-bar-fill" :style="{ width: tierProgress.percent + '%' }"></div>
          </div>
        </div>

        <div v-if="!showProfileForm">
          <div class="info-group">
            <label>Họ và tên:</label>
            <span>{{ user.hoTen }}</span>
          </div>
          <div class="info-group">
            <label>Email:</label>
            <span>{{ user.email }}</span>
          </div>
          <div class="info-group">
            <label>Số điện thoại:</label>
            <span>{{ user.soDienThoai }}</span>
          </div>
          <div class="info-group">
            <label>Giới tính:</label>
            <span>{{ user.gioiTinh === false ? 'Nữ' : 'Nam' }}</span>
          </div>
          <div class="info-group" v-if="user.ngaySinh">
            <label>Ngày sinh:</label>
            <span>{{ new Date(user.ngaySinh).toLocaleDateString('vi-VN') }}</span>
          </div>
        </div>

        <!-- Form Edit Profile -->
        <div v-if="showProfileForm" class="address-form-box" style="margin-top: 20px;">
          <div class="form-item">
            <label>Họ và tên <span class="req">*</span></label>
            <input v-model="profileForm.hoTen" placeholder="Nhập họ tên" />
          </div>
          <div class="form-item">
            <label>Số điện thoại</label>
            <input v-model="profileForm.soDienThoai" placeholder="Nhập số điện thoại" />
          </div>
          <div class="form-grid">
            <div class="form-item" style="grid-column: span 2;">
              <label>Giới tính</label>
              <select v-model="profileForm.gioiTinh" class="address-select">
                <option :value="true">Nam</option>
                <option :value="false">Nữ</option>
              </select>
            </div>
          </div>
          <div class="form-actions" style="margin-top: 15px;">
            <button class="btn-save" @click="saveProfile">💾 Lưu hồ sơ</button>
            <button class="btn-cancel" @click="cancelEditProfile">Hủy</button>
          </div>
        </div>
      </div>

      <!-- Cột phải: Danh sách địa chỉ nhận hàng -->
      <div class="profile-card address-section">
        <div class="section-header">
          <h3>Sổ địa chỉ nhận hàng</h3>
          <button v-if="!showForm" class="btn-add" @click="showForm = true">
            ➕ Thêm địa chỉ mới
          </button>
        </div>

        <!-- Form Địa chỉ -->
        <div v-if="showForm" class="address-form-box">
          <h4>{{ addressForm.id ? '✏️ Chỉnh sửa địa chỉ' : '➕ Thêm địa chỉ nhận hàng' }}</h4>

          <div class="form-grid">
            <div class="form-item">
              <label>Họ tên người nhận <span class="req">*</span></label>
              <input v-model="addressForm.hoTenNguoiNhan" placeholder="Nhập họ tên" />
            </div>

            <div class="form-item">
              <label>Số điện thoại nhận <span class="req">*</span></label>
              <input v-model="addressForm.soDienThoai" placeholder="Nhập số điện thoại" />
            </div>
          </div>

          <div class="form-grid">
            <div class="form-item">
              <label>Tỉnh / Thành phố <span class="req">*</span></label>
              <input v-model="addressForm.tinhThanh" placeholder="Tỉnh / Thành phố" />
            </div>

            <div class="form-item">
              <label>Phường / Xã <span class="req">*</span></label>
              <input v-model="addressForm.phuongXa" placeholder="Phường / Xã" />
            </div>
          </div>

          <div class="form-item">
            <label>Địa chỉ chi tiết <span class="req">*</span></label>
            <textarea
              v-model="addressForm.diaChiChiTiet"
              rows="3"
              placeholder="Số nhà, tên đường, ngõ ngách..."
            ></textarea>
          </div>

          <div class="form-actions">
            <button class="btn-save" @click="saveAddress">💾 Lưu địa chỉ</button>
            <button class="btn-cancel" @click="resetAddressForm">Hủy</button>
          </div>
        </div>

        <!-- Danh sách địa chỉ hiển thị -->
        <div class="address-list">
          <div v-if="addressList.length === 0" class="no-address">
            <p>Chưa có địa chỉ giao hàng nào được lưu. Hãy thêm mới để thanh toán nhanh hơn!</p>
          </div>

          <div v-for="addr in addressList" :key="addr.id" class="address-item-card">
            <div class="addr-details">
              <p class="addr-recipient">
                <strong>{{ addr.hoTenNguoiNhan }}</strong>
                <span class="addr-phone">📞 {{ addr.soDienThoai }}</span>
              </p>
              <p class="addr-text">
                {{ addr.diaChiChiTiet }}, {{ addr.phuongXa }}, {{ addr.tinhThanh }}
              </p>
            </div>
            <div class="addr-actions">
              <button class="btn-edit" @click="editAddress(addr)" title="Chỉnh sửa">✏️</button>
              <button class="btn-delete" @click="deleteAddress(addr.id)" title="Xóa">🗑️</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
  font-family: Inter, sans-serif;
}

.profile-header {
  margin-bottom: 30px;
}

.profile-header h2 {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 6px;
}

.subtitle {
  color: #64748b;
  font-size: 15px;
}

.error-msg {
  background: #fef2f2;
  color: #ef4444;
  padding: 12px 16px;
  border-radius: 10px;
  margin-bottom: 20px;
  border: 1px solid #fee2e2;
  font-size: 14px;
}

.profile-grid {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 30px;
}

@media (max-width: 992px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }
}

.profile-card {
  background: #fff;
  padding: 28px;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.profile-card h3 {
  font-size: 20px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 20px;
  border-bottom: 2px solid #f1f5f9;
  padding-bottom: 12px;
}

.info-group {
  display: flex;
  margin-bottom: 16px;
  font-size: 14px;
}

.info-group label {
  width: 130px;
  font-weight: 600;
  color: #64748b;
}

.info-group span {
  color: #1e293b;
  font-weight: 500;
}

.badge-hang {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700 !important;
  display: inline-block;
  line-height: 1;
}

.badge-hang.BRONZE {
  background: #f1f5f9;
  color: #64748b;
  border: 1px solid #cbd5e1;
}

.badge-hang.SILVER {
  background: #f1f5f9;
  color: #94a3b8;
  border: 1px solid #cbd5e1;
  background-image: linear-gradient(to right, #e2e8f0, #f8fafc);
}

.badge-hang.GOLD {
  background: #fef08a;
  color: #854d0e;
  border: 1px solid #fde047;
}

.badge-hang.DIAMOND {
  background: #e0f2fe;
  color: #0369a1;
  border: 1px solid #7dd3fc;
}

.tier-progress-box {
  margin: 16px 0 24px 0;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.progress-labels {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #475569;
  margin-bottom: 10px;
}

.progress-labels strong {
  color: #0ea5e9;
}

.progress-bar-bg {
  width: 100%;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #38bdf8, #2563eb);
  border-radius: 4px;
  transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 2px solid #f1f5f9;
  padding-bottom: 12px;
}

.section-header h3 {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.btn-add {
  padding: 8px 16px;
  border: none;
  background: #0ea5e9;
  color: white;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-add:hover {
  background: #0284c7;
}

.address-form-box {
  background: #f8fafc;
  padding: 20px;
  border-radius: 14px;
  margin-bottom: 24px;
  border: 1px solid #e2e8f0;
}

.address-form-box h4 {
  margin-bottom: 16px;
  font-size: 16px;
  color: #1e293b;
  font-weight: 700;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-grid-three {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 16px;
  margin-top: 16px;
}

@media (max-width: 576px) {
  .form-grid,
  .form-grid-three {
    grid-template-columns: 1fr;
  }
}

.form-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
}

.form-item label {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 6px;
}

.req {
  color: #ef4444;
}

.form-item input,
.form-item textarea {
  padding: 10px 14px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
}

.form-item input:focus,
.form-item textarea:focus {
  outline: none;
  border-color: #0ea5e9;
  box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.15);
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 10px;
}

.btn-save {
  padding: 10px 20px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}

.btn-save:hover {
  background: #059669;
}

.btn-cancel {
  padding: 10px 20px;
  background: #64748b;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}

.btn-cancel:hover {
  background: #475569;
}

.no-address {
  text-align: center;
  padding: 30px;
  color: #64748b;
  font-size: 14px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px dashed #cbd5e1;
}

.address-item-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  margin-bottom: 16px;
  transition: all 0.2s;
}

.address-item-card:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
}

.addr-recipient {
  font-size: 16px;
  color: #1e293b;
  margin-bottom: 6px;
}

.addr-phone {
  font-size: 13px;
  color: #64748b;
  margin-left: 12px;
  font-weight: normal;
}

.addr-text {
  font-size: 14px;
  color: #475569;
}

.addr-actions {
  display: flex;
  gap: 8px;
}

.addr-actions button {
  background: #f1f5f9;
  border: none;
  padding: 8px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.addr-actions button:hover {
  background: #e2e8f0;
}
</style>

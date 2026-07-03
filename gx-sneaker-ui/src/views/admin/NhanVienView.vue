<template>
  <div class="page">
    <!-- ================= HEADER ================= -->

    <div class="page-header">
      <div>
        <h2>Quản lý nhân viên</h2>
        <p>Quản lý thông tin nhân viên GX Sneaker</p>
      </div>

      <button class="btn-refresh" @click="loadNhanVien">🔄 Làm mới</button>
    </div>

    <!-- ================= DASHBOARD ================= -->

    <div class="summary">
      <div class="summary-card">
        <h4>Tổng nhân viên</h4>

        <h2>{{ tongNhanVien }}</h2>
      </div>

      <div class="summary-card success">
        <h4>Đang hoạt động</h4>

        <h2>{{ dangHoatDong }}</h2>
      </div>

      <div class="summary-card danger">
        <h4>Ngừng hoạt động</h4>

        <h2>{{ ngungHoatDong }}</h2>
      </div>
    </div>

    <!-- ================= TOOLBAR ================= -->

    <div class="toolbar">
      <input v-model="keyword" placeholder="🔍 Tìm theo mã, tên, email..." />

      <select v-model="filterTrangThai">
        <option value="">Tất cả trạng thái</option>

        <option value="true">Hoạt động</option>

        <option value="false">Ngừng hoạt động</option>
      </select>

      <button class="btn-add" @click="clearForm">+ Thêm mới</button>
    </div>

    <!-- ================= TABLE ================= -->

    <div class="table-card">
      <table>
        <thead>
          <tr>
            <th width="60">STT</th>

            <th width="80">Ảnh</th>

            <th>Mã NV</th>

            <th>Họ tên</th>

            <th>Email</th>

            <th>SĐT</th>

            <th>Quyền</th>

            <th>Trạng thái</th>

            <th width="180">Thao tác</th>
          </tr>
        </thead>

        <tbody>
          <tr v-if="filteredNhanVien.length === 0">
            <td colspan="9" class="empty">Không có dữ liệu</td>
          </tr>

          <tr
            v-for="(item, index) in filteredNhanVien"
            :key="item.id"
            @click="selectNhanVien(item)"
          >
            <td>
              {{ index + 1 }}
            </td>

            <td>
              <img
                :src="getAvatarUrl(item.anhDaiDien)"
                class="table-avatar"
                @error="onImageError"
              />
            </td>

            <td>
              {{ item.maNhanVien }}
            </td>

            <td>
              {{ item.hoTen }}
            </td>

            <td>
              {{ item.email }}
            </td>

            <td>
              {{ item.soDienThoai }}
            </td>

            <td class="col-role">
              <span class="nv-badge nv-role">
                {{ getTenQuyen(item) }}
              </span>
            </td>

            <td class="col-status">
              <span
                :class="
                  isActiveStatus(item.trangThai) ? 'nv-badge nv-active' : 'nv-badge nv-inactive'
                "
              >
                {{ getTrangThaiText(item.trangThai) }}
              </span>
            </td>

            <td>
              <button class="btn-edit" @click.stop="selectNhanVien(item)">✏️ Sửa</button>

              <button class="btn-delete" @click.stop="deleteNhanVien(item.id)">🗑 Xóa</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- ================= LOADING ================= -->

    <div v-if="loading" class="loading">Đang tải dữ liệu...</div>

    <!-- ================= FORM ================= -->

    <div class="form-card">
      <h3>
        {{ form.id ? 'Cập nhật nhân viên' : 'Thêm nhân viên' }}
      </h3>

      <div class="grid">
        <div class="form-group">
          <label>Mã nhân viên</label>

          <input v-model="form.maNhanVien" placeholder="NV001" />
        </div>

        <div class="form-group">
          <label>Họ tên</label>

          <input v-model="form.hoTen" placeholder="Nguyễn Văn A" />
        </div>

        <div class="form-group">
          <label>Email</label>

          <input v-model="form.email" type="email" />
        </div>

        <div class="form-group">
          <label>Mật khẩu</label>

          <input v-model="form.matKhau" type="password" />
        </div>

        <div class="form-group">
          <label>Số điện thoại</label>

          <input v-model="form.soDienThoai" />
        </div>

        <div class="form-group">
          <label>Giới tính</label>

          <select v-model="form.gioiTinh">
            <option :value="true">Nam</option>

            <option :value="false">Nữ</option>
          </select>
        </div>

        <div class="form-group">
          <label>Địa chỉ</label>

          <input v-model="form.diaChi" />
        </div>

        <div class="form-group">
          <label>Quyền</label>

          <select v-model.number="form.idPhanQuyen">
            <option :value="1">ADMIN</option>

            <option :value="2">STAFF</option>
          </select>
        </div>

        <div class="form-group">
          <label>Trạng thái</label>

          <select v-model="form.trangThai">
            <option :value="true">Hoạt động</option>

            <option :value="false">Ngừng hoạt động</option>
          </select>
        </div>

        <div class="form-group">
          <label>Ảnh đại diện</label>

          <input type="file" accept="image/*" @change="chooseAvatar" />

          <div class="avatar-box">
            <!-- Ảnh vừa chọn -->
            <img v-if="preview" :src="preview" class="avatar-preview" />

            <img
              v-else-if="form.anhDaiDien"
              :src="getAvatarUrl(form.anhDaiDien)"
              class="avatar-preview"
              @error="onImageError"
            />
          </div>
        </div>
      </div>

      <div class="button-group">
        <button class="btn-save" @click="addNhanVien">Thêm</button>

        <button class="btn-update" @click="updateNhanVien">Cập nhật</button>

        <button class="btn-reset" @click="clearForm">Làm mới</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import nhanVienService from '@/services/nhanVienService'
import { success, error, confirmDelete } from '@/utils/notify'
import uploadService from '@/services/uploadService'

// ================== DATA ==================

const preview = ref('')
const danhSach = ref([])
const loading = ref(false)
const saving = ref(false)
const keyword = ref('')
const filterTrangThai = ref('')
const errors = ref({})

const form = ref({
  id: null,
  idPhanQuyen: 2,
  tenQuyen: '',

  maNhanVien: '',
  hoTen: '',
  email: '',
  matKhau: '',
  soDienThoai: '',

  gioiTinh: true,
  diaChi: '',
  anhDaiDien: '',
  trangThai: true,
})

// ================== HELPER ==================

const isActiveStatus = (value) => {
  if (value === true || value === 1) return true

  const v = String(value || '')
    .trim()
    .toUpperCase()

  return v === 'TRUE' || v === '1' || v === 'HOAT_DONG' || v === 'ACTIVE'
}

const getTenQuyen = (nv) => {
  const value =
    nv?.tenQuyen ||
    nv?.phanQuyen?.tenQuyen ||
    nv?.tenQuyenNhanVien ||
    nv?.maQuyen ||
    nv?.phanQuyen?.maQuyen ||
    nv?.role ||
    ''

  const role = String(value).trim().toUpperCase()

  if (role === 'ADMIN' || role === 'QUAN_TRI') return 'Quản trị viên'
  if (role === 'STAFF' || role === 'NHAN_VIEN') return 'Nhân viên'

  return value || 'Chưa có quyền'
}

const getTrangThaiText = (value) => {
  return isActiveStatus(value) ? 'Hoạt động' : 'Ngừng hoạt động'
}

const getIdPhanQuyen = (nv) => {
  return nv?.idPhanQuyen || nv?.phanQuyen?.id || nv?.id_phan_quyen || ''
}

const normalizePhone = (value) => {
  let phone = String(value || '')
    .trim()
    .replaceAll(' ', '')
    .replaceAll('-', '')
    .replaceAll('.', '')

  if (phone.startsWith('+84')) {
    phone = '0' + phone.slice(3)
  }

  return phone
}

const getErrorMessage = (e, fallback) => {
  const data = e?.response?.data

  if (typeof data === 'string') return data

  if (data?.message) return data.message

  return fallback
}

const getAvatarUrl = (fileName) => {
  if (!fileName) {
    return 'https://ui-avatars.com/api/?background=dc2626&color=fff&name=GX'
  }

  if (String(fileName).startsWith('http') || String(fileName).startsWith('blob:')) {
    return fileName
  }

  return `http://localhost:8080/uploads/avatars/${fileName}`
}

const onImageError = (e) => {
  e.target.src = 'https://ui-avatars.com/api/?background=dc2626&color=fff&name=GX'
}

// ================== THỐNG KÊ ==================

const tongNhanVien = computed(() => danhSach.value.length)

const dangHoatDong = computed(
  () => danhSach.value.filter((i) => isActiveStatus(i.trangThai)).length,
)

const ngungHoatDong = computed(
  () => danhSach.value.filter((i) => !isActiveStatus(i.trangThai)).length,
)

// ================== SEARCH + FILTER ==================

const filteredNhanVien = computed(() => {
  let data = [...danhSach.value]

  if (keyword.value.trim()) {
    const key = keyword.value.trim().toLowerCase()

    data = data.filter((item) => {
      return (
        item.maNhanVien?.toLowerCase().includes(key) ||
        item.hoTen?.toLowerCase().includes(key) ||
        item.email?.toLowerCase().includes(key) ||
        item.soDienThoai?.includes(key)
      )
    })
  }

  if (filterTrangThai.value !== '') {
    data = data.filter((item) => {
      return isActiveStatus(item.trangThai) === (filterTrangThai.value === 'true')
    })
  }

  return data
})

// ================== LOAD ==================

const loadNhanVien = async () => {
  loading.value = true

  try {
    const res = await nhanVienService.getAll()

    console.log('===== NHAN VIEN DATA =====')
    console.log(res.data)

    const list = Array.isArray(res.data) ? res.data : res.data?.content || []

    danhSach.value = list.map((item) => ({
      ...item,
      tenQuyen: getTenQuyen(item),
      idPhanQuyen: Number(getIdPhanQuyen(item) || 2),
      trangThai: isActiveStatus(item.trangThai),
    }))
  } catch (e) {
    console.log(e)
    error('Không tải được dữ liệu')
  } finally {
    loading.value = false
  }
}

// ================== CLEAR ==================

const clearForm = () => {
  preview.value = ''
  errors.value = {}

  form.value = {
    id: null,
    idPhanQuyen: 2,
    tenQuyen: '',

    maNhanVien: '',
    hoTen: '',
    email: '',
    matKhau: '',
    soDienThoai: '',

    gioiTinh: true,
    diaChi: '',
    anhDaiDien: '',
    trangThai: true,
  }
}

// ================== CHỌN ==================

const selectNhanVien = (item) => {
  preview.value = ''

  form.value = {
    id: item.id,
    idPhanQuyen: Number(getIdPhanQuyen(item) || 2),
    tenQuyen: getTenQuyen(item),

    maNhanVien: item.maNhanVien || '',
    hoTen: item.hoTen || '',
    email: item.email || '',
    matKhau: '',
    soDienThoai: item.soDienThoai || '',

    gioiTinh: item.gioiTinh === true || item.gioiTinh === 1 || item.gioiTinh === 'true',
    diaChi: item.diaChi || '',
    anhDaiDien: item.anhDaiDien || '',
    trangThai: isActiveStatus(item.trangThai),
  }

  window.scrollTo({
    top: document.body.scrollHeight,
    behavior: 'smooth',
  })
}

// ================== VALIDATE ==================

const validateNhanVienForm = (data, isEdit = false) => {
  errors.value = {}

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  const phoneRegex = /^0(3|5|7|8|9)[0-9]{8}$/

  data.hoTen = String(data.hoTen || '')
    .trim()
    .replace(/\s+/g, ' ')
  data.email = String(data.email || '')
    .trim()
    .toLowerCase()
  data.soDienThoai = normalizePhone(data.soDienThoai)
  data.diaChi = String(data.diaChi || '').trim()

  if (!data.hoTen) {
    errors.value.hoTen = 'Vui lòng nhập họ tên'
  } else if (data.hoTen.length < 2 || data.hoTen.length > 100) {
    errors.value.hoTen = 'Họ tên phải từ 2 đến 100 ký tự'
  }

  if (!data.email) {
    errors.value.email = 'Vui lòng nhập email'
  } else if (!emailRegex.test(data.email)) {
    errors.value.email = 'Email không đúng định dạng'
  }

  if (!data.soDienThoai) {
    errors.value.soDienThoai = 'Vui lòng nhập số điện thoại'
  } else if (!phoneRegex.test(data.soDienThoai)) {
    errors.value.soDienThoai = 'Số điện thoại không đúng định dạng Việt Nam'
  }

  if (!data.idPhanQuyen) {
    errors.value.idPhanQuyen = 'Vui lòng chọn quyền'
  }

  if (!isEdit) {
    if (!data.matKhau || !String(data.matKhau).trim()) {
      errors.value.matKhau = 'Vui lòng nhập mật khẩu'
    } else if (String(data.matKhau).trim().length < 6) {
      errors.value.matKhau = 'Mật khẩu phải có ít nhất 6 ký tự'
    }
  } else if (data.matKhau && String(data.matKhau).trim().length < 6) {
    errors.value.matKhau = 'Mật khẩu phải có ít nhất 6 ký tự'
  }

  if (data.diaChi && data.diaChi.length > 255) {
    errors.value.diaChi = 'Địa chỉ không được vượt quá 255 ký tự'
  }

  const firstError = Object.values(errors.value)[0]

  if (firstError) {
    error(firstError)
    return false
  }

  return true
}

const buildPayload = () => {
  return {
    ...form.value,
    idPhanQuyen: Number(form.value.idPhanQuyen),
    trangThai: isActiveStatus(form.value.trangThai),
    gioiTinh:
      form.value.gioiTinh === true || form.value.gioiTinh === 1 || form.value.gioiTinh === 'true',
    matKhau: String(form.value.matKhau || '').trim(),
  }
}

// ================== ADD ==================

const addNhanVien = async () => {
  const payload = buildPayload()

  if (!validateNhanVienForm(payload, false)) return

  saving.value = true

  try {
    await nhanVienService.add(payload)

    success('Thêm nhân viên thành công')

    clearForm()
    await loadNhanVien()
  } catch (e) {
    console.log(e)
    error(getErrorMessage(e, 'Thêm thất bại'))
  } finally {
    saving.value = false
  }
}

// ================== UPDATE ==================

const updateNhanVien = async () => {
  if (!form.value.id) {
    error('Vui lòng chọn nhân viên cần cập nhật')
    return
  }

  const payload = buildPayload()

  if (!validateNhanVienForm(payload, true)) return

  saving.value = true

  try {
    await nhanVienService.update(form.value.id, payload)

    success('Cập nhật thành công')

    clearForm()
    await loadNhanVien()
  } catch (e) {
    console.log(e)
    error(getErrorMessage(e, 'Cập nhật thất bại'))
  } finally {
    saving.value = false
  }
}

// ================== DELETE ==================

const deleteNhanVien = async (id) => {
  const result = await confirmDelete()

  if (!result.isConfirmed) return

  try {
    await nhanVienService.delete(id)

    success('Đã xóa nhân viên')

    clearForm()
    await loadNhanVien()
  } catch (e) {
    console.log(e)
    error(getErrorMessage(e, 'Không thể xóa'))
  }
}

// ================== AVATAR ==================

const chooseAvatar = async (event) => {
  const file = event.target.files[0]

  if (!file) return

  preview.value = URL.createObjectURL(file)

  try {
    const res = await uploadService.upload(file)

    form.value.anhDaiDien = res.data

    success('Upload ảnh thành công')
  } catch (e) {
    console.log(e)
    error('Upload thất bại')
  }
}

// ================== INIT ==================

onMounted(() => {
  loadNhanVien()
})
</script>

<style scoped>
/* ====================== PAGE ====================== */

.page {
  padding: 30px;
  background: #f5f7fb;
  min-height: 100vh;
}

/* ====================== HEADER ====================== */

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.page-header h2 {
  margin: 0;
  color: #dc2626;
  font-size: 30px;
  font-weight: 700;
}

.page-header p {
  margin-top: 6px;
  color: #6b7280;
}

/* ====================== SUMMARY ====================== */

.summary {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.summary-card {
  background: white;
  border-radius: 14px;
  padding: 24px;
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.08);
  transition: 0.25s;
}

.summary-card:hover {
  transform: translateY(-5px);
}

.summary-card h4 {
  margin-bottom: 10px;
  color: #666;
  font-weight: 500;
}

.summary-card h2 {
  margin: 0;
  font-size: 34px;
  color: #dc2626;
}

.summary-card.success h2 {
  color: #16a34a;
}

.summary-card.danger h2 {
  color: #ef4444;
}

/* ====================== TOOLBAR ====================== */

.toolbar {
  display: flex;
  gap: 15px;
  margin-bottom: 25px;
}

.toolbar input,
.toolbar select {
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: white;
  transition: 0.25s;
}

.toolbar input {
  flex: 1;
}

.toolbar input:focus,
.toolbar select:focus {
  outline: none;
  border-color: #dc2626;
  box-shadow: 0 0 0 4px rgba(220, 38, 38, 0.12);
}

/* ====================== TABLE ====================== */

.table-card {
  background: white;
  border-radius: 12px;
  overflow-x: auto;
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
}

table {
  width: 100%;
  border-collapse: collapse;
  min-width: 1200px;
}

thead th {
  background: #111827;
  color: white;
  padding: 15px;
  text-align: center;
  white-space: nowrap;
}

tbody td {
  padding: 14px;
  border-bottom: 1px solid #eee;
  text-align: center;
}

tbody tr {
  transition: 0.25s;
}

tbody tr:hover {
  background: #f8fafc;
  cursor: pointer;
}

.empty {
  padding: 40px;
  text-align: center;
  color: #999;
}

/* ====================== AVATAR ====================== */

.avatar {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e5e7eb;
}

/* ====================== BADGE ====================== */

.badge {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 30px;
  font-size: 13px;
  color: white;
  font-weight: 600;
}

.active {
  background: #16a34a;
}

.inactive {
  background: #ef4444;
}

.role {
  background: #2563eb;
}

/* ====================== FORM ====================== */

.form-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.08);
}

.form-card h3 {
  margin-bottom: 25px;
  color: #dc2626;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 8px;
  font-weight: 600;
}

.form-group input,
.form-group select {
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  transition: 0.25s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #dc2626;
  box-shadow: 0 0 0 4px rgba(220, 38, 38, 0.12);
}

/* ====================== BUTTON ====================== */

.button-group {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

button {
  border: none;
  padding: 11px 22px;
  border-radius: 8px;
  cursor: pointer;
  color: white;
  font-weight: 600;
  transition: 0.25s;
}

button:hover {
  transform: translateY(-2px);
  opacity: 0.92;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-add,
.btn-save {
  background: #16a34a;
}

.btn-update {
  background: #2563eb;
}

.btn-refresh {
  background: #6b7280;
}

.btn-edit {
  background: #f59e0b;
  color: black;
  margin-right: 6px;
}

.btn-delete {
  background: #dc2626;
}

.btn-reset,
.btn-cancel {
  background: #6b7280;
}

/* ====================== LOADING ====================== */

.loading {
  background: white;
  padding: 30px;
  border-radius: 10px;
  text-align: center;
  font-size: 18px;
  color: #2563eb;
  font-weight: bold;
}

/* ====================== SCROLL ====================== */

.table-card::-webkit-scrollbar {
  height: 8px;
}

.table-card::-webkit-scrollbar-thumb {
  background: #c4c4c4;
  border-radius: 20px;
}

.table-card::-webkit-scrollbar-track {
  background: #f1f1f1;
}

/* ====================== RESPONSIVE ====================== */

@media (max-width: 992px) {
  .summary {
    grid-template-columns: 1fr;
  }

  .grid {
    grid-template-columns: 1fr;
  }

  .toolbar {
    flex-direction: column;
  }

  .button-group {
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .page {
    padding: 15px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .page-header h2 {
    font-size: 24px;
  }

  table {
    min-width: 1100px;
    font-size: 14px;
  }

  .button-group {
    flex-direction: column;
  }

  .button-group button {
    width: 100%;
  }
}

.btn-cancel {
  background: #6b7280;
}

/* ================= LOADING ================= */

.loading {
  background: white;
  padding: 30px;
  text-align: center;
  border-radius: 10px;
  color: #2563eb;
  font-size: 18px;
  font-weight: bold;
}

/* ================= SCROLL ================= */

.table-card::-webkit-scrollbar {
  height: 8px;
}

.table-card::-webkit-scrollbar-thumb {
  background: #bbb;
  border-radius: 20px;
}

/* ================= RESPONSIVE ================= */

@media (max-width: 992px) {
  .summary {
    grid-template-columns: 1fr;
  }

  .grid {
    grid-template-columns: 1fr;
  }

  .toolbar {
    flex-direction: column;
  }

  .actions {
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .page {
    padding: 15px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .page-header h2 {
    font-size: 24px;
  }

  table {
    font-size: 14px;
  }

  button {
    width: 100%;
  }

  .actions {
    flex-direction: column;
  }
}

.avatar-box {
  margin-top: 15px;
}

.avatar {
  width: 130px;

  height: 130px;

  object-fit: cover;

  border-radius: 50%;

  border: 3px solid #ddd;
}

.table-avatar {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e5e7eb;
}

.avatar-preview {
  width: 130px;
  height: 130px;
  object-fit: cover;
  border-radius: 50%;
  border: 3px solid #ddd;
}

.col-role,
.col-status {
  min-width: 130px;
  text-align: center;
  overflow: visible;
}

.nv-badge {
  display: inline-flex !important;
  align-items: center;
  justify-content: center;
  min-width: 105px;
  padding: 7px 12px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  line-height: 1;
  white-space: nowrap;
  color: #ffffff !important;
  visibility: visible !important;
  opacity: 1 !important;
}

.nv-role {
  background: #2563eb !important;
}

.nv-active {
  background: #16a34a !important;
}

.nv-inactive {
  background: #ef4444 !important;
}
</style>

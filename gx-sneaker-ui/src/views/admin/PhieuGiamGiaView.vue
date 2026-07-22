<script setup>
import { ref, onMounted, computed } from "vue"
import { getAll, create, update, remove } from "@/services/phieuGiamGiaService"

const list = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = 10

const form = ref({
  id: null,
  maPhieu: "",
  tenPhieu: "",
  loaiGiamGia: true, // true: Phần trăm, false: Số tiền cố định
  giaTriGiam: 0,
  giaTriDonHangToiThieu: 0,
  giaTriGiamToiDa: 0,
  soLuong: 0,
  ngayBatDau: "",
  ngayKetThuc: "",
  trangThai: true,
  kieuPhieu: "PUBLIC",
  dieuKienHangThanhVien: "BRONZE"
})

// FORMAT CURRENCY / PERCENT
const formatDiscount = (item) => {
  if (item.loaiGiamGia) {
    return `${item.giaTriGiam}% (Tối đa ${formatPrice(item.giaTriGiamToiDa)})`
  }
  return formatPrice(item.giaTriGiam)
}

const formatPrice = (price) => {
  if (!price) return "0đ"
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(price)
}

const formatDate = (date) => {
  if (!date) return ""
  return new Date(date).toLocaleString("vi-VN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  })
}

const formatDateTimeLocal = (date) => {
  if (!date) return ""
  const d = new Date(date)
  const pad = (n) => (n < 10 ? "0" + n : n)
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`
}

// LOAD DATA
const load = async () => {
  loading.value = true
  try {
    const res = await getAll()
    list.value = res.data
  } catch (err) {
    console.error("Lỗi load dữ liệu:", err)
  } finally {
    loading.value = false
  }
}

onMounted(load)

// RESET
const reset = () => {
  form.value = {
    id: null,
    maPhieu: "",
    tenPhieu: "",
    loaiGiamGia: true,
    giaTriGiam: 0,
    giaTriDonHangToiThieu: 0,
    giaTriGiamToiDa: 0,
    soLuong: 0,
    ngayBatDau: "",
    ngayKetThuc: "",
    trangThai: true,
    kieuPhieu: "PUBLIC",
    dieuKienHangThanhVien: "BRONZE"
  }
}

// SAVE
const save = async () => {
  if (!form.value.maPhieu || !form.value.maPhieu.trim()) {
    alert("Mã phiếu giảm giá không được để trống!");
    return;
  }
  if (!form.value.tenPhieu || !form.value.tenPhieu.trim()) {
    alert("Tên phiếu giảm giá không được để trống!");
    return;
  }
  if (form.value.giaTriGiam <= 0) {
    alert("Giá trị giảm phải lớn hơn 0!");
    return;
  }
  if (form.value.loaiGiamGia && form.value.giaTriGiam > 100) {
    alert("Giá trị giảm theo phần trăm không được vượt quá 100%!");
    return;
  }
  if (form.value.giaTriDonHangToiThieu < 0) {
    alert("Giá trị đơn hàng tối thiểu không được nhỏ hơn 0!");
    return;
  }
  if (form.value.loaiGiamGia && form.value.giaTriGiamToiDa < 0) {
    alert("Giá trị giảm tối đa không được nhỏ hơn 0!");
    return;
  }
  if (form.value.soLuong < 0) {
    alert("Số lượng phát hành không được nhỏ hơn 0!");
    return;
  }
  if (!form.value.ngayBatDau) {
    alert("Ngày bắt đầu không được để trống!");
    return;
  }
  if (!form.value.ngayKetThuc) {
    alert("Ngày kết thúc không được để trống!");
    return;
  }
  if (new Date(form.value.ngayKetThuc) < new Date(form.value.ngayBatDau)) {
    alert("Ngày kết thúc phải sau ngày bắt đầu!");
    return;
  }

  try {
    const data = { ...form.value }
    data.maPhieu = data.maPhieu.trim().toUpperCase()
    data.ngayBatDau = new Date(data.ngayBatDau)
    data.ngayKetThuc = new Date(data.ngayKetThuc)
    
    if (form.value.id) {
      await update(form.value.id, data)
      alert("Cập nhật phiếu giảm giá thành công!");
    } else {
      await create(data)
      alert("Tạo phiếu giảm giá thành công!");
    }
    reset()
    load()
  } catch (err) {
    console.error(err)
    alert(err.response?.data?.message || "Lỗi khi lưu phiếu giảm giá!");
  }
}

// EDIT
const edit = (item) => {
  form.value = {
    ...item,
    ngayBatDau: formatDateTimeLocal(item.ngayBatDau),
    ngayKetThuc: formatDateTimeLocal(item.ngayKetThuc)
  }
}

// DELETE
const del = async (id) => {
  if (!confirm("Bạn có chắc muốn xóa phiếu giảm giá này?")) return
  try {
    await remove(id)
    load()
  } catch (err) {
    console.error(err)
  }
}

// PAGINATION
const totalPages = computed(() => Math.ceil(list.value.length / pageSize))
const paginatedList = computed(() => {
  const start = (page.value - 1) * pageSize
  return list.value.slice(start, start + pageSize)
})
const changePage = (p) => {
  page.value = p
}
</script>

<template>
  <div class="container">
    <h2 class="title">🎟 QUẢN LÝ PHIẾU GIẢM GIÁ</h2>

    <!-- FORM BOX -->
    <div class="card form-box">
      <h3>{{ form.id ? "Cập nhật phiếu giảm giá" : "Tạo phiếu giảm giá mới" }}</h3>
      
      <div class="grid">
        <div class="form-group">
          <label>Mã phiếu <span class="required">*</span></label>
          <input v-model="form.maPhieu" placeholder="Ví dụ: KHUYENMAI20" style="text-transform: uppercase;" />
        </div>
        <div class="form-group">
          <label>Tên chương trình <span class="required">*</span></label>
          <input v-model="form.tenPhieu" placeholder="Tên chương trình / Tên phiếu" />
        </div>
      </div>

      <div class="grid">
        <div class="form-group">
          <label>Loại giảm giá</label>
          <select v-model="form.loaiGiamGia">
            <option :value="true">Giảm theo %</option>
            <option :value="false">Giảm tiền mặt trực tiếp</option>
          </select>
        </div>
        <div class="form-group">
          <label>Kiểu phiếu / Đối tượng</label>
          <select v-model="form.kieuPhieu">
            <option value="PUBLIC">Tất cả mọi người (PUBLIC)</option>
            <option value="NEW_CUSTOMER">Khách hàng mới (NEW_CUSTOMER)</option>
            <option value="MEMBER_ONLY">Chỉ dành cho Hội viên (MEMBER_ONLY)</option>
            <option value="HOLIDAY">Dịp lễ hội (HOLIDAY)</option>
            <option value="PERSONAL">Tặng riêng cá nhân (PERSONAL)</option>
          </select>
        </div>
      </div>
      
      <div class="grid" v-if="form.kieuPhieu === 'MEMBER_ONLY'">
        <div class="form-group">
          <label>Hạng thẻ yêu cầu</label>
          <select v-model="form.dieuKienHangThanhVien">
            <option value="BRONZE">Đồng (BRONZE)</option>
            <option value="SILVER">Bạc (SILVER)</option>
            <option value="GOLD">Vàng (GOLD)</option>
            <option value="DIAMOND">Kim cương (DIAMOND)</option>
          </select>
        </div>
        <div class="form-group">
          <!-- Placeholder to maintain grid -->
        </div>
      </div>

      <div class="grid">
        <div class="form-group">
          <label>Giá trị giảm <span class="required">*</span></label>
          <input v-model="form.giaTriGiam" type="number" :placeholder="form.loaiGiamGia ? 'Nhập % giảm (Ví dụ: 10)' : 'Nhập số tiền giảm (Ví dụ: 50000)'" />
        </div>
      </div>

      <div class="grid">
        <div class="form-group">
          <label>Đơn hàng tối thiểu <span class="required">*</span></label>
          <input v-model="form.giaTriDonHangToiThieu" type="number" placeholder="Giá trị đơn hàng tối thiểu" />
        </div>
        <div class="form-group">
          <label>Giảm tối đa (nếu giảm theo %)</label>
          <input v-model="form.giaTriGiamToiDa" type="number" placeholder="Giá trị giảm tối đa" :disabled="!form.loaiGiamGia" />
        </div>
      </div>

      <div class="grid">
        <div class="form-group">
          <label>Số lượng phát hành <span class="required">*</span></label>
          <input v-model="form.soLuong" type="number" placeholder="Số lượng phát hành" />
        </div>
        <div class="form-group">
          <label>Trạng thái</label>
          <select v-model="form.trangThai">
            <option :value="true">Hoạt động</option>
            <option :value="false">Tạm ẩn / Đóng</option>
          </select>
        </div>
      </div>

      <div class="grid">
        <div class="form-group">
          <label>Ngày bắt đầu <span class="required">*</span></label>
          <input v-model="form.ngayBatDau" type="datetime-local" />
        </div>
        <div class="form-group">
          <label>Ngày kết thúc <span class="required">*</span></label>
          <input v-model="form.ngayKetThuc" type="datetime-local" />
        </div>
      </div>

      <!-- Live Summary Card -->
      <div class="preview-card mt-2">
        <strong>💡 Tóm tắt chương trình:</strong>
        <p v-if="form.loaiGiamGia">
          Giảm <span class="text-primary">{{ form.giaTriGiam || 0 }}%</span> cho đơn hàng có giá trị từ 
          <span class="text-primary">{{ formatPrice(form.giaTriDonHangToiThieu) }}</span> trở lên. 
          Mức giảm tối đa là <span class="text-primary">{{ formatPrice(form.giaTriGiamToiDa) }}</span>.
        </p>
        <p v-else>
          Giảm <span class="text-primary">{{ formatPrice(form.giaTriGiam) }}</span> trực tiếp cho đơn hàng có giá trị từ 
          <span class="text-primary">{{ formatPrice(form.giaTriDonHangToiThieu) }}</span> trở lên.
        </p>
      </div>

      <div class="actions">
        <button class="btn-save" @click="save">💾 Lưu phiếu giảm giá</button>
        <button class="btn-reset" @click="reset">Reset</button>
      </div>
    </div>

    <!-- TABLE -->
    <div class="card">
      <table v-if="list.length > 0">
        <thead>
          <tr>
            <th>Mã</th>
            <th>Tên phiếu</th>
            <th>Mức giảm</th>
            <th>Đơn tối thiểu</th>
            <th>Số lượng</th>
            <th>Ngày bắt đầu</th>
            <th>Ngày kết thúc</th>
            <th>Loại phiếu</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="i in paginatedList" :key="i.id">
            <td><strong>{{ i.maPhieu }}</strong></td>
            <td>{{ i.tenPhieu }}</td>
            <td>{{ formatDiscount(i) }}</td>
            <td>{{ formatPrice(i.giaTriDonHangToiThieu) }}</td>
            <td>{{ i.soLuong }}</td>
            <td>{{ formatDate(i.ngayBatDau) }}</td>
            <td>{{ formatDate(i.ngayKetThuc) }}</td>
            <td>
              <span class="badge bg-secondary">{{ i.kieuPhieu }}</span>
              <div v-if="i.kieuPhieu === 'MEMBER_ONLY'" style="font-size: 11px; color: #666;">
                Hạng: {{ i.dieuKienHangThanhVien }}
              </div>
            </td>
            <td>
              <span :class="i.trangThai ? 'active' : 'hidden'">
                {{ i.trangThai ? "Đang chạy" : "Đã đóng" }}
              </span>
            </td>
            <td class="btn-group">
              <button @click="edit(i)" title="Sửa">✏️</button>
              <button @click="del(i.id)" title="Xóa">🗑</button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div class="pagination" v-if="totalPages > 1">
        <button @click="page--" :disabled="page === 1">Trước</button>
        <button v-for="p in totalPages" :key="p" @click="changePage(p)" :class="{ activePage: p === page }">
          {{ p }}
        </button>
        <button @click="page++" :disabled="page === totalPages">Sau</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  padding: 24px;
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
  background-color: #f8f9fa;
  min-height: 100vh;
}
.title {
  font-size: 24px;
  font-weight: 700;
  color: #212529;
  margin-bottom: 20px;
  text-align: left;
}
.card {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  border: 1px solid #e9ecef;
}
.form-box h3 {
  font-size: 18px;
  font-weight: 600;
  color: #343a40;
  margin-bottom: 20px;
  text-align: left;
}
.form-group {
  display: flex;
  flex-direction: column;
  text-align: left;
}
.form-group label {
  font-size: 13px;
  font-weight: 600;
  color: #495057;
  margin-bottom: 6px;
}
.required {
  color: #dc3545;
  margin-left: 2px;
}
.form-box input, .form-box select {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 14px;
  color: #212529;
  background-color: #fff;
  transition: border-color 0.2s, box-shadow 0.2s;
  margin-bottom: 12px;
}
.form-box input:focus, .form-box select:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.15);
  outline: none;
}
.form-box input:disabled {
  background-color: #e9ecef;
  color: #6c757d;
  cursor: not-allowed;
  border-color: #dee2e6;
}
.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.preview-card {
  background-color: #f1f8ff;
  border-left: 4px solid #0366d6;
  padding: 12px 16px;
  border-radius: 6px;
  text-align: left;
  font-size: 13.5px;
  color: #24292e;
  margin: 8px 0 16px 0;
}
.preview-card p {
  margin: 4px 0 0 0;
  line-height: 1.5;
}
.text-primary {
  color: #0d6efd;
  font-weight: 700;
}
.actions {
  display: flex;
  gap: 10px;
  justify-content: flex-start;
  margin-top: 8px;
}
button {
  cursor: pointer;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 600;
  border: none;
  border-radius: 6px;
  transition: background-color 0.2s, transform 0.1s;
}
button:active {
  transform: scale(0.98);
}
.btn-save {
  background: #0d6efd;
  color: white;
}
.btn-save:hover {
  background: #0b5ed7;
}
.btn-reset {
  background: #6c757d;
  color: white;
}
.btn-reset:hover {
  background: #5c636a;
}
.active {
  background-color: #d1e7dd;
  color: #0f5132;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}
.hidden {
  background-color: #f8d7da;
  color: #842029;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th {
  background-color: #f8f9fa;
  color: #495057;
  font-weight: 600;
  font-size: 13px;
  text-transform: uppercase;
  padding: 12px 16px;
  border-bottom: 2px solid #dee2e6;
  text-align: left;
}
td {
  padding: 14px 16px;
  border-bottom: 1px solid #dee2e6;
  font-size: 14px;
  color: #212529;
  text-align: left;
}
.btn-group {
  display: flex;
  gap: 6px;
}
.btn-group button {
  padding: 6px 10px;
  background: #f8f9fa;
  border: 1px solid #ced4da;
  font-size: 12px;
}
.btn-group button:hover {
  background: #e9ecef;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 6px;
}
.pagination button {
  padding: 8px 12px;
  background: #fff;
  border: 1px solid #dee2e6;
  color: #0d6efd;
}
.pagination button:hover:not(:disabled) {
  background: #e9ecef;
}
.pagination button:disabled {
  color: #6c757d;
  cursor: not-allowed;
  background-color: #f8f9fa;
}
.activePage {
  background: #0d6efd !important;
  color: white !important;
  border-color: #0d6efd !important;
}
</style>

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
  trangThai: true
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
  return new Date(date).toLocaleDateString("vi-VN")
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
    trangThai: true
  }
}

// SAVE
const save = async () => {
  try {
    const data = { ...form.value }
    // Convert datetimes
    if (data.ngayBatDau) data.ngayBatDau = new Date(data.ngayBatDau)
    if (data.ngayKetThuc) data.ngayKetThuc = new Date(data.ngayKetThuc)
    
    if (form.value.id) {
      await update(form.value.id, data)
    } else {
      await create(data)
    }
    reset()
    load()
  } catch (err) {
    console.error(err)
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
        <input v-model="form.maPhieu" placeholder="Mã phiếu (Ví dụ: KHUYENMAI20)" />
        <input v-model="form.tenPhieu" placeholder="Tên chương trình / Tên phiếu" />
      </div>

      <div class="grid">
        <select v-model="form.loaiGiamGia">
          <option :value="true">Giảm theo %</option>
          <option :value="false">Giảm tiền mặt trực tiếp</option>
        </select>
        <input v-model="form.giaTriGiam" type="number" placeholder="Giá trị giảm (% hoặc VNĐ)" />
      </div>

      <div class="grid">
        <input v-model="form.giaTriDonHangToiThieu" type="number" placeholder="Giá trị đơn hàng tối thiểu" />
        <input v-model="form.giaTriGiamToiDa" type="number" placeholder="Giá trị giảm tối đa (nếu giảm theo %)" :disabled="!form.loaiGiamGia" />
      </div>

      <div class="grid">
        <input v-model="form.soLuong" type="number" placeholder="Số lượng phát hành" />
        <select v-model="form.trangThai">
          <option :value="true">Hoạt động</option>
          <option :value="false">Tạm ẩn / Đóng</option>
        </select>
      </div>

      <div class="grid">
        <div>
          <label>Ngày bắt đầu:</label>
          <input v-model="form.ngayBatDau" type="datetime-local" />
        </div>
        <div>
          <label>Ngày kết thúc:</label>
          <input v-model="form.ngayKetThuc" type="datetime-local" />
        </div>
      </div>

      <div class="actions">
        <button class="btn-save" @click="save">💾 Lưu</button>
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
  padding: 20px;
  font-family: Inter, Arial, sans-serif;
}
.title {
  margin-bottom: 15px;
}
.card {
  background: #fff;
  padding: 15px;
  border-radius: 10px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.form-box input, .form-box select {
  width: 100%;
  margin: 5px 0;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.form-box label {
  font-size: 12px;
  color: #555;
  display: block;
  margin-top: 5px;
}
.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.actions {
  margin-top: 10px;
}
button {
  margin-right: 5px;
  cursor: pointer;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
}
.btn-save {
  background: green;
  color: white;
}
.btn-reset {
  background: gray;
  color: white;
}
.active {
  color: green;
  font-weight: bold;
}
.hidden {
  color: red;
  font-weight: bold;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  padding: 10px;
  border-bottom: 1px solid #ddd;
  text-align: left;
}
.btn-group button {
  background: #f0f0f0;
}
.btn-group button:hover {
  background: #e0e0e0;
}
.pagination {
  margin-top: 15px;
  display: flex;
  justify-content: center;
  gap: 5px;
}
.pagination button {
  background: #e9ecef;
}
.activePage {
  background: #0d6efd !important;
  color: white;
}
</style>

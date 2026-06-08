<script setup>
import { ref, onMounted, computed } from "vue"
import axios from "axios"

const API = "http://localhost:8080/api/xuat-xu"

// ================= DATA =================
const list = ref([])

// ================= SEARCH + FILTER =================
const search = ref("")
const filterStatus = ref("all")

// ================= PAGINATION =================
const page = ref(1)
const pageSize = 5

// ================= FORM =================
const form = ref({
  id: null,
  ma: "",
  ten: "",
  moTa: "",
  trangThai: true
})

// ================= ERROR =================
const errors = ref({
  ma: "",
  ten: ""
})

// ================= TOAST =================
const toast = ref("")
const toastType = ref("success")

const showToast = (msg, type = "success") => {
  toast.value = msg
  toastType.value = type
  setTimeout(() => (toast.value = ""), 2000)
}

// ================= LOAD DATA =================
const load = async () => {
  const res = await axios.get(API)
  list.value = res.data
}

// ================= VALIDATE =================
const validate = () => {
  let ok = true
  errors.value = { ma: "", ten: "" }

  if (!form.value.ma.trim()) {
    errors.value.ma = "Mã không được để trống"
    ok = false
  }

  if (!form.value.ten.trim()) {
    errors.value.ten = "Tên không được để trống"
    ok = false
  }

  return ok
}

// ================= SAVE =================
const save = async () => {
  if (!validate()) {
    showToast("Dữ liệu chưa hợp lệ", "error")
    return
  }

  try {
    if (form.value.id) {
      await axios.put(`${API}/${form.value.id}`, form.value)
      showToast("Cập nhật thành công")
    } else {
      await axios.post(API, form.value)
      showToast("Thêm thành công")
    }

    reset()
    load()

  } catch (e) {
    showToast("Lỗi server", "error")
  }
}

// ================= EDIT =================
const edit = (item) => {
  form.value = { ...item }
}

// ================= DELETE =================
const del = async (id) => {
  if (!confirm("Xóa mục này?")) return

  await axios.delete(`${API}/${id}`)
  showToast("Xóa thành công")
  load()
}

// ================= RESET =================
const reset = () => {
  form.value = {
    id: null,
    ma: "",
    ten: "",
    moTa: "",
    trangThai: true
  }

  errors.value = { ma: "", ten: "" }
}

// ================= FILTER DATA =================
const filteredList = computed(() => {
  let data = list.value

  if (search.value) {
    data = data.filter(i =>
      i.ten.toLowerCase().includes(search.value.toLowerCase()) ||
      i.ma.toLowerCase().includes(search.value.toLowerCase())
    )
  }

  if (filterStatus.value !== "all") {
    const status = filterStatus.value === "true"
    data = data.filter(i => i.trangThai === status)
  }

  return data
})

// ================= PAGINATION =================
const totalPages = computed(() =>
  Math.ceil(filteredList.value.length / pageSize)
)

const paginatedList = computed(() => {
  const start = (page.value - 1) * pageSize
  return filteredList.value.slice(start, start + pageSize)
})

const changePage = (p) => {
  page.value = p
}

onMounted(load)
</script>

<template>
  <div class="container">

    <h2>🌍 QUẢN LÝ XUẤT XỨ</h2>

    <!-- TOAST -->
    <div v-if="toast" :class="['toast', toastType]">
      {{ toast }}
    </div>

    <!-- SEARCH + FILTER -->
    <div class="card top-bar">
      <input v-model="search" placeholder="🔍 Tìm kiếm mã / tên..." />

      <select v-model="filterStatus">
        <option value="all">Tất cả</option>
        <option value="true">Hoạt động</option>
        <option value="false">Ẩn</option>
      </select>
    </div>

    <!-- FORM -->
    <div class="card form">

      <div class="grid">

        <div>
          <input v-model="form.ma" placeholder="Mã xuất xứ" />
          <small class="error">{{ errors.ma }}</small>
        </div>

        <div>
          <input v-model="form.ten" placeholder="Tên xuất xứ" />
          <small class="error">{{ errors.ten }}</small>
        </div>

      </div>

      <textarea v-model="form.moTa" placeholder="Mô tả"></textarea>

      <div class="actions">
        <button class="save" @click="save">
          {{ form.id ? "Cập nhật" : "Thêm mới" }}
        </button>
        <button class="reset" @click="reset">Reset</button>
      </div>

    </div>

    <!-- TABLE -->
    <div class="card">

      <table>
        <thead>
        <tr>
          <th>Mã</th>
          <th>Tên</th>
          <th>Mô tả</th>
          <th>Trạng thái</th>
          <th>Hành động</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="i in paginatedList" :key="i.id">

          <td>{{ i.ma }}</td>
          <td>{{ i.ten }}</td>
          <td>{{ i.moTa }}</td>

          <td>
              <span :class="i.trangThai ? 'active' : 'inactive'">
                {{ i.trangThai ? "Active" : "Hidden" }}
              </span>
          </td>

          <td>
            <button class="edit" @click="edit(i)">✏️</button>
            <button class="delete" @click="del(i.id)">🗑</button>
          </td>

        </tr>
        </tbody>

      </table>

      <!-- PAGINATION -->
      <div class="pagination">
        <button
          v-for="p in totalPages"
          :key="p"
          @click="changePage(p)"
          :class="{ activePage: p === page }"
        >
          {{ p }}
        </button>
      </div>

    </div>

  </div>
</template>

<style scoped>
.container {
  padding: 20px;
  font-family: Arial;
  background: #f4f6f9;
}

.card {
  background: white;
  padding: 15px;
  border-radius: 10px;
  margin-bottom: 15px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.08);
}

/* TOP BAR */
.top-bar {
  display: flex;
  gap: 10px;
}

/* FORM */
.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

input, textarea, select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  width: 100%;
}

textarea {
  margin-top: 10px;
}

/* ERROR */
.error {
  color: #00ff80;
  font-size: 12px;
}

/* BUTTON */
.actions {
  margin-top: 10px;
}

.save {
  background: #28a745;
  color: white;
  padding: 8px 12px;
  border: none;
}

.reset {
  background: #6c757d;
  color: white;
  padding: 8px 12px;
  border: none;
}

/* TABLE */
table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 10px;
  text-align: center;
  border-bottom: 1px solid #eee;
}

/* STATUS */
.active {
  color: green;
  font-weight: bold;
}

.inactive {
  color: red;
  font-weight: bold;
}

/* BUTTON TABLE */
.edit {
  background: #007bff;
  color: white;
  border: none;
  padding: 5px 8px;
}

.delete {
  background: red;
  color: white;
  border: none;
  padding: 5px 8px;
}

/* PAGINATION */
.pagination {
  margin-top: 10px;
  text-align: center;
}

.pagination button {
  margin: 2px;
  padding: 5px 10px;
  border: none;
  cursor: pointer;
}

.activePage {
  background: #007bff;
  color: white;
}

/* TOAST */
.toast {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 10px 15px;
  border-radius: 6px;
  color: white;
}

.success {
  background: #28a745;
}

.error {
  background: #f10606;
}
</style>

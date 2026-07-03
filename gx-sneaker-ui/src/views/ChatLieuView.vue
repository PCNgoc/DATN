<script setup>
import { ref, onMounted, computed } from "vue"
import axios from "axios"

const API = "http://localhost:8080/api/chat-lieu"

// ================= DATA =================
const list = ref([])

// ================= SEARCH + FILTER =================
const search = ref("")
const filterStatus = ref("all")

// ================= PAGINATION =================
const page = ref(1)
const pageSize = 10

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

    <h2>🌍 QUẢN LÝ CHẤT LIỆU</h2>

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
    <!-- FORM -->
    <div class="card form">

      <div class="grid">

        <!-- Mã chất liệu -->
        <div>
          <input v-model="form.ma" placeholder="Mã chất liệu" />
          <small class="error-text">{{ errors.ma }}</small>
        </div>

        <!-- Tên chất liệu -->
        <div>
          <input v-model="form.ten" placeholder="Tên chất liệu" />
          <small class="error-text">{{ errors.ten }}</small>
        </div>

      </div>

      <!-- Mô tả -->
      <textarea
        v-model="form.moTa"
        placeholder="Mô tả chất liệu">
  </textarea>

      <!-- Trạng thái -->
      <div class="status-box">

        <label>Trạng thái</label>

        <select v-model="form.trangThai">
          <option :value="true">Hoạt động</option>
          <option :value="false">Ngừng hoạt động</option>
        </select>

      </div>

      <!-- Button -->
      <div class="actions">

        <button class="save" @click="save">
          {{ form.id ? "Cập nhật" : "Thêm mới" }}
        </button>

        <button class="reset" @click="reset">
          Reset
        </button>

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
              {{ i.trangThai ? "Hoạt động" : "Ngừng hoạt động" }}
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
*{
  margin:0;
  padding:0;
  box-sizing:border-box;
}

.container{
  padding:25px;
  background:#f5f7fb;
  min-height:100vh;
  font-family:"Segoe UI",sans-serif;
}

h2{
  margin-bottom:20px;
  color:#2c3e50;
  font-size:28px;
  font-weight:700;
}

/* CARD */

.card{
  background:#fff;
  border-radius:14px;
  padding:20px;
  margin-bottom:20px;
  box-shadow:0 8px 20px rgba(0,0,0,.06);
}

/* ================= TOP BAR ================= */

.top-bar{
  display:flex;
  gap:15px;
}

.top-bar input{
  flex:1;
}

/* ================= FORM ================= */

.grid{
  display:grid;
  grid-template-columns:repeat(2,1fr);
  gap:18px;
}

input,
textarea,
select{
  width:100%;
  padding:11px 14px;
  border:1px solid #dcdfe6;
  border-radius:8px;
  outline:none;
  font-size:15px;
  transition:.25s;
  background:#fff;
}

textarea{
  margin-top:15px;
  min-height:100px;
  resize:vertical;
}

input:focus,
textarea:focus,
select:focus{
  border-color:#409EFF;
  box-shadow:0 0 0 4px rgba(64,158,255,.15);
}

/* STATUS */

.status-box{
  margin-top:18px;
}

.status-box label{
  display:block;
  margin-bottom:8px;
  font-weight:600;
  color:#444;
}

/* ================= BUTTON ================= */

.actions{
  margin-top:20px;
  display:flex;
  gap:12px;
}

button{
  cursor:pointer;
  border:none;
  transition:.25s;
  border-radius:8px;
}

.save{
  background:#1677ff;
  color:white;
  padding:10px 20px;
}

.save:hover{
  background:#0958d9;
}

.reset{
  background:#6c757d;
  color:white;
  padding:10px 20px;
}

.reset:hover{
  background:#545b62;
}

/* ================= TABLE ================= */

table{
  width:100%;
  border-collapse:collapse;
  overflow:hidden;
}

thead{
  background:#1677ff;
  color:#fff;
}

th{
  padding:14px;
  font-weight:600;
}

td{
  padding:14px;
  border-bottom:1px solid #edf2f7;
}

tbody tr{
  transition:.25s;
}

tbody tr:hover{
  background:#f5f9ff;
}

/* ================= STATUS ================= */

.active{
  display:inline-block;
  background:#e8fff0;
  color:#1aa053;
  padding:6px 14px;
  border-radius:30px;
  font-size:13px;
  font-weight:600;
}

.inactive{
  display:inline-block;
  background:#ffe9e9;
  color:#e53935;
  padding:6px 14px;
  border-radius:30px;
  font-size:13px;
  font-weight:600;
}

/* ================= BUTTON TABLE ================= */

.edit,
.delete{
  width:38px;
  height:38px;
  border-radius:8px;
  color:white;
  font-size:15px;
}

.edit{
  background:#1677ff;
}

.edit:hover{
  background:#0958d9;
}

.delete{
  background:#f5222d;
  margin-left:8px;
}

.delete:hover{
  background:#cf1322;
}

/* ================= PAGINATION ================= */

.pagination{
  margin-top:25px;
  display:flex;
  justify-content:center;
  gap:8px;
}

.pagination button{
  width:38px;
  height:38px;
  border-radius:8px;
  background:#fff;
  border:1px solid #ddd;
}

.pagination button:hover{
  background:#1677ff;
  color:#fff;
}

.activePage{
  background:#1677ff !important;
  color:white;
}

/* ================= ERROR ================= */

.error-text{
  display:block;
  margin-top:6px;
  color:#f5222d;
  font-size:13px;
}

/* ================= TOAST ================= */

.toast{
  position:fixed;
  top:25px;
  right:25px;
  padding:14px 24px;
  border-radius:10px;
  color:#fff;
  font-weight:600;
  z-index:999;
  animation:slide .3s;
}

.success{
  background:#52c41a;
}

.error{
  background:#f5222d;
}

@keyframes slide{
  from{
    opacity:0;
    transform:translateX(60px);
  }
  to{
    opacity:1;
    transform:translateX(0);
  }
}

/* ================= RESPONSIVE ================= */

@media(max-width:768px){

  .grid{
    grid-template-columns:1fr;
  }

  .top-bar{
    flex-direction:column;
  }

  .actions{
    flex-direction:column;
  }

  table{
    font-size:13px;
  }

}
</style>

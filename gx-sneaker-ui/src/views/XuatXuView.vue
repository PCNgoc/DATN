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
const pageSize = 6

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
  errors.value = {  ten: "" }



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
          <input v-model="form.ten" placeholder="Tên xuất xứ" />
          <small class="error-text">{{ errors.ten }}</small>
        </div>

      </div>

      <!-- Mô tả -->
      <textarea
        v-model="form.moTa"
        placeholder="Mô tả Xuất xứ">
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
  padding:28px;
  min-height:100vh;
  background:#f4f7fb;
  font-family:"Segoe UI",Tahoma,sans-serif;
}

h2{
  margin-bottom:22px;
  font-size:30px;
  font-weight:700;
  color:#1f2937;
}

/*================ CARD ================*/

.card{
  background:#fff;
  border-radius:18px;
  padding:24px;
  margin-bottom:22px;
  border:1px solid #eef2f7;
  box-shadow:0 12px 35px rgba(15,23,42,.06);
  transition:.3s;
}

.card:hover{
  transform:translateY(-2px);
}

/*================ SEARCH ================*/

.top-bar{
  display:flex;
  gap:16px;
}

.top-bar input{
  flex:1;
}

/*================ FORM ================*/

.grid{
  display:grid;
  grid-template-columns:repeat(2,1fr);
  gap:20px;
}

input,
textarea,
select{
  width:100%;
  padding:12px 15px;
  border:1px solid #dbe2ea;
  border-radius:10px;
  font-size:15px;
  transition:.25s;
  background:#fff;
}

textarea{
  margin-top:18px;
  min-height:110px;
  resize:none;
}

input:focus,
textarea:focus,
select:focus{
  outline:none;
  border-color:#2563eb;
  box-shadow:0 0 0 4px rgba(37,99,235,.15);
}

/*================ STATUS ================*/

.status-box{
  margin-top:18px;
}

.status-box label{
  display:block;
  margin-bottom:8px;
  font-weight:600;
  color:#374151;
}

/*================ BUTTON ================*/

.actions{
  display:flex;
  gap:15px;
  margin-top:25px;
}

.actions button{
  border:none;
  border-radius:10px;
  padding:11px 24px;
  cursor:pointer;
  font-weight:600;
  transition:.25s;
}

.save{
  background:linear-gradient(135deg,#2563eb,#3b82f6);
  color:white;
}

.save:hover{
  transform:translateY(-2px);
  box-shadow:0 10px 20px rgba(37,99,235,.3);
}

.reset{
  background:#e5e7eb;
  color:#374151;
}

.reset:hover{
  background:#d1d5db;
}

/*================ TABLE ================*/

table{
  width:100%;
  border-collapse:collapse;
}

thead{
  background:linear-gradient(135deg,#2563eb,#3b82f6);
  color:white;
}

th{
  padding:15px;
  font-weight:600;
}

td{
  padding:15px;
  text-align:center;
  border-bottom:1px solid #edf2f7;
}

tbody tr{
  transition:.25s;
}

tbody tr:hover{
  background:#f8fbff;
}

/*================ STATUS BADGE ================*/

.active{
  display:inline-block;
  background:#dcfce7;
  color:#16a34a;
  padding:6px 16px;
  border-radius:30px;
  font-size:13px;
  font-weight:600;
}

.inactive{
  display:inline-block;
  background:#fee2e2;
  color:#dc2626;
  padding:6px 16px;
  border-radius:30px;
  font-size:13px;
  font-weight:600;
}

/*================ ACTION BUTTON ================*/

.edit,
.delete{
  width:38px;
  height:38px;
  border:none;
  border-radius:10px;
  color:white;
  cursor:pointer;
  transition:.25s;
}

.edit{
  background:#2563eb;
}

.edit:hover{
  background:#1d4ed8;
}

.delete{
  background:#ef4444;
  margin-left:8px;
}

.delete:hover{
  background:#dc2626;
}

/*================ PAGINATION ================*/

.pagination{
  display:flex;
  justify-content:center;
  gap:8px;
  margin-top:24px;
}

.pagination button{
  width:40px;
  height:40px;
  border:none;
  border-radius:10px;
  cursor:pointer;
  background:#fff;
  box-shadow:0 2px 8px rgba(0,0,0,.08);
  transition:.25s;
}

.pagination button:hover{
  background:#2563eb;
  color:white;
}

.activePage{
  background:#2563eb!important;
  color:white!important;
}

/*================ ERROR ================*/

.error-text{
  display:block;
  margin-top:6px;
  color:#ef4444;
  font-size:13px;
}

/*================ TOAST ================*/

.toast{
  position:fixed;
  top:25px;
  right:25px;
  padding:14px 24px;
  border-radius:10px;
  color:white;
  font-weight:600;
  z-index:9999;
  animation:slide .3s ease;
  box-shadow:0 12px 30px rgba(0,0,0,.15);
}

.success{
  background:linear-gradient(135deg,#22c55e,#16a34a);
}

.error{
  background:linear-gradient(135deg,#ef4444,#dc2626);
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

/*================ RESPONSIVE ================*/

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

  .actions button{
    width:100%;
  }

  table{
    font-size:13px;
  }

}
</style>

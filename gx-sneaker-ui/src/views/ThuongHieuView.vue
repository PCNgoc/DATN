```vue
<script setup>
import { ref, computed, onMounted } from "vue"
import axios from "axios"

const API = "http://localhost:8080/api/thuong-hieu"

// ================= DATA =================
const list = ref([])
const page = ref(1)
const pageSize = 10

const form = ref({
  id: null,
  ma: "",
  ten: "",
  logo: "",
  website: "",
  moTa: "",
  quocGia: "",
  trangThai: true,
  ngayTao: null,
  ngayCapNhat: null
})

const errors = ref({
  ma: "",
  ten: ""
})

const toast = ref("")
const toastType = ref("success")

// ================= TOAST =================
const showToast = (msg, type = "success") => {
  toast.value = msg
  toastType.value = type

  setTimeout(() => {
    toast.value = ""
  }, 2500)
}

// ================= LOAD =================
const load = async () => {
  try {
    const res = await axios.get(API)
    list.value = res.data
  } catch (error) {
    console.error(error)
    showToast("Không tải được dữ liệu", "error")
  }
}

// ================= VALIDATE =================
const validate = () => {
  errors.value = {
    ma: "",
    ten: ""
  }

  let ok = true

  if (!form.value.ma.trim()) {
    errors.value.ma = "Mã thương hiệu không được để trống"
    ok = false
  }

  if (!form.value.ten.trim()) {
    errors.value.ten = "Tên thương hiệu không được để trống"
    ok = false
  }

  return ok
}

// ================= SAVE =================
const save = async () => {
  if (!validate()) {
    showToast("Vui lòng kiểm tra dữ liệu", "error")
    return
  }

  try {
    const payload = {
      ma: form.value.ma,
      ten: form.value.ten,
      logo: form.value.logo,
      website: form.value.website,
      moTa: form.value.moTa,
      quocGia: form.value.quocGia,
      trangThai: form.value.trangThai
    }

    if (form.value.id) {
      await axios.put(`${API}/${form.value.id}`, payload)
      showToast("Cập nhật thành công")
    } else {
      await axios.post(API, payload)
      showToast("Thêm mới thành công")
    }

    reset()
    load()
  } catch (error) {
    console.error(error)
    showToast("Có lỗi xảy ra", "error")
  }
}

// ================= DELETE =================
const del = async (id) => {
  if (!confirm("Bạn có chắc muốn xóa?")) return

  try {
    await axios.delete(`${API}/${id}`)
    showToast("Xóa thành công")
    load()
  } catch (error) {
    console.error(error)
    showToast("Xóa thất bại", "error")
  }
}

// ================= EDIT =================
const edit = (item) => {
  form.value = { ...item }

  window.scrollTo({
    top: 0,
    behavior: "smooth"
  })
}

// ================= RESET =================
const reset = () => {
  form.value = {
    id: null,
    ma: "",
    ten: "",
    logo: "",
    website: "",
    moTa: "",
    quocGia: "",
    trangThai: true,
    ngayTao: null,
    ngayCapNhat: null
  }

  errors.value = {
    ma: "",
    ten: ""
  }
}

// ================= FORMAT DATE =================
const formatDate = (date) => {
  if (!date) return "---"

  return new Date(date).toLocaleString("vi-VN")
}

// ================= PAGINATION =================
const totalPages = computed(() =>
  Math.ceil(list.value.length / pageSize)
)

const paginatedList = computed(() => {
  const start = (page.value - 1) * pageSize
  return list.value.slice(start, start + pageSize)
})

const changePage = (p) => {
  page.value = p
}

onMounted(load)
</script>

<template>
  <div class="container">

    <h2>🏷️ QUẢN LÝ THƯƠNG HIỆU</h2>

    <div
      v-if="toast"
      :class="['toast', toastType]"
    >
      {{ toast }}
    </div>

    <!-- FORM -->
    <div class="card">

      <div class="grid">

        <div>
          <label>Mã thương hiệu *</label>
          <input v-model="form.ma">
          <small class="error">{{ errors.ma }}</small>
        </div>

        <div>
          <label>Tên thương hiệu *</label>
          <input v-model="form.ten">
          <small class="error">{{ errors.ten }}</small>
        </div>

        <div>
          <label>Logo URL</label>
          <input v-model="form.logo">
        </div>

        <div>
          <label>Website</label>
          <input v-model="form.website">
        </div>

        <div>
          <label>Quốc gia</label>
          <input v-model="form.quocGia">
        </div>

      </div>

      <div class="mt">
        <label>Mô tả</label>
        <textarea
          rows="4"
          v-model="form.moTa"
        ></textarea>
      </div>

      <div class="status-box">

        <label>
          <input
            type="radio"
            :value="true"
            v-model="form.trangThai"
          >
          Hoạt động
        </label>

        <label>
          <input
            type="radio"
            :value="false"
            v-model="form.trangThai"
          >
          Ngừng hoạt động
        </label>

      </div>

      <div class="actions">
        <button
          class="btn-save"
          @click="save"
        >
          {{ form.id ? "Cập nhật" : "Thêm mới" }}
        </button>

        <button
          class="btn-reset"
          @click="reset"
        >
          Reset
        </button>
      </div>

      <div
        v-if="form.id"
        class="audit-box"
      >
        <h4>Thông tin hệ thống</h4>

        <div class="audit-grid">
          <div>
            <b>Ngày tạo:</b>
            {{ formatDate(form.ngayTao) }}
          </div>

          <div>
            <b>Ngày cập nhật:</b>
            {{ formatDate(form.ngayCapNhat) }}
          </div>
        </div>
      </div>

    </div>

    <!-- TABLE -->
    <div class="card">

      <table>

        <thead>
        <tr>
          <th>Logo</th>
          <th>Mã</th>
          <th>Tên thương hiệu</th>
          <th>Quốc gia</th>
          <th>Website</th>
          <th>Trạng thái</th>
          <th>Hành động</th>
        </tr>
        </thead>

        <tbody>

        <tr
          v-for="item in paginatedList"
          :key="item.id"
        >

          <td>
            <img
              :src="item.logo || 'https://via.placeholder.com/50'"
              class="logo"
            >
          </td>

          <td>{{ item.ma }}</td>

          <td>{{ item.ten }}</td>

          <td>{{ item.quocGia }}</td>

          <td>
            <a
              :href="item.website"
              target="_blank"
            >
              Website
            </a>
          </td>

          <td>
              <span
                :class="
                  item.trangThai
                    ? 'active-status'
                    : 'inactive-status'
                "
              >
                {{
                  item.trangThai
                    ? 'Hoạt động'
                    : 'Ngừng hoạt động'
                }}
              </span>
          </td>

          <td>
            <button
              class="btn-edit"
              @click="edit(item)"
            >
              ✏️
            </button>

            <button
              class="btn-delete"
              @click="del(item.id)"
            >
              🗑️
            </button>
          </td>

        </tr>

        </tbody>

      </table>

      <div class="pagination">

        <button
          @click="page--"
          :disabled="page === 1"
        >
          ◀
        </button>

        <button
          v-for="p in totalPages"
          :key="p"
          @click="changePage(p)"
          :class="{ activePage: page === p }"
        >
          {{ p }}
        </button>

        <button
          @click="page++"
          :disabled="page === totalPages"
        >
          ▶
        </button>

      </div>

    </div>

  </div>
</template>

<style scoped>
.container{
  padding:20px;
  background:#f5f6fa;
  min-height:100vh;
}

.card{
  background:#fff;
  padding:20px;
  border-radius:12px;
  margin-bottom:20px;
  box-shadow:0 2px 8px rgba(0,0,0,.08);
}

.grid{
  display:grid;
  grid-template-columns:repeat(2,1fr);
  gap:15px;
}

input,
textarea{
  width:100%;
  padding:10px;
  border:1px solid #ddd;
  border-radius:8px;
}

.mt{
  margin-top:15px;
}

.status-box{
  margin-top:15px;
  display:flex;
  gap:20px;
}

.actions{
  margin-top:20px;
}

.btn-save{
  background:#198754;
  color:white;
  border:none;
  padding:10px 16px;
  border-radius:8px;
}

.btn-reset{
  background:#6c757d;
  color:white;
  border:none;
  padding:10px 16px;
  border-radius:8px;
  margin-left:10px;
}

.logo{
  width:50px;
  height:50px;
  object-fit:contain;
}

table{
  width:100%;
  border-collapse:collapse;
}

th,td{
  padding:12px;
  border-bottom:1px solid #eee;
  text-align:center;
}

.active-status{
  color:#198754;
  font-weight:bold;
}

.inactive-status{
  color:#dc3545;
  font-weight:bold;
}

.btn-edit{
  background:#ffc107;
  border:none;
  padding:6px 10px;
  border-radius:6px;
}

.btn-delete{
  background:#dc3545;
  color:white;
  border:none;
  padding:6px 10px;
  border-radius:6px;
  margin-left:5px;
}

.pagination{
  margin-top:15px;
  display:flex;
  justify-content:center;
  gap:5px;
}

.pagination button{
  padding:6px 12px;
}

.activePage{
  background:#0d6efd;
  color:white;
}

.error{
  color:red;
}

.toast{
  position:fixed;
  top:20px;
  right:20px;
  padding:12px 18px;
  color:white;
  border-radius:8px;
  z-index:999;
}

.success{
  background:#198754;
}

.error{
  background:#dc3545;
}

.audit-box{
  margin-top:20px;
  padding:15px;
  background:#f8f9fa;
  border-radius:8px;
}

.audit-grid{
  display:grid;
  grid-template-columns:1fr 1fr;
}
</style>
```

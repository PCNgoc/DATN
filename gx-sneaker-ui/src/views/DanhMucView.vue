<script setup>
import { ref, onMounted, computed } from "vue"
import {
  getAll,
  create,
  update,
  remove
} from "@/services/danhMucService"

// ================= DATA =================

const list = ref([])
const detail = ref(null)
const loading = ref(false)

const page = ref(1)
const pageSize = 10

// ================= FORM =================

const form = ref({
  id: null,
  ma: "",
  ten: "",
  moTa: "",
  thuTuHienThi: 0,
  trangThai: true
})

// ================= ERROR =================

const errors = ref({
  ma: "",
  ten: "",
  moTa: "",
  thuTuHienThi: ""
})

// ================= TOAST =================

const toast = ref("")
const toastType = ref("success")

const showToast = (msg, type = "success") => {
  toast.value = msg
  toastType.value = type

  setTimeout(() => {
    toast.value = ""
  }, 2500)
}

// ================= FORMAT DATE =================

const formatDate = (date) => {
  if (!date) return ""

  return new Date(date).toLocaleString("vi-VN")
}

// ================= LOAD DATA =================

const load = async () => {
  loading.value = true

  try {
    const res = await getAll()
    list.value = res.data
  } catch (err) {
    console.log(err)
    showToast("Không thể tải dữ liệu", "error")
  } finally {
    loading.value = false
  }
}

// ================= ON MOUNT =================

onMounted(load)

// ================= RESET =================

const reset = () => {
  form.value = {
    id: null,
    ma: "",
    ten: "",
    moTa: "",
    thuTuHienThi: 0,
    trangThai: true
  }

  errors.value = {
    ma: "",
    ten: "",
    moTa: "",
    thuTuHienThi: ""
  }
}

// ================= VALIDATE =================

const validate = () => {
  let ok = true

  errors.value = {
    ten: "",
    moTa: "",
    thuTuHienThi: ""
  }



  // TÊN

  if (!form.value.ten.trim()) {
    errors.value.ten = "⚠ Tên danh mục không được để trống"
    ok = false
  }

  // MÔ TẢ

  if (!form.value.moTa.trim()) {
    errors.value.moTa = "⚠ Mô tả không được để trống"
    ok = false
  }

  // THỨ TỰ

  if (
    form.value.thuTuHienThi === "" ||
    form.value.thuTuHienThi === null
  ) {
    errors.value.thuTuHienThi =
      "⚠ Thứ tự hiển thị không được để trống"

    ok = false
  } else if (Number(form.value.thuTuHienThi) < 0) {
    errors.value.thuTuHienThi =
      "⚠ Thứ tự hiển thị phải lớn hơn hoặc bằng 0"

    ok = false
  }

  // TRÙNG MÃ

  const existMa = list.value.find(
    (item) =>
      item.ma?.trim().toLowerCase() ===
      form.value.ma.trim().toLowerCase() &&
      item.id !== form.value.id
  )

  if (existMa) {
    errors.value.ma = "⚠ Mã danh mục đã tồn tại"
    ok = false
  }

  // TRÙNG TÊN

  const existTen = list.value.find(
    (item) =>
      item.ten?.trim().toLowerCase() ===
      form.value.ten.trim().toLowerCase() &&
      item.id !== form.value.id
  )

  if (existTen) {
    errors.value.ten = "⚠ Tên danh mục đã tồn tại"
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
    if (form.value.id) {
      await update(form.value.id, form.value)

      showToast("Cập nhật danh mục thành công")
    } else {
      await create(form.value)

      showToast("Thêm danh mục thành công")
    }

    reset()
    load()

  } catch (err) {
    console.log(err)

    showToast("Có lỗi xảy ra", "error")
  }
}

// ================= EDIT =================

const edit = (item) => {
  form.value = {
    ...item
  }

  window.scrollTo({
    top: 0,
    behavior: "smooth"
  })
}

// ================= DELETE =================

const del = async (id) => {
  if (!confirm("Bạn có chắc muốn xóa danh mục này?")) {
    return
  }

  try {
    await remove(id)

    showToast("Xóa thành công")

    load()

  } catch (err) {
    console.log(err)

    showToast("Xóa thất bại", "error")
  }
}

// ================= DETAIL =================

const viewDetail = (item) => {
  detail.value = item
}

// ================= PAGINATION =================

const totalPages = computed(() =>
  Math.ceil(list.value.length / pageSize)
)

const paginatedList = computed(() => {
  const start = (page.value - 1) * pageSize

  return list.value.slice(
    start,
    start + pageSize
  )
})

const changePage = (p) => {
  page.value = p
}
</script>
<template>
  <div class="container">

    <!-- TITLE -->
    <h2 class="title">📁 QUẢN LÝ DANH MỤC</h2>

    <!-- TOAST -->
    <transition name="fade">
      <div
        v-if="toast"
        :class="['toast', toastType]"
      >
        {{ toast }}
      </div>
    </transition>

    <!-- LOADING -->
    <div
      v-if="loading"
      class="loading"
    >
      ⏳ Đang tải dữ liệu...
    </div>

    <!-- ================= FORM ================= -->

    <div class="card form-box">

      <h3 class="form-title">
        {{ form.id ? "CẬP NHẬT DANH MỤC" : "THÊM DANH MỤC" }}
      </h3>

      <div class="grid">

        <!-- Mã -->


        <!-- Tên -->

        <div class="form-group">
          <label>Tên danh mục</label>

          <input
            v-model="form.ten"
            placeholder="Nhập tên..."
          />

          <small class="error-text">
            {{ errors.ten }}
          </small>
        </div>

        <!-- Thứ tự -->

        <div class="form-group">
          <label>Thứ tự hiển thị</label>

          <input
            type="number"
            min="0"
            v-model="form.thuTuHienThi"
          />

          <small class="error-text">
            {{ errors.thuTuHienThi }}
          </small>
        </div>

        <!-- Trạng thái -->

        <div class="form-group">

          <label>Trạng thái</label>

          <select v-model="form.trangThai">

            <option :value="true">
              Hoạt động
            </option>

            <option :value="false">
              Tạm ẩn
            </option>

          </select>

        </div>

      </div>

      <!-- Mô tả -->

      <div class="form-group">

        <label>Mô tả</label>

        <textarea
          rows="4"
          v-model="form.moTa"
          placeholder="Nhập mô tả..."
        ></textarea>

        <small class="error-text">
          {{ errors.moTa }}
        </small>

      </div>

      <!-- BUTTON -->

      <div class="actions">

        <button
          class="btn-save"
          @click="save"
        >
          {{ form.id ? "💾 Cập nhật" : "➕ Thêm mới" }}
        </button>

        <button
          class="btn-reset"
          @click="reset"
        >
          🔄 Reset
        </button>

      </div>

    </div>

    <!-- ================= TABLE ================= -->

    <div class="card">

      <table v-if="list.length">

        <thead>

        <tr>

          <th>Mã</th>

          <th>Tên</th>

          <th>Mô tả</th>

          <th>Thứ tự</th>

          <th>Trạng thái</th>

          <th>Ngày tạo</th>

          <th width="180">
            Hành động
          </th>

        </tr>

        </thead>

        <tbody>

        <tr
          v-for="item in paginatedList"
          :key="item.id"
        >

          <td>{{ item.ma }}</td>

          <td>{{ item.ten }}</td>

          <td>{{ item.moTa }}</td>

          <td>{{ item.thuTuHienThi }}</td>

          <td>

            <span
              :class="item.trangThai ? 'active' : 'hidden'"
            >
              {{ item.trangThai ? "Hoạt động" : "Tạm ẩn" }}
            </span>

          </td>

          <td>

            {{ formatDate(item.ngayTao) }}

          </td>

          <td class="btn-group">

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
              🗑
            </button>

          </td>

        </tr>

        </tbody>

      </table>

      <div
        v-else
        class="empty"
      >
        Không có dữ liệu.
      </div>

      <!-- PAGINATION -->

      <div
        class="pagination"
        v-if="totalPages > 1"
      >

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
          :class="{ activePage: p === page }"
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

    <!-- ================= MODAL ================= -->

    <div
      v-if="detail"
      class="modal-overlay"
    >

      <div class="modal">

        <h3>
          📋 CHI TIẾT DANH MỤC
        </h3>

        <div class="detail-item">
          <b>Mã:</b>
          {{ detail.ma }}
        </div>

        <div class="detail-item">
          <b>Tên:</b>
          {{ detail.ten }}
        </div>

        <div class="detail-item">
          <b>Mô tả:</b>
          {{ detail.moTa }}
        </div>

        <div class="detail-item">
          <b>Thứ tự:</b>
          {{ detail.thuTuHienThi }}
        </div>

        <div class="detail-item">
          <b>Trạng thái:</b>

          {{ detail.trangThai ? "Hoạt động" : "Tạm ẩn" }}

        </div>

        <div class="detail-item">
          <b>Ngày tạo:</b>

          {{ formatDate(detail.ngayTao) }}

        </div>

        <div class="detail-item">
          <b>Ngày cập nhật:</b>

          {{ formatDate(detail.ngayCapNhat) }}

        </div>

        <button
          class="btn-close"
          @click="detail = null"
        >
          Đóng
        </button>

      </div>

    </div>

  </div>
</template>
<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.container {
  padding: 28px;
  background: #f4f7fb;
  min-height: 100vh;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}

.title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 22px;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 22px;
  margin-bottom: 22px;
  box-shadow: 0 8px 25px rgba(0,0,0,.08);
}

.form-box h3{
  margin-bottom:18px;
  color:#374151;
}

.grid{
  display:grid;
  grid-template-columns:repeat(2,1fr);
  gap:18px;
}

.form-box input,
.form-box textarea,
.form-box select{
  width:100%;
  padding:12px 15px;
  border:1px solid #d9dee8;
  border-radius:10px;
  font-size:15px;
  transition:.25s;
  background:#fff;
}

.form-box textarea{
  margin-top:18px;
  min-height:110px;
  resize:vertical;
}

.form-box input:focus,
.form-box textarea:focus,
.form-box select:focus{
  border-color:#2563eb;
  box-shadow:0 0 0 4px rgba(37,99,235,.15);
  outline:none;
}

.error-text{
  color:#ef4444;
  font-size:13px;
  margin-top:5px;
  display:block;
}

.actions{
  display:flex;
  gap:12px;
  margin-top:22px;
}

button{
  border:none;
  cursor:pointer;
  transition:.25s;
  font-weight:600;
}

.btn-save{
  background:#2563eb;
  color:#fff;
  padding:11px 22px;
  border-radius:10px;
}

.btn-save:hover{
  background:#1d4ed8;
  transform:translateY(-2px);
}

.btn-reset{
  background:#6b7280;
  color:#fff;
  padding:11px 22px;
  border-radius:10px;
}

.btn-reset:hover{
  background:#4b5563;
}

table{
  width:100%;
  border-collapse:collapse;
  overflow:hidden;
  border-radius:12px;
}

thead{
  background:#2563eb;
  color:#fff;
}

th{
  padding:15px;
  font-size:15px;
}

td{
  padding:14px;
  text-align:center;
  border-bottom:1px solid #ececec;
}

tbody tr{
  transition:.2s;
}

tbody tr:hover{
  background:#f5f9ff;
}

.active{
  color:#16a34a;
  background:#dcfce7;
  padding:5px 12px;
  border-radius:20px;
  font-size:13px;
  font-weight:700;
}

.hidden{
  color:#dc2626;
  background:#fee2e2;
  padding:5px 12px;
  border-radius:20px;
  font-size:13px;
  font-weight:700;
}

.btn-group{
  display:flex;
  justify-content:center;
  gap:8px;
}

.btn-group button{
  width:36px;
  height:36px;
  border-radius:8px;
  color:#fff;
  font-size:15px;
}

.btn-group button:nth-child(1){
  background:#0ea5e9;
}

.btn-group button:nth-child(2){
  background:#f59e0b;
}

.btn-group button:nth-child(3){
  background:#ef4444;
}

.btn-group button:hover{
  transform:scale(1.08);
}

.pagination{
  display:flex;
  justify-content:center;
  gap:8px;
  margin-top:25px;
}

.pagination button{
  min-width:38px;
  height:38px;
  border-radius:8px;
  background:#e5e7eb;
  color:#374151;
}

.pagination button:hover{
  background:#d1d5db;
}

.activePage{
  background:#2563eb!important;
  color:white!important;
}

.modal-overlay{
  position:fixed;
  inset:0;
  background:rgba(0,0,0,.45);
  display:flex;
  justify-content:center;
  align-items:center;
  backdrop-filter:blur(3px);
}

.modal{
  width:460px;
  background:white;
  border-radius:18px;
  padding:28px;
  animation:popup .25s ease;
  box-shadow:0 15px 40px rgba(0,0,0,.25);
}

.modal h3{
  margin-bottom:18px;
  color:#1f2937;
}

.modal p{
  margin:10px 0;
  font-size:15px;
  color:#374151;
}

.btn-close{
  margin-top:18px;
  width:100%;
  padding:11px;
  background:#ef4444;
  color:#fff;
  border-radius:10px;
}

.btn-close:hover{
  background:#dc2626;
}

.loading{
  text-align:center;
  font-size:17px;
  color:#2563eb;
  padding:20px;
}

@keyframes popup{
  from{
    opacity:0;
    transform:translateY(-25px) scale(.9);
  }
  to{
    opacity:1;
    transform:translateY(0) scale(1);
  }
}

@media(max-width:768px){

  .grid{
    grid-template-columns:1fr;
  }

  .card{
    padding:18px;
  }

  .modal{
    width:95%;
  }

  table{
    display:block;
    overflow-x:auto;
  }
}
</style>

<script setup>
import { ref, onMounted } from "vue"
import axios from "axios"

const API = "http://localhost:8080/api/thuong-hieu"

const list = ref([])

// ===== FORM =====
const form = ref({
  id: null,
  ma: "",
  ten: "",
  logo: "",
  website: "",
  moTa: "",
  quocGia: "",
  trangThai: true
})

// ===== ERROR =====
const errors = ref({
  ma: "",
  ten: "",
  website: "",
  quocGia: ""
})

// ===== TOAST =====
const toast = ref("")
const toastType = ref("success")

const showToast = (msg, type = "success") => {
  toast.value = msg
  toastType.value = type

  setTimeout(() => {
    toast.value = ""
  }, 2000)
}

// ===== LOAD =====
const load = async () => {
  const res = await axios.get(API)
  list.value = res.data
}

// ===== VALIDATE =====
const validate = () => {
  let ok = true

  errors.value = {
    ma: "",
    ten: "",
    website: "",
    quocGia: ""
  }

  if (!form.value.ma.trim()) {
    errors.value.ma = "⚠ Mã không được để trống"
    ok = false
  }

  if (!form.value.ten.trim()) {
    errors.value.ten = "⚠ Tên không được để trống"
    ok = false
  }

  if (!form.value.quocGia.trim()) {
    errors.value.quocGia = "⚠ Quốc gia không được để trống"
    ok = false
  }

  if (form.value.website && !form.value.website.includes(".")) {
    errors.value.website = "⚠ Website không hợp lệ"
    ok = false
  }

  return ok
}

// ===== SAVE =====
const save = async () => {
  if (!validate()) {
    showToast("Vui lòng kiểm tra lại dữ liệu", "error")
    return
  }

  try {
    if (form.value.id) {
      await axios.put(`${API}/${form.value.id}`, form.value)
      showToast("Cập nhật thành công")
    } else {
      await axios.post(API, form.value)
      showToast("Thêm mới thành công")
    }

    reset()
    load()

  } catch (err) {
    showToast("Có lỗi xảy ra", "error")
  }
}

// ===== DELETE =====
const del = async (id) => {
  if (!confirm("Bạn có chắc muốn xóa?")) return

  await axios.delete(`${API}/${id}`)
  showToast("Xóa thành công")
  load()
}

// ===== EDIT =====
const edit = (item) => {
  form.value = { ...item }
}

// ===== RESET =====
const reset = () => {
  form.value = {
    id: null,
    ma: "",
    ten: "",
    logo: "",
    website: "",
    moTa: "",
    quocGia: "",
    trangThai: true
  }

  errors.value = {
    ma: "",
    ten: "",
    website: "",
    quocGia: ""
  }
}

onMounted(load)
</script>

<template>
  <div class="container">

    <h2>🏷️ QUẢN LÝ THƯƠNG HIỆU</h2>

    <!-- TOAST -->
    <div v-if="toast" :class="['toast', toastType]">
      {{ toast }}
    </div>

    <!-- FORM -->
    <div class="card form">

      <div class="grid">

        <div>
          <input v-model="form.ma" placeholder="Mã thương hiệu" />
          <small class="error">{{ errors.ma }}</small>
        </div>

        <div>
          <input v-model="form.ten" placeholder="Tên thương hiệu" />
          <small class="error">{{ errors.ten }}</small>
        </div>

        <div>
          <input v-model="form.website" placeholder="Website" />
          <small class="error">{{ errors.website }}</small>
        </div>

        <div>
          <input v-model="form.quocGia" placeholder="Quốc gia" />
          <small class="error">{{ errors.quocGia }}</small>
        </div>

        <input v-model="form.logo" placeholder="Logo URL" />

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
          <th>Quốc gia</th>
          <th>Website</th>
          <th>Hành động</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="i in list" :key="i.id">
          <td>{{ i.ma }}</td>
          <td>{{ i.ten }}</td>
          <td>{{ i.quocGia }}</td>
          <td>{{ i.website }}</td>

          <td>
            <button @click="edit(i)">✏️</button>
            <button @click="del(i.id)">🗑</button>
          </td>
        </tr>
        </tbody>

      </table>
    </div>

  </div>
</template>

<style scoped>
.container {
  padding: 20px;
  font-family: Arial;
  background: #f6f7fb;
}

.card {
  background: white;
  padding: 15px;
  border-radius: 10px;
  margin-bottom: 15px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

/* GRID FORM */
.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

input, textarea {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  width: 100%;
}

textarea {
  grid-column: span 2;
}

.actions {
  margin-top: 10px;
}

/* BUTTON */
.save {
  background: green;
  color: white;
  padding: 8px 12px;
  border: none;
}

.reset {
  background: gray;
  color: white;
  padding: 8px 12px;
  border: none;
}

/* ERROR */
.error {
  color: red;
  font-size: 12px;
}

/* TABLE */
table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 10px;
  border-bottom: 1px solid #eee;
  text-align: center;
}

/* TOAST */
.toast {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 10px 15px;
  border-radius: 6px;
  color: white;
  z-index: 999;
}

.success {
  background: #28a745;
}

.error {
  background: #dc3545;
}
</style>

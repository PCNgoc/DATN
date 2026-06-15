```vue
<script setup>
import { ref, onMounted, computed } from "vue"
import axios from "axios"

const API = "http://localhost:8080/api/kich-thuoc"

const list = ref([])

// PAGINATION
const page = ref(1)
const pageSize = 10

// FORM
const form = ref({
  id: null,
  ma: "",
  size: "",
  trangThai: true
})

// ERROR
const errors = ref({
  ma: "",
  size: ""
})

// TOAST
const toast = ref("")
const toastType = ref("success")

const showToast = (msg, type = "success") => {
  toast.value = msg
  toastType.value = type

  setTimeout(() => {
    toast.value = ""
  }, 2000)
}

// LOAD
const load = async () => {
  const res = await axios.get(API)
  list.value = res.data
}

// VALIDATE
const validate = () => {
  let ok = true

  errors.value = {
    ma: "",
    size: ""
  }

  if (!form.value.ma.trim()) {
    errors.value.ma = "⚠ Mã không được để trống"
    ok = false
  }

  if (!form.value.size) {
    errors.value.size = "⚠ Size không được để trống"
    ok = false
  } else if (form.value.size < 35 || form.value.size > 46) {
    errors.value.size = "⚠ Size phải từ 35 đến 46"
    ok = false
  }

  return ok
}

// SAVE
const save = async () => {
  if (!validate()) {
    showToast("Vui lòng kiểm tra dữ liệu", "error")
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

  } catch (e) {
    console.log(e)
    showToast("Có lỗi xảy ra", "error")
  }
}

// DELETE
const del = async (id) => {
  if (!confirm("Bạn có chắc muốn xóa?")) return

  await axios.delete(`${API}/${id}`)

  showToast("Xóa thành công")
  load()
}

// EDIT
const edit = (item) => {
  form.value = { ...item }
}

// RESET
const reset = () => {
  form.value = {
    id: null,
    ma: "",
    size: "",
    trangThai: true
  }

  errors.value = {
    ma: "",
    size: ""
  }
}

// PAGINATION
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

    <h2>📏 QUẢN LÝ KÍCH THƯỚC</h2>

    <!-- TOAST -->
    <div v-if="toast" :class="['toast', toastType]">
      {{ toast }}
    </div>

    <!-- FORM -->
    <div class="card form">

      <div class="grid">

        <div>
          <input
            v-model="form.ma"
            placeholder="Mã kích thước"
          />
          <small class="error-text">
            {{ errors.ma }}
          </small>
        </div>

        <div>
          <input
            type="number"
            v-model="form.size"
            placeholder="Size (35 - 46)"
            min="35"
            max="46"
          />
          <small class="error-text">
            {{ errors.size }}
          </small>
        </div>

      </div>

      <div class="status-box">
        <label>
          <input
            type="radio"
            :value="true"
            v-model="form.trangThai"
          />
          Hoạt động
        </label>

        <label>
          <input
            type="radio"
            :value="false"
            v-model="form.trangThai"
          />
          Ngừng hoạt động
        </label>
      </div>

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
          <th>Size</th>
          <th>Trạng thái</th>
          <th>Hành động</th>
        </tr>
        </thead>

        <tbody>

        <tr
          v-for="i in paginatedList"
          :key="i.id"
        >
          <td>{{ i.ma }}</td>

          <td>{{ i.size }}</td>

          <td>
            <span
              :class="i.trangThai ? 'active-status' : 'inactive-status'"
            >
              {{ i.trangThai ? "Hoạt động" : "Ngừng hoạt động" }}
            </span>
          </td>

          <td>
            <button @click="edit(i)">
              ✏️
            </button>

            <button @click="del(i.id)">
              🗑️
            </button>
          </td>
        </tr>

        </tbody>

      </table>

      <!-- PAGINATION -->
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

.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  width: 100%;
}

.status-box {
  margin-top: 15px;
  display: flex;
  gap: 20px;
}

.actions {
  margin-top: 15px;
}

.save {
  background: green;
  color: white;
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
}

.reset {
  background: gray;
  color: white;
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  margin-left: 10px;
}

.error-text {
  color: red;
  font-size: 12px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 10px;
  border-bottom: 1px solid #eee;
  text-align: center;
}

.active-status {
  color: green;
  font-weight: bold;
}

.inactive-status {
  color: red;
  font-weight: bold;
}

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

.pagination {
  margin-top: 15px;
  display: flex;
  justify-content: center;
  gap: 5px;
}

.pagination button {
  padding: 6px 12px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  background: #e9ecef;
}

.pagination button:hover {
  background: #d6d8db;
}

.activePage {
  background: #0d6efd !important;
  color: white;
}
</style>
```

<script setup>
import { ref, onMounted } from "vue"
import { getAll, create, update, remove } from "@/services/danhMucService"

const list = ref([])
const detail = ref(null)
const loading = ref(false)

const form = ref({
  id: null,
  ma: "",
  ten: "",
  moTa: "",
  thuTuHienThi: 0,
  trangThai: true
})

// FORMAT DATE
const formatDate = (date) => {
  if (!date) return ""
  return new Date(date).toLocaleString("vi-VN")
}

// LOAD DATA
const load = async () => {
  loading.value = true
  try {
    const res = await getAll()
    list.value = res.data
  } catch (err) {
    console.log("Lỗi load dữ liệu:", err)
  } finally {
    loading.value = false
  }
}

onMounted(load)

// RESET FORM
const reset = () => {
  form.value = {
    id: null,
    ma: "",
    ten: "",
    moTa: "",
    thuTuHienThi: 0,
    trangThai: true
  }
}

// SAVE
const save = async () => {
  try {
    if (form.value.id) {
      await update(form.value.id, form.value)
    } else {
      await create(form.value)
    }
    reset()
    load()
  } catch (err) {
    console.log(err)
  }
}

// EDIT
const edit = (item) => {
  form.value = { ...item }
}

// DELETE
const del = async (id) => {
  if (!confirm("Bạn có chắc muốn xóa?")) return
  await remove(id)
  load()
}

// DETAIL
const viewDetail = (item) => {
  detail.value = item
}
</script>

<template>
  <div class="container">

    <!-- TITLE -->
    <h2 class="title">📁 QUẢN LÝ DANH MỤC</h2>

    <!-- FORM -->
    <div class="card form-box">

      <h3>{{ form.id ? "Cập nhật danh mục" : "Thêm danh mục" }}</h3>

      <div class="grid">
        <input v-model="form.ma" placeholder="Mã danh mục" />
        <input v-model="form.ten" placeholder="Tên danh mục" />
        <input v-model="form.thuTuHienThi" type="number" placeholder="Thứ tự hiển thị" />
      </div>

      <textarea v-model="form.moTa" placeholder="Mô tả chi tiết"></textarea>

      <select v-model="form.trangThai">
        <option :value="true">Hoạt động</option>
        <option :value="false">Tạm ẩn</option>
      </select>

      <div class="actions">
        <button class="btn-save" @click="save">💾 Lưu</button>
        <button class="btn-reset" @click="reset">Reset</button>
      </div>

    </div>

    <!-- LOADING -->
    <p v-if="loading">⏳ Đang tải dữ liệu...</p>

    <!-- TABLE -->
    <div class="card">
      <table v-if="list.length > 0">
        <thead>
        <tr>
          <th>Mã</th>
          <th>Tên</th>
          <th>Mô tả</th>
          <th>Thứ tự</th>
          <th>Trạng thái</th>
          <th>Ngày tạo</th>
          <th>Hành động</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="item in list" :key="item.id">

          <td>{{ item.ma }}</td>
          <td>{{ item.ten }}</td>
          <td>{{ item.moTa }}</td>
          <td>{{ item.thuTuHienThi }}</td>

          <td>
              <span :class="item.trangThai ? 'active' : 'hidden'">
                {{ item.trangThai ? "Active" : "Hidden" }}
              </span>
          </td>

          <td>{{ formatDate(item.ngayTao) }}</td>

          <td class="btn-group">
            <button @click="viewDetail(item)">👁</button>
            <button @click="edit(item)">✏️</button>
            <button @click="del(item.id)">🗑</button>
          </td>

        </tr>
        </tbody>
      </table>

      <p v-else>Không có dữ liệu</p>
    </div>

    <!-- MODAL DETAIL -->
    <div v-if="detail" class="modal-overlay">
      <div class="modal">

        <h3>📌 CHI TIẾT DANH MỤC</h3>

        <p><b>Mã:</b> {{ detail.ma }}</p>
        <p><b>Tên:</b> {{ detail.ten }}</p>
        <p><b>Mô tả:</b> {{ detail.moTa }}</p>
        <p><b>Thứ tự:</b> {{ detail.thuTuHienThi }}</p>
        <p><b>Trạng thái:</b> {{ detail.trangThai ? "Active" : "Hidden" }}</p>
        <p><b>Ngày tạo:</b> {{ formatDate(detail.ngayTao) }}</p>
        <p><b>Ngày cập nhật:</b> {{ formatDate(detail.ngayCapNhat) }}</p>

        <button class="btn-close" @click="detail = null">Đóng</button>

      </div>
    </div>

  </div>
</template>

<style scoped>
.container {
  padding: 20px;
  font-family: Arial;
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

.form-box input,
.form-box textarea,
.form-box select {
  width: 100%;
  margin: 5px 0;
  padding: 8px;
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
  padding: 8px;
  border-bottom: 1px solid #ddd;
}

.btn-group button {
  margin-right: 5px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  background: white;
  padding: 20px;
  border-radius: 10px;
  width: 400px;
}

.btn-close {
  margin-top: 10px;
  background: red;
  color: white;
}
</style>

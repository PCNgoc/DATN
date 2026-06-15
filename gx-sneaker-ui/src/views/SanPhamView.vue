<script setup>
import { ref, computed, onMounted } from "vue"
import axios from "axios"

const API_SAN_PHAM = "http://localhost:8080/api/san-pham"
const API_THUONG_HIEU = "http://localhost:8080/api/thuong-hieu"
const API_XUAT_XU = "http://localhost:8080/api/xuat-xu"
const API_CHAT_LIEU = "http://localhost:8080/api/chat-lieu"
const API_CO_GIAY = "http://localhost:8080/api/co-giay"
const API_DE_GIAY = "http://localhost:8080/api/de-giay"
const API_DANH_MUC = "http://localhost:8080/api/danh-muc"

// ====================== DATA ======================

const list = ref([])

const thuongHieuList = ref([])
const xuatXuList = ref([])
const chatLieuList = ref([])
const coGiayList = ref([])
const deGiayList = ref([])
const danhMucList = ref([])

const loading = ref(false)

// ====================== SEARCH ======================

const keyword = ref("")

// ====================== PAGINATION ======================

const page = ref(1)
const pageSize = 10

// ====================== FORM ======================

const form = ref({
  id: null,

  idThuongHieu: "",
  idXuatXu: "",
  idChatLieu: "",
  idCoGiay: "",
  idDeGiay: "",
  idDanhMuc: "",

  maSanPham: "",
  tenSanPham: "",

  gioiTinh: "Unisex",

  moTaNgan: "",
  moTaChiTiet: "",

  trangThai: true,

  nguoiTao: "Admin",
  nguoiCapNhat: ""
})

// ====================== ERROR ======================

const errors = ref({})

// ====================== TOAST ======================

const toast = ref("")
const toastType = ref("success")

const showToast = (msg, type = "success") => {
  toast.value = msg
  toastType.value = type

  setTimeout(() => {
    toast.value = ""
  }, 2500)
}

// ====================== LOAD ======================

const load = async () => {
  try {

    loading.value = true

    const [
      sp,
      th,
      xx,
      cl,
      cg,
      dg,
      dm
    ] = await Promise.all([
      axios.get(API_SAN_PHAM),
      axios.get(API_THUONG_HIEU),
      axios.get(API_XUAT_XU),
      axios.get(API_CHAT_LIEU),
      axios.get(API_CO_GIAY),
      axios.get(API_DE_GIAY),
      axios.get(API_DANH_MUC)
    ])

    list.value = sp.data

    thuongHieuList.value = th.data
    xuatXuList.value = xx.data
    chatLieuList.value = cl.data
    coGiayList.value = cg.data
    deGiayList.value = dg.data
    danhMucList.value = dm.data

  } catch (e) {
    console.log(e)
    showToast("Lỗi tải dữ liệu", "error")
  } finally {
    loading.value = false
  }
}

// ====================== VALIDATE ======================

const validate = () => {

  errors.value = {}

  if (!form.value.maSanPham?.trim()) {
    errors.value.maSanPham =
      "Mã sản phẩm không được để trống"
  }

  if (!form.value.tenSanPham?.trim()) {
    errors.value.tenSanPham =
      "Tên sản phẩm không được để trống"
  }

  if (!form.value.idThuongHieu) {
    errors.value.idThuongHieu =
      "Vui lòng chọn thương hiệu"
  }

  if (!form.value.idDanhMuc) {
    errors.value.idDanhMuc =
      "Vui lòng chọn danh mục"
  }

  if (!form.value.idXuatXu) {
    errors.value.idXuatXu =
      "Vui lòng chọn xuất xứ"
  }

  if (!form.value.idChatLieu) {
    errors.value.idChatLieu =
      "Vui lòng chọn chất liệu"
  }

  if (!form.value.idCoGiay) {
    errors.value.idCoGiay =
      "Vui lòng chọn cổ giày"
  }

  if (!form.value.idDeGiay) {
    errors.value.idDeGiay =
      "Vui lòng chọn đế giày"
  }

  return Object.keys(errors.value).length === 0
}

// ====================== SAVE ======================

const save = async () => {

  if (!validate()) {
    showToast(
      "Vui lòng kiểm tra dữ liệu",
      "error"
    )
    return
  }

  try {

    if (form.value.id) {

      form.value.nguoiCapNhat = "Admin"

      await axios.put(
        `${API_SAN_PHAM}/${form.value.id}`,
        form.value
      )

      showToast("Cập nhật thành công")

    } else {

      await axios.post(
        API_SAN_PHAM,
        form.value
      )

      showToast("Thêm mới thành công")
    }

    reset()
    load()

  } catch (e) {
    console.log(e)
    showToast("Có lỗi xảy ra", "error")
  }
}

// ====================== DELETE ======================

const del = async (id) => {

  if (
    !confirm("Bạn chắc chắn muốn xóa?")
  ) return

  try {

    await axios.delete(
      `${API_SAN_PHAM}/${id}`
    )

    showToast("Xóa thành công")

    load()

  } catch (e) {

    showToast(
      "Không thể xóa",
      "error"
    )
  }
}

// ====================== EDIT ======================

const edit = (item) => {

  form.value = {
    ...item
  }

  window.scrollTo({
    top: 0,
    behavior: "smooth"
  })
}

// ====================== RESET ======================

const reset = () => {

  form.value = {

    id: null,

    idThuongHieu: "",
    idXuatXu: "",
    idChatLieu: "",
    idCoGiay: "",
    idDeGiay: "",
    idDanhMuc: "",

    maSanPham: "",
    tenSanPham: "",

    gioiTinh: "Unisex",

    moTaNgan: "",
    moTaChiTiet: "",

    trangThai: true,

    nguoiTao: "Admin",
    nguoiCapNhat: ""
  }

  errors.value = {}
}

// ====================== SEARCH ======================

const filteredList = computed(() => {

  return list.value.filter(i => {

    const text =
      `${i.maSanPham} ${i.tenSanPham}`
        .toLowerCase()

    return text.includes(
      keyword.value.toLowerCase()
    )
  })
})

// ====================== PAGINATION ======================

const totalPages = computed(() =>
  Math.ceil(
    filteredList.value.length /
    pageSize
  )
)

const paginatedList = computed(() => {

  const start =
    (page.value - 1) * pageSize

  return filteredList.value.slice(
    start,
    start + pageSize
  )
})

const changePage = (p) => {
  page.value = p
}

// ====================== HELPER ======================

const getThuongHieuName = (id) => {
  const item = thuongHieuList.value.find(x => x.id == id)
  return item ? item.ten : ""
}

const getDanhMucName = (id) => {
  const item = danhMucList.value.find(x => x.id == id)
  return item ? item.ten : ""
}

const getXuatXuName = (id) => {
  const item = xuatXuList.value.find(x => x.id == id)
  return item ? item.ten : ""
}

const getChatLieuName = (id) => {
  const item = chatLieuList.value.find(x => x.id == id)
  return item ? item.ten : ""
}

const getCoGiayName = (id) => {
  const item = coGiayList.value.find(x => x.id == id)
  return item ? item.ten : ""
}

const getDeGiayName = (id) => {
  const item = deGiayList.value.find(x => x.id == id)
  return item ? item.ten : ""
}

// ====================== INIT ======================

onMounted(load)

</script>
<template>
  <div class="container">

    <h2>👟 QUẢN LÝ SẢN PHẨM</h2>

    <!-- TOAST -->
    <div
      v-if="toast"
      :class="['toast', toastType]"
    >
      {{ toast }}
    </div>

    <!-- FORM -->

    <div class="card">

      <h3>
        {{ form.id ? "CẬP NHẬT SẢN PHẨM" : "THÊM SẢN PHẨM" }}
      </h3>

      <div class="grid">

        <!-- Mã -->

        <div>
          <label>Mã sản phẩm</label>

          <input
            v-model="form.maSanPham"
            placeholder="SP001"
          />

          <small class="error">
            {{ errors.maSanPham }}
          </small>
        </div>

        <!-- Tên -->

        <div>
          <label>Tên sản phẩm</label>

          <input
            v-model="form.tenSanPham"
            placeholder="Nike Air Force 1"
          />

          <small class="error">
            {{ errors.tenSanPham }}
          </small>
        </div>

        <!-- THƯƠNG HIỆU -->

        <div>
          <label>Thương hiệu</label>

          <select
            v-model="form.idThuongHieu"
          >
            <option value="">
              Chọn thương hiệu
            </option>

            <option
              v-for="i in thuongHieuList"
              :key="i.id"
              :value="i.id"
            >
              {{ i.ten }}
            </option>
          </select>

          <small class="error">
            {{ errors.idThuongHieu }}
          </small>
        </div>

        <!-- DANH MỤC -->

        <div>
          <label>Danh mục</label>

          <select
            v-model="form.idDanhMuc"
          >
            <option value="">
              Chọn danh mục
            </option>

            <option
              v-for="i in danhMucList"
              :key="i.id"
              :value="i.id"
            >
              {{ i.ten }}
            </option>
          </select>

          <small class="error">
            {{ errors.idDanhMuc }}
          </small>
        </div>

        <!-- XUẤT XỨ -->

        <div>
          <label>Xuất xứ</label>

          <select
            v-model="form.idXuatXu"
          >
            <option value="">
              Chọn xuất xứ
            </option>

            <option
              v-for="i in xuatXuList"
              :key="i.id"
              :value="i.id"
            >
              {{ i.ten }}
            </option>
          </select>

          <small class="error">
            {{ errors.idXuatXu }}
          </small>
        </div>

        <!-- CHẤT LIỆU -->

        <div>
          <label>Chất liệu</label>

          <select
            v-model="form.idChatLieu"
          >
            <option value="">
              Chọn chất liệu
            </option>

            <option
              v-for="i in chatLieuList"
              :key="i.id"
              :value="i.id"
            >
              {{ i.ten }}
            </option>
          </select>

          <small class="error">
            {{ errors.idChatLieu }}
          </small>
        </div>

        <!-- CỔ GIÀY -->

        <div>
          <label>Cổ giày</label>

          <select
            v-model="form.idCoGiay"
          >
            <option value="">
              Chọn cổ giày
            </option>

            <option
              v-for="i in coGiayList"
              :key="i.id"
              :value="i.id"
            >
              {{ i.ten }}
            </option>
          </select>

          <small class="error">
            {{ errors.idCoGiay }}
          </small>
        </div>

        <!-- ĐẾ GIÀY -->

        <div>
          <label>Đế giày</label>

          <select
            v-model="form.idDeGiay"
          >
            <option value="">
              Chọn đế giày
            </option>

            <option
              v-for="i in deGiayList"
              :key="i.id"
              :value="i.id"
            >
              {{ i.ten }}
            </option>
          </select>

          <small class="error">
            {{ errors.idDeGiay }}
          </small>
        </div>

      </div>

      <!-- GIỚI TÍNH -->

      <div class="radio-group">

        <label>
          <input
            type="radio"
            value="Nam"
            v-model="form.gioiTinh"
          />
          Nam
        </label>

        <label>
          <input
            type="radio"
            value="Nữ"
            v-model="form.gioiTinh"
          />
          Nữ
        </label>

        <label>
          <input
            type="radio"
            value="Unisex"
            v-model="form.gioiTinh"
          />
          Unisex
        </label>

      </div>

      <!-- MÔ TẢ NGẮN -->

      <div class="mt-10">

        <label>Mô tả ngắn</label>

        <textarea
          rows="3"
          v-model="form.moTaNgan"
        />
      </div>

      <!-- MÔ TẢ CHI TIẾT -->

      <div class="mt-10">

        <label>Mô tả chi tiết</label>

        <textarea
          rows="5"
          v-model="form.moTaChiTiet"
        />
      </div>

      <!-- TRẠNG THÁI -->

      <div class="radio-group">

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

      <!-- BUTTON -->

      <div class="action">

        <button
          class="save-btn"
          @click="save"
        >
          {{ form.id ? "Cập nhật" : "Thêm mới" }}
        </button>

        <button
          class="reset-btn"
          @click="reset"
        >
          Làm mới
        </button>

      </div>

    </div>

    <!-- SEARCH -->

    <div class="card">

      <input
        v-model="keyword"
        placeholder="🔍 Tìm theo mã hoặc tên sản phẩm..."
      />

    </div>

    <!-- TABLE -->

    <div class="card">

      <table>

        <thead>

        <tr>
          <th>Mã</th>
          <th>Tên</th>
          <th>Thương hiệu</th>
          <th>Danh mục</th>
          <th>Xuất xứ</th>
          <th>Chất liệu</th>
          <th>Cổ giày</th>
          <th>Đế giày</th>
          <th>Giới tính</th>
          <th>Trạng thái</th>
          <th>Thao tác</th>
        </tr>

        </thead>

        <tbody>

        <tr
          v-for="i in paginatedList"
          :key="i.id"
        >
          <td>{{ i.maSanPham }}</td>

          <td>{{ i.tenSanPham }}</td>

          <td>{{ getThuongHieuName(i.idThuongHieu) }}</td>

          <td>{{ getDanhMucName(i.idDanhMuc) }}</td>

          <td>{{ getXuatXuName(i.idXuatXu) }}</td>

          <td>{{ getChatLieuName(i.idChatLieu) }}</td>

          <td>{{ getCoGiayName(i.idCoGiay) }}</td>

          <td>{{ getDeGiayName(i.idDeGiay) }}</td>

          <td>{{ i.gioiTinh }}</td>

          <td>
    <span
      :class="
        i.trangThai
          ? 'active-status'
          : 'inactive-status'
      "
    >
      {{
        i.trangThai
          ? 'Hoạt động'
          : 'Ngừng hoạt động'
      }}
    </span>
          </td>

          <td>
            <button
              class="edit-btn"
              @click="edit(i)"
            >
              ✏️
            </button>

            <button
              class="delete-btn"
              @click="del(i.id)"
            >
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
  background: #f5f7fa;
  min-height: 100vh;
  font-family: Arial, sans-serif;
}

h2 {
  margin-bottom: 20px;
  color: #2c3e50;
}

h3 {
  margin-bottom: 15px;
  color: #34495e;
}

.card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,.08);
}

.grid {
  display: grid;
  grid-template-columns: repeat(2,1fr);
  gap: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: 600;
}

input,
select,
textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  outline: none;
  transition: .2s;
  box-sizing: border-box;
}

input:focus,
select:focus,
textarea:focus {
  border-color: #409eff;
}

textarea {
  resize: vertical;
}

.error {
  color: red;
  font-size: 12px;
  margin-top: 3px;
  display: block;
}

.mt-10 {
  margin-top: 15px;
}

.radio-group {
  display: flex;
  gap: 25px;
  margin-top: 15px;
}

.radio-group label {
  display: flex;
  align-items: center;
  gap: 5px;
  font-weight: normal;
}

.action {
  margin-top: 20px;
}

.save-btn {
  background: #28a745;
  color: white;
  border: none;
  padding: 10px 18px;
  border-radius: 8px;
  cursor: pointer;
  margin-right: 10px;
}

.save-btn:hover {
  background: #218838;
}

.reset-btn {
  background: #6c757d;
  color: white;
  border: none;
  padding: 10px 18px;
  border-radius: 8px;
  cursor: pointer;
}

.reset-btn:hover {
  background: #5a6268;
}

.edit-btn {
  background: #ffc107;
  border: none;
  padding: 8px 10px;
  border-radius: 6px;
  cursor: pointer;
  margin-right: 5px;
}

.delete-btn {
  background: #dc3545;
  color: white;
  border: none;
  padding: 8px 10px;
  border-radius: 6px;
  cursor: pointer;
}

.edit-btn:hover {
  opacity: .9;
}

.delete-btn:hover {
  opacity: .9;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: #0d6efd;
  color: white;
}

th,
td {
  border: 1px solid #e9ecef;
  padding: 12px;
  text-align: center;
}

tbody tr:hover {
  background: #f8f9fa;
}

.active-status {
  color: #28a745;
  font-weight: bold;
}

.inactive-status {
  color: #dc3545;
  font-weight: bold;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  gap: 5px;
}

.pagination button {
  border: none;
  padding: 8px 14px;
  border-radius: 6px;
  background: #e9ecef;
  cursor: pointer;
}

.pagination button:hover {
  background: #d6d8db;
}

.activePage {
  background: #0d6efd !important;
  color: white;
}

.pagination button:disabled {
  opacity: .5;
  cursor: not-allowed;
}

.toast {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 12px 18px;
  border-radius: 8px;
  color: white;
  z-index: 9999;
  animation: fadeIn .3s ease;
}

.success {
  background: #28a745;
}

.error-toast {
  background: #dc3545;
}

.error {
  color: red;
}

@keyframes fadeIn {
  from {
    transform: translateY(-10px);
    opacity: 0;
  }

  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@media(max-width: 768px) {

  .grid {
    grid-template-columns: 1fr;
  }

  .radio-group {
    flex-direction: column;
    gap: 10px;
  }

  table {
    display: block;
    overflow-x: auto;
  }
}

</style>

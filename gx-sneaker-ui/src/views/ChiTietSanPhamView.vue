<script setup>

import { ref, computed, onMounted } from "vue"
import { useRoute } from "vue-router"

import axios from "axios"

import * as mauSacService
  from "@/services/mauSacService"

import * as kichThuocService
  from "@/services/kichThuocService"

import * as ctspService
  from "@/services/chiTietSanPhamService"



const route = useRoute()

// =======================

const product = ref(null)

const productDetails = ref([])

const mauSacList = ref([])

const kichThuocList = ref([])

const loading = ref(false)

const search = ref("")

const showModal = ref(false)

const isEdit = ref(false)

const showColorModal = ref(false)

const showSizeModal = ref(false)

const newColor = ref("")

const newSize = ref("")
// =======================

const form = ref({

  id:null,

  idSanPham:null,

  idMauSac:null,

  idKichThuoc:null,

  maChiTiet:"",

  soLuongTon:0,

  giaNhap:0,

  giaBan:0,

  trangThai:true

})
const errors = ref({

  maChiTiet: "",

  idMauSac: "",

  idKichThuoc: "",

  soLuongTon: "",

  giaNhap: "",

  giaBan: ""

})
// =======================

const formatMoney = (value)=>{

  if(!value){

    return "0 ₫"

  }

  return Number(value)
    .toLocaleString(
      "vi-VN"
    ) + " ₫"

}

// =======================

const loadProduct = async()=>{

  try{

    const id = route.params.id

    const res =
      await axios.get(
        `http://localhost:8080/api/san-pham/${id}`
      )

    product.value =
      res.data

  }catch(e){

    console.log(e)

  }

}

// =======================

const loadDetails = async()=>{

  try{

    loading.value = true

    const id = route.params.id

    const res =
      await ctspService
        .getBySanPham(id)

    productDetails.value =
      res.data

  }catch(e){

    console.log(e)

  }finally{

    loading.value = false

  }

}

// =======================

const loadCombobox = async()=>{

  try{

    const [

      mauSacRes,

      kichThuocRes

    ] = await Promise.all([

      mauSacService.getAll(),

      kichThuocService.getAll()

    ])

    mauSacList.value =
      mauSacRes.data

    kichThuocList.value =
      kichThuocRes.data

  }catch(e){

    console.log(e)

  }

}

// =======================

const filteredDetails =
  computed(()=>{

    if(!search.value){

      return productDetails.value

    }

    const key =
      search.value
        .toLowerCase()

    return productDetails.value
      .filter(item=>

        item.maChiTiet
          ?.toLowerCase()
          .includes(key)

        ||

        item.tenMauSac
          ?.toLowerCase()
          .includes(key)

        ||

        item.size
          ?.toString()
          .includes(key)

      )

  })

// =======================

const resetForm = ()=>{

  form.value = {

    id:null,

    idSanPham:
      Number(route.params.id),

    idMauSac:null,

    idKichThuoc:null,

    maChiTiet:"",

    soLuongTon:0,

    giaNhap:0,

    giaBan:0,

    trangThai:true

  }

}

// =======================

const openCreate = ()=>{

  isEdit.value = false

  resetForm()

  showModal.value = true

}

// =======================

const editItem = (item)=>{

  isEdit.value = true

  form.value = {

    ...item

  }

  showModal.value = true

}
//============================================
const validateForm = () => {

  errors.value = {

    maChiTiet: "",

    idMauSac: "",

    idKichThuoc: "",

    soLuongTon: "",

    giaNhap: "",

    giaBan: ""

  }

  let valid = true

  if (!form.value.maChiTiet.trim()) {

    errors.value.maChiTiet = "Vui lòng nhập mã chi tiết"

    valid = false

  }

  if (!form.value.idMauSac) {

    errors.value.idMauSac = "Vui lòng chọn màu sắc"

    valid = false

  }

  if (!form.value.idKichThuoc) {

    errors.value.idKichThuoc = "Vui lòng chọn kích thước"

    valid = false

  }

  if (form.value.soLuongTon === "" || form.value.soLuongTon <= 0) {
    errors.value.soLuongTon = "Số lượng tồn phải lớn hơn 0"
    valid = false
  }

  if (form.value.giaNhap === "" || form.value.giaNhap <= 0) {

    errors.value.giaNhap = "Giá nhập phải lớn hơn 0"

    valid = false

  }

  if (form.value.giaBan === "" || form.value.giaBan <= 0) {

    errors.value.giaBan = "Giá bán phải lớn hơn 0"

    valid = false

  }

  if (
    Number(form.value.giaBan)
    <
    Number(form.value.giaNhap)
  ) {

    errors.value.giaBan = "Giá bán phải lớn hơn hoặc bằng giá nhập"

    valid = false

  }

  return valid

}
// =======================

const saveItem = async()=>{

  if (!validateForm()) {

    return

  }

  try{

    if(isEdit.value){

      await ctspService.update(

        form.value.id,

        form.value

      )

    }else{

      await ctspService.create(

        form.value

      )

    }

    showModal.value = false

    await loadDetails()

  }catch(e){

    console.log(e)

    alert(
      "Lỗi lưu dữ liệu"
    )

  }

}

// =======================

const deleteItem = async(id)=>{

  if(

    !confirm(
      "Bạn có chắc muốn xóa?"
    )

  ){

    return

  }

  try{

    await ctspService.remove(id)

    await loadDetails()

  }catch(e){

    console.log(e)

  }

}

// =======================

const closeModal = ()=>{

  showModal.value = false

}
const addColor = async () => {

  if (!newColor.value) {
    alert("Nhập tên màu sắc")
    return
  }

  try {

    await mauSacService.create({
      ten: newColor.value
    })

    await loadCombobox()

    newColor.value = ""

    showColorModal.value = false

  } catch (e) {

    console.log(e)

    alert("Không thể thêm màu sắc")

  }

}
const addSize = async () => {

  if (!newSize.value) {
    alert("Nhập kích thước")
    return
  }

  try {

    await kichThuocService.create({
      size: newSize.value
    })

    await loadCombobox()

    newSize.value = ""

    showSizeModal.value = false

  } catch (e) {

    console.log(e)

    alert("Không thể thêm kích thước")

  }

}

// =======================

onMounted(()=>{

  loadProduct()

  loadDetails()

  loadCombobox()

})

</script>
<template>

  <div class="container">

    <!-- PRODUCT INFO -->

    <div
      v-if="product"
      class="product-card"
    >

      <div class="product-left">

        <img
          :src="`/images/${product.anhDaiDien}`"
          class="product-image"
          @error="$event.target.src='/images/no-image.png'"
        />

      </div>

      <div class="product-right">

        <h1>
          {{ product.tenSanPham }}
        </h1>

        <p>
          <strong>Mã sản phẩm:</strong>
          {{ product.maSanPham }}
        </p>

        <p>
          <strong>Giới tính:</strong>
          {{ product.gioiTinh }}
        </p>

        <p>
          <strong>Mô tả:</strong>
          {{ product.moTaNgan }}
        </p>

        <div class="status-box">

          <span
            class="status"
            :class="
              product.trangThai
                ? 'active'
                : 'inactive'
            "
          >
            {{
              product.trangThai
                ? 'Đang bán'
                : 'Ngừng bán'
            }}
          </span>

        </div>

      </div>

    </div>

    <!-- TOOLBAR -->

    <div class="toolbar">

      <input
        v-model="search"
        class="search"
        placeholder="🔍 Tìm mã chi tiết, màu sắc, size..."
      />

      <button
        class="btn-add"
        @click="openCreate"
      >
        + Thêm biến thể
      </button>

    </div>

    <!-- TABLE -->

    <div class="table-wrapper">

      <table>

        <thead>

        <tr>

          <th>Mã CT</th>

          <th>Màu sắc</th>

          <th>Size</th>

          <th>Tồn kho</th>

          <th>Giá nhập</th>

          <th>Giá bán</th>

          <th>Trạng thái</th>

          <th width="180">
            Thao tác
          </th>

        </tr>

        </thead>

        <tbody>

        <tr
          v-if="loading"
        >
          <td colspan="8">
            Đang tải dữ liệu...
          </td>
        </tr>

        <tr
          v-for="item in filteredDetails"
          :key="item.id"
        >

          <td>
            {{ item.maChiTiet }}
          </td>

          <td>
            {{ item.tenMauSac }}
          </td>

          <td>
            {{ item.size }}
          </td>

          <td>
            {{ item.soLuongTon }}
          </td>

          <td>
            {{ formatMoney(item.giaNhap) }}
          </td>

          <td>
            {{ formatMoney(item.giaBan) }}
          </td>

          <td>

            <span
              class="status"
              :class="
                item.trangThai
                  ? 'active'
                  : 'inactive'
              "
            >

              {{
                item.trangThai
                  ? 'Hoạt động'
                  : 'Ngừng'
              }}

            </span>

          </td>

          <td>

            <div class="action-group">

              <button
                class="btn-edit"
                @click="editItem(item)"
              >
                ✏ Sửa
              </button>

              <button
                class="btn-delete"
                @click="deleteItem(item.id)"
              >
                🗑 Xóa
              </button>

            </div>

          </td>

        </tr>

        <tr
          v-if="
            !loading &&
            filteredDetails.length === 0
          "
        >
          <td colspan="8">
            Không có dữ liệu
          </td>
        </tr>

        </tbody>

      </table>

    </div>

    <!-- MODAL -->

    <div
      v-if="showModal"
      class="modal"
    >

      <div class="modal-content">

        <div class="modal-header">

          <h2>

            {{
              isEdit
                ? 'Cập nhật biến thể'
                : 'Thêm biến thể'
            }}

          </h2>

        </div>

        <!-- FORM -->

        <div class="form-grid">

          <!-- MÃ CHI TIẾT -->
          <div>
            <label>Mã chi tiết</label>

            <input
              v-model="form.maChiTiet"
              :class="{ 'error-input': errors.maChiTiet }"
            />

            <div class="error">
              {{ errors.maChiTiet }}
            </div>
          </div>

          <!-- SỐ LƯỢNG TỒN -->
          <div>
            <label>Số lượng tồn</label>

            <input
              type="number"
              v-model="form.soLuongTon"
              :class="{ 'error-input': errors.soLuongTon }"
            />

            <div class="error">
              {{ errors.soLuongTon }}
            </div>
          </div>

          <!-- GIÁ NHẬP -->
          <div>
            <label>Giá nhập</label>

            <input
              type="number"
              v-model="form.giaNhap"
              :class="{ 'error-input': errors.giaNhap }"
            />

            <div class="error">
              {{ errors.giaNhap }}
            </div>
          </div>

          <!-- GIÁ BÁN -->
          <div>
            <label>Giá bán</label>

            <input
              type="number"
              v-model="form.giaBan"
              :class="{ 'error-input': errors.giaBan }"
            />

            <div class="error">
              {{ errors.giaBan }}
            </div>
          </div>

          <!-- MÀU SẮC -->
          <div>
            <label>Màu sắc</label>

            <div class="inline-group">

              <select
                v-model="form.idMauSac"
                :class="{ 'error-input': errors.idMauSac }"
              >

                <option :value="null">
                  Chọn màu sắc
                </option>

                <option
                  v-for="m in mauSacList"
                  :key="m.id"
                  :value="m.id"
                >
                  {{ m.ten }}
                </option>

              </select>

              <button
                type="button"
                class="btn-mini"
                @click="showColorModal = true"
              >
                +
              </button>

            </div>

            <div class="error">
              {{ errors.idMauSac }}
            </div>
          </div>

          <!-- KÍCH THƯỚC -->
          <div>
            <label>Kích thước</label>

            <div class="inline-group">

              <select
                v-model="form.idKichThuoc"
                :class="{ 'error-input': errors.idKichThuoc }"
              >

                <option :value="null">
                  Chọn kích thước
                </option>

                <option
                  v-for="k in kichThuocList"
                  :key="k.id"
                  :value="k.id"
                >
                  {{ k.size }}
                </option>

              </select>

              <button
                type="button"
                class="btn-mini"
                @click="showSizeModal = true"
              >
                +
              </button>

            </div>

            <div class="error">
              {{ errors.idKichThuoc }}
            </div>
          </div>

          <!-- TRẠNG THÁI -->
          <div>
            <label>Trạng thái</label>

            <select v-model="form.trangThai">
              <option :value="true">Hoạt động</option>
              <option :value="false">Ngừng</option>
            </select>
          </div>

        </div>

        <!-- BUTTON -->

        <div class="modal-footer">

          <button
            class="btn-save"
            @click="saveItem"
          >
            💾 Lưu
          </button>

          <button
            class="btn-cancel"
            @click="closeModal"
          >
            ✖ Hủy
          </button>

        </div>

      </div>

    </div>

  </div>

  <!-- MODAL THÊM MÀU SẮC NHANH -->
  <div
    v-if="showColorModal"
    class="modal"
  >

    <div class="modal-content small">

      <h3>Thêm màu sắc</h3>

      <input
        v-model="newColor"
        placeholder="Nhập tên màu"
      >

      <div class="modal-footer">

        <button
          class="btn-save"
          @click="addColor"
        >
          Lưu
        </button>

        <button
          class="btn-cancel"
          @click="showColorModal=false"
        >
          Hủy
        </button>

      </div>

    </div>

  </div>

  <!-- MODAL THÊM KÍCH THƯỚC NHANH -->
  <div
    v-if="showSizeModal"
    class="modal"
  >

    <div class="modal-content small">

      <h3>Thêm kích thước</h3>

      <input
        v-model="newSize"
        placeholder="Ví dụ 38"
      >

      <div class="modal-footer">

        <button
          class="btn-save"
          @click="addSize"
        >
          Lưu
        </button>

        <button
          class="btn-cancel"
          @click="showSizeModal=false"
        >
          Hủy
        </button>

      </div>

    </div>

  </div>

</template>
<style scoped>

.container{
  padding:24px;
  background:#f4f6f9;
  min-height:100vh;
}

/* =========================
   PRODUCT CARD
========================= */

.product-card{
  display:flex;
  gap:30px;
  background:#fff;
  border-radius:16px;
  padding:25px;
  margin-bottom:25px;
  box-shadow:0 4px 15px rgba(0,0,0,.08);
}

.product-left{
  width:320px;
  flex-shrink:0;
}

.product-image{
  width:100%;
  height:320px;
  object-fit:cover;
  border-radius:12px;
  border:1px solid #eee;
}

.product-right{
  flex:1;
}

.product-right h1{
  margin:0 0 15px;
  font-size:32px;
  color:#222;
}

.product-right p{
  margin:10px 0;
  font-size:15px;
  color:#555;
  line-height:1.6;
}

/* =========================
   STATUS
========================= */

.status{
  padding:6px 14px;
  border-radius:20px;
  font-size:13px;
  font-weight:600;
}

.active{
  background:#d1fae5;
  color:#065f46;
}

.inactive{
  background:#fee2e2;
  color:#991b1b;
}

.status-box{
  margin-top:15px;
}

/* =========================
   TOOLBAR
========================= */

.toolbar{
  display:flex;
  justify-content:space-between;
  align-items:center;
  margin-bottom:20px;
}

.search{
  width:350px;
  padding:12px;
  border:1px solid #ddd;
  border-radius:10px;
  outline:none;
  font-size:14px;
}

.search:focus{
  border-color:#2563eb;
}

.btn-add{
  background:#16a34a;
  color:white;
  border:none;
  padding:12px 18px;
  border-radius:10px;
  cursor:pointer;
  font-weight:600;
}

.btn-add:hover{
  opacity:.9;
}

/* =========================
   TABLE
========================= */

.table-wrapper{
  background:white;
  border-radius:16px;
  overflow:hidden;
  box-shadow:0 4px 15px rgba(0,0,0,.08);
}

table{
  width:100%;
  border-collapse:collapse;
}

thead{
  background:#2563eb;
  color:white;
}

th{
  padding:15px;
  text-align:center;
  font-weight:600;
}

td{
  padding:14px;
  border-bottom:1px solid #eee;
  text-align:center;
}

tbody tr:hover{
  background:#f8fafc;
}

/* =========================
   ACTION BUTTON
========================= */

.action-group{
  display:flex;
  justify-content:center;
  gap:8px;
}

.btn-edit{
  background:#f59e0b;
  color:white;
  border:none;
  padding:8px 12px;
  border-radius:8px;
  cursor:pointer;
}

.btn-delete{
  background:#dc2626;
  color:white;
  border:none;
  padding:8px 12px;
  border-radius:8px;
  cursor:pointer;
}

.btn-edit:hover,
.btn-delete:hover{
  opacity:.85;
}

/* =========================
   MODAL
========================= */

.modal{
  position:fixed;
  inset:0;
  background:rgba(0,0,0,.45);
  display:flex;
  justify-content:center;
  align-items:center;
  z-index:999;
}

.modal-content{
  width:850px;
  background:white;
  border-radius:18px;
  padding:25px;
  box-shadow:0 10px 30px rgba(0,0,0,.2);
}

.modal-header{
  margin-bottom:20px;
}

.modal-header h2{
  margin:0;
  color:#222;
}

/* =========================
   FORM
========================= */

.form-grid{
  display:grid;
  grid-template-columns:repeat(2,1fr);
  gap:18px;
}

.form-grid label{
  display:block;
  margin-bottom:6px;
  font-weight:600;
  color:#444;
}

.form-grid input,
.form-grid select{
  width:100%;
  padding:11px;
  border:1px solid #ddd;
  border-radius:10px;
  outline:none;
}

.form-grid input:focus,
.form-grid select:focus{
  border-color:#2563eb;
}

/* =========================
   FOOTER
========================= */

.modal-footer{
  margin-top:25px;
  display:flex;
  justify-content:flex-end;
  gap:10px;
}

.btn-save{
  background:#16a34a;
  color:white;
  border:none;
  padding:12px 18px;
  border-radius:10px;
  cursor:pointer;
}

.btn-cancel{
  background:#6b7280;
  color:white;
  border:none;
  padding:12px 18px;
  border-radius:10px;
  cursor:pointer;
}

.btn-save:hover,
.btn-cancel:hover{
  opacity:.9;
}

/* =========================
   RESPONSIVE
========================= */

@media(max-width:900px){

  .product-card{
    flex-direction:column;
  }

  .product-left{
    width:100%;
  }

  .product-image{
    height:250px;
  }

  .toolbar{
    flex-direction:column;
    gap:15px;
  }

  .search{
    width:100%;
  }

  .modal-content{
    width:95%;
  }

  .form-grid{
    grid-template-columns:1fr;
  }
}
.inline-group{
  display:flex;
  gap:8px;
}

.inline-group select{
  flex:1;
}

.btn-mini{
  width:42px;
  border:none;
  border-radius:8px;
  background:#16a34a;
  color:white;
  font-size:20px;
  cursor:pointer;
}

.btn-mini:hover{
  opacity:.9;
}

.modal-content.small{
  width:400px;
}
.error{
  color:#dc2626;
  font-size:13px;
  margin-top:4px;
  margin-left:2px;
  min-height:18px;
}

.form-grid input.error-input,
.form-grid select.error-input{
  border:1px solid #dc2626;
}
</style>

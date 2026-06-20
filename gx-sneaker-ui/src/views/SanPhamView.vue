<script setup>

import { ref, computed, onMounted } from "vue"
import axios from "axios"

import {
  getAll,
  create,
  update,
  remove
} from "@/services/sanPhamService"

import { useRouter } from "vue-router"

const router = useRouter()

const goToDetail = (id) => {
  router.push(`/san-pham/${id}/chi-tiet`)
}

// CATEGORY SERVICE

import {
  getAllThuongHieu
} from "@/services/thuongHieuService"

import {
  getAll as getAllChatLieu
} from "@/services/chatLieuService"


import {
  getAll as getAllCoGiay
} from "@/services/coGiayService"


import {
  getAll as getAllDeGiay
} from "@/services/deGiayService"


import {
  getAll as getAllDanhMuc
} from "@/services/danhMucService"


import {
  getAll as getAllXuatXu
} from "@/services/xuatXuService"


// ================= API =================

const UPLOAD_API =
  "http://localhost:8080/api/upload"



// ================= DATA =================

const products = ref([])

const loading = ref(false)

const search = ref("")


const showModal = ref(false)

const isEdit = ref(false)


const imageFile = ref(null)



// ================= CATEGORY =================

const thuongHieuList = ref([])

const xuatXuList = ref([])

const chatLieuList = ref([])

const coGiayList = ref([])

const deGiayList = ref([])

const danhMucList = ref([])




// ================= FORM DTO =================


const form = ref({

  id:null,

  idThuongHieu:null,

  idXuatXu:null,

  idChatLieu:null,

  idCoGiay:null,

  idDeGiay:null,

  idDanhMuc:null,


  maSanPham:"",

  tenSanPham:"",


  gioiTinh:"Unisex",


  moTaNgan:"",

  moTaChiTiet:"",


  anhDaiDien:"",

  preview:"",
  trangThai:true,


  nguoiTao:"ADMIN",

  nguoiCapNhat:"ADMIN"

})





// ================= LOAD PRODUCT =================


const loadData = async()=>{


  try{


    loading.value=true


    const res = await getAll()


    products.value = res.data



  }catch(e){


    console.log(e)


  }finally{


    loading.value=false


  }


}






// ================= LOAD CATEGORY =================


const loadCategoryData = async()=>{


  try{


    const [

      th,

      xx,

      cl,

      cg,

      dg,

      dm

    ] = await Promise.all([


      getAllThuongHieu(),


      getAllXuatXu(),


      getAllChatLieu(),


      getAllCoGiay(),


      getAllDeGiay(),


      getAllDanhMuc()


    ])




    thuongHieuList.value = th.data

    xuatXuList.value = xx.data

    chatLieuList.value = cl.data

    coGiayList.value = cg.data

    deGiayList.value = dg.data

    danhMucList.value = dm.data



  }catch(e){


    console.log(e)


  }


}







onMounted(()=>{


  loadData()


  loadCategoryData()


})
// ================= SEARCH =================


const filteredProducts = computed(()=>{
  if(!search.value.trim())

    return products.value
  const key =
    search.value
      .toLowerCase()

  return products.value.filter(item=>
    item.tenSanPham
      ?.toLowerCase()
      .includes(key)



    ||



    item.maSanPham
      ?.toLowerCase()
      .includes(key)
  )
})
// ================= IMAGE =================

const previewImage = computed(()=>{


  if(form.value.preview){

    return form.value.preview

  }


  if(form.value.anhDaiDien){

    return `/images/${form.value.anhDaiDien}`

  }


  return "/images/no-image.png"


})

const handleImageUpload=(event)=>{
  const file = event.target.files[0]

  if(file){
    imageFile.value = file

    form.value.preview =
      URL.createObjectURL(file)

    form.value.anhDaiDien =
      file.name
  }
}
// ================= UPLOAD =================


const uploadImage = async()=>{


  if(!imageFile.value){


    alert("Chưa chọn ảnh")


    return

  }
  try{


    const formData =
      new FormData()
    formData.append(

      "file",

      imageFile.value

    )
    const res =
      await axios.post(

        UPLOAD_API,

        formData,

        {

          headers:{

            "Content-Type":
              "multipart/form-data"

          }

        }

      )
    form.value.anhDaiDien = imageFile.value.name

    console.log("Tên ảnh trả về:", res.data)

    form.value.preview = ""
    alert(
      "Upload ảnh thành công"
    )

  }catch(e){


    console.log(e)


    alert(
      "Upload ảnh thất bại"
    )


  }


}

// ================= RESET FORM =================


const resetForm = ()=>{


  form.value = {


    id:null,


    idThuongHieu:null,

    idXuatXu:null,

    idChatLieu:null,

    idCoGiay:null,

    idDeGiay:null,

    idDanhMuc:null,


    maSanPham:"",

    tenSanPham:"",


    gioiTinh:"Unisex",


    moTaNgan:"",

    moTaChiTiet:"",


    anhDaiDien:"",


    trangThai:true,


    nguoiTao:"ADMIN",

    nguoiCapNhat:"ADMIN"


  }

  imageFile.value=null


}
// ================= CREATE =================


const openCreate=()=>{


  isEdit.value=false


  resetForm()


  showModal.value=true


}
// ================= EDIT =================


const editProduct=(item)=>{


  isEdit.value=true


  form.value={

    ...item

  }


  showModal.value=true


}
// ================= SAVE =================


const saveProduct=async()=>{


  if(

    !form.value.maSanPham

    ||

    !form.value.tenSanPham

  ){


    alert(
      "Vui lòng nhập mã và tên sản phẩm"
    )


    return

  }

  try{


    if(isEdit.value){



      await update(

        form.value.id,

        form.value

      )



    }else{



      await create(

        form.value

      )



    }

    showModal.value=false


    await loadData()



  }catch(e){


    console.log(e)


    alert(
      "Lỗi lưu sản phẩm"
    )


  }


}
// ================= DELETE =================


const deleteProduct=async(id)=>{


  if(!confirm(
    "Bạn chắc chắn muốn xóa?"
  ))

    return

  try{


    await remove(id)


    await loadData()



  }catch(e){


    console.log(e)


  }


}

// ================= CLOSE =================


const closeModal=()=>{


  showModal.value=false


}

</script>
<template>
  <div class="container">

    <!-- HEADER -->
    <div class="header">
      <h2>👟 QUẢN LÝ SẢN PHẨM</h2>

      <div class="toolbar">
        <input
          v-model="search"
          class="search"
          placeholder="🔍 Tìm mã hoặc tên sản phẩm..."
        >

        <button class="btn-add" @click="openCreate">
          + Thêm sản phẩm
        </button>
      </div>
    </div>


    <!-- LOADING -->
    <div v-if="loading" class="loading">
      Đang tải dữ liệu...
    </div>


    <!-- GRID -->
    <div class="grid">

      <div
        v-for="item in filteredProducts"
        :key="item.id"
        class="card"
        @click="goToDetail(item.id)"
      >

        <div class="image-box">

          <img
            :src="`/images/${item.anhDaiDien}`"
            :alt="item.tenSanPham"
            class="product-image"
            @error="$event.target.src='/images/no-image.png'"
          />

          <span
            class="status"
            :class="item.trangThai?'active':'inactive'"
          >
          {{item.trangThai?'Đang bán':'Ngừng bán'}}
        </span>

        </div>


        <div class="content">

          <h3>{{item.tenSanPham}}</h3>

          <p>
            <b>Mã:</b> {{item.maSanPham}}
          </p>

          <p>
            <b>Giới tính:</b> {{item.gioiTinh}}
          </p>

          <p class="desc">
            {{item.moTaNgan}}
          </p>


          <div class="actions">

            <button
              class="edit"
              @click.stop="editProduct(item)"
            >
              ✏ Sửa
            </button>

            <button
              class="delete"
              @click.stop="deleteProduct(item.id)"
            >
              🗑 Xóa
            </button>

          </div>

        </div>

      </div>

    </div>




    <!-- MODAL -->
    <div v-if="showModal" class="modal">

      <div class="modal-box">

        <h2>
          {{isEdit?'Cập nhật sản phẩm':'Thêm sản phẩm'}}
        </h2>


        <!-- PREVIEW IMAGE -->
        <img
          :src="previewImage"
          class="preview"
        >


        <!-- UPLOAD -->
        <div class="upload-area">

          <input
            type="file"
            accept="image/*"
            @change="handleImageUpload"
          >

          <button
            type="button"
            class="upload"
            @click="uploadImage"
          >
            📷 Upload ảnh
          </button>

        </div>



        <!-- THÔNG TIN -->
        <input
          v-model="form.maSanPham"
          placeholder="Mã sản phẩm"
        >

        <input
          v-model="form.tenSanPham"
          placeholder="Tên sản phẩm"
        >



        <!-- THUONG HIEU -->
        <select v-model="form.idThuongHieu">

          <option disabled value="">
            -- Chọn thương hiệu --
          </option>

          <option
            v-for="item in thuongHieuList"
            :key="item.id"
            :value="item.id"
          >
            {{item.ten}}
          </option>

        </select>




        <!-- XUAT XU -->
        <select v-model="form.idXuatXu">

          <option disabled value="">
            -- Chọn xuất xứ --
          </option>

          <option
            v-for="item in xuatXuList"
            :key="item.id"
            :value="item.id"
          >
            {{item.ten}}
          </option>

        </select>




        <!-- CHAT LIEU -->
        <select v-model="form.idChatLieu">

          <option disabled value="">
            -- Chọn chất liệu --
          </option>

          <option
            v-for="item in chatLieuList"
            :key="item.id"
            :value="item.id"
          >
            {{item.ten}}
          </option>

        </select>




        <!-- CO GIAY -->
        <select v-model="form.idCoGiay">

          <option disabled value="">
            -- Chọn cổ giày --
          </option>

          <option
            v-for="item in coGiayList"
            :key="item.id"
            :value="item.id"
          >
            {{item.ten}}
          </option>

        </select>





        <!-- DE GIAY -->
        <select v-model="form.idDeGiay">

          <option disabled value="">
            -- Chọn đế giày --
          </option>

          <option
            v-for="item in deGiayList"
            :key="item.id"
            :value="item.id"
          >
            {{item.ten}}
          </option>

        </select>





        <!-- DANH MUC -->
        <select v-model="form.idDanhMuc">

          <option disabled value="">
            -- Chọn danh mục --
          </option>

          <option
            v-for="item in danhMucList"
            :key="item.id"
            :value="item.id"
          >
            {{item.ten}}
          </option>

        </select>





        <!-- GIOI TINH -->
        <select v-model="form.gioiTinh">

          <option value="Nam">
            Nam
          </option>

          <option value="Nữ">
            Nữ
          </option>

          <option value="Unisex">
            Unisex
          </option>

        </select>




        <textarea
          v-model="form.moTaNgan"
          placeholder="Mô tả ngắn"
        />


        <textarea
          v-model="form.moTaChiTiet"
          placeholder="Mô tả chi tiết"
        />



        <select v-model="form.trangThai">

          <option :value="true">
            Đang bán
          </option>

          <option :value="false">
            Ngừng bán
          </option>

        </select>





        <!-- BUTTON -->
        <div class="modal-btn">

          <button
            class="save"
            @click="saveProduct"
          >
            💾 Lưu
          </button>
          <button
            class="cancel"
            @click="closeModal"
          >
            ❌ Hủy
          </button>
        </div>
      </div>

    </div>

  </div>
</template>
<style scoped>

.container{
  padding:30px;
  background:#f8fafc;
  min-height:100vh;
}

/* ================= HEADER ================= */

.header{
  display:flex;
  justify-content:space-between;
  align-items:center;

  margin-bottom:30px;

  background:white;

  padding:20px 25px;

  border-radius:18px;

  box-shadow:
    0 4px 20px rgba(0,0,0,.05);
}

.header h2{
  margin:0;
  font-size:28px;
  font-weight:700;
  color:#0f172a;
}

.toolbar{
  display:flex;
  gap:12px;
  align-items:center;
}

.search{
  width:350px;

  padding:14px 18px;

  border:1px solid #e2e8f0;

  border-radius:12px;

  background:white;

  transition:.3s;
}

.search:focus{
  outline:none;

  border-color:#2563eb;

  box-shadow:
    0 0 0 4px rgba(37,99,235,.1);
}

.btn-add{
  border:none;

  background:linear-gradient(
    135deg,
    #2563eb,
    #3b82f6
  );

  color:white;

  padding:14px 22px;

  border-radius:12px;

  font-weight:600;

  cursor:pointer;

  transition:.3s;
}

.btn-add:hover{
  transform:translateY(-2px);

  box-shadow:
    0 10px 20px rgba(37,99,235,.25);
}

/* ================= LOADING ================= */

.loading{
  text-align:center;
  padding:30px;
  font-size:18px;
  color:#64748b;
}

/* ================= GRID ================= */

.grid{
  display:grid;

  grid-template-columns:
    repeat(auto-fill,minmax(300px,1fr));

  gap:25px;
}

/* ================= CARD ================= */

.card{
  background:white;

  border-radius:20px;

  overflow:hidden;

  cursor:pointer;

  transition:.35s;

  box-shadow:
    0 8px 25px rgba(0,0,0,.08);
}

.card:hover{
  transform:translateY(-8px);

  box-shadow:
    0 15px 35px rgba(37,99,235,.15);
}

/* ================= IMAGE ================= */

.image-box{
  position:relative;
  overflow:hidden;
}

.product-image{
  width:100%;
  height:260px;
  object-fit:cover;

  transition:.4s;
}

.card:hover .product-image{
  transform:scale(1.08);
}

/* ================= STATUS ================= */

.status{
  position:absolute;

  top:12px;
  right:12px;

  padding:7px 14px;

  border-radius:30px;

  font-size:12px;
  font-weight:700;
}

.active{
  background:#dcfce7;
  color:#15803d;
}

.inactive{
  background:#fee2e2;
  color:#dc2626;
}

/* ================= CONTENT ================= */

.content{
  padding:20px;
}

.content h3{
  margin-bottom:12px;

  font-size:20px;
  font-weight:700;

  color:#0f172a;
}

.content p{
  margin:8px 0;
  color:#475569;
}

.desc{
  min-height:50px;

  overflow:hidden;

  color:#64748b;
}

/* ================= ACTION ================= */

.actions{
  display:flex;
  gap:10px;
  margin-top:18px;
}

.actions button{
  flex:1;

  border:none;

  padding:12px;

  border-radius:10px;

  color:white;

  font-weight:600;

  cursor:pointer;

  transition:.3s;
}

.edit{
  background:#2563eb;
}

.edit:hover{
  background:#1d4ed8;
}

.delete{
  background:#ef4444;
}

.delete:hover{
  background:#dc2626;
}

/* ================= MODAL ================= */

.modal{
  position:fixed;
  inset:0;

  background:rgba(15,23,42,.55);

  display:flex;
  justify-content:center;
  align-items:center;

  z-index:999;
}

.modal-box{
  width:700px;

  max-height:90vh;

  overflow-y:auto;

  background:white;

  padding:30px;

  border-radius:20px;

  display:flex;
  flex-direction:column;
  gap:14px;

  box-shadow:
    0 20px 40px rgba(0,0,0,.25);
}

.modal-box h2{
  text-align:center;
  margin-bottom:10px;
  color:#0f172a;
}

/* ================= INPUT ================= */

.modal-box input,
.modal-box select,
.modal-box textarea{

  width:100%;

  padding:14px;

  border:1px solid #dbe2ea;

  border-radius:12px;

  font-size:15px;

  transition:.3s;
}

.modal-box input:focus,
.modal-box select:focus,
.modal-box textarea:focus{

  outline:none;

  border-color:#2563eb;

  box-shadow:
    0 0 0 4px rgba(37,99,235,.1);
}

.modal-box textarea{
  resize:none;
  height:100px;
}

/* ================= PREVIEW ================= */

.preview{
  width:220px;
  height:220px;

  object-fit:cover;

  margin:auto;

  border-radius:16px;

  border:3px solid #e2e8f0;
}

/* ================= UPLOAD ================= */

.upload-area{
  display:flex;
  gap:10px;
  align-items:center;
}

.upload{
  border:none;

  background:#2563eb;

  color:white;

  padding:12px 18px;

  border-radius:10px;

  cursor:pointer;
}

.upload:hover{
  background:#1d4ed8;
}

/* ================= MODAL BUTTON ================= */

.modal-btn{
  display:flex;
  gap:12px;
  margin-top:10px;
}

.modal-btn button{
  flex:1;

  border:none;

  padding:14px;

  border-radius:12px;

  color:white;

  font-weight:700;

  cursor:pointer;
}

.save{
  background:#16a34a;
}

.save:hover{
  background:#15803d;
}

.cancel{
  background:#64748b;
}

.cancel:hover{
  background:#475569;
}

/* ================= RESPONSIVE ================= */

@media(max-width:768px){

  .header{
    flex-direction:column;
    gap:15px;
  }

  .toolbar{
    width:100%;
    flex-direction:column;
  }

  .search{
    width:100%;
  }

  .btn-add{
    width:100%;
  }

  .modal-box{
    width:95%;
  }

}

</style>

<script setup>

import {
  ref,
  computed,
  onMounted
} from "vue"

import axios from "axios"


const API =
  "http://localhost:8080/api/thuong-hieu"


const UPLOAD_API =
  "http://localhost:8080/api/upload"



// ================= DATA =================


const list = ref([])


const page = ref(1)


const pageSize = 10




// ================= IMAGE =================


const imageFile = ref(null)


const imagePreview = ref("")





// ================= FORM =================


const form = ref({

  id:null,

  ma:"",

  ten:"",

  logo:"",

  website:"",

  moTa:"",

  quocGia:"",

  trangThai:true,

  ngayTao:null,

  ngayCapNhat:null

})





const errors = ref({

  ma:"",

  ten:""

})




const toast = ref("")


const toastType = ref("success")




// ================= TOAST =================


const showToast = (
  msg,
  type="success"
)=>{


  toast.value = msg


  toastType.value = type



  setTimeout(()=>{


    toast.value=""


  },2500)



}





// ================= LOAD =================


const load = async()=>{


  try{


    const res =
      await axios.get(API)


    list.value =
      res.data



  }catch(error){


    console.log(error)


    showToast(
      "Không tải được dữ liệu",
      "error"
    )


  }


}





// ================= LOGO =================


const getLogo=(logo)=>{


  if(!logo){

    return "/images/no-image.png"

  }


  return `/images/thuong-hieu/${logo}`


}







// ================= CHỌN ẢNH =================


const handleImageUpload=(event)=>{


  const file =
    event.target.files[0]



  if(file){


    imageFile.value=file



    imagePreview.value =
      URL.createObjectURL(file)



    form.value.logo =
      file.name


  }


}






// ================= UPLOAD ẢNH =================


const uploadImage = async()=>{


  if(!imageFile.value){


    alert(
      "Chưa chọn ảnh"
    )


    return

  }



  try{


    const formData =
      new FormData()



    formData.append(
      "file",
      imageFile.value
    )




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




    form.value.logo =
      imageFile.value.name




    alert(
      "Upload ảnh thành công"
    )



  }catch(error){


    console.log(error)



    alert(
      "Upload ảnh thất bại"
    )


  }



}








// ================= VALIDATE =================


const validate=()=>{


  errors.value={

    ma:"",

    ten:""

  }



  let ok=true




  if(!form.value.ma.trim()){


    errors.value.ma =
      "Mã thương hiệu không được để trống"


    ok=false

  }





  if(!form.value.ten.trim()){


    errors.value.ten =
      "Tên thương hiệu không được để trống"


    ok=false

  }




  return ok


}








// ================= SAVE =================


const save=async()=>{


  if(!validate()){


    showToast(
      "Vui lòng kiểm tra dữ liệu",
      "error"
    )


    return

  }




  try{


    const payload={


      ma:
      form.value.ma,


      ten:
      form.value.ten,


      logo:
      form.value.logo,


      website:
      form.value.website,


      moTa:
      form.value.moTa,


      quocGia:
      form.value.quocGia,


      trangThai:
      form.value.trangThai


    }






    if(form.value.id){


      await axios.put(

        `${API}/${form.value.id}`,

        payload

      )



      showToast(
        "Cập nhật thành công"
      )



    }else{


      await axios.post(

        API,

        payload

      )



      showToast(
        "Thêm mới thành công"
      )


    }





    reset()


    load()



  }catch(error){


    console.log(error)



    showToast(
      "Có lỗi xảy ra",
      "error"
    )


  }


}







// ================= DELETE =================


const del=async(id)=>{


  if(!confirm(
    "Bạn có chắc muốn xóa?"
  ))

    return




  try{


    await axios.delete(

      `${API}/${id}`

    )



    showToast(
      "Xóa thành công"
    )



    load()



  }catch(error){


    showToast(
      "Xóa thất bại",
      "error"
    )


  }


}









// ================= EDIT =================


const edit=(item)=>{


  form.value={

    ...item

  }




  imagePreview.value =
    getLogo(item.logo)



  window.scrollTo({

    top:0,

    behavior:"smooth"

  })


}









// ================= RESET =================


const reset=()=>{


  form.value={


    id:null,

    ma:"",

    ten:"",

    logo:"",

    website:"",

    moTa:"",

    quocGia:"",

    trangThai:true,

    ngayTao:null,

    ngayCapNhat:null


  }



  imageFile.value=null


  imagePreview.value=""



}









// ================= PAGINATION =================


const totalPages = computed(()=>{


  return Math.ceil(

    list.value.length / pageSize

  )


})





const paginatedList = computed(()=>{


  const start =

    (page.value-1)*pageSize




  return list.value.slice(

    start,

    start+pageSize

  )


})





const changePage=(p)=>{


  page.value=p


}






onMounted(load)



</script>
<template>

  <div class="container">


    <h2>
      🏷️ QUẢN LÝ THƯƠNG HIỆU
    </h2>




    <!-- TOAST -->

    <div
      v-if="toast"
      :class="['toast',toastType]"
    >

      {{toast}}

    </div>





    <!-- ================= FORM ================= -->


    <div class="card">


      <div class="grid">



        <!-- MÃ -->

        <div>

          <label>
            Mã thương hiệu *
          </label>


          <input
            v-model="form.ma"
          >


          <small class="error">
            {{errors.ma}}
          </small>


        </div>






        <!-- TÊN -->


        <div>


          <label>
            Tên thương hiệu *
          </label>


          <input
            v-model="form.ten"
          >


          <small class="error">
            {{errors.ten}}
          </small>


        </div>






        <!-- ================= LOGO MỚI ================= -->


        <div class="logo-upload">


          <label>
            Logo thương hiệu
          </label>



          <div class="logo-area">



            <!-- PREVIEW -->


            <div class="logo-preview-box">


              <img

                v-if="imagePreview"

                :src="imagePreview"

                class="preview-logo"

              />



              <img

                v-else-if="form.logo"

                :src="getLogo(form.logo)"

                class="preview-logo"

              />



              <span v-else>

              Chưa có ảnh

            </span>


            </div>





            <!-- BUTTON -->


            <div class="upload-control">



              <label class="choose-image">


                📷 Chọn ảnh


                <input

                  type="file"

                  accept="image/*"

                  @change="handleImageUpload"

                >


              </label>






              <button

                type="button"

                class="upload-btn"

                @click="uploadImage"

              >

                ⬆ Upload ảnh

              </button>




              <small>

                JPG, PNG, WEBP

              </small>


            </div>




          </div>



        </div>







        <!-- WEBSITE -->


        <div>


          <label>
            Website
          </label>


          <input
            v-model="form.website"
          >


        </div>








        <!-- QUỐC GIA -->


        <div>


          <label>
            Quốc gia
          </label>


          <input
            v-model="form.quocGia"
          >


        </div>




      </div>








      <!-- MÔ TẢ -->


      <div class="mt">


        <label>
          Mô tả
        </label>


        <textarea

          rows="4"

          v-model="form.moTa"

        ></textarea>


      </div>







      <!-- TRẠNG THÁI -->


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









      <!-- BUTTON -->


      <div class="actions">


        <button

          class="btn-save"

          @click="save"

        >


          {{form.id
          ?"Cập nhật"
          :"Thêm mới"}}


        </button>





        <button

          class="btn-reset"

          @click="reset"

        >

          Reset

        </button>



      </div>





    </div>













    <!-- ================= TABLE ================= -->


    <div class="card">



      <table>



        <thead>


        <tr>


          <th>
            Logo
          </th>


          <th>
            Mã
          </th>


          <th>
            Tên
          </th>


          <th>
            Quốc gia
          </th>


          <th>
            Website
          </th>


          <th>
            Trạng thái
          </th>


          <th>
            Action
          </th>



        </tr>


        </thead>








        <tbody>



        <tr

          v-for="item in paginatedList"

          :key="item.id"

        >




          <td>


            <img

              :src="getLogo(item.logo)"

              class="logo"

            >


          </td>





          <td>

            {{item.ma}}

          </td>





          <td>

            {{item.ten}}

          </td>





          <td>

            {{item.quocGia}}

          </td>





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
            ?'active-status'
            :'inactive-status'
            "

          >


            {{

              item.trangThai
                ?
                'Hoạt động'
                :
                'Ngừng hoạt động'

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








      <!-- PAGINATION -->


      <div class="pagination">



        <button

          @click="page--"

          :disabled="page===1"

        >

          ◀

        </button>






        <button

          v-for="p in totalPages"

          :key="p"

          @click="changePage(p)"

          :class="{activePage:page===p}"

        >

          {{p}}


        </button>






        <button

          @click="page++"

          :disabled="page===totalPages"

        >

          ▶

        </button>



      </div>





    </div>





  </div>


</template>
<style scoped>


/* ================= CONTAINER ================= */


.container{

  padding:30px;

  background:#f8fafc;

  min-height:100vh;

}





h2{

  color:#0f172a;

  font-size:28px;

  font-weight:700;

  margin-bottom:25px;

}







/* ================= CARD ================= */


.card{

  background:white;

  padding:25px;

  border-radius:18px;

  margin-bottom:25px;

  box-shadow:

    0 8px 25px rgba(0,0,0,.08);

}







/* ================= FORM GRID ================= */


.grid{

  display:grid;

  grid-template-columns:repeat(2,1fr);

  gap:20px;

}



label{

  display:block;

  margin-bottom:8px;

  font-weight:600;

  color:#334155;

}







input,
textarea{


  width:100%;


  padding:13px 15px;


  border:1px solid #dbe2ea;


  border-radius:12px;


  font-size:15px;


  transition:.3s;


}




input:focus,
textarea:focus{


  outline:none;


  border-color:#2563eb;


  box-shadow:

    0 0 0 4px rgba(37,99,235,.1);


}









/* ================= LOGO UPLOAD ================= */



.logo-upload{


  background:#f8fafc;


  padding:18px;


  border-radius:15px;


  border:1px solid #e2e8f0;


}





.logo-area{


  display:flex;


  align-items:center;


  gap:25px;


}





.logo-preview-box{


  width:130px;


  height:130px;


  border-radius:18px;


  border:2px dashed #cbd5e1;


  background:white;


  display:flex;


  justify-content:center;


  align-items:center;


  overflow:hidden;


}





.preview-logo{


  width:100%;


  height:100%;


  object-fit:contain;


  padding:10px;


}





.logo-preview-box span{


  color:#94a3b8;


  font-size:14px;


}







.upload-control{


  display:flex;


  flex-direction:column;


  gap:12px;


}





.choose-image{


  background:#2563eb;


  color:white;


  padding:12px 20px;


  border-radius:10px;


  cursor:pointer;


  text-align:center;


  transition:.3s;


}




.choose-image:hover{


  background:#1d4ed8;


}





.choose-image input{


  display:none;


}







.upload-btn{


  background:#16a34a;


  color:white;


  border:none;


  padding:12px 20px;


  border-radius:10px;


  cursor:pointer;


  font-weight:600;


}



.upload-btn:hover{


  background:#15803d;


}





.upload-control small{


  color:#64748b;


}









/* ================= TEXTAREA ================= */


textarea{


  resize:none;


}







.mt{


  margin-top:20px;


}








/* ================= STATUS ================= */


.status-box{


  display:flex;


  gap:30px;


  margin-top:20px;


}





.status-box label{


  display:flex;


  align-items:center;


  gap:8px;


  cursor:pointer;


}







/* ================= BUTTON ================= */



.actions{


  margin-top:25px;


  display:flex;


  gap:15px;


}





.btn-save,
.btn-reset{


  border:none;


  padding:13px 25px;


  border-radius:12px;


  color:white;


  font-weight:600;


  cursor:pointer;


  transition:.3s;


}






.btn-save{


  background:#16a34a;


}



.btn-save:hover{


  background:#15803d;


  transform:translateY(-2px);


}







.btn-reset{


  background:#64748b;


}



.btn-reset:hover{


  background:#475569;


}







/* ================= TABLE ================= */



table{


  width:100%;


  border-collapse:collapse;


}





th{


  background:#f1f5f9;


  color:#334155;


  padding:15px;


}





td{


  padding:15px;


  text-align:center;


  border-bottom:1px solid #e2e8f0;


}





tr:hover{


  background:#f8fafc;


}







.logo{


  width:65px;


  height:65px;


  object-fit:contain;


  padding:5px;


  background:white;


  border-radius:12px;


  border:1px solid #e2e8f0;


}







/* ================= STATUS ================= */


.active-status{


  background:#dcfce7;


  color:#15803d;


  padding:6px 12px;


  border-radius:20px;


  font-weight:600;


}





.inactive-status{


  background:#fee2e2;


  color:#dc2626;


  padding:6px 12px;


  border-radius:20px;


  font-weight:600;


}








/* ================= ACTION TABLE ================= */



.btn-edit,
.btn-delete{


  border:none;


  padding:8px 12px;


  border-radius:8px;


  cursor:pointer;


  margin:0 3px;


}



.btn-edit{


  background:#facc15;


}



.btn-delete{


  background:#ef4444;


  color:white;


}







/* ================= TOAST ================= */



.toast{


  position:fixed;


  top:20px;


  right:20px;


  padding:15px 25px;


  color:white;


  border-radius:12px;


  z-index:999;


  font-weight:600;


}




.toast.success{


  background:#16a34a;


}



.toast.error{


  background:#dc2626;


}







.error{


  color:#dc2626;


  font-size:13px;


}







/* ================= PAGINATION ================= */



.pagination{


  margin-top:25px;


  display:flex;


  justify-content:center;


  gap:8px;


}



.pagination button{


  border:none;


  padding:8px 14px;


  border-radius:8px;


  cursor:pointer;


  background:#e2e8f0;


}





.pagination button:hover{


  background:#cbd5e1;


}





.activePage{


  background:#2563eb !important;


  color:white;


}








/* ================= RESPONSIVE ================= */


@media(max-width:768px){


  .grid{


    grid-template-columns:1fr;


  }



  .logo-area{


    flex-direction:column;


    align-items:flex-start;


  }



  table{


    font-size:13px;


  }



}


</style>

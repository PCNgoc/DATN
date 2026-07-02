<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { getNewestProducts } from "@/services/productService"

const router = useRouter()

const products = ref([])


const productColors = [

  "#e8f1ff",
  "#ffe8e8",
  "#e7f8ed",
  "#eeeeee",
  "#f7eadc",
  "#eee8ff",
  "#e5f1d8",
  "#fff2cc"

]



onMounted(async()=>{

  try{

    const res = await getNewestProducts()


    products.value =

      res.data

        .slice(0,8)

        .map((item,index)=>({

          ...item,

          color:productColors[index]

        }))


  }

  catch(error){

    console.error(
      "Lỗi tải sản phẩm:",
      error
    )

  }

})



const goDetail=(id)=>{

  router.push(`/products/${id}`)

}

</script>




<template>


  <section class="new-products">



    <div class="section-header">


      <div>


<span class="sub-title">

GX SNEAKER

</span>



        <h2 class="section-title">

          Sản phẩm mới nhất

        </h2>



        <p class="desc">

          Những mẫu giày mới cập nhật

        </p>



      </div>


    </div>





    <div class="product-grid">



      <div

        v-for="sp in products"

        :key="sp.id"

        class="product-card"

        :style="{

background:sp.color

}"

        @click="goDetail(sp.id)"

      >



        <div class="image-wrapper">


<span class="new-badge">

NEW

</span>



          <img

            :src="`/images/${sp.anhDaiDien}`"

            :alt="sp.tenSanPham"

          />



        </div>




        <div class="info">


<span class="brand">

{{sp.tenThuongHieu}}

</span>



          <h3 class="product-name">

            {{sp.tenSanPham}}

          </h3>




          <div class="product-footer">

            <div class="btn-detail">

              Xem chi tiết →

            </div>

          </div>



        </div>



      </div>



    </div>



  </section>


</template>





<style scoped>


.new-products{


  padding:80px 8%;


  background:transparent;


}





/* HEADER */


.section-header{


  margin-bottom:45px;


}



.sub-title{


  display:block;


  font-size:15px;


  font-weight:900;


  letter-spacing:4px;


  color:#777;


  margin-bottom:12px;


}



.section-title{


  font-size:45px;


  font-weight:900;


  color:#111;


  margin:0;


}



.section-title::after{


  content:"";


  display:block;


  width:90px;


  height:5px;


  background:#111;


  margin-top:18px;


  border-radius:20px;


}



.desc{


  margin-top:15px;


  color:#666;


}






/* GRID */


.product-grid{


  display:grid;


  grid-template-columns:


repeat(4,1fr);


  gap:28px;


}







/* CARD */


.product-card{


  border-radius:25px;


  overflow:hidden;


  cursor:pointer;


  transition:.4s;


  box-shadow:


    0 15px 35px rgba(0,0,0,.12);


}



.product-card:hover{


  transform:


    translateY(-12px);


  box-shadow:


    0 25px 55px rgba(0,0,0,.22);


}





/* IMAGE FULL CARD */


.image-wrapper{


  height:330px;


  position:relative;


  overflow:hidden;


}



.image-wrapper img{


  width:100%;


  height:100%;


  object-fit:cover;


  transition:.5s;


}





.product-card:hover img{


  transform:


    scale(1.08);


}






/* BADGE */


.new-badge{


  position:absolute;


  top:18px;


  left:18px;


  z-index:2;


  background:#111;


  color:white;


  padding:7px 15px;


  border-radius:30px;


  font-size:11px;


  font-weight:900;


  letter-spacing:1px;


}





/* INFO */


.info{


  padding:22px;


}



.brand{


  font-size:11px;


  font-weight:800;


  letter-spacing:3px;


  color:#777;


  text-transform:uppercase;


}



.product-name{


  margin:12px 0;


  font-size:19px;


  font-weight:900;


  color:#111;


  line-height:1.4;


  min-height:55px;


  display:-webkit-box;


  -webkit-line-clamp:2;


  -webkit-box-orient:vertical;


  overflow:hidden;


}





.product-footer{


  margin-top:18px;


}



.btn-detail{


  font-size:14px;


  font-weight:800;


  color:#111;


  transition:.3s;


}



.product-card:hover .btn-detail{


  transform:translateX(8px);


}






@media(max-width:1200px){


  .product-grid{


    grid-template-columns:


repeat(3,1fr);


  }


}





@media(max-width:900px){


  .product-grid{


    grid-template-columns:


repeat(2,1fr);


  }


}





@media(max-width:600px){


  .product-grid{


    grid-template-columns:


1fr;


  }



  .section-title{


    font-size:32px;


  }


}



</style>

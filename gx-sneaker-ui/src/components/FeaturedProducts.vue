<script setup>

import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { getAll } from "@/services/sanPhamService"


const router = useRouter()


const products = ref([])
const loading = ref(true)



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



// lấy sản phẩm nổi bật

const loadProducts = async()=>{

  try{


    const res = await getAll()


    products.value =

      res.data

        .filter(item=>item.trangThai === true)

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

  finally{

    loading.value=false

  }


}



// chuyển trang chi tiết

const goDetail=(id)=>{

  router.push(`/products/${id}`)

}



onMounted(loadProducts)


</script>





<template>


  <section class="featured">



    <div class="section-header">


      <div>


<span class="sub-title">

GX SNEAKER

</span>



        <h2 class="section-title">

          Sản phẩm nổi bật

        </h2>



        <p class="desc">

          Khám phá những mẫu giày mới nhất

        </p>



      </div>





      <router-link

        to="/products"

        class="view-all"

      >

        Xem tất cả →

      </router-link>



    </div>





    <div

      v-if="loading"

      class="loading"

    >

      Đang tải sản phẩm...

    </div>





    <div

      v-else

      class="product-grid"

    >





      <div

        v-for="sp in products"

        :key="sp.id"

        class="product-card"


        :style="{

background:

`linear-gradient(

180deg,

${sp.color},

#ffffff

)`

}"


        @click="goDetail(sp.id)"

      >





        <div class="image-wrapper">



<span class="product-badge">

HOT

</span>



          <img

            :src="`/images/${sp.anhDaiDien}`"

            :alt="sp.tenSanPham"

          />



        </div>
        <div class="product-info">


          <span class="brand">

            {{sp.tenThuongHieu}}

          </span>




          <h3 class="product-name">

            {{sp.tenSanPham}}

          </h3>




          <div class="category-tag">

            {{sp.tenDanhMuc}}

          </div>




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


.featured{

  padding:80px 8%;

  background:transparent;

}





/* HEADER */

.section-header{

  display:flex;

  justify-content:space-between;

  align-items:end;

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

  margin:0;

  color:#111;

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







.view-all{

  text-decoration:none;

  font-size:15px;

  font-weight:800;

  color:#111;

  transition:.3s;

}



.view-all:hover{

  transform:translateX(8px);

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

    0 15px 35px rgba(0,0,0,.1);

}



.product-card:hover{

  transform:

    translateY(-12px);


  box-shadow:

    0 25px 55px rgba(0,0,0,.2);

}







/* IMAGE FULL KHUNG */

.image-wrapper{


  height:300px;

  width:100%;


  position:relative;

  overflow:hidden;


  display:flex;

  align-items:center;

  justify-content:center;


}





.image-wrapper img{


  width:100%;

  height:100%;


  object-fit:cover;


  padding:0;


  transition:.5s;


  filter:

    drop-shadow(

      0 20px 25px rgba(0,0,0,.18)

    );

}





.product-card:hover img{

  transform:scale(1.08);

}








.product-badge{


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


}







/* INFO */

.product-info{


  padding:22px;


}





.brand{


  font-size:11px;


  letter-spacing:3px;


  font-weight:800;


  color:#777;


}





.product-name{


  margin:12px 0;


  font-size:19px;


  font-weight:900;


  line-height:1.4;


  color:#111;


  min-height:55px;



  display:-webkit-box;


  -webkit-line-clamp:2;


  -webkit-box-orient:vertical;


  overflow:hidden;


}







.category-tag{


  display:inline-block;


  padding:7px 15px;


  border-radius:30px;



  background:

    rgba(255,255,255,.6);



  font-size:12px;


  font-weight:700;


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







.loading{


  text-align:center;


  padding:60px;


}







/* RESPONSIVE */


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

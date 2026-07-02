<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { getAll } from "@/services/danhMucService"


const router = useRouter()

const categories = ref([])



const categoryImages = {

  "Giày Chạy Bộ":
    "/images/categories/running.jpg",

  "Giày Bóng Rổ":
    "/images/categories/basketball.jpg",

  "Giày Đá Bóng":
    "/images/categories/football.jpg",

  "Giày Sneaker":
    "/images/categories/sneaker.jpg",

  "Giày Casual":
    "/images/categories/casual.jpg",

  "Giày Training":
    "/images/categories/training.jpg",

  "Giày Outdoor":
    "/images/categories/outdoor.jpg",

  "Giày Tennis":
    "/images/categories/tennis.jpg",

}



const categoryColors = {


  "Giày Chạy Bộ":
    "#dcecff",

  "Giày Bóng Rổ":
    "#ffe2df",

  "Giày Đá Bóng":
    "#dcf5e5",

  "Giày Sneaker":
    "#eeeeee",

  "Giày Casual":
    "#f5e5d5",

  "Giày Training":
    "#ebe2ff",

  "Giày Outdoor":
    "#e5f1d8",

  "Giày Tennis":
    "#fff1cc",

}



const categoryIcons = {


  "Giày Chạy Bộ":"🏃",

  "Giày Bóng Rổ":"🏀",

  "Giày Đá Bóng":"⚽",

  "Giày Sneaker":"👟",

  "Giày Casual":"🔥",

  "Giày Training":"🏋️",

  "Giày Outdoor":"🥾",

  "Giày Tennis":"🎾",

}



onMounted(async()=>{


  try{


    const res = await getAll()



    const list = [

      "Giày Chạy Bộ",

      "Giày Bóng Rổ",

      "Giày Đá Bóng",

      "Giày Sneaker",

      "Giày Casual",

      "Giày Training",

      "Giày Outdoor",

      "Giày Tennis"

    ]



    categories.value =

      res.data

        .filter(item =>

          list.includes(item.ten)

        )

        .map(item=>({

          ...item,


          image:

            categoryImages[item.ten]

            || "/images/categories/default.png",


          color:

            categoryColors[item.ten]

            || "#eee",


          icon:

            categoryIcons[item.ten]

            || "👟",


          total:

            Math.floor(Math.random()*120)+30


        }))



  }


  catch(error){

    console.log(error)

  }


})




const goToProducts=(item)=>{


  router.push({

    path:"/products",

    query:{

      category:item.id

    }

  })


}


</script>





<template>


  <section class="category-section">



    <div class="section-header">


<span>

GX SNEAKER

</span>



      <h2>

        Danh mục nổi bật

      </h2>


      <p>

        Khám phá bộ sưu tập giày mới nhất

      </p>


    </div>






    <div class="category-grid">



      <div

        v-for="item in categories"

        :key="item.id"

        class="category-card"


        :style="{

background:item.color

}"


        @click="goToProducts(item)"


      >



        <div class="category-badge">

          {{item.icon}}

        </div>




        <img

          :src="item.image"

          class="shoe-image"

        />




        <div class="card-overlay">


          <div>

            <h3>

              {{item.ten}}

            </h3>


            <p>

              {{item.total}}+ sản phẩm

            </p>


          </div>



          <div class="arrow">

            →

          </div>



        </div>



      </div>



    </div>


  </section>


</template>





<style scoped>


.category-section{


  padding:80px 8%;

  background:transparent;


}




.section-header{


  text-align:left;

  margin-bottom:45px;


}



.section-header span{


  font-size:15px;

  font-weight:900;

  letter-spacing:4px;

  color:#777;


}



.section-header h2{


  font-size:45px;

  font-weight:900;

  margin:12px 0;


  color:#111;


}



.section-header h2::after{


  content:"";

  display:block;

  width:90px;

  height:5px;

  background:#111;

  margin-top:18px;

  border-radius:20px;


}



.section-header p{


  color:#666;

  font-size:16px;


}






/* 4 CARD / 1 HÀNG */


.category-grid{


  display:grid;


  grid-template-columns:

repeat(4,1fr);


  gap:25px;


}




.category-card{


  height:390px;


  border-radius:25px;


  overflow:hidden;


  position:relative;


  cursor:pointer;


  box-shadow:

    0 15px 35px rgba(0,0,0,.12);


  transition:.4s;


}



.category-card:hover{


  transform:

    translateY(-10px);


  box-shadow:

    0 25px 50px rgba(0,0,0,.22);


}




.category-badge{


  position:absolute;


  top:18px;

  left:18px;


  z-index:2;


  width:45px;

  height:45px;


  background:white;


  border-radius:50%;


  display:flex;

  align-items:center;

  justify-content:center;


  font-size:22px;


}




.shoe-image{


  width:100%;


  height:100%;


  object-fit:cover;


  position:absolute;


  inset:0;


  transition:.5s;


}



.category-card:hover
.shoe-image{


  transform:

    scale(1.1);


}





.card-overlay{


  position:absolute;


  bottom:0;


  width:100%;


  padding:90px 20px 20px;


  display:flex;


  justify-content:space-between;


  align-items:end;


  color:white;


  background:

    linear-gradient(

      transparent,

      rgba(0,0,0,.75)

    );


}



.card-overlay h3{


  font-size:22px;

  font-weight:900;

  margin:0;


}



.card-overlay p{


  margin-top:5px;

  font-size:14px;


}




.arrow{


  width:40px;


  height:40px;


  background:white;


  color:#111;


  border-radius:50%;


  display:flex;


  align-items:center;


  justify-content:center;


  font-size:22px;


}



@media(max-width:1200px){


  .category-grid{

    grid-template-columns:

repeat(3,1fr);

  }


}



@media(max-width:900px){


  .category-grid{

    grid-template-columns:

repeat(2,1fr);

  }


}



@media(max-width:600px){


  .category-grid{

    grid-template-columns:1fr;

  }



  .section-header h2{

    font-size:32px;

  }


}



</style>

<script setup>
import { ref, onMounted } from "vue"
import { getAllThuongHieu } from "@/services/thuongHieuService"


const brands = ref([])



// link website chính thức của thương hiệu

const brandLinks = {

  "Nike":
    "https://www.nike.com/vn/",

  "Adidas":
    "https://www.adidas.com/",

  "Puma":
    "https://www.puma.com/",

  "New Balance":
    "https://www.newbalance.com/",

  "Converse":
    "https://www.converse.com/",

  "Vans":
    "https://www.vans.com/",

  "Jordan":
    "https://www.nike.com/jordan/",

  "MLB":
    "https://www.mlb.com/"

}





const getLogo=(logo)=>{


  if(!logo){

    return "/images/default-logo.png"

  }


  return `/images/thuong-hieu/${logo}`

}





const loadBrands=async()=>{


  try{


    const res = await getAllThuongHieu()


    brands.value =

      res.data.slice(0,8)



  }

  catch(error){

    console.error(
      "Lỗi tải thương hiệu:",
      error
    )

  }


}






const openBrand=(brand)=>{


  const link = brandLinks[brand.ten]



  if(link){


    window.open(

      link,

      "_blank"

    )


  }


}






onMounted(loadBrands)



</script>





<template>


  <section class="brand-section">



    <div class="section-header">


<span class="sub-title">

GX SNEAKER

</span>



      <h2 class="section-title">

        Thương hiệu nổi bật

      </h2>



      <p class="desc">

        Khám phá những thương hiệu giày hàng đầu thế giới

      </p>


    </div>






    <div class="brand-grid">



      <div

        v-for="item in brands"

        :key="item.id"

        class="brand-card"

        @click="openBrand(item)"

      >




        <img

          :src="getLogo(item.logo)"

          :alt="item.ten"

          class="brand-image"

        />





        <div class="overlay">


          <h3>

            {{item.ten}}

          </h3>



          <span>

Khám phá thương hiệu →

</span>


        </div>



      </div>



    </div>



  </section>


</template>





<style scoped>


.brand-section{


  padding:80px 8%;


  background:transparent;


}





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






.brand-grid{


  display:grid;


  grid-template-columns:

repeat(4,1fr);


  gap:28px;


}







.brand-card{


  height:320px;


  position:relative;


  overflow:hidden;


  border-radius:25px;


  cursor:pointer;


  box-shadow:

    0 15px 35px rgba(0,0,0,.12);


  transition:.4s;


}



.brand-card:hover{


  transform:

    translateY(-12px);


  box-shadow:

    0 25px 55px rgba(0,0,0,.25);


}





.brand-image{


  width:100%;


  height:100%;


  object-fit:cover;


  transition:.5s;


}





.brand-card:hover .brand-image{


  transform:

    scale(1.1);


}





.overlay{


  position:absolute;


  bottom:0;


  left:0;


  width:100%;


  padding:100px 25px 25px;


  background:

    linear-gradient(

      transparent,

      rgba(0,0,0,.75)

    );


  color:white;


}





.overlay h3{


  font-size:28px;


  font-weight:900;


  margin:0 0 8px;


}



.overlay span{


  font-size:14px;


  font-weight:700;


}





@media(max-width:1200px){


  .brand-grid{

    grid-template-columns:

repeat(3,1fr);

  }


}




@media(max-width:900px){


  .brand-grid{

    grid-template-columns:

repeat(2,1fr);

  }


}





@media(max-width:600px){


  .brand-grid{

    grid-template-columns:

1fr;

  }



  .section-title{

    font-size:32px;

  }


}


</style>

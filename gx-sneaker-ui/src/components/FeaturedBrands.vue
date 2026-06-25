<script setup>
import { ref, onMounted } from "vue"
import { getAllThuongHieu } from "@/services/thuongHieuService"


const brands = ref([])


// lấy đường dẫn logo
const getLogo = (logo) => {

  if(!logo){
    return "https://via.placeholder.com/100"
  }

  return `/images/thuong-hieu/${logo}`

}



onMounted(async()=>{

  const res = await getAllThuongHieu()

  brands.value = res.data.slice(0,8)

})

</script>


<template>

  <section class="brand-section">

    <div class="section-header">

      <div>

        <span class="sub-title">
          GX SNEAKER
        </span>

        <h2 class="section-title">
          Thương hiệu nổi bật
        </h2>

      </div>

    </div>

    <div class="brand-grid">

      <div
        v-for="item in brands"
        :key="item.id"
        class="brand-card"
      >

        <div class="logo-wrapper">

          <img
            :src="getLogo(item.logo)"
            :alt="item.ten"
          />

        </div>

        <h4>
          {{ item.ten }}
        </h4>

      </div>

    </div>

  </section>

</template>



<style scoped>


.brand-section{
  padding:70px 40px;
  background:#f8f9fb;
}

.section-header{
  margin-bottom:40px;
}

.sub-title{
  color:#ff2a3d;
  font-size:11px;
  font-weight:800;
  letter-spacing:4px;
  text-transform:uppercase;
}

.section-title{
  font-size:38px;
  font-weight:900;
  color:#111;
}

.brand-grid{
  display:grid;
  grid-template-columns:repeat(4,1fr);
  gap:24px;
}

.brand-card{
  background:#fff;
  border:none;
  border-radius:22px;
  padding:30px;
  text-align:center;
  cursor:pointer;
  transition:.35s;

  box-shadow:
    0 4px 15px rgba(0,0,0,.05);
}

.brand-card:hover{
  transform:translateY(-8px);

  box-shadow:
    0 18px 35px rgba(255,42,61,.12);
}

.logo-wrapper{
  height:100px;

  display:flex;
  align-items:center;
  justify-content:center;
}

.brand-card img{
  max-width:120px;
  max-height:80px;
  object-fit:contain;
  transition:.35s;
}

.brand-card:hover img{
  transform:scale(1.08);
}

.brand-card h4{
  margin-top:18px;
  font-size:16px;
  font-weight:800;
  color:#111;
}

@media(max-width:1200px){
  .brand-grid{
    grid-template-columns:repeat(3,1fr);
  }
}

@media(max-width:768px){
  .brand-section{
    padding:50px 20px;
  }

  .section-title{
    font-size:30px;
  }

  .brand-grid{
    grid-template-columns:repeat(2,1fr);
  }
}

@media(max-width:576px){
  .brand-grid{
    grid-template-columns:1fr;
  }
}

</style>

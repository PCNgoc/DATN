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
  padding:60px 40px;
  background:#fafafa;
}

.section-header{
  margin-bottom:30px;
}

.sub-title{
  color:#888;
  font-size:11px;
  font-weight:700;
  letter-spacing:3px;
  text-transform:uppercase;
}

.section-title{
  margin-top:6px;
  font-size:28px;
  font-weight:800;
  color:#111;
}

.brand-grid{
  display:grid;
  grid-template-columns:repeat(4,1fr);
  gap:20px;
}

.brand-card{
  background:#fff;

  border:1px solid #eee;

  border-radius:18px;

  padding:25px;

  text-align:center;

  cursor:pointer;

  transition:.3s;
}

.brand-card:hover{
  transform:translateY(-5px);

  border-color:#111;

  box-shadow:
    0 12px 25px rgba(0,0,0,.08);
}

.logo-wrapper{
  height:90px;

  display:flex;
  align-items:center;
  justify-content:center;
}

.brand-card img{
  max-width:100px;
  max-height:70px;

  object-fit:contain;

  transition:.3s;
}

.brand-card:hover img{
  transform:scale(1.08);
}

.brand-card h4{
  margin-top:18px;

  font-size:15px;
  font-weight:700;

  color:#111;
}

@media(max-width:1200px){

  .brand-grid{
    grid-template-columns:repeat(3,1fr);
  }

}

@media(max-width:768px){

  .brand-section{
    padding:40px 20px;
  }

  .brand-grid{
    grid-template-columns:repeat(2,1fr);
  }

  .section-title{
    font-size:24px;
  }

}

@media(max-width:576px){

  .brand-grid{
    grid-template-columns:1fr;
  }

}



</style>

<script setup>
import { ref, onMounted } from "vue"
import { getAll } from "@/services/danhMucService"

const categories = ref([])

onMounted(async () => {
  const res = await getAll()
  categories.value = res.data.slice(0, 6)
})
</script>

<template>
  <section class="category-section">

    <div class="section-header">

      <div>
        <span class="sub-title">
          GX SNEAKER
        </span>

        <h2 class="section-title">
          Danh mục nổi bật
        </h2>
      </div>

    </div>

    <div class="category-grid">

      <div
        v-for="item in categories"
        :key="item.id"
        class="category-card"
      >

        <div class="category-icon">
          👟
        </div>

        <div class="category-name">
          {{ item.ten }}
        </div>

      </div>

    </div>

  </section>
</template>

<style scoped>

/* =========================
   QUAN TRỌNG: BỎ NỀN TRẮNG
========================= */

.category-section{
  padding:70px 40px;

  /* ❌ trước đây: #fff */
  /* background:#ffffff; */

  /* ✅ để nền Home hiển thị */
  background:transparent;
}

/* HEADER */

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

/* GRID */

.category-grid{
  display:grid;
  grid-template-columns:repeat(6,1fr);
  gap:22px;
}

/* CARD */

.category-card{
  background:rgba(255,255,255,.92);
  backdrop-filter:blur(10px);

  border-radius:22px;
  padding:30px 15px;

  text-align:center;
  cursor:pointer;

  transition:.35s;

  box-shadow:0 4px 15px rgba(0,0,0,.05);
}

.category-card:hover{
  transform:translateY(-8px);
  box-shadow:0 15px 35px rgba(255,42,61,.15);
}

.category-icon{
  font-size:38px;
  margin-bottom:15px;
}

.category-name{
  font-size:15px;
  font-weight:800;
  color:#111;
}

/* RESPONSIVE */

@media(max-width:1200px){
  .category-grid{
    grid-template-columns:repeat(4,1fr);
  }
}

@media(max-width:768px){
  .category-section{
    padding:50px 20px;
  }

  .section-title{
    font-size:30px;
  }

  .category-grid{
    grid-template-columns:repeat(3,1fr);
  }
}

@media(max-width:576px){
  .category-grid{
    grid-template-columns:repeat(2,1fr);
  }
}

</style>

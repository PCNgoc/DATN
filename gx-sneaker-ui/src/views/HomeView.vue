<template>
  <div class="home-page">

    <!-- Banner -->
    <Banner />

    <!-- Nội dung bên dưới banner -->
    <div class="main-content">

      <section class="section-gap">
        <FeaturedCategories />
      </section>

      <section class="section-gap">
        <FeaturedProducts />
      </section>

      <section class="section-gap">
        <FeaturedBrands />
      </section>

      <section class="section-gap">
        <NewProducts />
      </section>

    </div>

  </div>
</template>

<script setup>
import Banner from "@/components/Banner.vue"
import FeaturedCategories from "@/components/FeaturedCategories.vue"
import FeaturedProducts from "@/components/FeaturedProducts.vue"
import FeaturedBrands from "@/components/FeaturedBrands.vue"
import NewProducts from "@/components/NewProducts.vue"
</script>

<style scoped>

.home-page{
  min-height:100vh;
  background:#fff;
}

/* ==========================
   TOÀN BỘ NỘI DUNG DƯỚI BANNER
========================== */

.main-content{

  background-image:
    linear-gradient(
      rgba(255,255,255,.35),
      rgba(255,255,255,.35)
    ),
    url("/images/home-background.jpg");

  background-size:cover;
  background-position:center;
  background-repeat:no-repeat;

  /* Hiệu ứng Parallax */
  background-attachment:fixed;

  padding:40px 0 80px;
}

/* ==========================
        KHOẢNG CÁCH
========================== */

.section-gap{

  position:relative;

  padding:30px 0 50px;

}

/* ==========================
      ĐƯỜNG LINE ĐỎ
========================== */

.section-gap::before{

  content:"";

  display:block;

  width:120px;
  height:3px;

  margin:0 auto 55px;

  border-radius:999px;

  background:
    linear-gradient(
      90deg,
      transparent,
      #ff2a3d,
      transparent
    );

}

.section-gap:first-child::before{
  display:none;
}

/* ==========================
        RESPONSIVE
========================== */

@media(max-width:992px){

  .main-content{
    background-attachment:scroll;
  }

}

</style>

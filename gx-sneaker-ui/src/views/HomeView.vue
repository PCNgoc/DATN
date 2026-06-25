<template>
  <div class="home-page">
    <Banner />

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
</template>

<script setup>
import Banner from "@/components/Banner.vue"
import FeaturedCategories from "@/components/FeaturedCategories.vue"
import FeaturedProducts from "@/components/FeaturedProducts.vue"
import NewProducts from "@/components/NewProducts.vue"
import FeaturedBrands from "@/components/FeaturedBrands.vue"
</script>

<style scoped>
.home-page{
  background:#f8f9fb;
  min-height:100vh;
}

.section-gap{
  position:relative;
  padding:10px 0 30px;
}

.section-gap::before{
  content:"";
  display:block;

  width:120px;
  height:3px;

  margin:0 auto 50px;

  border-radius:999px;

  background:linear-gradient(
    90deg,
    transparent,
    #ff2a3d,
    transparent
  );
}

.section-gap:first-of-type::before{
  display:none;
}
</style>

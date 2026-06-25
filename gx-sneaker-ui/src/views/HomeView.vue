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

<style scoped>
.section-gap{
  margin-top:20px;
}
</style>

<script setup>
import Banner from "@/components/Banner.vue"
import FeaturedCategories from "@/components/FeaturedCategories.vue"
import FeaturedProducts from "@/components/FeaturedProducts.vue"
import NewProducts from "@/components/NewProducts.vue"
import FeaturedBrands from "@/components/FeaturedBrands.vue"
</script>
<style scoped>
.section-gap{
  position:relative;
}

.section-gap::before{
  content:"";

  display:block;

  width:100px;
  height:1px;

  background:#e5e5e5;

  margin:0 auto 50px;
}
</style>

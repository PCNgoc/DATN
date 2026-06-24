<script setup>
import { ref,onMounted } from "vue"
import { getAllThuongHieu }
  from "@/services/thuongHieuService"

const brands = ref([])

onMounted(async()=>{

  const res =
    await getAllThuongHieu()

  brands.value =
    res.data.slice(0,8)

})
</script>

<template>

  <section class="section">

    <h2>Thương hiệu nổi bật</h2>

    <div class="brand-grid">

      <div
        v-for="item in brands"
        :key="item.id"
        class="brand-card"
      >

        <img
          :src="item.logo"
        />

        <h4>
          {{ item.ten }}
        </h4>

      </div>

    </div>

  </section>

</template>

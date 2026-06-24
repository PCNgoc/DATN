<script setup>
import { ref,onMounted } from "vue"
import { getAll } from "@/services/danhMucService"

const categories = ref([])

onMounted(async()=>{

  const res = await getAll()

  categories.value = res.data.slice(0,6)

})
</script>

<template>
  <section class="section">

    <h2>Danh mục nổi bật</h2>

    <div class="grid">

      <div
        v-for="item in categories"
        :key="item.id"
        class="card"
      >
        {{ item.ten }}
      </div>

    </div>

  </section>
</template>

<style scoped>
.section{
  padding:80px;
}

.grid{
  display:grid;
  grid-template-columns:repeat(6,1fr);
  gap:20px;
}

.card{
  background:white;
  border-radius:16px;
  padding:30px;
  text-align:center;
  font-weight:700;
  box-shadow:0 5px 15px rgba(0,0,0,.08);
}
</style>

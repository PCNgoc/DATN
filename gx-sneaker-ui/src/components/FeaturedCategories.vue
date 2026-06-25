<script setup>
import { ref,onMounted } from "vue"
import { getAll } from "@/services/danhMucService"

const categories = ref([])

onMounted(async()=>{

  const res = await getAll()

  categories.value = res.data.slice(0,6)

})
</script>

```vue
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
```css
<style scoped>

.category-section{
  padding:60px 40px;
  background:#fff;
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

.category-grid{
  display:grid;
  grid-template-columns:repeat(6,1fr);
  gap:18px;
}

.category-card{
  background:#fff;

  border:1px solid #eee;

  border-radius:16px;

  padding:24px 15px;

  text-align:center;

  cursor:pointer;

  transition:.3s;
}

.category-card:hover{
  transform:translateY(-5px);

  border-color:#111;

  box-shadow:
    0 12px 25px rgba(0,0,0,.08);
}

.category-icon{
  font-size:28px;
  margin-bottom:12px;
}

.category-name{
  font-size:14px;
  font-weight:700;
  color:#111;
}

@media(max-width:1200px){

  .category-grid{
    grid-template-columns:repeat(4,1fr);
  }

}

@media(max-width:768px){

  .category-section{
    padding:40px 20px;
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


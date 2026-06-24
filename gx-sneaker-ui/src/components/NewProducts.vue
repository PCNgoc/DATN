<script setup>
import { ref, onMounted } from "vue"
import { getNewestProducts }
  from "@/services/productService"

const products = ref([])

onMounted(async () => {

  const res =
    await getNewestProducts()

  products.value = res.data

})
</script>

<template>
  <section class="new-products">

    <div class="section-header">
      <h2>Sản phẩm mới</h2>
    </div>

    <div class="product-grid">

      <div
        v-for="sp in products"
        :key="sp.id"
        class="product-card"
      >
        <img
          :src="`/images/${sp.anhDaiDien}`"
          :alt="sp.tenSanPham"
        >

        <div class="info">

          <span class="brand">
            {{ sp.tenThuongHieu }}
          </span>

          <h3>
            {{ sp.tenSanPham }}
          </h3>

          <span class="new-badge">
            NEW
          </span>

        </div>

      </div>

    </div>

  </section>
</template>

<style scoped>

.new-products{
  padding:80px;
  background:#fff;
}

.section-header{
  margin-bottom:35px;
}

.section-header h2{
  font-size:34px;
  font-weight:800;
}

.product-grid{
  display:grid;
  grid-template-columns:repeat(5,1fr);
  gap:24px;
}

.product-card{
  background:white;
  border-radius:18px;
  overflow:hidden;
  border:1px solid #eee;
  transition:.3s;
  cursor:pointer;
}

.product-card:hover{
  transform:translateY(-8px);
  box-shadow:0 15px 35px rgba(0,0,0,.1);
}

.product-card img{
  width:100%;
  height:260px;
  object-fit:cover;
}

.info{
  padding:15px;
}

.brand{
  font-size:12px;
  color:#888;
  text-transform:uppercase;
}

.info h3{
  margin-top:8px;
  font-size:16px;
  min-height:50px;
}

.new-badge{
  display:inline-block;
  margin-top:10px;
  background:#111;
  color:white;
  padding:5px 10px;
  border-radius:20px;
  font-size:11px;
  font-weight:700;
}

@media(max-width:1400px){
  .product-grid{
    grid-template-columns:repeat(4,1fr);
  }
}

@media(max-width:1100px){
  .product-grid{
    grid-template-columns:repeat(3,1fr);
  }
}

@media(max-width:768px){
  .new-products{
    padding:40px 20px;
  }

  .product-grid{
    grid-template-columns:repeat(2,1fr);
  }
}

</style>

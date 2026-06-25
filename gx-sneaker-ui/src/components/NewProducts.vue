```vue
<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { getNewestProducts } from "@/services/productService"

const router = useRouter()

const products = ref([])

onMounted(async () => {
  try {
    const res = await getNewestProducts()
    products.value = res.data
  } catch (error) {
    console.error("Lỗi tải sản phẩm:", error)
  }
})

const goDetail = (id) => {
  router.push(`/products/${id}`)
}
</script>

<template>
  <section class="new-products">

    <div class="section-header">

      <div>
        <span class="sub-title">
          NEW ARRIVAL
        </span>

        <h2 class="section-title">
          Sản phẩm mới nhất
        </h2>
      </div>

    </div>

    <div class="product-grid">

      <div
        v-for="sp in products"
        :key="sp.id"
        class="product-card"
        @click="goDetail(sp.id)"
      >

        <div class="image-wrapper">

          <span class="new-badge">
            NEW
          </span>

          <img
            :src="`/images/${sp.anhDaiDien}`"
            :alt="sp.tenSanPham"
          />

        </div>

        <div class="info">

          <span class="brand">
            {{ sp.tenThuongHieu }}
          </span>

          <h3 class="product-name">
            {{ sp.tenSanPham }}
          </h3>

          <div class="product-footer">
            <div class="btn-detail">
              Xem chi tiết →
            </div>
          </div>

        </div>

      </div>

    </div>

  </section>
</template>

<style scoped>

.new-products{
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

.product-grid{
  display:grid;
  grid-template-columns:repeat(4,1fr);
  gap:20px;
}

.product-card{
  position:relative;
  background:#fff;
  border:1px solid #eee;
  border-radius:16px;
  overflow:hidden;
  cursor:pointer;
  transition:all .35s ease;
}

.product-card:hover{
  transform:translateY(-8px);
  box-shadow:0 20px 40px rgba(0,0,0,.12);
}

.image-wrapper{
  position:relative;
  overflow:hidden;
}

.image-wrapper img{
  width:100%;
  height:230px;
  object-fit:cover;
  transition:.4s;
}

.product-card:hover img{
  transform:scale(1.08);
}

.new-badge{
  position:absolute;
  top:12px;
  left:12px;

  background:#111;
  color:#fff;

  padding:5px 10px;

  border-radius:20px;

  font-size:10px;
  font-weight:700;

  z-index:10;
}

.info{
  padding:14px;
}

.brand{
  display:block;

  color:#888;

  font-size:10px;
  font-weight:700;

  text-transform:uppercase;

  letter-spacing:1px;
}

.product-name{
  margin-top:8px;

  font-size:15px;
  font-weight:700;

  color:#111;

  line-height:1.4;

  height:42px;

  overflow:hidden;

  display:-webkit-box;
  -webkit-line-clamp:2;
  -webkit-box-orient:vertical;
}

.product-footer{
  margin-top:14px;
}

.btn-detail{
  font-size:12px;
  font-weight:600;
  color:#777;
  transition:.3s;
}

.product-card:hover .btn-detail{
  color:#111;
  transform:translateX(4px);
}

@media(max-width:1200px){

  .product-grid{
    grid-template-columns:repeat(3,1fr);
  }

}

@media(max-width:768px){

  .new-products{
    padding:40px 20px;
  }

  .section-title{
    font-size:24px;
  }

  .product-grid{
    grid-template-columns:repeat(2,1fr);
  }

}

@media(max-width:576px){

  .product-grid{
    grid-template-columns:1fr;
  }

}
</style>
```

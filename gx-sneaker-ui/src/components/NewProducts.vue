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
  background:#ffffff;
}

.section-header{
  display:flex;
  justify-content:space-between;
  align-items:center;
  margin-bottom:35px;
}

.sub-title{
  display:block;
  color:#ff2a3d;
  font-size:11px;
  font-weight:800;
  letter-spacing:4px;
  text-transform:uppercase;
  margin-bottom:8px;
}

.section-title{
  font-size:36px;
  font-weight:900;
  color:#111;
  line-height:1.2;
}

.product-grid{
  display:grid;
  grid-template-columns:repeat(4,minmax(0,1fr));
  gap:24px;
}

.product-card{
  background:#fff;
  border-radius:20px;
  overflow:hidden;
  cursor:pointer;
  transition:all .3s ease;

  box-shadow:
    0 5px 20px rgba(0,0,0,.06);
}

.product-card:hover{
  transform:translateY(-8px);

  box-shadow:
    0 18px 35px rgba(0,0,0,.12);
}

.image-wrapper{
  position:relative;
  height:240px;
  overflow:hidden;
}

.image-wrapper img{
  width:100%;
  height:100%;
  object-fit:cover;
  display:block;
  transition:.4s;
}

.product-card:hover img{
  transform:scale(1.08);
}

.new-badge{
  position:absolute;
  top:12px;
  left:12px;

  background:#ff2a3d;
  color:#fff;

  padding:6px 12px;

  border-radius:999px;

  font-size:10px;
  font-weight:800;

  z-index:2;
}

.info{
  padding:18px;
}

.brand{
  display:block;

  color:#999;

  font-size:10px;
  font-weight:700;

  letter-spacing:2px;

  text-transform:uppercase;
}

.product-name{
  margin-top:10px;

  font-size:17px;
  font-weight:800;

  color:#111;

  line-height:1.5;

  min-height:52px;

  display:-webkit-box;
  -webkit-line-clamp:2;
  -webkit-box-orient:vertical;

  overflow:hidden;
}

.product-footer{
  margin-top:14px;
}

.btn-detail{
  color:#ff2a3d;
  font-size:13px;
  font-weight:700;
  transition:.3s;
}

.product-card:hover .btn-detail{
  transform:translateX(4px);
}

@media(max-width:1200px){

  .product-grid{
    grid-template-columns:repeat(3,1fr);
  }

}

@media(max-width:768px){

  .new-products{
    padding:50px 20px;
  }

  .section-title{
    font-size:30px;
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




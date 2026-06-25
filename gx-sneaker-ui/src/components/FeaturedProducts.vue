<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"

import { getAll } from "@/services/sanPhamService"

const router = useRouter()

const products = ref([])
const loading = ref(true)

const loadProducts = async () => {
  try {
    const res = await getAll()

    products.value = res.data
      .filter(item => item.trangThai === true)
      .slice(0, 8)

  } catch (error) {
    console.error("Lỗi tải sản phẩm:", error)
  } finally {
    loading.value = false
  }
}

const goDetail = (id) => {
  router.push(`/products/${id}`)
}

onMounted(loadProducts)
</script>

```vue
<template>
  <section class="featured">

    <div class="section-header">

      <div>
        <span class="sub-title">
          GX SNEAKER
        </span>

        <h2 class="section-title">
          Sản phẩm nổi bật
        </h2>
      </div>

      <router-link
        to="/products"
        class="view-all"
      >
        Xem tất cả →
      </router-link>

    </div>

    <div
      v-if="loading"
      class="loading"
    >
      Đang tải sản phẩm...
    </div>

    <div
      v-else
      class="product-grid"
    >

      <div
        v-for="sp in products"
        :key="sp.id"
        class="product-card"
        @click="goDetail(sp.id)"
      >

        <div class="image-wrapper">

          <span class="product-badge">
            HOT
          </span>

          <img
            :src="`/images/${sp.anhDaiDien}`"
            :alt="sp.tenSanPham"
          />

        </div>

        <div class="product-info">

          <span class="brand">
            {{ sp.tenThuongHieu }}
          </span>

          <h3 class="product-name">
            {{ sp.tenSanPham }}
          </h3>

          <div class="category-tag">
            {{ sp.tenDanhMuc }}
          </div>

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

.featured{
  padding:60px 40px;
  background:#f8f9fb;
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

.view-all{
  text-decoration:none;
  color:#111;
  font-size:15px;
  font-weight:700;
  transition:.3s;
}

.view-all:hover{
  color:#ff2a3d;
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
  box-shadow:0 5px 20px rgba(0,0,0,.06);
}

.product-card:hover{
  transform:translateY(-8px);
  box-shadow:0 18px 35px rgba(0,0,0,.12);
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

.product-badge{
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

.product-info{
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

.category-tag{
  display:inline-block;

  margin-top:10px;

  padding:6px 12px;

  border-radius:999px;

  background:#fff1f2;

  color:#ff2a3d;

  font-size:11px;
  font-weight:700;
}

.product-footer{
  margin-top:14px;
}

.btn-detail{
  color:#ff2a3d;
  font-size:13px;
  font-weight:700;
}

.loading{
  text-align:center;
  padding:50px;
  color:#666;
}

@media(max-width:1200px){

  .product-grid{
    grid-template-columns:repeat(3,1fr);
  }

}

@media(max-width:768px){

  .featured{
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



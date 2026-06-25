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
  padding:50px 40px;
  background:#fafafa;
}

.section-header{
  display:flex;
  justify-content:space-between;
  align-items:center;
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

.view-all{
  text-decoration:none;
  color:#111;
  font-size:14px;
  font-weight:600;
  transition:.3s;
}

.view-all:hover{
  color:#ff4d4f;
}

.product-grid{
  display:grid;
  grid-template-columns:repeat(4,1fr);
  gap:18px;
}

.product-card{
  background:#fff;
  border:1px solid #eee;
  border-radius:14px;
  overflow:hidden;
  cursor:pointer;
  transition:.3s;
}

.product-card:hover{
  transform:translateY(-4px);
  box-shadow:0 10px 25px rgba(0,0,0,.08);
}

.image-wrapper{
  position:relative;
  overflow:hidden;
}

.product-badge{
  position:absolute;
  top:10px;
  left:10px;
  z-index:10;

  background:#111;
  color:#fff;

  padding:4px 8px;

  border-radius:20px;

  font-size:9px;
  font-weight:700;
}

.image-wrapper img{
  width:100%;
  height:220px;
  object-fit:cover;
  transition:.4s;
}

.product-card:hover img{
  transform:scale(1.05);
}

.product-info{
  padding:14px;
}

.brand{
  display:block;
  color:#888;
  font-size:10px;
  font-weight:700;
  letter-spacing:1px;
  text-transform:uppercase;
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

.category-tag{
  display:inline-block;
  margin-top:8px;

  padding:4px 10px;

  border-radius:20px;

  background:#f5f5f5;

  color:#666;

  font-size:11px;
}

.product-footer{
  margin-top:10px;
}

.btn-detail{
  font-size:12px;
  font-weight:600;
  color:#777;
  transition:.3s;
}

.product-card:hover .btn-detail{
  color:#111;
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


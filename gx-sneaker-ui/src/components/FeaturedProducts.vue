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
      .slice(0, 5)

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

<template>
  <section class="featured">

    <div class="section-header">

      <div>
        <span class="sub-title">
          GX SNEAKER
        </span>

        <h2>
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

          <img
            :src="`/images/${sp.anhDaiDien}`"
            :alt="sp.tenSanPham"
          >

        </div>

        <div class="product-info">

          <span class="brand">
            {{ sp.tenThuongHieu }}
          </span>

          <h3>
            {{ sp.tenSanPham }}
          </h3>

          <p>
            {{ sp.tenDanhMuc }}
          </p>

          <div class="btn-detail">
            Xem chi tiết →
          </div>

        </div>

      </div>

    </div>

  </section>
</template>

<style scoped>

.featured{
  padding:80px 60px;
  background:#fafafa;
}

.section-header{
  display:flex;
  justify-content:space-between;
  align-items:center;
  margin-bottom:40px;
}

.sub-title{
  color:#888;
  font-size:13px;
  letter-spacing:2px;
  text-transform:uppercase;
}

.section-header h2{
  font-size:40px;
  font-weight:800;
  margin-top:8px;
}

.view-all{
  text-decoration:none;
  color:#111;
  font-weight:700;
}

.product-grid{
  display:grid;
  grid-template-columns:repeat(5,1fr);
  gap:24px;
}

.product-card{
  background:white;
  border-radius:20px;
  overflow:hidden;
  cursor:pointer;
  border:1px solid #ececec;
  transition:.35s;
}

.product-card:hover{
  transform:translateY(-10px);

  box-shadow:
    0 20px 40px rgba(0,0,0,.1);
}

.image-wrapper{
  overflow:hidden;
}

.image-wrapper img{
  width:100%;
  height:260px;
  object-fit:cover;
  transition:.5s;
}

.product-card:hover img{
  transform:scale(1.08);
}

.product-info{
  padding:18px;
}

.brand{
  font-size:12px;
  color:#888;
  font-weight:700;
  text-transform:uppercase;
}

.product-info h3{
  margin-top:10px;
  font-size:17px;
  min-height:48px;
}

.product-info p{
  color:#666;
  margin-top:8px;
}

.btn-detail{
  margin-top:15px;
  font-weight:700;
}

.loading{
  text-align:center;
  padding:50px;
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

  .featured{
    padding:40px 20px;
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

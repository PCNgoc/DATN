<script setup>
import { ref, computed, onMounted } from "vue"
import { useRouter } from "vue-router"

import {
  getProducts
} from "@/services/productService"

import {
  getAllThuongHieu
} from "@/services/thuongHieuService"

import {
  getAll as getDanhMuc
} from "@/services/danhMucService"

const router = useRouter()

const products = ref([])
const brands = ref([])
const categories = ref([])

const loading = ref(true)

const keyword = ref("")
const selectedBrand = ref("")
const selectedCategory = ref("")

const loadData = async () => {
  try {

    loading.value = true

    const [sp, th, dm] = await Promise.all([
      getProducts(),
      getAllThuongHieu(),
      getDanhMuc()
    ])

    products.value = sp.data
    brands.value = th.data
    categories.value = dm.data

  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const filteredProducts = computed(() => {

  return products.value.filter(sp => {

    const matchKeyword =
      sp.tenSanPham
        ?.toLowerCase()
        .includes(keyword.value.toLowerCase())

    const matchBrand =
      !selectedBrand.value ||
      sp.idThuongHieu == selectedBrand.value

    const matchCategory =
      !selectedCategory.value ||
      sp.idDanhMuc == selectedCategory.value

    return (
      matchKeyword &&
      matchBrand &&
      matchCategory
    )

  })

})

const detail = (id) => {
  router.push(`/products/${id}`)
}

onMounted(() => {
  loadData()
})
</script>
<template>

  <div class="product-page">

    <!-- HERO -->

    <section class="hero">

      <div class="hero-content">

      <span class="hero-tag">
        GX SNEAKER
      </span>

        <h1>
          Premium Sneaker Collection
        </h1>

        <p>
          Khám phá những mẫu sneaker chất lượng,
          phong cách và hiện đại nhất.
        </p>

      </div>

    </section>

    <!-- CONTENT -->

    <div class="container">

      <!-- SIDEBAR -->

      <aside class="sidebar">

        <h3>Bộ lọc</h3>

        <input
          v-model="keyword"
          placeholder="Tìm sản phẩm..."
          class="search-input"
        />

        <div class="filter-group">

          <label>Thương hiệu</label>

          <select v-model="selectedBrand">

            <option value="">
              Tất cả
            </option>

            <option
              v-for="item in brands"
              :key="item.id"
              :value="item.id"
            >
              {{ item.ten }}
            </option>

          </select>

        </div>

        <div class="filter-group">

          <label>Danh mục</label>

          <select v-model="selectedCategory">

            <option value="">
              Tất cả
            </option>

            <option
              v-for="item in categories"
              :key="item.id"
              :value="item.id"
            >
              {{ item.ten }}
            </option>

          </select>

        </div>

      </aside>

      <!-- PRODUCTS -->

      <section class="content">

        <div class="content-header">

          <h2>
            Sản phẩm
          </h2>

          <span>
          {{ filteredProducts.length }}
          sản phẩm
        </span>

        </div>

        <!-- LOADING -->

        <div
          v-if="loading"
          class="product-grid"
        >

          <div
            class="skeleton-card"
            v-for="i in 8"
            :key="i"
          ></div>

        </div>

        <!-- EMPTY -->

        <div
          v-else-if="filteredProducts.length === 0"
          class="empty-state"
        >

          Không tìm thấy sản phẩm

        </div>

        <!-- DATA -->

        <div
          v-else
          class="product-grid"
        >

          <div
            class="product-card"
            v-for="sp in filteredProducts"
            :key="sp.id"
          >

            <div class="image-wrapper">

              <img
                :src="`/images/${sp.anhDaiDien}`"
                :alt="sp.tenSanPham"
              />

            </div>

            <div class="product-info">

            <span class="brand">

              {{ sp.tenThuongHieu }}

            </span>

              <h4>
                {{ sp.tenSanPham }}
              </h4>

              <p>
                {{ sp.tenDanhMuc }}
              </p>

              <button
                @click="detail(sp.id)"
              >
                Xem chi tiết
              </button>

            </div>

          </div>

        </div>

      </section>

    </div>

  </div>

</template>
<style scoped>

.product-page{
  background:#f5f5f5;
  min-height:100vh;
}

/* HERO */

.hero{
  height:320px;
  background:#111;

  display:flex;
  justify-content:center;
  align-items:center;

  color:white;
}

.hero-content{
  text-align:center;
}

.hero-tag{
  display:inline-block;
  padding:8px 16px;

  border:1px solid #444;
  border-radius:30px;

  margin-bottom:20px;

  font-size:13px;
  letter-spacing:2px;
}

.hero h1{
  font-size:56px;
  font-weight:800;
}

.hero p{
  margin-top:15px;
  color:#bdbdbd;
}

/* LAYOUT */

.container{
  width:95%;
  max-width:1500px;

  margin:auto;

  padding:40px 0;

  display:grid;
  grid-template-columns:280px 1fr;

  gap:30px;
}

/* SIDEBAR */

.sidebar{
  background:white;

  padding:25px;

  border-radius:16px;

  height:fit-content;
}

.sidebar h3{
  margin-bottom:20px;
}

.search-input,
.sidebar select{
  width:100%;

  padding:12px;

  border:1px solid #ddd;
  border-radius:10px;

  margin-bottom:20px;
}

.filter-group{
  margin-bottom:15px;
}

.filter-group label{
  display:block;
  margin-bottom:8px;
  font-weight:600;
}

/* CONTENT */

.content-header{
  display:flex;
  justify-content:space-between;
  align-items:center;

  margin-bottom:25px;
}

.content-header h2{
  font-size:28px;
}

/* GRID */

.product-grid{
  display:grid;

  grid-template-columns:
    repeat(auto-fill,minmax(260px,1fr));

  gap:25px;
}

/* CARD */

.product-card{
  background:white;

  border-radius:18px;

  overflow:hidden;

  transition:.3s;

  border:1px solid #eee;
}

.product-card:hover{
  transform:translateY(-6px);

  box-shadow:
    0 10px 30px rgba(0,0,0,.08);
}

.image-wrapper{
  overflow:hidden;
}

.image-wrapper img{
  width:100%;
  height:280px;

  object-fit:cover;

  transition:.4s;
}

.product-card:hover img{
  transform:scale(1.05);
}

.product-info{
  padding:18px;
}

.brand{
  font-size:13px;
  color:#777;
}

.product-info h4{
  margin:10px 0;

  font-size:18px;
  font-weight:700;

  color:#111;
}

.product-info p{
  color:#666;
  margin-bottom:15px;
}

.product-info button{
  width:100%;

  border:none;

  background:#111;
  color:white;

  padding:12px;

  border-radius:10px;

  cursor:pointer;
}

.product-info button:hover{
  background:#222;
}

/* LOADING */

.skeleton-card{
  height:380px;

  border-radius:18px;

  background:
    linear-gradient(
      90deg,
      #ececec 25%,
      #f7f7f7 50%,
      #ececec 75%
    );

  background-size:400% 100%;

  animation:loading 1.2s infinite;
}

@keyframes loading{

  0%{
    background-position:100% 0;
  }

  100%{
    background-position:-100% 0;
  }

}

/* EMPTY */

.empty-state{
  background:white;

  padding:80px;

  text-align:center;

  border-radius:16px;

  font-size:18px;
  color:#666;
}

/* MOBILE */

@media(max-width:992px){

  .container{
    grid-template-columns:1fr;
  }

  .hero h1{
    font-size:38px;
  }

}

</style>

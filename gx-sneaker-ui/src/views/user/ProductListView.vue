<script setup>
import { ref, computed, onMounted } from "vue"
import { useRouter } from "vue-router"

import { getProducts } from "@/services/productService"
import { getAllThuongHieu } from "@/services/thuongHieuService"
import { getAll as getDanhMuc } from "@/services/danhMucService"

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

  } catch (error) {

    console.error(error)

  } finally {

    loading.value = false

  }
}

const filteredProducts = computed(() => {

  return products.value.filter(sp => {

    const matchKeyword =
      !keyword.value ||
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

onMounted(loadData)
</script>
<template>
  <div class="product-page">

    <!-- HERO -->
    <section class="hero">
      <div class="hero-content">
        <span class="hero-tag">GX SNEAKER</span>

        <h1>Premium Sneaker Collection</h1>

        <p>
          Khám phá những mẫu sneaker chất lượng,
          phong cách và hiện đại nhất.
        </p>
      </div>
    </section>

    <!-- 👇 MAIN BACKGROUND GIỐNG HOME -->
    <div class="main-content">

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
              <option value="">Tất cả</option>
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
              <option value="">Tất cả</option>
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

        <!-- CONTENT -->
        <section class="content">

          <div class="content-header">
            <h2>Sản phẩm</h2>
            <span>{{ filteredProducts.length }} sản phẩm</span>
          </div>

          <!-- LOADING -->
          <div v-if="loading" class="product-grid">
            <div class="skeleton-card" v-for="i in 8" :key="i"></div>
          </div>

          <!-- EMPTY -->
          <div v-else-if="filteredProducts.length === 0" class="empty-state">
            Không tìm thấy sản phẩm
          </div>

          <!-- LIST -->
          <div v-else class="product-grid">

            <div
              class="product-card"
              v-for="sp in filteredProducts"
              :key="sp.id"
              @click="detail(sp.id)"
            >

              <div class="image-wrapper">
                <img
                  :src="`/images/${sp.anhDaiDien}`"
                  :alt="sp.tenSanPham"
                />

                <div class="image-overlay">
                  Xem sản phẩm →
                </div>
              </div>

              <div class="product-info">
                <span class="brand">{{ sp.tenThuongHieu }}</span>
                <h4>{{ sp.tenSanPham }}</h4>
                <p>{{ sp.tenDanhMuc }}</p>

                <div class="gender-tag">
                  {{ sp.gioiTinh }}
                </div>
              </div>

            </div>

          </div>

        </section>

      </div>

    </div>

  </div>
</template>
<style scoped>

/* =========================
   PAGE WRAPPER
========================= */
.product-page{
  min-height:100vh;
  background:#fff;
}

/* =========================
   HERO (giữ như cũ)
========================= */
.hero{
  height:350px;
  background:
    linear-gradient(rgba(0,0,0,.55),rgba(0,0,0,.55)),
    url("/images/banner.jpg");
  background-size:cover;
  background-position:center;
  display:flex;
  justify-content:center;
  align-items:center;
  color:white;
}

.hero-content{
  text-align:center;
  max-width:800px;
}

.hero-tag{
  display:inline-block;
  padding:8px 18px;
  border:1px solid rgba(255,255,255,.3);
  border-radius:30px;
  margin-bottom:20px;
  font-size:13px;
  letter-spacing:3px;
  backdrop-filter:blur(8px);
}

.hero h1{
  font-size:58px;
  font-weight:800;
  margin-bottom:15px;
}

.hero p{
  color:#d6d6d6;
  font-size:17px;
  line-height:1.6;
}

/* =========================
   🔥 BACKGROUND GIỐNG HOME
========================= */
.main-content{

  background-image:
    linear-gradient(
      rgba(255,255,255,.35),
      rgba(255,255,255,.35)
    ),
    url("/images/home-background.jpg");

  background-size:cover;
  background-position:center;
  background-repeat:no-repeat;

  background-attachment:fixed;

  padding:40px 0 80px;
}

/* =========================
   LAYOUT
========================= */
.container{
  width:95%;
  max-width:1500px;
  margin:auto;
  display:grid;
  grid-template-columns:280px 1fr;
  gap:30px;
}

/* =========================
   SIDEBAR
========================= */
.sidebar{
  background:white;
  padding:25px;
  border-radius:20px;
  height:fit-content;
  box-shadow:0 10px 30px rgba(0,0,0,.05);
  position:sticky;
  top:20px;
}

.sidebar h3{
  font-size:22px;
  margin-bottom:20px;
}

.search-input,
.sidebar select{
  width:100%;
  padding:13px 14px;
  border:1px solid #e5e7eb;
  border-radius:12px;
  font-size:14px;
  margin-bottom:20px;
  transition:.3s;
}

.search-input:focus,
.sidebar select:focus{
  outline:none;
  border-color:#111;
  box-shadow:0 0 0 3px rgba(0,0,0,.08);
}

.filter-group{
  margin-bottom:20px;
}

.filter-group label{
  display:block;
  margin-bottom:8px;
  font-weight:600;
}

/* =========================
   CONTENT
========================= */
.content-header{
  display:flex;
  justify-content:space-between;
  align-items:center;
  margin-bottom:25px;
}

.content-header h2{
  font-size:32px;
  font-weight:800;
}

.content-header span{
  color:#6b7280;
}

/* =========================
   GRID
========================= */
.product-grid{
  display:grid;
  grid-template-columns:repeat(auto-fill,minmax(270px,1fr));
  gap:25px;
}

/* =========================
   CARD
========================= */
.product-card{
  background:white;
  border-radius:22px;
  overflow:hidden;
  cursor:pointer;
  transition:.35s;
  border:1px solid #ececec;
}

.product-card:hover{
  transform:translateY(-10px);
  box-shadow:0 20px 40px rgba(0,0,0,.12);
}

.image-wrapper{
  position:relative;
  overflow:hidden;
}

.image-wrapper img{
  width:100%;
  height:290px;
  object-fit:cover;
  transition:.5s;
}

.product-card:hover img{
  transform:scale(1.08);
}

.image-overlay{
  position:absolute;
  inset:0;
  background:rgba(0,0,0,.45);
  color:white;
  display:flex;
  justify-content:center;
  align-items:center;
  opacity:0;
  transition:.35s;
  font-weight:700;
}

.product-card:hover .image-overlay{
  opacity:1;
}

/* =========================
   INFO
========================= */
.product-info{
  padding:20px;
}

.brand{
  font-size:12px;
  font-weight:700;
  text-transform:uppercase;
  color:#6b7280;
}

.product-info h4{
  margin:10px 0;
  font-size:20px;
  min-height:56px;
}

.product-info p{
  color:#6b7280;
  font-size:14px;
  margin-bottom:15px;
}

.gender-tag{
  display:inline-block;
  padding:7px 14px;
  border-radius:999px;
  background:#f3f4f6;
  font-size:12px;
  font-weight:700;
}

/* =========================
   LOADING
========================= */
.skeleton-card{
  height:420px;
  border-radius:22px;
  background:linear-gradient(90deg,#ececec 25%,#f7f7f7 50%,#ececec 75%);
  background-size:400% 100%;
  animation:loading 1.2s infinite;
}

@keyframes loading{
  0%{background-position:100% 0;}
  100%{background-position:-100% 0;}
}

/* =========================
   EMPTY
========================= */
.empty-state{
  background:white;
  padding:80px;
  border-radius:20px;
  text-align:center;
  color:#6b7280;
}

/* =========================
   RESPONSIVE
========================= */
@media(max-width:992px){
  .container{
    grid-template-columns:1fr;
  }

  .main-content{
    background-attachment:scroll;
  }

  .hero h1{
    font-size:40px;
  }
}

@media(max-width:576px){
  .product-grid{
    grid-template-columns:1fr;
  }

  .hero h1{
    font-size:30px;
  }
}

</style>

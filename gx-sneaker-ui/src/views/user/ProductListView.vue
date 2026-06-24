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

    <div class="container">

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

      <section class="content">

        <div class="content-header">

          <h2>Sản phẩm</h2>

          <span>
            {{ filteredProducts.length }} sản phẩm
          </span>

        </div>

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

        <div
          v-else-if="filteredProducts.length === 0"
          class="empty-state"
        >

          Không tìm thấy sản phẩm

        </div>

        <div
          v-else
          class="product-grid"
        >

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

              <span class="brand">
                {{ sp.tenThuongHieu }}
              </span>

              <h4>
                {{ sp.tenSanPham }}
              </h4>

              <p>
                {{ sp.tenDanhMuc }}
              </p>

              <div class="gender-tag">
                {{ sp.gioiTinh }}
              </div>

            </div>

          </div>

        </div>

      </section>

    </div>

  </div>

</template>
<style scoped>

.product-page{
  background:#f6f7fb;
  min-height:100vh;
}

/* HERO */

.hero{
  height:350px;

  background:
    linear-gradient(
      rgba(0,0,0,.55),
      rgba(0,0,0,.55)
    ),
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

/* LAYOUT */

.container{
  width:95%;
  max-width:1500px;

  margin:auto;

  padding:40px 0;

  display:grid;

  grid-template-columns:
    280px 1fr;

  gap:30px;
}

/* SIDEBAR */

.sidebar{
  background:white;

  padding:25px;

  border-radius:20px;

  height:fit-content;

  box-shadow:
    0 10px 30px rgba(0,0,0,.05);

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

  transition:.3s;

  margin-bottom:20px;
}

.search-input:focus,
.sidebar select:focus{
  outline:none;

  border-color:#111;

  box-shadow:
    0 0 0 3px rgba(0,0,0,.08);
}

.filter-group{
  margin-bottom:20px;
}

.filter-group label{
  display:block;

  margin-bottom:8px;

  font-weight:600;

  color:#374151;
}

/* CONTENT */

.content{
  min-width:0;
}

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
  font-weight:600;
}

/* GRID */

.product-grid{
  display:grid;

  grid-template-columns:
    repeat(auto-fill,minmax(270px,1fr));

  gap:25px;
}

/* CARD */

.product-card{
  background:white;

  border-radius:22px;

  overflow:hidden;

  cursor:pointer;

  transition:.35s ease;

  border:1px solid #ececec;

  position:relative;
}

.product-card:hover{
  transform:
    translateY(-10px);

  box-shadow:
    0 20px 40px rgba(0,0,0,.12);
}

/* IMAGE */

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

/* OVERLAY */

.image-overlay{
  position:absolute;

  inset:0;

  background:
    rgba(0,0,0,.45);

  color:white;

  display:flex;

  justify-content:center;
  align-items:center;

  font-size:18px;
  font-weight:700;

  opacity:0;

  transition:.35s;
}

.product-card:hover .image-overlay{
  opacity:1;
}

/* PRODUCT INFO */

.product-info{
  padding:20px;
}

.brand{
  display:inline-block;

  font-size:12px;

  letter-spacing:1px;

  font-weight:700;

  color:#6b7280;

  text-transform:uppercase;
}

.product-info h4{
  margin-top:10px;
  margin-bottom:10px;

  font-size:20px;
  font-weight:700;

  color:#111827;

  line-height:1.4;

  min-height:56px;
}

.product-info p{
  color:#6b7280;

  margin-bottom:15px;

  font-size:14px;
}

/* GENDER */

.gender-tag{
  display:inline-flex;

  align-items:center;

  gap:5px;

  padding:7px 14px;

  border-radius:999px;

  background:#f3f4f6;

  color:#374151;

  font-size:12px;

  font-weight:700;
}

/* BADGE */

.badge{
  position:absolute;

  top:15px;
  left:15px;

  background:#111827;

  color:white;

  padding:7px 12px;

  border-radius:30px;

  font-size:11px;

  font-weight:700;

  z-index:2;
}

.badge.hot{
  background:#dc2626;
}

.badge.new{
  background:#16a34a;
}

/* LOADING */

.skeleton-card{
  height:420px;

  border-radius:22px;

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

  border-radius:20px;

  text-align:center;

  color:#6b7280;

  font-size:18px;

  box-shadow:
    0 10px 25px rgba(0,0,0,.04);
}

/* SCROLLBAR */

::-webkit-scrollbar{
  width:8px;
}

::-webkit-scrollbar-thumb{
  background:#cbd5e1;
  border-radius:20px;
}

/* TABLET */

@media(max-width:1200px){

  .product-grid{
    grid-template-columns:
      repeat(auto-fill,minmax(240px,1fr));
  }

}

/* MOBILE */

@media(max-width:992px){

  .container{
    grid-template-columns:1fr;
  }

  .sidebar{
    position:static;
  }

  .hero{
    height:280px;
  }

  .hero h1{
    font-size:40px;
  }

  .hero p{
    font-size:15px;
  }

}

/* SMALL MOBILE */

@media(max-width:576px){

  .hero h1{
    font-size:30px;
  }

  .content-header{
    flex-direction:column;

    align-items:flex-start;

    gap:8px;
  }

  .product-grid{
    grid-template-columns:1fr;
  }

  .image-wrapper img{
    height:250px;
  }

}

</style>

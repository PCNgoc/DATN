<script setup>
import { ref, computed, onMounted } from "vue"
import { useRouter } from "vue-router"

import { getProducts } from "@/services/productService"
import { getAllThuongHieu } from "@/services/thuongHieuService"
import { getAll as getDanhMuc } from "@/services/danhMucService"
import { getAll as getXuatXu } from "@/services/xuatXuService"
import { getAll as getChatLieu } from "@/services/chatLieuService"
import { getAll as getCoGiay } from "@/services/coGiayService"
import { getAll as getDeGiay } from "@/services/deGiayService"

const router = useRouter()

const products = ref([])

const brands = ref([])
const categories = ref([])
const origins = ref([])
const materials = ref([])
const collars = ref([])
const soles = ref([])

const loading = ref(true)

// ==========================
// FILTER
// ==========================

const keyword = ref("")
const selectedBrand = ref("")
const selectedCategory = ref("")
const selectedOrigin = ref("")
const selectedMaterial = ref("")
const selectedCollar = ref("")
const selectedSole = ref("")
const selectedGender = ref("")

const sortBy = ref("newest")

// ==========================
// FAVORITES
// ==========================

const favorites = ref([])

const loadFavorites = () => {

  favorites.value =
    JSON.parse(localStorage.getItem("favorites")) || []

}

const isFavorite = (product) => {

  return favorites.value.some(item => item.id === product.id)

}

const toggleFavorite = (product) => {

  let list =
    JSON.parse(localStorage.getItem("favorites")) || []

  const exists =
    list.some(item => item.id === product.id)

  if (exists) {

    list = list.filter(item => item.id !== product.id)

  } else {

    list.push(product)

  }

  localStorage.setItem(
    "favorites",
    JSON.stringify(list)
  )

  loadFavorites()

}

// ==========================
// LOAD FILTER
// ==========================

const loadFilters = async () => {

  try {

    const [
      th,
      dm,
      xx,
      cl,
      cg,
      dg
    ] = await Promise.all([

      getAllThuongHieu(),

      getDanhMuc(),

      getXuatXu(),

      getChatLieu(),

      getCoGiay(),

      getDeGiay()

    ])

    brands.value = th.data
    categories.value = dm.data
    origins.value = xx.data
    materials.value = cl.data
    collars.value = cg.data
    soles.value = dg.data

  } catch (e) {

    console.error(e)

  }

}

// ==========================
// LOAD PRODUCT
// ==========================

const loadProducts = async () => {

  try {

    loading.value = true

    const res = await getProducts()

    products.value = res.data

    console.log(products.value)
  } catch (e) {

    console.error(e)

  } finally {

    loading.value = false

  }

}

// ==========================
// FILTER PRODUCT
// ==========================

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

    const matchOrigin =
      !selectedOrigin.value ||
      sp.idXuatXu == selectedOrigin.value

    const matchMaterial =
      !selectedMaterial.value ||
      sp.idChatLieu == selectedMaterial.value

    const matchCollar =
      !selectedCollar.value ||
      sp.idCoGiay == selectedCollar.value

    const matchSole =
      !selectedSole.value ||
      sp.idDeGiay == selectedSole.value

    const matchGender =
      !selectedGender.value ||
      sp.gioiTinh == selectedGender.value

    return (

      matchKeyword &&

      matchBrand &&

      matchCategory &&

      matchOrigin &&

      matchMaterial &&

      matchCollar &&

      matchSole &&

      matchGender

    )

  })

})

// ==========================
// SORT
// ==========================

const displayProducts = computed(() => {

  const list = [...filteredProducts.value]

  switch (sortBy.value) {

    case "az":

      return list.sort((a, b) =>
        a.tenSanPham.localeCompare(b.tenSanPham)
      )

    case "za":

      return list.sort((a, b) =>
        b.tenSanPham.localeCompare(a.tenSanPham)
      )

    default:

      return list

  }

})

// ==========================
// DETAIL
// ==========================

const detail = (id) => {

  router.push(`/products/${id}`)

}

// ==========================
// INIT
// ==========================

onMounted(async () => {

  await loadFilters()

  await loadProducts()

  loadFavorites()

})
</script>
<template>

  <div class="product-page">


    <!-- =====================
         HERO
    ====================== -->

    <section class="hero">

      <div class="hero-overlay"></div>

      <div class="hero-content">

        <span class="hero-tag">
            GX SNEAKER
        </span>

        <h1>
          Premium Sneaker Collection
        </h1>

        <p>
          Khám phá hơn {{ displayProducts.length }} mẫu sneaker chính hãng.
        </p>

      </div>

    </section>


    <!-- =====================
         MAIN
    ====================== -->


    <div class="main-content">


      <div class="container">





        <!-- =====================
             SIDEBAR FILTER
        ====================== -->


        <aside class="sidebar">

          <h3>Bộ lọc</h3>

          <!-- Tìm kiếm -->
          <input
            v-model="keyword"
            placeholder="Tìm sản phẩm..."
            class="search-input"
          />

          <!-- Giới tính -->
          <div class="filter-group">
            <label>Giới tính</label>

            <select v-model="selectedGender">
              <option value="">Tất cả</option>
              <option value="Nam">Nam</option>
              <option value="Nữ">Nữ</option>
              <option value="Unisex">Unisex</option>
            </select>
          </div>

          <!-- Thương hiệu -->
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

          <!-- Danh mục -->
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

          <!-- Xuất xứ -->
          <div class="filter-group">
            <label>Xuất xứ</label>

            <select v-model="selectedOrigin">
              <option value="">Tất cả</option>

              <option
                v-for="item in origins"
                :key="item.id"
                :value="item.id"
              >
                {{ item.ten }}
              </option>
            </select>
          </div>

          <!-- Chất liệu -->
          <div class="filter-group">
            <label>Chất liệu</label>

            <select v-model="selectedMaterial">
              <option value="">Tất cả</option>

              <option
                v-for="item in materials"
                :key="item.id"
                :value="item.id"
              >
                {{ item.ten }}
              </option>
            </select>
          </div>

          <!-- Cổ giày -->
          <div class="filter-group">
            <label>Cổ giày</label>

            <select v-model="selectedCollar">
              <option value="">Tất cả</option>

              <option
                v-for="item in collars"
                :key="item.id"
                :value="item.id"
              >
                {{ item.ten }}
              </option>
            </select>
          </div>

          <!-- Đế giày -->
          <div class="filter-group">
            <label>Đế giày</label>

            <select v-model="selectedSole">
              <option value="">Tất cả</option>

              <option
                v-for="item in soles"
                :key="item.id"
                :value="item.id"
              >
                {{ item.ten }}
              </option>
            </select>
          </div>

          <!-- Xóa bộ lọc -->
          <button
            class="clear-filter-btn"
            @click="
      keyword='';
      selectedBrand='';
      selectedCategory='';
      selectedOrigin='';
      selectedMaterial='';
      selectedCollar='';
      selectedSole='';
      selectedGender='';
    "
          >
            Xóa bộ lọc
          </button>

        </aside>









        <!-- =====================
             PRODUCT CONTENT
        ====================== -->


        <section class="content">



          <div class="content-header">

            <div>

              <h2>

                Tất cả sản phẩm

              </h2>

              <span>

            {{ displayProducts.length }} sản phẩm

        </span>

            </div>

            <select v-model="sortBy">

              <option value="newest">

                Mới nhất

              </option>

              <option value="az">

                Tên A-Z

              </option>

              <option value="za">

                Tên Z-A

              </option>

            </select>

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

            v-else-if="displayProducts.length===0"

            class="empty-state"

          >

            Không tìm thấy sản phẩm


          </div>









          <!-- PRODUCT LIST -->


          <div

            v-else

            class="product-grid"

          >




            <div


              class="product-card"


              v-for="sp in displayProducts"


              :key="sp.id"



              @click="detail(sp.id)"


            >






              <!-- IMAGE -->

              <div class="image-wrapper">





                <img

                  :src="`/images/${sp.anhDaiDien}`"

                  :alt="sp.tenSanPham"

                />







                <!-- NÚT YÊU THÍCH -->

                <button


                  class="favorite-btn"


                  @click.stop="toggleFavorite(sp)"


                >



                  <i


                    :class="

                  isFavorite(sp)

                  ?

                  'fa-solid fa-heart active'

                  :

                  'fa-regular fa-heart'

                  "


                  ></i>



                </button>







                <div class="image-overlay">


                  Xem sản phẩm →


                </div>




              </div>









              <!-- INFO -->


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






  </div>

  <section class="service">

    <div class="service-item">

      <i class="fa-solid fa-truck-fast"></i>

      <h3>

        Miễn phí giao hàng

      </h3>

      <p>

        Đơn từ 1.000.000đ

      </p>

    </div>

    <div class="service-item">

      <i class="fa-solid fa-arrow-right-arrow-left"></i>

      <h3>

        Đổi trả

      </h3>

      <p>

        Trong vòng 30 ngày

      </p>

    </div>

    <div class="service-item">

      <i class="fa-solid fa-shield-halved"></i>

      <h3>

        Chính hãng

      </h3>

      <p>

        Cam kết 100%

      </p>

    </div>

    <div class="service-item">

      <i class="fa-solid fa-headset"></i>

      <h3>

        Hỗ trợ

      </h3>

      <p>

        24 / 7

      </p>

    </div>

  </section>
</template>
<style scoped>
/* =====================================================
   PAGE
===================================================== */

.product-page{

  min-height:100vh;

  background:#f8f9fb;

}

/* =====================================================
   HERO
===================================================== */

.hero{

  position:relative;

  height:430px;

  display:flex;

  justify-content:center;

  align-items:center;

  overflow:hidden;

  background:
    linear-gradient(
      rgba(0,0,0,.55),
      rgba(0,0,0,.55)
    ),
    url("/images/banner.jpg");

  background-size:cover;

  background-position:center;

}

.hero-overlay{

  position:absolute;

  inset:0;

  background:rgba(0,0,0,.25);

}

.hero-content{

  position:relative;

  z-index:5;

  max-width:900px;

  padding:0 20px;

  text-align:center;

  color:#fff;

}

.hero-tag{

  display:inline-block;

  padding:8px 22px;

  border:1px solid rgba(255,255,255,.35);

  border-radius:30px;

  letter-spacing:3px;

  font-size:13px;

  margin-bottom:20px;

  backdrop-filter:blur(6px);

}

.hero h1{

  font-size:62px;

  font-weight:800;

  line-height:1.1;

  margin-bottom:20px;

}

.hero p{

  font-size:18px;

  color:#ececec;

  max-width:700px;

  margin:auto;

  line-height:1.7;

}

/* =====================================================
   MAIN CONTENT
===================================================== */

.main-content{

  padding:70px 0 90px;

  background:

    linear-gradient(
      rgba(255,255,255,.55),
      rgba(255,255,255,.55)
    ),

    url("/images/home-background.jpg");

  background-size:cover;

  background-position:center;

  background-repeat:no-repeat;

  background-attachment:fixed;

}

/* =====================================================
   CONTAINER
===================================================== */

.container{

  width:95%;

  max-width:1500px;

  margin:auto;

  display:grid;

  grid-template-columns:280px 1fr;

  gap:35px;

  align-items:start;

}

/* =====================================================
   SIDEBAR
===================================================== */

.sidebar{

  width:280px;

  background:#fff;

  border-radius:20px;

  padding:24px;

  border:1px solid #ececec;

  box-shadow:0 10px 25px rgba(0,0,0,.06);

  position:sticky;

  top:90px;

  max-height:calc(100vh - 120px);

  overflow-y:auto;

  transition:.35s;

}

.sidebar h3{

  font-size:24px;

  font-weight:700;

  margin-bottom:25px;

  text-align:center;

  color:#111;

}

/* ==========================
   INPUT + SELECT
========================== */

.search-input,
.sidebar select{

  width:100%;

  height:48px;

  padding:0 15px;

  border:1px solid #ddd;

  border-radius:12px;

  background:#fafafa;

  outline:none;

  font-size:14px;

  transition:.3s;

  cursor:pointer;

  appearance:none;

}

.search-input:hover,
.sidebar select:hover{

  border-color:#111;

  background:#fff;

}

.search-input:focus,
.sidebar select:focus{

  border-color:#111;

  background:#fff;

  box-shadow:0 0 0 5px rgba(0,0,0,.05);

}

/* ==========================
   FILTER
========================== */

.filter-group{

  margin-bottom:22px;

  padding-bottom:18px;

  border-bottom:1px solid #efefef;

  animation:fadeUp .4s ease;

}

.filter-group:last-child{

  border-bottom:none;

}

.filter-group label{

  display:block;

  font-size:15px;

  font-weight:700;

  color:#111;

  margin-bottom:10px;

}

/* ==========================
   CLEAR FILTER
========================== */

.clear-filter-btn{

  width:100%;

  height:50px;

  border:none;

  border-radius:14px;

  background:#111;

  color:#fff;

  font-size:15px;

  font-weight:700;

  cursor:pointer;

  transition:.3s;

  margin-top:15px;

}

.clear-filter-btn:hover{

  background:#d62828;

  transform:translateY(-2px);

}

/* ==========================
   FILTER COUNT
========================== */

.filter-count{

  float:right;

  color:#999;

  font-size:13px;

}

/* ==========================
   SCROLLBAR
========================== */

.sidebar::-webkit-scrollbar{

  width:6px;

}

.sidebar::-webkit-scrollbar-thumb{

  background:#d8d8d8;

  border-radius:20px;

}

.sidebar::-webkit-scrollbar-thumb:hover{

  background:#999;

}

/* ==========================
   ANIMATION
========================== */

@keyframes fadeUp{

  from{

    opacity:0;

    transform:translateY(12px);

  }

  to{

    opacity:1;

    transform:translateY(0);

  }

}
/* =====================================================
   CONTENT
===================================================== */

.content{

  display:flex;

  flex-direction:column;

}

/* =====================================================
   CONTENT HEADER
===================================================== */

.content-header{

  display:flex;

  justify-content:space-between;

  align-items:center;

  background:#fff;

  padding:24px 30px;

  border-radius:20px;

  margin-bottom:35px;

  box-shadow:
    0 12px 35px rgba(0,0,0,.06);

}

.content-header h2{

  font-size:34px;

  font-weight:800;

  color:#111;

}

.content-header span{

  color:#777;

  font-size:15px;

}

.content-header select{

  width:190px;

  height:46px;

  border:1px solid #ddd;

  border-radius:12px;

  padding:0 15px;

  outline:none;

  background:#fff;

  transition:.3s;

  cursor:pointer;

}

.content-header select:hover{

  border-color:#111;

}

.content-header select:focus{

  border-color:#111;

  box-shadow:0 0 0 4px rgba(0,0,0,.05);

}

/* =====================================================
   PRODUCT GRID
===================================================== */

.product-grid{

  display:grid;

  grid-template-columns:repeat(auto-fill,minmax(280px,1fr));

  gap:30px;

}

/* =====================================================
   PRODUCT CARD
===================================================== */

.product-card{

  background:#fff;

  border-radius:24px;

  overflow:hidden;

  cursor:pointer;

  transition:.35s;

  border:1px solid #eee;

  box-shadow:0 8px 20px rgba(0,0,0,.05);

}

.product-card:hover{

  transform:translateY(-10px);

  box-shadow:0 18px 40px rgba(0,0,0,.12);

}

/* =====================================================
   IMAGE
===================================================== */

.image-wrapper{

  position:relative;

  overflow:hidden;

  background:#fafafa;

}

.image-wrapper img{

  width:100%;

  height:300px;

  object-fit:cover;

  transition:.45s;

}

.product-card:hover img{

  transform:scale(1.08);

}

/* =====================================================
   FAVORITE BUTTON
===================================================== */

.favorite-btn{

  position:absolute;

  top:15px;

  right:15px;

  width:44px;

  height:44px;

  border:none;

  border-radius:50%;

  background:#fff;

  display:flex;

  justify-content:center;

  align-items:center;

  cursor:pointer;

  transition:.3s;

  z-index:5;

  box-shadow:0 8px 18px rgba(0,0,0,.15);

}

.favorite-btn:hover{

  transform:scale(1.08);

}

.favorite-btn i{

  font-size:20px;

  color:#666;

  transition:.3s;

}

.favorite-btn .active{

  color:#e53935;

}

/* =====================================================
   IMAGE OVERLAY
===================================================== */

.image-overlay{

  position:absolute;

  inset:0;

  display:flex;

  justify-content:center;

  align-items:center;

  color:#fff;

  font-size:17px;

  font-weight:700;

  letter-spacing:.5px;

  opacity:0;

  transition:.35s;

  background:
    linear-gradient(
      rgba(0,0,0,.15),
      rgba(0,0,0,.6)
    );

}

.product-card:hover .image-overlay{

  opacity:1;

}

/* =====================================================
   PRODUCT INFO
===================================================== */

.product-info{

  padding:22px;

}

.brand{

  display:inline-block;

  font-size:12px;

  font-weight:700;

  text-transform:uppercase;

  color:#999;

  letter-spacing:1px;

  margin-bottom:10px;

}

.product-info h4{

  font-size:20px;

  font-weight:700;

  color:#111;

  line-height:1.4;

  margin-bottom:10px;

  min-height:58px;

  transition:.3s;

}

.product-card:hover h4{

  color:#d62828;

}

.product-info p{

  color:#777;

  font-size:14px;

  margin-bottom:15px;

}

.gender-tag{

  display:inline-flex;

  align-items:center;

  justify-content:center;

  padding:8px 16px;

  border-radius:999px;

  background:#f3f4f6;

  color:#444;

  font-size:12px;

  font-weight:700;

}
/* =====================================================
   LOADING
===================================================== */

.skeleton-card{

  height:430px;

  border-radius:22px;

  overflow:hidden;

  background:
    linear-gradient(
      90deg,
      #ececec 25%,
      #f7f7f7 50%,
      #ececec 75%
    );

  background-size:400% 100%;

  animation:skeleton-loading 1.2s linear infinite;

}

@keyframes skeleton-loading{

  0%{

    background-position:100% 0;

  }

  100%{

    background-position:-100% 0;

  }

}

/* =====================================================
   EMPTY
===================================================== */

.empty-state{

  background:#fff;

  border-radius:20px;

  padding:90px 30px;

  text-align:center;

  border:1px solid #ececec;

  box-shadow:
    0 12px 30px rgba(0,0,0,.05);

}

.empty-state h3{

  font-size:28px;

  margin-bottom:15px;

  color:#111;

}

.empty-state p{

  color:#777;

  font-size:16px;

}

/* =====================================================
   SERVICE
===================================================== */

.service{

  width:95%;

  max-width:1500px;

  margin:80px auto;

  display:grid;

  grid-template-columns:repeat(4,1fr);

  gap:28px;

}

.service-item{

  background:#fff;

  border-radius:22px;

  padding:40px 25px;

  text-align:center;

  border:1px solid #ececec;

  transition:.35s;

  box-shadow:
    0 12px 30px rgba(0,0,0,.05);

}

.service-item:hover{

  transform:translateY(-8px);

  box-shadow:
    0 18px 40px rgba(0,0,0,.12);

}

.service-item i{

  font-size:38px;

  color:#111;

  margin-bottom:20px;

}

.service-item h3{

  font-size:20px;

  font-weight:700;

  margin-bottom:10px;

}

.service-item p{

  color:#666;

  line-height:1.7;

  font-size:15px;

}

/* =====================================================
   GLOBAL SCROLLBAR
===================================================== */

::-webkit-scrollbar{

  width:8px;

}

::-webkit-scrollbar-thumb{

  background:#cfcfcf;

  border-radius:30px;

}

::-webkit-scrollbar-thumb:hover{

  background:#999;

}

/* =====================================================
   RESPONSIVE
===================================================== */

@media(max-width:1200px){

  .container{

    grid-template-columns:260px 1fr;

    gap:25px;

  }

}

@media(max-width:992px){

  .container{

    grid-template-columns:1fr;

  }

  .sidebar{

    width:100%;

    position:static;

    max-height:none;

    overflow:visible;

    margin-bottom:30px;

  }

  .content-header{

    flex-direction:column;

    align-items:flex-start;

    gap:18px;

  }

  .content-header select{

    width:100%;

  }

  .service{

    grid-template-columns:repeat(2,1fr);

  }

  .hero{

    height:360px;

  }

  .hero h1{

    font-size:44px;

  }

  .hero p{

    font-size:16px;

  }

}

@media(max-width:768px){

  .product-grid{

    grid-template-columns:
        repeat(auto-fill,minmax(240px,1fr));

  }

  .sidebar{

    padding:20px;

  }

}

@media(max-width:576px){

  .hero{

    height:290px;

  }

  .hero-tag{

    font-size:11px;

  }

  .hero h1{

    font-size:30px;

  }

  .hero p{

    font-size:15px;

  }

  .product-grid{

    grid-template-columns:1fr;

  }

  .content-header{

    padding:18px;

  }

  .content-header h2{

    font-size:24px;

  }

  .service{

    grid-template-columns:1fr;

  }

  .service-item{

    padding:28px 18px;

  }

}
</style>

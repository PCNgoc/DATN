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



// tìm kiếm + lọc

const keyword = ref("")
const selectedBrand = ref("")
const selectedCategory = ref("")
const sortBy = ref("newest")



// ==========================
// YÊU THÍCH
// ==========================

const favorites = ref([])



// load danh sách yêu thích

const loadFavorites = ()=>{

  favorites.value =
    JSON.parse(
      localStorage.getItem("favorites")
    ) || []

}




// kiểm tra sản phẩm đã thích chưa

const isFavorite = (product)=>{

  return favorites.value.some(
    item=>item.id === product.id
  )

}



// thêm / xóa yêu thích

const toggleFavorite = (product)=>{


  let list =
    JSON.parse(
      localStorage.getItem("favorites")
    ) || []



  const exists =
    list.some(
      item=>item.id === product.id
    )



  if(exists){


    list =
      list.filter(
        item=>item.id !== product.id
      )


  }else{


    list.push(product)


  }



  localStorage.setItem(
    "favorites",
    JSON.stringify(list)
  )


  loadFavorites()

}






// ==========================
// LOAD DATA
// ==========================


const loadData = async ()=>{


  try{


    loading.value=true



    const [
      sp,
      th,
      dm
    ] = await Promise.all([

      getProducts(),

      getAllThuongHieu(),

      getDanhMuc()

    ])



    products.value = sp.data

    brands.value = th.data

    categories.value = dm.data



  }catch(error){

    console.error(error)

  }

  finally{

    loading.value=false

  }


}






// ==========================
// FILTER
// ==========================


const filteredProducts = computed(()=>{


  return products.value.filter(sp=>{


    const matchKeyword =

      !keyword.value ||

      sp.tenSanPham
        ?.toLowerCase()
        .includes(
          keyword.value.toLowerCase()
        )



    const matchBrand =

      !selectedBrand.value ||

      sp.idThuongHieu ==
      selectedBrand.value




    const matchCategory =

      !selectedCategory.value ||

      sp.idDanhMuc ==
      selectedCategory.value




    return (

      matchKeyword &&

      matchBrand &&

      matchCategory

    )


  })


})
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


const detail=(id)=>{


  router.push(
    `/products/${id}`
  )


}




onMounted(()=>{


  loadData()

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


          <h3>
            Bộ lọc
          </h3>



          <input

            v-model="keyword"

            placeholder="Tìm sản phẩm..."

            class="search-input"

          />




          <div class="filter-group">


            <label>
              Thương hiệu
            </label>



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


            <label>
              Danh mục
            </label>



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

            v-else-if="filteredProducts.length===0"

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

  height:fit-content;

  position:sticky;

  top:100px;

  align-self:start;

}
.sidebar h3{

  font-size:24px;

  font-weight:700;

  margin-bottom:25px;

  color:#111;

}

.search-input,
.sidebar select{

  width:100%;

  height:48px;

  padding:0 15px;

  border:1px solid #ddd;

  border-radius:12px;

  outline:none;

  font-size:14px;

  background:#fff;

  transition:.3s;

  margin-bottom:20px;

}

.search-input:focus,
.sidebar select:focus{

  border-color:#111;

  box-shadow:
    0 0 0 4px rgba(0,0,0,.05);

}

.search-input:hover,
.sidebar select:hover{

  border-color:#111;

}

.filter-group{

  margin-bottom:22px;

}

.filter-group label{

  display:block;

  font-weight:600;

  margin-bottom:10px;

  color:#333;

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

  background:white;

  padding:24px 30px;

  border-radius:20px;

  margin-bottom:35px;

  box-shadow:

    0 12px 35px rgba(0,0,0,.06);

}

.content-header h2{

  font-size:34px;

  font-weight:800;

}

.content-header span{

  color:#888;

  font-size:15px;

}

.content-header select{

  width:180px;

  height:46px;

  border:1px solid #ddd;

  border-radius:12px;

  padding:0 12px;

  outline:none;

  cursor:pointer;

  transition:.3s;

}

.content-header select:hover{

  border-color:#111;

}

.content-header select:focus{

  border-color:#111;

}

/* =====================================================
   PRODUCT GRID
===================================================== */

.product-grid{

  display:grid;

  grid-template-columns:
        repeat(auto-fill,minmax(280px,1fr));

  gap:32px;

}

/* =====================================================
   PRODUCT CARD
===================================================== */

.product-card{

  background:white;

  border-radius:24px;

  overflow:hidden;

  transition:.35s;

  box-shadow:

    0 8px 20px rgba(0,0,0,.05);

  border:none;

}
.product-card:hover{

  transform:translateY(-12px);

  box-shadow:

    0 20px 45px rgba(0,0,0,.12);

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

  transform:scale(1.05);

}

/* =====================================================
   FAVORITE BUTTON
===================================================== */

.favorite-btn{

  position:absolute;

  top:16px;

  right:16px;

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

  box-shadow:
    0 8px 18px rgba(0,0,0,.15);

}

.favorite-btn:hover{

  transform:scale(1.08);

}

.favorite-btn i{

  font-size:20px;

  color:#555;

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

  background:
    linear-gradient(
      rgba(0,0,0,.15),
      rgba(0,0,0,.55)
    );

  color:#fff;

  display:flex;

  justify-content:center;

  align-items:center;

  opacity:0;

  transition:.35s;

  font-size:17px;

  font-weight:700;

  letter-spacing:.5px;

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

  color:#888;

  letter-spacing:1px;

  margin-bottom:10px;

}

.product-info h4{

  font-size:20px;

  font-weight:700;

  color:#111;

  line-height:1.4;

  margin-bottom:10px;

  min-height:56px;

  transition:.3s;

}

.product-card:hover h4{

  color:#e53935;

}

.product-info p{

  font-size:14px;

  color:#777;

  margin-bottom:18px;

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
      #f6f6f6 50%,
      #ececec 75%
    );

  background-size:400% 100%;

  animation:skeleton-loading 1.3s infinite linear;

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

  padding:100px 30px;

  text-align:center;

  border:1px solid #ececec;

  box-shadow:
    0 12px 30px rgba(0,0,0,.05);

  color:#777;

  font-size:18px;

  font-weight:600;

}

/* =====================================================
   SERVICE
===================================================== */

.service{

  width:95%;

  max-width:1500px;

  margin:90px auto 70px;

  display:grid;

  grid-template-columns:repeat(4,1fr);

  gap:28px;

}

.service-item{

  background:#fff;

  border-radius:20px;

  padding:38px 25px;

  text-align:center;

  border:1px solid #ececec;

  transition:.35s;

  box-shadow:
    0 12px 30px rgba(0,0,0,.05);

}

.service-item:hover{

  transform:translateY(-8px);

  box-shadow:
    0 20px 45px rgba(0,0,0,.12);

}

.service-item i{

  font-size:36px;

  color:#111;

  margin-bottom:18px;

}

.service-item h3{

  font-size:20px;

  margin-bottom:12px;

  color:#111;

}

.service-item p{

  color:#666;

  line-height:1.7;

}

/* =====================================================
   SCROLLBAR
===================================================== */

::-webkit-scrollbar{

  width:8px;

}

::-webkit-scrollbar-thumb{

  background:#d4d4d4;

  border-radius:20px;

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

    gap:30px;

  }

}

@media(max-width:992px){

  .container{

    grid-template-columns:1fr;

  }

  .sidebar{

    position:static;

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

    font-size:42px;

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

}

@media(max-width:576px){

  .hero{

    height:300px;

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

    padding:30px 20px;

  }

}
</style>

<script setup>
import { ref, computed, onMounted, watch } from "vue"
import { useRoute, useRouter } from "vue-router"

import {
  getById as getProductById
} from "@/services/SanPhamService"

import {
  getBySanPham
} from "@/services/ChiTietSanPhamService"

const route = useRoute()
const router = useRouter()

const loading = ref(true)

const product = ref({})

const details = ref([])

const selectedColor = ref(null)

const selectedSize = ref(null)

const quantity = ref(1)

const activeImage = ref("")

const activeTab = ref("overview")

const relatedProducts = ref([])

/* ===========================
   FORMAT
=========================== */

const formatMoney = (value) => {

  if (!value) return "0"

  return Number(value).toLocaleString("vi-VN")
}

/* ===========================
   LOAD DATA
=========================== */

const loadProduct = async () => {

  try {

    loading.value = true

    const id = route.params.id

    const productRes = await getProductById(id)

    product.value = productRes.data

    if (product.value?.anhDaiDien) {

      activeImage.value =
        `/images/${product.value.anhDaiDien}`
    }

    const detailRes = await getBySanPham(id)

    details.value = detailRes.data || []

    if (details.value.length) {

      selectedColor.value =
        details.value[0].idMauSac
    }

  } catch (error) {

    console.error(error)

  } finally {

    loading.value = false
  }
}

/* ===========================
   COLORS
=========================== */

const colors = computed(() => {

  const map = new Map()

  details.value.forEach(item => {

    if (!map.has(item.idMauSac)) {

      map.set(item.idMauSac, {
        id: item.idMauSac,
        name: item.tenMauSac
      })
    }
  })

  return [...map.values()]
})

/* ===========================
   SIZES
=========================== */

/* ===========================
   ALL SIZE 35 -> 44
=========================== */

const allSizes = computed(() => {

  const result = []

  for (let i = 35; i <= 44; i++) {

    result.push(i)

  }

  return result
})

const getSizeInfo = (size) => {

  return details.value.find(

    item =>

      item.idMauSac === selectedColor.value &&

      Number(item.size) === Number(size)

  )
}

/* ===========================
   AUTO SELECT SIZE
=========================== */

watch(
  selectedColor,
  () => {

    quantity.value = 1

    selectedSize.value = null

    const firstAvailable = details.value.find(
      item =>
        item.idMauSac === selectedColor.value &&
        item.soLuongTon > 0
    )

    if (firstAvailable) {
      selectedSize.value =
        firstAvailable.idKichThuoc
    }
  },
  {
    immediate: true
  }
)

/* ===========================
   SELECTED VARIANT
=========================== */

const selectedVariant = computed(() => {

  return details.value.find(
    item =>
      item.idMauSac === selectedColor.value &&
      item.idKichThuoc === selectedSize.value
  )
})

/* ===========================
   PRICE
=========================== */

const displayPrice = computed(() => {

  if (selectedVariant.value) {

    return formatMoney(
      selectedVariant.value.giaBan
    )
  }

  if (!details.value.length) {

    return "0"
  }

  return formatMoney(
    details.value[0].giaBan
  )
})

const minPrice = computed(() => {

  if (!details.value.length) {

    return 0
  }

  return Math.min(
    ...details.value.map(
      item => Number(item.giaBan)
    )
  )
})

const maxPrice = computed(() => {

  if (!details.value.length) {

    return 0
  }

  return Math.max(
    ...details.value.map(
      item => Number(item.giaBan)
    )
  )
})

/* ===========================
   STOCK
=========================== */

const stock = computed(() => {

  return selectedVariant.value
    ?.soLuongTon || 0
})

const totalStock = computed(() => {

  return details.value.reduce(
    (sum, item) =>
      sum + (item.soLuongTon || 0),
    0
  )
})

/* ===========================
   QUANTITY
=========================== */

const increaseQuantity = () => {

  if (quantity.value < stock.value) {

    quantity.value++
  }
}

const decreaseQuantity = () => {

  if (quantity.value > 1) {

    quantity.value--
  }
}

watch(stock, () => {

  if (quantity.value > stock.value) {

    quantity.value = 1
  }
})

/* ===========================
   ADD TO CART
=========================== */

const addToCart = () => {

  if (!selectedVariant.value) {

    alert(
      "Vui lòng chọn màu sắc và kích thước"
    )

    return
  }

  const cart =
    JSON.parse(
      localStorage.getItem("cart")
    ) || []

  cart.push({

    productId: product.value.id,

    detailId:
    selectedVariant.value.id,

    productName:
    product.value.tenSanPham,

    image:
    product.value.anhDaiDien,

    color:
    selectedVariant.value.tenMauSac,

    size:
    selectedVariant.value.size,

    quantity:
    quantity.value,

    price:
    selectedVariant.value.giaBan
  })

  localStorage.setItem(
    "cart",
    JSON.stringify(cart)
  )

  alert(
    "Đã thêm sản phẩm vào giỏ hàng"
  )
}

/* ===========================
   BUY NOW
=========================== */

const buyNow = () => {

  addToCart()

  router.push("/cart")
}

/* ===========================
   RELATED PRODUCTS
=========================== */

const loadRelatedProducts =
  async () => {

    try {

      relatedProducts.value = []

    } catch (error) {

      console.error(error)
    }
  }

/* ===========================
   IMAGE
=========================== */

const changeImage = image => {

  activeImage.value = image
}

/* ===========================
   TABS
=========================== */

const changeTab = tab => {

  activeTab.value = tab
}

/* ===========================
   MOUNTED
=========================== */

onMounted(async () => {

  await loadProduct()

  await loadRelatedProducts()
})
</script>
<template>
  <div class="product-detail-page">

    <!-- LOADING -->
    <div v-if="loading" class="loading-container">
      <div class="loader"></div>
      <p>Đang tải sản phẩm...</p>
    </div>

    <template v-else>

      <!-- BREADCRUMB -->
      <div class="breadcrumb">

        <span @click="$router.push('/')">
          Trang chủ
        </span>

        <span>/</span>

        <span>
          {{ product.tenDanhMuc }}
        </span>

        <span>/</span>

        <b>
          {{ product.tenSanPham }}
        </b>

      </div>

      <!-- MAIN -->
      <div class="detail-container">

        <!-- LEFT -->
        <div class="gallery-section">

          <div class="main-image-wrapper">

            <img
              :src="activeImage"
              class="main-image"
              alt=""
            />

            <div class="image-badge">
              HOT
            </div>

          </div>

          <div class="thumbnail-list">

            <div
              class="thumbnail active"
            >
              <img
                :src="activeImage"
                alt=""
              />
            </div>

          </div>

        </div>

        <!-- RIGHT -->
        <div class="info-panel">

          <!-- BRAND -->
          <div class="brand">
            {{ product.tenThuongHieu }}
          </div>

          <!-- TITLE -->
          <h1 class="product-title">
            {{ product.tenSanPham }}
          </h1>

          <!-- BADGES -->
          <div class="badges">

            <span class="badge brand-badge">
              {{ product.tenThuongHieu }}
            </span>

            <span class="badge stock-badge">
              {{ totalStock }} sản phẩm
            </span>

            <span class="badge hot-badge">
              BÁN CHẠY
            </span>

          </div>

          <!-- RATING -->
          <div class="rating">

            <span class="stars">
              ★★★★★
            </span>

            <span class="rating-text">
              4.9 (120 đánh giá)
            </span>

          </div>

          <!-- PRICE -->
          <div class="price-box">

            <div class="current-price">
              {{ displayPrice }} đ
            </div>

            <div
              v-if="minPrice !== maxPrice"
              class="price-range"
            >
              Giá từ
              {{ formatMoney(minPrice) }}
              -
              {{ formatMoney(maxPrice) }} đ
            </div>

          </div>

          <!-- STOCK -->
          <div class="stock-section">

            <span
              v-if="stock > 0"
              class="in-stock"
            >
              ✔ Còn hàng ({{ stock }})
            </span>

            <span
              v-else
              class="out-stock"
            >
              Hết hàng
            </span>

          </div>

          <!-- COLORS -->
          <div class="option-group">

            <h4>
              Màu sắc
            </h4>

            <div class="color-list">

              <button
                v-for="color in colors"
                :key="color.id"
                class="color-btn"
                :class="{
                  active:
                    selectedColor === color.id
                }"
                @click="
                  selectedColor = color.id
                "
              >
                {{ color.name }}
              </button>

            </div>

          </div>

          <!-- SIZE -->
          <div class="option-group">

            <h4>
              Kích thước
            </h4>

            <div class="size-list">

              <button

                v-for="size in allSizes"

                :key="size"

                class="size-btn"

                :class="{
  active:
    selectedSize === getSizeInfo(size)?.idKichThuoc,

  disabled:
    !getSizeInfo(size) ||
    getSizeInfo(size)?.soLuongTon <= 0
}"

                :disabled="
      !getSizeInfo(size) ||
      getSizeInfo(size)?.soLuongTon <= 0
    "

                @click="
      selectedSize =
      getSizeInfo(size)?.idKichThuoc
    "

              >

                <div class="size-number">
                  {{ size }}
                </div>

                <small
                  v-if="getSizeInfo(size)"
                >
                  {{
                    getSizeInfo(size).soLuongTon > 0
                      ? `Còn ${getSizeInfo(size).soLuongTon}`
                      : 'Hết hàng'
                  }}
                </small>

              </button>

            </div>

          </div>

          <!-- QUANTITY -->
          <div class="option-group">

            <h4>
              Số lượng
            </h4>

            <div class="quantity-box">

              <button
                @click="decreaseQuantity"
              >
                -
              </button>

              <input
                v-model="quantity"
                type="number"
              />

              <button
                @click="increaseQuantity"
              >
                +
              </button>

            </div>

          </div>

          <!-- ACTIONS -->
          <div class="action-buttons">

            <button
              class="btn-cart"
              @click="addToCart"
            >
              🛒 Thêm vào giỏ
            </button>

            <button
              class="btn-buy"
              @click="buyNow"
            >
              MUA NGAY
            </button>

          </div>

        </div>

      </div>

      <!-- TABS -->
      <div class="tabs">

        <button
          :class="{
            active:
              activeTab === 'overview'
          }"
          @click="
            changeTab('overview')
          "
        >
          Mô tả
        </button>

        <button
          :class="{
            active:
              activeTab === 'info'
          }"
          @click="
            changeTab('info')
          "
        >
          Thông tin
        </button>

        <button
          :class="{
            active:
              activeTab === 'variants'
          }"
          @click="
            changeTab('variants')
          "
        >
          Phiên bản
        </button>

      </div>

      <!-- OVERVIEW -->
      <div
        v-if="activeTab === 'overview'"
        class="content-card"
      >

        <h2>
          Mô tả sản phẩm
        </h2>

        <p>
          {{ product.moTaChiTiet }}
        </p>

      </div>

      <!-- INFO -->
      <div
        v-if="activeTab === 'info'"
        class="content-card"
      >

        <div class="info-grid">

          <div class="info-item">

            <span>
              Thương hiệu
            </span>

            <strong>
              {{ product.tenThuongHieu }}
            </strong>

          </div>

          <div class="info-item">

            <span>
              Danh mục
            </span>

            <strong>
              {{ product.tenDanhMuc }}
            </strong>

          </div>

          <div class="info-item">

            <span>
              Chất liệu
            </span>

            <strong>
              {{ product.tenChatLieu }}
            </strong>

          </div>

          <div class="info-item">

            <span>
              Xuất xứ
            </span>

            <strong>
              {{ product.tenXuatXu }}
            </strong>

          </div>

          <div class="info-item">

            <span>
              Giới tính
            </span>

            <strong>
              {{ product.gioiTinh }}
            </strong>

          </div>

        </div>

      </div>

      <!-- VARIANTS -->
      <div
        v-if="activeTab === 'variants'"
        class="content-card"
      >

        <table class="variant-table">

          <thead>

          <tr>
            <th>Màu</th>
            <th>Size</th>
            <th>Giá</th>
            <th>Tồn</th>
          </tr>

          </thead>

          <tbody>

          <tr
            v-for="item in details"
            :key="item.id"
          >

            <td>
              {{ item.tenMauSac }}
            </td>

            <td>
              {{ item.size }}
            </td>

            <td>
              {{ formatMoney(item.giaBan) }} đ
            </td>

            <td>
              {{ item.soLuongTon }}
            </td>

          </tr>

          </tbody>

        </table>

      </div>

      <!-- RELATED -->
      <section class="related-section">

        <h2>
          Có thể bạn sẽ thích
        </h2>

        <div class="related-grid">

          <div
            v-for="item in relatedProducts"
            :key="item.id"
            class="related-card"
          >

            <img
              :src="`/images/${item.anhDaiDien}`"
              alt=""
            />

            <h4>
              {{ item.tenSanPham }}
            </h4>

          </div>

        </div>

      </section>

    </template>

  </div>
</template>
<style scoped>
.product-detail-page{
  background:#f8fafc;
  min-height:100vh;
  padding-bottom:80px;
}

/* =========================
   LOADING
========================= */

.loading-container{
  min-height:70vh;
  display:flex;
  flex-direction:column;
  justify-content:center;
  align-items:center;
  gap:20px;
}

.loader{
  width:50px;
  height:50px;
  border:4px solid #e2e8f0;
  border-top:4px solid #111827;
  border-radius:50%;
  animation:spin 1s linear infinite;
}

@keyframes spin{
  to{
    transform:rotate(360deg);
  }
}

/* =========================
   BREADCRUMB
========================= */

.breadcrumb{
  width:90%;
  max-width:1400px;
  margin:auto;
  padding:30px 0;
  display:flex;
  gap:10px;
  align-items:center;
  color:#64748b;
  font-size:14px;
}

.breadcrumb span{
  cursor:pointer;
}

.breadcrumb span:hover{
  color:#111827;
}

/* =========================
   MAIN
========================= */

.detail-container{
  width:90%;
  max-width:1400px;
  margin:auto;

  display:grid;
  grid-template-columns:1fr 1fr;
  gap:60px;

  animation:fadeUp .5s ease;
}

@keyframes fadeUp{
  from{
    opacity:0;
    transform:translateY(20px);
  }
  to{
    opacity:1;
    transform:translateY(0);
  }
}

/* =========================
   GALLERY
========================= */

.gallery-section{
  position:sticky;
  top:20px;
  align-self:start;
}

.main-image-wrapper{
  position:relative;
  background:white;
  border-radius:24px;
  overflow:hidden;
  box-shadow:0 20px 60px rgba(0,0,0,.08);
}

.main-image{
  width:100%;
  display:block;
  object-fit:cover;
  transition:.4s;
}

.main-image:hover{
  transform:scale(1.03);
}

.image-badge{
  position:absolute;
  top:20px;
  left:20px;

  background:#ef4444;
  color:white;

  padding:8px 16px;
  border-radius:999px;

  font-size:12px;
  font-weight:700;
}

.thumbnail-list{
  display:flex;
  gap:15px;
  margin-top:20px;
}

.thumbnail{
  width:90px;
  height:90px;

  border-radius:16px;
  overflow:hidden;

  border:2px solid #2563eb;
}

.thumbnail img{
  width:100%;
  height:100%;
  object-fit:cover;
}

/* =========================
   INFO PANEL
========================= */

.info-panel{
  background:white;
  border-radius:24px;
  padding:40px;
  box-shadow:0 20px 60px rgba(0,0,0,.05);
}

.brand{
  color:#2563eb;
  font-weight:700;
  letter-spacing:1px;
  text-transform:uppercase;
}

.product-title{
  font-size:42px;
  line-height:1.2;
  margin:15px 0;
  color:#111827;
}

/* =========================
   BADGES
========================= */

.badges{
  display:flex;
  gap:10px;
  flex-wrap:wrap;
  margin-top:20px;
}

.badge{
  padding:8px 14px;
  border-radius:999px;
  font-size:12px;
  font-weight:600;
}

.brand-badge{
  background:#dbeafe;
  color:#1d4ed8;
}

.stock-badge{
  background:#dcfce7;
  color:#15803d;
}

.hot-badge{
  background:#fee2e2;
  color:#dc2626;
}

/* =========================
   RATING
========================= */

.rating{
  margin-top:25px;
  display:flex;
  gap:10px;
  align-items:center;
}

.stars{
  color:#f59e0b;
  font-size:20px;
}

.rating-text{
  color:#64748b;
}

/* =========================
   PRICE
========================= */

.price-box{
  margin-top:30px;
}

.current-price{
  font-size:40px;
  font-weight:800;
  color:#111827;
}

.price-range{
  margin-top:8px;
  color:#64748b;
}

/* =========================
   STOCK
========================= */

.stock-section{
  margin-top:25px;
}

.in-stock{
  color:#16a34a;
  font-weight:600;
}

.out-stock{
  color:#dc2626;
  font-weight:600;
}

/* =========================
   OPTIONS
========================= */

.option-group{
  margin-top:35px;
}

.option-group h4{
  margin-bottom:15px;
  color:#111827;
}

.color-list,
.size-list{
  display:flex;
  gap:12px;
  flex-wrap:wrap;
}

.color-btn,
.size-btn{
  border:none;
  background:#f1f5f9;

  padding:12px 18px;

  border-radius:14px;

  cursor:pointer;

  font-weight:600;

  transition:.3s;
}

.color-btn:hover,
.size-btn:hover{
  transform:translateY(-2px);
}

.color-btn.active,
.size-btn.active{
  background:#111827;
  color:white;
}

/* =========================
   QUANTITY
========================= */

.quantity-box{
  display:flex;
  align-items:center;
  gap:10px;
}

.quantity-box button{
  width:45px;
  height:45px;
  border:none;
  border-radius:12px;
  cursor:pointer;
  font-size:18px;
  font-weight:700;
}

.quantity-box input{
  width:80px;
  text-align:center;
  height:45px;

  border:1px solid #cbd5e1;
  border-radius:12px;
}

/* =========================
   ACTIONS
========================= */

.action-buttons{
  display:flex;
  gap:15px;
  margin-top:40px;
}

.btn-cart,
.btn-buy{
  flex:1;

  height:58px;

  border:none;
  border-radius:16px;

  cursor:pointer;

  font-weight:700;
  font-size:15px;

  transition:.3s;
}

.btn-cart{
  background:white;
  border:2px solid #111827;
}

.btn-cart:hover{
  background:#111827;
  color:white;
}

.btn-buy{
  color:white;
  background:linear-gradient(
    135deg,
    #2563eb,
    #7c3aed
  );
}

.btn-buy:hover{
  transform:translateY(-3px);
  box-shadow:0 20px 40px rgba(37,99,235,.35);
}

/* =========================
   TABS
========================= */

.tabs{
  width:90%;
  max-width:1400px;

  margin:80px auto 30px;

  display:flex;
  gap:12px;
}

.tabs button{
  border:none;
  background:#e2e8f0;

  padding:14px 28px;

  border-radius:14px;

  cursor:pointer;

  font-weight:600;

  transition:.3s;
}

.tabs button:hover{
  transform:translateY(-2px);
}

.tabs button.active{
  background:#111827;
  color:white;
}

/* =========================
   CONTENT
========================= */

.content-card{
  width:90%;
  max-width:1400px;

  margin:auto;

  background:white;

  border-radius:24px;

  padding:40px;

  box-shadow:0 10px 30px rgba(0,0,0,.05);
}

.content-card h2{
  margin-bottom:20px;
}

.content-card p{
  line-height:1.8;
  color:#475569;
}

/* =========================
   INFO GRID
========================= */

.info-grid{
  display:grid;
  grid-template-columns:repeat(2,1fr);
  gap:20px;
}

.info-item{
  background:#f8fafc;
  padding:20px;
  border-radius:16px;
  transition:.3s;
}

.info-item:hover{
  transform:translateY(-5px);
  box-shadow:0 12px 24px rgba(0,0,0,.08);
}

.info-item span{
  display:block;
  color:#64748b;
  margin-bottom:8px;
}

/* =========================
   TABLE
========================= */

.variant-table{
  width:100%;
  border-collapse:collapse;
}

.variant-table th{
  background:#111827;
  color:white;
  padding:15px;
}

.variant-table td{
  padding:15px;
  text-align:center;
  border-bottom:1px solid #e5e7eb;
}

.variant-table tr:hover{
  background:#f8fafc;
}

/* =========================
   RELATED
========================= */

.related-section{
  width:90%;
  max-width:1400px;
  margin:80px auto 0;
}

.related-section h2{
  margin-bottom:30px;
}

.related-grid{
  display:grid;
  grid-template-columns:
    repeat(auto-fit,minmax(250px,1fr));

  gap:25px;
}

.related-card{
  background:white;
  border-radius:20px;
  overflow:hidden;
  cursor:pointer;

  transition:.35s;

  box-shadow:0 10px 25px rgba(0,0,0,.05);
}

.related-card:hover{
  transform:translateY(-8px);
}

.related-card img{
  width:100%;
  height:250px;
  object-fit:cover;
}

.related-card h4{
  padding:20px;
}

/* =========================
   RESPONSIVE
========================= */

@media (max-width:1024px){

  .detail-container{
    grid-template-columns:1fr;
  }

  .gallery-section{
    position:static;
  }

  .product-title{
    font-size:32px;
  }
}

@media (max-width:768px){

  .product-title{
    font-size:26px;
  }

  .current-price{
    font-size:28px;
  }

  .action-buttons{
    flex-direction:column;
  }

  .tabs{
    overflow:auto;
  }

  .info-grid{
    grid-template-columns:1fr;
  }

  .content-card{
    padding:25px;
  }
}

@media (max-width:480px){

  .info-panel{
    padding:25px;
  }

  .breadcrumb{
    font-size:12px;
  }

  .color-btn,
  .size-btn{
    width:100%;
  }

  .quantity-box{
    width:100%;
  }

  .quantity-box input{
    flex:1;
  }
}
/* =========================
   SIZE LIST (COMPACT)
========================= */

.size-list{
  display:grid;
  grid-template-columns:repeat(5, 58px);
  gap:10px;
}

.size-btn{

  width:58px;
  height:58px;

  padding:4px;

  display:flex;
  flex-direction:column;
  justify-content:center;
  align-items:center;

  border:1px solid #e5e7eb;
  border-radius:10px;

  background:#fff;

  cursor:pointer;

  transition:.25s ease;

}

.size-btn:hover:not(.disabled){

  border-color:#111827;

  transform:translateY(-2px);

}

.size-number{

  font-size:14px;
  font-weight:700;
  line-height:1;

}

.size-btn small{

  font-size:9px;
  margin-top:3px;
  color:#64748b;
  line-height:1;

}

.size-btn.active{

  background:#111827;
  color:#fff;
  border-color:#111827;

}

.size-btn.active small{

  color:#fff;

}

.size-btn.disabled{

  opacity:.45;
  cursor:not-allowed;
  position:relative;

}

.size-btn.disabled::after{

  content:"";

  position:absolute;

  width:70%;
  height:1px;

  background:#cbd5e1;

  transform:rotate(-20deg);

}
</style>

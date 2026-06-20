<script setup>

import { ref, computed, onMounted } from "vue"
import { useRoute } from "vue-router"

import { getProductById } from "@/services/productService"
import { getBySanPham } from "@/services/chiTietSanPhamService"

const route = useRoute()

const product = ref({})
const details = ref([])

const quantity = ref(1)

const selectedColor = ref(null)
const selectedSize = ref(null)

const activeImage = ref("")

// =====================
// LOAD DATA
// =====================

const loadData = async () => {

  try {

    const id = route.params.id

    const productRes = await getProductById(id)
    product.value = productRes.data

    activeImage.value = productRes.data.anhDaiDien

    const detailRes = await getBySanPham(id)
    details.value = detailRes.data

  } catch (error) {
    console.error("Lỗi tải dữ liệu:", error)
  }

}

// =====================
// COLORS (có stock + disable hết hàng)
// =====================

const colors = computed(() => {

  const map = new Map()

  details.value.forEach(item => {

    if (!map.has(item.idMauSac)) {

      map.set(item.idMauSac, {
        id: item.idMauSac,
        ten: item.tenMauSac,
        stock: item.soLuongTon
      })

    } else {
      map.get(item.idMauSac).stock += item.soLuongTon
    }

  })

  return Array.from(map.values())

})

// =====================
// SIZES 35 - 44 (chuẩn shop giày)
// =====================

const allSizes = [35,36,37,38,39,40,41,42,43,44]

const sizes = computed(() => {

  if (!selectedColor.value) return []

  return allSizes.map(size => {

    const variant = details.value.find(item =>
      item.idMauSac === selectedColor.value &&
      Number(item.size) === size
    )

    return {
      size,
      idKichThuoc: variant?.idKichThuoc || null,
      stock: variant?.soLuongTon || 0
    }

  })

})

// =====================
// VARIANT SELECTED
// =====================

const selectedVariant = computed(() => {

  if (!selectedColor.value || !selectedSize.value) return null

  return details.value.find(item =>
    item.idMauSac === selectedColor.value &&
    item.idKichThuoc === selectedSize.value
  )

})

// =====================
// PRICE
// =====================

const displayPrice = computed(() => {

  if (selectedVariant.value) {
    return Number(selectedVariant.value.giaBan).toLocaleString("vi-VN")
  }

  if (details.value.length > 0) {
    const minPrice = Math.min(
      ...details.value.map(i => Number(i.giaBan))
    )
    return minPrice.toLocaleString("vi-VN")
  }

  return "0"
})

// =====================
// TOTAL STOCK
// =====================

const totalStock = computed(() => {
  return details.value.reduce((sum, i) => sum + i.soLuongTon, 0)
})

// =====================
// ACTION SELECT
// =====================

const chooseColor = (id) => {
  selectedColor.value = id
  selectedSize.value = null
}

const chooseSize = (id) => {
  if (!id) return
  selectedSize.value = id
}

// =====================
// QUANTITY
// =====================

const increaseQty = () => quantity.value++

const decreaseQty = () => {
  if (quantity.value > 1) quantity.value--
}

// =====================
// CART
// =====================

const addToCart = () => {

  if (!selectedVariant.value) {
    alert("Vui lòng chọn màu và size")
    return
  }

  alert("Đã thêm vào giỏ hàng")
}

const buyNow = () => {

  if (!selectedVariant.value) {
    alert("Vui lòng chọn màu và size")
    return
  }

  alert("Chuyển sang thanh toán")
}

// =====================
// FORMAT
// =====================

const formatMoney = (v) => {
  return Number(v).toLocaleString("vi-VN")
}

onMounted(loadData)

</script>
<template>

  <div class="product-detail-page">

    <!-- BREADCRUMB -->
    <div class="breadcrumb">
      <router-link to="/">Trang chủ</router-link>

      <span>/</span>

      <router-link to="/products">Cửa hàng</router-link>

      <span>/</span>

      <b>
        {{ product.tenSanPham || 'Đang tải...' }}
      </b>
    </div>

    <!-- MAIN -->
    <div class="detail-container">

      <!-- LEFT -->
      <div class="gallery-section">

        <div class="main-image">
          <img
            :src="`/images/${product.anhDaiDien}`"
            :alt="product.tenSanPham"
            class="product-image"
          >
        </div>

      </div>

      <!-- RIGHT -->
      <div class="info-section">

        <div class="brand-name">
          {{ product.tenThuongHieu }}
        </div>

        <h1 class="product-name">
          {{ product.tenSanPham }}
        </h1>

        <div class="product-sub">
          {{ product.gioiTinh }} · {{ product.tenDanhMuc }}
        </div>

        <div class="price">
          {{ displayPrice }} đ
        </div>

        <div
          v-if="totalStock > 0"
          class="stock stock-ok"
        >
          Còn hàng
        </div>

        <div
          v-else
          class="stock stock-out"
        >
          Hết hàng
        </div>

        <!-- ===================== -->
        <!-- MÀU SẮC (có stock + hết hàng) -->
        <!-- ===================== -->

        <div class="option-group">

          <div class="option-title">
            Màu sắc
          </div>

          <div class="color-list">

            <button
              v-for="color in colors"
              :key="color.id"
              class="color-btn"
              :class="{
                active: selectedColor === color.id,
                disabled: color.stock <= 0
              }"
              :disabled="color.stock <= 0"
              @click="chooseColor(color.id)"
            >

              <span>{{ color.ten }}</span>

              <small v-if="color.stock <= 0">
                Hết hàng
              </small>

              <small v-else>
                {{ color.stock }} sp
              </small>

            </button>

          </div>

        </div>

        <!-- ===================== -->
        <!-- SIZE 35 - 44 -->
        <!-- ===================== -->

        <div
          v-if="selectedColor"
          class="option-group"
        >

          <div class="option-title">
            Kích thước
          </div>

          <div class="size-list">

            <button
              v-for="size in sizes"
              :key="size.size"
              class="size-btn"
              :class="{
                active: selectedSize === size.idKichThuoc,
                disabled: size.stock <= 0
              }"
              :disabled="size.stock <= 0"
              @click="chooseSize(size.idKichThuoc)"
            >

              <span>{{ size.size }}</span>

              <small v-if="size.stock <= 0">
                Hết
              </small>

              <small v-else>
                {{ size.stock }}
              </small>

            </button>

          </div>

        </div>

        <!-- SELECTED -->
        <div
          v-if="selectedVariant"
          class="selected-box"
        >

          <div class="selected-row">
            <span>Màu sắc</span>
            <b>{{ selectedVariant.tenMauSac }}</b>
          </div>

          <div class="selected-row">
            <span>Size</span>
            <b>{{ selectedVariant.size }}</b>
          </div>

          <div class="selected-row">
            <span>Tồn kho</span>
            <b>{{ selectedVariant.soLuongTon }}</b>
          </div>

        </div>

        <!-- QUANTITY -->
        <div class="qty-section">

          <div class="option-title">
            Số lượng
          </div>

          <div class="qty-box">

            <button
              class="qty-btn"
              @click="decreaseQty"
            >
              -
            </button>

            <input
              v-model="quantity"
              type="number"
              min="1"
            >

            <button
              class="qty-btn"
              @click="increaseQty"
            >
              +
            </button>

          </div>

        </div>

        <!-- ACTION -->
        <div class="action-group">

          <button
            class="btn-cart"
            @click="addToCart"
          >
            Thêm vào giỏ hàng
          </button>

          <button
            class="btn-buy"
            @click="buyNow"
          >
            Mua ngay
          </button>

        </div>

      </div>

    </div>

    <!-- DESCRIPTION -->
    <div class="description-section">

      <h2>Mô tả sản phẩm</h2>

      <div class="description-box">
        {{ product.moTaChiTiet }}
      </div>

    </div>

    <!-- INFO -->
    <div class="info-section">

      <h2>Thông tin sản phẩm</h2>

      <div class="info-grid">

        <div class="info-card">
          <div class="info-label">Thương hiệu</div>
          <div class="info-value">{{ product.tenThuongHieu }}</div>
        </div>

        <div class="info-card">
          <div class="info-label">Danh mục</div>
          <div class="info-value">{{ product.tenDanhMuc }}</div>
        </div>

        <div class="info-card">
          <div class="info-label">Chất liệu</div>
          <div class="info-value">{{ product.tenChatLieu }}</div>
        </div>

        <div class="info-card">
          <div class="info-label">Xuất xứ</div>
          <div class="info-value">{{ product.tenXuatXu }}</div>
        </div>

      </div>

    </div>

    <!-- TABLE -->
    <div class="variant-section">

      <h2>Tất cả phiên bản</h2>

      <table class="variant-table">

        <thead>
        <tr>
          <th>Màu</th>
          <th>Size</th>
          <th>Giá</th>
          <th>Tồn kho</th>
        </tr>
        </thead>

        <tbody>

        <tr v-for="ct in details" :key="ct.id">

          <td>{{ ct.tenMauSac }}</td>
          <td>{{ ct.size }}</td>
          <td>{{ formatMoney(ct.giaBan) }} đ</td>
          <td>{{ ct.soLuongTon }}</td>

        </tr>

        </tbody>

      </table>

    </div>

  </div>

</template>
<style scoped>

/* =========================
   PAGE
========================= */

.product-detail-page{
  background:#f8fafc;
  min-height:100vh;
  color:#1e293b;
  padding-bottom:80px;
}

/* =========================
   BREADCRUMB
========================= */

.breadcrumb{
  width:90%;
  max-width:1280px;
  margin:0 auto 30px;

  font-size:14px;
  color:#64748b;

  display:flex;
  gap:8px;
  align-items:center;
}

.breadcrumb b{
  color:#0f172a;
  font-weight:600;
}

/* =========================
   MAIN
========================= */

.detail-container{
  width:90%;
  max-width:1280px;
  margin:auto;

  display:grid;
  grid-template-columns:55% 45%;
  gap:60px;
  align-items:flex-start;
}

/* =========================
   IMAGE
========================= */

.gallery-section{
  position:sticky;
  top:20px;
}

.main-image{
  background:white;
  border-radius:24px;
  padding:30px;
  border:1px solid #e2e8f0;
  box-shadow:0 8px 30px rgba(0,0,0,.04);
}

.product-image{
  width:100%;
  height:520px;
  object-fit:contain;
  display:block;
  transition:.3s;
}

.product-image:hover{
  transform:scale(1.03);
}

/* =========================
   INFO
========================= */

.info-section{
  padding-top:10px;
}

.brand-name{
  font-size:13px;
  font-weight:700;
  letter-spacing:2px;
  text-transform:uppercase;
  color:#64748b;
}

.product-name{
  margin-top:10px;
  font-size:42px;
  font-weight:700;
  line-height:1.2;
  color:#0f172a;
}

.product-sub{
  margin-top:8px;
  color:#64748b;
  font-size:15px;
}

.price{
  margin-top:24px;
  font-size:38px;
  font-weight:700;
  color:#ef4444;
}

.stock{
  display:inline-flex;
  align-items:center;
  margin-top:18px;
  padding:8px 14px;
  border-radius:999px;
  font-size:13px;
  font-weight:600;
}

.stock-ok{
  background:#dcfce7;
  color:#15803d;
}

.stock-out{
  background:#fee2e2;
  color:#dc2626;
}

/* =========================
   OPTION GROUP
========================= */

.option-group{
  margin-top:28px;
}

.option-title{
  font-size:16px;
  font-weight:600;
  color:#0f172a;
  margin-bottom:12px;
}

.color-list,
.size-list{
  display:flex;
  flex-wrap:wrap;
  gap:12px;
}

/* =========================
   COLOR BUTTON (PRO)
========================= */

.color-btn{
  min-width:120px;
  height:65px;

  display:flex;
  flex-direction:column;
  justify-content:center;
  align-items:center;

  border:1px solid #dbe2ea;
  background:white;

  border-radius:14px;

  cursor:pointer;
  transition:.25s;
}

.color-btn span{
  font-size:14px;
  font-weight:600;
}

.color-btn small{
  font-size:11px;
  margin-top:4px;
  color:#64748b;
}

.color-btn:hover{
  border-color:#3b82f6;
  color:#2563eb;
}

.color-btn.active{
  background:#2563eb;
  color:white;
  border-color:#2563eb;
}

.color-btn.active small{
  color:#dbeafe;
}

.color-btn.disabled{
  background:#f8fafc;
  color:#94a3b8;
  border-color:#e2e8f0;
  cursor:not-allowed;
  opacity:.7;
}

/* =========================
   SIZE BUTTON (PRO)
========================= */

.size-btn{
  width:75px;
  height:65px;

  display:flex;
  flex-direction:column;
  justify-content:center;
  align-items:center;

  border:1px solid #dbe2ea;
  background:white;

  border-radius:12px;

  cursor:pointer;
  transition:.25s;
}

.size-btn span{
  font-size:15px;
  font-weight:600;
}

.size-btn small{
  margin-top:3px;
  font-size:11px;
  color:#64748b;
}

.size-btn:hover{
  border-color:#3b82f6;
  color:#2563eb;
}

.size-btn.active{
  background:#2563eb;
  color:white;
  border-color:#2563eb;
}

.size-btn.active small{
  color:#dbeafe;
}

.size-btn.disabled{
  background:#f8fafc;
  color:#94a3b8;
  border-color:#e2e8f0;
  cursor:not-allowed;
  opacity:.7;
}

.size-btn.disabled span{
  text-decoration:line-through;
}

.size-btn.disabled small{
  color:#ef4444;
}

/* =========================
   SELECTED BOX
========================= */

.selected-box{
  margin-top:24px;
  background:white;
  border:1px solid #e2e8f0;
  border-radius:16px;
  padding:18px;
}

.selected-row{
  display:flex;
  justify-content:space-between;
  padding:10px 0;
  border-bottom:1px solid #f1f5f9;
}

.selected-row:last-child{
  border-bottom:none;
}

/* =========================
   QUANTITY
========================= */

.qty-section{
  margin-top:28px;
}

.qty-box{
  display:flex;
  align-items:center;
  gap:12px;
}

.qty-btn{
  width:44px;
  height:44px;
  border:none;
  background:#f1f5f9;
  border-radius:12px;
  font-size:20px;
  font-weight:700;
  cursor:pointer;
  transition:.2s;
}

.qty-btn:hover{
  background:#e2e8f0;
}

.qty-box input{
  width:90px;
  height:44px;
  border:1px solid #cbd5e1;
  border-radius:12px;
  text-align:center;
  font-size:15px;
}

/* =========================
   ACTION BUTTONS
========================= */

.action-group{
  display:flex;
  gap:15px;
  margin-top:35px;
}

.btn-cart{
  flex:1;
  height:54px;
  border:2px solid #2563eb;
  background:white;
  color:#2563eb;
  border-radius:14px;
  font-size:15px;
  font-weight:600;
  cursor:pointer;
  transition:.25s;
}

.btn-cart:hover{
  background:#2563eb;
  color:white;
}

.btn-buy{
  flex:1;
  height:54px;
  border:none;
  background:#2563eb;
  color:white;
  border-radius:14px;
  font-size:15px;
  font-weight:600;
  cursor:pointer;
  transition:.25s;
}

.btn-buy:hover{
  background:#1d4ed8;
}

/* =========================
   DESCRIPTION + INFO
========================= */

.description-section,
.variant-section{
  width:90%;
  max-width:1280px;
  margin:70px auto 0;
}

.description-section h2,
.variant-section h2{
  font-size:30px;
  font-weight:700;
  margin-bottom:20px;
}

.description-box{
  background:white;
  padding:28px;
  border-radius:20px;
  border:1px solid #e2e8f0;
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

.info-card{
  background:white;
  padding:24px;
  border-radius:18px;
  border:1px solid #e2e8f0;
}

.info-label{
  color:#64748b;
  font-size:14px;
  margin-bottom:10px;
}

.info-value{
  font-size:17px;
  font-weight:600;
  color:#0f172a;
}

/* =========================
   TABLE
========================= */

.variant-table{
  width:100%;
  border-collapse:collapse;
  background:white;
  border-radius:20px;
  overflow:hidden;
  box-shadow:0 6px 20px rgba(0,0,0,.04);
}

.variant-table th{
  background:#f1f5f9;
  color:#334155;
  font-size:15px;
  font-weight:600;
  padding:18px;
}

.variant-table td{
  padding:18px;
  text-align:center;
  border-top:1px solid #e2e8f0;
}

.variant-table tr:hover{
  background:#f8fafc;
}

/* =========================
   RESPONSIVE
========================= */

@media(max-width:992px){
  .detail-container{
    grid-template-columns:1fr;
  }

  .gallery-section{
    position:static;
  }

  .product-image{
    height:380px;
  }

  .product-name{
    font-size:34px;
  }

  .price{
    font-size:30px;
  }

  .action-group{
    flex-direction:column;
  }

  .info-grid{
    grid-template-columns:1fr;
  }
}

@media(max-width:576px){
  .product-image{
    height:280px;
  }

  .product-name{
    font-size:28px;
  }

  .price{
    font-size:26px;
  }
}

</style>

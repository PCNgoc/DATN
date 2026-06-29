<script setup>
import { ref, computed, onMounted } from "vue"
import { useRouter } from "vue-router"
import { getCartByKhachHangId, updateItemQuantity, removeItemFromCart, clearCart } from "@/services/gioHangService"
import { getByMa } from "@/services/phieuGiamGiaService"
import { getMeApi } from "@/api/authApi"
import { useCart } from "@/composables/useCart"

const router = useRouter()
const cart = ref(null)
const loading = ref(false)
const user = ref(null)
const { fetchCartCount } = useCart()

// Voucher states
const voucherCode = ref("")
const appliedVoucher = ref(null)
const voucherError = ref("")
const voucherSuccess = ref("")

const formatMoney = (value) => {
  if (!value) return "0 đ"
  return Number(value).toLocaleString("vi-VN") + " đ"
}

// LOAD USER & CART
const loadCartData = async () => {
  loading.value = true
  try {
    // Get logged-in customer
    const userRes = await getMeApi()
    user.value = userRes.data
    
    if (user.value && user.value.id) {
      const cartRes = await getCartByKhachHangId(user.value.id)
      cart.value = cartRes.data
      await fetchCartCount(user.value.id)
    }
  } catch (err) {
    console.error("Lỗi khi tải giỏ hàng:", err)
  } finally {
    loading.value = false
  }
}

onMounted(loadCartData)

// CHANGE QUANTITY
const updateQty = async (item, delta) => {
  const newQty = item.soLuong + delta
  if (newQty <= 0) {
    await deleteItem(item.id)
    return
  }
  
  try {
    await updateItemQuantity(item.id, newQty)
    await loadCartData()
  } catch (err) {
    alert("Không thể cập nhật số lượng: " + (err.response?.data?.message || err.message))
  }
}

// DELETE ITEM
const deleteItem = async (itemId) => {
  if (!confirm("Bạn muốn xóa sản phẩm này khỏi giỏ hàng?")) return
  try {
    await removeItemFromCart(itemId)
    await loadCartData()
  } catch (err) {
    console.error(err)
  }
}

// CLEAR CART
const clearAll = async () => {
  if (!confirm("Bạn muốn xóa toàn bộ giỏ hàng?")) return
  try {
    await clearCart(user.value.id)
    await loadCartData()
    appliedVoucher.value = null
    voucherCode.value = ""
  } catch (err) {
    console.error(err)
  }
}

// APPLY VOUCHER
const applyVoucher = async () => {
  voucherError.value = ""
  voucherSuccess.value = ""
  if (!voucherCode.value.trim()) {
    voucherError.value = "Vui lòng nhập mã giảm giá"
    return
  }
  
  try {
    const res = await getByMa(voucherCode.value.trim())
    const voucher = res.data
    
    if (!voucher || !voucher.trangThai) {
      voucherError.value = "Mã giảm giá không hợp lệ hoặc đã hết hạn"
      return
    }
    
    // Check minimum order value
    if (subtotal.value < voucher.giaTriDonHangToiThieu) {
      voucherError.value = `Đơn hàng tối thiểu phải từ ${formatMoney(voucher.giaTriDonHangToiThieu)} để sử dụng mã này`
      return
    }
    
    appliedVoucher.value = voucher
    voucherSuccess.value = `Áp dụng mã ${voucher.maPhieu} thành công!`
  } catch (err) {
    voucherError.value = "Mã giảm giá không tồn tại"
    appliedVoucher.value = null
  }
}

// CALCULATE TOTALS
const subtotal = computed(() => {
  if (!cart.value || !cart.value.items) return 0
  return cart.value.items.reduce((sum, item) => {
    // Check price from product detail
    const price = item.chiTietSanPham?.giaBan || 0
    return sum + (price * item.soLuong)
  }, 0)
})

const discountAmount = computed(() => {
  if (!appliedVoucher.value) return 0
  const v = appliedVoucher.value
  if (v.loaiGiamGia) {
    // Percentage discount
    let calc = (subtotal.value * v.giaTriGiam) / 100
    if (v.giaTriGiamToiDa > 0 && calc > v.giaTriGiamToiDa) {
      calc = v.giaTriGiamToiDa
    }
    return calc
  } else {
    // Fixed cash discount
    return v.giaTriGiam
  }
})

const finalTotal = computed(() => {
  const total = subtotal.value - discountAmount.value
  return total < 0 ? 0 : total
})

// CHECKOUT REDIRECT
const checkout = () => {
  if (!cart.value || !cart.value.items || cart.value.items.length === 0) {
    alert("Giỏ hàng của bạn đang trống")
    return
  }
  
  const checkoutData = {
    isFromCart: true,
    items: cart.value.items.map(item => ({
      image: item.chiTietSanPham?.hinhAnh || "banner1.png",
      productName: item.chiTietSanPham?.sanPham?.ten || "Giày Sneaker",
      color: item.chiTietSanPham?.mauSac?.ten || "Mặc định",
      size: item.chiTietSanPham?.kichThuoc?.ten || "Mặc định",
      quantity: item.soLuong,
      price: item.chiTietSanPham?.giaBan || 0
    })),
    appliedVoucher: appliedVoucher.value,
    discountAmount: discountAmount.value,
    subtotal: subtotal.value,
    finalTotal: finalTotal.value
  }
  
  localStorage.setItem("checkoutData", JSON.stringify(checkoutData))
  localStorage.removeItem("buyNowProduct")
  router.push("/checkout")
}
</script>

<template>
  <div class="cart-page">
    <div class="header-container">
      <h1>🛒 Giỏ hàng của bạn</h1>
      <button v-if="cart && cart.items && cart.items.length > 0" class="btn-clear" @click="clearAll">
        🗑 Xóa toàn bộ giỏ hàng
      </button>
    </div>

    <div v-if="loading" class="loading-state">
      <p>⏳ Đang tải thông tin giỏ hàng...</p>
    </div>

    <div v-else class="cart-container">
      <!-- Empty state -->
      <div v-if="!cart || !cart.items || cart.items.length === 0" class="empty-cart-card">
        <div class="empty-icon">🛍️</div>
        <h3>Giỏ hàng của bạn đang trống</h3>
        <p>Hãy khám phá các sản phẩm sneaker cực hot của chúng tôi nhé!</p>
        <router-link to="/products" class="btn-shop">TIẾP TỤC MUA SẮM</router-link>
      </div>

      <!-- Cart Content -->
      <div v-else class="cart-wrapper">
        <!-- Cart Items list -->
        <div class="cart-items-list">
          <div v-for="item in cart.items" :key="item.id" class="cart-item-card">
            <img :src="item.chiTietSanPham?.hinhAnh ? `/images/${item.chiTietSanPham.hinhAnh}` : '/images/banner1.png'" alt="Sneaker Image" class="item-img" />
            
            <div class="item-details">
              <h3>{{ item.chiTietSanPham?.sanPham?.ten || "Giày Sneaker" }}</h3>
              <div class="specs">
                <span>Màu sắc: <strong>{{ item.chiTietSanPham?.mauSac?.ten || "Mặc định" }}</strong></span>
                <span>Kích cỡ: <strong>{{ item.chiTietSanPham?.kichThuoc?.ten || "Mặc định" }}</strong></span>
              </div>
              <p class="unit-price">Đơn giá: <strong>{{ formatMoney(item.chiTietSanPham?.giaBan) }}</strong></p>
            </div>

            <!-- Quantity controls -->
            <div class="quantity-controls">
              <button @click="updateQty(item, -1)">-</button>
              <span class="qty-display">{{ item.soLuong }}</span>
              <button @click="updateQty(item, 1)">+</button>
            </div>

            <!-- Total price & delete -->
            <div class="item-action-total">
              <p class="total-item-price">
                {{ formatMoney((item.chiTietSanPham?.giaBan || 0) * item.soLuong) }}
              </p>
              <button class="btn-delete-item" @click="deleteItem(item.id)">
                🗑️
              </button>
            </div>
          </div>
        </div>

        <!-- Summary & Voucher Box -->
        <div class="cart-summary-box">
          <h2>Tóm tắt đơn hàng</h2>
          
          <div class="price-row">
            <span>Tạm tính ({{ cart.tongSoLuong }} sản phẩm)</span>
            <strong>{{ formatMoney(subtotal) }}</strong>
          </div>

          <!-- Voucher Apply Form -->
          <div class="voucher-section">
            <h3>🎟️ Áp dụng Mã Giảm Giá</h3>
            <div class="voucher-input-group">
              <input v-model="voucherCode" placeholder="Nhập mã giảm giá..." @keyup.enter="applyVoucher" />
              <button @click="applyVoucher">Áp dụng</button>
            </div>
            <p v-if="voucherError" class="voucher-error">{{ voucherError }}</p>
            <p v-if="voucherSuccess" class="voucher-success">{{ voucherSuccess }}</p>
          </div>

          <div v-if="appliedVoucher" class="price-row voucher-applied-row">
            <span>Mã đã áp dụng: <strong>{{ appliedVoucher.maPhieu }}</strong></span>
            <span class="discount-value">- {{ formatMoney(discountAmount) }}</span>
          </div>

          <hr class="divider" />

          <div class="price-row final-total-row">
            <span>Tổng cộng</span>
            <strong class="grand-total">{{ formatMoney(finalTotal) }}</strong>
          </div>

          <button class="btn-checkout" @click="checkout">
            TIẾN HÀNH THANH TOÁN
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cart-page {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
  font-family: Inter, sans-serif;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-container h1 {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
}

.btn-clear {
  background: #fee2e2;
  color: #ef4444;
  border: none;
  padding: 10px 18px;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-clear:hover {
  background: #fecaca;
}

.loading-state {
  text-align: center;
  padding: 50px;
  font-size: 18px;
  color: #64748b;
}

.empty-cart-card {
  background: white;
  border-radius: 20px;
  padding: 60px 40px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-cart-card h3 {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 10px;
}

.empty-cart-card p {
  color: #64748b;
  margin-bottom: 30px;
}

.btn-shop {
  display: inline-block;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  text-decoration: none;
  padding: 14px 28px;
  border-radius: 12px;
  font-weight: 700;
  box-shadow: 0 10px 25px rgba(239, 68, 68, 0.3);
  transition: all 0.3s ease;
}

.btn-shop:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 30px rgba(239, 68, 68, 0.4);
}

.cart-wrapper {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

.cart-items-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cart-item-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.cart-item-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
}

.item-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
}

.item-details {
  flex: 1;
}

.item-details h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
}

.specs {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #64748b;
  margin-bottom: 8px;
}

.unit-price {
  font-size: 14px;
  color: #475569;
}

.quantity-controls {
  display: flex;
  align-items: center;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  overflow: hidden;
}

.quantity-controls button {
  background: #f8fafc;
  border: none;
  width: 36px;
  height: 36px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.quantity-controls button:hover {
  background: #e2e8f0;
}

.qty-display {
  padding: 0 16px;
  font-weight: 600;
  color: #1e293b;
}

.item-action-total {
  text-align: right;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
  min-width: 120px;
}

.total-item-price {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.btn-delete-item {
  background: #fee2e2;
  color: #ef4444;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-delete-item:hover {
  background: #ef4444;
  color: white;
}

/* Summary Box */
.cart-summary-box {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.04);
  height: fit-content;
}

.cart-summary-box h2 {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 20px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  font-size: 15px;
  color: #475569;
}

.voucher-section {
  margin: 20px 0;
  background: #f8fafc;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.voucher-section h3 {
  font-size: 14px;
  font-weight: 700;
  color: #334155;
  margin-bottom: 10px;
}

.voucher-input-group {
  display: flex;
  gap: 8px;
}

.voucher-input-group input {
  flex: 1;
  padding: 10px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  font-size: 13px;
}

.voucher-input-group button {
  background: #1e293b;
  color: white;
  border: none;
  padding: 0 16px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.voucher-input-group button:hover {
  background: #0f172a;
}

.voucher-error {
  color: #ef4444;
  font-size: 12px;
  margin-top: 6px;
}

.voucher-success {
  color: #10b981;
  font-size: 12px;
  margin-top: 6px;
  font-weight: 600;
}

.voucher-applied-row {
  background: #ecfdf5;
  padding: 10px 14px;
  border-radius: 8px;
  color: #065f46;
}

.discount-value {
  color: #10b981;
  font-weight: 700;
}

.divider {
  border: 0;
  border-top: 1px solid #f1f5f9;
  margin: 20px 0;
}

.final-total-row {
  font-size: 18px;
  color: #1e293b;
}

.grand-total {
  font-size: 24px;
  color: #ef4444;
  font-weight: 800;
}

.btn-checkout {
  width: 100%;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  border: none;
  padding: 16px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 25px rgba(239, 68, 68, 0.25);
  transition: all 0.3s ease;
  margin-top: 10px;
}

.btn-checkout:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 30px rgba(239, 68, 68, 0.35);
}

@media (max-width: 992px) {
  .cart-wrapper {
    grid-template-columns: 1fr;
  }
}
</style>

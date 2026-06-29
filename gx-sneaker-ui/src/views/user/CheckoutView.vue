<script setup>
import { ref, computed, onMounted } from "vue"
import { useRouter } from "vue-router"
import { useCart } from "@/composables/useCart"
import { getByKhachHangId as getAddresses } from "@/services/diaChiService"

const router = useRouter()
const { fetchCartCount } = useCart()

const checkoutItems = ref([])
const appliedVoucher = ref(null)
const discountAmount = ref(0)
const isFromCart = ref(false)
const savedAddresses = ref([])

const fullName = ref("")
const phone = ref("")
const address = ref("")
const note = ref("")

const shipFee = ref(30000)

const selectAddress = (addr) => {
  fullName.value = addr.hoTenNguoiNhan || ""
  phone.value = addr.soDienThoai || ""
  address.value = `${addr.diaChiChiTiet}, ${addr.phuongXa}, ${addr.tinhThanh}`
}

const formatMoney = (value) => {
  if (!value) return "0 đ"
  return Number(value).toLocaleString("vi-VN") + " đ"
}

onMounted(() => {
  // Load user info from localStorage if available to prefill
  const userData = localStorage.getItem("user")
  if (userData) {
    const parsedUser = JSON.parse(userData)
    fullName.value = parsedUser.hoTen || ""
    phone.value = parsedUser.soDienThoai || ""
    
    // Fetch saved addresses
    if (parsedUser.id) {
      getAddresses(parsedUser.id)
        .then(res => {
          savedAddresses.value = res.data || []
        })
        .catch(err => {
          console.error("Lỗi tải danh sách địa chỉ đã lưu:", err)
        })
    }
  }

  // Load checkout details
  const cartCheckout = JSON.parse(localStorage.getItem("checkoutData"))
  if (cartCheckout) {
    isFromCart.value = true
    checkoutItems.value = cartCheckout.items || []
    appliedVoucher.value = cartCheckout.appliedVoucher
    discountAmount.value = cartCheckout.discountAmount || 0
  } else {
    const buyNowItem = JSON.parse(localStorage.getItem("buyNowProduct"))
    if (buyNowItem) {
      isFromCart.value = false
      checkoutItems.value = [buyNowItem]
      appliedVoucher.value = null
      discountAmount.value = 0
    }
  }
})

const totalMoney = computed(() => {
  return checkoutItems.value.reduce((sum, item) => {
    return sum + (Number(item.price) * Number(item.quantity))
  }, 0)
})

const finalTotal = computed(() => {
  const total = totalMoney.value - discountAmount.value + shipFee.value
  return total < 0 ? 0 : total
})

const placeOrder = async () => {
  if (!fullName.value.trim()) {
    alert("Vui lòng nhập họ tên")
    return
  }

  if (!phone.value.trim()) {
    alert("Vui lòng nhập số điện thoại")
    return
  }

  if (!address.value.trim()) {
    alert("Vui lòng nhập địa chỉ nhận hàng")
    return
  }

  console.log({
    customerName: fullName.value,
    phone: phone.value,
    address: address.value,
    note: note.value,
    items: checkoutItems.value,
    voucher: appliedVoucher.value,
    discountAmount: discountAmount.value,
    shipFee: shipFee.value,
    totalMoney: finalTotal.value
  })

  alert("Đặt hàng thành công! Xin cảm ơn quý khách.")
  
  // Clear checkout storage
  localStorage.removeItem("checkoutData")
  localStorage.removeItem("buyNowProduct")
  
  // If it was checked out from the database cart, we should clear the cart in DB
  const userData = localStorage.getItem("user")
  if (userData && isFromCart.value) {
    try {
      const parsedUser = JSON.parse(userData)
      // Clear DB cart count using our composable/API
      await fetchCartCount(parsedUser.id)
    } catch (e) {
      console.error(e)
    }
  }

  router.push("/")
}
</script>

<template>
  <div class="checkout-page">
    <h1>🛒 Thanh toán đơn hàng</h1>

    <div class="checkout-wrapper">
      <!-- Thông tin nhận hàng -->
      <div class="customer-info">
        <h2>Thông tin nhận hàng</h2>

        <!-- Saved Address Picker -->
        <div v-if="savedAddresses.length > 0" class="saved-addresses-picker">
          <label class="picker-label">🔑 Chọn nhanh địa chỉ nhận hàng đã lưu:</label>
          <div class="address-chips">
            <button
              v-for="addr in savedAddresses"
              :key="addr.id"
              type="button"
              class="address-chip"
              @click="selectAddress(addr)"
            >
              <strong>{{ addr.hoTenNguoiNhan }}</strong> - {{ addr.soDienThoai }}
              <span class="chip-text">{{ addr.diaChiChiTiet }}, {{ addr.phuongXa }}, {{ addr.tinhThanh }}</span>
            </button>
          </div>
        </div>

        <div class="form-group">
          <label>Họ và tên</label>
          <input
            v-model="fullName"
            type="text"
            placeholder="Nhập họ tên người nhận"
          >
        </div>

        <div class="form-group">
          <label>Số điện thoại</label>
          <input
            v-model="phone"
            type="text"
            placeholder="Nhập số điện thoại"
          >
        </div>

        <div class="form-group">
          <label>Địa chỉ nhận hàng</label>
          <textarea
            v-model="address"
            rows="4"
            placeholder="Nhập địa chỉ chi tiết (Số nhà, tên đường, phường/xã, quận/huyện, tỉnh/thành phố)"
          />
        </div>

        <div class="form-group">
          <label>Ghi chú</label>
          <textarea
            v-model="note"
            rows="3"
            placeholder="Ghi chú thêm cho shipper hoặc cửa hàng..."
          />
        </div>
      </div>

      <!-- Đơn hàng -->
      <div class="order-summary">
        <h2>Đơn hàng của bạn</h2>

        <div class="product-list">
          <div
            v-for="(item, idx) in checkoutItems"
            :key="idx"
            class="product-item"
          >
            <img
              :src="item.image ? `/images/${item.image}` : '/images/banner1.png'"
              alt="Product Image"
            >

            <div class="product-info">
              <h3>{{ item.productName }}</h3>
              <p>Màu: <strong>{{ item.color }}</strong></p>
              <p>Size: <strong>{{ item.size }}</strong></p>
              <p>Số lượng: <strong>{{ item.quantity }}</strong></p>
              <p>Giá: <strong>{{ formatMoney(item.price) }}</strong></p>
            </div>
          </div>
        </div>

        <hr>

        <div class="price-row">
          <span>Tạm tính</span>
          <strong>{{ formatMoney(totalMoney) }}</strong>
        </div>

        <div v-if="discountAmount > 0" class="price-row discount-row">
          <span>Mã giảm giá áp dụng ({{ appliedVoucher?.maPhieu }})</span>
          <strong class="discount-val">- {{ formatMoney(discountAmount) }}</strong>
        </div>

        <div class="price-row">
          <span>Phí vận chuyển</span>
          <strong>{{ formatMoney(shipFee) }}</strong>
        </div>

        <hr>

        <div class="price-row total-row">
          <span>Tổng cộng</span>
          <strong class="grand-total">{{ formatMoney(finalTotal) }}</strong>
        </div>

        <button
          class="btn-order"
          @click="placeOrder"
        >
          XÁC NHẬN ĐẶT HÀNG
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.checkout-page {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
  font-family: Inter, sans-serif;
}

.checkout-page h1 {
  margin-bottom: 30px;
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
}

.checkout-wrapper {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

.customer-info,
.order-summary {
  background: #fff;
  padding: 28px;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.customer-info h2,
.order-summary h2 {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #475569;
  font-size: 14px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #cbd5e1;
  border-radius: 10px;
  font-size: 14px;
  font-family: inherit;
  transition: all 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #64748b;
  outline: none;
  box-shadow: 0 0 0 3px rgba(100, 116, 139, 0.1);
}

.product-list {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
}

.product-item {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f1f5f9;
}

.product-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.product-item img {
  width: 90px;
  height: 90px;
  object-fit: cover;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.product-info h3 {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 6px;
}

.product-info p {
  margin-bottom: 4px;
  font-size: 13px;
  color: #64748b;
}

.price-row {
  display: flex;
  justify-content: space-between;
  margin: 14px 0;
  font-size: 15px;
  color: #475569;
}

.discount-row {
  color: #059669;
  background: #ecfdf5;
  padding: 8px 12px;
  border-radius: 8px;
}

.discount-val {
  color: #10b981;
}

.total-row {
  font-size: 18px;
  color: #1e293b;
}

.grand-total {
  font-size: 24px;
  color: #ef4444;
  font-weight: 800;
}

.btn-order {
  width: 100%;
  margin-top: 20px;
  padding: 16px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 25px rgba(239, 68, 68, 0.25);
  transition: all 0.3s;
}

.btn-order:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 30px rgba(239, 68, 68, 0.35);
}

@media (max-width: 992px) {
  .checkout-wrapper {
    grid-template-columns: 1fr;
  }
}

.saved-addresses-picker {
  margin-bottom: 24px;
  background: #f8fafc;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.picker-label {
  display: block;
  font-weight: 700;
  font-size: 14px;
  color: #475569;
  margin-bottom: 12px !important;
}

.address-chips {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.address-chip {
  text-align: left;
  background: #ffffff;
  border: 1px solid #cbd5e1;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
}

.address-chip:hover {
  border-color: #0ea5e9;
  background: #f0f9ff;
  box-shadow: 0 4px 12px rgba(14, 165, 233, 0.05);
}

.chip-text {
  display: block;
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

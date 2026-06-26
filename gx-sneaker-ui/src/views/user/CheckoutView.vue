
<script setup>
import { ref, computed, onMounted } from "vue"

const product = ref(null)

const fullName = ref("")
const phone = ref("")
const address = ref("")
const note = ref("")

const shipFee = ref(30000)

const formatMoney = (value) => {
  return Number(value).toLocaleString("vi-VN") + " đ"
}

onMounted(() => {
  const buyNowItem = JSON.parse(
    localStorage.getItem("buyNowProduct")
  )

  if (buyNowItem) {
    product.value = buyNowItem
  }
})

const totalMoney = computed(() => {
  if (!product.value) return 0

  return (
    Number(product.value.price) *
    Number(product.value.quantity)
  )
})

const finalTotal = computed(() => {
  return totalMoney.value + shipFee.value
})

const placeOrder = () => {

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
    product: product.value,
    totalMoney: finalTotal.value
  })

  alert("Đặt hàng thành công")
}
</script>

<template>

  <div class="checkout-page">

    <h1>Thanh toán đơn hàng</h1>

    <div class="checkout-wrapper">

      <!-- Thông tin nhận hàng -->
      <div class="customer-info">

        <h2>Thông tin nhận hàng</h2>

        <div class="form-group">
          <label>Họ và tên</label>
          <input
            v-model="fullName"
            type="text"
            placeholder="Nhập họ tên"
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
            placeholder="Nhập địa chỉ nhận hàng"
          />
        </div>

        <div class="form-group">
          <label>Ghi chú</label>
          <textarea
            v-model="note"
            rows="3"
            placeholder="Ghi chú cho đơn hàng"
          />
        </div>

      </div>

      <!-- Đơn hàng -->
      <div class="order-summary">

        <h2>Đơn hàng của bạn</h2>

        <div
          v-if="product"
          class="product-item"
        >

          <img
            :src="`/images/${product.image}`"
            alt=""
          >

          <div class="product-info">

            <h3>
              {{ product.productName }}
            </h3>

            <p>
              Màu:
              <strong>{{ product.color }}</strong>
            </p>

            <p>
              Size:
              <strong>{{ product.size }}</strong>
            </p>

            <p>
              Số lượng:
              <strong>{{ product.quantity }}</strong>
            </p>

            <p>
              Giá:
              <strong>
                {{ formatMoney(product.price) }}
              </strong>
            </p>

          </div>

        </div>

        <hr>

        <div class="price-row">
          <span>Tạm tính</span>
          <strong>
            {{ formatMoney(totalMoney) }}
          </strong>
        </div>

        <div class="price-row">
          <span>Phí vận chuyển</span>
          <strong>
            {{ formatMoney(shipFee) }}
          </strong>
        </div>

        <hr>

        <div class="price-row total-row">
          <span>Tổng cộng</span>
          <strong>
            {{ formatMoney(finalTotal) }}
          </strong>
        </div>

        <button
          class="btn-order"
          @click="placeOrder"
        >
          ĐẶT HÀNG
        </button>

      </div>

    </div>

  </div>

</template>

<style scoped>

.checkout-page{
  max-width:1200px;
  margin:40px auto;
  padding:0 20px;
}

.checkout-page h1{
  margin-bottom:30px;
  font-size:36px;
}

.checkout-wrapper{
  display:grid;
  grid-template-columns:2fr 1fr;
  gap:30px;
}

.customer-info,
.order-summary{
  background:#fff;
  padding:24px;
  border-radius:16px;
  box-shadow:0 4px 20px rgba(0,0,0,.08);
}

.customer-info h2,
.order-summary h2{
  margin-bottom:20px;
}

.form-group{
  margin-bottom:18px;
}

.form-group label{
  display:block;
  margin-bottom:6px;
  font-weight:600;
}

.form-group input,
.form-group textarea{
  width:100%;
  padding:12px;
  border:1px solid #ddd;
  border-radius:8px;
  font-size:14px;
}

.product-item{
  display:flex;
  gap:16px;
}

.product-item img{
  width:120px;
  height:120px;
  object-fit:cover;
  border-radius:10px;
  border:1px solid #eee;
}

.product-info h3{
  margin-bottom:10px;
}

.product-info p{
  margin-bottom:6px;
}

.price-row{
  display:flex;
  justify-content:space-between;
  margin:14px 0;
}

.total-row{
  font-size:24px;
  color:#dc2626;
  font-weight:700;
}

.btn-order{
  width:100%;
  margin-top:20px;
  padding:14px;
  border:none;
  border-radius:10px;
  background:#dc2626;
  color:white;
  font-size:18px;
  font-weight:700;
  cursor:pointer;
}

.btn-order:hover{
  background:#b91c1c;
}

@media (max-width: 992px){

  .checkout-wrapper{
    grid-template-columns:1fr;
  }

}
</style>


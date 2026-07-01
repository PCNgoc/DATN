<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

const checking = ref(false)
const checkError = ref('')
const paymentInfo = ref(null)

const orderId = computed(() => route.params.id)
const maHoaDon = computed(() => route.query.maHoaDon || paymentInfo.value?.maHoaDon || '')
const paymentMethod = computed(() => route.query.payment || route.query.paymentMethod || '')

const payosReturned = computed(() => {
  return Boolean(
    route.query.status ||
    route.query.orderCode ||
    route.query.id ||
    route.query.code ||
    route.query.cancel,
  )
})

const paymentStatus = computed(() => {
  return paymentInfo.value?.trangThaiThanhToan || ''
})

const orderStatus = computed(() => {
  return paymentInfo.value?.trangThai || ''
})

const isPaid = computed(() => paymentStatus.value === 'DA_THANH_TOAN')
const isExpired = computed(() => paymentStatus.value === 'QUA_HAN')
const isPayOS = computed(() => {
  const method = String(paymentMethod.value || '').toUpperCase()
  return method === 'QR' || method === 'PAYOS' || payosReturned.value
})

const statusTitle = computed(() => {
  if (checking.value) return 'Đang kiểm tra thanh toán'

  if (isPayOS.value) {
    if (isPaid.value) return 'Thanh toán thành công'
    if (isExpired.value) return 'Đơn hàng đã quá hạn thanh toán'
    return 'Đơn hàng đang chờ thanh toán'
  }

  return 'Đặt hàng thành công'
})

const statusMessage = computed(() => {
  if (checking.value) {
    return 'Hệ thống đang kiểm tra trạng thái thanh toán từ payOS.'
  }

  if (checkError.value) {
    return checkError.value
  }

  if (isPayOS.value) {
    if (isPaid.value) {
      return 'Hệ thống đã xác nhận thanh toán thành công. Đơn hàng đang chờ shop xác nhận và xử lý.'
    }

    if (isExpired.value) {
      return 'Đơn hàng đã quá thời gian thanh toán và sẽ bị hủy.'
    }

    return 'Thanh toán chưa hoàn tất hoặc payOS đang xử lý. Bạn có thể bấm kiểm tra lại.'
  }

  return 'Đơn hàng COD đã được gửi thành công và đang chờ shop xác nhận.'
})

const statusIcon = computed(() => {
  if (checking.value) return '⏳'
  if (isPaid.value) return '✅'
  if (isExpired.value) return '❌'
  if (isPayOS.value) return '💳'
  return '🎉'
})

const formatMoney = (value) => {
  if (!value) return '0 đ'
  return Number(value).toLocaleString('vi-VN') + ' đ'
}

const checkPayOSPayment = async () => {
  if (!orderId.value) {
    checkError.value = 'Không tìm thấy mã đơn hàng để kiểm tra thanh toán.'
    return
  }

  try {
    checking.value = true
    checkError.value = ''

    const res = await axios.get(`${API_BASE_URL}/payos/check-order/${orderId.value}`)

    paymentInfo.value = res.data || {}
  } catch (err) {
    console.error('Lỗi kiểm tra thanh toán payOS:', err)

    checkError.value =
      err.response?.data?.message ||
      err.response?.data ||
      'Không thể kiểm tra trạng thái thanh toán. Vui lòng thử lại sau.'
  } finally {
    checking.value = false
  }
}

onMounted(() => {
  if (isPayOS.value) {
    checkPayOSPayment()
  }
})

const goHome = () => {
  router.push('/')
}

const goOrders = () => {
  router.push('/orders')
}

const goProducts = () => {
  router.push('/products')
}
</script>

<template>
  <div class="success-page">
    <div class="success-card">
      <div class="status-icon">
        {{ statusIcon }}
      </div>

      <h1>{{ statusTitle }}</h1>
      <p class="message">
        {{ statusMessage }}
      </p>

      <div class="order-info">
        <div class="info-row">
          <span>Mã đơn hàng</span>
          <strong>{{ maHoaDon || `#${orderId}` }}</strong>
        </div>

        <div class="info-row">
          <span>Mã hệ thống</span>
          <strong>#{{ orderId }}</strong>
        </div>

        <div v-if="paymentInfo?.payosStatus" class="info-row">
          <span>Trạng thái payOS</span>
          <strong>{{ paymentInfo.payosStatus }}</strong>
        </div>

        <div v-if="paymentStatus" class="info-row">
          <span>Thanh toán</span>
          <strong
            :class="{
              paid: paymentStatus === 'DA_THANH_TOAN',
              pending: paymentStatus === 'CHO_THANH_TOAN',
              expired: paymentStatus === 'QUA_HAN',
            }"
          >
            {{ paymentStatus }}
          </strong>
        </div>

        <div v-if="orderStatus" class="info-row">
          <span>Trạng thái đơn</span>
          <strong>{{ orderStatus }}</strong>
        </div>

        <div v-if="paymentInfo?.tongTienThanhToan" class="info-row">
          <span>Tổng thanh toán</span>
          <strong class="money">
            {{ formatMoney(paymentInfo.tongTienThanhToan) }}
          </strong>
        </div>
      </div>

      <div v-if="isPayOS && !isPaid" class="payos-note">
        <p>
          Nếu bạn đã thanh toán nhưng trạng thái chưa cập nhật, hãy đợi vài giây rồi bấm
          <strong>Kiểm tra lại thanh toán</strong>.
        </p>
      </div>

      <div class="actions">
        <button
          v-if="isPayOS"
          class="btn-secondary"
          :disabled="checking"
          @click="checkPayOSPayment"
        >
          {{ checking ? 'Đang kiểm tra...' : 'Kiểm tra lại thanh toán' }}
        </button>

        <button class="btn-primary" @click="goOrders">Xem đơn hàng</button>

        <button class="btn-outline" @click="goProducts">Tiếp tục mua sắm</button>

        <button class="btn-outline" @click="goHome">Về trang chủ</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.success-page {
  min-height: 100vh;
  padding: 50px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at top left, #fee2e2, transparent 35%),
    linear-gradient(135deg, #f8fafc, #eef2ff);
  font-family: Inter, sans-serif;
}

.success-card {
  width: 100%;
  max-width: 620px;
  background: #ffffff;
  border-radius: 28px;
  padding: 42px 34px;
  text-align: center;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.12);
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.status-icon {
  width: 86px;
  height: 86px;
  margin: 0 auto 22px;
  border-radius: 50%;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 46px;
  box-shadow: inset 0 0 0 1px #e5e7eb;
}

h1 {
  margin: 0;
  font-size: 32px;
  font-weight: 900;
  color: #111827;
}

.message {
  margin: 14px auto 28px;
  max-width: 480px;
  color: #4b5563;
  line-height: 1.7;
  font-size: 15px;
}

.order-info {
  margin: 24px 0;
  padding: 18px;
  border-radius: 18px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  text-align: left;
}

.info-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid #e5e7eb;
  color: #4b5563;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row strong {
  color: #111827;
  text-align: right;
}

.info-row .money {
  color: #dc2626;
  font-size: 18px;
}

.paid {
  color: #059669 !important;
}

.pending {
  color: #d97706 !important;
}

.expired {
  color: #dc2626 !important;
}

.payos-note {
  margin: 22px 0;
  padding: 16px;
  border-radius: 16px;
  background: #fffbeb;
  color: #92400e;
  text-align: left;
  line-height: 1.6;
  border: 1px solid #fde68a;
}

.actions {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
  margin-top: 26px;
}

button {
  border: none;
  border-radius: 14px;
  padding: 14px 18px;
  font-weight: 800;
  cursor: pointer;
  transition: 0.2s ease;
}

button:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.btn-primary {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  box-shadow: 0 12px 26px rgba(220, 38, 38, 0.25);
}

.btn-secondary {
  background: #111827;
  color: white;
}

.btn-outline {
  background: white;
  color: #111827;
  border: 1px solid #d1d5db;
}

button:hover:not(:disabled) {
  transform: translateY(-1px);
}

@media (max-width: 560px) {
  .success-card {
    padding: 32px 22px;
  }

  h1 {
    font-size: 26px;
  }

  .info-row {
    flex-direction: column;
    gap: 4px;
  }

  .info-row strong {
    text-align: left;
  }
}
</style>

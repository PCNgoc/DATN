<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const orderId = route.params.id

const checkoutUrl = ref('')
const maHoaDon = ref('')
const totalAmount = ref(0)
const expiredAt = ref(null)

const remainingSeconds = ref(0)
const countdownInterval = ref(null)
const checkInterval = ref(null)
const checking = ref(false)
const expired = ref(false)
const redirected = ref(false)

const formatMoney = (value) => {
  return Number(value || 0).toLocaleString('vi-VN') + ' đ'
}

const formatTime = computed(() => {
  const minutes = Math.floor(remainingSeconds.value / 60)
  const seconds = remainingSeconds.value % 60

  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})

const percent = computed(() => {
  const max = 30 * 60
  return Math.max((remainingSeconds.value / max) * 100, 0)
})

const clearAllTimers = () => {
  if (countdownInterval.value) {
    clearInterval(countdownInterval.value)
    countdownInterval.value = null
  }

  if (checkInterval.value) {
    clearInterval(checkInterval.value)
    checkInterval.value = null
  }
}

const redirectWhenCancelled = () => {
  if (redirected.value) return

  redirected.value = true
  clearAllTimers()
  localStorage.removeItem('payosPaymentInfo')

  alert('Đơn hàng đã bị hủy')

  router.replace({
    path: '/orders',
    query: {
      cancelled: 'true',
      orderId,
    },
  })
}

const redirectWhenPaid = () => {
  if (redirected.value) return

  redirected.value = true
  clearAllTimers()
  localStorage.removeItem('payosPaymentInfo')

  router.replace({
    path: `/order-success/${orderId}`,
    query: {
      maHoaDon: maHoaDon.value,
      payment: 'QR',
    },
  })
}

const loadPaymentInfo = () => {
  const raw = localStorage.getItem('payosPaymentInfo')

  if (!raw) {
    alert('Không tìm thấy thông tin thanh toán')
    router.replace('/orders')
    return false
  }

  try {
    const data = JSON.parse(raw)

    checkoutUrl.value = data.checkoutUrl || ''
    maHoaDon.value = data.maHoaDon || ''
    totalAmount.value = Number(data.totalAmount || 0)

    if (data.expiredAt) {
      expiredAt.value = new Date(data.expiredAt)
    } else {
      expiredAt.value = new Date(Date.now() + 30 * 60 * 1000)
    }

    calculateRemaining()
    return true
  } catch (e) {
    console.error(e)
    alert('Thông tin thanh toán không hợp lệ')
    router.replace('/orders')
    return false
  }
}

const calculateRemaining = () => {
  if (!expiredAt.value) return

  const diff = Math.floor((expiredAt.value.getTime() - Date.now()) / 1000)

  remainingSeconds.value = diff > 0 ? diff : 0

  if (remainingSeconds.value <= 0) {
    expired.value = true
    clearAllTimers()
  }
}

const startCountdown = () => {
  calculateRemaining()

  countdownInterval.value = setInterval(() => {
    calculateRemaining()
  }, 1000)
}

const openPayOS = () => {
  if (!checkoutUrl.value || expired.value) return

  window.open(checkoutUrl.value, '_blank')
}

const goPayOSSameTab = () => {
  if (!checkoutUrl.value || expired.value) return

  window.location.href = checkoutUrl.value
}

const checkPaymentStatus = async (showAlert = false) => {
  if (!orderId || redirected.value) return

  try {
    checking.value = true

    const res = await axios.get(`http://localhost:8080/api/payos/check-order/${orderId}`)

    const data = res.data || {}

    const payosStatus = String(
      data.payosStatus || data.paymentStatus || data.status || '',
    ).toUpperCase()
    const trangThai = String(data.trangThai || '').toUpperCase()
    const trangThaiThanhToan = String(data.trangThaiThanhToan || '').toUpperCase()

    if (
      payosStatus === 'CANCELLED' ||
      payosStatus === 'CANCELED' ||
      payosStatus === 'EXPIRED' ||
      trangThai === 'DA_HUY'
    ) {
      redirectWhenCancelled()
      return
    }

    if (payosStatus === 'PAID' || trangThaiThanhToan === 'DA_THANH_TOAN') {
      redirectWhenPaid()
      return
    }

    if (showAlert) {
      alert('Đơn hàng chưa được thanh toán. Vui lòng hoàn tất thanh toán trên payOS.')
    }
  } catch (err) {
    console.error(err)

    if (showAlert) {
      alert('Chưa kiểm tra được trạng thái thanh toán')
    }
  } finally {
    checking.value = false
  }
}

const handlePayOSCancelled = async () => {
  try {
    await checkPaymentStatus(false)
  } catch (e) {
    console.error(e)
  }

  redirectWhenCancelled()
}

onMounted(async () => {
  if (route.query.cancel === 'true') {
    await handlePayOSCancelled()
    return
  }

  const loaded = loadPaymentInfo()

  if (!loaded) return

  startCountdown()

  await checkPaymentStatus(false)

  checkInterval.value = setInterval(() => {
    checkPaymentStatus(false)
  }, 5000)
})

onBeforeUnmount(() => {
  clearAllTimers()
})
</script>

<template>
  <div class="payos-page">
    <div class="payos-card">
      <div class="header">
        <div class="logo">GX</div>

        <div>
          <h1>Thanh toán qua payOS</h1>
          <p>Vui lòng hoàn tất thanh toán trong thời gian quy định</p>
        </div>
      </div>

      <div class="countdown-box" :class="{ danger: remainingSeconds <= 300 }">
        <p>Thời gian còn lại</p>
        <strong>{{ formatTime }}</strong>

        <div class="progress">
          <div class="progress-bar" :style="{ width: percent + '%' }"></div>
        </div>

        <span v-if="!expired">
          Sau thời gian này, đơn hàng sẽ tự động bị hủy nếu chưa thanh toán.
        </span>

        <span v-else class="expired-text"> Đơn hàng đã quá thời gian thanh toán. </span>
      </div>

      <div class="order-info">
        <div>
          <span>Mã hóa đơn</span>
          <strong>{{ maHoaDon || 'Đang cập nhật' }}</strong>
        </div>

        <div>
          <span>Số tiền</span>
          <strong>{{ formatMoney(totalAmount) }}</strong>
        </div>
      </div>

      <div class="actions">
        <button class="btn-main" :disabled="expired || !checkoutUrl" @click="goPayOSSameTab">
          Thanh toán trên payOS
        </button>

        <button class="btn-outline" :disabled="expired || !checkoutUrl" @click="openPayOS">
          Mở payOS tab mới
        </button>

        <button class="btn-check" :disabled="checking" @click="checkPaymentStatus(true)">
          {{ checking ? 'Đang kiểm tra...' : 'Tôi đã thanh toán' }}
        </button>
      </div>

      <div v-if="checkoutUrl && !expired" class="iframe-wrap">
        <iframe :src="checkoutUrl" title="payOS Checkout"></iframe>
      </div>

      <p class="note">
        Nếu khung thanh toán không hiển thị, hãy bấm nút
        <b>Thanh toán trên payOS</b>.
      </p>
    </div>
  </div>
</template>

<style scoped>
.payos-page {
  min-height: 100vh;
  padding: 32px;
  background:
    radial-gradient(circle at top left, #fee2e2, transparent 30%),
    linear-gradient(135deg, #f8fafc, #eef2ff);
  display: flex;
  justify-content: center;
}

.payos-card {
  width: 100%;
  max-width: 1000px;
  background: white;
  border-radius: 26px;
  padding: 28px;
  box-shadow: 0 22px 55px rgba(15, 23, 42, 0.12);
}

.header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.logo {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 20px;
}

.header h1 {
  margin: 0;
  font-size: 30px;
  color: #111827;
}

.header p {
  margin: 5px 0 0;
  color: #6b7280;
}

.countdown-box {
  padding: 24px;
  border-radius: 22px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  text-align: center;
  margin-bottom: 22px;
}

.countdown-box.danger {
  background: #fff7ed;
  border-color: #fed7aa;
}

.countdown-box p {
  margin: 0;
  font-weight: 800;
  color: #374151;
}

.countdown-box strong {
  display: block;
  margin: 8px 0 14px;
  font-size: 56px;
  font-weight: 900;
  color: #dc2626;
  letter-spacing: 2px;
}

.progress {
  height: 12px;
  border-radius: 999px;
  background: #e5e7eb;
  overflow: hidden;
  margin-bottom: 12px;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(135deg, #22c55e, #16a34a);
  transition: width 0.3s;
}

.expired-text {
  color: #dc2626;
  font-weight: 900;
}

.order-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin-bottom: 20px;
}

.order-info div {
  background: #f9fafb;
  border-radius: 18px;
  padding: 16px;
  border: 1px solid #e5e7eb;
}

.order-info span {
  display: block;
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 6px;
}

.order-info strong {
  color: #111827;
  font-size: 18px;
}

.actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.actions button {
  border: none;
  border-radius: 14px;
  padding: 13px 18px;
  font-weight: 900;
  cursor: pointer;
}

.actions button:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.btn-main {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
}

.btn-outline {
  background: #111827;
  color: white;
}

.btn-check {
  background: #16a34a;
  color: white;
}

.iframe-wrap {
  width: 100%;
  height: 650px;
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  background: #f9fafb;
}

.iframe-wrap iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.note {
  margin: 14px 0 0;
  color: #6b7280;
  font-size: 14px;
  text-align: center;
}

@media (max-width: 768px) {
  .payos-page {
    padding: 18px;
  }

  .payos-card {
    padding: 20px;
  }

  .header {
    align-items: flex-start;
  }

  .header h1 {
    font-size: 24px;
  }

  .countdown-box strong {
    font-size: 42px;
  }

  .order-info {
    grid-template-columns: 1fr;
  }

  .actions {
    flex-direction: column;
  }

  .actions button {
    width: 100%;
  }

  .iframe-wrap {
    height: 520px;
  }
}
</style>

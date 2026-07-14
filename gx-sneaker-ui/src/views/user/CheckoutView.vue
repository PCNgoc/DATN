<script setup>
import { ref, computed, onMounted } from 'vue'
import { datHang } from '@/services/hoaDonService.js'
import { getByMa, getAll } from '@/services/phieuGiamGiaService'
import { useRouter } from 'vue-router'

const router = useRouter()

const checkoutItems = ref([])
const isFromCart = ref(false)

const fullName = ref('')
const phone = ref('')
const address = ref('')
const note = ref('')

const paymentMethod = ref('COD')
const shipFee = ref(30000)
const loading = ref(false)

const couponCode = ref('')
const appliedCoupon = ref(null)
const couponLoading = ref(false)

const availableCoupons = ref([])
const showCoupons = ref(false)
const couponError = ref('')
const couponSuccess = ref('')
const couponPage = ref(1)
const couponPageSize = 3

const formatMoney = (value) => {
  return Number(value || 0).toLocaleString('vi-VN') + ' đ'
}

const normalizeItem = (item) => {
  return {
    detailId:
        item?.detailId ||
        item?.chiTietSanPhamId ||
        item?.idChiTietSanPham ||
        item?.id_chi_tiet_san_pham ||
        item?.chiTietSanPham?.id ||
        item?.chi_tiet_san_pham_id ||
        item?.idCTSP ||
        null,

    productName:
      item?.productName ||
      item?.tenSanPham ||
      item?.name ||
      item?.chiTietSanPham?.sanPham?.tenSanPham ||
      'Sản phẩm',

    image:
      item?.image ||
      item?.anhDaiDien ||
      item?.hinhAnh ||
      item?.chiTietSanPham?.sanPham?.anhDaiDien ||
      '',

    color:
      item?.color || item?.mauSac || item?.tenMauSac || item?.chiTietSanPham?.mauSac?.ten || '',

    size:
      item?.size ||
      item?.kichThuoc ||
      item?.tenKichThuoc ||
      item?.chiTietSanPham?.kichThuoc?.size ||
      '',

    quantity: Number(item?.quantity || item?.soLuong || 1),

    price: Number(item?.price || item?.giaBan || item?.donGia || item?.chiTietSanPham?.giaBan || 0),
  }
}

onMounted(async () => {
  const userData = localStorage.getItem('user')

  if (userData) {
    try {
      const user = JSON.parse(userData)
      fullName.value = user.hoTen || user.tenKhachHang || ''
      phone.value = user.soDienThoai || user.phone || ''
    } catch (e) {
      console.error('Lỗi parse user:', e)
    }
  }

  const cartCheckout = localStorage.getItem('checkoutData')
  const buyNowData = localStorage.getItem('buyNowProduct')

  if (cartCheckout) {
    const parsed = JSON.parse(cartCheckout)

    const rawItems = Array.isArray(parsed)
      ? parsed
      : parsed.items || parsed.selectedItems || parsed.products || []

    checkoutItems.value = rawItems.map(normalizeItem)
    isFromCart.value = true

    if (parsed.appliedCoupon) {
      appliedCoupon.value = parsed.appliedCoupon
      couponCode.value =
        parsed.appliedCoupon.maPhieuGiamGia ||
        parsed.appliedCoupon.maPhieu ||
        parsed.couponCode ||
        ''
      discountAmount.value = Number(parsed.appliedCoupon.soTienGiam || parsed.discountAmount || 0)
    } else if (parsed.couponCode || parsed.maPhieuGiamGia) {
      couponCode.value = parsed.couponCode || parsed.maPhieuGiamGia
      appliedCoupon.value = {
        maPhieuGiamGia: couponCode.value,
      }
      discountAmount.value = Number(parsed.discountAmount || parsed.soTienGiam || 0)
    }
  } else if (buyNowData) {
    checkoutItems.value = [normalizeItem(JSON.parse(buyNowData))]
    isFromCart.value = false
  } else {
    alert('Không có sản phẩm để thanh toán')
    router.push('/products')
    return
  }
  await loadAvailableCoupons()
  console.log('===== CHECKOUT ITEMS =====')
  console.log(checkoutItems.value)
})

const totalMoney = computed(() => {
  return checkoutItems.value.reduce((sum, item) => {
    return sum + Number(item.price || 0) * Number(item.quantity || 1)
  }, 0)
})

const getCouponCode = (coupon) => {
  return coupon?.maPhieu || coupon?.maPhieuGiamGia || coupon?.ma || ''
}

const getCouponName = (coupon) => {
  return coupon?.tenPhieu || coupon?.tenPhieuGiamGia || 'Mã giảm giá'
}

const getMinOrder = (coupon) => {
  return Number(coupon?.giaTriDonHangToiThieu || 0)
}

const formatMoneyCompact = (value) => {
  const number = Number(value || 0)

  if (number >= 1000000) return (number / 1000000).toFixed(0) + 'M'
  if (number >= 1000) return (number / 1000).toFixed(0) + 'K'

  return String(number)
}

const getPotentialDiscount = (coupon, total) => {
  if (!coupon) return 0

  const totalValue = Number(total || 0)
  const discountValue = Number(coupon.giaTriGiam || 0)
  let discount = 0

  if (coupon.loaiGiamGia === true) {
    discount = (totalValue * discountValue) / 100

    if (coupon.giaTriGiamToiDa && discount > Number(coupon.giaTriGiamToiDa)) {
      discount = Number(coupon.giaTriGiamToiDa)
    }
  } else {
    discount = discountValue
  }

  if (discount > totalValue) discount = totalValue
  if (discount < 0) discount = 0

  return discount
}

const isCouponEligible = (coupon) => {
  return totalMoney.value >= getMinOrder(coupon)
}

const discountAmount = computed(() => {
  if (!appliedCoupon.value) return 0
  return getPotentialDiscount(appliedCoupon.value, totalMoney.value)
})

const finalTotal = computed(() => {
  return Math.max(totalMoney.value - discountAmount.value + shipFee.value, 0)
})

const sortedAvailableCoupons = computed(() => {
  return [...availableCoupons.value].sort((a, b) => {
    const eligibleA = isCouponEligible(a) ? 1 : 0
    const eligibleB = isCouponEligible(b) ? 1 : 0

    if (eligibleA !== eligibleB) {
      return eligibleB - eligibleA
    }

    return getPotentialDiscount(b, totalMoney.value) - getPotentialDiscount(a, totalMoney.value)
  })
})

const totalCouponPages = computed(() => {
  const count = sortedAvailableCoupons.value.length
  return count > 0 ? Math.ceil(count / couponPageSize) : 1
})

const paginatedCoupons = computed(() => {
  const start = (couponPage.value - 1) * couponPageSize
  const end = start + couponPageSize

  return sortedAvailableCoupons.value.slice(start, end)
})

const loadAvailableCoupons = async () => {
  try {
    const res = await getAll()
    const rawData = res.data

    const list = Array.isArray(rawData) ? rawData : rawData?.content || rawData?.data || []

    const now = new Date()

    availableCoupons.value = list.filter((coupon) => {
      const active = coupon.trangThai === true

      const hasQty =
        coupon.soLuong === null || coupon.soLuong === undefined || Number(coupon.soLuong) > 0

      const started = !coupon.ngayBatDau || new Date(coupon.ngayBatDau) <= now

      const notExpired = !coupon.ngayKetThuc || new Date(coupon.ngayKetThuc) >= now

      return active && hasQty && started && notExpired
    })
  } catch (err) {
    console.error('Lỗi tải mã giảm giá khả dụng:', err)
  }
}

const applyCoupon = async () => {
  couponError.value = ''
  couponSuccess.value = ''

  if (!couponCode.value.trim()) {
    couponError.value = 'Vui lòng nhập mã giảm giá'
    return
  }

  try {
    couponLoading.value = true

    const res = await getByMa(couponCode.value.trim())
    const coupon = res.data

    if (!coupon || coupon.trangThai !== true) {
      appliedCoupon.value = null
      couponError.value = 'Mã giảm giá không hợp lệ hoặc đã ngừng hoạt động'
      return
    }

    if (!isCouponEligible(coupon)) {
      appliedCoupon.value = null
      couponError.value = `Đơn hàng tối thiểu phải từ ${formatMoney(getMinOrder(coupon))} để sử dụng mã này`
      return
    }

    appliedCoupon.value = coupon
    couponCode.value = getCouponCode(coupon)
    couponSuccess.value = `Áp dụng mã ${getCouponCode(coupon)} thành công`
  } catch (err) {
    appliedCoupon.value = null
    couponError.value =
      err.response?.data?.message || err.response?.data || 'Mã giảm giá không tồn tại'
  } finally {
    couponLoading.value = false
  }
}

const selectCoupon = (coupon) => {
  couponError.value = ''
  couponSuccess.value = ''

  if (!isCouponEligible(coupon)) {
    couponError.value = `Bạn cần mua thêm ${formatMoney(getMinOrder(coupon) - totalMoney.value)} để dùng mã này`
    return
  }

  appliedCoupon.value = coupon
  couponCode.value = getCouponCode(coupon)
  couponSuccess.value = `Áp dụng mã ${getCouponCode(coupon)} thành công`
}

const removeCoupon = () => {
  couponCode.value = ''
  appliedCoupon.value = null
  couponError.value = ''
  couponSuccess.value = ''
}

const getChiTietSanPhamId = (item) => {
  return item?.detailId
}

const getImageUrl = (image) => {
  if (!image) return '/images/no-image.png'

  if (image.startsWith('http')) return image
  if (image.startsWith('/')) return image

  return `/images/${image}`
}

const getCustomerId = () => {
  const userData = localStorage.getItem('user')

  if (userData) {
    try {
      const user = JSON.parse(userData)
      return user?.id || user?.idKhachHang || user?.khachHangId
    } catch (e) {
      console.error('Lỗi parse user:', e)
    }
  }

  return (
    localStorage.getItem('userId') ||
    localStorage.getItem('idKhachHang') ||
    localStorage.getItem('khachHangId') ||
    1
  )
}

const validateForm = () => {
  if (!fullName.value.trim()) {
    alert('Vui lòng nhập họ tên người nhận')
    return false
  }

  if (!phone.value.trim()) {
    alert('Vui lòng nhập số điện thoại')
    return false
  }

  const phoneRegex = /^(0|\+84)[0-9]{9,10}$/
  if (!phoneRegex.test(phone.value.trim())) {
    alert('Số điện thoại không hợp lệ')
    return false
  }

  if (!address.value.trim()) {
    alert('Vui lòng nhập địa chỉ nhận hàng')
    return false
  }

  return true
}

const placeOrder = async () => {
  if (!checkoutItems.value.length) {
    alert('Không có sản phẩm')
    return
  }

  if (!validateForm()) {
    return
  }

  const invalidItem = checkoutItems.value.find((item) => !getChiTietSanPhamId(item))

  if (invalidItem) {
    alert('Có sản phẩm bị thiếu ID chi tiết sản phẩm')
    console.log('Sản phẩm thiếu ID chi tiết:', invalidItem)
    return
  }

  try {
    loading.value = true

    const idKhachHang = getCustomerId()

    const request = {
      idKhachHang: Number(idKhachHang),
      tenNguoiNhan: fullName.value.trim(),
      soDienThoai: phone.value.trim(),
      diaChi: address.value.trim(),
      ghiChu:
        paymentMethod.value === 'QR'
          ? `[PAYOS_CHO_THANH_TOAN] ${note.value.trim()}`
          : `[COD_CHO_XAC_NHAN] ${note.value.trim()}`,
      phuongThucThanhToan: paymentMethod.value === 'QR' ? 'PAYOS' : 'COD',
      maPhieuGiamGia: appliedCoupon.value ? getCouponCode(appliedCoupon.value) : null,
      items: checkoutItems.value.map((item) => ({
        chiTietSanPhamId: Number(getChiTietSanPhamId(item)),
        soLuong: Number(item.quantity || 1),
      })),
    }

    console.log('===== REQUEST DAT HANG =====')
    console.log(request)

    const res = await datHang(request)

    console.log('===== DAT HANG RESPONSE =====')
    console.log(res.data)

    localStorage.removeItem('buyNowProduct')
    localStorage.removeItem('checkoutData')

    if (paymentMethod.value === 'QR') {
      if (res.data?.checkoutUrl) {
        const expiredAt =
          res.data?.hanThanhToan || new Date(Date.now() + 30 * 60 * 1000).toISOString()

        localStorage.setItem(
          'payosPaymentInfo',
          JSON.stringify({
            orderId: res.data.id,
            maHoaDon: res.data.maHoaDon,
            checkoutUrl: res.data.checkoutUrl,
            totalAmount: res.data.tongTienThanhToan,
            expiredAt,
          }),
        )

        router.push(`/payos-waiting/${res.data.id}`)
        return
      }

      alert('Không nhận được link thanh toán payOS')
      return
    }

    router.push({
      path: `/order-success/${res.data.id}`,
      query: {
        maHoaDon: res.data.maHoaDon,
        payment: paymentMethod.value,
      },
    })
  } catch (e) {
    console.error(e)
    console.log(e.response)
    console.log(e.response?.data)

    alert(e.response?.data?.message || e.response?.data || 'Đặt hàng thất bại')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="checkout-page">
    <div class="checkout-header">
      <button class="back-btn" @click="router.back()">← Quay lại</button>

      <div>
        <h1>Thanh toán</h1>
        <p>Hoàn tất thông tin để đặt hàng</p>
      </div>
    </div>

    <div class="checkout-wrapper">
      <!-- LEFT -->
      <div class="left-content">
        <!-- Thông tin nhận hàng -->
        <section class="card">
          <div class="card-title">
            <span class="step">1</span>
            <div>
              <h2>Thông tin nhận hàng</h2>
              <p>Vui lòng nhập chính xác thông tin giao hàng</p>
            </div>
          </div>

          <div class="form-grid">
            <div class="form-group">
              <label>Họ và tên người nhận <span>*</span></label>
              <input v-model="fullName" type="text" placeholder="Ví dụ: Nguyễn Văn A" />
            </div>

            <div class="form-group">
              <label>Số điện thoại <span>*</span></label>
              <input v-model="phone" type="text" placeholder="Ví dụ: 0987654321" />
            </div>
          </div>

          <div class="form-group">
            <label>Địa chỉ nhận hàng <span>*</span></label>
            <textarea
              v-model="address"
              rows="4"
              placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành phố"
            />
          </div>

          <div class="form-group">
            <label>Ghi chú</label>
            <textarea
              v-model="note"
              rows="3"
              placeholder="Ghi chú cho shop hoặc đơn vị vận chuyển"
            />
          </div>
        </section>

        <!-- Phương thức thanh toán -->
        <section class="card">
          <div class="card-title">
            <span class="step">2</span>
            <div>
              <h2>Phương thức thanh toán</h2>
              <p>Chọn hình thức thanh toán phù hợp</p>
            </div>
          </div>

          <div class="payment-list">
            <label class="payment-item" :class="{ active: paymentMethod === 'COD' }">
              <input v-model="paymentMethod" type="radio" value="COD" />
              <div class="payment-icon">💵</div>
              <div>
                <h3>Thanh toán khi nhận hàng</h3>
                <p>Khách hàng thanh toán trực tiếp cho shipper khi nhận hàng.</p>
              </div>
            </label>

            <label class="payment-item" :class="{ active: paymentMethod === 'QR' }">
              <input v-model="paymentMethod" type="radio" value="QR" />
              <div class="payment-icon">🏦</div>
              <div>
                <h3>Thanh toán tự động qua payOS</h3>
                <p>
                  Hệ thống sẽ chuyển bạn sang trang thanh toán payOS. Sau khi thanh toán thành công,
                  đơn hàng sẽ tự động cập nhật.
                </p>
              </div>
            </label>
          </div>

          <div v-if="paymentMethod === 'QR'" class="qr-box">
            <h3>Thanh toán tự động qua payOS</h3>
            <p>Sau khi bấm đặt hàng, hệ thống sẽ chuyển bạn sang trang thanh toán payOS.</p>
            <p>
              Sau khi thanh toán thành công, hệ thống sẽ kiểm tra và cập nhật trạng thái thanh toán.
            </p>
            <p>
              Bạn có 30 phút để hoàn tất thanh toán. Sau 30 phút, đơn hàng sẽ tự động bị hủy nếu
              chưa thanh toán.
            </p>
          </div>
        </section>
      </div>

      <!-- RIGHT -->
      <aside class="order-summary">
        <h2>Đơn hàng của bạn</h2>

        <div v-for="(item, index) in checkoutItems" :key="index" class="product-box">
          <img :src="getImageUrl(item.image)" alt="Ảnh sản phẩm" />

          <div class="product-info">
            <h3>{{ item.productName }}</h3>

            <div class="variant">
              <span v-if="item.color"
                >Màu: <b>{{ item.color }}</b></span
              >
              <span v-if="item.size"
                >Size: <b>{{ item.size }}</b></span
              >
            </div>

            <div class="quantity-price">
              <span>x{{ item.quantity }}</span>
              <strong>{{ formatMoney(item.price) }}</strong>
            </div>
          </div>
        </div>

        <div class="coupon-box">
          <h3>🎟️ Mã giảm giá</h3>

          <div class="coupon-input">
            <input
              v-model="couponCode"
              type="text"
              placeholder="Nhập mã giảm giá"
              :disabled="!!appliedCoupon"
              @keyup.enter="applyCoupon"
            />

            <button
              v-if="!appliedCoupon"
              type="button"
              :disabled="couponLoading"
              @click="applyCoupon"
            >
              {{ couponLoading ? 'Đang kiểm tra...' : 'Áp dụng' }}
            </button>

            <button v-else type="button" class="btn-remove-coupon" @click="removeCoupon">
              Hủy
            </button>
          </div>

          <p v-if="couponError" class="coupon-error">
            {{ couponError }}
          </p>

          <p v-if="couponSuccess" class="coupon-success">
            {{ couponSuccess }} - giảm {{ formatMoney(discountAmount) }}
          </p>

          <div
            v-if="availableCoupons.length > 0"
            class="coupon-toggle"
            @click="showCoupons = !showCoupons"
          >
            Bạn có <strong>{{ availableCoupons.length }}</strong> mã giảm giá khả dụng.
            <span>{{ showCoupons ? 'Thu gọn' : 'Xem mã' }}</span>
          </div>

          <div v-if="showCoupons" class="coupon-list">
            <div
              v-for="coupon in paginatedCoupons"
              :key="coupon.id"
              class="coupon-card"
              :class="{
                eligible: isCouponEligible(coupon),
                disabled: !isCouponEligible(coupon),
                applied: appliedCoupon?.id === coupon.id,
              }"
            >
              <div class="coupon-left">
                <strong>
                  {{
                    coupon.loaiGiamGia
                      ? `${coupon.giaTriGiam}%`
                      : formatMoneyCompact(coupon.giaTriGiam)
                  }}
                </strong>
                <span>GIẢM</span>
              </div>

              <div class="coupon-right">
                <div class="coupon-code">
                  {{ getCouponCode(coupon) }}
                </div>

                <p>{{ getCouponName(coupon) }}</p>

                <small> Đơn tối thiểu: {{ formatMoney(getMinOrder(coupon)) }} </small>

                <small v-if="isCouponEligible(coupon)">
                  Dự kiến giảm: {{ formatMoney(getPotentialDiscount(coupon, totalMoney)) }}
                </small>

                <small v-if="!isCouponEligible(coupon)" class="coupon-warning">
                  Mua thêm {{ formatMoney(getMinOrder(coupon) - totalMoney) }} để dùng
                </small>

                <button
                  v-if="appliedCoupon?.id !== coupon.id"
                  type="button"
                  :disabled="!isCouponEligible(coupon)"
                  @click="selectCoupon(coupon)"
                >
                  Áp dụng
                </button>

                <button v-else type="button" class="btn-remove-coupon" @click="removeCoupon">
                  Đang áp dụng
                </button>
              </div>
            </div>

            <div v-if="totalCouponPages > 1" class="coupon-pagination">
              <button type="button" :disabled="couponPage === 1" @click="couponPage--">◀</button>

              <span>Trang {{ couponPage }} / {{ totalCouponPages }}</span>

              <button
                type="button"
                :disabled="couponPage === totalCouponPages"
                @click="couponPage++"
              >
                ▶
              </button>
            </div>
          </div>
        </div>

        <div class="divider"></div>

        <div class="price-row">
          <span>Tạm tính</span>
          <strong>{{ formatMoney(totalMoney) }}</strong>
        </div>

        <div class="price-row">
          <span>Giảm giá</span>
          <strong class="discount-text">-{{ formatMoney(discountAmount) }}</strong>
        </div>

        <div class="price-row">
          <span>Phí vận chuyển</span>
          <strong>{{ formatMoney(shipFee) }}</strong>
        </div>

        <div class="divider"></div>

        <div class="price-row total-row">
          <span>Tổng thanh toán</span>
          <strong>{{ formatMoney(finalTotal) }}</strong>
        </div>

        <button class="btn-order" :disabled="loading" @click="placeOrder">
          {{
            loading
              ? 'ĐANG XỬ LÝ...'
              : paymentMethod === 'QR'
                ? 'THANH TOÁN QUA PAYOS'
                : 'ĐẶT HÀNG - CHỜ XÁC NHẬN'
          }}
        </button>

        <p class="policy-text">
          Bằng việc đặt hàng, bạn đồng ý với chính sách mua hàng của GX Sneaker.
        </p>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.checkout-page {
  min-height: 100vh;
  padding: 40px 24px;
  background:
    radial-gradient(circle at top left, #fee2e2, transparent 30%),
    linear-gradient(135deg, #f8fafc, #eef2ff);
}

.checkout-header {
  max-width: 1200px;
  margin: 0 auto 28px;
  display: flex;
  align-items: center;
  gap: 18px;
}

.back-btn {
  border: none;
  background: white;
  color: #111827;
  padding: 12px 18px;
  border-radius: 14px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 25px rgba(15, 23, 42, 0.08);
}

.checkout-header h1 {
  font-size: 38px;
  font-weight: 800;
  color: #111827;
  margin: 0;
}

.checkout-header p {
  margin-top: 4px;
  color: #6b7280;
}

.checkout-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) 430px;
  gap: 28px;
  align-items: flex-start;
}

.left-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card,
.order-summary {
  background: rgba(255, 255, 255, 0.96);
  border-radius: 24px;
  padding: 28px;
  box-shadow: 0 18px 45px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.card-title {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  margin-bottom: 24px;
}

.step {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: 800;
}

.card-title h2,
.order-summary h2 {
  margin: 0;
  font-size: 22px;
  color: #111827;
}

.card-title p {
  margin-top: 4px;
  color: #6b7280;
  font-size: 14px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

.form-group {
  margin-bottom: 18px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 700;
  color: #374151;
}

.form-group label span {
  color: #dc2626;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  font-size: 15px;
  outline: none;
  transition: 0.2s;
  background: #f9fafb;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #ef4444;
  background: white;
  box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.08);
}

.payment-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.payment-item {
  display: flex;
  gap: 16px;
  padding: 18px;
  border: 2px solid #e5e7eb;
  border-radius: 18px;
  cursor: pointer;
  transition: 0.2s;
  background: #fff;
}

.payment-item.active {
  border-color: #dc2626;
  background: #fef2f2;
}

.payment-item.disabled {
  opacity: 0.55;
  cursor: not-allowed;
  background: #f3f4f6;
}

.payment-item input {
  margin-top: 6px;
}

.payment-icon {
  font-size: 30px;
}

.payment-item h3 {
  margin: 0 0 6px;
  font-size: 16px;
  color: #111827;
}

.payment-item p {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
}

.order-summary {
  position: sticky;
  top: 24px;
}

.product-box {
  display: flex;
  gap: 16px;
  margin-top: 20px;
}

.product-box img {
  width: 118px;
  height: 118px;
  object-fit: cover;
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  background: #f9fafb;
}

.product-info {
  flex: 1;
}

.product-info h3 {
  margin: 0 0 10px;
  font-size: 17px;
  line-height: 1.35;
  color: #111827;
}

.variant {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 12px;
  color: #6b7280;
  font-size: 14px;
}

.quantity-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quantity-price strong {
  color: #dc2626;
}

.divider {
  height: 1px;
  background: #e5e7eb;
  margin: 22px 0;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 14px 0;
  font-size: 15px;
  color: #374151;
}

.total-row {
  font-size: 20px;
  font-weight: 800;
  color: #111827;
}

.total-row strong {
  color: #dc2626;
  font-size: 26px;
}

.btn-order {
  width: 100%;
  margin-top: 18px;
  padding: 16px;
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  font-size: 17px;
  font-weight: 800;
  cursor: pointer;
  transition: 0.25s;
  box-shadow: 0 14px 25px rgba(220, 38, 38, 0.28);
}

.btn-order:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 18px 32px rgba(220, 38, 38, 0.38);
}

.btn-order:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.policy-text {
  margin-top: 14px;
  font-size: 13px;
  color: #6b7280;
  text-align: center;
  line-height: 1.5;
}

@media (max-width: 992px) {
  .checkout-wrapper {
    grid-template-columns: 1fr;
  }

  .order-summary {
    position: static;
  }
}

@media (max-width: 640px) {
  .checkout-page {
    padding: 24px 14px;
  }

  .checkout-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .checkout-header h1 {
    font-size: 30px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .card,
  .order-summary {
    padding: 20px;
    border-radius: 20px;
  }

  .product-box {
    flex-direction: column;
  }

  .product-box img {
    width: 100%;
    height: 220px;
  }
}

.qr-box {
  margin-top: 22px;
  padding: 22px;
  border-radius: 20px;
  background: #f8fafc;
  border: 1px dashed #dc2626;
}

.qr-box h3 {
  margin: 0 0 16px;
  color: #111827;
}

.qr-content {
  display: flex;
  gap: 22px;
  align-items: center;
}

.qr-image {
  width: 220px;
  height: 220px;
  border-radius: 16px;
  background: white;
  border: 1px solid #e5e7eb;
  padding: 10px;
}

.bank-detail p {
  margin: 8px 0;
  color: #374151;
}

.bank-detail strong {
  color: #111827;
}

.bank-detail .money {
  color: #dc2626;
  font-size: 20px;
}

.confirm-transfer {
  margin-top: 18px;
  display: flex;
  gap: 10px;
  align-items: center;
  font-weight: 700;
  color: #111827;
}

.confirm-transfer input {
  width: 18px;
  height: 18px;
}

.coupon-box {
  margin-top: 22px;
  padding: 16px;
  border-radius: 16px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
}

.coupon-box label {
  display: block;
  margin-bottom: 10px;
  font-weight: 800;
  color: #374151;
}

.coupon-input {
  display: flex;
  gap: 10px;
}

.coupon-input input {
  flex: 1;
  padding: 12px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  outline: none;
}

.coupon-input button {
  border: none;
  padding: 12px 16px;
  border-radius: 12px;
  background: #111827;
  color: white;
  font-weight: 800;
  cursor: pointer;
}

.coupon-input button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-remove-coupon {
  background: #dc2626 !important;
}

.coupon-success {
  margin-top: 10px;
  color: #166534;
  font-weight: 700;
}

.coupon-box {
  margin-top: 22px;
  padding: 18px;
  border-radius: 18px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
}

.coupon-box h3 {
  margin: 0 0 14px;
  color: #111827;
  font-size: 17px;
}

.coupon-input {
  display: flex;
  gap: 10px;
}

.coupon-input input {
  flex: 1;
  padding: 12px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  outline: none;
}

.coupon-input button,
.coupon-card button,
.coupon-pagination button {
  border: none;
  padding: 10px 14px;
  border-radius: 12px;
  background: #111827;
  color: white;
  font-weight: 800;
  cursor: pointer;
}

.coupon-input button:disabled,
.coupon-card button:disabled,
.coupon-pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-remove-coupon {
  background: #dc2626 !important;
}

.coupon-error {
  margin-top: 10px;
  color: #dc2626;
  font-weight: 700;
  font-size: 13px;
}

.coupon-success {
  margin-top: 10px;
  color: #166534;
  font-weight: 700;
  font-size: 13px;
}

.coupon-toggle {
  margin-top: 14px;
  padding: 12px;
  border-radius: 12px;
  background: #fff7ed;
  color: #9a3412;
  cursor: pointer;
  font-weight: 700;
}

.coupon-toggle span {
  margin-left: 8px;
  text-decoration: underline;
}

.coupon-list {
  margin-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.coupon-card {
  display: flex;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  overflow: hidden;
  background: white;
}

.coupon-card.eligible {
  border-color: #22c55e;
}

.coupon-card.disabled {
  opacity: 0.65;
}

.coupon-card.applied {
  border-color: #dc2626;
  background: #fef2f2;
}

.coupon-left {
  width: 90px;
  background: #111827;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 4px;
}

.coupon-left strong {
  font-size: 18px;
}

.coupon-left span {
  font-size: 11px;
  font-weight: 800;
}

.coupon-right {
  flex: 1;
  padding: 12px;
}

.coupon-code {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 999px;
  background: #fee2e2;
  color: #991b1b;
  font-weight: 900;
  font-size: 12px;
}

.coupon-right p {
  margin: 8px 0 4px;
  font-weight: 700;
  color: #111827;
}

.coupon-right small {
  display: block;
  color: #6b7280;
  margin-bottom: 6px;
}

.coupon-warning {
  color: #dc2626 !important;
}

.coupon-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 14px;
  margin-top: 8px;
}

.discount-text {
  color: #16a34a;
}
</style>

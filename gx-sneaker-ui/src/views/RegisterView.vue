<script setup>
import { ref } from 'vue'
import { registerApi } from '@/api/authApi'

const hoTen = ref('')
const email = ref('')
const soDienThoai = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')
const loading = ref(false)

const register = async () => {
  try {
    errorMessage.value = ''

    if (password.value !== confirmPassword.value) {
      errorMessage.value = 'Xác nhận mật khẩu không khớp'
      return
    }

    loading.value = true

    await registerApi({
      hoTen: hoTen.value,
      email: email.value,
      soDienThoai: soDienThoai.value,
      password: password.value
    })

    alert('Đăng ký thành công')
    window.location.href = '/login'
  } catch (error) {
    errorMessage.value =
      error.response?.data?.message || 'Đăng ký thất bại'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-wrapper">
    <div class="register-header">
      <div class="brand">
        <span class="brand-main">GX</span>
        <span class="brand-sub">Sneaker</span>
      </div>

      <span class="page-title">Đăng ký</span>

      <router-link to="/login" class="help-link">
        Đăng nhập
      </router-link>
    </div>

    <div class="register-body">
      <div class="register-banner">
        <h1>GX SNEAKER</h1>
        <h2>Tham gia ngay<br />Nhận ưu đãi lớn</h2>
        <p>
          Tạo tài khoản để mua sắm sneaker chính hãng, theo dõi đơn hàng
          và nhận nhiều voucher hấp dẫn.
        </p>
      </div>

      <div class="register-card">
        <h2>Tạo tài khoản</h2>

        <input
          v-model="hoTen"
          class="input"
          placeholder="Họ và tên"
        />

        <input
          v-model="email"
          class="input"
          placeholder="Email"
        />

        <input
          v-model="soDienThoai"
          class="input"
          placeholder="Số điện thoại"
        />

        <input
          v-model="password"
          class="input"
          type="password"
          placeholder="Mật khẩu"
        />

        <input
          v-model="confirmPassword"
          class="input"
          type="password"
          placeholder="Xác nhận mật khẩu"
        />

        <p v-if="errorMessage" class="error">
          {{ errorMessage }}
        </p>

        <button
          class="register-btn"
          @click="register"
          :disabled="loading"
        >
          {{ loading ? 'Đang đăng ký...' : 'ĐĂNG KÝ' }}
        </button>

        <p class="policy">
          Bằng việc đăng ký, bạn đã đồng ý với
          <span>Điều khoản dịch vụ</span> &
          <span>Chính sách bảo mật</span>
        </p>

        <div class="divider">
          <span></span>
          <p>HOẶC</p>
          <span></span>
        </div>

        <div class="social-row">
          <button>Facebook</button>
          <button>Google</button>
        </div>

        <div class="login-row">
          <span>Bạn đã có tài khoản?</span>
          <router-link to="/login">Đăng nhập</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-wrapper {
  min-height: 100vh;
  background: #fff;
}

.register-header {
  height: 84px;
  display: flex;
  align-items: center;
  padding: 0 13%;
  background: #fff;
  gap: 16px;
}

.brand {
  font-weight: 800;
  font-size: 30px;
}

.brand-main {
  color: #ee1c25;
  margin-right: 8px;
}

.brand-sub {
  color: #111;
}

.page-title {
  font-size: 24px;
  color: #222;
}

.help-link {
  margin-left: auto;
  color: #ee1c25;
  text-decoration: none;
  font-size: 15px;
}

.register-body {
  min-height: calc(100vh - 84px);
  background: linear-gradient(135deg, #f01822, #ff4a2d);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 80px;
  padding: 50px 10%;
}

.register-banner {
  color: #fff;
  width: 520px;
}

.register-banner h1 {
  font-size: 42px;
  font-weight: 900;
  margin-bottom: 18px;
}

.register-banner h2 {
  font-size: 52px;
  line-height: 1.15;
  text-transform: uppercase;
  font-weight: 900;
  margin-bottom: 24px;
}

.register-banner p {
  font-size: 19px;
  line-height: 1.6;
  opacity: 0.95;
}

.register-card {
  width: 430px;
  background: #fff;
  border-radius: 4px;
  padding: 32px;
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.18);
}

.register-card h2 {
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 24px;
}

.input {
  width: 100%;
  height: 44px;
  border: 1px solid #ddd;
  padding: 0 14px;
  margin-bottom: 14px;
  font-size: 15px;
  outline: none;
}

.input:focus {
  border-color: #ee1c25;
}

.register-btn {
  width: 100%;
  height: 46px;
  border: none;
  background: #ee1c25;
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  margin-top: 6px;
}

.register-btn:hover {
  background: #d9151f;
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error {
  color: #ee1c25;
  font-size: 14px;
  margin-bottom: 10px;
}

.policy {
  font-size: 12px;
  color: #666;
  text-align: center;
  line-height: 1.5;
  margin: 16px 0;
}

.policy span {
  color: #ee1c25;
}

.divider {
  display: flex;
  align-items: center;
  gap: 14px;
  color: #bbb;
  font-size: 13px;
  margin-bottom: 18px;
}

.divider span {
  height: 1px;
  background: #e5e5e5;
  flex: 1;
}

.social-row {
  display: flex;
  gap: 12px;
}

.social-row button {
  flex: 1;
  height: 42px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
}

.login-row {
  margin-top: 24px;
  text-align: center;
  color: #999;
}

.login-row a {
  color: #ee1c25;
  margin-left: 6px;
  text-decoration: none;
  font-weight: 600;
}
</style>

<script setup>
import { ref } from 'vue'
import { loginApi, staffLoginApi } from '@/api/authApi'
import { useAuth } from '@/composables/useAuth'

const username = ref('')
const password = ref('')
const loginType = ref('CUSTOMER')
const errorMessage = ref('')

const { setLogin } = useAuth()

const login = async () => {
  try {
    errorMessage.value = ''

    const data = {
      username: username.value,
      password: password.value
    }

    const response =
      loginType.value === 'STAFF'
        ? await staffLoginApi(data)
        : await loginApi(data)

    setLogin(response.data)

    if (response.data.role === 'ADMIN') {
      window.location.href = '/admin'
    } else if (response.data.role === 'STAFF') {
      window.location.href = '/staff'
    } else {
      window.location.href = '/'
    }
  } catch (error) {
    errorMessage.value =
      error.response?.data?.message || 'Đăng nhập thất bại'
  }
}
</script>

<template>
  <div class="login-wrapper">
    <div class="login-header">
      <div class="brand">
        <span class="brand-main">GX</span>
        <span class="brand-sub">Sneaker</span>
      </div>
      <span class="login-title">Đăng nhập</span>
      <router-link to="/" class="help-link">Về trang chủ</router-link>
    </div>

    <div class="login-body">
      <div class="login-banner">
        <h1>GX SNEAKER</h1>
        <h2>Bước chất riêng<br />Sống đam mê</h2>
        <p>Nơi hội tụ những đôi giày sneaker đỉnh cao dành cho bạn.</p>
      </div>

      <div class="login-card">
        <div class="card-top">
          <h2>Đăng nhập</h2>
          <div class="qr-box">GX</div>
        </div>

        <select v-model="loginType" class="input">
          <option value="CUSTOMER">Khách hàng</option>
          <option value="STAFF">Nhân viên / Admin</option>
        </select>

        <input
          v-model="username"
          class="input"
          placeholder="Số điện thoại / Email"
        />

        <input
          v-model="password"
          class="input"
          type="password"
          placeholder="Mật khẩu"
        />

        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>

        <button class="login-btn" @click="login">
          ĐĂNG NHẬP
        </button>

        <div class="forgot-row">
          <router-link to="/forgot-password">Quên mật khẩu</router-link>
        </div>

        <div class="divider">
          <span></span>
          <p>HOẶC</p>
          <span></span>
        </div>

        <div class="social-row">
          <button>Facebook</button>
          <button>Google</button>
        </div>

        <div class="register-row">
          <span>Bạn mới biết đến GX Sneaker?</span>
          <router-link to="/register">Đăng ký</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  background: #fff;
}

.login-header {
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

.login-title {
  font-size: 24px;
  color: #222;
}

.help-link {
  margin-left: auto;
  color: #ee1c25;
  text-decoration: none;
  font-size: 14px;
}

.login-body {
  min-height: calc(100vh - 84px);
  background: linear-gradient(135deg, #f01822, #ff4a2d);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 80px;
  padding: 60px 10%;
}

.login-banner {
  color: #fff;
  width: 520px;
}

.login-banner h1 {
  font-size: 42px;
  font-weight: 900;
  margin-bottom: 18px;
}

.login-banner h2 {
  font-size: 56px;
  line-height: 1.15;
  text-transform: uppercase;
  font-weight: 900;
  margin-bottom: 24px;
}

.login-banner p {
  font-size: 20px;
  opacity: 0.95;
}

.login-card {
  width: 420px;
  background: #fff;
  border-radius: 4px;
  padding: 32px;
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.18);
}

.card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28px;
}

.card-top h2 {
  font-size: 24px;
  font-weight: 500;
}

.qr-box {
  border: 2px solid #ee1c25;
  color: #ee1c25;
  font-weight: 800;
  padding: 10px 12px;
  border-radius: 4px;
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

.login-btn {
  width: 100%;
  height: 46px;
  border: none;
  background: #ee1c25;
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  margin-top: 8px;
}

.login-btn:hover {
  background: #d9151f;
}

.error {
  color: #ee1c25;
  font-size: 14px;
  margin-bottom: 10px;
}

.forgot-row {
  margin: 14px 0 22px;
}

.forgot-row a {
  color: #0066cc;
  text-decoration: none;
  font-size: 14px;
}

.divider {
  display: flex;
  align-items: center;
  gap: 14px;
  color: #bbb;
  font-size: 13px;
  margin-bottom: 20px;
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

.register-row {
  margin-top: 26px;
  text-align: center;
  color: #999;
}

.register-row a {
  color: #ee1c25;
  margin-left: 6px;
  text-decoration: none;
  font-weight: 600;
}
</style>

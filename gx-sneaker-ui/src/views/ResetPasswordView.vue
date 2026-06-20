<script setup>
import { ref } from 'vue'
import { resetPasswordApi } from '@/api/authApi'

const email = ref(localStorage.getItem('resetEmail') || '')
const otp = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')
const loading = ref(false)

const resetPassword = async () => {
  try {
    errorMessage.value = ''
    loading.value = true

    await resetPasswordApi({
      email: email.value,
      otp: otp.value,
      newPassword: newPassword.value,
      confirmPassword: confirmPassword.value,
    })

    alert('Đặt lại mật khẩu thành công')
    localStorage.removeItem('resetEmail')
    window.location.href = '/login'
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Đặt lại mật khẩu thất bại'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="reset-page">
    <div class="reset-header">
      <div class="brand">
        <span class="logo-icon">GX</span>
        <span class="brand-name">Sneaker</span>
      </div>

      <span class="page-title">Reset Password</span>

      <router-link to="/login" class="help-link"> Need help? </router-link>
    </div>

    <div class="reset-body">
      <div class="reset-card">
        <div class="card-title">
          <router-link to="/forgot-password" class="back-btn"> ← </router-link>

          <h2>Enter Verification Code</h2>
        </div>

        <p class="desc">Mã OTP đã được gửi về email</p>

        <p class="email-text">
          {{ email }}
        </p>

        <input v-model="otp" class="otp-input" maxlength="6" placeholder="Nhập mã OTP" />

        <input v-model="newPassword" class="input" type="password" placeholder="Mật khẩu mới" />

        <input
          v-model="confirmPassword"
          class="input"
          type="password"
          placeholder="Xác nhận mật khẩu"
        />

        <p v-if="errorMessage" class="error">
          {{ errorMessage }}
        </p>

        <button class="next-btn" @click="resetPassword" :disabled="loading">
          {{ loading ? 'Đang xử lý...' : 'NEXT' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.reset-page {
  min-height: 100vh;
  background: #f7f7f7;
}

.reset-header {
  height: 86px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 0 13%;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  background: #ee1c25;
  color: #fff;
  font-weight: 800;
  font-size: 22px;
  padding: 8px 10px;
  border-radius: 6px;
}

.brand-name {
  color: #ee1c25;
  font-size: 30px;
  font-weight: 500;
}

.page-title {
  font-size: 28px;
  margin-left: 18px;
  color: #222;
}

.help-link {
  margin-left: auto;
  color: #ee1c25;
  text-decoration: none;
  font-size: 15px;
}

.reset-body {
  min-height: calc(100vh - 86px);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 70px;
}

.reset-card {
  width: 560px;
  min-height: 520px;
  background: #fff;
  padding: 38px 88px 52px;
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.12);
  border-radius: 4px;
}

.card-title {
  display: flex;
  align-items: center;
  margin-bottom: 34px;
  position: relative;
}

.card-title h2 {
  flex: 1;
  text-align: center;
  font-size: 24px;
  font-weight: 400;
  color: #222;
}

.back-btn {
  position: absolute;
  left: -38px;
  color: #ee1c25;
  font-size: 36px;
  text-decoration: none;
  font-weight: 300;
}

.desc {
  text-align: center;
  color: #777;
  font-size: 15px;
  margin-bottom: 8px;
}

.email-text {
  text-align: center;
  color: #555;
  font-size: 16px;
  margin-bottom: 30px;
}

.otp-input {
  width: 100%;
  height: 46px;
  border: none;
  border-bottom: 2px solid #ddd;
  text-align: center;
  font-size: 26px;
  letter-spacing: 12px;
  outline: none;
  margin-bottom: 24px;
}

.otp-input:focus {
  border-bottom-color: #ee1c25;
}

.input {
  width: 100%;
  height: 46px;
  border: 1px solid #ddd;
  padding: 0 14px;
  font-size: 15px;
  outline: none;
  margin-bottom: 14px;
}

.input:focus {
  border-color: #ee1c25;
}

.next-btn {
  width: 100%;
  height: 48px;
  border: none;
  background: #f47b6b;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  margin-top: 12px;
}

.next-btn:hover {
  background: #ee1c25;
}

.next-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error {
  color: #ee1c25;
  font-size: 14px;
  margin-bottom: 10px;
}
</style>

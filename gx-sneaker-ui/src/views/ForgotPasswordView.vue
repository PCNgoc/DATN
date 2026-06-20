<script setup>
import { ref } from 'vue'
import { forgotPasswordApi } from '@/api/authApi'

const email = ref('')
const errorMessage = ref('')
const loading = ref(false)

const sendOtp = async () => {
  try {
    errorMessage.value = ''
    loading.value = true

    await forgotPasswordApi({
      email: email.value
    })

    localStorage.setItem('resetEmail', email.value)
    window.location.href = '/reset-password'
  } catch (error) {
    errorMessage.value =
      error.response?.data?.message || 'Gửi OTP thất bại'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="forgot-page">
    <div class="forgot-header">
      <div class="brand">
        <span class="logo-icon">GX</span>
        <span class="brand-name">Sneaker</span>
      </div>

      <span class="page-title">Reset Password</span>

      <router-link to="/login" class="help-link">
        Need help?
      </router-link>
    </div>

    <div class="forgot-body">
      <div class="forgot-card">
        <div class="card-title">
          <router-link to="/login" class="back-btn">
            ←
          </router-link>

          <h2>Reset Password</h2>
        </div>

        <input
          v-model="email"
          class="input"
          placeholder="Email"
        />

        <p v-if="errorMessage" class="error">
          {{ errorMessage }}
        </p>

        <button
          class="next-btn"
          @click="sendOtp"
          :disabled="loading"
        >
          {{ loading ? 'Đang gửi...' : 'NEXT' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.forgot-page {
  min-height: 100vh;
  background: #f7f7f7;
}

.forgot-header {
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

.forgot-body {
  min-height: calc(100vh - 86px);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 175px;
}

.forgot-card {
  width: 560px;
  background: #fff;
  padding: 42px 88px 52px;
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.12);
  border-radius: 4px;
}

.card-title {
  display: flex;
  align-items: center;
  margin-bottom: 52px;
  position: relative;
}

.card-title h2 {
  flex: 1;
  text-align: center;
  font-size: 26px;
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

.input {
  width: 100%;
  height: 46px;
  border: 1px solid #ddd;
  padding: 0 14px;
  font-size: 16px;
  outline: none;
  margin-bottom: 26px;
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
  margin-top: -14px;
  margin-bottom: 14px;
}
</style>

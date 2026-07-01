<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminLoginApi, saveAdminSession } from '@/api/authApi'

const router = useRouter()

const username = ref('')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')

const goHomeByRole = (role) => {
  if (role === 'ADMIN') {
    router.push('/admin/dashboard')
    return
  }

  if (role === 'STAFF') {
    router.push('/admin/hoa-don')
    return
  }

  router.push('/admin/login')
}

const handleLogin = async () => {
  errorMessage.value = ''

  if (!username.value.trim()) {
    errorMessage.value = 'Vui lòng nhập email hoặc số điện thoại'
    return
  }

  if (!password.value.trim()) {
    errorMessage.value = 'Vui lòng nhập mật khẩu'
    return
  }

  try {
    loading.value = true

    const res = await adminLoginApi({
      username: username.value.trim(),
      password: password.value.trim(),
    })

    const role = saveAdminSession(res.data)

    if (role !== 'ADMIN' && role !== 'STAFF') {
      errorMessage.value = 'Tài khoản chưa được phân quyền ADMIN hoặc STAFF'
      return
    }

    goHomeByRole(role)
  } catch (err) {
    errorMessage.value = err.response?.data?.message || err.response?.data || 'Đăng nhập thất bại'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  localStorage.removeItem('adminToken')
  localStorage.removeItem('adminRole')
  localStorage.removeItem('adminUser')
})
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="brand">
        <div class="logo">GX</div>
        <h1>GX Sneaker</h1>
        <p>Đăng nhập quản trị</p>
      </div>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>Email hoặc số điện thoại</label>
          <input v-model="username" type="text" placeholder="Nhập email hoặc số điện thoại" />
        </div>

        <div class="form-group">
          <label>Mật khẩu</label>
          <input v-model="password" type="password" placeholder="Nhập mật khẩu" />
        </div>

        <p v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </p>

        <button type="submit" :disabled="loading">
          {{ loading ? 'Đang đăng nhập...' : 'Đăng nhập' }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #111827, #1f2937);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 430px;
  background: white;
  border-radius: 24px;
  padding: 34px;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.25);
}

.brand {
  text-align: center;
  margin-bottom: 28px;
}

.logo {
  width: 62px;
  height: 62px;
  margin: 0 auto 14px;
  border-radius: 18px;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 24px;
}

.brand h1 {
  margin: 0;
  color: #111827;
  font-size: 30px;
  font-weight: 900;
}

.brand p {
  margin: 8px 0 0;
  color: #6b7280;
}

.form-group {
  margin-bottom: 18px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #374151;
  font-weight: 800;
}

.form-group input {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  outline: none;
  font-size: 15px;
  background: #f9fafb;
}

.form-group input:focus {
  border-color: #ef4444;
  background: white;
  box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.1);
}

.error-message {
  margin: 0 0 14px;
  color: #dc2626;
  font-weight: 700;
  font-size: 14px;
}

button {
  width: 100%;
  border: none;
  border-radius: 14px;
  padding: 15px;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  font-size: 16px;
  font-weight: 900;
  cursor: pointer;
}

button:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}
</style>

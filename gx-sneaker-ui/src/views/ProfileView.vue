<script setup>
import { ref, onMounted } from 'vue'
import { getMeApi } from '@/api/authApi'

const user = ref(null)
const errorMessage = ref('')

onMounted(async () => {
  try {
    const response = await getMeApi()
    user.value = response.data
  } catch (error) {
    errorMessage.value = 'Không thể lấy thông tin tài khoản'
  }
})
</script>

<template>
  <div class="profile-page">
    <h2>Tài khoản của tôi</h2>

    <p v-if="errorMessage" class="error">
      {{ errorMessage }}
    </p>

    <div v-if="user" class="profile-card">
      <p><b>Mã khách hàng:</b> {{ user.maKhachHang }}</p>
      <p><b>Họ tên:</b> {{ user.hoTen }}</p>
      <p><b>Email:</b> {{ user.email }}</p>
      <p><b>Số điện thoại:</b> {{ user.soDienThoai }}</p>
    </div>
  </div>
</template>

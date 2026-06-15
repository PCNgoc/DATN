<script setup>
import { ref } from 'vue'
import { changePasswordApi } from '@/api/authApi'

const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')
const successMessage = ref('')

const changePassword = async () => {
  try {
    errorMessage.value = ''
    successMessage.value = ''

    await changePasswordApi({
      oldPassword: oldPassword.value,
      newPassword: newPassword.value,
      confirmPassword: confirmPassword.value,
    })

    successMessage.value = 'Đổi mật khẩu thành công'

    oldPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
  } catch (error) {
    errorMessage.value = error.response?.data?.message || 'Đổi mật khẩu thất bại'
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-box">
      <h2>Đổi mật khẩu</h2>

      <input v-model="oldPassword" type="password" placeholder="Mật khẩu cũ" />

      <input v-model="newPassword" type="password" placeholder="Mật khẩu mới" />

      <input v-model="confirmPassword" type="password" placeholder="Xác nhận mật khẩu" />

      <p v-if="successMessage" class="success">{{ successMessage }}</p>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>

      <button @click="changePassword">Đổi mật khẩu</button>
    </div>
  </div>
</template>

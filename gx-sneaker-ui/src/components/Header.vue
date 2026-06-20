<script setup>
import { useAuth } from '@/composables/useAuth'

const { user, logout } = useAuth()
</script>

<template>
  <header class="navbar">
    <div class="logo">
      <span class="gx">GX</span>
      <span class="sneaker">SNEAKER</span>
    </div>

    <nav class="menu">
      <router-link to="/">Trang chủ</router-link>
      <a href="#">Nam</a>
      <a href="#">Nữ</a>
      <a href="#">Thương hiệu</a>
      <a href="#">Bộ sưu tập</a>
      <a href="#">Sale</a>
    </nav>

    <div class="search-box">
      <input type="text" placeholder="Tìm kiếm sản phẩm..." />
      <button>
        <i class="fas fa-search"></i>
      </button>
    </div>

    <div class="actions">
      <div v-if="user" class="action-item user-box">
        <i class="far fa-user"></i>
        <span>Xin chào {{ user.hoTen }}</span>
        <button class="logout-btn" @click="logout">Đăng xuất</button>
      </div>

      <router-link v-else to="/login" class="action-item">
        <i class="far fa-user"></i>
        <span>Đăng nhập</span>
      </router-link>

      <router-link v-if="user" to="/profile"> Xin chào {{ user.hoTen }} </router-link>

      <div class="action-item">
        <div class="icon-wrap">
          <i class="far fa-heart"></i>
        </div>
        <span>Yêu thích</span>
      </div>

      <router-link to="/cart" class="action-item">
        <div class="icon-wrap">
          <i class="fas fa-cart-shopping"></i>
        </div>
        <span>Giỏ hàng</span>
      </router-link>
    </div>
  </header>
</template>

<style scoped>
.action-item {
  text-decoration: none;
  color: inherit;
}

.user-box {
  gap: 8px;
}

.logout-btn {
  border: none;
  background: transparent;
  cursor: pointer;
  color: red;
}
</style>

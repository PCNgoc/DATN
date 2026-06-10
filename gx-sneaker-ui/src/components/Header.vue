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
      <router-link to="/nam">Nam</router-link>
      <router-link to="/nu">Nữ</router-link>
      <router-link to="/bo-suu-tap">Bộ sưu tập</router-link>
      <router-link to="/sale">Sale</router-link>

      <div class="dropdown">
        <span class="drop-title">Danh mục ▾</span>
        <div class="dropdown-menu">
          <router-link to="/thuong-hieu">Thương hiệu</router-link>
          <router-link to="/danh-muc">Danh mục</router-link>
          <router-link to="/xuat-xu">Xuất xứ</router-link>
          <router-link to="/chat-lieu">Chất liệu</router-link>
          <router-link to="/co-giay">Cổ giày</router-link>
          <router-link to="/de-giay">Đế giày</router-link>
          <router-link to="/mau-sac">Màu sắc</router-link>
          <router-link to="/kich-thuoc">Kích thước</router-link>
        </div>
      </div>
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
        <router-link to="/profile">Xin chào {{ user.hoTen }}</router-link>
        <button class="logout-btn" @click="logout">Đăng xuất</button>
      </div>

      <router-link v-else to="/login" class="action-item">
        <i class="far fa-user"></i>
        <span>Đăng nhập</span>
      </router-link>

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

.user-box a {
  text-decoration: none;
  color: inherit;
}

.logout-btn {
  border: none;
  background: transparent;
  cursor: pointer;
  color: red;
}

.dropdown {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.drop-title {
  color: #0e0e0e;
  padding: 8px 10px;
}

.dropdown-menu {
  display: none;
  position: absolute;
  top: 100%;
  left: 0;
  background: white;
  min-width: 180px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  border-radius: 6px;
  z-index: 999;
}

.dropdown-menu a {
  display: block;
  padding: 10px;
  color: black;
  text-decoration: none;
}

.dropdown-menu a:hover {
  background: #f5f5f5;
}

.dropdown:hover .dropdown-menu {
  display: block;
}
</style>

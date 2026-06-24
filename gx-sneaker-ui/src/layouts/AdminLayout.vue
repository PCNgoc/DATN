<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const adminName = localStorage.getItem('adminName')
const role = localStorage.getItem('adminRole')

const logout = () => {
  localStorage.removeItem('adminToken')
  localStorage.removeItem('adminRole')
  localStorage.removeItem('adminName')

  router.push('/admin/login')
}
</script>

<template>
  <div class="admin-layout">

    <!-- Sidebar -->
    <aside class="sidebar">

      <div class="brand">
        <div class="logo">GX</div>
        <div>
          <h2>GX Sneaker</h2>
          <span>Admin Panel</span>
        </div>
      </div>

      <div class="menu-group">

        <p class="menu-title">QUẢN TRỊ</p>

        <router-link to="/admin/dashboard">
          🏠 Dashboard
        </router-link>

      </div>

      <div class="menu-group">

        <p class="menu-title">SẢN PHẨM</p>

        <router-link to="/admin/san-pham">
          👟 Sản phẩm
        </router-link>

        <router-link to="/admin/danh-muc">
          📂 Danh mục
        </router-link>

        <router-link to="/admin/thuong-hieu">
          🏷 Thương hiệu
        </router-link>

        <router-link to="/admin/chat-lieu">
          🧵 Chất liệu
        </router-link>

        <router-link to="/admin/co-giay">
          👞 Cổ giày
        </router-link>

        <router-link to="/admin/de-giay">
          👟 Đế giày
        </router-link>

        <router-link to="/admin/mau-sac">
          🎨 Màu sắc
        </router-link>

        <router-link to="/admin/kich-thuoc">
          📏 Kích thước
        </router-link>

        <router-link to="/admin/xuat-xu">
          🌎 Xuất xứ
        </router-link>

      </div>

      <div class="menu-group">

        <p class="menu-title">BÁN HÀNG</p>

        <router-link to="/admin/hoa-don">
          🧾 Hóa đơn
        </router-link>

      </div>

    </aside>

    <!-- Main -->
    <div class="main-content">

      <!-- Header -->
      <header class="topbar">

        <div>
          <h3>Trang quản trị GX Sneaker</h3>
        </div>

        <div class="admin-info">

          <div class="user-box">
            <span class="name">{{ adminName }}</span>
            <span class="role">{{ role }}</span>
          </div>

          <button @click="logout">
            Đăng xuất
          </button>

        </div>

      </header>

      <!-- Content -->
      <div class="page-content">
        <router-view />
      </div>

    </div>

  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f4f6f9;
}

/* SIDEBAR */

.sidebar {
  width: 260px;
  background: #111827;
  color: white;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  overflow-y: auto;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  border-bottom: 1px solid #1f2937;
}

.logo {
  width: 50px;
  height: 50px;
  background: #ef4444;
  border-radius: 10px;

  display: flex;
  justify-content: center;
  align-items: center;

  font-weight: bold;
  font-size: 20px;
}

.brand h2 {
  margin: 0;
  font-size: 18px;
}

.brand span {
  font-size: 12px;
  color: #9ca3af;
}

.menu-group {
  margin-top: 20px;
}

.menu-title {
  color: #9ca3af;
  font-size: 12px;
  padding: 0 20px;
  margin-bottom: 10px;
}

.sidebar a {
  display: block;
  margin: 5px 12px;
  padding: 12px 15px;
  border-radius: 10px;

  color: #d1d5db;
  text-decoration: none;

  transition: 0.3s;
}

.sidebar a:hover {
  background: #1f2937;
}

.router-link-active {
  background: #ef4444 !important;
  color: white !important;
}

/* MAIN */

.main-content {
  margin-left: 260px;
  width: calc(100% - 260px);
}

/* TOPBAR */

.topbar {
  height: 70px;
  background: white;

  display: flex;
  justify-content: space-between;
  align-items: center;

  padding: 0 25px;

  box-shadow: 0 2px 10px rgba(0,0,0,.05);
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-box {
  display: flex;
  flex-direction: column;
}

.name {
  font-weight: 600;
}

.role {
  font-size: 12px;
  color: #ef4444;
}

.topbar button {
  border: none;
  background: #ef4444;
  color: white;
  padding: 10px 18px;
  border-radius: 8px;
  cursor: pointer;
}

.topbar button:hover {
  background: #dc2626;
}

/* CONTENT */

.page-content {
  padding: 25px;
}

/* MOBILE */

@media (max-width: 900px) {

  .sidebar {
    width: 220px;
  }

  .main-content {
    margin-left: 220px;
    width: calc(100% - 220px);
  }

}
</style>

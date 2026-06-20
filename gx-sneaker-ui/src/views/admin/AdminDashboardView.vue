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
  <div class="admin-page">
    <!-- HEADER -->

    <header class="navbar">
      <div class="logo">
        <span class="gx">GX</span>
        <span class="sneaker">ADMIN</span>
      </div>

      <div class="admin-info">
        <span>Xin chào {{ adminName }}</span>

        <span class="role">
          {{ role }}
        </span>

        <button @click="logout">Đăng xuất</button>
      </div>
    </header>

    <!-- BODY -->

    <div class="layout">
      <!-- SIDEBAR -->

      <aside class="sidebar">
        <router-link to="/admin/dashboard"> Dashboard </router-link>

        <router-link to="/chat-lieu"> Chất liệu </router-link>

        <router-link to="/co-giay"> Cổ giày </router-link>

        <router-link to="/danh-muc"> Danh mục </router-link>

        <router-link to="/san-pham"> Sản phẩm </router-link>

        <router-link to="/hoa-don"> Hóa đơn </router-link>
      </aside>

      <!-- CONTENT -->

      <main class="content">
        <h1>Dashboard Quản Trị</h1>

        <div class="cards">
          <div class="card">
            <h3>Sản phẩm</h3>
            <p>120</p>
          </div>

          <div class="card">
            <h3>Đơn hàng</h3>
            <p>50</p>
          </div>

          <div class="card">
            <h3>Khách hàng</h3>
            <p>300</p>
          </div>

          <div class="card">
            <h3>Doanh thu</h3>
            <p>150.000.000đ</p>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.navbar {
  height: 70px;
  background: #111;
  color: white;

  display: flex;
  justify-content: space-between;
  align-items: center;

  padding: 0 30px;
}

.logo {
  font-size: 28px;
  font-weight: bold;
}

.gx {
  color: red;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.admin-info button {
  background: red;
  border: none;
  color: white;
  padding: 8px 14px;
  cursor: pointer;
}

.layout {
  display: flex;
}

.sidebar {
  width: 250px;
  min-height: calc(100vh - 70px);

  background: white;
  border-right: 1px solid #ddd;

  display: flex;
  flex-direction: column;
}

.sidebar a {
  padding: 16px 20px;
  text-decoration: none;
  color: #333;
  border-bottom: 1px solid #eee;
}

.sidebar a:hover {
  background: #f3f3f3;
}

.content {
  flex: 1;
  padding: 30px;
}

.cards {
  margin-top: 30px;

  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.card {
  background: white;
  padding: 25px;
  border-radius: 10px;

  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.card h3 {
  margin-bottom: 10px;
}

.card p {
  font-size: 28px;
  font-weight: bold;
  color: red;
}
</style>

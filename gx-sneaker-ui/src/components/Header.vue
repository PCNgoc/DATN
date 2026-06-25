
<script setup>
import { useAuth } from '@/composables/useAuth'

const { user, logout } = useAuth()
</script>

<template>

  <div class="top-bar">
    <div class="marquee">

      <span>👟 GX Sneaker - Nâng tầm phong cách của bạn</span>

      <span>🔥 Bộ sưu tập Sneaker 2026 mới nhất đã cập bến</span>

      <span>🚚 Miễn phí giao hàng toàn quốc</span>

      <span>💎 Chất lượng tạo nên đẳng cấp</span>

      <span>⭐ Hơn 10.000 khách hàng đã lựa chọn GX Sneaker</span>

      <span>🎁 Ưu đãi độc quyền dành cho thành viên GX Club</span>

    </div>
  </div>

  <!-- HEADER -->
  <header class="navbar">

    <!-- LOGO -->
    <router-link to="/" class="logo">
      <div class="logo-icon">
        GX
      </div>

      <div class="logo-content">
        <div class="logo-title">
          GX SNEAKER
        </div>

        <div class="logo-subtitle">
          Premium Sneaker Store
        </div>
      </div>
    </router-link>

    <!-- MENU -->
    <nav class="menu">

      <router-link to="/">
        Trang chủ
      </router-link>

      <router-link to="/products">
        Cửa hàng
      </router-link>

      <router-link to="/new-arrival">
        New Arrival
      </router-link>

      <router-link to="/sale" class="sale-link">
        🔥 Sale
      </router-link>

    </nav>

    <!-- SEARCH -->
    <div class="search-box">

      <i class="fas fa-search"></i>

      <input
        type="text"
        placeholder="Tìm kiếm sản phẩm..."
      />

    </div>

    <!-- ACTIONS -->
    <div class="actions">

      <!-- USER -->
      <div
        v-if="user"
        class="user-dropdown"
      >

        <div class="user-trigger">

          <div class="avatar">
            {{ user.hoTen?.charAt(0) }}
          </div>

          <span>
            {{ user.hoTen }}
          </span>

        </div>

        <div class="user-menu">

          <router-link to="/profile">
            👤 Hồ sơ cá nhân
          </router-link>

          <router-link to="/orders">
            📦 Đơn hàng
          </router-link>

          <button @click="logout">
            🚪 Đăng xuất
          </button>

        </div>

      </div>

      <!-- LOGIN -->
      <router-link
        v-else
        to="/login"
        class="action-item"
      >
        <div class="icon-wrap">
          <i class="far fa-user"></i>
        </div>

        <span>Đăng nhập</span>
      </router-link>

      <!-- WISHLIST -->
      <router-link
        to="/wishlist"
        class="action-item"
      >
        <div class="icon-wrap">

          <i class="far fa-heart"></i>

          <span class="badge">
            0
          </span>

        </div>

        <span>Yêu thích</span>
      </router-link>

      <!-- CART -->
      <router-link
        to="/cart"
        class="action-item"
      >
        <div class="icon-wrap">

          <i class="fas fa-cart-shopping"></i>

          <span class="badge">
            0
          </span>

        </div>

        <span>Giỏ hàng</span>
      </router-link>

    </div>

  </header>

</template>

<style scoped>

*{
  box-sizing:border-box;
}

.top-bar{
  height:40px;
  background:#111;
  color:#fff;
  overflow:hidden;
  position:relative;
}

.marquee{
  display:flex;
  align-items:center;
  gap:80px;

  width:max-content;
  height:100%;

  animation: scroll-left 20s linear infinite;
}

.marquee span{
  white-space:nowrap;
  font-size:13px;
  font-weight:500;
}

@keyframes scroll-left{
  from{
    transform:translateX(0);
  }

  to{
    transform:translateX(-50%);
  }
}
.navbar{
  height:90px;

  display:flex;
  align-items:center;
  justify-content:space-between;

  padding:0 50px;

  background:white;

  border-bottom:1px solid #eee;
}

.logo{
  display:flex;
  align-items:center;
  gap:12px;

  text-decoration:none;
}

.logo-icon{
  width:55px;
  height:55px;

  border-radius:16px;

  background:linear-gradient(
    135deg,
    #000,
    #444
  );

  color:white;

  display:flex;
  align-items:center;
  justify-content:center;

  font-weight:800;
  font-size:18px;

  transition:.3s;
}

.logo:hover .logo-icon{
  transform:rotate(-8deg);
}

.logo-title{
  color:#111;
  font-size:24px;
  font-weight:800;
}

.logo-subtitle{
  color:#888;
  font-size:12px;
}

.menu{
  display:flex;
  gap:35px;
}

.menu a{
  color:#222;
  text-decoration:none;
  font-weight:600;

  position:relative;
}

.menu a::after{
  content:"";

  position:absolute;

  left:0;
  bottom:-8px;

  width:0;
  height:2px;

  background:black;

  transition:.3s;
}

.menu a:hover::after{
  width:100%;
}

.sale-link{
  color:#ff4d4f !important;
}

.search-box{
  width:420px;
  height:52px;

  display:flex;
  align-items:center;

  padding:0 20px;

  background:#f7f7f7;

  border-radius:60px;

  transition:.3s;
}

.search-box:focus-within{
  background:white;

  box-shadow:
    0 0 0 2px #111,
    0 10px 30px rgba(0,0,0,.08);
}

.search-box input{
  flex:1;

  border:none;
  outline:none;

  background:transparent;

  padding-left:10px;
}

.actions{
  display:flex;
  align-items:center;
  gap:18px;
}

.action-item{
  display:flex;
  flex-direction:column;
  align-items:center;

  gap:5px;

  text-decoration:none;
  color:#111;

  font-size:12px;
}

.icon-wrap{
  position:relative;

  width:48px;
  height:48px;

  border-radius:14px;

  background:#f6f6f6;

  display:flex;
  align-items:center;
  justify-content:center;

  transition:.3s;
}

.action-item:hover .icon-wrap{
  transform:translateY(-3px);

  background:#111;
  color:white;
}

.badge{
  position:absolute;

  top:-6px;
  right:-6px;

  width:18px;
  height:18px;

  border-radius:50%;

  background:red;
  color:white;

  display:flex;
  align-items:center;
  justify-content:center;

  font-size:10px;
}

.user-dropdown{
  position:relative;
}

.user-trigger{
  display:flex;
  align-items:center;
  gap:10px;

  cursor:pointer;
}

.avatar{
  width:40px;
  height:40px;

  border-radius:50%;

  background:#111;
  color:white;

  display:flex;
  align-items:center;
  justify-content:center;

  font-weight:700;
}

.user-menu{
  position:absolute;

  top:120%;
  right:0;

  width:240px;

  background:white;

  border-radius:16px;

  overflow:hidden;

  box-shadow:
    0 15px 35px rgba(0,0,0,.15);

  opacity:0;
  visibility:hidden;

  transition:.25s;
}

.user-dropdown:hover .user-menu{
  opacity:1;
  visibility:visible;
}

.user-menu a,
.user-menu button{
  width:100%;

  padding:14px;

  display:block;

  border:none;

  background:none;

  text-align:left;

  text-decoration:none;

  color:#333;

  cursor:pointer;
}

.user-menu a:hover,
.user-menu button:hover{
  background:#f7f7f7;
}

@media(max-width:992px){

  .navbar{
    flex-wrap:wrap;
    height:auto;
    padding:20px;
    gap:15px;
  }

  .search-box{
    width:100%;
  }

}

@media(max-width:768px){

  .menu{
    display:none;
  }

  .top-bar{
    display:none;
  }

}

</style>
```

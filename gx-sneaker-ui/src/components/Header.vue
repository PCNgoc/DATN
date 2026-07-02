<script setup>
import { ref,onMounted,watch } from "vue"
import { useAuth } from "@/composables/useAuth"
import { useCart } from "@/composables/useCart"

const { user,logout } = useAuth()

const {
  cartCount,
  fetchCartCount
} = useCart()

//=========================
// FAVORITE
//=========================

const favoriteCount = ref(0)

const loadFavoriteCount=()=>{

  const favorites=
    JSON.parse(localStorage.getItem("favorites")) || []

  favoriteCount.value=favorites.length

}

//=========================
// LOAD
//=========================

const loadData=()=>{

  if(user.value?.id){

    fetchCartCount(user.value.id)

  }else{

    fetchCartCount(null)

  }

  loadFavoriteCount()

}

//=========================
// WATCH USER
//=========================

watch(

  ()=>user.value,

  ()=>{

    if(user.value?.id){

      fetchCartCount(user.value.id)

    }else{

      fetchCartCount(null)

    }

  }

)

//=========================
// STORAGE
//=========================

window.addEventListener(
  "storage",
  loadFavoriteCount
)

//=========================
// MOUNTED
//=========================

onMounted(()=>{

  loadData()

})
</script>
<template>

  <!-- ================= TOP BAR ================= -->

  <div class="top-bar">
    <div class="marquee">
      <span>🚚 Miễn phí giao hàng toàn quốc</span>
      <span>🔥 Giảm đến 30% cho thành viên GX Club</span>
      <span>⭐ Hơn 10.000 khách hàng tin dùng</span>
      <span>💎 Premium Sneaker Store</span>
      <span>👟 Bộ sưu tập Sneaker 2026</span>
      <span>🎁 Đổi trả trong 30 ngày</span>
    </div>
  </div>

  <!-- ================= HEADER ================= -->

  <header class="navbar">

    <!-- LOGO -->

    <router-link
      to="/"
      class="logo"
    >
      <div class="logo-icon">
        GX
      </div>

      <div class="logo-text">
        <h2>GX SNEAKER</h2>
        <span>PREMIUM SNEAKER STORE</span>
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

      <router-link to="/favorites">
        Yêu thích
      </router-link>

      <router-link to="/contact">
        Liên hệ
      </router-link>

    </nav>

    <!-- ACTION -->

    <div class="actions">

      <template v-if="user">

        <!-- FAVORITE -->

        <router-link
          to="/favorites"
          class="circle-btn"
        >

          <i class="far fa-heart"></i>

          <span
            class="badge"
            v-if="favoriteCount"
          >
            {{ favoriteCount }}
          </span>

        </router-link>

        <!-- CART -->

        <router-link
          to="/cart"
          class="circle-btn"
        >

          <i class="fas fa-cart-shopping"></i>

          <span
            class="badge"
            v-if="cartCount"
          >
            {{ cartCount }}
          </span>

        </router-link>

        <!-- USER -->

        <div class="user-dropdown">

          <div class="user-trigger">

            <div class="avatar">
              {{ user.hoTen?.charAt(0).toUpperCase() }}
            </div>

            <div class="user-info">
              <strong>{{ user.hoTen }}</strong>
              <small>Thành viên GX Club</small>
            </div>

            <i class="fas fa-chevron-down arrow"></i>

          </div>

          <!-- DROPDOWN -->

          <div class="user-menu">

            <router-link to="/profile">
              <i class="far fa-user"></i>
              Hồ sơ cá nhân
            </router-link>

            <router-link to="/orders">
              <i class="fas fa-box"></i>
              Đơn hàng
            </router-link>

            <router-link to="/favorites">
              <i class="far fa-heart"></i>
              Yêu thích
            </router-link>

            <button @click="logout">
              <i class="fas fa-right-from-bracket"></i>
              Đăng xuất
            </button>

          </div>

        </div>

      </template>

      <!-- LOGIN -->

      <router-link
        v-else
        to="/login"
        class="login-btn"
      >
        <i class="far fa-user"></i>
        Đăng nhập
      </router-link>

    </div>

  </header>

</template>
<style scoped>

/*==============================
RESET
==============================*/

*{
  margin:0;
  padding:0;
  box-sizing:border-box;
}

a{
  text-decoration:none;
}

button{
  font-family:inherit;
  cursor:pointer;
}

:root{
  --primary:#111;
  --secondary:#222;
  --border:#ececec;
  --text:#222;
  --gray:#777;
  --white:#fff;
  --danger:#ff3b30;
}

/*==============================
TOP BAR
==============================*/

.top-bar{
  height:36px;
  background:linear-gradient(90deg,#111,#1f1f1f,#111);
  color:#fff;
  display:flex;
  align-items:center;
  overflow:hidden;
}

.marquee{
  display:flex;
  align-items:center;
  gap:70px;
  white-space:nowrap;
  animation:marquee 25s linear infinite;
}

.marquee span{
  font-size:13px;
  font-weight:500;
  letter-spacing:.5px;
}

@keyframes marquee{
  from{transform:translateX(100%);}
  to{transform:translateX(-100%);}
}

/*==============================
NAVBAR
==============================*/

.navbar{
  position:sticky;
  top:0;
  z-index:1000;
  height:72px;
  background:rgba(255,255,255,.95);
  backdrop-filter:blur(18px);
  display:flex;
  justify-content:space-between;
  align-items:center;
  padding:0 70px;
  border-bottom:1px solid var(--border);
  transition:.3s;
  box-shadow:0 8px 22px rgba(0,0,0,.05);
}

.navbar:hover{
  box-shadow:0 15px 35px rgba(0,0,0,.08);
}

/*==============================
LOGO
==============================*/

.logo{
  display:flex;
  align-items:center;
  gap:14px;
}

.logo-icon{
  width:54px;
  height:54px;
  border-radius:16px;
  background:linear-gradient(135deg,#111,#444);
  color:#fff;
  display:flex;
  justify-content:center;
  align-items:center;
  font-size:22px;
  font-weight:800;
  transition:.35s;
}

.logo:hover .logo-icon{
  transform:rotate(-8deg) scale(1.06);
}

.logo-text{
  display:flex;
  flex-direction:column;
}

.logo-text h2{
  font-size:23px;
  font-weight:800;
  letter-spacing:2px;
  color:#111;
}

.logo-text span{
  margin-top:2px;
  font-size:10px;
  letter-spacing:2px;
  color:#888;
}

/*==============================
MENU
==============================*/

.menu{
  display:flex;
  align-items:center;
  gap:42px;
}

.menu a{
  position:relative;
  color:#333;
  font-size:15px;
  font-weight:600;
  transition:.3s;
}

.menu a::after{
  content:"";
  position:absolute;
  left:0;
  bottom:-8px;
  width:0;
  height:2px;
  border-radius:20px;
  background:#111;
  transition:.3s;
}

.menu a:hover{
  color:#000;
}

.menu a:hover::after{
  width:100%;
}

.menu .router-link-active{
  color:#000;
}

.menu .router-link-active::after{
  width:100%;
}

/*==============================
ACTIONS
==============================*/

.actions{
  display:flex;
  align-items:center;
  gap:12px;
}

/*==============================
LOGIN
==============================*/

.login-btn{
  height:44px;
  padding:0 22px;
  border-radius:40px;
  background:#111;
  color:#fff;
  display:flex;
  align-items:center;
  gap:10px;
  font-size:14px;
  font-weight:600;
  transition:.3s;
}

.login-btn:hover{
  background:#000;
  transform:translateY(-2px);
  box-shadow:0 10px 25px rgba(0,0,0,.18);
}

/*==============================
ICON BUTTON
==============================*/

.circle-btn{
  position:relative;
  width:46px;
  height:46px;
  border-radius:50%;
  background:#f5f5f5;
  display:flex;
  justify-content:center;
  align-items:center;
  color:#222;
  transition:.3s;
}

.circle-btn i{
  font-size:18px;
}

.circle-btn:hover{
  background:#111;
  color:#fff;
  transform:translateY(-2px);
}

/*==============================
BADGE
==============================*/

.badge{
  position:absolute;
  top:-4px;
  right:-4px;
  min-width:18px;
  height:18px;
  padding:0 5px;
  border-radius:20px;
  background:#ff3b30;
  color:#fff;
  font-size:10px;
  font-weight:700;
  display:flex;
  justify-content:center;
  align-items:center;
  border:2px solid #fff;
}

/*==============================
USER
==============================*/

.user-dropdown{
  position:relative;
}

.user-trigger{
  display:flex;
  align-items:center;
  gap:12px;
  padding:5px 10px;
  border-radius:40px;
  transition:.3s;
  cursor:pointer;
}

.user-trigger:hover{
  background:#f7f7f7;
}

.avatar{
  width:44px;
  height:44px;
  border-radius:50%;
  background:linear-gradient(135deg,#111,#555);
  color:#fff;
  display:flex;
  justify-content:center;
  align-items:center;
  font-weight:700;
  font-size:17px;
}

.user-info{
  display:flex;
  flex-direction:column;
  line-height:1.25;
}

.user-info strong{
  color:#111;
  font-size:14px;
}

.user-info small{
  color:#888;
  font-size:11px;
}

.arrow{
  color:#666;
  font-size:11px;
}
/*==================================
USER DROPDOWN
==================================*/

.user-menu{
  position:absolute;
  top:60px;
  right:0;
  width:250px;
  background:#fff;
  border-radius:18px;
  border:1px solid #ececec;
  overflow:hidden;
  box-shadow:0 20px 40px rgba(0,0,0,.12);

  opacity:0;
  visibility:hidden;
  transform:translateY(15px);

  transition:.3s;
}

.user-dropdown:hover .user-menu{
  opacity:1;
  visibility:visible;
  transform:translateY(0);
}

.user-menu a,
.user-menu button{
  width:100%;
  padding:15px 20px;
  display:flex;
  align-items:center;
  gap:12px;
  border:none;
  background:#fff;
  color:#222;
  font-size:14px;
  transition:.25s;
  text-align:left;
}

.user-menu a:hover,
.user-menu button:hover{
  background:#f8f8f8;
  padding-left:28px;
}

.user-menu button{
  color:#e53935;
  border-top:1px solid #eee;
}

.user-menu i{
  width:18px;
  text-align:center;
}

/*==================================
ANIMATION
==================================*/

.logo,
.menu a,
.circle-btn,
.login-btn,
.user-trigger{
  animation:fadeUp .6s ease both;
}

@keyframes fadeUp{

  from{
    opacity:0;
    transform:translateY(15px);
  }

  to{
    opacity:1;
    transform:translateY(0);
  }

}

@keyframes dropdown{

  from{
    opacity:0;
    transform:translateY(10px) scale(.96);
  }

  to{
    opacity:1;
    transform:translateY(0) scale(1);
  }

}

.user-menu{
  animation:dropdown .25s ease;
}

/*==================================
SCROLLBAR
==================================*/

::-webkit-scrollbar{
  width:8px;
}

::-webkit-scrollbar-thumb{
  background:#bbb;
  border-radius:20px;
}

::-webkit-scrollbar-thumb:hover{
  background:#888;
}

/*==================================
RESPONSIVE
==================================*/

@media(max-width:1200px){

  .navbar{
    padding:0 40px;
  }

  .menu{
    gap:30px;
  }

}

@media(max-width:992px){

  .navbar{
    height:auto;
    padding:18px 24px;
    flex-wrap:wrap;
    gap:18px;
  }

  .logo{
    width:100%;
    justify-content:center;
  }

  .menu{
    width:100%;
    justify-content:center;
    gap:22px;
  }

  .actions{
    width:100%;
    justify-content:center;
  }

}

@media(max-width:768px){

  .top-bar{
    display:none;
  }

  .navbar{
    padding:15px;
  }

  .menu{
    display:none;
  }

  .actions{
    gap:10px;
  }

  .user-info{
    display:none;
  }

  .user-menu{
    width:220px;
    right:-10px;
  }

}

@media(max-width:576px){

  .logo-icon{
    width:48px;
    height:48px;
    font-size:18px;
  }

  .logo-text h2{
    font-size:18px;
  }

  .logo-text span{
    font-size:9px;
    letter-spacing:1px;
  }

  .circle-btn{
    width:42px;
    height:42px;
  }

  .login-btn{
    height:42px;
    padding:0 18px;
    font-size:13px;
  }

  .avatar{
    width:40px;
    height:40px;
    font-size:15px;
  }

}

/*==================================
BODY
==================================*/

html{
  scroll-behavior:smooth;
}

body{
  font-family:"Inter","Segoe UI",sans-serif;
  background:#fff;
  color:#222;
}

/*==================================
TRANSITION
==================================*/

.logo,
.menu a,
.circle-btn,
.login-btn,
.user-trigger{
  transition:all .3s ease;
}

.logo:hover{
  transform:translateY(-2px);
}

.menu a:hover{
  transform:translateY(-2px);
}

.circle-btn:hover{
  transform:translateY(-2px) scale(1.05);
}
</style>

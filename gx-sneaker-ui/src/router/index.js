import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'

// Auth
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ProfileView from '@/views/ProfileView.vue'
import ForgotPasswordView from '@/views/ForgotPasswordView.vue'
import ResetPasswordView from '@/views/ResetPasswordView.vue'
import ChangePasswordView from '@/views/ChangePasswordView.vue'
import Dashboard from '@/views/Dashboard.vue'

// Danh mục sản phẩm
import ThuongHieuView from '../views/ThuongHieuView.vue'
import DanhMucView from '@/views/DanhMucView.vue'
import XuatXuView from '@/views/XuatXuView.vue'
import ChatLieuView from '@/views/ChatLieuView.vue'
import CoGiayView from '@/views/CoGiayView.vue'
import DeGiayView from '@/views/DeGiayView.vue'
import KichThuocView from '@/views/KichThuocView.vue'
import MauSacView from '@/views/MauSacView.vue'
// Thống kê
//import HoaDonView from '@/views/HoaDonView.vue'
//Hóa đơn
import HoaDonView from '@/views/HoaDonView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },

    // Auth
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
    },
    {
      path: '/forgot-password',
      name: 'forgot-password',
      component: ForgotPasswordView,
    },
    {
      path: '/reset-password',
      name: 'reset-password',
      component: ResetPasswordView,
    },
    {
      path: '/change-password',
      name: 'change-password',
      component: ChangePasswordView,
    },

    // Danh mục
    {
      path: '/thuong-hieu',
      name: 'thuong-hieu',
      component: ThuongHieuView,
    },
    {
      path: '/danh-muc',
      name: 'danh-muc',
      component: DanhMucView,
    },
    {
      path: '/xuat-xu',
      name: 'xuat-xu',
      component: XuatXuView,
    },
    {
      path: '/chat-lieu',
      name: 'chat-lieu',
      component: ChatLieuView,
    },
    {
      path: '/co-giay',
      name: 'co-giay',
      component: CoGiayView,
    },
    {
      path: '/de-giay',
      name: 'de-giay',
      component: DeGiayView,
    },
    {
      path: '/mau-sac',
      name: 'mau-sac',
      component: MauSacView,
    },
    {
      path: '/kich-thuoc',
      name: 'kich-thuoc',
      component: KichThuocView,
    },

    // Role pages
    {
      path: '/admin',
      name: 'admin',
      component: HomeView,
    },
    {
      path: '/staff',
      name: 'staff',
      component: HomeView,
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: Dashboard
    },
    // {
    //   path: '/hoa-don',
    //   component: HoaDonView
    // }
    {
      path: '/hoa-don',
      component: HoaDonView
    }


  ],
})

router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user'))
  const token = localStorage.getItem('token')

  const protectedRoutes = [
    '/profile',
    '/change-password',
    '/cart',
    '/checkout'
  ]

  if (protectedRoutes.includes(to.path) && !token) {
    next('/login')
    return
  }

  if (to.path.startsWith('/admin')) {
    if (!user || user.role !== 'ADMIN') {
      next('/')
      return
    }
  }

  if (to.path.startsWith('/staff')) {
    if (!user || (user.role !== 'ADMIN' && user.role !== 'STAFF')) {
      next('/')
      return
    }
  }

  next()
})

export default router

import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ProfileView from '@/views/ProfileView.vue'
import ForgotPasswordView from '@/views/ForgotPasswordView.vue'
import ResetPasswordView from '@/views/ResetPasswordView.vue'
import ChangePasswordView from '@/views/ChangePasswordView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
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

    // Tạm thời tạo trước
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
  ],
})

router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user'))
  const token = localStorage.getItem('token')

  // Chưa login
  const protectedRoutes = ['/profile', '/change-password', '/cart', '/checkout']

  if (protectedRoutes.includes(to.path) && !token) {
    next('/login')
    return
  }

  // Admin
  if (to.path.startsWith('/admin')) {
    if (!user || user.role !== 'ADMIN') {
      next('/')
      return
    }
  }

  // Staff
  if (to.path.startsWith('/staff')) {
    if (!user || (user.role !== 'ADMIN' && user.role !== 'STAFF')) {
      next('/')
      return
    }
  }

  next()
})

export default router

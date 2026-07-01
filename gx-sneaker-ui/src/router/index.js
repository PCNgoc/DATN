import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'

// Auth
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ProfileView from '@/views/ProfileView.vue'
import ForgotPasswordView from '@/views/ForgotPasswordView.vue'
import ResetPasswordView from '@/views/ResetPasswordView.vue'
import ChangePasswordView from '@/views/ChangePasswordView.vue'
import PayOSWaitingView from '@/views/user/PayOSWaitingView.vue'

// Admin
import AdminLoginView from '@/views/admin/AdminLoginView.vue'
import AdminDashboardView from '@/views/admin/AdminDashboardView.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import NhanVienView from '@/views/admin/NhanVienView.vue'
import HoaDonView from '@/views/admin/HoaDonView.vue'
import Dashboard from '@/views/admin/Dashboard.vue'
import KhachHangView from '@/views/admin/KhachHangView.vue'
import PhieuGiamGiaView from '@/views/admin/PhieuGiamGiaView.vue'

// Danh mục sản phẩm
import ThuongHieuView from '../views/ThuongHieuView.vue'
import DanhMucView from '@/views/DanhMucView.vue'
import XuatXuView from '@/views/XuatXuView.vue'
import ChatLieuView from '@/views/ChatLieuView.vue'
import CoGiayView from '@/views/CoGiayView.vue'
import DeGiayView from '@/views/DeGiayView.vue'
import KichThuocView from '@/views/KichThuocView.vue'
import MauSacView from '@/views/MauSacView.vue'
import SanPhamView from '@/views/SanPhamView.vue'
import ChiTietSanPhamView from '@/views/ChiTietSanPhamView.vue'

// User
import ProductListView from '@/views/user/ProductListView.vue'
import ProductDetailView from '@/views/user/ProductDetailView.vue'
import CheckoutView from '@/views/user/CheckoutView.vue'
import OrderView from '@/views/user/OrderView.vue'
import OrderDetailView from '@/views/user/OrderDetailView.vue'
import OrderSuccessView from '@/views/user/OrderSuccessView.vue'
import GioHangView from '@/views/user/GioHangView.vue'
import ContactView from '@/views/user/ContactView.vue'

// Khác
import FavoriteView from '@/views/FavoriteView.vue'

const normalizeRole = (role) => {
  if (!role) return ''

  let value = String(role).trim().toUpperCase()

  if (value.startsWith('ROLE_')) {
    value = value.replace('ROLE_', '')
  }

  if (value === 'NHAN_VIEN') {
    value = 'STAFF'
  }

  if (value === 'QUAN_TRI' || value === 'ADMINISTRATOR') {
    value = 'ADMIN'
  }

  return value
}

const getAdminRole = () => {
  return normalizeRole(localStorage.getItem('adminRole'))
}

const getDefaultAdminPath = () => {
  const role = getAdminRole()

  if (role === 'ADMIN') {
    return '/admin/dashboard'
  }

  if (role === 'STAFF') {
    return '/admin/hoa-don'
  }

  return '/admin/login'
}

const router = createRouter({
  history: createWebHistory(),

  routes: [
    // ================= USER PUBLIC =================
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/products',
      name: 'products',
      component: ProductListView,
    },
    {
      path: '/products/:id',
      name: 'product-detail',
      component: ProductDetailView,
    },
    {
      path: '/favorites',
      name: 'favorites',
      component: FavoriteView,
    },
    {
      path: '/contact',
      name: 'contact',
      component: ContactView,
    },
    {
      path: '/lien-he',
      name: 'lien-he',
      component: ContactView,
    },

    // ================= USER AUTH =================
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
      path: '/profile',
      name: 'profile',
      component: ProfileView,
      meta: {
        requiresUser: true,
      },
    },
    {
      path: '/change-password',
      name: 'change-password',
      component: ChangePasswordView,
      meta: {
        requiresUser: true,
      },
    },
    {
      path: '/cart',
      name: 'cart',
      component: GioHangView,
      meta: {
        requiresUser: true,
      },
    },
    {
      path: '/checkout',
      name: 'checkout',
      component: CheckoutView,
      meta: {
        requiresUser: true,
      },
    },
    {
      path: '/orders',
      name: 'orders',
      component: OrderView,
      meta: {
        requiresUser: true,
      },
    },
    {
      path: '/orders/:id',
      name: 'OrderDetail',
      component: OrderDetailView,
      meta: {
        requiresUser: true,
      },
    },
    {
      path: '/order-success/:id',
      name: 'OrderSuccess',
      component: OrderSuccessView,
    },
    {
      path: '/payos-waiting/:id',
      name: 'PayOSWaiting',
      component: PayOSWaitingView,
    },

    // ================= ADMIN LOGIN =================
    {
      path: '/admin/login',
      name: 'AdminLogin',
      component: AdminLoginView,
    },

    // ================= ADMIN + STAFF LAYOUT =================
    {
      path: '/admin',
      component: AdminLayout,
      meta: {
        requiresAdmin: true,
      },
      children: [
        {
          path: '',
          redirect: () => getDefaultAdminPath(),
        },

        // ADMIN ONLY
        {
          path: 'dashboard',
          name: 'AdminDashboard',
          component: AdminDashboardView,
          meta: {
            roles: ['ADMIN'],
          },
        },
        {
          path: 'thong-ke',
          name: 'AdminThongKe',
          component: Dashboard,
          meta: {
            roles: ['ADMIN'],
          },
        },
        {
          path: 'nhan-vien',
          name: 'AdminNhanVien',
          component: NhanVienView,
          meta: {
            roles: ['ADMIN'],
          },
        },
        {
          path: 'phieu-giam-gia',
          name: 'AdminPhieuGiamGia',
          component: PhieuGiamGiaView,
          meta: {
            roles: ['ADMIN'],
          },
        },

        // ADMIN + STAFF
        {
          path: 'hoa-don',
          name: 'AdminHoaDon',
          component: HoaDonView,
          meta: {
            roles: ['ADMIN', 'STAFF'],
          },
        },
        {
          path: 'san-pham',
          name: 'AdminSanPham',
          component: SanPhamView,
          meta: {
            roles: ['ADMIN', 'STAFF'],
          },
        },
        {
          path: 'san-pham/:id/chi-tiet',
          name: 'chi-tiet-san-pham',
          component: ChiTietSanPhamView,
          meta: {
            roles: ['ADMIN', 'STAFF'],
          },
        },
        {
          path: 'danh-muc',
          name: 'AdminDanhMuc',
          component: DanhMucView,
          meta: {
            roles: ['ADMIN', 'STAFF'],
          },
        },
        {
          path: 'thuong-hieu',
          name: 'AdminThuongHieu',
          component: ThuongHieuView,
          meta: {
            roles: ['ADMIN', 'STAFF'],
          },
        },
        {
          path: 'chat-lieu',
          name: 'AdminChatLieu',
          component: ChatLieuView,
          meta: {
            roles: ['ADMIN', 'STAFF'],
          },
        },
        {
          path: 'co-giay',
          name: 'AdminCoGiay',
          component: CoGiayView,
          meta: {
            roles: ['ADMIN', 'STAFF'],
          },
        },
        {
          path: 'de-giay',
          name: 'AdminDeGiay',
          component: DeGiayView,
          meta: {
            roles: ['ADMIN', 'STAFF'],
          },
        },
        {
          path: 'mau-sac',
          name: 'AdminMauSac',
          component: MauSacView,
          meta: {
            roles: ['ADMIN', 'STAFF'],
          },
        },
        {
          path: 'kich-thuoc',
          name: 'AdminKichThuoc',
          component: KichThuocView,
          meta: {
            roles: ['ADMIN', 'STAFF'],
          },
        },
        {
          path: 'xuat-xu',
          name: 'AdminXuatXu',
          component: XuatXuView,
          meta: {
            roles: ['ADMIN', 'STAFF'],
          },
        },
        {
          path: 'khach-hang',
          name: 'AdminKhachHang',
          component: KhachHangView,
          meta: {
            roles: ['ADMIN', 'STAFF'],
          },
        },
      ],
    },

    // ================= STAFF OLD PATH SUPPORT =================
    // Nếu AdminLoginView cũ vẫn redirect STAFF sang /staff/dashboard
    // thì route này sẽ đưa STAFF về /admin/hoa-don
    {
      path: '/staff',
      redirect: '/admin/hoa-don',
    },
    {
      path: '/staff/dashboard',
      redirect: '/admin/hoa-don',
    },

    // Nếu có link cũ /dashboard thì đưa về thống kê admin
    {
      path: '/dashboard',
      redirect: '/admin/thong-ke',
    },
  ],
})

router.beforeEach((to) => {
  const userToken = localStorage.getItem('token')
  const adminToken = localStorage.getItem('adminToken')
  const adminRole = getAdminRole()

  // ================= USER ROUTE GUARD =================
  if (to.meta?.requiresUser && !userToken) {
    return '/login'
  }

  // ================= ADMIN LOGIN =================
  if (to.path === '/admin/login') {
    return true
  }

  // ================= STAFF OLD ROUTE =================
  if (to.path.startsWith('/staff')) {
    if (!adminToken || (adminRole !== 'ADMIN' && adminRole !== 'STAFF')) {
      return '/admin/login'
    }

    return '/admin/hoa-don'
  }

  // ================= ADMIN ROUTE GUARD =================
  if (to.path.startsWith('/admin')) {
    if (!adminToken) {
      return '/admin/login'
    }

    if (adminRole !== 'ADMIN' && adminRole !== 'STAFF') {
      localStorage.removeItem('adminToken')
      localStorage.removeItem('adminRole')
      localStorage.removeItem('adminUser')
      return '/admin/login'
    }

    if (to.path === '/admin') {
      return getDefaultAdminPath()
    }

    const allowRoles = to.meta?.roles

    if (allowRoles && !allowRoles.includes(adminRole)) {
      if (adminRole === 'STAFF') {
        return '/admin/hoa-don'
      }

      if (adminRole === 'ADMIN') {
        return '/admin/dashboard'
      }

      return '/admin/login'
    }
  }

  return true
})

export default router

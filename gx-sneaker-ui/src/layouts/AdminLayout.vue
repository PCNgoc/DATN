<script setup>
import { computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

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

const adminRole = computed(() => {
  return normalizeRole(localStorage.getItem('adminRole'))
})

const adminUser = computed(() => {
  const raw = localStorage.getItem('adminUser')

  if (!raw) {
    return {}
  }

  try {
    return JSON.parse(raw)
  } catch (e) {
    return {}
  }
})

const isAdmin = computed(() => adminRole.value === 'ADMIN')
const isStaff = computed(() => adminRole.value === 'STAFF')

const displayName = computed(() => {
  return (
    adminUser.value?.hoTen ||
    adminUser.value?.fullName ||
    adminUser.value?.tenNhanVien ||
    adminUser.value?.email ||
    'Tài khoản'
  )
})

const roleLabel = computed(() => {
  if (isAdmin.value) return 'Quản trị viên'
  if (isStaff.value) return 'Nhân viên'
  return 'Không xác định'
})

const menuItems = computed(() => [
  {
    label: 'Tổng quan',
    icon: '📊',
    path: '/admin/dashboard',
    roles: ['ADMIN'],
  },
  {
    label: 'Thống kê',
    icon: '📈',
    path: '/admin/thong-ke',
    roles: ['ADMIN'],
  },
  {
    label: 'Quản lý hóa đơn',
    icon: '🧾',
    path: '/admin/hoa-don',
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Quản lý sản phẩm',
    icon: '👟',
    path: '/admin/san-pham',
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Quản lý đơn hàng',
    icon: '🧾',
    path: '/admin/hoa-don',
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Khách hàng',
    icon: '👥',
    path: '/admin/khach-hang',
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Phiếu giảm giá',
    icon: '🎟️',
    path: '/admin/phieu-giam-gia',
    roles: ['ADMIN'],
  },
  {
    label: 'Quản lý nhân viên',
    icon: '🧑‍💼',
    path: '/admin/nhan-vien',
    roles: ['ADMIN'],
  },
])

const productAttributeItems = computed(() => [
  {
    label: 'Danh mục',
    path: '/admin/danh-muc',
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Thương hiệu',
    path: '/admin/thuong-hieu',
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Chất liệu',
    path: '/admin/chat-lieu',
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Cổ giày',
    path: '/admin/co-giay',
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Đế giày',
    path: '/admin/de-giay',
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Màu sắc',
    path: '/admin/mau-sac',
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Kích thước',
    path: '/admin/kich-thuoc',
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Xuất xứ',
    path: '/admin/xuat-xu',
    roles: ['ADMIN', 'STAFF'],
  },
])

const canShow = (item) => {
  if (!item.roles || item.roles.length === 0) {
    return true
  }

  return item.roles.includes(adminRole.value)
}

const visibleMenuItems = computed(() => {
  return menuItems.value.filter(canShow)
})

const visibleProductAttributeItems = computed(() => {
  return productAttributeItems.value.filter(canShow)
})

const isProductAttributeActive = computed(() => {
  return visibleProductAttributeItems.value.some((item) => route.path === item.path)
})

const goToHomeByRole = () => {
  if (isAdmin.value) {
    router.push('/admin/dashboard')
    return
  }

  if (isStaff.value) {
    router.push('/admin/hoa-don')
    return
  }

  router.push('/admin/login')
}

const logout = () => {
  localStorage.removeItem('adminToken')
  localStorage.removeItem('adminRole')
  localStorage.removeItem('adminUser')

  router.push('/admin/login')
}

onMounted(() => {
  const adminToken = localStorage.getItem('adminToken')

  if (!adminToken || (!isAdmin.value && !isStaff.value)) {
    logout()
  }
})
</script>

<template>
  <div class="admin-layout">
    <!-- SIDEBAR -->
    <aside class="sidebar">
      <div class="brand" @click="goToHomeByRole">
        <div class="brand-logo">GX</div>

        <div class="brand-text">
          <h2>GX Sneaker</h2>
          <p>Admin Panel</p>
        </div>
      </div>

      <div class="user-box">
        <div class="avatar">
          {{ displayName.charAt(0).toUpperCase() }}
        </div>

        <div class="user-info">
          <strong>{{ displayName }}</strong>

          <span
            class="role-badge"
            :class="{
              admin: isAdmin,
              staff: isStaff,
            }"
          >
            {{ roleLabel }}
          </span>
        </div>
      </div>

      <nav class="menu">
        <router-link
          v-for="item in visibleMenuItems"
          :key="item.path"
          :to="item.path"
          class="menu-link"
          active-class="active"
        >
          <span class="menu-icon">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </router-link>

        <div v-if="visibleProductAttributeItems.length > 0" class="menu-group">
          <div class="menu-group-title" :class="{ active: isProductAttributeActive }">
            <span>⚙️</span>
            <span>Thuộc tính sản phẩm</span>
          </div>

          <router-link
            v-for="item in visibleProductAttributeItems"
            :key="item.path"
            :to="item.path"
            class="submenu-link"
            active-class="active"
          >
            {{ item.label }}
          </router-link>
        </div>
      </nav>

      <button class="logout-btn" @click="logout">🚪 Đăng xuất</button>
    </aside>

    <!-- MAIN -->
    <main class="main-content">
      <header class="topbar">
        <div>
          <h1>
            {{ isAdmin ? 'Trang quản trị' : 'Trang nhân viên' }}
          </h1>

          <p>
            {{
              isAdmin
                ? 'Quản lý toàn bộ hệ thống GX Sneaker'
                : 'Xử lý đơn hàng và nghiệp vụ bán hàng'
            }}
          </p>
        </div>

        <div class="topbar-right">
          <span class="current-role">
            {{ roleLabel }}
          </span>
        </div>
      </header>

      <section class="content-wrapper">
        <RouterView />
      </section>
    </main>
  </div>
</template>

<style scoped>
.admin-layout {
  min-height: 100vh;
  display: flex;
  background: #f3f4f6;
  color: #111827;
}

.sidebar {
  width: 280px;
  min-height: 100vh;
  background: #111827;
  color: white;
  display: flex;
  flex-direction: column;
  padding: 22px 18px;
  position: sticky;
  top: 0;
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  padding-bottom: 22px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.brand-logo {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 18px;
}

.brand-text h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 900;
}

.brand-text p {
  margin: 4px 0 0;
  font-size: 13px;
  color: #9ca3af;
}

.user-box {
  margin-top: 20px;
  padding: 14px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.08);
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: #ef4444;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
}

.user-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.user-info strong {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.role-badge {
  width: fit-content;
  margin-top: 6px;
  padding: 4px 9px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 800;
}

.role-badge.admin {
  background: #fef3c7;
  color: #92400e;
}

.role-badge.staff {
  background: #dbeafe;
  color: #1d4ed8;
}

.menu {
  margin-top: 22px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  overflow-y: auto;
  padding-right: 4px;
}

.menu-link,
.submenu-link {
  text-decoration: none;
  color: #d1d5db;
  border-radius: 14px;
  transition: 0.2s;
}

.menu-link {
  padding: 12px 14px;
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
}

.menu-link:hover,
.menu-link.active,
.submenu-link:hover,
.submenu-link.active {
  background: #ef4444;
  color: white;
}

.menu-icon {
  width: 22px;
  display: inline-flex;
  justify-content: center;
}

.menu-group {
  margin-top: 6px;
}

.menu-group-title {
  padding: 12px 14px;
  border-radius: 14px;
  color: #f9fafb;
  font-weight: 800;
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.06);
}

.menu-group-title.active {
  background: rgba(239, 68, 68, 0.28);
}

.submenu-link {
  display: block;
  margin: 7px 0 0 34px;
  padding: 9px 12px;
  font-size: 14px;
  font-weight: 700;
}

.logout-btn {
  margin-top: 18px;
  width: 100%;
  border: none;
  border-radius: 14px;
  padding: 13px 16px;
  background: #dc2626;
  color: white;
  font-weight: 900;
  cursor: pointer;
  transition: 0.2s;
}

.logout-btn:hover {
  background: #b91c1c;
}

.main-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.topbar {
  min-height: 88px;
  background: white;
  border-bottom: 1px solid #e5e7eb;
  padding: 18px 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.topbar h1 {
  margin: 0;
  font-size: 26px;
  font-weight: 900;
  color: #111827;
}

.topbar p {
  margin: 5px 0 0;
  color: #6b7280;
  font-size: 14px;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.current-role {
  padding: 9px 14px;
  border-radius: 999px;
  background: #f3f4f6;
  color: #111827;
  font-weight: 800;
  font-size: 13px;
}

.content-wrapper {
  padding: 28px;
  flex: 1;
}

@media (max-width: 992px) {
  .admin-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    min-height: auto;
    position: static;
  }

  .menu {
    max-height: none;
  }

  .topbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}

@media (max-width: 640px) {
  .content-wrapper {
    padding: 18px;
  }

  .topbar {
    padding: 16px 18px;
  }

  .topbar h1 {
    font-size: 22px;
  }
}
</style>

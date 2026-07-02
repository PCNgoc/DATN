import axios from 'axios'

const BASE_URL = 'http://localhost:8080'
const API_URL = `${BASE_URL}/api/auth`

// Mỗi lần đổi version này, trình duyệt sẽ tự xóa token cũ 1 lần
const AUTH_STORAGE_VERSION = 'GX_SNEAKER_AUTH_2026_07_01_V1'

const USER_KEYS = ['token', 'user']
const ADMIN_KEYS = ['adminToken', 'adminRole', 'adminUser']

const isBadValue = (value) => {
  return (
    value === null ||
    value === undefined ||
    value === '' ||
    value === 'null' ||
    value === 'undefined'
  )
}

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

export const clearUserSession = () => {
  USER_KEYS.forEach((key) => localStorage.removeItem(key))
}

export const clearAdminSession = () => {
  ADMIN_KEYS.forEach((key) => localStorage.removeItem(key))
}

export const clearAllAuthSession = () => {
  clearUserSession()
  clearAdminSession()
}

export const initAuthStorage = () => {
  const currentVersion = localStorage.getItem('gxAuthStorageVersion')

  if (currentVersion !== AUTH_STORAGE_VERSION) {
    clearAllAuthSession()
    localStorage.setItem('gxAuthStorageVersion', AUTH_STORAGE_VERSION)
  }

  ;[...USER_KEYS, ...ADMIN_KEYS].forEach((key) => {
    const value = localStorage.getItem(key)

    if (isBadValue(value)) {
      localStorage.removeItem(key)
    }
  })

  const adminRole = normalizeRole(localStorage.getItem('adminRole'))

  if (adminRole && adminRole !== 'ADMIN' && adminRole !== 'STAFF') {
    clearAdminSession()
  }
}

export const getUserToken = () => {
  return localStorage.getItem('token')
}

export const getAdminToken = () => {
  return localStorage.getItem('adminToken')
}

const isUserRequest = (url) => {
  return (
    url.includes('/api/auth/me') ||
    url.includes('/api/auth/change-password') ||
    url.includes('/api/gio-hang') ||
    url.includes('/api/dia-chi') ||
    url.includes('/api/hoa-don/dat-hang') ||
    url.includes('/api/hoa-don/khach-hang') ||
    url.includes('/api/hoa-don/ap-dung-ma-giam-gia')
  )
}

const isAdminRequest = (url) => {
  if (
    url.includes('/api/auth/admin') ||
    url.includes('/api/nhan-vien') ||
    url.includes('/api/hoa-don/thong-ke')
  ) {
    return true
  }

  if (
    url.includes('/api/hoa-don') &&
    !url.includes('/api/hoa-don/dat-hang') &&
    !url.includes('/api/hoa-don/khach-hang') &&
    !url.includes('/api/hoa-don/ap-dung-ma-giam-gia')
  ) {
    return true
  }

  return false
}

const getTokenForRequest = (config) => {
  const url = String(config?.url || '')

  if (isUserRequest(url)) {
    return getUserToken()
  }

  if (isAdminRequest(url)) {
    return getAdminToken()
  }

  return getUserToken() || getAdminToken()
}

export const authHeader = () => {
  const token = getUserToken()

  return {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  }
}

export const adminAuthHeader = () => {
  const token = getAdminToken()

  return {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  }
}

export const saveUserSession = (data) => {
  localStorage.setItem('token', data?.token || '')
  localStorage.setItem('user', JSON.stringify(data?.user || data || {}))
}

export const saveAdminSession = (data) => {
  const role = normalizeRole(data?.role)

  localStorage.setItem('adminToken', data?.token || '')
  localStorage.setItem('adminRole', role)
  localStorage.setItem(
    'adminUser',
    JSON.stringify({
      ...data,
      role,
    }),
  )

  return role
}

// Tránh cài nhiều interceptor khi import nhiều nơi
if (!window.__GX_AUTH_INTERCEPTOR_INSTALLED__) {
  window.__GX_AUTH_INTERCEPTOR_INSTALLED__ = true

  axios.interceptors.request.use((config) => {
    initAuthStorage()

    const token = getTokenForRequest(config)

    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  })

  axios.interceptors.response.use(
    (response) => response,
    (error) => {
      const status = error?.response?.status
      const url = String(error?.config?.url || '')
      const currentPath = window.location.pathname

      // Token user lỗi khi gọi /api/auth/me thì tự logout user
      if (url.includes('/api/auth/me') && (status === 400 || status === 401 || status === 403)) {
        clearUserSession()

        if (!currentPath.includes('/login')) {
          window.location.href = '/login'
        }
      }

      // Token hết hạn/sai chữ ký
      if (status === 401) {
        if (currentPath.startsWith('/admin')) {
          clearAdminSession()

          if (!currentPath.includes('/admin/login')) {
            window.location.href = '/admin/login'
          }
        } else {
          clearUserSession()

          if (!currentPath.includes('/login')) {
            window.location.href = '/login'
          }
        }
      }

      return Promise.reject(error)
    },
  )
}

export const loginApi = (data) => axios.post(`${API_URL}/login`, data)

export const adminLoginApi = (data) => axios.post(`${API_URL}/admin/login`, data)

export const staffLoginApi = (data) => adminLoginApi(data)

export const registerApi = (data) => axios.post(`${API_URL}/register`, data)

export const getMeApi = () => axios.get(`${API_URL}/me`, authHeader())

export const changePasswordApi = (data) =>
  axios.put(`${API_URL}/change-password`, data, authHeader())

export const forgotPasswordApi = (data) => axios.post(`${API_URL}/forgot-password`, data)

export const resetPasswordApi = (data) => axios.post(`${API_URL}/reset-password`, data)

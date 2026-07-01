import axios from 'axios'

const BASE_URL = 'http://localhost:8080'
const API_URL = `${BASE_URL}/api/auth`

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

export const getAccessToken = () => {
  return localStorage.getItem('adminToken') || localStorage.getItem('token')
}

export const authHeader = () => {
  const token = getAccessToken()

  return {
    headers: token
      ? {
          Authorization: `Bearer ${token}`,
        }
      : {},
  }
}

axios.interceptors.request.use((config) => {
  const token = getAccessToken()

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

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

export const clearAdminSession = () => {
  localStorage.removeItem('adminToken')
  localStorage.removeItem('adminRole')
  localStorage.removeItem('adminUser')
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

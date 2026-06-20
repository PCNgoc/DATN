import axios from 'axios'

const API_URL = 'http://localhost:8080/api/auth'

const authHeader = () => {
  const token = localStorage.getItem('token')
  return {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  }
}

export const loginApi = (data) => axios.post(`${API_URL}/login`, data)

export const staffLoginApi = (data) => axios.post(`${API_URL}/staff/login`, data)

export const registerApi = (data) => axios.post(`${API_URL}/register`, data)

export const getMeApi = () => axios.get(`${API_URL}/me`, authHeader())

export const changePasswordApi = (data) =>
  axios.put(`${API_URL}/change-password`, data, authHeader())

export const forgotPasswordApi = (data) => axios.post(`${API_URL}/forgot-password`, data)

export const resetPasswordApi = (data) => axios.post(`${API_URL}/reset-password`, data)

import axios from "axios"

const API = "http://localhost:8080/api/san-pham"

export const getAllSanPham = () => axios.get(API)

export const getSanPhamById = (id) =>
  axios.get(`${API}/${id}`)

export const createSanPham = (data) =>
  axios.post(API, data)

export const updateSanPham = (id, data) =>
  axios.put(`${API}/${id}`, data)

export const deleteSanPham = (id) =>
  axios.delete(`${API}/${id}`)

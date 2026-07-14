import axios from "axios"

const API =
  "http://localhost:8080/api/chi-tiet-san-pham"

export const getBySanPham = (id) =>
  axios.get(`${API}/san-pham/${id}`)

export const create = (data) =>
  axios.post(API, data)

export const update = (id, data) =>
  axios.put(`${API}/${id}`, data)

export const remove = (id) =>
  axios.delete(`${API}/${id}`)

export const getBySanPhamUser = (id) =>
  axios.get(`${API}/user/san-pham/${id}`)


// =============================
// BÁN TẠI QUẦY
// =============================
export const getBanTaiQuay = () =>
  axios.get(`${API}/ban-tai-quay`)



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

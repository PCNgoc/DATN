import axios from "axios"

const API = "http://localhost:8080/api/dia-chi"

export const getByKhachHangId = (khachHangId) => axios.get(`${API}/khach-hang/${khachHangId}`)
export const getById = (id) => axios.get(`${API}/${id}`)
export const create = (data) => axios.post(API, data)
export const update = (id, data) => axios.put(`${API}/${id}`, data)
export const remove = (id) => axios.delete(`${API}/${id}`)

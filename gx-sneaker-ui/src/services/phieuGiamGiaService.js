import axios from "axios"

const API = "http://localhost:8080/api/phieu-giam-gia"

export const getAll = () => axios.get(API)
export const getById = (id) => axios.get(`${API}/${id}`)
export const getByMa = (maPhieu) => axios.get(`${API}/ma/${maPhieu}`)
export const create = (data) => axios.post(API, data)
export const update = (id, data) => axios.put(`${API}/${id}`, data)
export const remove = (id) => axios.delete(`${API}/${id}`)

import axios from "axios"

const API = "http://localhost:8080/api/san-pham"

export const getAll = () => axios.get(API)

export const getById = (id) =>
  axios.get(`${API}/${id}`)

export const create = (data) =>
  axios.post(API, data)

export const update = (id, data) =>
  axios.put(`${API}/${id}`, data)

export const remove = (id) =>
  axios.delete(`${API}/${id}`)
export const getFeaturedProducts = () =>
  axios.get(`${API}/featured`)

export const getNewProducts = () =>
  axios.get(`${API}/new`)

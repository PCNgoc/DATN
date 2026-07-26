import axios from "axios"

const API = "http://localhost:8080/api/products"

export const getProducts = (params = {}) => {
  return axios.get(API, { params })
}

export const getProductById = (id) =>
  axios.get(`${API}/${id}`)

export const searchProducts = (keyword) =>
  axios.get(`${API}/search`, {
    params: { keyword }
  })

export const getNewestProducts = () =>
  axios.get(`${API}/newest`)

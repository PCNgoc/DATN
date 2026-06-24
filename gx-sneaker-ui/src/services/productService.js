import axios from "axios"

const API = "http://localhost:8080/api/products"

export const getProducts = () => axios.get(API)

export const getProductById = (id) =>
  axios.get(`${API}/${id}`)
export const searchProducts = (keyword) =>
  axios.get(
    `${API}/search?keyword=${keyword}`
  )
export const getNewestProducts = () =>
  axios.get(`${API}/newest`)

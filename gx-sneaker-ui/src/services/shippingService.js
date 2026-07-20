import axios from 'axios'

const API_URL = 'http://localhost:8080/api/shipping'

export const getShippingQuote = (data) => {
  return axios.post(`${API_URL}/quote`, data)
}

export const getGhnProvinces = () => {
  return axios.get(`${API_URL}/address/provinces`)
}

export const getGhnDistricts = (provinceId) => {
  return axios.get(`${API_URL}/address/districts`, {
    params: {
      provinceId,
    },
  })
}

export const getGhnWards = (districtId) => {
  return axios.get(`${API_URL}/address/wards`, {
    params: {
      districtId,
    },
  })
}

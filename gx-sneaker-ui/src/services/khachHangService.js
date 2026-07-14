import axios from "axios"

const API = "http://localhost:8080/api/khach-hang"

export const getAll = () => axios.get(API)
export const getById = (id) => axios.get(`${API}/${id}`)
export const create = (data) => axios.post(API, data)
export const update = (id, data) => axios.put(`${API}/${id}`, data)
export const remove = (id) => axios.delete(`${API}/${id}`)

export const getKhachHangBanTaiQuay = () => {

  return axios.get(
    "http://localhost:8080/api/khach-hang/ban-tai-quay"
  );

}

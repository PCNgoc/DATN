import axios from "axios";

const API_URL = "http://localhost:8080/api/thuong-hieu";

export const getAllThuongHieu = () => {
  return axios.get(API_URL);
};

export const getThuongHieuById = (id) => {
  return axios.get(`${API_URL}/${id}`);
};

export const createThuongHieu = (data) => {
  return axios.post(API_URL, data);
};

export const updateThuongHieu = (id, data) => {
  return axios.put(`${API_URL}/${id}`, data);
};

export const deleteThuongHieu = (id) => {
  return axios.delete(`${API_URL}/${id}`);
};

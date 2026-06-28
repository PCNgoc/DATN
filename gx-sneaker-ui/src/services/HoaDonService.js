import axios from "axios";

const API = "http://localhost:8080/api/hoa-don";

// Đặt hàng
export const datHang = (data) => {
  return axios.post(`${API}/dat-hang`, data);
};

// Lấy danh sách đơn hàng theo khách hàng
export const getHoaDonByKhachHang = (idKhachHang) => {
  return axios.get(`${API}/khach-hang/${idKhachHang}`);
};

// Lấy chi tiết hóa đơn
export const getHoaDonById = (id) => {
  return axios.get(`${API}/${id}`);
};

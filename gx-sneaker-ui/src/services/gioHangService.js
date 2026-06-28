import axios from "axios"

const API = "http://localhost:8080/api/gio-hang"

export const getCartByKhachHangId = (khachHangId) => axios.get(`${API}/khach-hang/${khachHangId}`)
export const addItemToCart = (khachHangId, chiTietSanPhamId, soLuong) => 
    axios.post(`${API}/khach-hang/${khachHangId}/add?chiTietSanPhamId=${chiTietSanPhamId}&soLuong=${soLuong}`)
export const updateItemQuantity = (cartItemId, soLuong) => 
    axios.put(`${API}/item/${cartItemId}?soLuong=${soLuong}`)
export const removeItemFromCart = (cartItemId) => axios.delete(`${API}/item/${cartItemId}`)
export const clearCart = (khachHangId) => axios.delete(`${API}/khach-hang/${khachHangId}/clear`)

import axios from "axios"

const API = "http://localhost:8080/api/phieu-giam-gia-khach-hang"

export const getByKhachHangId = (khachHangId) => axios.get(`${API}/khach-hang/${khachHangId}`)
export const assignVoucher = (khachHangId, phieuGiamGiaId) => 
    axios.post(`${API}/assign?khachHangId=${khachHangId}&phieuGiamGiaId=${phieuGiamGiaId}`)
export const useVoucher = (id) => axios.put(`${API}/${id}/use`)

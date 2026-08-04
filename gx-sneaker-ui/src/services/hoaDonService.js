import axios from 'axios'

const API_URL = 'http://localhost:8080/api/hoa-don'

const getAuthHeader = () => {
  const isAdminPath = window.location.pathname.startsWith('/admin')
  const token = isAdminPath ? localStorage.getItem('adminToken') : localStorage.getItem('token')

  return {
    headers: token
      ? {
        Authorization: `Bearer ${token}`,
      }
      : {},
  }
}
export const exportPdf = (id) => {
  return axios.get(
    `${API_URL}/${id}/pdf`,
    {
      responseType: "blob"
    }
  );
}

export const datHang = (data) => {
  return axios.post(`${API_URL}/dat-hang`, data, getAuthHeader())
}

export const getHoaDonByKhachHang = (idKhachHang) => {
  return axios.get(`${API_URL}/khach-hang/${idKhachHang}`, getAuthHeader())
}

export const getHoaDonById = (idHoaDon) => {
  return axios.get(`${API_URL}/${idHoaDon}`, getAuthHeader())
}

export const apDungMaGiamGia = (data) => {
  return axios.post(`${API_URL}/ap-dung-ma-giam-gia`, data)
}

export const huyHoaDon = (id, data) => {
  return axios.put(
    `${API_URL}/${id}/huy`,
    data,
    getAuthHeader()
  )
}
export default {
  datHang,
  getHoaDonByKhachHang,
  getHoaDonById,
  apDungMaGiamGia,
  huyHoaDon,
}

export const taoHoaDonCho = () => {
  return axios.post(
    `${API_URL}/tao-hoa-don-cho`,
    {},
    getAuthHeader()
  )
}

export const getHoaDonCho = () => {
  return axios.get(
    `${API_URL}/hoa-don-cho`,
    getAuthHeader()
  );
};

export const themSanPham = (hoaDonId, data) => {
  return axios.post(
    `${API_URL}/${hoaDonId}/them-san-pham`,
    data,
    getAuthHeader()
  );
};

export const getChiTietHoaDon = (id) => {

  return axios.get(`${API_URL}/${id}/chi-tiet`);

}

export const capNhatSoLuong = (id,soLuong)=>{

  return axios.put(

    `${API_URL}/chi-tiet/${id}/so-luong?soLuong=${soLuong}`

  );

}
export const xoaSanPham = (id) => {

  return axios.delete(

    `${API_URL}/chi-tiet/${id}`,

    getAuthHeader()

  );

}

export const getHoaDonTaiQuay = (
  keyword = "",
  trangThaiThanhToan = "",
  tuNgay = "",
  denNgay = ""
) => {

  return axios.get(`${API_URL}/tai-quay`, {

    params: {

      keyword,
      trangThaiThanhToan,
      tuNgay,
      denNgay

    }

  });

}

export const getHoaDonOnline = (
  keyword = "",
  trangThai = "",
  tuNgay = "",
  denNgay = "",
  page = 0,
  size = 10
) => {

  return axios.get(`${API_URL}/online`, {

    params: {

      keyword,
      trangThai,
      tuNgay,
      denNgay,
      page,
      size

    }

  });

};

export const thanhToanTienMat = (hoaDonId, data) => {

  return axios.post(

    `${API_URL}/${hoaDonId}/thanh-toan`,

    data,

    getAuthHeader()

  );

};
export const doiKhachHang = (hoaDonId, khachHangId) => {

  return axios.put(

    `${API_URL}/${hoaDonId}/khach-hang`,

    null,

    {
      params: {
        khachHangId
      },
      ...getAuthHeader()
    }

  );

};

export const thanhToanVnpayTaiQuay = (hoaDonId) => {
  return axios.post(`${API_URL}/${hoaDonId}/thanh-toan-vnpay`, {}, getAuthHeader())
}




export const apDungVoucherTaiQuay = (hoaDonId, maVoucher) => { return axios.put(`${API_URL}/${hoaDonId}/ap-dung-voucher`, null, { params: { maVoucher }, ...getAuthHeader() }); };
export const xoaVoucherTaiQuay = (hoaDonId) => { return axios.put(`${API_URL}/${hoaDonId}/xoa-voucher`, null, { ...getAuthHeader() }); };

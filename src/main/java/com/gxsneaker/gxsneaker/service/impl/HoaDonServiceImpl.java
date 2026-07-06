package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.*;
import com.gxsneaker.gxsneaker.entity.ChiTietSanPham;
import com.gxsneaker.gxsneaker.entity.HoaDon;
import com.gxsneaker.gxsneaker.entity.HoaDonChiTiet;
import com.gxsneaker.gxsneaker.entity.PhieuGiamGia;
import com.gxsneaker.gxsneaker.repository.ChiTietSanPhamRepository;
import com.gxsneaker.gxsneaker.repository.HoaDonChiTietRepository;
import com.gxsneaker.gxsneaker.repository.HoaDonRepository;
import com.gxsneaker.gxsneaker.repository.PhieuGiamGiaRepository;
import com.gxsneaker.gxsneaker.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final PayOSPaymentService payOSPaymentService;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0(3|5|7|8|9)[0-9]{8}$");

    @Override
    public List<DoanhThuTheoThangDTO> getDoanhThuTheoThang(int year) {
        List<Object[]> data = hoaDonRepository.getDoanhThuTheoThang(year);
        return data.stream()
                .map(item -> new DoanhThuTheoThangDTO(
                        ((Number) item[0]).intValue(),
                        (BigDecimal) item[1]
                ))
                .toList();
    }

    @Override
    public List<TrangThaiDonHangDTO> getThongKeTrangThaiDonHang(int year) {
        List<Object[]> data = hoaDonRepository.getThongKeTrangThaiDonHang(year);
        return data.stream()
                .map(item -> new TrangThaiDonHangDTO(
                        (String) item[0],
                        ((Number) item[1]).longValue()
                ))
                .toList();
    }

    @Override
    public List<TopSanPhamBanChayDTO> getTop5SanPhamBanChay(int year) {
        List<Object[]> data = hoaDonRepository.getTop5SanPhamBanChay(year);
        return data.stream()
                .map(item -> new TopSanPhamBanChayDTO(
                        (String) item[0],
                        ((Number) item[1]).longValue()
                ))
                .toList();
    }

    @Override
    @Transactional
    public HoaDon datHang(DatHangRequestDTO request) {
        // 1. Kiểm tra dữ liệu đầu vào nghiêm ngặt
        validateDatHangRequest(request);

        // 2. Chuẩn hóa dữ liệu chống khoảng trắng bẩn
        request.setTenNguoiNhan(normalizeName(request.getTenNguoiNhan()));
        request.setSoDienThoai(normalizePhone(request.getSoDienThoai()));
        request.setDiaChi(clean(request.getDiaChi()));
        request.setGhiChu(clean(request.getGhiChu()));

        if (request.getMaPhieuGiamGia() != null) {
            request.setMaPhieuGiamGia(request.getMaPhieuGiamGia().trim().toUpperCase());
        }

        String phuongThucThanhToan = xacDinhPhuongThucThanhToan(request);

        HoaDon hoaDon = new HoaDon();
        hoaDon.setIdKhachHang(request.getIdKhachHang());
        hoaDon.setLoaiDon("ONLINE");
        hoaDon.setNgayDatHang(new Date());
        hoaDon.setNgayTao(new Date());
        hoaDon.setNguoiTao("CUSTOMER");
        hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());
        hoaDon.setSoDienThoaiNguoiNhan(request.getSoDienThoai());
        hoaDon.setDiaChiNguoiNhan(request.getDiaChi());
        hoaDon.setGhiChu(request.getGhiChu());
        hoaDon.setPhuongThucThanhToan(phuongThucThanhToan);

        if ("PAYOS".equals(phuongThucThanhToan)) {
            hoaDon.setTrangThai("CHO_THANH_TOAN");
            hoaDon.setTrangThaiThanhToan("CHO_THANH_TOAN");

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 30);
            hoaDon.setHanThanhToan(calendar.getTime());
        } else {
            hoaDon.setTrangThai("CHO_XAC_NHAN");
            hoaDon.setTrangThaiThanhToan("CHUA_THANH_TOAN");
        }

        Long count = hoaDonRepository.countHoaDon() + 1;
        hoaDon.setMaHoaDon(String.format("HD%06d", count));

        BigDecimal tongTienHang = BigDecimal.ZERO;
        hoaDon = hoaDonRepository.save(hoaDon);
        hoaDon.setPayosOrderCode(hoaDon.getId());

        // 3. Xử lý lưu chi tiết đơn hàng và trừ tồn kho
        for (DatHangItemDTO item : request.getItems()) {
            ChiTietSanPham ctsp = chiTietSanPhamRepository
                    .findById(item.getChiTietSanPhamId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            if (ctsp.getSoLuongTon() < item.getSoLuong()) {
                throw new RuntimeException(ctsp.getSanPham().getTenSanPham() + " không đủ tồn kho");
            }

            BigDecimal thanhTien = ctsp.getGiaBan().multiply(BigDecimal.valueOf(item.getSoLuong()));

            HoaDonChiTiet hdct = HoaDonChiTiet.builder()
                    .hoaDon(hoaDon)
                    .chiTietSanPham(ctsp)
                    .soLuong(item.getSoLuong())
                    .donGia(ctsp.getGiaBan())
                    .thanhTien(thanhTien)
                    .build();

            hoaDonChiTietRepository.save(hdct);

            ctsp.setSoLuongTon(ctsp.getSoLuongTon() - item.getSoLuong());
            chiTietSanPhamRepository.save(ctsp);

            tongTienHang = tongTienHang.add(thanhTien);
        }

        BigDecimal phiVanChuyen = BigDecimal.valueOf(30000);
        BigDecimal soTienGiam = BigDecimal.ZERO;
        hoaDon.setTongTienHang(tongTienHang);

        // 4. Áp dụng mã giảm giá
        if (request.getMaPhieuGiamGia() != null && !request.getMaPhieuGiamGia().trim().isEmpty()) {
            PhieuGiamGia phieu = phieuGiamGiaRepository
                    .findByMaPhieuIgnoreCase(request.getMaPhieuGiamGia().trim())
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

            soTienGiam = tinhTienGiam(phieu, tongTienHang);
            hoaDon.setIdPhieuGiamGia(phieu.getId().longValue());

            if (phieu.getSoLuong() != null && phieu.getSoLuong() > 0) {
                phieu.setSoLuong(phieu.getSoLuong() - 1);
                phieuGiamGiaRepository.save(phieu);
            }
        }

        hoaDon.setSoTienGiam(soTienGiam);
        hoaDon.setPhiVanChuyen(phiVanChuyen);
        hoaDon.setTongTienThanhToan(tongTienHang.subtract(soTienGiam).add(phiVanChuyen));

        // 5. Tạo link cổng thanh toán trực tuyến PayOS nếu có
        if ("PAYOS".equals(phuongThucThanhToan)) {
            try {
                CreatePaymentLinkResponse payOSResponse = payOSPaymentService.createPaymentLink(hoaDon);
                hoaDon.setCheckoutUrl(payOSResponse.getCheckoutUrl());
                hoaDon.setPaymentLinkId(payOSResponse.getPaymentLinkId());
            } catch (Exception e) {
                throw new RuntimeException("Không thể tạo link thanh toán payOS: " + e.getMessage());
            }
        }

        return hoaDonRepository.save(hoaDon);
    }

    private String xacDinhPhuongThucThanhToan(DatHangRequestDTO request) {
        String value = request.getPhuongThucThanhToan();

        if (value == null || value.trim().isEmpty()) {
            String ghiChu = request.getGhiChu();
            if (ghiChu != null && ghiChu.toUpperCase().contains("QR")) {
                return "PAYOS";
            }
            return "COD";
        }

        value = value.trim().toUpperCase();
        if ("QR".equals(value) || "PAYOS".equals(value) || "BANK".equals(value)) {
            return "PAYOS";
        }
        return "COD";
    }

    private BigDecimal tinhTienGiam(PhieuGiamGia phieu, BigDecimal tongTienHang) {
        if (phieu == null) return BigDecimal.ZERO;
        if (phieu.getTrangThai() == null || !phieu.getTrangThai()) throw new RuntimeException("Mã giảm giá không hoạt động");

        Date now = new Date();
        if (phieu.getNgayBatDau() != null && now.before(phieu.getNgayBatDau())) throw new RuntimeException("Mã giảm giá chưa đến thời gian sử dụng");
        if (phieu.getNgayKetThuc() != null && now.after(phieu.getNgayKetThuc())) throw new RuntimeException("Mã giảm giá đã hết hạn");
        if (phieu.getSoLuong() != null && phieu.getSoLuong() <= 0) throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng");
        if (phieu.getGiaTriGiam() == null) throw new RuntimeException("Giá trị giảm không hợp lệ");

        BigDecimal dieuKienToiThieu = phieu.getGiaTriDonHangToiThieu() == null ? BigDecimal.ZERO : phieu.getGiaTriDonHangToiThieu();
        if (tongTienHang.compareTo(dieuKienToiThieu) < 0) throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu để dùng mã này");

        BigDecimal soTienGiam;
        if (Boolean.TRUE.equals(phieu.getLoaiGiamGia())) {
            soTienGiam = tongTienHang.multiply(phieu.getGiaTriGiam()).divide(BigDecimal.valueOf(100));
        } else {
            soTienGiam = phieu.getGiaTriGiam();
        }

        if (phieu.getGiaTriGiamToiDa() != null && soTienGiam.compareTo(phieu.getGiaTriGiamToiDa()) > 0) {
            soTienGiam = phieu.getGiaTriGiamToiDa();
        }
        if (soTienGiam.compareTo(tongTienHang) > 0) soTienGiam = tongTienHang;
        if (soTienGiam.compareTo(BigDecimal.ZERO) < 0) soTienGiam = BigDecimal.ZERO;

        return soTienGiam;
    }

    @Override
    @Transactional
    public HoaDon capNhatTrangThai(Long idHoaDon, UpdateTrangThaiRequest request) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        String trangThaiCu = upper(hoaDon.getTrangThai());
        String trangThaiMoi = upper(request.getTrangThaiMoi());

        if (trangThaiMoi.isEmpty()) throw new RuntimeException("Trạng thái mới không được để trống");
        if (trangThaiCu.equals(trangThaiMoi)) throw new RuntimeException("Trạng thái mới phải khác trạng thái hiện tại");

        kiemTraChuyenTrangThaiHopLe(hoaDon, trangThaiMoi, request);

        if ("DA_XAC_NHAN".equals(trangThaiMoi)) hoaDon.setNgayXacNhan(new Date());
        if ("DANG_GIAO".equals(trangThaiMoi)) hoaDon.setNgayGiaoHang(new Date());
        if ("HOAN_THANH".equals(trangThaiMoi)) hoaDon.setNgayHoanThanh(new Date());

        if ("DA_HUY".equals(trangThaiMoi) || "HUY".equals(trangThaiMoi)) {
            hoaDon.setTrangThai("DA_HUY");
            hoaDon.setNgayHuy(new Date());
            hoaDon.setLyDoHuy(request.getGhiChu());

            hoanTraTonKho(hoaDon);
            hoanTraLuotVoucher(hoaDon);

            if ("PAYOS".equalsIgnoreCase(String.valueOf(hoaDon.getPhuongThucThanhToan()))
                    && !"DA_THANH_TOAN".equalsIgnoreCase(String.valueOf(hoaDon.getTrangThaiThanhToan()))) {
                hoaDon.setTrangThaiThanhToan("QUA_HAN");
            }
        } else {
            hoaDon.setTrangThai(trangThaiMoi);
        }

        hoaDon.setNguoiCapNhat(request.getNguoiThucHien() == null || request.getNguoiThucHien().trim().isEmpty() ? "SYSTEM" : request.getNguoiThucHien().trim());
        hoaDon.setNgayCapNhat(new Date());

        return hoaDonRepository.save(hoaDon);
    }

    private void kiemTraChuyenTrangThaiHopLe(HoaDon hoaDon, String trangThaiMoi, UpdateTrangThaiRequest request) {
        String trangThaiCu = upper(hoaDon.getTrangThai());

        if ("DA_HUY".equals(trangThaiCu) || "HUY".equals(trangThaiCu)) throw new RuntimeException("Đơn hàng đã hủy, không thể cập nhật trạng thái");
        if ("HOAN_THANH".equals(trangThaiCu)) throw new RuntimeException("Đơn hàng đã hoàn thành, không thể cập nhật trạng thái");

        if ("DA_XAC_NHAN".equals(trangThaiMoi)) {
            if (!"CHO_XAC_NHAN".equals(trangThaiCu)) throw new RuntimeException("Chỉ đơn chờ xác nhận mới được xác nhận");
            kiemTraThanhToanTruocKhiXacNhan(hoaDon);
        }
        if ("DANG_GIAO".equals(trangThaiMoi)) {
            if (!"DA_XAC_NHAN".equals(trangThaiCu)) throw new RuntimeException("Chỉ đơn đã xác nhận mới được chuyển sang đang giao");
        }
        if ("HOAN_THANH".equals(trangThaiMoi)) {
            if (!"DANG_GIAO".equals(trangThaiCu)) throw new RuntimeException("Chỉ đơn đang giao mới được hoàn thành");
        }
        if ("DA_HUY".equals(trangThaiMoi) || "HUY".equals(trangThaiMoi)) {
            if (request.getGhiChu() == null || request.getGhiChu().trim().isEmpty()) throw new RuntimeException("Vui lòng nhập lý do hủy đơn");
        }
    }

    private void kiemTraThanhToanTruocKhiXacNhan(HoaDon hoaDon) {
        String phuongThuc = String.valueOf(hoaDon.getPhuongThucThanhToan());
        String trangThaiThanhToan = String.valueOf(hoaDon.getTrangThaiThanhToan());
        if ("PAYOS".equalsIgnoreCase(phuongThuc) && !"DA_THANH_TOAN".equalsIgnoreCase(trangThaiThanhToan)) {
            throw new RuntimeException("Đơn PAYOS chưa thanh toán, không thể xác nhận");
        }
    }

    private void hoanTraTonKho(HoaDon hoaDon) {
        if (hoaDon.getHoaDonChiTiets() == null) return;
        for (HoaDonChiTiet ct : hoaDon.getHoaDonChiTiets()) {
            if (ct.getChiTietSanPham() == null) continue;
            ChiTietSanPham ctsp = ct.getChiTietSanPham();
            int tonHienTai = ctsp.getSoLuongTon() == null ? 0 : ctsp.getSoLuongTon();
            int soLuongHoan = ct.getSoLuong() == null ? 0 : ct.getSoLuong();
            ctsp.setSoLuongTon(tonHienTai + soLuongHoan);
            chiTietSanPhamRepository.save(ctsp);
        }
    }

    private void hoanTraLuotVoucher(HoaDon hoaDon) {
        if (hoaDon.getIdPhieuGiamGia() != null) {
            phieuGiamGiaRepository.findById(hoaDon.getIdPhieuGiamGia().intValue()).ifPresent(phieu -> {
                if (phieu.getSoLuong() != null) {
                    phieu.setSoLuong(phieu.getSoLuong() + 1);
                    phieuGiamGiaRepository.save(phieu);
                }
            });
        }
    }

    private String upper(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    @Override
    @Transactional
    public void huyDon(Long id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        String trangThaiCu = upper(hoaDon.getTrangThai());
        if (!"CHO_XAC_NHAN".equals(trangThaiCu) && !"CHO_THANH_TOAN".equals(trangThaiCu)) {
            throw new RuntimeException("Đơn hàng đã được xử lý hoặc giao đi, không thể tự hủy.");
        }

        hoaDon.setTrangThai("DA_HUY");
        hoaDon.setNgayHuy(new Date());
        hoaDon.setLyDoHuy("Khách hàng chủ động hủy đơn");
        hoaDon.setNguoiCapNhat("CUSTOMER");
        hoaDon.setNgayCapNhat(new Date());

        // Sử dụng hàm dùng chung để code ngắn sạch
        hoanTraTonKho(hoaDon);
        hoanTraLuotVoucher(hoaDon);

        if ("PAYOS".equalsIgnoreCase(String.valueOf(hoaDon.getPhuongThucThanhToan()))
                && !"DA_THANH_TOAN".equalsIgnoreCase(String.valueOf(hoaDon.getTrangThaiThanhToan()))) {
            hoaDon.setTrangThaiThanhToan("QUA_HAN");
        }

        hoaDonRepository.save(hoaDon);
    }

    @Override
    public List<TopTonKhoDTO> getTop5TonKho() {
        return chiTietSanPhamRepository.topTonKho(PageRequest.of(0, 5));
    }

    @Override
    public List<OrderResponseDTO> getOrdersByCustomer(Long customerId) {
        List<HoaDon> hoaDons = hoaDonRepository.findByIdKhachHang(customerId);
        return hoaDons.stream().map(this::convertToDTO).toList();
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return convertToDTO(hoaDon);
    }

    private OrderResponseDTO convertToDTO(HoaDon hd) {
        List<OrderItemResponseDTO> items = hd.getHoaDonChiTiets().stream()
                .map(ct -> OrderItemResponseDTO.builder()
                        .chiTietSanPhamId(ct.getChiTietSanPham().getId())
                        .productName(ct.getChiTietSanPham().getSanPham().getTenSanPham())
                        .image(ct.getChiTietSanPham().getSanPham().getAnhDaiDien())
                        .color(ct.getChiTietSanPham().getMauSac().getTen())
                        .size(ct.getChiTietSanPham().getKichThuoc().getSize())
                        .quantity(ct.getSoLuong())
                        .price(ct.getDonGia())
                        .total(ct.getThanhTien())
                        .build())
                .toList();

        return OrderResponseDTO.builder()
                .id(hd.getId())
                .maHoaDon(hd.getMaHoaDon())
                .ngayDatHang(hd.getNgayDatHang())
                .trangThai(hd.getTrangThai())
                .tenNguoiNhan(hd.getTenNguoiNhan())
                .soDienThoai(hd.getSoDienThoaiNguoiNhan())
                .diaChi(hd.getDiaChiNguoiNhan())
                .tongTien(hd.getTongTienThanhToan())
                .items(items)
                .build();
    }

    private void validateDatHangRequest(DatHangRequestDTO request) {
        if (request == null) throw new RuntimeException("Dữ liệu đặt hàng không hợp lệ");
        if (request.getIdKhachHang() == null || request.getIdKhachHang() <= 0) throw new RuntimeException("Vui lòng đăng nhập trước khi thanh toán");

        String tenNguoiNhan = normalizeName(request.getTenNguoiNhan());
        if (tenNguoiNhan.isBlank()) throw new RuntimeException("Vui lòng nhập họ tên người nhận");
        if (tenNguoiNhan.length() < 2 || tenNguoiNhan.length() > 100) throw new RuntimeException("Họ tên người nhận phải từ 2 đến 100 ký tự");

        String phone = normalizePhone(request.getSoDienThoai());
        if (phone.isBlank()) throw new RuntimeException("Vui lòng nhập số điện thoại");
        if (!PHONE_PATTERN.matcher(phone).matches()) throw new RuntimeException("Số điện thoại không đúng định dạng Việt Nam");

        String diaChi = clean(request.getDiaChi());
        if (diaChi.isBlank()) throw new RuntimeException("Vui lòng nhập địa chỉ nhận hàng");
        if (diaChi.length() < 10) throw new RuntimeException("Địa chỉ nhận hàng quá ngắn");
        if (diaChi.length() > 500) throw new RuntimeException("Địa chỉ nhận hàng không được vượt quá 500 ký tự");

        String ghiChu = clean(request.getGhiChu());
        if (ghiChu.length() > 500) throw new RuntimeException("Ghi chú không được vượt quá 500 ký tự");

        if (request.getItems() == null || request.getItems().isEmpty()) throw new RuntimeException("Đơn hàng chưa có sản phẩm");
        if (request.getItems().size() > 50) throw new RuntimeException("Đơn hàng không được vượt quá 50 sản phẩm");

        Set<Long> ids = new HashSet<>();
        for (DatHangItemDTO item : request.getItems()) {
            if (item == null) throw new RuntimeException("Sản phẩm trong đơn hàng không hợp lệ");
            if (item.getChiTietSanPhamId() == null || item.getChiTietSanPhamId() <= 0) throw new RuntimeException("Có sản phẩm bị thiếu ID chi tiết sản phẩm");
            if (!ids.add(item.getChiTietSanPhamId())) throw new RuntimeException("Đơn hàng có sản phẩm bị trùng");
            if (item.getSoLuong() == null || item.getSoLuong() <= 0) throw new RuntimeException("Số lượng sản phẩm phải lớn hơn 0");
            if (item.getSoLuong() > 99) throw new RuntimeException("Số lượng mỗi sản phẩm không được vượt quá 99");
        }

        String maPhieu = clean(request.getMaPhieuGiamGia());
        if (maPhieu.length() > 50) throw new RuntimeException("Mã giảm giá không được vượt quá 50 ký tự");
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeName(String value) {
        return clean(value).replaceAll("\\s+", " ");
    }

    private String normalizePhone(String value) {
        String phone = clean(value).replace(" ", "").replace("-", "").replace(".", "");
        if (phone.startsWith("+84")) {
            phone = "0" + phone.substring(3);
        }
        return phone;
    }
}
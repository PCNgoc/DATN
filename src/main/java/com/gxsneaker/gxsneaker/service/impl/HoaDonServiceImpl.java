package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.*;
import com.gxsneaker.gxsneaker.entity.*;
import com.gxsneaker.gxsneaker.repository.*;
import com.gxsneaker.gxsneaker.service.HoaDonService;
import com.gxsneaker.gxsneaker.service.VNPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.itextpdf.text.pdf.BaseFont;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final PayOSPaymentService payOSPaymentService;
    private final VNPayService vnPayService;



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
        if ("VNPAY".equals(phuongThucThanhToan)) {
            try {
                String checkoutUrl = vnPayService.createPaymentUrl(
                        hoaDon.getId(),
                        hoaDon.getTongTienThanhToan(),
                        "127.0.0.1"
                );

                hoaDon.setCheckoutUrl(checkoutUrl);
            } catch (Exception e) {
                throw new RuntimeException("Không thể tạo link thanh toán VNPAY: " + e.getMessage());
            }
        }

        return hoaDonRepository.save(hoaDon);
    }

    private String xacDinhPhuongThucThanhToan(DatHangRequestDTO request) {
        String value = request.getPhuongThucThanhToan();

        if (value == null || value.trim().isEmpty()) {
            return "COD";
        }

        value = value.trim().toUpperCase();

        if ("QR".equals(value) || "VNPAY".equals(value) || "BANK".equals(value)) {
            return "VNPAY";
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
    public void huyDon(Long id, String ghiChu, String nguoiThucHien) {

        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        String trangThaiCu = upper(hoaDon.getTrangThai());

        if (!"CHO_XAC_NHAN".equals(trangThaiCu)
                && !"CHO_THANH_TOAN".equals(trangThaiCu)) {

            throw new RuntimeException("Đơn hàng đã được xử lý hoặc giao đi, không thể tự hủy.");
        }

        // Bắt nhập lý do
        if (ghiChu == null || ghiChu.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập lý do hủy đơn.");
        }

        hoaDon.setTrangThai("DA_HUY");
        hoaDon.setNgayHuy(new Date());

        // Lưu đúng lý do khách nhập
        String nguoi = "Khách hàng";

        if ("Admin".equalsIgnoreCase(nguoiThucHien)) {
            nguoi = "Admin";
        }

        hoaDon.setLyDoHuy(
                "[" + nguoi + "] " + ghiChu
        );

        hoaDon.setNguoiCapNhat(
                nguoiThucHien == null || nguoiThucHien.isBlank()
                        ? "CUSTOMER"
                        : nguoiThucHien
        );

        hoaDon.setNgayCapNhat(new Date());

        // Hoàn kho
        hoanTraTonKho(hoaDon);

        // Hoàn voucher
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
        List<HoaDon> hoaDons =
                hoaDonRepository.findByIdKhachHangOrderByNgayDatHangDesc(customerId);
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
                        .sku(ct.getChiTietSanPham().getSku())
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
                .maVanDon(hd.getMaVanDon())
                .loaiDon(hd.getLoaiDon())

                .ngayDatHang(hd.getNgayDatHang())
                .ngayXacNhan(hd.getNgayXacNhan())
                .ngayGiaoHang(hd.getNgayGiaoHang())
                .ngayHoanThanh(hd.getNgayHoanThanh())
                .ngayHuy(hd.getNgayHuy())

                .trangThai(hd.getTrangThai())
                .trangThaiThanhToan(hd.getTrangThaiThanhToan())
                .phuongThucThanhToan(hd.getPhuongThucThanhToan())

                .tenNguoiNhan(hd.getTenNguoiNhan())
                .soDienThoai(hd.getSoDienThoaiNguoiNhan())
                .diaChi(hd.getDiaChiNguoiNhan())

                .emailNguoiNhan(
                        hd.getKhachHang() != null
                                ? hd.getKhachHang().getEmail()
                                : null
                )




                .ghiChu(hd.getGhiChu())

                .lyDoHuy(hd.getLyDoHuy())
                .tongTienHang(hd.getTongTienHang())
                .phiVanChuyen(hd.getPhiVanChuyen())
                .soTienGiam(hd.getSoTienGiam())
                .tongTien(hd.getTongTienThanhToan())

                .checkoutUrl(hd.getCheckoutUrl())
                .hanThanhToan(hd.getHanThanhToan())

                .maPhieuGiamGia(
                        hd.getPhieuGiamGia() != null
                                ? hd.getPhieuGiamGia().getMaPhieu()
                                : null
                )

                .tenPhieuGiamGia(
                        hd.getPhieuGiamGia() != null
                                ? hd.getPhieuGiamGia().getTenPhieu()
                                : null
                )

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


    @Override
    public ResponseEntity<byte[]> exportPdf(Long id) {

        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            Document document = new Document(PageSize.A4);

            PdfWriter.getInstance(document, out);

            document.open();

            BaseFont bf = getBaseFont();

            Font title = new Font(bf, 20, Font.BOLD);

            Font normal = new Font(bf, 12);

            Font bold = new Font(bf, 12, Font.BOLD);

            Paragraph p = new Paragraph("GX SNEAKER\n HÓA ĐƠN BÁN HÀNG", title);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            document.add(new Paragraph(" "));

            document.add(new Paragraph("Mã hóa đơn: " + hoaDon.getMaHoaDon(), normal));
            document.add(new Paragraph("Ngày đặt: " + formatDate(hoaDon.getNgayDatHang()), normal));
//            document.add(new Paragraph("Khách hàng: " + hoaDon.getTenNguoiNhan(), normal));
//            document.add(new Paragraph("Email: " + hoaDon.getKhachHang().getEmail(), normal));
//            document.add(new Paragraph("SĐT: " + hoaDon.getSoDienThoaiNguoiNhan(), normal));
//            document.add(new Paragraph("Địa chỉ: " + hoaDon.getDiaChiNguoiNhan(), normal));
            document.add(new Paragraph(
                    "Khách hàng: " +
                            (hoaDon.getTenNguoiNhan() == null ? "Khách lẻ" : hoaDon.getTenNguoiNhan()),
                    normal));

            document.add(new Paragraph(
                    "Email: " +
                            (hoaDon.getKhachHang() == null ? "" : hoaDon.getKhachHang().getEmail()),
                    normal));

            document.add(new Paragraph(
                    "SĐT: " +
                            (hoaDon.getSoDienThoaiNguoiNhan() == null ? "" : hoaDon.getSoDienThoaiNguoiNhan()),
                    normal));

            document.add(new Paragraph(
                    "Địa chỉ: " +
                            (hoaDon.getDiaChiNguoiNhan() == null ? "" : hoaDon.getDiaChiNguoiNhan()),
                    normal));

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            table.addCell(new PdfPCell(new Phrase("Sản phẩm", bold)));
            table.addCell(new PdfPCell(new Phrase("Size", bold)));
            table.addCell(new PdfPCell(new Phrase("Màu", bold)));
            table.addCell(new PdfPCell(new Phrase("SL", bold)));
            table.addCell(new PdfPCell(new Phrase("Thành tiền", bold)));

            for (HoaDonChiTiet ct : hoaDon.getHoaDonChiTiets()) {

                table.addCell(ct.getChiTietSanPham().getSanPham().getTenSanPham());
                table.addCell(String.valueOf(ct.getChiTietSanPham().getKichThuoc().getSize()));
                table.addCell(ct.getChiTietSanPham().getMauSac().getTen());
                table.addCell(String.valueOf(ct.getSoLuong()));
                table.addCell(formatMoney(ct.getThanhTien()));

            }

            document.add(table);

            document.add(new Paragraph(" "));

            document.add(new Paragraph("Tổng tiền hàng: " + formatMoney(hoaDon.getTongTienHang()), bold));
            document.add(new Paragraph("Giảm giá: -" + formatMoney(hoaDon.getSoTienGiam()), bold));
            document.add(new Paragraph("Phí vận chuyển: " + formatMoney(hoaDon.getPhiVanChuyen()), bold));
            document.add(new Paragraph("Tổng thanh toán: " + formatMoney(hoaDon.getTongTienThanhToan()), bold));

            document.add(new Paragraph(" "));

            document.add(new Paragraph("Phương thức thanh toán: " + hoaDon.getPhuongThucThanhToan(), normal));
            document.add(new Paragraph("Trạng thái đơn: " + hoaDon.getTrangThai(), normal));
            document.add(new Paragraph("Trạng thái thanh toán: " + hoaDon.getTrangThaiThanhToan(), normal));

            document.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=HoaDon_" + hoaDon.getMaHoaDon() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(date);
    }

    private String formatMoney(BigDecimal money) {

        if (money == null) {
            return "0";
        }

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        return nf.format(money) + " đ";
    }


    private BaseFont getBaseFont() throws Exception {

        return BaseFont.createFont(
                "D:\\DATN2\\src\\main\\resources\\ fonts\\arial.ttf",
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED
        );

    }

    @Override

    @Transactional
    public void xacNhanThanhToanVnpay(Long hoaDonId, String maGiaoDich) {
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (!"VNPAY".equalsIgnoreCase(String.valueOf(hoaDon.getPhuongThucThanhToan()))) {
            throw new RuntimeException("Đơn hàng không phải thanh toán VNPAY");
        }

        if ("DA_HUY".equalsIgnoreCase(String.valueOf(hoaDon.getTrangThai()))) {
            throw new RuntimeException("Đơn hàng đã hủy");
        }

        hoaDon.setTrangThai("CHO_XAC_NHAN");
        hoaDon.setTrangThaiThanhToan("DA_THANH_TOAN");
        hoaDon.setNgayCapNhat(new Date());
        hoaDon.setNguoiCapNhat("DA_THANH_TOAN");
        hoaDon.setNgayCapNhat(new Date());
        hoaDon.setNguoiCapNhat("VNPAY");
        hoaDon.setGhiChu(
                (hoaDon.getGhiChu() == null ? "" : hoaDon.getGhiChu())
                        + " [VNPAY_THANH_CONG: " + maGiaoDich + "]"
        );

        hoaDonRepository.save(hoaDon);
    }

    public HoaDon taoHoaDonCho() {

        HoaDon hd = new HoaDon();

        hd.setMaHoaDon(generateMaHoaDon());

        hd.setLoaiDon("TAI_QUAY");

        hd.setTrangThai("CHO_XAC_NHAN");

        hd.setTongTienHang(BigDecimal.ZERO);
        hd.setSoTienGiam(BigDecimal.ZERO);
        hd.setPhiVanChuyen(BigDecimal.ZERO);
        hd.setTongTienThanhToan(BigDecimal.ZERO);

        hd.setNgayTao(new Date());

        hd.setNguoiTao("Admin");

        return hoaDonRepository.save(hd);

    }

    private String generateMaHoaDon() {

        Long maxId = hoaDonRepository.getMaxId();

        long next = (maxId == null ? 1 : maxId + 1);

        return String.format("HD%08d", next);

    }
    @Override
    public List<HoaDon> getHoaDonCho() {

        return hoaDonRepository.findByLoaiDonAndTrangThaiOrderByNgayTaoDesc(
                "TAI_QUAY",
                "CHO_XAC_NHAN"
        );

    }

    @Override
    @Transactional
    public void themSanPham(Long hoaDonId, ThemSanPhamRequest request) {

        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(request.getChiTietSanPhamId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (ctsp.getSoLuongTon() < request.getSoLuong()) {
            throw new RuntimeException("Không đủ tồn kho");
        }

        HoaDonChiTiet hdct =
                hoaDonChiTietRepository.findByHoaDon_IdAndChiTietSanPham_Id(
                        hoaDonId,
                        request.getChiTietSanPhamId()
                );

        if (hdct == null) {

            hdct = new HoaDonChiTiet();

            hdct.setHoaDon(hoaDon);
            hdct.setChiTietSanPham(ctsp);
            hdct.setDonGia(ctsp.getGiaBan());
            hdct.setSoLuong(request.getSoLuong());

        } else {

            hdct.setSoLuong(
                    hdct.getSoLuong() + request.getSoLuong()
            );

        }

        hdct.setThanhTien(
                hdct.getDonGia()
                        .multiply(BigDecimal.valueOf(hdct.getSoLuong()))
        );

        hoaDonChiTietRepository.save(hdct);

        ctsp.setSoLuongTon(
                ctsp.getSoLuongTon() - request.getSoLuong()
        );

        chiTietSanPhamRepository.save(ctsp);

        capNhatTongTienHoaDon(hoaDon);

    }
    private void capNhatTongTienHoaDon(HoaDon hoaDon) {

        List<HoaDonChiTiet> list = hoaDonChiTietRepository.findByHoaDonId(hoaDon.getId());

        BigDecimal tongTien = BigDecimal.ZERO;

        for (HoaDonChiTiet ct : list) {

            tongTien = tongTien.add(ct.getThanhTien());

        }

        hoaDon.setTongTienHang(tongTien);

        BigDecimal giamGia = hoaDon.getSoTienGiam() == null
                ? BigDecimal.ZERO
                : hoaDon.getSoTienGiam();

        BigDecimal phiShip = hoaDon.getPhiVanChuyen() == null
                ? BigDecimal.ZERO
                : hoaDon.getPhiVanChuyen();

        hoaDon.setTongTienThanhToan(
                tongTien
                        .subtract(giamGia)
                        .add(phiShip)
        );

        hoaDonRepository.save(hoaDon);

    }

    @Override
    @Transactional
    public void capNhatSoLuong(Long hoaDonChiTietId,Integer soLuong){

        HoaDonChiTiet hdct =
                hoaDonChiTietRepository.findById(hoaDonChiTietId)
                        .orElseThrow();

        ChiTietSanPham ctsp = hdct.getChiTietSanPham();

        int cu = hdct.getSoLuong();

        if(soLuong<=0){

            ctsp.setSoLuongTon(ctsp.getSoLuongTon()+cu);

            chiTietSanPhamRepository.save(ctsp);

            hoaDonChiTietRepository.delete(hdct);

            capNhatTongTienHoaDon(hdct.getHoaDon());

            return;

        }

        int chenhLech = soLuong-cu;

        if(chenhLech>0){

            if(ctsp.getSoLuongTon()<chenhLech){

                throw new RuntimeException("Không đủ tồn");

            }

            ctsp.setSoLuongTon(ctsp.getSoLuongTon()-chenhLech);

        }else{

            ctsp.setSoLuongTon(ctsp.getSoLuongTon()+Math.abs(chenhLech));

        }

        hdct.setSoLuong(soLuong);

        hdct.setThanhTien(

                hdct.getDonGia()

                        .multiply(BigDecimal.valueOf(soLuong))

        );

        hoaDonChiTietRepository.save(hdct);

        chiTietSanPhamRepository.save(ctsp);

        capNhatTongTienHoaDon(hdct.getHoaDon());

    }
    @Override
    @Transactional
    public void doiKhachHang(Long hoaDonId, Long khachHangId) {

        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        hoaDon.setIdKhachHang(khachHangId);

        hoaDonRepository.save(hoaDon);

    }

    @Override
    @Transactional
    public void xoaSanPham(Long hoaDonChiTietId) {

        HoaDonChiTiet hdct = hoaDonChiTietRepository.findById(hoaDonChiTietId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        HoaDon hoaDon = hdct.getHoaDon();

        ChiTietSanPham ctsp = hdct.getChiTietSanPham();

        // cộng lại tồn kho
        ctsp.setSoLuongTon(
                ctsp.getSoLuongTon() + hdct.getSoLuong()
        );

        chiTietSanPhamRepository.save(ctsp);

        // xóa khỏi hóa đơn
        hoaDonChiTietRepository.delete(hdct);

        // cập nhật tiền hóa đơn
        capNhatTongTienHoaDon(hoaDon);

    }

    @Override
    @Transactional
    public void thanhToan(Long hoaDonId, ThanhToanRequest request) {

        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (hoaDon.getTongTienThanhToan()
                .compareTo(request.getTienKhachDua()) > 0) {

            throw new RuntimeException("Khách đưa chưa đủ tiền");

        }

        hoaDon.setTrangThai("HOAN_THANH");

        hoaDon.setTrangThaiThanhToan("DA_THANH_TOAN");

        hoaDon.setPhuongThucThanhToan("TIEN_MAT");

        hoaDon.setNgayThanhToan(new Date());

        hoaDonRepository.save(hoaDon);

    }


}
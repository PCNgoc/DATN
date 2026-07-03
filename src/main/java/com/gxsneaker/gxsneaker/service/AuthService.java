package com.gxsneaker.gxsneaker.service;

import com.gxsneaker.gxsneaker.dto.*;
import com.gxsneaker.gxsneaker.entity.KhachHang;
import com.gxsneaker.gxsneaker.entity.NhanVien;
import com.gxsneaker.gxsneaker.entity.OtpXacThuc;
import com.gxsneaker.gxsneaker.repository.KhachHangRepository;
import com.gxsneaker.gxsneaker.repository.NhanVienRepository;
import com.gxsneaker.gxsneaker.repository.OtpXacThucRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final OtpXacThucRepository otpXacThucRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^0(3|5|7|8|9)[0-9]{8}$");

    public LoginResponse login(LoginRequest request) {

        String username = normalizeUsername(request == null ? null : request.getUsername());
        String password = clean(request == null ? null : request.getPassword());

        validateLoginInput(username, password);

        Optional<KhachHang> kh =
                isEmail(username)
                        ? khachHangRepository.findByEmail(username)
                        : khachHangRepository.findBySoDienThoai(username);

        if (kh.isEmpty()) {
            throw new RuntimeException("Email/Số điện thoại hoặc mật khẩu không đúng");
        }

        if (Boolean.FALSE.equals(kh.get().getTrangThai())) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        if (!passwordEncoder.matches(password, kh.get().getMatKhau())) {
            throw new RuntimeException("Email/Số điện thoại hoặc mật khẩu không đúng");
        }

        String token = jwtService.generateToken(
                kh.get().getEmail(),
                "CUSTOMER"
        );

        return new LoginResponse(
                kh.get().getId(),
                kh.get().getMaKhachHang(),
                kh.get().getHoTen(),
                kh.get().getEmail(),
                kh.get().getSoDienThoai(),
                "Đăng nhập thành công",
                "CUSTOMER",
                token
        );
    }

    public LoginResponse loginNhanVien(LoginRequest request) {

        String username = normalizeUsername(request == null ? null : request.getUsername());
        String password = clean(request == null ? null : request.getPassword());

        validateLoginInput(username, password);

        Optional<NhanVien> nv =
                isEmail(username)
                        ? nhanVienRepository.findByEmail(username)
                        : nhanVienRepository.findBySoDienThoai(username);

        if (nv.isEmpty()) {
            throw new RuntimeException("Email/Số điện thoại hoặc mật khẩu không đúng");
        }

        if (Boolean.FALSE.equals(nv.get().getTrangThai())) {
            throw new RuntimeException("Tài khoản nhân viên đã bị khóa");
        }

        if (!passwordEncoder.matches(password, nv.get().getMatKhau())) {
            throw new RuntimeException("Email/Số điện thoại hoặc mật khẩu không đúng");
        }

        String role = normalizeRole(nv.get());

        String token = jwtService.generateToken(
                nv.get().getEmail(),
                role
        );

        return new LoginResponse(
                nv.get().getId(),
                nv.get().getMaNhanVien(),
                nv.get().getHoTen(),
                nv.get().getEmail(),
                nv.get().getSoDienThoai(),
                "Đăng nhập nhân viên thành công",
                role,
                token
        );
    }

    public RegisterResponse register(RegisterRequest request) {

        if (request.getHoTen() == null || request.getHoTen().isBlank()) {
            throw new RuntimeException("Họ tên không được để trống");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email không được để trống");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }

        if (khachHangRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (khachHangRepository.findBySoDienThoai(request.getSoDienThoai()).isPresent()) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        long count = khachHangRepository.count() + 1;
        String maKhachHang = String.format("KH%03d", count);

        KhachHang khachHang = KhachHang.builder()
                .maKhachHang(maKhachHang)
                .hoTen(request.getHoTen())
                .email(request.getEmail())
                .soDienThoai(request.getSoDienThoai())
                .matKhau(passwordEncoder.encode(request.getPassword()))
                .trangThai(true)
                .daXacThuc(true)
                .build();

        khachHangRepository.save(khachHang);

        return new RegisterResponse(
                khachHang.getMaKhachHang(),
                khachHang.getHoTen(),
                khachHang.getEmail(),
                khachHang.getSoDienThoai(),
                "Đăng ký thành công"
        );
    }

    public LoginResponse me(String token) {

        String email = jwtService.extractUsername(token);

        KhachHang kh = khachHangRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        return new LoginResponse(
                kh.getId(),
                kh.getMaKhachHang(),
                kh.getHoTen(),
                kh.getEmail(),
                kh.getSoDienThoai(),
                "Lấy thông tin thành công",
                "CUSTOMER",
                token
        );
    }

    public String changePassword(String token, ChangePasswordRequest request) {

        String email = jwtService.extractUsername(token);

        KhachHang kh = khachHangRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        if (!passwordEncoder.matches(request.getOldPassword(), kh.getMatKhau())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new RuntimeException("Mật khẩu mới không được để trống");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Xác nhận mật khẩu không khớp");
        }

        kh.setMatKhau(passwordEncoder.encode(request.getNewPassword()));
        khachHangRepository.save(kh);

        return "Đổi mật khẩu thành công";
    }

    public String forgotPassword(ForgotPasswordRequest request) {

        KhachHang kh = khachHangRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        OtpXacThuc otpXacThuc = OtpXacThuc.builder()
                .email(kh.getEmail())
                .maOtp(otp)
                .loaiOtp("FORGOT_PASSWORD")
                .thoiGianHetHan(LocalDateTime.now().plusMinutes(5))
                .daSuDung(false)
                .ngayTao(LocalDateTime.now())
                .build();

        otpXacThucRepository.save(otpXacThuc);
        emailService.sendOtpEmail(kh.getEmail(), otp);

        return "Mã OTP đã được gửi về email";
    }

    public String resetPassword(ResetPasswordRequest request) {

        KhachHang kh = khachHangRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        OtpXacThuc otp = otpXacThucRepository
                .findTopByEmailAndLoaiOtpAndDaSuDungOrderByIdDesc(
                        request.getEmail(),
                        "FORGOT_PASSWORD",
                        false
                )
                .orElseThrow(() -> new RuntimeException("OTP không tồn tại"));

        if (!otp.getMaOtp().equals(request.getOtp())) {
            throw new RuntimeException("OTP không đúng");
        }

        if (otp.getThoiGianHetHan().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP đã hết hạn");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Xác nhận mật khẩu không khớp");
        }

        kh.setMatKhau(passwordEncoder.encode(request.getNewPassword()));
        khachHangRepository.save(kh);

        otp.setDaSuDung(true);
        otpXacThucRepository.save(otp);

        return "Đặt lại mật khẩu thành công";
    }

    public AdminLoginResponse adminLogin(AdminLoginRequest request) {

        String username = normalizeUsername(request == null ? null : request.getUsername());
        String password = clean(request == null ? null : request.getPassword());

        validateLoginInput(username, password);

        Optional<NhanVien> nv =
                isEmail(username)
                        ? nhanVienRepository.findByEmail(username)
                        : nhanVienRepository.findBySoDienThoai(username);

        if (nv.isEmpty()) {
            throw new RuntimeException("Email/Số điện thoại hoặc mật khẩu không đúng");
        }

        if (Boolean.FALSE.equals(nv.get().getTrangThai())) {
            throw new RuntimeException("Tài khoản nhân viên đã bị khóa");
        }

        if (!passwordEncoder.matches(password, nv.get().getMatKhau())) {
            throw new RuntimeException("Email/Số điện thoại hoặc mật khẩu không đúng");
        }

        String role = normalizeRole(nv.get());

        String token =
                jwtService.generateToken(
                        nv.get().getEmail(),
                        role
                );

        return AdminLoginResponse.builder()
                .id(nv.get().getId())
                .maNhanVien(nv.get().getMaNhanVien())
                .hoTen(nv.get().getHoTen())
                .email(nv.get().getEmail())
                .role(role)
                .token(token)
                .avatar(nv.get().getAnhDaiDien())
                .message("Đăng nhập thành công")
                .build();
    }

    private void validateLoginInput(String username, String password) {
        if (username.isBlank()) {
            throw new RuntimeException("Vui lòng nhập email hoặc số điện thoại");
        }

        if (!isEmail(username) && !isPhone(username)) {
            throw new RuntimeException("Email hoặc số điện thoại không đúng định dạng");
        }

        if (password.isBlank()) {
            throw new RuntimeException("Vui lòng nhập mật khẩu");
        }

        if (password.length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
        }
    }

    private String normalizeRole(NhanVien nv) {
        if (nv.getPhanQuyen() == null || nv.getPhanQuyen().getMaQuyen() == null) {
            throw new RuntimeException("Tài khoản chưa được phân quyền");
        }

        String role = nv.getPhanQuyen()
                .getMaQuyen()
                .trim()
                .toUpperCase();

        if ("NHAN_VIEN".equals(role)) {
            role = "STAFF";
        }

        if ("QUAN_TRI".equals(role) || "ADMINISTRATOR".equals(role)) {
            role = "ADMIN";
        }

        if (!"ADMIN".equals(role) && !"STAFF".equals(role)) {
            throw new RuntimeException("Tài khoản chưa được phân quyền quản trị");
        }

        return role;
    }

    private String normalizeUsername(String value) {
        String cleaned = clean(value);

        if (cleaned.contains("@")) {
            return cleaned.toLowerCase();
        }

        return normalizePhone(cleaned);
    }

    private String normalizePhone(String value) {
        String phone = clean(value)
                .replace(" ", "")
                .replace("-", "")
                .replace(".", "");

        if (phone.startsWith("+84")) {
            phone = "0" + phone.substring(3);
        }

        return phone;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isEmail(String value) {
        return value != null && EMAIL_PATTERN.matcher(value).matches();
    }

    private boolean isPhone(String value) {
        return value != null && PHONE_PATTERN.matcher(normalizePhone(value)).matches();
    }
}
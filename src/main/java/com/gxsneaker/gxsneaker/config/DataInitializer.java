package com.gxsneaker.gxsneaker.config;

import com.gxsneaker.gxsneaker.entity.NhanVien;
import com.gxsneaker.gxsneaker.entity.PhanQuyen;
import com.gxsneaker.gxsneaker.repository.NhanVienRepository;
import com.gxsneaker.gxsneaker.repository.PhanQuyenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PhanQuyenRepository phanQuyenRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create ADMIN role if not exists
        PhanQuyen adminRole = phanQuyenRepository.findByMaQuyen("ADMIN")
                .orElseGet(() -> {
                    PhanQuyen role = PhanQuyen.builder()
                            .maQuyen("ADMIN")
                            .tenQuyen("Quản trị viên")
                            .moTa("Quyền tối cao hệ thống")
                            .trangThai(true)
                            .build();
                    log.info("Creating default ADMIN role...");
                    return phanQuyenRepository.save(role);
                });

        // Create STAFF role if not exists
        phanQuyenRepository.findByMaQuyen("STAFF")
                .orElseGet(() -> {
                    PhanQuyen role = PhanQuyen.builder()
                            .maQuyen("STAFF")
                            .tenQuyen("Nhân viên")
                            .moTa("Nhân viên bán hàng")
                            .trangThai(true)
                            .build();
                    log.info("Creating default STAFF role...");
                    return phanQuyenRepository.save(role);
                });

        // Create default Admin account if no employees exist
        if (nhanVienRepository.count() == 0) {
            NhanVien admin = NhanVien.builder()
                    .phanQuyen(adminRole)
                    .maNhanVien("NV001")
                    .hoTen("Admin GX Sneaker")
                    .email("admin@gmail.com")
                    .matKhau(passwordEncoder.encode("123"))
                    .soDienThoai("0987654321")
                    .trangThai(true)
                    .build();
            nhanVienRepository.save(admin);
            log.info("=================================================");
            log.info("CREATED DEFAULT ADMIN ACCOUNT:");
            log.info("Username / Email: admin@gmail.com");
            log.info("Password: 123");
            log.info("=================================================");
        }
    }
}

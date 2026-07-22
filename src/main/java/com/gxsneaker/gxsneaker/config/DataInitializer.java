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
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

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
                    .matKhau(passwordEncoder.encode("123456"))
                    .soDienThoai("0987654321")
                    .trangThai(true)
                    .build();
            nhanVienRepository.save(admin);
            log.info("=================================================");
            log.info("CREATED DEFAULT ADMIN ACCOUNT:");
            log.info("Username / Email: admin@gmail.com");
            log.info("Password: 123456");
            log.info("=================================================");
        }

        log.info("Starting safe database columns alter to NVARCHAR...");
        alterColumnSafely("dia_chi", "ho_ten_nguoi_nhan", "NVARCHAR(255)");
        alterColumnSafely("dia_chi", "tinh_thanh", "NVARCHAR(255)");
        alterColumnSafely("dia_chi", "phuong_xa", "NVARCHAR(255)");
        alterColumnSafely("dia_chi", "dia_chi_chi_tiet", "NVARCHAR(255)");
        alterColumnSafely("khach_hang", "ho_ten", "NVARCHAR(255)");
        alterColumnSafely("nhan_vien", "ho_ten", "NVARCHAR(255)");
        alterColumnSafely("phieu_giam_gia", "ten_phieu", "NVARCHAR(255)");
        alterColumnSafely("chat_lieu", "ten", "NVARCHAR(255)");
        alterColumnSafely("co_giay", "ten", "NVARCHAR(255)");
        alterColumnSafely("danh_muc", "ten", "NVARCHAR(255)");
        alterColumnSafely("de_giay", "ten", "NVARCHAR(255)");
        alterColumnSafely("don_vi_van_chuyen", "ten_cong_ty", "NVARCHAR(255)");
        alterColumnSafely("hoa_don", "ten_nguoi_nhan", "NVARCHAR(255)");
        alterColumnSafely("hoa_don", "ghi_chu", "NVARCHAR(500)");
        alterColumnSafely("hoa_don", "ly_do_huy", "NVARCHAR(500)");
        alterColumnSafely("mau_sac", "ten", "NVARCHAR(255)");
        alterColumnSafely("san_pham", "ten_san_pham", "NVARCHAR(255)");
        alterColumnSafely("san_pham", "mo_ta", "NVARCHAR(500)");
        alterColumnSafely("thuong_hieu", "ten", "NVARCHAR(255)");
        alterColumnSafely("thuong_hieu", "mo_ta", "NVARCHAR(500)");
        alterColumnSafely("xuat_xu", "ten", "NVARCHAR(255)");
        log.info("Finished safe database columns alter.");
        
        log.info("Adding new columns for Voucher Upgrade...");
        addColumnSafely("khach_hang", "hang_thanh_vien", "VARCHAR(50) DEFAULT 'BRONZE'");
        addColumnSafely("phieu_giam_gia", "kieu_phieu", "VARCHAR(50) DEFAULT 'PUBLIC'");
        addColumnSafely("phieu_giam_gia", "dieu_kien_hang_thanh_vien", "VARCHAR(50)");
        
        log.info("Adding missing columns for payment integration...");
        addColumnSafely("hoa_don", "phuong_thuc_thanh_toan", "VARCHAR(50)");
        addColumnSafely("hoa_don", "trang_thai_thanh_toan", "VARCHAR(50)");
        addColumnSafely("hoa_don", "han_thanh_toan", "DATETIME");
        addColumnSafely("hoa_don", "ngay_thanh_toan", "DATETIME");
        addColumnSafely("hoa_don", "payos_order_code", "BIGINT");
        addColumnSafely("hoa_don", "payment_link_id", "VARCHAR(255)");
        addColumnSafely("hoa_don", "checkout_url", "NVARCHAR(2000)");
        addColumnSafely("hoa_don", "dia_chi_nguoi_nhan", "NVARCHAR(1000)");

        log.info("Adding missing columns for nhan_vien...");
        addColumnSafely("nhan_vien", "anh_dai_dien", "VARCHAR(255)");
        addColumnSafely("nhan_vien", "dia_chi", "NVARCHAR(500)");
        addColumnSafely("nhan_vien", "gioi_tinh", "BIT");
        addColumnSafely("nhan_vien", "ngay_tao", "DATETIME");
        addColumnSafely("nhan_vien", "ngay_cap_nhat", "DATETIME");

        log.info("Finished adding new columns.");
    }

    private void addColumnSafely(String tableName, String columnName, String definition) {
        try {
            // Check if column exists, if not add it
            String checkSql = String.format("IF COL_LENGTH('%s', '%s') IS NULL BEGIN ALTER TABLE %s ADD %s %s END", 
                tableName, columnName, tableName, columnName, definition);
            jdbcTemplate.execute(checkSql);
        } catch (Exception e) {
            log.warn("Could not add column {}.{} : {}", tableName, columnName, e.getMessage());
        }
    }

    private void alterColumnSafely(String tableName, String columnName, String definition) {
        try {
            jdbcTemplate.execute(String.format("ALTER TABLE %s ALTER COLUMN %s %s", tableName, columnName, definition));
        } catch (Exception e) {
            log.warn("Could not alter column {}.{} to {}: {}", tableName, columnName, definition, e.getMessage());
        }
    }
}

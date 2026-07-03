package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.NhanVienDTO;
import com.gxsneaker.gxsneaker.entity.NhanVien;
import com.gxsneaker.gxsneaker.entity.PhanQuyen;
import com.gxsneaker.gxsneaker.repository.NhanVienRepository;
import com.gxsneaker.gxsneaker.repository.PhanQuyenRepository;
import com.gxsneaker.gxsneaker.service.NhanVienService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienRepository nhanVienRepository;

    private final PhanQuyenRepository phanQuyenRepository;

    private final PasswordEncoder passwordEncoder;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^0(3|5|7|8|9)[0-9]{8}$");


    // ================= DTO =================

    private NhanVienDTO convertToDTO(NhanVien nv){

        return NhanVienDTO.builder()

                .id(nv.getId())

                .idPhanQuyen(
                        nv.getPhanQuyen()!=null?
                                nv.getPhanQuyen().getId():null
                )

                .tenQuyen(
                        nv.getPhanQuyen()!=null?
                                nv.getPhanQuyen().getTenQuyen():null
                )

                .maNhanVien(nv.getMaNhanVien())

                .hoTen(nv.getHoTen())

                .email(nv.getEmail())

                .soDienThoai(nv.getSoDienThoai())

                .gioiTinh(nv.getGioiTinh())

                .diaChi(nv.getDiaChi())

                .anhDaiDien(nv.getAnhDaiDien())

                .trangThai(nv.getTrangThai())

                .build();

    }

    // ================= AUTO CODE =================

    private String taoMaNhanVien(){

        Optional<NhanVien> last =
                nhanVienRepository.findTopByOrderByIdDesc();

        if(last.isEmpty()){

            return "NV001";

        }

        String ma =
                last.get().getMaNhanVien().replace("NV","");

        int number =
                Integer.parseInt(ma);

        return String.format("NV%03d",number+1);

    }

    // ================= GET ALL =================

    @Override
    public List<NhanVienDTO> getAll() {

        return nhanVienRepository.findAll()

                .stream()

                .map(this::convertToDTO)

                .collect(Collectors.toList());

    }

    // ================= GET ONE =================

    @Override
    public NhanVienDTO getById(Integer id) {

        NhanVien nv =
                nhanVienRepository.findById(id)

                        .orElseThrow(
                                ()->new RuntimeException("Không tìm thấy nhân viên")
                        );

        return convertToDTO(nv);

    }

    // ================= CREATE =================

    @Override
    @Transactional
    public NhanVienDTO create(NhanVienDTO dto) {
        validateCreate(dto);

        PhanQuyen pq =
                phanQuyenRepository.findById(dto.getIdPhanQuyen())

                        .orElseThrow(
                                ()->new RuntimeException("Không tìm thấy quyền")
                        );

        NhanVien nv = new NhanVien();

        nv.setPhanQuyen(pq);

        nv.setMaNhanVien(
                taoMaNhanVien()
        );

        nv.setHoTen(dto.getHoTen());

        nv.setEmail(dto.getEmail());

        nv.setMatKhau(

                passwordEncoder.encode(dto.getMatKhau())

        );

        nv.setSoDienThoai(dto.getSoDienThoai());

        nv.setGioiTinh(dto.getGioiTinh());

        nv.setDiaChi(dto.getDiaChi());

        nv.setAnhDaiDien(dto.getAnhDaiDien());

        nv.setTrangThai(true);

        nv.setNgayTao(LocalDateTime.now());

        nhanVienRepository.save(nv);

        return convertToDTO(nv);

    }

    // ================= UPDATE =================

    @Override
    @Transactional
    public NhanVienDTO update(Integer id, NhanVienDTO dto) {

        NhanVien nv =
                nhanVienRepository.findById(id)

                        .orElseThrow(
                                ()->new RuntimeException("Không tìm thấy nhân viên")
                        );

        validateUpdate(id, dto);


        PhanQuyen pq =
                phanQuyenRepository.findById(dto.getIdPhanQuyen())

                        .orElseThrow(
                                ()->new RuntimeException("Không tìm thấy quyền")
                        );

        nv.setPhanQuyen(pq);

        nv.setHoTen(dto.getHoTen());

        nv.setEmail(dto.getEmail());

        if (dto.getMatKhau() != null && !dto.getMatKhau().isBlank()) {
            nv.setMatKhau(passwordEncoder.encode(dto.getMatKhau()));
        }

        nv.setSoDienThoai(dto.getSoDienThoai());

        nv.setGioiTinh(dto.getGioiTinh());

        nv.setDiaChi(dto.getDiaChi());

        nv.setAnhDaiDien(dto.getAnhDaiDien());

        nv.setTrangThai(dto.getTrangThai());

        nv.setNgayCapNhat(LocalDateTime.now());

        nhanVienRepository.save(nv);

        return convertToDTO(nv);

    }

    // ================= DELETE =================

    @Override
    @Transactional
    public void delete(Integer id) {

        NhanVien nv =
                nhanVienRepository.findById(id)

                        .orElseThrow(
                                ()->new RuntimeException("Không tìm thấy nhân viên")
                        );

        // Soft Delete

        nv.setTrangThai(false);

        nv.setNgayCapNhat(LocalDateTime.now());

        nhanVienRepository.save(nv);

    }

    private void validateCreate(NhanVienDTO dto) {
        normalizeNhanVienDTO(dto);

        validateCommon(dto, true);

        if (nhanVienRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (nhanVienRepository.existsBySoDienThoai(dto.getSoDienThoai())) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }
    }

    private void validateUpdate(Integer id, NhanVienDTO dto) {
        if (id == null) {
            throw new RuntimeException("ID nhân viên không hợp lệ");
        }

        normalizeNhanVienDTO(dto);

        validateCommon(dto, false);

        if (nhanVienRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (nhanVienRepository.existsBySoDienThoaiAndIdNot(dto.getSoDienThoai(), id)) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }
    }

    private void validateCommon(NhanVienDTO dto, boolean isCreate) {
        if (dto == null) {
            throw new RuntimeException("Dữ liệu nhân viên không hợp lệ");
        }

        if (isBlank(dto.getHoTen())) {
            throw new RuntimeException("Họ tên không được để trống");
        }

        if (dto.getHoTen().length() < 2 || dto.getHoTen().length() > 100) {
            throw new RuntimeException("Họ tên phải từ 2 đến 100 ký tự");
        }

        if (isBlank(dto.getEmail())) {
            throw new RuntimeException("Email không được để trống");
        }

        if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
            throw new RuntimeException("Email không đúng định dạng");
        }

        if (isBlank(dto.getSoDienThoai())) {
            throw new RuntimeException("Số điện thoại không được để trống");
        }

        if (!PHONE_PATTERN.matcher(dto.getSoDienThoai()).matches()) {
            throw new RuntimeException("Số điện thoại không đúng định dạng Việt Nam");
        }

        if (dto.getIdPhanQuyen() == null) {
            throw new RuntimeException("Vui lòng chọn quyền nhân viên");
        }

        if (dto.getDiaChi() != null && dto.getDiaChi().length() > 255) {
            throw new RuntimeException("Địa chỉ không được vượt quá 255 ký tự");
        }

        if (isCreate) {
            if (isBlank(dto.getMatKhau())) {
                throw new RuntimeException("Mật khẩu không được để trống");
            }

            if (dto.getMatKhau().length() < 6) {
                throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
            }
        } else {
            if (dto.getMatKhau() != null && !dto.getMatKhau().isBlank()
                    && dto.getMatKhau().length() < 6) {
                throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
            }
        }
    }

    private void normalizeNhanVienDTO(NhanVienDTO dto) {
        if (dto == null) {
            throw new RuntimeException("Dữ liệu nhân viên không hợp lệ");
        }

        dto.setHoTen(clean(dto.getHoTen()).replaceAll("\\s+", " "));
        dto.setEmail(clean(dto.getEmail()).toLowerCase());
        dto.setSoDienThoai(normalizePhone(dto.getSoDienThoai()));

        if (dto.getMatKhau() != null) {
            dto.setMatKhau(dto.getMatKhau().trim());
        }

        if (dto.getDiaChi() != null) {
            dto.setDiaChi(dto.getDiaChi().trim());
        }

        if (dto.getAnhDaiDien() != null) {
            dto.setAnhDaiDien(dto.getAnhDaiDien().trim());
        }
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
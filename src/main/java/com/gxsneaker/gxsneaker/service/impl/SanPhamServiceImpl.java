package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.SanPhamDTO;
import com.gxsneaker.gxsneaker.entity.*;
import com.gxsneaker.gxsneaker.mapper.SanPhamMapper;
import com.gxsneaker.gxsneaker.repository.*;
import com.gxsneaker.gxsneaker.service.SanPhamService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final XuatXuRepository xuatXuRepository;
    private final ChatLieuRepository chatLieuRepository;
    private final CoGiayRepository coGiayRepository;
    private final DeGiayRepository deGiayRepository;
    private final DanhMucRepository danhMucRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final GioHangChiTietRepository gioHangChiTietRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    public SanPhamServiceImpl(
            SanPhamRepository sanPhamRepository,
            ThuongHieuRepository thuongHieuRepository,
            XuatXuRepository xuatXuRepository,
            ChatLieuRepository chatLieuRepository,
            CoGiayRepository coGiayRepository,
            DeGiayRepository deGiayRepository,
            DanhMucRepository danhMucRepository,
            ChiTietSanPhamRepository chiTietSanPhamRepository,
            GioHangChiTietRepository gioHangChiTietRepository,
            HoaDonChiTietRepository hoaDonChiTietRepository
    ) {
        this.sanPhamRepository = sanPhamRepository;
        this.thuongHieuRepository = thuongHieuRepository;
        this.xuatXuRepository = xuatXuRepository;
        this.chatLieuRepository = chatLieuRepository;
        this.coGiayRepository = coGiayRepository;
        this.deGiayRepository = deGiayRepository;
        this.danhMucRepository = danhMucRepository;
        this.chiTietSanPhamRepository = chiTietSanPhamRepository;
        this.gioHangChiTietRepository = gioHangChiTietRepository;
        this.hoaDonChiTietRepository = hoaDonChiTietRepository;
    }

    @Override
    public List<SanPhamDTO> getAll() {
        return sanPhamRepository.findAll()
                .stream()
                .map(SanPhamMapper::toDTO)
                .toList();
    }

    @Override
    public SanPhamDTO getById(Long id) {
        return sanPhamRepository.findById(id)
                .map(SanPhamMapper::toDTO)
                .orElse(null);
    }

    @Override
    public SanPhamDTO create(SanPhamDTO dto) {

        SanPham sp = new SanPham();

        ThuongHieu thuongHieu =
                thuongHieuRepository.findById(dto.getIdThuongHieu()).orElse(null);

        XuatXu xuatXu =
                xuatXuRepository.findById(dto.getIdXuatXu()).orElse(null);

        ChatLieu chatLieu =
                chatLieuRepository.findById(dto.getIdChatLieu()).orElse(null);

        CoGiay coGiay =
                coGiayRepository.findById(dto.getIdCoGiay()).orElse(null);

        DeGiay deGiay =
                deGiayRepository.findById(dto.getIdDeGiay()).orElse(null);

        DanhMuc danhMuc =
                danhMucRepository.findById(dto.getIdDanhMuc()).orElse(null);

        sp.setThuongHieu(thuongHieu);
        sp.setXuatXu(xuatXu);
        sp.setChatLieu(chatLieu);
        sp.setCoGiay(coGiay);
        sp.setDeGiay(deGiay);
        sp.setDanhMuc(danhMuc);

        sp.setMaSanPham(dto.getMaSanPham());
        sp.setTenSanPham(dto.getTenSanPham());

        sp.setGioiTinh(dto.getGioiTinh());

        sp.setMoTaNgan(dto.getMoTaNgan());
        sp.setMoTaChiTiet(dto.getMoTaChiTiet());
        sp.setAnhDaiDien(dto.getAnhDaiDien());
        sp.setTrangThai(dto.getTrangThai());

        sp.setNguoiTao(dto.getNguoiTao());

        sanPhamRepository.save(sp);

        return SanPhamMapper.toDTO(sp);
    }

    @Override
    public SanPhamDTO update(Long id, SanPhamDTO dto) {

        SanPham sp = sanPhamRepository.findById(id).orElse(null);

        if (sp == null) return null;

        sp.setThuongHieu(
                thuongHieuRepository.findById(dto.getIdThuongHieu()).orElse(null)
        );

        sp.setXuatXu(
                xuatXuRepository.findById(dto.getIdXuatXu()).orElse(null)
        );

        sp.setChatLieu(
                chatLieuRepository.findById(dto.getIdChatLieu()).orElse(null)
        );

        sp.setCoGiay(
                coGiayRepository.findById(dto.getIdCoGiay()).orElse(null)
        );

        sp.setDeGiay(
                deGiayRepository.findById(dto.getIdDeGiay()).orElse(null)
        );

        sp.setDanhMuc(
                danhMucRepository.findById(dto.getIdDanhMuc()).orElse(null)
        );

        sp.setMaSanPham(dto.getMaSanPham());
        sp.setTenSanPham(dto.getTenSanPham());

        sp.setGioiTinh(dto.getGioiTinh());

        sp.setMoTaNgan(dto.getMoTaNgan());
        sp.setMoTaChiTiet(dto.getMoTaChiTiet());
        sp.setAnhDaiDien(dto.getAnhDaiDien());
        sp.setTrangThai(dto.getTrangThai());

        sp.setNguoiCapNhat(dto.getNguoiCapNhat());

        return SanPhamMapper.toDTO(
                sanPhamRepository.save(sp)
        );
    }

    @Override
    public void delete(Long id) {

        // 1. Kiểm tra đã từng bán chưa
        boolean daBan = hoaDonChiTietRepository
                .existsByChiTietSanPham_SanPham_Id(id);

        // 2. Nếu đã bán → KHÔNG cho xóa
        if (daBan) {
            throw new RuntimeException("Sản phẩm đã có hóa đơn, không thể xóa");
        }

        // 3. Xóa giỏ hàng (nếu có)
        gioHangChiTietRepository.deleteByChiTietSanPham_SanPham_Id(id);

        // 4. Xóa chi tiết sản phẩm
        chiTietSanPhamRepository.deleteBySanPhamId(id);

        // 5. Xóa sản phẩm
        sanPhamRepository.deleteById(id);
    }
    @Override
    public List<SanPhamDTO> search(
            String keyword
    ) {

        return sanPhamRepository
                .search(keyword)
                .stream()
                .map(SanPhamMapper::toDTO)
                .toList();
    }
    @Override
    public List<SanPhamDTO> getNewProducts() {

        return sanPhamRepository
                .findTop8ByTrangThaiTrueOrderByNgayTaoDesc()
                .stream()
                .map(SanPhamMapper::toDTO)
                .toList();
    }
    @Override
    public List<SanPhamDTO> getFeaturedProducts() {

        return sanPhamRepository
                .findTop12ByTrangThaiTrue()
                .stream()
                .map(SanPhamMapper::toDTO)
                .toList();
    }
    @Override
    public List<SanPhamDTO> getNewestProducts() {

        return sanPhamRepository
                .findNewestProducts()
                .stream()
                .limit(5)
                .map(SanPhamMapper::toDTO)
                .toList();
    }
}
package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.ChiTietSanPhamDTO;
import com.gxsneaker.gxsneaker.entity.*;
import com.gxsneaker.gxsneaker.mapper.ChiTietSanPhamMapper;
import com.gxsneaker.gxsneaker.repository.*;
import com.gxsneaker.gxsneaker.service.ChiTietSanPhamService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChiTietSanPhamServiceImpl
        implements ChiTietSanPhamService {

    private final ChiTietSanPhamRepository repository;

    private final SanPhamRepository sanPhamRepository;

    private final MauSacRepository mauSacRepository;

    private final KichThuocRepository kichThuocRepository;

    public ChiTietSanPhamServiceImpl(
            ChiTietSanPhamRepository repository,
            SanPhamRepository sanPhamRepository,
            MauSacRepository mauSacRepository,
            KichThuocRepository kichThuocRepository
    ) {
        this.repository = repository;
        this.sanPhamRepository = sanPhamRepository;
        this.mauSacRepository = mauSacRepository;
        this.kichThuocRepository = kichThuocRepository;
    }

    @Override
    public List<ChiTietSanPhamDTO> getAll() {

        return repository.findAll()
                .stream()
                .map(ChiTietSanPhamMapper::toDTO)
                .toList();
    }

    @Override
    public List<ChiTietSanPhamDTO> getBySanPham(
            Long idSanPham
    ) {

        return repository
                .findBySanPhamId(idSanPham)
                .stream()
                .map(ChiTietSanPhamMapper::toDTO)
                .toList();
    }

    @Override
    public ChiTietSanPhamDTO getById(Long id) {

        return repository.findById(id)
                .map(ChiTietSanPhamMapper::toDTO)
                .orElse(null);
    }

    @Override
    public ChiTietSanPhamDTO create(
            ChiTietSanPhamDTO dto
    ) {

        ChiTietSanPham ctsp =
                new ChiTietSanPham();

        SanPham sanPham =
                sanPhamRepository
                        .findById(dto.getIdSanPham())
                        .orElse(null);

        MauSac mauSac =
                mauSacRepository
                        .findById(dto.getIdMauSac())
                        .orElse(null);

        KichThuoc kichThuoc =
                kichThuocRepository
                        .findById(dto.getIdKichThuoc())
                        .orElse(null);

        ctsp.setSanPham(sanPham);

        ctsp.setMauSac(mauSac);

        ctsp.setKichThuoc(kichThuoc);

        ctsp.setMaChiTiet(
                dto.getMaChiTiet()
        );

        ctsp.setSoLuongTon(
                dto.getSoLuongTon()
        );

        ctsp.setGiaNhap(
                dto.getGiaNhap()
        );

        ctsp.setGiaBan(
                dto.getGiaBan()
        );

        ctsp.setTrangThai(
                dto.getTrangThai()
        );

        repository.save(ctsp);

        return ChiTietSanPhamMapper
                .toDTO(ctsp);
    }

    @Override
    public ChiTietSanPhamDTO update(
            Long id,
            ChiTietSanPhamDTO dto
    ) {

        ChiTietSanPham old =
                repository.findById(id)
                        .orElse(null);

        if(old == null){
            return null;
        }

        old.setSanPham(
                sanPhamRepository
                        .findById(dto.getIdSanPham())
                        .orElse(null)
        );

        old.setMauSac(
                mauSacRepository
                        .findById(dto.getIdMauSac())
                        .orElse(null)
        );

        old.setKichThuoc(
                kichThuocRepository
                        .findById(dto.getIdKichThuoc())
                        .orElse(null)
        );

        old.setMaChiTiet(
                dto.getMaChiTiet()
        );

        old.setSoLuongTon(
                dto.getSoLuongTon()
        );

        old.setGiaNhap(
                dto.getGiaNhap()
        );

        old.setGiaBan(
                dto.getGiaBan()
        );

        old.setTrangThai(
                dto.getTrangThai()
        );

        return ChiTietSanPhamMapper.toDTO(
                repository.save(old)
        );
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
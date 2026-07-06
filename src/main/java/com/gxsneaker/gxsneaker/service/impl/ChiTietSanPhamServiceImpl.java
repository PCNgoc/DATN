package com.gxsneaker.gxsneaker.service.impl;

import com.gxsneaker.gxsneaker.dto.ChiTietSanPhamDTO;
import com.gxsneaker.gxsneaker.dto.GenerateVariantDTO;
import com.gxsneaker.gxsneaker.entity.*;
import com.gxsneaker.gxsneaker.mapper.ChiTietSanPhamMapper;
import com.gxsneaker.gxsneaker.repository.*;
import com.gxsneaker.gxsneaker.service.ChiTietSanPhamService;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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

        String maxMa = repository.getMaxMaChiTiet();

        String maMoi;

        if (maxMa == null) {
            maMoi = "CT001";
        } else {
            int so = Integer.parseInt(maxMa.substring(2));
            maMoi = String.format("CT%03d", so + 1);
        }

        ctsp.setMaChiTiet(maMoi);

        String sku =
                sanPham.getMaSanPham()
                        + "-"
                        + mauSac.getMa()
                        + "-"
                        + kichThuoc.getMa();

        if(repository.existsBySku(sku)){
            throw new RuntimeException("Biến thể này đã tồn tại");
        }

        ctsp.setSku(sku);

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
        boolean tonTai = repository.existsBySanPhamIdAndMauSacIdAndKichThuocId(
                dto.getIdSanPham(),
                dto.getIdMauSac(),
                dto.getIdKichThuoc()
        );

        if (tonTai) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Biến thể này đã tồn tại."
            );
        }
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

        String sku =
                old.getSanPham().getMaSanPham()
                        + "-"
                        + old.getMauSac().getMa()
                        + "-"
                        + old.getKichThuoc().getMa();

        old.setSku(sku);
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
        if(repository.existsWhenUpdate(
                id,
                dto.getIdSanPham(),
                dto.getIdMauSac(),
                dto.getIdKichThuoc()
        )){
            throw new RuntimeException("Biến thể đã tồn tại");
        }
        return ChiTietSanPhamMapper.toDTO(
                repository.save(old)
        );
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void generateVariants(GenerateVariantDTO dto) {

        SanPham sanPham = sanPhamRepository
                .findById(dto.getIdSanPham())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        for (Long mauId : dto.getMauSacIds()) {

            MauSac mauSac = mauSacRepository
                    .findById(mauId)
                    .orElse(null);

            for (Long sizeId : dto.getKichThuocIds()) {

                KichThuoc kichThuoc = kichThuocRepository
                        .findById(sizeId)
                        .orElse(null);

                // kiểm tra đã tồn tại chưa
                boolean tonTai = repository
                        .existsBySanPhamIdAndMauSacIdAndKichThuocId(
                                dto.getIdSanPham(),
                                mauId,
                                sizeId
                        );

                if (tonTai) {
                    continue;
                }

                ChiTietSanPham ct = new ChiTietSanPham();

                ct.setSanPham(sanPham);

                ct.setMauSac(mauSac);

                ct.setKichThuoc(kichThuoc);

                //-----------------------------------
                // Mã chi tiết
                //-----------------------------------

                String maxMa = repository.getMaxMaChiTiet();

                String maMoi;

                if (maxMa == null) {

                    maMoi = "CT001";

                } else {

                    int so = Integer.parseInt(maxMa.substring(2));

                    maMoi = String.format("CT%03d", so + 1);
                }

                ct.setMaChiTiet(maMoi);

                //-----------------------------------
                // SKU
                //-----------------------------------

                String sku =
                        sanPham.getMaSanPham()
                                + "-"
                                + mauSac.getMa()
                                + "-"
                                + kichThuoc.getMa();

                ct.setSku(sku);

                //-----------------------------------

                ct.setGiaNhap(dto.getGiaNhap());

                ct.setGiaBan(dto.getGiaBan());

                ct.setSoLuongTon(dto.getSoLuongTon());

                ct.setTrangThai(dto.getTrangThai());

                repository.save(ct);

            }

        }

    }
}
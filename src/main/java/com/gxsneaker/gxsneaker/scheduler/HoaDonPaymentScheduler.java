package com.gxsneaker.gxsneaker.scheduler;

import com.gxsneaker.gxsneaker.entity.ChiTietSanPham;
import com.gxsneaker.gxsneaker.entity.HoaDon;
import com.gxsneaker.gxsneaker.entity.HoaDonChiTiet;
import com.gxsneaker.gxsneaker.entity.PhieuGiamGia;
import com.gxsneaker.gxsneaker.repository.ChiTietSanPhamRepository;
import com.gxsneaker.gxsneaker.repository.HoaDonRepository;
import com.gxsneaker.gxsneaker.repository.PhieuGiamGiaRepository;
import com.gxsneaker.gxsneaker.service.impl.PayOSPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HoaDonPaymentScheduler {

    private final HoaDonRepository hoaDonRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final PayOSPaymentService payOSPaymentService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void huyHoaDonQuaHanThanhToan() {
        List<HoaDon> expiredOrders =
                hoaDonRepository.findByTrangThaiThanhToanAndHanThanhToanBefore(
                        "CHO_THANH_TOAN",
                        new Date()
                );

        for (HoaDon hoaDon : expiredOrders) {
            if ("DA_THANH_TOAN".equals(hoaDon.getTrangThaiThanhToan())) {
                continue;
            }

            hoaDon.setTrangThai("DA_HUY");
            hoaDon.setTrangThaiThanhToan("QUA_HAN");
            hoaDon.setNgayHuy(new Date());
            hoaDon.setLyDoHuy("Quá hạn thanh toán");
            hoaDon.setNgayCapNhat(new Date());

            if (hoaDon.getHoaDonChiTiets() != null) {
                for (HoaDonChiTiet chiTiet : hoaDon.getHoaDonChiTiets()) {
                    ChiTietSanPham ctsp = chiTiet.getChiTietSanPham();

                    if (ctsp != null) {
                        ctsp.setSoLuongTon(ctsp.getSoLuongTon() + chiTiet.getSoLuong());
                        chiTietSanPhamRepository.save(ctsp);
                    }
                }
            }

            if (hoaDon.getIdPhieuGiamGia() != null) {
                phieuGiamGiaRepository
                        .findById(hoaDon.getIdPhieuGiamGia().intValue())
                        .ifPresent(phieu -> {
                            Integer soLuong = phieu.getSoLuong() == null ? 0 : phieu.getSoLuong();
                            phieu.setSoLuong(soLuong + 1);
                            phieuGiamGiaRepository.save(phieu);
                        });
            }

            payOSPaymentService.cancelPaymentLink(
                    hoaDon.getPayosOrderCode(),
                    "Quá hạn thanh toán"
            );

            hoaDonRepository.save(hoaDon);
        }
    }
}
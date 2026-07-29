package com.gxsneaker.gxsneaker.scheduler;

import com.gxsneaker.gxsneaker.repository.PhieuGiamGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PhieuGiamGiaScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PhieuGiamGiaScheduler.class);

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    // Chạy mỗi phút (60 giây) một lần để kiểm tra và vô hiệu hóa phiếu giảm giá đã hết hạn
    @Scheduled(cron = "0 * * * * *")
    public void autoUpdateExpiredVouchers() {
        try {
            int updatedCount = phieuGiamGiaRepository.updateTrangThaiHetHan();
            if (updatedCount > 0) {
                logger.info("Đã cập nhật tự động vô hiệu hóa cho {} phiếu giảm giá hết hạn.", updatedCount);
            }
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật trạng thái phiếu giảm giá hết hạn: {}", e.getMessage());
        }
    }
}

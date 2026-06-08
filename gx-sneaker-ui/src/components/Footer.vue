<template>
  <footer class="footer">
    <!-- Đăng ký nhận tin -->
    <div class="newsletter">
      <div class="newsletter-title">
        ĐĂNG KÝ NHẬN ƯU ĐÃI
      </div>

      <div class="newsletter-form">
        <input
          type="email"
          v-model="email"
          placeholder="Nhập email của bạn"
        >
        <button @click="subscribe">
          ĐĂNG KÝ
        </button>
      </div>
    </div>

    <!-- Nội dung footer -->
    <div class="footer-content">
      <div class="footer-column">
        <h3>GIỚI THIỆU</h3>

        <p>
          GX Sneaker chuyên cung cấp các dòng giày thể thao
          chính hãng từ Nike, Adidas, Converse, Puma,
          New Balance và nhiều thương hiệu nổi tiếng khác.
        </p>

        <p>
          <i class="fa-solid fa-location-dot"></i>
          Hà Nội, Việt Nam
        </p>

        <p>
          <i class="fa-solid fa-phone"></i>
          0123 456 789
        </p>

        <p>
          <i class="fa-solid fa-envelope"></i>
          gxsneaker@gmail.com
        </p>
      </div>

      <div class="footer-column">
        <h3>CHÍNH SÁCH</h3>

        <ul>
          <li><RouterLink to="/bao-mat">Chính sách bảo mật</RouterLink></li>
          <li><RouterLink to="/van-chuyen">Chính sách vận chuyển</RouterLink></li>
          <li><RouterLink to="/doi-tra">Chính sách đổi trả</RouterLink></li>
          <li><RouterLink to="/thanh-toan">Chính sách thanh toán</RouterLink></li>
          <li><RouterLink to="/bao-hanh">Chính sách bảo hành</RouterLink></li>
        </ul>
      </div>

      <div class="footer-column">
        <h3>HỖ TRỢ KHÁCH HÀNG</h3>

        <ul>
          <li><RouterLink to="/huong-dan-mua-hang">Hướng dẫn mua hàng</RouterLink></li>
          <li><RouterLink to="/huong-dan-thanh-toan">Hướng dẫn thanh toán</RouterLink></li>
          <li><RouterLink to="/faq">Câu hỏi thường gặp</RouterLink></li>
          <li><RouterLink to="/lien-he">Liên hệ</RouterLink></li>
        </ul>
      </div>

      <div class="footer-column">
        <h3>THEO DÕI CHÚNG TÔI</h3>

        <div class="social-icons">
          <a href="#" target="_blank">
            <i class="fab fa-facebook-f"></i>
          </a>

          <a href="#" target="_blank">
            <i class="fab fa-tiktok"></i>
          </a>

          <a href="#" target="_blank">
            <i class="fab fa-instagram"></i>
          </a>

          <a href="#" target="_blank">
            <i class="fa-solid fa-comment"></i>
          </a>
        </div>
      </div>
    </div>

    <!-- Copyright -->
    <div class="copyright">
      © 2026 GX Sneaker. All Rights Reserved.
    </div>
  </footer>
</template>

<script setup>
import { ref } from 'vue'

const email = ref('')

const subscribe = () => {
  if (!email.value) {
    alert('Vui lòng nhập email!')
    return
  }

  alert(`Đăng ký thành công: ${email.value}`)
  email.value = ''
}
</script>

<style scoped>
/* CSS của footer đặt ở đây */

/* ================= FOOTER ================= */

.footer{
  margin-top:60px;
  background:#111;
  color:white;
}

/* Newsletter */

.newsletter{
  background:#e60023;
  padding:25px 60px;

  display:flex;
  justify-content:space-between;
  align-items:center;
}

.newsletter-title{
  font-size:24px;
  font-weight:700;
}

.newsletter-form{
  display:flex;
}

.newsletter-form input{
  width:350px;
  padding:14px;
  border:none;
  outline:none;
}

.newsletter-form button{
  padding:14px 30px;
  border:none;
  background:black;
  color:white;
  font-weight:600;
  cursor:pointer;
}

/* Content */

.footer-content{
  display:grid;
  grid-template-columns:repeat(4,1fr);

  gap:40px;

  padding:50px 60px;
}

.footer-column h3{
  margin-bottom:20px;
  color:white;
}

.footer-column p{
  margin-bottom:12px;
  color:#ddd;
  line-height:1.6;
}

.footer-column ul{
  list-style:none;
}

.footer-column ul li{
  margin-bottom:12px;
  color:#ddd;
  cursor:pointer;
}

.footer-column ul li:hover{
  color:#ff3b3b;
}

.social-icons{
  display:flex;
  gap:15px;
  margin-top:20px;
}

.social-icons a{
  width:45px;
  height:45px;

  background:#222;

  display:flex;
  justify-content:center;
  align-items:center;

  border-radius:50%;

  color:white;
  text-decoration:none;

  transition:0.3s;
}

.social-icons a:hover{
  background:#e60023;
}

/* Copyright */

.copyright{
  text-align:center;
  padding:20px;
  border-top:1px solid #333;
  color:#aaa;
}
</style>

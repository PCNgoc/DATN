@echo off
echo ==========================================
echo    KHOI DONG HE THONG GX-SNEAKER
echo ==========================================

echo [1/2] Dang khoi dong Backend (Spring Boot)...
start "GX-Sneaker Backend (Java)" cmd /k "mvn spring-boot:run"

echo [2/2] Dang khoi dong Frontend (VueJS)...
start "GX-Sneaker Frontend (Vue)" cmd /k "cd gx-sneaker-ui && npm run dev"

echo.
echo He thong dang duoc khoi dong trong 2 cua so moi.
echo Vui long cho it phut de Backend va Frontend load hoan tat.
echo ==========================================
pause

const banners = [
    "/images/banner1.png",
    "/images/banner2.png"
];

let current = 0;

const banner = document.getElementById("bannerSlider");

setInterval(() => {

    current = (current + 1) % banners.length;

    banner.src = banners[current];

}, 3000);
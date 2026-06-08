<template>
  <section class="hero">
    <img
      :src="banners[current]"
      alt="Banner GX Sneaker"
      class="banner-image"
    />

    <div class="slider-dots">
      <span
        v-for="(banner, index) in banners"
        :key="index"
        class="dot"
        :class="{ active: current === index }"
      ></span>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

import banner1 from '@/assets/images/banner1.png'
import banner2 from '@/assets/images/banner2.png'

const current = ref(0)

const banners = [
  banner1,
  banner2
]

let intervalId = null

onMounted(() => {
  intervalId = setInterval(() => {
    current.value = (current.value + 1) % banners.length
  }, 3000)
})

onUnmounted(() => {
  clearInterval(intervalId)
})
</script>

<style scoped>
.hero {
  position: relative;
  width: 100%;
  overflow: hidden;
}

.banner-image {
  width: 100%;
  display: block;
}

.slider-dots {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
}

.dot {
  width: 12px;
  height: 12px;
  margin: 0 5px;
  border-radius: 50%;
  background: #ccc;
  display: inline-block;
  cursor: pointer;
}

.dot.active {
  background: #d70018;
}
</style>

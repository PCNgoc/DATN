import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '../views/HomeView.vue'

// Auth


// Danh mục sản phẩm
import ThuongHieuView from '../views/ThuongHieuView.vue'
import DanhMucView from '@/views/DanhMucView.vue'
import XuatXuView from '@/views/XuatXuView.vue'
import ChatLieuView from '@/views/ChatLieuView.vue'
import CoGiayView from '@/views/CoGiayView.vue'
import DeGiayView from '@/views/DeGiayView.vue'
import KichThuocView from '@/views/KichThuocView.vue'
import MauSacView from '@/views/MauSacView.vue'
import SanPhamView from '@/views/SanPhamView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/san-pham',
      name: 'san-pham',
      component: SanPhamView,
    },


    // Auth
    // Danh mục
    {
      path: '/thuong-hieu',
      name: 'thuong-hieu',
      component: ThuongHieuView,
    },
    {
      path: '/danh-muc',
      name: 'danh-muc',
      component: DanhMucView,
    },
    {
      path: '/xuat-xu',
      name: 'xuat-xu',
      component: XuatXuView,
    },
    {
      path: '/chat-lieu',
      name: 'chat-lieu',
      component: ChatLieuView,
    },
    {
      path: '/co-giay',
      name: 'co-giay',
      component: CoGiayView,
    },
    {
      path: '/de-giay',
      name: 'de-giay',
      component: DeGiayView,
    },
    {
      path: '/mau-sac',
      name: 'mau-sac',
      component: MauSacView,
    },
    {
      path: '/kich-thuoc',
      name: 'kich-thuoc',
      component: KichThuocView,
    }
    ]
})

export default router

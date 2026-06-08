import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ThuongHieuView from '../views/ThuongHieuView.vue'
import DanhMucView from '@/views/DanhMucView.vue'
import XuatXuView from '@/views/XuatXuView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/thuong-hieu',
      name: 'thuong-hieu',
      component: ThuongHieuView
    },
    {
      path: '/danh-muc',
      name:'danh-muc',
      component: DanhMucView
    },
    {
      path: '/xuat-xu',
      name:'xuat-xu',
      component: XuatXuView
    }
  ]
})

export default router

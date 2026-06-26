import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'


// Bootstrap trước
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'


// CSS custom sau để override Bootstrap
import '@/assets/css/style.css'


// Font Awesome
import '@fortawesome/fontawesome-free/css/all.min.css'



const app = createApp(App)


app.use(createPinia())

app.use(router)


app.mount('#app')

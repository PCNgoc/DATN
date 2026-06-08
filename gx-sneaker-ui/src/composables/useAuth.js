import { ref } from 'vue'

const user = ref(JSON.parse(localStorage.getItem('user')))

export function useAuth() {
  const setLogin = (data) => {
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(data))
    user.value = data
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    user.value = null
    window.location.href = '/login'
  }

  return {
    user,
    setLogin,
    logout,
  }
}

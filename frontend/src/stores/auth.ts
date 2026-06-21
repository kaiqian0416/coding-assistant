import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { userApi } from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<any>(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!user.value)
  const isAdmin = computed(() => user.value?.role === 'admin')
  const userId = computed(() => user.value?.id)

  function setUser(u: any) {
    user.value = u
    if (u) {
      localStorage.setItem('user', JSON.stringify(u))
    } else {
      localStorage.removeItem('user')
    }
  }

  async function login(data: { username: string; password: string }) {
    const res = await userApi.login(data)
    if (res.data.code === 200) {
      setUser(res.data.data.user)
    }
    return res.data
  }

  async function register(data: { username: string; password: string; nickname?: string; email?: string }) {
    const res = await userApi.register(data)
    return res.data
  }

  async function logout() {
    try {
      await userApi.logout()
    } finally {
      setUser(null)
    }
  }

  async function fetchProfile() {
    const res = await userApi.getProfile()
    if (res.data.code === 200) {
      setUser(res.data.data)
    }
    return res.data
  }

  return { user, isLoggedIn, isAdmin, userId, setUser, login, register, logout, fetchProfile }
})

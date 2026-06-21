<template>
  <div class="register-page">
    <ParticleBackground />
    <div class="register-wrapper">
      <div class="glass-card">
        <div class="glow-border"></div>
        <div class="card-inner">
          <div class="logo-area">
            <div class="logo-icon">🚀</div>
            <h1>创建账号</h1>
            <p class="subtitle">加入 AI 编程练习平台</p>
          </div>
          <div v-if="error" class="alert alert-error">{{ error }}</div>
          <div v-if="success" class="alert alert-success">{{ success }}</div>
          <form @submit.prevent="handleRegister">
            <div class="input-group"><span class="input-icon">👤</span><input v-model="form.username" placeholder="用户名" required /></div>
            <div class="input-group"><span class="input-icon">🔒</span><input v-model="form.password" type="password" placeholder="密码（至少6位）" required /></div>
            <div class="input-group"><span class="input-icon">😊</span><input v-model="form.nickname" placeholder="昵称（可选）" /></div>
            <div class="input-group"><span class="input-icon">📧</span><input v-model="form.email" type="email" placeholder="邮箱（可选）" /></div>
            <button type="submit" class="neon-btn" :disabled="loading"><span>{{ loading ? '注册中...' : '注 册' }}</span></button>
          </form>
          <p class="login-link">已有账号？<router-link to="/login">立即登录</router-link></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import ParticleBackground from '@/components/ParticleBackground.vue'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false); const error = ref(''); const success = ref('')
const form = reactive({ username: '', password: '', nickname: '', email: '' })

async function handleRegister() {
  loading.value = true; error.value = ''; success.value = ''
  try {
    const res = await auth.register(form)
    if (res.code === 200) { success.value = '注册成功！即将跳转登录...'; setTimeout(() => router.push('/login'), 1500) }
    else { error.value = res.message }
  } catch (e: any) { error.value = e.response?.data?.message || '注册失败' }
  finally { loading.value = false }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh; display: flex; align-items: center; justify-content: center;
  background: var(--bg-primary); position: relative; overflow: hidden;
  transition: background 0.3s;
}
.register-wrapper { position: relative; z-index: 10; }
.glass-card {
  position: relative; width: 440px;
  background: var(--bg-card); backdrop-filter: blur(20px);
  border: 1px solid var(--border-color); border-radius: 24px; padding: 40px;
  overflow: hidden; transition: background 0.3s;
}
.glow-border {
  position: absolute; inset: -2px; border-radius: 26px;
  background: conic-gradient(from 0deg, #8b5cf6, #6366f1, #ec4899, #f43f5e, #8b5cf6, #6366f1, #ec4899, #f43f5e);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor; mask-composite: exclude;
  padding: 2px; background-size: 200% 200%;
  animation: rotateGlow 4s linear infinite; opacity: 0.4; transition: opacity 0.4s;
}
.glass-card:hover .glow-border { opacity: 0.9; animation-duration: 2s; }
@keyframes rotateGlow { 0% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } 100% { background-position: 0% 50%; } }
.card-inner { position: relative; z-index: 1; }

.logo-area { text-align: center; margin-bottom: 28px; }
.logo-icon { font-size: 48px; margin-bottom: 8px; animation: float 3s ease-in-out infinite; }
@keyframes float { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-8px); } }
.logo-area h1 {
  font-size: 22px; font-weight: 800;
  background: linear-gradient(135deg, #8b5cf6, #ec4899, #f43f5e);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
}
.subtitle { font-size: 13px; color: var(--text-muted); margin-top: 4px; letter-spacing: 2px; }

form { display: flex; flex-direction: column; gap: 14px; }
.input-group {
  display: flex; align-items: center;
  background: var(--bg-input); border: 1px solid var(--border-input);
  border-radius: 12px; padding: 0 14px;
  transition: border-color 0.3s, box-shadow 0.3s, background 0.3s;
}
.input-group:focus-within { border-color: var(--accent); box-shadow: 0 0 20px rgba(139,92,246,0.15); }
.input-icon { font-size: 18px; margin-right: 10px; opacity: 0.6; }
.input-group input { flex: 1; background: none; border: none; outline: none; color: var(--text-primary); font-size: 14px; padding: 14px 0; }
.input-group input::placeholder { color: var(--text-placeholder); }

.neon-btn {
  position: relative; display: block; width: 100%;
  padding: 14px; border: none; border-radius: 12px;
  font-size: 16px; font-weight: 700; letter-spacing: 4px; cursor: pointer; overflow: hidden; margin-top: 4px;
  background: linear-gradient(135deg, #8b5cf6, #6366f1, #ec4899, #f43f5e);
  background-size: 300% 300%; animation: gradientMove 3s ease infinite;
  color: #fff; transition: transform 0.2s, box-shadow 0.3s;
}
.neon-btn:hover { transform: translateY(-2px); box-shadow: 0 0 30px rgba(139,92,246,0.4); }
.neon-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.neon-btn::before {
  content: ''; position: absolute; top: 0; left: -100%; width: 60%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transform: skewX(-25deg); transition: left 0.5s;
}
.neon-btn:hover::before { left: 150%; }
@keyframes gradientMove { 0% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } 100% { background-position: 0% 50%; } }

.login-link { text-align: center; margin-top: 20px; font-size: 14px; color: var(--text-muted); }
.login-link a { color: var(--accent); text-decoration: none; font-weight: 600; }
.login-link a:hover { color: var(--accent-light); }
</style>

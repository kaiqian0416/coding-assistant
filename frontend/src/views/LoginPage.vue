<template>
  <div class="login-page">
    <ParticleBackground />
    <FireworksEffect :active="showFireworks" />

    <div class="login-wrapper" :class="{ 'slide-up': showFireworks }">
      <div class="glass-card">
        <div class="glow-border"></div>
        <div class="card-inner">
          <div class="logo-area">
            <div class="logo-icon">⚡</div>
            <h1>AI 编程练习助手</h1>
            <p class="subtitle">智能化编程学习平台</p>
          </div>
          <div v-if="error" class="alert alert-error">{{ error }}</div>
          <form @submit.prevent="handleLogin" class="login-form">
            <div class="input-group"><span class="input-icon">👤</span><input v-model="form.username" placeholder="用户名" required /></div>
            <div class="input-group"><span class="input-icon">🔒</span><input v-model="form.password" type="password" placeholder="密码" required /></div>
            <button type="submit" class="neon-btn" :disabled="loading"><span>{{ loading ? '登录中...' : '登 录' }}</span></button>
          </form>
          <p class="register-link">还没有账号？<router-link to="/register">立即注册</router-link></p>
        </div>
      </div>
    </div>

    <div v-if="showWelcome" class="welcome-overlay" @click.self="closeWelcome">
      <div class="welcome-modal">
        <div class="welcome-bg"></div>
        <div class="welcome-content">
          <div class="welcome-emoji">{{ isAdmin ? '👋' : '🎉' }}</div>
          <h1 class="welcome-title">{{ isAdmin ? '欢迎回来，管理员！' : `欢迎回来，${username}！` }}</h1>
          <p class="welcome-typing">{{ displayedText }}<span class="cursor">|</span></p>
          <div class="welcome-quote">💬 {{ randomQuote }}</div>
          <button class="neon-btn welcome-btn" @click="closeWelcome"><span>{{ isAdmin ? '进入后台 →' : '开始学习 →' }}</span></button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import ParticleBackground from '@/components/ParticleBackground.vue'
import FireworksEffect from '@/components/FireworksEffect.vue'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const error = ref('')
const form = reactive({ username: '', password: '' })
const showFireworks = ref(false)
const showWelcome = ref(false)
const username = ref('')
const isAdmin = ref(false)
const displayedText = ref('')
const randomQuote = ref('')
let typeInterval: ReturnType<typeof setInterval> | null = null

const studentQuotes = [
  '代码是写给人看的，顺便能在机器上运行。 — Harold Abelson',
  '在编程的世界里，没有完美的代码，只有不断改进的代码。',
  '每一次提交都让你离大神更近一步。', '不要怕犯错，每个错误都是最好的老师。',
  '编程不是关于你知道什么，而是关于你能学到什么。',
  'Talk is cheap. Show me the code. — Linus Torvalds',
]
const adminQuotes = [
  '好的管理者不是控制一切，而是让一切井井有条。',
  '质量不是偶然，而是用心的结果。', '管理是把事情做对，领导是做对的事情。',
  '优秀的题库培养优秀的程序员。', '每一次审核都在为代码质量把关。',
]

async function handleLogin() {
  loading.value = true; error.value = ''
  try {
    const res = await auth.login(form)
    if (res.code === 200) {
      username.value = auth.user?.nickname || auth.user?.username || ''
      isAdmin.value = auth.isAdmin
      randomQuote.value = isAdmin.value
        ? adminQuotes[Math.floor(Math.random() * adminQuotes.length)]
        : studentQuotes[Math.floor(Math.random() * studentQuotes.length)]
      showFireworks.value = true
      setTimeout(() => { showWelcome.value = true; startTyping() }, 600)
    } else { error.value = res.message }
  } catch (e: any) { error.value = e.response?.data?.message || '登录失败' }
  finally { loading.value = false }
}

function startTyping() {
  const text = isAdmin.value ? '今天也要好好管理题库哦 📚' : '今天也要加油刷题哦 💪'
  let i = 0; displayedText.value = ''
  typeInterval = setInterval(() => {
    if (i < text.length) { displayedText.value += text[i]; i++ }
    else if (typeInterval) clearInterval(typeInterval)
  }, 60)
}

function closeWelcome() {
  showWelcome.value = false; showFireworks.value = false
  if (typeInterval) clearInterval(typeInterval)
  router.push('/')
}
onBeforeUnmount(() => { if (typeInterval) clearInterval(typeInterval) })
</script>

<style scoped>
.login-page {
  min-height: 100vh; display: flex; align-items: center; justify-content: center;
  background: var(--bg-primary); position: relative; overflow: hidden;
  transition: background 0.3s;
}
.login-wrapper { position: relative; z-index: 10; transition: transform 0.6s ease, opacity 0.6s ease; }
.login-wrapper.slide-up { transform: translateY(-60px); opacity: 0.85; }

.glass-card {
  position: relative; width: 400px;
  background: var(--bg-card); backdrop-filter: blur(20px);
  border: 1px solid var(--border-color); border-radius: 24px;
  padding: 40px; overflow: hidden; transition: background 0.3s;
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

.logo-area { text-align: center; margin-bottom: 32px; }
.logo-icon { font-size: 48px; margin-bottom: 8px; animation: float 3s ease-in-out infinite; }
@keyframes float { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-8px); } }
.logo-area h1 {
  font-size: 22px; font-weight: 800;
  background: linear-gradient(135deg, #8b5cf6, #ec4899, #f43f5e);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
}
.subtitle { font-size: 13px; color: var(--text-muted); margin-top: 4px; letter-spacing: 2px; }

.login-form { display: flex; flex-direction: column; gap: 16px; }
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
  font-size: 16px; font-weight: 700; letter-spacing: 4px;
  cursor: pointer; overflow: hidden; margin-top: 4px;
  background: linear-gradient(135deg, #8b5cf6, #6366f1, #ec4899, #f43f5e);
  background-size: 300% 300%; animation: gradientMove 3s ease infinite;
  color: #fff; transition: transform 0.2s, box-shadow 0.3s;
}
.neon-btn:hover { transform: translateY(-2px); box-shadow: 0 0 30px rgba(139,92,246,0.4); }
.neon-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.neon-btn::before {
  content: ''; position: absolute; top: 0; left: -100%;
  width: 60%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transform: skewX(-25deg); transition: left 0.5s;
}
.neon-btn:hover::before { left: 150%; }
@keyframes gradientMove { 0% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } 100% { background-position: 0% 50%; } }

.register-link { text-align: center; margin-top: 20px; font-size: 14px; color: var(--text-muted); }
.register-link a { color: var(--accent); text-decoration: none; font-weight: 600; }
.register-link a:hover { color: var(--accent-light); }

/* Welcome modal unchanged (dark-only) */
.welcome-overlay {
  position: fixed; inset: 0; z-index: 1100;
  display: flex; align-items: center; justify-content: center;
  background: rgba(0,0,0,0.3); animation: fadeIn 0.5s ease;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
.welcome-modal {
  position: relative; width: 460px; padding: 48px 40px 36px;
  border-radius: 24px; overflow: hidden;
  background: rgba(15, 15, 40, 0.6); backdrop-filter: blur(30px);
  border: 1px solid rgba(255,255,255,0.1);
  animation: popIn 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 0 80px rgba(139,92,246,0.2);
}
@keyframes popIn { from { transform: scale(0.5); opacity: 0; } to { transform: scale(1); opacity: 1; } }
.welcome-bg {
  position: absolute; inset: 0;
  background: linear-gradient(135deg, rgba(139,92,246,0.2), rgba(236,72,153,0.15), rgba(59,130,246,0.2), rgba(139,92,246,0.15));
  background-size: 400% 400%; animation: rainbowMove 6s ease infinite;
}
@keyframes rainbowMove { 0% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } 100% { background-position: 0% 50%; } }
.welcome-content { position: relative; z-index: 1; text-align: center; }
.welcome-emoji { font-size: 56px; margin-bottom: 8px; animation: bounce 1s ease infinite; }
@keyframes bounce { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-12px); } }
.welcome-title {
  font-size: 28px; font-weight: 800; margin-bottom: 12px;
  background: linear-gradient(135deg, #8b5cf6, #ec4899, #f43f5e, #8b5cf6);
  background-size: 200% auto;
  -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
  animation: shimmer 3s linear infinite;
}
@keyframes shimmer { 0% { background-position: 0% center; } 100% { background-position: 200% center; } }
.welcome-typing { font-size: 16px; color: rgba(255,255,255,0.9); min-height: 28px; margin-bottom: 16px; }
.cursor { animation: blink 0.8s step-end infinite; }
@keyframes blink { 50% { opacity: 0; } }
.welcome-quote { font-size: 13px; color: rgba(255,255,255,0.75); font-style: italic; padding: 12px 16px; background: rgba(255,255,255,0.06); border-radius: 10px; margin-bottom: 20px; line-height: 1.6; }
.welcome-btn { max-width: 280px; margin: 0 auto; }
</style>

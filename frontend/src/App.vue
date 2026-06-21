<template>
  <div class="app-layout">
    <nav class="navbar">
      <div class="nav-brand">
        <router-link to="/">⚡ AI 编程练习助手</router-link>
      </div>
      <div class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link to="/leaderboard">排行榜</router-link>
        <router-link v-if="auth.isLoggedIn && !auth.isAdmin" to="/questions">题库</router-link>
        <template v-if="auth.isLoggedIn">
          <router-link v-if="!auth.isAdmin" to="/wrong-answers">错题本</router-link>
          <router-link v-if="!auth.isAdmin" to="/achievements">成就</router-link>
          <router-link v-if="!auth.isAdmin" to="/stats">学习统计</router-link>
          <router-link to="/profile">{{ auth.isAdmin ? '个人中心' : (auth.user?.nickname || auth.user?.username) }}</router-link>
          <a href="#" @click.prevent="handleLogout">退出</a>
        </template>
        <template v-else>
          <router-link to="/login">登录</router-link>
          <router-link to="/register">注册</router-link>
        </template>
        <button class="theme-toggle" @click="theme.toggle" :title="theme.isDark ? '切换浅色模式' : '切换深色模式'">
          {{ theme.isDark ? '☀️' : '🌙' }}
        </button>
      </div>
    </nav>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const theme = useThemeStore()
const router = useRouter()

async function handleLogout() { await auth.logout(); router.push('/login') }
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

:root, [data-theme="dark"] {
  --bg-primary: #0d0d2b;
  --bg-card: rgba(20,20,50,0.6);
  --bg-nav: rgba(13,13,43,0.85);
  --bg-input: rgba(255,255,255,0.05);
  --bg-input-hover: rgba(255,255,255,0.08);
  --bg-table-header: rgba(255,255,255,0.03);
  --bg-table-hover: rgba(139,92,246,0.04);
  --bg-code: rgba(0,0,0,0.4);
  --border-color: rgba(139,92,246,0.15);
  --border-input: rgba(255,255,255,0.1);
  --text-primary: #e0e0f0;
  --text-secondary: rgba(255,255,255,0.6);
  --text-muted: rgba(255,255,255,0.3);
  --text-placeholder: rgba(255,255,255,0.25);
  --text-label: rgba(255,255,255,0.5);
  --accent: #8b5cf6;
  --accent-light: #a78bfa;
}
[data-theme="light"] {
  --bg-primary: #f0f2f8;
  --bg-card: rgba(255,255,255,0.85);
  --bg-nav: rgba(255,255,255,0.9);
  --bg-input: rgba(0,0,0,0.03);
  --bg-input-hover: rgba(0,0,0,0.05);
  --bg-table-header: rgba(0,0,0,0.02);
  --bg-table-hover: rgba(139,92,246,0.04);
  --bg-code: #f5f5f5;
  --border-color: rgba(139,92,246,0.15);
  --border-input: rgba(0,0,0,0.12);
  --text-primary: #1a1a3e;
  --text-secondary: rgba(0,0,0,0.55);
  --text-muted: rgba(0,0,0,0.3);
  --text-placeholder: rgba(0,0,0,0.25);
  --text-label: rgba(0,0,0,0.5);
  --accent: #7c3aed;
  --accent-light: #8b5cf6;
}

* { margin:0; padding:0; box-sizing:border-box; }
body { font-family:'Inter',sans-serif; background:var(--bg-primary); color:var(--text-primary); transition:background 0.3s,color 0.3s; }
.navbar { background:var(--bg-nav); backdrop-filter:blur(16px); border-bottom:1px solid var(--border-color); padding:0 28px; display:flex; align-items:center; justify-content:space-between; height:60px; position:sticky; top:0; z-index:100; }
.nav-brand a { font-size:18px; font-weight:800; background:linear-gradient(135deg,#8b5cf6,#ec4899); -webkit-background-clip:text; -webkit-text-fill-color:transparent; background-clip:text; text-decoration:none; }
.nav-links { display:flex; gap:24px; align-items:center; }
.nav-links a { color:var(--text-secondary); text-decoration:none; font-size:14px; font-weight:500; transition:color 0.2s; }
.nav-links a:hover, .nav-links a.router-link-active { color:var(--accent-light); }
.theme-toggle { background:var(--bg-input); border:1px solid var(--border-input); border-radius:50%; width:36px; height:36px; display:flex; align-items:center; justify-content:center; font-size:18px; cursor:pointer; transition:all 0.3s; }
.theme-toggle:hover { background:var(--bg-input-hover); transform:scale(1.1); }
.main-content { flex:1; max-width:1200px; width:100%; margin:0 auto; padding:28px 24px; }

.card { background:var(--bg-card); backdrop-filter:blur(12px); border:1px solid var(--border-color); border-radius:16px; padding:24px; transition:transform 0.2s,box-shadow 0.3s; }
.card:hover { transform:translateY(-2px); box-shadow:0 8px 32px rgba(139,92,246,0.08); }
.card + .card { margin-top:16px; }

.btn { display:inline-flex; align-items:center; justify-content:center; padding:8px 20px; border-radius:10px; font-size:14px; font-weight:600; cursor:pointer; border:1px solid transparent; transition:all 0.25s; text-decoration:none; font-family:inherit; }
.btn-primary { background:linear-gradient(135deg,#8b5cf6,#6366f1); color:#fff; border:none; }
.btn-primary:hover { transform:translateY(-2px); box-shadow:0 4px 20px rgba(139,92,246,0.3); }
.btn-success { background:linear-gradient(135deg,#059669,#10b981); color:#fff; border:none; }
.btn-success:hover { transform:translateY(-2px); box-shadow:0 4px 20px rgba(16,185,129,0.3); }
.btn-warning { background:linear-gradient(135deg,#d97706,#f59e0b); color:#fff; border:none; }
.btn-warning:hover { transform:translateY(-2px); box-shadow:0 4px 20px rgba(245,158,11,0.3); }
.btn-danger { background:linear-gradient(135deg,#dc2626,#ef4444); color:#fff; border:none; }
.btn-danger:hover { transform:translateY(-2px); box-shadow:0 4px 20px rgba(239,68,68,0.3); }
.btn-default { background:var(--bg-input); color:var(--text-secondary); border:1px solid var(--border-input); }
.btn-default:hover { background:var(--bg-input-hover); color:var(--text-primary); }
.btn-sm { padding:5px 14px; font-size:12px; }
.btn + .btn { margin-left:8px; }

.form-group { margin-bottom:16px; }
.form-group label { display:block; margin-bottom:6px; font-size:14px; color:var(--text-label); }
.form-input, .form-select { width:100%; padding:10px 14px; background:var(--bg-input); border:1px solid var(--border-input); border-radius:10px; font-size:14px; color:var(--text-primary); outline:none; transition:border-color 0.3s; font-family:inherit; }
.form-input:focus, .form-select:focus { border-color:var(--accent); box-shadow:0 0 16px rgba(139,92,246,0.1); }
.form-input::placeholder { color:var(--text-placeholder); }
.form-textarea { width:100%; padding:10px 14px; background:var(--bg-input); border:1px solid var(--border-input); border-radius:10px; font-size:14px; color:var(--text-primary); outline:none; min-height:100px; resize:vertical; font-family:monospace; }
.form-textarea:focus { border-color:var(--accent); }
.form-input:disabled { opacity:0.6; background:var(--bg-input); color:var(--text-muted); cursor:not-allowed; }

.tag { display:inline-block; padding:3px 10px; border-radius:6px; font-size:12px; font-weight:600; }
.tag-easy { background:rgba(16,185,129,0.15); color:#10b981; }
.tag-medium { background:rgba(245,158,11,0.15); color:#f59e0b; }
.tag-hard { background:rgba(239,68,68,0.15); color:#ef4444; }
.tag-pending { background:rgba(255,255,255,0.06); color:var(--text-muted); }
.tag-approved { background:rgba(16,185,129,0.15); color:#10b981; }
.tag-rejected { background:rgba(239,68,68,0.15); color:#ef4444; }

.empty-state { text-align:center; padding:60px 20px; color:var(--text-muted); }
.empty-state h3 { font-size:16px; margin-bottom:8px; }
.empty-state p { font-size:14px; }
.alert { padding:12px 16px; border-radius:10px; font-size:14px; margin-bottom:16px; }
.alert-error { background:rgba(239,68,68,0.12); color:#fca5a5; border:1px solid rgba(239,68,68,0.2); }
.alert-success { background:rgba(16,185,129,0.12); color:#6ee7b7; border:1px solid rgba(16,185,129,0.2); }
.alert-info { background:rgba(99,102,241,0.12); color:#a5b4fc; border:1px solid rgba(99,102,241,0.2); }

.data-table { width:100%; border-collapse:collapse; font-size:14px; }
.data-table th, .data-table td { padding:12px 16px; text-align:left; border-bottom:1px solid rgba(255,255,255,0.05); }
.data-table th { background:var(--bg-table-header); font-weight:600; color:var(--text-muted); }
.data-table tr:hover td { background:var(--bg-table-hover); }

.code-block { background:var(--bg-code); color:var(--text-primary); border-radius:10px; font-family:monospace; font-size:13px; line-height:1.6; overflow-x:auto; padding:16px; }
.loading { text-align:center; padding:40px; color:var(--text-muted); font-size:14px; }
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:24px; }
.page-header h1 { font-size:22px; color:var(--text-primary); font-weight:700; }

.progress-bar { height:8px; background:rgba(255,255,255,0.06); border-radius:4px; overflow:hidden; }
.progress-fill { height:100%; border-radius:4px; transition:width 0.3s; }
.fill-green { background:linear-gradient(90deg,#059669,#10b981); }
.fill-yellow { background:linear-gradient(90deg,#d97706,#f59e0b); }
.fill-red { background:linear-gradient(90deg,#dc2626,#ef4444); }
</style>

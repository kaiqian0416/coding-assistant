<template>
  <div class="home-page">
    <template v-if="auth.isAdmin">
      <div class="card">
        <h1 style="font-size:22px;color:var(--text-primary);margin-bottom:4px">管理后台</h1>
        <p style="color:var(--text-muted);margin-bottom:24px">欢迎回来，{{ auth.user?.nickname }}！</p>
        <div class="admin-dashboard">
          <router-link to="/admin/questions" class="dash-card" style="border-top:3px solid #409eff">
            <span class="dash-icon">📚</span><span class="dash-title">题目管理</span><span class="dash-desc">增删改查、AI出题、审核</span>
          </router-link>
          <router-link to="/admin/submissions" class="dash-card" style="border-top:3px solid #67c23a">
            <span class="dash-icon">📝</span><span class="dash-title">提交记录</span><span class="dash-desc">所有用户提交详情</span>
          </router-link>
          <router-link to="/admin/users" class="dash-card" style="border-top:3px solid #e6a23c">
            <span class="dash-icon">👥</span><span class="dash-title">用户管理</span><span class="dash-desc">系统用户列表</span>
          </router-link>
          <router-link to="/admin/achievements" class="dash-card" style="border-top:3px solid #8b5cf6">
            <span class="dash-icon">🏆</span><span class="dash-title">成就管理</span><span class="dash-desc">成就系统配置与查看</span>
          </router-link>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="welcome-card card">
        <h1 v-if="auth.isLoggedIn">欢迎回来，{{ auth.user?.nickname || auth.user?.username }}！</h1>
        <h1 v-else>欢迎来到 AI 编程练习助手</h1>
        <p class="welcome-quote-text">{{ welcomeQuote }}</p>
        <div v-if="!auth.isLoggedIn" class="welcome-actions">
          <router-link to="/register" class="btn btn-primary">立即注册</router-link>
          <router-link to="/login" class="btn btn-default">登录</router-link>
        </div>
      </div>

      <div v-if="auth.isLoggedIn" class="card">
        <h2 class="section-title">学习概览</h2>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else class="stats-grid">
          <div class="stat-item"><span class="stat-value">{{ overview.totalQuestions || 0 }}</span><span class="stat-label">总做题数</span></div>
          <div class="stat-item"><span class="stat-value" style="color:#10b981">{{ overview.correctCount || 0 }}</span><span class="stat-label">通过数</span></div>
          <div class="stat-item"><span class="stat-value" style="color:#ef4444">{{ overview.wrongCount || 0 }}</span><span class="stat-label">错误数</span></div>
          <div class="stat-item"><span class="stat-value" style="color:#8b5cf6">{{ (overview.overallAccuracy || 0) + '%' }}</span><span class="stat-label">正确率</span></div>
        </div>
      </div>

      <div v-if="auth.isLoggedIn" class="card-row">
        <div class="side-card">
          <div class="side-card-inner">
            <span class="side-card-icon">{{ checkinData.checkedIn ? '✅' : '📅' }}</span>
            <div class="side-card-body">
              <div class="side-card-title">{{ checkinData.checkedIn ? '今日已打卡' : '今日未打卡' }}</div>
              <div class="side-card-sub">今日提交 {{ checkinData.todayCount || 0 }} 次</div>
            </div>
          </div>
        </div>
        <router-link to="/achievements" class="side-card side-link">
          <div class="side-card-inner">
            <span class="side-card-icon">🏆</span>
            <div class="side-card-body">
              <div class="side-card-title">成就系统</div>
              <div class="side-card-sub">{{ earnedCount }} / {{ achievements.length }} 已获得</div>
            </div>
            <span class="side-card-arrow">→</span>
          </div>
        </router-link>
      </div>

      <div v-if="auth.isLoggedIn" class="quick-links">
        <router-link to="/wrong-answers" class="quick-link">📝 错题本</router-link>
        <router-link to="/leaderboard" class="quick-link">🏆 排行榜</router-link>
        <router-link to="/stats" class="quick-link">📊 学习统计</router-link>
      </div>

      <div v-if="auth.isLoggedIn" class="card">
        <h2 class="section-title">推荐题目</h2>
        <div v-if="loadingRec" class="loading">加载推荐中...</div>
        <div v-else-if="recommended.length === 0" class="empty-state"><h3>暂无推荐</h3><p>先去题库看看</p></div>
        <div v-else class="question-list">
          <div v-for="q in recommended" :key="q.id" class="question-item">
            <div class="q-info">
              <router-link :to="`/practice/${q.id}`" class="q-title">{{ q.title }}</router-link>
              <div class="q-meta">
                <span :class="'tag tag-' + q.difficulty">{{ {easy:'简单',medium:'中等',hard:'困难'}[q.difficulty] || q.difficulty }}</span>
                <span class="q-knowledge">{{ q.knowledgePoint }}</span>
              </div>
            </div>
            <router-link :to="`/practice/${q.id}`" class="btn btn-primary btn-sm">去练习</router-link>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { questionApi, statsApi, checkinApi, achievementApi } from '@/api'

const auth = useAuthStore()
const loading = ref(false); const loadingRec = ref(false)
const overview = ref<any>({}); const recommended = ref<any[]>([])
const checkinData = ref<any>({}); const achievements = ref<any[]>([])
const earnedCount = computed(() => achievements.value.filter((a: any) => a.earned).length)
const welcomeQuote = ref("今天也要加油刷题哦 💪")

const studentQuotes = [
  "代码是写给人看的，顺便能在机器上运行。 — Harold Abelson",
  "在编程的世界里，没有完美的代码，只有不断改进的代码。",
  "每一次提交都让你离大神更近一步。",
  "不要怕犯错，每个错误都是最好的老师。",
  "编程不是关于你知道什么，而是关于你能学到什么。",
  "Talk is cheap. Show me the code. — Linus Torvalds",
]

onMounted(async () => {
  welcomeQuote.value = studentQuotes[Math.floor(Math.random() * studentQuotes.length)]
  if (auth.isLoggedIn && !auth.isAdmin) {
    await Promise.all([loadOverview(), loadRecommended(), loadCheckin(), loadAchievements()])
  }
})

async function loadOverview() {
  loading.value = true
  try { const r = await statsApi.getOverview(); if (r.data.code === 200) overview.value = r.data.data } catch (e) {}
  finally { loading.value = false }
}
async function loadRecommended() {
  loadingRec.value = true
  try { const r = await questionApi.getRecommended(auth.userId!); if (r.data.code === 200) recommended.value = r.data.data } catch (e) {}
  finally { loadingRec.value = false }
}
async function loadCheckin() {
  try { const r = await checkinApi.get(); if (r.data.code === 200) checkinData.value = r.data.data } catch (e) {}
}
async function loadAchievements() {
  try { const r = await achievementApi.list(); if (r.data.code === 200) achievements.value = r.data.data || [] } catch (e) {}
}
</script>

<style scoped>
.welcome-card { text-align: center; padding: 48px 24px; }
.welcome-quote-text {
  font-size: 15px;
  color: var(--text-muted);
  margin-top: 8px;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;
  line-height: 1.6;
}
.welcome-card h1 { font-size: 26px; color: var(--text-primary); margin-bottom: 12px; }
.welcome-card p { color: var(--text-muted); font-size: 15px; margin-bottom: 24px; }
.welcome-actions { display: flex; gap: 12px; justify-content: center; }
.section-title { font-size: 18px; color: var(--text-primary); margin-bottom: 16px; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.stat-item { text-align: center; padding: 16px; }
.stat-value { display: block; font-size: 32px; font-weight: 700; color: var(--text-primary); margin-bottom: 4px; }
.stat-label { font-size: 14px; color: var(--text-muted); }

.card-row { display: flex; gap: 16px; margin-top: 16px; }
.side-card {
  flex: 1; display: flex; align-items: center;
  padding: 20px 24px;
  background: var(--bg-card);
  backdrop-filter: blur(12px);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  transition: transform 0.2s, box-shadow 0.3s;
  box-sizing: border-box;
  min-height: 80px;
}
.side-card:hover { transform: translateY(-2px); box-shadow: 0 8px 32px rgba(139,92,246,0.08); }
.side-link { text-decoration: none; cursor: pointer; }
.side-link:hover .side-card-title { color: var(--accent-light); }
.side-card-inner { display: flex; align-items: center; gap: 16px; width: 100%; }
.side-card-icon { font-size: 32px; flex-shrink: 0; line-height: 1; }
.side-card-body { flex: 1; min-width: 0; }
.side-card-title { font-size: 15px; font-weight: 600; color: var(--text-primary); line-height: 1.4; }
.side-card-sub { font-size: 13px; color: var(--text-muted); margin-top: 2px; line-height: 1.4; }
.side-card-arrow { font-size: 18px; color: var(--text-muted); flex-shrink: 0; line-height: 1; }

.quick-links { display: flex; gap: 12px; margin-top: 16px; flex-wrap: wrap; }
.quick-link {
  padding: 10px 20px; border-radius: 12px; font-size: 14px; font-weight: 500;
  text-decoration: none; color: var(--text-primary);
  background: var(--bg-card); border: 1px solid var(--border-color);
  transition: all 0.2s;
}
.quick-link:hover { background: var(--accent); color: #fff; border-color: var(--accent); transform: translateY(-2px); }

.question-list { display: flex; flex-direction: column; gap: 12px; }
.question-item { display: flex; justify-content: space-between; align-items: center; padding: 16px; border: 1px solid rgba(255,255,255,0.04); border-radius: 10px; }
.q-title { font-size: 15px; font-weight: 500; color: var(--text-primary); text-decoration: none; }
.q-title:hover { color: var(--accent-light); }
.q-meta { margin-top: 6px; display: flex; gap: 8px; align-items: center; }
.q-knowledge { font-size: 12px; color: var(--text-muted); }

.admin-dashboard { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.dash-card {
  background: var(--bg-card); border: 1px solid var(--border-color); border-radius: 12px;
  padding: 28px 20px; display: flex; flex-direction: column; align-items: center;
  text-decoration: none; transition: all 0.2s;
}
.dash-card:hover { box-shadow: 0 6px 24px rgba(0,0,0,0.15); transform: translateY(-2px); }
.dash-icon { font-size: 32px; margin-bottom: 10px; }
.dash-title { font-size: 15px; font-weight: 600; color: var(--text-primary); margin-bottom: 4px; }
.dash-desc { font-size: 12px; color: var(--text-muted); text-align: center; }
</style>

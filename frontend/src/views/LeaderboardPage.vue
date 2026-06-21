<template>
  <div class="leaderboard-page">
    <div class="page-header">
      <h1>🏆 刷题排行榜</h1>
      <p class="header-subtitle">按通过题目数排名，一起卷起来！</p>
    </div>

    <div class="card">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="ranking.length === 0" class="empty-state"><h3>暂无数据</h3><p>还没有人完成题目</p></div>
      <div v-else class="rank-list">
        <div v-for="(item, i) in ranking" :key="item.id" class="rank-item" :class="{ 'is-me': item.id === currentUserId }">
          <div class="rank-pos">
            <span v-if="i === 0" class="medal">🥇</span>
            <span v-else-if="i === 1" class="medal">🥈</span>
            <span v-else-if="i === 2" class="medal">🥉</span>
            <span v-else class="rank-num">{{ i + 1 }}</span>
          </div>
          <div class="rank-avatar">{{ item.nickname?.[0] || item.username?.[0] || '?' }}</div>
          <div class="rank-info">
            <span class="rank-name">{{ item.nickname || item.username }}</span>
            <span class="rank-user">@{{ item.username }}</span>
          </div>
          <div class="rank-stats">
            <div class="stat-badge accepted">
              <span class="stat-num">{{ item.accepted_count || 0 }}</span>
              <span class="stat-lbl">通过</span>
            </div>
            <div class="stat-badge total">
              <span class="stat-num">{{ item.total_count || 0 }}</span>
              <span class="stat-lbl">总提交</span>
            </div>
            <div class="stat-badge accuracy">
              <span class="stat-num">{{ calcAccuracy(item) }}%</span>
              <span class="stat-lbl">正确率</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/api'

const auth = useAuthStore()
const currentUserId = auth.user?.id
const loading = ref(true)
const ranking = ref<any[]>([])

onMounted(async () => {
  try {
    const res = await api.get('/leaderboard')
    if (res.data.code === 200) ranking.value = res.data.data || []
  } catch (e) { /* ignore */ }
  finally { loading.value = false }
})

function calcAccuracy(item: any) {
  const total = item.total_count || 0
  if (total === 0) return 0
  return Math.round((item.accepted_count || 0) / total * 100)
}
</script>

<style scoped>
.header-subtitle { font-size: 14px; color: var(--text-muted); margin-top: 4px; }

.rank-list { display: flex; flex-direction: column; gap: 8px; }
.rank-item {
  display: flex; align-items: center; gap: 14px;
  padding: 14px 18px; border-radius: 14px;
  background: rgba(255,255,255,0.02);
  border: 1px solid rgba(255,255,255,0.04);
  transition: all 0.2s;
}
.rank-item:hover { background: rgba(139,92,246,0.04); }
.rank-item.is-me {
  background: rgba(139,92,246,0.08);
  border-color: rgba(139,92,246,0.2);
}

.rank-pos { width: 36px; text-align: center; flex-shrink: 0; }
.medal { font-size: 28px; }
.rank-num {
  font-size: 18px; font-weight: 700; color: var(--text-muted);
  width: 32px; height: 32px; display: flex; align-items: center; justify-content: center;
}

.rank-avatar {
  width: 40px; height: 40px; border-radius: 50%; flex-shrink: 0;
  background: linear-gradient(135deg, #8b5cf6, #ec4899);
  display: flex; align-items: center; justify-content: center;
  font-size: 18px; font-weight: 700; color: #fff;
}

.rank-info { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.rank-name { font-size: 15px; font-weight: 600; color: var(--text-primary); }
.rank-user { font-size: 12px; color: var(--text-muted); }

.rank-stats { display: flex; gap: 12px; }
.stat-badge {
  text-align: center; padding: 4px 12px; border-radius: 8px;
  min-width: 56px;
}
.stat-badge.accepted { background: rgba(16,185,129,0.1); }
.stat-badge.total { background: rgba(139,92,246,0.1); }
.stat-badge.accuracy { background: rgba(245,158,11,0.1); }
.stat-num { display: block; font-size: 16px; font-weight: 700; }
.stat-badge.accepted .stat-num { color: #10b981; }
.stat-badge.total .stat-num { color: #8b5cf6; }
.stat-badge.accuracy .stat-num { color: #f59e0b; }
.stat-lbl { font-size: 11px; color: var(--text-muted); }
</style>

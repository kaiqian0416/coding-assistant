<template>
  <div class="page">
    <div class="page-header"><h1>🏆 成就系统</h1></div>
    <div class="card">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else class="ach-grid">
        <div v-for="a in list" :key="a.id" class="ach-item" :class="{ earned: a.earned }">
          <div class="ach-icon">{{ a.icon }}</div>
          <div class="ach-name">{{ a.name }}</div>
          <div class="ach-desc">{{ a.description }}</div>
          <div class="ach-progress">
            <div class="progress-track">
              <div class="progress-fill" :style="{ width: Math.min(100, a.progress || 0) + '%' }"></div>
            </div>
            <span class="progress-text">{{ a.earned ? '✅' : (a.progress || 0) + '%' }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { achievementApi } from '@/api'

const loading = ref(true)
const list = ref<any[]>([])

onMounted(async () => {
  try { const r = await achievementApi.list(); if (r.data.code === 200) list.value = r.data.data || [] } catch (e) {}
  finally { loading.value = false }
})
</script>

<style scoped>
.ach-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 16px; }
.ach-item {
  text-align: center; padding: 24px 16px 20px; border-radius: 14px;
  border: 1px solid rgba(255,255,255,0.04); transition: all 0.3s;
}
.ach-item.earned { background: rgba(139,92,246,0.06); border-color: rgba(139,92,246,0.15); }
.ach-item:not(.earned) { opacity: 0.55; filter: grayscale(0.4); }
.ach-icon { font-size: 40px; margin-bottom: 10px; }
.ach-name { font-size: 15px; font-weight: 600; color: var(--text-primary); margin-bottom: 4px; }
.ach-desc { font-size: 12px; color: var(--text-muted); margin-bottom: 12px; }
.ach-progress { display: flex; align-items: center; gap: 8px; }
.progress-track { flex: 1; height: 6px; background: rgba(255,255,255,0.08); border-radius: 3px; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, #8b5cf6, #ec4899); border-radius: 3px; transition: width 0.5s; }
.progress-text { font-size: 12px; color: var(--text-muted); min-width: 32px; }
.ach-item.earned .progress-fill { background: linear-gradient(90deg, #10b981, #34d399); }
</style>

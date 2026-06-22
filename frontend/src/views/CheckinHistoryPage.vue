<template>
  <div class="page">
    <div class="page-header"><h1>📅 打卡记录</h1></div>

    <div class="card">
      <div v-if="loading" class="loading">加载中...</div>
      <template v-else>
        <!-- 概览 -->
        <div class="checkin-overview">
          <div class="overview-item">
            <span class="ov-icon">{{ data.checkedIn ? '✅' : '📅' }}</span>
            <span class="ov-label">{{ data.checkedIn ? '今日已打卡' : '今日未打卡' }}</span>
          </div>
          <div class="overview-item">
            <span class="ov-num">{{ data.consecutiveDays || 0 }}</span>
            <span class="ov-label">连续打卡天数</span>
          </div>
          <div class="overview-item">
            <span class="ov-num">{{ data.totalCheckinDays || 0 }}</span>
            <span class="ov-label">累计打卡天数</span>
          </div>
          <div class="overview-item">
            <span class="ov-num">{{ data.todayCount || 0 }}</span>
            <span class="ov-label">今日提交次数</span>
          </div>
        </div>

        <!-- 日历视图 -->
        <h3 style="margin: 24px 0 16px;font-size:16px;color:var(--text-primary)">数据日历</h3>
        <div class="calendar-grid">
          <div v-for="d in calendarDays" :key="d.label" class="cal-day" :class="{ checked: d.checked, 'is-today': d.isToday }">
            <span class="cal-date">{{ d.day }}</span>
            <span class="cal-dot" v-if="d.checked">●</span>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { checkinApi } from '@/api'

const loading = ref(true)
const data = ref<any>({})

// 生成最近30天的日历视图
const calendarDays = computed(() => {
  const days = []
  const checkDates = new Set(data.value.dates || [])
  const now = new Date()
  for (let i = 29; i >= 0; i--) {
    const d = new Date(now)
    d.setDate(d.getDate() - i)
    const key = d.toISOString().substring(0, 10)
    const isToday = i === 0
    days.push({
      label: `${d.getMonth() + 1}/${d.getDate()}`,
      day: d.getDate(),
      checked: checkDates.has(key),
      isToday
    })
  }
  return days
})

onMounted(async () => {
  try { const r = await checkinApi.get(); if (r.data.code === 200) data.value = r.data.data }
  catch (e) { /* ignore */ }
  finally { loading.value = false }
})
</script>

<style scoped>
.checkin-overview { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 8px; }
.overview-item { text-align: center; padding: 16px; background: var(--bg-input); border-radius: 12px; }
.ov-icon { font-size: 28px; display: block; margin-bottom: 4px; }
.ov-num { font-size: 28px; font-weight: 700; color: var(--accent); display: block; }
.ov-label { font-size: 13px; color: var(--text-muted); margin-top: 4px; display: block; }

.calendar-grid { display: grid; grid-template-columns: repeat(7, 1fr); gap: 6px; }
.cal-day {
  text-align: center; padding: 10px 4px; border-radius: 10px;
  background: var(--bg-input); border: 1px solid transparent;
  transition: all 0.2s;
}
.cal-day.checked { background: rgba(139,92,246,0.12); border-color: rgba(139,92,246,0.3); }
.cal-day.is-today { border-color: var(--accent); }
.cal-date { display: block; font-size: 14px; color: var(--text-primary); font-weight: 500; }
.cal-dot { display: block; font-size: 12px; color: var(--accent); margin-top: 2px; }
</style>

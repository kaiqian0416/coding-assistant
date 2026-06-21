<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>全部提交记录</h1>
    </div>

    <div class="card">
      <div class="toolbar">
        <div class="filter-bar">
          <input v-model="filters.username" class="form-input" style="width:150px" placeholder="按用户名筛选" @input="applyFilter" />
          <select v-model="filters.result" class="form-select" style="width:130px" @change="applyFilter">
            <option value="">全部状态</option>
            <option value="accepted">通过</option>
            <option value="wrong_answer">答案错误</option>
            <option value="compile_error">编译错误</option>
            <option value="runtime_error">运行错误</option>
          </select>
        </div>
        <span class="filter-count">共 {{ filtered.length }} 条</span>
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="filtered.length === 0" class="empty-state"><h3>暂无提交记录</h3></div>
      <div v-else class="submission-list">
        <div v-for="sub in filtered" :key="sub.id" class="sub-item" @click="toggleDetail(sub.id)">
          <div class="sub-header">
            <div class="sub-left">
              <span class="sub-user">{{ sub.username }}</span>
              <span class="sub-sep">|</span>
              <span class="sub-title">{{ questionTitleMap[sub.question_id] || '题目 #' + sub.question_id }}</span>
            </div>
            <div class="sub-meta">
              <span :class="'tag tag-' + resultTag(sub)">{{ resultText(sub) }}</span>
              <span class="sub-score" v-if="sub.score != null">{{ sub.score }}分</span>
              <span class="sub-time">{{ formatTime(sub.created_at) }}</span>
              <span class="expand-icon">{{ expandedId === sub.id ? '▼' : '▶' }}</span>
            </div>
          </div>
          <div v-if="expandedId === sub.id" class="sub-detail">
            <div class="detail-section"><strong>代码：</strong><pre class="code-block">{{ sub.code }}</pre></div>
            <div v-if="sub.error_info" class="detail-section"><strong>错误信息：</strong><pre class="code-block error-block">{{ sub.error_info }}</pre></div>
            <div v-if="sub.ai_diagnosis" class="detail-section"><strong>AI 诊断：</strong><pre class="diagnosis-block">{{ sub.ai_diagnosis }}</pre></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { adminApi, questionApi } from '@/api'

const loading = ref(true)
const allSubmissions = ref<any[]>([])
const expandedId = ref<number | null>(null)
const questionTitleMap = ref<Record<number, string>>({})
const filters = reactive({ username: '', result: '' })

const filtered = computed(() => {
  return allSubmissions.value.filter(s => {
    if (filters.username && !(s.username || '').includes(filters.username)) return false
    if (filters.result && s.result !== filters.result) return false
    return true
  })
})

onMounted(async () => {
  try {
    const [subRes, qRes] = await Promise.all([
      adminApi.getAllSubmissions(),
      questionApi.list({}).catch(() => ({ data: { data: [] } }))
    ])
    if (subRes.data.code === 200) allSubmissions.value = subRes.data.data || []
    if (qRes.data?.data) {
      for (const q of qRes.data.data) questionTitleMap.value[q.id] = q.title
    }
  } catch (e) { /* ignore */ }
  finally { loading.value = false }
})

function applyFilter() {}
function toggleDetail(id: number) { expandedId.value = expandedId.value === id ? null : id }
function resultTag(sub: any) {
  const r = sub.result
  if (r === 'accepted') return 'approved'
  if (['wrong_answer','compile_error','runtime_error'].includes(r)) return 'rejected'
  return 'pending'
}
function resultText(sub: any) {
  const map: Record<string, string> = { accepted:'通过', wrong_answer:'答案错误', compile_error:'编译错误', runtime_error:'运行错误', judging:'判题中', pending:'待判' }
  return map[sub.result] || sub.result
}
function formatTime(t: string) { return t ? t.substring(0, 16).replace('T', ' ') : '' }
</script>

<style scoped>
.admin-nav { display: flex; gap: 8px; flex-wrap: wrap; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.filter-bar { display: flex; gap: 12px; }
.filter-count { font-size: 13px; color: #909399; }
.submission-list { display: flex; flex-direction: column; gap: 8px; }
.sub-item {
  border: 1px solid #ebeef5; border-radius: 6px; overflow: hidden; cursor: pointer; transition: box-shadow 0.2s;
}
.sub-item:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.sub-header {
  display: flex; justify-content: space-between; align-items: center; padding: 12px 16px;
}
.sub-left { display: flex; align-items: center; gap: 8px; }
.sub-user { font-size: 13px; font-weight: 600; color: #409eff; }
.sub-sep { color: #dcdfe6; }
.sub-title { font-size: 14px; color: #303133; }
.sub-meta { display: flex; align-items: center; gap: 10px; font-size: 13px; }
.sub-score { color: #606266; font-weight: 600; }
.sub-time { color: #909399; font-size: 12px; }
.expand-icon { color: #909399; font-size: 12px; }
.sub-detail { padding: 0 16px 16px; border-top: 1px solid #ebeef5; }
.detail-section { margin-top: 12px; }
.detail-section strong { font-size: 13px; color: #606266; display: block; margin-bottom: 6px; }
.code-block {
  background: #1e1e1e; color: #d4d4d4; border-radius: 6px; padding: 12px;
  font-family: 'Consolas', monospace; font-size: 13px; line-height: 1.5; overflow-x: auto; max-height: 200px; overflow-y: auto;
}
.error-block { background: #2d1b1b; color: #f8d7da; }
.diagnosis-block {
  white-space: pre-wrap; background: #f0f5ff; color: #303133; padding: 12px 16px;
  border-radius: 6px; font-size: 13px; line-height: 1.6; border: 1px solid #d9ecff; font-family: inherit;
}
</style>

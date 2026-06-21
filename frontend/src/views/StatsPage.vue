<template>
  <div class="stats-page">
    <div class="page-header"><h1>学习统计</h1></div>

    <div v-if="loading" class="loading">加载统计信息...</div>
    <template v-else>
      <!-- 合并后的学习概览 -->
      <div class="card">
        <h2 class="section-title">
          学习概览
          <span class="section-subtitle">共 {{ overview.totalQuestions || 0 }} 题，{{ submissions.length }} 次提交</span>
        </h2>
        <div class="stats-grid">
          <div class="stat-item">
            <span class="stat-value" style="color:#409eff">{{ overview.totalQuestions || 0 }}</span>
            <span class="stat-label">总做题数</span>
          </div>
          <div class="stat-item">
            <span class="stat-value" style="color:#67c23a">{{ overview.correctCount || 0 }}</span>
            <span class="stat-label">通过</span>
          </div>
          <div class="stat-item">
            <span class="stat-value" style="color:#e6a23c">{{ subStats.wrongAnswer || 0 }}</span>
            <span class="stat-label">答案错误</span>
          </div>
          <div class="stat-item">
            <span class="stat-value" style="color:#f56c6c">{{ (subStats.compileError || 0) + (subStats.runtimeError || 0) }}</span>
            <span class="stat-label">编译/运行错误</span>
          </div>
        </div>
        <div class="accuracy-bar-wrapper">
          <div class="accuracy-label">
            <span>正确率</span>
            <span :style="{ color: accuracyColor(overview.overallAccuracy || 0), fontWeight: 700 }">
              {{ (overview.overallAccuracy || 0) + '%' }}
            </span>
          </div>
          <div class="accuracy-track">
            <div class="accuracy-fill" :style="{ width: (overview.overallAccuracy || 0) + '%', background: accuracyColor(overview.overallAccuracy || 0) }"></div>
          </div>
        </div>
      </div>

      <!-- 知识点掌握度 -->
      <div class="card">
        <h2 class="section-title">各知识点掌握度</h2>
        <div v-if="!overview.knowledgePoints || overview.knowledgePoints.length === 0" class="empty-state">
          <h3>暂无数据</h3>
          <p>开始做题后这里会显示各知识点的掌握情况</p>
        </div>
        <div v-else class="knowledge-list">
          <div v-for="kp in overview.knowledgePoints" :key="kp.knowledgePoint" class="knowledge-item">
            <div class="kp-header">
              <span class="kp-name">{{ kp.knowledgePoint }}</span>
              <span class="kp-accuracy">{{ kp.accuracy }}%</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: kp.accuracy + '%' }" :class="accuracyClass(kp.accuracy)"></div>
            </div>
            <div class="kp-detail">
              共 {{ kp.totalCount }} 题，正确 {{ kp.correctCount }} 题，错误 {{ kp.wrongCount }} 题
            </div>
          </div>
        </div>
      </div>

      <!-- 需加强的知识点 -->
      <div class="card">
        <h2 class="section-title">需加强的知识点</h2>
        <div v-if="!overview.weakPoints || overview.weakPoints.length === 0" class="empty-state">
          <h3>表现不错！</h3>
          <p>目前没有需要特别加强的知识点</p>
        </div>
        <div v-else>
          <div v-for="(wp, i) in overview.weakPoints" :key="i" class="weak-item">
            <span class="weak-rank">{{ i + 1 }}</span>
            <span class="weak-name">{{ wp.knowledgePoint }}</span>
            <span class="tag tag-hard">正确率 {{ wp.accuracy }}%</span>
          </div>
        </div>
      </div>

      <!-- 提交历史（可展开查看详情） -->
      <div class="card">
        <h2 class="section-title">提交历史</h2>
        <div v-if="loadingSub" class="loading">加载中...</div>
        <div v-else-if="submissions.length === 0" class="empty-state">
          <h3>暂无提交记录</h3>
          <p>开始做题后你的提交记录会显示在这里</p>
        </div>
        <div v-else class="submission-list">
          <div v-for="sub in submissions" :key="sub.id" class="sub-item" @click="toggleDetail(sub.id)">
            <div class="sub-header">
              <span class="sub-title">{{ questionTitleMap[sub.questionId] || '题目 #' + sub.questionId }}</span>
              <div class="sub-meta">
                <span :class="'tag tag-' + resultTag(sub)">{{ resultText(sub) }}</span>
                <span class="sub-score" v-if="sub.score != null">{{ sub.score }}分</span>
                <span class="sub-time">{{ formatTime(sub.createdAt) }}</span>
                <span class="expand-icon">{{ expandedId === sub.id ? '▼' : '▶' }}</span>
              </div>
            </div>
            <div v-if="expandedId === sub.id" class="sub-detail">
              <div class="detail-section">
                <strong>提交代码：</strong>
                <pre class="code-block">{{ sub.code }}</pre>
              </div>
              <div v-if="sub.errorInfo" class="detail-section">
                <strong>错误信息：</strong>
                <pre class="code-block error-block">{{ sub.errorInfo }}</pre>
              </div>
              <div v-if="sub.aiDiagnosis" class="detail-section">
                <strong>AI 诊断：</strong>
                <pre class="diagnosis-block">{{ sub.aiDiagnosis }}</pre>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { statsApi, submissionApi, questionApi } from '@/api'

const loading = ref(true)
const loadingSub = ref(true)
const overview = ref<any>({})
const subStats = ref<any>({})
const submissions = ref<any[]>([])
const expandedId = ref<number | null>(null)
const questionTitleMap = ref<Record<number, string>>({})

onMounted(async () => {
  await Promise.all([loadOverview(), loadSubmissions()])
})

async function loadOverview() {
  try {
    const [statsRes, overviewRes] = await Promise.all([
      submissionApi.getStats(),
      statsApi.getOverview()
    ])
    if (overviewRes.data.code === 200) overview.value = overviewRes.data.data
    if (statsRes.data.code === 200) subStats.value = statsRes.data.data
  } catch (e) { /* ignore */ }
  finally { loading.value = false }
}

async function loadSubmissions() {
  try {
    const [subRes, qRes] = await Promise.all([
      submissionApi.getMySubmissions(),
      questionApi.list({}).catch(() => ({ data: { data: [] } }))
    ])
    if (qRes.data?.data) {
      for (const q of qRes.data.data) questionTitleMap.value[q.id] = q.title
    }
    if (subRes.data.code === 200) submissions.value = (subRes.data.data || []).slice(0, 20)
  } catch (e) { /* ignore */ }
  finally { loadingSub.value = false }
}

function toggleDetail(id: number) {
  expandedId.value = expandedId.value === id ? null : id
}

function resultTag(sub: any) {
  const r = sub.result
  if (r === 'accepted') return 'approved'
  if (r === 'wrong_answer' || r === 'compile_error' || r === 'runtime_error') return 'rejected'
  return 'pending'
}

function resultText(sub: any) {
  const map: Record<string, string> = {
    accepted: '通过', wrong_answer: '答案错误', compile_error: '编译错误',
    runtime_error: '运行错误', judging: '判题中', pending: '待判'
  }
  return map[sub.result] || sub.result
}

function formatTime(t: string) {
  if (!t) return ''
  return t.substring(0, 16).replace('T', ' ')
}

function accuracyColor(acc: number) {
  if (acc >= 70) return '#67c23a'
  if (acc >= 40) return '#e6a23c'
  return '#f56c6c'
}

function accuracyClass(acc: number) {
  if (acc >= 70) return 'fill-green'
  if (acc >= 40) return 'fill-yellow'
  return 'fill-red'
}
</script>

<style scoped>
.section-title {
  font-size: 18px; color: #303133; margin-bottom: 16px;
  display: flex; align-items: center; justify-content: space-between;
}
.section-subtitle { font-size: 13px; color: #909399; font-weight: 400; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.stat-item { text-align: center; padding: 16px; }
.stat-value { display: block; font-size: 32px; font-weight: 700; margin-bottom: 4px; }
.stat-label { font-size: 14px; color: #909399; }

.accuracy-bar-wrapper { padding: 0 16px 16px; }
.accuracy-label { display: flex; justify-content: space-between; font-size: 14px; color: #606266; margin-bottom: 8px; }
.accuracy-track { height: 10px; background: #ebeef5; border-radius: 5px; overflow: hidden; }
.accuracy-fill { height: 100%; border-radius: 5px; transition: width 0.5s ease; }

.knowledge-list { display: flex; flex-direction: column; gap: 16px; }
.kp-header { display: flex; justify-content: space-between; margin-bottom: 6px; }
.kp-name { font-size: 14px; font-weight: 500; color: #303133; }
.kp-accuracy { font-size: 14px; color: #606266; font-weight: 600; }
.progress-bar { height: 8px; background: #ebeef5; border-radius: 4px; overflow: hidden; }
.progress-fill { height: 100%; border-radius: 4px; transition: width 0.3s; }
.fill-green { background: #67c23a; } .fill-yellow { background: #e6a23c; } .fill-red { background: #f56c6c; }
.kp-detail { font-size: 12px; color: #909399; margin-top: 4px; }

.weak-item { display: flex; align-items: center; gap: 12px; padding: 12px 0; border-bottom: 1px solid #ebeef5; }
.weak-item:last-child { border-bottom: none; }
.weak-rank { width: 24px; height: 24px; border-radius: 50%; background: #f56c6c; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 600; }
.weak-name { flex: 1; font-size: 14px; color: #303133; }
.card + .card { margin-top: 16px; }

.submission-list { display: flex; flex-direction: column; gap: 8px; }
.sub-item {
  border: 1px solid #ebeef5; border-radius: 6px; overflow: hidden;
  cursor: pointer; transition: box-shadow 0.2s;
}
.sub-item:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.sub-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 16px;
}
.sub-title { font-size: 14px; font-weight: 500; color: #303133; }
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

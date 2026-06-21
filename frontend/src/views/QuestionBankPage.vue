<template>
  <div class="question-bank">
    <div class="page-header">
      <h1>题库</h1>
      <div class="filter-bar">
        <select v-model="filters.difficulty" class="form-select" style="width:120px" @change="loadQuestions">
          <option value="">全部难度</option>
          <option value="easy">简单</option>
          <option value="medium">中等</option>
          <option value="hard">困难</option>
        </select>
        <select v-model="filters.knowledgePoint" class="form-select" style="width:140px" @change="loadQuestions">
          <option value="">全部知识点</option>
          <option v-for="kp in knowledgePoints" :key="kp" :value="kp">{{ kp }}</option>
        </select>
      </div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="questions.length === 0" class="empty-state">
      <h3>暂无题目</h3>
      <p>请等待管理添加新的题目</p>
    </div>
    <div v-else class="question-grid">
      <div v-for="q in questions" :key="q.id" class="q-card">
        <div class="q-card-header">
          <router-link :to="`/practice/${q.id}`" class="q-card-title">{{ q.title }}</router-link>
          <div class="q-card-tags">
            <span :class="'tag tag-' + q.difficulty">
              {{ {easy:'简单', medium:'中等', hard:'困难'}[q.difficulty] || q.difficulty }}
            </span>
            <span class="tag tag-pending">{{ q.knowledgePoint }}</span>
          </div>
        </div>
        <p class="q-card-desc">{{ truncate(q.description, 150) }}</p>
        <router-link :to="`/practice/${q.id}`" class="btn btn-primary btn-sm q-card-btn">开始练习</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { questionApi } from '@/api'

const loading = ref(false)
const questions = ref<any[]>([])
const filters = reactive({ difficulty: '', knowledgePoint: '' })
const knowledgePoints = ['基础语法', '条件判断', '循环结构', '字符串处理', '数组操作', '递归与递推', '排序算法', '查找算法', '动态规划', '图算法']

onMounted(() => loadQuestions())

async function loadQuestions() {
  loading.value = true
  try {
    const params: any = {}
    if (filters.difficulty) params.difficulty = filters.difficulty
    if (filters.knowledgePoint) params.knowledgePoint = filters.knowledgePoint
    const res = await questionApi.list(params)
    if (res.data.code === 200) questions.value = res.data.data
  } catch (e) { /* ignore */ }
  finally { loading.value = false }
}

function truncate(text: string, len: number) {
  if (!text) return ''
  return text.length > len ? text.substring(0, len) + '...' : text
}
</script>

<style scoped>
.filter-bar { display: flex; gap: 12px; }

/* ===== 等高等宽网格 ===== */
.question-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

/* 每张卡片统一尺寸 */
.q-card {
  display: flex;
  flex-direction: column;
  background: var(--bg-card);
  backdrop-filter: blur(12px);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 20px;
  transition: transform 0.2s, box-shadow 0.3s;
  min-height: 180px;
}
.q-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(139,92,246,0.08);
}

.q-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 8px;
}

.q-card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  text-decoration: none;
  line-height: 1.4;
  flex-shrink: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.q-card-title:hover { color: var(--accent-light); }

.q-card-tags {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

.q-card-desc {
  font-size: 14px;
  color: var(--text-muted);
  line-height: 1.6;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 0;
}

.q-card-btn {
  margin-top: 16px;
  align-self: flex-start;
}

/* 响应式：窄屏降为2列 */
@media (max-width: 900px) {
  .question-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 600px) {
  .question-grid { grid-template-columns: 1fr; }
}
</style>

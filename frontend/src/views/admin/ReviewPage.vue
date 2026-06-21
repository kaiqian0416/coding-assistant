<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>待审核题目</h1>
      <div class="admin-nav">
        <router-link to="/admin/questions" class="btn btn-default btn-sm">题目管理</router-link>
        <router-link to="/admin/generate" class="btn btn-success btn-sm">AI 出题</router-link>
        <router-link to="/admin/review" class="btn btn-warning btn-sm">待审核</router-link>
      </div>
    </div>

    <div class="card">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="questions.length === 0" class="empty-state">
        <h3>暂无待审核题目</h3>
        <p>所有题目都已审核完毕</p>
      </div>
      <div v-else>
        <div v-for="q in questions" :key="q.id" class="review-item">
          <div class="review-header">
            <h3>{{ q.title }}</h3>
            <span :class="'tag tag-' + q.difficulty">
              {{ {easy:'简单', medium:'中等', hard:'困难'}[q.difficulty] }}
            </span>
          </div>
          <div class="review-meta">
            知识点：{{ q.knowledgePoint }} | 来源：{{ q.source === 'ai_generated' ? 'AI生成' : '手动录入' }}
          </div>
          <p class="review-desc">{{ q.description }}</p>
          <div v-if="q.sampleInput" class="review-section">
            <strong>样例输入：</strong>
            <pre class="code-block">{{ q.sampleInput }}</pre>
          </div>
          <div v-if="q.sampleOutput" class="review-section">
            <strong>样例输出：</strong>
            <pre class="code-block">{{ q.sampleOutput }}</pre>
          </div>
          <div class="review-actions">
            <button class="btn btn-success btn-sm" @click="handleApprove(q.id)">审核通过</button>
            <button class="btn btn-danger btn-sm" @click="handleReject(q.id)">驳回</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'

const loading = ref(true)
const questions = ref<any[]>([])

onMounted(() => loadQuestions())

async function loadQuestions() {
  loading.value = true
  try {
    const res = await adminApi.getPendingReview()
    if (res.data.code === 200) questions.value = res.data.data
  } catch (e) { /* ignore */ }
  finally { loading.value = false }
}

async function handleApprove(id: number) {
  try {
    await adminApi.approveQuestion(id)
    questions.value = questions.value.filter(q => q.id !== id)
  } catch (e) { /* ignore */ }
}

async function handleReject(id: number) {
  try {
    await adminApi.rejectQuestion(id)
    questions.value = questions.value.filter(q => q.id !== id)
  } catch (e) { /* ignore */ }
}
</script>

<style scoped>
.admin-nav { display: flex; gap: 8px; }
.review-item {
  padding: 20px; border: 1px solid #ebeef5; border-radius: 6px; margin-bottom: 16px;
}
.review-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.review-header h3 { font-size: 16px; color: #303133; }
.review-meta { font-size: 13px; color: #909399; margin-bottom: 12px; }
.review-desc { font-size: 14px; color: #606266; margin-bottom: 12px; line-height: 1.6; }
.review-section { margin-bottom: 8px; }
.review-section strong { font-size: 13px; color: #606266; }
.review-section pre { margin-top: 4px; padding: 8px 12px; }
.review-actions { margin-top: 12px; display: flex; gap: 8px; }
</style>

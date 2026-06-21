<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>AI 辅助出题</h1>
      <div class="admin-nav">
        <router-link to="/admin/questions" class="btn btn-default btn-sm">题目管理</router-link>
        <router-link to="/admin/generate" class="btn btn-success btn-sm">AI 出题</router-link>
        <router-link to="/admin/review" class="btn btn-warning btn-sm">待审核</router-link>
      </div>
    </div>

    <div class="card">
      <h2 class="section-title">输入题目要求</h2>
      <div v-if="error" class="alert alert-error">{{ error }}</div>
      <div v-if="success" class="alert alert-success">{{ success }}</div>
      <form @submit.prevent="handleGenerate">
        <div class="form-group">
          <label>知识点</label>
          <select v-model="form.knowledgePoint" class="form-select" required>
            <option value="">请选择知识点</option>
            <option v-for="kp in knowledgePoints" :key="kp" :value="kp">{{ kp }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>难度</label>
          <select v-model="form.difficulty" class="form-select" required>
            <option value="easy">简单</option>
            <option value="medium">中等</option>
            <option value="hard">困难</option>
          </select>
        </div>
        <div class="form-group">
          <label>题目主题</label>
          <input v-model="form.topic" class="form-input" placeholder="例如：数组求和、字符串匹配" required />
          <p style="font-size:12px;color:#909399;margin-top:4px;">简要描述你想要题目的核心主题</p>
        </div>
        <button type="submit" class="btn btn-primary" :disabled="generating">
          {{ generating ? '生成中（调用AI可能需要几秒）...' : '开始生成' }}
        </button>
      </form>
    </div>

    <!-- 生成结果预览 -->
    <div v-if="generatedQuestion" class="card">
      <h2 class="section-title">生成结果预览</h2>
      <div class="alert alert-info">该题目当前为「待审核」状态，审核通过后将对用户可见。</div>
      <div class="form-group">
        <label>标题</label>
        <input :value="generatedQuestion.title" class="form-input" disabled />
      </div>
      <div class="form-group">
        <label>题目描述</label>
        <textarea :value="generatedQuestion.description" class="form-textarea" rows="4" disabled></textarea>
      </div>
      <div class="form-group">
        <label>样例输入</label>
        <textarea :value="generatedQuestion.sampleInput" class="form-textarea" rows="2" disabled></textarea>
      </div>
      <div class="form-group">
        <label>样例输出</label>
        <textarea :value="generatedQuestion.sampleOutput" class="form-textarea" rows="2" disabled></textarea>
      </div>
      <div class="form-group">
        <label>参考代码</label>
        <textarea :value="generatedQuestion.referenceCode" class="form-textarea" rows="6" disabled></textarea>
      </div>
      <div class="modal-actions">
        <button class="btn btn-success" @click="handleApprove(generatedQuestion.id)">审核通过</button>
        <button class="btn btn-danger" @click="handleReject(generatedQuestion.id)">驳回</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { adminApi } from '@/api'

const form = reactive({ knowledgePoint: '', difficulty: 'easy', topic: '' })
const generating = ref(false)
const error = ref('')
const success = ref('')
const generatedQuestion = ref<any>(null)
const knowledgePoints = ['基础语法', '条件判断', '循环结构', '字符串处理', '数组操作', '递归与递推', '排序算法', '查找算法', '动态规划', '图算法']

async function handleGenerate() {
  generating.value = true
  error.value = ''
  success.value = ''
  generatedQuestion.value = null
  try {
    const res = await adminApi.generateQuestion(form)
    if (res.data.code === 200) {
      generatedQuestion.value = res.data.data
      success.value = '题目生成成功！请预览并审核。'
    } else {
      error.value = res.data.message
    }
  } catch (e: any) {
    error.value = e.response?.data?.message || '生成失败，请重试'
  } finally {
    generating.value = false
  }
}

async function handleApprove(id: number) {
  try {
    await adminApi.approveQuestion(id)
    success.value = '已审核通过，题目对用户可见！'
    generatedQuestion.value = null
  } catch (e) { /* ignore */ }
}

async function handleReject(id: number) {
  try {
    await adminApi.rejectQuestion(id)
    success.value = '已驳回该题目'
    generatedQuestion.value = null
  } catch (e) { /* ignore */ }
}
</script>

<style scoped>
.admin-nav { display: flex; gap: 8px; }
.section-title { font-size: 18px; color: #303133; margin-bottom: 16px; }
form { max-width: 500px; }
.modal-actions { margin-top: 16px; }
.card + .card { margin-top: 16px; }
</style>

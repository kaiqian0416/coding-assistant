<template>
  <div class="practice-page">
    <div v-if="loading" class="loading">加载题目中...</div>
    <div v-else-if="error" class="alert alert-error">{{ error }}</div>
    <div v-else-if="isAdmin" class="card" style="text-align:center;padding:60px">
      <h2 style="color:#909399">管理员无需做题</h2>
      <p style="color:#c0c4cc;margin-top:8px">管理员负责题库管理，请在后台管理操作</p>
      <router-link to="/admin/questions" class="btn btn-primary" style="margin-top:20px;display:inline-flex">前往后台管理</router-link>
    </div>
    <template v-else-if="question">
      <div class="practice-layout">
        <!-- 左栏：题目描述 -->
        <div class="left-panel">
          <div class="card">
            <div class="q-header">
              <h1>{{ question.title }}</h1>
              <div class="q-tags">
                <span :class="'tag tag-' + question.difficulty">
                  {{ {easy:'简单', medium:'中等', hard:'困难'}[question.difficulty] || question.difficulty }}
                </span>
                <span class="tag tag-pending">{{ question.knowledgePoint }}</span>
              </div>
            </div>
            <div class="q-description" v-html="renderDescription(question.description)"></div>
            <div v-if="question.inputDescription" class="q-section">
              <h3>输入格式</h3>
              <p>{{ question.inputDescription }}</p>
            </div>
            <div v-if="question.outputDescription" class="q-section">
              <h3>输出格式</h3>
              <p>{{ question.outputDescription }}</p>
            </div>
            <div v-if="question.sampleInput" class="q-section">
              <h3>样例输入</h3>
              <pre class="code-block">{{ question.sampleInput }}</pre>
            </div>
            <div v-if="question.sampleOutput" class="q-section">
              <h3>样例输出</h3>
              <pre class="code-block">{{ question.sampleOutput }}</pre>
            </div>
          </div>
        </div>
        <!-- 右栏：代码编辑器 -->
        <div class="right-panel">
          <div class="card editor-card">
            <div class="editor-header">
              <span>代码编辑器</span>
              <button class="btn btn-primary btn-sm" @click="handleSubmit" :disabled="submitting">
                {{ submitting ? '判题中...' : '提交代码' }}
              </button>
            </div>
            <textarea v-model="code" class="code-editor" placeholder="在此编写你的 Python 代码..." spellcheck="false"></textarea>
          </div>
          <div v-if="submission" class="card result-card">
            <div class="result-header">
              <h3>判题结果</h3>
              <span :class="'tag tag-' + resultTag">{{ resultText }}</span>
            </div>
            <div v-if="submission.score != null"><p>得分：<strong>{{ submission.score }}</strong> / 100</p></div>
            <div v-if="submission.errorInfo" class="result-section">
              <h4>错误信息</h4>
              <pre class="code-block">{{ submission.errorInfo }}</pre>
            </div>
            <div v-if="submission.aiDiagnosis" class="result-section">
              <h4>AI 诊断建议</h4>
              <pre class="diagnosis-block">{{ submission.aiDiagnosis }}</pre>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { questionApi, submissionApi } from '@/api'

const route = useRoute()
const auth = useAuthStore()
const isAdmin = computed(() => auth.isAdmin)
const question = ref<any>(null)
const loading = ref(true)
const error = ref('')
const code = ref('')
const submitting = ref(false)
const submission = ref<any>(null)

const resultTag = computed(() => {
  if (!submission.value) return ''
  const r = submission.value.result
  if (r === 'accepted') return 'approved'
  if (['wrong_answer','compile_error','runtime_error'].includes(r)) return 'rejected'
  return 'pending'
})
const resultText = computed(() => {
  const r = submission.value?.result
  return { accepted:'通过', wrong_answer:'答案错误', compile_error:'编译错误', runtime_error:'运行错误', judging:'判题中' }[r] || r
})

onMounted(async () => {
  const id = Number(route.params.id)
  if (!id) { error.value = '题目ID无效'; loading.value = false; return }
  try {
    const res = await questionApi.getById(id)
    if (res.data.code === 200) {
      question.value = res.data.data
      code.value = '# 在这里编写你的 Python 代码\n'
    } else {
      error.value = res.data.message
    }
  } catch (e) { error.value = '加载题目失败' }
  finally { loading.value = false }
})

async function handleSubmit() {
  submitting.value = true; submission.value = null
  try {
    const res = await submissionApi.submit({
      questionId: Number(route.params.id), code: code.value, language: 'python'
    })
    if (res.data.code === 200) submission.value = res.data.data
    else submission.value = { result:'error', score:0, errorInfo:res.data.message, aiDiagnosis:'' }
  } catch (e: any) {
    submission.value = { result:'error', score:0,
      errorInfo: e.response?.data?.message || e.message, aiDiagnosis:'' }
  } finally { submitting.value = false }
}

function renderDescription(text: string) {
  return text ? text.replace(/\n/g, '<br>') : ''
}
</script>

<style scoped>
.practice-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; min-height: calc(100vh - 120px); }
.left-panel { overflow-y: auto; }
.q-header { margin-bottom: 16px; }
.q-header h1 { font-size: 22px; color: #303133; margin-bottom: 8px; }
.q-tags { display: flex; gap: 6px; }
.q-description { font-size: 15px; line-height: 1.8; color: #303133; margin-bottom: 16px; }
.q-section { margin-top: 16px; }
.q-section h3 { font-size: 14px; color: #606266; margin-bottom: 8px; }
.q-section p { font-size: 14px; color: #606266; }
.right-panel { display: flex; flex-direction: column; gap: 16px; }
.editor-card { flex: 1; display: flex; flex-direction: column; }
.editor-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; font-size: 15px; font-weight: 600; color: #303133; }
.code-editor {
  flex: 1; min-height: 400px; font-family: 'Consolas', monospace;
  font-size: 14px; line-height: 1.6; padding: 16px; border: 1px solid #dcdfe6;
  border-radius: 6px; resize: vertical; outline: none; background: #1e1e1e; color: #d4d4d4;
}
.code-editor:focus { border-color: #409eff; }
.code-editor::placeholder { color: #6a6a6a; }
.result-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.result-header h3 { font-size: 16px; color: #303133; }
.result-section { margin-top: 12px; }
.result-section h4 { font-size: 14px; color: #606266; margin-bottom: 8px; }
.diagnosis-block {
  white-space: pre-wrap; background: #f0f5ff; color: #303133;
  padding: 12px 16px; border-radius: 6px; font-size: 13px; line-height: 1.6;
  border: 1px solid #d9ecff; font-family: inherit;
}
.card + .card { margin-top: 16px; }
</style>

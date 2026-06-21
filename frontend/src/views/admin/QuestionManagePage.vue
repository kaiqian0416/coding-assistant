<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>题目管理</h1>
      <div class="admin-nav">
        <router-link to="/admin/questions" class="btn btn-primary btn-sm">题目管理</router-link>
        <router-link to="/admin/generate" class="btn btn-success btn-sm">AI 出题</router-link>
        <router-link to="/admin/review" class="btn btn-warning btn-sm">待审核</router-link>
      </div>
    </div>

    <div class="card">
      <div class="toolbar">
        <div class="filter-bar">
          <select v-model="filters.difficulty" class="form-select" style="width:120px" @change="loadQuestions">
            <option value="">全部难度</option>
            <option value="easy">简单</option>
            <option value="medium">中等</option>
            <option value="hard">困难</option>
          </select>
          <select v-model="filters.reviewStatus" class="form-select" style="width:120px" @change="loadQuestions">
            <option value="">全部状态</option>
            <option value="approved">已通过</option>
            <option value="pending">待审核</option>
            <option value="rejected">已驳回</option>
          </select>
        </div>
        <button class="btn btn-primary btn-sm" @click="showAddDialog = true">+ 新增题目</button>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>难度</th>
            <th>知识点</th>
            <th>来源</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="q in questions" :key="q.id">
            <td>{{ q.id }}</td>
            <td>{{ q.title }}</td>
            <td><span :class="'tag tag-' + q.difficulty">{{ {easy:'简单',medium:'中等',hard:'困难'}[q.difficulty] }}</span></td>
            <td>{{ q.knowledgePoint }}</td>
            <td>{{ q.source === 'ai_generated' ? 'AI生成' : '手动录入' }}</td>
            <td><span :class="'tag tag-' + q.reviewStatus">{{ {pending:'待审核',approved:'已通过',rejected:'已驳回'}[q.reviewStatus] || q.reviewStatus }}</span></td>
            <td>
              <button class="btn btn-default btn-sm" @click="editQuestion(q)">编辑</button>
              <button class="btn btn-danger btn-sm" @click="handleDelete(q.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="questions.length === 0" class="empty-state"><h3>暂无题目</h3></div>
    </div>

    <!-- 编辑/新增对话框 -->
    <div v-if="showAddDialog || editingQuestion" class="modal-overlay" @click.self="closeDialog">
      <div class="modal-dialog">
        <h2>{{ editingQuestion ? '编辑题目' : '新增题目' }}</h2>
        <div class="form-group">
          <label>标题</label>
          <input v-model="editForm.title" class="form-input" />
        </div>
        <div class="form-group">
          <label>题目描述</label>
          <textarea v-model="editForm.description" class="form-textarea" rows="4"></textarea>
        </div>
        <div class="form-row">
          <div class="form-group" style="flex:1">
            <label>难度</label>
            <select v-model="editForm.difficulty" class="form-select">
              <option value="easy">简单</option>
              <option value="medium">中等</option>
              <option value="hard">困难</option>
            </select>
          </div>
          <div class="form-group" style="flex:1">
            <label>知识点</label>
            <select v-model="editForm.knowledgePoint" class="form-select">
              <option v-for="kp in knowledgePoints" :key="kp" :value="kp">{{ kp }}</option>
            </select>
          </div>
        </div>
        <div class="form-group">
          <label>样例输入</label>
          <textarea v-model="editForm.sampleInput" class="form-textarea" rows="2"></textarea>
        </div>
        <div class="form-group">
          <label>样例输出</label>
          <textarea v-model="editForm.sampleOutput" class="form-textarea" rows="2"></textarea>
        </div>
        <div class="form-group">
          <label>参考答案</label>
          <textarea v-model="editForm.referenceCode" class="form-textarea" rows="6"></textarea>
        </div>
        <div class="modal-actions">
          <button class="btn btn-default" @click="closeDialog">取消</button>
          <button class="btn btn-primary" @click="saveQuestion">{{ editingQuestion ? '保存' : '添加' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { questionApi, adminApi } from '@/api'

const questions = ref<any[]>([])
const filters = reactive({ difficulty: '', reviewStatus: '' })
const showAddDialog = ref(false)
const editingQuestion = ref<any>(null)
const editForm = reactive({
  title: '', description: '', difficulty: 'easy', knowledgePoint: '基础语法',
  sampleInput: '', sampleOutput: '', referenceCode: ''
})
const knowledgePoints = ['基础语法', '条件判断', '循环结构', '字符串处理', '数组操作', '递归与递推', '排序算法', '查找算法', '动态规划', '图算法']

onMounted(() => loadQuestions())

async function loadQuestions() {
  try {
    const res = await questionApi.list({
      difficulty: filters.difficulty || undefined,
      ...(filters.reviewStatus ? {} : {})
    })
    if (res.data.code === 200) questions.value = res.data.data
  } catch (e) { /* ignore */ }
}

function editQuestion(q: any) {
  editingQuestion.value = q
  Object.assign(editForm, {
    title: q.title, description: q.description, difficulty: q.difficulty,
    knowledgePoint: q.knowledgePoint, sampleInput: q.sampleInput,
    sampleOutput: q.sampleOutput, referenceCode: q.referenceCode
  })
}

function closeDialog() {
  showAddDialog.value = false
  editingQuestion.value = null
  Object.assign(editForm, { title: '', description: '', difficulty: 'easy', knowledgePoint: '基础语法', sampleInput: '', sampleOutput: '', referenceCode: '' })
}

async function saveQuestion() {
  try {
    if (editingQuestion.value) {
      await adminApi.updateQuestion(editingQuestion.value.id, editForm)
    } else {
      await adminApi.addQuestion(editForm)
    }
    closeDialog()
    await loadQuestions()
  } catch (e) { /* ignore */ }
}

async function handleDelete(id: number) {
  if (!confirm('确定删除该题目吗？')) return
  try {
    await adminApi.deleteQuestion(id)
    await loadQuestions()
  } catch (e) { /* ignore */ }
}
</script>

<style scoped>
.admin-nav { display: flex; gap: 8px; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.filter-bar { display: flex; gap: 12px; }
.modal-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.4); display: flex; align-items: center;
  justify-content: center; z-index: 200;
}
.modal-dialog {
  background: #fff; border-radius: 8px; padding: 24px; width: 600px;
  max-height: 80vh; overflow-y: auto;
}
.modal-dialog h2 { margin-bottom: 20px; }
.form-row { display: flex; gap: 16px; }
.modal-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 20px; }
</style>

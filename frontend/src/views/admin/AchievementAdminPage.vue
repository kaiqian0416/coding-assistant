<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>🏆 成就管理</h1>

    </div>

    <div class="card">
      <div class="toolbar">
        <span class="filter-count">共 {{ list.length }} 个成就</span>
        <button class="btn btn-primary btn-sm" @click="openAdd">+ 新增成就</button>
      </div>

      <table class="data-table">
        <thead>
          <tr><th>图标</th><th>名称</th><th>描述</th><th>条件类型</th><th>阈值</th><th>排序</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="a in list" :key="a.id">
            <td style="font-size:24px">{{ a.icon }}</td>
            <td>{{ a.name }}</td>
            <td>{{ a.description || '-' }}</td>
            <td>{{ typeLabel(a.conditionType) }}</td>
            <td>{{ a.conditionValue }}</td>
            <td>{{ a.sortOrder }}</td>
            <td>
              <button class="btn btn-default btn-sm" @click="openEdit(a)">编辑</button>
              <button class="btn btn-danger btn-sm" @click="handleDelete(a.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 编辑对话框 -->
    <div v-if="showDialog" class="modal-overlay" @click.self="closeDialog">
      <div class="modal-dialog">
        <h2>{{ editing ? '编辑成就' : '新增成就' }}</h2>
        <div v-if="dialogError" class="alert alert-error">{{ dialogError }}</div>
        <div class="form-group">
          <label>名称</label>
          <input v-model="form.name" class="form-input" />
        </div>
        <div class="form-group">
          <label>描述</label>
          <input v-model="form.description" class="form-input" />
        </div>
        <div class="form-group">
          <label>图标（emoji）</label>
          <input v-model="form.icon" class="form-input" style="width:80px" />
        </div>
        <div class="form-row">
          <div class="form-group" style="flex:1">
            <label>条件类型</label>
            <select v-model="form.conditionType" class="form-select">
              <option value="total_accepted">通过数</option>
              <option value="total_wrong">错误数</option>
              <option value="total_submit">总提交数</option>
              <option value="consecutive_checkin">连续签到天数</option>
            </select>
          </div>
          <div class="form-group" style="flex:1">
            <label>阈值（≥）</label>
            <input v-model.number="form.conditionValue" class="form-input" type="number" min="1" />
          </div>
          <div class="form-group" style="flex:1">
            <label>排序</label>
            <input v-model.number="form.sortOrder" class="form-input" type="number" min="0" />
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn btn-default" @click="closeDialog">取消</button>
          <button class="btn btn-primary" @click="handleSave" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'

const list = ref<any[]>([])
const loading = ref(true)
const showDialog = ref(false)
const editing = ref<any>(null)
const saving = ref(false)
const dialogError = ref('')
const form = ref({ name: '', description: '', icon: '🏆', conditionType: 'total_accepted', conditionValue: 10, sortOrder: 0 })

onMounted(() => load())

async function load() {
  loading.value = true
  try { const r = await adminApi.listAchievements(); if (r.data.code === 200) list.value = r.data.data || [] } catch (e) {}
  finally { loading.value = false }
}

function openAdd() {
  editing.value = null
  form.value = { name: '', description: '', icon: '🏆', conditionType: 'total_accepted', conditionValue: 10, sortOrder: list.value.length + 1 }
  dialogError.value = ''; showDialog.value = true
}

function openEdit(a: any) {
  editing.value = a
  form.value = { name: a.name, description: a.description || '', icon: a.icon, conditionType: a.conditionType, conditionValue: a.conditionValue, sortOrder: a.sortOrder }
  dialogError.value = ''; showDialog.value = true
}

function closeDialog() { showDialog.value = false; editing.value = null }

async function handleSave() {
  if (!form.value.name) { dialogError.value = '名称不能为空'; return }
  saving.value = true; dialogError.value = ''
  try {
    if (editing.value) {
      const r = await adminApi.updateAchievement(editing.value.id, form.value)
      if (r.data.code === 200) { await load(); closeDialog() } else { dialogError.value = r.data.message }
    } else {
      const r = await adminApi.addAchievement(form.value)
      if (r.data.code === 200) { await load(); closeDialog() } else { dialogError.value = r.data.message }
    }
  } catch (e: any) { dialogError.value = e.response?.data?.message || '操作失败' }
  finally { saving.value = false }
}

async function handleDelete(id: number) {
  if (!confirm('确定删除该成就吗？')) return
  try { await adminApi.deleteAchievement(id); await load() } catch (e) {}
}

function typeLabel(t: string) {
  return { total_accepted: '通过数', total_wrong: '错误数', total_submit: '总提交数', consecutive_checkin: '连续签到天数' }[t] || t
}
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.filter-count { font-size: 13px; color: var(--text-muted); }
.modal-overlay { position:fixed; inset:0; background:rgba(0,0,0,0.4); display:flex; align-items:center; justify-content:center; z-index:200; }
.modal-dialog { background:var(--bg-card); backdrop-filter:blur(20px); border-radius:16px; padding:24px; width:520px; border:1px solid var(--border-color); }
.modal-dialog h2 { margin-bottom:20px; }
.form-row { display:flex; gap:16px; }
.modal-actions { display:flex; justify-content:flex-end; gap:8px; margin-top:20px; }
</style>

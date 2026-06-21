<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>用户管理</h1>
      <button class="btn btn-primary btn-sm" @click="openAdd">+ 新增用户</button>
    </div>

    <div class="card">
      <div class="toolbar">
        <input v-model="filterText" class="form-input" style="width:200px" placeholder="搜索用户名..." />
        <span class="filter-count">共 {{ filtered.length }} 个用户</span>
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="filtered.length === 0" class="empty-state"><h3>暂无用户</h3></div>
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>昵称</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>能力等级</th>
            <th>注册时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in filtered" :key="u.id">
            <td>{{ u.id }}</td>
            <td>{{ u.username }}</td>
            <td>{{ u.nickname || '-' }}</td>
            <td>{{ u.email || '-' }}</td>
            <td>
              <span :class="'tag ' + (u.role === 'admin' ? 'tag-approved' : 'tag-pending')">
                {{ u.role === 'admin' ? '管理员' : '普通用户' }}
              </span>
            </td>
            <td>{{ { easy:'简单', medium:'中等', hard:'困难' }[u.abilityLevel] || u.abilityLevel }}</td>
            <td>{{ formatTime(u.createdAt) }}</td>
            <td>
              <button class="btn btn-default btn-sm" @click="openEdit(u)">编辑</button>
              <button class="btn btn-danger btn-sm" @click="handleDelete(u)" :disabled="u.id === currentAdminId">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 新增/编辑对话框 -->
    <div v-if="showDialog" class="modal-overlay" @click.self="closeDialog">
      <div class="modal-dialog">
        <h2>{{ editingUser ? '编辑用户' : '新增用户' }}</h2>
        <div v-if="dialogError" class="alert alert-error">{{ dialogError }}</div>
        <div v-if="dialogSuccess" class="alert alert-success">{{ dialogSuccess }}</div>

        <div class="form-group">
          <label>用户名</label>
          <input v-model="form.username" class="form-input" :disabled="!!editingUser" />
        </div>
        <div class="form-group">
          <label>密码{{ editingUser ? '（留空不修改）' : '' }}</label>
          <input v-model="form.password" class="form-input" type="password" :placeholder="editingUser ? '留空则不修改密码' : '至少6个字符'" />
        </div>
        <div class="form-group">
          <label>昵称</label>
          <input v-model="form.nickname" class="form-input" />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input v-model="form.email" class="form-input" type="email" />
        </div>
        <div class="form-row">
          <div class="form-group" style="flex:1">
            <label>角色</label>
            <select v-model="form.role" class="form-select">
              <option value="user">普通用户</option>
              <option value="admin">管理员</option>
            </select>
          </div>
          <div class="form-group" style="flex:1">
            <label>能力等级</label>
            <select v-model="form.abilityLevel" class="form-select">
              <option value="easy">简单</option>
              <option value="medium">中等</option>
              <option value="hard">困难</option>
            </select>
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
import { ref, computed, onMounted } from 'vue'
import { adminApi } from '@/api'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const currentAdminId = computed(() => auth.user?.id)

const loading = ref(true)
const users = ref<any[]>([])
const filterText = ref('')
const showDialog = ref(false)
const editingUser = ref<any>(null)
const saving = ref(false)
const dialogError = ref('')
const dialogSuccess = ref('')
const form = ref({ username: '', password: '', nickname: '', email: '', role: 'user', abilityLevel: 'easy' })

const filtered = computed(() => {
  if (!filterText.value) return users.value
  return users.value.filter(u =>
    (u.username || '').includes(filterText.value) || (u.nickname || '').includes(filterText.value)
  )
})

onMounted(() => loadUsers())

async function loadUsers() {
  loading.value = true
  try { const res = await adminApi.listUsers(); if (res.data.code === 200) users.value = res.data.data || [] }
  catch (e) { /* ignore */ }
  finally { loading.value = false }
}

function openAdd() {
  editingUser.value = null
  form.value = { username: '', password: '', nickname: '', email: '', role: 'user', abilityLevel: 'easy' }
  dialogError.value = ''; dialogSuccess.value = ''
  showDialog.value = true
}

function openEdit(u: any) {
  editingUser.value = u
  form.value = {
    username: u.username, password: '', nickname: u.nickname || '',
    email: u.email || '', role: u.role || 'user', abilityLevel: u.abilityLevel || 'easy'
  }
  dialogError.value = ''; dialogSuccess.value = ''
  showDialog.value = true
}

function closeDialog() { showDialog.value = false; editingUser.value = null }

async function handleSave() {
  saving.value = true; dialogError.value = ''; dialogSuccess.value = ''
  try {
    if (editingUser.value) {
      const payload: any = { nickname: form.value.nickname, email: form.value.email, role: form.value.role, abilityLevel: form.value.abilityLevel }
      if (form.value.password) payload.password = form.value.password
      const res = await adminApi.updateUser(editingUser.value.id, payload)
      if (res.data.code === 200) { dialogSuccess.value = '保存成功'; await loadUsers(); setTimeout(closeDialog, 800) }
      else { dialogError.value = res.data.message }
    } else {
      if (!form.value.password || form.value.password.length < 6) { dialogError.value = '密码至少6个字符'; return }
      const res = await adminApi.addUser(form.value)
      if (res.data.code === 200) { dialogSuccess.value = '创建成功'; await loadUsers(); setTimeout(closeDialog, 800) }
      else { dialogError.value = res.data.message }
    }
  } catch (e: any) { dialogError.value = e.response?.data?.message || '操作失败' }
  finally { saving.value = false }
}

async function handleDelete(u: any) {
  if (!confirm(`确定删除用户「${u.username}」吗？`)) return
  try {
    const res = await adminApi.deleteUser(u.id)
    if (res.data.code === 200) await loadUsers()
    else alert(res.data.message)
  } catch (e: any) { alert(e.response?.data?.message || '删除失败') }
}

function formatTime(t: string) { return t ? t.substring(0, 16).replace('T', ' ') : '' }
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.filter-count { font-size: 13px; color: #909399; }
.modal-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.4); display: flex; align-items: center;
  justify-content: center; z-index: 200;
}
.modal-dialog { background: #fff; border-radius: 8px; padding: 24px; width: 480px; max-height: 80vh; overflow-y: auto; }
.modal-dialog h2 { margin-bottom: 20px; }
.form-row { display: flex; gap: 16px; }
.modal-actions { display: flex; justify-content: flex-end; gap: 8px; margin-top: 20px; }
</style>

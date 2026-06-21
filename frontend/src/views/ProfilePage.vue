<template>
  <div class="profile-page">
    <div class="page-header"><h1>个人中心</h1></div>

    <div v-if="loading" class="loading">加载中...</div>
    <template v-else>
      <div class="card">
        <h2 class="section-title">基本信息</h2>
        <div v-if="saveSuccess" class="alert alert-success">保存成功</div>
        <div v-if="saveError" class="alert alert-error">{{ saveError }}</div>
        <form @submit.prevent="handleUpdate">
          <div class="form-group">
            <label>用户名</label>
            <input :value="auth.user?.username" class="form-input" disabled />
          </div>
          <div class="form-group">
            <label>昵称</label>
            <input v-model="form.nickname" class="form-input" />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="form.email" class="form-input" type="email" />
          </div>
          <div class="form-group">
            <label>角色</label>
            <input :value="auth.user?.role === 'admin' ? '管理员' : '普通用户'" class="form-input" disabled />
          </div>
          <button type="submit" class="btn btn-primary" :disabled="saving">{{ saving ? '保存中...' : '保存修改' }}</button>
        </form>
      </div>

      <div class="card">
        <h2 class="section-title">修改密码</h2>
        <div v-if="pwSuccess" class="alert alert-success">{{ pwSuccess }}</div>
        <div v-if="pwError" class="alert alert-error">{{ pwError }}</div>
        <form @submit.prevent="handleChangePassword">
          <div class="form-group">
            <label>原密码</label>
            <input v-model="pwForm.oldPassword" class="form-input" type="password" placeholder="输入原密码" required />
          </div>
          <div class="form-group">
            <label>新密码</label>
            <input v-model="pwForm.newPassword" class="form-input" type="password" placeholder="至少6个字符" required />
          </div>
          <div class="form-group">
            <label>确认新密码</label>
            <input v-model="pwForm.confirmPassword" class="form-input" type="password" placeholder="再次输入新密码" required />
          </div>
          <button type="submit" class="btn btn-primary" :disabled="pwSaving">{{ pwSaving ? '修改中...' : '修改密码' }}</button>
        </form>
      </div>

      <div v-if="!auth.isAdmin" class="card">
        <h2 class="section-title">学习数据</h2>
        <div class="stats-grid">
          <div class="stat-item">
            <span class="stat-value">{{ overview.totalQuestions || 0 }}</span>
            <span class="stat-label">总做题数</span>
          </div>
          <div class="stat-item">
            <span class="stat-value" style="color:#10b981">{{ overview.correctCount || 0 }}</span>
            <span class="stat-label">通过数</span>
          </div>
          <div class="stat-item">
            <span class="stat-value" style="color:#ef4444">{{ overview.wrongCount || 0 }}</span>
            <span class="stat-label">错误数</span>
          </div>
          <div class="stat-item">
            <span class="stat-value" style="color:#8b5cf6">{{ (overview.overallAccuracy || 0) + '%' }}</span>
            <span class="stat-label">正确率</span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { userApi, statsApi } from '@/api'

const auth = useAuthStore()
const loading = ref(true); const saving = ref(false)
const saveSuccess = ref(false); const saveError = ref('')
const overview = ref<any>({})
const form = reactive({ nickname: '', email: '' })
const pwSaving = ref(false); const pwSuccess = ref(''); const pwError = ref('')
const pwForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

onMounted(async () => {
  if (auth.user) { form.nickname = auth.user.nickname || ''; form.email = auth.user.email || '' }
  try { const res = await statsApi.getOverview(); if (res.data.code === 200) overview.value = res.data.data }
  catch (e) { /* ignore */ }
  finally { loading.value = false }
})

async function handleUpdate() {
  saving.value = true; saveSuccess.value = false; saveError.value = ''
  try {
    const res = await userApi.updateProfile({ nickname: form.nickname, email: form.email })
    if (res.data.code === 200) { auth.setUser(res.data.data); saveSuccess.value = true; setTimeout(() => saveSuccess.value = false, 3000) }
    else { saveError.value = res.data.message }
  } catch (e: any) { saveError.value = e.response?.data?.message || '保存失败' }
  finally { saving.value = false }
}

async function handleChangePassword() {
  pwSuccess.value = ''; pwError.value = ''
  if (pwForm.newPassword !== pwForm.confirmPassword) { pwError.value = '两次输入的新密码不一致'; return }
  if (pwForm.newPassword.length < 6) { pwError.value = '新密码至少6个字符'; return }
  pwSaving.value = true
  try {
    const res = await userApi.changePassword({ oldPassword: pwForm.oldPassword, newPassword: pwForm.newPassword })
    if (res.data.code === 200) {
      pwSuccess.value = '密码修改成功！'; pwForm.oldPassword = ''; pwForm.newPassword = ''; pwForm.confirmPassword = ''
      setTimeout(() => pwSuccess.value = '', 5000)
    } else { pwError.value = res.data.message }
  } catch (e: any) { pwError.value = e.response?.data?.message || '修改失败' }
  finally { pwSaving.value = false }
}
</script>

<style scoped>
.section-title { font-size: 18px; color: var(--text-primary); margin-bottom: 16px; }
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.stat-item { text-align: center; padding: 16px; }
.stat-value { display: block; font-size: 32px; font-weight: 700; color: var(--text-primary); margin-bottom: 4px; }
.stat-label { font-size: 14px; color: var(--text-muted); }
.card + .card { margin-top: 16px; }
form { max-width: 500px; }
</style>

<template>
  <div class="page">
    <div class="page-header"><h1>📝 错题本</h1></div>
    <div class="card">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="items.length === 0" class="empty-state"><h3>暂无错题</h3><p>继续加油！</p></div>
      <div v-else class="list">
        <div v-for="s in items" :key="s.id" class="item" @click="toggle(s.id)">
          <div class="hdr">
            <span class="title">{{ s.question_title || '题目 #' + s.question_id }}</span>
            <span class="meta">{{ tag(s) }} · {{ s.score }}分 · {{ fmt(s.created_at) }}<span class="arrow">{{ expanded === s.id ? '▼' : '▶' }}</span></span>
          </div>
          <div v-if="expanded === s.id" class="detail">
            <pre class="code-block">{{ s.code }}</pre>
            <div v-if="s.error_info" class="err"><strong>错误：</strong><pre>{{ s.error_info }}</pre></div>
            <div v-if="s.ai_diagnosis" class="diag"><strong>AI：</strong><pre>{{ s.ai_diagnosis }}</pre></div>
            <button class="btn btn-default btn-sm" @click.stop="copy(s.code)">📋 复制代码</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { submissionApi } from '@/api'

const loading = ref(true)
const items = ref<any[]>([])
const expanded = ref<number | null>(null)

onMounted(async () => {
  try { const r = await submissionApi.getWrong(); if (r.data.code === 200) items.value = r.data.data || [] }
  catch (e) { /* ignore */ }
  finally { loading.value = false }
})

function toggle(id: number) { expanded.value = expanded.value === id ? null : id }
function tag(s: any) { return s.result === 'wrong_answer' ? '答案错误' : s.result === 'compile_error' ? '编译错误' : '运行错误' }
function fmt(t: string) { return t ? t.substring(0, 16).replace('T', ' ') : '' }
function copy(c: string) { navigator.clipboard?.writeText(c).then(() => alert('已复制')) }
</script>

<style scoped>
.list { display: flex; flex-direction: column; gap: 8px; }
.item { border: 1px solid rgba(255,255,255,0.06); border-radius: 12px; cursor: pointer; transition: box-shadow 0.2s; }
.item:hover { box-shadow: 0 2px 12px rgba(0,0,0,0.1); }
.hdr { display: flex; justify-content: space-between; padding: 12px 16px; align-items: center; }
.title { font-size: 14px; font-weight: 600; color: var(--text-primary); }
.meta { font-size: 12px; color: var(--text-muted); display: flex; gap: 8px; align-items: center; }
.arrow { margin-left: 4px; }
.detail { padding: 0 16px 16px; border-top: 1px solid rgba(255,255,255,0.04); }
.code-block { background: var(--bg-code); color: var(--text-primary); border-radius: 8px; padding: 12px; font-size: 13px; font-family: monospace; overflow-x: auto; max-height: 200px; margin-top: 12px; }
.err pre, .diag pre { white-space: pre-wrap; font-size: 13px; font-family: inherit; margin-top: 4px; }
</style>

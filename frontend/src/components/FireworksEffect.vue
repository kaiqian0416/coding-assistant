<template>
  <canvas ref="canvasRef" class="fireworks-canvas" v-show="active"></canvas>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{ active: boolean }>()
const canvasRef = ref<HTMLCanvasElement | null>(null)
let ctx: CanvasRenderingContext2D | null = null
let animationId = 0
let particles: FireParticle[] = []
let rockets: Rocket[] = []
let startTime = 0
const DURATION = 5000

interface FireParticle {
  x: number; y: number; vx: number; vy: number; size: number
  color: string; alpha: number; life: number; maxLife: number; decay: number
}
interface Rocket {
  x: number; y: number; targetY: number; speed: number; alpha: number; trail: { x: number; y: number }[]
}

const COLORS = ['#ff6b6b', '#feca57', '#48dbfb', '#ff9ff3', '#54a0ff', '#5f27cd', '#ff9f43', '#00d2d3', '#a29bfe', '#fd79a8', '#6c5ce7', '#00cec9']

function hexToRgba(hex: string, alpha: number): string {
  const r = parseInt(hex.slice(1, 3), 16)
  const g = parseInt(hex.slice(3, 5), 16)
  const b = parseInt(hex.slice(5, 7), 16)
  return `rgba(${r}, ${g}, ${b}, ${alpha})`
}

function createExplosion(x: number, y: number) {
  const count = 60 + Math.floor(Math.random() * 60)
  const color = COLORS[Math.floor(Math.random() * COLORS.length)]
  for (let i = 0; i < count; i++) {
    const angle = (Math.PI * 2 * i) / count + (Math.random() - 0.5) * 0.3
    const speed = 2 + Math.random() * 5
    particles.push({
      x, y, vx: Math.cos(angle) * speed, vy: Math.sin(angle) * speed,
      size: 2 + Math.random() * 3, color,
      alpha: 1, life: 0, maxLife: 40 + Math.random() * 40,
      decay: 0.01 + Math.random() * 0.02
    })
  }
}

function launchRocket() {
  const x = 100 + Math.random() * (window.innerWidth - 200)
  const targetY = 50 + Math.random() * window.innerHeight * 0.5
  rockets.push({ x, y: window.innerHeight + 20, targetY, speed: 6 + Math.random() * 4, alpha: 1, trail: [] })
}

function animate() {
  if (!ctx || !canvasRef.value) return
  const w = canvasRef.value.width, h = canvasRef.value.height
  const elapsed = Date.now() - startTime

  ctx.clearRect(0, 0, w, h)

  for (let i = rockets.length - 1; i >= 0; i--) {
    const r = rockets[i]
    r.trail.push({ x: r.x, y: r.y })
    if (r.trail.length > 12) r.trail.shift()
    r.y -= r.speed; r.speed *= 0.995
    for (let j = 0; j < r.trail.length; j++) {
      ctx.beginPath(); ctx.arc(r.trail[j].x, r.trail[j].y, 1.5 * (j / r.trail.length), 0, Math.PI * 2)
      ctx.fillStyle = `rgba(255, 200, 100, ${0.3 * (j / r.trail.length)})`; ctx.fill()
    }
    if (r.y <= r.targetY || r.speed < 0.5) { createExplosion(r.x, r.y); rockets.splice(i, 1) }
  }

  for (let i = particles.length - 1; i >= 0; i--) {
    const p = particles[i]
    p.x += p.vx; p.y += p.vy; p.vy += 0.05; p.vx *= 0.99
    p.alpha -= p.decay; p.life++
    if (p.alpha <= 0 || p.life > p.maxLife) { particles.splice(i, 1); continue }
    ctx.beginPath(); ctx.arc(p.x, p.y, p.size * p.alpha, 0, Math.PI * 2)
    ctx.fillStyle = p.color; ctx.globalAlpha = p.alpha; ctx.fill(); ctx.globalAlpha = 1
    const glow = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, p.size * 4)
    glow.addColorStop(0, hexToRgba(p.color, 0.2)); glow.addColorStop(1, 'transparent')
    ctx.fillStyle = glow; ctx.fillRect(p.x - p.size * 4, p.y - p.size * 4, p.size * 8, p.size * 8)
  }

  if (elapsed < DURATION && Math.random() < 0.08) launchRocket()
  if (elapsed < DURATION || particles.length > 0) animationId = requestAnimationFrame(animate)
}

function startFireworks() {
  if (!canvasRef.value) return
  canvasRef.value.width = window.innerWidth; canvasRef.value.height = window.innerHeight
  ctx = canvasRef.value.getContext('2d'); particles = []; rockets = []; startTime = Date.now()
  for (let i = 0; i < 5; i++) setTimeout(() => launchRocket(), i * 200)
  cancelAnimationFrame(animationId); animationId = requestAnimationFrame(animate)
}

watch(() => props.active, (val) => {
  if (val) setTimeout(startFireworks, 100)
  else { cancelAnimationFrame(animationId); particles = []; rockets = [] }
})
</script>

<style scoped>
.fireworks-canvas { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; z-index: 1000; pointer-events: none; }
</style>

<template>
  <canvas ref="canvasRef" class="particle-canvas"></canvas>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'

const canvasRef = ref<HTMLCanvasElement | null>(null)
let ctx: CanvasRenderingContext2D | null = null
let animationId = 0
let mouseX = 0
let mouseY = 0
let particles: Particle[] = []
let stars: Star[] = []

interface Particle {
  x: number; y: number; vx: number; vy: number; size: number
  color: string; alpha: number; life: number; maxLife: number
}
interface Star {
  x: number; y: number; size: number; alpha: number; twinkleSpeed: number
}

const COLORS = ['#8b5cf6', '#6366f1', '#ec4899', '#f43f5e', '#06b6d4', '#a855f7', '#3b82f6']

function initParticles(w: number, h: number) {
  particles = []
  for (let i = 0; i < 80; i++) {
    const maxLife = 100 + Math.random() * 200
    particles.push({
      x: Math.random() * w, y: Math.random() * h,
      vx: (Math.random() - 0.5) * 0.5, vy: (Math.random() - 0.5) * 0.5 - 0.3,
      size: 2 + Math.random() * 4,
      color: COLORS[Math.floor(Math.random() * COLORS.length)],
      alpha: 0.3 + Math.random() * 0.7, life: 0, maxLife
    })
  }
}

function initStars(w: number, h: number) {
  stars = []
  for (let i = 0; i < 120; i++) {
    stars.push({
      x: Math.random() * w, y: Math.random() * h,
      size: 0.5 + Math.random() * 1.5,
      alpha: 0.1 + Math.random() * 0.5,
      twinkleSpeed: 0.005 + Math.random() * 0.02
    })
  }
}

function drawGradientBackground(ctx: CanvasRenderingContext2D, w: number, h: number, t: number) {
  const grad = ctx.createRadialGradient(w * 0.5, h * 0.5, 0, w * 0.5, h * 0.5, w * 0.8)
  const hue1 = (t * 0.02) % 360
  const hue2 = (t * 0.02 + 120) % 360
  grad.addColorStop(0, `hsla(${hue1}, 70%, 60%, 0.08)`)
  grad.addColorStop(0.4, `hsla(${hue2}, 60%, 50%, 0.05)`)
  grad.addColorStop(1, 'transparent')
  ctx.fillStyle = grad
  ctx.fillRect(0, 0, w, h)
}

function drawConnections(ctx: CanvasRenderingContext2D, w: number, h: number) {
  for (let i = 0; i < particles.length; i++) {
    for (let j = i + 1; j < particles.length; j++) {
      const dx = particles[i].x - particles[j].x
      const dy = particles[i].y - particles[j].y
      const dist = Math.sqrt(dx * dx + dy * dy)
      if (dist < 150) {
        ctx.beginPath()
        ctx.moveTo(particles[i].x, particles[i].y)
        ctx.lineTo(particles[j].x, particles[j].y)
        ctx.strokeStyle = `rgba(139, 92, 246, ${0.06 * (1 - dist / 150)})`
        ctx.lineWidth = 0.5
        ctx.stroke()
      }
    }
  }
}

function animate(t: number) {
  if (!ctx || !canvasRef.value) return
  const w = canvasRef.value.width
  const h = canvasRef.value.height

  ctx.clearRect(0, 0, w, h)

  // 透明背景 + 流动极光 overlay（让 CSS 背景色透出）
  drawGradientBackground(ctx, w, h, t)

  // 星星
  for (const star of stars) {
    star.alpha = 0.1 + 0.4 * (0.5 + 0.5 * Math.sin(t * star.twinkleSpeed))
    ctx.beginPath()
    ctx.arc(star.x, star.y, star.size, 0, Math.PI * 2)
    ctx.fillStyle = `rgba(255, 255, 255, ${star.alpha * 0.4})`
    ctx.fill()
  }

  // 粒子
  for (const p of particles) {
    p.life++
    if (p.life > p.maxLife) {
      p.x = Math.random() * w; p.y = h + 10
      p.vx = (Math.random() - 0.5) * 0.5
      p.vy = -0.5 - Math.random() * 0.8
      p.life = 0; p.maxLife = 100 + Math.random() * 200
      p.color = COLORS[Math.floor(Math.random() * COLORS.length)]
    }

    const dx = mouseX - p.x; const dy = mouseY - p.y
    const dist = Math.sqrt(dx * dx + dy * dy)
    if (dist < 200) {
      p.vx -= dx * 0.00008; p.vy -= dy * 0.00008
    }

    p.x += p.vx; p.y += p.vy
    const fade = p.life < 30 ? p.life / 30 : 1
    ctx.beginPath()
    ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2)
    ctx.fillStyle = p.color
    ctx.globalAlpha = p.alpha * fade * 0.7
    ctx.fill()
    ctx.globalAlpha = 1
  }

  drawConnections(ctx, w, h)
  animationId = requestAnimationFrame(() => animate(t + 1))
}

function onMouse(e: MouseEvent) {
  if (!canvasRef.value) return
  const rect = canvasRef.value.getBoundingClientRect()
  mouseX = e.clientX - rect.left; mouseY = e.clientY - rect.top
}

function resize() {
  if (!canvasRef.value) return
  canvasRef.value.width = window.innerWidth; canvasRef.value.height = window.innerHeight
  initParticles(canvasRef.value.width, canvasRef.value.height)
  initStars(canvasRef.value.width, canvasRef.value.height)
}

onMounted(() => {
  if (!canvasRef.value) return
  ctx = canvasRef.value.getContext('2d')
  resize()
  window.addEventListener('resize', resize)
  window.addEventListener('mousemove', onMouse)
  animationId = requestAnimationFrame(() => animate(0))
})

onBeforeUnmount(() => {
  cancelAnimationFrame(animationId)
  window.removeEventListener('resize', resize)
  window.removeEventListener('mousemove', onMouse)
})
</script>

<style scoped>
.particle-canvas {
  position: fixed; top: 0; left: 0; width: 100vw; height: 100vh;
  z-index: 0; pointer-events: none;
}
</style>

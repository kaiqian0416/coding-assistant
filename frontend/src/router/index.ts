import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: () => import('@/views/HomePage.vue'), meta: { requiresAuth: false } },
    { path: '/login', name: 'login', component: () => import('@/views/LoginPage.vue'), meta: { requiresAuth: false } },
    { path: '/register', name: 'register', component: () => import('@/views/RegisterPage.vue'), meta: { requiresAuth: false } },
    { path: '/questions', name: 'questions', component: () => import('@/views/QuestionBankPage.vue'), meta: { requiresAuth: false } },
    { path: '/practice/:id', name: 'practice', component: () => import('@/views/PracticePage.vue'), meta: { requiresAuth: true } },
    { path: '/stats', name: 'stats', component: () => import('@/views/StatsPage.vue'), meta: { requiresAuth: true } },
    { path: '/leaderboard', name: 'leaderboard', component: () => import('@/views/LeaderboardPage.vue'), meta: { requiresAuth: false } },
    { path: '/wrong-answers', name: 'wrong-answers', component: () => import('@/views/WrongAnswersPage.vue'), meta: { requiresAuth: true } },
    { path: '/achievements', name: 'achievements', component: () => import('@/views/AchievementsPage.vue'), meta: { requiresAuth: true } },
    { path: '/profile', name: 'profile', component: () => import('@/views/ProfilePage.vue'), meta: { requiresAuth: true } },
    { path: '/admin/questions', name: 'admin-questions', component: () => import('@/views/admin/QuestionManagePage.vue'), meta: { requiresAuth: true, requiresAdmin: true } },
    { path: '/admin/generate', name: 'admin-generate', component: () => import('@/views/admin/AiGeneratePage.vue'), meta: { requiresAuth: true, requiresAdmin: true } },
    { path: '/admin/review', name: 'admin-review', component: () => import('@/views/admin/ReviewPage.vue'), meta: { requiresAuth: true, requiresAdmin: true } },
    { path: '/admin/submissions', name: 'admin-submissions', component: () => import('@/views/admin/SubmissionsPage.vue'), meta: { requiresAuth: true, requiresAdmin: true } },
    { path: '/admin/users', name: 'admin-users', component: () => import('@/views/admin/UserManagePage.vue'), meta: { requiresAuth: true, requiresAdmin: true } },
    { path: '/admin/achievements', name: 'admin-achievements', component: () => import('@/views/admin/AchievementAdminPage.vue'), meta: { requiresAuth: true, requiresAdmin: true } }
  ]
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) return '/login'
  if (to.meta.requiresAdmin && !auth.isAdmin) return '/'
})

export default router

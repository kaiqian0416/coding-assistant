import axios from 'axios'
import router from '@/router'

const api = axios.create({
  baseURL: 'http://localhost:8080/api', timeout: 30000,
  withCredentials: true, headers: { 'Content-Type': 'application/json' }
})

api.interceptors.response.use(
  (r) => r,
  (e) => { if (e.response?.status === 401) { localStorage.removeItem('user'); router.push('/login') }; return Promise.reject(e) }
)

export const userApi = {
  register(d: any) { return api.post('/user/register', d) },
  login(d: any) { return api.post('/user/login', d) },
  logout() { return api.post('/user/logout') },
  getProfile() { return api.get('/user/profile') },
  updateProfile(d: any) { return api.put('/user/profile', d) },
  changePassword(d: any) { return api.put('/user/password', d) }
}

export const questionApi = {
  list(p?: any) { return api.get('/questions', { params: p }) },
  getById(id: number) { return api.get(`/questions/${id}`) },
  getRecommended(uid: number) { return api.get('/questions/recommended', { params: { userId: uid } }) }
}

export const submissionApi = {
  submit(d: any) { return api.post('/submissions', d) },
  getMySubmissions() { return api.get('/submissions/my') },
  getWrong() { return api.get('/submissions/wrong') },
  getMyQuestionSubmissions(qid: number) { return api.get(`/submissions/my/question/${qid}`) },
  getStats() { return api.get('/submissions/stats') }
}

export const statsApi = { getOverview() { return api.get('/stats/overview') } }

export const adminApi = {
  generateQuestion(d: any) { return api.post('/admin/ai/generate-question', d) },
  addQuestion(d: any) { return api.post('/admin/questions', d) },
  updateQuestion(id: number, d: any) { return api.put(`/admin/questions/${id}`, d) },
  deleteQuestion(id: number) { return api.delete(`/admin/questions/${id}`) },
  getPendingReview() { return api.get('/admin/questions/pending-review') },
  approveQuestion(id: number) { return api.put(`/admin/questions/${id}/approve`) },
  rejectQuestion(id: number) { return api.put(`/admin/questions/${id}/reject`) },
  getAllSubmissions() { return api.get('/admin/submissions') },
  listUsers() { return api.get('/admin/users') },
  addUser(d: any) { return api.post('/admin/users', d) },
  updateUser(id: number, d: any) { return api.put(`/admin/users/${id}`, d) },
  deleteUser(id: number) { return api.delete(`/admin/users/${id}`) },
  listAchievements() { return api.get('/admin/achievements') },
  addAchievement(d: any) { return api.post('/admin/achievements', d) },
  updateAchievement(id: number, d: any) { return api.put(`/admin/achievements/${id}`, d) },
  deleteAchievement(id: number) { return api.delete(`/admin/achievements/${id}`) }
}

export const checkinApi = { get() { return api.get('/checkin') } }
export const achievementApi = { list() { return api.get('/achievements') } }
export default api

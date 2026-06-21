# AI驱动的编程练习助手

基于 Spring Boot 3 + Vue 3 + MySQL 的 web 应用，提供在线编程练习、AI 代码诊断、个性化推荐等功能。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + TypeScript + Vite + Pinia + Vue Router |
| 后端 | Spring Boot 3.2 + MyBatis-Plus 3.5 + Maven |
| 数据库 | MySQL 8.0 |
| AI | DeepSeek API（可选，默认使用模拟模式） |

---

## 快速开始

### 0. 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 20+
- MySQL 8.0+

### 1. 初始化数据库

执行数据库建表脚本：

```bash
mysql -u root -p < backend/src/main/resources/sql/init.sql
```

详细说明见 [数据库说明文档](backend/src/main/resources/sql/README.md)

### 2. 配置数据库连接

编辑 `backend/src/main/resources/application.properties`，把密码改成你的：

```properties
spring.datasource.password=你的MySQL密码
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端将在 `http://localhost:8080` 启动。

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端将在 `http://localhost:5173` 启动（默认 Vite 端口）。

### 5. 访问系统

打开浏览器访问 `http://localhost:5173`

**预置管理员账号：** `admin` / `admin123`

---

## 项目结构

```
coding-assistant/
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/com/example/backend/
│   │   ├── BackendApplication.java       # 启动类
│   │   ├── config/                       # 配置（跨域、拦截器、MyBatis-Plus）
│   │   ├── controller/                   # REST 控制器
│   │   │   ├── UserController.java       # 用户注册/登录/资料
│   │   │   ├── QuestionController.java   # 题目列表/推荐/审核
│   │   │   ├── SubmissionController.java # 代码提交/判题/历史
│   │   │   ├── LearningStatsController.java  # 学习统计
│   │   │   └── AdminController.java      # 管理员：题目CRUD/AI出题
│   │   ├── service/                      # 业务逻辑层
│   │   │   └── impl/                     # 实现类
│   │   │       ├── UserServiceImpl.java
│   │   │       ├── QuestionServiceImpl.java   # 含推荐算法
│   │   │       ├── SubmissionServiceImpl.java # 含判题核心
│   │   │       ├── LearningStatsServiceImpl.java
│   │   │       └── AiServiceImpl.java         # DeepSeek API/模拟
│   │   ├── mapper/                       # MyBatis-Plus Mapper
│   │   ├── entity/                       # 实体类
│   │   └── dto/                          # 数据传输对象
│   └── src/main/resources/
│       ├── application.properties        # 应用配置
│       └── sql/init.sql                  # 数据库建表脚本
├── frontend/                         # Vue 3 前端
│   └── src/
│       ├── api/index.ts                  # Axios 封装
│       ├── stores/auth.ts                # Pinia 认证状态
│       ├── router/index.ts               # 路由配置
│       └── views/                        # 页面组件
│           ├── HomePage.vue              # 首页（概览+推荐）
│           ├── LoginPage.vue             # 登录
│           ├── RegisterPage.vue          # 注册
│           ├── QuestionBankPage.vue      # 题库
│           ├── PracticePage.vue          # 编程练习（含判题）
│           ├── StatsPage.vue             # 学习统计
│           ├── ProfilePage.vue           # 个人中心
│           └── admin/
│               ├── QuestionManagePage.vue # 题目管理CRUD
│               ├── AiGeneratePage.vue    # AI辅助出题
│               └── ReviewPage.vue        # 待审核题目
└── README.md
```

---

## API 接口一览

### 用户模块

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/user/register` | 注册 |
| POST | `/api/user/login` | 登录 |
| POST | `/api/user/logout` | 登出 |
| GET | `/api/user/profile` | 获取个人信息 |
| PUT | `/api/user/profile` | 更新个人信息 |

### 题目模块

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/questions` | 题目列表（支持筛选） |
| GET | `/api/questions/{id}` | 题目详情 |
| GET | `/api/questions/recommended?userId={id}` | 个性化推荐 |
| GET | `/api/questions/pending-review` | 待审核列表（管理员） |
| PUT | `/api/questions/{id}/approve` | 审核通过 |
| PUT | `/api/questions/{id}/reject` | 驳回 |

### 提交判题

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/submissions` | 提交代码（含判题+AI诊断） |
| GET | `/api/submissions/my` | 我的提交历史 |
| GET | `/api/submissions/stats` | 提交统计 |
| GET | `/api/submissions/my/question/{qid}` | 某题的提交记录 |

### 学习统计

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/stats/overview` | 学习概况（含知识点掌握度） |

### 管理员

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/admin/ai/generate-question` | AI 生成题目 |
| POST | `/api/admin/questions` | 手动录入题目 |
| PUT | `/api/admin/questions/{id}` | 编辑题目 |
| DELETE | `/api/admin/questions/{id}` | 删除题目 |

---

## AI 功能配置

项目默认使用**模拟模式**（不依赖外部 API），可正常演示所有功能。

如需真实 AI 诊断和出题，配置 DeepSeek API Key：

```properties
# application.properties
ai.api.key=sk-your-deepseek-api-key-here
ai.api.url=https://api.deepseek.com/v1/chat/completions
ai.api.model=deepseek-chat
```

---

## 数据库设计

### E-R 图（简要）

- **user** (1) ──→ (N) **submission**: 一个用户有多次提交
- **question** (1) ──→ (N) **submission**: 一道题有多次提交
- **user** (1) ──→ (N) **learning_stats**: 一个用户有多个知识点统计

### 核心表说明

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `user` | 用户表 | username, password(BCrypt), role, ability_level |
| `question` | 题目表 | title, difficulty, knowledge_point, sample_input/output, review_status |
| `submission` | 提交记录表 | user_id, question_id, code, result, score, ai_diagnosis |
| `learning_stats` | 学习统计表 | user_id, knowledge_point, total/correct/wrong_count, accuracy |

---

## 功能演示路径

1. **普通用户**：注册 → 登录 → 首页看推荐 → 进题库选题目 → 写代码 → 提交看结果和AI诊断 → 学习统计看知识点掌握度
2. **管理员**：登录（admin/admin123）→ 后台管理 → 手动新增题目/AI辅助出题 → 审核题目

# AI驱动的编程练习助手

基于 Spring Boot 3 + Vue 3 + MySQL 的 web 应用，提供在线编程练习、AI 代码诊断、个性化推荐等功能。

## 技术栈

| 层级 | 技术                                             |
|------|------------------------------------------------|
| 前端 | Vue 3 + TypeScript + Vite + Pinia + Vue Router |
| 后端 | Spring Boot 3.2 + MyBatis-Plus 3.5 + Maven     |
| 数据库 | MySQL 8.0                                      |
| AI | glm-4-plus（可选）          |

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

**预置管理员账号：** `admin` 
**密码：** `admin123` 
**预置普通用户账号：** `头斯特` 
**密码：** `123qwe` 


---

## AI 功能配置

项目默认使用glm-4-plus模型，如需切换其他模型，请配置 API Key：

```properties
# application.properties
ai.api.key=sk-your-api-key-here
ai.api.url=https://xxxxxxxxxxxx
ai.api.model=xxxxx-xxxx
```
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


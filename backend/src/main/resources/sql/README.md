# 数据库使用说明

## 前提条件

- 已安装 MySQL 8.0+
- MySQL 服务正在运行（端口 3306）

## 第一步：创建数据库和表

### 方式一：命令行执行

打开终端，进入本目录：

```bash
# 登录 MySQL
mysql -u root -p

# 执行建表脚本
source /path/to/init.sql;
```

### 方式二：使用 MySQL Workbench 等图形化工具

1. 打开 `init.sql` 文件
2. 全选（Ctrl+A）并执行（Ctrl+Enter 或点击闪电按钮）

## 第二步：配置数据库连接

打开 `backend/src/main/resources/application.properties`：

```properties
# 将用户名和密码改为你的 MySQL 配置
spring.datasource.username=root
spring.datasource.password=你的MySQL密码
```

## 执行后生成的内容

执行 `init.sql` 后会自动：
1. 创建数据库 `coding_assistant`
2. 创建 4 张表：`user`、`question`、`submission`、`learning_stats`
3. 预置 1 个管理员账号和 15 道编程题（覆盖简单/中等/困难三级难度）

### 预置管理员账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |

### 预置题目一览

| 标题 | 难度 | 知识点 |
|------|------|--------|
| 两数之和 | 简单 | 基础语法 |
| 判断奇偶 | 简单 | 基础语法 |
| 打印乘法表 | 简单 | 循环结构 |
| 求最大值 | 简单 | 条件判断 |
| 字符串反转 | 简单 | 字符串处理 |
| 斐波那契数列 | 中等 | 递归与递推 |
| 回文数判断 | 中等 | 字符串处理 |
| 冒泡排序 | 中等 | 排序算法 |
| 数组去重 | 中等 | 数组操作 |
| 二分查找 | 中等 | 查找算法 |
| 最长公共子序列 | 困难 | 动态规划 |
| 0-1背包问题 | 困难 | 动态规划 |
| 拓扑排序 | 困难 | 图算法 |
| 最小生成树 | 困难 | 图算法 |

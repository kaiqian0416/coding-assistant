-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: coding_assistant
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `achievement`
--

DROP TABLE IF EXISTS `achievement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `achievement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '成就ID',
  `name` varchar(100) NOT NULL COMMENT '成就名称',
  `description` varchar(255) DEFAULT NULL COMMENT '成就描述',
  `icon` varchar(20) DEFAULT 0xF09F8F86 COMMENT '成就图标',
  `condition_type` varchar(50) NOT NULL COMMENT '条件类型: total_accepted-通过数, total_wrong-错误数, total_submit-总提交数',
  `condition_value` int NOT NULL COMMENT '条件阈值',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_condition` (`condition_type`,`condition_value`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='成就定义表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `achievement`
--

LOCK TABLES `achievement` WRITE;
/*!40000 ALTER TABLE `achievement` DISABLE KEYS */;
INSERT INTO `achievement` VALUES (1,'第一步','完成第一次提交','?','total_submit',1,1,'2026-06-21 19:06:22'),(2,'首胜','第一次通过题目','?','total_accepted',1,2,'2026-06-21 19:06:22'),(3,'小试牛刀','通过5道题','⭐','total_accepted',5,3,'2026-06-21 19:06:22'),(4,'渐入佳境','通过10道题','?','total_accepted',10,4,'2026-06-21 19:06:22'),(5,'题库探险家','通过25道题','?','total_accepted',25,5,'2026-06-21 19:06:22'),(6,'越挫越勇','做错5道题','?','total_wrong',5,6,'2026-06-21 19:06:22'),(7,'百折不挠','做错10道题','?','total_wrong',10,7,'2026-06-21 19:06:22'),(8,'签到达人','连续打卡7天','?','consecutive_checkin',7,4,'2026-06-21 23:18:57');
/*!40000 ALTER TABLE `achievement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `learning_stats`
--

DROP TABLE IF EXISTS `learning_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `learning_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `knowledge_point` varchar(100) NOT NULL COMMENT '知识点',
  `total_count` int DEFAULT '0' COMMENT '总做题数',
  `correct_count` int DEFAULT '0' COMMENT '正确数',
  `wrong_count` int DEFAULT '0' COMMENT '错误数',
  `accuracy` decimal(5,2) DEFAULT '0.00' COMMENT '正确率(%)',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_knowledge` (`user_id`,`knowledge_point`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学习统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `learning_stats`
--

LOCK TABLES `learning_stats` WRITE;
/*!40000 ALTER TABLE `learning_stats` DISABLE KEYS */;
INSERT INTO `learning_stats` VALUES (1,1,'基础语法',21,8,13,38.10,'2026-06-21 15:19:18'),(2,2,'图算法',4,1,3,25.00,'2026-06-21 16:43:07'),(3,7,'基础语法',5,3,2,60.00,'2026-06-21 21:45:01'),(4,7,'字符串处理',1,0,1,0.00,'2026-06-21 23:24:44');
/*!40000 ALTER TABLE `learning_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `title` varchar(200) NOT NULL COMMENT '题目标题',
  `description` text NOT NULL COMMENT '题目描述',
  `difficulty` varchar(10) NOT NULL DEFAULT 'easy' COMMENT '难度: easy-简单, medium-中等, hard-困难',
  `knowledge_point` varchar(100) NOT NULL COMMENT '知识点分类',
  `input_description` varchar(500) DEFAULT NULL COMMENT '输入描述',
  `output_description` varchar(500) DEFAULT NULL COMMENT '输出描述',
  `sample_input` text COMMENT '样例输入',
  `sample_output` text COMMENT '样例输出',
  `reference_code` text COMMENT '参考答案代码',
  `source` varchar(20) DEFAULT 'manual' COMMENT '来源: manual-手工录入, ai_generated-AI生成',
  `review_status` varchar(20) DEFAULT 'approved' COMMENT '审核状态: pending-待审核, approved-已通过, rejected-已驳回',
  `created_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_difficulty` (`difficulty`),
  KEY `idx_knowledge_point` (`knowledge_point`),
  KEY `idx_review_status` (`review_status`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='题目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES (1,'两数之和','编写一个程序，从标准输入读取两个整数，计算它们的和并输出。','easy','基础语法','一行两个整数 a 和 b，以空格分隔。','输出 a + b 的值。','3 5','8','a, b = map(int, input().split())\nprint(a + b)','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(2,'判断奇偶','输入一个整数，判断它是奇数还是偶数。如果是偶数输出\"even\"，否则输出\"odd\"。','easy','基础语法','一个整数 n。','输出 \"even\" 或 \"odd\"。','4','even','n = int(input())\nprint(\"even\" if n % 2 == 0 else \"odd\")','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(3,'打印乘法表','输入一个整数 n，输出 n 以内的九九乘法表片段（n × n 的乘法表）。','easy','循环结构','一个整数 n (1 ≤ n ≤ 9)。','输出 n 行，每行 i 个算式，格式为 \"j*i=积\"，算式之间用制表符(\\t)分隔。','3','1*1=1\n1*2=2	2*2=4\n1*3=3	2*3=6	3*3=9','n = int(input())\nfor i in range(1, n + 1):\n    line = \"\\t\".join(f\"{j}*{i}={i*j}\" for j in range(1, i + 1))\n    print(line)','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(4,'求最大值','输入三个整数，输出其中最大的数。','easy','条件判断','一行三个整数 a b c，以空格分隔。','输出三个数中的最大值。','10 25 7','25','a, b, c = map(int, input().split())\nprint(max(a, b, c))','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(5,'字符串反转','输入一个字符串，将其反转后输出。','easy','字符串处理','一行字符串 s。','输出反转后的字符串。','hello','olleh','s = input()\nprint(s[::-1])','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(6,'斐波那契数列','斐波那契数列的定义为：F(1)=1, F(2)=1, F(n)=F(n-1)+F(n-2) (n≥3)。输入 n，输出第 n 项的值。','medium','递归与递推','一个正整数 n (1 ≤ n ≤ 45)。','输出斐波那契数列的第 n 项。','10','55','n = int(input())\nf = [0] * (n + 1)\nf[1] = 1\nif n >= 2:\n    f[2] = 1\nfor i in range(3, n + 1):\n    f[i] = f[i-1] + f[i-2]\nprint(f[n])','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(7,'回文数判断','输入一个整数，判断它是否为回文数（正着读和倒着读一样）。','medium','字符串处理','一个整数 n。','如果是回文数输出 \"true\"，否则输出 \"false\"。','121','true','s = input().strip()\nprint(\"true\" if s == s[::-1] else \"false\")','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(8,'冒泡排序','输入一个长度为 n 的整数数组，使用冒泡排序将其按升序排列并输出。','medium','排序算法','第一行一个整数 n (1 ≤ n ≤ 100)；第二行 n 个整数。','输出排序后的数组，元素之间用空格分隔。','5\n3 1 4 1 5','1 1 3 4 5','n = int(input())\narr = list(map(int, input().split()))\nfor i in range(n - 1):\n    for j in range(n - 1 - i):\n        if arr[j] > arr[j + 1]:\n            arr[j], arr[j + 1] = arr[j + 1], arr[j]\nprint(\" \".join(map(str, arr)))','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(9,'数组去重','给定一个整数数组，去除其中重复的元素，并按升序输出。','medium','数组操作','第一行一个整数 n；第二行 n 个整数。','输出去重并排序后的数组，元素之间用空格分隔。','6\n4 2 4 1 3 2','1 2 3 4','n = int(input())\narr = list(map(int, input().split()))\nresult = sorted(set(arr))\nprint(\" \".join(map(str, result)))','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(10,'二分查找','给定一个升序排列的整数数组和一个目标值，使用二分查找判断目标值是否在数组中。','medium','查找算法','第一行两个整数 n 和 target；第二行 n 个升序整数。','如果存在输出 \"true\"，否则输出 \"false\"。','5 3\n1 2 3 4 5','true','n, target = map(int, input().split())\narr = list(map(int, input().split()))\nleft, right = 0, n - 1\nfound = False\nwhile left <= right:\n    mid = (left + right) // 2\n    if arr[mid] == target:\n        found = True\n        break\n    elif arr[mid] < target:\n        left = mid + 1\n    else:\n        right = mid - 1\nprint(str(found).lower())','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(11,'最长公共子序列','给定两个字符串，求它们的最长公共子序列(LCS)的长度。','hard','动态规划','两行，每行一个字符串 s1 和 s2。','输出 LCS 的长度。','abcde\nace','3','a = input().strip()\nb = input().strip()\nm, n = len(a), len(b)\ndp = [[0] * (n + 1) for _ in range(m + 1)]\nfor i in range(1, m + 1):\n    for j in range(1, n + 1):\n        if a[i-1] == b[j-1]:\n            dp[i][j] = dp[i-1][j-1] + 1\n        else:\n            dp[i][j] = max(dp[i-1][j], dp[i][j-1])\nprint(dp[m][n])','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(12,'0-1背包问题','给定 n 个物品，每个物品有重量 w[i] 和价值 v[i]。背包容量为 W，求能装入的最大价值。','hard','动态规划','第一行两个整数 n 和 W；第二行 n 个整数表示重量；第三行 n 个整数表示价值。','输出最大总价值。','4 5\n2 1 3 2\n3 2 4 2','7','n, W = map(int, input().split())\nw = list(map(int, input().split()))\nv = list(map(int, input().split()))\ndp = [0] * (W + 1)\nfor i in range(n):\n    for j in range(W, w[i] - 1, -1):\n        dp[j] = max(dp[j], dp[j - w[i]] + v[i])\nprint(dp[W])','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(13,'拓扑排序','给定一个有向无环图(DAG)，输出其拓扑排序结果。','hard','图算法','第一行两个整数 n(顶点数) 和 m(边数)；接下来 m 行每行两个整数 u v 表示 u→v 的有向边。','输出一行拓扑排序结果（节点编号 1~n）。若有多个结果，输出任意一个。','4 3\n1 2\n2 3\n1 3','1 2 3 4','from collections import deque\n\nn, m = map(int, input().split())\ng = [[] for _ in range(n + 1)]\nindeg = [0] * (n + 1)\nfor _ in range(m):\n    u, v = map(int, input().split())\n    g[u].append(v)\n    indeg[v] += 1\nq = deque([i for i in range(1, n + 1) if indeg[i] == 0])\nres = []\nwhile q:\n    u = q.popleft()\n    res.append(str(u))\n    for v in g[u]:\n        indeg[v] -= 1\n        if indeg[v] == 0:\n            q.append(v)\nprint(\" \".join(res))','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(14,'最小生成树','给定一个带权无向图，使用Kruskal算法求最小生成树的总权重。','hard','图算法','第一行两个整数 n(顶点数) 和 m(边数)；接下来 m 行每行三个整数 u v w 表示 u-v 边权重 w。','输出最小生成树的总权重。若图不连通输出 -1。','4 5\n1 2 1\n1 3 2\n1 4 3\n2 3 2\n3 4 4','6','def find(x):\n    if parent[x] != x:\n        parent[x] = find(parent[x])\n    return parent[x]\n\ndef union(a, b):\n    parent[find(a)] = find(b)\n\nn, m = map(int, input().split())\nedges = []\nfor _ in range(m):\n    u, v, w = map(int, input().split())\n    edges.append((w, u, v))\nedges.sort()\nparent = list(range(n + 1))\ntotal = cnt = 0\nfor w, u, v in edges:\n    if find(u) != find(v):\n        union(u, v)\n        total += w\n        cnt += 1\nprint(total if cnt == n - 1 else -1)','manual','approved',NULL,'2026-06-21 15:18:15','2026-06-21 15:18:15'),(15,'图的最短路径','','medium','图算法','','','','','','ai_generated','rejected',1,'2026-06-21 16:23:06','2026-06-21 16:23:06'),(16,'图的最短路径','{\n    \"title\":','medium','图算法',NULL,NULL,NULL,NULL,NULL,'ai_generated','rejected',1,'2026-06-21 16:24:37','2026-06-21 16:24:37'),(17,'图的最短路径','','medium','图算法','','','','','','ai_generated','rejected',1,'2026-06-21 16:24:53','2026-06-21 16:24:53'),(18,'图的最短路径','','medium','图算法','','','','','','ai_generated','rejected',1,'2026-06-21 16:29:05','2026-06-21 16:29:05'),(19,'图的最短路径','','medium','图算法','','','','','','ai_generated','rejected',1,'2026-06-21 16:33:52','2026-06-21 16:33:52'),(20,'图的最短路径','','medium','图算法','','','','','','ai_generated','rejected',1,'2026-06-21 16:35:55','2026-06-21 16:35:55'),(21,'Dijkstra算法求最短路径','给定一个有向加权图和起点终点，使用Dijkstra算法求起点到终点的最短路径长度。如果终点不可达，返回-1。','medium','图算法','第一行包含三个整数n, m, s，分别表示节点数、边数和起点编号（节点编号从1到n）。接下来m行，每行三个整数u, v, w，表示从u到有一条权值为w的有向边。最后一行包含一个整数t，表示终点编号。','输出一个整数，表示起点s到终点t的最短路径长度。如果不可达，输出-1。','4 5 1\n1 2 2\n1 3 5\n2 3 1\n2 4 3\n3 4 2\n4','5','import heapq\n\ndef dijkstra(n, edges, s, t):\n    graph = [[] for _ in range(n+1)]\n    for u, v, w in edges:\n        graph[u].append((v, w))\n    \n    dist = [float(\'inf\')] * (n+1)\n    dist[s] = 0\n    heap = [(0, s)]\n    \n    while heap:\n        d, u = heapq.heappop(heap)\n        if d != dist[u]:\n            continue\n        if u == t:\n            return d\n        for v, w in graph[u]:\n            if dist[v] > dist[u] + w:\n                dist[v] = dist[u] + w\n                heapq.heappush(heap, (dist[v], v))\n    return -1\n\nn, m, s = map(int, input().split())\nedges = []\nfor _ in range(m):\n    u, v, w = map(int, input().split())\n    edges.append((u, v, w))\nt = int(input())\nprint(dijkstra(n, edges, s, t))','ai_generated','approved',1,'2026-06-21 16:41:41','2026-06-21 16:41:41'),(22,'递推实现卡特兰数计算','卡特兰数是一组在组合数学中经常出现的数列，其定义为：C(0) = 1，C(n) = sum(C(i)*C(n-1-i)) for i in range(n)。请使用递推方法计算第n个卡特兰数。','easy','递归与递推','一个整数n，表示要计算的卡特兰数的序号（0 <= n <= 30）。','返回第n个卡特兰数的值。','5','42','def catalan(n):\n    if n <= 1:\n        return 1\n    dp = [0] * (n + 1)\n    dp[0] = dp[1] = 1\n    for i in range(2, n + 1):\n        for j in range(i):\n            dp[i] += dp[j] * dp[i - 1 - j]\n    return dp[n]\n\nn = int(input())\nprint(catalan(n))','ai_generated','approved',1,'2026-06-21 22:58:12','2026-06-21 22:58:12'),(23,'数组分段求和','给定一个整数数组和两个整数k和m，将数组分成连续的k个元素为一组，计算每组的和，然后返回所有组和中第m大的值。如果组数不足k个，则返回-1。','medium','数组操作','输入包含一个整数列表nums，两个整数k和m。1 <= k <= len(nums) <= 1000, 1 <= m <= 数组分段后的组数','返回一个整数，表示所有组和中第m大的值。如果组数不足k个，返回-1。','[1, 2, 3, 4, 5], 2, 2','5','def segment_sum(nums, k, m):\n    n = len(nums)\n    if n < k:\n        return -1\n    segment_sums = []\n    for i in range(n - k + 1):\n        segment_sum = sum(nums[i:i+k])\n        segment_sums.append(segment_sum)\n    segment_sums.sort(reverse=True)\n    return segment_sums[m-1]','ai_generated','rejected',1,'2026-06-21 23:00:24','2026-06-21 23:00:24');
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `submission`
--

DROP TABLE IF EXISTS `submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `submission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `code` text NOT NULL COMMENT '用户提交的代码',
  `language` varchar(20) DEFAULT 'python' COMMENT '编程语言',
  `result` varchar(20) DEFAULT 'pending' COMMENT '判题结果: pending-待判, judging-判题中, accepted-通过, wrong_answer-答案错误, compile_error-编译错误, runtime_error-运行错误',
  `score` int DEFAULT '0' COMMENT '得分(0-100)',
  `error_info` text COMMENT '错误信息（编译/运行时错误详情）',
  `ai_diagnosis` text COMMENT 'AI诊断建议',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_result` (`result`),
  KEY `idx_user_question` (`user_id`,`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='提交记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `submission`
--

LOCK TABLES `submission` WRITE;
/*!40000 ALTER TABLE `submission` DISABLE KEYS */;
INSERT INTO `submission` VALUES (1,1,1,'# 在这里编写你的 Python 代码\nprint(8)','python','accepted',100,NULL,NULL,'2026-06-21 15:19:17'),(2,1,1,'# 在这里编写你的 Python 代码\na, b = map(int, input().split())\n\nprint(a + b)','python','accepted',100,NULL,NULL,'2026-06-21 15:20:15'),(3,1,1,'# 在这里编写你的 Python 代码\nprint(8)','python','wrong_answer',15,NULL,'【AI 代码审查】\n解题评分：10/100\n⚠️ 代码看起来只是打印了固定值，没有真正读取输入、处理数据。\n这可能是面向结果编程（输出和预期一致，但程序无法处理其他输入）。\n? 提示：使用 input() 读取输入，然后编写通用算法来处理数据。\n\n\n⚠️ 注意：代码只输出了固定答案，未真正实现算法！请用 input() 读取输入后编写通用解法。','2026-06-21 15:25:19'),(4,1,1,'# 读取输入的一行，按空格分割\na, b = map(int, input().split())\n\n# 计算两数之和并输出\nprint(a + b)','python','accepted',80,NULL,'【AI 代码审查】\n解题评分：80/100\n✅ 代码结构良好，正确读取了输入并进行了处理。\n? 输出匹配预期，解题思路正确！\n\n','2026-06-21 15:28:13'),(5,1,1,'line = input()          # 读取整行：\"3 5\"\nparts = line.split()    # 分割成列表：[\"3\", \"5\"]\na = int(parts[0])       # 第一个数：3\nb = int(parts[1])       # 第二个数：5\nresult = a + b          # 计算和：8\nprint(result)           # 输出：8','python','accepted',80,NULL,'【AI 代码审查】\n解题评分：80/100\n✅ 代码结构良好，正确读取了输入并进行了处理。\n? 输出匹配预期，解题思路正确！\n\n','2026-06-21 15:28:31'),(6,1,1,'a, b = input().split()\na = int(a)\nb = int(b)\nprint(a + b)','python','accepted',80,NULL,'【AI 代码审查】\n解题评分：80/100\n✅ 代码结构良好，正确读取了输入并进行了处理。\n? 输出匹配预期，解题思路正确！\n\n','2026-06-21 15:28:47'),(7,1,1,'a, b = map(int, input().split())\n\nprint(a + b)','python','accepted',80,NULL,'【AI 代码审查】\n解题评分：80/100\n✅ 代码结构良好，正确读取了输入并进行了处理。\n? 输出匹配预期，解题思路正确！\n\n','2026-06-21 15:29:13'),(8,1,1,'# 在这里编写你的 Python 代码\nprint(8)\n','python','wrong_answer',15,NULL,'【AI 代码审查】\n解题评分：10/100\n⚠️ 代码看起来只是打印了固定值，没有真正读取输入、处理数据。\n这可能是面向结果编程（输出和预期一致，但程序无法处理其他输入）。\n? 提示：使用 input() 读取输入，然后编写通用算法来处理数据。\n\n\n⚠️ 注意：代码只输出了固定答案，未真正实现算法！请用 input() 读取输入后编写通用解法。','2026-06-21 15:43:45'),(9,1,1,'# 在这里编写你的 Python 代码\nprint(\n','python','compile_error',0,'语法错误：请检查括号、引号是否匹配。','【错误诊断】\n【AI 诊断建议】\n\n? 错误分析：\n您的代码存在语法错误，请检查以下方面：\n1. 检查所有括号是否匹配（{}、()）\n2. 确保每条语句以分号(;)结尾\n3. 检查变量类型声明是否正确\n\n? 学习建议：\n• 本题涉及知识点：基础语法\n• 建议先理解题目中的算法逻辑，再动手编码\n• 可以尝试在本地 IDE 中调试运行\n\n\n【AI 代码审查】\n解题评分：20/100\n? 代码中没有检测到 input() 调用。\n请确认程序能读取不同的输入数据，而不是只处理固定值。\n\n','2026-06-21 15:47:09'),(10,1,1,'# 在这里编写你的 Python 代码\nprint(','python','compile_error',0,'语法错误：请检查括号、引号是否匹配。','【错误诊断】\n【AI 诊断建议】\n\n? 错误分析：\n您的代码存在语法错误，请检查以下方面：\n1. 检查所有括号是否匹配（{}、()）\n2. 确保每条语句以分号(;)结尾\n3. 检查变量类型声明是否正确\n\n? 学习建议：\n• 本题涉及知识点：基础语法\n• 建议先理解题目中的算法逻辑，再动手编码\n• 可以尝试在本地 IDE 中调试运行\n\n\n【AI 代码审查】\n解题评分：20/100\n? 代码中没有检测到 input() 调用。\n请确认程序能读取不同的输入数据，而不是只处理固定值。\n\n','2026-06-21 15:49:25'),(11,1,1,'# 在这里编写你的 Python 代码\nhai','python','wrong_answer',30,'期望输出：\n8\n\n你的输出：\n','【错误诊断】\n【AI 诊断建议】\n\n? 错误分析：\n您的代码运行结果与预期不符。建议：\n1. 仔细阅读题目要求，确认输出格式\n2. 使用题目给出的样例输入进行测试\n3. 检查边界条件是否处理正确\n\n? 学习建议：\n• 本题涉及知识点：基础语法\n• 建议先理解题目中的算法逻辑，再动手编码\n• 可以尝试在本地 IDE 中调试运行\n\n\n【AI 代码审查】\n解题评分：20/100\n? 代码中没有检测到 input() 调用。\n请确认程序能读取不同的输入数据，而不是只处理固定值。\n\n','2026-06-21 15:49:59'),(12,1,1,'gih','python','wrong_answer',30,'期望输出：\n8\n\n你的输出：\n','【错误诊断】\n【AI 诊断建议】\n\n? 错误分析：\n您的代码运行结果与预期不符。建议：\n1. 仔细阅读题目要求，确认输出格式\n2. 使用题目给出的样例输入进行测试\n3. 检查边界条件是否处理正确\n\n? 学习建议：\n• 本题涉及知识点：基础语法\n• 建议先理解题目中的算法逻辑，再动手编码\n• 可以尝试在本地 IDE 中调试运行\n\n\n【AI 代码审查】\n解题评分：20/100\n? 代码中没有检测到 input() 调用。\n请确认程序能读取不同的输入数据，而不是只处理固定值。\n\n','2026-06-21 15:50:09'),(13,1,1,'# 在这里编写你的 Python 代码\nprint(\n','python','compile_error',0,'语法错误：请检查括号、引号是否匹配。','【错误诊断】\n你好！作为你的编程导师，我很高兴能协助你解决这个编程问题。让我们一起来分析这段代码的错误，并学习如何正确地编写它。\n\n### 1. 错误原因分析\n\n你遇到的错误是 **语法错误**。\n具体来说，在你的代码 `print(` 中，开启了一个左括号 `(`，但是**没有闭合它**（缺少右括号 `)`）。Python 解释器在读取到文件末尾时，发现括号没有匹配完成，就会报出类似 `SyntaxError: unexpected EOF while parsing` 的错误。\n\n此外，从逻辑上看，代码目前还没有实现“读取两个整数”和“计算它们的和”的功能，仅仅写了一个未完成的 `print` 语句。\n\n---\n\n### 2. 修复建议\n\n要完成这个题目，我们需要三个步骤：\n1. **读取输入**：使用 `input()` 函数读取标准输入。\n2. **类型转换**：因为 `input()` 读取的内容默认是字符串，我们需要用 `int()` 将其转换为整数。\n3. **计算并输出**：将两个整数相加，并通过 `print()` 完整地输出结果。\n\n假设题目要求输入的两个整数在同一行，且用空格隔开（这是算法题最常见的格式），修复后的代码如下：\n\n```python\n# 读取一行输入，按空格分割，并分别转换为整数 a 和 b\na, b = map(int, input().split())\n\n# 计算它们的和并输出（注意这里的括号是闭合的）\nprint(a + b)\n```\n\n**补充情况：** 如果题目要求两个整数分两行输入，代码应该这样写：\n```python\n# 读取第一行并转换为整数\na = int(input())\n# 读取第二行并转换为整数\nb = int(input())\n\n# 计算并输出\nprint(a + b)\n```\n\n---\n\n### 3. 相关的知识点说明\n\n为了让你以后能更自信地写代码，这里复习几个关键知识点：\n\n*   **括号匹配原则**：\n    在 Python 中，无论是小括号 `()`、中括号 `[]` 还是大括号 `{}`，亦或是引号 `\'` `\"`，都必须成对出现。一旦开启，就必须在合适的位置闭合。写代码时养成随时闭合括号的习惯可以避免这类错误。\n*   **`input()` 函数**：\n    用于接收控制台的输入。它会把用户输入的一整行内容作为**字符串**返回。\n*   **`split()` 方法**：\n    字符串的方法，默认按空白字符（空格、Tab、换行等）将字符串分割成一个列表。例如 `\"3 5\".split()` 会变成 `[\'3\', \'5\']`。\n*   **`map()` 函数**：\n    `map(函数, 可迭代对象)` 会将传入的函数依次作用于可迭代对象的每一个元素上。`map(int, input().split())` 的作用就是将输入分割出的每一个字符串元素都转换为 `int` 类型。\n*   **变量解包**：\n    `a, b = ...` 这种写法可以将一个包含两个元素的序列分别赋值给变量 `a` 和 `b`。\n\n编程是一个不断试错和修改的过程，遇到语法错误非常正常。仔细检查报错信息，通常都能快速定位问题。继续加油，你可以的！如果有其他问题，随时问我。\n\n【AI 代码审查】\n解题评分：50/100\nAI 审查分析完成。\n','2026-06-21 15:51:49'),(14,1,1,'# 在这里编写你的 Python 代码\n','python','wrong_answer',15,'期望输出：\n8\n\n实际输出：\n','【错误诊断】\n你好！作为你的编程导师，我很高兴为你解答这个问题。\n\n### 1. 错误原因分析\n\n从你提供的代码来看，**你的代码文件中只有一行注释，并没有编写任何实际的 Python 代码**。\n\n因为程序体内没有任何指令，所以运行该代码时，计算机什么也不会做：它没有去读取输入，没有进行加法计算，也没有输出结果。这就是为什么“实际输出”为空，而系统期望你输出 `8` 的原因。\n\n### 2. 修复建议\n\n要解决这个问题，你需要按照题目要求，编写读取输入、计算和输出的代码。\n\n通常，在线编程平台读取两个整数有两种常见的输入格式，你可以根据平台的具体要求选择其中一种：\n\n**情况 A：两行输入（每行一个整数）**\n```python\n# 读取两行输入，并将其转换为整数\nnum1 = int(input())\nnum2 = int(input())\n\n# 计算它们的和\nresult = num1 + num2\n\n# 输出结果\nprint(result)\n```\n\n**情况 B：一行输入（两个整数在同一行，用空格隔开）**\n```python\n# 读取一行输入，按空格分割，并分别转换为整数\nnum1, num2 = map(int, input().split())\n\n# 计算它们的和\nresult = num1 + num2\n\n# 输出结果\nprint(result)\n```\n\n### 3. 相关的知识点说明\n\n为了帮助你更好地理解上面的代码，这里为你梳理几个关键的 Python 基础知识点：\n\n*   **`input()` 函数**：\n    *   用于暂停程序，等待用户从键盘输入一行文本。\n    *   **注意**：`input()` 读取到的内容默认是**字符串类型**，即使你输入的是数字。\n*   **`int()` 函数（类型转换）**：\n    *   因为 `input()` 得到的是字符串，比如 `\"3\"`，而字符串相加 `\"3\" + \"5\"` 的结果是 `\"35\"`（这叫字符串拼接），而不是数学上的 `8`。\n    *   所以我们需要用 `int()` 将字符串强制转换为整型，这样 `3 + 5` 才会等于 `8`。\n*   **`map()` 和 `split()`（针对情况 B）**：\n    *   `split()`：将一个字符串按空格切分成列表。例如 `\"3 5\"` 会被切分成 `[\'3\', \'5\']`。\n    *   `map(int, ...)`：对列表中的每一个元素执行 `int()` 转换操作。\n*   **变量赋值**：\n    *   `num1 = ...` 是把等号右边计算出的结果存放到名为 `num1` 的变量中，方便后续使用。\n*   **`print()` 函数**：\n    *   用于将括号里的内容打印输出到屏幕（标准输出）上，这样评测系统就能看到你的结果了。\n\n**下一步建议**：你可以把修复后的代码复制到编辑器中，尝试运行一下，输入两个数字看看是否能正确输出它们的和。如果有任何不明白的地方，随时问我！\n\n【AI 代码审查】\n解题评分：0/100\n代码为空，仅包含注释，未实现任何输入读取或计算逻辑。\n✅ 优点：无\n? 建议：代码完全为空，未读取输入，未计算两数之和，未输出结果。\n\n⚠️ 注意：代码只输出了固定答案，未真正实现算法！请用 input() 读取输入后编写通用解法。','2026-06-21 15:52:22'),(15,1,1,'# 在这里编写你的 Python 代码\nprint(','python','compile_error',0,'语法错误：请检查括号、引号是否匹配。','【错误诊断】\n问题出在第2行代码 `print(`，这里只有左括号且缺少要打印的内容。你需要补全 `print` 函数的右括号，并在括号内填入实际要\n\n【AI 代码审查】\n解题评分：50/100\nAI 审查分析完成。\n','2026-06-21 15:57:18'),(16,1,1,'# 在这里编写你的 Python 代码\nprint(','python','compile_error',0,'语法错误：请检查括号、引号是否匹配。','【错误诊断】\n问题出在第2行代码，`print` 函数只有左括号，缺少匹配的右括号。你需要将这行代码修改为 `print()`，或者在括号内补充需要打印的内容后再闭合。例如，如果只是占位，直接改成 `print()` 即可；如果需要输出结果，请改成 `print(结果变量)`。请补全代码并确保括号成对出现。\n\n【AI 代码审查】\n代码不完整，存在语法错误，无法执行。\n⚠️ 注意：代码只输出了固定答案，未真正实现算法！请用 input() 读取输入后编写通用解法。','2026-06-21 16:00:38'),(17,1,1,'# 在这里编写你的 Python 代码\nprint(8)','python','accepted',100,NULL,'【AI 代码审查】\nAI 审查分析完成。\n','2026-06-21 16:01:18'),(18,1,1,'# 在这里编写你的 Python 代码\nprint(8)','python','wrong_answer',15,NULL,'【AI 代码审查】\n代码仅打印固定数字8，完全未实现两数之和的逻辑，属于硬编码。\n⚠️ 注意：代码只输出了固定答案，未真正实现算法！请用 input() 读取输入后编写通用解法。','2026-06-21 16:05:17'),(19,1,1,'# 在这里编写你的 Python 代码\nprint(','python','compile_error',0,'语法错误：请检查括号、引号是否匹配。','【错误诊断】\n问题出在第2行，`print` 函数的左括号 `(` 缺少对应的右括号 `)` 闭合。同时，`print` 内部为空，没有实现题目要求的“两数之和”功能。你需要补全右括号并传入两个数相加的表达式，例如修改为 `print(a + b)`。\n\n【AI 代码审查】\n代码不完整，仅有一个未闭合的print语句，没有任何解题逻辑\n⚠️ 注意：代码只输出了固定答案，未真正实现算法！请用 input() 读取输入后编写通用解法。','2026-06-21 16:05:37'),(20,1,1,'# 在这里编写你的 Python 代码\nprint(8)','python','wrong_answer',15,NULL,'【AI 代码审查】\n代码仅打印固定数字8，未读取输入也未实现两数之和的查找逻辑，属于硬编码。\n⚠️ 注意：代码只输出了固定答案，未真正实现算法！请用 input() 读取输入后编写通用解法。','2026-06-21 16:06:28'),(21,1,1,'# 读取输入的一行，按空格分割\na, b = map(int, input().split())\n\n# 计算两数之和并输出\nprint(a + b)','python','accepted',100,NULL,'【AI 代码审查】\n代码正确读取输入并计算了两数之和，是通用且正确的解法，非硬编码。\n','2026-06-21 16:07:01'),(22,1,1,'# 在这里编写你的 Python 代码\nprint(','python','compile_error',0,'语法错误：请检查括号、引号是否匹配。','【错误诊断】\n问题出在第2行，`print(` 后面缺少了闭合的右括号以及要打印的内容。你需要补充完整的 `print()` 语句。例如，如果要打印变量 `result`，应修改为 `print(result)`。务必确保括号成对闭合。\n\n【AI 代码审查】\n代码不完整，仅有注释和未闭合的print语句，未实现任何解题逻辑\n','2026-06-21 16:09:24'),(23,2,21,'# 在这里编写你的 Python 代码\nimport heapq\nimport sys\n\ndef dijkstra(n, edges, s, t):\n    \"\"\"\n    使用Dijkstra算法求从s到t的最短路径\n    \n    Args:\n        n: 节点数\n        edges: 邻接表，edges[u] = [(v, w), ...]\n        s: 起点\n        t: 终点\n    \n    Returns:\n        int: 最短路径长度，不可达返回-1\n    \"\"\"\n    # 初始化距离数组，所有节点距离为无穷大\n    INF = float(\'inf\')\n    dist = [INF] * (n + 1)  # 节点编号从1开始\n    dist[s] = 0\n    \n    # 优先队列：(当前距离, 节点编号)\n    pq = [(0, s)]\n    \n    while pq:\n        # 弹出当前距离最小的节点\n        curr_dist, u = heapq.heappop(pq)\n        \n        # 如果当前节点是终点，提前返回（优化）\n        if u == t:\n            return curr_dist\n        \n        # 如果当前距离已经不是最短距离，跳过（避免重复处理）\n        if curr_dist > dist[u]:\n            continue\n        \n        # 遍历所有邻接边\n        for v, w in edges[u]:\n            new_dist = curr_dist + w\n            \n            # 如果找到更短的路径，更新并加入队列\n            if new_dist < dist[v]:\n                dist[v] = new_dist\n                heapq.heappush(pq, (new_dist, v))\n    \n    # 如果终点不可达\n    return -1 if dist[t] == INF else dist[t]\n\n\ndef main():\n    # 读取输入\n    data = sys.stdin.read().strip().split()\n    if not data:\n        return\n    \n    # 解析第一行\n    idx = 0\n    n = int(data[idx])\n    m = int(data[idx + 1])\n    s = int(data[idx + 2])\n    idx += 3\n    \n    # 构建邻接表\n    edges = [[] for _ in range(n + 1)]\n    for _ in range(m):\n        u = int(data[idx])\n        v = int(data[idx + 1])\n        w = int(data[idx + 2])\n        idx += 3\n        edges[u].append((v, w))\n    \n    # 读取终点\n    t = int(data[idx])\n    \n    # 调用Dijkstra算法\n    result = dijkstra(n, edges, s, t)\n    print(result)\n\n\nif __name__ == \"__main__\":\n    main()','python','accepted',100,NULL,'【AI 代码审查】\n完全正确的Dijkstra算法实现，正确处理了输入输出和最短路径计算。\n','2026-06-21 16:43:05'),(24,2,21,'import heapq\nimport sys\n\ndef dijkstra(n, edges, s, t):\n    # 错误1：距离数组初始化错误\n    dist = [float(\'inf\')] * n\n    dist[s] = 0\n    \n    pq = [(0, s)]\n    visited = []\n    \n    while pq:\n        curr_dist, u = heapq.heappop(pq)\n        \n        # 错误2：没有检查当前距离是否过期\n        \n        # 错误3：没有提前返回终点\n        \n        # 错误4：只记录visited，但不做任何判断\n        visited.append(u)\n        \n        for v, w in edges[u]:\n            # 错误5：使用错误的比较逻辑\n            if curr_dist + w >= dist[v]:\n                continue\n            dist[v] = curr_dist + w\n            heapq.heappush(pq, (dist[v], v))\n    \n    # 错误6：返回错误的值\n    return dist[t]\n\n\ndef main():\n    data = sys.stdin.read().strip().split()\n    if not data:\n        return\n    \n    idx = 0\n    n = int(data[idx])\n    m = int(data[idx + 1])\n    s = int(data[idx + 2])\n    idx += 3\n    \n    edges = [[] for _ in range(n)]\n    for _ in range(m):\n        u = int(data[idx]) - 1\n        v = int(data[idx + 1]) - 1\n        w = int(data[idx + 2])\n        idx += 3\n        # 错误7：错误的数据存储\n        edges[u].append((u, w))\n    \n    t = int(data[idx]) - 1\n    \n    result = dijkstra(n, edges, s, t)\n    print(result)\n\n\nif __name__ == \"__main__\":\n    main()','python','wrong_answer',30,'期望输出：\n5\n\n实际输出：\ninf','【错误诊断】\n1. 第28行：`edges[u].append((u, w))` 应改为 `edges[u].append((v, w))`，错误存储了顶点u而非v。  \n2. 第17行：`if curr_dist + w >= dist[v]:` 应改为 `if curr_dist + w > dist[v]:`，避免因等于时跳过最优解。  \n3. 第12行：添加 `if u == t: return dist[t]` 提前终止。  \n4. 第14行：`visited.append(u)` 无用，应删除。  \n5. 第9行：添加 `if curr_dist != dist[u]: continue` 跳过期节点。\n\n【AI 代码审查】\n代码存在多处严重错误，无法正确实现Dijkstra算法，仅为框架性代码。\n','2026-06-21 16:43:59'),(25,2,21,'import heapq\nimport sys\n\ndef dijkstra(n, edges, s, t):\n\n    dist = [float(\'inf\')] * n\n    dist[s] = 0\n    \n    pq = [(0, s)]\n    visited = []\n    \n    while pq:\n        curr_dist, u = heapq.heappop(pq)\n \n        visited.append(u)\n        \n        for v, w in edges[u]:\n\n            if curr_dist + w >= dist[v]:\n                continue\n            dist[v] = curr_dist + w\n            heapq.heappush(pq, (dist[v], v))\n    \n\n    return dist[t]\n\n\ndef main():\n    data = sys.stdin.read().strip().split()\n    if not data:\n        return\n    \n    idx = 0\n    n = int(data[idx])\n    m = int(data[idx + 1])\n    s = int(data[idx + 2])\n    idx += 3\n    \n    edges = [[] for _ in range(n)]\n    for _ in range(m):\n        u = int(data[idx]) - 1\n        v = int(data[idx + 1]) - 1\n        w = int(data[idx + 2])\n        idx += 3\n\n        edges[u].append((u, w))\n    \n    t = int(data[idx]) - 1\n    \n    result = dijkstra(n, edges, s, t)\n    print(result)\n\n\nif __name__ == \"__main__\":\n    main()','python','wrong_answer',30,'期望输出：\n5\n\n实际输出：\ninf','【错误诊断】\n问题在第29行，`edges[u].append((u, w))` 应改为 `edges[u].append((v, w))`，这里错误地将u作为邻接点加入，导致邻接表构建错误，无法正确到达目标节点t，因此输出inf。\n\n【AI 代码审查】\n实现了Dijkstra算法的基本逻辑，但存在小错误（如边添加时u和v混淆）和效率问题（未使用visited数组优化）。\n','2026-06-21 16:44:37'),(26,2,21,'import heapq\nimport sys\n\ndef dijkstra(n, edges, s, t):\n\n    dist = [float(\'inf\')] * n\n    dist[s] = 0\n    \n    pq = [(0, s)]\n    visited = []\n    \n    while pq:\n        curr_dist, u = heapq.heappop(pq)\n \n        visited.append(u)\n        \n        for v, w in edges[u]:\n\n            if curr_dist + w >= dist[v]:\n                continue\n            dist[v] = curr_dist + w\n            heapq.heappush(pq, (dist[v], v))\n    \n\n    return dist[t]\n\n\ndef main():\n    data = sys.stdin.read().strip().split()\n    if not data:\n        return\n    \n    idx = 0\n    n = int(data[idx])\n    m = int(data[idx + 1])\n    s = int(data[idx + 2])\n    idx += 3\n    \n    edges = [[] for _ in range(n)]\n    for _ in range(m):\n        u = int(data[idx]) - 1\n        v = int(data[idx + 1]) - 1\n        w = int(data[idx + 2])\n        idx += 3\n\n        edges[u].append((v, w))\n    \n    t = int(data[idx]) - 1\n    \n    result = dijkstra(n, edges, s, t)\n    print(result)\n\n\nif __name__ == \"__main__\":\n    main()','python','wrong_answer',30,'期望输出：\n5\n\n实际输出：\n3','【错误诊断】\n错误在第20行：`if curr_dist + w >= dist[v]:` 应改为 `if curr_dist + w >= dist[v] or v in visited:`。  \n或者更高效地，在第19行 `visited.append(u)` 后添加 `if curr_dist > dist[u]: continue` 来跳过过时节点。  \n推荐后者修改，避免重复处理已确定最短路径的节点。\n\n【AI 代码审查】\n实现了完整的Dijkstra算法，正确处理了输入输出和图结构。\n','2026-06-21 16:45:18'),(27,7,1,'# 在这里编写你的 Python 代码\na, b = map(int, input().split())\nprint(a + b)','python','accepted',100,NULL,'【AI 代码审查】\n正确实现了两数相加的核心逻辑，属于真正解题。\n','2026-06-21 21:44:58'),(28,7,1,'# 在这里编写你的 Python 代码\na, b = map(int, input().split())\nprint(a + b)','python','accepted',100,NULL,'【AI 代码审查】\n这是一个真正解题的代码，正确实现了两数相加的功能。\n','2026-06-21 21:45:13'),(29,7,1,'# 在这里编写你的 Python 代码\na, b = map(int, input().split())\nprint(a + b)','python','accepted',100,NULL,'【AI 代码审查】\n这是一个真正解决问题的通用代码，能够正确处理任意输入的两个整数并输出它们的和。\n','2026-06-21 21:45:48'),(30,7,1,'# 在这里编写你的 Python 代码\na, b = map(int, input().split())\nprint(','python','compile_error',0,'语法错误：请检查括号、引号是否匹配。','【错误诊断】\n第3行 `print(` 缺少闭合括号和参数，应改为 `print(a + b)`。\n\n【AI 代码审查】\n代码未完成，无法判断是否为真正解题\n','2026-06-21 21:46:28'),(31,7,1,'# 在这里编写你的 Python 代码\na, b = map(int, input().split())\nprint(8)','python','wrong_answer',15,NULL,'【AI 代码审查】\n代码完全硬编码，未处理输入，未真正解决问题\n⚠️ 注意：输出虽然正确，但代码像是直接打印固定值。请用 input() 读取输入后编写通用解法。','2026-06-21 21:46:42'),(32,7,5,'# 在这里编写你的 Python 代码\nasdf','python','wrong_answer',30,'期望输出：\nolleh\n\n你的输出：\n','【错误诊断】\n问题在第2行：`asdf` 不是有效的Python代码，且未实现字符串反转。  \n改为：`print(\'hello\'[::-1])`，用切片 `[::-1]` 反转字符串 \'hello\' 并输出。\n\n【AI 代码审查】\n代码未实现任何功能，仅为字符串占位符\n','2026-06-21 23:24:41');
/*!40000 ALTER TABLE `submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `role` varchar(10) DEFAULT 'user' COMMENT '角色: user-普通用户, admin-管理员',
  `ability_level` varchar(20) DEFAULT 'easy' COMMENT '能力等级: easy-简单, medium-中等, hard-困难',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','$2a$10$dFB61MjIBthQ/EpimY6GDu/73awVhyK47PlI4gbXRwihrbbh.HvKy','管理员',NULL,NULL,'admin','hard','2026-06-21 15:18:15','2026-06-21 15:18:15'),(2,'头斯特','$2a$10$HgN2zjA/k2dvOxHz4ISJbObXiGwwr6ykR8SQyxzQp14M3k6D7NYl.','toast','140443@qq.com',NULL,'user','easy','2026-06-21 16:19:14','2026-06-21 16:19:14'),(3,'admin2','$2a$10$r67Oy0pEN.ruUNPHX5HXXew2nuWWpTUFgsSqOBQdjRTWP4vVT7Nwm','','',NULL,'admin','medium','2026-06-21 17:34:05','2026-06-21 17:34:05'),(6,'提米','$2a$10$Q/Jp3kyxr6i8IYHaCFLSROTaeh0TrWSejJU1OsaAxcmjmntktS9u.','timi',NULL,NULL,'user','easy','2026-06-21 18:00:53','2026-06-21 18:00:53'),(7,'user','$2a$10$cXjG8ixJee9v4AlYKHqTyu.qKoiAhOtJVUQIprA1mKj3aPSP7FkaK','小U','123@qq.com',NULL,'user','easy','2026-06-21 19:44:04','2026-06-21 19:44:04');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-22 15:54:47

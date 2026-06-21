-- ============================================================
-- AI驱动的编程练习助手 - 数据库初始化脚本
-- 数据库: coding_assistant
-- ============================================================

CREATE DATABASE IF NOT EXISTS coding_assistant
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE coding_assistant;

-- ============================================================
-- 1. 用户表
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    `username`     VARCHAR(50)  NOT NULL                  COMMENT '用户名',
    `password`     VARCHAR(255) NOT NULL                  COMMENT '密码(BCrypt加密)',
    `nickname`     VARCHAR(50)  DEFAULT NULL              COMMENT '昵称',
    `email`        VARCHAR(100) DEFAULT NULL              COMMENT '邮箱',
    `avatar`       VARCHAR(255) DEFAULT NULL              COMMENT '头像URL',
    `role`         VARCHAR(10)  DEFAULT 'user'            COMMENT '角色: user-普通用户, admin-管理员',
    `ability_level` VARCHAR(20) DEFAULT 'easy'            COMMENT '能力等级: easy-简单, medium-中等, hard-困难',
    `created_at`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 预置管理员账号（密码: admin123）
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `ability_level`)
VALUES ('admin', '$2a$10$dFB61MjIBthQ/EpimY6GDu/73awVhyK47PlI4gbXRwihrbbh.HvKy', '管理员', 'admin', 'hard');

-- ============================================================
-- 2. 题目表
-- ============================================================
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '题目ID',
    `title`            VARCHAR(200) NOT NULL                  COMMENT '题目标题',
    `description`      TEXT         NOT NULL                  COMMENT '题目描述',
    `difficulty`       VARCHAR(10)  NOT NULL DEFAULT 'easy'   COMMENT '难度: easy-简单, medium-中等, hard-困难',
    `knowledge_point`  VARCHAR(100) NOT NULL                  COMMENT '知识点分类',
    `input_description` VARCHAR(500) DEFAULT NULL             COMMENT '输入描述',
    `output_description` VARCHAR(500) DEFAULT NULL            COMMENT '输出描述',
    `sample_input`     TEXT         DEFAULT NULL              COMMENT '样例输入',
    `sample_output`    TEXT         DEFAULT NULL              COMMENT '样例输出',
    `reference_code`   TEXT         DEFAULT NULL              COMMENT '参考答案代码',
    `source`           VARCHAR(20)  DEFAULT 'manual'          COMMENT '来源: manual-手工录入, ai_generated-AI生成',
    `review_status`    VARCHAR(20)  DEFAULT 'approved'        COMMENT '审核状态: pending-待审核, approved-已通过, rejected-已驳回',
    `created_by`       BIGINT       DEFAULT NULL              COMMENT '创建人ID',
    `created_at`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_difficulty` (`difficulty`),
    KEY `idx_knowledge_point` (`knowledge_point`),
    KEY `idx_review_status` (`review_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- ============================================================
-- 预置题目数据（15道，Python 语言，覆盖各难度和知识点）
-- ============================================================
INSERT INTO `question` (`title`, `description`, `difficulty`, `knowledge_point`, `input_description`, `output_description`, `sample_input`, `sample_output`, `reference_code`, `source`, `review_status`) VALUES
-- 简单题
('两数之和', '编写一个程序，从标准输入读取两个整数，计算它们的和并输出。', 'easy', '基础语法',
 '一行两个整数 a 和 b，以空格分隔。', '输出 a + b 的值。', '3 5', '8',
 'a, b = map(int, input().split())\nprint(a + b)',
 'manual', 'approved'),

('判断奇偶', '输入一个整数，判断它是奇数还是偶数。如果是偶数输出"even"，否则输出"odd"。', 'easy', '基础语法',
 '一个整数 n。', '输出 "even" 或 "odd"。', '4', 'even',
 'n = int(input())\nprint("even" if n % 2 == 0 else "odd")',
 'manual', 'approved'),

('打印乘法表', '输入一个整数 n，输出 n 以内的九九乘法表片段（n × n 的乘法表）。', 'easy', '循环结构',
 '一个整数 n (1 ≤ n ≤ 9)。', '输出 n 行，每行 i 个算式，格式为 "j*i=积"，算式之间用制表符(\\t)分隔。', '3',
 '1*1=1\n1*2=2\t2*2=4\n1*3=3\t2*3=6\t3*3=9',
 'n = int(input())\nfor i in range(1, n + 1):\n    line = "\\t".join(f"{j}*{i}={i*j}" for j in range(1, i + 1))\n    print(line)',
 'manual', 'approved'),

('求最大值', '输入三个整数，输出其中最大的数。', 'easy', '条件判断',
 '一行三个整数 a b c，以空格分隔。', '输出三个数中的最大值。', '10 25 7', '25',
 'a, b, c = map(int, input().split())\nprint(max(a, b, c))',
 'manual', 'approved'),

('字符串反转', '输入一个字符串，将其反转后输出。', 'easy', '字符串处理',
 '一行字符串 s。', '输出反转后的字符串。', 'hello', 'olleh',
 's = input()\nprint(s[::-1])',
 'manual', 'approved'),

-- 中等题
('斐波那契数列', '斐波那契数列的定义为：F(1)=1, F(2)=1, F(n)=F(n-1)+F(n-2) (n≥3)。输入 n，输出第 n 项的值。', 'medium', '递归与递推',
 '一个正整数 n (1 ≤ n ≤ 45)。', '输出斐波那契数列的第 n 项。', '10', '55',
 'n = int(input())\nf = [0] * (n + 1)\nf[1] = 1\nif n >= 2:\n    f[2] = 1\nfor i in range(3, n + 1):\n    f[i] = f[i-1] + f[i-2]\nprint(f[n])',
 'manual', 'approved'),

('回文数判断', '输入一个整数，判断它是否为回文数（正着读和倒着读一样）。', 'medium', '字符串处理',
 '一个整数 n。', '如果是回文数输出 "true"，否则输出 "false"。', '121', 'true',
 's = input().strip()\nprint("true" if s == s[::-1] else "false")',
 'manual', 'approved'),

('冒泡排序', '输入一个长度为 n 的整数数组，使用冒泡排序将其按升序排列并输出。', 'medium', '排序算法',
 '第一行一个整数 n (1 ≤ n ≤ 100)；第二行 n 个整数。', '输出排序后的数组，元素之间用空格分隔。', '5\n3 1 4 1 5', '1 1 3 4 5',
 'n = int(input())\narr = list(map(int, input().split()))\nfor i in range(n - 1):\n    for j in range(n - 1 - i):\n        if arr[j] > arr[j + 1]:\n            arr[j], arr[j + 1] = arr[j + 1], arr[j]\nprint(" ".join(map(str, arr)))',
 'manual', 'approved'),

('数组去重', '给定一个整数数组，去除其中重复的元素，并按升序输出。', 'medium', '数组操作',
 '第一行一个整数 n；第二行 n 个整数。', '输出去重并排序后的数组，元素之间用空格分隔。', '6\n4 2 4 1 3 2', '1 2 3 4',
 'n = int(input())\narr = list(map(int, input().split()))\nresult = sorted(set(arr))\nprint(" ".join(map(str, result)))',
 'manual', 'approved'),

('二分查找', '给定一个升序排列的整数数组和一个目标值，使用二分查找判断目标值是否在数组中。', 'medium', '查找算法',
 '第一行两个整数 n 和 target；第二行 n 个升序整数。', '如果存在输出 "true"，否则输出 "false"。', '5 3\n1 2 3 4 5', 'true',
 'n, target = map(int, input().split())\narr = list(map(int, input().split()))\nleft, right = 0, n - 1\nfound = False\nwhile left <= right:\n    mid = (left + right) // 2\n    if arr[mid] == target:\n        found = True\n        break\n    elif arr[mid] < target:\n        left = mid + 1\n    else:\n        right = mid - 1\nprint(str(found).lower())',
 'manual', 'approved'),

-- 困难题
('最长公共子序列', '给定两个字符串，求它们的最长公共子序列(LCS)的长度。', 'hard', '动态规划',
 '两行，每行一个字符串 s1 和 s2。', '输出 LCS 的长度。', 'abcde\nace', '3',
 'a = input().strip()\nb = input().strip()\nm, n = len(a), len(b)\ndp = [[0] * (n + 1) for _ in range(m + 1)]\nfor i in range(1, m + 1):\n    for j in range(1, n + 1):\n        if a[i-1] == b[j-1]:\n            dp[i][j] = dp[i-1][j-1] + 1\n        else:\n            dp[i][j] = max(dp[i-1][j], dp[i][j-1])\nprint(dp[m][n])',
 'manual', 'approved'),

('0-1背包问题', '给定 n 个物品，每个物品有重量 w[i] 和价值 v[i]。背包容量为 W，求能装入的最大价值。', 'hard', '动态规划',
 '第一行两个整数 n 和 W；第二行 n 个整数表示重量；第三行 n 个整数表示价值。', '输出最大总价值。', '4 5\n2 1 3 2\n3 2 4 2', '7',
 'n, W = map(int, input().split())\nw = list(map(int, input().split()))\nv = list(map(int, input().split()))\ndp = [0] * (W + 1)\nfor i in range(n):\n    for j in range(W, w[i] - 1, -1):\n        dp[j] = max(dp[j], dp[j - w[i]] + v[i])\nprint(dp[W])',
 'manual', 'approved'),

('拓扑排序', '给定一个有向无环图(DAG)，输出其拓扑排序结果。', 'hard', '图算法',
 '第一行两个整数 n(顶点数) 和 m(边数)；接下来 m 行每行两个整数 u v 表示 u→v 的有向边。', '输出一行拓扑排序结果（节点编号 1~n）。若有多个结果，输出任意一个。', '4 3\n1 2\n2 3\n1 3', '1 2 3 4',
 'from collections import deque\n\nn, m = map(int, input().split())\ng = [[] for _ in range(n + 1)]\nindeg = [0] * (n + 1)\nfor _ in range(m):\n    u, v = map(int, input().split())\n    g[u].append(v)\n    indeg[v] += 1\nq = deque([i for i in range(1, n + 1) if indeg[i] == 0])\nres = []\nwhile q:\n    u = q.popleft()\n    res.append(str(u))\n    for v in g[u]:\n        indeg[v] -= 1\n        if indeg[v] == 0:\n            q.append(v)\nprint(" ".join(res))',
 'manual', 'approved'),

('最小生成树', '给定一个带权无向图，使用Kruskal算法求最小生成树的总权重。', 'hard', '图算法',
 '第一行两个整数 n(顶点数) 和 m(边数)；接下来 m 行每行三个整数 u v w 表示 u-v 边权重 w。', '输出最小生成树的总权重。若图不连通输出 -1。', '4 5\n1 2 1\n1 3 2\n1 4 3\n2 3 2\n3 4 4', '6',
 'def find(x):\n    if parent[x] != x:\n        parent[x] = find(parent[x])\n    return parent[x]\n\ndef union(a, b):\n    parent[find(a)] = find(b)\n\nn, m = map(int, input().split())\nedges = []\nfor _ in range(m):\n    u, v, w = map(int, input().split())\n    edges.append((w, u, v))\nedges.sort()\nparent = list(range(n + 1))\ntotal = cnt = 0\nfor w, u, v in edges:\n    if find(u) != find(v):\n        union(u, v)\n        total += w\n        cnt += 1\nprint(total if cnt == n - 1 else -1)',
 'manual', 'approved');

-- ============================================================
-- 3. 提交记录表
-- ============================================================
DROP TABLE IF EXISTS `submission`;
CREATE TABLE `submission` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '提交ID',
    `user_id`      BIGINT       NOT NULL                  COMMENT '用户ID',
    `question_id`  BIGINT       NOT NULL                  COMMENT '题目ID',
    `code`         TEXT         NOT NULL                  COMMENT '用户提交的代码',
    `language`     VARCHAR(20)  DEFAULT 'python'          COMMENT '编程语言',
    `result`       VARCHAR(20)  DEFAULT 'pending'         COMMENT '判题结果: pending-待判, judging-判题中, accepted-通过, wrong_answer-答案错误, compile_error-编译错误, runtime_error-运行错误',
    `score`        INT          DEFAULT 0                 COMMENT '得分(0-100)',
    `error_info`   TEXT         DEFAULT NULL              COMMENT '错误信息（编译/运行时错误详情）',
    `ai_diagnosis` TEXT         DEFAULT NULL              COMMENT 'AI诊断建议',
    `created_at`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_question_id` (`question_id`),
    KEY `idx_result` (`result`),
    KEY `idx_user_question` (`user_id`, `question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提交记录表';

-- ============================================================
-- 4. 学习统计表
-- ============================================================
DROP TABLE IF EXISTS `learning_stats`;
CREATE TABLE `learning_stats` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '记录ID',
    `user_id`          BIGINT       NOT NULL                  COMMENT '用户ID',
    `knowledge_point`  VARCHAR(100) NOT NULL                  COMMENT '知识点',
    `total_count`      INT          DEFAULT 0                 COMMENT '总做题数',
    `correct_count`    INT          DEFAULT 0                 COMMENT '正确数',
    `wrong_count`      INT          DEFAULT 0                 COMMENT '错误数',
    `accuracy`         DECIMAL(5,2) DEFAULT 0.00              COMMENT '正确率(%)',
    `updated_at`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_knowledge` (`user_id`, `knowledge_point`),
    KEY `idx_user_id` (`user_id`)

-- ============================================================
-- 5. 成就定义表
-- ============================================================
DROP TABLE IF EXISTS `achievement`;
CREATE TABLE `achievement` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '成就ID',
    `name`             VARCHAR(100) NOT NULL                  COMMENT '成就名称',
    `description`      VARCHAR(255) DEFAULT NULL              COMMENT '成就描述',
    `icon`             VARCHAR(20)  DEFAULT '🏆'             COMMENT '成就图标',
    `condition_type`   VARCHAR(50)  NOT NULL                  COMMENT '条件类型: total_accepted-通过数, total_wrong-错误数, total_submit-总提交数',
    `condition_value`  INT          NOT NULL                  COMMENT '条件阈值',
    `sort_order`       INT          DEFAULT 0                 COMMENT '排序',
    `created_at`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_condition` (`condition_type`, `condition_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成就定义表';

INSERT INTO `achievement` (`name`, `description`, `icon`, `condition_type`, `condition_value`, `sort_order`) VALUES
('第一步', '完成第一次提交', '📝', 'total_submit', 1, 1),
('首胜', '第一次通过题目', '🎉', 'total_accepted', 1, 2),
('小试牛刀', '通过5道题', '⭐', 'total_accepted', 5, 3),
('渐入佳境', '通过10道题', '🌟', 'total_accepted', 10, 4),
('题库探险家', '通过25道题', '🏆', 'total_accepted', 25, 5),
('越挫越勇', '做错5道题', '💪', 'total_wrong', 5, 6),
('百折不挠', '做错10道题', '🔥', 'total_wrong', 10, 7);


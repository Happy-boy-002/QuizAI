-- QuizAI 练习记录详情表
-- 用于存储每次练习中每道题的答题详情
-- 执行此脚本前请先确保已切换到 quiz_ai 数据库: USE quiz_ai;

CREATE TABLE IF NOT EXISTS `practice_record_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `practice_record_id` bigint NOT NULL COMMENT '关联练习记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `question_content` text NOT NULL COMMENT '题目内容',
  `option_a` varchar(255) DEFAULT NULL COMMENT '选项A',
  `option_b` varchar(255) DEFAULT NULL COMMENT '选项B',
  `option_c` varchar(255) DEFAULT NULL COMMENT '选项C',
  `option_d` varchar(255) DEFAULT NULL COMMENT '选项D',
  `user_answer` char(1) DEFAULT NULL COMMENT '用户选择的答案(A/B/C/D)',
  `correct_answer` char(1) NOT NULL COMMENT '正确答案',
  `is_correct` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否答对 0-错误 1-正确',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_practice_record_id` (`practice_record_id`) USING BTREE,
  KEY `idx_question_id` (`question_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='练习记录详情表';

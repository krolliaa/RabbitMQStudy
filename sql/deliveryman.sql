SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `deliveryman`;
CREATE TABLE `deliveryman`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '骑手id',
  `name` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `status` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `date` datetime(0) NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `deliveryman` VALUES (1, 'wangxiaoer', 'AVALIABLE', '2020-06-10 20:30:17');

SET FOREIGN_KEY_CHECKS = 1;
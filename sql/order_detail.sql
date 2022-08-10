SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `status` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `address` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单地址',
  `account_id` int(0) NULL DEFAULT NULL COMMENT '用户id',
  `product_id` int(0) NULL DEFAULT NULL COMMENT '产品id',
  `deliveryman_id` int(0) NULL DEFAULT NULL COMMENT '骑手id',
  `settlement_id` int(0) NULL DEFAULT NULL COMMENT '结算id',
  `reward_id` int(0) NULL DEFAULT NULL COMMENT '积分奖励id',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `date` datetime(0) NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 403 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50721
Source Host           : localhost:3306
Source Database       : cloud_disk

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2019-04-19 14:21:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for advanced_general
-- ----------------------------
DROP TABLE IF EXISTS `advanced_general`;
CREATE TABLE `advanced_general` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `keyword` varchar(255) DEFAULT NULL,
  `node_id` bigint(20) NOT NULL,
  `root` varchar(255) DEFAULT NULL,
  `score` double NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of advanced_general
-- ----------------------------
INSERT INTO `advanced_general` VALUES ('1', '小组会议', '4', '人物活动-会议学习活动', '0.420905');
INSERT INTO `advanced_general` VALUES ('2', '室内一角', '4', '建筑-室内', '0.294632');
INSERT INTO `advanced_general` VALUES ('3', '空调', '4', '商品-家用电器', '0.200411');
INSERT INTO `advanced_general` VALUES ('4', '电视电话会议', '4', '人物活动-会议学习活动', '0.106139');
INSERT INTO `advanced_general` VALUES ('5', '人物活动', '4', '人物活动-会议学习活动', '0.0530695');
INSERT INTO `advanced_general` VALUES ('6', '屏幕截图', '6', '非自然图像-屏幕截图', '0.276069');
INSERT INTO `advanced_general` VALUES ('7', '小狗', '6', '动物-狗', '0.210153');
INSERT INTO `advanced_general` VALUES ('8', '猫', '6', '动物-猫', '0.146085');
INSERT INTO `advanced_general` VALUES ('9', '玩具', '6', '商品-玩具', '0.074695');
INSERT INTO `advanced_general` VALUES ('10', '亚洲猫', '6', '动物-哺乳动物', '0.007037');

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `addtime` datetime DEFAULT NULL,
  `adduserid` bigint(20) NOT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `updatetime` datetime DEFAULT NULL,
  `content_type` varchar(200) DEFAULT NULL,
  `ext` varchar(50) DEFAULT NULL,
  `file_name` varchar(200) DEFAULT NULL,
  `file_size` bigint(20) NOT NULL,
  `file_token` varchar(50) DEFAULT NULL,
  `file_type` int(11) NOT NULL,
  `folderid` int(11) NOT NULL,
  `landmark` varchar(500) DEFAULT NULL,
  `md5val` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of file
-- ----------------------------
INSERT INTO `file` VALUES ('2', '2019-03-09 16:18:59', '1', '\0', '2019-03-09 16:18:59', 'image/jpeg', '.jpg', 'IMG_20190307_140253.jpg', '278040', 'wu_1d5hjvjr41i9u1qdla9l1udjbacc.jpg', '1', '0', null, 'bfd07be525bc1db03e61bfb339e16962');
INSERT INTO `file` VALUES ('3', '2019-03-09 16:26:44', '1', '\0', '2019-03-09 16:26:44', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '.xlsx', '系统基础数据统计表最新.xlsx', '1131967', 'wu_1d5hkeadn1mh21in21vkn3ctmq84.xlsx', '2', '0', null, '6f58fd1e523f182a078f4f109ec195f1');
INSERT INTO `file` VALUES ('4', '2019-03-09 16:41:44', '1', '\0', '2019-03-09 16:42:43', 'image/jpeg', '.jpg', 'mm17516.jpg', '120648', 'wu_1d5hl94o5se31fn21ksc1en7g2e5.jpg', '1', '0', null, '838fec3716a6320d8c3e200f224f0d75');

-- ----------------------------
-- Table structure for file_general
-- ----------------------------
DROP TABLE IF EXISTS `file_general`;
CREATE TABLE `file_general` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `addtime` datetime DEFAULT NULL,
  `adduserid` bigint(20) NOT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `updatetime` datetime DEFAULT NULL,
  `file_id` bigint(20) NOT NULL,
  `keyword` varchar(255) DEFAULT NULL,
  `root` varchar(255) DEFAULT NULL,
  `score` double NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of file_general
-- ----------------------------
INSERT INTO `file_general` VALUES ('1', '2019-03-09 16:19:00', '1', '\0', '2019-03-09 16:19:00', '2', '小组会议', '人物活动-会议学习活动', '0.420905');
INSERT INTO `file_general` VALUES ('2', '2019-03-09 16:19:00', '1', '\0', '2019-03-09 16:19:00', '2', '室内一角', '建筑-室内', '0.294632');
INSERT INTO `file_general` VALUES ('3', '2019-03-09 16:19:00', '1', '\0', '2019-03-09 16:19:00', '2', '空调', '商品-家用电器', '0.200411');
INSERT INTO `file_general` VALUES ('4', '2019-03-09 16:19:00', '1', '\0', '2019-03-09 16:19:00', '2', '电视电话会议', '人物活动-会议学习活动', '0.106139');
INSERT INTO `file_general` VALUES ('5', '2019-03-09 16:19:00', '1', '\0', '2019-03-09 16:19:00', '2', '人物活动', '人物活动-会议学习活动', '0.0530695');
INSERT INTO `file_general` VALUES ('6', '2019-03-09 16:41:45', '1', '\0', '2019-03-09 16:41:45', '4', '屏幕截图', '非自然图像-屏幕截图', '0.276069');
INSERT INTO `file_general` VALUES ('7', '2019-03-09 16:41:45', '1', '\0', '2019-03-09 16:41:45', '4', '小狗', '动物-狗', '0.210153');
INSERT INTO `file_general` VALUES ('8', '2019-03-09 16:41:45', '1', '\0', '2019-03-09 16:41:45', '4', '猫', '动物-猫', '0.146085');
INSERT INTO `file_general` VALUES ('9', '2019-03-09 16:41:45', '1', '\0', '2019-03-09 16:41:45', '4', '玩具', '商品-玩具', '0.074695');
INSERT INTO `file_general` VALUES ('10', '2019-03-09 16:41:45', '1', '\0', '2019-03-09 16:41:45', '4', '亚洲猫', '动物-哺乳动物', '0.007037');

-- ----------------------------
-- Table structure for folder
-- ----------------------------
DROP TABLE IF EXISTS `folder`;
CREATE TABLE `folder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `addtime` datetime DEFAULT NULL,
  `adduserid` bigint(20) NOT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `updatetime` datetime DEFAULT NULL,
  `folder_name` varchar(100) DEFAULT NULL,
  `pid` bigint(20) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of folder
-- ----------------------------

-- ----------------------------
-- Table structure for node
-- ----------------------------
DROP TABLE IF EXISTS `node`;
CREATE TABLE `node` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `add_time` date DEFAULT NULL,
  `adduserid` bigint(20) NOT NULL,
  `content_type` varchar(200) DEFAULT NULL,
  `ext` varchar(50) DEFAULT NULL,
  `file_name` varchar(200) DEFAULT NULL,
  `file_size` bigint(20) NOT NULL,
  `file_token` varchar(100) DEFAULT NULL,
  `file_type` int(11) NOT NULL,
  `landmark` varchar(500) DEFAULT NULL,
  `md5val` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of node
-- ----------------------------
INSERT INTO `node` VALUES ('4', '2019-03-09', '1', 'image/jpeg', '.jpg', 'IMG_20190307_140253.jpg', '278040', 'wu_1d5hjvjr41i9u1qdla9l1udjbacc.jpg', '1', null, 'bfd07be525bc1db03e61bfb339e16962');
INSERT INTO `node` VALUES ('5', '2019-03-09', '1', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '.xlsx', '系统基础数据统计表最新.xlsx', '1131967', 'wu_1d5hkeadn1mh21in21vkn3ctmq84.xlsx', '2', null, '6f58fd1e523f182a078f4f109ec195f1');
INSERT INTO `node` VALUES ('6', '2019-03-09', '1', 'image/jpeg', '.jpg', 'mmexport1551804477516.jpg', '120648', 'wu_1d5hl94o5se31fn21ksc1en7g2e5.jpg', '1', null, '838fec3716a6320d8c3e200f224f0d75');

-- ----------------------------
-- Table structure for recycle
-- ----------------------------
DROP TABLE IF EXISTS `recycle`;
CREATE TABLE `recycle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `addtime` datetime DEFAULT NULL,
  `adduserid` bigint(20) NOT NULL,
  `file_id` bigint(20) NOT NULL,
  `filename` varchar(100) DEFAULT NULL,
  `filesize` bigint(20) NOT NULL,
  `filetype` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of recycle
-- ----------------------------

-- ----------------------------
-- Table structure for share_file
-- ----------------------------
DROP TABLE IF EXISTS `share_file`;
CREATE TABLE `share_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `addtime` datetime DEFAULT NULL,
  `adduserid` bigint(20) NOT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `updatetime` datetime DEFAULT NULL,
  `if_secert` bit(1) NOT NULL,
  `download_count` int(11) NOT NULL,
  `extract_state` varchar(100) DEFAULT NULL,
  `node_id` bigint(20) NOT NULL,
  `over_type` int(11) NOT NULL,
  `overtime` date DEFAULT NULL,
  `save_count` int(11) NOT NULL,
  `share_file_name` varchar(100) DEFAULT NULL,
  `share_time` date DEFAULT NULL,
  `share_token` varchar(100) DEFAULT NULL,
  `view_count` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of share_file
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `addtime` datetime DEFAULT NULL,
  `adduserid` int(11) NOT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `updatetime` datetime DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `login_name` varchar(50) NOT NULL,
  `nick_name` varchar(50) DEFAULT NULL,
  `picture` varchar(50) DEFAULT NULL,
  `pwd` varchar(50) DEFAULT NULL,
  `role` varchar(50) NOT NULL,
  `sex` int(11) NOT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UK_5gbm5vnx03n0kiur98hdj8fcu` (`login_name`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '2019-02-18 08:23:57', '0', '\0', '2019-03-09 16:42:32', '2019-03-09 14:46:35', 'admin', 'admin2', '85315ede-546c-4959-aa5f-83ac3c353e7f.jpg', '2QeC+6mkJQnVRSh4jUUJSQ==', 'Admin', '2', '1');
INSERT INTO `user` VALUES ('2', '2019-02-27 15:18:08', '0', '\0', '2019-02-27 15:18:23', null, 'q', 'q11', null, null, 'User', '1', '1');
INSERT INTO `user` VALUES ('3', '2019-02-27 15:20:32', '0', '\0', '2019-02-27 15:20:40', null, '23', '3232323', null, 'PhpjeXn6+lk=', 'User', '1', '1');
INSERT INTO `user` VALUES ('4', '2019-03-08 17:44:56', '0', '\0', '2019-03-09 16:48:07', '2019-03-09 16:48:07', 'igeekfan', '123qwe', null, '2S5XwKFN1tpyVrjnfPJPJQ==', 'User', '1', '1');

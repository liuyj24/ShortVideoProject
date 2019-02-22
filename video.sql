/*
SQLyog Ultimate v8.32 
MySQL - 5.5.36 : Database - video
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`video` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `video`;

/*Table structure for table `bgm` */

DROP TABLE IF EXISTS `bgm`;

CREATE TABLE `bgm` (
  `id` varchar(64) NOT NULL,
  `author` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `path` varchar(255) NOT NULL COMMENT '播放地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `bgm` */

insert  into `bgm`(`id`,`author`,`name`,`path`) values ('190131CXH7G0X400','test1','test1','/bgm/护花使者.mp3'),('190131CXZ59HFHSW','test2','test2','/bgm/独家记忆.mp3');

/*Table structure for table `comments` */

DROP TABLE IF EXISTS `comments`;

CREATE TABLE `comments` (
  `id` varchar(20) NOT NULL,
  `father_comment_id` varchar(20) DEFAULT NULL,
  `to_user_id` varchar(20) DEFAULT NULL,
  `video_id` varchar(20) NOT NULL COMMENT '视频id',
  `from_user_id` varchar(20) NOT NULL COMMENT '留言者，评论的用户id',
  `comment` text NOT NULL COMMENT '评论内容',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程评论表';

/*Data for the table `comments` */

/*Table structure for table `search_records` */

DROP TABLE IF EXISTS `search_records`;

CREATE TABLE `search_records` (
  `id` varchar(64) NOT NULL,
  `content` varchar(255) NOT NULL COMMENT '搜索的内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频搜索的记录表';

/*Data for the table `search_records` */

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` varchar(64) NOT NULL,
  `username` varchar(20) NOT NULL COMMENT '用户名',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `face_image` varchar(255) DEFAULT NULL COMMENT '我的头像，如果没有默认给一张',
  `nickname` varchar(20) NOT NULL COMMENT '昵称',
  `fans_counts` int(11) DEFAULT '0' COMMENT '我的粉丝数量',
  `follow_counts` int(11) DEFAULT '0' COMMENT '我关注的人总数',
  `receive_like_counts` int(11) DEFAULT '0' COMMENT '我接受到的赞美/收藏 的数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `users` */

insert  into `users`(`id`,`username`,`password`,`face_image`,`nickname`,`fans_counts`,`follow_counts`,`receive_like_counts`) values ('180425CFA4RB6T0H','imooc','kU8h64TG/bK2Y91vRT9lyg==','/180425CFA4RB6T0H/face/wxb281653441432c9a.o6zAJs-v-acRKWI2Ow_Lx3foMYQE.HZ2lPYBbcABK310dcccd09c219672e10ccf0cf7a1d21.png','imooc1',2,0,5),('1901268H50CFXRKP','xiaopang','ICy5YqxZB1uWSwcVLSNLcA==','/1901268H50CFXRKP/face/tmp_28cc939a327f6d35689e613a0c0265eb624cdcf910b0d447.jpg','xiaopang',0,1,0),('190127BSG3FMRFRP','123','ICy5YqxZB1uWSwcVLSNLcA==',NULL,'123',0,0,0);

/*Table structure for table `users_fans` */

DROP TABLE IF EXISTS `users_fans`;

CREATE TABLE `users_fans` (
  `id` varchar(64) NOT NULL,
  `user_id` varchar(64) NOT NULL COMMENT '用户',
  `fan_id` varchar(64) NOT NULL COMMENT '粉丝',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`,`fan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户粉丝关联关系表';

/*Data for the table `users_fans` */

insert  into `users_fans`(`id`,`user_id`,`fan_id`) values ('190119DKNXSRFB7C','180425CFA4RB6T0H','1901197566SHNWBC'),('1901268HD0PRC754','180425CFA4RB6T0H','1901268H50CFXRKP');

/*Table structure for table `users_like_videos` */

DROP TABLE IF EXISTS `users_like_videos`;

CREATE TABLE `users_like_videos` (
  `id` varchar(64) NOT NULL,
  `user_id` varchar(64) NOT NULL COMMENT '用户',
  `video_id` varchar(64) NOT NULL COMMENT '视频',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_video_rel` (`user_id`,`video_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户喜欢的/赞过的视频';

/*Data for the table `users_like_videos` */

insert  into `users_like_videos`(`id`,`user_id`,`video_id`) values ('190119DK6DKT7SCH','180425CFA4RB6T0H','190117B8DD2G4H94'),('190126829TS3CPX4','180425CFA4RB6T0H','19012682040YKK1P'),('190131D0TYHSC1KP','180425CFA4RB6T0H','190131D0MAN0CT54'),('190119F29C71KDD4','1901197566SHNWBC','190117B8AD4D0Z2W'),('190119F278SBDYW0','1901197566SHNWBC','190117B8DD2G4H94');

/*Table structure for table `users_report` */

DROP TABLE IF EXISTS `users_report`;

CREATE TABLE `users_report` (
  `id` varchar(64) NOT NULL,
  `deal_user_id` varchar(64) NOT NULL COMMENT '被举报用户id',
  `deal_video_id` varchar(64) NOT NULL,
  `title` varchar(128) NOT NULL COMMENT '类型标题，让用户选择，详情见 枚举',
  `content` varchar(255) DEFAULT NULL COMMENT '内容',
  `userid` varchar(64) NOT NULL COMMENT '举报人的id',
  `create_date` datetime NOT NULL COMMENT '举报时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报用户表';

/*Data for the table `users_report` */

insert  into `users_report`(`id`,`deal_user_id`,`deal_video_id`,`title`,`content`,`userid`,`create_date`) values ('19012683F5TKT25P','180425CFA4RB6T0H','19012682040YKK1P','色情低俗','太低俗了','180425CFA4RB6T0H','2019-01-26 11:23:05');

/*Table structure for table `videos` */

DROP TABLE IF EXISTS `videos`;

CREATE TABLE `videos` (
  `id` varchar(64) NOT NULL,
  `user_id` varchar(64) NOT NULL COMMENT '发布者id',
  `audio_id` varchar(64) DEFAULT NULL COMMENT '用户使用音频的信息',
  `video_desc` varchar(128) DEFAULT NULL COMMENT '视频描述',
  `video_path` varchar(255) NOT NULL COMMENT '视频存放的路径',
  `video_seconds` float(6,2) DEFAULT NULL COMMENT '视频秒数',
  `video_width` int(6) DEFAULT NULL COMMENT '视频宽度',
  `video_height` int(6) DEFAULT NULL COMMENT '视频高度',
  `cover_path` varchar(255) DEFAULT NULL COMMENT '视频封面图',
  `like_counts` bigint(20) NOT NULL DEFAULT '0' COMMENT '喜欢/赞美的数量',
  `status` int(1) NOT NULL COMMENT '视频状态：\r\n1、发布成功\r\n2、禁止播放，管理员操作',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频信息表';

/*Data for the table `videos` */

insert  into `videos`(`id`,`user_id`,`audio_id`,`video_desc`,`video_path`,`video_seconds`,`video_width`,`video_height`,`cover_path`,`like_counts`,`status`,`create_time`) values ('190131D02PZ55N0H','180425CFA4RB6T0H','','签名','/180425CFA4RB6T0H/video/tmp_347a30024d2f844476c2a2d98e72b1ecf87b6ffdfe17df04.mp4',3.00,544,320,'/180425CFA4RB6T0H/video/tmp_347a30024d2f844476c2a2d98e72b1ecf87b6ffdfe17df04.jpg',0,1,'2019-01-31 18:13:12'),('190131D0MAN0CT54','180425CFA4RB6T0H','190131CXH7G0X400','','/180425CFA4RB6T0H/video/9f30d7cb-077c-4d82-a6c2-60e37b3162a9.mp4',3.00,544,320,'/180425CFA4RB6T0H/video/tmp_30a37cb309a3887ee008e19895251632045b6a6f90bdba69.jpg',1,1,'2019-01-31 18:14:52'),('190131D12PZXGX1P','180425CFA4RB6T0H','','','/180425CFA4RB6T0H/video/tmp_9f7995f8bf24fefa66f43e410fe716795040a4da35983c3c.mp4',4.00,272,480,'/180425CFA4RB6T0H/video/tmp_9f7995f8bf24fefa66f43e410fe716795040a4da35983c3c.jpg',0,1,'2019-01-31 18:16:12');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

# DevSchoolFull

# 数据库

```
CREATE TABLE `user` (
  `uid` int NOT NULL AUTO_INCREMENT,
  /* 昵称 */
  `nickname` varchar(255) NOT NULL,
  /* 邮件地址 */
  `email` varchar(255) NOT NULL,
  /* 密码 */
  `password` varchar(255) NOT NULL,
  /* 约束 */
  PRIMARY KEY (`uid`),
  UNIQUE KEY `nickname` (`nickname`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8;


CREATE TABLE `assets` (
  `index` int NOT NULL AUTO_INCREMENT,
  /* 上传用户 */
  `uid` int NOT NULL,
  /* 路径 */
  `path` text NOT NULL,
  /* 源文件名 */
  `filename` text NOT NULL,
  /* 文件名 */
  `name` text NOT NULL,
  /* 文件后缀 */
  `ext` varchar(255) DEFAULT "",
  /* 文件类型 */
  `type` varchar(255) DEFAULT "",
  /* 上传时间 */
  `upload_time` datetime NOT NULL,
  /* 文件大小 */
  `size` bigint NOT NULL,
  /* 资源状态：0公开 1私有 -1删除 */
  `status` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`index`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `live` (
  /* 索引，rtmp name */
  `index` int NOT NULL AUTO_INCREMENT,
  /* 直播用户 */
  `uid` int NOT NULL,
  /* 上传验证 */
  `md5` text NOT NULL,
  /* 直播标题 */
  `title` text NOT NULL,
  /* 直播类别 */
  `category` varchar(255) NOT NULL,
  /* 直播状态 */
  `status` varchar(255) NOT NULL DEFAULT "pending",
  /* order时间 */
  `order_time` datetime NOT NULL,
  /* 预订live时间 */
  `live_time` datetime NOT NULL,
  PRIMARY KEY (`index`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```


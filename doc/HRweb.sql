CREATE TABLE `Question` (
`qid` bigint NOT NULL,
`ctime` datetime NOT NULL,
`state` int NOT NULL,
`content` text NOT NULL,
`choose` text NOT NULL COMMENT 'Danh sách các đáp án trong câu hỏi trắc nghiệm ở dang xml, nếu là tự luận thì để trống',
`answer` int NOT NULL,
`weight` int NOT NULL,
`creator` bigint NOT NULL,
`isdraf` int NOT NULL,
PRIMARY KEY (`qid`, `ctime`) 
);

CREATE TABLE `Test` (
`tid` bigint NOT NULL,
`cvid` bigint NOT NULL,
`ctime` datetime NOT NULL,
`stime` datetime NOT NULL,
`state` int NOT NULL,
PRIMARY KEY (`tid`, `ctime`) 
);

CREATE TABLE `QuestionInTest` (
`tid` bigint NOT NULL,
`qid` bigint NOT NULL,
`answer` int NOT NULL,
PRIMARY KEY (`tid`, `qid`) 
);

CREATE TABLE `CV` (
`cvid` bigint NOT NULL,
`ctime` datetime NOT NULL,
`state` int NOT NULL,
`rid` bigint NOT NULL,
`level` int NOT NULL,
`canretest` bit NOT NULL,
`forwarded` bit NOT NULL,
`name` varchar(255) NOT NULL,
`filename` varchar(255) NOT NULL,
`isnew` bit NOT NULL,
`email` varchar(255) NOT NULL,
PRIMARY KEY (`cvid`, `ctime`) 
);

CREATE TABLE `Request` (
`rid` bigint NOT NULL,
`unit` int NOT NULL,
`jobdesc` text NOT NULL,
`title` varchar(255) NOT NULL,
`position` varchar(255) NOT NULL,
`interest` text NOT NULL,
`requirement` text NOT NULL,
`creator` bigint NOT NULL,
`ctime` datetime NOT NULL,
`cvfid` bigint NOT NULL,
`quantity` int NOT NULL,
`state` int NOT NULL,
PRIMARY KEY (`rid`, `ctime`) 
);

CREATE TABLE `User` (
`uid` bigint NOT NULL,
`fullname` varchar(255) NOT NULL,
`ctime` datetime NOT NULL,
`state` int NOT NULL,
`unit` int NULL,
PRIMARY KEY (`uid`) 
);

CREATE TABLE `Auth` (
`uid` bigint NOT NULL,
`auth` varchar(64) NOT NULL COMMENT 'Mã số',
`lastused` time NOT NULL,
PRIMARY KEY (`uid`, `auth`) 
);

CREATE TABLE `Error` (
`eid` bigint NOT NULL,
`uid` bigint NOT NULL,
` ctime` datetime NOT NULL,
`details` varchar(255) NOT NULL,
`code` int NOT NULL,
`action` varchar(255) NOT NULL,
PRIMARY KEY (`eid`) 
);

CREATE TABLE `Log` (
`lid` bigint NOT NULL,
`uid` bigint NOT NULL,
`url` varchar(255) NOT NULL DEFAULT '\"\"',
`time` datetime NOT NULL,
`param` varchar(255) NOT NULL DEFAULT '\"\"',
`code` int NOT NULL,
`restime` int NOT NULL,
`ip` bigint NOT NULL,
`useragent` varchar(255) NOT NULL,
`cmdtype` int NOT NULL DEFAULT '0' COMMENT 'Đọc = 0, thêm = 1, sửa = 2, xóa = 3',
PRIMARY KEY (`lid`) 
);

CREATE TABLE `Action` (
`aid` bigint NOT NULL,
`name` varchar(255) NOT NULL,
`description` varchar(255) NOT NULL,
PRIMARY KEY (`aid`) 
);

CREATE TABLE `Role` (
`rid` bigint NOT NULL,
`name` varchar(255) NOT NULL,
PRIMARY KEY (`rid`) 
);

CREATE TABLE `Perm` (
`aid` bigint NOT NULL,
`rid` bigint NOT NULL,
PRIMARY KEY (`aid`, `rid`) 
);

CREATE TABLE `Membership` (
`uid` bigint NOT NULL,
`rid` bigint NOT NULL,
PRIMARY KEY (`uid`, `rid`) 
);

CREATE TABLE `NegPerm` (
`rid` bigint NOT NULL,
`aid` bigint NOT NULL,
PRIMARY KEY (`rid`, `aid`) 
);

CREATE TABLE `UP` (
`uid` bigint NOT NULL,
`username` varchar(255) NOT NULL,
`password` binary(64) NOT NULL,
`type` int NOT NULL COMMENT '1: ứng viên, 2 là người dùng',
PRIMARY KEY (`uid`) 
);

CREATE TABLE `TP` (
`uid` bigint NOT NULL,
`thirdpartyid` varchar(255) NOT NULL,
PRIMARY KEY (`uid`) 
);

CREATE TABLE `ErrorOfUser` (
`eid` bigint NOT NULL,
`uid` bigint NOT NULL,
`readed` int NOT NULL,
PRIMARY KEY (`eid`, `uid`) 
);

CREATE TABLE `Unit` (
`uid` int NOT NULL,
`name` varchar(255) NOT NULL,
PRIMARY KEY (`uid`) 
);

CREATE TABLE `Captcha` (
`capid` bigint NOT NULL,
`ctime` datetime NOT NULL,
`answer` varchar(20) NOT NULL,
PRIMARY KEY (`capid`) 
);

CREATE TABLE `Config` (
`ref` varchar(255) NOT NULL,
`val` varchar(255) NOT NULL,
PRIMARY KEY (`ref`) 
);

CREATE TABLE `Feed` (
`fid` bigint NOT NULL,
`title` varchar(255) NOT NULL,
`ctime` datetime NOT NULL,
`content` varchar(255) NOT NULL,
`state` int NOT NULL,
PRIMARY KEY (`fid`, `ctime`) 
);

CREATE TABLE `FeedOfUser` (
`uid` bigint NOT NULL,
`fid` bigint NOT NULL,
`readed` int NOT NULL,
PRIMARY KEY (`uid`, `fid`) 
);

CREATE TABLE `Comment` (
`cid` bigint NOT NULL,
`cvid` bigint NOT NULL,
`ctime` datetime NOT NULL,
`author` bigint NOT NULL,
`state` int NOT NULL,
`comment` varchar(255) NOT NULL,
PRIMARY KEY (`ctime`, `cid`) 
);

CREATE TABLE `Rating` (
`rid` bigint NOT NULL,
`cvid` bigint NOT NULL,
`iq` int NOT NULL,
`pres` int NOT NULL,
`arch` int NOT NULL,
`sum` int NOT NULL,
`total` int NOT NULL,
`rating` int NOT NULL,
`pote` int NOT NULL,
`state` int NOT NULL,
`ctime` datetime NOT NULL,
PRIMARY KEY (`rid`, `ctime`) 
);

CREATE TABLE `TestAccessCode` (
`testid` bigint NOT NULL,
`code` binary(224) NOT NULL,
PRIMARY KEY (`code`) 
);

CREATE TABLE `CVForm` (
`formid` bigint NOT NULL,
`name` varchar(255) NOT NULL,
`state` int NOT NULL,
`ctime` datetime NOT NULL,
PRIMARY KEY (`formid`) 
);

CREATE TABLE `Info` (
`iid` bigint NOT NULL,
`cvfid` bigint NOT NULL,
`info` text NOT NULL,
`cvid` bigint NOT NULL,
PRIMARY KEY (`iid`) 
);

CREATE TABLE `Field` (
`cvfid` bigint NOT NULL,
`title` varchar(255) NOT NULL,
`formid` bigint NOT NULL,
`state` int NOT NULL,
`ctime` datetime NOT NULL,
`creator` bigint NOT NULL,
PRIMARY KEY (`cvfid`, `ctime`) 
);

CREATE TABLE `CVFormofRequest` (
`cvform` bigint NOT NULL,
`rid` bigint NOT NULL,
PRIMARY KEY (`rid`, `cvform`) 
);


ALTER TABLE `QuestionInTest` ADD CONSTRAINT `fk_QuestionInTest_Test_1` FOREIGN KEY (`tid`) REFERENCES `Test` (`tid`);
ALTER TABLE `QuestionInTest` ADD CONSTRAINT `fk_QuestionInTest_QuestionType_1` FOREIGN KEY (`qid`) REFERENCES `Question` (`qid`);
ALTER TABLE `Auth` ADD CONSTRAINT `fk_Auth_APPLICANT_1` FOREIGN KEY (`uid`) REFERENCES `User` (`uid`);
ALTER TABLE `Log` ADD CONSTRAINT `fk_UserLog_APPLICANT_1` FOREIGN KEY (`uid`) REFERENCES `User` (`uid`);
ALTER TABLE `Perm` ADD CONSTRAINT `fk_Perm_Action_1` FOREIGN KEY (`aid`) REFERENCES `Action` (`aid`);
ALTER TABLE `Perm` ADD CONSTRAINT `fk_Perm_Role_1` FOREIGN KEY (`rid`) REFERENCES `Role` (`rid`);
ALTER TABLE `Membership` ADD CONSTRAINT `fk_Membership_Role_1` FOREIGN KEY (`rid`) REFERENCES `Role` (`rid`);
ALTER TABLE `Membership` ADD CONSTRAINT `fk_Membership_APPLICANT_1` FOREIGN KEY (`uid`) REFERENCES `User` (`uid`);
ALTER TABLE `NegPerm` ADD CONSTRAINT `fk_NegPerm_Action_1` FOREIGN KEY (`aid`) REFERENCES `Action` (`aid`);
ALTER TABLE `NegPerm` ADD CONSTRAINT `fk_NegPerm_Role_1` FOREIGN KEY (`rid`) REFERENCES `Role` (`rid`);
ALTER TABLE `UP` ADD CONSTRAINT `fk_UP_Membership_1` FOREIGN KEY (`uid`) REFERENCES `User` (`uid`);
ALTER TABLE `TP` ADD CONSTRAINT `fk_TP_Membership_1` FOREIGN KEY (`uid`) REFERENCES `User` (`uid`);
ALTER TABLE `ErrorOfUser` ADD CONSTRAINT `fk_ErrorReaded_User_1` FOREIGN KEY (`uid`) REFERENCES `User` (`uid`);
ALTER TABLE `ErrorOfUser` ADD CONSTRAINT `fk_ErrorReaded_Error_1` FOREIGN KEY (`eid`) REFERENCES `Error` (`eid`);
ALTER TABLE `User` ADD CONSTRAINT `fk_User_Unit_1` FOREIGN KEY (`unit`) REFERENCES `Unit` (`uid`);
ALTER TABLE `CV` ADD CONSTRAINT `fk_CV_Request_1` FOREIGN KEY (`rid`) REFERENCES `Request` (`rid`);
ALTER TABLE `Request` ADD CONSTRAINT `fk_Request_Unit_1` FOREIGN KEY (`unit`) REFERENCES `Unit` (`uid`);
ALTER TABLE `FeedOfUser` ADD CONSTRAINT `fk_FeedofUser_Feed_1` FOREIGN KEY (`fid`) REFERENCES `Feed` (`fid`);
ALTER TABLE `FeedOfUser` ADD CONSTRAINT `fk_FeedofUser_User_1` FOREIGN KEY (`uid`) REFERENCES `User` (`uid`);
ALTER TABLE `Comment` ADD CONSTRAINT `fk_Comment_CV_1` FOREIGN KEY (`cvid`) REFERENCES `CV` (`cvid`);
ALTER TABLE `Rating` ADD CONSTRAINT `fk_Rating_CV_1` FOREIGN KEY (`cvid`) REFERENCES `CV` (`cvid`);
ALTER TABLE `Test` ADD CONSTRAINT `fk_Test_CV_1` FOREIGN KEY (`cvid`) REFERENCES `CV` (`cvid`);
ALTER TABLE `Info` ADD CONSTRAINT `fk_Info_Field_1` FOREIGN KEY (`cvfid`) REFERENCES `Field` (`cvfid`);
ALTER TABLE `Field` ADD CONSTRAINT `fk_Field_CVForm_1` FOREIGN KEY (`formid`) REFERENCES `CVForm` (`formid`);
ALTER TABLE `Info` ADD CONSTRAINT `fk_Info_CV_1` FOREIGN KEY (`cvid`) REFERENCES `CV` (`cvid`);
ALTER TABLE `CVFormofRequest` ADD CONSTRAINT `fk_CVFormofRequest_Request_1` FOREIGN KEY (`rid`) REFERENCES `Request` (`rid`);
ALTER TABLE `CVFormofRequest` ADD CONSTRAINT `fk_CVFormofRequest_CVForm_1` FOREIGN KEY (`cvform`) REFERENCES `CVForm` (`formid`);
ALTER TABLE `TestAccessCode` ADD CONSTRAINT `fk_CVAccessCode_Test_1` FOREIGN KEY (`testid`) REFERENCES `Test` (`tid`);


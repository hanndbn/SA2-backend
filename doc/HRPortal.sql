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
`unit` int NOT NULL,
`type` int NOT NULL,
PRIMARY KEY (`qid`, `ctime`) 
);

CREATE TABLE `Test` (
`tid` bigint NOT NULL,
`ctime` datetime NOT NULL,
`state` int NOT NULL,
`type` int NOT NULL,
PRIMARY KEY (`tid`, `ctime`) 
);

CREATE TABLE `QuestionInTest` (
`tid` bigint NOT NULL,
`qid` bigint NOT NULL,
PRIMARY KEY (`tid`, `qid`) 
);

CREATE TABLE `CV` (
`cvid` bigint NOT NULL,
`ctime` datetime NOT NULL,
`state` int NOT NULL,
`level` int NOT NULL,
`name` varchar(255) NOT NULL,
`filename` varchar(255) NOT NULL,
`email` varchar(255) NOT NULL,
`bagid` bigint NOT NULL,
`engtested` bit NOT NULL,
`eng` int NOT NULL,
`iq` int NOT NULL,
`pote` int NOT NULL,
`total` int NOT NULL,
`sum` int NOT NULL,
`ach` int NOT NULL,
`pres` int NOT NULL,
`engtestresultmail` bit NOT NULL,
`iqtestresultmail` bit NOT NULL,
`receivedmail` bit NOT NULL,
`cantestiq` bit NOT NULL,
`cantesteng` bit NOT NULL,
`iqtested` bit NOT NULL,
PRIMARY KEY (`cvid`, `ctime`) 
);

CREATE TABLE `Request` (
`rid` bigint NOT NULL,
`jobdesc` text NOT NULL,
`title` varchar(255) NOT NULL,
`position` varchar(255) NOT NULL,
`interest` text NOT NULL,
`requirement` text NOT NULL,
`creator` bigint NOT NULL,
`ctime` datetime NOT NULL,
`unitid` int NOT NULL,
`quantity` int NOT NULL,
`state` int NOT NULL,
PRIMARY KEY (`rid`, `ctime`) 
);

CREATE TABLE `User` (
`uid` bigint NOT NULL,
`fullname` varchar(255) NOT NULL,
`ctime` datetime NOT NULL,
`state` int NOT NULL,
PRIMARY KEY (`uid`) 
);

CREATE TABLE `Auth` (
`uid` bigint NOT NULL,
`auth` varchar(64) NOT NULL COMMENT 'Mã số',
`lastused` time NOT NULL,
PRIMARY KEY (`uid`, `auth`) 
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
PRIMARY KEY (`uid`, `username`) 
);

CREATE TABLE `TP` (
`uid` bigint NOT NULL,
`thirdpartyid` varchar(255) NOT NULL,
PRIMARY KEY (`uid`) 
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
`ip` varchar(20) NOT NULL,
PRIMARY KEY (`capid`) 
);

CREATE TABLE `Config` (
`ref` varchar(255) NOT NULL,
`val` varchar(255) NOT NULL,
`unit` int NOT NULL,
PRIMARY KEY (`ref`, `unit`) 
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

CREATE TABLE `TestAccessCode` (
`testid` bigint NOT NULL,
`code` binary(224) NOT NULL,
PRIMARY KEY (`code`) 
);

CREATE TABLE `Info` (
`iid` bigint NOT NULL,
`field` bigint NOT NULL,
`info` text NOT NULL,
`cvid` bigint NOT NULL,
PRIMARY KEY (`iid`) 
);

CREATE TABLE `RequiredField` (
`fieldid` bigint NOT NULL,
`title` varchar(255) NOT NULL,
`state` int NOT NULL,
`ctime` datetime NOT NULL,
`creator` bigint NOT NULL,
`unit` int NOT NULL,
PRIMARY KEY (`fieldid`, `ctime`) 
);

CREATE TABLE `Bag` (
`bid` bigint NOT NULL,
`name` varchar(255) NOT NULL,
`ctime` datetime NOT NULL,
`state` int NOT NULL,
`request` bigint NOT NULL,
PRIMARY KEY (`bid`, `ctime`) 
);

CREATE TABLE `EmailForm` (
`efid` int NOT NULL,
`title` varchar(255) NOT NULL,
`body` text NOT NULL,
`signature` text NOT NULL,
`type` int NOT NULL,
`creator` bigint NOT NULL,
`ctime` datetime NOT NULL,
`state` int NOT NULL,
`unit` int NOT NULL,
PRIMARY KEY (`efid`, `ctime`) 
);

CREATE TABLE `In` (
`unitid` int NOT NULL,
`userid` bigint NOT NULL,
`ctime` datetime NOT NULL,
PRIMARY KEY (`userid`, `unitid`, `ctime`) 
);

CREATE TABLE `TestAnswer` (
`taid` bigint NOT NULL,
`ctime` datetime NOT NULL,
`state` int NOT NULL,
`testid` bigint NOT NULL,
`point` int NOT NULL,
`stime` datetime NOT NULL,
PRIMARY KEY (`taid`, `ctime`) 
);

CREATE TABLE `Answer` (
`taid` bigint NOT NULL,
`qid` bigint NOT NULL,
`answer` int NOT NULL,
PRIMARY KEY (`taid`, `qid`) 
);

CREATE TABLE `CVTest` (
`testid` bigint NOT NULL,
`cvid` bigint NOT NULL,
PRIMARY KEY (`testid`, `cvid`) 
);

CREATE TABLE `CVTestAnswer` (
`taid` bigint NOT NULL,
`cvid` bigint NOT NULL,
PRIMARY KEY (`taid`, `cvid`) 
);

CREATE TABLE `AnswerAccessCode` (
`taid` bigint NOT NULL,
`code` binary(224) NOT NULL,
PRIMARY KEY (`code`) 
);

CREATE TABLE `CVFile` (
`cvid` bigint NOT NULL,
`file` mediumtext NOT NULL,
`ctime` datetime NOT NULL,
PRIMARY KEY (`cvid`) 
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
ALTER TABLE `Comment` ADD CONSTRAINT `fk_Comment_CV_1` FOREIGN KEY (`cvid`) REFERENCES `CV` (`cvid`);
ALTER TABLE `Info` ADD CONSTRAINT `fk_Info_Field_1` FOREIGN KEY (`field`) REFERENCES `RequiredField` (`fieldid`);
ALTER TABLE `Info` ADD CONSTRAINT `fk_Info_CV_1` FOREIGN KEY (`cvid`) REFERENCES `CV` (`cvid`);
ALTER TABLE `TestAccessCode` ADD CONSTRAINT `fk_CVAccessCode_Test_1` FOREIGN KEY (`testid`) REFERENCES `Test` (`tid`);
ALTER TABLE `Bag` ADD CONSTRAINT `fk_Bag_Request_1` FOREIGN KEY (`request`) REFERENCES `Request` (`rid`);
ALTER TABLE `In` ADD CONSTRAINT `fk_In_Unit_1` FOREIGN KEY (`unitid`) REFERENCES `Unit` (`uid`);
ALTER TABLE `In` ADD CONSTRAINT `fk_In_User_1` FOREIGN KEY (`userid`) REFERENCES `User` (`uid`);
ALTER TABLE `Request` ADD CONSTRAINT `fk_Request_Unit_1` FOREIGN KEY (`unitid`) REFERENCES `Unit` (`uid`);
ALTER TABLE `Question` ADD CONSTRAINT `fk_Question_Unit_1` FOREIGN KEY (`unit`) REFERENCES `Unit` (`uid`);
ALTER TABLE `Config` ADD CONSTRAINT `fk_Config_Unit_1` FOREIGN KEY (`unit`) REFERENCES `Unit` (`uid`);
ALTER TABLE `EmailForm` ADD CONSTRAINT `fk_EmailField_Unit_1` FOREIGN KEY (`unit`) REFERENCES `Unit` (`uid`);
ALTER TABLE `CV` ADD CONSTRAINT `fk_CV_Bag_1` FOREIGN KEY (`bagid`) REFERENCES `Bag` (`bid`);
ALTER TABLE `RequiredField` ADD CONSTRAINT `fk_RequiredField_Unit_1` FOREIGN KEY (`unit`) REFERENCES `Unit` (`uid`);
ALTER TABLE `Answer` ADD CONSTRAINT `fk_Answer_TestAnswer_1` FOREIGN KEY (`taid`) REFERENCES `TestAnswer` (`taid`);
ALTER TABLE `Answer` ADD CONSTRAINT `fk_Answer_Question_1` FOREIGN KEY (`qid`) REFERENCES `Question` (`qid`);
ALTER TABLE `CVTest` ADD CONSTRAINT `fk_CVTest_Test_1` FOREIGN KEY (`testid`) REFERENCES `Test` (`tid`);
ALTER TABLE `CVTest` ADD CONSTRAINT `fk_CVTest_CV_1` FOREIGN KEY (`cvid`) REFERENCES `CV` (`cvid`);
ALTER TABLE `CVTestAnswer` ADD CONSTRAINT `fk_CVTestAnswer_TestAnswer_1` FOREIGN KEY (`taid`) REFERENCES `TestAnswer` (`taid`);
ALTER TABLE `CVTestAnswer` ADD CONSTRAINT `fk_CVTestAnswer_CV_1` FOREIGN KEY (`cvid`) REFERENCES `CV` (`cvid`);
ALTER TABLE `TestAnswer` ADD CONSTRAINT `fk_TestAnswer_Test_1` FOREIGN KEY (`testid`) REFERENCES `Test` (`tid`);
ALTER TABLE `CVFile` ADD CONSTRAINT `fk_CVFile_CV_1` FOREIGN KEY (`cvid`) REFERENCES `CV` (`cvid`);
ALTER TABLE `AnswerAccessCode` ADD CONSTRAINT `fk_AnswerAccessCode_TestAnswer_1` FOREIGN KEY (`taid`) REFERENCES `TestAnswer` (`taid`);


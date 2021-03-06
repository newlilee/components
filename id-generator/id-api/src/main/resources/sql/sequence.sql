DROP TABLE IF EXISTS `sequence`;
CREATE TABLE `sequence`
(
    `seq_type` char(16) NOT NULL,
    `cur_id`   bigint(20) NOT NULL DEFAULT '0',
    PRIMARY KEY (`seq_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

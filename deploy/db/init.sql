-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema cmsfs
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `cmsfs` ;

-- -----------------------------------------------------
-- Schema cmsfs
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `cmsfs` DEFAULT CHARACTER SET utf8 ;
USE `cmsfs` ;

-- -----------------------------------------------------
-- Table `cmsfs`.`core_collect`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmsfs`.`core_collect` ;

CREATE TABLE IF NOT EXISTS `cmsfs`.`core_collect` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `path` JSON NOT NULL,
  `args` JSON NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cmsfs`.`core_connector_jdbc`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmsfs`.`core_connector_jdbc` ;

CREATE TABLE IF NOT EXISTS `cmsfs`.`core_connector_jdbc` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `category` VARCHAR(45) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `url` TEXT NOT NULL,
  `user` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cmsfs`.`core_connector_ssh`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmsfs`.`core_connector_ssh` ;

CREATE TABLE IF NOT EXISTS `cmsfs`.`core_connector_ssh` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `ip` VARCHAR(45) NOT NULL,
  `port` INT(11) NOT NULL,
  `user` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NULL,
  `private_key` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cmsfs`.`core_format_analyze`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmsfs`.`core_format_analyze` ;

CREATE TABLE IF NOT EXISTS `cmsfs`.`core_format_analyze` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `path` JSON NOT NULL,
  `args` JSON NULL,
  `collect_id` INT(11) NOT NULL,
  `_index` VARCHAR(45) NOT NULL,
  `_type` VARCHAR(45) NOT NULL,
  `_metric` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cmsfs`.`core_monitor_detail`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmsfs`.`core_monitor_detail` ;

CREATE TABLE IF NOT EXISTS `cmsfs`.`core_monitor_detail` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cron` VARCHAR(45) NOT NULL,
  `connector_mode` VARCHAR(45) NOT NULL,
  `connector_id` INT(11) NOT NULL,
  `collect_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cmsfs`.`core_format_alarm`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmsfs`.`core_format_alarm` ;

CREATE TABLE IF NOT EXISTS `cmsfs`.`core_format_alarm` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `path` JSON NOT NULL,
  `args` JSON NULL,
  `collect_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cmsfs`.`core_connector_mongo`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmsfs`.`core_connector_mongo` ;

CREATE TABLE IF NOT EXISTS `cmsfs`.`core_connector_mongo` (
  `id` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `cmsfs`.`core_collect`
-- -----------------------------------------------------
START TRANSACTION;
USE `cmsfs`;
INSERT INTO `cmsfs`.`core_collect` (`id`, `name`, `path`, `args`) VALUES (1, 'disk_space', '[\"os\",\"centos\",\"common\",\"disk_space\",\"collect.sh\"]', NULL);
INSERT INTO `cmsfs`.`core_collect` (`id`, `name`, `path`, `args`) VALUES (2, 'tablespace_space', '[\"oracle\",\"common\",\"tablespace_space\",\"collect.sql\"]', NULL);

COMMIT;


-- -----------------------------------------------------
-- Data for table `cmsfs`.`core_connector_jdbc`
-- -----------------------------------------------------
START TRANSACTION;
USE `cmsfs`;
INSERT INTO `cmsfs`.`core_connector_jdbc` (`id`, `category`, `name`, `url`, `user`, `password`) VALUES (1, 'oracle', 'yali2', 'jdbc:oracle:thin:@10.65.193.25:1521/orayali', 'system', 'oracle');

COMMIT;


-- -----------------------------------------------------
-- Data for table `cmsfs`.`core_connector_ssh`
-- -----------------------------------------------------
START TRANSACTION;
USE `cmsfs`;
INSERT INTO `cmsfs`.`core_connector_ssh` (`id`, `name`, `ip`, `port`, `user`, `password`, `private_key`) VALUES (1, 'yali2', '10.65.193.25', 22, 'oracle', NULL, NULL);

COMMIT;


-- -----------------------------------------------------
-- Data for table `cmsfs`.`core_format_analyze`
-- -----------------------------------------------------
START TRANSACTION;
USE `cmsfs`;
INSERT INTO `cmsfs`.`core_format_analyze` (`id`, `path`, `args`, `collect_id`, `_index`, `_type`, `_metric`) VALUES (1, '[\"os\",\"centos\",\"common\",\"disk_space\"]', NULL, 1, 'monitor', 'os', 'disk_space');
INSERT INTO `cmsfs`.`core_format_analyze` (`id`, `path`, `args`, `collect_id`, `_index`, `_type`, `_metric`) VALUES (2, '[\"oracle\",\"common\",\"tablespace_space\"]', NULL, 2, 'monitor', 'oracle', 'tablespace_space');

COMMIT;


-- -----------------------------------------------------
-- Data for table `cmsfs`.`core_monitor_detail`
-- -----------------------------------------------------
START TRANSACTION;
USE `cmsfs`;
INSERT INTO `cmsfs`.`core_monitor_detail` (`id`, `cron`, `connector_mode`, `connector_id`, `collect_id`) VALUES (1, '0/5 * * * * ?', 'rdb', 1, 1);

COMMIT;


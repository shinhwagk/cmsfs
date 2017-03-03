-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema oso_monitor
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema oso_monitor
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `oso_monitor` DEFAULT CHARACTER SET utf8 ;
USE `oso_monitor` ;

-- -----------------------------------------------------
-- Table `oso_monitor`.`MonitorAlarm`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oso_monitor`.`MonitorAlarm` (
  `id` INT(11) NOT NULL,
  `monitorId` VARCHAR(45) NOT NULL,
  `args` JSON NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `oso_monitor`.`alarms`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oso_monitor`.`alarms` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `script` TEXT NOT NULL,
  `state` TINYINT(1) NOT NULL DEFAULT '1',
  `args` JSON NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `oso_monitor`.`machine`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oso_monitor`.`machine` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `tags` JSON NOT NULL,
  `ip` VARCHAR(45) NOT NULL,
  `state` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `oso_monitor`.`connector_mode_jdbc`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oso_monitor`.`connector_mode_jdbc` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `machine_id` INT(11) NOT NULL,
  `url` TEXT NOT NULL,
  `user` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `tags` JSON NOT NULL,
  `state` TINYINT(1) NOT NULL,
  `category` VARCHAR(45) NOT NULL,
  `category_version` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_jdbc_machine_id_idx` (`machine_id` ASC),
  CONSTRAINT `fk_jdbc_machine_id`
    FOREIGN KEY (`machine_id`)
    REFERENCES `oso_monitor`.`machine` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `oso_monitor`.`connector_mode_ssh`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oso_monitor`.`connector_mode_ssh` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `machine_id` INT(11) NOT NULL,
  `user` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NULL DEFAULT NULL,
  `private_key` VARCHAR(45) NULL DEFAULT NULL,
  `port` INT(11) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `tags` JSON NOT NULL,
  `state` TINYINT(1) NULL DEFAULT NULL,
  `category` VARCHAR(45) NOT NULL,
  `category_version` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `machine_id_UNIQUE` (`machine_id` ASC),
  INDEX `fk_ssh_machine_id_idx` (`machine_id` ASC),
  CONSTRAINT `fk_ssh_machine_id`
    FOREIGN KEY (`machine_id`)
    REFERENCES `oso_monitor`.`machine` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `oso_monitor`.`groups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oso_monitor`.`groups` (
  `group_id` INT(11) NOT NULL COMMENT '一个组的所有用户，统一只能发送一个模式',
  `group_name` VARCHAR(45) NOT NULL,
  `send_mode` INT(11) NOT NULL,
  PRIMARY KEY (`group_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `oso_monitor`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oso_monitor`.`users` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `group_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_group_id_idx` (`group_id` ASC),
  CONSTRAINT `fk_group_id`
    FOREIGN KEY (`group_id`)
    REFERENCES `oso_monitor`.`groups` (`group_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `oso_monitor`.`email`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oso_monitor`.`email` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `address` VARCHAR(45) NOT NULL,
  `user_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_user_id_idx` (`user_id` ASC),
  CONSTRAINT `fk_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `oso_monitor`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `oso_monitor`.`metric`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oso_monitor`.`metric` (
  `id` INT(11) NOT NULL,
  `state` TINYINT(1) NOT NULL DEFAULT '1',
  `name` VARCHAR(45) NOT NULL,
  `mode` VARCHAR(45) NOT NULL,
  `tags` JSON NOT NULL,
  `cron` VARCHAR(45) NOT NULL,
  `category` VARCHAR(45) NOT NULL,
  `category_version` JSON NOT NULL,
  `description` TEXT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `oso_monitor`.`monitor_detail`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oso_monitor`.`monitor_detail` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `metric_id` INT(11) NOT NULL,
  `connector_id` INT(11) NOT NULL,
  `cron` VARCHAR(45) NOT NULL,
  `collect_args` JSON NULL DEFAULT NULL,
  `analyze_args` JSON NULL DEFAULT NULL,
  `alarm` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_metric_id_idx` (`metric_id` ASC),
  CONSTRAINT `fk_metric_id`
    FOREIGN KEY (`metric_id`)
    REFERENCES `oso_monitor`.`metric` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 45
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `oso_monitor`.`monitor_depository`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oso_monitor`.`monitor_depository` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `monitor_id` INT(11) NOT NULL,
  `collect` JSON NOT NULL,
  `analyze` JSON NULL DEFAULT NULL,
  `alarm` JSON NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_collect_monitor_id_idx` (`monitor_id` ASC),
  CONSTRAINT `fk_monitor_detail_id`
    FOREIGN KEY (`monitor_id`)
    REFERENCES `oso_monitor`.`monitor_detail` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `oso_monitor`.`phone`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oso_monitor`.`phone` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `number` VARCHAR(45) NOT NULL,
  `user_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_phone_user_id_idx` (`user_id` ASC),
  CONSTRAINT `fk_phone_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `oso_monitor`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `oso_monitor`.`machine`
-- -----------------------------------------------------
START TRANSACTION;
USE `oso_monitor`;
INSERT INTO `oso_monitor`.`machine` (`id`, `name`, `tags`, `ip`, `state`) VALUES (1, 'yali2', '[]', '10.65.193.25', true);

COMMIT;


-- -----------------------------------------------------
-- Data for table `oso_monitor`.`connector_mode_ssh`
-- -----------------------------------------------------
START TRANSACTION;
USE `oso_monitor`;
INSERT INTO `oso_monitor`.`connector_mode_ssh` (`id`, `machine_id`, `user`, `password`, `private_key`, `port`, `name`, `tags`, `state`, `category`, `category_version`) VALUES (1, 1, 'oracle', NULL, NULL, 22, 'yali2', '[]', true, 'CENTOS', '6');

COMMIT;


-- -----------------------------------------------------
-- Data for table `oso_monitor`.`metric`
-- -----------------------------------------------------
START TRANSACTION;
USE `oso_monitor`;
INSERT INTO `oso_monitor`.`metric` (`id`, `state`, `name`, `mode`, `tags`, `cron`, `category`, `category_version`, `description`) VALUES (1, true, 'disk_space', 'SSH', '[]', '0/5 * * * * ?', 'CENTOS', '[]', '1111');

COMMIT;


-- -----------------------------------------------------
-- Data for table `oso_monitor`.`monitor_detail`
-- -----------------------------------------------------
START TRANSACTION;
USE `oso_monitor`;
INSERT INTO `oso_monitor`.`monitor_detail` (`id`, `metric_id`, `connector_id`, `cron`, `collect_args`, `analyze_args`, `alarm`) VALUES (1, 1, 1, '0/5 * * * * ?', NULL, NULL, DEFAULT);

COMMIT;


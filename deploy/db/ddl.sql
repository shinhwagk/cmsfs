DROP SCHEMA IF EXISTS `cmsfs` ;

CREATE SCHEMA IF NOT EXISTS `cmsfs` DEFAULT CHARACTER SET utf8 ;

DROP TABLE IF EXISTS `cmsfs`.`core_format_analyze` ;
CREATE TABLE IF NOT EXISTS `cmsfs`.`core_format_analyze` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `path` JSON NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `args` JSON NULL,
  `collect_id` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

DROP TABLE IF EXISTS `cmsfs`.`core_format_alarm` ;
CREATE TABLE IF NOT EXISTS `cmsfs`.`core_format_alarm` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `path` JSON NULL,
  `name` VARCHAR(45) NOT NULL,
  `args` JSON NULL,
  `collect_id` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

DROP TABLE IF EXISTS `cmsfs`.`core_collect` ;
CREATE TABLE IF NOT EXISTS `cmsfs`.`core_collect` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `path` JSON NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `args` JSON NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

DROP TABLE IF EXISTS `cmsfs`.`core_connector_jdbc` ;
CREATE TABLE IF NOT EXISTS `cmsfs`.`core_connector_jdbc` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `url` TEXT NOT NULL,
  `user` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB;

DROP TABLE IF EXISTS `cmsfs`.`core_monitor_detail` ;
CREATE TABLE IF NOT EXISTS `cmsfs`.`core_monitor_detail` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cron` VARCHAR(45) NOT NULL,
  `category` VARCHAR(45) NOT NULL,
  `connector_id` INT(11) NOT NULL,
  `collect_id` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

DROP TABLE IF EXISTS `cmsfs`.`core_connector_ssh` ;
CREATE TABLE IF NOT EXISTS `cmsfs`.`core_connector_ssh` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `ip` VARCHAR(45) NOT NULL,
  `port` INT(11) NOT NULL,
  `user` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NULL,
  `private_key` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;
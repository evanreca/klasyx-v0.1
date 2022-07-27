-- create_klasyx_db
-- Authored by: Evan Reca
-- Forward Engineered

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema create_klasyx_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `create_klasyx_db` DEFAULT CHARACTER SET utf8 ;
USE `create_klasyx_db` ;

-- -----------------------------------------------------
-- Table `create_klasyx_db`.`composers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `create_klasyx_db`.`composers` (
  `composer_id` 		INT 			NOT NULL 	AUTO_INCREMENT,
  `composer_name_first` VARCHAR(45)		NULL,
  `composer_name_last`  VARCHAR(45)  	NULL,
  `composer_img_path`   VARCHAR(255)  	NOT NULL,
  `composer_desc` 		VARCHAR(512) 	NULL,
  PRIMARY KEY (`composer_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `create_klasyx_db`.`songs`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `create_klasyx_db`.`songs` (
  `song_id` 			  INT 		   NOT NULL 	AUTO_INCREMENT,
  `title`  				  VARCHAR(255) NOT NULL,
  `release_year`  		  INT 		   NULL,
  `song_length` 		  VARCHAR(8)   NOT NULL,
  `mp3_path` 			  VARCHAR(255) NOT NULL,
  `composers_composer_id` INT 		   NOT NULL,
  
  PRIMARY KEY (`song_id`),
  INDEX `fk_songs_composers_idx` (`composers_composer_id` ASC) VISIBLE,
  CONSTRAINT `fk_songs_composers`
    FOREIGN KEY (`composers_composer_id`)
    REFERENCES `create_klasyx_db`.`composers` (`composer_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)

ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `create_klasyx_db`.`accounts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `create_klasyx_db`.`accounts` (
  `account_id`       INT 		  NOT NULL 		AUTO_INCREMENT,
  `user_name_first`  VARCHAR(45)  NOT NULL,
  `user_name_last`   VARCHAR(45)  NOT NULL,
  `account_username` VARCHAR(45)  NOT NULL,
  `account_password` VARCHAR(45)  NOT NULL,
  `profile_img_path` VARCHAR(128) NOT NULL,
  
  PRIMARY KEY (`account_id`),
  UNIQUE INDEX `account_username_UNIQUE` (`account_username` ASC) VISIBLE,
  UNIQUE INDEX `account_id_UNIQUE` (`account_id` ASC) VISIBLE)

ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `create_klasyx_db`.`accounts_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `create_klasyx_db`.`accounts_history` (
  `accounts_history_id` INT 	 NOT NULL,
  `accounts_account_id` INT 	 NOT NULL,
  `songs_song_id` 		INT 	 NOT NULL,
  `time_song_listened`  DATETIME NOT NULL,
  
  INDEX `fk_accounts_history_accounts1_idx` (`accounts_account_id` ASC) VISIBLE,
  INDEX `fk_accounts_history_songs1_idx` (`songs_song_id` ASC) VISIBLE,
  PRIMARY KEY (`accounts_history_id`),
  
  CONSTRAINT `fk_accounts_history_accounts1`
    FOREIGN KEY (`accounts_account_id`)
    REFERENCES `create_klasyx_db`.`accounts` (`account_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  
  CONSTRAINT `fk_accounts_history_songs1`
    FOREIGN KEY (`songs_song_id`)
    REFERENCES `create_klasyx_db`.`songs` (`song_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)

ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `create_klasyx_db`.`playlists`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `create_klasyx_db`.`playlists` (
  `playlist_id` 		INT 		 NOT NULL AUTO_INCREMENT,
  `playlist_name` 		VARCHAR(255) NOT NULL,
  `songs_song_id` 		INT 		 NOT NULL,
  `accounts_account_id` INT 		 NOT NULL,
  
  PRIMARY KEY (`playlist_id`),
  INDEX `fk_playlists_songs1_idx` (`songs_song_id` ASC) VISIBLE,
  INDEX `fk_playlists_accounts1_idx` (`accounts_account_id` ASC) VISIBLE,
  
  CONSTRAINT `fk_playlists_songs1`
    FOREIGN KEY (`songs_song_id`)
    REFERENCES `create_klasyx_db`.`songs` (`song_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  
  CONSTRAINT `fk_playlists_accounts1`
    FOREIGN KEY (`accounts_account_id`)
    REFERENCES `create_klasyx_db`.`accounts` (`account_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)

ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- Insert Test Data Into Tables

INSERT INTO accounts (account_id, user_name_first, user_name_last, account_username, account_password, profile_img_path) VALUES
(DEFAULT, 'John', 'Smith', 'jsmith77', 'password', 'profile\image.png'),
();

INSERT INTO songs (song_id, title, release_year, song_length, mp3_path, composers_composer_id) VALUES
(DEFAULT, 'Introduction and Rondo Cappricioso', 1863, '9:15', 'introduction_and_rondo_cappricioso.mp3, 1' ),
();

INSERT INTO accounts_history (accounts_history_id, accounts_account_id, songs_song_id, time_song_listened) VALUES
(DEFAULT, 1, 1, 1, 2020-11-11),
();

INSERT INTO composers (composer_id, composer_name_first, composer_name_last, composer_img_path, composer_desc) VALUES
(DEFAULT, 'Charles-Camille', 'Saint-Saëns', 'Saint-Saëns.jpeg', 'A French composer, organist, conductor and pianist of the Romantic era.'),
();

INSERT INTO playlists (playlist_id, playlist_name, songs_song_id, accounts_account_id) VALUES
(DEFAULT, 'My Favorites', 1, 1),
();

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;


DROP TABLE IF EXISTS `folders`;
CREATE TABLE IF NOT EXISTS `folders` (
  `id` varchar(36) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `parent_id` varchar(36) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `title` varchar(256) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `path` varchar(512) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  KEY `parent_id` (`parent_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO `folders` (`id`, `parent_id`, `title`, `path`) VALUES
('0', '-1', 'ROOT FOLDER', '0$'),
('1', '0', 'Videos', 'Y:\\video'),
('2', '0', 'Music', 'Y:\\mp3'),
('3', '0', 'Pictures', 'Y:\\photos'),
('4', '2', 'Artists', '2$4$'),
('5', '2', 'Folders', '5$');

DROP TABLE IF EXISTS `pictures`;
CREATE TABLE IF NOT EXISTS `pictures` (
  `id` varchar(36) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `parent_id` varchar(36) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `title` varchar(128) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `path` varchar(512) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `size` bigint(20) unsigned NOT NULL,
  `mime_type` varchar(45) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `resolution` varchar(16) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `color_depth` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path_UNIQUE` (`path`),
  FULLTEXT KEY `parent_id_FULLTEXT` (`parent_id`),
  FULLTEXT KEY `title_FULLTEXT` (`title`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `songs`;
CREATE TABLE IF NOT EXISTS `songs` (
  `id` varchar(36) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `parent_id` varchar(36) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `title` varchar(256) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `size` bigint(12) unsigned NOT NULL,
  `path` varchar(512) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `duration` varchar(16) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `bits_per_sample` int(10) unsigned NOT NULL,
  `mime_type` varchar(16) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `sample_frequency` int(10) unsigned NOT NULL,
  `nr_audio_channels` int(1) unsigned NOT NULL,
  `position` int(2) unsigned DEFAULT NULL,
  `performer` varchar(128) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  `album_title` varchar(128) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  `album_performer` varchar(128) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  `album_tracks` int(2) unsigned DEFAULT NULL,
  `album_released` varchar(16) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  KEY `parent_id` (`parent_id`),
  FULLTEXT KEY `performer` (`performer`,`album_title`,`album_performer`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `videos`;
CREATE TABLE IF NOT EXISTS `videos` (
  `id` varchar(36) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `parent_id` varchar(36) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `title` varchar(256) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `size` bigint(12) unsigned NOT NULL,
  `path` varchar(512) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `duration` varchar(16) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `bits_per_sample` int(10) unsigned NOT NULL,
  `bitrate` int(10) unsigned NOT NULL,
  `color_depth` int(2) unsigned NOT NULL,
  `mime_type` varchar(16) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `resolution` varchar(10) CHARACTER SET latin1 COLLATE latin1_general_cs NOT NULL,
  `sample_frequency` int(10) unsigned NOT NULL,
  `nr_audio_channels` int(1) unsigned NOT NULL,
  `subtitles_title` varchar(32) CHARACTER SET latin1 COLLATE latin1_general_cs DEFAULT NULL,
  `subtitles_size` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  KEY `parent_id` (`parent_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


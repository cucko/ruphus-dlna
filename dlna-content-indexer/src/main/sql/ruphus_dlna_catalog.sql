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

--INSERT INTO `folders` (`id`, `parent_id`, `title`, `path`) VALUES
--('0', '-1', 'ROOT FOLDER', '0$'),
--('1', '0', 'Videos', 'Y:\\video'),
--('2', '0', 'Music', 'Y:\\mp3'),
--('3', '0', 'Pictures', 'Y:\\photos'),
--('4', '2', 'Artists', '2$4$'),
--('5', '2', 'Folders', '5$');

CREATE TABLE IF NOT EXISTS `media` (
  `id` varchar(36) COLLATE latin1_general_cs NOT NULL,
  `parent_id` varchar(36) COLLATE latin1_general_cs NOT NULL,
  `title` varchar(128) COLLATE latin1_general_cs NOT NULL,
  `path` varchar(512) COLLATE latin1_general_cs NOT NULL,
  `size` bigint(20) unsigned NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `type` varchar(1) COLLATE latin1_general_cs NOT NULL,
  `mime_type` varchar(45) COLLATE latin1_general_cs NOT NULL,
  `resolution` varchar(16) COLLATE latin1_general_cs DEFAULT NULL,
  `color_depth` varchar(10) COLLATE latin1_general_cs DEFAULT NULL,
  `duration` varchar(16) COLLATE latin1_general_cs DEFAULT NULL,
  `bits_per_sample` int(10) unsigned DEFAULT NULL,
  `sample_frequency` int(10) unsigned DEFAULT NULL,
  `nr_audio_channels` int(1) unsigned DEFAULT NULL,
  `position` int(2) unsigned DEFAULT NULL,
  `performer` varchar(128) COLLATE latin1_general_cs DEFAULT NULL,
  `album_title` varchar(128) COLLATE latin1_general_cs DEFAULT NULL,
  `album_performer` varchar(128) COLLATE latin1_general_cs DEFAULT NULL,
  `album_tracks` int(2) unsigned DEFAULT NULL,
  `album_released` varchar(16) COLLATE latin1_general_cs DEFAULT NULL,
  `bitrate` int(10) unsigned NOT NULL,
  `subtitles_title` varchar(128) COLLATE latin1_general_cs DEFAULT NULL,
  `subtitles_size` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parent_id` (`parent_id`,`path`,`type`,`album_performer`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs;


DROP TABLE IF EXISTS folders;
CREATE TABLE IF NOT EXISTS folders (
  id varchar(36) NOT NULL,
  parent_id varchar(36) NOT NULL,
  title varchar(256) NULL,
  path varchar(256) NOT NULL,
  last_modified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY path (path),
  KEY parent_id (parent_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--INSERT INTO folders (id, parent_id, title, path) VALUES
--('0', '-1', 'ROOT FOLDER', '0$'),
--('1', '0', 'Videos', 'Y:\\video'),
--('2', '0', 'Music', 'Y:\\mp3'),
--('3', '0', 'Pictures', 'Y:\\photos'),
--('4', '2', 'Artists', '2$4$'),
--('5', '2', 'Folders', '5$');

CREATE TABLE IF NOT EXISTS `media` (
  `id` varchar(36) NOT NULL,
  `parent_id` varchar(36) NOT NULL,
  `title` varchar(128) NOT NULL,
  `path` varchar(256) NOT NULL,
  `size` bigint(20) unsigned NOT NULL,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `type` varchar(1) NOT NULL,
  `mime_type` varchar(45) NOT NULL,
  `resolution` varchar(16) DEFAULT NULL,
  `color_depth` varchar(10) DEFAULT NULL,
  `duration` varchar(16) DEFAULT NULL,
  `bits_per_sample` varchar(10) DEFAULT NULL,
  `sample_frequency` varchar(10) DEFAULT NULL,
  `nr_audio_channels` varchar(5) DEFAULT NULL,
  `position` varchar(2) DEFAULT NULL,
  `performer` varchar(128) DEFAULT NULL,
  `album_title` varchar(128) DEFAULT NULL,
  `album_performer` varchar(128) DEFAULT NULL,
  `album_tracks` varchar(2) DEFAULT NULL,
  `album_released` varchar(16) DEFAULT NULL,
  `bitrate` varchar(10) DEFAULT NULL,
  `subtitles_title` varchar(128) DEFAULT NULL,
  `subtitles_size` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  KEY `parent_id` (`parent_id`),
  KEY `type` (`type`),
  KEY `title` (`title`),
  KEY `performer_2` (`performer`),
  KEY `album_title` (`album_title`),
  KEY `album_performer_2` (`album_performer`),
  FULLTEXT KEY `performer` (`performer`),
  FULLTEXT KEY `album_performer` (`album_performer`),
  FULLTEXT KEY `title_2` (`title`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


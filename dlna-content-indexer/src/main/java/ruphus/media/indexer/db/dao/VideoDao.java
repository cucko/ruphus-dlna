package ruphus.media.indexer.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import ruphus.media.indexer.db.model.Video;

public class VideoDao extends AudioDao<Video> {
	
	@Override
	protected Video getAssetInstance() {
		return new Video();
	}
	
	@Override
	protected void addInfos(Video video, Map<String, Object> columnValues) {
		super.addInfos(video, columnValues);
		
		columnValues.put("bitrate", video.getBitrate());
		columnValues.put("color_depth", video.getColorDepth());
		columnValues.put("resolution", video.getResolution());

		if (video.getSubtitlesTitle() != null) {
			columnValues.put("subtitles_title", video.getSubtitlesTitle());
			columnValues.put("subtitles_size", video.getSubtitlesSize());
		}
	}
	
	@Override
	protected void fillAssetProperties(Video video, ResultSet rs) throws SQLException {
		super.fillAssetProperties(video, rs);
		
		video.setBitrate( rs.getString("bitrate") );
		video.setColorDepth( rs.getString("color_depth") );
		video.setResolution( rs.getString("resolution") );
		video.setSubtitlesSize( rs.getInt("subtitles_size") );
		video.setSubtitlesTitle( rs.getString("subtitles_title") );
	}
	
}

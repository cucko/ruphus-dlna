package ruphus.media.indexer.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import ruphus.media.indexer.db.model.Picture;

public class PictureDao extends MediumDao<Picture> {
	
	@Override
	protected Picture getAssetInstance() {
		return new Picture();
	}
	
	@Override
	protected void addInfos(Picture picture, Map<String, Object> columnValues) {
		super.addInfos(picture, columnValues);
		
		columnValues.put("resolution", picture.getResolution());
		columnValues.put("color_depth", picture.getColorDepth());
	}
	
	@Override
	protected void fillAssetProperties(Picture picture, ResultSet rs) throws SQLException {
		super.fillAssetProperties(picture, rs);
		
		picture.setColorDepth( rs.getString("color_depth") );
		picture.setResolution( rs.getString("resolution") );
	}
	
}

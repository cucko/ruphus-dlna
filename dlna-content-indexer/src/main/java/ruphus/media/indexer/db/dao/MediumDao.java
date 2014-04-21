package ruphus.media.indexer.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import ruphus.media.indexer.db.model.Medium;

public abstract class MediumDao<M extends Medium> extends AssetDao<M> {

	@Override
	protected final String getTable() {
		return "media";
	}
	
	@Override
	protected void addInfos(M medium, Map<String, Object> columnValues) {
		super.addInfos(medium, columnValues);
		
		columnValues.put("size", medium.getSize());
		columnValues.put("mime_type", medium.getMimeType());
		columnValues.put("type", medium.getType());
	}
	
	@Override
	protected void fillAssetProperties(M medium, ResultSet rs) throws SQLException {
		super.fillAssetProperties(medium, rs);
		medium.setSize( rs.getLong("size"));
		medium.setMimeType( rs.getString("mime_type") );
	}
	
}
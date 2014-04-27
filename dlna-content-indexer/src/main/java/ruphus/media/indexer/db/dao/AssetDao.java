package ruphus.media.indexer.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ruphus.media.indexer.db.model.Asset;

public abstract class AssetDao<A extends Asset> extends JdbcTemplate {

	abstract protected String getTable();
	abstract protected A getAssetInstance();
	
	protected void addInfos(A asset, Map<String, Object> columnValues) {
		columnValues.put("id", asset.getId());
		columnValues.put("parent_id", asset.getParentId());
		columnValues.put("title", asset.getTitle());
		columnValues.put("path", asset.getPath());
		columnValues.put("last_modified", asset.getLastModified());
	};
	
	protected void fillAssetProperties(A asset, ResultSet rs) throws SQLException {
		asset.setId( rs.getString("id") );
		asset.setParentId( rs.getString("parent_id"));
		asset.setTitle( rs.getString("title") );
		asset.setPath( rs.getString("path") );
		asset.setLastModified( rs.getTimestamp("last_modified") );
	}
	
	public void storeAsset(A asset) throws Exception {
		LinkedHashMap<String, Object> columnValues = new LinkedHashMap<String, Object>();
		
		if (asset.getId() == null) asset.setId( UUID.randomUUID().toString() );
		
		addInfos(asset, columnValues);
		
		insert(getTable(), columnValues);
	}
	
	public void deleteAsset(String id) throws Exception {
		String sql = new StringBuffer()
			.append("DELETE FROM ").append( getTable() )
			.append(" WHERE id = ?")
			.toString()
		;
		
		execute(sql, id);
	}
	
	public List<A> retrieveSubassets(String parentId) throws Exception {
		return retrieveSubassets(parentId, "title");
	}
	
	public List<A> retrieveSubassets(String parentId, String orderByClause) throws Exception {
		ResultSet rs = null;
		try {
			String folderSql = new StringBuffer()
				.append("SELECT * FROM ").append( getTable() )
				.append(" WHERE parent_id = ?")
				.append(" ORDER BY ").append(orderByClause)
				.toString()
			;
			
			rs = query(folderSql, parentId);
			
			ArrayList<A> subassets = new ArrayList<A>();
			while (rs.next()) {
				A subasset = getAssetInstance();
				fillAssetProperties(subasset, rs);
				subassets.add(subasset);
			}
			
			return subassets;
		}
		finally {
			closeSession(rs);
		}
	}
	
	public A retrieveAssetByPath(String path) throws Exception {
		ResultSet rs = null;
		try {
			String sql = new StringBuffer()
				.append("SELECT * FROM ").append( getTable() )
				.append(" WHERE path = ?" )
				.toString()
			;
			
			rs = query(sql, path);
			
			A asset = null;
			if (rs.first()) {
				asset = getAssetInstance();
				fillAssetProperties(asset, rs);
			}
			
			return asset;
		}
		finally {
			closeSession(rs);
		}
	}
	
	public A retrieveAssetById(String id) throws Exception {
		ResultSet rs = null;
		try {
			String folderSql = new StringBuffer()
				.append("SELECT * FROM ").append( getTable() )
				.append(" WHERE id = ?" )
				.toString()
			;
			
			rs = query(folderSql, id);
			
			A asset = null;
			if (rs.first()) {
				asset = getAssetInstance();
				fillAssetProperties(asset, rs);
			}
			
			return asset;
		}
		finally {
			closeSession(rs);
		}
	}
	
	public void updateAsset(A asset) throws Exception {
		LinkedHashMap<String, Object> columnValues = new LinkedHashMap<String, Object>();
		
		addInfos(asset, columnValues);
		
		update(getTable(), columnValues);
	}

	public void deleteAll() throws Exception{
		execute("DELETE FROM " + getTable());
	}
}

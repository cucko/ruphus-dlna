package ruphus.media.indexer.db.dao;

import ruphus.media.indexer.db.model.Folder;

public class FolderDao extends AssetDao<Folder> {
	
	@Override
	protected String getTable() {
		return "folders";
	}
	
	@Override
	protected Folder getAssetInstance() {
		return new Folder();
	}

}

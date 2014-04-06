package ruphus.media.indexer.db.model;

import java.util.Date;


public abstract class Asset {

	protected String id;
	protected String parentId;
	protected String title;
	protected String path;
	protected Date lastModified;
	
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	public Date getLastModified() {
		return lastModified;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}

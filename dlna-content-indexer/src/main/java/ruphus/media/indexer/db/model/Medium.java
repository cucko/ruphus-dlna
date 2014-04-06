package ruphus.media.indexer.db.model;


public abstract class Medium extends Asset {

	protected Long size;
	protected String mimeType;
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public void setSize(Long size) {
		this.size = size;
	}
	
	public Long getSize() {
		return size;
	}
	
}

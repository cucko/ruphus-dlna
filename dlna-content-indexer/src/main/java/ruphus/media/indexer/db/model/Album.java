package ruphus.media.indexer.db.model;

public class Album {
	
	protected String title;
	protected String performer;
	protected String released;
	protected String nTracks;

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setNTracks(String nTracks) {
		this.nTracks = nTracks;
	}
	
	public String getNTracks() {
		return nTracks;
	}

	public String getPerformer() {
		return performer;
	}

	public void setPerformer(String performer) {
		this.performer = performer;
	}

	public String getReleased() {
		return released;
	}
	
	public void setReleased(String released) {
		this.released = released;
	}

}

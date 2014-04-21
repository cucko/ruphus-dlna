package ruphus.media.indexer.db.model;

public class Song extends Audio {

	protected Album album;
	protected Integer position;
	protected String performer;

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getPerformer() {
		return performer;
	}

	public void setPerformer(String performer) {
		this.performer = performer;
	}

	@Override
	public String getType() {
		return "M";
	}
}

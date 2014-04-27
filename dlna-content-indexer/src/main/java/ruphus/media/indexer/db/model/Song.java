package ruphus.media.indexer.db.model;

public class Song extends Audio {

	protected Album album;
	protected String position;
	protected String performer;

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
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

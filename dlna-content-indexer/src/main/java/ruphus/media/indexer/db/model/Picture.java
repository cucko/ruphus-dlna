package ruphus.media.indexer.db.model;

public class Picture extends Medium {

	private String resolution;
	private String colorDepth;
	
	public void setColorDepth(String colorDepth) {
		this.colorDepth = colorDepth;
	}
	
	public String getColorDepth() {
		return colorDepth;
	}
	
	public String getResolution() {
		return resolution;
	}
	
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	
	@Override
	public String getType() {
		return "P";
	}
}

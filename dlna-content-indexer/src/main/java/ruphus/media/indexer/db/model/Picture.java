package ruphus.media.indexer.db.model;

public class Picture extends Medium {

	private String resolution;
	private Integer colorDepth;
	
	public void setColorDepth(Integer colorDepth) {
		this.colorDepth = colorDepth;
	}
	
	public Integer getColorDepth() {
		return colorDepth;
	}
	
	public String getResolution() {
		return resolution;
	}
	
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
}

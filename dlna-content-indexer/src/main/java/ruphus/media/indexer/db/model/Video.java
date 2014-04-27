package ruphus.media.indexer.db.model;

public class Video extends Audio {

	private String bitrate;
	private String colorDepth;
	private String resolution;
	private String subtitlesTitle;
	private Integer subtitlesSize;

	public String getBitrate() {
		return bitrate;
	}

	public void setBitrate(String bitrate) {
		this.bitrate = bitrate;
	}

	public String getColorDepth() {
		return colorDepth;
	}

	public void setColorDepth(String colorDepth) {
		this.colorDepth = colorDepth;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getSubtitlesTitle() {
		return subtitlesTitle;
	}

	public void setSubtitlesTitle(String subtitlesTitle) {
		this.subtitlesTitle = subtitlesTitle;
	}

	public Integer getSubtitlesSize() {
		return subtitlesSize;
	}

	public void setSubtitlesSize(Integer subtitlesSize) {
		this.subtitlesSize = subtitlesSize;
	}
	
	@Override
	public String getType() {
		return "V";
	}

}

package ruphus.media.indexer.db.model;

public class Video extends Audio {

	private Integer bitrate;
	private Integer colorDepth;
	private String resolution;
	private String subtitlesTitle;
	private Integer subtitlesSize;

	public Integer getBitrate() {
		return bitrate;
	}

	public void setBitrate(Integer bitrate) {
		this.bitrate = bitrate;
	}

	public Integer getColorDepth() {
		return colorDepth;
	}

	public void setColorDepth(Integer colorDepth) {
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

}

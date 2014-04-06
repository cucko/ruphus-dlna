package ruphus.media.indexer.db.model;


public class Audio extends Medium {

	protected Integer bitsPerSample;
	protected String duration;
	protected Integer sampleFrequency;
	protected Integer nrAudioChannels;
	
	public Integer getBitsPerSample() {
		return bitsPerSample;
	}

	public void setBitsPerSample(Integer bitsPerSample) {
		this.bitsPerSample = bitsPerSample;
	}

	public String getDuration() {
		return duration;
	}
	
	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Integer getSampleFrequency() {
		return sampleFrequency;
	}

	public void setSampleFrequency(Integer sampleFrequency) {
		this.sampleFrequency = sampleFrequency;
	}

	public Integer getNrAudioChannels() {
		return nrAudioChannels;
	}

	public void setNrAudioChannels(Integer nrAudioChannels) {
		this.nrAudioChannels = nrAudioChannels;
	}

}

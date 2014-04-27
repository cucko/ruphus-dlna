package ruphus.media.indexer.db.model;


public abstract class Audio extends Medium {

	protected String bitsPerSample;
	protected String duration;
	protected String sampleFrequency;
	protected String nrAudioChannels;
	
	public String getBitsPerSample() {
		return bitsPerSample;
	}

	public void setBitsPerSample(String bitsPerSample) {
		this.bitsPerSample = bitsPerSample;
	}

	public String getDuration() {
		return duration;
	}
	
	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getSampleFrequency() {
		return sampleFrequency;
	}

	public void setSampleFrequency(String sampleFrequency) {
		this.sampleFrequency = sampleFrequency;
	}

	public String getNrAudioChannels() {
		return nrAudioChannels;
	}

	public void setNrAudioChannels(String nrAudioChannels) {
		this.nrAudioChannels = nrAudioChannels;
	}
	
}

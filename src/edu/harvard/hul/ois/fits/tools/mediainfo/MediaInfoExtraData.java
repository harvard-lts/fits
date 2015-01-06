package edu.harvard.hul.ois.fits.tools.mediainfo;

public class MediaInfoExtraData {
	
	private String delay;
	// Audio Only
	private String audioSamplesCount;
	// Video Only
	private String frameCount;
	
	public String getDelay() {
		return delay;
	}
	public void setDelay(String delay) {
		this.delay = delay;
	}
	public String getAudioSamplesCount() {
		return audioSamplesCount;
	}
	public void setAudioSamplesCount(String audioSamplesCount) {
		this.audioSamplesCount = audioSamplesCount;
	}
	public String getFrameCount() {
		return frameCount;
	}
	public void setFrameCount(String frameCount) {
		this.frameCount = frameCount;
	}
}

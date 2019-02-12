package org.praisenter.data.media.tools;

public final class TranscodeSettings {
	private String commandTemplate;
	private boolean adjustVolumeEnabled;
	private double targetMeanVolume;
	
	public TranscodeSettings() {
		
	}

	public String getCommandTemplate() {
		return commandTemplate;
	}

	public void setCommandTemplate(String commandTemplate) {
		this.commandTemplate = commandTemplate;
	}

	public double getTargetMeanVolume() {
		return targetMeanVolume;
	}

	public void setTargetMeanVolume(double targetMeanVolume) {
		this.targetMeanVolume = targetMeanVolume;
	}

	public boolean isAdjustVolumeEnabled() {
		return adjustVolumeEnabled;
	}

	public void setAdjustVolumeEnabled(boolean adjustVolumeEnabled) {
		this.adjustVolumeEnabled = adjustVolumeEnabled;
	}
}

package org.praisenter.data.media;

public interface MediaConfiguration {
	/** The video target extension (will be treated as the destination format as well) */
	public static final String DEFAULT_VIDEO_EXTENSION = "mp4";
	
	/** The audio target extension (will be treated as the destination format as well) */
	public static final String DEFAULT_AUDIO_EXTENSION = "m4a";
	
	/** The target mean volume for audio/video (this was found through experimentation with Java FX) */
	public static final double DEFAULT_TARGET_MEAN_VOLUME = -30.0;
	
	/** The default FFmpeg command for transcoding */
	public static final String DEFAULT_TRANSCODE_COMMAND = "{ffmpeg} -v fatal -i {source} -y -ignore_unknown {volumeadjust} {target}";
	
	/** The default command (extract a frame every 2 seconds until we have 10 frames) */
	public static final String DEFAULT_VIDEO_FRAME_EXTRACT_COMMAND = "{ffmpeg} -v fatal -i {media} -vf fps=2 -frames:v 10 -vsync vfr {frame}";
	
	public int getThumbnailWidth();
	public int getThumbnailHeight();
	public boolean isAudioTranscodingEnabled();
	public boolean isVideoTranscodingEnabled();
	public boolean isVolumeAdjustmentEnabled();
	public double getTargetMeanVolume();
	public String getAudioTranscodeExtension();
	public String getVideoTranscodeExtension();
	public String getAudioTranscodeCommand();
	public String getVideoTranscodeCommand();
	public String getVideoFrameExtractCommand();
}

package org.praisenter.data.media;

public interface MediaConfiguration {
	/** The video target extension (will be treated as the destination format as well) */
	public static final String DEFAULT_VIDEO_EXTENSION = ".mp4";
	
	/** The audio target extension (will be treated as the destination format as well) */
	public static final String DEFAULT_AUDIO_EXTENSION = ".m4a";
	
	/** The default FFmpeg command for transcoding */
	public static final String DEFAULT_TRANSCODE_COMMAND = "{ffmpeg} -v fatal -i {source} -y -ignore_unknown {target}";
	
	/** The default command */
	public static final String DEFAULT_VIDEO_FRAME_EXTRACT_COMMAND = "{ffmpeg} -v fatal -ss 3 -i {media} -vf \"select=gt(scene\\,0.2)\" -frames:v 10 -vsync vfr {frame}";
	
	public int getThumbnailWidth();
	public int getThumbnailHeight();
	public boolean isAudioTranscodingEnabled();
	public boolean isVideoTranscodingEnabled();
	public String getAudioTranscodeExtension();
	public String getVideoTranscodeExtension();
	public String getAudioTranscodeCommand();
	public String getVideoTranscodeCommand();
	public String getVideoFrameExtractCommand();
}

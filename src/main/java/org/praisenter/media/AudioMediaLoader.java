package org.praisenter.media;

import io.humble.video.Codec;
import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerFormat;
import io.humble.video.DemuxerStream;
import io.humble.video.MediaDescriptor;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AudioMediaLoader extends AbstractMediaLoader implements MediaLoader {
	private static Logger LOGGER = LogManager.getLogger(AudioMediaLoader.class);
	
	public AudioMediaLoader(MediaThumbnailSettings settings) {
		super(settings);
	}
	
	@Override
	public boolean isSupported(String mimeType) {
		if (mimeType != null && mimeType.contains("audio")) {
			// ffmpeg/humble does not support midi
			if (mimeType.contains("midi")) {
				return false;
			}
			// handle any mimetype
			return true;
		}
		return false;
	}

	@Override
	public LoadedMedia load(Path path) throws IOException, FileNotFoundException, MediaFormatException {
		Demuxer demuxer = null;
		try {
			demuxer = Demuxer.make();
			demuxer.open(path.toString(), null, false, true, null, null);
			
			final DemuxerFormat format = demuxer.getFormat();
			final long length = demuxer.getDuration() / 1000 / 1000;
			
			final int streams = demuxer.getNumStreams();
			for (int i = 0; i < streams; i++) {
				final DemuxerStream stream = demuxer.getStream(i);
				final Decoder decoder = stream.getDecoder();
				if (decoder.getCodecType() == MediaDescriptor.Type.MEDIA_AUDIO) {
					final Codec codec = decoder.getCodec();
					final MediaCodec mc = new MediaCodec(CodecType.AUDIO, codec.getName(), codec.getLongName());
					final MediaFormat mf = new MediaFormat(format.getName().toLowerCase(), format.getLongName(), mc);
					final MediaMetadata metadata = MediaMetadata.forAudio(path, mf, length, null);
					BufferedImage thumb = settings.audioDefaultThumbnail;
					Media media = new Media(metadata, thumb);
					return new LoadedMedia(media, null);
				}
			}
			
			// no audio stream present
			throw new MediaFormatException();
		} catch (InterruptedException ex) {
			throw new IOException(ex);
		} finally {
			if (demuxer != null) {
				try {
					demuxer.close();
				} catch (Exception e) {
					// just eat them
					LOGGER.warn("Failed to close demuxer on: '" + path.toString() + "': ", e);
				}
			}
		}
	}
}

/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.resources.translations.Translations;

/**
 * {@link MediaLoader} that loads audio media.
 * @author William Bittle
 * @version 3.0.0
 */
public final class AudioMediaLoader extends AbstractMediaLoader implements MediaLoader {
	/** The class-level logger */
	private static Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Minimal constructor.
	 * @param settings the thumbnail generation settings
	 */
	public AudioMediaLoader(MediaThumbnailSettings settings) {
		super(settings);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#isSupported(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#load(java.nio.file.Path)
	 */
	@Override
	public LoadedMedia load(Path path) throws IOException, FileNotFoundException, MediaFormatException {
		if (Files.exists(path) && Files.isRegularFile(path)) {
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
						
						// FEATURE could add some code to call a service to go get the album art
						
						// use the default audio thumbnail
						BufferedImage thumb = this.settings.audioDefaultThumbnail;
						Media media = new Media(metadata, thumb);
						
						// pass null for the full-size image (we don't have one)
						return new LoadedMedia(media, null);
					}
				}
				
				LOGGER.warn("No audio stream present on file: '{}'", path.toAbsolutePath().toString());
				// no audio stream present
				throw new MediaFormatException(MessageFormat.format(Translations.getTranslation("media.load.error.audio.missing"), path.toAbsolutePath().toString()));
			} catch (InterruptedException ex) {
				throw new IOException(ex.getMessage(), ex);
			} finally {
				if (demuxer != null) {
					try {
						demuxer.close();
					} catch (Exception e) {
						// just eat them
						LOGGER.warn("Failed to close demuxer on: '{}': {}.", path.toAbsolutePath().toString(), e.getMessage());
					}
				}
			}
		} else {
			throw new FileNotFoundException(MessageFormat.format(Translations.getTranslation("error.file.missing"), path.toAbsolutePath().toString()));
		}
	}
}

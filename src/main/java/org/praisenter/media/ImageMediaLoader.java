package org.praisenter.media;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.utility.ImageManipulator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

public final class ImageMediaLoader extends AbstractMediaLoader implements MediaLoader {
	private static final Logger LOGGER = LogManager.getLogger(ImageMediaLoader.class);
	
	public ImageMediaLoader(MediaThumbnailSettings settings) {
		super(settings);
	}
	
	@Override
	public boolean isSupported(String mimeType) {
		String[] supportedTypes = ImageIO.getReaderMIMETypes();
		for (String type : supportedTypes) {
			if (type.equalsIgnoreCase(mimeType)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public LoadedMedia load(Path path) throws IOException, FileNotFoundException, MediaFormatException {
		if (Files.exists(path) && Files.isRegularFile(path)) {
			// attempt to read the orientation
			// and just log any exceptions
			int orientation = -1;
			try {
				Metadata meta = ImageMetadataReader.readMetadata(path.toFile());
				ExifIFD0Directory directory = meta.getFirstDirectoryOfType(ExifIFD0Directory.class);
				if (directory != null) {
					orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
				}
			} catch (Exception e) {
				LOGGER.warn("Failed to read EXIF orientation.", e);
			}
			
			try (ImageInputStream in = ImageIO.createImageInputStream(path.toFile())) {
				Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
				// loop through the readers until we find one that works
				while (readers.hasNext()) {
					ImageReader reader = readers.next();
					reader.setInput(in);
					try {
						// read and correct the image
						BufferedImage image = ImageManipulator.correctExifOrientation(reader.read(0), orientation);
						BufferedImage thumb = createThumbnail(image);
						MediaFormat format = new MediaFormat(reader.getFormatName().toLowerCase(), "");
						MediaMetadata metadata = MediaMetadata.forImage(path, format, (int)image.getWidth(), (int)image.getHeight(), null);
						Media media = new Media(metadata, thumb);
						return new LoadedMedia(media, image);
					} finally {
						reader.dispose();
					}
				}
				// no readers
				throw new MediaFormatException();
			} catch (IllegalArgumentException ex) {
				throw new MediaFormatException(ex);
			}
		} else {
			throw new FileNotFoundException(path.toString());
		}
	}
}

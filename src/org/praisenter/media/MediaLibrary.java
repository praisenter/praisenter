package org.praisenter.media;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.resources.Messages;

/**
 * Static thread-safe class for managing the media library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MediaLibrary {
	/** File path separator */
	private static final String SEPARATOR = FileSystems.getDefault().getSeparator();
	
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(MediaLibrary.class);
	
	// media
	
	/** The map of media loaders */
	private static Map<MediaType, List<MediaLoader<?>>> MEDIA_LOADERS = new HashMap<MediaType, List<MediaLoader<?>>>();
	
	/** The map of weak references to media */
	private static final Map<String, WeakReference<Media>> MEDIA = new HashMap<String, WeakReference<Media>>();
	
	// thumbnails
	
	/** The thumbnail size */
	private static final Dimension THUMBNAIL_SIZE = new Dimension(64, 48);
	
	/** The thumbnail file name */
	private static final String THUMBS_FILE = SEPARATOR + "Thumbs.xml";
	
	/** The list of all thumbnails */
	private static final List<Thumbnail> THUMBNAILS = new ArrayList<Thumbnail>();
	
	// state
	
	/** True if the media library has been loaded */
	private static boolean loaded = false;
	
	// static loading
	
	static {
		// add the media loader lists
		MEDIA_LOADERS.put(MediaType.IMAGE, new ArrayList<MediaLoader<?>>());
		MEDIA_LOADERS.put(MediaType.VIDEO, new ArrayList<MediaLoader<?>>());
		MEDIA_LOADERS.put(MediaType.AUDIO, new ArrayList<MediaLoader<?>>());
		
		// get the first video and audio loaders from the classpath
		addClasspathImageLoaders();
		addClasspathVideoLoaders();
		addClasspathAudioLoaders();
		
		// FIXME we need to do this on startup so that we get the thumbnails loaded
		try {
			loadMediaLibrary();
		} catch (MediaLibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Hidden constructor.
	 */
	private MediaLibrary() {}
	
	/**
	 * Searches the classpath for any classes that implement sthe {@link ImageMediaLoader} interface
	 * and adds them to the list of loaders for {@link MediaType#IMAGE}.
	 */
	private static synchronized final void addClasspathImageLoaders() {
		Iterator<ImageMediaLoader> it = ServiceLoader.load(ImageMediaLoader.class).iterator();
		while (it.hasNext()) {
			ImageMediaLoader loader = it.next();
			MEDIA_LOADERS.get(MediaType.IMAGE).add(loader);
			break;
		}
	}
	
	/**
	 * Searches the classpath for any classes that implement sthe {@link VideoMediaLoader} interface
	 * and adds them to the list of loaders for {@link MediaType#VIDEO}.
	 */
	private static synchronized final void addClasspathVideoLoaders() {
		Iterator<VideoMediaLoader> it = ServiceLoader.load(VideoMediaLoader.class).iterator();
		while (it.hasNext()) {
			VideoMediaLoader loader = it.next();
			MEDIA_LOADERS.get(MediaType.VIDEO).add(loader);
			break;
		}
	}
	
	/**
	 * Searches the classpath for any classes that implement sthe {@link AudioMediaLoader} interface
	 * and adds them to the list of loaders for {@link MediaType#AUDIO}.
	 */
	private static synchronized final void addClasspathAudioLoaders() {
		Iterator<AudioMediaLoader> it = ServiceLoader.load(AudioMediaLoader.class).iterator();
		while (it.hasNext()) {
			AudioMediaLoader loader = it.next();
			MEDIA_LOADERS.get(MediaType.AUDIO).add(loader);
			break;
		}
	}
	
	/**
	 * Loads the media library.
	 * @throws MediaLibraryException thrown if an exception occurs while loading the media library
	 */
	public static synchronized final void loadMediaLibrary() throws MediaLibraryException {
		// see if the media library has already been loaded
		if (!loaded) {
			if (isMediaSupported(MediaType.IMAGE)) {
				String path = Constants.MEDIA_LIBRARY_PATH + SEPARATOR + Constants.MEDIA_LIBRARY_IMAGE_PATH;
				loadMediaLibrary(path);
			}
			if (isMediaSupported(MediaType.VIDEO)) {
				String path = Constants.MEDIA_LIBRARY_PATH + SEPARATOR + Constants.MEDIA_LIBRARY_VIDEO_PATH;
				loadMediaLibrary(path);
			}
			if (isMediaSupported(MediaType.AUDIO)) {
				String path = Constants.MEDIA_LIBRARY_PATH + SEPARATOR + Constants.MEDIA_LIBRARY_AUDIO_PATH;
				loadMediaLibrary(path);
			}
			loaded = true;
		}
	}
	
	/**
	 * Loads the media references and thumbnails from the given media library path.
	 * <p>
	 * This method does not do a recursive look up.
	 * <p>
	 * This method will ensure that a Thumbs.xml file is present and up to date with the
	 * files in the directory.
	 * @param path the media library path
	 * @throws MediaLibraryException thrown if the media library load encounters an error
	 */
	// FIXME right now a user could manually put the wrong media type file in the folder and this will load it in the wrong place...
	private static synchronized final void loadMediaLibrary(String path) throws MediaLibraryException {
		// attempt to read the thumbs file in the respective folder
		List<Thumbnail> thumbnailsFromFile = null;
		try {
			thumbnailsFromFile = Thumbnails.read(path + THUMBS_FILE);
		} catch (JAXBException e) {
			// silently ignore this error
			LOGGER.error("Could not read [" + path + THUMBS_FILE + "]: ", e);
		}
		if (thumbnailsFromFile == null) {
			thumbnailsFromFile = new ArrayList<Thumbnail>();
		}
		
		// create a new list to store the thumbnails
		List<Thumbnail> thumbnails = new ArrayList<Thumbnail>();
		// track whether we need to resave the thumbnail XML
		boolean save = false;
		
		// read the media library file names
		File[] files = new File(path).listFiles();
		for (File file : files) {
			// skip directories
			if (file.isDirectory()) continue;
			// get the file path
			String filePath = file.getPath();
			// skip xml files
			if (getContentType(filePath).contains("xml")) continue;
			// make sure there exists a thumnail for the file
			boolean exists = false;
			for (Thumbnail thumb : thumbnailsFromFile) {
				if (thumb.getFileProperties().getFileName().equals(file.getName())) {
					// flag that the thumbnail exists
					exists = true;
					// add it to the thumbnails array
					thumbnails.add(thumb);
					// we can break from the loop
					break;
				}
			}
			// check if we need to generate a thumbnail for the file
			if (!exists) {
				// generate a thumbnail for the image using the media loader
				// load the media
				try {
					Media media = loadFromMediaLibrary(filePath);
					// add the media to the media library (might as well since we loaded it)
					MEDIA.put(filePath, new WeakReference<Media>(media));
					// create the thumbnail
					Thumbnail thumbnail = media.getThumbnail(THUMBNAIL_SIZE);
					// add the thumbnail to the list
					thumbnails.add(thumbnail);
					// flag that we need to save it
					save = true;
				} catch (MediaException e) {
					throw new MediaLibraryException(MessageFormat.format(Messages.getString("media.exception.couldNotLoadMedia"), filePath), e);
				}
			} else {
				// we need to add a media reference anyway
				MEDIA.put(filePath, new WeakReference<Media>(null));
			}
		}
		// add all the thumbnails
		THUMBNAILS.addAll(thumbnails);
		// sort the thumbnails
		Collections.sort(THUMBNAILS);
		// after we have read all the files we need to save the new thumbs xml
		if (save || thumbnailsFromFile.size() != thumbnails.size()) {
			saveThumbnailsFile(path, thumbnails);
		}
	}

	/**
	 * Writes the thumbnails file for the given media type.
	 * @param type the media type
	 */
	private static synchronized final void saveThumbnailsFile(MediaType type) {
		List<Thumbnail> thumbnails = getThumbnails(type);
		String path = getMediaTypePath(type);
		saveThumbnailsFile(path, thumbnails);
	}
	
	/**
	 * Writes the thumbnails file for the given path and list of thumbnails.
	 * @param path the path of the thumbnails file (no file name)
	 * @param thumbnails the list of thumbnails
	 */
	private static synchronized final void saveThumbnailsFile(String path, List<Thumbnail> thumbnails) {
		try {
			Thumbnails.save(path + THUMBS_FILE, thumbnails);
			LOGGER.info("File [" + path + THUMBS_FILE + "] updated.");
		} catch (JAXBException | IOException e) {
			// silently log this error
			LOGGER.error("Failed to re-save [" + path + THUMBS_FILE + "]: ", e);
		}
	}
	
	/**
	 * Loads a media file from a system path.
	 * <p>
	 * This method will copy the system path media file to the media library first.
	 * @param filePath the file and path
	 * @return {@link Media}
	 * @throws NoMediaLoaderException thrown if no {@link MediaLoader} exists for the given media
	 * @throws MediaException thrown if an exception occurs while reading the media
	 * @throws FileNotFoundException thrown if the given file is not found
	 * @throws FileAlreadyExistsException thrown if the given file name is already taken by a file in the media library
	 * @throws IOException thrown if an IO error occurs
	 */
	private static synchronized final Media loadFromFileSystem(String filePath) throws NoMediaLoaderException, MediaException, FileNotFoundException, FileAlreadyExistsException, IOException {
		String contentType = getContentType(filePath);
		// get the media type
		MediaType type = getMediaType(contentType);
		// make sure we can load this type before copying it
		if (isMediaSupported(type)) {
			// get the folder for the media type
			String folder = getMediaTypePath(type);
			// copy the file over to the media library
			String mediaLibraryPath = copyToMediaLibrary(filePath, folder);
			// load and return the media
			return loadFromMediaLibrary(mediaLibraryPath);
		} else {
			throw new NoMediaLoaderException(MessageFormat.format(Messages.getString("media.exception.mediaNotSupported"), type));
		}
	}
	
	/**
	 * Copies the given file to the media library.
	 * @param filePath the file
	 * @param destPath the media library destination path
	 * @return String the media library path
	 * @throws FileNotFoundException thrown if the given file is not found
	 * @throws FileAlreadyExistsException thrown if the given file name is already taken by a file in the media library
	 * @throws IOException thrown if an IO error occurs
	 */
	private static final String copyToMediaLibrary(String filePath, String destPath) throws FileNotFoundException, FileAlreadyExistsException, IOException {
		// get a handle on the file to copy
		FileSystem fs = FileSystems.getDefault();
		Path source = fs.getPath(filePath);
		// make sure it exists and is a file
		if (Files.exists(source) && Files.isRegularFile(source)) {
			String name = source.getFileName().toString();
			Path target = fs.getPath(destPath + name);
			// see if we can use the same name in the destination file
			if (Files.exists(target)) {
				throw new FileAlreadyExistsException(MessageFormat.format(Messages.getString("media.exception.fileExists"), name));
			}
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
			return target.toString();
		} else {
			throw new FileNotFoundException(MessageFormat.format(Messages.getString("media.exception.fileNotFound"), source.toAbsolutePath().toString()));
		}
	}
	
	/**
	 * Loads the given media from the media library.
	 * @param filePath the file name
	 * @return {@link Media}
	 * @throws MediaException if an exception occurs while loading the media
	 * @throws NoMediaLoaderException thrown if a media loader does not exist for the media
	 */
	private static synchronized final Media loadFromMediaLibrary(String filePath) throws NoMediaLoaderException, MediaException {
		String contentType = getContentType(filePath);
		MediaType type = getMediaType(contentType);
		MediaLoader<?> loader = getMediaLoader(type, contentType);
		return loader.load(filePath);
	}
	
	/**
	 * Returns the path for the given media type in the media library.
	 * @param type the media type
	 * @return String
	 */
	private static final String getMediaTypePath(MediaType type) {
		String sep = FileSystems.getDefault().getSeparator();
		String path = Constants.MEDIA_LIBRARY_PATH + sep;
		if (type == MediaType.IMAGE) {
			return path + Constants.MEDIA_LIBRARY_IMAGE_PATH + sep;
		} else if (type == MediaType.VIDEO) {
			return path + Constants.MEDIA_LIBRARY_VIDEO_PATH + sep;
		} else if (type == MediaType.AUDIO) {
			return path + Constants.MEDIA_LIBRARY_AUDIO_PATH + sep;
		}
		return path;
	}
	
	/**
	 * Returns the media loader for the given media type.
	 * @param type the media type
	 * @param contentType the mime type
	 * @return {@link MediaLoader}
	 * @throws NoMediaLoaderException thrown if no media loader exists for the given media type
	 */
	private static synchronized final MediaLoader<?> getMediaLoader(MediaType type, String contentType) throws NoMediaLoaderException {
		List<MediaLoader<?>> loaders = MEDIA_LOADERS.get(type);
		if (loaders != null) {
			for (MediaLoader<?> loader : loaders) {
				if (loader.isSupported(contentType)) {
					return loader;
				}
			}
		}
		throw new NoMediaLoaderException(MessageFormat.format(Messages.getString("media.exception.noMediaLoader"), type, contentType));
	}
	
	/**
	 * Returns the mime type of the given file.
	 * @param filePath the file path and name
	 * @return String
	 */
	private static final String getContentType(String filePath) {
		FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
		return map.getContentType(filePath);
	}
	
	/**
	 * Returns the media type of the given file name.
	 * <p>
	 * This method inspects the file extension and determines the content type.
	 * @param contentType the file name
	 * @return {@link MediaType}
	 * @throws NoMediaLoaderException thrown if the content type is not image, video, or audio
	 */
	private static final MediaType getMediaType(String contentType) throws NoMediaLoaderException {
		// attempt to determine the type of media
		if (contentType.contains("image")) {
			return MediaType.IMAGE;
		} else if (contentType.contains("video")) {
			return MediaType.VIDEO;
		} else if (contentType.contains("audio")) {
			return MediaType.AUDIO;
		} else {
			// if its not image, video, or audio, what is it?
			throw new NoMediaLoaderException(MessageFormat.format(Messages.getString("media.exception.unknownMediaType"), contentType));
		}
	}
	
	// public interface
	
	/**
	 * Returns a new list containing the {@link MediaLoader}s being referenced by the media library.
	 * @param type the media type
	 * @return List&lt;{@link MediaLoader}&gt;
	 */
	public static synchronized final List<MediaLoader<?>> getMediaLoaders(MediaType type) {
		List<MediaLoader<?>> loaders = new ArrayList<>();
		loaders.addAll(MEDIA_LOADERS.get(type));
		return loaders;
	}
	
	/**
	 * Adds the given media loader for the given type of media.
	 * @param loader the media loader
	 * @param type the media type
	 */
	public static synchronized final void addMediaLoader(MediaLoader<?> loader, MediaType type) {
		MEDIA_LOADERS.get(type).add(loader);
	}
	
	/**
	 * Removes the given media loader from the media loaders.
	 * @param loader the loader to remove
	 * @param type the loader type (if the loader supports multiple types)
	 * @return boolean true if the loader was removed
	 */
	public static synchronized final boolean removeMediaLoader(MediaLoader<?> loader, MediaType type) {
		return MEDIA_LOADERS.get(type).remove(loader);
	}
	
	/**
	 * Returns true if there exists a {@link MediaLoader} available for the given {@link MediaType}.
	 * @param type the media type
	 * @return boolean
	 */
	public static synchronized final boolean isMediaSupported(MediaType type) {
		return MEDIA_LOADERS.get(type).size() > 0;
	}
	
	/**
	 * Returns the list of thumbnails for the given media type.
	 * @param type the media type
	 * @return List&lt;{@link Thumbnail}&gt;
	 */
	public static synchronized final List<Thumbnail> getThumbnails(MediaType type) {
		List<Thumbnail> thumbnails = new ArrayList<Thumbnail>();
		for (Thumbnail thumbnail : THUMBNAILS) {
			if (thumbnail.getType() == type) {
				thumbnails.add(thumbnail);
			}
		}
		return thumbnails;
	}
	
	/**
	 * Returns the thumbnail for the given media.
	 * @param media the media
	 * @return {@link Thumbnail}
	 */
	public static synchronized final Thumbnail getThumbnail(Media media) {
		for (Thumbnail thumbnail : THUMBNAILS) {
			if (thumbnail.getType() == media.getType()
			 && thumbnail.getFileProperties().getFileName().equals(media.getFileProperties().getFileName())) {
				return thumbnail;
			}
		}
		return media.getThumbnail(THUMBNAIL_SIZE);
	}
	
	/**
	 * Returns the thumbnail for the given media.
	 * @param filePath the file name and path of the media
	 * @return {@link Thumbnail}
	 */
	private static synchronized final Thumbnail getThumbnail(String filePath) {
		for (Thumbnail thumbnail : THUMBNAILS) {
			if (thumbnail.getFileProperties().getFilePath().equals(filePath)) {
				return thumbnail;
			}
		}
		return null;
	}
	
	/**
	 * Adds the given file system media to the media library.
	 * @param filePath the file system path and file name
	 * @return {@link Media}
	 * @throws MediaException thrown if an exception occurs while reading the media
	 * @throws NoMediaLoaderException thrown if no {@link MediaLoader} exists for the given media
	 * @throws FileNotFoundException thrown if the given file does not exist
	 * @throws FileAlreadyExistsException thrown if the given file name is already taken by a file in the media library
	 * @throws IOException thrown if an IO error occurs
	 */
	public static synchronized final Media addMedia(String filePath) throws FileNotFoundException, FileAlreadyExistsException, NoMediaLoaderException, MediaException, IOException {
		Media media = loadFromFileSystem(filePath);
		
		// add a weak reference to it
		MEDIA.put(media.getFileProperties().getFilePath(), new WeakReference<Media>(media));
		// add it to the thumbnails list
		THUMBNAILS.add(media.getThumbnail(THUMBNAIL_SIZE));
		// resort the thumbnails
		Collections.sort(THUMBNAILS);
		// save a new thumbnails file
		saveThumbnailsFile(media.getType());
		
		return media;
	}

	/**
	 * Returns the media from the media library.
	 * <p>
	 * Returns null if the media is not found.
	 * @param filePath the file path/name of the media in the media library
	 * @return {@link Media}
	 * @throws MediaException if an exception occurs while loading the media
	 * @throws NoMediaLoaderException thrown if a media loader does not exist for the media
	 */
	public static synchronized final Media getMedia(String filePath) throws MediaException, NoMediaLoaderException {
		WeakReference<Media> ref = MEDIA.get(filePath);
		// make sure the key is there
		if (ref == null) return null;
		// check if it was reclaimed
		if (ref.get() == null) {
			// then we need to reload the media
			Media media = loadFromMediaLibrary(filePath);
			// create a new weak reference to the media
			ref = new WeakReference<Media>(media);
			// return the media
			return media;
		}
		return ref.get();
	}
	
	/**
	 * Removes the given media from the media library.
	 * @param filePath the file path/name
	 * @return boolean
	 * @throws IOException thrown if the file fails to be deleted
	 */
	public static synchronized final boolean removeMedia(String filePath) throws IOException {
		// make sure the media is removed
		WeakReference<Media> media = MEDIA.remove(filePath);
		// remove the thumbnail (no resorting needed)
		Thumbnail thumbnail = getThumbnail(filePath);
		THUMBNAILS.remove(thumbnail);
		// delete the file
		Path path = FileSystems.getDefault().getPath(filePath);
		boolean deleted = Files.deleteIfExists(path);
		
		if (media != null && deleted) {
			// save a new thumbnails file
			saveThumbnailsFile(thumbnail.getType());
			return true;
		}
		return false;
	}
}

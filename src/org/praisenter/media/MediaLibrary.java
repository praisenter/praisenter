package org.praisenter.media;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.praisenter.Constants;

public class MediaLibrary {
	private static final Dimension THUMBNAIL_SIZE = new Dimension(64, 48);
	private static final Map<String, Thumbnail> THUMBNAILS = new HashMap<String, Thumbnail>();
	private static final Map<String, WeakReference<Media>> MEDIA = new HashMap<String, WeakReference<Media>>();
	// TODO later we should allow multiple media loaders so you can plug and play any loaders and the first one to support the media will be used
	private static Map<MediaType, MediaLoader<?>> MEDIA_LOADERS = new HashMap<MediaType, MediaLoader<?>>();
	
	static {
		// setup the default image loader
		MEDIA_LOADERS.put(MediaType.IMAGE, new DefaultImageMediaLoader());
		// get the first video and audio loaders from the classpath
		setVideoLoader();
		setAudioLoader();
		
		// read the folders and initialize the weak references
		try {
			loadMediaLibrary();
		} catch (NoMediaLoaderException | MediaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static synchronized final void setVideoLoader() {
		// no default audio/video loaders yet...
		Iterator<VideoMediaLoader> it = ServiceLoader.load(VideoMediaLoader.class).iterator();
		while (it.hasNext()) {
			VideoMediaLoader loader = it.next();
			MEDIA_LOADERS.put(MediaType.VIDEO, loader);
			break;
		}
	}
	
	private static synchronized final void setAudioLoader() {
		// no default audio/video loaders yet...
		Iterator<AudioMediaLoader> it = ServiceLoader.load(AudioMediaLoader.class).iterator();
		while (it.hasNext()) {
			AudioMediaLoader loader = it.next();
			MEDIA_LOADERS.put(MediaType.AUDIO, loader);
			break;
		}
	}
	
	// TODO we need to do this on startup so that we get the thumbnails loaded
	private static synchronized final void loadMediaLibrary() throws NoMediaLoaderException, MediaException {
		String sep = FileSystems.getDefault().getSeparator();
		File mediaDir = new File(Constants.MEDIA_LIBRARY_PATH + sep + Constants.MEDIA_LIBRARY_IMAGE_PATH);
		for (File file : mediaDir.listFiles()) {
			String fileName = file.getPath();
			Media media = loadFromMediaLibrary(fileName);
			MEDIA.put(fileName, new WeakReference<Media>(media));
			// FIXME i should generate these thumbs and save them somewhere in the media library folder (should still check if all thumbs are there for each media file)
			THUMBNAILS.put(fileName, media.getThumbnail(THUMBNAIL_SIZE));
		}
		// TODO load other media types
	}
	
	/**
	 * Sets the media loader for the given type of media.
	 * @param loader the media loader
	 * @param type the media type
	 */
	public static synchronized final void setMediaLoader(MediaLoader<?> loader, MediaType type) {
		MEDIA_LOADERS.put(type, loader);
	}
	
	/**
	 * Returns true if there exists a {@link MediaLoader} available for the given {@link MediaType}.
	 * @param type the media type
	 * @return boolean
	 */
	public static synchronized final boolean isMediaSupported(MediaType type) {
		return MEDIA_LOADERS.get(type) != null;
	}
	
	/**
	 * Adds the given file system media to the media library.
	 * @param fileName the file system path and file name
	 * @return {@link Media}
	 * @throws MediaException thrown if an exception occurs while reading the media
	 * @throws NoMediaLoaderException thrown if no {@link MediaLoader} exists for the given media
	 * @throws FileNotFoundException thrown if the given file does not exist
	 * @throws IOException thrown if an IO error occurs
	 */
	public static synchronized final Media addMedia(String fileName) throws FileNotFoundException, NoMediaLoaderException, MediaException, IOException {
		Media media = loadFromFileSystem(fileName);
		
		MEDIA.put(media.getFileName(), new WeakReference<Media>(media));
		
		return media;
	}

	/**
	 * Returns the media from the media library.
	 * @param fileName the file name of the media in the media library
	 * @return {@link Media}
	 * @throws MediaException if an exception occurs while loading the media
	 * @throws NoMediaLoaderException thrown if a media loader does not exist for the media
	 */
	public static synchronized final Media getMedia(String fileName) throws MediaException, NoMediaLoaderException {
		WeakReference<Media> ref = MEDIA.get(fileName);
		// make sure the key is there
		if (ref == null) return null;
		// check if it was reclaimed
		if (ref.get() == null) {
			// then we need to reload the media
			Media media = loadFromMediaLibrary(fileName);
			// create a new weak reference to the media
			ref = new WeakReference<Media>(media);
			// return the media
			return media;
		}
		return ref.get();
	}
	
	/**
	 * Removes the given media from the media library.
	 * @param fileName the file name
	 * @throws IOException thrown if the file fails to be deleted
	 */
	public static synchronized final void removeMedia(String fileName) throws IOException {
		// make sure the media is removed
		MEDIA.remove(fileName);
		// remove the thumbnail
		THUMBNAILS.remove(fileName);
		// delete the file
		Path path = FileSystems.getDefault().getPath(fileName);
		Files.deleteIfExists(path);
	}
	
	/**
	 * Returns all the thumbnails of the given media type.
	 * @param type the media type
	 * @return 
	 */
	public static synchronized final List<Thumbnail> getThumbnails(MediaType type) {
		List<Thumbnail> thumbs = new ArrayList<Thumbnail>();
		for (Thumbnail thumb : THUMBNAILS.values()) {
			if (thumb.type == type) {
				thumbs.add(thumb);
			}
		}
		return thumbs;
	}
	
	/**
	 * Loads a media file from a system path.
	 * <p>
	 * This method will copy the system path media file to the media library first.
	 * @param fileName the file name and path
	 * @return {@link Media}
	 * @throws NoMediaLoaderException thrown if no {@link MediaLoader} exists for the given media
	 * @throws MediaException thrown if an exception occurs while reading the media
	 * @throws FileNotFoundException thrown if the given file is not found
	 * @throws IOException thrown if an IO error occurs
	 */
	private static final Media loadFromFileSystem(String fileName) throws NoMediaLoaderException, MediaException, FileNotFoundException, IOException {
		// get the media type
		MediaType type = getMediaType(fileName);
		// make sure we can load this type before copying it
		if (isMediaSupported(type)) {
			// get the folder for the media type
			String folder = getMediaTypePath(type);
			// copy the file over to the media library
			String mediaLibraryPath = copyToMediaLibrary(fileName, folder);
			// load and return the media
			return loadFromMediaLibrary(mediaLibraryPath);
		} else {
			throw new NoMediaLoaderException("No media loader available for media type: " + type);
		}
	}
	
	/**
	 * Copies the given file to the media library.
	 * @param fileName the file
	 * @param destPath the media library destination path
	 * @return String the media library path
	 * @throws FileNotFoundException thrown if the given file is not found
	 * @throws IOException thrown if an IO error occurs
	 */
	private static final String copyToMediaLibrary(String fileName, String destPath) throws FileNotFoundException, IOException {
		// get a handle on the file to copy
		FileSystem fs = FileSystems.getDefault();
		Path source = fs.getPath(fileName);
		// make sure it exists and is a file
		if (Files.exists(source) && Files.isRegularFile(source)) {
			String name = source.getFileName().toString();
			Path target = fs.getPath(destPath + name);
			// see if we can use the same name in the destination file
			if (Files.exists(target)) {
				// FIXME throw an exception
			}
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
			return target.toString();
		} else {
			throw new FileNotFoundException("The file [" + source.toAbsolutePath().toString() + "] was not found.");
		}
	}
	
	/**
	 * Loads the given media from the media library.
	 * @param fileName the file name
	 * @return {@link Media}
	 * @throws MediaException if an exception occurs while loading the media
	 * @throws NoMediaLoaderException thrown if a media loader does not exist for the media
	 */
	private static final Media loadFromMediaLibrary(String fileName) throws NoMediaLoaderException, MediaException {
		MediaType type = getMediaType(fileName);
		MediaLoader<?> loader = getMediaLoader(type);
		return loader.load(fileName);
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
	 * @return {@link MediaLoader}
	 * @throws NoMediaLoaderException thrown if no media loader exists for the given media type
	 */
	private static final MediaLoader<?> getMediaLoader(MediaType type) throws NoMediaLoaderException {
		MediaLoader<?> loader = MEDIA_LOADERS.get(type);
		if (loader == null) {
			throw new NoMediaLoaderException("No media loader available for media type: " + type);
		}
		return loader;
	}
	
	/**
	 * Returns the media type of the given file name.
	 * <p>
	 * This method inspects the file extension and determines the content type.
	 * @param fileName the file name
	 * @return {@link MediaType}
	 * @throws NoMediaLoaderException thrown if the content type is not image, video, or audio
	 */
	private static final MediaType getMediaType(String fileName) throws NoMediaLoaderException {
		// attempt to determine the type of media
		FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
		String contentType = map.getContentType(fileName);
		if (contentType.contains("image")) {
			return MediaType.IMAGE;
		} else if (contentType.contains("video")) {
			return MediaType.VIDEO;
		} else if (contentType.contains("audio")) {
			return MediaType.AUDIO;
		} else {
			// if its not image, video, or audio, what is it?
			throw new NoMediaLoaderException("Unknown media type: " + contentType);
		}
	}
}

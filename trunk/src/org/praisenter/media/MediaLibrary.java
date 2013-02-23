/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.praisenter.common.NotInitializedException;
import org.praisenter.common.utilities.FileUtilities;
import org.praisenter.common.xml.XmlIO;

/**
 * Static thread-safe class for managing the media library.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public final class MediaLibrary {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(MediaLibrary.class);
	
	// constants
	
	/** The media library path */
	private static final String MEDIA_LIBRARY_PATH = "media";
	
	/** The media library images path */
	private static final String MEDIA_LIBRARY_IMAGE_PATH = MEDIA_LIBRARY_PATH + FileUtilities.getSeparator() + "images";
	
	/** The media library videos path */
	private static final String MEDIA_LIBRARY_VIDEO_PATH = MEDIA_LIBRARY_PATH + FileUtilities.getSeparator() + "videos";
	
	/** The media library audio path */
	private static final String MEDIA_LIBRARY_AUDIO_PATH = MEDIA_LIBRARY_PATH + FileUtilities.getSeparator() + "audio";

	/** The thumbnail size */
	private static final Dimension THUMBNAIL_SIZE = new Dimension(64, 48);
	
	/** The thumbnail file name */
	private static final String THUMBS_FILE = FileUtilities.getSeparator() + "_thumbs.xml";
	
	// media loaders and players
	
	/** The map of media loaders */
	private static Map<MediaType, List<MediaLoader<?>>> MEDIA_LOADERS = new HashMap<MediaType, List<MediaLoader<?>>>();
	
	/** The list of available MediaPlayers */
	private static final List<MediaPlayerFactory<?>> MEDIA_PLAYER_FACTORIES = new ArrayList<MediaPlayerFactory<?>>();
	
	static {
		// add the media loader lists
		MEDIA_LOADERS.put(MediaType.IMAGE, new ArrayList<MediaLoader<?>>());
		MEDIA_LOADERS.put(MediaType.VIDEO, new ArrayList<MediaLoader<?>>());
		MEDIA_LOADERS.put(MediaType.AUDIO, new ArrayList<MediaLoader<?>>());
		
		// get the first video and audio loaders from the classpath
		addClasspathImageLoaders();
		addClasspathVideoLoaders();
		addClasspathAudioLoaders();
		
		// add any class path media players
		addClasspathMediaPlayerFactories();
	}
	
	/**
	 * Searches the classpath for any classes that implement sthe {@link ImageMediaLoader} interface
	 * and adds them to the list of loaders for {@link MediaType#IMAGE}.
	 */
	private static synchronized final void addClasspathImageLoaders() {
		Iterator<ImageMediaLoader> it = ServiceLoader.load(ImageMediaLoader.class).iterator();
		while (it.hasNext()) {
			ImageMediaLoader loader = it.next();
			MEDIA_LOADERS.get(MediaType.IMAGE).add(loader);
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
		}
	}
	
	/**
	 * Searches the classpath for any classes that implement the {@link MediaPlayerFactory} interface
	 * and adds them to the list of factories.
	 */
	private static synchronized final void addClasspathMediaPlayerFactories() {
		@SuppressWarnings("rawtypes")
		Iterator<MediaPlayerFactory> it = ServiceLoader.load(MediaPlayerFactory.class).iterator();
		while (it.hasNext()) {
			MediaPlayerFactory<?> player = it.next();
			MEDIA_PLAYER_FACTORIES.add(player);
		}
	}

	// members

	/** The media library base path */
	private String basePath;
	
	/** The full path to the media library images */
	private final String imagePath;
	
	/** The full path to the media library audio */
	private final String audioPath;
	
	/** The full path to the media library videos */
	private final String videoPath;
	
	/** The map of weak references to media */
	private Map<String, WeakReference<Media>> media;
	
	/** The list of all thumbnails */
	private List<MediaThumbnail> thumbnails;
	
	// static members
	
	/** The current media library */
	private static MediaLibrary instance;
	
	// static interface
	
	/**
	 * Initializes the media library at the given path.
	 * @param basePath the base path for the media library
	 */
	public static final synchronized void initialize(String basePath) {
		if (basePath == null) {
			basePath = "";
		}
		// create a new instance
		MediaLibrary library = new MediaLibrary(basePath);
		// assign the current instance
		instance = library;
	}
	
	/**
	 * Returns the current media library.
	 * @return {@link MediaLibrary}
	 * @throws NotInitializedException thrown if {@link #initialize(String)} was not called
	 */
	public static final synchronized MediaLibrary getInstance() throws NotInitializedException {
		if (instance == null) {
			throw new NotInitializedException();
		}
		return instance;
	}
	
	/**
	 * Full constructor.
	 * @param basePath the base path
	 */
	private MediaLibrary(String basePath) {
		this.basePath = basePath;
		
		String sep = FileUtilities.getSeparator();
		this.imagePath = basePath + sep + MEDIA_LIBRARY_IMAGE_PATH;
		this.audioPath = basePath + sep + MEDIA_LIBRARY_AUDIO_PATH;
		this.videoPath = basePath + sep + MEDIA_LIBRARY_VIDEO_PATH;
		
		this.media = new HashMap<String, WeakReference<Media>>();
		this.thumbnails = new ArrayList<MediaThumbnail>();
		
		// verify the existence of the /media and sub directories
		FileUtilities.createFolder(this.imagePath);
		FileUtilities.createFolder(this.audioPath);
		FileUtilities.createFolder(this.videoPath);
		
		// load the media library (thumbnails only...usually)
		this.loadMediaLibrary(MediaType.IMAGE);
		this.loadMediaLibrary(MediaType.AUDIO);
		this.loadMediaLibrary(MediaType.VIDEO);
	}

	/**
	 * Loads the media references and thumbnails for the given media type.
	 * <p>
	 * This method does not do a recursive look up.
	 * <p>
	 * This method will ensure that a _thumbs.xml file is present and up to date with the
	 * files in the directory.
	 * @param type the media type
	 */
	private void loadMediaLibrary(MediaType type) {
		String path = getMediaTypeFullPath(type);
		if (!isMediaSupported(type)) {
			return;
		}
		// attempt to read the thumbs file in the respective folder
		List<MediaThumbnail> thumbnailsFromFile = null;
		try {
			MediaThumbnails mts = XmlIO.read(path + THUMBS_FILE, MediaThumbnails.class);
			if (mts != null) {
				thumbnailsFromFile = mts.getThumbnails();
			}
		} catch (Exception e) {
			// silently ignore this error
			LOGGER.warn("Could not read [" + path + THUMBS_FILE + "]");
		}
		if (thumbnailsFromFile == null) {
			thumbnailsFromFile = new ArrayList<MediaThumbnail>();
		}
		
		FileSystem system = FileSystems.getDefault();
		Path rootPath = system.getPath(this.basePath);
		
		// create a new list to store the thumbnails
		List<MediaThumbnail> thumbnails = new ArrayList<MediaThumbnail>();
		// track whether we need to resave the thumbnail XML
		boolean save = false;
		
		// read the media library file names
		File[] files = new File(path).listFiles();
		for (File file : files) {
			// skip directories
			if (file.isDirectory()) continue;
			// get the file path
			String filePath = file.getPath();
			// skip xml files (these are the thumb files)
			if (FileUtilities.getContentType(filePath).contains("xml")) continue;
			// make sure there exists a thumnail for the file
			boolean exists = false;
			for (MediaThumbnail thumb : thumbnailsFromFile) {
				if (thumb.getFile().getName().equals(file.getName())) {
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
					// check the loaded media type
					if (media.getType() != type) {
						LOGGER.warn("The media [" + filePath + "] is not the correct media type for this path. Media not loaded.");
						continue;
					}
					// add the media to the media library (might as well since we loaded it)
					this.media.put(media.getFile().getRelativePath(), new WeakReference<Media>(media));
					// create the thumbnail
					MediaThumbnail thumbnail = media.getThumbnail(THUMBNAIL_SIZE);
					// add the thumbnail to the list
					thumbnails.add(thumbnail);
					// flag that we need to save it
					save = true;
				} catch (MediaException e) {
					LOGGER.error("Could not load media [" + filePath + "]: ", e);
				}
			} else {
				// we need to add a media reference anyway (using the relative path)
				this.media.put(rootPath.relativize(system.getPath(filePath)).toString(), new WeakReference<Media>(null));
			}
		}
		// add all the thumbnails
		this.thumbnails.addAll(thumbnails);
		// sort the thumbnails
		Collections.sort(this.thumbnails);
		// after we have read all the files we need to save the new thumbs xml
		if (save || thumbnailsFromFile.size() != thumbnails.size()) {
			this.saveThumbnailsFile(path, thumbnails);
		}
	}

	/**
	 * Writes the thumbnails file for the given path and list of thumbnails.
	 * @param path the path of the thumbnails file without file name
	 * @param thumbnails the list of thumbnails
	 */
	private void saveThumbnailsFile(String path, List<MediaThumbnail> thumbnails) {
		try {
			XmlIO.save(path + THUMBS_FILE, new MediaThumbnails(thumbnails));
			LOGGER.debug("File [" + path + THUMBS_FILE + "] updated.");
		} catch (JAXBException | IOException e) {
			// silently log this error
			LOGGER.error("Failed to re-save [" + path + THUMBS_FILE + "]: ", e);
		}
	}
	
	/**
	 * Writes the thumbnails file for the given media type.
	 * @param type the media type
	 */
	private void saveThumbnailsFile(MediaType type) {
		List<MediaThumbnail> thumbnails = getThumbnails(type);
		String path = getMediaTypeFullPath(type);
		this.saveThumbnailsFile(path, thumbnails);
	}
	
	/**
	 * Loads a media file from a system path.
	 * <p>
	 * This method will copy the system path media file to the media library first.
	 * @param filePath the full path to the file
	 * @return {@link Media}
	 * @throws NoMediaLoaderException thrown if no {@link MediaLoader} exists for the given media
	 * @throws MediaException thrown if an exception occurs while reading the media
	 * @throws FileNotFoundException thrown if the given file is not found
	 * @throws FileAlreadyExistsException thrown if the given file name is already taken by a file in the media library
	 * @throws IOException thrown if an IO error occurs
	 */
	private Media loadFromFileSystem(String filePath) throws NoMediaLoaderException, MediaException, FileNotFoundException, FileAlreadyExistsException, IOException {
		String contentType = FileUtilities.getContentType(filePath);
		// get the media type
		MediaType type = getMediaType(contentType);
		// make sure we can load this type before copying it
		if (isMediaSupported(type)) {
			// get the folder for the media type
			String folder = getMediaTypeFullPath(type);
			// copy the file over to the media library
			String mediaLibraryPath = copyToMediaLibrary(filePath, folder);
			// load and return the media
			return loadFromMediaLibrary(mediaLibraryPath);
		} else {
			throw new NoMediaLoaderException(contentType);
		}
	}
	
	/**
	 * Copies the given file to the media library.
	 * @param filePath the full path to the file to copy
	 * @param destPath the media library destination full path
	 * @return String the media library path
	 * @throws FileNotFoundException thrown if the given file is not found
	 * @throws FileAlreadyExistsException thrown if the given file name is already taken by a file in the media library
	 * @throws IOException thrown if an IO error occurs
	 */
	private String copyToMediaLibrary(String filePath, String destPath) throws FileNotFoundException, FileAlreadyExistsException, IOException {
		// get a handle on the file to copy
		FileSystem fs = FileSystems.getDefault();
		Path source = fs.getPath(filePath);
		// make sure it exists and is a file
		if (Files.exists(source) && Files.isRegularFile(source)) {
			String name = source.getFileName().toString();
			Path target = fs.getPath(destPath + FileUtilities.getSeparator() + name);
			// see if we can use the same name in the destination file
			if (Files.exists(target)) {
				throw new FileAlreadyExistsException(target.toString());
			}
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
			return target.toString();
		} else {
			throw new FileNotFoundException();
		}
	}
	
	/**
	 * Loads the given media from the media library.
	 * @param fullPath the full file name and path
	 * @return {@link Media}
	 * @throws MediaException if an exception occurs while loading the media
	 * @throws NoMediaLoaderException thrown if a media loader does not exist for the media
	 */
	private Media loadFromMediaLibrary(String fullPath) throws NoMediaLoaderException, MediaException {
		String contentType = FileUtilities.getContentType(fullPath);
		MediaType type = getMediaType(contentType);
		MediaLoader<?> loader = getMediaLoader(type, contentType);
		return loader.load(this.basePath, fullPath);
	}
	
	/**
	 * Returns the path for the given media type in the media library.
	 * @param type the media type
	 * @return String
	 */
	private String getMediaTypeFullPath(MediaType type) {
		if (type == MediaType.IMAGE) {
			return this.imagePath;
		} else if (type == MediaType.VIDEO) {
			return this.videoPath;
		} else if (type == MediaType.AUDIO) {
			return this.audioPath;
		}
		return this.basePath;
	}
	
	/**
	 * Returns the media loader for the given media type.
	 * @param type the media type
	 * @param contentType the mime type
	 * @return {@link MediaLoader}
	 * @throws NoMediaLoaderException thrown if no media loader exists for the given media type
	 */
	private MediaLoader<?> getMediaLoader(MediaType type, String contentType) throws NoMediaLoaderException {
		List<MediaLoader<?>> loaders = MEDIA_LOADERS.get(type);
		if (loaders != null) {
			for (MediaLoader<?> loader : loaders) {
				if (loader.isSupported(contentType)) {
					return loader;
				}
			}
		}
		throw new NoMediaLoaderException(contentType);
	}
	
	/**
	 * Returns the media type of the given file name.
	 * <p>
	 * This method inspects the file extension and determines the content type.
	 * @param contentType the file name
	 * @return {@link MediaType}
	 * @throws NoMediaLoaderException thrown if the content type is not image, video, or audio
	 */
	private MediaType getMediaType(String contentType) throws NoMediaLoaderException {
		// attempt to determine the type of media
		if (contentType.contains("image")) {
			return MediaType.IMAGE;
		} else if (contentType.contains("video")) {
			return MediaType.VIDEO;
		} else if (contentType.contains("audio")) {
			return MediaType.AUDIO;
		} else {
			// if its not image, video, or audio, what is it?
			throw new NoMediaLoaderException(contentType);
		}
	}
	
	// public interface
	
	/**
	 * Returns the first {@link MediaLoader} for the given media type.
	 * <p>
	 * Returns null if the a media loader does not exist for the given type.
	 * @param type the media type
	 * @return List&lt;{@link MediaLoader}&gt;
	 */
	public static synchronized final MediaLoader<?> getMediaLoader(MediaType type) {
		List<MediaLoader<?>> loaders = MEDIA_LOADERS.get(type);
		if (loaders != null && loaders.size() > 0) {
			return loaders.get(0);
		}
		return null;
	}
	
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
	 * Returns the first matching {@link MediaPlayerFactory} for the given {@link PlayableMedia} type.
	 * <p>
	 * Returns null if a {@link MediaPlayerFactory} is not available.
	 * @param clazz the media type
	 * @return {@link MediaPlayerFactory}
	 */
	public static synchronized final <T extends PlayableMedia> MediaPlayerFactory<?> getMediaPlayerFactory(Class<T> clazz) {
		for (MediaPlayerFactory<?> factory : MEDIA_PLAYER_FACTORIES) {
			if (factory.isTypeSupported(clazz)) {
				return factory;
			}
		}
		return null;
	}
	
	/**
	 * Returns all the matching {@link MediaPlayerFactory}s for the given {@link PlayableMedia} type.
	 * @param clazz the media type
	 * @return List&lt;{@link MediaPlayerFactory}&gt;
	 */
	public static synchronized final <T extends PlayableMedia> List<MediaPlayerFactory<?>> getMediaPlayerFactories(Class<T> clazz) {
		List<MediaPlayerFactory<?>> factories = new ArrayList<MediaPlayerFactory<?>>();
		for (MediaPlayerFactory<?> factory : MEDIA_PLAYER_FACTORIES) {
			if (factory.isTypeSupported(clazz)) {
				factories.add(factory);
			}
		}
		return factories;
	}

	/**
	 * Adds the given {@link MediaPlayerFactory} to the available media player factories.
	 * @param factory the factory
	 */
	public static synchronized final void addMediaPlayerFactory(MediaPlayerFactory<?> factory) {
		MEDIA_PLAYER_FACTORIES.add(factory);
	}

	/**
	 * Removes the given {@link MediaPlayerFactory} from the available media player factories.
	 * @param factory the factory
	 * @return boolean true if the player factory was removed
	 */
	public static synchronized final boolean removeMediaPlayerFactory(MediaPlayerFactory<?> factory) {
		return MEDIA_PLAYER_FACTORIES.remove(factory);
	}
	
	/**
	 * Returns true if there exists a {@link MediaLoader} available for the given {@link MediaType}.
	 * @param type the media type
	 * @return boolean
	 */
	public static synchronized final boolean isMediaSupported(MediaType type) {
		List<MediaLoader<?>> loaders = getMediaLoaders(type);
		// if we dont have a loader, then its not supported
		if (loaders == null || loaders.size() == 0) {
			return false;
		}
		// next check if we can play the media
		for (MediaLoader<?> loader : loaders) {
			// for each media loader of the given media type
			// we need to see if it has an associated player
			Class<?> media = loader.getMediaType();
			if (PlayableMedia.class.isAssignableFrom(media)) {
				Class<? extends PlayableMedia> clazz = media.asSubclass(PlayableMedia.class);
				// we can, so we need to make sure there is a player for it
				MediaPlayerFactory<?> player = getMediaPlayerFactory(clazz);
				if (player == null) {
					return false;
				}
			}
		}
		// if we make it here, then its supported
		return true;
	}
	
	/**
	 * Returns true if the given file is contained in the media library.
	 * @param fileName the file name
	 * @param type the media type
	 * @return boolean
	 */
	public synchronized boolean containsMedia(String fileName, MediaType type) {
		FileSystem system = FileSystems.getDefault();
		// get the full path to the media given the type
		String path = this.getMediaTypeFullPath(type);
		Path fullPath = system.getPath(path);
		Path rootPath = system.getPath(this.basePath);
		// if it exists on the file system or the media map (which both should
		// always return the same boolean, but for extra assurance)
		boolean fileExists = Files.exists(fullPath);
		boolean inLibrary = this.media.containsKey(rootPath.relativize(fullPath));
		return fileExists || inLibrary;
	}
	
	/**
	 * Returns the list of thumbnails for the given media type.
	 * @param type the media type
	 * @return List&lt;{@link MediaThumbnail}&gt;
	 */
	public synchronized List<MediaThumbnail> getThumbnails(MediaType type) {
		List<MediaThumbnail> thumbnails = new ArrayList<MediaThumbnail>();
		for (MediaThumbnail thumbnail : this.thumbnails) {
			if (thumbnail.getMediaType() == type) {
				thumbnails.add(thumbnail);
			}
		}
		return thumbnails;
	}
	
	/**
	 * Returns the thumbnail for the given media.
	 * @param media the media
	 * @return {@link MediaThumbnail}
	 */
	public synchronized MediaThumbnail getThumbnail(Media media) {
		for (MediaThumbnail thumbnail : this.thumbnails) {
			if (thumbnail.getFile().getRelativePath().equals(media.getFile().getRelativePath())) {
				return thumbnail;
			}
		}
		return media.getThumbnail(THUMBNAIL_SIZE);
	}
	
	/**
	 * Returns the thumbnail for the given media.
	 * @param filePath the relative file name and path of the media
	 * @return {@link MediaThumbnail}
	 */
	private MediaThumbnail getThumbnail(String filePath) {
		for (MediaThumbnail thumbnail : this.thumbnails) {
			if (thumbnail.getFile().getRelativePath().equals(filePath)) {
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
	public synchronized Media addMedia(String filePath) throws FileNotFoundException, FileAlreadyExistsException, NoMediaLoaderException, MediaException, IOException {
		Media media = this.loadFromFileSystem(filePath);
		
		// add a weak reference to it
		this.media.put(media.getFile().getRelativePath(), new WeakReference<Media>(media));
		// add it to the thumbnails list
		this.thumbnails.add(media.getThumbnail(THUMBNAIL_SIZE));
		// resort the thumbnails
		Collections.sort(this.thumbnails);
		// save a new thumbnails file
		this.saveThumbnailsFile(media.getType());
		
		return media;
	}

	/**
	 * Adds the given media stream to the media library.
	 * <p>
	 * This will save the stream to a file.
	 * @param stream the media stream
	 * @param type the media type
	 * @param fileName the destination file name
	 * @return {@link Media}
	 * @throws MediaException thrown if an exception occurs while reading the media
	 * @throws NoMediaLoaderException thrown if no {@link MediaLoader} exists for the given media
	 * @throws FileNotFoundException thrown if the given file does not exist
	 * @throws FileAlreadyExistsException thrown if the given file name is already taken by a file in the media library
	 * @throws IOException thrown if an IO error occurs
	 */
	public synchronized Media addMedia(InputStream stream, MediaType type, String fileName) throws FileNotFoundException, FileAlreadyExistsException, NoMediaLoaderException, MediaException, IOException {
		// make sure the given type is supported
		if (isMediaSupported(type)) {
			// get the destination path
			String mediaPath = this.getMediaTypeFullPath(type);
			String path = mediaPath + FileUtilities.getSeparator() + fileName;
			File file = new File(path);
			if (!file.exists()) {
				// write the input stream to the file
				try (FileOutputStream fos = new FileOutputStream(file)) {
					byte[] buffer = new byte[1024];
				    while (true) {
				    	int readCount = stream.read(buffer);
				    	if (readCount < 0) {
				    		break;
				    	}
				    	fos.write(buffer, 0, readCount);
				    }
				}
				
				Media media = this.loadFromMediaLibrary(path);
				
				// add a weak reference to it
				this.media.put(media.getFile().getRelativePath(), new WeakReference<Media>(media));
				// add it to the thumbnails list
				this.thumbnails.add(media.getThumbnail(THUMBNAIL_SIZE));
				// resort the thumbnails
				Collections.sort(this.thumbnails);
				// save a new thumbnails file
				this.saveThumbnailsFile(media.getType());
				
				return media;
			} else {
				throw new FileAlreadyExistsException(path);
			}
		} else {
			throw new NoMediaLoaderException(type.toString());
		}
	}
	
	/**
	 * Returns the media from the media library.
	 * <p>
	 * Returns null if the media is not found.
	 * @param file the media file
	 * @return {@link Media}
	 * @throws MediaException if an exception occurs while loading the media
	 * @throws NoMediaLoaderException thrown if a media loader does not exist for the media
	 */
	public synchronized Media getMedia(MediaFile file) throws MediaException, NoMediaLoaderException {
		return this.getMedia(file, false);
	}
	
	/**
	 * Returns the media from the media library.
	 * <p>
	 * Returns null if the media is not found.
	 * <p>
	 * If the newInstance parameter is true a new instance of the media is created. This is useful when a
	 * media object has shared resources that should not be copied but multiple copies need to be used.
	 * @param file the media file
	 * @param newInstance true if a new instance of the media should be returned
	 * @return {@link Media}
	 * @throws MediaException if an exception occurs while loading the media
	 * @throws NoMediaLoaderException thrown if a media loader does not exist for the media
	 */
	public synchronized Media getMedia(MediaFile file, boolean newInstance) throws MediaException, NoMediaLoaderException {
		String relativePath = file.getRelativePath();
		WeakReference<Media> ref = this.media.get(relativePath);
		// make sure the key is there
		if (ref == null) return null;
		// check if it was reclaimed
		if (ref.get() == null || newInstance) {
			// then we need to reload the media
			Media media = this.loadFromMediaLibrary(file.getFullPath());
			// create a new weak reference to the media
			ref = new WeakReference<Media>(media);
			// store the weak reference
			this.media.put(relativePath, ref);
			// return the media
			return media;
		}
		return ref.get();
	}
	
	/**
	 * Returns the media for the given relative path.
	 * <p>
	 * The relative path should be something like media/audio.
	 * @param relativePath the relative path to the media
	 * @return {@link Media}
	 * @throws MediaException if an exception occurs while loading the media
	 * @throws NoMediaLoaderException thrown if a media loader does not exist for the media
	 */
	public synchronized Media getMedia(String relativePath) throws MediaException, NoMediaLoaderException {
		return this.getMedia(relativePath, false);
	}
	
	/**
	 * Returns the media for the given relative path.
	 * <p>
	 * The relative path should be something like media/audio.
	 * @param relativePath the relative path to the media
	 * @param newInstance true if a new instance of the media should be returned
	 * @return {@link Media}
	 * @throws MediaException if an exception occurs while loading the media
	 * @throws NoMediaLoaderException thrown if a media loader does not exist for the media
	 */
	public synchronized Media getMedia(String relativePath, boolean newInstance) throws MediaException, NoMediaLoaderException {
		WeakReference<Media> ref = this.media.get(relativePath);
		// make sure the key is there
		if (ref == null) return null;
		// check if it was reclaimed
		if (ref.get() == null || newInstance) {
			// get the full path
			String path = this.basePath + FileUtilities.getSeparator() + relativePath;
			// then we need to reload the media
			Media media = this.loadFromMediaLibrary(path);
			// create a new weak reference to the media
			ref = new WeakReference<Media>(media);
			// store the weak reference
			this.media.put(relativePath, ref);
			// return the media
			return media;
		}
		return ref.get();
	}
	
	/**
	 * Removes the given media from the media library.
	 * @param file the media file
	 * @return boolean
	 * @throws IOException thrown if the file fails to be deleted
	 */
	public synchronized boolean removeMedia(MediaFile file) throws IOException {
		String relativePath = file.getRelativePath();
		// make sure the media is removed
		WeakReference<Media> media = this.media.remove(relativePath);
		// remove the thumbnail (no resorting needed)
		MediaThumbnail thumbnail = this.getThumbnail(relativePath);
		this.thumbnails.remove(thumbnail);
		// delete the file
		Path path = FileSystems.getDefault().getPath(file.getFullPath());
		boolean deleted = Files.deleteIfExists(path);
		
		if (media != null && deleted) {
			// save a new thumbnails file
			this.saveThumbnailsFile(thumbnail.getMediaType());
			return true;
		}
		return false;
	}
}

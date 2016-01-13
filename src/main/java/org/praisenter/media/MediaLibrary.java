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

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Tag;
import org.praisenter.xml.XmlIO;

/**
 * A collection of media that has been loaded into a specific location and converted
 * into supported formats with additional files generated for enhanced performance.
 * <p>
 * Obtain a {@link MediaLibrary} instance by calling the {@link #open(Path, MediaThumbnailSettings)}
 * static method. Only one instance should be created for each path. Multiple instances
 * modifying the same path can have unexpected results and can show different sets of media.
 * <p>
 * This class is intended to be thread safe within this application but can still contend
 * with other programs during disk operations.
 * <p>
 * Opening a media library will initiate a process of verification of the current media
 * at the given path. This process can take some time as it generates any missing metadata, 
 * thumbnails and frames.
 * <p>  
 * While it is possible to place media directly into the path and have the {@link MediaLibrary}
 * generate the necessary files as described above, this is not recommended, but cannot
 * be prevented.
 * @author William Bittle
 * @version 3.0.0
 */
public final class MediaLibrary {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	// constants
	
	/** The directory to store the metadata files */
	private static final String METADATA_DIR = "_metadata";
	
	/** The directory to store the thumbnail files */
	private static final String THUMB_DIR = "_thumbs";
	
	/** The directory to store the video frame files */
	private static final String FRAME_DIR = "_frames";
	
	/** The directory to store temporary files */
	private static final String TEMP_DIR = "_temp";
	
	/** The suffix added to a media file for metadata */
	private static final String METADATA_EXT = "_metadata.xml";
	
	/** The suffix added to a media file for thumbnails */
	private static final String THUMB_EXT = "_thumb.png";
	
	/** The suffix added to a media file for frames */
	private static final String FRAME_EXT = "_frame.jpg";

	// instance variables
	
	/** The root path to the media library */
	private final Path path;
	
	/** The full path to the metatdata */
	private final Path metadataPath;
	
	/** The full path to the thumbnails */
	private final Path thumbsPath;
	
	/** The full path to the frames */
	private final Path framesPath;
	
	/** The import filter */
	private final MediaImportFilter importFilter;
	
	/** The thumbnail settings */
	private final MediaThumbnailSettings settings;
	
	// loaded
	
	/** The media loaders */
	private final MediaLoader[] loaders;
	
	/** The media */
	private final Map<Path, Media> media;
	
	// FIXME this should be global so the tags can be used for slides and such
	/** The global set of media tags */
	private final Set<Tag> tags;
	
	/**
	 * Sets up a new {@link MediaLibrary} at the given path using the {@link DefaultMediaImportFilter}
	 * with the given {@link MediaThumbnailSettings}.
	 * @param path the root path to the media library
	 * @param settings the thumbnail settings
	 * @return {@link MediaLibrary}
	 * @throws IOException if an IO error occurs
	 */
	public static final MediaLibrary open(Path path, MediaThumbnailSettings settings) throws IOException {
		return open(path, null, settings);
	}
	
	/**
	 * Sets up a new {@link MediaLibrary} at the given path using the {@link DefaultMediaImportFilter}
	 * with the given {@link MediaThumbnailSettings}.
	 * @param path the root path to the media library
	 * @param importFilter the import filter
	 * @param settings the thumbnail settings
	 * @return {@link MediaLibrary}
	 * @throws IOException if an IO error occurs
	 */
	public static final MediaLibrary open(Path path, MediaImportFilter importFilter, MediaThumbnailSettings settings) throws IOException {
		MediaLibrary library = new MediaLibrary(path, importFilter, settings);
		library.initialize();
		return library;
	}
	
	/**
	 * Private constructor.
	 * @param path the path to initialize in
	 * @param importFilter the import filter
	 * @param settings the thumbnail settings
	 */
	private MediaLibrary(Path path, MediaImportFilter importFilter, MediaThumbnailSettings settings) {
		this.path = path;
		this.metadataPath = path.resolve(METADATA_DIR);
		this.thumbsPath = path.resolve(THUMB_DIR);
		this.framesPath = path.resolve(FRAME_DIR);
		
		this.importFilter = importFilter == null ? new DefaultMediaImportFilter() : importFilter;
		
		this.settings = settings;
		
		this.loaders = new MediaLoader[] {
			new ImageMediaLoader(settings),
			new VideoMediaLoader(settings),
			new AudioMediaLoader(settings)
		};
		
		this.media = new HashMap<Path, Media>();
		this.tags = new TreeSet<Tag>();
	}

	/**
	 * Initializes the media library at the given path.
	 * @throws IOException
	 */
	private final void initialize() throws IOException {
		// make sure the paths exist
		Files.createDirectories(this.path);
		// for metadata and thumbnails
		Files.createDirectories(this.metadataPath);
		Files.createDirectories(this.thumbsPath);
		Files.createDirectories(this.framesPath);
		
		// load existing meta data into a temporary map for verification
		Map<Path, MediaMetadata> metadata = new HashMap<Path, MediaMetadata>();
		FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
		try (DirectoryStream<Path> dir = Files.newDirectoryStream(this.metadataPath)) {
			for (Path path : dir) {
				if (Files.isRegularFile(path)) {
					String mimeType = map.getContentType(path.toString());
					if (mimeType.equals("application/xml")) {
						try {
							MediaMetadata meta = XmlIO.read(path, MediaMetadata.class);
							metadata.put(meta.path, meta);
						} catch (Exception e) {
							// just continue loading
							LOGGER.warn("Failed to read file '" + path.toAbsolutePath().toString() + "'.", e);
						}
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Failed loading existing media metadata.", ex);
		}
		
		// scan media folder and perform verification
		try (DirectoryStream<Path> dir = Files.newDirectoryStream(this.path)) {
			for (Path path : dir) {
				if (Files.isRegularFile(path)) {
					boolean update = false;
					
					// get the metadata
					MediaMetadata meta = metadata.get(path);
					
					// are we cached at all
					if (meta != null) {
						// compare last modified values
						BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
						if (attributes.lastModifiedTime().toMillis() != meta.lastModified) {
							// the file has been modified, so update
							update = true;
						}
					} else {
						// no existing metadata
						update = true;
					}
					
					// verify existence of thumbnail (except for audio)
					Path tPath = this.thumbsPath.resolve(path.getFileName().toString() + THUMB_EXT);
					if (!Files.exists(tPath)) {
						if (meta == null || (meta != null && meta.type != MediaType.AUDIO)) {
							update = true;
						}
					}
					
					// verify existence of frame for videos
					Path fPath = this.framesPath.resolve(path.getFileName().toString() + FRAME_EXT);
					if (!Files.exists(fPath)) {
						if (meta == null || (meta != null && meta.type == MediaType.VIDEO)) {
							update = true;
						}
					}
					
					if (update) {
						try {
							// there's potential here for the media to not be supported by 
							// JavaFX, but we'll still show it in the library
							// for now we'll just leave this open and just check when we
							// attempt to play any media for exceptions
							
							// if the meta data was just out of date
							// we want to keep the tags that were there
							update(path, meta != null ? meta.tags : null);
						} catch (Exception e) {
							LOGGER.warn("Failed to load file '" + path.toAbsolutePath().toString() + "'.", e);
						}
					} else {
						Media media = null;
						if (meta.type != MediaType.AUDIO) {
							media = new Media(meta, loadThumbnail(tPath));
						} else {
							media = new Media(meta, settings.audioDefaultThumbnail);
						}
						this.media.put(media.metadata.path, media);
					}
				}
			}
		}
		
		// collect all tags
		for (Media media : this.media.values()) {
			if (media.metadata.tags.size() > 0) {
				tags.addAll(media.metadata.tags);
			}
		}
	}
	
	/**
	 * Loads the given thumbnail or null if not found or cannot be read.
	 * @param path the path
	 * @return BufferedImage
	 */
	private final BufferedImage loadThumbnail(Path path) {
		try {
			return ImageIO.read(path.toFile());
		} catch (IOException e) {
			LOGGER.warn("Failed to load thumbnail '" + path.toAbsolutePath().toString() + "'", e);
		}
		return null;
	}
	
	/**
	 * Updates the given media's metadata (including tags) and thumbnail.
	 * @param path the file
	 * @param tags the tags to add
	 * @return {@link Media}
	 * @throws MediaFormatException if the media format isn't something recognized or readable
	 * @throws IOException if an IO error occurs
	 */
	private final Media update(Path path, Set<Tag> tags) throws MediaFormatException, IOException {
		LoadedMedia lm = load(path);
		Media media = lm.media;
		
		// add tags
		if (tags != null) {
			media.metadata.tags.addAll(tags);
		}
		
		// save the metadata
		try {
			saveMetadata(media);
		} catch (Exception e) {
			LOGGER.warn("Failed to save metadata for '" + path.toAbsolutePath().toString() + "'", e);
		}
		
		// save a thumbnail for video and images
		if (media.metadata.type != MediaType.AUDIO) {
			try {
				saveThumbnail(media);
			} catch (Exception e) {
				LOGGER.warn("Failed to save thumbnail for '" + path.toAbsolutePath().toString() + "'", e);
			}
		}
		
		// save a single frame for videos
		if (media.metadata.type == MediaType.VIDEO && lm.image != null) {
			try {
				saveFrame(lm);
			} catch (Exception e) {
				LOGGER.warn("Failed to save image for '" + path.toAbsolutePath().toString() + "'", e);
			}
		}
		
		// we loaded the media, so add it to the map
		this.media.put(media.metadata.path, media);
		
		return media;
	}
	
	/**
	 * Saves the metadata of the given media.
	 * @param media the media
	 * @throws JAXBException if an error occurs serializing to XML
	 * @throws IOException if an IO error occurs
	 */
	private final void saveMetadata(Media media) throws JAXBException, IOException {
		Path path = media.metadata.path;
		Path mPath = this.metadataPath.resolve(path.getFileName().toString() + METADATA_EXT);
		XmlIO.save(mPath, media.metadata);
	}
	
	/**
	 * Saves the thumbnail of the given media.
	 * @param media the media
	 * @throws IOException if an IO error occurs
	 */
	private final void saveThumbnail(Media media) throws IOException {
		Path path = media.metadata.path;
		Path tPath = this.thumbsPath.resolve(path.getFileName().toString() + THUMB_EXT);
		ImageIO.write(media.thumbnail, "png", tPath.toFile());
	}
	
	/**
	 * Saves the image of the given media.
	 * @param media the media
	 * @throws IOException if an IO error occurs
	 */
	private final void saveFrame(LoadedMedia media) throws IOException {
		Path path = media.media.metadata.path;
		Path tPath = this.framesPath.resolve(path.getFileName().toString() + FRAME_EXT);
		ImageIO.write(media.image, "jpg", tPath.toFile());
	}
	
	/**
	 * Loads the given file and returns a {@link Media} object describing the file.
	 * @param path the file
	 * @return {@link Media}
	 * @throws MediaFormatException if the media format isn't something recognized or readable
	 * @throws IOException if an IO error occurs
	 */
	private final LoadedMedia load(Path path) throws MediaFormatException, IOException {
		List<MediaLoader> loaders = getLoadersForPath(path);
		
		// any supporting media loaders?
		if (loaders.size() == 0) {
			LOGGER.warn("No supporting media loaders for file '" + path.toAbsolutePath().toString() + "'.");
			throw new MediaFormatException();
		}
		
		LoadedMedia media = null;
		
		// iterate through the loaders
		int n = loaders.size();
		for (int i = 0; i < n; i++) {
			MediaLoader loader = loaders.get(i);
			try {
				// the first successful loader wins
				media = loader.load(path);
				break;
			} catch (IOException e) {
				LOGGER.warn("Failed to load file '" + path.toAbsolutePath().toString() + "': ", e);
			} catch (MediaFormatException e) {
				LOGGER.warn("Unsupported media format for '" + path.toAbsolutePath().toString() + "' using media loader '" + loader.getClass().getName() + "'.", e);
			}
		}
		
		if (media == null) {
			LOGGER.warn("The supporting media loaders couldn't load '" + path.toAbsolutePath().toString() + "'.");
			throw new MediaFormatException();
		}
		
		return media;
	}
	
	/**
	 * Returns a list of {@link MediaLoader}s that support the given file type.
	 * @param path the file
	 * @return List&lt;{@link MediaLoader}&gt;
	 */
	private final List<MediaLoader> getLoadersForPath(Path path) {
		FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
		String mimeType = map.getContentType(path.toString());
		
		// it should be rare that there are collisions in mimetypes with
		// loading types, but it happens, mp4 for example can be audio or
		// video
		List<MediaLoader> ldrs = new ArrayList<MediaLoader>();
		for (MediaLoader loader : this.loaders) {
			if (loader.isSupported(mimeType)) {
				ldrs.add(loader);
			}
		}
		
		return ldrs;
	}
	
	/**
	 * Copies the given file to the media library performing any pre-processing before hand.
	 * @param source the source file
	 * @return Path the new media library path
	 * @throws FileNotFoundException if the given source file isn't found
	 * @throws FileAlreadyExistsException if a file with the same name already exists
	 * @throws MediaFormatException if the media format isn't something recognized or readable
	 * @throws TranscodeException if the media failed to be transcoded into a supported format
	 * @throws IOException if an IO error occurs
	 */
	private final Path copy(Path source) throws FileAlreadyExistsException, FileNotFoundException, IOException, TranscodeException, MediaFormatException {
		// make sure it exists and is a file
		if (Files.exists(source) && Files.isRegularFile(source)) {
			MediaType type = getMediaType(source.toString());
			return insert(source, type, source.getFileName().toString());
		} else {
			throw new FileNotFoundException();
		}
	}

	/**
	 * Copies the given input stream to the media library performing any pre-processing before hand.
	 * @param source the source input stream
	 * @param name the file name of the input stream
	 * @return Path the new media library path
	 * @throws FileAlreadyExistsException if a file with the same name already exists
	 * @throws MediaFormatException if the media format isn't something recognized or readable
	 * @throws TranscodeException if the media failed to be transcoded into a supported format
	 * @throws IOException if an IO error occurs
	 */
	private final Path copy(InputStream source, String name) throws FileAlreadyExistsException, IOException, TranscodeException, MediaFormatException {
		// copy to a temp file
		Path temp = this.path.resolve(TEMP_DIR).resolve(UUID.randomUUID().toString() + "_" + name);
		Files.copy(source, temp, StandardCopyOption.REPLACE_EXISTING);
		
		// insert the media into the library
		MediaType type = getMediaType(temp.toString());
		Path target = insert(temp, type, name);
		
		// delete the temp file
		Files.delete(temp);
		return target;
	}
	
	/**
	 * Inserts the given source into the media library performing any pre-processing before hand.
	 * @param source the source file
	 * @param type the media type
	 * @param name the file name
	 * @return Path the new media library path
	 * @throws FileAlreadyExistsException if a file with the same name already exists
	 * @throws MediaFormatException if the media format isn't something recognized or readable
	 * @throws TranscodeException if the media failed to be transcoded into a supported format
	 * @throws IOException if an IO error occurs
	 */
	private final Path insert(Path source, MediaType type, String name) throws FileAlreadyExistsException, IOException, TranscodeException, MediaFormatException {
		if (type == null) {
			throw new MediaFormatException("Unknown media type for file '" + source.toAbsolutePath().toString() + "'.");
		}
		
		Path target = this.importFilter.getTarget(this.path, name, type);
		this.importFilter.filter(source, target, type);
		return target;
	}
	
	/**
	 * Returns the media type for the given file name using the packaged mime.types file in META-INF.
	 * @param name the file name
	 * @return {@link MediaType}
	 */
	private final MediaType getMediaType(String name) {
		FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
		String mimeType = map.getContentType(name);
		return MediaType.getMediaTypeFromMimeType(mimeType);
	}
	
	/**
	 * Deletes the given media.
	 * @param path the path
	 * @return boolean if the deletion succeeds
	 * @throws IOException if an IO error occurs
	 */
	private final boolean delete(Path path) throws IOException {
		// delete the metadata, thumbnail, and file
		// FIXME test what happens when file is in use during presentation
		Files.deleteIfExists(path);

		Path mPath = this.metadataPath.resolve(path.getFileName().toString() + METADATA_EXT);
		Files.deleteIfExists(mPath);
		
		Path tPath = this.thumbsPath.resolve(path.getFileName().toString() + THUMB_EXT);
		Files.deleteIfExists(tPath);

		Path fPath = this.framesPath.resolve(path.getFileName().toString() + FRAME_EXT);
		Files.deleteIfExists(fPath);
		
		return true;
	}
	
	/**
	 * Renames the given media.
	 * @param path the path
	 * @return boolean if the deletion succeeds
	 * @throws FileAlreadyExistsException if the target file name already exists
	 * @throws IOException if an IO error occurs
	 */
	private final boolean move(Media media, String name) throws FileAlreadyExistsException, IOException {
		// FIXME test what happens when file is in use during presentation
		Path source = media.metadata.path;
		String name0 = source.getFileName().toString();
		String name1 = name;
		
		// make sure the file will have the same extension
		int idx = name0.lastIndexOf('.');
		String ext = name0.substring(idx);
		if (!name1.endsWith(ext)) {
			name1 += ext;
		}
		
		// create paths to copy the file and metadata
		Path target = source.getParent().resolve(name);
		Path smPath = this.metadataPath.resolve(name0 + METADATA_EXT);
		Path tmPath = this.metadataPath.resolve(name1 + METADATA_EXT);
		
		// move (rename) the media
		Files.move(source, target);
		
		// metadata
		Files.move(smPath, tmPath);
		
		// thumbnail (audio doesn't have thumbnails)
		if (media.metadata.getType() != MediaType.AUDIO) {
			Path stPath = this.thumbsPath.resolve(name0 + THUMB_EXT);
			Path ttPath = this.thumbsPath.resolve(name1 + THUMB_EXT);
			Files.move(stPath, ttPath);
		}
		
		// frame (only for video)
		if (media.metadata.getType() == MediaType.VIDEO) {
			Path sfPath = this.framesPath.resolve(name0 + FRAME_EXT);
			Path tfPath = this.framesPath.resolve(name1 + FRAME_EXT);
			Files.move(sfPath, tfPath);
		}
		
		Media media1 = new Media(MediaMetadata.forRenamed(target, media.metadata), media.thumbnail);
		
		this.media.remove(source);
		this.media.put(target, media1);
		
		return true;
	}
	
	// public interface
	
	/**
	 * Returns true if the given path is in the library.
	 * @param path the path
	 * @return boolean
	 */
	public synchronized boolean contains(Path path) {
		return this.media.containsKey(path);
	}
	
	/**
	 * Returns the media for the given path.
	 * <p>
	 * This method assumes the media is already in the library and
	 * has been initialized with metadata.
	 * <p>
	 * Returns null if the media is not in the library or hasn't
	 * been initialized.
	 * @param path the path
	 * @return {@link Media}
	 */
	public synchronized Media get(Path path) {
		return this.media.get(path);
	}
	
	/**
	 * Returns a list of all the media currently being maintained in the library.
	 * @return Collection&lt;{@link Media}&gt;
	 */
	public synchronized List<Media> all() {
		return new ArrayList<Media>(this.media.values());
	}
	
	/**
	 * Adds the media at the given path to the library.
	 * @param path the path
	 * @return {@link Media}
	 * @throws FileNotFoundException if the given source file isn't found
	 * @throws FileAlreadyExistsException if a file with the same name already exists
	 * @throws MediaFormatException if the media format isn't something recognized or readable
	 * @throws TranscodeException if the media failed to be transcoded into a supported format
	 * @throws IOException if an IO error occurs
	 */
	public synchronized Media add(Path path) throws FileAlreadyExistsException, FileNotFoundException, IOException, TranscodeException, MediaFormatException {
		// copy it to the library
		Path libraryPath = copy(path);
		// attempt to load it
		Media media = update(libraryPath, null);
		// return it
		return media;
	}
	
	/**
	 * Adds the media in the given stream to the library.
	 * @param stream the stream
	 * @param name the file name
	 * @return {@link Media}
	 * @throws FileAlreadyExistsException if a file with the same name already exists
	 * @throws MediaFormatException if the media format isn't something recognized or readable
	 * @throws TranscodeException if the media failed to be transcoded into a supported format
	 * @throws IOException if an IO error occurs
	 */
	public synchronized Media add(InputStream stream, String name) throws FileAlreadyExistsException, IOException, TranscodeException, MediaFormatException {
		// copy it to the library
		Path libraryPath = copy(stream, name);
		// attempt to load it
		Media media = update(libraryPath, null);
		// return it
		return media;
	}
	
	/**
	 * Removes the media from the library and deletes all generated files.
	 * @param media the media to remove
	 * @throws IOException if an IO error occurs
	 */
	public synchronized void remove(Media media) throws IOException {
		Path path = media.metadata.path;
		
		// delete the files
		delete(path);
		
		// remove from the library's cache
		this.media.remove(path);
	}

	/**
	 * Renames the media to the given name.
	 * @param media the media
	 * @param name the new name
	 * @throws FileAlreadyExistsException if a file already exists with the given name
	 * @throws IOException if an IO error occurs
	 */
	public synchronized void rename(Media media, String name) throws FileAlreadyExistsException, IOException {
		move(media, name);
	}
	
	/**
	 * Adds the given tag to the given media and saves the metadata.
	 * @param media the media
	 * @param tag the new tag
	 * @return boolean true if the tag was added successfully
	 * @throws JAXBException if the media metadata failed to save
	 * @throws IOException if an IO error occurs
	 */
	public synchronized boolean addTag(Media media, Tag tag) throws JAXBException, IOException {
		this.tags.add(tag);
		boolean added = media.metadata.tags.add(tag);
		if (added) {
			saveMetadata(media);
		}
		return added;
	}
	
	/**
	 * Adds the given tags to the given media and saves the metadata.
	 * @param media the media
	 * @param tags the new tags
	 * @return boolean true if the tags were added successfully
	 * @throws JAXBException if the media metadata failed to save
	 * @throws IOException if an IO error occurs
	 */	
	public synchronized boolean addTags(Media media, Collection<Tag> tags) throws JAXBException, IOException {
		this.tags.addAll(tags);
		boolean added = media.metadata.tags.addAll(tags);
		if (added) {
			saveMetadata(media);
		}
		return added;
	}
	
	/**
	 * Sets the given tags on the given media and saves the metadata.
	 * @param media the media
	 * @param tags the new tags
	 * @return boolean true if the tags were set successfully
	 * @throws JAXBException if the media metadata failed to save
	 * @throws IOException if an IO error occurs
	 */	
	public synchronized boolean setTags(Media media, Collection<Tag> tags) throws JAXBException, IOException {
		this.tags.addAll(tags);
		boolean changed = media.metadata.tags.addAll(tags);
		changed |= media.metadata.tags.retainAll(tags);
		if (changed) {
			saveMetadata(media);
		}
		return changed;
	}
	
	/**
	 * Removes the given tag from the given media and saves the metadata.
	 * @param media the media
	 * @param tag the tag to remove
	 * @return boolean true if the tag was removed successfully
	 * @throws JAXBException if the media metadata failed to save
	 * @throws IOException if an IO error occurs
	 */	
	public synchronized boolean removeTag(Media media, Tag tag) throws JAXBException, IOException {
		boolean removed = media.metadata.tags.remove(tag);
		if (removed) {
			saveMetadata(media);
		}
		return removed;
	}
	
	/**
	 * Removes the given tags from the given media and saves the metadata.
	 * @param media the media
	 * @param tags the tags to remove
	 * @return boolean true if the tags were removed successfully
	 * @throws JAXBException if the media metadata failed to save
	 * @throws IOException if an IO error occurs
	 */	
	public synchronized boolean removeTags(Media media, Collection<Tag> tags) throws JAXBException, IOException {
		boolean removed = media.metadata.tags.removeAll(tags);
		if (removed) {
			saveMetadata(media);
		}
		return removed;
	}

	/**
	 * Returns a snapshot of all the tags on the media.	
	 * @return Set&lt;{@link Tag}&gt;
	 */
	public synchronized Set<Tag> getTags() {
		return new TreeSet<Tag>(this.tags);
	}
}

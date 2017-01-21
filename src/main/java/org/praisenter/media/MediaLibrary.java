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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.InvalidFormatException;
import org.praisenter.LockMap;
import org.praisenter.Tag;
import org.praisenter.ThumbnailSettings;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.StringManipulator;
import org.praisenter.utility.Zip;
import org.praisenter.xml.XmlIO;

/**
 * A collection of media that has been loaded into a specific location and converted
 * into supported formats with additional files generated for enhanced performance.
 * <p>
 * Obtain a {@link MediaLibrary} instance by calling the {@link #open(Path, ThumbnailSettings)}
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
	
	/** The suffix added to a media file for metadata */
	private static final String METADATA_EXT = "_metadata.xml";
	
	// instance variables
	
	/** The root path to the media library */
	private final Path path;
	
	/** The full path to the metatdata */
	private final Path metadataPath;
	
	/** The import filter */
	private final MediaImportFilter importFilter;
	
	/** The thumbnail settings */
	private final ThumbnailSettings thumbnailSettings;
	
	// loaded
	
	/** The media loaders */
	private final MediaLoader[] loaders;
	
	/** The media */
	private final Map<UUID, Media> media;
	
	// locking
	
	private final LockMap<String> locks;
	private final StampedLock exportLock;
	
	/**
	 * Sets up a new {@link MediaLibrary} at the given path using the {@link DefaultMediaImportFilter}
	 * with the given {@link ThumbnailSettings}.
	 * @param path the root path to the media library
	 * @param thumbnailSettings the thumbnail settings
	 * @return {@link MediaLibrary}
	 * @throws IOException if an IO error occurs
	 */
	public static final MediaLibrary open(Path path, ThumbnailSettings thumbnailSettings) throws IOException {
		return open(path, null, thumbnailSettings);
	}
	
	/**
	 * Sets up a new {@link MediaLibrary} at the given path using the {@link DefaultMediaImportFilter}
	 * with the given {@link ThumbnailSettings}.
	 * @param path the root path to the media library
	 * @param importFilter the import filter
	 * @param thumbnailSettings the thumbnail settings
	 * @return {@link MediaLibrary}
	 * @throws IOException if an IO error occurs
	 */
	public static final MediaLibrary open(Path path, MediaImportFilter importFilter, ThumbnailSettings thumbnailSettings) throws IOException {
		MediaLibrary library = new MediaLibrary(path, importFilter, thumbnailSettings);
		library.initialize();
		return library;
	}
	
	/**
	 * Private constructor.
	 * @param path the path to initialize in
	 * @param importFilter the import filter
	 * @param settings the thumbnail settings
	 */
	private MediaLibrary(Path path, MediaImportFilter importFilter, ThumbnailSettings thumbnailSettings) {
		this.path = path;
		this.metadataPath = path.resolve(METADATA_DIR);
		this.thumbnailSettings = thumbnailSettings;
		
		this.importFilter = importFilter == null ? new DefaultMediaImportFilter() : importFilter;
		
		this.loaders = new MediaLoader[] {
			new ImageMediaLoader(thumbnailSettings),
			new VideoMediaLoader(thumbnailSettings),
			new AudioMediaLoader(thumbnailSettings)
		};
		
		this.media = new ConcurrentHashMap<UUID, Media>();
		this.locks = new LockMap<String>();
		this.exportLock = new StampedLock();
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
		
		// load existing meta data into a temporary map for verification
		Map<Path, Media> metadata = new HashMap<Path, Media>();
		try (DirectoryStream<Path> dir = Files.newDirectoryStream(this.metadataPath)) {
			for (Path path : dir) {
				if (Files.isRegularFile(path)) {
					if (MimeType.XML.check(path)) {
						try {
							Media media = XmlIO.read(path, Media.class);
							// when we read the metadata we need to update it with
							// the path to the media file since we don't store it
							media.path = this.path.resolve(media.fileName);
							metadata.put(media.getPath(), media);
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
					Media media = metadata.get(path);
					
					// are we cached at all
					if (media != null) {
						// compare last modified values
						BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
						if (attributes.lastModifiedTime().toMillis() != media.lastModified) {
							// the file has been modified, so update
							update = true;
						}
					} else {
						// no existing metadata
						update = true;
					}
					
					// verify existence of thumbnail (except for audio)
					if (media != null && media.thumbnail == null) {
						if (media.type != MediaType.AUDIO) {
							update = true;
						}
					}
					
					// verify existence of frame for videos
					if (media != null && media.frame == null) {
						if (media.type == MediaType.VIDEO) {
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
							// we want to keep certain metadata information like
							// date added and the tags
							update(path, media);
						} catch (Exception e) {
							LOGGER.warn("Failed to load file '" + path.toAbsolutePath().toString() + "'.", e);
						}
					} else {
						this.media.put(media.id, media);
					}
				}
			}
		}
	}

	private final Object getPathLock(Path path) {
		return this.locks.get(path.getFileName().toString());
	}
	
	private final Object getMediaLock(Media media) {
		return this.locks.get(media.getId().toString());
	}
	
	/**
	 * Returns the media type for the given path.
	 * @param path the path
	 * @return {@link MediaType}
	 */
	private final MediaType getMediaType(Path path) {
		String mimeType = MimeType.get(path);
		return MediaType.getMediaTypeFromMimeType(mimeType);
	}

	/**
	 * Returns the metadata file name for the given path (media file).
	 * @param path the path 
	 * @return String
	 */
	private final String getMetadataFileName(Path path) {
		return path.getFileName().toString() + METADATA_EXT;
	}
	
	/**
	 * Returns the metadata file path for the given path (media file).
	 * @param path the path 
	 * @return Path
	 */
	private final Path getMetadataPath(Path path) {
		return this.metadataPath.resolve(this.getMetadataFileName(path));
	}
	
	/**
	 * Returns a list of {@link MediaLoader}s that support the given file type.
	 * @param path the file
	 * @return List&lt;{@link MediaLoader}&gt;
	 */
	private final List<MediaLoader> getLoaders(Path path) {
		String mimeType = MimeType.get(path);
		
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
	 * Saves the metadata of the given media.
	 * @param media the media
	 * @throws JAXBException if an error occurs serializing to XML
	 * @throws IOException if an IO error occurs
	 */
	private final void saveMetadata(Media media) throws JAXBException, IOException {
		Path path = media.getPath();
		Path mPath = this.getMetadataPath(path);
		XmlIO.save(mPath, media);
	}

	/**
	 * Updates the given media (including tags) and thumbnail.
	 * @param path the file
	 * @param media the existing media; can be null
	 * @return {@link Media}
	 * @throws InvalidFormatException if the media format isn't something recognized or readable
	 * @throws IOException if an IO error occurs
	 */
	private final Media update(Path path, Media media) throws InvalidFormatException, IOException {
		// reload the media
		Media newMedia = this.load(path);
		
		// check for existing metadata
		if (media != null) {
			// update the new metadata with the old
			// date added and tags
			newMedia = Media.forUpdated(media.dateAdded, media.tags != null ? new TreeSet<Tag>(media.tags) : null, newMedia);
		}
		
		// save the metadata
		try {
			saveMetadata(newMedia);
		} catch (Exception e) {
			LOGGER.warn("Failed to save metadata for '" + path.toAbsolutePath().toString() + "'", e);
		}
		
		// we loaded the media, so add it to the map
		this.media.put(newMedia.id, newMedia);
		
		return newMedia;
	}
	
	/**
	 * Loads the given file and returns a {@link Media} object describing the file.
	 * @param path the file
	 * @return {@link Media}
	 * @throws InvalidFormatException if the media format isn't something recognized or readable
	 * @throws IOException if an IO error occurs
	 */
	private final Media load(Path path) throws InvalidFormatException, IOException {
		List<MediaLoader> loaders = this.getLoaders(path);
		
		// any supporting media loaders?
		if (loaders.size() == 0) {
			// remove it from the library
			Files.delete(path);
			LOGGER.warn("No supporting media loaders for file '" + path.toAbsolutePath().toString() + "'.");
			throw new UnsupportedMediaException(new UnknownMediaTypeException(path.toAbsolutePath().toString()));
		}
		
		Media media = null;
		
		// iterate through the loaders
		int n = loaders.size();
		LOGGER.debug("Found " + n + " loaders for '" + path.toAbsolutePath().toString() + "'");
		List<String> errors = new ArrayList<String>();
		for (int i = 0; i < n; i++) {
			MediaLoader loader = loaders.get(i);
			try {
				LOGGER.debug("Attempting to use " + loader.getClass().getName() + " to load '" + path.toAbsolutePath().toString() + "'");
				// the first successful loader wins
				media = loader.load(path);
				break;
			} catch (IOException e) {
				LOGGER.warn("Failed to load file '" + path.toAbsolutePath().toString() + "': ", e);
				errors.add(e.getMessage());
			} catch (InvalidFormatException e) {
				LOGGER.warn("Unsupported media format for '" + path.toAbsolutePath().toString() + "' using media loader '" + loader.getClass().getName() + "'.", e);
				errors.add(e.getMessage());
			}
		}
		
		if (media == null) {
			// remove it from the library
			Files.delete(path);
			LOGGER.warn("The supporting media loaders couldn't load '" + path.toAbsolutePath().toString() + "'.");
			throw new UnsupportedMediaException(path.toAbsolutePath().toString() + Constants.NEW_LINE + String.join(", ", errors));
		}
		
		return media;
	}
	
	/**
	 * Copies the given file to the media library performing any pre-processing before hand.
	 * @param source the source file
	 * @return Path the new media library path
	 * @throws FileNotFoundException if the given source file isn't found
	 * @throws FileAlreadyExistsException if a file with the same name already exists
	 * @throws UnknownMediaTypeException if the media's mime type was not discernible
	 * @throws TranscodeException if the media failed to be transcoded into a supported format
	 * @throws IOException if an IO error occurs
	 */
	private final Path copy(Path source) throws FileAlreadyExistsException, FileNotFoundException, IOException, TranscodeException, UnknownMediaTypeException {
		// make sure it exists and is a file
		if (Files.exists(source) && Files.isRegularFile(source)) {
			// get the media type
			MediaType type = getMediaType(source);
			// check to make sure it's a media file
			if (type == null) {
				throw new UnknownMediaTypeException(source.toAbsolutePath().toString());
			}
			// get the target name based on the import filter
			Path target = this.importFilter.getTarget(this.path, source.getFileName().toString(), type);
			// get the media file into the library
			this.importFilter.filter(source, target, type);
			// return the library path
			return target;
		} else {
			throw new FileNotFoundException(source.toAbsolutePath().toString());
		}
	}

	/**
	 * Deletes the given media.
	 * @param path the path
	 * @return boolean if the deletion succeeds
	 * @throws IOException if an IO error occurs
	 */
	private final boolean delete(Path path) throws IOException {
		Path mPath = this.getMetadataPath(path);
		
		// delete the file
		Files.deleteIfExists(path);

		// delete the metadata
		Files.deleteIfExists(mPath);
		
		return true;
	}
	
	/**
	 * Renames the given media and returns a new media object
	 * representing the renamed media.
	 * @param path the path
	 * @param name the new name
	 * @return {@link Media}
	 * @throws FileAlreadyExistsException if the target file name already exists
	 * @throws JAXBException if the metadata fails to save
	 * @throws IOException if an IO error occurs
	 */
	private final Media move(Media media, String name) throws FileAlreadyExistsException, IOException, JAXBException {
		Path source = media.getPath();
		String name0 = source.getFileName().toString();
		String name1 = name;
		
		// make sure the file will have the same extension
		int idx = name0.lastIndexOf('.');
		if (idx >= 0) {
			String ext = name0.substring(idx);
			if (!name1.endsWith(ext)) {
				name1 += ext;
			}
		}
		
		// create paths to copy the file and metadata
		Path target = source.getParent().resolve(name1);
		
		synchronized(this.getPathLock(target)) {
			// move (rename) the media
			Files.move(source, target);
			
			Media media1 = Media.forRenamed(target, media);
			
			// metadata
			
			// remove old metadata
			Path smPath = this.metadataPath.resolve(name0 + METADATA_EXT);
			
			Files.delete(smPath);
			// save new metadata
			this.saveMetadata(media1);
	
			// just overwrite the media item with the new one
			this.media.put(media.id, media1);
			
			return media1;
		}
	}
	
	// public interface
	
	/**
	 * Returns true if the given path is in the library.
	 * @param id the id
	 * @return boolean
	 */
	public boolean contains(UUID id) {
		return this.media.containsKey(id);
	}
	
	/**
	 * Returns the media for the given path.
	 * <p>
	 * This method assumes the media is already in the library and
	 * has been initialized with metadata.
	 * <p>
	 * Returns null if the media is not in the library or hasn't
	 * been initialized.
	 * @param id the id
	 * @return {@link Media}
	 */
	public Media get(UUID id) {
		return this.media.get(id);
	}
	
	/**
	 * Returns a list of all the media currently being maintained in the library.
	 * @return Collection&lt;{@link Media}&gt;
	 */
	public List<Media> all() {
		return new ArrayList<Media>(this.media.values());
	}
	
	/**
	 * Returns a list of all the media currently being maintained in the library of
	 * the given types.
	 * @param types the media types to return
	 * @return Collection&lt;{@link Media}&gt;
	 */
	public List<Media> all(MediaType... types) {
		if (types == null || types.length == 0) {
			return all();
		}
		ArrayList<Media> all = new ArrayList<Media>();
		for (Media media : this.media.values()) {
			for (MediaType type : types) {
				if (media.type == type) {
					all.add(media);
					break;
				}
			}
		}
		return all;
	}
	
	/**
	 * Returns the number of media items in the library.
	 * @return int
	 */
	public int size() {
		return this.media.size();
	}
	
	/**
	 * Adds the media at the given path to the library.
	 * @param path the path
	 * @return {@link Media}
	 * @throws FileNotFoundException if the given source file isn't found
	 * @throws FileAlreadyExistsException if a file with the same name already exists
	 * @throws UnknownMediaTypeException if the media's mime type was not discernible
	 * @throws InvalidFormatException if the media format isn't something recognized or readable
	 * @throws TranscodeException if the media failed to be transcoded into a supported format
	 * @throws IOException if an IO error occurs
	 */
	public Media add(Path path) throws FileAlreadyExistsException, FileNotFoundException, IOException, TranscodeException, UnknownMediaTypeException, InvalidFormatException {
		// lock on the incoming file name
		synchronized(this.getPathLock(path)) {
			// copy it to the library
			// NOTE: don't synchronize this since it could take a long time
			Path libraryPath = copy(path);
			// attempt to load it
			Media media = update(libraryPath, null);
			// return it
			return media;
		}
	}

	public List<Media> importMedia(Path path) throws FileAlreadyExistsException, FileNotFoundException, IOException, UnknownMediaTypeException, InvalidFormatException {
		byte[] buffer = new byte[1024];
		int length;
		// TODO locking
		
		List<Media> imported = new ArrayList<Media>();
		
		// for an import, we verify that it's a zip file
		// and then import whatever media has metadata as is 
		// (doesn't pass through the filter process)
		
		// 1) copy all media files to the media folder
		// 2) verify each media file has a metadata file
		// 3) if so, write the metadata file
		// 4) if not, delete the media file
		
		// read all metadata first
		Map<String, Media> metadata = new HashMap<String, Media>();
		try (FileInputStream fis = new FileInputStream(path.toFile());
			 ZipInputStream zis = new ZipInputStream(fis)) {
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					String name = entry.getName();
					// check for xml entries
					if (MimeType.XML.check(name)) {
						// its a metadata file
						// read and parse it
						try {
							byte[] data = Zip.read(zis);
							Media media = XmlIO.read(new ByteArrayInputStream(data), Media.class);
							metadata.put(media.fileName, media);
						} catch (Exception ex) {
							// it failed, might be some other type of xml file
							// TODO handle
						}
					}
				}
			}
		}
		
		// then read all media with metadata
		try (FileInputStream fis = new FileInputStream(path.toFile());
			 ZipInputStream zis = new ZipInputStream(fis)) {
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					String name = entry.getName();
					// does it have associated metadata?
					Media media = metadata.get(name);
					if (media != null) {
						try {
							Path mediaFile = this.path.resolve(name);
							// check if a media file of the same name exists
							if (!Files.exists(mediaFile)) {
								// use the UUID
								String newName = StringManipulator.toFileName(media.getId());
								// append extension
								int idx = name.lastIndexOf('.');
								if (idx >= 0) {
									newName += name.substring(idx);
								}
								mediaFile = this.path.resolve(newName);
							}
							// update the media after being renamed
							media = Media.forRenamed(mediaFile, media);
							// copy the media
							try (FileOutputStream fos = new FileOutputStream(mediaFile.toFile())) {
								length = 0;
								while ((length = zis.read(buffer)) > 0) {
									fos.write(buffer, 0, length);
								}
							}
							// write the metadata
							this.saveMetadata(media);
							
							synchronized (this) {
								// update the list of media
								this.media.put(media.getId(), media);
							}
							
							imported.add(media);
						} catch (Exception ex) {
							// it failed, not sure why
							// TODO handle
						}
					} else {
						// TODO no metadata
					}
				} else {
					// TODO not a media file
				}
			}
		}
		
		return imported;
	}
	
	/**
	 * Exports the given media to the given path.
	 * @param path the path; a zip file
	 * @param media the media to export
	 * @throws FileNotFoundException if the given path is not a file or cannot be created
	 * @throws ZipException if an error occurs while building the zip file
	 * @throws IOException if an IO error occurs
	 */
	public void exportMedia(Path path, List<Media> media) throws FileNotFoundException, ZipException, IOException {
		long stamp = this.exportLock.writeLock();
		
		byte[] buffer = new byte[1024];
		int length;

		try (FileOutputStream fos = new FileOutputStream(path.toFile());
			 ZipOutputStream zos = new ZipOutputStream(fos)) {
			for (Media m : media) {
				Path mediaPath = m.getPath();
				Path metaPath = this.getMetadataPath(mediaPath);
				String mediafileName = mediaPath.getFileName().toString();
				String metaFileName = this.getMetadataFileName(mediaPath);
				
				// the media
				try (FileInputStream fis = new FileInputStream(mediaPath.toFile())) {
					ZipEntry entry = new ZipEntry(mediafileName);
					zos.putNextEntry(entry);
					length = 0;
					while ((length = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}
					zos.closeEntry();
				}
				
				// the metadata
				try (FileInputStream fis = new FileInputStream(metaPath.toFile())) {
					ZipEntry entry = new ZipEntry(METADATA_DIR + "/" + metaFileName);
					zos.putNextEntry(entry);
					length = 0;
					while ((length = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}
					zos.closeEntry();
				}
			}
		} finally {
			this.exportLock.unlockWrite(stamp);
		}
	}
	
	/**
	 * Removes the media from the library and deletes all generated files.
	 * @param media the media to remove
	 * @throws IOException if an IO error occurs
	 */
	public void remove(Media media) throws IOException {
		Path path = media.getPath();
		// get the export lock so an export doesn't occur
		// during remove
		long stamp = this.exportLock.readLock();
		try {
			// obtain the lock for this media item
			synchronized(this.getMediaLock(media)) {
				// sanity check, it's possible that while this thread
				// was waiting for the lock, that this media was renamed
				// the media map will contain the latest metadata for us 
				// to update
				media = this.media.get(media.getId());
				// make sure the media wasn't removed
				if (media != null) {
					// delete the files
					delete(path);
					// remove from the library's cache
					this.media.remove(path);
				}
			}
		} finally {
			this.exportLock.unlockRead(stamp);
		}
	}

	/**
	 * Renames the media to the given name and returns a new media
	 * object with the new name.
	 * @param media the media
	 * @param name the new name
	 * @return {@link Media}
	 * @throws FileAlreadyExistsException if a file already exists with the given name
	 * @throws JAXBException if the metadata fails to save
	 * @throws IOException if an IO error occurs
	 */
	public Media rename(Media media, String name) throws FileAlreadyExistsException, IOException, JAXBException {
		// get the export lock so an export doesn't occur
		// during rename
		long stamp = this.exportLock.readLock();
		try {
			// obtain the lock for this media item
			synchronized(this.getMediaLock(media)) {
				// sanity check, it's possible that while this thread
				// was waiting for the lock, that this media was deleted
				// or updated. the media map will contain the latest 
				// metadata for us to use
				Media latest = this.media.get(media.getId());
				// make sure the media wasn't removed
				if (latest != null) {
					return move(latest, name);
				}
				// throw an exception if it was deleted
				throw new FileNotFoundException(media.getFileName());
			}
		} finally {
			this.exportLock.unlockRead(stamp);
		}
	}
	
	/**
	 * Adds the given tag to the given media and saves the metadata.
	 * @param media the media
	 * @param tag the new tag
	 * @return boolean true if the tag was added successfully
	 * @throws JAXBException if the media metadata failed to save
	 * @throws IOException if an IO error occurs
	 */
	public boolean addTag(Media media, Tag tag) throws JAXBException, IOException {
		// obtain the lock for this media item
		synchronized(this.getMediaLock(media)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this media was deleted
			// or renamed. the media map will contain the latest 
			// metadata for us to update
			media = this.media.get(media.getId());
			// make sure the media wasn't removed
			if (media != null) {
				boolean added = media.tags.add(tag);
				if (added) {
					saveMetadata(media);
				}
				return added;
			}
			return false;			
		}
	}
	
	/**
	 * Adds the given tags to the given media and saves the metadata.
	 * @param media the media
	 * @param tags the new tags
	 * @return boolean true if the tags were added successfully
	 * @throws JAXBException if the media metadata failed to save
	 * @throws IOException if an IO error occurs
	 */	
	public boolean addTags(Media media, Collection<Tag> tags) throws JAXBException, IOException {
		// obtain the lock for this media item
		synchronized(this.getMediaLock(media)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this media was deleted
			// or renamed. the media map will contain the latest 
			// metadata for us to update
			media = this.media.get(media.getId());
			// make sure the media wasn't removed
			if (media != null) {
				boolean added = media.tags.addAll(tags);
				if (added) {
					saveMetadata(media);
				}
				return added;
			}
			return false;
		}
	}
	
	/**
	 * Sets the given tags on the given media and saves the metadata.
	 * @param media the media
	 * @param tags the new tags
	 * @return boolean true if the tags were set successfully
	 * @throws JAXBException if the media metadata failed to save
	 * @throws IOException if an IO error occurs
	 */	
	public boolean setTags(Media media, Collection<Tag> tags) throws JAXBException, IOException {
		// obtain the lock for this media item
		synchronized(this.getMediaLock(media)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this media was deleted
			// or renamed. the media map will contain the latest 
			// metadata for us to update
			media = this.media.get(media.getId());
			// make sure the media wasn't removed
			if (media != null) {
				boolean changed = media.tags.addAll(tags);
				changed |= media.tags.retainAll(tags);
				if (changed) {
					saveMetadata(media);
				}
				return changed;
			}
			return false;
		}
	}
	
	/**
	 * Removes the given tag from the given media and saves the metadata.
	 * @param media the media
	 * @param tag the tag to remove
	 * @return boolean true if the tag was removed successfully
	 * @throws JAXBException if the media metadata failed to save
	 * @throws IOException if an IO error occurs
	 */	
	public boolean removeTag(Media media, Tag tag) throws JAXBException, IOException {
		// obtain the lock for this media item
		synchronized(this.getMediaLock(media)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this media was deleted
			// or renamed. the media map will contain the latest 
			// metadata for us to update
			media = this.media.get(media.getId());
			// make sure the media wasn't removed
			if (media != null) {
				boolean removed = media.tags.remove(tag);
				if (removed) {
					saveMetadata(media);
				}
				return removed;
			}
			return false;
		}
	}
	
	/**
	 * Removes the given tags from the given media and saves the metadata.
	 * @param media the media
	 * @param tags the tags to remove
	 * @return boolean true if the tags were removed successfully
	 * @throws JAXBException if the media metadata failed to save
	 * @throws IOException if an IO error occurs
	 */	
	public boolean removeTags(Media media, Collection<Tag> tags) throws JAXBException, IOException {
		// obtain the lock for this media item
		synchronized(this.getMediaLock(media)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this media was deleted
			// or renamed. the media map will contain the latest 
			// metadata for us to update
			media = this.media.get(media.getId());
			// make sure the media wasn't removed
			if (media != null) {
				boolean removed = media.tags.removeAll(tags);
				if (removed) {
					saveMetadata(media);
				}
				return removed;
			}
			return false;
		}
	}

	/**
	 * Returns the thumbnail settings.
	 * @return {@link ThumbnailSettings}
	 */
	public ThumbnailSettings getThumbnailSettings() {
		return this.thumbnailSettings;
	}
}

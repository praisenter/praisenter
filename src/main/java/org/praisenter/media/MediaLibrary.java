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
import java.util.stream.Collectors;
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
import org.praisenter.MediaType;
import org.praisenter.Tag;
import org.praisenter.ThumbnailSettings;
import org.praisenter.json.JsonIO;
import org.praisenter.tools.ToolExecutionException;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.Streams;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * A collection of media that has been loaded into a specific location and converted
 * into supported formats with additional files generated for enhanced performance.
 * <p>
 * Obtain a {@link MediaLibrary} instance by calling the {@link #open(Path, MediaLibraryContext, MediaImportProcessor)}
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
	
	/** The directory to store media when exported with other data */
	private static final String ZIP_DIR = "media";

	// instance variables
	
	/** The root path to the media library */
	private final Path path;
	
	/** The full path to the metatdata */
	private final Path metadataPath;
	
	/** The context */
	private final MediaLibraryContext context;
	
	/** The import filter */
	private final MediaImportProcessor importFilter;
	
	// loaded
	
	/** The media loaders */
	private final MediaLoader[] loaders;
	
	/** The media */
	private final Map<UUID, Media> media;
	
	// locking
	
	/** The mutex locks */
	private final LockMap<String> locks;
	
	/** The export lock */
	private final StampedLock exportLock;
	
	/**
	 * Sets up a new {@link MediaLibrary} at the given path using the {@link DefaultMediaImportProcessor}
	 * with the given {@link ThumbnailSettings}.
	 * @param path the root path to the media library
	 * @param context the context
	 * @param processor the media import processor
	 * @return {@link MediaLibrary}
	 * @throws IOException if an IO error occurs
	 */
	public static final MediaLibrary open(Path path, MediaLibraryContext context, MediaImportProcessor processor) throws IOException {
		MediaLibrary library = new MediaLibrary(path, context, processor);
		library.initialize();
		return library;
	}
	
	/**
	 * Private constructor.
	 * @param path the path to initialize in
	 * @param importFilter the import filter
	 * @param settings the thumbnail settings
	 */
	private MediaLibrary(Path path, MediaLibraryContext context, MediaImportProcessor processor) {
		this.path = path;
		this.metadataPath = path.resolve(METADATA_DIR);
		this.context = context;
		this.importFilter = processor == null ? new DefaultMediaImportProcessor() : processor;
		
		this.loaders = new MediaLoader[] {
			new ImageMediaLoader(context),
			new VideoMediaLoader(context),
			new AudioMediaLoader(context)
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
		LOGGER.debug("Initializing media library at '{}'.", this.path);
		// make sure the paths exist
		Files.createDirectories(this.path);
		// for metadata and thumbnails
		Files.createDirectories(this.metadataPath);
		
		// load existing meta data into a temporary map for verification
		LOGGER.debug("Reading media metadata.");
		Map<Path, Media> metadata = new HashMap<Path, Media>();
		try (DirectoryStream<Path> dir = Files.newDirectoryStream(this.metadataPath)) {
			for (Path path : dir) {
				if (Files.isRegularFile(path)) {
					if (MimeType.JSON.check(path)) {
						try {
							Media media = JsonIO.read(path, Media.class);
							// when we read the metadata we need to update it with
							// the path to the media file since we don't store it
							media.path = this.path.resolve(media.fileName);
							
							// verify the media exists, this will perform clean up on left over
							// metadata if media gets deleted and the deletion of the metadata fails
							// that should be the only case where this would happen
							try {
								if (!Files.exists(media.path)) {
									try {
										LOGGER.warn("Media '{}' doesn't exist for metadata '{}'. Deleting metadata.", media.path, path);
										// then delete the metadata
										Files.deleteIfExists(path);
									} catch (Exception e2) {
										LOGGER.error("Failed to remove metadata '" + path + "' after detecting the media it refers to doesn't exist.", e2);
									}
									// and move onto the next item
									continue;
								}
							} catch (Exception e1) {
								LOGGER.error("Failed to verify if the media '" + media.path + "' exists.", e1);
								// if we fail to confirm existence we should just skip it
								continue;
							}
							
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
		
		LOGGER.debug("Scaning for media without metadata.");
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
							LOGGER.debug("Creating or updating media metadata for '{}'.", path.getFileName());
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
	
	/**
	 * Returns a lock for the given path.
	 * @param path the path
	 * @return Object
	 */
	private final Object getPathLock(Path path) {
		return this.locks.get(path.getFileName().toString());
	}
	
	/**
	 * Returns a lock for the given media.
	 * @param media the media
	 * @return Object
	 */
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
		return path.getFileName().toString() + Constants.MEDIA_METADATA_FILE_EXTENSION;
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
	 * @throws JsonMappingException if an error occurs mapping the object to JSON
	 * @throws JsonGenerationException if an error occurs generating the JSON
	 * @throws IOException if an IO error occurs
	 */
	private final void saveMetadata(Media media) throws JsonGenerationException, JsonMappingException, IOException {
		Path path = media.getPath();
		Path mPath = this.getMetadataPath(path);
		JsonIO.write(mPath, media);
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
			LOGGER.debug("Saving new metadata for '{}'.", newMedia.fileName);
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
			} catch (MediaImportException e) {
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
	 * @throws MediaImportException if the media failed to be transcoded into a supported format
	 * @throws IOException if an IO error occurs
	 */
	private final Path copy(Path source) throws FileAlreadyExistsException, FileNotFoundException, IOException, UnknownMediaTypeException, MediaImportException {
		Path target = null;
		// make sure it exists and is a file
		if (Files.exists(source) && Files.isRegularFile(source)) {
			try {
				// get the media type
				MediaType type = this.getMediaType(source);
				// check to make sure it's a media file
				if (type == null) {
					throw new UnknownMediaTypeException(source.toAbsolutePath().toString());
				}
				// get the target name based on the import filter
				target = this.importFilter.getTarget(this.path, source.getFileName().toString(), type);
				// make sure we obtain a lock on the target path
				synchronized (this.getPathLock(target)) {
					LOGGER.debug("Copying and filtering media '{}' to '{}'.", source, target);
					// its possible that while waiting for the target lock that another thread
					// creates a file with the same name, we need to check for existence again
					// to make sure its not there.
					if (Files.exists(target)) {
						// if it is there, this should be a rare case, just throw an exception and the user
						// can try again
						LOGGER.warn("Failed to copy media '{}' to '{}' because '{}' already exists.", source, target, target);
						throw new FileAlreadyExistsException(target.getFileName().toString());
					}
					// get the media file into the library
					this.importFilter.process(source, target, type);
				}
				// return the library path
				return target;
			} catch (Exception ex) {
				// if it's not a media type exception is something else
				// and we should log that
				if (!(ex instanceof UnknownMediaTypeException)) {
					LOGGER.warn("Failed to copy or filter the media '" + source + "'.", ex);
				}
				// clean up the target if it exists
				if (target != null) {
					try {
						Files.deleteIfExists(target);
					} catch (Exception ex1) {
						LOGGER.error("Failed to clean up file '" + target + "'.", ex1);
					}
				}
				// re-throw the exception
				throw ex;
			}
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
	private final boolean delete(Media media) throws IOException {
		// make sure the media isn't null
		if (media != null) {
			// get the media path
			Path path = media.getPath();
			// get the metadata path
			Path mPath = this.getMetadataPath(path);
			
			// delete the media
			LOGGER.debug("Deleting media '{}'.", path);
			Files.deleteIfExists(path);
			
			// if the media gets deleted successfully, then we need to
			// remove it from the library
			this.media.remove(media.getId());
			
			// delete the metadata
			try  {
				// deleting the metadata second is better since the media will
				// not be shown in the library if the metadata is missing
				LOGGER.debug("Deleting media metadata '{}'.", mPath);
				Files.deleteIfExists(mPath);
			} catch (Exception ex) {
				// we only want to log the message here and return back to the caller
				// that the media is actually gone (since the media itself is gone
				// even though the metadata isn't)
				LOGGER.warn("Failed to delete metadata '" + mPath + "'. This should be cleaned up upon the next initialization of the media library.", ex);
			}
			
			// it all worked
			return true;
		}
		
		// nothing to delete
		return false;
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
	private final Media move(Media media, String name) throws FileAlreadyExistsException, IOException {
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
			// NOTE: this will blow up if the target already exists, which is what we want
			// it's possible that while waiting for the target lock, that an add occurs with
			// the same file name
			LOGGER.debug("Moving (renaming) the media file from '{}' to '{}'", source, target);
			Files.move(source, target);
			
			// save the renamed metadata
			Media media1 = Media.forRenamed(target, media);
			try {
				LOGGER.debug("Saving the renamed metadata '{}'.", media1.getFileName());
				this.saveMetadata(media1);
			} catch (Exception ex) {
				LOGGER.error("Failed to save new metadata for '" + media1.name + "'. Attempting to undo rename operation.", ex);
				// try to move it back
				try {
					Files.move(target, source);
					LOGGER.info("Successfully recovered from failed rename operation.");
				} catch (Exception e1) {
					LOGGER.fatal("Failed to recover from failed rename operation. This should be fixed on the next initialization of the media library.");
				}
				throw ex;
			}

			// remove old metadata
			Path smPath = this.metadataPath.resolve(name0 + Constants.MEDIA_METADATA_FILE_EXTENSION);
			try {
				LOGGER.debug("Removing the old metadata '{}'.", smPath);
				Files.delete(smPath);
			} catch (Exception ex) {
				LOGGER.warn("Failed to delete old metadata '" + smPath + "'. This should be cleaned up upon the next initialization of the media library.", ex);
			}
			
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
	 * @throws ToolExecutionException if the media failed to be transcoded into a supported format
	 * @throws IOException if an IO error occurs
	 */
	public Media add(Path path) throws FileAlreadyExistsException, FileNotFoundException, IOException, ToolExecutionException, UnknownMediaTypeException, InvalidFormatException {
		LOGGER.debug("Adding media '{}'", path);
		// lock on the incoming file name
		synchronized(this.getPathLock(path)) {
			// copy it to the library
			// NOTE: don't synchronize this since it could take a long time
			LOGGER.debug("Copying media '{}' to media library", path);
			Path target = copy(path);
			// attempt to load it
			LOGGER.debug("Loading media '{}'", path);
			Media media = update(target, null);
			// return it
			return media;
		}
	}

	/**
	 * Attempts to import the media contained in a zip file at the given path.
	 * <p>
	 * The media imported this way is assumed to have underwent the filtering/transcoding process at some
	 * time. Therefore these operations are not performed in this method.
	 * <p>
	 * This should be a zip file generated by Praisenter. Any media files contained within that do not
	 * have related metadata will be skipped.
	 * @param path the path to the zip file
	 * @return List&lt;{@link Media}&gt;
	 * @throws FileNotFoundException if the given path isn't found
	 * @throws IOException if an IO error occurs
	 * @throws ZipException if an error occurs reading the zip file
	 */
	public List<Media> importMedia(Path path) throws FileNotFoundException, ZipException, IOException {
		byte[] buffer = new byte[1024];
		int length;
		
		// keep track of successfully imported media
		List<Media> imported = new ArrayList<Media>();
		
		// get the root folder by inspecting what's in the zip
		// if there's a media folder then look there only, otherwise
		// look in the root
		String root = "";
		try (FileInputStream fis = new FileInputStream(path.toFile());
			 ZipInputStream zis = new ZipInputStream(fis)) {
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				// does any entry start with the ZIP_DIR
				if (entry.getName().toLowerCase().startsWith(ZIP_DIR)) {
					root = ZIP_DIR + "/";
					break;
				}
			}
		}
		
		// for an import, we verify that it's a zip file
		// and then import whatever media has metadata as is 
		// (doesn't pass through the filter process)
		
		// this allows a user to do the transcoding, reading
		// thumbnail & frame generation on a different computer
		// and then transfer to the primary
		
		String metadataRoot = root + METADATA_DIR;
		
		// read all metadata in the zip first
		Map<String, Media> metadata = new HashMap<String, Media>();
		try (FileInputStream fis = new FileInputStream(path.toFile());
			 ZipInputStream zis = new ZipInputStream(fis)) {
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					String name = entry.getName();
					// verify that the media is in the correct folder
					if (name.toLowerCase().startsWith(metadataRoot)) {
						// check for (metadata) entries
						// this is the best we can do here since ZipInputStream doesn't
						// support mark/reset; looking at the file name is the best we can do
						if (MimeType.JSON.check(name)) {
							// its a metadata file, try to read and parse it
							try {
								byte[] data = Streams.read(zis);
								Media media = JsonIO.read(new ByteArrayInputStream(data), Media.class);
								metadata.put(root + media.fileName, media);
							} catch (Exception ex) {
								// it failed, might be some other type of json file
								LOGGER.warn("Failed to read '" + name + "' as media metadata.", ex);
							}
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
				// make sure it's not a directory and not a JSON file
				if (!entry.isDirectory() && !MimeType.JSON.check(entry.getName())) {
					String name = entry.getName();
					// verify that the media is in the correct folder
					if (name.toLowerCase().startsWith(root)) {
						// does it have associated metadata?
						Media media = metadata.get(name);
						if (media != null) {
							try {
								// get a lock for the source file name
								synchronized (this.locks.get(name)) {
									// get the target name based on the import filter
									Path target = this.importFilter.getTarget(this.path, media.getFileName(), media.type);
									// get a lock for the target file name
									synchronized (this.getPathLock(target)) {
										// make sure the target doesn't exist
										if (Files.exists(target)) {
											LOGGER.warn("Unable to import '{}' because the file '{}' already exists.", name, target);
											continue;
										}
										
										// copy the media to the library
										try (FileOutputStream fos = new FileOutputStream(target.toFile())) {
											length = 0;
											while ((length = zis.read(buffer)) > 0) {
												fos.write(buffer, 0, length);
											}
										}
										
										// update the media after being renamed
										media = Media.forRenamed(target, media);
										
										try {
											// write the metadata
											this.saveMetadata(media);
										} catch (Exception ex) {
											// attempt to delete the target file
											try {
												Files.deleteIfExists(target);
											} catch (Exception ex1) {
												LOGGER.warn("Unable to delete '{}' after failing to write metadata. This should be fixed upon the next initialization of the media library.", target);
											}
											throw ex;
										}
										
										// update the list of media
										this.media.put(media.getId(), media);
									}
								}
								imported.add(media);
							} catch (Exception ex) {
								// it failed to write either the media or the metadata
								LOGGER.error("Failed to write the media or metadata to the library for '" + name + "'.", ex);
							}
						} else {
							LOGGER.info("Media '{}' doesn't have related metadata so cannot be imported. Please unzip and import this file manually.", name);
						}
					}
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
		try (FileOutputStream fos = new FileOutputStream(path.toFile());
			 ZipOutputStream zos = new ZipOutputStream(fos)) {
			this.exportMedia(zos, null, media);
		}
	}
	
	/**
	 * Exports the given media to the given path.
	 * @param stream the zip output stream to write to
	 * @param media the media to export
	 * @throws FileNotFoundException if the given path is not a file or cannot be created
	 * @throws ZipException if an error occurs while building the zip file
	 * @throws IOException if an IO error occurs
	 */
	public void exportMedia(ZipOutputStream stream, List<Media> media) throws FileNotFoundException, ZipException, IOException {
		this.exportMedia(stream, MediaLibrary.ZIP_DIR, media);
	}
	
	/**
	 * Exports the given media to the given path.
	 * @param stream the zip output stream to write to
	 * @param folder the folder in the zip to place the media
	 * @param media the media to export
	 * @throws FileNotFoundException if the given path is not a file or cannot be created
	 * @throws ZipException if an error occurs while building the zip file
	 * @throws IOException if an IO error occurs
	 */
	private void exportMedia(ZipOutputStream stream, String folder, List<Media> media) throws FileNotFoundException, ZipException, IOException {
		long stamp = this.exportLock.writeLock();
		
		byte[] buffer = new byte[1024];
		int length;
		
		try {
			for (Media m : media) {
				Path mediaPath = m.getPath();
				Path metaPath = this.getMetadataPath(mediaPath);
				String mediafileName = mediaPath.getFileName().toString();
				String metaFileName = this.getMetadataFileName(mediaPath);
				
				String root = "";
				if (folder != null) {
					root = folder + "/";
				}
				
				// the media
				try (FileInputStream fis = new FileInputStream(mediaPath.toFile())) {
					ZipEntry entry = new ZipEntry(root + mediafileName);
					stream.putNextEntry(entry);
					length = 0;
					while ((length = fis.read(buffer)) > 0) {
						stream.write(buffer, 0, length);
					}
					stream.closeEntry();
				}
				
				// the metadata
				try (FileInputStream fis = new FileInputStream(metaPath.toFile())) {
					ZipEntry entry = new ZipEntry(root + METADATA_DIR + "/" + metaFileName);
					stream.putNextEntry(entry);
					length = 0;
					while ((length = fis.read(buffer)) > 0) {
						stream.write(buffer, 0, length);
					}
					stream.closeEntry();
				}
			}
			LOGGER.debug("Export completed successfully.");
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
		// get the export lock so an export doesn't occur
		// during remove
		long stamp = this.exportLock.readLock();
		try {
			// obtain the lock for this media item
			synchronized(this.getMediaLock(media)) {
				LOGGER.debug("Removing media '{}'.", media.getName());
				// sanity check, it's possible that while this thread
				// was waiting for the lock, that this media was renamed.
				// the media map will contain the latest metadata for us 
				// to update
				Media latest = this.media.get(media.getId());
				// attempt to delete it
				delete(latest);
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
	 * @throws IOException if an IO error occurs
	 */
	public Media rename(Media media, String name) throws FileAlreadyExistsException, IOException {
		// get the export lock so an export doesn't occur
		// during rename
		long stamp = this.exportLock.readLock();
		try {
			// obtain the lock for this media item
			synchronized(this.getMediaLock(media)) {
				LOGGER.debug("Renaming media '{}' to '{}'.", media.getName(), name);
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
	 * @throws IOException if an IO error occurs
	 */
	public boolean addTag(Media media, Tag tag) throws IOException {
		// obtain the lock for this media item
		synchronized(this.getMediaLock(media)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this media was deleted
			// or renamed. the media map will contain the latest 
			// metadata for us to update
			Media latest = this.media.get(media.getId());
			// make sure the media wasn't removed
			if (latest != null) {
				LOGGER.debug("Adding tag '{}' to media '{}'.", tag, media.getName());
				// see if adding the tag really does add it...
				boolean added = latest.tags.add(tag);
				if (added) {
					try {
						saveMetadata(latest);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save metadata after adding tag '{}' to media '{}'.", tag, media.getName());
						// remove the tag due to not being able to save
						latest.tags.remove(tag);
						// rethrow the exception
						throw ex;
					}
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
	 * @throws IOException if an IO error occurs
	 */	
	public boolean addTags(Media media, Collection<Tag> tags) throws IOException {
		// obtain the lock for this media item
		synchronized(this.getMediaLock(media)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this media was deleted
			// or renamed. the media map will contain the latest 
			// metadata for us to update
			Media latest = this.media.get(media.getId());
			// make sure the media wasn't removed
			if (latest != null) {
				String ts = tags.stream().map(t -> t.getName()).collect(Collectors.joining(", "));
				LOGGER.debug("Adding tags '{}' to media '{}'.", ts, media.getName());
				// keep the old set just in case the new set fails to save
				TreeSet<Tag> old = new TreeSet<Tag>(latest.tags);
				// attempt to add all of the tags
				boolean added = latest.tags.addAll(tags);
				if (added) {
					try {
						// attempt to save
						saveMetadata(latest);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save metadata after adding tags '{}' to media '{}'.", ts, media.getName());
						// reset to initial state
						latest.tags.retainAll(old);
						// rethrow the exception
						throw ex;
					}
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
	 * @throws IOException if an IO error occurs
	 */	
	public boolean setTags(Media media, Collection<Tag> tags) throws IOException {
		// obtain the lock for this media item
		synchronized(this.getMediaLock(media)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this media was deleted
			// or renamed. the media map will contain the latest 
			// metadata for us to update
			Media latest = this.media.get(media.getId());
			// make sure the media wasn't removed
			if (latest != null) {
				String ts = tags.stream().map(t -> t.getName()).collect(Collectors.joining(", "));
				LOGGER.debug("Setting tags '{}' on media '{}'.", ts, media.getName());
				// keep the old set just in case the new set fails to save
				TreeSet<Tag> old = new TreeSet<Tag>(latest.tags);
				// attempt to set the tags
				boolean changed = latest.tags.addAll(tags);
				changed |= latest.tags.retainAll(tags);
				if (changed) {
					try {
						// attempt to save
						saveMetadata(latest);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save metadata after setting tags '{}' on media '{}'.", ts, media.getName());
						// reset to initial state
						latest.tags.clear();
						latest.tags.addAll(old);
						// rethrow the exception
						throw ex;
					}
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
	 * @throws IOException if an IO error occurs
	 */	
	public boolean removeTag(Media media, Tag tag) throws IOException {
		// obtain the lock for this media item
		synchronized(this.getMediaLock(media)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this media was deleted
			// or renamed. the media map will contain the latest 
			// metadata for us to update
			Media latest = this.media.get(media.getId());
			// make sure the media wasn't removed
			if (latest != null) {
				LOGGER.debug("Removing tag '{}' from media '{}'.", tag, media.getName());
				boolean removed = latest.tags.remove(tag);
				if (removed) {
					try {
						saveMetadata(latest);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save metadata after removing tag '{}' from media '{}'.", tag, media.getName());
						// reset to initial state
						latest.tags.add(tag);
						// rethrow the exception
						throw ex;
					}
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
	 * @throws IOException if an IO error occurs
	 */	
	public boolean removeTags(Media media, Collection<Tag> tags) throws IOException {
		// obtain the lock for this media item
		synchronized(this.getMediaLock(media)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this media was deleted
			// or renamed. the media map will contain the latest 
			// metadata for us to update
			Media latest = this.media.get(media.getId());
			// make sure the media wasn't removed
			if (latest != null) {
				String ts = tags.stream().map(t -> t.getName()).collect(Collectors.joining(", "));
				LOGGER.debug("Removing tags '{}' from media '{}'.", ts, media.getName());
				// keep the old set just in case the new set fails to save
				TreeSet<Tag> old = new TreeSet<Tag>(latest.tags);
				// attempt to set the tags
				boolean removed = latest.tags.removeAll(tags);
				if (removed) {
					try {
						saveMetadata(latest);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save metadata after removing tags '{}' from media '{}'.", ts, media.getName());
						// reset to initial state
						latest.tags.clear();
						latest.tags.addAll(old);
						// rethrow the exception
						throw ex;
					}
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
		return this.context.getThumbnailSettings();
	}
}

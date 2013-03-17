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
package org.praisenter.slide;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.praisenter.common.NotInitializedException;
import org.praisenter.common.utilities.FileUtilities;
import org.praisenter.common.xml.XmlIO;
import org.praisenter.media.AbstractAudioMedia;
import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.media.ImageMedia;
import org.praisenter.media.Media;
import org.praisenter.media.MediaException;
import org.praisenter.media.MediaFile;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaType;
import org.praisenter.media.NoMediaLoaderException;
import org.praisenter.slide.media.AudioMediaComponent;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.MediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.slide.resources.Messages;

/**
 * Static interface for loading and saving slides and templates.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public final class SlideLibrary {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlideLibrary.class);
	
	// relative paths
	
	/** The slide library relative slide path */
	private static final String SLIDE_PATH = "slides";
	
	/** The slide library relative template path */
	private static final String TEMPLATE_PATH = SLIDE_PATH + FileUtilities.getSeparator() + "templates";
	
	/** The slide library relative bible template path */
	private static final String BIBLE_TEMPLATE_PATH = TEMPLATE_PATH + FileUtilities.getSeparator() + "bible";
	
	/** The slide library relative song template path */
	private static final String SONGS_TEMPLATE_PATH = TEMPLATE_PATH + FileUtilities.getSeparator() + "songs";
	
	/** The slide library relative notification template path */
	private static final String NOTIFICATIONS_TEMPLATE_PATH = TEMPLATE_PATH + FileUtilities.getSeparator() + "notifications";

	// thumbnails
	
	/** The thumbnail size */
	public static final Dimension THUMBNAIL_SIZE = new Dimension(64, 48);
	
	/** The thumbnail file name */
	private static final String THUMBS_FILE = FileUtilities.getSeparator() + "_thumbs.xml";
	
	/** The instances */
	
	// members
	
	/** The slide library base path */
	private String basePath;
	
	/** The full path to the slides directory */
	private final String slidePath;
	
	/** The full path to the templates directory */
	private final String templatePath;
	
	/** The full path to the bible templates directory */
	private final String bibleTemplatePath;
	
	/** The full path to the song templates directory */
	private final String songsTemplatePath;
	
	/** The full path to the notification templates directory */
	private final String notificationsTemplatePath;
	
	/** The saved slides */
	private Map<String, BasicSlide> slides;
	
	/** The saved templates */
	private Map<String, BasicSlideTemplate> templates;
	
	/** The saved bible templates */
	private Map<String, BibleSlideTemplate> bibleTemplates;
	
	/** The saved song templates */
	private Map<String, SongSlideTemplate> songTemplates;

	/** The saved notification templates */
	private Map<String, NotificationSlideTemplate> notificationTemplates;

	/** The list of all thumbnails */
	private Map<String, SlideThumbnail> thumbnails;

	// static interface
	
	/** The singleton instance */
	private static SlideLibrary instance;

	/**
	 * Initializes the singleton {@link SlideLibrary} instance using the given base path.
	 * @param basePath the base path
	 */
	public static final synchronized void initialize(String basePath) {
		if (basePath == null) {
			basePath = "";
		}
		// create a new library
		SlideLibrary library = new SlideLibrary(basePath);
		// set the instance
		instance = library;
	}
	
	/**
	 * Returns the singleton instance of the slide library.
	 * @return {@link SlideLibrary}
	 * @throws NotInitializedException thrown if {@link #initialize(String)} was not called before this method
	 */
	public static final synchronized SlideLibrary getInstance() throws NotInitializedException {
		if (instance == null) {
			throw new NotInitializedException();
		}
		return instance;
	}

	// members
	
	/**
	 * Minimal constructor.
	 * @param basePath the base path
	 */
	private SlideLibrary(String basePath) {
		this.basePath = basePath;
		
		// setup pathing
		String sep = FileUtilities.getSeparator();
		this.slidePath = basePath + sep + SLIDE_PATH;
		this.templatePath = basePath + sep + TEMPLATE_PATH;
		this.bibleTemplatePath = basePath + sep + BIBLE_TEMPLATE_PATH;
		this.songsTemplatePath = basePath + sep + SONGS_TEMPLATE_PATH;
		this.notificationsTemplatePath = basePath + sep + NOTIFICATIONS_TEMPLATE_PATH;
		
		this.slides = new HashMap<String, BasicSlide>();
		this.templates = new HashMap<String, BasicSlideTemplate>();
		this.bibleTemplates = new HashMap<String, BibleSlideTemplate>();
		this.songTemplates = new HashMap<String, SongSlideTemplate>();
		this.notificationTemplates = new HashMap<String, NotificationSlideTemplate>();
		
		this.thumbnails = new HashMap<String, SlideThumbnail>();
		
		// initialize the slide library at the given base path
		FileUtilities.createFolder(this.slidePath);
		FileUtilities.createFolder(this.templatePath);
		FileUtilities.createFolder(this.bibleTemplatePath);
		FileUtilities.createFolder(this.songsTemplatePath);
		FileUtilities.createFolder(this.notificationsTemplatePath);
		
		// preload all the slide/templates and thumbnails
		this.loadSlideLibrary(this.slidePath, BasicSlide.class, this.slides);
		this.loadSlideLibrary(this.templatePath, BasicSlideTemplate.class, this.templates);
		this.loadSlideLibrary(this.bibleTemplatePath, BibleSlideTemplate.class, this.bibleTemplates);
		this.loadSlideLibrary(this.songsTemplatePath, SongSlideTemplate.class, this.songTemplates);
		this.loadSlideLibrary(this.notificationsTemplatePath, NotificationSlideTemplate.class, this.notificationTemplates);
	}
	
	/**
	 * Loads the thumbnails file from the given path.
	 * <p>
	 * If the file does not exist or is out of sync, it is generated and saved.
	 * @param path the path for the thumbnail file
	 * @param clazz the type to load
	 * @param map the map to add the loaded slide/template
	 */
	private <E extends Slide> void loadSlideLibrary(String path, Class<E> clazz, Map<String, E> map) {
		// attempt to read the thumbs file in the respective folder
		List<SlideThumbnail> thumbnailsFromFile = null;
		try {
			SlideThumbnails sts = XmlIO.read(path + THUMBS_FILE, SlideThumbnails.class);
			if (sts != null) {
				thumbnailsFromFile = sts.getThumbnails();
			}
		} catch (FileNotFoundException e) {
			// just eat this one
		} catch (Exception e) {
			// silently ignore this error
			LOGGER.error("Could not read [" + path + THUMBS_FILE + "]: ", e);
		}
		if (thumbnailsFromFile == null) {
			thumbnailsFromFile = new ArrayList<SlideThumbnail>();
		}
		
		// create a new list to store the thumbnails
		List<SlideThumbnail> thumbnails = new ArrayList<SlideThumbnail>();
		// track whether we need to resave the thumbnail XML
		boolean save = false;
		
		// read the slide library file names
		File[] files = new File(path).listFiles();
		if (files != null) {
			for (File file : files) {
				// skip directories
				if (file.isDirectory()) continue;
				// ignore hidden files
				if (file.isHidden()) continue;
				// get the file path
				String filePath = file.getPath();
				// skip the thumbnail file
				if (filePath.contains(THUMBS_FILE)) continue;
				// skip any file that doesn't end in .xml
				if (!filePath.toLowerCase().endsWith(".xml")) continue;
				// make sure there exists a thumnail for the file
				SlideThumbnail thumbnail = null;
				for (SlideThumbnail thumb : thumbnailsFromFile) {
					if (thumb.getFile().getName().equals(file.getName())) {
						// set the thumbnail
						thumbnail = thumb;
						// add it to the thumbnails array
						thumbnails.add(thumb);
						// we can break from the loop
						break;
					}
				}
				// always load up the slide
				try {
					E slide = loadFromSlideLibrary(filePath, clazz);
					// check if we need to generate a thumbnail for the file
					if (thumbnail == null) {
						// generate a thumbnail for the slide
						BufferedImage image = slide.getThumbnail(THUMBNAIL_SIZE);
						thumbnail = new SlideThumbnail(
								new SlideFile(this.basePath, filePath), 
								slide.getName(), 
								image);
						// add the thumbnail to the list
						thumbnails.add(thumbnail);
						// flag that we need to save the thumbnails file
						save = true;
					}
					// add the slide to the slide library (using relative path)
					map.put(thumbnail.getFile().getRelativePath(), slide);
				} catch (SlideLibraryException e) {
					LOGGER.error("Unable to load slide/template [" + filePath + "|" + clazz.getName() + "]: ", e);
				}
			}
			// add all the thumbnails
			for (SlideThumbnail thumbnail : thumbnails) {
				this.thumbnails.put(thumbnail.getFile().getRelativePath(), thumbnail);
			}
			// after we have read all the files we need to save the new thumbs xml
			if (save || thumbnailsFromFile.size() != thumbnails.size()) {
				saveThumbnailsFile(clazz);
			}
		}
	}
	
	/**
	 * Loads the given slide from the slide library.
	 * @param fullPath the full file name and path
	 * @param clazz the type to load
	 * @return {@link Slide}
	 * @throws SlideLibraryException thrown if the slide failed to be loaded
	 */
	private <E extends Slide> E loadFromSlideLibrary(String fullPath, Class<E> clazz) throws SlideLibraryException {
		try {
			return XmlIO.read(fullPath, clazz);
		} catch (JAXBException | IOException e) {
			throw new SlideLibraryException(MessageFormat.format("Could not load slide [{0}]", fullPath), e);
		}
	}

	/**
	 * Writes the thumbnails file for the given class.
	 * @param clazz the class type
	 */
	private void saveThumbnailsFile(Class<?> clazz) {
		// get the path and thumbnails for the given class type
		String path = getPath(clazz);
		List<SlideThumbnail> thumbnails = getThumbnails(clazz);
		
		try {
			XmlIO.save(path + THUMBS_FILE, new SlideThumbnails(thumbnails));
			LOGGER.debug("File [" + path + THUMBS_FILE + "] updated.");
		} catch (JAXBException | IOException e) {
			// silently log this error
			LOGGER.error("Failed to re-save [" + path + THUMBS_FILE + "]: ", e);
		}
	}

	// public interface

	/**
	 * Returns the file system path for the given class.
	 * @param clazz the class
	 * @return String
	 */
	public String getPath(Class<?> clazz) {
		String path = this.slidePath;
		
		// see if the type is a template
		if (Template.class.isAssignableFrom(clazz)) {
			// it could be a bible, song, or notification template
			if (BibleSlide.class.isAssignableFrom(clazz)) {
				path = this.bibleTemplatePath;
			} else if (NotificationSlide.class.isAssignableFrom(clazz)) {
				path = this.notificationsTemplatePath;
			} else if (SongSlide.class.isAssignableFrom(clazz)) {
				path = this.songsTemplatePath;
			} else {
				// generic template
				path = this.templatePath;
			}
		}
		
		return path;
	}
	
	// thumbnails
	
	/**
	 * Returns the thumbnails for the given class.
	 * @param clazz the class
	 * @return List&lt;{@link SlideThumbnail}&gt;
	 */
	public synchronized List<SlideThumbnail> getThumbnails(Class<?> clazz) {
		// we can use the slides map to get all the file path/names and use
		// those to look up all the thumbnails in that directory
		Set<String> paths = this.slides.keySet();
		// see if the type is a template
		if (Template.class.isAssignableFrom(clazz)) {
			// it could be a bible, song, or notification template
			if (BibleSlide.class.isAssignableFrom(clazz)) {
				paths = this.bibleTemplates.keySet();
			} else if (NotificationSlide.class.isAssignableFrom(clazz)) {
				paths = this.notificationTemplates.keySet();
			} else if (SongSlide.class.isAssignableFrom(clazz)) {
				paths = this.songTemplates.keySet();
			} else {
				// generic template
				paths = this.templates.keySet();
			}
		}
		// get the thumbnails
		List<SlideThumbnail> thumbnails = new ArrayList<SlideThumbnail>();
		for (String filePath : paths) {
			thumbnails.add(this.thumbnails.get(filePath));
		}
		// sort them
		Collections.sort(thumbnails);
		return thumbnails;
	}
	
	// slide library
	
	/**
	 * Returns true if the given slide exists in the slide library.
	 * @param fileName the file name (no path)
	 * @param clazz the slide type
	 * @return boolean
	 */
	public synchronized <E extends Slide> boolean containsSlide(String fileName, Class<E> clazz) {
		FileSystem system = FileSystems.getDefault();
		Path full = system.getPath(getPath(clazz) + FileUtilities.getSeparator() + fileName);
		Path root = system.getPath(this.basePath);
		String path = root.relativize(full).toString();
		// see if the type is a template
		if (Template.class.isAssignableFrom(clazz)) {
			// it could be a bible, song, or notification template
			if (BibleSlide.class.isAssignableFrom(clazz)) {
				return this.bibleTemplates.containsKey(path);
			} else if (NotificationSlide.class.isAssignableFrom(clazz)) {
				return this.notificationTemplates.containsKey(path);
			} else if (SongSlide.class.isAssignableFrom(clazz)) {
				return this.songTemplates.containsKey(path);
			} else {
				// generic template
				return this.templates.containsKey(path);
			}
		} else {
			return this.slides.containsKey(path);
		}
	}
	
	/**
	 * Returns the saved slide.
	 * @param slideFile the slide file
	 * @return {@link Slide}
	 * @throws SlideLibraryException thrown if an exception occurs while loading the slide
	 */
	public synchronized BasicSlide getSlide(SlideFile slideFile) throws SlideLibraryException {
		String relativePath = slideFile.getRelativePath();
		return this.getSlide(relativePath);
	}
	
	/**
	 * Returns the saved slide.
	 * @param relativePath the relative file name and path
	 * @return {@link Slide}
	 * @throws SlideLibraryException thrown if an exception occurs while loading the slide
	 */
	public synchronized BasicSlide getSlide(String relativePath) throws SlideLibraryException {
		try {
			BasicSlide slide = this.slides.get(relativePath);
			// see if the slide was cached
			if (slide == null) {
				String fullPath = this.basePath + FileUtilities.getSeparator() + relativePath;
				LOGGER.info("Slide [" + fullPath + "] not present in the slide library. Loading from file system.");
				slide = XmlIO.read(fullPath, BasicSlide.class);
				// cache it
				this.slides.put(relativePath, slide);
				// cache the thumbnail
				SlideThumbnail thumbnail = new SlideThumbnail(
						new SlideFile(this.basePath, fullPath), 
						slide.getName(),
						slide.getThumbnail(THUMBNAIL_SIZE));
				this.thumbnails.put(relativePath, thumbnail);
			}
			// return the slide
			return slide;
		} catch (JAXBException | IOException e) {
			throw new SlideLibraryException(e);
		}
	}
	
	/**
	 * Adds the given slide to the slide library.
	 * <p>
	 * Returns an new {@link SlideThumbnail} for the given slide.
	 * @param fileName the desired file name (no path)
	 * @param slide the slide
	 * @return {@link SlideThumbnail}
	 * @throws SlideLibraryException thrown if an error occurs saving the slide
	 */
	public synchronized SlideThumbnail addSlide(String fileName, BasicSlide slide) throws SlideLibraryException {
		if (!fileName.toLowerCase().matches("^.+\\.xml$")) {
			// append the .xml on the end
			fileName += ".xml";
		}
		String fullPath = this.slidePath + FileUtilities.getSeparator() + fileName;
		return this.saveSlideByPath(fullPath, slide);
	}
	
	/**
	 * Saves an already existing slide.
	 * <p>
	 * Returns an new {@link SlideThumbnail} for the given slide.
	 * @param file the file
	 * @param slide the slide
	 * @return {@link SlideThumbnail}
	 * @throws SlideLibraryException thrown if an error occurs saving the slide
	 */
	public synchronized SlideThumbnail saveSlide(SlideFile file, BasicSlide slide) throws SlideLibraryException {
		String fullPath = file.getFullPath();
		return this.saveSlideByPath(fullPath, slide);
	}
	
	/**
	 * Saves the given slide to the given path and updates the slide library.
	 * <p>
	 * Returns an new {@link SlideThumbnail} for the given slide.
	 * @param fullPath the full path and file name of the slide
	 * @param slide the slide
	 * @return {@link SlideThumbnail}
	 * @throws SlideLibraryException thrown if an error occurs saving the slide
	 */
	private SlideThumbnail saveSlideByPath(String fullPath, BasicSlide slide) throws SlideLibraryException {
		try {
			// save the slide to disc
			XmlIO.save(fullPath, slide);
			// update the thumbnail
			SlideThumbnail thumbnail = new SlideThumbnail(
					new SlideFile(this.basePath, fullPath), 
					slide.getName(),
					slide.getThumbnail(THUMBNAIL_SIZE));
			String relativePath = thumbnail.getFile().getRelativePath();
			// update the slide in the slide library
			this.slides.put(relativePath, slide);
			// update the thumbnail in the list of thumbnails
			this.thumbnails.put(relativePath, thumbnail);
			// update the thumbnail file for this directory
			saveThumbnailsFile(Slide.class);
			
			return thumbnail;
		} catch (JAXBException e) {
			throw new SlideLibraryException(e);
		} catch (IOException e) {
			throw new SlideLibraryException(e);
		}
	}
	
	/**
	 * Removes the given slide.
	 * @param slideFile the slide file
	 * @return boolean
	 */
	public synchronized boolean deleteSlide(SlideFile slideFile) {
		String relativePath = slideFile.getRelativePath();
		File file = new File(slideFile.getFullPath());
		// delete the file
		if (file.delete()) {
			// remove the references
			this.slides.remove(relativePath);
			// remove the thumbnail
			this.thumbnails.remove(relativePath);
			return true;
		}
		return false;
	}
	
	// template library
	
	/**
	 * Returns the saved template.
	 * @param slideFile the slide file
	 * @param clazz the type of the template
	 * @return {@link Slide}
	 * @throws SlideLibraryException thrown if an exception occurs while loading the template
	 */
	public synchronized <E extends Template> E getTemplate(SlideFile slideFile, Class<E> clazz) throws SlideLibraryException {
		String relativePath = slideFile.getRelativePath();
		return this.getTemplate(relativePath, clazz);
	}
	
	/**
	 * Returns the saved template.
	 * <p>
	 * If possible use the {@link #getTemplate(SlideFile, Class)} method instead.
	 * @param relativePath the relative file name and path
	 * @param clazz the type of the template
	 * @return {@link Slide}
	 * @throws SlideLibraryException thrown if an exception occurs while loading the template
	 */
	@SuppressWarnings("unchecked")
	public synchronized <E extends Template> E getTemplate(String relativePath, Class<E> clazz) throws SlideLibraryException {
		try {
			Map<String, E> map = null;
			// updated the template in the slide library
			if (BibleSlideTemplate.class.isAssignableFrom(clazz)) {
				map = (Map<String, E>)this.bibleTemplates;
			} else if (NotificationSlideTemplate.class.isAssignableFrom(clazz)) {
				map = (Map<String, E>)this.notificationTemplates;
			} else if (SongSlideTemplate.class.isAssignableFrom(clazz)) {
				map = (Map<String, E>)this.songTemplates;
			} else {
				map = (Map<String, E>)this.templates;
			}
			
			E slide = map.get(relativePath);
			// see if the slide was cached
			if (slide == null) {
				String fullPath = this.basePath + FileUtilities.getSeparator() + relativePath;
				LOGGER.info("Template [" + fullPath + "] not present in the slide library. Loading from file system.");
				slide = XmlIO.read(fullPath, clazz);
				// cache it
				map.put(relativePath, slide);
				// cache the thumbnail
				SlideThumbnail thumbnail = new SlideThumbnail(
						new SlideFile(this.basePath, fullPath), 
						slide.getName(),
						slide.getThumbnail(THUMBNAIL_SIZE));
				this.thumbnails.put(relativePath, thumbnail);
			}
			// return the slide
			return slide;
		} catch (JAXBException | IOException e) {
			throw new SlideLibraryException(e);
		}
	}
	
	/**
	 * Adds the given template to the slide library.
	 * <p>
	 * Returns an new {@link SlideThumbnail} for the given template.
	 * @param fileName the file name (no path)
	 * @param template the template
	 * @return {@link SlideThumbnail}
	 * @throws SlideLibraryException thrown if an exception occurs while saving the template
	 */
	public synchronized <E extends Template> SlideThumbnail addTemplate(String fileName, E template) throws SlideLibraryException {
		String path = this.getPath(template.getClass());
		if (!fileName.toLowerCase().matches("^.+\\.xml$")) {
			// append the .xml on the end
			fileName += ".xml";
		}
		String fullPath = path + FileUtilities.getSeparator() + fileName;
		return this.saveTemplateByPath(fullPath, template);
	}
	
	/**
	 * Saves an already existing template.
	 * <p>
	 * Returns an new {@link SlideThumbnail} for the given template.
	 * @param file the template file
	 * @param template the template
	 * @return {@link SlideThumbnail}
	 * @throws SlideLibraryException thrown if an exception occurs while saving the template
	 */
	public synchronized <E extends Template> SlideThumbnail saveTemplate(SlideFile file, E template) throws SlideLibraryException {
		String fullPath = file.getFullPath();
		return this.saveTemplateByPath(fullPath, template);
	}
	
	/**
	 * Saves the given template to the slide library.
	 * <p>
	 * Returns an new {@link SlideThumbnail} for the given template.
	 * @param fullPath the full file name and path
	 * @param template the template
	 * @return {@link SlideThumbnail}
	 * @throws SlideLibraryException thrown if an exception occurs while saving the template
	 */
	private synchronized <E extends Template> SlideThumbnail saveTemplateByPath(String fullPath, E template) throws SlideLibraryException {
		try {
			// save the template to disc
			XmlIO.save(fullPath, template);
			// update the thumbnail
			SlideThumbnail thumbnail = new SlideThumbnail(
					new SlideFile(this.basePath, fullPath), 
					template.getName(),
					template.getThumbnail(THUMBNAIL_SIZE));
			String relativePath = thumbnail.getFile().getRelativePath();
			// update the template in the slide library
			if (template instanceof BibleSlideTemplate) {
				this.bibleTemplates.put(relativePath, (BibleSlideTemplate)template);
			} else if (template instanceof NotificationSlideTemplate) {
				this.notificationTemplates.put(relativePath, (NotificationSlideTemplate)template);
			} else if (template instanceof SongSlideTemplate) {
				this.songTemplates.put(relativePath, (SongSlideTemplate)template);
			} else {
				this.templates.put(relativePath, (BasicSlideTemplate)template);
			}
			// updated the thumbnail in the list of thumbnails
			this.thumbnails.put(relativePath, thumbnail);
			// update the thumbnail file for this directory
			saveThumbnailsFile(template.getClass());
			
			return thumbnail;
		} catch (JAXBException e) {
			throw new SlideLibraryException(e);
		} catch (IOException e) {
			throw new SlideLibraryException(e);
		}
	}

	/**
	 * Removes the given template.
	 * @param slideFile the slide file
	 * @return boolean
	 */
	public synchronized boolean deleteTemplate(SlideFile slideFile) {
		String relativePath = slideFile.getRelativePath();
		File file = new File(slideFile.getFullPath());
		// delete the file
		if (file.delete()) {
			// remove the weak reference
			// remove it from all template maps (since its a no-op if its not there)
			this.templates.remove(relativePath);
			this.bibleTemplates.remove(relativePath);
			this.songTemplates.remove(relativePath);
			this.notificationTemplates.remove(relativePath);
			// remove the thumbnail
			this.templates.remove(relativePath);
			return true;
		}
		return false;
	}
	
	// import export

	/**
	 * Returns a unique file name for the given file name.
	 * <p>
	 * This will append the date and time to the file name.
	 * @param fileName the file name
	 * @return String
	 */
	private static final String getUniqueFileName(String fileName) {
		// find the last period in the file name
		int i = fileName.lastIndexOf(".");
		String name = "";
		// if the index is not 0 (for example 0 = ".xml" or -1 = "test")
		if (i > 0) {
			// then get the name of the file
			name = fileName.substring(0, i < 0 ? fileName.length() : i);
		}
		String ext = "";
		// if the index was zero or higher
		if (i >= 0) {
			// get the rest of the string
			ext = fileName.substring(i, fileName.length());
		}
		// put them together using the name collision format
		return MessageFormat.format(Messages.getString("fileNameCollision.format"), name, new Date(), ext);
	}
	
	/**
	 * Reads the given file path and writes it to the given zip output stream using the
	 * given file name.
	 * <p>
	 * The file name can include the path inside the zip.
	 * @param fullPath the full file name and path of the file to add to the zip
	 * @param zipPath the file name and path of the file inside the zip
	 * @param zos the zip input stream
	 * @throws FileNotFoundException thrown if the given file path does not exist
	 * @throws ZipException thrown if an error occurs while writing the file to the zip output stream
	 * @throws IOException thrown if an error occurs while reading or writing the file
	 */
	private void addFileToZip(String fullPath, String zipPath, ZipOutputStream zos) throws FileNotFoundException, ZipException, IOException {
		// add the slide xml file
		File sf = new File(fullPath);
		try (FileInputStream fis = new FileInputStream(sf)) {
			ZipEntry entry = new ZipEntry(zipPath);
			zos.putNextEntry(entry);
			// write the bytes of the file to the entry
			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}
			zos.closeEntry();
		}
	}
	
	/**
	 * Exports the given slides to the given file name.
	 * @param fileName the target zip file name and path
	 * @param exports the array of slides to export
	 * @throws SlideLibraryException thrown if an exception occurs while loading the slide/template
	 * @throws JAXBException thrown if an error occurs while writing the types file
	 * @throws IOException thrown if an error occurs while generating the export file
	 */
	public synchronized void exportSlides(String fileName, SlideExport... exports) throws SlideLibraryException, JAXBException, IOException {
		// make sure the file ends with .zip
		if (!fileName.toLowerCase().matches("^.+\\.zip$")) {
			// append the .zip on the end
			fileName += ".zip";
		}
		
		// we must keep track of duplicated media items so that they
		// are added only one time
		List<String> paths = new ArrayList<String>();
		try (FileOutputStream fos = new FileOutputStream(new File(fileName));
			 ZipOutputStream zos = new ZipOutputStream(fos);) {
			
			ExportManifest manifest = new ExportManifest();
			int i = 1;
			for (SlideExport export : exports) {
				// get the slide
				SlideFile slideFile = export.getSlideFile();
				String name = slideFile.getName();
				
				Slide slide = null;
				if (export.isTemplate()) {
					slide = this.getTemplate(slideFile, export.getTemplateType());
				} else {
					slide = this.getSlide(slideFile);
				}
				
				// add the slide xml file
				String inExportFileName = "SlideTemplate" + i + ".xml";
				addFileToZip(slideFile.getFullPath(), inExportFileName, zos);
				
				// add a types file to describe the type to file relationship
				ExportItem item = new ExportItem(name, inExportFileName, slide.getClass());
				manifest.getExportItems().add(item);
				
				// add any attached media
				// audio
				List<AudioMediaComponent> audioComponents = slide.getComponents(AudioMediaComponent.class, true);
				for (AudioMediaComponent component : audioComponents) {
					AbstractAudioMedia media = component.getMedia();
					if (media != null) {
						MediaFile mf = media.getFile();
						String mPath = "audio/" + mf.getName();
						if (!paths.contains(mPath)) {
							addFileToZip(mf.getFullPath(), mPath, zos);
							paths.add(mPath);
						}
					}
				}
				// image
				List<ImageMediaComponent> imageComponents = slide.getComponents(ImageMediaComponent.class, true);
				for (ImageMediaComponent component : imageComponents) {
					ImageMedia media = component.getMedia();
					if (media != null) {
						MediaFile mf = media.getFile();
						String mPath = "images/" + mf.getName();
						if (!paths.contains(mPath)) {
							addFileToZip(mf.getFullPath(), mPath, zos);
							paths.add(mPath);
						}
					}
				}
				// video
				List<VideoMediaComponent> videoComponents = slide.getComponents(VideoMediaComponent.class, true);
				for (VideoMediaComponent component : videoComponents) {
					AbstractVideoMedia media = component.getMedia();
					if (media != null) {
						MediaFile mf = media.getFile();
						String mPath = "videos/" + mf.getName();
						if (!paths.contains(mPath)) {
							addFileToZip(mf.getFullPath(), mPath, zos);
							paths.add(mPath);
						}
					}
				}
				i++;
			}
			
			// add the manifest entry
			ZipEntry entry = new ZipEntry("manifest.xml");
			zos.putNextEntry(entry);
			XmlIO.save(zos, manifest);
			zos.closeEntry();
		}
	}
	
	/**
	 * Imports the given slides export file.
	 * @param file the zip file containing the exported contents
	 * @throws SlideLibraryException thrown if an error occurs while adding the slide/templates
	 * @throws MediaException thrown if an error occurs while adding media
	 * @throws FileNotFoundException thrown if the manifest.xml file is not found
	 * @throws JAXBException thrown if the xml files are invalid
	 * @throws IOException thrown if any IO error occurs during the process
	 */
	public synchronized void importSlides(File file) throws SlideLibraryException, MediaException, FileNotFoundException, JAXBException, IOException {
		// get a file reference to the zip
		try (ZipFile zipFile = new ZipFile(file)) {
			// read the types entry first
			ZipEntry entry = zipFile.getEntry("manifest.xml");
			if (entry == null) {
				LOGGER.error("Types file [manifest.xml] not found.");
				// throw exception since we wont know how to read the slide/template
				// xml files unless we know what type they are
				throw new FileNotFoundException();
			}
			LOGGER.debug("Reading [manifest.xml] file.");
			ExportManifest manifest = XmlIO.read(zipFile.getInputStream(entry), ExportManifest.class);
			
			// read in the media items first
			// we save the zip entry names of the slide files so that we can read them later
			// we must do this since the media associated with the slide may come after the
			// slide, which would make the slide media reference null
			List<String> slideEntries = new ArrayList<String>();
			Map<String, Media> renamedMedia = new HashMap<String, Media>();
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				// get the entry
				entry = entries.nextElement();
				String currentEntry = entry.getName();
				
				// skip directory
				if (entry.isDirectory()) {
					LOGGER.debug("Ignoring directory entry.");
					continue;
				}
				
				// check for the slide
				if (currentEntry.toLowerCase().equals("manifest.xml")) {
					// skip any xml files
					continue;
				}
				
				Path path = Paths.get(currentEntry);
				String fileName = path.getFileName().toString();
				// check for the slide
				if (currentEntry.toLowerCase().endsWith(".xml")) {
					// save the entry for later reading
					slideEntries.add(currentEntry);
					// continue reading the other files
					continue;
				}
				
				MediaType type = null;
				if (path.startsWith("audio")) {
					type = MediaType.AUDIO;
				} else if (path.startsWith("images")) {
					type = MediaType.IMAGE;
				} else if (path.startsWith("videos")) {
					type = MediaType.VIDEO;
				} else {
					LOGGER.warn("[" + path + "] ignored");
					// ignore it and continue
					continue;
				}
				
				try {
					boolean renamed = false;
					MediaLibrary mLibrary = MediaLibrary.getInstance();
					if (mLibrary.containsMedia(fileName, type)) {
						LOGGER.warn("A media item in the Media Library already exists with the name [" + fileName + "].");
						fileName = getUniqueFileName(fileName);
						LOGGER.warn("Using file name [" + fileName + "] instead.");
						renamed = true;
					}
					LOGGER.debug("Adding the media [" + fileName + "|" + type + "] to the media library.");
					Media media = mLibrary.addMedia(zipFile.getInputStream(entry), type, fileName);
					if (renamed) {
						// save the original filename to new media mapping
						renamedMedia.put(path.getFileName().toString(), media);
					}
				} catch (NoMediaLoaderException e) {
					LOGGER.warn("The media [" + path + "] is not supported. Media ignored.");
				} catch (NotInitializedException e) {
					LOGGER.error("The media library has not been initialized.");
				}
			}
			
			// we support multiple slides in one file at this time
			if (slideEntries.size() > 0) {
				for (String currentEntry : slideEntries) {
					// get the zip entry
					entry = zipFile.getEntry(currentEntry);
					if (entry == null) {
						continue;
					}
					// get the slide type for the entry using the manifest file
					ExportItem item = manifest.getExportItem(currentEntry);
					if (item == null) {
						LOGGER.warn("Export item not found for [" + currentEntry + "]. Ignoring entry.");
						continue;
					}
					
					// read the slide
					LOGGER.debug("Reading [" + currentEntry + "] slide/template file with class [" + item.getType().getName() + "].");
					Slide slide = (Slide)XmlIO.read(zipFile.getInputStream(entry), item.getType());
					// use the original file name
					String fileName = item.getFileName();
					
					// we need to update any slide components that use the renamed media
					@SuppressWarnings("rawtypes")
					List<MediaComponent> components = slide.getComponents(MediaComponent.class, true);
					for (MediaComponent<? extends Media> component : components) {
						Media media = component.getMedia();
						// loop over the renamed media items
						for (String originalName : renamedMedia.keySet()) {
							if (media.getFile().getName().equals(originalName)) {
								// get the new media
								Media newMedia = renamedMedia.get(originalName);
								// we need to change the media reference
								if (component instanceof AudioMediaComponent && newMedia instanceof AbstractAudioMedia) {
									AudioMediaComponent amc = (AudioMediaComponent)component;
									AbstractAudioMedia am = (AbstractAudioMedia)newMedia;
									amc.setMedia(am);
								} else if (component instanceof ImageMediaComponent && newMedia instanceof ImageMedia) {
									ImageMediaComponent imc = (ImageMediaComponent)component;
									ImageMedia im = (ImageMedia)newMedia;
									imc.setMedia(im);
								} else if (component instanceof VideoMediaComponent && newMedia instanceof AbstractVideoMedia) {
									VideoMediaComponent vmc = (VideoMediaComponent)component;
									AbstractVideoMedia vm = (AbstractVideoMedia)newMedia;
									vmc.setMedia(vm);
								} else {
									// just log the error
									LOGGER.warn("Unable to change media because of unknown component type [" + component.getClass().getName() + "] or unknown media type [" + newMedia.getClass().getName() + "].");
								}
							}
						}
					}
					
					// if the name is already taken the change the name
					if (this.containsSlide(fileName, slide.getClass())) {
						LOGGER.warn("A slide or template in the Slide Library already exists with the name [" + fileName + "].");
						fileName = getUniqueFileName(fileName);
						LOGGER.warn("Using file name [" + fileName + "] instead.");
					}
					
					// save the slide to the slide library
					LOGGER.debug("Adding slide/template [" + fileName + "] to the slide library.");
					if (slide instanceof Template) {
						this.addTemplate(fileName, (Template)slide);
					} else {
						this.addSlide(fileName, (BasicSlide)slide);
					}
				}
			}
		}
	}
}

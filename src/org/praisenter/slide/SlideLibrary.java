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
import org.praisenter.Constants;
import org.praisenter.media.AbstractAudioMedia;
import org.praisenter.media.AbstractVideoMedia;
import org.praisenter.media.ImageMedia;
import org.praisenter.media.Media;
import org.praisenter.media.MediaException;
import org.praisenter.media.MediaFile;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaType;
import org.praisenter.media.NoMediaLoaderException;
import org.praisenter.resources.Messages;
import org.praisenter.slide.media.AudioMediaComponent;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.MediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.xml.XmlIO;

/**
 * Static interface for loading and saving slides and templates.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public final class SlideLibrary {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlideLibrary.class);
	
	// storage
	
	/** The saved slides */
	private static final Map<String, BasicSlide> SLIDES = new HashMap<String, BasicSlide>();
	
	/** The saved templates */
	private static final Map<String, BasicSlideTemplate> TEMPLATES = new HashMap<String, BasicSlideTemplate>();
	
	/** The saved bible templates */
	private static final Map<String, BibleSlideTemplate> BIBLE_TEMPLATES = new HashMap<String, BibleSlideTemplate>();
	
	/** The saved song templates */
	private static final Map<String, SongSlideTemplate> SONG_TEMPLATES = new HashMap<String, SongSlideTemplate>();

	/** The saved notification templates */
	private static final Map<String, NotificationSlideTemplate> NOTIFICATION_TEMPLATES = new HashMap<String, NotificationSlideTemplate>();
	
	// thumbnails
	
	/** The thumbnail size */
	public static final Dimension THUMBNAIL_SIZE = new Dimension(64, 48);
	
	/** The thumbnail file name */
	private static final String THUMBS_FILE = Constants.SEPARATOR + Constants.THUMBNAIL_FILE;
	
	/** The list of all thumbnails */
	private static final Map<String, SlideThumbnail> THUMBNAILS = new HashMap<String, SlideThumbnail>();

	// state
	
	/** True if the slide library has been loaded */
	private static boolean loaded = false;
	
	/** Hidden constructor */
	private SlideLibrary() {}

	/**
	 * Loads the slide and template library.
	 */
	public static final synchronized void loadSlideLibrary() {
		if (!loaded) {
			// preload the thumbnails, slides, and templates for all slides and templates
			loadSlideLibrary(Constants.SLIDE_PATH, BasicSlide.class, SLIDES);
			loadSlideLibrary(Constants.TEMPLATE_PATH, BasicSlideTemplate.class, TEMPLATES);
			loadSlideLibrary(Constants.BIBLE_TEMPLATE_PATH, BibleSlideTemplate.class, BIBLE_TEMPLATES);
			loadSlideLibrary(Constants.NOTIFICATIONS_TEMPLATE_PATH, NotificationSlideTemplate.class, NOTIFICATION_TEMPLATES);
			loadSlideLibrary(Constants.SONGS_TEMPLATE_PATH, SongSlideTemplate.class, SONG_TEMPLATES);
			loaded = true;
		}
	}
	
	/**
	 * Loads the thumbnails file from the given path.
	 * <p>
	 * If the file does not exist or is out of sync, it is generated and saved.
	 * @param path the path for the thumbnail file
	 * @param clazz the type to load
	 * @param map the map to add the loaded slide/template
	 */
	private static final synchronized <E extends Slide> void loadSlideLibrary(String path, Class<E> clazz, Map<String, E> map) {
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
				// get the file path
				String filePath = file.getPath();
				// skip the thumbnail file
				if (filePath.contains(THUMBS_FILE)) continue;
				// make sure there exists a thumnail for the file
				boolean exists = false;
				for (SlideThumbnail thumb : thumbnailsFromFile) {
					if (thumb.getFile().getName().equals(file.getName())) {
						// flag that the thumbnail exists
						exists = true;
						// add it to the thumbnails array
						thumbnails.add(thumb);
						// we can break from the loop
						break;
					}
				}
				// always load up the slide
				try {
					E slide = loadFromSlideLibrary(filePath, clazz);
					// add the media to the media library (might as well since we loaded it)
					map.put(filePath, slide);
					// check if we need to generate a thumbnail for the file
					if (!exists) {
						// generate a thumbnail for the slide
						BufferedImage image = slide.getThumbnail(THUMBNAIL_SIZE);
						// add the thumbnail to the list
						thumbnails.add(new SlideThumbnail(new SlideFile(filePath), slide.getName(), image));
						// flag that we need to save it
						save = true;					
					}
				} catch (SlideLibraryException e) {
					LOGGER.error("Unable to load slide/template [" + filePath + "|" + clazz.getName() + "]: ", e);
				}
			}
			// add all the thumbnails
			for (SlideThumbnail thumbnail : thumbnails) {
				THUMBNAILS.put(thumbnail.getFile().getPath(), thumbnail);
			}
			// after we have read all the files we need to save the new thumbs xml
			if (save || thumbnailsFromFile.size() != thumbnails.size()) {
				saveThumbnailsFile(clazz);
			}
		}
	}
	
	/**
	 * Returns the file system path for the given class.
	 * @param clazz the class
	 * @return String
	 */
	public static final String getPath(Class<?> clazz) {
		String path = Constants.SLIDE_PATH;
		
		// see if the type is a template
		if (Template.class.isAssignableFrom(clazz)) {
			// it could be a bible, song, or notification template
			if (BibleSlide.class.isAssignableFrom(clazz)) {
				path = Constants.BIBLE_TEMPLATE_PATH;
			} else if (NotificationSlide.class.isAssignableFrom(clazz)) {
				path = Constants.NOTIFICATIONS_TEMPLATE_PATH;
			} else if (SongSlide.class.isAssignableFrom(clazz)) {
				path = Constants.SONGS_TEMPLATE_PATH;
			} else {
				// generic template
				path = Constants.TEMPLATE_PATH;
			}
		}
		
		return path;
	}
	
	/**
	 * Writes the thumbnails file for the given class.
	 * @param clazz the class type
	 */
	private static synchronized final void saveThumbnailsFile(Class<?> clazz) {
		// get the path and thumbnails for the given class type
		String path = getPath(clazz);
		List<SlideThumbnail> thumbnails = getThumbnails(clazz);
		
		try {
			XmlIO.save(path + THUMBS_FILE, new SlideThumbnails(thumbnails));
			LOGGER.info("File [" + path + THUMBS_FILE + "] updated.");
		} catch (JAXBException | IOException e) {
			// silently log this error
			LOGGER.error("Failed to re-save [" + path + THUMBS_FILE + "]: ", e);
		}
	}

	/**
	 * Loads the given slide from the slide library.
	 * @param filePath the file name and path
	 * @param clazz the type to load
	 * @return {@link Slide}
	 * @throws SlideLibraryException thrown if the slide failed to be loaded
	 */
	private static synchronized final <E extends Slide> E loadFromSlideLibrary(String filePath, Class<E> clazz) throws SlideLibraryException {
		try {
			return XmlIO.read(filePath, clazz);
		} catch (JAXBException | IOException e) {
			throw new SlideLibraryException(MessageFormat.format("Could not load slide [{0}]", filePath), e);
		}
	}
	
	// public interface
	
	// thumbnails
	
	/**
	 * Returns the thumbnail for the given file path.
	 * <p>
	 * Returns null if no thumbnail exists.
	 * @param filePath the file path
	 * @return {@link SlideThumbnail}
	 */
	public static final synchronized SlideThumbnail getThumbnail(String filePath) {
		return THUMBNAILS.get(filePath);
	}

	/**
	 * Returns the thumbnails for the given class.
	 * @param clazz the class
	 * @return List&lt;{@link SlideThumbnail}&gt;
	 */
	public static final synchronized List<SlideThumbnail> getThumbnails(Class<?> clazz) {
		// we can use the slides map to get all the file path/names and use
		// those to look up all the thumbnails in that directory
		Set<String> paths = SLIDES.keySet();
		// see if the type is a template
		if (Template.class.isAssignableFrom(clazz)) {
			// it could be a bible, song, or notification template
			if (BibleSlide.class.isAssignableFrom(clazz)) {
				paths = BIBLE_TEMPLATES.keySet();
			} else if (NotificationSlide.class.isAssignableFrom(clazz)) {
				paths = NOTIFICATION_TEMPLATES.keySet();
			} else if (SongSlide.class.isAssignableFrom(clazz)) {
				paths = SONG_TEMPLATES.keySet();
			} else {
				// generic template
				paths = TEMPLATES.keySet();
			}
		}
		// get the thumbnails
		List<SlideThumbnail> thumbnails = new ArrayList<SlideThumbnail>();
		for (String filePath : paths) {
			thumbnails.add(THUMBNAILS.get(filePath));
		}
		// sort them
		Collections.sort(thumbnails);
		return thumbnails;
	}
	
	// slide library
	
	/**
	 * Returns true if the given slide exists in the slide library.
	 * @param fileName the file name
	 * @param clazz the slide type
	 * @return boolean
	 */
	public static final synchronized <E extends Slide> boolean containsSlide(String fileName, Class<E> clazz) {
		String path = getPath(clazz) + Constants.SEPARATOR + fileName;
		// see if the type is a template
		if (Template.class.isAssignableFrom(clazz)) {
			// it could be a bible, song, or notification template
			if (BibleSlide.class.isAssignableFrom(clazz)) {
				return BIBLE_TEMPLATES.containsKey(path);
			} else if (NotificationSlide.class.isAssignableFrom(clazz)) {
				return NOTIFICATION_TEMPLATES.containsKey(path);
			} else if (SongSlide.class.isAssignableFrom(clazz)) {
				return SONG_TEMPLATES.containsKey(path);
			} else {
				// generic template
				return TEMPLATES.containsKey(path);
			}
		} else {
			return SLIDES.containsKey(path);
		}
	}
	
	/**
	 * Returns the saved slide.
	 * @param filePath the file name and path of the saved slide
	 * @return {@link Slide}
	 * @throws SlideLibraryException thrown if an exception occurs while loading the slide
	 */
	public static final synchronized BasicSlide getSlide(String filePath) throws SlideLibraryException {
		try {
			BasicSlide slide = SLIDES.get(filePath);
			// see if the slide was cached
			if (slide == null) {
				LOGGER.info("Slide [" + filePath + "] not present in the slide library. Loading from file system.");
				slide = XmlIO.read(filePath, BasicSlide.class);
				// cache it
				SLIDES.put(filePath, slide);
				// cache the thumbnail
				SlideThumbnail thumbnail = new SlideThumbnail(
						new SlideFile(filePath), 
						slide.getName(),
						slide.getThumbnail(THUMBNAIL_SIZE));
				THUMBNAILS.put(filePath, thumbnail);
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
	 * @param fileName the desired file name
	 * @param slide the slide
	 * @return {@link SlideThumbnail}
	 * @throws SlideLibraryException thrown if an error occurs saving the slide
	 */
	public static final synchronized SlideThumbnail saveSlide(String fileName, BasicSlide slide) throws SlideLibraryException {
		if (!fileName.toLowerCase().matches("^.+\\.xml$")) {
			// append the .xml on the end
			fileName += ".xml";
		}
		String filePath = Constants.SLIDE_PATH + Constants.SEPARATOR + fileName;
		return SlideLibrary.saveSlideByPath(filePath, slide);
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
	public static final synchronized SlideThumbnail saveSlide(SlideFile file, BasicSlide slide) throws SlideLibraryException {
		String filePath = file.getPath();
		return SlideLibrary.saveSlideByPath(filePath, slide);
	}
	
	/**
	 * Saves the given slide to the given path and updates the slide library.
	 * <p>
	 * Returns an new {@link SlideThumbnail} for the given slide.
	 * @param path the location and file name of the slide
	 * @param slide the slide
	 * @return {@link SlideThumbnail}
	 * @throws SlideLibraryException thrown if an error occurs saving the slide
	 */
	private static final synchronized SlideThumbnail saveSlideByPath(String path, BasicSlide slide) throws SlideLibraryException {
		try {
			// save the slide to disc
			XmlIO.save(path, slide);
			// update the slide in the slide library
			SLIDES.put(path, slide);
			// update the thumbnail
			SlideThumbnail thumbnail = new SlideThumbnail(
					new SlideFile(path), 
					slide.getName(),
					slide.getThumbnail(THUMBNAIL_SIZE));
			// updated the thumbnail in the list of thumbnails
			THUMBNAILS.put(path, thumbnail);
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
	 * @param filePath the file name and path
	 * @return boolean
	 */
	public static final synchronized boolean deleteSlide(String filePath) {
		File file = new File(filePath);
		// delete the file
		if (file.delete()) {
			// remove the weak reference
			SLIDES.remove(filePath);
			// remove the thumbnail
			THUMBNAILS.remove(filePath);
			return true;
		}
		return false;
	}
	
	// template library
	
	/**
	 * Returns the saved template.
	 * @param filePath the file name and path of the saved template
	 * @param clazz the type of the template
	 * @return {@link Slide}
	 * @throws SlideLibraryException thrown if an exception occurs while loading the template
	 */
	@SuppressWarnings("unchecked")
	public static final synchronized <E extends Template> E getTemplate(String filePath, Class<E> clazz) throws SlideLibraryException {
		try {
			Map<String, E> map = null;
			// updated the template in the slide library
			if (BibleSlideTemplate.class.isAssignableFrom(clazz)) {
				map = (Map<String, E>)BIBLE_TEMPLATES;
			} else if (NotificationSlideTemplate.class.isAssignableFrom(clazz)) {
				map = (Map<String, E>)NOTIFICATION_TEMPLATES;
			} else if (SongSlideTemplate.class.isAssignableFrom(clazz)) {
				map = (Map<String, E>)SONG_TEMPLATES;
			} else {
				map = (Map<String, E>)TEMPLATES;
			}
			
			E slide = map.get(filePath);
			// see if the slide was cached
			if (slide == null) {
				LOGGER.info("Template [" + filePath + "] not present in the slide library. Loading from file system.");
				slide = XmlIO.read(filePath, clazz);
				// cache it
				map.put(filePath, slide);
				// cache the thumbnail
				SlideThumbnail thumbnail = new SlideThumbnail(
						new SlideFile(filePath), 
						slide.getName(),
						slide.getThumbnail(THUMBNAIL_SIZE));
				THUMBNAILS.put(filePath, thumbnail);
			}
			// return the slide
			return slide;
		} catch (JAXBException | IOException e) {
			throw new SlideLibraryException(e);
		}
	}
	
	/**
	 * Saves the given template to the slide library.
	 * <p>
	 * Returns an new {@link SlideThumbnail} for the given template.
	 * @param fileName the file name
	 * @param template the template
	 * @return {@link SlideThumbnail}
	 * @throws SlideLibraryException thrown if an exception occurs while saving the template
	 */
	public static final synchronized <E extends Template> SlideThumbnail saveTemplate(String fileName, E template) throws SlideLibraryException {
		String path = getPath(template.getClass());
		if (!fileName.toLowerCase().matches("^.+\\.xml$")) {
			// append the .xml on the end
			fileName += ".xml";
		}
		String filePath = path + Constants.SEPARATOR + fileName;
		return SlideLibrary.saveTemplateByPath(filePath, template);
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
	public static final synchronized <E extends Template> SlideThumbnail saveTemplate(SlideFile file, E template) throws SlideLibraryException {
		return SlideLibrary.saveTemplateByPath(file.getPath(), template);
	}
	
	/**
	 * Saves the given template to the slide library.
	 * <p>
	 * Returns an new {@link SlideThumbnail} for the given template.
	 * @param path the file name and path
	 * @param template the template
	 * @return {@link SlideThumbnail}
	 * @throws SlideLibraryException thrown if an exception occurs while saving the template
	 */
	private static final synchronized <E extends Template> SlideThumbnail saveTemplateByPath(String path, E template) throws SlideLibraryException {
		try {
			// save the template to disc
			XmlIO.save(path, template);
			// updated the template in the slide library
			if (template instanceof BibleSlideTemplate) {
				BIBLE_TEMPLATES.put(path, (BibleSlideTemplate)template);
			} else if (template instanceof NotificationSlideTemplate) {
				NOTIFICATION_TEMPLATES.put(path, (NotificationSlideTemplate)template);
			} else if (template instanceof SongSlideTemplate) {
				SONG_TEMPLATES.put(path, (SongSlideTemplate)template);
			} else {
				TEMPLATES.put(path, (BasicSlideTemplate)template);
			}
			// update the thumbnail
			SlideThumbnail thumbnail = new SlideThumbnail(
					new SlideFile(path), 
					template.getName(),
					template.getThumbnail(THUMBNAIL_SIZE));
			// updated the thumbnail in the list of thumbnails
			THUMBNAILS.put(path, thumbnail);
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
	 * @param filePath the file name and path
	 * @return boolean
	 */
	public static final synchronized boolean deleteTemplate(String filePath) {
		File file = new File(filePath);
		// delete the file
		if (file.delete()) {
			// remove the weak reference
			// remove it from all template maps (since its a no-op if its not there)
			TEMPLATES.remove(filePath);
			BIBLE_TEMPLATES.remove(filePath);
			SONG_TEMPLATES.remove(filePath);
			NOTIFICATION_TEMPLATES.remove(filePath);
			// remove the thumbnail
			THUMBNAILS.remove(filePath);
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
		return MessageFormat.format(Messages.getString("panel.slide.import.fileNameCollision.format"), name, new Date(), ext);
	}
	
	/**
	 * Reads the given file path and writes it to the given zip output stream using the
	 * given file name.
	 * <p>
	 * The file name can include the path inside the zip.
	 * @param filePath the file name and path of the file to add to the zip
	 * @param fileName the file name and path of the file inside the zip
	 * @param zos the zip input stream
	 * @throws FileNotFoundException thrown if the given file path does not exist
	 * @throws ZipException thrown if an error occurs while writing the file to the zip output stream
	 * @throws IOException thrown if an error occurs while reading or writing the file
	 */
	private static final void addFileToZip(String filePath, String fileName, ZipOutputStream zos) throws FileNotFoundException, ZipException, IOException {
		// add the slide xml file
		File sf = new File(filePath);
		try (FileInputStream fis = new FileInputStream(sf)) {
			ZipEntry entry = new ZipEntry(fileName);
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
	 * @param fileName the target zip file name
	 * @param exports the array of slides to export
	 * @throws SlideLibraryException thrown if an exception occurs while loading the slide/template
	 * @throws JAXBException thrown if an error occurs while writing the types file
	 * @throws IOException thrown if an error occurs while generating the export file
	 */
	public static final synchronized void exportSlides(String fileName, SlideExport... exports) throws SlideLibraryException, JAXBException, IOException {
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
				String path = slideFile.getPath();
				String name = slideFile.getName();
				
				Slide slide = null;
				if (export.isTemplate()) {
					slide = getTemplate(path, export.getTemplateType());
				} else {
					slide = getSlide(path);
				}
				
				// add the slide xml file
				String inExportFileName = "SlideTemplate" + i + ".xml";
				addFileToZip(path, inExportFileName, zos);
				
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
							addFileToZip(mf.getPath(), mPath, zos);
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
							addFileToZip(mf.getPath(), mPath, zos);
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
							addFileToZip(mf.getPath(), mPath, zos);
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
	public static final synchronized void importSlides(File file) throws SlideLibraryException, MediaException, FileNotFoundException, JAXBException, IOException {
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
					if (MediaLibrary.containsMedia(fileName, type)) {
						LOGGER.warn("A media item in the Media Library already exists with the name [" + fileName + "].");
						fileName = getUniqueFileName(fileName);
						LOGGER.warn("Using file name [" + fileName + "] instead.");
						renamed = true;
					}
					LOGGER.debug("Adding the media [" + fileName + "|" + type + "] to the media library.");
					Media media = MediaLibrary.addMedia(zipFile.getInputStream(entry), type, fileName);
					if (renamed) {
						// save the original filename to new media mapping
						renamedMedia.put(path.getFileName().toString(), media);
					}
					
				} catch (NoMediaLoaderException e) {
					LOGGER.warn("The media [" + path + "] is not supported. Media ignored.");
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
					String slideName = item.getFileName();
					
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
					if (SlideLibrary.containsSlide(slideName, slide.getClass())) {
						LOGGER.warn("A slide or template in the Slide Library already exists with the name [" + slideName + "].");
						slideName = getUniqueFileName(slideName);
						LOGGER.warn("Using file name [" + slideName + "] instead.");
					}
					
					// save the slide to the slide library
					LOGGER.debug("Adding slide/template [" + slideName + "] to the slide library.");
					if (slide instanceof Template) {
						SlideLibrary.saveTemplate(slideName, (Template)slide);
					} else {
						SlideLibrary.saveSlide(slideName, (BasicSlide)slide);
					}
				}
			}
		}
	}
}

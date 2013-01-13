package org.praisenter.slide;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.xml.XmlIO;

/**
 * Static interface for loading and saving slides and templates.
 * @author USWIBIT
 *
 */
public class SlideLibrary {
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
			TEMPLATES.remove(filePath);
			// remove the thumbnail
			THUMBNAILS.remove(filePath);
			return true;
		}
		return false;
	}
}

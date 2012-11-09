package org.praisenter.slide;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.FileSystems;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.utilities.FileUtilities;
import org.praisenter.xml.FileProperties;
import org.praisenter.xml.Thumbnail;
import org.praisenter.xml.XmlIO;

/**
 * Static interface for loading and saving slides and templates.
 * @author USWIBIT
 *
 */
public class SlideLibrary {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(SlideLibrary.class);
	
	/** File path separator */
	private static final String SEPARATOR = FileSystems.getDefault().getSeparator();
	
	// storage
	
	/** The saved slides */
	private static final Map<String, WeakReference<Slide>> SLIDES = new HashMap<String, WeakReference<Slide>>();
	
	/** The saved templates */
	private static final Map<String, WeakReference<Slide>> TEMPLATES = new HashMap<String, WeakReference<Slide>>();
	
	/** The saved bible templates */
	private static final Map<String, WeakReference<BibleSlide>> BIBLE_TEMPLATES = new HashMap<String, WeakReference<BibleSlide>>();
	
	/** The saved song templates */
	private static final Map<String, WeakReference<Slide>> SONG_TEMPLATES = new HashMap<String, WeakReference<Slide>>();

	// thumbnails
	
	/** The thumbnail size */
	private static final Dimension THUMBNAIL_SIZE = new Dimension(64, 48);
	
	/** The thumbnail file name */
	private static final String THUMBS_FILE = SEPARATOR + "Thumbs.xml";
	
	/** The list of all thumbnails */
	private static final Map<String, Thumbnail> THUMBNAILS = new HashMap<String, Thumbnail>();

	// state
	
	/** True if the slide library has been loaded */
	private static boolean loaded = false;
	
	static {
		// FIXME move this
		try {
			loadSlideLibrary();
		} catch (SlideLibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** Hidden constructor */
	private SlideLibrary() {}

	/**
	 * Loads the slide and template library.
	 * @throws SlideLibraryException thrown if an exception occurs during loading
	 */
	public static final synchronized void loadSlideLibrary() throws SlideLibraryException {
		if (!loaded) {
			// preload the thumbnails, slides, and templates for all slides and templates
			loadSlideLibrary(Constants.SLIDE_PATH, Slide.class);
			loadSlideLibrary(Constants.TEMPLATE_PATH, Slide.class);
			loadSlideLibrary(Constants.BIBLE_TEMPLATE_PATH, BibleSlide.class);
			loadSlideLibrary(Constants.SONGS_TEMPLATE_PATH, Slide.class);
			loaded = true;
		}
	}
	
	/**
	 * Loads the thumbnails file from the given path.
	 * <p>
	 * If the file does not exist or is out of sync, it is generated and saved.
	 * @param path the path for the thumbnail file
	 * @param clazz the type to load
	 * @throws SlideLibraryException thrown if the slide failed to be loaded
	 */
	private static final synchronized void loadSlideLibrary(String path, Class<? extends Slide> clazz) throws SlideLibraryException {
		// attempt to read the thumbs file in the respective folder
		List<Thumbnail> thumbnailsFromFile = null;
		try {
			SlideThumbnails sts = XmlIO.read(path + THUMBS_FILE, SlideThumbnails.class);
			if (sts != null) {
				thumbnailsFromFile = sts.getThumbnails();
			}
		} catch (Exception e) {
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
			// skip the thumbnail file
			if (FileUtilities.getContentType(filePath).contains(THUMBS_FILE)) continue;
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
				Slide slide = loadFromSlideLibrary(filePath, clazz);
				// add the media to the media library (might as well since we loaded it)
				SLIDES.put(filePath, new WeakReference<Slide>(slide));
				// create the thumbnail
				BufferedImage image = slide.getThumbnail(THUMBNAIL_SIZE);
				// add the thumbnail to the list
				thumbnails.add(new Thumbnail(FileProperties.getFileProperties(filePath), image));
				// flag that we need to save it
				save = true;
			} else {
				// we need to add a media reference anyway
				SLIDES.put(filePath, new WeakReference<Slide>(null));
			}
		}
		// add all the thumbnails
		for (Thumbnail thumbnail : thumbnails) {
			THUMBNAILS.put(thumbnail.getFileProperties().getFilePath(), thumbnail);
		}
		// after we have read all the files we need to save the new thumbs xml
		if (save || thumbnailsFromFile.size() != thumbnails.size()) {
			saveThumbnailsFile(path, thumbnails);
		}
	}
	
	/**
	 * Writes the thumbnails file for the slides path.
	 */
	private static synchronized final void saveSlideThumbnails() {
		try {
			List<Thumbnail> thumbnails = getSlideThumbnails();
			XmlIO.save(Constants.SLIDE_PATH + THUMBS_FILE, new SlideThumbnails(thumbnails));
			LOGGER.info("File [" + Constants.SLIDE_PATH + THUMBS_FILE + "] updated.");
		} catch (JAXBException | IOException e) {
			// silently log this error
			LOGGER.error("Failed to re-save [" + Constants.SLIDE_PATH + THUMBS_FILE + "]: ", e);
		}
	}

	/**
	 * Writes the thumbnails file for the templates path.
	 */
	private static synchronized final void saveTemplateThumbnails() {
		try {
			List<Thumbnail> thumbnails = getTemplateThumbnails();
			XmlIO.save(Constants.TEMPLATE_PATH + THUMBS_FILE, new SlideThumbnails(thumbnails));
			LOGGER.info("File [" + Constants.TEMPLATE_PATH + THUMBS_FILE + "] updated.");
		} catch (JAXBException | IOException e) {
			// silently log this error
			LOGGER.error("Failed to re-save [" + Constants.TEMPLATE_PATH + THUMBS_FILE + "]: ", e);
		}
	}
	
	/**
	 * Writes the thumbnails file for the given path and list of thumbnails.
	 * @param path the path of the thumbnails file (no file name)
	 * @param thumbnails the list of thumbnails
	 */
	private static synchronized final void saveThumbnailsFile(String path, List<Thumbnail> thumbnails) {
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
	private static synchronized final Slide loadFromSlideLibrary(String filePath, Class<? extends Slide> clazz) throws SlideLibraryException {
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
	 * @return {@link Thumbnail}
	 */
	public static final synchronized Thumbnail getThumbnail(String filePath) {
		return THUMBNAILS.get(filePath);
	}
	
	/**
	 * Returns all the thumbnails in the slide library.
	 * @return List&lt;{@link Thumbnail}&gt;
	 */
	public static synchronized final List<Thumbnail> getSlideThumbnails() {
		List<Thumbnail> thumbnails = new ArrayList<Thumbnail>();
		// we can use the slides map to get all the file path/names and use
		// those to look up all the thumbnails in that directory
		for (String filePath : SLIDES.keySet()) {
			thumbnails.add(THUMBNAILS.get(filePath));
		}
		return thumbnails;
	}
	
	/**
	 * Returns all the thumbnails in the template library.
	 * @return List&lt;{@link Thumbnail}&gt;
	 */
	public static final synchronized List<Thumbnail> getTemplateThumbnails() {
		List<Thumbnail> thumbnails = new ArrayList<Thumbnail>();
		// we can use the templates map to get all the file path/names and use
		// those to look up all the thumbnails in that directory
		for (String filePath : TEMPLATES.keySet()) {
			thumbnails.add(THUMBNAILS.get(filePath));
		}
		return thumbnails;
	}
	
	/**
	 * Returns all the thumbnails in the bible template library.
	 * @return List&lt;{@link Thumbnail}&gt;
	 */
	public static final synchronized List<Thumbnail> getBibleTemplateThumbnails() {
		List<Thumbnail> thumbnails = new ArrayList<Thumbnail>();
		// we can use the bible templates map to get all the file path/names and use
		// those to look up all the thumbnails in that directory
		for (String filePath : BIBLE_TEMPLATES.keySet()) {
			thumbnails.add(THUMBNAILS.get(filePath));
		}
		return thumbnails;
	}
	
	/**
	 * Returns all the thumbnails in the song template library.
	 * @return List&lt;{@link Thumbnail}&gt;
	 */
	public static final synchronized List<Thumbnail> getSongTemplateThumbnails() {
		List<Thumbnail> thumbnails = new ArrayList<Thumbnail>();
		// we can use the song templates map to get all the file path/names and use
		// those to look up all the thumbnails in that directory
		for (String filePath : SONG_TEMPLATES.keySet()) {
			thumbnails.add(THUMBNAILS.get(filePath));
		}
		return thumbnails;
	}
	
	// slide library
	
	/**
	 * Returns the saved slide.
	 * @param filePath the file name and path of the saved slide
	 * @return {@link Slide}
	 * @throws SlideLibraryException thrown if an exception occurs while loading the slide
	 */
	public static final synchronized Slide getSlide(String filePath) throws SlideLibraryException {
		try {
			return XmlIO.read(filePath, Slide.class);
		} catch (JAXBException | IOException e) {
			throw new SlideLibraryException(e);
		}
	}
	
	/**
	 * Adds the given slide to the slide library.
	 * @param fileName the desired file name
	 * @param slide the slide
	 * @throws SlideLibraryException thrown if an error occurs saving the slide
	 */
	public static final synchronized void saveSlide(String fileName, Slide slide) throws SlideLibraryException {
		String filePath = Constants.SLIDE_PATH + SEPARATOR + fileName;
		try {
			// save the slide to disc
			XmlIO.save(filePath, slide);
			// add the slide to the slide library
			SLIDES.put(filePath, new WeakReference<Slide>(slide));
			// create a thumbnail
			Thumbnail thumbnail = new Thumbnail(
					FileProperties.getFileProperties(filePath), 
					slide.getThumbnail(THUMBNAIL_SIZE));
			// add the thumbnail to the list of thumbnails
			THUMBNAILS.put(filePath, thumbnail);
			// update the thumbnail file for this directory
			saveSlideThumbnails();
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
	 * @param clazz the type of the slide
	 * @return {@link Slide}
	 * @throws SlideLibraryException thrown if an exception occurs while loading the slide
	 */
	public static final synchronized <E extends Slide> E getTemplate(String filePath, Class<E> clazz) throws SlideLibraryException {
		try {
			return XmlIO.read(filePath, clazz);
		} catch (JAXBException | IOException e) {
			throw new SlideLibraryException(e);
		}
	}
	
	/**
	 * Saves the given template to the slide library.
	 * @param fileName the file name
	 * @param slide the template
	 * @throws SlideLibraryException thrown if an exception occurs while saving the slide
	 */
	public static final synchronized <E extends Slide> void saveSlideTemplate(String fileName, E slide) throws SlideLibraryException {
		String path = Constants.TEMPLATE_PATH;
		if (slide instanceof BibleSlide) {
			path = Constants.BIBLE_TEMPLATE_PATH;
		}
		String filePath = path + SEPARATOR + fileName;
		try {
			// save the slide to disc
			XmlIO.save(filePath, slide);
			// add the slide to the slide library
			TEMPLATES.put(filePath, new WeakReference<Slide>(slide));
			// create a thumbnail
			Thumbnail thumbnail = new Thumbnail(
					FileProperties.getFileProperties(filePath), 
					slide.getThumbnail(THUMBNAIL_SIZE));
			// add the thumbnail to the list of thumbnails
			THUMBNAILS.put(filePath, thumbnail);
			// update the thumbnail file for this directory
			saveTemplateThumbnails();
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
	public static final synchronized boolean deleteSlideTemplate(String filePath) {
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

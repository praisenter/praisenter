package org.praisenter.javafx;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.concurrent.Task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.data.Database;
import org.praisenter.javafx.media.JavaFXMediaImportFilter;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnailSettings;
import org.praisenter.slide.SlideLibrary;
import org.praisenter.song.SongLibrary;
import org.praisenter.utility.ClasspathLoader;

// TODO translate
final class ContextLoadingTask extends Task<PraisenterContext> {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/* (non-Javadoc)
	 * @see javafx.concurrent.Task#call()
	 */
	@Override
	protected PraisenterContext call() throws Exception {
		updateProgress(0, 4);
		
		// bible loading
		LOGGER.info("Loading bible library");
    	updateMessage("Loading bibles");
		Database db = Database.open(Paths.get(Constants.DATABASE_ABSOLUTE_PATH));
		BibleLibrary bibles = new BibleLibrary(db);
		updateProgress(1, 4);
		LOGGER.info("Bible library loaded");
		
		// song loading
		LOGGER.info("Loading song library");
		updateMessage("Loading songs");
		SongLibrary songs = SongLibrary.open(Paths.get(Constants.SONGS_ABSOLUTE_PATH));
		updateProgress(2, 4);
		LOGGER.info("Song library loaded");

		// media loading
		LOGGER.info("Loading media library");
		updateMessage("Loading media");
		Path mediaPath = Paths.get(Constants.MEDIA_ABSOLUTE_PATH);
		MediaThumbnailSettings settings = new MediaThumbnailSettings(
				Constants.MEDIA_THUMBNAIL_SIZE, 
				Constants.MEDIA_THUMBNAIL_SIZE,
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/image-default-thumbnail.png"),
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/music-default-thumbnail.png"),
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/video-default-thumbnail.png"));
    	MediaLibrary media = MediaLibrary.open(mediaPath, new JavaFXMediaImportFilter(mediaPath), settings);
    	updateProgress(3, 4);
    	LOGGER.info("Media library loaded");
		
    	// song loading
    	LOGGER.info("Loading slide library");
		updateMessage("Loading slides");
		SlideLibrary slides = SlideLibrary.open(Paths.get(Constants.SLIDES_ABSOLUTE_PATH));
		updateProgress(4, 4);
		LOGGER.info("Slide library loaded");
    	
		PraisenterContext context = new PraisenterContext(media, songs, bibles, slides);
		LOGGER.info("Context created successfully");
		
		return context;
	}
}

package org.praisenter.javafx;

import org.praisenter.bible.BibleLibrary;
import org.praisenter.media.MediaLibrary;
import org.praisenter.song.SongLibrary;

public final class PraisenterContext {
	private final MediaLibrary mediaLibrary;
	private final SongLibrary songLibrary;
	private final BibleLibrary bibleLibrary;
	private final ImageCache imageCache;
	
	public PraisenterContext(MediaLibrary ml, SongLibrary sl, BibleLibrary bl) {
		this.mediaLibrary = ml;
		this.songLibrary = sl;
		this.bibleLibrary = bl;
		this.imageCache = new ImageCache();
	}

	public MediaLibrary getMediaLibrary() {
		return mediaLibrary;
	}

	public SongLibrary getSongLibrary() {
		return songLibrary;
	}

	public BibleLibrary getBibleLibrary() {
		return bibleLibrary;
	}
	
	public ImageCache getImageCache() {
		return imageCache;
	}
}

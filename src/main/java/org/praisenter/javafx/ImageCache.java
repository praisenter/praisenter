package org.praisenter.javafx;

import java.lang.ref.SoftReference;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javafx.scene.image.Image;

public final class ImageCache {
	private final Map<Path, SoftReference<Image>> images;
	
	public ImageCache() {
		this.images = new HashMap<Path, SoftReference<Image>>();
	}
	
	public synchronized Image get(Path path) {
		this.evict();
		if (this.images.containsKey(path)) {
			Image image = this.images.get(path).get();
			if (image != null) {
				return image;
			}
		}
		Image image = this.load(path);
		this.images.put(path, new SoftReference<Image>(image));
		return image;
	}
	
	private void evict() {
		Iterator<SoftReference<Image>> it = this.images.values().iterator();
		while (it.hasNext()) {
			SoftReference<Image> ref = it.next();
			if (ref.get() == null) {
				it.remove();
			}
		}
	}
	
	private Image load(Path path) {
		return new Image(path.toUri().toString(), false);
	}
}

package org.praisenter.data.slide;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.Persistable;
import org.praisenter.data.Tag;
import org.praisenter.data.TextStore;
import org.praisenter.data.search.Indexable;
import org.praisenter.data.slide.animation.SlideAnimation;

import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public interface ReadOnlySlide extends ReadOnlySlideRegion, Indexable, Persistable, Copyable, Identifiable {
	public TextStore getPlaceholderData();
	public long getTime();
	public Path getThumbnailPath();
	
	public ReadOnlyObjectProperty<TextStore> placeholderDataProperty();
	public ReadOnlyLongProperty timeProperty();
	public ReadOnlyObjectProperty<Path> thumbnailPathProperty();
	
	public ObservableSet<Tag> getTagsUnmodifiable();
	public ObservableList<SlideComponent> getComponentsUnmodifiable();
	public ObservableList<SlideAnimation> getAnimationsUnmodifiable();
	
	public long getTotalTime();
	public boolean hasPlaceholders();
	
	public <E extends SlideComponent> List<E> getComponents(Class<E> clazz);
	public SlideComponent getComponent(UUID id);
	public List<SlideAnimation> getAnimations(UUID id);
}

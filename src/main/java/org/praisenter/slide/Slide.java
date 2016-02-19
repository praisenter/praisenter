package org.praisenter.slide;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({
	BasicSlide.class,
	SongSlide.class,
	BibleSlide.class
})
@XmlAccessorType(XmlAccessType.NONE)
public interface Slide extends SlideRegion {
	public static final String VERSION = "3.0.0";
	
	// properties
	
	public abstract UUID getId();
	public abstract Path getPath();
	public abstract void setPath(Path path);
	public abstract String getVersion();
	
	// components
	
	public abstract void addComponent(SlideComponent component);
	public abstract boolean removeComponent(SlideComponent component);
	public abstract List<SlideComponent> getComponents();
	public abstract <E extends SlideComponent> List<E> getComponents(Class<E> clazz);
	public abstract boolean hasPlaceholders();
	
	// z-ordering
	
	public abstract void moveComponentUp(SlideComponent component);
	public abstract void moveComponentDown(SlideComponent component);
	
	// transition
	
	public abstract int getTransition();
	public abstract void setTransition(int id);
	public abstract int getEasing();
	public abstract void setEasing(int id);
	public abstract int getDuration();
	public abstract void setDuration(int duration);
	
	// other
	
	// this is the time the slide should stay visible in an automatic show; -1 indicates forever
	public abstract int getTime();
	public abstract void setTime(int time);
	
	// copy
	
	public abstract Slide copy();
}

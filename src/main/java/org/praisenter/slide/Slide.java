package org.praisenter.slide;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import javafx.scene.image.Image;

@XmlAccessorType(XmlAccessType.NONE)
public interface Slide extends SlideRegion {
	
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
	
	// z-ordering
	
	public abstract void moveComponentUp(SlideComponent component);
	public abstract void moveComponentDown(SlideComponent component);
}

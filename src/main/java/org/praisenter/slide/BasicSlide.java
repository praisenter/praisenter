package org.praisenter.slide;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "slide")
@XmlAccessorType(XmlAccessType.NONE)
public abstract class BasicSlide extends AbstractSlideRegion implements Slide, SlideRegion {
	@XmlAttribute(name = "id", required = false)
	UUID id;
	
	@XmlAttribute(name = "path", required = false)
	Path path;
	
	@XmlAttribute(name = "version", required = false)
	final String version = "3.0.0";
	
//	@XmlElementRefs({
//		@XmlElementRef(name = "", type = ),
//		@XmlElementRef(name = "", type = ),
//		@XmlElementRef(name = "", type = ),
//		@XmlElementRef(name = "", type = )
//	})
	@XmlElementWrapper(name = "components", required = false)
	final List<SlideComponent> components;
	
	public BasicSlide() {
		this.components = new ArrayList<SlideComponent>();
	}
	
	@Override
	public UUID getId() {
		return this.id;
	}
	
	@Override
	public Path getPath() {
		return this.path;
	}

	@Override
	public void setPath(Path path) {
		this.path = path;
	}

	@Override
	public String getVersion() {
		return this.version;
	}
	
	@Override
	public void addComponent(SlideComponent component) {
		int order = this.getNextIndex();
		((SlideComponent)component).setOrder(order);
		this.components.add(component);
		this.sortComponentsByOrder(this.components);
	}
	
	@Override
	public boolean removeComponent(SlideComponent component) {
		// no re-sort required here
		return this.components.remove(component);
	}
	
	@Override
	public List<SlideComponent> getComponents() {
		return new ArrayList<SlideComponent>(this.components);
	}
	
	@Override
	public <E extends SlideComponent> List<E> getComponents(Class<E> clazz) {
		List<E> components = new ArrayList<E>();
		for (SlideComponent component : this.components) {
			if (clazz.isInstance(component)) {
				components.add(clazz.cast(component));
			}
		}
		return components;
	}
	
	@Override
	public void moveComponentUp(SlideComponent component) {
		// move the given component up in the order
		
		// get all the components
		List<SlideComponent> components = this.components;
		// verify the component exists on this slide
		if (components.contains(component) && components.size() > 0) {
			int size = components.size();
			// see if the component is already in the last position
			if (components.get(size - 1).equals(component)) {
				// if it is, then just return its order
				return;
			} else {
				// if its not in the last position then we need to 
				// move it up and change the subsequent component (move it back by one)
				int order = component.getOrder();
				for (SlideComponent cmp : components) {
					// see if the current component order is greater
					// than this component's order
					if (cmp.getOrder() == order + 1) {
						// we only need to move back the next component
						cmp.setOrder(cmp.getOrder() - 1);
						break;
					}
				}
				// move the given component up
				component.setOrder(order + 1);
				// resort the components
				this.sortComponentsByOrder(this.components);
			}
		}
	}

	@Override
	public void moveComponentDown(SlideComponent component) {
		// move the given component down in the order
		
		// get all the components
		List<SlideComponent> components = this.components;
		// verify the component exists on this slide
		if (components.contains(component) && components.size() > 0) {
			// see if the component is already in the first position
			if (components.get(0).equals(component)) {
				// if it is, then just return its order
				return;
			} else {
				// if its not in the first position then we need to 
				// move it down and change the previous component (move it up by one)
				int order = component.getOrder();
				for (SlideComponent cmp : components) {
					// find the previous component
					if (cmp.getOrder() == order - 1) {
						// we only need to move up the previous component
						cmp.setOrder(cmp.getOrder() + 1);
						break;
					}
				}
				// move the given component up
				component.setOrder(order - 1);
				// resort the components
				this.sortComponentsByOrder(this.components);
			}
		}
	}
	
	/**
	 * Sorts the given components using their z-ordering.
	 * @param components the list of components to sort
	 */
	private <E extends SlideComponent> void sortComponentsByOrder(List<E> components) {
		Collections.sort(components);
	}
	
	/**
	 * Returns the next order index in the list of components.
	 * @return int
	 */
	private int getNextIndex() {
		List<SlideComponent> components = this.components;
		if (components.size() > 0) {
			int maximum = 1;
			for (SlideComponent component : components) {
				if (maximum < component.getOrder()) {
					maximum = component.getOrder();
				}
			}
			return maximum + 1;
		} else {
			return 1;
		}
	}
	
}

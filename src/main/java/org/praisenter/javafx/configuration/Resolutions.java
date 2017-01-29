package org.praisenter.javafx.configuration;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "resolutions")
@XmlAccessorType(XmlAccessType.NONE)
public final class Resolutions implements Iterable<Resolution>, Collection<Resolution>, Set<Resolution> {
	@XmlElement(name = "resolution", required = false)
	private final Set<Resolution> resolutions;
	
	public Resolutions() {
		this.resolutions = new TreeSet<Resolution>();
	}
	
	public boolean add(Resolution resolution) {
		return this.resolutions.add(resolution);
	}
	
	public boolean addAll(Collection<? extends Resolution> resolutions) {
		return this.resolutions.addAll(resolutions);
	}
	
	public boolean remove(Object object) {
		return this.resolutions.remove(object);
	}
	
	public boolean removeAll(Collection<?> resolutions) {
		return this.resolutions.removeAll(resolutions);
	}
	
	public boolean contains(Object object) {
		return this.resolutions.contains(object);
	}
	
	public boolean containsAll(Collection<?> resolutions) {
		return this.resolutions.containsAll(resolutions);
	}
	
	public boolean retainAll(Collection<?> resolutions) {
		return this.resolutions.retainAll(resolutions);
	}

	public Iterator<Resolution> iterator() {
		return this.resolutions.iterator();
	}
	
	public int size() {
		return this.resolutions.size();
	}
	
	public boolean isEmpty() {
		return this.resolutions.isEmpty();
	}

	@Override
	public Object[] toArray() {
		return this.resolutions.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.resolutions.toArray(a);
	}

	@Override
	public void clear() {
		this.resolutions.clear();
	}
}

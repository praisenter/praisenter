/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.configuration;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a set of resolutions.
 * @author William Bittle
 * @version 3.0.0
 */
public final class ResolutionSet implements Iterable<Resolution>, Collection<Resolution>, Set<Resolution> {
	/** The resolutions */
	@JsonProperty
	private final TreeSet<Resolution> resolutions;
	
	/**
	 * Default constructor.
	 */
	public ResolutionSet() {
		this.resolutions = new TreeSet<Resolution>();
	}
	
	/**
	 * Returns the resolution before the given resolution.
	 * <p>
	 * If the given resolution is null, the first resolution in the list is returned.
	 * <p>
	 * If the given resolution is the first resolution, then null is returned.
	 * @param resolution the resolution to search by
	 * @return {@link Resolution}
	 */
	public Resolution getPreviousResolution(Resolution resolution) {
		if (resolution == null) return this.resolutions.first();
		return this.resolutions.lower(resolution);
	}
	
	/**
	 * Returns the resolution after the given resolution.
	 * <p>
	 * If the given resolution is null, the first resolution in the list is returned.
	 * <p>
	 * If the given resolution is the last resolution, then null is returned.
	 * @param resolution the resolution to search by
	 * @return {@link Resolution}
	 */
	public Resolution getNextResolution(Resolution resolution) {
		if (resolution == null) return this.resolutions.first();
		return this.resolutions.higher(resolution);
	}
	
	/**
	 * Returns the closest resolution to the given resolution.
	 * <p>
	 * If the given resolution is null, the first resolution in the list is returned.
	 * <p>
	 * If there are no resolutions left in the set, then null is returned.
	 * @param resolution the resolution to search by
	 * @return {@link Resolution}
	 */
	public Resolution getClosestResolution(Resolution resolution) {
		Resolution r = this.getPreviousResolution(resolution);
		if (r == null) {
			r = this.getNextResolution(resolution);
		}
		return r;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Resolution resolution) {
		return this.resolutions.add(resolution);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends Resolution> resolutions) {
		return this.resolutions.addAll(resolutions);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object object) {
		return this.resolutions.remove(object);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> resolutions) {
		return this.resolutions.removeAll(resolutions);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object object) {
		return this.resolutions.contains(object);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> resolutions) {
		return this.resolutions.containsAll(resolutions);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> resolutions) {
		return this.resolutions.retainAll(resolutions);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Resolution> iterator() {
		return this.resolutions.iterator();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return this.resolutions.size();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return this.resolutions.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.resolutions.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return this.resolutions.toArray(a);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		this.resolutions.clear();
	}
}

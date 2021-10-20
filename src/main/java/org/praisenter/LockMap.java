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
package org.praisenter;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * Class used to store a set of locks based on a given key.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> the key type
 */
public final class LockMap<T> {
	/** The key map */
	private final WeakHashMap<KeyWrapper<T>, WeakReference<KeyWrapper<T>>> keys = new WeakHashMap<>();
	
	/**
	 * Returns the lock for the given key or creates a new lock
	 * if one doesn't exist.
	 * @param key the key
	 * @return Object
	 */
	public synchronized Object get(T key) {
		if (key == null) throw new NullPointerException();
		KeyWrapper<T> mKey = new KeyWrapper<T>(key);
		WeakReference<KeyWrapper<T>> value = this.keys.get(mKey);
		KeyWrapper<T> old = value == null ? null : value.get();
		if (old == null) { 
			value = new WeakReference<KeyWrapper<T>>(mKey);
			this.keys.put(mKey, value);
			return mKey;
		}
		return old;
	}
	
	/**
	 * A wrapper class for the keys so that while the wrapper is in
	 * use the key will not be garbage collected.
	 * @author William Bittle
	 * @version 3.0.0
	 * @param <T> the key type
	 */
	private static final class KeyWrapper<T> {
		/** The key */
		private final T key;
		
		/** The hashcode */
		private final int hashCode;
		
		/**
		 * Minimal constructor.
		 * @param key the key
		 */
		public KeyWrapper(T key) {
			this.key = key;
			this.hashCode = key.hashCode();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj == null) return false;
			if (obj instanceof KeyWrapper) {
				return this.key.equals(((KeyWrapper<?>)obj).key);
			}
			return false;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return this.hashCode;
		}
	}
}

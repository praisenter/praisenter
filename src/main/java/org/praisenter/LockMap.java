package org.praisenter;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public final class LockMap<T> {
	private final WeakHashMap<KeyWrapper<T>, WeakReference<KeyWrapper<T>>> keys = new WeakHashMap<>();
	
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
	
	private static final class KeyWrapper<T> {
		private final T key;
		private final int hashCode;
		public KeyWrapper(T key) {
			this.key = key;
			this.hashCode = key.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj == null) return false;
			if (obj instanceof KeyWrapper) {
				return this.key.equals(((KeyWrapper<?>)obj).key);
			}
			return false;
		}
		@Override
		public int hashCode() {
			return this.hashCode;
		}
	}
}

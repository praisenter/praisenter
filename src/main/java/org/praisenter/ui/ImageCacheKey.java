package org.praisenter.ui;

final class ImageCacheKey {
	private final ImageCacheKeyType type;
	private final String key;
	
	public ImageCacheKey(ImageCacheKeyType type, String key) {
		if (type == null || key == null) throw new NullPointerException("The type and key parameters cannot be null.");
		this.type = type;
		this.key = key;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof ImageCacheKey) {
			ImageCacheKey key = (ImageCacheKey)obj;
			return key.key.equals(this.key) && key.type == this.type;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + this.type.hashCode();
		hash = hash * 31 + this.key.hashCode();
		return hash;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(this.type).append("|").append(this.key).append("]");
		return sb.toString();
	}
	
	public ImageCacheKeyType getType() {
		return this.type;
	}
	
	public String getKey() {
		return this.key;
	}
}

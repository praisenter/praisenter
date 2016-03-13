package org.praisenter.javafx.slide;

// TODO translate
public enum PaintType {
	NONE("None"),
	COLOR("Solid Color"),
	GRADIENT("Gradient"),
	IMAGE("Image"),
	VIDEO("Video");
	
	/** The name */
	private String name;
	
	/**
	 * Full constructor.
	 * @param name the readable name
	 */
	private PaintType(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return this.name;
	}
}

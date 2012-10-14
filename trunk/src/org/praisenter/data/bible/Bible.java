package org.praisenter.data.bible;

/**
 * Represents a {@link Bible}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Bible implements Comparable<Bible> {
	/** The bible id */
	protected int id = -1;
	
	/** The name of the bible */
	protected String name;
	
	/** The language the bible is in (non-ISO unfortunately) */
	protected String language;

	/** The source for the bible's contents */
	protected String source;
	
	/** Default constructor */
	protected Bible() {}
	
	/**
	 * Full constructor.
	 * @param id the bible id
	 * @param name the bible name
	 * @param language the bible language
	 * @param source the bible source
	 */
	protected Bible(int id, String name, String language, String source) {
		this.id = id;
		this.name = name;
		this.language = language;
		this.source = source;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Bible) {
			Bible other = (Bible)obj;
			if (this.id == other.id) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Bible[Id=").append(this.id)
		  .append("|Name=").append(this.name)
		  .append("|Language=").append(this.language)
		  .append("|Source=").append(this.source)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Bible o) {
		if (o == null) return 1;
		// sort by id
		return o.id - this.id;
	}
	
	/**
	 * Returns the id for this {@link Bible}.
	 * @return int
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Returns the name of this {@link Bible}.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the language of this {@link Bible}.
	 * <p>
	 * The language code is not the ISO language code.
	 * @return String
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Returns the source for this {@link Bible}'s contents.
	 * @return String
	 */
	public String getSource() {
		return this.source;
	}
}

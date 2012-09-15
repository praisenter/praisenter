package org.praisenter.data.bible;

/**
 * Represents a book in the Bible.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Book implements Comparable<Book> {
	/** The {@link Bible} this {@link Book} came from */
	protected Bible bible;
	
	/** The book code */
	protected String code;
	
	/** The book name */
	protected String name;
	
	/** Default constructor */
	public Book() {}
	
	/**
	 * Full constructor.
	 * @param bible the bible this book came from
	 * @param code the book code (id)
	 * @param name the book name
	 */
	public Book(Bible bible, String code, String name) {
		this.bible = bible;
		this.code = code;
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Book) {
			Book other = (Book)obj;
			// two books are the same if they are from the same bible
			// and they have the same code
			if (this.bible.equals(other.bible) && this.code.equals(other.code)) {
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
		return this.code.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Book[Bible=").append(this.bible.getName())
		  .append("|Code=").append(this.code)
		  .append("|Name=").append(this.name)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Book o) {
		if (o == null) return 1;
		return this.code.compareTo(o.code);
	}
	
	/**
	 * Returns the bible this {@link Book} is contained in.
	 * @return {@link Bible}
	 */
	public Bible getBible() {
		return this.bible;
	}
	
	/**
	 * Returns the book code.
	 * @return String
	 */
	public String getCode() {
		return this.code;
	}
	
	/**
	 * Returns the book name.
	 * @return String
	 */
	public String getName() {
		return name;
	}
}

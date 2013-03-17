/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
	protected Book() {}
	
	/**
	 * Full constructor.
	 * @param bible the bible this book came from
	 * @param code the book code (id)
	 * @param name the book name
	 */
	protected Book(Bible bible, String code, String name) {
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
			if (this.bible.equals(other.bible) && this.code.equals(other.code)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the given book is the same as this book.
	 * <p>
	 * This does a reference comparison so that a book from another
	 * Bible will be the same as this book as long as they are both
	 * "Acts" for example.
	 * @param book the book
	 * @return boolean
	 */
	public boolean isSameBook(Book book) {
		if (book == null) return false;
		if (book == this) return true;
		if (this.code.equals(book.code)) {
			return true;
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
		return this.name;
	}
}

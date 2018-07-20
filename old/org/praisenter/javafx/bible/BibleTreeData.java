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
package org.praisenter.javafx.bible;

import org.praisenter.bible.Bible;

/**
 * Tree data for a bible.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class BibleTreeData extends TreeData {
	/** The bible */
	final Bible bible;
	
	/**
	 * Minimal constructor.
	 * @param bible the bible
	 */
	public BibleTreeData(Bible bible) {
		this.bible = bible;
		this.label.set(bible.getName());
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(TreeData)
	 */
	@Override
	public int compareTo(TreeData o) {
		if (o != null && o instanceof BibleTreeData) {
			BibleTreeData ctd = (BibleTreeData)o;
			return this.bible.compareTo(ctd.bible);
		}
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.bible.TreeData#update()
	 */
	@Override
	public void update() {
		this.label.set(this.bible.getName());
	}

	/**
	 * Returns the bible.
	 * @return {@link Bible}
	 */
	public Bible getBible() {
		return this.bible;
	}
}

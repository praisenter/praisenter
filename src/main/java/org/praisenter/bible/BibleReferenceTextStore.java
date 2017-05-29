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
package org.praisenter.bible;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.TextItem;
import org.praisenter.TextStore;
import org.praisenter.TextType;
import org.praisenter.TextVariant;

/**
 * A {@link TextStore} specifically for bible verses.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "bibleReferenceTextStore")
@XmlAccessorType(XmlAccessType.NONE)
public final class BibleReferenceTextStore implements TextStore {
	/** The bible verse data */
	@XmlElement(name = "dataVariant", required = false)
	@XmlElementWrapper(name = "data", required = false)
	private final Map<TextVariant, BibleReferenceSet> data;
	
	/**
	 * Creates an new empty store.
	 */
	public BibleReferenceTextStore() {
		this.data = new HashMap<TextVariant, BibleReferenceSet>();
		for (TextVariant variant : TextVariant.values()) {
			this.data.put(variant, new BibleReferenceSet());
		}
	}
	
	/**
	 * Copy constructor.
	 * @param store the store to copy
	 */
	public BibleReferenceTextStore(BibleReferenceTextStore store) {
		this();
		for (TextVariant variant : store.data.keySet()) {
			this.data.put(variant, store.data.get(variant).copy());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.TextStore#get(org.praisenter.TextVariant)
	 */
	@Override
	public Map<TextType, TextItem> get(TextVariant variant) {
		Map<TextType, TextItem> data = new HashMap<TextType, TextItem>();
		BibleReferenceSet brs = this.data.get(variant);
		if (brs != null) {
			for (TextType type : TextType.values()) {
				data.put(type, new TextItem(brs.getText(type)));
			}
		}
		return data;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.TextStore#get(org.praisenter.TextVariant, org.praisenter.TextType)
	 */
	@Override
	public TextItem get(TextVariant variant, TextType type) {
		BibleReferenceSet brs = this.data.get(variant);
		if (brs != null) {
			return new TextItem(brs.getText(type));
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.TextStore#copy()
	 */
	@Override
	public BibleReferenceTextStore copy() {
		return new BibleReferenceTextStore(this);
	}
	
	/**
	 * Returns the {@link BibleReferenceSet} for the given variant.
	 * @param variant the variant
	 * @return {@link BibleReferenceSet}
	 */
	public BibleReferenceSet getVariant(TextVariant variant) {
		return this.data.get(variant);
	}
	
	/**
	 * Sets the {@link BibleReferenceSet} for the given variant.
	 * @param variant the variant
	 * @param data the reference set
	 */
	public void setVariant(TextVariant variant, BibleReferenceSet data) {
		this.data.put(variant, data);
	}
	
	/**
	 * Clear's this text store.
	 */
	public void clear() {
		for (BibleReferenceSet set : this.data.values()) {
			set.clear();
		}
	}
}

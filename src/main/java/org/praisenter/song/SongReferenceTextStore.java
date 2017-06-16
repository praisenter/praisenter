package org.praisenter.song;

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

// this should contain all the information required to display a single verse on
// a slide (both musician and main) and any variants/types associated

@XmlRootElement(name = "songReferenceTextStore")
@XmlAccessorType(XmlAccessType.NONE)
public final class SongReferenceTextStore implements TextStore {
	/** The bible verse data */
	@XmlElement(name = "dataVariant", required = false)
	@XmlElementWrapper(name = "data", required = false)
	private final Map<TextVariant, SongReferenceVerse> data;
	
	public SongReferenceTextStore() {
		this.data = new HashMap<TextVariant, SongReferenceVerse>();
	}
	
	public SongReferenceTextStore(SongReferenceTextStore store) {
		this();
		for (TextVariant variant : store.data.keySet()) {
			this.data.put(variant, store.data.get(variant));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.TextStore#copy()
	 */
	@Override
	public TextStore copy() {
		return new SongReferenceTextStore(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.TextStore#get(org.praisenter.TextVariant)
	 */
	@Override
	public Map<TextType, TextItem> get(TextVariant variant) {
		Map<TextType, TextItem> data = new HashMap<TextType, TextItem>();
		SongReferenceVerse srv = this.data.get(variant);
		if (srv != null) {
			for (TextType type : TextType.values()) {
				data.put(type, new TextItem(srv.getText(type)));
			}
		}
		return data;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.TextStore#get(org.praisenter.TextVariant, org.praisenter.TextType)
	 */
	@Override
	public TextItem get(TextVariant variant, TextType type) {
		SongReferenceVerse srv = this.data.get(variant);
		if (srv != null) {
			return new TextItem(srv.getText(type));
		}
		return null;
	}

	/**
	 * Returns the {@link SongReferenceVerse} for the given variant.
	 * @param variant the variant
	 * @return {@link SongReferenceVerse}
	 */
	public SongReferenceVerse getVariant(TextVariant variant) {
		return this.data.get(variant);
	}
	
	/**
	 * Sets the {@link SongReferenceVerse} for the given variant.
	 * @param variant the variant
	 * @param data the reference set
	 */
	public void setVariant(TextVariant variant, SongReferenceVerse data) {
		this.data.put(variant, data);
	}
	
	/**
	 * Clear's this text store.
	 */
	public void clear() {
		this.data.clear();
	}
}

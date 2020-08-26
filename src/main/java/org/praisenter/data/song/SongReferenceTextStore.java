package org.praisenter.data.song;

import java.util.HashMap;
import java.util.Map;

import org.praisenter.data.TextItem;
import org.praisenter.data.TextStore;
import org.praisenter.data.TextType;
import org.praisenter.data.TextVariant;

import com.fasterxml.jackson.annotation.JsonProperty;

// this should contain all the information required to display a single verse on
// a slide (both musician and main) and any variants/types associated

public final class SongReferenceTextStore implements TextStore {
	/** The bible verse data */
	@JsonProperty
	private final Map<TextVariant, SongReferenceVerse> variants;
	
	public SongReferenceTextStore() {
		this.variants = new HashMap<TextVariant, SongReferenceVerse>();
	}
	
	public SongReferenceTextStore(SongReferenceTextStore store) {
		this();
		for (TextVariant variant : store.variants.keySet()) {
			this.variants.put(variant, store.variants.get(variant));
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
		SongReferenceVerse srv = this.variants.get(variant);
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
		SongReferenceVerse srv = this.variants.get(variant);
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
		return this.variants.get(variant);
	}
	
	/**
	 * Sets the {@link SongReferenceVerse} for the given variant.
	 * @param variant the variant
	 * @param data the reference set
	 */
	public void setVariant(TextVariant variant, SongReferenceVerse data) {
		this.variants.put(variant, data);
	}
	
	/**
	 * Clear's this text store.
	 */
	public void clear() {
		this.variants.clear();
	}
}

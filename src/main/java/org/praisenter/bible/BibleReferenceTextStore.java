package org.praisenter.bible;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.TextStore;
import org.praisenter.TextType;
import org.praisenter.TextVariant;

@XmlRootElement(name = "bibleReferenceTextStore")
@XmlAccessorType(XmlAccessType.NONE)
public class BibleReferenceTextStore implements TextStore {
	@XmlElement(name = "dataVariant", required = false)
	@XmlElementWrapper(name = "data", required = false)
	private final Map<TextVariant, BibleReferenceSet> data;
	
	public BibleReferenceTextStore() {
		this.data = new HashMap<TextVariant, BibleReferenceSet>();
		for (TextVariant variant : TextVariant.values()) {
			this.data.put(variant, new BibleReferenceSet());
		}
	}
	
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
	public Map<TextType, String> get(TextVariant variant) {
		Map<TextType, String> data = new HashMap<TextType, String>();
		BibleReferenceSet brs = this.data.get(variant);
		if (brs != null) {
			for (TextType type : TextType.values()) {
				data.put(type, brs.getText(type));
			}
		}
		return data;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.TextStore#get(org.praisenter.TextVariant, org.praisenter.TextType)
	 */
	@Override
	public String get(TextVariant variant, TextType type) {
		BibleReferenceSet brs = this.data.get(variant);
		if (brs != null) {
			return brs.getText(type);
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
	
	public BibleReferenceSet getVariant(TextVariant variant) {
		return this.data.get(variant);
	}
	
	public void setVariant(TextVariant variant, BibleReferenceSet data) {
		this.data.put(variant, data);
	}
	
	public void clear() {
		for (BibleReferenceSet set : this.data.values()) {
			set.clear();
		}
	}
}

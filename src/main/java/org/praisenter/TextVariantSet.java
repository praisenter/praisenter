package org.praisenter;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.bible.BibleReferenceSet;

@XmlRootElement(name = "textVariantSet")
@XmlAccessorType(XmlAccessType.NONE)
public final class TextVariantSet<T extends TextTypeSet> {
	@XmlElement(name = "variant", required = false)
	@XmlElementWrapper(name = "variants", required = false)
	private final Map<TextVariant, T> variants;
	
	public TextVariantSet() {
		this.variants = new HashMap<TextVariant, T>();
	}
	
	public TextVariantSet(TextVariantSet other) {
		this.variants = new HashMap<TextVariant, T>();
		for (TextVariant variant : this.variants.keySet()) {
			this.variants.put(variant, (T)this.variants.get(variant).copy());
		}
	}
	
	public T set(TextVariant variant, T value) {
		return this.variants.put(variant, value);
	}
	
	public T get(TextVariant variant) {
		return this.variants.get(variant);
	}
	
	public T remove(TextVariant variant) {
		return this.variants.remove(variant);
	}
	
	public boolean isSet(TextVariant variant) {
		return this.variants.containsKey(variant) && this.variants.get(variant) != null;
	}
}

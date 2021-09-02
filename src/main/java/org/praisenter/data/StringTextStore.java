package org.praisenter.data;

import java.util.HashMap;
import java.util.Map;

public final class StringTextStore implements TextStore {
	final String text;
	final Map<TextType, TextItem> items;
	
	public StringTextStore(String text) {
		this.text = text;
		this.items = new HashMap<TextType, TextItem>();
		this.items.put(TextType.TEXT, new TextItem(this.text));
		this.items.put(TextType.TITLE, new TextItem(this.text));
	}
	
	@Override
	public TextStore copy() {
		return new StringTextStore(this.text);
	}
	
	@Override
	public Map<TextType, TextItem> get(TextVariant variant) {
		return this.items;
	}
	
	@Override
	public TextItem get(TextVariant variant, TextType type) {
		return this.items.get(TextType.TEXT);
	}
	
	@Override
	public String toString() {
		return this.text;
	}
}

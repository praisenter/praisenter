package org.praisenter.javafx;

import java.util.Objects;

import org.praisenter.resources.translations.Translations;

public final class FilterOption<T> {
	final String name;
	final T data;
	
	// null/all option
	public FilterOption() {
		this.name = null;
		this.data = null;
	}
	
	public FilterOption(String name, T data) {
		this.name = name;
		this.data = data;
	}
	
	@Override
	public int hashCode() {
		return name == null ? -1 : name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof FilterOption) {
			FilterOption<?> mtf = (FilterOption<?>)obj;
			if (Objects.equals(mtf.name, this.name)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		if (this.name == null) {
			return Translations.getTranslation("media.type.filter.all");
		} else {
			return name;
		}
	}
	
	public T getData() {
		return this.data;
	}
}

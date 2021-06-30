package org.praisenter.ui.controls;

import java.util.function.BiFunction;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public final class AutoCompleteComboBox<T> extends ComboBox<T> {
	public AutoCompleteComboBox(ObservableList<T> items, BiFunction<String, T, Boolean> matcher) {
		super(items);
		
		this.setEditable(true);
		
		this.addEventHandler(KeyEvent.KEY_RELEASED, (e) -> {
			TextField editor = getEditor();
			String text = editor.getText();
			int start = editor.getCaretPosition();
			if (text == null || text.length() == 0) {
				return;
			}
			
			// try to allow some keys
			if (e.getCode() == KeyCode.BACK_SPACE ||
				e.getCode() == KeyCode.DELETE ||
				e.getCode() == KeyCode.SHIFT ||
				e.getCode() == KeyCode.ALT ||
				e.getCode() == KeyCode.CONTROL ||
				e.getCode() == KeyCode.CAPS ||
				e.getCode() == KeyCode.TAB) {
				return;
			}
			for (T item : items) {
				if (matcher.apply(text, item)) {
					String newText = item.toString();
					editor.setText(newText);
					editor.commitValue();
					editor.selectRange(start, newText.length());
					break;
				}
			}
		});
		
		this.setConverter(new StringConverter<T>() {
			@Override
			public T fromString(String string) {
				for (T item : items) {
					if (item.toString().equals(string)) {
						return item;
					}
				}
				return null;
			}
			@Override
			public String toString(T object) {
				if (object != null) {
					return object.toString();
				}
				return null;
			}
		});
	}
}
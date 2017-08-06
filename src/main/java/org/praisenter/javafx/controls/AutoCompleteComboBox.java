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
package org.praisenter.javafx.controls;

import java.util.function.BiFunction;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * Represents a ComboBox with an auto-complete feature.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> the value type
 */
public final class AutoCompleteComboBox<T> extends ComboBox<T> {
	/**
	 * Constructor.
	 * @param items the item list
	 * @param matcher the auto-complete matcher
	 */
	public AutoCompleteComboBox(ObservableList<T> items, BiFunction<String, T, Boolean> matcher) {
		super(items);
		
		this.getStyleClass().add("auto-complete-combobox");
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
				e.getCode() == KeyCode.CAPS) {
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
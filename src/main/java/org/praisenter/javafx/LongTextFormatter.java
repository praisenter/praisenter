package org.praisenter.javafx;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.LongStringConverter;

public class LongTextFormatter extends TextFormatter<Long> {

	public LongTextFormatter() {
		super(new LongStringConverter(), 0l, new UnaryOperator<Change>() {
			@Override
			public Change apply(Change change) {
				String text = change.getText();
				for (int i = 0; i < text.length(); i++) {
                	if (!Character.isDigit(text.charAt(i))) {
                		return null;
                	}
                }
                return change;
			}
		});
	}
}

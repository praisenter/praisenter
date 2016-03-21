package org.praisenter.javafx;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public final class TimeSpanTextFormatter extends TextFormatter<Long> {

	public TimeSpanTextFormatter() {
		super(new StringConverter<Long>() {
			@Override
			public Long fromString(String s) {
				if (s == null) return 0l;
				String[] parts = s.split(":");
				if (parts.length == 2) {
					return Long.parseLong(parts[0]) * 60 + Long.parseLong(parts[1]);
				} else {
					// unknown format
					return 0l;
				}
			}
			@Override
			public String toString(Long l) {
				if (l == null) return "00:00";
				long minutes = l / 60;
				long seconds = l - minutes * 60;
				return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
			}
		},
		0l,
		new UnaryOperator<Change>() {
			@Override
			public Change apply(Change t) {
				String text = t.getText();
				// FIXME need to filter the input
				return t;
			}
		});
	}

}

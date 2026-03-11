package org.praisenter.ui.slide;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.praisenter.data.TextItem;
import org.praisenter.data.TextStore;
import org.praisenter.data.TextType;
import org.praisenter.data.TextVariant;
import org.praisenter.data.bible.BibleReferenceTextStore;

final class RouteBibleQrOverlay {
	private RouteBibleQrOverlay() {}

	static String buildQrUrl(String referenceLabel) {
		String encodedReferenceLabel = URLEncoder.encode(referenceLabel, StandardCharsets.UTF_8);
		return "https://route.bible/qr?passage="
			+ encodedReferenceLabel
			+ "&format=png&size=256&utm_source=praisenter&utm_medium=qr&mode=launcher";
	}

	static String getReferenceLabel(TextStore data) {
		if (!(data instanceof BibleReferenceTextStore)) {
			return null;
		}

		TextItem title = data.get(TextVariant.PRIMARY, TextType.TITLE);
		if (title == null || title.getText() == null || title.getText().isBlank()) {
			return null;
		}

		return title.getText();
	}

	static boolean shouldShow(boolean enabled, TextStore data, SlideMode mode) {
		return enabled && mode == SlideMode.PRESENT && getReferenceLabel(data) != null;
	}
}

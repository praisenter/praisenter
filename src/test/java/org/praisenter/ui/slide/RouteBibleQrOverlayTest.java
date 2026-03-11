package org.praisenter.ui.slide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.praisenter.data.StringTextStore;
import org.praisenter.data.TextVariant;
import org.praisenter.data.bible.BibleReferenceTextStore;
import org.praisenter.data.bible.BibleReferenceVerse;

class RouteBibleQrOverlayTest {
	@Test
	void buildsTheExpectedQrUrl() {
		assertEquals(
			"https://route.bible/qr?passage=Psalm+23&format=png&size=256&utm_source=praisenter&utm_medium=qr&mode=launcher",
			RouteBibleQrOverlay.buildQrUrl("Psalm 23")
		);
	}

	@Test
	void showsTheOverlayOnlyForBiblePresentationContentWhenEnabled() {
		assertTrue(
			RouteBibleQrOverlay.shouldShow(true, createBibleData("John 3:16"), SlideMode.PRESENT)
		);
		assertFalse(
			RouteBibleQrOverlay.shouldShow(false, createBibleData("John 3:16"), SlideMode.PRESENT)
		);
		assertFalse(
			RouteBibleQrOverlay.shouldShow(true, createBibleData("John 3:16"), SlideMode.VIEW)
		);
		assertFalse(
			RouteBibleQrOverlay.shouldShow(true, new StringTextStore("Song verse"), SlideMode.PRESENT)
		);
	}

	@Test
	void extractsTheDisplayedBibleReferenceLabel() {
		assertEquals("1 John 4:7", RouteBibleQrOverlay.getReferenceLabel(createBibleData("1 John 4:7")));
	}

	private static BibleReferenceTextStore createBibleData(String referenceLabel) {
		String[] parts = referenceLabel.split(" ");
		String bookName = String.join(" ", java.util.Arrays.copyOf(parts, parts.length - 1));
		String[] chapterVerse = parts[parts.length - 1].split(":");
		BibleReferenceTextStore store = new BibleReferenceTextStore();
		store.getVariant(TextVariant.PRIMARY).getReferenceVerses().add(
			new BibleReferenceVerse(
				UUID.randomUUID(),
				"KJV",
				bookName,
				19,
				Integer.parseInt(chapterVerse[0]),
				Integer.parseInt(chapterVerse[1]),
				"The Lord is my shepherd"
			)
		);
		return store;
	}
}

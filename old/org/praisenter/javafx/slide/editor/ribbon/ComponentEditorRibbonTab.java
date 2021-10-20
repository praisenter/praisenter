package org.praisenter.javafx.slide.editor.ribbon;

import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.editor.SlideEditorContext;

abstract class ComponentEditorRibbonTab extends SlideRegionRibbonTab<ObservableSlideRegion<?>> {
	public ComponentEditorRibbonTab(SlideEditorContext context, String name) {
		super(context, name);
	}
}

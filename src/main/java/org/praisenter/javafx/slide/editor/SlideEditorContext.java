package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.command.EditManager;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class SlideEditorContext {
	private final PraisenterContext praisenterContext;
	private final EditManager manager = new EditManager();
	
	private final ObjectProperty<ObservableSlide<?>> slide = new SimpleObjectProperty<>();
	private final ObjectProperty<ObservableSlideRegion<?>> selected = new SimpleObjectProperty<>();

	
	public SlideEditorContext(PraisenterContext context) {
		this.praisenterContext = context;
	}
	
	// getters
	
	public PraisenterContext getPraisenterContext() {
		return this.praisenterContext;
	}
	
	public EditManager getEditManager() {
		return this.manager;
	}
	
	// the slide
	
	public final ObjectProperty<ObservableSlide<?>> slideProperty() {
		return this.slide;
	}
	
	public final ObservableSlide<?> getSlide() {
		return this.slideProperty().get();
	}
	
	public final void setSlide(final ObservableSlide<?> slide) {
		this.slideProperty().set(slide);
	}
	
	// selected
	
	public final ObjectProperty<ObservableSlideRegion<?>> selectedProperty() {
		return this.selected;
	}
	
	public final ObservableSlideRegion<?> getSelected() {
		return this.selectedProperty().get();
	}
	
	public final void setSelected(final ObservableSlideRegion<?> selected) {
		this.selectedProperty().set(selected);
	}
}

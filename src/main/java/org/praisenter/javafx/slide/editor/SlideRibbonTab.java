package org.praisenter.javafx.slide.editor;

import java.util.Comparator;

import org.praisenter.Tag;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.TagEvent;
import org.praisenter.javafx.TagListView;
import org.praisenter.javafx.configuration.Resolution;
import org.praisenter.javafx.configuration.Resolutions;
import org.praisenter.javafx.configuration.Setting;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

final class SlideRibbonTab extends EditorRibbonTab<ObservableSlide<?>> {
	private final PraisenterContext context;
	
	private final TextField name;
	private final TextField time;
	private final ComboBox<Resolution> cmbResolutions;
	private final TagListView lstTags;
	
	public SlideRibbonTab(PraisenterContext context) {
		super("Slide");
		
		this.context = context;
		
		name = new TextField();
		name.setPromptText("Name");
		
		time = new TextField();
		time.setPromptText("00:00");
		
		// target resolution
		SortedList<Resolution> sorted = context.getConfiguration().getResolutions().sorted((a, b) -> {
			return a.compareTo(b);
		});
		cmbResolutions = new ComboBox<Resolution>(sorted);
		
		Button btnNewResolution = new Button("Add");
		
		lstTags = new TagListView(context.getTags());
		
		// layout
		
		HBox row1 = new HBox(2, this.name);
		HBox row2 = new HBox(2, this.time);
		HBox row3 = new HBox(2, this.cmbResolutions, btnNewResolution);
		VBox layout = new VBox(2, row1, row2, row3);
		this.container.setCenter(layout);
	
		// events
		
		this.component.addListener((obs, ov, nv) -> {
			mutating = true;
			ObservableSlideRegion<?> comp = this.component.get();
			if (comp != null && comp instanceof ObservableSlide) {
				ObservableSlide<?> slide = (ObservableSlide<?>)comp;
				this.name.setText(slide.getName());
//				this.time.setText(slide.getTime());
				this.cmbResolutions.setValue(new Resolution((int)slide.getWidth(), (int)slide.getHeight()));
			}
			mutating = false;
		});
		
		name.textProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> comp = this.component.get();
			if (comp != null && comp instanceof ObservableSlide) {
				ObservableSlide<?> slide = (ObservableSlide<?>)comp;
				slide.setName(nv);
			}
		});
		
		cmbResolutions.valueProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> comp = this.component.get();
			if (comp != null && comp instanceof ObservableSlide) {
				ObservableSlide<?> slide = (ObservableSlide<?>)comp;
				// when this changes we need to adjust all the sizes of the controls in the slide
				slide.fit(nv.getWidth(), nv.getHeight());
				// then we need to update all the Java FX nodes
				fireEvent(new SlideTargetResolutionEvent(this.cmbResolutions, SlideRibbonTab.this, slide, nv));
			}
		});
		
		btnNewResolution.setOnAction((e) -> {
			// TODO fix
			Resolution res = new Resolution(2000, 4000);
			Resolutions resolutions = context.getConfiguration().getObject(Setting.DISPLAY_RESOLUTIONS, null);
			resolutions.add(res);
			context.getConfiguration()
				.setObject(Setting.DISPLAY_RESOLUTIONS, resolutions)
				.execute(context.getExecutorService());
			
			cmbResolutions.setValue(res);
		});
		
		lstTags.addEventHandler(TagEvent.ALL, new EventHandler<TagEvent>() {
			@Override
			public void handle(TagEvent event) {
				Tag tag = event.getTag();
				ObservableSlideRegion<?> comp = component.get();
				if (comp != null && comp instanceof ObservableSlide) {
					ObservableSlide<?> slide = (ObservableSlide<?>)comp;
					if (event.getEventType() == TagEvent.ADDED) {
						slide.addTag(tag);
					} else if (event.getEventType() == TagEvent.REMOVED) {
						slide.removeTag(tag);
					}
				}
			}
        });
	}
}

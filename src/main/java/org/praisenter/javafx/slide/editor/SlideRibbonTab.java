package org.praisenter.javafx.slide.editor;

import org.controlsfx.control.PopOver;
import org.praisenter.Tag;
import org.praisenter.javafx.ApplicationGlyphs;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.TagEvent;
import org.praisenter.javafx.TagListView;
import org.praisenter.javafx.configuration.Resolution;
import org.praisenter.javafx.configuration.ResolutionSet;
import org.praisenter.javafx.configuration.Setting;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

final class SlideRibbonTab extends EditorRibbonTab<ObservableSlide<?>> {
	private final PraisenterContext context;
	
	private final TextField name;
	private final TextField time;
	private final ComboBox<Resolution> cmbResolutions;
	private final TagListView lstTags;
	
	private final PopOver popAddResolution;
	private final Spinner<Integer> spnWidth;
	private final Spinner<Integer> spnHeight;
	
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
		
		Button btnNewResolution = new Button("", ApplicationGlyphs.ADD.duplicate());
		Button btnRemoveResolution = new Button("", ApplicationGlyphs.REMOVE.duplicate());
		
		btnNewResolution.setTooltip(new Tooltip("Add a new resolution target."));
		btnRemoveResolution.setTooltip(new Tooltip("Remove the selected resolution target."));
		
		lstTags = new TagListView(context.getTags());
		
		this.spnWidth = new Spinner<Integer>(100, Integer.MAX_VALUE, 1280, 100);
		this.spnHeight = new Spinner<Integer>(100, Integer.MAX_VALUE, 1024, 100);
		this.spnWidth.setEditable(true);
		this.spnHeight.setEditable(true);
		
		Button btnAdd = new Button("Add");
		Button btnCancel = new Button("Cancel");
		GridPane pneResolution = new GridPane();
		pneResolution.setVgap(5);
		pneResolution.setHgap(5);
		pneResolution.setPadding(new Insets(10));
		pneResolution.add(new Label("Width"), 0, 0);
		pneResolution.add(this.spnWidth, 1, 0);
		pneResolution.add(new Label("Height"), 0, 1);
		pneResolution.add(this.spnHeight, 1, 1);
		pneResolution.add(btnAdd, 0, 2);
		pneResolution.add(btnCancel, 1, 2);
		
		popAddResolution = new PopOver(pneResolution);
		
		// layout
		
		HBox row1 = new HBox(2, this.name);
		HBox row2 = new HBox(2, this.time);
		HBox row3 = new HBox(2, this.cmbResolutions, btnNewResolution, btnRemoveResolution);
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
				notifyComponentChanged();
			}
		});
		
		cmbResolutions.valueProperty().addListener((obs, ov, nv) -> {
			if (mutating) return;
			ObservableSlideRegion<?> comp = this.component.get();
			if (comp != null && comp instanceof ObservableSlide && nv != null) {
				ObservableSlide<?> slide = (ObservableSlide<?>)comp;
				// when this changes we need to adjust all the sizes of the controls in the slide
				slide.fit(nv.getWidth(), nv.getHeight());
				// then we need to update all the Java FX nodes
				fireEvent(new SlideTargetResolutionEvent(this.cmbResolutions, SlideRibbonTab.this, slide, nv));
				notifyComponentChanged();
			}
		});
		
		btnNewResolution.setOnAction((e) -> {
			Resolution r = cmbResolutions.getValue();
			if (r != null) {
				spnWidth.getValueFactory().setValue(r.getWidth());
				spnHeight.getValueFactory().setValue(r.getHeight());
			}
			popAddResolution.show(btnNewResolution);
		});
		
		btnAdd.setOnAction(e -> {
			int w = spnWidth.getValue();
			int h = spnHeight.getValue();
			Resolution r = new Resolution(w, h);
			ResolutionSet resolutions = context.getConfiguration().getObject(Setting.DISPLAY_RESOLUTIONS, ResolutionSet.class, new ResolutionSet());
			resolutions.add(r);
			context.getConfiguration()
				.setObject(Setting.DISPLAY_RESOLUTIONS, resolutions)
				.execute(context.getExecutorService());
			
			cmbResolutions.setValue(r);
			popAddResolution.hide();
		});
		
		btnCancel.setOnAction(e -> {
			popAddResolution.hide();
		});
		
		btnRemoveResolution.setOnAction(e -> {
			Resolution r = cmbResolutions.getValue();
			if (r != null) {
				ResolutionSet resolutions = context.getConfiguration().getObject(Setting.DISPLAY_RESOLUTIONS, ResolutionSet.class, new ResolutionSet());
				Resolution other = resolutions.getClosestResolution(r);
				if (resolutions.remove(r)) {
					context.getConfiguration()
						.setObject(Setting.DISPLAY_RESOLUTIONS, resolutions)
						.execute(context.getExecutorService());
				
					cmbResolutions.setValue(other);
				}
			}
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
	
	public void setName(String name) {
		this.name.setText(name);
	}
	
}

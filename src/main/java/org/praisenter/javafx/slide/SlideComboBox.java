package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.Slide;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

public class SlideComboBox extends Pane implements Callback<ListView<SlideListItem>, ListCell<SlideListItem>> {
	private final PraisenterContext context;
	
	private final ComboBox<SlideListItem> cmbSlides;
	private boolean mutating = false;
	private final ObjectProperty<Slide> selected = new SimpleObjectProperty<Slide>();
	
	public SlideComboBox(PraisenterContext context) {
		this.cmbSlides = new ComboBox<SlideListItem>();
		
		ObservableList<SlideListItem> theList = context.getSlideLibrary().getItems();
        FilteredList<SlideListItem> filtered = new FilteredList<SlideListItem>(theList, 
        		p -> p.isLoaded() && p.getSlide() != null && p.getSlide().hasPlaceholders());
        SortedList<SlideListItem> sorted = new SortedList<SlideListItem>(filtered);
        this.cmbSlides.setItems(sorted);
        
		this.context = context;
		
		this.cmbSlides.setCellFactory(this);
		this.cmbSlides.setButtonCell(new ListCell<SlideListItem>() {
			@Override
			protected void updateItem(SlideListItem item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
				} else {
					setText(item.getName());
				}
			}
		});
		
		this.getChildren().add(this.cmbSlides);
		
		this.selected.addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			this.setSlide(nv);
		});
		
		this.cmbSlides.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null && nv.getSlide() != null) {
				this.mutating = true;
				this.selected.set(nv.getSlide());
				this.mutating = false;
			}
		});
	}
	
	@Override
	public ListCell<SlideListItem> call(ListView<SlideListItem> param) {
		return new ListCell<SlideListItem>() {
			private final ImageView graphic;
			
			{
				graphic = new ImageView();
				graphic.setFitWidth(100);
			}
			
			@Override
			protected void updateItem(SlideListItem item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					this.graphic.setImage(null);
					setText(null);
				} else {
					this.graphic.setImage(SwingFXUtils.toFXImage(item.getSlide().getThumbnail(), null));
					setGraphic(this.graphic);
					setText(item.getName());
				}
			}
		};
	}
	
	public Slide getSlide() {
		return this.selected.get();
	}
	
	public void setSlide(Slide slide) {
		if (slide == null) {
			this.cmbSlides.setValue(null);
			return;
		}
		
		SlideListItem sli = null;
		for (SlideListItem item : this.cmbSlides.getItems()) {
			if (item.isLoaded() && item.getSlide() != null && item.getSlide().getId().equals(slide.getId())) {
				sli = item;
				break;
			}
		}
		if (sli != null) {
			this.cmbSlides.setValue(sli);
		}
	}
	
	public ObjectProperty<Slide> slideProperty() {
		return this.selected;
	}
}

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
package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.Fx;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

/**
 * A combobox that shows a list of slides that contain placeholders.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PlaceholderSlideComboBox extends Pane implements Callback<ListView<SlideListItem>, ListCell<SlideListItem>> {
	/** The value */
	private final ObjectProperty<Slide> value = new SimpleObjectProperty<Slide>();
	
	// nodes
	
	/** The combobox of slides */
	private final ComboBox<SlideListItem> cmbSlides;
	
	// state
	
	/** Whether the property is being updated */
	private boolean mutating = false;
	
	/**
	 * Minimal constructor.
	 * @param context the praisenter context
	 */
	public PlaceholderSlideComboBox(PraisenterContext context) {
		this.cmbSlides = new ComboBox<SlideListItem>();
		
		ObservableList<SlideListItem> theList = context.getSlideLibrary().getItems();
        FilteredList<SlideListItem> filtered = theList.filtered(p -> {
        	return  p.isLoaded() && 
        			p.getSlide() != null && 
        			p.getSlide().hasPlaceholders();
        });
        SortedList<SlideListItem> sorted = filtered.sorted((a, b) -> {
        	if (a == b) return 0;
        	if (a == null && b != null) return 1;
        	if (a != null && b == null) return -1;
        	return a.compareTo(b);
        });
        this.cmbSlides.setItems(sorted);
        
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
		
		this.value.addListener((obs, ov, nv) -> {
			if (this.mutating) return;
			this.setSlide(nv);
		});
		
		this.cmbSlides.valueProperty().addListener((obs, ov, nv) -> {
			if (nv != null && nv.getSlide() != null) {
				this.mutating = true;
				this.value.set(nv.getSlide());
				this.mutating = false;
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see javafx.util.Callback#call(java.lang.Object)
	 */
	@Override
	public ListCell<SlideListItem> call(ListView<SlideListItem> param) {
		return new ListCell<SlideListItem>() {
			private final ImageView graphic;
			private final Pane pane;
			
			{
				pane = new Pane();
				pane.setBackground(new Background(new BackgroundImage(Fx.TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
				graphic = new ImageView();
				graphic.setFitWidth(100);
				pane.getChildren().add(graphic);
				setGraphic(pane);
			}
			
			@Override
			protected void updateItem(SlideListItem item, boolean empty) {
				super.updateItem(item, empty);
				
				if (item == null || empty) {
					this.graphic.setImage(null);
					setText(null);
				} else {
					this.graphic.setImage(SwingFXUtils.toFXImage(item.getSlide().getThumbnail(), null));
					setText(item.getName());
				}
			}
		};
	}
	
	// value
	
	/**
	 * Returns the current value or null.
	 * @return {@link Slide}
	 */
	public Slide getValue() {
		return this.value.get();
	}
	
	/**
	 * Sets the current value or null to clear.
	 * @param slide the slide
	 */
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
	
	/**
	 * Returns the value property.
	 * @return ObjectProperty&lt;{@link Slide}&gt;
	 */
	public ObjectProperty<Slide> valueProperty() {
		return this.value;
	}
}

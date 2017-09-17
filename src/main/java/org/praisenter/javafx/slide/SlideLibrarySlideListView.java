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

import java.util.function.BiConsumer;

import org.praisenter.javafx.MappedList;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideAssignment;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;

/**
 * A combobox that shows a list of slides that contain placeholders.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideLibrarySlideListView extends ListView<Slide> {
	protected final PraisenterContext context;

	protected final ObjectProperty<BiConsumer<MouseEvent, Slide>> onCellClick = new SimpleObjectProperty<>();
	
	/**
	 * Minimal constructor.
	 * @param context the praisenter context
	 */
	public SlideLibrarySlideListView(PraisenterContext context) {
		this.context = context;
		
		this.getStyleClass().add("slide-library-slide-list-view");
		
		ObservableList<Slide> items = this.createList();
        
		this.setItems(items);
		this.setCellFactory((view) -> {
			SlideListCell cell = new SlideListCell();
			
			cell.prefWidthProperty().bind(this.widthProperty().subtract(2));
			
			// cell click
			cell.setOnMouseClicked(e -> {
				BiConsumer<MouseEvent, Slide> func = this.onCellClick.get();
				if (func != null) {
					func.accept(e, cell.getItem());
				}
			});
						
			return cell;
		});
		this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}
	
	/**
	 * Creates the source list for the list view.
	 * @return ObservableList&lt;{@link SlideListItem}&gt;
	 */
	private ObservableList<Slide> createList() {
		ObservableList<SlideListItem> items = this.context.getSlideLibrary().getItems();
		
        FilteredList<SlideListItem> filtered = items.filtered(p -> {
        	return  p.isLoaded() && 
        			p.getSlide() != null;
        });
        
        SortedList<SlideListItem> sorted = filtered.sorted((a, b) -> {
        	if (a == b) return 0;
        	if (a == null && b != null) return 1;
        	if (a != null && b == null) return -1;
        	return a.compareTo(b);
        });
        
        return new MappedList<>(sorted, (i, s) -> {
        	return s.getSlide();
        });
	}

	public void setOnCellClick(BiConsumer<MouseEvent, Slide> handler) {
		this.onCellClick.set(handler);
	}
	
	public BiConsumer<MouseEvent, Slide> getOnCellClick() {
		return this.onCellClick.get();
	}
	
	public ObjectProperty<BiConsumer<MouseEvent, Slide>> onCellClickProperty() {
		return this.onCellClick;
	}
}

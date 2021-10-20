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

import org.praisenter.javafx.MappedList;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.Slide;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

/**
 * A combobox that shows a list of slides that contain placeholders.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PlaceholderSlideComboBox extends ComboBox<Slide> {
	/**
	 * Minimal constructor.
	 * @param context the praisenter context
	 */
	public PlaceholderSlideComboBox(PraisenterContext context) {
		this.getStyleClass().add("placeholder-slide-combobox");
		
		ObservableList<SlideListItem> theList = context.getSlideLibrary().getSlideItems();
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
        this.setItems(new MappedList<>(sorted, (i, s) -> {
        	return s.getSlide();
        }));
        
        // JAVABUG (L) 09/16/17 [workaround] The combobox's drop down goes off screen - I've mitigated by reducing the number of items visible at one time
        this.setVisibleRowCount(6);
		this.setCellFactory((view) -> {
			return new SlideListCell(context);
		});
		this.setButtonCell(new ListCell<Slide>() {
			@Override
			protected void updateItem(Slide item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
				} else {
					setText(item.getName());
				}
			}
		});
	}
}

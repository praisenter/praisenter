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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.praisenter.javafx.DataFormats;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.SlideAssignment;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 * A combobox that shows a list of slides that contain placeholders.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideShowSlideListView extends ListView<SlideAssignment> {
	protected final PraisenterContext context;
	
	protected final ObjectProperty<ObservableSlideShow> value = new SimpleObjectProperty<ObservableSlideShow>();
	
	protected final ObjectProperty<BiConsumer<MouseEvent, SlideAssignment>> onCellClick = new SimpleObjectProperty<>();
	
	/** A special data format for this instance of the list only */
	private final DataFormat REORDER = DataFormats.getUniqueFormat();
	
	/**
	 * Minimal constructor.
	 * @param context the praisenter context
	 */
	public SlideShowSlideListView(PraisenterContext context) {
		this.context = context;
		
		this.getStyleClass().add("slide-show-slide-list-view");
		
		this.setCellFactory((view) -> {
			SlideAssignmentListCell cell = new SlideAssignmentListCell(context);
			
			// cell text wrapping
			
			cell.prefWidthProperty().bind(this.widthProperty().subtract(2));
			
			// cell click
			cell.setOnMouseClicked(e -> {
				BiConsumer<MouseEvent, SlideAssignment> func = this.onCellClick.get();
				if (func != null) {
					func.accept(e, cell.getItem());
				}
			});
			
			// DRAG n' DROP reordering
			
			cell.setOnDragDetected(e -> {
				List<SlideAssignment> selections = new ArrayList<SlideAssignment>(getSelectionModel().getSelectedItems());
				if (cell.getItem() == null) return;
				Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent cc = new ClipboardContent();
				cc.put(REORDER, selections);
				db.setDragView(cell.snapshot(null, null));
				db.setContent(cc);
			});
			cell.setOnDragOver(e -> {
				if (e.getGestureSource() != cell && e.getDragboard().hasContent(REORDER)) {
					e.acceptTransferModes(TransferMode.MOVE);
				}
			});
			cell.setOnDragEntered(e -> {
				if (e.getGestureSource() != cell && e.getDragboard().hasContent(REORDER)) {
					cell.setOpacity(0.5);
				}
			});
			cell.setOnDragExited(e -> {
				if (e.getGestureSource() != cell && e.getDragboard().hasContent(REORDER)) {
					cell.setOpacity(1);
				}
			});
			cell.setOnDragDropped(e -> {
				SlideAssignment target = cell.getItem();
				if (e.getGestureSource() != cell && e.getDragboard().hasContent(REORDER)) {
					List<SlideAssignment> moving = (List<SlideAssignment>)e.getDragboard().getContent(REORDER);
					ObservableList<SlideAssignment> assignments = value.get().getSlides();
					
					if (moving.isEmpty()) return;
					
					Map<UUID, SlideAssignment> mapping = moving.stream().collect(Collectors.toMap(sa -> sa.getId(), sa -> sa));
					assignments.removeIf(sa -> {
						return mapping.containsKey(sa.getId());
					});
					
					int index = -1;
					if (target != null) {
						index = assignments.indexOf(target);
					}
					
					if (index >= 0) {
						assignments.addAll(index, moving);
					} else {
						assignments.addAll(moving);
					}
					e.setDropCompleted(true);
				}
			});
			return cell;
		});
		this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.setPlaceholder(new Label("Select slides from the left"));
		
		this.value.addListener((obs, ov, nv) -> {
			if (nv != null) {
				this.setItems(nv.getSlides());
			}
		});
		
		context.getSlideLibrary().getSlideItems().addListener((ListChangeListener.Change<? extends SlideListItem> change) -> {
			ObservableSlideShow show = value.get();
			while (change.next()) {
				if (change.wasRemoved() && show != null) {
					for (SlideListItem removed : change.getRemoved()) {
						show.getSlides().removeIf(s -> s.getSlideId().equals(removed.getSlide().getId()));
					}
				}
			}
		});
	}
	
	public void setOnCellClick(BiConsumer<MouseEvent, SlideAssignment> handler) {
		this.onCellClick.set(handler);
	}
	
	public BiConsumer<MouseEvent, SlideAssignment> getOnCellClick() {
		return this.onCellClick.get();
	}
	
	public ObjectProperty<BiConsumer<MouseEvent, SlideAssignment>> onCellClickProperty() {
		return this.onCellClick;
	}
	
	public ObservableSlideShow getValue() {
		return this.value.get();
	}
	
	public void setValue(ObservableSlideShow show) {
		this.value.set(show);
	}
	
	public ObjectProperty<ObservableSlideShow> valueProperty() {
		return this.value;
	}
}

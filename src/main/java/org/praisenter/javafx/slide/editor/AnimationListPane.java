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
package org.praisenter.javafx.slide.editor;

import java.text.Collator;
import java.util.Comparator;
import java.util.UUID;
import java.util.function.Consumer;

import org.praisenter.javafx.PreventUndoRedoEventFilter;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.animation.Animations;
import org.praisenter.javafx.slide.editor.commands.AddAnimationEditCommand;
import org.praisenter.javafx.slide.editor.commands.EditAnimationEditCommand;
import org.praisenter.javafx.slide.editor.commands.RemoveAnimationEditCommand;
import org.praisenter.javafx.slide.editor.events.SlideEditorEvent;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.animation.Animation;
import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.animation.Swap;
import org.praisenter.slide.easing.EasingType;
import org.praisenter.slide.easing.Linear;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

// FEATURE (M) Show a timeline-view of the animations for a slide

/**
 * A pane used to list and edit the animations for an {@link ObservableSlide}.
 * @author William Bittle
 * @version 3.0.0
 */
final class AnimationListPane extends BorderPane {
	/** The class to apply to the animated region when an animation is hovered over */
	private static final PseudoClass ANIMATION_HOVERED = PseudoClass.getPseudoClass("animation-hovered");
	
	/** A collator for string comparison for the current locale */
	private static final Collator COLLATOR = Collator.getInstance();
	
	/** The default animation when adding a new one */
	private static final Animation DEFAULT_ANIMATION = new Swap(
			AnimationType.IN, 500, 0, 1, false, new Linear(EasingType.IN));
	
	// context
	
	/** The editor context */
	private final SlideEditorContext context;
	
	// data
	
	/** The list of all animations */
	private final ObservableList<SlideAnimation> animations = FXCollections.observableArrayList();
	
	// nodes
	
	/** The dialog for configuring animations */
	private AnimationPickerDialog dlgAnimationPicker;
	
	/** The list view of all the animations */
	private final ListView<SlideAnimation> lstAnimations;
	
	/**
	 * A pane to show the animations for the selected slide.
	 * @param context the editor context
	 */
	public AnimationListPane(SlideEditorContext context) {
		this.getStyleClass().add("animation-list-pane");
		
		this.context = context;
		
		// sort the animations by their delay
		SortedList<SlideAnimation> ordered = new SortedList<>(this.animations, new Comparator<SlideAnimation>() {
			@Override
			public int compare(SlideAnimation o1, SlideAnimation o2) {
				if (o1 == null) return 1;
				if (o2 == null) return -1;
				
				// sort by the start time first
				long diff = o1.getAnimation().getDelay() - o2.getAnimation().getDelay();
				if (diff < 0) {
					return -1;
				} else if (diff > 0) {
					return 1;
				} else {
					ObservableSlideRegion<?> c1 = context.getSlide().getComponent(o1.getId());
					ObservableSlideRegion<?> c2 = context.getSlide().getComponent(o2.getId());
					
					// are the ids the same?
					if (o1.getId().equals(o2.getId())) {
						// then compare by animation name
						String a1 = Animations.getName(o1.getAnimation());
						String a2 = Animations.getName(o2.getAnimation());
						if (a1 == null) a1 = "";
						if (a2 == null) a2 = "";
						return COLLATOR.compare(a1, a2);
					}
					
					// if ids are not the same, check if either id is for the slide itself
					UUID slideId = context.getSlide().getId();
					if (slideId.equals(o1.getId())) {
						return -1;
					} else if (slideId.equals(o2.getId())) {
						return 1;
					}
					
					// otherwise, neither is the slide, so order by component name
					if (c1 != null && c2 != null) {
						String n1 = c1.getName();
						String n2 = c2.getName();
						if (n1 == null) n1 = "";
						if (n2 == null) n2 = "";
						return COLLATOR.compare(n1, n2);
					} else if (c1 == null) {
						return 1;
					} else if (c2 == null) {
						return -1;
					} else {
						return o1.getId().compareTo(o2.getId());
					}
				}
			}
		});
		
		this.lstAnimations = new ListView<SlideAnimation>(ordered);
		this.lstAnimations.getStyleClass().add("animation-list-pane-list");
		this.lstAnimations.setCellFactory(new Callback<ListView<SlideAnimation>, ListCell<SlideAnimation>>() {
			@Override
			public ListCell<SlideAnimation> call(ListView<SlideAnimation> param) {
				AnimationListCell cell = new AnimationListCell();
				// for text ellipsis
				cell.prefWidthProperty().bind(lstAnimations.widthProperty().subtract(2));
				cell.slideProperty().bind(context.slideProperty());
				cell.setOnMouseClicked(e -> {
					if (e.getClickCount() >= 2) {
						editHandler(null);
					}
				});
				cell.setOnMouseEntered(e -> {
					highlightNodeOnHover(cell.getItem());
				});
				cell.setOnMouseExited(e -> {
					highlightNodeOnHover(null);
				});
				return cell;
			}
		});

		Button btnAddAnimation = new Button(Translations.get("slide.animations.add"));
		Button btnEditAnimation = new Button(Translations.get("slide.animations.edit"));
		Button btnRemoveAnimation = new Button(Translations.get("slide.animations.remove"));
		
		HBox btns = new HBox(btnAddAnimation, btnEditAnimation, btnRemoveAnimation);
		btns.setSpacing(5);
		btns.setAlignment(Pos.BASELINE_CENTER);
		
		Label title = new Label(Translations.get("slide.animations.title"));
		title.getStyleClass().add("animation-list-pane-title");
		title.setMaxWidth(Double.MAX_VALUE);
		
		VBox top = new VBox(title, btns);
		top.getStyleClass().add("animation-list-pane-top");
		
		this.setTop(top);
		this.setCenter(this.lstAnimations);
		
		// events
		
		btnAddAnimation.setOnAction(this::addHandler);
		btnEditAnimation.setOnAction(this::editHandler);
		btnRemoveAnimation.setOnAction(this::removeHandler);
		
		this.lstAnimations.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.DELETE) {
				this.removeHandler(null);
			}
		});

		this.lstAnimations.addEventFilter(KeyEvent.ANY, new PreventUndoRedoEventFilter(this.lstAnimations));
		
		context.slideProperty().addListener((obs, ov, nv) -> {
			if (ov != null) {
				Bindings.unbindContent(this.animations, ov.getAnimations());
			}
			if (nv != null) {
				Bindings.bindContent(this.animations, nv.getAnimations());
			}
		});
	}
	
	/**
	 * Called when an item from the list of animations is entered by the mouse or exited.
	 * @param nv the item
	 */
	private void highlightNodeOnHover(SlideAnimation nv) {
		ObservableSlide<?> slide = this.context.getSlide();
		if (slide == null) {
			return;
		}
		
		// when the selection changes, we need to show some sort of indication that the animation
		// is for a specified component in the slide
		UUID id = null;
		if (nv != null) {
			id = nv.getId();
		}
		
		// is it the slide?
		if (slide.getId().equals(id)) {
			slide.getEditBorderNode().pseudoClassStateChanged(ANIMATION_HOVERED, true);
		} else {
			slide.getEditBorderNode().pseudoClassStateChanged(ANIMATION_HOVERED, false);
		}
		
		// is it a component?
		for (ObservableSlideComponent<?> component : slide.getComponents()) {
			if (component.getId().equals(id)) {
				component.getEditBorderNode().pseudoClassStateChanged(ANIMATION_HOVERED, true);
			} else {
				component.getEditBorderNode().pseudoClassStateChanged(ANIMATION_HOVERED, false);
			}
		}
	}
	
	/**
	 * Called when the add button is clicked.
	 * @param e the event
	 */
	private void addHandler(ActionEvent e) {
		ObservableSlideRegion<?> selected = this.context.getSelected();
		ObservableSlide<?> slide = this.context.getSlide();
		addOrEdit(DEFAULT_ANIMATION, a -> {
			UUID id = null;
			if (selected != null) {
				id = selected.getId();
			} else if (slide != null) {
				id = slide.getId();
			}
			if (a != null && id != null) {
				SlideAnimation sa = new SlideAnimation(id, a);
				this.add(sa);
			}
		});
	}
	
	/**
	 * Called when the edit button is clicked or if an animation is double clicked.
	 * @param e the event
	 */
	private void editHandler(ActionEvent e) {
		SlideAnimation selected = this.lstAnimations.getSelectionModel().getSelectedItem();
		if (selected != null) {
			addOrEdit(selected.getAnimation(), a -> {
				UUID id = selected.getId();
				if (a != null) {
					SlideAnimation sa = new SlideAnimation(id, a);
					this.edit(selected, sa);
				}
			});
		}
	}
	
	/**
	 * Called when the remove button is clicked.
	 * @param e the event
	 */
	private void removeHandler(ActionEvent e) {
		SlideAnimation sa = this.lstAnimations.getSelectionModel().getSelectedItem();
		if (sa != null) {
			this.remove(sa);
		}
	}
	
	// helpers
	
	/**
	 * Performs an edit.
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	private void edit(SlideAnimation oldValue, SlideAnimation newValue) {
		this.applyCommand(new EditAnimationEditCommand(
				oldValue,
				newValue,
				this.context.getSlide(), 
				this.context.selectedProperty(), 
				this.lstAnimations));
	}
	
	/**
	 * Removes the slide animation.
	 * @param animation the animation to remove
	 */
	private void remove(SlideAnimation animation) {
		this.applyCommand(new RemoveAnimationEditCommand(
				animation, 
				this.context.getSlide(), 
				this.context.selectedProperty(), 
				this.lstAnimations));
	}
	
	/**
	 * Adds the slide animation.
	 * @param animation the animation to add
	 */
	private void add(SlideAnimation animation) {
		this.applyCommand(new AddAnimationEditCommand(
				animation, 
				this.context.getSlide(), 
				this.context.selectedProperty(), 
				this.lstAnimations));
	}
	
	/**
	 * Adds or edits the slide animation.
	 * @param value the animation; or null in the case of add
	 * @param callback the callback for when the user is satisfied with what they've configured
	 */
	private void addOrEdit(Animation value, Consumer<Animation> callback) {
		if (this.dlgAnimationPicker == null) {
			this.dlgAnimationPicker = new AnimationPickerDialog(getScene().getWindow());
		}
		this.dlgAnimationPicker.setValue(value);
		if (callback != null) {
			this.dlgAnimationPicker.show(callback);
		}
	}
	
	/**
	 * Applies the given command.
	 * @param command the command
	 */
	private void applyCommand(EditCommand command) {
		this.context.getEditManager().execute(command);
		fireEvent(new SlideEditorEvent(this, this, SlideEditorEvent.CHANGED));
	}
}

package org.praisenter.javafx.slide.editor;

import java.util.Comparator;
import java.util.Iterator;
import java.util.UUID;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.animation.Animations;
import org.praisenter.javafx.themes.Styles;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.animation.SlideAnimation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

final class AnimationsPane extends BorderPane {
	private static final PseudoClass ANIMATION_HOVERED = PseudoClass.getPseudoClass("animation-hovered");
	
	private final PraisenterContext context;
	
	private final ObjectProperty<ObservableSlide<?>> slide = new SimpleObjectProperty<ObservableSlide<?>>(null);
	private final ObjectProperty<ObservableSlideRegion<?>> component = new SimpleObjectProperty<ObservableSlideRegion<?>>();
	
	private AnimationPickerDialog dlgAnimationPicker;
	private final ListView<SlideAnimation> lstAnimations;
	
	public AnimationsPane(PraisenterContext context) {
		this.context = context;
		
		// vertical listing of all animations a slide has
		// options to add, edit, or remove
		
		ObservableList<SlideAnimation> animations = FXCollections.observableArrayList();
		SortedList<SlideAnimation> ordered = new SortedList<>(animations, new Comparator<SlideAnimation>() {
			@Override
			public int compare(SlideAnimation o1, SlideAnimation o2) {
				if (o1 == null) return 1;
				if (o2 == null) return -1;
				long diff = o1.getAnimation().getDelay() - o2.getAnimation().getDelay();
				if (diff < 0) {
					return -1;
				} else if (diff > 0) {
					return 1;
				} else {
					return o1.getId().compareTo(o2.getId());
				}
			}
		});
		
		this.lstAnimations = new ListView<SlideAnimation>(ordered);
		this.lstAnimations.setCellFactory(new Callback<ListView<SlideAnimation>, ListCell<SlideAnimation>>() {
			@Override
			public ListCell<SlideAnimation> call(ListView<SlideAnimation> param) {
				return new ListCell<SlideAnimation>() {
					@Override
					protected void updateItem(SlideAnimation item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null && !empty) {
							// FIXME make name a property and bind/unbind so its updated when the component is updated
							String name = Animations.getName(item.getAnimation());
							UUID id = item.getId();
							ObservableSlideComponent<?> component = slide.get().getComponent(id);
							if (component != null) {
								name += " " + component.getRegion().getName();
							}
							this.setText(name);
						} else {
							this.setText(null);
						}
						this.setOnMouseEntered(e -> {
							highlightNodeOnHover(item);
						});
						this.setOnMouseExited(e -> {
							highlightNodeOnHover(null);
						});
						this.setOnMouseClicked(e -> {
							if (e.getClickCount() >= 2) {
								edit(animations);
							}
						});
					}
				};
			}
		});

		Button btnAddAnimation = new Button("Add");
		Button btnEditAnimation = new Button("Edit");
		Button btnRemoveAnimation = new Button("Remove");
		
		HBox btns = new HBox(btnAddAnimation, btnEditAnimation, btnRemoveAnimation);
		btns.setSpacing(5);
		
		Label title = new Label("Animations");
		title.setAlignment(Pos.BASELINE_CENTER);
		title.setMaxWidth(Double.MAX_VALUE);
		title.setFont(Font.font("System", FontWeight.BOLD, 10));
		
		VBox top = new VBox(title, btns);
		top.setPadding(new Insets(5));
		top.setSpacing(5);
		top.setBorder(new Border(new BorderStroke(Color.GRAY, new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 1, 0, null), null, new BorderWidths(0, 1, 0, 1))));
		
		this.setTop(top);
		this.setCenter(this.lstAnimations);
		
		// events
		
		btnAddAnimation.setOnAction(e -> {
			if (this.dlgAnimationPicker == null) {
				this.dlgAnimationPicker = new AnimationPickerDialog(getScene().getWindow());
			}
			this.dlgAnimationPicker.show((a) -> {
				UUID id = null;
				if (this.component.get() != null) {
					id = this.component.get().getId();
				} else if (this.slide.get() != null) {
					id = this.slide.get().getId();
				}
				if (a != null && id != null) {
					SlideAnimation sa = new SlideAnimation(id, a);
					slide.get().addAnimation(sa);
					animations.add(sa);
				}
			});
		});
		
		btnEditAnimation.setOnAction(e -> {
			edit(animations);
		});
		
		btnRemoveAnimation.setOnAction(e -> {
			SlideAnimation sa = this.lstAnimations.getSelectionModel().getSelectedItem();
			slide.get().removeAnimation(sa);
			animations.remove(sa);
		});
		
		slide.addListener((obs, ov, nv) -> {
			animations.clear();
			if (nv != null) {
				animations.addAll(nv.getAnimations());
			}
		});
	}
	
	private void highlightNodeOnHover(SlideAnimation nv) {
		// when the selection changes, we need to show some sort of indication that the animation
		// is for a specified component in the slide
		UUID id = null;
		if (nv != null) {
			id = nv.getId();
		}
		
		if (this.slide.get().getId().equals(id)) {
			// its the slide itself
			this.slide.get().getEditBorderNode().pseudoClassStateChanged(ANIMATION_HOVERED, true);
		} else {
			this.slide.get().getEditBorderNode().pseudoClassStateChanged(ANIMATION_HOVERED, false);
		}
		
		Iterator<ObservableSlideComponent<?>> it = this.slide.get().componentIterator();
		while (it.hasNext()) {
			ObservableSlideComponent<?> component = it.next();
			if (component.getId().equals(id)) {
				// its a component
				component.getEditBorderNode().pseudoClassStateChanged(ANIMATION_HOVERED, true);
			} else {
				component.getEditBorderNode().pseudoClassStateChanged(ANIMATION_HOVERED, false);
			}
		}
	}
	
	private void edit(ObservableList<SlideAnimation> animations) {
		if (this.dlgAnimationPicker == null) {
			this.dlgAnimationPicker = new AnimationPickerDialog(getScene().getWindow());
		}
		SlideAnimation selected = this.lstAnimations.getSelectionModel().getSelectedItem();
		if (selected != null) {
			this.dlgAnimationPicker.setValue(selected.getAnimation());
			this.dlgAnimationPicker.show((a) -> {
				UUID id = selected.getId();
				if (a != null) {
					SlideAnimation sa = new SlideAnimation(id, a);
					slide.get().removeAnimation(selected);
					animations.remove(selected);
					slide.get().addAnimation(sa);
					animations.add(sa);
				}
			});
		}
	}
	
	public ObservableSlide<?> getSlide() {
		return this.slide.get();
	}
	
	public void setSlide(ObservableSlide<?> slide) {
		this.slide.set(slide);
	}
	
	public ObjectProperty<ObservableSlide<?>> slideProperty() {
		return this.slide;
	}
	
	public ObservableSlideRegion<?> getComponent() {
		return this.component.get();
	}
	
	public void setComponent(ObservableSlideRegion<?> component) {
		this.component.set(component);
	}
	
	public ObjectProperty<ObservableSlideRegion<?>> componentProperty() {
		return this.component;
	}
}

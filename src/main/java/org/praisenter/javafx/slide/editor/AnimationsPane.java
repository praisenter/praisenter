package org.praisenter.javafx.slide.editor;

import java.util.Comparator;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.MediaType;
import org.praisenter.javafx.ApplicationGlyphs;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.ObservableBasicTextComponent;
import org.praisenter.javafx.slide.ObservableCountdownComponent;
import org.praisenter.javafx.slide.ObservableDateTimeComponent;
import org.praisenter.javafx.slide.ObservableMediaComponent;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;
import org.praisenter.javafx.slide.animation.Animations;
import org.praisenter.slide.animation.Animation;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.object.MediaObject;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
import javafx.util.Callback;

final class AnimationsPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final PseudoClass ANIMATION_HOVERED = PseudoClass.getPseudoClass("animation-hovered");
	
	private final PraisenterContext context;
	
	private final ObjectProperty<ObservableSlide<?>> slide = new SimpleObjectProperty<ObservableSlide<?>>(null);
	private final ObjectProperty<ObservableSlideRegion<?>> component = new SimpleObjectProperty<ObservableSlideRegion<?>>();
	
	private final ObservableList<SlideAnimation> animations = FXCollections.observableArrayList();
	
	private AnimationPickerDialog dlgAnimationPicker;
	private final ListView<SlideAnimation> lstAnimations;
	
	public AnimationsPane(PraisenterContext context) {
		this.context = context;
		
		// vertical listing of all animations a slide has
		// options to add, edit, or remove
		
		SortedList<SlideAnimation> ordered = new SortedList<>(this.animations, new Comparator<SlideAnimation>() {
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
					// if they are the same, then compare the type and sort slide first
					return o1.getId().compareTo(o2.getId());
				}
			}
		});
		
		// TODO move this ListCell into it's own class
		this.lstAnimations = new ListView<SlideAnimation>(ordered);
		this.lstAnimations.setCellFactory(new Callback<ListView<SlideAnimation>, ListCell<SlideAnimation>>() {
			@Override
			public ListCell<SlideAnimation> call(ListView<SlideAnimation> param) {
				return new ListCell<SlideAnimation>() {
					private final StringProperty animationName = new SimpleStringProperty();
					private final StringProperty componentName = new SimpleStringProperty();
					private final StringBinding name = new StringBinding() {
						{
							bind(animationName, componentName);
						}
						@Override
						protected String computeValue() {
							String an = animationName.get();
							String cn = componentName.get();
							if (an != null && cn != null) {
								return an + " " + cn;
							} else if (an != null) {
								return an;
							}
							return null;
						}
					};
					{
						this.textProperty().bind(name);
					}
					@Override
					protected void updateItem(SlideAnimation item, boolean empty) {
						super.updateItem(item, empty);
						this.componentName.unbind();
						if (item != null && !empty) {
							this.animationName.set(Animations.getName(item.getAnimation()));
							
							// set the name and graphic
							UUID id = item.getId();
							if (id.equals(slide.get().getId())) {
								this.componentName.bind(slide.get().nameProperty());
								this.setGraphic(ApplicationGlyphs.SLIDE.duplicate());
							} else {
								ObservableSlideComponent<?> component = slide.get().getComponent(id);
								if (component != null) {
									this.componentName.bind(component.nameProperty());
									if (component instanceof ObservableTextPlaceholderComponent) {
										this.setGraphic(ApplicationGlyphs.PLACEHOLDER_COMPONENT.duplicate());
									} else if (component instanceof ObservableDateTimeComponent) {
										this.setGraphic(ApplicationGlyphs.DATETIME_COMPONENT.duplicate());
									} else if (component instanceof ObservableCountdownComponent) {
										this.setGraphic(ApplicationGlyphs.COUNTDOWN_COMPONENT.duplicate());
									} else if (component instanceof ObservableBasicTextComponent) {
										this.setGraphic(ApplicationGlyphs.BASIC_TEXT_COMPONENT.duplicate());
									} else if (component instanceof ObservableMediaComponent) {
										MediaObject mo = ((ObservableMediaComponent)component).getMedia();
										if (mo == null || mo.getType() == null) {
											this.setGraphic(ApplicationGlyphs.MEDIA_COMPONENT.duplicate());
										} else if (mo.getType() == MediaType.AUDIO) {
											this.setGraphic(ApplicationGlyphs.AUDIO_MEDIA_COMPONENT.duplicate());
										} else if (mo.getType() == MediaType.IMAGE) {
											this.setGraphic(ApplicationGlyphs.IMAGE_MEDIA_COMPONENT.duplicate());
										}  else if (mo.getType() == MediaType.VIDEO) {
											this.setGraphic(ApplicationGlyphs.VIDEO_MEDIA_COMPONENT.duplicate());
										} else {
											LOGGER.warn("Unknown media type {} when choosing icon for animation.", mo.getType());
										}
									} else {
										// just log it
										LOGGER.warn("Unknown type {} when choosing icon for animation.", component.getClass());
									}
								}
							}
							
							this.setOnMouseClicked(e -> {
								if (e.getClickCount() >= 2) {
									editHandler(null);
								}
							});
						} else {
							this.componentName.set(null);
							this.animationName.set(null);
							this.setOnMouseClicked(null);
							this.setGraphic(null);
						}
						this.setOnMouseEntered(e -> {
							highlightNodeOnHover(item);
						});
						this.setOnMouseExited(e -> {
							highlightNodeOnHover(null);
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
		
		btnAddAnimation.setOnAction(this::addHandler);
		btnEditAnimation.setOnAction(this::editHandler);
		btnRemoveAnimation.setOnAction(this::removeHandler);
		
		slide.addListener((obs, ov, nv) -> {
			if (ov != null) {
				Bindings.unbindContent(this.animations, ov.getAnimations());
			}
			if (nv != null) {
				Bindings.bindContent(this.animations, nv.getAnimations());
			}
		});
	}
	
	private void highlightNodeOnHover(SlideAnimation nv) {
		if (this.slide.get() == null) {
			return;
		}
		
		// when the selection changes, we need to show some sort of indication that the animation
		// is for a specified component in the slide
		UUID id = null;
		if (nv != null) {
			id = nv.getId();
		}
		
		// is it the slide?
		if (this.slide.get().getId().equals(id)) {
			this.slide.get().getEditBorderNode().pseudoClassStateChanged(ANIMATION_HOVERED, true);
		} else {
			this.slide.get().getEditBorderNode().pseudoClassStateChanged(ANIMATION_HOVERED, false);
		}
		
		// is it a component?
		for (ObservableSlideComponent<?> component : this.slide.get().getComponents()) {
			if (component.getId().equals(id)) {
				component.getEditBorderNode().pseudoClassStateChanged(ANIMATION_HOVERED, true);
			} else {
				component.getEditBorderNode().pseudoClassStateChanged(ANIMATION_HOVERED, false);
			}
		}
	}
	
	private void addHandler(ActionEvent e) {
		addOrEdit(null, a -> {
			UUID id = null;
			if (this.component.get() != null) {
				id = this.component.get().getId();
			} else if (this.slide.get() != null) {
				id = this.slide.get().getId();
			}
			if (a != null && id != null) {
				SlideAnimation sa = new SlideAnimation(id, a);
				this.add(sa);
			}
		});
	}
	
	private void editHandler(ActionEvent e) {
		SlideAnimation selected = this.lstAnimations.getSelectionModel().getSelectedItem();
		if (selected != null) {
			addOrEdit(selected.getAnimation(), a -> {
				UUID id = selected.getId();
				if (a != null) {
					SlideAnimation sa = new SlideAnimation(id, a);
					this.remove(selected);
					this.add(sa);
				}
			});
		}
	}
	
	private void removeHandler(ActionEvent e) {
		SlideAnimation sa = this.lstAnimations.getSelectionModel().getSelectedItem();
		if (sa != null) {
			this.remove(sa);
		}
	}
	
	private void remove(SlideAnimation animation) {
		if (this.slide.get() == null) {
			return;
		}
		
		this.slide.get().removeAnimation(animation);
	}
	
	private void add(SlideAnimation animation) {
		if (this.slide.get() == null) {
			return;
		}
		
		this.slide.get().addAnimation(animation);
	}
	
	private void addOrEdit(Animation value, Consumer<Animation> callback) {
		if (this.dlgAnimationPicker == null) {
			this.dlgAnimationPicker = new AnimationPickerDialog(getScene().getWindow());
		}
		this.dlgAnimationPicker.setValue(value);
		if (callback != null) {
			this.dlgAnimationPicker.show(callback);
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

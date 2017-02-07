package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.ObservableMediaComponent;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;

final class SlideEditorRibbon extends TabPane implements EventHandler<SlideRibbonEvent> {
	private final ObjectProperty<ObservableSlide<?>> slide = new SimpleObjectProperty<ObservableSlide<?>>();
	private final ObjectProperty<ObservableSlideRegion<?>> component = new SimpleObjectProperty<ObservableSlideRegion<?>>();
	
	public SlideEditorRibbon(PraisenterContext context) {
		// slide
		SlideRibbonTab slide = new SlideRibbonTab(context);
		
		// insert
		InsertTextRibbonTab insertText = new InsertTextRibbonTab(context);
		InsertMediaRibbonTab insertMedia = new InsertMediaRibbonTab(context);
		
		// container
		ComponentEditorRibbonTab background = new BackgroundRibbonTab(context);
		ComponentEditorRibbonTab border = new BorderRibbonTab();
		ComponentEditorRibbonTab stacking = new StackingRibbonTab();
		ComponentEditorRibbonTab shadow = new ShadowRibbonTab();
		ComponentEditorRibbonTab glow = new GlowRibbonTab();
		ComponentEditorRibbonTab container = new ContainerRibbonTab();
		
		// text format
		ComponentEditorRibbonTab font = new FontRibbonTab();
		ComponentEditorRibbonTab paragraph = new ParagraphRibbonTab();
		ComponentEditorRibbonTab fontBorder = new FontBorderRibbonTab();
		ComponentEditorRibbonTab fontShadow = new FontShadowRibbonTab();
		ComponentEditorRibbonTab fontGlow = new FontGlowRibbonTab();
		
		// text content
		ComponentEditorRibbonTab text = new TextRibbonTab();
		ComponentEditorRibbonTab dateTimeFormat = new DateTimeRibbonTab();
		ComponentEditorRibbonTab countdownFormat = new CountdownRibbonTab();
		ComponentEditorRibbonTab placeholder = new PlaceholderRibbonTab();
		
		this.addEventHandler(SlideRibbonEvent.ALL, this);
		
		slide.componentProperty().bind(this.slide);
		
		background.componentProperty().bind(component);
		border.componentProperty().bind(component);
		stacking.componentProperty().bind(component);
		shadow.componentProperty().bind(component);
		glow.componentProperty().bind(component);
		container.componentProperty().bind(component);
		
		font.componentProperty().bind(component);
		paragraph.componentProperty().bind(component);
		fontBorder.componentProperty().bind(component);
		fontShadow.componentProperty().bind(component);
		fontGlow.componentProperty().bind(component);
		
		text.componentProperty().bind(component);
		dateTimeFormat.componentProperty().bind(component);
		countdownFormat.componentProperty().bind(component);
		placeholder.componentProperty().bind(component);

		background.setDisable(true);
		border.setDisable(true);
		stacking.setDisable(true);
		shadow.setDisable(true);
		glow.setDisable(true);
		container.setDisable(true);
		
		font.setDisable(true);
		paragraph.setDisable(true);
		fontBorder.setDisable(true);
		fontShadow.setDisable(true);
		fontGlow.setDisable(true);
		
		text.setDisable(true);
		dateTimeFormat.setDisable(true);
		countdownFormat.setDisable(true);
		placeholder.setDisable(true);
		
		HBox ribSlide = new HBox(slide);
		ribSlide.setPadding(new Insets(0, 0, 0, 2));
		Tab tabSlide = new Tab(" Slide ", ribSlide);
		tabSlide.setClosable(false);
		
		HBox ribInsert = new HBox(insertText, insertMedia);
		ribInsert.setPadding(new Insets(0, 0, 0, 2));
		Tab tabInsert = new Tab(" Insert ", ribInsert);
		tabInsert.setClosable(false);
		
		HBox ribBox = new HBox(background, border, stacking, shadow, glow, container);
		ribBox.setPadding(new Insets(0, 0, 0, 2));
		Tab tabBox = new Tab(" Container ", ribBox);
		tabBox.setClosable(false);
		
		HBox ribText = new HBox(font, paragraph, fontBorder, fontShadow, fontGlow);
		ribText.setPadding(new Insets(0, 0, 0, 2));
		Tab tabText = new Tab(" Text Format ", ribText);
		tabText.setClosable(false);
		
		HBox ribFormat = new HBox(text, dateTimeFormat, countdownFormat, placeholder);
		ribFormat.setPadding(new Insets(0, 0, 0, 2));
		Tab tabFormat = new Tab(" Text Content ", ribFormat);
		tabFormat.setClosable(false);
		
		this.getTabs().addAll(tabSlide, tabInsert, tabBox, tabText, tabFormat);
		
		this.component.addListener((obs, ov, nv) -> {
			// switch focus to the likely tab given the component type
			if (nv != null) {
				if (nv instanceof ObservableSlide) {
					this.getSelectionModel().select(tabBox);
				} else if (nv instanceof ObservableTextComponent) {
					this.getSelectionModel().select(tabText);
				} else if (nv instanceof ObservableMediaComponent) {
					this.getSelectionModel().select(tabBox);
				}
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(SlideRibbonEvent event) {
		if (event.getEventType() == SlideRibbonEvent.PLACEHOLDER) {
			this.slide.get().updatePlaceholders();
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

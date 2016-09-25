package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;

final class SlideEditorRibbon extends TabPane {
	private final ObjectProperty<ObservableSlideRegion<?>> component = new SimpleObjectProperty<ObservableSlideRegion<?>>();
	
	public SlideEditorRibbon(PraisenterContext context) {
		EditorRibbonTab background = new BackgroundRibbonTab(context);
		EditorRibbonTab border = new BorderRibbonTab();
		EditorRibbonTab general = new GeneralRibbonTab();
		EditorRibbonTab shadow = new ShadowRibbonTab();
		EditorRibbonTab glow = new GlowRibbonTab();
		
		EditorRibbonTab font = new FontRibbonTab();
		EditorRibbonTab paragraph = new ParagraphRibbonTab();
		EditorRibbonTab fontBorder = new FontBorderRibbonTab();
		EditorRibbonTab fontShadow = new FontShadowRibbonTab();
		EditorRibbonTab fontGlow = new FontGlowRibbonTab();
		
		EditorRibbonTab dateTimeFormat = new DateTimeRibbonTab();
		EditorRibbonTab countdownFormat = new CountdownRibbonTab();
		EditorRibbonTab placeholder = new PlaceholderRibbonTab();
		
		background.componentProperty().bind(component);
		border.componentProperty().bind(component);
		general.componentProperty().bind(component);
		shadow.componentProperty().bind(component);
		glow.componentProperty().bind(component);
		font.componentProperty().bind(component);
		paragraph.componentProperty().bind(component);
		fontBorder.componentProperty().bind(component);
		fontShadow.componentProperty().bind(component);
		fontGlow.componentProperty().bind(component);
		dateTimeFormat.componentProperty().bind(component);
		countdownFormat.componentProperty().bind(component);
		placeholder.componentProperty().bind(component);
		
		HBox ribBox = new HBox(background, border, general, shadow, glow);
		ribBox.setPadding(new Insets(0, 0, 0, 2));
		Tab tabBox = new Tab(" General ", ribBox);
		tabBox.setClosable(false);
		
		HBox ribText = new HBox(font, paragraph, fontBorder, fontShadow, fontGlow);
		ribText.setPadding(new Insets(0, 0, 0, 2));
		Tab tabText = new Tab(" Text ", ribText);
		tabText.setClosable(false);
		
		Tab tabInsert = new Tab(" Insert ");
		tabInsert.setClosable(false);
		
		HBox ribFormat = new HBox(dateTimeFormat, countdownFormat, placeholder);
		ribFormat.setPadding(new Insets(0, 0, 0, 2));
		Tab tabFormat = new Tab(" Format ", ribFormat);
		tabFormat.setClosable(false);
		
		this.getTabs().addAll(tabInsert, tabBox, tabText, tabFormat);
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

package org.praisenter.javafx.slide.editor.ribbon;

import org.praisenter.javafx.slide.ObservableMediaComponent;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.javafx.slide.editor.SlideEditorContext;

import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;

public final class SlideEditorRibbon extends TabPane {
	private final SlideEditorContext context;
	
	private final SlideRibbonTab slideTab;
	private final TagsRibbonTab tagsTab;
	
	private final InsertTextRibbonTab insertTextTab;
	private final InsertMediaRibbonTab insertMediaTab;
	
	private final BackgroundRibbonTab backgroundTab;
	private final BorderRibbonTab borderTab;
	private final StackingRibbonTab stackingTab;
	private final ShadowRibbonTab shadowTab;
	private final GlowRibbonTab glowTab;
	private final ContainerRibbonTab containerTab;
	
	private final FontRibbonTab fontTab;
	private final ParagraphRibbonTab paragraphTab;
	private final TextBorderRibbonTab fontBorderTab;
	private final TextShadowRibbonTab fontShadowTab;
	private final TextGlowRibbonTab fontGlowTab;
	private final TextRibbonTab textTab;
	private final DateTimeRibbonTab dateTimeTab;
	private final CountdownRibbonTab countdownTab;
	private final PlaceholderRibbonTab placeholderTab;
	
	private final MediaRibbonTab mediaTab;
	
	public SlideEditorRibbon(SlideEditorContext context) {
		this.context = context;
		
		// create the tab content
		
		this.slideTab = new SlideRibbonTab(context);
		this.tagsTab = new TagsRibbonTab(context);
		
		this.insertTextTab = new InsertTextRibbonTab(context);
		this.insertMediaTab = new InsertMediaRibbonTab(context);
		
		this.backgroundTab = new BackgroundRibbonTab(context);
		this.borderTab = new BorderRibbonTab(context);
		this.stackingTab = new StackingRibbonTab(context);
		this.shadowTab = new ShadowRibbonTab(context);
		this.glowTab = new GlowRibbonTab(context);
		this.containerTab = new ContainerRibbonTab(context);
		
		this.fontTab = new FontRibbonTab(context);
		this.paragraphTab = new ParagraphRibbonTab(context);
		this.fontBorderTab = new TextBorderRibbonTab(context);
		this.fontShadowTab = new TextShadowRibbonTab(context);
		this.fontGlowTab = new TextGlowRibbonTab(context);
		this.textTab = new TextRibbonTab(context);
		this.dateTimeTab = new DateTimeRibbonTab(context);
		this.countdownTab = new CountdownRibbonTab(context);
		this.placeholderTab = new PlaceholderRibbonTab(context);
		
		this.mediaTab = new MediaRibbonTab(context);
		
		// initial state
		
		this.backgroundTab.setDisable(true);
		this.borderTab.setDisable(true);
		this.stackingTab.setDisable(true);
		this.shadowTab.setDisable(true);
		this.glowTab.setDisable(true);
		this.containerTab.setDisable(true);
		
		this.fontTab.setDisable(true);
		this.paragraphTab.setDisable(true);
		this.fontBorderTab.setDisable(true);
		this.fontShadowTab.setDisable(true);
		this.fontGlowTab.setDisable(true);
		this.textTab.setVisible(false);
		this.dateTimeTab.setVisible(false);
		this.countdownTab.setVisible(false);
		this.placeholderTab.setVisible(false);
		
		this.mediaTab.setDisable(true);

		// build the UI
		
		HBox ribSlide = new HBox(this.slideTab, this.tagsTab);
		ribSlide.setPadding(new Insets(0, 0, 0, 2));
		Tab tabSlide = new Tab(" Slide ", ribSlide);
		tabSlide.setClosable(false);
		
		HBox ribInsert = new HBox(this.insertTextTab, this.insertMediaTab);
		ribInsert.setPadding(new Insets(0, 0, 0, 2));
		Tab tabInsert = new Tab(" Insert ", ribInsert);
		tabInsert.setClosable(false);
		
		HBox ribBox = new HBox(this.backgroundTab, this.borderTab, this.stackingTab, this.shadowTab, this.glowTab, this.containerTab);
		ribBox.setPadding(new Insets(0, 0, 0, 2));
		Tab tabBox = new Tab(" Container ", ribBox);
		tabBox.setClosable(false);
		
		HBox ribText = new HBox(this.fontTab, this.paragraphTab, this.fontBorderTab, this.fontShadowTab, this.fontGlowTab, this.textTab, this.dateTimeTab, this.countdownTab, this.placeholderTab);
		ribText.setPadding(new Insets(0, 0, 0, 2));
		Tab tabText = new Tab(" Text ", ribText);
		tabText.setClosable(false);
		
		HBox ribMedia = new HBox(this.mediaTab);
		ribMedia.setPadding(new Insets(0, 0, 0, 2));
		Tab tabMedia = new Tab(" Media ", ribMedia);
		tabMedia.setClosable(false);
		
		this.getTabs().addAll(tabSlide, tabInsert, tabBox, tabText, tabMedia);

		// events
		
		context.selectedProperty().addListener((obs, ov, nv) -> {
			// switch focus to the likely tab given the component type
			if (nv != null) {
				if (nv instanceof ObservableSlide) {
					this.getSelectionModel().select(tabBox);
				} else if (nv instanceof ObservableTextComponent) {
					this.getSelectionModel().select(tabText);
				} else if (nv instanceof ObservableMediaComponent) {
					this.getSelectionModel().select(tabMedia);
				}
			}
		});
	}

	// other
	
	public void setSlideName(String name) {
		this.slideTab.setName(name);
	}
}

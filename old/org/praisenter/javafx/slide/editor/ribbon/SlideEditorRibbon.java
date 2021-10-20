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
package org.praisenter.javafx.slide.editor.ribbon;

import org.praisenter.javafx.slide.ObservableMediaComponent;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.ui.translations.Translations;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;

/**
 * The slide editing ribbon.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideEditorRibbon extends TabPane {
	// slide
	
	/** The slide tab */
	private final SlideRibbonTab slideTab;
	
	/** The tags tab */
	private final TagsRibbonTab tagsTab;
	
	// insert
	
	/** The insert text tab */
	private final InsertTextRibbonTab insertTextTab;
	
	/** The insert media tab */
	private final InsertMediaRibbonTab insertMediaTab;
	
	// container
	
	/** The background tab */
	private final BackgroundRibbonTab backgroundTab;
	
	/** The border tab */
	private final BorderRibbonTab borderTab;
	
	/** The stacking tab */
	private final StackingRibbonTab stackingTab;
	
	/** The shadow tab */
	private final ShadowRibbonTab shadowTab;
	
	/** The glow tab */
	private final GlowRibbonTab glowTab;
	
	/** The container tab */
	private final ContainerRibbonTab containerTab;
	
	// text
	
	/** The font tab */
	private final FontRibbonTab fontTab;
	
	/** The paragraph tab */
	private final ParagraphRibbonTab paragraphTab;
	
	/** The text border tab */
	private final TextBorderRibbonTab fontBorderTab;
	
	/** The text shadow tab */
	private final TextShadowRibbonTab fontShadowTab;
	
	/** The text glow tab */
	private final TextGlowRibbonTab fontGlowTab;
	
	/** The text tab */
	private final TextRibbonTab textTab;
	
	/** The date-time tab */
	private final DateTimeRibbonTab dateTimeTab;
	
	/** The countdown tab */
	private final CountdownRibbonTab countdownTab;
	
	/** The placeholder tab */
	private final PlaceholderRibbonTab placeholderTab;
	
	// media
	
	/** The media tab */
	private final MediaRibbonTab mediaTab;
	
	/**
	 * Minimal constructor.
	 * @param context the slide editor context
	 */
	public SlideEditorRibbon(SlideEditorContext context) {
		this.getStyleClass().add("slide-editor-ribbon");
		
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
		ribSlide.getStyleClass().add("slide-editor-ribbon-content");
		Tab tabSlide = new Tab(Translations.get("slide.editor.ribbon.tab.slide"), ribSlide);
		tabSlide.setClosable(false);
		
		HBox ribInsert = new HBox(this.insertTextTab, this.insertMediaTab);
		ribInsert.getStyleClass().add("slide-editor-ribbon-content");
		Tab tabInsert = new Tab(Translations.get("slide.editor.ribbon.tab.insert"), ribInsert);
		tabInsert.setClosable(false);
		
		HBox ribBox = new HBox(this.backgroundTab, this.borderTab, this.stackingTab, this.shadowTab, this.glowTab, this.containerTab);
		ribBox.getStyleClass().add("slide-editor-ribbon-content");
		Tab tabBox = new Tab(Translations.get("slide.editor.ribbon.tab.container"), ribBox);
		tabBox.setClosable(false);
		
		HBox ribText = new HBox(this.fontTab, this.paragraphTab, this.fontBorderTab, this.fontShadowTab, this.fontGlowTab, this.textTab, this.dateTimeTab, this.countdownTab, this.placeholderTab);
		ribText.getStyleClass().add("slide-editor-ribbon-content");
		Tab tabText = new Tab(Translations.get("slide.editor.ribbon.tab.text"), ribText);
		tabText.setClosable(false);
		
		HBox ribMedia = new HBox(this.mediaTab);
		ribMedia.getStyleClass().add("slide-editor-ribbon-content");
		Tab tabMedia = new Tab(Translations.get("slide.editor.ribbon.tab.media"), ribMedia);
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
	
	/**
	 * Sets the name of the slide.
	 * <p>
	 * This is primarily for save as functionality.
	 * @param name the name
	 */
	public void setSlideName(String name) {
		this.slideTab.setName(name);
	}
}

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
package org.praisenter.javafx.controls;

import org.praisenter.utility.StringManipulator;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

/**
 * Represents a section header with a title and description.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SectionHeader extends VBox {
	/** The title text */
	private final StringProperty title = new SimpleStringProperty();
	
	/** The description text */
	private final StringProperty description = new SimpleStringProperty();
	
	// nodes
	
	/** The title label */
	private final Label lblTitle;
	
	/** The description label */
	private final Label lblDescription;

	/** The divider */
	private final Separator divider;
	
	/**
	 * Minimal constructor.
	 * @param title the title text
	 */
	public SectionHeader(String title) {
		this(title, null);
	}
	
	/**
	 * Optional constructor.
	 * @param title the title text
	 * @param description the description text
	 */
	public SectionHeader(String title, String description) {
		this.getStyleClass().add("section-header");
		
		this.lblTitle = new Label(title);
		this.lblDescription = new Label(description);
		this.divider = new Separator();
		
		this.lblTitle.getStyleClass().add("section-header-title");
		this.lblDescription.getStyleClass().add("section-header-description");
		this.divider.getStyleClass().add("section-header-divider");
		
		this.lblTitle.textProperty().bind(this.title);
		this.lblDescription.textProperty().bind(this.description);
		
		this.description.set(description);
		this.title.set(title);
		
		this.getChildren().addAll(this.lblTitle, this.divider, this.lblDescription);
		
		// only show the text if it was given
		this.lblDescription.managedProperty().bind(this.lblDescription.visibleProperty());
		this.lblDescription.visibleProperty().bind(new BooleanBinding() {
			{
				bind(lblDescription.textProperty());
			}
			@Override
			protected boolean computeValue() {
				return !StringManipulator.isNullOrEmpty(lblDescription.getText());
			}
		});
	}
	
	/**
	 * Returns the title label for further customization.
	 * <p>
	 * Ideally, any styling changes should be done in CSS.
	 * @return Label
	 */
	public Label getTitleLabel() {
		return this.lblTitle;
	}

	/**
	 * Returns the description label for further customization.
	 * <p>
	 * Ideally, any styling changes should be done in CSS.
	 * @return Label
	 */
	public Label getDescriptionLabel() {
		return this.lblDescription;
	}
	
	/**
	 * Returns the separator for futher customization.
	 * <p>
	 * Ideally, any styling changes should be done in CSS.
	 * @return Separator
	 */
	public Separator getSeparator() {
		return this.divider;
	}

	// title
	
	/**
	 * Sets the title text.
	 * @param title the title text
	 */
	public void setTitle(String title) {
		this.title.set(title);
	}
	
	/**
	 * Returns the title text.
	 * @return String
	 */
	public String getTitle() {
		return this.title.get();
	}
	
	/**
	 * Returns the title property.
	 * @return StringProperty
	 */
	public StringProperty titleProperty() {
		return this.title;
	}
	
	// description
	
	/**
	 * Sets the description text.
	 * @param description the description text
	 */
	public void setDescription(String description) {
		this.description.set(description);
	}
	
	/**
	 * Returns the description text.
	 * @return String
	 */
	public String getDescription() {
		return this.description.get();
	}
	
	/**
	 * Returns the description property.
	 * @return StringProperty
	 */
	public StringProperty descriptionProperty() {
		return this.description;
	}
}

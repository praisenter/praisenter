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

import org.praisenter.javafx.ApplicationGlyphs;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents a information, warning or error message with appropriate icon.
 * @author William Bittle
 * @version 3.0.0
 */
public final class MessageLabel extends HBox {
	/** The class */
	private static final String CLASS_NAME = "message-label";
	
	/** The wrapped version of the class */
	private static final String WRAPPED_CLASS_NAME = "message-label-wrapped";
	
	/** The message text */
	private final StringProperty text = new SimpleStringProperty();
	
	/** The message type */
	private final ObjectProperty<AlertType> type = new SimpleObjectProperty<AlertType>();
	
	// nodes
	
	/** The label */
	private final Label label;
	
	/** The graphic (separate so we can top align for wrapped text) */
	private final Label graphic;
	
	/**
	 * Minimal constructor.
	 * @param text the text
	 * @param type the type
	 * @param wrapText true to wrap text
	 */
	public MessageLabel(String text, AlertType type, boolean wrapText) {
		this.getStyleClass().add("message-label");
		
		this.label = new Label();
		this.graphic = new Label();
		
		this.label.getStyleClass().add(".message-label-message");
		this.graphic.getStyleClass().add(".message-label-graphic");
		
		this.label.textProperty().bind(this.text);
		this.graphic.graphicProperty().bind(new ObjectBinding<Node>() {
			{
				bind(MessageLabel.this.type);
			}
			@Override
			protected Node computeValue() {
				AlertType t = MessageLabel.this.type.get();
				if (t == AlertType.INFORMATION) {
					return ApplicationGlyphs.INFO.duplicate();
				} else if (t == AlertType.WARNING) {
					return ApplicationGlyphs.WARN.duplicate();
				} else if (t == AlertType.ERROR) {
					return ApplicationGlyphs.ERROR.duplicate();
				} else {
					return null;
				}
			}
		});
		
		this.getChildren().addAll(this.graphic, this.label);
		
		this.label.wrapTextProperty().addListener((obs, ov, nv) -> {
			// toggle the style classes
			if (nv) {
				this.getStyleClass().remove(CLASS_NAME);
				this.getStyleClass().add(WRAPPED_CLASS_NAME);
			} else {
				this.getStyleClass().remove(WRAPPED_CLASS_NAME);
				this.getStyleClass().add(CLASS_NAME);
			}
		});
		
		this.text.set(text);
		this.type.set(type);
		this.label.setWrapText(wrapText);
	}
	
	/**
	 * Returns the underlying label.
	 * @return Label
	 */
	public Label getLabel() {
		return this.label;
	}
	
	/**
	 * Sets the text.
	 * @param text the text
	 */
	public void setText(String text) {
		this.text.set(text);
	}
	
	/**
	 * Returns the text.
	 * @return String
	 */
	public String getText() {
		return this.text.get();
	}
	
	/**
	 * Returns the text property.
	 * @return StringProperty
	 */
	public StringProperty textProperty() {
		return this.text;
	}
	
	// type
	
	/**
	 * Sets the message type.
	 * @param type the type
	 */
	public void setType(AlertType type) {
		this.type.set(type);
	}
	
	/**
	 * Returns the message type.
	 * @return AlertType
	 */
	public AlertType getType() {
		return this.type.get();
	}
	
	/**
	 * Returns the message type property.
	 * @return ObjectProperty&lt;AlertType&gt;
	 */
	public ObjectProperty<AlertType> typeProperty() {
		return this.type;
	}
	
	// text wrap
	
	/**
	 * Sets the text wrapping flag.
	 * @param wrap true if text should be wrapped
	 */
	public void setWrapText(boolean wrap) {
		this.label.setWrapText(wrap);
	}
	
	/**
	 * Returns true if text wrapping is enabled.
	 * @return boolean
	 */
	public boolean isWrapText() {
		return this.label.isWrapText();
	}
	
	/**
	 * Returns the text wrapping property.
	 * @return BooleanProperty
	 */
	public BooleanProperty wrapTextProperty() {
		return this.label.wrapTextProperty();
	}
}

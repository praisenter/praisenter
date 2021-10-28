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
package org.praisenter.ui.controls;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import org.controlsfx.control.textfield.TextFields;
import org.praisenter.data.Tag;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Represents a list of tags that can be added and removed.
 * @author William Bittle
 * @version 3.0.0
 */
public final class TagListView extends BorderPane {
	private static final String TAG_LIST_VIEW_CSS = "p-tag-list-view";
	private static final String TAG_LIST_VIEW_TAGS_CSS = "p-tag-list-view-tags";
	private static final String TAG_LIST_VIEW_TAG_CSS = "p-tag-list-view-tag";
	private static final String TAG_LIST_VIEW_TAG_X_CSS = "p-tag-list-view-tag-x";
	
	/** The set of tags */
	private final ObservableSet<Tag> tags;
	
	// nodes
	
	/** The text field */
	private final TextField textField;
	
	/** The list of tag nodes */
	private final ObservableList<Node> tagNodes;
	
	/**
	 * Full constructor.
	 * @param all the set of all tags
	 */
	public TagListView(ObservableSet<Tag> all) {
		this.tags = FXCollections.observableSet();
		this.tagNodes = FXCollections.observableArrayList();
		
		this.getStyleClass().add(TAG_LIST_VIEW_CSS);
		
		FlowPane btns = new FlowPane();
		btns.getStyleClass().add(TAG_LIST_VIEW_TAGS_CSS);
		btns.prefWrapLengthProperty().bind(this.widthProperty());
		
		// bind the children of this view to the tagNode list
		Bindings.bindContent(btns.getChildren(), this.tagNodes);
		
		// when the tag list changes regenerate the nodes
		// sadly we can't do a mappedlist since the source is a set
		this.tags.addListener(new SetChangeListener<Tag>() {
			@Override
			public void onChanged(Change<? extends Tag> change) {
				if (change.wasAdded()) {
					// append a new node
					tagNodes.add(generateTagNode(change.getElementAdded()));
				}
				if (change.wasRemoved()) {
					// remove the node with the same id as the tag name
					tagNodes.removeIf(n -> n.getUserData().equals(change.getElementRemoved()));
				}
			}
		});
		
		// create an autocomplete field
		this.textField = new TextField();
		this.textField.setPromptText(Translations.get("tags.add.placeholder"));
		TextInputFieldEventFilter.applyTextInputFieldEventFilter(this.textField);
		
		// apply the auto completion binding
		TextFields.bindAutoCompletion(
        		this.textField,
                new Callback<ISuggestionRequest, Collection<Tag>>() {
					@Override
					public Collection<Tag> call(ISuggestionRequest request) {
						String name = request.getUserText().toLowerCase();
						if (name == null || name.length() == 0) {
							return Collections.emptyList();
						}
						return all.stream().filter(t -> t.getName().toLowerCase().startsWith(name)).collect(Collectors.toList());
					}
                },
                new StringConverter<Tag>() {
					@Override
					public Tag fromString(String name) {
						if (name == null || name.length() == 0) return null;
						return new Tag(name);
					}
					@Override
					public String toString(Tag tag) {
						if (tag == null) return null;
						return tag.getName();
					}
                });
        
        
        // when the user hits enter add the tag
        this.textField.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String name = textField.getText();
				Tag tag = new Tag(name);
				tags.add(tag);
				textField.clear();
			}
		});
		
        this.setTop(textField);
		this.setCenter(btns);
	}
	
	/**
	 * Helper method to generate a node for a tag.
	 * @param tag the tag
	 * @return Button
	 */
	private final Button generateTagNode(Tag tag) {
		Button btn = new Button(tag.getName(), generateX());
		btn.getStyleClass().add(TAG_LIST_VIEW_TAG_CSS);
		btn.setTooltip(new Tooltip(Translations.get("tags.remove")));
		btn.setUserData(tag);
		
		// set the click action
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// remove the item from the tags
				Tag tag = new Tag(btn.getText());
				tags.remove(tag);
			}
		});
		
		return btn;
	}

	/**
	 * Generates an X like icon.
	 * @return Path
	 */
	private static final Node generateX() {
		Path x = new Path();
    	MoveTo m1 = new MoveTo(0, 0);
    	LineTo l1 = new LineTo(7, 7);
    	MoveTo m2 = new MoveTo(7, 0);
    	LineTo l2 = new LineTo(0, 7);
    	x.getElements().addAll(m1, l1, m2, l2);
    	x.getStyleClass().add(TAG_LIST_VIEW_TAG_X_CSS);
    	return x;
	}
	
	/* (non-Javadoc)
	 * @see javafx.scene.Node#requestFocus()
	 */
	@Override
	public void requestFocus() {
		this.textField.requestFocus();
	}
	
	/**
	 * Sets the text of the text field.
	 * @param text the text
	 */
	public void setText(String text) {
		this.textField.setText(text);
	}
	
	/**
	 * Returns the text in the text field.
	 * @return String
	 */
	public String getText() {
		return this.textField.getText();
	}
	
	/**
	 * Returns the text property.
	 * @return StringProperty
	 */
	public StringProperty textProperty() {
		return this.textField.textProperty();
	}
	
	/**
	 * Returns the tags.
	 * @return ObservableSet&lt;{@link Tag}&gt;
	 */
	public ObservableSet<Tag> getTags() {
		return this.tags;
	}
}

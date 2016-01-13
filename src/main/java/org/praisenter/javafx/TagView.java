package org.praisenter.javafx;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import org.controlsfx.control.textfield.TextFields;
import org.praisenter.Tag;
import org.praisenter.resources.translations.Translations;

public class TagView extends BorderPane {
	private final SimpleSetProperty<Tag> tags;
	
	private final TextField textField;
	private final ObservableList<Node> tagNodes;
	
	public TagView(ObservableSet<Tag> all) {
		this.tags = new SimpleSetProperty<Tag>(FXCollections.observableSet());
		
		this.tagNodes = FXCollections.observableArrayList();
		
		FlowPane btns = new FlowPane();
		btns.setHgap(2);
		btns.setVgap(2);
		btns.setPadding(new Insets(5, 0, 0, 0));
		
		// bind the children of this view to the tagNode list
		Bindings.bindContent(btns.getChildren(), this.tagNodes);
		
		// when the tag list changes regenerate the nodes
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
		this.textField.setPromptText(Translations.getTranslation("tags.add.placeholder"));
		
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
						return all.stream().filter(t -> t.getName().toLowerCase().contains(name)).collect(Collectors.toList());
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
				
				fireEvent(new TagEvent(textField, TagView.this, TagEvent.ADDED, tag));
			}
		});
		
        this.setTop(textField);
		this.setCenter(btns);
	}
	
	private final Node generateTagNode(Tag tag) {
		Button btn = new Button(tag.getName(), generateX());
		btn.setTooltip(new Tooltip(tag.getName()));
		btn.setMinWidth(0);
		btn.setUserData(tag);
		
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// remove the item from the tags
				Tag tag = new Tag(btn.getText());
				tags.remove(tag);
				
				fireEvent(new TagEvent(btn, TagView.this, TagEvent.REMOVED, tag));
			}
		});
		
		return btn;
	}

	private static final Node generateX() {
		Path x = new Path();
    	MoveTo m1 = new MoveTo(0, 0);
    	LineTo l1 = new LineTo(10, 10);
    	MoveTo m2 = new MoveTo(10, 0);
    	LineTo l2 = new LineTo(0, 10);
    	x.setStroke(Color.GREY);
    	x.setStrokeWidth(1.2);
    	x.setSmooth(true);
    	x.getElements().addAll(m1, l1, m2, l2);
    	return x;
	}
	
	public void setText(String text) {
		this.textField.setText(text);
	}
	
	public String getText() {
		return this.textField.getText();
	}
	
	public void setTags(ObservableSet<Tag> tags) {
		this.tags.set(tags);
	}
	
	public ObservableSet<Tag> getTags() {
		return this.tags.get();
	}
}

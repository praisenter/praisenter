package org.praisenter.javafx.slide;

import java.util.Collections;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideTransition;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;

public final class EditableFxSlide {
	
	final FxSlide slide;
	
	final IntegerProperty x;
	final IntegerProperty y;
	final IntegerProperty width;
	final IntegerProperty height;
	final StringProperty name;
	final LongProperty time;
	final ObjectProperty<SlidePaint> background;
	final ObjectProperty<SlideStroke> border;
	final ListProperty<SlideTransition> animations;
	final ListProperty<SlideComponent> components;
	
	final List<EditableFxSlideComponent> editables;
	
	public EditableFxSlide(FxSlide slide) {
		this.slide = slide;
		
		this.x = new SimpleIntegerProperty();
		this.y = new SimpleIntegerProperty();
		this.width = new SimpleIntegerProperty();
		this.height = new SimpleIntegerProperty();
		this.name = new SimpleStringProperty();
		this.time = new SimpleLongProperty();
		this.background = new SimpleObjectProperty<SlidePaint>();
		this.border = new SimpleObjectProperty<SlideStroke>();
		this.animations = new SimpleListProperty<SlideTransition>();
		this.components = new SimpleListProperty<SlideComponent>();
		
		final Slide sld = slide.component;
		
		// x/y
		// wire up
		this.x.set(sld.getX());
		this.y.set(sld.getY());
		this.x.addListener((obs, o, n) -> {
			int v = n.intValue();
			sld.setX(v);
		});
		this.y.addListener((obs, o, n) -> {
			int v = n.intValue();
			sld.setY(v);
		});

		// width/height
		// wire up
		this.width.set(sld.getWidth());
		this.height.set(sld.getHeight());
		this.width.addListener((obs, o, n) -> {
			int v = n.intValue();
			int ch = height.get();
			sld.setWidth(v);
			JavaFxNodeHelper.setSize(this.foregroundNode, v, ch);
		});
		this.height.addListener((obs, o, n) -> {
			int v = n.intValue();
			int cw = width.get();
			sld.setHeight(v);
			JavaFxNodeHelper.setSize(this.foregroundNode, cw, v);
		});

		// border
		// wire up
		this.border.set(sld.getBorder());
		this.border.addListener((obs, o, n) -> {
			sld.setBorder(n);
			Border border = new Border(this.slide.getBorderStroke(n));
			this.slide.borderNode.setBorder(border);
		});

		// background
		// wire up
		this.background.set(sld.getBackground());
		this.background.addListener((obs, o, n) -> {
			sld.setBackground(n);
			Background background = this.slide.getBackground(n);
			this.backgroundNode.setBackground(background);
		});
		
		// name
		// wire up
		this.name.set(sld.getName());
		this.name.addListener((obs, o, n) -> {
			sld.setName(n);
		});
		
		// time
		// wire up
		this.time.set(sld.getTime());
		this.time.addListener((obs, o, n) -> {
			sld.setTime(n.intValue());
		});
		
		// components
		// wire up
		this.components.set(FXCollections.observableArrayList(sld.getComponents(SlideComponent.class)));
		this.components.addListener((Change<? extends SlideComponent> c) -> {
			while (c.next()) {
				if (c.wasPermutated()) {
					// this means they were reordered
					// this will happen when an object is set backward or
					// forward and the list is re-sorted
					// reorder the components in the foreground
					for (int i = c.getFrom(); i < c.getTo(); ++i) {
						Collections.swap(this.contentNode.getChildren(), i, c.getPermutation(i));
						Collections.swap(this.children, i, c.getPermutation(i));
					}
				} else if (c.wasUpdated()) {
					// elements between from and to were updated
					// this means that items a certain indexes were replaced
					// TODO this shouldn't happen, but i suppose its possible
				} else {
					// items were either removed or added
					for (SlideComponent remitem : c.getRemoved()) {
						// remove from the children and from the foreground
						component.removeComponent(remitem);
						// remove based on id from the scene graph
						contentNode.getChildren().removeIf(n -> n.getUserData().equals(remitem.getId()));
						// remove it from the children list too
						children.removeIf(n -> n.component.getId().equals(remitem.getId()));
					}
					for (SlideComponent additem : c.getAddedSubList()) {
						// add to the children and foreground
						component.addComponent(additem);
						// convert it
						EditableFxSlideComponent component = new EditableFxSlideComponent(this.context, additem, this.mode);
						// add it to the foreground and the children list
						contentNode.getChildren().add(component.node);
						children.add(component);
					}
				}
			 }
		});
		
		// TODO animations
	}
}

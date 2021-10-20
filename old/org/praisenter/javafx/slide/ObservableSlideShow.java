package org.praisenter.javafx.slide;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.praisenter.javafx.controls.FlowListCell;
import org.praisenter.slide.SlideAssignment;
import org.praisenter.slide.SlideShow;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public final class ObservableSlideShow {
	private final SlideShow show;
	
	private final StringProperty name = new SimpleStringProperty();
	private final BooleanProperty loop = new SimpleBooleanProperty();
	private final ObservableList<SlideAssignment> slides = FXCollections.observableArrayList();
	
	public ObservableSlideShow(SlideShow show) {
		this.show = show;
		
		this.name.set(show.getName());
		this.loop.set(show.loop());
		this.slides.addAll(show.getSlides());
		
		this.name.addListener((obs, ov, nv) -> {
			show.setName(nv);
		});
		this.loop.addListener((obs, ov, nv) -> {
			show.setLoop(nv);
		});
		
		this.slides.addListener((ListChangeListener.Change<? extends SlideAssignment> changes) -> {
			// iterate the changes
			List<SlideAssignment> slides = show.getSlides();
			while (changes.next()) {
	             if (changes.wasPermutated()) {
                	 // reorder
	            	 int from = changes.getFrom();
	            	 int to = changes.getTo();
	            	// re-order a sub list so we don't have duplicate nodes in the scene graph
	            	 List<SlideAssignment> range = new ArrayList<SlideAssignment>(slides.subList(from, to));
                     for (int i = from; i < to; ++i) {
                    	 int j = changes.getPermutation(i);
                    	 range.set(j - from, slides.get(i));
                     }
                     // now replace this in the real list
                     slides.subList(from, to).clear();
                     slides.addAll(from, range);
                 } else if (changes.wasUpdated()) {
                	 // not sure what to do here
                 } else {
                     for (SlideAssignment remitem : changes.getRemoved()) {
                         slides.removeIf(id -> id.equals(remitem));
                     }
                     // clear from selections
                     int i = changes.getFrom();
                     for (SlideAssignment additem : changes.getAddedSubList()) {
                    	 if (i >= 0) {
                    		 slides.add(i++, additem);
                    	 } else {
                    		 slides.add(additem);
                    	 }
                     }
                 }
	         }
		});
	}
	
	// name
	
	public String getName() {
		return this.name.get();
	}
	
	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}
	
	// loop
	
	public boolean loop() {
		return this.loop.get();
	}
	
	public void setLoop(boolean flag) {
		this.loop.set(flag);
	}
	
	public BooleanProperty loopProperty() {
		return this.loop;
	}
	
	// slides
	
	public ObservableList<SlideAssignment> getSlides() {
		return this.slides;
	}
	
	public SlideShow getSlideShow() {
		return this.show;
	}
}

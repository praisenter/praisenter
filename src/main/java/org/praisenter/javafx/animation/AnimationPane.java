package org.praisenter.javafx.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.praisenter.javafx.FlowListView;
import org.praisenter.javafx.TimeSpanTextFormatter;
import org.praisenter.javafx.easing.Easings;
import org.praisenter.resources.translations.Translations;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public final class AnimationPane extends BorderPane {
	
	final IntegerProperty animationId = new SimpleIntegerProperty();
	final IntegerProperty easingId = new SimpleIntegerProperty();
	final LongProperty duration = new SimpleLongProperty();
	final LongProperty delay = new SimpleLongProperty();
	
	public AnimationPane() {
		// setup the animation selection
		Set<Integer> animationIds = Animations.getAnimationIds();
		List<AnimationOption> options = new ArrayList<AnimationOption>();
		
		for (Integer id : animationIds) {
			AnimationOption option = new AnimationOption(id, Translations.get("animation." + id + ".name"), new Image("/org/praisenter/resources/animation.10.png"));
			options.add(option);
		}
		Collections.sort(options);
		
		FlowListView<AnimationOption> aniListPane = new FlowListView<AnimationOption>(new AnimationOptionCellFactory());
		aniListPane.itemsProperty().set(FXCollections.observableArrayList(options));
		aniListPane.setOrientation(Orientation.HORIZONTAL);
		
		// setup the easing selection
		Set<Integer> easingIds = Easings.getEasingIds();
		List<AnimationOption> easingOptions = new ArrayList<AnimationOption>();
		
		for (Integer id : easingIds) {
			AnimationOption option = new AnimationOption(id, Translations.get("easing." + id + ".name"), new Image("/org/praisenter/resources/animation.10.png"));
			easingOptions.add(option);
		}
		Collections.sort(easingOptions);
		
		FlowListView<AnimationOption> easingListPane = new FlowListView<AnimationOption>(new AnimationOptionCellFactory());
		easingListPane.itemsProperty().set(FXCollections.observableArrayList(easingOptions));
		easingListPane.setOrientation(Orientation.VERTICAL);
		
		// setup the animation config
		
		TextField txtDuration = new TextField();
		txtDuration.setPromptText("in milliseconds");
		TextField txtDelay = new TextField();
		txtDelay.setPromptText("in milliseconds");
		
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(5));
		grid.setHgap(2);
		grid.setVgap(2);
		
		grid.add(new Label("Duration"), 0, 0);
		grid.add(txtDuration, 1, 0);
		
		grid.add(new Label("Delay"), 0, 1);
		grid.add(txtDelay, 1, 1);
		
		ScrollPane scrAnimations = new ScrollPane(aniListPane);
		scrAnimations.setFitToWidth(true);
		scrAnimations.setPrefHeight(300);
		
		ScrollPane scrEasings = new ScrollPane(easingListPane);
		scrEasings.setFitToHeight(true);
		scrEasings.setPrefHeight(115);
		
		TitledPane ttlProperties = new TitledPane("Settings", grid);
		ttlProperties.prefHeightProperty().bind(scrAnimations.heightProperty());
		
		this.setCenter(scrAnimations);
		this.setRight(ttlProperties);
		this.setBottom(scrEasings);
	}
}

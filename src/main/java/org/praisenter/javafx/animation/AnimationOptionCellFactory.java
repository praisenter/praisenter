package org.praisenter.javafx.animation;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import org.praisenter.javafx.FlowListItem;

public final class AnimationOptionCellFactory implements Callback<AnimationOption, FlowListItem<AnimationOption>> {
	@Override
	public FlowListItem<AnimationOption> call(AnimationOption option) {
		FlowListItem<AnimationOption> cell = new FlowListItem<AnimationOption>(option);
		
		cell.setPrefSize(100, 80);
		
		String name = null;
		
		name = option.name;
    	// setup the thumbnail image
    	final ImageView thumb = new ImageView(option.image);
    	cell.getChildren().add(thumb);
    	
    	// setup the media name label
    	final Label label = new Label();
    	label.setText(name);
    	label.setWrapText(true);
    	label.setTextAlignment(TextAlignment.CENTER);
    	label.setTooltip(new Tooltip(name));
    	label.setPadding(new Insets(5, 0, 0, 0));
		
    	// add the image and label to the cell
    	cell.getChildren().addAll(label);
    	
		return cell;
	}
}

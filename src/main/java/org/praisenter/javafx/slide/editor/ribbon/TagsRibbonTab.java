package org.praisenter.javafx.slide.editor.ribbon;

import org.praisenter.Tag;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.TagEvent;
import org.praisenter.javafx.TagListView;
import org.praisenter.javafx.command.CommandFactory;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.javafx.slide.editor.commands.AddTagEditCommand;
import org.praisenter.javafx.slide.editor.commands.RemoveTagEditCommand;
import org.praisenter.javafx.slide.editor.commands.SlideEditorCommandFactory;

import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

final class TagsRibbonTab extends SlideRegionRibbonTab<ObservableSlide<?>> {
	private final TagListView lstTags;
	
	public TagsRibbonTab(SlideEditorContext context) {
		super(context, "Tags");
		
		this.lstTags = new TagListView(context.getContext().getTags());
		
		// layout
		
		HBox row1 = new HBox(2, this.lstTags);
		VBox layout = new VBox(2, row1);
		this.container.setCenter(layout);
	
		// events
		
		this.context.slideProperty().addListener((obs, ov, nv) -> {
			mutating = true;
			if (nv != null) {
				this.lstTags.tagsProperty().clear();
				this.lstTags.tagsProperty().addAll(nv.getTags());
			}
			mutating = false;
		});
		
		this.lstTags.addEventHandler(TagEvent.ALL, new EventHandler<TagEvent>() {
			@Override
			public void handle(TagEvent event) {
				if (mutating) return;
				Tag tag = event.getTag();
				
				EditCommand command = null;
				if (event.getEventType() == TagEvent.ADDED) {
					command = new AddTagEditCommand(
							lstTags, context.getSlide(), tag, 
							// select the component
							SlideEditorCommandFactory.select(context.selectedProperty(), context.getSlide()),
							// set the TextArea, focus it, and set the caret position
							CommandFactory.focus(lstTags));
				} else if (event.getEventType() == TagEvent.REMOVED) {
					command = new RemoveTagEditCommand(
							lstTags, context.getSlide(), tag, 
							// select the component
							SlideEditorCommandFactory.select(context.selectedProperty(), context.getSlide()),
							// set the TextArea, focus it, and set the caret position
							CommandFactory.focus(lstTags));					
				}
				
//				ObservableSlide<?> slide = context.getSlide();
//				if (event.getEventType() == TagEvent.ADDED) {
//					slide.addTag(tag);
//				} else if (event.getEventType() == TagEvent.REMOVED) {
//					slide.removeTag(tag);
//				}
//				notifyComponentChanged();
				context.applyCommand(command);
			}
        });
	}
}

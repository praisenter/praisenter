package org.praisenter.javafx.slide.editor.ribbon;

import org.praisenter.data.Tag;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.controls.TagEvent;
import org.praisenter.javafx.controls.TagListView;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.editor.SlideEditorContext;
import org.praisenter.javafx.slide.editor.commands.AddTagEditCommand;
import org.praisenter.javafx.slide.editor.commands.RemoveTagEditCommand;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

final class TagsRibbonTab extends SlideRegionRibbonTab<ObservableSlide<?>> {
	private final TagListView lstTags;
	
	public TagsRibbonTab(SlideEditorContext context) {
		super(context, "Tags");
		
		this.lstTags = new TagListView(context.getPraisenterContext().getTags());
		
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
		
		this.lstTags.addEventHandler(TagEvent.ALL, event -> {
			if (this.mutating) return;
			
			Tag tag = event.getTag();
			EditCommand command = null;
			if (event.getEventType() == TagEvent.ADDED) {
				command = new AddTagEditCommand(context.getSlide(), tag, context.selectedProperty(), this.lstTags);
			} else if (event.getEventType() == TagEvent.REMOVED) {
				command = new RemoveTagEditCommand(context.getSlide(), tag, context.selectedProperty(), this.lstTags);
			}
			applyCommand(command);
        });
	}
}

package org.praisenter.javafx.slide.editor;

import org.praisenter.Tag;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.TagEvent;
import org.praisenter.javafx.TagListView;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

final class TagsRibbonTab extends EditorRibbonTab<ObservableSlide<?>> {
	private final PraisenterContext context;
	
	private final TagListView lstTags;
	
	public TagsRibbonTab(PraisenterContext context) {
		super("Tags");
		
		this.context = context;
		
		lstTags = new TagListView(context.getTags());
		
		// layout
		
		HBox row1 = new HBox(2, this.lstTags);
		VBox layout = new VBox(2, row1);
		this.container.setCenter(layout);
	
		// events
		
		this.component.addListener((obs, ov, nv) -> {
			mutating = true;
			ObservableSlide<?> comp = this.component.get();
			if (comp != null) {
				this.lstTags.tagsProperty().clear();
				this.lstTags.tagsProperty().addAll(nv.getTags());
			}
			mutating = false;
		});
		
		lstTags.addEventHandler(TagEvent.ALL, new EventHandler<TagEvent>() {
			@Override
			public void handle(TagEvent event) {
				Tag tag = event.getTag();
				ObservableSlideRegion<?> comp = component.get();
				if (comp != null && comp instanceof ObservableSlide) {
					ObservableSlide<?> slide = (ObservableSlide<?>)comp;
					if (event.getEventType() == TagEvent.ADDED) {
						slide.addTag(tag);
					} else if (event.getEventType() == TagEvent.REMOVED) {
						slide.removeTag(tag);
					}
				}
			}
        });
	}
}

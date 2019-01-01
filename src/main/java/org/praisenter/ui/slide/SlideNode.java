package org.praisenter.ui.slide;

import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.media.MediaComponent;
import org.praisenter.data.slide.text.TextComponent;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.MappedList;

import javafx.beans.binding.Bindings;
import javafx.scene.layout.Pane;

final class SlideNode extends SlideRegionNode<Slide> implements Playable {
	private final Pane components;
	private final MappedList<SlideComponentNode<?>, SlideComponent> mapping;
	
	public SlideNode(GlobalContext context, Slide region) {
		super(context, region);
		
		this.components = new Pane();
		
		this.mapping = new MappedList<SlideComponentNode<?>, SlideComponent>(region.getComponents(), (SlideComponent c) -> {
			if (c instanceof TextComponent) {
				return new TextComponentNode(context, (TextComponent)c);
			} else if (c instanceof MediaComponent) {
				return new MediaComponentNode(context, (MediaComponent)c);
			} else {
				throw new IllegalArgumentException("Unknown type '" + c.getClass().getName() + "' when generating slide UI.");
			}
		});
		Bindings.bindContent(this.components.getChildren(), this.mapping);
		
		this.content.getChildren().add(this.components);
		
		// TODO animations
		// TODO transitions without restarting video -- Maybe for this we actually update the slide placeholders - that's really the only place it should be needed
	}

	@Override
	public void play() {
		super.play();
		for (SlideComponentNode<?> child : this.mapping) {
			child.play();
		}
	}
	
	@Override
	public void pause() {
		super.pause();
		for (SlideComponentNode<?> child : this.mapping) {
			child.pause();
		}
	}
	
	@Override
	public void stop() {
		super.stop();
		for (SlideComponentNode<?> child : this.mapping) {
			child.stop();
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		for (SlideComponentNode<?> child : this.mapping) {
			child.dispose();
		}
	}
}

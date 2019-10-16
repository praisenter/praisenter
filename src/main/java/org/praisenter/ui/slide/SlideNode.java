package org.praisenter.ui.slide;

import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.media.MediaComponent;
import org.praisenter.data.slide.text.TextComponent;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.MappedList;
import org.praisenter.ui.Playable;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;

final class SlideNode extends SlideRegionNode<Slide> implements Playable {
	private final Pane components;
	private final MappedList<SlideComponentNode<?>, SlideComponent> mapping;
	private final ObservableList<SlideComponentNode<?>> mappingUnmodifiable;
	
	public SlideNode(GlobalContext context, Slide region) {
		super(context, region);
		
		this.components = new Pane();
		
		this.mapping = new MappedList<SlideComponentNode<?>, SlideComponent>(region.getComponents(), (SlideComponent c) -> {
			if (c instanceof TextComponent) {
				TextComponentNode tcn = new TextComponentNode(context, (TextComponent)c);
				tcn.modeProperty().bind(this.mode);
				return tcn;
			} else if (c instanceof MediaComponent) {
				MediaComponentNode mcn = new MediaComponentNode(context, (MediaComponent)c);
				mcn.modeProperty().bind(this.mode);
				return mcn;
			} else {
				throw new IllegalArgumentException("Unknown type '" + c.getClass().getName() + "' when generating slide UI.");
			}
		});
		Bindings.bindContent(this.components.getChildren(), this.mapping);
		this.mappingUnmodifiable = FXCollections.unmodifiableObservableList(this.mapping);
		
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
	
	public ObservableList<SlideComponentNode<?>> getSlideComponentNodesUnmodifiable() {
		return this.mappingUnmodifiable;
	}
}

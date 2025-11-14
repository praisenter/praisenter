package org.praisenter.ui.slide;

import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.media.MediaComponent;
import org.praisenter.data.slide.text.TextComponent;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.Playable;
import org.praisenter.ui.bind.MappedList2;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;

final class SlideNode extends SlideRegionNode<Slide> implements Playable {
	private final Pane components;
	private final MappedList2<SlideComponentNode<?>, SlideComponent> mapping;
	private final ObservableList<SlideComponentNode<?>> mappingUnmodifiable;
	
	public SlideNode(GlobalContext context, Slide region) {
		super(context, region);

		this.components = new Pane();
		
		this.mapping = new MappedList2<SlideComponentNode<?>, SlideComponent>(region.getComponents(), (SlideComponent c) -> {
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
	
	public boolean isReady() {
		if (!this.background.isMediaReady()) {
			return false;
		}
		
		for (SlideComponentNode<?> sc : this.mappingUnmodifiable) {
			if (!sc.background.isMediaReady()) {
				return false;
			}
			if (sc instanceof MediaComponentNode) {
				MediaComponentNode mcn = (MediaComponentNode)sc;
				if (!mcn.media.isMediaReady()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public ObservableList<SlideComponentNode<?>> getSlideComponentNodesUnmodifiable() {
		return this.mappingUnmodifiable;
	}
}

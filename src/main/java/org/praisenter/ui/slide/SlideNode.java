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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

final class SlideNode extends SlideRegionNode<Slide> implements Playable {
	private final Pane components;
	private final ImageView routeBibleQr;
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

		this.routeBibleQr = new ImageView();
		this.routeBibleQr.setFitWidth(112);
		this.routeBibleQr.setFitHeight(112);
		this.routeBibleQr.setPreserveRatio(true);
		this.routeBibleQr.setSmooth(true);
		this.routeBibleQr.managedProperty().bind(this.routeBibleQr.visibleProperty());
		this.routeBibleQr.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
			return RouteBibleQrOverlay.shouldShow(
				context.getWorkspaceConfiguration().isRouteBibleQrEnabled(),
				region.getPlaceholderData(),
				this.mode.get()
			);
		}, region.placeholderDataProperty(), context.getWorkspaceConfiguration().routeBibleQrEnabledProperty(), this.mode));
		this.routeBibleQr.imageProperty().bind(Bindings.createObjectBinding(() -> {
			if (!RouteBibleQrOverlay.shouldShow(
				context.getWorkspaceConfiguration().isRouteBibleQrEnabled(),
				region.getPlaceholderData(),
				this.mode.get()
			)) {
				return null;
			}

			String referenceLabel = RouteBibleQrOverlay.getReferenceLabel(region.getPlaceholderData());
			if (referenceLabel == null) {
				return null;
			}

			return new Image(RouteBibleQrOverlay.buildQrUrl(referenceLabel), 112, 112, true, true, true);
		}, region.placeholderDataProperty(), context.getWorkspaceConfiguration().routeBibleQrEnabledProperty(), this.mode));
		StackPane.setAlignment(this.routeBibleQr, Pos.BOTTOM_RIGHT);
		StackPane.setMargin(this.routeBibleQr, new Insets(16));

		this.content.getChildren().addAll(this.components, this.routeBibleQr);
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

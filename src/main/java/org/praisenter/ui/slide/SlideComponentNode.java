package org.praisenter.ui.slide;

import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.effects.SlideShadow;
import org.praisenter.data.slide.graphics.SlideStroke;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.slide.convert.EffectConverter;

import javafx.beans.binding.Bindings;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

abstract class SlideComponentNode<T extends SlideComponent> extends SlideRegionNode<T> {
	protected SlideComponentNode(GlobalContext context, T region) {
		super(context, region);
		
		this.layoutXProperty().bind(this.region.xProperty());
		this.layoutYProperty().bind(this.region.yProperty());
		
		this.container.effectProperty().bind(Bindings.createObjectBinding(() -> {
			return this.computeEffect();
		}, this.region.shadowProperty(), this.region.glowProperty()));

		this.background.clipProperty().bind(Bindings.createObjectBinding(() -> {
			SlideStroke border = this.region.getBorder();
			return this.getBorderBasedClip(border);
		}, this.region.borderProperty()));
		
//		this.content.clipProperty().bind(Bindings.createObjectBinding(() -> {
//			SlideStroke border = this.region.getBorder();
//			return this.getBorderBasedClip(border);
//		}, this.region.borderProperty()));
	}

	private final Shape getBorderBasedClip(SlideStroke stroke) {
		if (stroke != null) {
			double radius = stroke.getRadius();
			if (radius > 0) {
				Rectangle r = new Rectangle();
				r.widthProperty().bind(this.widthProperty());
				r.heightProperty().bind(this.heightProperty());
				r.setArcHeight(2 * radius);
				r.setArcWidth(2 * radius);
				return r;
			}
		}
		return null;
	}
	
	private Effect computeEffect() {
		SlideShadow ss = this.region.getShadow();
		SlideShadow sg = this.region.getGlow();
		EffectBuilder builder = EffectBuilder.create();
		Effect shadow = EffectConverter.toJavaFX(ss);
		Effect glow = EffectConverter.toJavaFX(sg);
		builder.add(shadow, shadow != null && shadow instanceof InnerShadow ? 10 : 30);
		builder.add(glow, glow != null && glow instanceof InnerShadow ? 20 : 40);
		return builder.build();
	}
}

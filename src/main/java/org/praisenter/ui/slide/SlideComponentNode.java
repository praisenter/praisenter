package org.praisenter.ui.slide;

import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.effects.SlideShadow;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.slide.convert.EffectConverter;

import javafx.beans.binding.Bindings;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;

abstract class SlideComponentNode<T extends SlideComponent> extends SlideRegionNode<T> {
	protected SlideComponentNode(GlobalContext context, T region) {
		super(context, region);
		
		this.layoutXProperty().bind(this.region.xProperty());
		this.layoutYProperty().bind(this.region.yProperty());
		
		this.container.effectProperty().bind(Bindings.createObjectBinding(() -> {
			return this.computeEffect();
		}, this.region.shadowProperty(), this.region.glowProperty()));
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

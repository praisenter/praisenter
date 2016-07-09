package org.praisenter.javafx.slide;

import javafx.scene.effect.Effect;

final class OrderedEffect implements Comparable<OrderedEffect> {
	final int order;
	final Effect effect;
	
	public OrderedEffect(int order, Effect effect) {
		this.order = order;
		this.effect = effect;
	}
	
	@Override
	public int compareTo(OrderedEffect o) {
		return this.order - o.order;
	}

	public int getOrder() {
		return this.order;
	}

	public Effect getEffect() {
		return this.effect;
	}
}

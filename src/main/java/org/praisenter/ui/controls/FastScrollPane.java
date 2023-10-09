package org.praisenter.ui.controls;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class FastScrollPane extends ScrollPane {

	private final double speed;
	
	public FastScrollPane() {
		this(1.0);
	}
	
	public FastScrollPane(double speed) {
		super();
		this.speed = speed;
		setupFasterScrolling();
	}

	public FastScrollPane(Node content, double speed) {
		super(content);
		this.speed = speed;
		setupFasterScrolling();
	}

	private void setupFasterScrolling() {
		this.getContent().setOnScroll(scrollEvent -> {
		    double deltaY = scrollEvent.getDeltaY() * this.speed;
		    double contentHeight = this.getContent().getBoundsInLocal().getHeight();
		    double scrollPaneHeight = this.getHeight();
		    double diff = contentHeight - scrollPaneHeight;
		    if (diff < 1) diff = 1;
		    double vvalue = this.getVvalue();
		    this.setVvalue(vvalue + -deltaY/diff);
		});
	}
}

package org.praisenter.ui.controls;

import javafx.scene.control.SplitPane;
import javafx.scene.control.skin.SplitPaneSkin;

public final class SimpleSplitPaneSkin extends SplitPaneSkin {

	public SimpleSplitPaneSkin(SplitPane splitPane) {
		super(splitPane);
	}

	@Override
	protected void layoutChildren(double x, double y, double w, double h) {
		// JAVABUG (L) 10/13/23 [workaround] SplitPane doesn't honor divider positions https://stackoverflow.com/a/44284465
		double[] dividerPositions = getSkinnable().getDividerPositions();
		super.layoutChildren(x, y, w, h);
		getSkinnable().setDividerPositions(dividerPositions);
	}
}
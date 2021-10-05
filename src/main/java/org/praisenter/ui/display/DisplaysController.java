package org.praisenter.ui.display;

import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.MappedList;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public final class DisplaysController extends BorderPane {
	private static final String DISPLAY_CONTROLLERS_CSS = "p-display-controllers";
	
	private final GlobalContext context;
	
	private final MappedList<Node, DisplayTarget> displayTargetToNodeMapping;
	
	public DisplaysController(GlobalContext context) {
		this.context = context;
		
		this.displayTargetToNodeMapping = new MappedList<Node, DisplayTarget>(context.getDisplayManager().getDisplayTargets(), (DisplayTarget d) -> {
			return new DisplayController(context, d);
		});
		
		HBox controllers = new HBox();
		controllers.getStyleClass().add(DISPLAY_CONTROLLERS_CSS);
		controllers.setFillHeight(true);
		Bindings.bindContent(controllers.getChildren(), this.displayTargetToNodeMapping);
//		controllers.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
		
		ScrollPane scr = new ScrollPane(controllers);
		scr.setFitToHeight(true);
		scr.setVbarPolicy(ScrollBarPolicy.NEVER);
//		scr.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
		
		this.setCenter(scr);
	}
}

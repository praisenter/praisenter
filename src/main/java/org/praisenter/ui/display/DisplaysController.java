package org.praisenter.ui.display;

import java.util.List;

import org.praisenter.data.workspace.Display;
import org.praisenter.data.workspace.DisplayRole;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.MappedList;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

public final class DisplaysController extends BorderPane {
	private final GlobalContext context;
	
	private final MappedList<Node, DisplayTarget> displayTargetToNodeMapping;
	
	public DisplaysController(GlobalContext context) {
		this.context = context;
		
		this.displayTargetToNodeMapping = new MappedList<Node, DisplayTarget>(context.getDisplayManager().getDisplayTargets(), (DisplayTarget d) -> {
			TitledPane ctr = new TitledPane(d.toString(), new DisplayController(context, d));
//			ctr.setMaxWidth(400);
			ctr.setMinWidth(400);
			ctr.setCollapsible(false);
			ctr.setMaxHeight(Double.MAX_VALUE);
//			ctr.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
			return ctr;
		});
		
		HBox controllers = new HBox();
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

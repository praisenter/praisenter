package org.praisenter.ui.pages;

import java.util.ArrayList;

import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bind.EmptyItemList;
import org.praisenter.ui.bind.MappedList;
import org.praisenter.ui.controls.FastScrollPane;
import org.praisenter.ui.display.DisplayController;
import org.praisenter.ui.display.DisplayTarget;
import org.praisenter.ui.translations.Translations;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public final class PresentPage extends BorderPane {
	private static final String PRESENT_PAGE_CLASS = "p-present-page";
	
	private static final String PRESENT_PAGE_CONTROLLER_LIST_CSS = "p-present-page-controller-list";
	private static final String PRESENT_PAGE_CONTROLLER_CSS = "p-present-page-controller";
	private static final String PRESENT_PAGE_CONTROLLER_ADD_CSS = "p-present-page-controller-add";
	
	private final ObservableList<DisplayTarget> displayTargets;
	private final ObservableList<Node> displayControllers;
	private final ObservableList<MenuItem> inactiveDisplayControllers;
	
	public PresentPage(GlobalContext context) {
		// create an observable list that listens to the active property of the elements
		this.displayTargets = FXCollections.observableList(new ArrayList<DisplayTarget>(), target -> {
			return new Observable[] { 
				target.getDisplayConfiguration().activeProperty() 
			};
		});
		
		// then bind the list content to the set of display targets available
		Bindings.bindContent(this.displayTargets, context.getDisplayManager().getDisplayTargets());

		FilteredList<DisplayTarget> inactiveDisplayTargets = new FilteredList<>(this.displayTargets, target -> {
			return !target.getDisplayConfiguration().isActive();
		});

		// the no other displays option
		MenuItem mnuNone = new MenuItem(Translations.get("display.none"));
		mnuNone.visibleProperty().bind(Bindings.size(inactiveDisplayTargets).lessThanOrEqualTo(0));
		
		// the other displays option
		this.inactiveDisplayControllers = new EmptyItemList<>(new MappedList<MenuItem, DisplayTarget>(this.displayTargets, (DisplayTarget target) -> {
			MenuItem item = new MenuItem();
			item.setText(target.toString());
			item.visibleProperty().bind(target.getDisplayConfiguration().activeProperty().not());
			item.setOnAction(e -> {
				target.getDisplayConfiguration().setActive(true);
			});
			return item;
		}), mnuNone);
		
		MenuButton mnuAddController = new MenuButton(Translations.get("display.add"));
		Bindings.bindContent(mnuAddController.getItems(), this.inactiveDisplayControllers);
		
		this.displayControllers = new MappedList<Node, DisplayTarget>(this.displayTargets, (DisplayTarget target) -> {
			DisplayController dc = new DisplayController(context, target);
			dc.visibleProperty().bind(target.getDisplayConfiguration().activeProperty());
			dc.managedProperty().bind(dc.visibleProperty());
			dc.getStyleClass().add(PRESENT_PAGE_CONTROLLER_CSS);
			return dc;
		});
		
		HBox controllers = new HBox();
		controllers.getStyleClass().add(PRESENT_PAGE_CONTROLLER_LIST_CSS);
		controllers.setFillHeight(true);
		Bindings.bindContent(controllers.getChildren(), this.displayControllers);

		HBox addControllerContainer = new HBox(mnuAddController);
		addControllerContainer.getStyleClass().add(PRESENT_PAGE_CONTROLLER_ADD_CSS);
		
		HBox layout = new HBox();
		layout.getChildren().addAll(controllers, addControllerContainer);
//		controllers.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
		
		ScrollPane scroller = new FastScrollPane(layout, 2.0);
		scroller.setFitToHeight(true);
		scroller.setVbarPolicy(ScrollBarPolicy.NEVER);
//		scr.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
		
		this.setCenter(scroller);
		this.getStyleClass().add(PRESENT_PAGE_CLASS);
	}
}

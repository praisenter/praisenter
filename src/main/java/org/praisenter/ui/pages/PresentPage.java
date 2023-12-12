package org.praisenter.ui.pages;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.workspace.DisplayConfiguration;
import org.praisenter.data.workspace.DisplayType;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.bind.AdditionalItemsList;
import org.praisenter.ui.bind.EmptyItemList;
import org.praisenter.ui.bind.MappedList;
import org.praisenter.ui.controls.Dialogs;
import org.praisenter.ui.controls.FastScrollPane;
import org.praisenter.ui.controls.WindowHelper;
import org.praisenter.ui.display.DisplayController;
import org.praisenter.ui.display.DisplayTarget;
import org.praisenter.ui.display.NDIDisplaySettingsPane;
import org.praisenter.ui.translations.Translations;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class PresentPage extends BorderPane implements Page {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String PRESENT_PAGE_CLASS = "p-present-page";
	
	private static final String PRESENT_PAGE_CONTROLLER_LIST_CSS = "p-present-page-controller-list";
	private static final String PRESENT_PAGE_CONTROLLER_CSS = "p-present-page-controller";
	private static final String PRESENT_PAGE_CONTROLLER_ADD_CSS = "p-present-page-controller-add";
	
	private final ObservableList<DisplayTarget> displayTargets;
	private final ObservableList<DisplayController> displayControllers;
	private final ObservableList<MenuItem> inactiveScreenDisplayControllers;
	private final ObservableList<MenuItem> inactiveNDIDisplayControllers;
	
	private final Stage dlgNDIDisplay;
	
	public PresentPage(GlobalContext context) {
		// create an observable list that listens to the active property of the elements
		this.displayTargets = FXCollections.observableList(new ArrayList<DisplayTarget>(), target -> {
			return new Observable[] { 
				target.getDisplayConfiguration().activeProperty() 
			};
		});
		
		// then bind the list content to the set of display targets available
		Bindings.bindContent(this.displayTargets, context.getDisplayManager().getDisplayTargets());

		FilteredList<DisplayTarget> inactiveScreenDisplayTargets = new FilteredList<>(this.displayTargets, target -> {
			// null check is for versions prior to 3.1.2
			return !target.getDisplayConfiguration().isActive() && (target.getDisplayConfiguration().getType() == DisplayType.SCREEN || target.getDisplayConfiguration().getType() == null);
		});
		FilteredList<DisplayTarget> inactiveNDIDisplayTargets = new FilteredList<>(this.displayTargets, target -> {
			return !target.getDisplayConfiguration().isActive() && target.getDisplayConfiguration().getType() == DisplayType.NDI;
		});

		// the no other displays option
		MenuItem mnuScreenNone = new MenuItem(Translations.get("display.screen.none"));
		mnuScreenNone.visibleProperty().bind(Bindings.size(inactiveScreenDisplayTargets).lessThanOrEqualTo(0));
		
		this.inactiveScreenDisplayControllers = new EmptyItemList<>(new MappedList<MenuItem, DisplayTarget>(inactiveScreenDisplayTargets, (DisplayTarget target) -> {
			MenuItem item = new MenuItem();
			item.setText(target.toString());
			item.visibleProperty().bind(target.getDisplayConfiguration().activeProperty().not());
			item.setOnAction(e -> {
				target.getDisplayConfiguration().setActive(true);
			});
			return item;
		}), mnuScreenNone);

		NDIDisplaySettingsPane lep = new NDIDisplaySettingsPane(context);
		
		Button btnCancel = new Button(Translations.get("cancel"));
		Button btnOk = new Button(Translations.get("ok"));
		btnOk.setDefaultButton(true);
		
		ButtonBar.setButtonData(btnCancel, ButtonData.CANCEL_CLOSE);
		ButtonBar.setButtonData(btnOk, ButtonData.OK_DONE);
		
		this.dlgNDIDisplay = Dialogs.createStageDialog(
				context, 
				Translations.get("display.add.ndi.new"), 
				StageStyle.DECORATED, 
				Modality.WINDOW_MODAL, 
				lep, 
				btnCancel, btnOk);
		
		this.dlgNDIDisplay.setResizable(true);
		this.dlgNDIDisplay.setMinWidth(500);
		this.dlgNDIDisplay.setMinHeight(600);
		this.dlgNDIDisplay.setWidth(500);
		this.dlgNDIDisplay.setHeight(600);
		btnCancel.setOnAction(e -> {
			this.dlgNDIDisplay.hide();
		});
		btnOk.setOnAction(e -> {
			DisplayConfiguration dc = lep.getValue();
			context.getWorkspaceConfiguration().getDisplayConfigurations().add(dc);
			this.dlgNDIDisplay.hide();
		});
		btnOk.disableProperty().bind(Bindings.createBooleanBinding(() -> {
			// validation
			DisplayConfiguration dc = lep.getValue();
			if (dc == null)
				return true;
			
			if (dc.getName() == null || dc.getName().isBlank()) 
				return true;
			
			if (dc.getWidth() <= 0 || dc.getHeight() <= 0)
				return true;
			
			if (dc.getFramesPerSecond() <= 0) 
				return true;
			
			return false;
		}, lep.valueProperty()));
		
		MenuItem mnuNDINone = new MenuItem(Translations.get("display.ndi.none"));
		mnuNDINone.visibleProperty().bind(Bindings.size(inactiveNDIDisplayTargets).lessThanOrEqualTo(0));
		MenuItem mnuAddNewNDI = new MenuItem(Translations.get("display.add.ndi.new"));
		mnuAddNewNDI.setOnAction(e -> {
			this.dlgNDIDisplay.setWidth(500);
			this.dlgNDIDisplay.setHeight(600);
			this.dlgNDIDisplay.setMaximized(false);
			WindowHelper.centerOnParent(this.getScene().getWindow(), this.dlgNDIDisplay);
		    this.dlgNDIDisplay.showAndWait();
		});
		
		MenuItem mnuNDILink = new MenuItem(Translations.get("ndi.link"));
		mnuNDILink.setOnAction(e -> {
			openUrl(Translations.get("ndi.link"));
		});
		
		List<MenuItem> mnuBefore = new ArrayList<>();
		mnuBefore.add(mnuAddNewNDI);
		mnuBefore.add(mnuNDILink);
		mnuBefore.add(new SeparatorMenuItem());
		mnuBefore.add(mnuNDINone);
		List<MenuItem> mnuAfter = new ArrayList<>();
		
		this.inactiveNDIDisplayControllers = new AdditionalItemsList<>(new MappedList<MenuItem, DisplayTarget>(inactiveNDIDisplayTargets, (DisplayTarget target) -> {
			MenuItem item = new MenuItem();
			item.setText(target.getDisplayConfiguration().getName());
			item.visibleProperty().bind(target.getDisplayConfiguration().activeProperty().not());
			item.setOnAction(e -> {
				target.getDisplayConfiguration().setActive(true);
			});
			return item;
		}), mnuBefore, mnuAfter);
		
		MenuButton mnuAddController = new MenuButton(Translations.get("display.add"));
		if (context.isNDIReady()) {
			Menu mnuAddScreen = new Menu(Translations.get("display.add.screen"));
			Bindings.bindContent(mnuAddScreen.getItems(), this.inactiveScreenDisplayControllers);
			Menu mnuAddNDI = new Menu(Translations.get("display.add.ndi"));
			mnuAddNDI.visibleProperty().bind(context.ndiReadyProperty());
			Bindings.bindContent(mnuAddNDI.getItems(), this.inactiveNDIDisplayControllers);
			mnuAddController.getItems().addAll(mnuAddScreen, mnuAddNDI);
		} else {
			Bindings.bindContent(mnuAddController.getItems(), this.inactiveScreenDisplayControllers);
		}
		
		this.displayControllers = new MappedList<DisplayController, DisplayTarget>(this.displayTargets, (DisplayTarget target) -> {
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
	
	@Override
	public void setDefaultFocus() {
		if (this.displayControllers.size() > 0) {
			this.displayControllers.get(0).setDefaultFocus();
		}
	}
	
	private void openUrl(String url) {
		// Desktop must be used from the AWT EventQueue
		// https://stackoverflow.com/a/65863422
		EventQueue.invokeLater(() -> {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				try {
				    Desktop.getDesktop().browse(new URI(url));
				} catch (Exception e) {
					LOGGER.error("Failed to open default browser for URL: " + url, e);
				}
			}
		});
	}
}

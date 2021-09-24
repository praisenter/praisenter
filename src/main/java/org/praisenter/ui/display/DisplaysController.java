package org.praisenter.ui.display;

import java.util.List;

import org.praisenter.data.workspace.Display;
import org.praisenter.data.workspace.DisplayRole;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.MappedList;
import org.praisenter.ui.Option;
import org.praisenter.ui.bind.BindingHelper;
import org.praisenter.ui.controls.FormFieldSet;
import org.praisenter.ui.translations.Translations;

import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public final class DisplaysController extends BorderPane {
	private static final String DISPLAYS_CLASS = "p-displays";
	private static final String DISPLAY_HEADER_TEXT_CLASS = "p-display-header-text";
	private static final String DISPLAY_HEADER_CLASS = "p-display-header";
	
	private final GlobalContext context;
	
	private final MappedList<Node, DisplayTarget> displayTargetToNodeMapping;
	
	public DisplaysController(GlobalContext context) {
		this.context = context;
		
		this.displayTargetToNodeMapping = new MappedList<Node, DisplayTarget>(context.getDisplayManager().getDisplayTargets(), (DisplayTarget d) -> {
			ObservableList<Option<DisplayRole>> displayRoleOptions = FXCollections.observableArrayList();
			displayRoleOptions.add(new Option<>(Translations.get("display.role." + DisplayRole.NONE), DisplayRole.NONE));
			displayRoleOptions.add(new Option<>(Translations.get("display.role." + DisplayRole.MAIN), DisplayRole.MAIN));
			displayRoleOptions.add(new Option<>(Translations.get("display.role." + DisplayRole.TELEPROMPT), DisplayRole.TELEPROMPT));
			displayRoleOptions.add(new Option<>(Translations.get("display.role." + DisplayRole.OTHER), DisplayRole.OTHER));
			ChoiceBox<Option<DisplayRole>> cbDisplayRole = new ChoiceBox<>(displayRoleOptions);
			
			cbDisplayRole.setValue(new Option<>(null, d.getDisplay().getRole()));
			BindingHelper.bindBidirectional(cbDisplayRole.valueProperty(), d.getDisplay().roleProperty());
			
			final DisplayIdentifier identify = new DisplayIdentifier(d.getDisplay());
			Button btnIdentify = new Button(Translations.get("display.identify"));
			btnIdentify.setOnAction(e -> {
				identify.show();
				
				Transition tx = new PauseTransition(new Duration(5000));
				tx.setOnFinished(ev -> {
					identify.hide();
				});
				
				tx.play();
			});
			
			Label lbl = new Label(d.toString());
			lbl.getStyleClass().add(DISPLAY_HEADER_TEXT_CLASS);
			
			HBox spacer = new HBox();
			spacer.setMaxWidth(Double.MAX_VALUE);
			HBox layout = new HBox(lbl, spacer, cbDisplayRole, btnIdentify);
			layout.getStyleClass().add(DISPLAY_HEADER_CLASS);
			HBox.setHgrow(spacer, Priority.ALWAYS);
			HBox.setHgrow(cbDisplayRole, Priority.SOMETIMES);
			
			DisplayController dc = new DisplayController(context, d);

//			VBox ctr = new VBox(layout, dc);
			BorderPane ctr = new BorderPane();
			ctr.setTop(layout);
			ctr.setCenter(dc);

			ctr.setMinWidth(400);
			ctr.setMaxHeight(Double.MAX_VALUE);
			return ctr;
		});
		
		HBox controllers = new HBox();
		controllers.getStyleClass().add(DISPLAYS_CLASS);
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

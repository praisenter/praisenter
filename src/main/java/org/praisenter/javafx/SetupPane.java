package org.praisenter.javafx;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.screen.ScreenConfiguration;
import org.praisenter.javafx.screen.ScreenView;
import org.praisenter.javafx.screen.ScreenViewDragDropManager;
import org.praisenter.javafx.styles.Theme;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

// TODO translate
// TODO clean up UI
// TODO add other settings (see preferences in praisenter2)

public final class SetupPane extends GridPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	public SetupPane(PraisenterContext context) {
		// the in use config and the saved config can be different if the user
		// hasn't restarted the app after changing a setting that requires it
		
		// inUseConfig = the current configuration being used by the application
		// savedConfig = the configuration that is currently saved to disk
		
		this.setHgap(5);
		this.setVgap(5);
//		this.setGridLinesVisible(true);
		
		int row = 0;
		
		// FIXME changes to configuration requires restart warning
		
		List<Option<Locale>> locales = new ArrayList<Option<Locale>>();
		for (Locale locale : Translations.SUPPORTED_LOCALES) {
			locales.add(new Option<Locale>(getLocaleName(locale), locale));
		}
		
		// ui.language (requires restart)
		Label lblLocale = new Label("Language");
		ComboBox<Option<Locale>> cmbLocale = new ComboBox<Option<Locale>>(FXCollections.observableArrayList(locales));
		cmbLocale.setValue(new Option<Locale>(null, context.getConfiguration().getLanguage()));
		this.add(lblLocale, 0, row);
		this.add(cmbLocale, 1, row++);
		
		List<Option<Theme>> themes = new ArrayList<Option<Theme>>();
		themes.add(new Option<Theme>("Default", Theme.DEFAULT));
		themes.add(new Option<Theme>("Dark", Theme.DARK));
		
		// ui.theme (requires restart)
		Label lblTheme = new Label("Theme");
		ComboBox<Option<Theme>> cmbTheme = new ComboBox<Option<Theme>>(FXCollections.observableArrayList(themes));
		cmbTheme.setValue(new Option<Theme>(null, context.getConfiguration().getTheme()));
		this.add(lblTheme, 0, row);
		this.add(cmbTheme, 1, row++);
		
		// get a screenshot of all the screens
		GridPane screenPane = new GridPane();
		screenPane.setHgap(10);
		screenPane.setVgap(10);
		
		Label lblOperatorScreen = new Label("Operator");
		Label lblPrimaryScreen = new Label("Primary");
		Label lblMusicianScreen = new Label("Musician");
		
		screenPane.add(lblOperatorScreen, 0, 1);
		screenPane.add(lblPrimaryScreen, 1, 1);
		screenPane.add(lblMusicianScreen, 2, 1);
		
		GridPane.setHalignment(lblOperatorScreen, HPos.CENTER);
		GridPane.setHalignment(lblPrimaryScreen, HPos.CENTER);
		GridPane.setHalignment(lblMusicianScreen, HPos.CENTER);
		
		// create a custom manager
		ScreenViewDragDropManager manager = new ScreenViewDragDropManager() {
			@Override
			public void swap(ScreenView view1, ScreenView view2) {
				int col1 = GridPane.getColumnIndex(view1);
				int row1 = GridPane.getRowIndex(view1);
				int col2 = GridPane.getColumnIndex(view2);
				int row2 = GridPane.getRowIndex(view2);
				screenPane.getChildren().removeAll(view1, view2);
				screenPane.add(view2, col1, row1);
				screenPane.add(view1, col2, row2);
				
				// record the changes (will save automatically)
				ScreenConfiguration sc = context.getScreenManager().getScreenConfiguration();
				if (col1 == 0) {
					sc.setOperatorScreen(view2.getDisplay());
				} else if (col1 == 1) {
					sc.setMainScreen(view2.getDisplay());
				} else if (col1 == 2) {
					sc.setMusicianScreen(view2.getDisplay());
				}
				if (col2 == 0) {
					sc.setOperatorScreen(view1.getDisplay());
				} else if (col2 == 1) {
					sc.setMainScreen(view1.getDisplay());
				} else if (col2 == 2) {
					sc.setMusicianScreen(view1.getDisplay());
				}
			}
		};
		
		InvalidationListener screenListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				ScreenConfiguration sc = context.getScreenManager().getScreenConfiguration();
				
				Map<Integer, ScreenView> views = ScreenView.createScreenViews(manager);
				
				ScreenView operator = null;
				ScreenView main = null;
				ScreenView musician = null;
				if (sc.getOperatorScreen() != null) {
					operator = views.get(sc.getOperatorScreen().getId());
				}
				if (sc.getMainScreen() != null) {
					main = views.get(sc.getMainScreen().getId());
				}
				if (sc.getMusicianScreen() != null) {
					musician = views.get(sc.getMusicianScreen().getId());
				}
				
				if (operator == null) {
					operator = ScreenView.createUnassignedScreenView(manager);
				}
				if (main == null) {
					main = ScreenView.createUnassignedScreenView(manager);
				}
				if (musician == null) {
					musician = ScreenView.createUnassignedScreenView(manager);
				}
				
				screenPane.add(operator, 0, 0);
				screenPane.add(main, 1, 0);
				screenPane.add(musician, 2, 0);
			}
		};
		
		// update when the parent changes
		parentProperty().addListener(screenListener);
		
		// update when the screens change
		Screen.getScreens().addListener(screenListener);
		
		TitledPane tp1 = new TitledPane("Display Setup", screenPane);
		tp1.setCollapsible(false);
		this.add(tp1, 0, row++, 2, 1);
		
		Button btnSave = new Button("Save changes");
		this.add(btnSave, 0, row++);
		
		btnSave.setOnAction((e) -> {
//			// build a configuration object based on the controls
//			configuration.setLanguage(cmbLocale.getValue().value);
//			configuration.setTheme(cmbTheme.getValue().value);
//			
//			configuration.getScreenMappings().clear();
//			for (ComboBox<Option<ScreenRole>> cmb : screenCombos) {
//				GraphicsDevice device = (GraphicsDevice)cmb.getUserData();
//				configuration.getScreenMappings().add(new ScreenMapping(device.getIDstring(), cmb.getValue().value));
//			}
//			
//			try {
//				// save the configuration
//				Configuration.save(configuration);
//			} catch (Exception ex) {
//				LOGGER.error("An error occurred saving the configuration: ", ex);
//				Alert alert = Alerts.exception(this.getScene().getWindow(), "Error Saving Configuration", "", "An error occurred when saving the configuration:", ex);
//				alert.show();
//			}
		});
	}
	
	private static final String getLocaleName(Locale locale) {
		return locale.getDisplayLanguage() + (locale.getDisplayCountry().isEmpty() ? "" : " (" + locale.getDisplayCountry() + ")");
	}
}

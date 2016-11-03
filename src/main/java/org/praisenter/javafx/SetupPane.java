package org.praisenter.javafx;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.configuration.Setting;
import org.praisenter.javafx.screen.ScreenConfiguration;
import org.praisenter.javafx.screen.ScreenView;
import org.praisenter.javafx.screen.ScreenViewDragDropManager;
import org.praisenter.javafx.themes.Theme;
import org.praisenter.resources.translations.Translations;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;

// TODO translate
// FEATURE option to export theme and translation to create new ones

public final class SetupPane extends VBox {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	public SetupPane(PraisenterContext context) {
		this.setPadding(new Insets(5));
		this.setSpacing(5);
		
		Label lblRestartWarning = new Label("Changing the Theme, Language, or Debug Mode requires the application to be restarted to take effect.", FONT_AWESOME.create(FontAwesome.Glyph.WARNING).color(Color.ORANGE));
		
		List<Option<Locale>> locales = new ArrayList<Option<Locale>>();
		for (Locale locale : Translations.SUPPORTED_LOCALES) {
			locales.add(new Option<Locale>(locale.getDisplayName(), locale));
		}
		
		GridPane gridGeneral = new GridPane();
		gridGeneral.setHgap(5);
		gridGeneral.setVgap(5);
		
		Locale locale = context.getConfiguration().getLanguage();
		if (locale == null) {
			locale = Locale.getDefault();
		}
		
		Theme theme = context.getConfiguration().getTheme();
		if (theme == null) {
			theme = Theme.DEFAULT;
		}
		
		// language
		Label lblLocale = new Label("Language");
		ComboBox<Option<Locale>> cmbLocale = new ComboBox<Option<Locale>>(FXCollections.observableArrayList(locales));
		cmbLocale.setValue(new Option<Locale>(null, locale));
		gridGeneral.add(lblLocale, 0, 0);
		gridGeneral.add(cmbLocale, 1, 0);
		
		// theme
		Label lblTheme = new Label("Theme");
		ComboBox<Theme> cmbTheme = new ComboBox<Theme>(FXCollections.observableArrayList(Theme.THEMES));
		cmbTheme.setValue(theme);
		gridGeneral.add(lblTheme, 0, 1);
		gridGeneral.add(cmbTheme, 1, 1);
		
		// debug mode
		Label lblDebugMode = new Label("Debug Mode");
		CheckBox chkDebugMode = new CheckBox();
		chkDebugMode.setSelected(context.getConfiguration().isSet(Setting.DEBUG_MODE));
		gridGeneral.add(lblDebugMode, 0, 2);
		gridGeneral.add(chkDebugMode, 1, 2);
		
		VBox vboxGeneral = new VBox(gridGeneral, lblRestartWarning);
		vboxGeneral.setSpacing(7);
		
		TitledPane ttlGeneral = new TitledPane("General Settings", vboxGeneral);
		ttlGeneral.setCollapsible(false);
		this.getChildren().add(ttlGeneral);
		
		Label lblScreenWarning = new Label("Changing the screen assignment will close any currently displayed slides or notifications.", FONT_AWESOME.create(FontAwesome.Glyph.WARNING).color(Color.ORANGE));
		Label lblScreenHowTo = new Label("Drag and drop the screen to assign its role.");
		
		// get a screenshot of all the screens
		GridPane screenPane = new GridPane();
		screenPane.setHgap(10);
		screenPane.setVgap(10);
		
		Label lblOperatorScreen = new Label("Operator");
		Label lblPrimaryScreen = new Label("Primary");
		Label lblMusicianScreen = new Label("Musician");
		lblOperatorScreen.setFont(Font.font("System", FontWeight.BOLD, 15));
		lblPrimaryScreen.setFont(Font.font("System", FontWeight.BOLD, 15));
		lblMusicianScreen.setFont(Font.font("System", FontWeight.BOLD, 15));
		
		Label lblOperatorDescription = new Label("This is the screen that you will be operating from with the Praisenter application and any other tools. Typically your default desktop.");
		Label lblPrimaryDescription = new Label("This is the screen that will show the presentations and notifications.");
		Label lblMusicianDescription = new Label("This is the screen that will show specialized musician or song related information (optional).");
		
		lblOperatorDescription.setWrapText(true);
		lblOperatorDescription.setTextAlignment(TextAlignment.CENTER);
		GridPane.setHgrow(lblOperatorDescription, Priority.NEVER);
		GridPane.setValignment(lblOperatorDescription, VPos.TOP);
		
		lblPrimaryDescription.setWrapText(true);
		lblPrimaryDescription.setTextAlignment(TextAlignment.CENTER);
		GridPane.setHgrow(lblPrimaryDescription, Priority.NEVER);
		GridPane.setValignment(lblPrimaryDescription, VPos.TOP);

		lblMusicianDescription.setWrapText(true);
		lblMusicianDescription.setTextAlignment(TextAlignment.CENTER);
		GridPane.setHgrow(lblMusicianDescription, Priority.NEVER);
		GridPane.setValignment(lblMusicianDescription, VPos.TOP);
		
		screenPane.add(lblOperatorScreen, 0, 1);
		screenPane.add(lblPrimaryScreen, 1, 1);
		screenPane.add(lblMusicianScreen, 2, 1);
		screenPane.add(lblOperatorDescription, 0, 2);
		screenPane.add(lblPrimaryDescription, 1, 2);
		screenPane.add(lblMusicianDescription, 2, 2);
		
		GridPane.setHalignment(lblOperatorScreen, HPos.CENTER);
		GridPane.setHalignment(lblPrimaryScreen, HPos.CENTER);
		GridPane.setHalignment(lblMusicianScreen, HPos.CENTER);
		
		VBox vboxScreens = new VBox(lblScreenHowTo, screenPane, lblScreenWarning);
		vboxScreens.setSpacing(7);
		
		TitledPane ttlScreens = new TitledPane("Display Setup", vboxScreens);
		ttlScreens.setCollapsible(false);
		this.getChildren().add(ttlScreens);
		
		// EVENTS

		cmbLocale.valueProperty().addListener((obs, ov, nv) -> {
			context.getConfiguration().set(Setting.GENERAL_LANGUAGE, nv.value.toLanguageTag());
		});
		
		cmbTheme.valueProperty().addListener((obs, ov, nv) -> {
			context.getConfiguration().set(Setting.GENERAL_THEME, nv.getName());
		});
		
		chkDebugMode.selectedProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				context.getConfiguration().setBoolean(Setting.DEBUG_MODE, true);
			} else {
				context.getConfiguration().remove(Setting.DEBUG_MODE);
			}
		});

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
					lblOperatorDescription.setPrefWidth(view2.getPrefWidth());
				} else if (col1 == 1) {
					sc.setMainScreen(view2.getDisplay());
					lblPrimaryDescription.setPrefWidth(view2.getPrefWidth());
				} else if (col1 == 2) {
					sc.setMusicianScreen(view2.getDisplay());
					lblMusicianDescription.setPrefWidth(view2.getPrefWidth());
				}
				if (col2 == 0) {
					sc.setOperatorScreen(view1.getDisplay());
					lblOperatorDescription.setPrefWidth(view1.getPrefWidth());
				} else if (col2 == 1) {
					sc.setMainScreen(view1.getDisplay());
					lblPrimaryDescription.setPrefWidth(view1.getPrefWidth());
				} else if (col2 == 2) {
					sc.setMusicianScreen(view1.getDisplay());
					lblMusicianDescription.setPrefWidth(view1.getPrefWidth());
				}
			}
		};
		
		// listener for updating the screen views when the 
		// screens change or when the parent node changes
		InvalidationListener screenListener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				ScreenConfiguration sc = context.getScreenManager().getScreenConfiguration();
				
				Map<Integer, ScreenView> views = ScreenView.createScreenViews(manager);
				
				ScreenView operator = null;
				ScreenView main = null;
				ScreenView musician = null;
				if (sc.getOperatorScreen() != null) {
					operator = views.remove(sc.getOperatorScreen().getId());
				}
				if (sc.getMainScreen() != null) {
					main = views.remove(sc.getMainScreen().getId());
				}
				if (sc.getMusicianScreen() != null) {
					musician = views.remove(sc.getMusicianScreen().getId());
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
				
				GridPane.setHalignment(operator, HPos.CENTER);
				GridPane.setHalignment(main, HPos.CENTER);
				GridPane.setHalignment(musician, HPos.CENTER);
				
				lblOperatorDescription.setPrefWidth(operator.getPrefWidth());
				lblPrimaryDescription.setPrefWidth(main.getPrefWidth());
				lblMusicianDescription.setPrefWidth(musician.getPrefWidth());
				
				int i = 3;
				for (ScreenView view : views.values()) {
					Label lblUnused = new Label("Unused");
					screenPane.add(view, i, 0);
					screenPane.add(lblUnused, i, 1);
					GridPane.setHalignment(lblUnused, HPos.CENTER);
					i++;
				}
			}
		};
		
		// update when the parent changes
		parentProperty().addListener(screenListener);
		
		// update when the screens change
		Screen.getScreens().addListener(screenListener);
		
	}
}

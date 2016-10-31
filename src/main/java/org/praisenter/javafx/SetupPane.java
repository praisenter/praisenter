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
import org.praisenter.javafx.styles.Theme;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
		
//		// get screen options
//		List<Option<ScreenRole>> screenTypes = new ArrayList<Option<ScreenRole>>();
//		screenTypes.add(new Option<ScreenRole>("None", ScreenRole.NONE));
//		screenTypes.add(new Option<ScreenRole>("Presentation", ScreenRole.PRESENTATION));
//		screenTypes.add(new Option<ScreenRole>("Musician", ScreenRole.MUSICIAN));
		
		// get a screenshot of all the screens
		GridPane screenPane = new GridPane();
		screenPane.setHgap(10);
		screenPane.setVgap(10);
		
		screenPane.add(new Label("Operator"), 0, 1);
		screenPane.add(new Label("Primary"), 1, 1);
		screenPane.add(new Label("Musician"), 2, 1);
		
		parentProperty().addListener((obs, ov, nv) -> {
			List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] devices = ge.getScreenDevices();
			
			Map<Integer, Node> screenshots = new HashMap<Integer, Node>(); 
			
			final double s = 200.0 / 1080.0;
			final double bw = 10;
			
			for (int i = 0; i < devices.length; i++) {
				GraphicsDevice device = devices[i];
				
				// find a matching screen for this device
				java.awt.Rectangle bounds = device.getDefaultConfiguration().getBounds();
				int dx = bounds.x;
				int dy = bounds.y;
				int id = ScreenConfiguration.SCREEN_NOT_AVAILABLE;
				for (int j = 0; j < screens.size(); j++) {
					Screen scrn = screens.get(j);
					int sx = (int)scrn.getBounds().getMinX();
					int sy = (int)scrn.getBounds().getMinY();
					if (sx == dx && sy == dy) {
						id = j;
					}
				}
				
				// take a screenshot of the device
				try {
					Robot robot = new Robot(device);
					BufferedImage image = robot.createScreenCapture(device.getDefaultConfiguration().getBounds());
					Image fximage = SwingFXUtils.toFXImage(image, null);
					ImageView img = new ImageView(fximage);
					 
					final double w = Math.ceil(fximage.getWidth() * s);
					final double h = Math.ceil(fximage.getHeight() * s);
					
					img.setFitHeight(h);
					img.setFitWidth(w);
					img.setPreserveRatio(true);
					img.setLayoutX(bw);
					img.setLayoutY(bw);
					
					StackPane stack = new StackPane(img);
					stack.setBackground(new Background(new BackgroundFill(Color.BLACK, null, new Insets(bw * 0.5))));
					Fx.setSize(stack, w + bw * 2, h + bw * 2);
					stack.getStyleClass().add("screen-snapshot");
					
					screenshots.put(id, stack);
				} catch (Exception ex) {
					
				}
			}
			
			ScreenConfiguration sc = context.getScreenManager().getScreenConfiguration();
			
			if (sc.getOperatorScreenId() != ScreenConfiguration.SCREEN_NOT_AVAILABLE) {
				screenPane.add(screenshots.get(sc.getOperatorScreenId()), 0, 0);
			}
			
			if (sc.getPrimaryScreenId() != ScreenConfiguration.SCREEN_NOT_AVAILABLE) {
				screenPane.add(screenshots.get(sc.getPrimaryScreenId()), 1, 0);
			} else {
				screenPane.add(getMissingScreenGraphic(200, 150, bw), 2, 0);
			}
			
			if (sc.getMusicianScreenId() != ScreenConfiguration.SCREEN_NOT_AVAILABLE) {
				screenPane.add(screenshots.get(sc.getMusicianScreenId()), 2, 0);
			} else {
				screenPane.add(getMissingScreenGraphic(200, 150, bw), 2, 0);
			}
		});
		
//		List<ComboBox<Option<ScreenRole>>> screenCombos = new ArrayList<ComboBox<Option<ScreenRole>>>();
//		parentProperty().addListener((obs, ov, nv) -> {
//			if (nv == null) return;
//			screenPane.getChildren().clear();
//			screenCombos.clear();
//			final double s = 200.0 / 1080.0;
//			final double bw = 10;
//			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//			GraphicsDevice[] devices = ge.getScreenDevices();
//			for (int i = 0; i < devices.length; i++) {
//				GraphicsDevice device = devices[i];
//				try {
//					Robot robot = new Robot(device);
//					BufferedImage image = robot.createScreenCapture(device.getDefaultConfiguration().getBounds());
//					Image fximage = SwingFXUtils.toFXImage(image, null);
//					ImageView img = new ImageView(fximage);
//					 
//					final double w = Math.ceil(fximage.getWidth() * s);
//					final double h = Math.ceil(fximage.getHeight() * s);
//					
//					img.setFitHeight(h);
//					img.setFitWidth(w);
//					img.setPreserveRatio(true);
//					img.setLayoutX(bw);
//					img.setLayoutY(bw);
//					
//					StackPane stack = new StackPane(img);
//					stack.setBackground(new Background(new BackgroundFill(Color.BLACK, null, new Insets(bw * 0.5))));
//					Fx.setSize(stack, w + bw * 2, h + bw * 2);
//					stack.getStyleClass().add("screen-snapshot");
//
//					ComboBox<Option<ScreenRole>> cmbDisplayType = new ComboBox<Option<ScreenRole>>(FXCollections.observableArrayList(screenTypes));
//					cmbDisplayType.setUserData(device);
//					screenCombos.add(cmbDisplayType);
//					
//					for (ScreenMapping map : configuration.getScreenMappings()) {
//						if (map.getId().equals(device.getIDstring())) {
//							cmbDisplayType.setValue(new Option<ScreenRole>(null, map.getRole()));
//							break;
//						}
//					}
//					
//					screenPane.add(stack, i, 0);
//					screenPane.add(cmbDisplayType, i, 1);
//					GridPane.setHalignment(cmbDisplayType, HPos.CENTER);
//				} catch (AWTException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//		});
		
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
	
	private static final Node getMissingScreenGraphic(double w, double h, double bw) {
		Label label = new Label("No screen detected");
		StackPane stack = new StackPane(label);
		stack.setBackground(new Background(new BackgroundFill(Color.BLACK, null, new Insets(bw * 0.5))));
		Fx.setSize(stack, w + bw * 2, h + bw * 2);
		stack.getStyleClass().add("screen-snapshot");
		return stack;
	}
	
	private static final String getLocaleName(Locale locale) {
		return locale.getDisplayLanguage() + (locale.getDisplayCountry().isEmpty() ? "" : " (" + locale.getDisplayCountry() + ")");
	}
}

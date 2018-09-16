import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.controlsfx.glyphfont.FontAwesome.Glyph;
import org.praisenter.ui.Praisenter;
import org.praisenter.ui.fonts.OpenIconic;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import com.jfoenix.controls.JFXToggleButton;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PraisenterLayout extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	private Path currentCss = null;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FontAwesome fa = new FontAwesome(Praisenter.class.getResourceAsStream("/org/praisenter/fonts/fontawesome-webfont.ttf"));
		OpenIconic openiconic = new OpenIconic(Praisenter.class.getResourceAsStream("/org/praisenter/fonts/open-iconic.ttf"));
    	GlyphFontRegistry.register(fa);
		GlyphFontRegistry.register(openiconic);
		
		MenuBar menu = new MenuBar();
		Menu mnuFile = new Menu("File");
		mnuFile.getItems().addAll(new MenuItem("Preferences", fa.create(Glyph.GEAR)));
		Menu mnuAbout = new Menu("About");
		mnuAbout.getItems().addAll(new MenuItem("About Praisenter", fa.create(Glyph.INFO_CIRCLE).color(Color.CORNFLOWERBLUE)), new MenuItem("Logs"));
		menu.setUseSystemMenuBar(true);
		menu.getMenus().addAll(mnuFile, mnuAbout);
		
		ToolBar toolbar = new ToolBar(
				new Button(null, fa.create(Glyph.SAVE)),
				new Button(null, openiconic.create(OpenIconic.Glyph.ACTION_UNDO)),
				new Button(null, openiconic.create(OpenIconic.Glyph.ACTION_REDO)),
				new Button(null, fa.create(Glyph.PLUS)),
				new Button(null, fa.create(Glyph.COPY)),
				new Button(null, fa.create(Glyph.CUT)),
				new Button(null, fa.create(Glyph.PASTE)),
				new Button(null, fa.create(Glyph.DOWNLOAD)),
				new Button(null, fa.create(Glyph.TERMINAL)),
				new Button(null, fa.create(Glyph.REMOVE)),
				new Button(null, fa.create(Glyph.SORT_ALPHA_ASC)),
				new Button(null, fa.create(Glyph.SORT_AMOUNT_ASC)));
		toolbar.setOrientation(Orientation.VERTICAL);
		
		VBox toolbarSubPane = new VBox(5);
		// selections to create something new
		// make selections before creation (media, type, etc)
		MenuButton mbtn = new MenuButton("Type", null, new MenuItem("Option 1"), new MenuItem("Another option"));
		ComboBox<String> cmb = new ComboBox<>(FXCollections.observableArrayList("This option", "Another option", "2", "And a long option"));
		Button btn = new Button("Done");
		btn.setAlignment(Pos.BASELINE_RIGHT);
		toolbarSubPane.getChildren().addAll(mbtn, cmb, btn);
		
		TabPane docs = new TabPane(new Tab("doc 1"), new Tab("the bible"), new Tab("It's all in him"));
		
		StackPane stack = new StackPane();
		stack.getChildren().add(docs);
		stack.getChildren().add(toolbarSubPane);
		StackPane.setAlignment(toolbarSubPane, Pos.TOP_LEFT);
		StackPane.setAlignment(docs, Pos.TOP_LEFT);
		
		TabPane store = new TabPane(new Tab("Bibles"), new Tab("Songs"), new Tab("Slides"), new Tab("Slide Shows"), new Tab("Media"));
		
		VBox properties = new VBox(5,
				new TextField(),
				new PasswordField(),
				new CheckBox(),
				new RadioButton(),
				new ChoiceBox<>(),
				new ComboBox<>(),
				new Slider(0, 10, 0),
				new Spinner<>());
		
		
		HBox tools = new HBox(5, toolbar);
		
		BorderPane mainLayout = new BorderPane();
		
		mainLayout.setTop(menu);
		mainLayout.setLeft(tools);
		mainLayout.setCenter(stack);
		mainLayout.setBottom(store);
		mainLayout.setRight(properties);
		
		TextArea area = new TextArea();
		
		final Path css1 = Paths.get(System.getProperty("user.home")).resolve("temp1.css");
		final Path css2 = Paths.get(System.getProperty("user.home")).resolve("temp2.css");
		this.currentCss = css1;

		Button swap = new Button("Swap CSS");
		
		VBox cssPane = new VBox(5, area, swap);
		
		SplitPane split = new SplitPane(mainLayout, cssPane);
		split.setOrientation(Orientation.HORIZONTAL);
		
		BorderPane root = new BorderPane();
		root.setCenter(split);
		
		Scene scene = new Scene(root);
		
		swap.setOnAction(e -> {
			// save the css to the other 
			if (this.currentCss == css1) {
				this.currentCss = css2;
			} else {
				this.currentCss = css1;
			}
			try {
				Files.write(this.currentCss, area.getText().getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				scene.getStylesheets().setAll(this.currentCss.toAbsolutePath().toUri().toURL().toExternalForm());
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
		});
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private static Node buildPanelSet() {
		BorderPane main = new BorderPane();
		main.getStyleClass().add("main");
		
		ToolBar toolbar = new ToolBar(
				new Button("a"),
				new Button("b"),
				new Button("c"),
				new Button("d"),
				new Button("e"),
				new Button("f"),
				new Button("g"),
				new Button("h"),
				new Button("i"),
				new Button("j"),
				new Button("k"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"),
				new Button("l"));
		toolbar.getStyleClass().addAll("content-pane", "button-bar");
		toolbar.setOrientation(Orientation.VERTICAL);
		
		Node sink = buildKitchenSink();
		sink.getStyleClass().add("content-pane");
		
		TitledPane ttl = new TitledPane("THE TITLE", new Label("some content"));
		ttl.getStyleClass().add("property-panel");
		Pane pane = new Pane(ttl);
		pane.getStyleClass().add("content-pane");
		
		main.setLeft(new HBox(5, toolbar, new ScrollPane(sink), pane));
		
		return main;
	}
	
	
	private static Node buildKitchenSink() {
		Button disabled = new Button("disabled");
		disabled.setDisable(true);
		
		ToggleButton tglselected = new ToggleButton("selected");
		tglselected.setSelected(true);
		
		ToggleButton tgldisabled = new ToggleButton("disabled");
		tgldisabled.setDisable(true);
		
		ToggleButton tgldisabled2 = new ToggleButton("disabled");
		tgldisabled2.setDisable(true);
		tgldisabled2.setSelected(true);
		
		CheckBox chkdisabled = new CheckBox("disabled");
		chkdisabled.setDisable(true);
		
		CheckBox chkselected = new CheckBox("selected");
		chkselected.setSelected(true);
		
		CheckBox chkdisabled2 = new CheckBox("disabled");
		chkdisabled2.setDisable(true);
		chkdisabled2.setSelected(true);
		
		RadioButton rdoselected = new RadioButton("selected");
		rdoselected.setSelected(true);
		
		RadioButton rdodisabled = new RadioButton("disabled");
		rdodisabled.setDisable(true);
		
		RadioButton rdodisabled2 = new RadioButton("disabled");
		rdodisabled2.setDisable(true);
		rdodisabled2.setSelected(true);
		
		ChoiceBox<String> chodisabled = new ChoiceBox<String>(FXCollections.observableArrayList("item1", "item2", "item3"));
		chodisabled.setDisable(true);
		
		ComboBox<String> cmbdisabled = new ComboBox<String>(FXCollections.observableArrayList("item1", "item2", "item3"));
		cmbdisabled.setDisable(true);
		
		Label lbldisabled = new Label("disabled");
		lbldisabled.setDisable(true);
		
		Hyperlink hypdisabled = new Hyperlink("disabled");
		hypdisabled.setDisable(true);
		
		Hyperlink hypvisited = new Hyperlink("visited");
		hypvisited.setVisited(true);
		
		Slider slddisabled = new Slider(0, 100, 0);
		slddisabled.setDisable(true);

		Slider sldticks = new Slider(0, 100, 0);
		sldticks.setMajorTickUnit(25);
		sldticks.setMinorTickCount(5);
		sldticks.setShowTickMarks(true);
		sldticks.setShowTickLabels(true);
		
		Spinner<Integer> spnnormal = new Spinner<>(0, 10, 1);
		spnnormal.getEditor().setPromptText("normal");
		
		Spinner<Integer> spndisabled = new Spinner<>(0, 10, 1);
		spndisabled.setDisable(true);
		spndisabled.getEditor().setPromptText("disabled");
		
		Spinner<Integer> spneditable = new Spinner<>(0, 10, 1);
		spneditable.setEditable(true);
		spneditable.getEditor().setPromptText("editable");
		
		TextField txtnormal = new TextField();
		txtnormal.setPromptText("normal");
		
		TextField txttext = new TextField();
		txttext.setText("with text");
		
		TextField txtdisabled = new TextField();
		txtdisabled.setPromptText("disabled");
		txtdisabled.setDisable(true);
		
		TextArea areanormal = new TextArea();
		areanormal.setPromptText("normal");
		
		TextArea areadisabled = new TextArea();
		areadisabled.setPromptText("disabled");
		areadisabled.setDisable(true);
		
		PasswordField passnormal = new PasswordField();
		passnormal.setPromptText("normal");
		
		PasswordField passtext = new PasswordField();
		passtext.setText("with text");

		PasswordField passdisabled = new PasswordField();
		passdisabled.setPromptText("disabled");
		passdisabled.setDisable(true);
		
		List<String> lotsOfItems = new ArrayList<>();
		for (int i = 0; i < 50; i++) lotsOfItems.add("item " + i);
		
		SplitMenuButton splnormal = new SplitMenuButton(new MenuItem("item1"), new MenuItem("item2"), new MenuItem("item3"));
		splnormal.setText("split button");
		
		SplitMenuButton spldisabled = new SplitMenuButton(new MenuItem("item1"), new MenuItem("item2"), new MenuItem("item3"));
		spldisabled.setText("disabled");
		spldisabled.setDisable(true);
		
		MenuButton mbndisabled = new MenuButton("disabled", null, new MenuItem("item1"), new MenuItem("item2"), new MenuItem("item3"));
		mbndisabled.setDisable(true);
		
		SplitMenuButton splother = new SplitMenuButton(new MenuItem("item1"), new MenuItem("item2"), new MenuItem("item3"));
		splother.setText("button");
		
		// toolbar
		ToolBar tbarnormal = new ToolBar(
				new Button("test"), 
				new MenuButton("test", null, new MenuItem("item1"), new MenuItem("item1")), 
				splother,
				new TextField(),
				new Spinner<Integer>(0, 10, 0));
		
		// tooltip
		Label lbltip = new Label("tooltip");
		lbltip.setTooltip(new Tooltip("hello this is a bit of text in a tooltip."));
		
		// tableview
		
		// listview
		ListView<String> lstnormal = new ListView<>(FXCollections.observableArrayList("item 1", "item 2", "item 3"));
		ListView<String> lstlots = new ListView<>(FXCollections.observableArrayList("item 1", "item 2", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3", "item 3"));
		ListView<String> lstdisabled = new ListView<>(FXCollections.observableArrayList("item 1", "item 2", "item 3"));
		lstdisabled.setDisable(true);
		
		// treeview
		
		// titledpane
		TitledPane ttlnormal = new TitledPane("titled pane 1", new Button("in titled pane"));
		ttlnormal.setCollapsible(false);
		TitledPane ttldisabled = new TitledPane("diabled", new Button("in titled pane"));
		ttldisabled.setDisable(true);
		TitledPane ttlcollapsible = new TitledPane("collapsible", new Button("in titled pane"));
		ttlcollapsible.setCollapsible(true);
		
		// progress bar
		ProgressBar prgnormal = new ProgressBar(0.3);
		ProgressBar prgdiabled = new ProgressBar(0.6);
		prgdiabled.setDisable(true);
		
		// progress indicator
		ProgressIndicator indnormal = new ProgressIndicator(0.4);
		ProgressIndicator inddisabled = new ProgressIndicator(0.6);
		inddisabled.setDisable(true);
		
		// tabs
		TabPane tabnormal = new TabPane(new Tab("tab 1"), new Tab("tab 2"), new Tab("tab 3"));
		TabPane tablots = new TabPane(new Tab("tab 1"), new Tab("tab 2"), new Tab("tab 3"), new Tab("tab 3"), new Tab("tab 3"), new Tab("tab 3"), new Tab("tab 3"), new Tab("tab 3"), new Tab("tab 3"), new Tab("tab 3"), new Tab("tab 3"), new Tab("tab 3"), new Tab("tab 3"));
		TabPane tabdisable = new TabPane(new Tab("tab 1"), new Tab("tab 2"), new Tab("tab 3"));
		tabdisable.setDisable(true);
		
		// probably don't need the following:
		// htmleditor
		// file chooser
		// pagination
		
		VBox row1 = new VBox(10,
				new HBox(10, new Label("label"), lbldisabled, lbltip),
				new HBox(10, new Hyperlink("link"), hypdisabled, hypvisited),
				new HBox(10, new Button("button"), disabled),
				new HBox(10, new ToggleButton("toggle"), tglselected, tgldisabled, tgldisabled2),
				new HBox(10, new ToggleButton("for sizing"), splnormal, spldisabled),
				new HBox(10, new ToggleButton("for sizing"), new MenuButton("menu button", null, new MenuItem("item1"), new MenuItem("item2"), new MenuItem("item3")), mbndisabled),
				tbarnormal,
				new Separator(),
				new HBox(10, new CheckBox("check"), chkselected, chkdisabled, chkdisabled2),
				new HBox(10, new RadioButton("radio"), rdoselected, rdodisabled, rdodisabled2),
				new HBox(10, new ChoiceBox<String>(FXCollections.observableArrayList("item1", "item2", "item3")), new ChoiceBox<String>(FXCollections.observableArrayList(lotsOfItems)), chodisabled),
				new HBox(10, new ComboBox<String>(FXCollections.observableArrayList("item1", "item2", "item3")), new ComboBox<String>(FXCollections.observableArrayList(lotsOfItems)), cmbdisabled),
				new HBox(10, spnnormal, spneditable, spndisabled),
				new HBox(10, txtnormal, txttext, txtdisabled),
				new HBox(10, passnormal, passtext, passdisabled),
				new HBox(10, areanormal, areadisabled),
				new HBox(10, new Slider(0, 100, 0), sldticks, slddisabled),
				new HBox(10, lstnormal, lstlots, lstdisabled),
				new HBox(10, ttlnormal, ttlcollapsible, ttldisabled),
				new HBox(10, prgnormal, prgdiabled),
				new HBox(10, indnormal, inddisabled),
				new HBox(10, tabnormal),
				new HBox(10, tablots),
				new HBox(10, tabdisable),
				new MenuBar(new Menu("menu", null, new MenuItem("item1"), new SeparatorMenuItem(), new MenuItem("item2"), new Menu("sub", null, new MenuItem("sub1")))),

				new DatePicker(),
				new ColorPicker());
		
		row1.setPadding(new Insets(10));
		
		return row1;
	}
}

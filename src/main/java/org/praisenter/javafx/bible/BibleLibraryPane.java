package org.praisenter.javafx.bible;

import java.io.File;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.FailedOperation;
import org.praisenter.WarningOperation;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.bible.UnboundBibleImporter;
import org.praisenter.javafx.Alerts;
import org.praisenter.javafx.FlowListView;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.resources.translations.Translations;

public final class BibleLibraryPane extends BorderPane {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	// selection
	
	/** The selected bible */
	private final ObjectProperty<Bible> selected = new SimpleObjectProperty<Bible>();
	
	// data
	
	/** The context */
	private final PraisenterContext context;
	
	/** The master list of bibles */
	private final ObservableList<BibleListItem> master;
	
	// nodes
	
	/** The bible listing */
	private final FlowListView<BibleListItem> lstBibles;
	
	public BibleLibraryPane(PraisenterContext context) {
		this.context = context;
		
		BibleLibrary bibleLibrary = context.getBibleLibrary();
		List<Bible> bibles = null;
		try {
			bibles = bibleLibrary.getBibles();
		} catch (SQLException e) {
			// log it and show an error
			LOGGER.error("Failed to get the list of bibles from the database", e);
			Alert alert = Alerts.exception(
					this.getScene().getWindow(), 
					null, 
					null, 
					Translations.get("bible.load.error"),
					e);
			alert.show();
			// just set it to empty
			bibles = new ArrayList<Bible>();
		}
		
		this.master = FXCollections.observableArrayList();
		for (Bible bible : bibles) {
			this.master.add(new BibleListItem(bible));
		}
		
		VBox right = new VBox();
		VBox importSteps = new VBox();
		
		// TODO wire up settings controls
		
		Hyperlink lnkUnboundBible = new Hyperlink(Translations.get("bible.unbound"));
		lnkUnboundBible.setOnAction((e) -> {
			context.getApplication().getHostServices().showDocument("http://unbound.biola.edu/index.cfm?method=downloads.showDownloadMain");
		});
		Label lblStep1 = new Label(Translations.get("bible.import.list1"));
		Label lblStep2 = new Label(Translations.get("bible.import.list2"));
		Label lblStep3 = new Label(Translations.get("bible.import.list3"));
		TextFlow lblStep1Text = new TextFlow(new Text(Translations.get("bible.import.step1")), lnkUnboundBible);
		Label lblStep2Text = new Label(Translations.get("bible.import.step2"));
		Label lblStep3Text = new Label(Translations.get("bible.import.step3"));
		lblStep1.setMinWidth(20);
		// try to align the text due to the weird height of hyperlinks
		lblStep1.setPadding(new Insets(3, 0, 0, 0));
		lblStep2.setMinWidth(20);
		lblStep3.setMinWidth(20);
		lblStep2Text.setWrapText(true);
		lblStep3Text.setWrapText(true);
		
		importSteps.getChildren().addAll(
				new HBox(lblStep1, lblStep1Text),
				new HBox(lblStep2, lblStep2Text),
				new HBox(lblStep3, lblStep3Text));

		BibleMetadataPane bmp = new BibleMetadataPane();

		GridPane settingsPane = new GridPane();
		settingsPane.setHgap(5);
		settingsPane.setVgap(5);
		
		Label lblIncludeApocrypha = new Label(Translations.get("bible.apocrypha"));
		CheckBox chkIncludeApocrypha = new CheckBox();
		chkIncludeApocrypha.selectedProperty().bindBidirectional(context.getConfiguration().apocryphaIncludedProperty());
		settingsPane.add(lblIncludeApocrypha, 0, 0);
		settingsPane.add(chkIncludeApocrypha, 1, 0);
		
		TitledPane ttlImport = new TitledPane(Translations.get("bible.import.title"), importSteps);
		TitledPane ttlMetadata = new TitledPane(Translations.get("bible.properties.title"), bmp);
		TitledPane ttlSettings = new TitledPane(Translations.get("bible.settings.title"), settingsPane);
		
		right.getChildren().addAll(ttlImport, ttlMetadata, ttlSettings);
		
		this.lstBibles = new FlowListView<BibleListItem>(new BibleListViewCellFactory());
		this.lstBibles.itemsProperty().bindContent(this.master);
		this.lstBibles.setOrientation(Orientation.HORIZONTAL);
		
		ScrollPane leftScroller = new ScrollPane();
        leftScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScroller.setFitToWidth(true);
		leftScroller.addEventHandler(KeyEvent.KEY_PRESSED, this::onBibleDelete);
		leftScroller.setFocusTraversable(true);
        leftScroller.setContent(this.lstBibles);
        leftScroller.setOnDragOver(this::onBibleDragOver);
        leftScroller.setOnDragDropped(this::onBibleDragDropped);
		
        ScrollPane rightScroller = new ScrollPane();
        rightScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScroller.setFitToWidth(true);
        rightScroller.setContent(right);
        rightScroller.setMinWidth(250);
        
		SplitPane split = new SplitPane(leftScroller, rightScroller);
		split.setDividerPositions(0.75);
		SplitPane.setResizableWithParent(rightScroller, false);
		
		this.setCenter(split);
		
		this.lstBibles.selectionProperty().addListener((obs, ov, nv) -> {
        	if (nv == null) {
        		this.selected.set(null);
        	} else {
        		this.selected.set(nv.bible);
        	}
        });
        this.selected.addListener((obs, ov, nv) -> {
        	if (nv == null) {
        		lstBibles.selectionProperty().set(null);
        	} else {
        		lstBibles.selectionProperty().set(new BibleListItem(nv));
        	}
        });
        
        // wire up the selected media to the media metadata view with a unidirectional binding
        bmp.bibleProperty().bind(this.lstBibles.selectionProperty());
	}
	
	/**
	 * Called when something is dragged over the element.
	 * @param event the event
	 */
	private void onBibleDragOver(DragEvent event) {
		Dragboard db = event.getDragboard();
		if (db.hasFiles()) {
			event.acceptTransferModes(TransferMode.COPY);
		} else {
			event.consume();
		}
	}
	
	/**
	 * Handler for when files have been drag and dropped to import.
	 * @param event the drag event
	 */
	private void onBibleDragDropped(DragEvent event) {
		Dragboard db = event.getDragboard();
		if (db.hasFiles()) {
			final List<File> files = db.getFiles();
			
			// add some loading items
			List<BibleListItem> loadings = new ArrayList<BibleListItem>();
			if (files != null && files.size() > 0) {
				for (File file : files) {
					loadings.add(new BibleListItem(file.toPath().getFileName().toString()));
				}
				master.addAll(loadings);
			}
			
			// import the media
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					if (files != null && files.size() > 0) {
						final List<WarningOperation<Bible>> warnings = new ArrayList<WarningOperation<Bible>>();
						final List<FailedOperation<File>> failed = new ArrayList<FailedOperation<File>>();
						
						for (File file : files) {
							try {
								final BibleListItem loading = new BibleListItem(file.toPath().getFileName().toString());
								// import the bible
								final Bible bible = new UnboundBibleImporter(context.getBibleLibrary()).execute(file.toPath());
								// check for import warnings
								if (bible.hadImportWarning()) {
									warnings.add(new WarningOperation<Bible>(bible, null));
								}
								BibleListItem success = new BibleListItem(bible);
								Platform.runLater(() -> {
									// remove the loading
									master.remove(loading);
									master.add(success);
								});
							} catch (Exception e) {
								LOGGER.error("Failed to add bible '" + file.toPath().toAbsolutePath().toString() + "' to the bible library.", e);
								failed.add(new FailedOperation<File>(file, e));
							}
						}
						
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								master.removeAll(loadings);
								
								if (warnings.size() > 0) {
									// get the warning files
									String[] wFileNames = warnings.stream().map(f -> f.getData().getName()).collect(Collectors.toList()).toArray(new String[0]);
									// get the failed media
									String list = String.join(", ", wFileNames);
									Alert alert = new Alert(AlertType.INFORMATION);
									alert.initOwner(getScene().getWindow());
									alert.initModality(Modality.WINDOW_MODAL);
									alert.setTitle(Translations.get("bible.import.info.title"));
									alert.setHeaderText(Translations.get("bible.import.info.header"));
									alert.setContentText(list);
									alert.show();
								}
								
								if (failed.size() > 0) {
									// get the exceptions
									Exception[] exceptions = failed.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
									// get the failed media
									String list = String.join(", ", failed.stream().map(f -> f.getData().getName()).collect(Collectors.toList()));
									Alert alert = Alerts.exception(
											getScene().getWindow(),
											null, 
											null,
											MessageFormat.format(Translations.get("bible.import.error"), list), 
											exceptions);
									alert.show();
								}
							}
						});
					}
					return null;
				}
			};
			new Thread(task).start();
		}
		event.setDropCompleted(true);
		event.consume();
	}
	
	/**
	 * Handler for when bibles are deleted using the delete key
	 * @param event the key event
	 */
	private void onBibleDelete(KeyEvent event) {
		if (event.getCode() == KeyCode.DELETE) {
			List<BibleListItem> items = new ArrayList<BibleListItem>();
			for (BibleListItem item : lstBibles.selectionsProperty().get()) {
				// can't delete items that are still being imported
				if (item.loaded) {
					items.add(item);
				}
			}
			if (items.size() > 0) {
				// attempt to delete the selected media
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.initOwner(getScene().getWindow());
				alert.initModality(Modality.WINDOW_MODAL);
				alert.setTitle(Translations.get("bible.remove.title"));
				alert.setContentText(Translations.get("bible.remove.content"));
				alert.setHeaderText(null);
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					// remove the real items
					master.removeAll(items);
					// add some loading ones
					List<BibleListItem> removings = new ArrayList<BibleListItem>();
					for (BibleListItem item : items) {
						BibleListItem removing = new BibleListItem(item.name + Constants.NEW_LINE + Translations.get("bible.removing"));
						master.add(removing);
						removings.add(removing);
					}
					
					Task<Void> task = new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							final List<BibleListItem> succeeded = new ArrayList<BibleListItem>();
							final List<FailedOperation<Bible>> failed = new ArrayList<FailedOperation<Bible>>();
							for (BibleListItem bli : items) {
								if (bli.loaded) {
									Bible bible = bli.bible;
									try {
										context.getBibleLibrary().deleteBible(bible);
										succeeded.add(bli);
									} catch (SQLException e) {
										LOGGER.error("Failed to remove bible '" + bible.getName() + "' from bible library.", e);
										failed.add(new FailedOperation<Bible>(bible, e));
									}
								}
							}
							
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									master.removeAll(removings);
									
									if (failed.size() > 0) {
										// get the exceptions
										Exception[] exceptions = failed.stream().map(f -> f.getException()).collect(Collectors.toList()).toArray(new Exception[0]);
										// get the failed media
										String list = String.join(", ", failed.stream().map(f -> f.getData().getName()).collect(Collectors.toList()));
										Alert alert = Alerts.exception(
												getScene().getWindow(),
												null, 
												null, 
												MessageFormat.format(Translations.get("bible.remove.error"), list), 
												exceptions);
										alert.show();
									}
								}
							});
							
							return null;
						}
					};
					new Thread(task).start();
				}
			}
		}
	}
}

package org.praisenter.javafx;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.bible.Bible;
import org.praisenter.configuration.Display;
import org.praisenter.javafx.async.AsyncTask;
import org.praisenter.javafx.async.AsyncTaskFactory;
import org.praisenter.javafx.bible.BibleActions;
import org.praisenter.javafx.bible.BibleEditorPane;
import org.praisenter.javafx.bible.BibleLibraryPane;
import org.praisenter.javafx.media.MediaActions;
import org.praisenter.javafx.media.MediaLibraryPane;
import org.praisenter.javafx.slide.SlideActions;
import org.praisenter.javafx.slide.SlideLibraryPane;
import org.praisenter.javafx.slide.SlideShowLibraryPane;
import org.praisenter.javafx.slide.editor.SlideEditorPane;
import org.praisenter.javafx.slide.editor.SlideShowEditorDialog;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideShow;
import org.praisenter.ui.controls.Alerts;
import org.praisenter.ui.translations.Translations;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Worker.State;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public final class MainPane extends BorderPane implements ApplicationPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final PraisenterContext context;
	
	private final MainMenu menu;
	private final MainStatusBar status;
	
	private final SlideDataPane presentPane;
	private final SetupPane setupPane;
	private final BibleLibraryPane bibleLibraryPane;
	private final BibleEditorPane bibleEditorPane;
	private final MediaLibraryPane mediaLibraryPane;
	private final SlideLibraryPane slideLibraryPane;
	private final SlideEditorPane slideEditorPane;
	private final SlideShowLibraryPane slideShowLibraryPane;
	
	private SlideShowEditorDialog slideShowEditorDialog;
	
	private final ObjectProperty<Node> mainContent = new SimpleObjectProperty<Node>();

//	private final Map<KeyCombination, Runnable> defaultAccelerators = new HashMap<>();
	
	public MainPane(PraisenterContext context) {
		this.context = context;
		
		this.menu = new MainMenu(this);
		
//		VBox top = new VBox(menu);
		this.setTop(this.menu);
		
		this.status = new MainStatusBar(context);
		this.setBottom(this.status);
		
		this.setupPane = new SetupPane(context);
		this.bibleLibraryPane = new BibleLibraryPane(context);
		this.bibleEditorPane = new BibleEditorPane(context);
		this.mediaLibraryPane = new MediaLibraryPane(context, Orientation.HORIZONTAL);
		this.slideLibraryPane = new SlideLibraryPane(context, Orientation.HORIZONTAL);
		this.slideEditorPane = new SlideEditorPane(context);
		this.slideShowLibraryPane = new SlideShowLibraryPane(context, Orientation.HORIZONTAL);
		
		this.addEventHandler(ApplicationEvent.ALL, e -> {
			handleApplicationEvent(e);
		});
		
		this.presentPane = new SlideDataPane(context);
		
		//this.setCenter(this.presentPane);
		
		List<Node> items = new ArrayList<Node>();
		items.add(this.setupPane);
		items.add(this.presentPane);
		items.add(this.bibleLibraryPane);
		items.add(this.bibleEditorPane);
		items.add(this.mediaLibraryPane);
		items.add(this.slideLibraryPane);
		items.add(this.slideEditorPane);
		items.add(this.slideShowLibraryPane);
		
		for (Node node : items) {
			setVisible(node, false);
		}
		
		StackPane views = new StackPane();
		views.getChildren().addAll(items);

		this.setCenter(views);
		
		setVisible(this.presentPane, true);
		this.mainContent.set(this.presentPane);
		
		this.mainContent.addListener((obs, ov, nv) -> {
			if (ov != null && ov instanceof ApplicationPane) {
				((ApplicationPane)ov).cleanup();
			}
		});
		
		//		this.sceneProperty().addListener((obs, ov, nv) -> {
//			if (nv != null) {
//				this.defaultAccelerators.clear();
//				this.defaultAccelerators.putAll(nv.getAccelerators());
//			}
//		});
	}
	
	// APPLICATION PANE
	
	public void handleApplicationEvent(ApplicationEvent event) {
		ApplicationAction action = event.getAction();
		Object data = event.getData();
		switch (action) {
			case IMPORT_BIBLES:
				this.promptBibleImport();
				break;
			case IMPORT_MEDIA:
				this.promptMediaImport();
				break;
			case IMPORT_SLIDES:
				this.promptSlideImport();
				break;
			case PRESENT:
				this.navigate(this.presentPane);
				break;
			case PREFERENCES:
				this.navigate(this.setupPane);
				break;
			case MANAGE_BIBLES:
				this.navigate(this.bibleLibraryPane);
				break;
			case REINDEX_BIBLES:
				this.context.getBibleLibrary()
					.reindex()
					.execute(this.context.getExecutorService());
				break;
			case EDIT:
				// get the data to know what to do
				if (data instanceof Bible) {
					this.bibleEditorPane.setBible(null);
					this.bibleEditorPane.setBible(((Bible)data).copy(true));
					this.navigate(this.bibleEditorPane);
				}
				if (data instanceof Slide) {
					this.slideEditorPane.setSlide(null);
					this.slideEditorPane.setSlide(((Slide)data).copy(true));
					this.navigate(this.slideEditorPane);
				}
				if (data instanceof SlideShow) {
					if (this.slideShowEditorDialog == null) {
						this.slideShowEditorDialog = new SlideShowEditorDialog(this.getScene().getWindow(), this.context);
					}
					this.slideShowEditorDialog.setValue(null);
					this.slideShowEditorDialog.setValue(((SlideShow) data).copy(true));
					this.slideShowEditorDialog.show(s -> {
						if (s != null) {
							this.context.getSlideLibrary().save(s)
								.execute(this.context.getExecutorService());
						}
					});
				}
				break;
			case NEW_BIBLE:
				Bible bible = new Bible();
				bible.setName("Untitled");
				bible.setLanguage(Locale.getDefault().toLanguageTag());
				bible.setSource("Praisenter");
				this.bibleEditorPane.setBible(bible);
				this.navigate(this.bibleEditorPane);
				break;
			case NEW_SLIDE:
				BasicSlide slide = new BasicSlide();
				slide.setName("Untitled");
				slide.setX(0);
				slide.setY(0);
				// set the default size to the target screen's size
				Display target = context.getDisplayManager().getPresentationDisplay();
				slide.setWidth(target.getWidth());
				slide.setHeight(target.getHeight());
				this.slideEditorPane.setSlide(slide);
				this.navigate(this.slideEditorPane);
				break;
			case NEW_SLIDE_SHOW:
				if (this.slideShowEditorDialog == null) {
					this.slideShowEditorDialog = new SlideShowEditorDialog(this.getScene().getWindow(), this.context);
				}
				this.slideShowEditorDialog.setValue(new SlideShow());
				this.slideShowEditorDialog.show(s -> {
					if (s != null) {
						this.context.getSlideLibrary().save(s)
							.execute(this.context.getExecutorService());
					}
				});
				break;
			case MANAGE_MEDIA:
				this.navigate(this.mediaLibraryPane);
				break;
			case MANAGE_SLIDES:
				this.navigate(this.slideLibraryPane);
				break;
			case MANAGE_SHOWS:
				this.navigate(this.slideShowLibraryPane);
				break;
			case EXIT:
				// close the application
				// NOTE: we execute a close request so that we can stop the request if
				// there's unsaved changes or background processes still working
				Stage stage = this.context.getJavaFXContext().getStage();
				stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
				break;
			case LOGS:
				// open the log directory
				if (Desktop.isDesktopSupported()) {
				    try {
						Desktop.getDesktop().open(Paths.get(Constants.LOGS_ABSOLUTE_PATH).toFile());
					} catch (IOException ex) {
						LOGGER.error("Unable to open logs directory due to: " + ex.getMessage(), ex);
					}
				} else {
					LOGGER.warn("Desktop is not supported. Failed to open log path.");
				}
			default:
				// do nothing
				break;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.ApplicationPane#isApplicationActionEnabled(org.praisenter.javafx.ApplicationAction)
	 */
	@Override
	public boolean isApplicationActionEnabled(ApplicationAction action) {
    	switch (action) {
			case ABOUT:
			case EXIT:
			case IMPORT_BIBLES:
			case NEW_BIBLE:
			case NEW_SLIDE:
			case NEW_SLIDE_SHOW:
			case IMPORT_MEDIA:
			case IMPORT_SLIDES:
			case IMPORT_SONGS:
			case MANAGE_BIBLES:
			case REINDEX_BIBLES:
			case MANAGE_MEDIA:
			case MANAGE_SLIDES:
			case MANAGE_SHOWS:
			case MANAGE_SONGS:
			case PREFERENCES:
			case LOGS:
				return true;
			default:
				return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.ApplicationPane#isApplicationActionVisible(org.praisenter.javafx.ApplicationAction)
	 */
	@Override
	public boolean isApplicationActionVisible(ApplicationAction action) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.ApplicationPane#setDefaultFocus()
	 */
	@Override
	public void setDefaultFocus() {
		Node content = this.mainContent.get();
		if (content != null && content instanceof ApplicationPane) {
			((ApplicationPane)content).setDefaultFocus();
		} else {
			this.requestFocus();
		}
	}

    /**
     * Event handler for importing bibles.
     */
    private final void promptBibleImport() {
    	BibleActions.biblePromptImport(
    			this.context.getBibleLibrary(), 
    			this.getScene().getWindow())
    	.execute(this.context.getExecutorService());
    }
    
    /**
     * Event handler for importing media.
     */
    private final void promptMediaImport() {
    	MediaActions.mediaPromptImport(
    			this.context.getMediaLibrary(), 
    			this.getScene().getWindow())
    	.execute(this.context.getExecutorService());
    }
    
    /**
     * Event handler for importing slides.
     */
    private final void promptSlideImport() {
    	SlideActions.slidePromptImport(
    			this.context, 
    			this.getScene().getWindow())
    	.execute(this.context.getExecutorService());
    }
    
	// NAVIGATION
	
    /**
     * Checks the current main content {@link ApplicationEditorPane} for unsaved changes
     * and prompts the user if they want to save or discard the changes.
     * <p>
     * If the user chooses to save, this method will call the {@link ApplicationEditorPane#saveChanges}
     * method on the {@link ApplicationEditorPane}.
     * <p>
     * If the user chooses to discard the changes or cancel, nothing will occur.
     * <p>
     * In all cases the user's choice is returned to the caller or null in the case the
     * current main content node is not an {@link ApplicationEditorPane}.
     * @return ButtonType
     */
    public AsyncTask<ButtonType> checkForUnsavedChanges() {
    	Node current = this.getCenter();
    	if (current instanceof ApplicationEditorPane) {
    		ApplicationEditorPane aep = (ApplicationEditorPane)current;
    		// check for any unsaved changes
    		if (aep.hasUnsavedChanges()) {
    			String target = aep.getEditTargetName();
    			LOGGER.info("Unsaved changes detected for '{}'", target);
				Alert alert = Alerts.yesNoCancel(
						getScene().getWindow(),
						Modality.WINDOW_MODAL,
						Translations.get("warning.modified.title"), 
						MessageFormat.format(Translations.get("warning.modified.header"), target), 
						Translations.get("warning.modified.content"));
				Optional<ButtonType> result = alert.showAndWait();
				
				if (result.get() == ButtonType.YES) {
					LOGGER.info("The user requested that the changes to '{}' be saved.", target);
					// get the save task
					return aep.saveChanges().map(t -> {
						if (t.getState() == State.FAILED || t.getState() == State.CANCELLED) {
							return ButtonType.CANCEL;
						}
						return result.get();
					});
					
				} else if (result.get() == ButtonType.NO) {
					LOGGER.info("The user requested that the changes to '{}' be discarded.", target);
				}
				return AsyncTaskFactory.single(result.get());
    		}
		}
    	return AsyncTaskFactory.single();
    }
    
    private void setVisible(Node node, boolean visible) {
		node.setVisible(visible);
		node.setManaged(visible);
		node.setDisable(!visible);
    }
    
	private void navigate(Node node) { 
		AsyncTask<ButtonType> task = checkForUnsavedChanges();
		task.addCompletedHandler(e -> {
			// if the user canceled, we need to stop the navigation
			if (task.getValue() == ButtonType.CANCEL) {
				return;
			}
			
//			this.setCenter(null);
//			this.setCenter(node);
			
			Node current = this.mainContent.get();
			if (current != null)  {
				this.setVisible(current, false);
			}
			
			this.setVisible(node, true);
			
			// JAVABUG (M) 10/24/16 [workaround] Duplicated accelerators https://bugs.openjdk.java.net/browse/JDK-8088068
			// we need to do this so that any accelerators on the content area (the center node) are overridden 
			// by the accelerators in the menu
			this.setTop(null);
			this.setTop(this.menu);
			
			this.mainContent.set(node);

			if (node instanceof ApplicationPane) {
				// when a pane is set as the current pane
				// we may want to focus a particular part of the pane
				((ApplicationPane)node).setDefaultFocus();
			} else {
				node.requestFocus();
			}
		}).execute(this.context.getExecutorService());
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.ApplicationPane#cleanup()
	 */
	@Override
	public void cleanup() {
		// no-op
	}
}

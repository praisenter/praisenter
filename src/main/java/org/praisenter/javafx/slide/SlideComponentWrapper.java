package org.praisenter.javafx.slide;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.text.TextMeasurer;
import org.praisenter.javafx.utility.JavaFxNodeHelper;
import org.praisenter.media.Media;
import org.praisenter.media.MediaType;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.object.MediaObject;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.TextComponent;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public final class SlideComponentWrapper extends SlideRegionWrapper<SlideRegion> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	final StackPane node;
	
	// players for audio/video
	final List<MediaPlayer> players;
	
	public SlideComponentWrapper(PraisenterContext context, SlideRegion component, SlideMode mode) {
		super(context, component, mode);
		
		this.node = new StackPane();
		
		this.players = new ArrayList<MediaPlayer>();
		
		this.build();
	}
	
	// StackPane
	//		VBox/MediaView		background
	// 		Text/VBox/MediaView	content (text, image, video/audio respectively)
	//		VBox				border
	void build() {
		this.node.setUserData(this.component.getId());
		
		int x = this.component.getX();
		int y = this.component.getY();
		int w = this.component.getWidth();
		int h = this.component.getHeight();
		
		// x/y
		this.node.setLayoutX(x);
		this.node.setLayoutY(y);
		
		// width/height
		JavaFxNodeHelper.setSize(this.node, w, h);
	
		VBox contents = new VBox();
		JavaFxNodeHelper.setSize(contents, w, h);
		
		// border
		// the border will go on the background node
		SlideStroke bdr = this.component.getBorder();
		double borderWidth = 0;
		if (bdr != null) {
			contents.setBorder(new Border(getBorderStroke(bdr)));
			borderWidth = bdr.getWidth();
		}
		
		// background
		SlidePaint bg = this.component.getBackground();
		if (bg instanceof MediaObject) {
			// get the media id
			MediaObject mo = (MediaObject)bg;
			UUID id = mo.getId();
			// make sure the id is present
			if (id != null) {
				// get the media
				Media m = this.context.getMediaLibrary().get(id);
				// check for missing media
				if (m != null) {
					// check the media type
					if (m.getMetadata().getType() == MediaType.VIDEO) {
						// check if we need to show a single frame (EDIT) or the video (PRESENT)
						if (this.mode == SlideMode.PRESENT) {
							try {
								// attempt to open the media
								javafx.scene.media.Media media = new javafx.scene.media.Media(m.getMetadata().getPath().toAbsolutePath().toString());
								// create a player
								MediaPlayer player = new MediaPlayer(media);
								this.players.add(player);
								// set the player attributes
								player.setMute(mo.isMute());
								player.setCycleCount(mo.isLoop() ? MediaPlayer.INDEFINITE : 0);
								MediaView view = new MediaView(player);
								// set the scaling
								if (mo.getScaling() == ScaleType.NONUNIFORM) { 
									view.setFitWidth(w);
									view.setFitHeight(h);
								} else if (mo.getScaling() == ScaleType.UNIFORM) {
									// set the fit w/h based on the min
									if (w < h) {
										view.setFitWidth(w);
									} else {
										view.setFitWidth(h);
									}
								}
								// add the player to the background node
								this.node.getChildren().add(view);
							} catch (Exception ex) {
								// if it blows up, then just log the error
								LOGGER.error("Failed to create media or media player.", ex);
							}
						} else {
							// if not in present mode, then just show the single frame
							try  {
								Image image = this.context.getImageCache().get(this.context.getMediaLibrary().getFramePath(m));
								VBox img = new VBox();
								JavaFxNodeHelper.setSize(img, w, h);
								Rectangle r = new Rectangle(0, 0, w, h);
								if (bdr != null) {
									r.setArcHeight(bdr.getRadius() * 2);
									r.setArcWidth(bdr.getRadius() * 2);
								}
								img.setClip(r);
								img.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, this.getBackgroundSize(mo.getScaling()))));
								this.node.getChildren().add(img);
							} catch (Exception ex) {
								// just log the error
								LOGGER.warn("Failed to load image " + m.getMetadata().getPath() + ".", ex);
							}
						}
					} else if (m.getMetadata().getType() == MediaType.IMAGE) {
						// image
						try  {
							Image image = this.context.getImageCache().get(m.getMetadata().getPath());
							VBox img = new VBox();
							JavaFxNodeHelper.setSize(img, w, h);
							Rectangle r = new Rectangle(0, 0, w, h);
							if (bdr != null) {
								r.setArcHeight(bdr.getRadius() * 2);
								r.setArcWidth(bdr.getRadius() * 2);
							}
							img.setClip(r);
							img.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, this.getBackgroundSize(mo.getScaling()))));
							this.node.getChildren().add(img);
						} catch (Exception ex) {
							// just log the error
							LOGGER.warn("Failed to load image " + m.getMetadata().getPath() + ".", ex);
						}
					} else {
						// log warning about type (audio)
						LOGGER.warn("Invalid media type for background {} with path {}.", m.getMetadata().getType(), m.getMetadata().getPath());
					}
				} else {
					// log warning about missing media
					LOGGER.warn("The referenced media {} was not found in the media library.", id);
				}
			} else {
				LOGGER.warn("The media id is null.");
			}
		} else {
			Paint paint = getPaint(bg);
			this.node.setBackground(new Background(new BackgroundFill(paint, bdr != null ? new CornerRadii(bdr.getRadius()) : null, null)));
		}
		
		// type dependent stuff
		
		if (this.component instanceof MediaComponent) {
			// TODO add relevant code
			// for this case we need to just add another node since you could have a video overlaying a gradient or image for example
		} else if (this.component instanceof DateTimeComponent) {
			// TODO how do we do the animation of the time?
		} else if (this.component instanceof TextComponent) {
			// NOTE: TextPlaceHolderComponents should already have their text replaced by this point
			
			TextComponent tc = (TextComponent)this.component;
			// compute the bounding text width and height so 
			// we can compute an accurate font size
			double padding = tc.getPadding();
			double pw = this.component.getWidth() - padding * 2 - borderWidth * 2;
			double ph = this.component.getHeight() - padding * 2;
			
			// set the wrapping width and the bounds type
			Text text = new Text();
			text.setWrappingWidth(pw);
			text.setBoundsType(TextBoundsType.VISUAL);
			
			// component.getText()
			String str = tc.getText();
			
			// compute a fitting font, if necessary
			Font base = Font.font(tc.getFontName(), tc.getFontSize());
			Font font = base;
			if (tc.getFontScaleType() == FontScaleType.REDUCE_SIZE_ONLY) {
				font = TextMeasurer.getFittingFontForParagraph(str, base, tc.getFontSize(), pw, ph, tc.getLineSpacing(), TextBoundsType.VISUAL);
			} else if (tc.getFontScaleType() == FontScaleType.BEST_FIT) {
				font = TextMeasurer.getFittingFontForParagraph(str, base, Double.MAX_VALUE, pw, ph, tc.getLineSpacing(), TextBoundsType.VISUAL);
			}
			System.out.print(font.getSize());
			
			// the text, font, text fill, line spacing and horizontal alignment
			text.setText(str);
			text.setFont(font);
			text.setFill(this.getPaint(tc.getTextPaint()));
			text.setLineSpacing(tc.getLineSpacing());
			text.setTextAlignment(this.getTextAlignment(tc.getHorizontalTextAlignment()));
			
			// text border
			SlideStroke ss = tc.getTextBorder();
			if (ss != null) {
				text.setStroke(this.getPaint(ss.getPaint()));
				text.setStrokeLineCap(this.getStrokeLineCap(ss.getStyle().getCap()));
				text.setStrokeLineJoin(this.getStrokeLineJoin(ss.getStyle().getJoin()));
				text.setStrokeType(this.getStrokeType(ss.getStyle().getType()));
				text.setStrokeWidth(ss.getWidth());
				text.getStrokeDashArray().addAll(ss.getStyle().getDashes());
			}
			
			contents.setPadding(new Insets(padding));
			
			// vertical alignment
			contents.setAlignment(this.getPos(tc.getVerticalTextAlignment()));
			
			contents.getChildren().add(text);
		}
		
		this.node.getChildren().add(contents);
	}
}

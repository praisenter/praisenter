

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.praisenter.song.Lyrics;
import org.praisenter.song.SongInterpreter;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class PDFTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {
    	PDDocument document = PDDocument.load(Paths.get("C:\\Users\\wbittle\\Desktop\\Saved By Grace israel.pdf").toFile());
    	PDFRenderer renderer = new PDFRenderer(document);

    	float width = document.getPage(0).getMediaBox().getWidth();
    	float height = document.getPage(0).getMediaBox().getHeight();
    	
    	// compute a scale based on the quality we need
    	float targetWidth = 1024;
    	float scale = 1.0f;
    	if (width < targetWidth) {
    		scale = targetWidth / width;
    		height = height * scale;
    	}
    	
    	BufferedImage image = new BufferedImage((int)targetWidth, (int)height * document.getNumberOfPages(), BufferedImage.TYPE_INT_RGB);
    	Graphics2D g2d = image.createGraphics();
    	for (int page = 0; page < document.getNumberOfPages(); ++page)
    	{
    		renderer.renderPageToGraphics(page, g2d, scale);
    	}
    	g2d.dispose();
    	
    	System.out.println(image.getWidth());
    	System.out.println(image.getHeight());
    	
    	ImageIO.write(image, "png", Paths.get("C:\\Users\\wbittle\\Desktop\\test.png").toFile());
    	
        
    	// extract all text from PDF
    	PDFTextStripper stripper = new PDFTextStripper();
    	stripper.setStartPage(0);
    	stripper.setEndPage(document.getNumberOfPages());
    	stripper.setSortByPosition(true);
    	String text = stripper.getText(document);
    	System.out.println(text);
    	
    	Image img = SwingFXUtils.toFXImage(image, null);
    	
    	BorderPane root = new BorderPane();
    	ImageView view = new ImageView(img);
    	
    	root.setCenter(view);
    	
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
        
        document.close();
    }
}
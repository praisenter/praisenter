package org.praisenter.data.slide;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.praisenter.data.AbstractPersistAdapter;
import org.praisenter.data.ImportExportFormat;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.PraisenterFormatProvider;
import org.praisenter.data.json.JsonIO;
import org.praisenter.utility.MimeType;

public final class SlidePersistAdapter extends AbstractPersistAdapter<Slide, SlidePathResolver> implements PersistAdapter<Slide> {
	private static final String EXTENSION = "json";
	
	private final SlideConfiguration configuration;
	private final SlideRenderer renderer;
	
	public SlidePersistAdapter(Path basePath, SlideRenderer renderer, SlideConfiguration configuration) {
		super(new SlidePathResolver(basePath, EXTENSION));
		this.configuration = configuration;
		this.renderer = renderer;
		
		this.importExportProviders.put(ImportExportFormat.PRAISENTER3, new PraisenterFormatProvider<>(Slide.class));
	}
	
	@Override
	protected Slide load(Path path) throws IOException {
		if (Files.isRegularFile(path)) {
			String mimeType = MimeType.get(path);
			if (MimeType.JSON.is(mimeType)) {
				Slide s = JsonIO.read(path, Slide.class);
				s.setThumbnailPath(this.pathResolver.getThumbPath(s));
				return s;
			}
		}
		return null;
	}
	
	@Override
	protected void create(Path path, Slide item) throws IOException {
		Path thumbnailPath = this.pathResolver.getThumbPath(item);
		BufferedImage image = this.renderer.renderThumbnail(item, this.configuration.getThumbnailWidth(), this.configuration.getThumbnailHeight());
		ImageIO.write(image, this.pathResolver.getThumbExtension(), thumbnailPath.toFile());
		// NOTE: need to set the thumbnail path because it won't be set
		item.setThumbnailPath(thumbnailPath);
		JsonIO.write(path, item);
	}
	
	@Override
	protected void update(Path path, Slide item) throws IOException {
		Path thumbnailPath = this.pathResolver.getThumbPath(item);
		BufferedImage image = this.renderer.renderThumbnail(item, this.configuration.getThumbnailWidth(), this.configuration.getThumbnailHeight());
		ImageIO.write(image, this.pathResolver.getThumbExtension(), thumbnailPath.toFile());
		// NOTE: need to set the thumbnail path every time 
		// in the case of the slide being new where the field
		// will stay null until the editor is closed and re-opened
		item.setThumbnailPath(thumbnailPath);
		JsonIO.write(path, item);
	}
	
	@Override
	protected void delete(Path path, Slide item) throws IOException {
		Files.deleteIfExists(path);
		Files.deleteIfExists(this.pathResolver.getThumbPath(item));
	}
}

package org.praisenter.ui;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import javafx.scene.image.Image;

public abstract class CachedImage {
    protected final SoftReference<Image> image;
    protected final Instant lastModified;

    public CachedImage(Image image, Instant lastModified) {
        this.image = new SoftReference<Image>(image);
        this.lastModified = lastModified;
    }

    public SoftReference<Image> getImageReference() {
        return this.image;
    }

    public Instant getLastModified() {
        return this.lastModified;
    }

    public Image getImage() {
        return this.image.get();
    }

    public abstract String getLocation();
    public abstract boolean hasLocationChanged(String newLocation);
    public abstract boolean hasUpdated();

    protected static Instant getLastModifiedTime(Path path) throws IOException {
        FileTime fileTime = Files.getLastModifiedTime(path);
        return fileTime.toInstant();
    }

    public static final CachedImage fromPath(Image image, Path path) {
        Instant lastModified = Instant.MIN;
        try {
            lastModified = getLastModifiedTime(path);
        } catch (IOException ex) {
            // if we can't get last modified, just use Instant.MIN as a fallback
        }
        return new PathCachedImage(image, path, lastModified);
    }

    public static final CachedImage fromString(Image image, String location) {
        return new StringCachedImage(image, location);
    }

    private static final class PathCachedImage extends CachedImage {
        private final Path path;

        public PathCachedImage(Image image, Path path, Instant lastModified) {
            super(image, lastModified);
            this.path = path;
        }

        @Override
        public String getLocation() {
            return this.path.toAbsolutePath().toString();
        }

        @Override
        public boolean hasLocationChanged(String newLocation) {
            return !this.path.toAbsolutePath().toString().equals(newLocation);
        }

        @Override
        public boolean hasUpdated() {
            try {
                Instant currentLastModified = getLastModifiedTime(this.path);
                return !currentLastModified.equals(lastModified);
            } catch (IOException ex) {
                // if we can't get last modified, then assume it hasn't changed
                // the reasons for not getting last modified are usually the
                // file doesn't exist or is inaccessible

                // returning false here means that we don't update the image
                // but it also means that we don't flicker
                return false;
            }
        }
    }

    private static final class StringCachedImage extends CachedImage {
        private final String location;

        public StringCachedImage(Image image, String location) {
            super(image, Instant.now());
            this.location = location;
        }

        @Override
        public String getLocation() {
            return this.location;
        }

        @Override
        public boolean hasLocationChanged(String newLocation) {
            return !this.location.equals(newLocation);
        }

        @Override
        public boolean hasUpdated() {
            // For string-based cached images, we assume they never update
            return false;
        }
    }
}
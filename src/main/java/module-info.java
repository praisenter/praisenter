/**
 * Annoyingly, not all dependencies are modular and eclipse won't allow me to manage the maven dependencies one by one
 * either all or none are on the module path.
 * <p>
 * The issue was with lucene. Lucene suguest inludes analyizers.common which contains a class that is also in lucene.core
 * and JPMS won't allow this.
 */
module praisenter {
	// java
	requires java.activation;
	requires java.base;
	requires java.management;
	requires java.net.http;
	requires java.sql;
	requires java.xml;
	requires java.desktop;
	
	// javafx
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.media;
	requires javafx.swing;
	requires javafx.web;
	
	// jackson (JSON)
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.annotation;
	
	// TODO twelvemonkeys image io (not modular, but has MANIFEST value)
	requires com.twelvemonkeys.imageio.core;
	requires com.twelvemonkeys.imageio.jpeg;
	requires com.twelvemonkeys.imageio.metadata;
	requires com.twelvemonkeys.imageio.tiff;
	requires com.twelvemonkeys.common.image;
	requires com.twelvemonkeys.common.lang;
	requires com.twelvemonkeys.common.io;
	
	// apache
	requires org.apache.logging.log4j;
	requires org.apache.logging.log4j.core;
	requires org.apache.commons.io;
	requires org.apache.commons.lang3;
	requires org.apache.commons.text;
	
	// lucene (not modular)
	// biggest issue here is that inclusion of other file-named modules run the risk
	// of including the same classes more than once causing the build to fail.  At this
	// point this is working because this is all we need, but that could change at any
	// time.  The following link is the recommend approach to get around the issue, but
	// I couldn't get it working (it's call shading)
	// https://lucene.apache.org/core/7_3_1/MIGRATE.html
	// https://stackoverflow.com/questions/57607637/lucene-split-package-module-reads-package-org-apache-lucene-analysis-standard
	// https://lucene.472066.n3.nabble.com/Java-9-issues-td4348032.html
	// TODO watch this project to see when they move
	requires lucene.core;
	requires lucene.highlighter;
	requires lucene.memory;
	//requires lucene.suggest;
	//requires lucene.analyzers.common;
	
	// other
	requires org.controlsfx.controls;
	requires org.apache.fontbox;
	requires org.apache.pdfbox;
	requires org.apache.tika.core;
	
	// TODO not modular - only used because ProcessBuilder is broken in Java 16
	requires commons.exec;
	
	// metadata extractor (not modular)
	// TODO not modular - couldn't find a replacement
	requires metadata.extractor;
	requires xmpcore;
	
	// to allow javafx to reflect and instantiate our main class
	exports org.praisenter.ui to javafx.graphics;
	
	// to allow javafx to reflect and pull resources from inside the css
	opens org.praisenter.images;
	
	// to allow jackson to read our data types during serialization/deserialization
	opens org.praisenter.data to com.fasterxml.jackson.databind;
	opens org.praisenter.data.configuration to com.fasterxml.jackson.databind;
	opens org.praisenter.data.bible to com.fasterxml.jackson.databind;
	opens org.praisenter.data.media to com.fasterxml.jackson.databind;
	opens org.praisenter.data.json to com.fasterxml.jackson.databind;
	opens org.praisenter.data.slide to com.fasterxml.jackson.databind;
	opens org.praisenter.data.slide.animation to com.fasterxml.jackson.databind;
	opens org.praisenter.data.slide.effects to com.fasterxml.jackson.databind;
	opens org.praisenter.data.slide.graphics to com.fasterxml.jackson.databind;
	opens org.praisenter.data.slide.media to com.fasterxml.jackson.databind;
	opens org.praisenter.data.slide.text to com.fasterxml.jackson.databind;
	opens org.praisenter.data.song to com.fasterxml.jackson.databind;
}
/**
 * NOT BEING USED - BUT HERE FOR REFERENCE LATER
 * 
 * Annoyingly, not all dependencies are modular and there's lots of issues with including
 * non-modular dependencies (see below for those that are not modular).
 * <p>
 * The setup below (module-info.java and --java-options) worked with modules, but I didn't 
 * feel comfortable at the time doing it this way. So instead, we're building a jlink'ed 
 * JVM just filtering down to the java modules and Java FX modules.  Everything else, i.e.
 * all other maven dependencies are added to the build package and added to the classpath
 * at runtime.  
 */

// ============================================================================
// FOR JPACKAGE ADD THE FOLLOWING TO THE JPACKAGE.ARGS FILES
// - These are needed for Controls FX package
// https://stackoverflow.com/questions/53695304/autocompletionbinding-cannot-access-class-com-sun-javafx-event-eventhandlermanag 
// https://github.com/controlsfx/controlsfx/blob/9.0.0/build.gradle#L1
// ============================================================================
//--java-options "--add-exports=javafx.base/com.sun.javafx.event=ALL-UNNAMED"
//--java-options "--add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED"
//--java-options "--add-exports=javafx.graphics/com.sun.javafx.scene.traversal=ALL-UNNAMED"
//--java-options "--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED"
//--java-options "--add-exports=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED"
//--java-options "--add-exports=javafx.controls/com.sun.javafx.scene.control.inputmap=ALL-UNNAMED"
//--java-options "--add-exports=javafx.base/com.sun.javafx.collections=ALL-UNNAMED"
//--java-options "--add-exports=javafx.base/com.sun.javafx.runtime=ALL-UNNAMED"
//--java-options "--add-opens=javafx.controls/javafx.scene.control.skin=ALL-UNNAMED"
//--java-options "--add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED"

// ============================================================================
// FOR RUNNING LOCALLY FROM ECLIPSE ADD THE FOLLOWING TO THE JVM ARGS
// - These are needed for Controls FX package
// ============================================================================
// --add-exports javafx.base/com.sun.javafx.event=org.controlsfx.controls

//module praisenter {
//	// java
//	requires java.activation;
//	requires java.base;
//	requires java.management;
//	requires java.net.http;
//	requires java.sql;
//	requires java.xml;
//	requires java.desktop;
//	
//	// javafx
//	requires javafx.base;
//	requires javafx.controls;
//	requires javafx.graphics;
//	requires javafx.media;
//	requires javafx.swing;
//	requires javafx.web;
//	
//	// jackson (JSON)
//	requires com.fasterxml.jackson.core;
//	requires com.fasterxml.jackson.databind;
//	requires com.fasterxml.jackson.annotation;
//	
//	// twelvemonkeys image io (NOT MODULAR, but has Automatic-Module-Name value)
//	requires com.twelvemonkeys.imageio.core;
//	requires com.twelvemonkeys.imageio.jpeg;
//	requires com.twelvemonkeys.imageio.metadata;
//	requires com.twelvemonkeys.imageio.tiff;
//	requires com.twelvemonkeys.common.image;
//	requires com.twelvemonkeys.common.lang;
//	requires com.twelvemonkeys.common.io;
//	
//	// apache
//	requires org.apache.logging.log4j;
//	requires org.apache.logging.log4j.core;
//	requires org.apache.commons.io;
//	requires org.apache.commons.lang3;
//	requires org.apache.commons.text;
//	
//	// lucene (NOT MODULAR)
//	// biggest issue here is that inclusion of other file-named modules run the risk
//	// of including the same classes more than once causing the build to fail.  At this
//	// point this is working because this is all we need, but that could change at any
//	// time.  The following link is the recommend approach to get around the issue, but
//	// I couldn't get it working (it's call shading)
//	// https://lucene.apache.org/core/7_3_1/MIGRATE.html
//	// https://stackoverflow.com/questions/57607637/lucene-split-package-module-reads-package-org-apache-lucene-analysis-standard
//	// https://lucene.472066.n3.nabble.com/Java-9-issues-td4348032.html
//	requires lucene.core;
//	requires lucene.highlighter;
//	requires lucene.memory;
//  // These don't seem to be needed so I removed them to fix the issue of duplicate
//  // classes being included
//	//requires lucene.suggest;
//	//requires lucene.analyzers.common;
//	
//	// other
//	requires org.controlsfx.controls;
//	requires org.apache.fontbox;
//	requires org.apache.pdfbox;
//	requires org.apache.tika.core;
//	
//	// NOT MODULAR - only used because ProcessBuilder is broken in Java 16
//	requires commons.exec;
//	
//	// metadata extractor (not modular)
//	// NOT MODULAR - couldn't find a replacement
//	requires metadata.extractor;
//	requires xmpcore;
//	
//	// to allow javafx to reflect and instantiate our main class
//	exports org.praisenter.ui to javafx.graphics;
//	
//	// to allow javafx to reflect and pull resources from inside the css
//	opens org.praisenter.images;
//	
//	// to allow jackson to read our data types during serialization/deserialization
//	opens org.praisenter.data to com.fasterxml.jackson.databind;
//	opens org.praisenter.data.configuration to com.fasterxml.jackson.databind;
//	opens org.praisenter.data.bible to com.fasterxml.jackson.databind;
//	opens org.praisenter.data.media to com.fasterxml.jackson.databind;
//	opens org.praisenter.data.json to com.fasterxml.jackson.databind;
//	opens org.praisenter.data.slide to com.fasterxml.jackson.databind;
//	opens org.praisenter.data.slide.animation to com.fasterxml.jackson.databind;
//	opens org.praisenter.data.slide.effects to com.fasterxml.jackson.databind;
//	opens org.praisenter.data.slide.graphics to com.fasterxml.jackson.databind;
//	opens org.praisenter.data.slide.media to com.fasterxml.jackson.databind;
//	opens org.praisenter.data.slide.text to com.fasterxml.jackson.databind;
//	opens org.praisenter.data.song to com.fasterxml.jackson.databind;
//}

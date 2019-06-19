/**
 * Annoyingly, not all dependencies are modular and eclipse won't allow me to manage the maven dependencies one by one
 * either all or none are on the module path.
 * <p>
 * The issue was with lucene. Lucene suguest inludes analyizers.common which contains a class that is also in lucene.core
 * and JPMS won't allow this.
 */


//module praisenter {
//	// java
//	requires java.activation;
//	requires java.base;
//	requires java.management;
//	requires java.net.http;
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
//	requires jackson.annotations;
//	
//	// twelvemonkeys image io (not modular)
////	requires imageio.core;
////	requires imageio.jpeg;
////	requires imageio.metadata;
////	requires imageio.tiff;
////	requires common.image;
////	requires common.lang;
////	requires common.io;
//	
//	// apache
//	requires org.apache.logging.log4j;
//	requires org.apache.logging.log4j.core;
//	requires org.apache.commons.io;
//	requires org.apache.commons.lang3;
//	
//	// lucene (not modular)
////	requires lucene.core;
////	requires lucene.highlighter;
////	requires lucene.memory;
////	requires lucene.suggest;
//	//requires lucene.analyzers.common;
//	
//	// other
//	requires org.controlsfx.controls;
//	// (not modular)
////	requires commons.logging;
////	requires fontbox;
////	requires pdfbox;
//	requires org.apache.tika.core;
//	
//	// metadata extractor (not modular)
////	requires metadata.extractor;
////	requires xmpcore;
//}
package org.praisenter.javafx.screen;

import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

@XmlRootElement(name = "screens")
@XmlAccessorType(XmlAccessType.NONE)
public final class ScreenConfiguration {
	private final ObjectProperty<Display> primaryScreen = new SimpleObjectProperty<Display>(null);
	
	private final ObjectProperty<Display> operatorScreen = new SimpleObjectProperty<Display>(null);
	private final ObjectProperty<Display> mainScreen = new SimpleObjectProperty<Display>(null);
	private final ObjectProperty<Display> musicianScreen = new SimpleObjectProperty<Display>(null);
	
	private final ObservableSet<Resolution> resolutions = FXCollections.observableSet(new TreeSet<Resolution>());
	
	@XmlElement(name = "primaryScreen", required = false)
	public Display getPrimaryScreen() {
		return this.primaryScreen.get();
	}

	public void setPrimaryScreen(Display display) {
		this.primaryScreen.set(display);
	}

	public ObjectProperty<Display> primaryScreenProperty() {
		return this.primaryScreen;
	}
	
	@XmlElement(name = "operatorScreen", required = false)
	public Display getOperatorScreen() {
		return this.operatorScreen.get();
	}
	
	public void setOperatorScreen(Display display) {
		this.operatorScreen.set(display);
	}
	
	public ObjectProperty<Display> operatorScreenProperty() {
		return this.operatorScreen;
	}
	
	@XmlElement(name = "mainScreen", required = false)
	public Display getMainScreen() {
		return this.mainScreen.get();
	}

	public void setMainScreen(Display display) {
		this.mainScreen.set(display);
	}

	public ObjectProperty<Display> mainScreenProperty() {
		return this.mainScreen;
	}
	
	@XmlElement(name = "musicianScreen", required = false)
	public Display getMusicianScreen() {
		return this.musicianScreen.get();
	}

	public void setMusicianScreen(Display display) {
		this.musicianScreen.set(display);
	}

	public ObjectProperty<Display> musicianScreenProperty() {
		return this.musicianScreen;
	}
	
	@XmlElement(name = "resolution", required = false)
	@XmlElementWrapper(name = "resolutions", required = false)
	public ObservableSet<Resolution> getResolutions() {
		return this.resolutions;
	}
}

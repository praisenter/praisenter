package org.praisenter.javafx.screen;

import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

@XmlRootElement(name = "screens")
@XmlAccessorType(XmlAccessType.NONE)
public final class ScreenConfiguration {
	public static final int SCREEN_NOT_AVAILABLE = -1;
	
	private final IntegerProperty operatorScreenId = new SimpleIntegerProperty(SCREEN_NOT_AVAILABLE);
	private final IntegerProperty primaryScreenId = new SimpleIntegerProperty(SCREEN_NOT_AVAILABLE);
	private final IntegerProperty musicianScreenId = new SimpleIntegerProperty(SCREEN_NOT_AVAILABLE);
	
	private final ObservableSet<Resolution> resolutions = FXCollections.observableSet(new TreeSet<Resolution>());
	
	@XmlElement(name = "operatorScreenId", required = false)
	public int getOperatorScreenId() {
		return this.operatorScreenId.get();
	}
	
	public void setOperatorScreenId(int id) {
		this.operatorScreenId.set(id);
	}
	
	public IntegerProperty operatorScreenIdProperty() {
		return this.operatorScreenId;
	}
	
	@XmlElement(name = "primaryScreenId", required = false)
	public int getPrimaryScreenId() {
		return this.primaryScreenId.get();
	}

	public void setPrimaryScreenId(int id) {
		this.primaryScreenId.set(id);
	}

	public IntegerProperty primaryScreenIdProperty() {
		return this.primaryScreenId;
	}
	
	@XmlElement(name = "musicianScreenId", required = false)
	public int getMusicianScreenId() {
		return this.musicianScreenId.get();
	}

	public void setMusicianScreenId(int id) {
		this.musicianScreenId.set(id);
	}

	public IntegerProperty musicianScreenIdProperty() {
		return this.musicianScreenId;
	}
	
	@XmlElement(name = "resolution", required = false)
	@XmlElementWrapper(name = "resolutions", required = false)
	public ObservableSet<Resolution> getResolutions() {
		return this.resolutions;
	}
}

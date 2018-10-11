/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.data.configuration;

import java.util.Objects;

import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a unique display of the host system.
 * <p>
 * The information stored here is both to aid the presentation system and to 
 * detect changes to the screen layout at start up.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Display implements ReadOnlyDisplay, Copyable, Comparable<Display> {
	private final IntegerProperty id;
	private final ObjectProperty<DisplayRole> role;
	private final StringProperty name;
	private final IntegerProperty x;
	private final IntegerProperty y;
	private final IntegerProperty width;
	private final IntegerProperty height;
	
	public Display() {
		this.id = new SimpleIntegerProperty();
		this.role = new SimpleObjectProperty<>();
		this.name = new SimpleStringProperty();
		
		this.x = new SimpleIntegerProperty();
		this.y = new SimpleIntegerProperty();
		this.width = new SimpleIntegerProperty();
		this.height = new SimpleIntegerProperty();
	}
	
	@Override
	public int compareTo(Display o) {
		int diff = this.x.get() - o.x.get();
		if (diff == 0) {
			diff = this.y.get() - o.y.get();
			if (diff == 0) {
				return this.id.get() - o.id.get();
			}
		}
		return diff;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Display) {
			Display o = (Display)obj;
			return (this.id.get() == o.id.get() &&
					Objects.equals(this.role.get(), o.role.get()) &&
					Objects.equals(this.name.get(), o.name.get()) &&
					this.x.get() == o.x.get() &&
					this.y.get() == o.y.get() &&
					this.width.get() == o.width.get() &&
					this.height.get() == o.height.get());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(
				this.id.get(), 
				this.role.get(), 
				this.name.get(), 
				this.x.get(), 
				this.y.get(), 
				this.width.get(),
				this.height.get());
	}
	
	@Override
	public Display copy() {
		Display display = new Display();
		display.id.set(this.id.get());
		display.role.set(this.role.get());
		display.name.set(this.name.get());
		display.x.set(this.x.get());
		display.y.set(this.y.get());
		display.width.set(this.width.get());
		display.height.set(this.height.get());
		return display;
	}
	
	@Override
	public String toString() {
		return this.name + " #" + this.id + " " + this.role + " (" + this.x + "," + this.y + ") " + this.width + "x" + this.height;
	}
	
	@Override
	@JsonProperty
	public int getId() {
		return this.id.get();
	}
	
	@JsonProperty
	public void setId(int id) {
		this.id.set(id);
	}
	
	@Override
	public IntegerProperty idProperty() {
		return this.id;
	}
	
	@Override
	@JsonProperty
	public DisplayRole getRole() {
		return this.role.get();
	}
	
	@JsonProperty
	public void setRole(DisplayRole role) {
		this.role.set(role);
	}
	
	@Override
	public ObjectProperty<DisplayRole> roleProperty() {
		return this.role;
	}
	
	@Override
	@JsonProperty
	public String getName() {
		return this.name.get();
	}
	
	@JsonProperty
	public void setName(String name) {
		this.name.set(name);
	}
	
	@Override
	public StringProperty nameProperty() {
		return this.name;
	}
	
	@Override
	@JsonProperty
	public int getX() {
		return this.x.get();
	}
	
	@JsonProperty
	public void setX(int x) {
		this.x.set(x);
	}
	
	@Override
	public IntegerProperty xProperty() {
		return this.x;
	}
	
	@Override
	@JsonProperty
	public int getY() {
		return this.y.get();
	}
	
	@JsonProperty
	public void setY(int y) {
		this.y.set(y);
	}
	
	@Override
	public IntegerProperty yProperty() {
		return this.y;
	}
	
	@Override
	@JsonProperty
	public int getWidth() {
		return this.width.get();
	}
	
	@JsonProperty
	public void setWidth(int width) {
		this.width.set(width);
	}
	
	@Override
	public IntegerProperty widthProperty() {
		return this.width;
	}
	
	@Override
	@JsonProperty
	public int getHeight() {
		return this.height.get();
	}
	
	@JsonProperty
	public void setHeight(int height) {
		this.height.set(height);
	}
	
	@Override
	public IntegerProperty heightProperty() {
		return this.height;
	}
}

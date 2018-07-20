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
package org.praisenter.data.slide.animation;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a split animation.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Split extends Animation {
	/** The default orientation */
	public static final Orientation DEFAULT_ORIENTATION = Orientation.VERTICAL;
	
	/** The default operation */
	public static final Operation DEFAULT_OPERATION = Operation.EXPAND;
	
	private final ObjectProperty<Orientation> orientation;
	private final ObjectProperty<Operation> operation;
	
	public Split() {
		this.orientation = new SimpleObjectProperty<>(DEFAULT_ORIENTATION);
		this.operation = new SimpleObjectProperty<>(DEFAULT_OPERATION);
	}
	
	@Override
	public Split copy() {
		Split ani = new Split();
		this.copyTo(ani);
		ani.operation.set(this.operation.get());
		ani.orientation.set(this.orientation.get());
		return ani;
	}

	@JsonProperty
	public Orientation getOrientation() {
		return this.orientation.get();
	}
	
	@JsonProperty
	public void setOrientation(Orientation orientation) {
		this.orientation.set(orientation);
	}
	
	public ObjectProperty<Orientation> orientationProperty() {
		return this.orientation;
	}

	@JsonProperty
	public Operation getOperation() {
		return this.operation.get();
	}
	
	@JsonProperty
	public void setOperation(Operation operation) {
		this.operation.set(operation);
	}
	
	public ObjectProperty<Operation> operationProperty() {
		return this.operation;
	}
}

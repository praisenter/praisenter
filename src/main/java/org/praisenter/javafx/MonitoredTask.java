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
package org.praisenter.javafx;

import org.praisenter.javafx.utility.Fx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;

/**
 * Represents a task that can be monitored and that has a name and resulting status.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 * @param <T> the task result type
 */
public abstract class MonitoredTask<T> extends Task<T> {
	/** The task name/description */
	private final String name;
	
	/** The task's result status */
	private ObjectProperty<MonitoredTaskResultStatus> resultStatus = new SimpleObjectProperty<>();
	
	/**
	 * Minimal constructor.
	 * @param name the task name or description
	 */
	public MonitoredTask(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	/**
	 * Returns the task name/description.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the result status or null if not complete.
	 * @return {@link MonitoredTaskResultStatus}
	 */
	public MonitoredTaskResultStatus getResultStatus() {
		return this.resultStatus.get();
	}

	/**
	 * Returns the result status property.
	 * @return ReadOnlyObjectProperty&lt;{@link MonitoredTaskResultStatus}&gt;
	 */
	public ReadOnlyObjectProperty<MonitoredTaskResultStatus> resultStatusProperty() {
		return this.resultStatus;
	}
	
	/**
	 * Called from sub classes to set the result status.
	 * @param resultStatus the result status
	 */
	protected void setResultStatus(MonitoredTaskResultStatus resultStatus) {
		// make sure we run on the FX thread
		Fx.runOnFxThead(() -> {
			this.resultStatus.set(resultStatus);
		});
	}
}

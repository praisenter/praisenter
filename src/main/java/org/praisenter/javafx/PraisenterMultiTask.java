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

import java.util.Collections;
import java.util.List;

import org.praisenter.FailedOperation;

/**
 * Represents a task that can be monitored and that has a name and resulting status.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 * @param <T> the task result type
 * @param <V> the task result
 */
public abstract class PraisenterMultiTask<T, V> extends PraisenterTask<T> {
	/** The list of failures */
	private List<FailedOperation<V>> failures;
	
	/**
	 * Minimal constructor.
	 * @param name the task name or description
	 */
	public PraisenterMultiTask(String name) {
		super(name);
		this.failures = null;
	}
	
	/**
	 * Returns the result status or null if not complete.
	 * @return List&lt;{@link FailedOperation}&lt;V&gt;&gt;
	 */
	public List<FailedOperation<V>> getResultFailures() {
		if (this.failures == null) return null;
		return Collections.unmodifiableList(this.failures);
	}
	
	/**
	 * Called from sub classes to set the result failures.
	 * @param failures the result failures
	 */
	protected void setResultFailures(List<FailedOperation<V>> failures) {
		this.failures = failures;
	}
}

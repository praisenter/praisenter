/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.common;

/**
 * Represents a common interface for an object that displays the progress
 * of a long running task.
 * @author William Bittle
 * @version 2.0.2
 * @since 2.0.2
 */
public interface ProgressListener {
	/**
	 * Updates the progress to 0% complete using the given task name
	 * as the new task (clearing the sub task).
	 * @param wait true to wait until the task finishes
	 * @param taskName the task name
	 */
	public abstract void updateProgress(boolean wait, String taskName);
	
	/**
	 * Updates the progress given the percent complete.  The current task names
	 * are retained.
	 * @param wait true to wait until the task finishes
	 * @param value the percent complete in the range [0, 100]
	 */
	public abstract void updateProgress(boolean wait, int value);
	
	/**
	 * Updates the progress given the percent complete and sub task name.  The main
	 * task name is retained.
	 * @param wait true to wait until the task finishes
	 * @param value the percent complete in the range [0, 100]
	 * @param subTaskName the sub task name
	 */
	public abstract void updateProgress(boolean wait, int value, String subTaskName);
	
	/**
	 * Updates the progress given the percent complete, task name, and sub task name.
	 * @param wait true to wait until the task finishes
	 * @param value the percent complete in the range [0, 100]
	 * @param taskName the task name
	 * @param subTaskName the sub task name
	 */
	public abstract void updateProgress(boolean wait, int value, String taskName, String subTaskName);
}

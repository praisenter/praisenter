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
package org.praisenter.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a batch of changes to make to the settings in one commit.
 * <p>
 * This class supports chaining of the changes.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> the return type after modification
 */
public final class SettingBatch<T> extends SettingMap<SettingBatch<T>> {
	/** The parent settings map */
	private final SettingMap<T> parent;
	
	/** The batched settings */
	private final Map<Setting, Object> settings;
	
	/**
	 * Minimal constructor.
	 * @param parent the parent settings map
	 */
	SettingBatch(SettingMap<T> parent) {
		this.parent = parent;
		this.settings = new HashMap<Setting, Object>();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#get(org.praisenter.javafx.configuration.Setting)
	 */
	@Override
	public Object get(Setting setting) {
		return this.settings.get(setting);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#isSet(org.praisenter.javafx.configuration.Setting)
	 */
	@Override
	public boolean isSet(Setting setting) {
		return this.settings.containsKey(setting) && this.settings.get(setting) != null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#set(org.praisenter.javafx.configuration.Setting, java.lang.Object)
	 */
	@Override
	public SettingBatch<T> set(Setting setting, Object value) {
		this.settings.put(setting, value);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#remove(org.praisenter.javafx.configuration.Setting)
	 */
	@Override
	public SettingBatch<T> remove(Setting setting) {
		// for remove, we actually want to just set the value to null
		this.settings.put(setting, null);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#setAll(java.util.Map)
	 */
	@Override
	public SettingBatch<T> setAll(Map<Setting, Object> settings) {
		this.settings.putAll(settings);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.configuration.SettingMap#getAll()
	 */
	@Override
	public Map<Setting, Object> getAll() {
		return Collections.unmodifiableMap(this.settings);
	}
	
	/**
	 * Commits this batch to the settings.
	 * @return T
	 */
	public T commitBatch() {
		return this.parent.setAll(this.settings);
	}
}

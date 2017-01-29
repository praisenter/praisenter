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
package org.praisenter.javafx.configuration;

import java.util.Map;
import java.util.UUID;

/**
 * Represents a generic settings map for storing various settings.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> the return type after making a change
 */
abstract class SettingMap<T> {
	
	// internal
	
	/**
	 * Sets all the given settings.
	 * @param settings the settings to set
	 * @return T
	 */
	protected abstract T setAll(Map<Setting, Object> settings);

	/**
	 * Sets the given setting to the given value.
	 * @param setting the setting
	 * @param value the value
	 * @return T
	 */
	protected abstract T set(Setting setting, Object value);
	
	/**
	 * Returns the given settings value or null if not present.
	 * @param setting the setting
	 * @return String
	 */
	protected abstract Object get(Setting setting);

	/**
	 * Returns an unmodifiable set of the settings.
	 * @return Map&lt;{@link Setting}, String&gt;
	 */
	protected abstract Map<Setting, Object> getAll();
	
	// public interface
	
	/**
	 * Returns true if the given setting is present and non-null.
	 * @param setting the setting
	 * @return boolean
	 */
	public abstract boolean isSet(Setting setting);
	
	/**
	 * Removes the given setting.
	 * @param setting the setting
	 * @return T
	 */
	public abstract T remove(Setting setting);
	
	// convenience set
	
	/**
	 * Sets the given setting to the given string value.
	 * @param setting the setting
	 * @param value the value
	 * @return T
	 */
	public T setString(Setting setting, String value) {
		return this.set(setting, value);
	}
	
	/**
	 * Sets the given setting to the given boolean value.
	 * @param setting the setting
	 * @param value the value
	 * @return T
	 */
	public T setBoolean(Setting setting, boolean value) {
		return this.set(setting, value);
	}
	
	/**
	 * Sets the given setting to the given byte value.
	 * @param setting the setting
	 * @param value the value
	 * @return T
	 */
	public T setByte(Setting setting, byte value) {
		return this.set(setting, value);
	}

	/**
	 * Sets the given setting to the given short value.
	 * @param setting the setting
	 * @param value the value
	 * @return T
	 */
	public T setShort(Setting setting, short value) {
		return this.set(setting, value);
	}
	
	/**
	 * Sets the given setting to the given int value.
	 * @param setting the setting
	 * @param value the value
	 * @return T
	 */
	public T setInt(Setting setting, int value) {
		return this.set(setting, value);
	}
	
	/**
	 * Sets the given setting to the given long value.
	 * @param setting the setting
	 * @param value the value
	 * @return T
	 */
	public T setLong(Setting setting, long value) {
		return this.set(setting, value);
	}
	
	/**
	 * Sets the given setting to the given double value.
	 * @param setting the setting
	 * @param value the value
	 * @return T
	 */
	public T setDouble(Setting setting, double value) {
		return this.set(setting, value);
	}
	
	/**
	 * Sets the given setting to the given float value.
	 * @param setting the setting
	 * @param value the value
	 * @return T
	 */
	public T setFloat(Setting setting, float value) {
		return this.set(setting, value);
	}
	
	/**
	 * Sets the given setting to the given UUID value.
	 * @param setting the setting
	 * @param value the value
	 * @return T
	 */
	public T setUUID(Setting setting, UUID value) {
		return this.set(setting, value);
	}
	
	/**
	 * Sets the given setting to the given object value.
	 * @param setting the setting
	 * @param value the value
	 * @return T
	 */
	public <V> T setObject(Setting setting, V value) {
		return this.set(setting, value);
	}
	
	// convenience get

	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or is empty.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return String
	 */
	public String getString(Setting setting, String defaultValue) {
		Object val = this.get(setting);
		String value = val != null && val instanceof String ? (String)val : null;
		if (value == null || value.trim().length() == 0) {
			return defaultValue;
		}
		return value;
	}
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a boolean.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return boolean
	 */
	public boolean getBoolean(Setting setting, boolean defaultValue) {
		Object val = this.get(setting);
		if (val != null && val instanceof Boolean) {
			return (Boolean)val;
		}
		return defaultValue;
	}
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a byte.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return byte
	 */
	public byte getByte(Setting setting, byte defaultValue) {
		Object val = this.get(setting);
		if (val != null && val instanceof Byte) {
			return (Byte)val;
		}
		return defaultValue;
	}

	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a short.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return short
	 */
	public short getShort(Setting setting, short defaultValue) {
		Object val = this.get(setting);
		if (val != null && val instanceof Short) {
			return (Short)val;
		}
		return defaultValue;
	}
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a int.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return int
	 */
	public int getInt(Setting setting, int defaultValue) {
		Object val = this.get(setting);
		if (val != null && val instanceof Integer) {
			return (Integer)val;
		}
		return defaultValue;
	}
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a long.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return long
	 */
	public long getLong(Setting setting, long defaultValue) {
		Object val = this.get(setting);
		if (val != null && val instanceof Long) {
			return (Long)val;
		}
		return defaultValue;
	}
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a double.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return double
	 */
	public double getDouble(Setting setting, double defaultValue) {
		Object val = this.get(setting);
		if (val != null && val instanceof Double) {
			return (Double)val;
		}
		return defaultValue;
	}
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a float.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return float
	 */
	public float getFloat(Setting setting, float defaultValue) {
		Object val = this.get(setting);
		if (val != null && val instanceof Float) {
			return (Float)val;
		}
		return defaultValue;
	}
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't a UUID.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return UUID
	 */
	public UUID getUUID(Setting setting, UUID defaultValue) {
		Object val = this.get(setting);
		if (val != null) {
			if (val instanceof UUID) {
				return (UUID)val;
			// annoyingly UUID is stored as a string when contained
			// in a map, so let's just try to parse it
			} else if (val instanceof String) {
				try {
					return UUID.fromString((String)val);
				} catch (Exception ex) {
					// if the parsing fails, then just assume its
					// the wrong value
				}
			}
		}
		return defaultValue;
	}
	
	/**
	 * Returns the value for the given setting or the given default value
	 * if the setting isn't present or isn't of type V.
	 * @param setting the setting
	 * @param defaultValue the default
	 * @return V
	 */
	@SuppressWarnings("unchecked")
	public <V> V getObject(Setting setting, V defaultValue) {
		Object val = this.get(setting);
		if (val != null) {
			try {
				return (V)val;
			} catch (ClassCastException ex) {
				return defaultValue;
			}
		}
		return defaultValue;
	}
	
	// batching
	
	/**
	 * Creates a new batch to set settings on.
	 * @return {@link SettingBatch}&lt;T&gt;
	 */
	public SettingBatch<T> createBatch() {
		return new SettingBatch<T>(this);
	}
	
	/**
	 * Commits the given batch to this settings.
	 * @param batch the batch to commit
	 * @return T
	 */
	public T commitBatch(SettingBatch<T> batch) {
		return this.setAll(batch.getAll());
	}
}

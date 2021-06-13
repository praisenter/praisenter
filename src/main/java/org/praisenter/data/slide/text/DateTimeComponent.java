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
package org.praisenter.data.slide.text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;

import org.praisenter.Watchable;
import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.json.SimpleDateFormatJsonDeserializer;
import org.praisenter.data.json.SimpleDateFormatJsonSerializer;
import org.praisenter.data.slide.ReadOnlySlideComponent;
import org.praisenter.data.slide.ReadOnlySlideRegion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A component to show the current date and time based on a given format.
 * @author William Bittle
 * @version 3.0.0
 */
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY)
@JsonTypeName(value = "dateTimeComponent")
public final class DateTimeComponent extends TimedTextComponent implements ReadOnlyDateTimeComponent, ReadOnlyTimedTextComponent, ReadOnlyTextComponent, ReadOnlySlideComponent, ReadOnlySlideRegion, Copyable, Identifiable {
	private final ObjectProperty<SimpleDateFormat> dateTimeFormat;
	
	public DateTimeComponent() {
		this.dateTimeFormat = new SimpleObjectProperty<>();
		
		this.text.bind(Bindings.createStringBinding(() -> {
			DateFormat format = this.dateTimeFormat.get();
			
			if (format == null) format = SimpleDateFormat.getDateInstance();
			
			return format.format(Date.from(this.now.get().atZone(ZoneId.systemDefault()).toInstant()));
			
		}, this.dateTimeFormat, this.now));
	}
	
	@Override
	public DateTimeComponent copy() {
		DateTimeComponent dtc = new DateTimeComponent();
		this.copyTo(dtc);
		dtc.dateTimeFormat.set(this.dateTimeFormat.get());
		return dtc;
	}
	
	@Override
	@JsonProperty
	@JsonSerialize(using = SimpleDateFormatJsonSerializer.class)
	public SimpleDateFormat getDateTimeFormat() {
		return this.dateTimeFormat.get();
	}
	
	@JsonProperty
	@JsonDeserialize(using = SimpleDateFormatJsonDeserializer.class)
	public void setDateTimeFormat(SimpleDateFormat format) {
		this.dateTimeFormat.set(format);
	}

	@Override
	@Watchable(name = "dateTimeFormat")
	public ObjectProperty<SimpleDateFormat> dateTimeFormatProperty() {
		return this.dateTimeFormat;
	}
}

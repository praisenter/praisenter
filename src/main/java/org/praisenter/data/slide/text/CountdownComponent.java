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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.praisenter.Watchable;
import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.json.LocalDateTimeJsonDeserializer;
import org.praisenter.data.json.LocalDateTimeJsonSerializer;
import org.praisenter.data.slide.ReadOnlySlideComponent;
import org.praisenter.data.slide.ReadOnlySlideRegion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A component to show a count down to a specified local time.
 * @author William Bittle
 * @version 3.0.0
 */
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY)
@JsonTypeName(value = "countdownComponent")
public final class CountdownComponent extends TimedTextComponent implements ReadOnlyCountdownComponent, ReadOnlyTimedTextComponent, ReadOnlyTextComponent, ReadOnlySlideComponent, ReadOnlySlideRegion, Copyable, Identifiable {
	public static final String DEFAULT_FORMAT = "%1$02d:%2$02d:%3$02d:%4$02d:%5$02d:%6$02d";
	
	private final ObjectProperty<LocalDateTime> countdownTarget;
	private final BooleanProperty countdownTimeOnly;
	private final BooleanProperty stopAtZeroEnabled;
	private final StringProperty countdownFormat;
	
	public CountdownComponent() {
		this.countdownTarget = new SimpleObjectProperty<>();
		this.countdownTimeOnly = new SimpleBooleanProperty(false);
		this.stopAtZeroEnabled = new SimpleBooleanProperty(true);
		this.countdownFormat = new SimpleStringProperty(DEFAULT_FORMAT);
		
		this.text.bind(Bindings.createStringBinding(() -> {
			boolean timeOnly = this.countdownTimeOnly.get();
			boolean stopAtZero = this.stopAtZeroEnabled.get();
			LocalDateTime target = this.countdownTarget.get();
			LocalDateTime now = this.now.get();
			String format = this.countdownFormat.get();
			
			if (target == null) target = LocalDateTime.now();
			if (format == null) format = DEFAULT_FORMAT;
			
			if (timeOnly) {
				// get the time of the target only
				LocalTime time = target.toLocalTime();
				target = time.atDate(LocalDate.now());
				if (target.isBefore(now)) {
					target = target.plusDays(1);
				}
			}
			
			return formatCountdown(format, target, now, stopAtZero);
		}, this.countdownTarget, this.countdownFormat, this.countdownTimeOnly, this.stopAtZeroEnabled, this.now));
	}
	
	@Override
	public CountdownComponent copy() {
		CountdownComponent cc = new CountdownComponent();
		this.copyTo(cc);
		cc.countdownFormat.set(this.countdownFormat.get());
		cc.countdownTarget.set(this.countdownTarget.get());
		cc.countdownTimeOnly.set(this.countdownTimeOnly.get());
		return cc;
	}
	
	/**
	 * Computes and formats the difference in target date and now.
	 * @param format the format defined by String.format
	 * @param target the target date/time
	 * @return String
	 */
	private final String formatCountdown(String format, LocalDateTime target, LocalDateTime now, boolean stopAtZero) {
		long years = 0;
		long months = 0;
		long days = 0;
		long hours = 0;
		long minutes = 0;
		long seconds = 0;
		
		// get the individual durations and advance each time
		if (target != null) {
			years	= now.until(target, ChronoUnit.YEARS); 	now = now.plusYears(years);
			months	= now.until(target, ChronoUnit.MONTHS); 	now = now.plusMonths(months);
			days	= now.until(target, ChronoUnit.DAYS); 		now = now.plusDays(days);
			hours	= now.until(target, ChronoUnit.HOURS); 	now = now.plusHours(hours);
			minutes = now.until(target, ChronoUnit.MINUTES); 	now = now.plusMinutes(minutes);
			seconds = now.until(target, ChronoUnit.SECONDS);
		}
		
		if (stopAtZero) {
			years = Math.max(years, 0);
			months = Math.max(months, 0);
			days = Math.max(days, 0);
			hours = Math.max(hours, 0);
			minutes = Math.max(minutes, 0);
			seconds = Math.max(seconds, 0);
		}
		
		if (format != null && format.trim().length() > 0) {
			return String.format(
			        format, 
			        years,
			        months,
			        days,
			        hours,
			        minutes,
			        seconds);
		}
		
		return String.format(
		        DEFAULT_FORMAT, 
		        years,
		        months,
		        days,
		        hours,
		        minutes,
		        seconds);
	}

	@Override
	@JsonProperty
	@JsonSerialize(using = LocalDateTimeJsonSerializer.class)
	public LocalDateTime getCountdownTarget() {
		return this.countdownTarget.get();
	}
	
	@JsonProperty
	@JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
	public void setCountdownTarget(LocalDateTime target) {
		this.countdownTarget.set(target);
	}

	@Override
	@Watchable(name = "countdownTarget")
	public ObjectProperty<LocalDateTime> countdownTargetProperty() {
		return this.countdownTarget;
	}
	
	@Override
	@JsonProperty
	public String getCountdownFormat() {
		return this.countdownFormat.get();
	}
	
	@JsonProperty
	public void setCountdownFormat(String format) {
		this.countdownFormat.set(format);
	}
	
	@Override
	@Watchable(name = "countdownFormat")
	public StringProperty countdownFormatProperty() {
		return this.countdownFormat;
	}
	
	@Override
	@JsonProperty
	public boolean isCountdownTimeOnly() {
		return this.countdownTimeOnly.get();
	}
	
	@JsonProperty
	public void setCountdownTimeOnly(boolean timeOnly) {
		this.countdownTimeOnly.set(timeOnly);
	}
	
	@Override
	@Watchable(name = "countdownTimeOnly")
	public BooleanProperty countdownTimeOnlyProperty() {
		return this.countdownTimeOnly;
	}
	
	@Override
	public boolean hasAnimatedContent() {
		return true;
	}
	
	@JsonProperty
	public void setStopAtZeroEnabled(boolean enabled) {
		this.stopAtZeroEnabled.set(enabled);
	}
	
	@Override
	@Watchable(name = "stopAtZeroEnabled")
	public BooleanProperty stopAtZeroEnabledProperty() {
		return this.stopAtZeroEnabled;
	}
	
	@Override
	public boolean isStopAtZeroEnabled() {
		return this.stopAtZeroEnabled.get();
	}
}

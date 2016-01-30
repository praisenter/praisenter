package org.praisenter.slide.text;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.xml.adapters.SimpleDateFormatTypeAdapter;

@XmlRootElement(name = "dateTimeComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class DateTimeComponent extends AbstractTextComponent implements SlideRegion, SlideComponent, TextComponent {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The default date format */
	private static final String DEFAULT_FORMAT = "EEEE MMMM d, yyyy";
	
	/** The date/time format */
	@XmlAttribute(name = "format", required = false)
	@XmlJavaTypeAdapter(value = SimpleDateFormatTypeAdapter.class)
	SimpleDateFormat dateTimeFormat;
	
	public String getText() {
		return this.dateTimeFormat.format(new Date());
	}

	public SimpleDateFormat getDateTimeFormat() {
		return dateTimeFormat;
	}

	public void setDateTimeFormat(SimpleDateFormat dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}
}

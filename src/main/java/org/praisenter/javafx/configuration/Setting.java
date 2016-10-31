package org.praisenter.javafx.configuration;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum Setting {
	GENERAL_THEME,
	GENERAL_LANGUAGE,
	
	BIBLE_PRIMARY,
	BIBLE_SECONDARY,
	BIBLE_SHOW_RENUMBER_WARNING
}

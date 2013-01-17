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
package org.praisenter.utilities;

/**
 * Class used to format a valid XML document with indents and newlines.
 * <p>
 * Simplified from: http://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java/2920419#2920419
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class XmlFormatter {
	/** The new line separator */
	private static final String NEW_LINE = System.getProperty("line.separator");
	
	/**
	 * Hidden constructor.
	 */
	private XmlFormatter() {}

	/**
	 * Formats the string (xml).
	 * @param xml the xml string
	 * @param indentNumChars the number of spaces to use for the indention
	 * @return String
	 */
	public static final String format(String xml, int indentNumChars) {
		boolean singleLine = false;
		int indent = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < xml.length(); i++) {
			char currentChar = xml.charAt(i);
			if (currentChar == '<') {
				char nextChar = xml.charAt(i + 1);
				if (nextChar == '/')
					indent -= indentNumChars;
				if (!singleLine)
		            sb.append(XmlFormatter.createIndentation(indent));
				if (nextChar != '?' && nextChar != '!' && nextChar != '/')
					indent += indentNumChars;
				singleLine = false;
			}
			sb.append(currentChar);
			if (currentChar == '>') {
				if (xml.charAt(i - 1) == '/') {
					indent -= indentNumChars;
					sb.append(NEW_LINE);
				} else {
					int nextStartElementPos = xml.indexOf('<', i);
					if (nextStartElementPos > i + 1) {
						String textBetweenElements = xml.substring(i + 1, nextStartElementPos);
						// If the space between elements is solely newlines,
						// let them through to preserve additional newlines
						// in source document.
						if (textBetweenElements.replaceAll("(\n|\r\n|\r)", "").length() == 0) {
							sb.append(textBetweenElements + NEW_LINE);
						}
						// Put tags and text on a single line if the text is
						// short.
						else {
							sb.append(textBetweenElements);
							singleLine = true;
						}
						i = nextStartElementPos - 1;
					} else {
						sb.append(NEW_LINE);
					}
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * Adds spaces to create indentation.
	 * @param numChars the number of spaces
	 * @return String
	 */
	private static final String createIndentation(int numChars) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numChars; i++)
			sb.append(" ");
		return sb.toString();
	}
}
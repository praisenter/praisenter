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
package org.praisenter.slide.text;

/**
 * An enumeration of OS/2 table of the <a href="http://www.microsoft.com/typography/otspec/os2.htm#wtc">OpenType font spec</a>  
 * (matching Java FX's enumeration).
 * @author William Bittle
 * @version 3.0.0
 */
public enum SlideFontWeight {
	/** Represents a font weight of 900 */
	BLACK,
	
	/** Represents a font weight of 700 */
	BOLD,
	
	/** Represents a font weight of 800 */
	EXTRA_BOLD,
	
	/** Represents a font weight of 200 */
	EXTRA_LIGHT,
	
	/** Represents a font weight of 300 */
	LIGHT,
	
	/** Represents a font weight of 500 */
	MEDIUM,
	
	/** Represents a font weight of 400 */
	NORMAL,
	
	/** Represents a font weight of 600 */
	SEMI_BOLD,
	
	/** Represents a font weight of 100 */
	THIN;
}

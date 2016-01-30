package org.praisenter.utility;

public class Maf {
	public static final double EPSILON = 0.01;
	
	public static final boolean equals(double a, double b) {
		return Math.abs(a - b) <= EPSILON;
	}
}

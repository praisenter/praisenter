package org.praisenter;

public final class Version {
	private Version() {};
	
	public static final int MAJOR = 3;
	public static final int MINOR = 0;
	public static final int REVISION = 0;
	public static final String STRING = asString();
	
	private static final String asString() {
		return MAJOR + "." + MINOR + "." + REVISION;
	}
	
	public static final int[] parse(String version) {
		String[] parts = version.split("\\.");
		if (parts.length != 3) throw new IllegalArgumentException("The version string didn't split into 3 parts using the '.' character");
		int[] v = new int[3];
		try {
			v[0] = Integer.parseInt(parts[0]);
			v[1] = Integer.parseInt(parts[1]);
			v[2] = Integer.parseInt(parts[2]);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("The version parts must all be integers: " + ex.getMessage(), ex);
		}
		return v;
	}
}

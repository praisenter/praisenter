package org.praisenter;

public final class Version implements Comparable<Version> {
	private static final int MAJOR = 3;
	private static final int MINOR = 1;
	private static final int REVISION = 4;
	
	public static final Version VERSION = new Version(MAJOR, MINOR, REVISION);
	public static final String STRING = VERSION.toString();
	
	private final int major;
	private final int minor;
	private final int revision;

	public Version(int major, int minor, int revision) {
		this.major = major;
		this.minor = minor;
		this.revision = revision;
	}
	
	@Override
	public String toString() {
		return this.major + "." + this.minor + "." + this.revision;
	}
	
	public static final Version parse(String version) throws IllegalArgumentException {
		if (version == null) throw new IllegalArgumentException("The version string cannot be null.");
		
		String[] parts = version.split("\\.");
		if (parts.length != 3) throw new IllegalArgumentException("The version string '" + version + "' didn't split into 3 parts using the '.' character");
		
		int[] v = new int[3];
		try {
			v[0] = Integer.parseInt(parts[0]);
			v[1] = Integer.parseInt(parts[1]);
			v[2] = Integer.parseInt(parts[2]);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("The version '" + version + "' parts must all be integers: " + ex.getMessage(), ex);
		}
		
		return new Version(v[0], v[1], v[2]);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Version) {
			Version v = (Version)obj;
			return v.compareTo(this) == 0;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
	    int result = 1;
	    result = prime * result + this.major;
	    result = prime * result + this.minor;
	    result = prime * result + this.revision;
	    return result;
	}
	
	public boolean isGreaterThan(Version other) {
		return this.compareTo(other) > 0;
	}
	
	public boolean isLessThan(Version other) {
		return this.compareTo(other) < 0;
	}
	
	@Override
	public int compareTo(Version version) {
		int diff = this.major - version.major;
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return 1;
		}
		
		diff = this.minor - version.minor;
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return 1;
		}
		
		diff = this.revision - version.revision;
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return 1;
		}
		
		return 0;
	}
	
	public int getMajor() {
		return this.major;
	}
	
	public int getMinor() {
		return this.minor;
	}
	
	public int getRevision() {
		return this.revision;
	}
}

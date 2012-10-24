package org.praisenter;

/**
 * The version of the application.
 * @author William Bittle
 * @version 3.1.1
 * @since 1.0.0
 */
public class Version {
	/** The major version number; high impact changes; changes that require a migration plan */
	private static final int MAJOR = 1;
	
	/** The minor version number; medium impact changes; changes that do not require a migration plan */
	private static final int MINOR = 0;
	
	/** The revision number; small impact changes; bug fixes, small enhancements, etc. */
	private static final int REVISION = 0;
	
	/**
	 * Hide the constructor.
	 */
	private Version() {}
	
	/**
	 * Returns the version as a string.
	 * @return String
	 */
	public static String getVersion() {
		return MAJOR + "." + MINOR + "." + REVISION;
	}
	
	/**
	 * Returns the version numbers in an array of ints.
	 * <p>
	 * The array is of length 3 and has the major, minor, and
	 * revision numbers in that order.
	 * @return int[] the major, minor, and revision numbers
	 */
	public static int[] getVersionNumbers() {
		return new int[] { MAJOR, MINOR, REVISION };
	}
	
	/**
	 * Returns the major version number.
	 * @return int
	 */
	public static int getMajorNumber() {
		return MAJOR;
	}
	
	/**
	 * Returns the minor version number.
	 * @return int
	 */
	public static int getMinorNumber() {
		return MINOR;
	}
	
	/**
	 * Returns the revision number.
	 * @return int
	 */
	public static int getRevisionNumber() {
		return REVISION;
	}
}

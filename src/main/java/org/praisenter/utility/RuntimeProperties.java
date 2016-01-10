package org.praisenter.utility;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.List;

public final class RuntimeProperties {
	private static final FileSystem FILE_SYSTEM = FileSystems.getDefault();
	
	public static final String NEW_LINE_SEPARATOR = getNewLineSeparator();
	public static final String PATH_SEPARATOR = FILE_SYSTEM.getSeparator();
	public static final String JAVA_VERSION = getJavaVersion();
	public static final String JAVA_VENDOR = getJavaVendor();
	public static final String OPERATING_SYSTEM = getOperatingSystem();
	public static final boolean IS_WINDOWS_OS = isWindows();
	public static final boolean IS_MAC_OS = isMac();
	public static final boolean IS_LINUX_OS = isLinux();
	public static final String ARCHITECTURE = getArchitecture();
	public static final String USER_HOME = getUserHomeDirectory();
	public static final String JAVA_HOME = getJavaHomeDirectory();
	public static final String JVM_ARGUMENTS = getJvmArguments();
	
	private RuntimeProperties() {}
	
	private static final String getNewLineSeparator() {
		try {
			return System.getProperty("line.separator");
		} catch (SecurityException ex) {
			return "\n";
		}
	}
	
	/**
	 * Returns the current runtime version.
	 * <p>
	 * Returns null if an exception occurs.
	 * @return String
	 */
	private static final String getJavaVersion() {
		try{
			return System.getProperty("java.version");
		} catch (SecurityException e) {
			return null;
		}
	}
	
	/**
	 * Returns the current runtime vendor.
	 * <p>
	 * Returns null if an exception occurs.
	 * @return String
	 */
	private static final String getJavaVendor() {
		try{
			return System.getProperty("java.vendor");
		} catch (SecurityException e) {
			return null;
		}
	}
	
	/**
	 * Returns the current operating system name.
	 * <p>
	 * Returns null if an exception occurs.
	 * @return String
	 */
	private static final String getOperatingSystem() {
		try{
			return System.getProperty("os.name") + " " + System.getProperty("os.version");
		} catch (SecurityException e) {
			return null;
		}
	}
	
	/**
	 * Returns true if the operating system is Windows.
	 * @return boolean
	 * @since 2.0.1
	 */
	private static final boolean isWindows() {
		try{
			String os = System.getProperty("os.name");
			if (os != null) {
				return os.toLowerCase().startsWith("windows");
			}
		} catch (SecurityException e) {}
		return false;
	}
	
	/**
	 * Returns true if the operating system is Mac Os.
	 * @return boolean
	 */
	private static final boolean isMac() {
		try{
			String os = System.getProperty("os.name");
			if (os != null) {
				return os.toLowerCase().indexOf("mac") >= 0;
			}
		} catch (SecurityException e) {}
		return false;
	}
	
	/**
	 * Returns true if the operating system is Linux.
	 * @return boolean
	 * @since 2.0.1
	 */
	private static final boolean isLinux() {
		try{
			String os = System.getProperty("os.name");
			if (os != null) {
				return os.toLowerCase().indexOf("linux") >= 0;
			}
		} catch (SecurityException e) {}
		return false;
	}
	
	/**
	 * Returns the current architecture.
	 * <p>
	 * Returns null if an exception occurs.
	 * @return String
	 */
	private static final String getArchitecture() {
		try{
			return System.getProperty("os.arch");
		} catch (SecurityException e) {
			return "";
		}
	}
	
	/**
	 * Returns the user's home directory.
	 * <p>
	 * Returns <b>blank</b> if an exception occurs (rather than null like the other methods).
	 * @return String
	 */
	private static final String getUserHomeDirectory() {
		try{
			return System.getProperty("user.home");
		} catch (SecurityException e) {
			return "";
		}
	}
	
	/**
	 * Returns the directory of the executing java process.
	 * <p>
	 * Returns null if an exception occurs.
	 * @return String
	 */
	private static final String getJavaHomeDirectory() {
		try{
			return System.getProperty("java.home");
		} catch (SecurityException e) {
			return "";
		}
	}
	
	/**
	 * Returns a string of all the JVM arguments used to start this VM.
	 * @return String
	 * @since 2.0.1
	 */
	private static final String getJvmArguments() {
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = runtimeBean.getInputArguments();
		StringBuilder sb = new StringBuilder();
		for (String argument : arguments) {
			sb.append(argument).append(" ");
		}
		return sb.toString();
	}
}

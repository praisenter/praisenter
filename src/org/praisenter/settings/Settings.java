package org.praisenter.settings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.log4j.Logger;
import org.praisenter.display.FontScaleType;
import org.praisenter.display.ScaleQuality;
import org.praisenter.display.ScaleType;
import org.praisenter.display.TextAlignment;
import org.praisenter.utilities.ImageUtilities;
import org.praisenter.utilities.WindowUtilities;

/**
 * Base class used to store application settings.
 * <p>
 * This class handles translation from the different settings types into Strings 
 * and back again.  This class also handles storing null objects.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Settings {
	/** The static logger */
	private static final Logger LOGGER = Logger.getLogger(Settings.class);
	
	/** A static object representing null */
	protected static final Object NULL = new Object();
	
	/** Map to hold the key to object pairs */
	protected Map<String, Object> settings;
	
	/** The properties */
	protected Properties properties;
	
	/**
	 * Default constructor.
	 */
	protected Settings() {
		this(new Properties());
	}
	
	/**
	 * Optional constructor.
	 * @param properties the properties
	 */
	protected Settings(Properties properties) {
		if (properties == null) throw new NullPointerException();
		this.properties = properties;
		this.settings = new ConcurrentHashMap<String, Object>();
	}
	
	/**
	 * Sets a the given setting.
	 * @param key the key
	 * @param value the value
	 * @throws SettingsException thrown if an error occurred while assigning the setting
	 */
	protected void setSetting(String key, Object value) throws SettingsException {
		if (value == null) value = NULL;
		// set the setting
		this.settings.put(key, value);
		// set the property
		this.properties.setProperty(key, this.objectToString(value));
	}
	
	/**
	 * Helper method for copying settings from one settings object to another.
	 * @param settings the settings to copy
	 * @throws SettingsException thrown if an error occurred while assigning a setting
	 */
	protected void setSettings(Settings settings) throws SettingsException {
		// make sure they are the same type
		if (this.getClass() == settings.getClass()) {
			// if they are the same then set all the settings
			Set<String> keys = settings.properties.stringPropertyNames();
			for (String key : keys) {
				String value = settings.properties.getProperty(key);
				this.properties.setProperty(key, value);
			}
		} else {
			throw new SettingsException("The settings types are not the same: " + this.getClass() + " != " + settings.getClass());
		}
	}
	
	// helper methods for getting value types
	
	/**
	 * Returns the setting as a color object for the given key.
	 * <p>
	 * Returns Color.BLACK if the setting is not set.
	 * @param key the key
	 * @return Color
	 */
	protected Color getColorSetting(String key) {
		// get the setting
		Object object = this.settings.get(key);
		// check if its null
		if (object == NULL || object == null) {
			// default the object
			object = Color.BLACK;
			// get the string from the properties
			String string = this.properties.getProperty(key);
			// make sure we can parse it
			if (string != null && string.trim().length() > 0) {
				// get the color
				try {
					object = this.colorFromString(string.trim());
				} catch (Exception e) {
					LOGGER.warn("Unable to parse setting: " + key + " value: " + string, e);
				}
			}
			// set it 
			this.settings.put(key, object);
		}
		
		return (Color)object;
	}
	
	/**
	 * Returns the setting as an image object for the given key.
	 * <p>
	 * Returns null if the setting is not set.
	 * @param key the key
	 * @return BufferedImage
	 */
	protected BufferedImage getImageSetting(String key) {
		// get the setting
		Object object = this.settings.get(key);
		// check if its null
		if (object == NULL || object == null) {
			object = null;
			// get the string from the properties
			String string = this.properties.getProperty(key);
			// make sure we can parse it
			if (string != null && string.trim().length() > 0) {
				// get the image
				try {
					BufferedImage image = this.imageFromString(string);
					// set it 
					this.settings.put(key, image);
					// return it
					return image;
				} catch (IOException e) {
					LOGGER.warn("Unable to parse setting: " + key + " value: " + string, e);
				}
			}
		}
		
		return (BufferedImage)object;
	}
	
	/**
	 * Returns the setting as a font object for the given key.
	 * <p>
	 * Returns null if the setting is not set.
	 * @param key the key
	 * @return Font
	 */
	protected Font getFontSetting(String key) {
		// get the setting
		Object object = this.settings.get(key);
		// check if its null
		if (object == NULL || object == null) {
			object = null;
			// get the string from the properties
			String string = this.properties.getProperty(key);
			// make sure we can parse it
			if (string != null && string.trim().length() > 0) {
				// get the font
				try {
					Font font = this.fontFromString(string.trim());
					// set it 
					this.settings.put(key, font);
					// return it
					return font;
				} catch (Exception e) {
					LOGGER.warn("Unable to parse setting: " + key + " value: " + string, e);
				}
			}
		}
		
		return (Font)object;
	}
	
	/**
	 * Returns the setting as a rectangle object for the given key.
	 * @param key the key
	 * @return Rectangle
	 */
	protected Rectangle getRectangleSetting(String key) {
		// get the setting
		Object object = this.settings.get(key);
		// check if its null
		if (object == NULL || object == null) {
			object = null;
			// get the string from the properties
			String string = this.properties.getProperty(key);
			// make sure we can parse it
			if (string != null && string.trim().length() > 0) {
				// get the font
				try {
					Rectangle rectangle = this.rectangleFromString(string.trim());
					// set it 
					this.settings.put(key, rectangle);
					// return it
					return rectangle;
				} catch (Exception e) {
					LOGGER.warn("Unable to parse setting: " + key + " value: " + string, e);
				}
			}
		}
		
		return (Rectangle)object;
	}
	
	/**
	 * Returns the setting as a Dimension object for the given key.
	 * @param key the key
	 * @return Dimension
	 */
	protected Dimension getDimensionSetting(String key) {
		// get the setting
		Object object = this.settings.get(key);
		// check if its null
		if (object == NULL || object == null) {
			object = null;
			// get the string from the properties
			String string = this.properties.getProperty(key);
			// make sure we can parse it
			if (string != null && string.trim().length() > 0) {
				// get the font
				try {
					Dimension size = this.dimensionFromString(string.trim());
					// set it 
					this.settings.put(key, size);
					// return it
					return size;
				} catch (Exception e) {
					LOGGER.warn("Unable to parse setting: " + key + " value: " + string, e);
				}
			}
		}
		
		return (Dimension)object;
	}
	
	/**
	 * Returns the setting as a GraphicsDevice object for the given key.
	 * <p>
	 * Returns the secondary device if not set.
	 * <p>
	 * Returns the primary device if no secondary device is available.
	 * @param key the key
	 * @return GraphicsDevice
	 */
	protected GraphicsDevice getGraphicsDevice(String key) {
		// get the setting
		Object object = this.settings.get(key);
		// check if its null
		if (object == NULL || object == null) {
			object = null;
			// get the string from the properties
			String string = this.properties.getProperty(key);
			// make sure we can parse it
			if (string != null && string.trim().length() > 0) {
				// get the font
				try {
					GraphicsDevice device = this.deviceFromString(string);
					if (device != null) {
						// set it 
						this.settings.put(key, device);
						// return it
						return device;
					} else {
						LOGGER.warn("Display device: [" + string + "] not found.");
					}
				} catch (Exception e) {
					LOGGER.warn("Unable to parse setting: " + key + " value: " + string, e);
				}
			}
		}
		
		return (GraphicsDevice)object;
	}
	
	/**
	 * Returns the setting as a {@link ScaleType} enum for the given key.
	 * <p>
	 * Returns {@link ScaleType#NONUNIFORM} if the setting is not set.
	 * @param key the key
	 * @return {@link ScaleType}
	 */
	protected ScaleType getScaleTypeSetting(String key) {
		// get the setting
		Object object = this.settings.get(key);
		// check if its null
		if (object == NULL || object == null) {
			// default the object
			object = ScaleType.NONUNIFORM;
			// get the string from the properties
			String string = this.properties.getProperty(key);
			// make sure we can parse it
			if (string != null && string.trim().length() > 0) {
				// get the enum
				try {
					object = ScaleType.valueOf(string.trim());
				} catch (IllegalArgumentException e) {
					LOGGER.warn("Unable to parse setting: " + key + " value: " + string, e);
				}
			}
			// set it 
			this.settings.put(key, object);
		}
		
		return (ScaleType)object;
	}
	
	/**
	 * Returns the setting as a {@link ScaleQuality} enum for the given key.
	 * <p>
	 * Returns {@link ScaleQuality#BILINEAR} if the setting is not set.
	 * @param key the key
	 * @return {@link ScaleQuality}
	 */
	protected ScaleQuality getScaleQualitySetting(String key) {
		// get the setting
		Object object = this.settings.get(key);
		// check if its null
		if (object == NULL || object == null) {
			// default the object
			object = ScaleQuality.BILINEAR;
			// get the string from the properties
			String string = this.properties.getProperty(key);
			// make sure we can parse it
			if (string != null && string.trim().length() > 0) {
				// get the enum
				try {
					object = ScaleQuality.valueOf(string.trim());
				} catch (IllegalArgumentException e) {
					LOGGER.warn("Unable to parse setting: " + key + " value: " + string, e);
				}
			}
			// set it 
			this.settings.put(key, object);
		}
		
		return (ScaleQuality)object;
	}
	
	/**
	 * Returns the setting as a {@link FontScaleType} enum for the given key.
	 * <p>
	 * Returns {@link FontScaleType#REDUCE_SIZE_ONLY} if the setting is not set.
	 * @param key the key
	 * @return {@link FontScaleType}
	 */
	protected FontScaleType getFontScaleTypeSetting(String key) {
		// get the setting
		Object object = this.settings.get(key);
		// check if its null
		if (object == NULL || object == null) {
			// default the object
			object = FontScaleType.REDUCE_SIZE_ONLY;
			// get the string from the properties
			String string = this.properties.getProperty(key);
			// make sure we can parse it
			if (string != null && string.trim().length() > 0) {
				// get the enum
				try {
					object = FontScaleType.valueOf(string.trim());
				} catch (IllegalArgumentException e) {
					LOGGER.warn("Unable to parse setting: " + key + " value: " + string, e);
				}
			}
			// set it 
			this.settings.put(key, object);
		}
		
		return (FontScaleType)object;
	}
	
	/**
	 * Returns the setting as a {@link TextAlignment} enum for the given key.
	 * <p>
	 * Returns {@link TextAlignment#CENTER} if the setting is not set.
	 * @param key the key
	 * @return {@link TextAlignment}
	 */
	protected TextAlignment getTextAlignmentSetting(String key) {
		// get the setting
		Object object = this.settings.get(key);
		// check if its null
		if (object == NULL || object == null) {
			// default the object
			object = TextAlignment.CENTER;
			// get the string from the properties
			String string = this.properties.getProperty(key);
			// make sure we can parse it
			if (string != null && string.trim().length() > 0) {
				// get the enum
				try {
					object = TextAlignment.valueOf(string.trim());
				} catch (IllegalArgumentException e) {
					LOGGER.warn("Unable to parse setting: " + key + " value: " + string, e);
				}
			}
			// set it 
			this.settings.put(key, object);
		}
		
		return (TextAlignment)object;
	}
	
	/**
	 * Returns the setting as a boolean for the given key.
	 * <p>
	 * Returns false if the setting is not set.
	 * @param key the key
	 * @return boolean
	 */
	protected String getStringSetting(String key) {
		// get the setting
		Object object = this.settings.get(key);
		// check if its null
		if (object == NULL || object == null) {
			// default the object
			object = null;
			// get the string from the properties
			String string = this.properties.getProperty(key);
			// make sure we can parse it
			if (string != null && string.trim().length() > 0) {
				// get the boolean
				object = string.trim();
			}
			// set the setting
			this.settings.put(key, object);
		}
		
		return (String)object;
	}
	
	/**
	 * Returns the setting as a boolean for the given key.
	 * <p>
	 * Returns false if the setting is not set.
	 * @param key the key
	 * @return boolean
	 */
	protected boolean getBooleanSetting(String key) {
		// get the setting
		Object object = this.settings.get(key);
		// check if its null
		if (object == NULL || object == null) {
			// default the object
			object = false;
			// get the string from the properties
			String string = this.properties.getProperty(key);
			// make sure we can parse it
			if (string != null && string.trim().length() > 0) {
				// get the boolean
				object = Boolean.parseBoolean(string.trim());
			}
			// set the setting
			this.settings.put(key, object);
		}
		
		return (boolean)object;
	}
	
	/**
	 * Returns the setting as a integer for the given key.
	 * <p>
	 * Returns 0 if the setting is not set.
	 * @param key the key
	 * @return integer
	 */
	protected int getIntegerSetting(String key) {
		// get the setting
		Object object = this.settings.get(key);
		// check if its null
		if (object == NULL || object == null) {
			// default the object
			object = 0;
			// get the string from the properties
			String string = this.properties.getProperty(key);
			// make sure we can parse it
			if (string != null && string.trim().length() > 0) {
				// get the integer
				try {
					object = Integer.parseInt(string.trim());
				} catch (NumberFormatException e) {
					LOGGER.warn("Unable to parse setting: " + key + " value: " + string, e);
				}
			}
			// set the setting
			this.settings.put(key, object);
		}
		
		return (int)object;
	}
	
	// helper methods for storing and retrieving complex types
	
	/**
	 * Method to translate any object into a string.
	 * @param object the object
	 * @return String its string representation
	 * @throws SettingsException if a setting could not be converted
	 */
	protected String objectToString(Object object) throws SettingsException {
		if (object == null) {
			return "";
		} else if (object == NULL) {
			return "";
		} else if (object instanceof String) {
			return (String)object;
		} else if (object instanceof Color) {
			return this.colorToString((Color)object);
		} else if (object instanceof BufferedImage) {
			try {
				return this.imageToString((BufferedImage)object);
			} catch (IOException e) {
				throw new SettingsException("Unable to convert the buffered image into a string.", e);
			}
		} else if (object instanceof Font) {
			return this.fontToString((Font)object);
		} else if (object instanceof Rectangle) {
			return this.rectangleToString((Rectangle)object);
		} else if (object instanceof Dimension) {
			return this.dimensionToString((Dimension)object);
		} else if (object instanceof GraphicsDevice) {
			return this.deviceToString((GraphicsDevice)object);
		} else {
			return object.toString();
		}
	}
	
	/**
	 * Converts a Color object to a string.
	 * @param color the color
	 * @return String
	 */
	protected String colorToString(Color color) {
		StringBuilder sb = new StringBuilder();
		sb.append(color.getRed()).append(",")
		  .append(color.getGreen()).append(",")
		  .append(color.getBlue()).append(",")
		  .append(color.getAlpha());
		return sb.toString();
	}
	
	/**
	 * Converts a String to a Color object.
	 * @param color the string
	 * @return Color
	 * @throws NumberFormatException if any color component is not an integer
	 * @throws IndexOutOfBoundsException if the color does not have 4 components (rgba)
	 * @throws IllegalArgumentException if any color component is not in the range [0, 255]
	 */
	protected Color colorFromString(String color) throws NumberFormatException, IndexOutOfBoundsException, IllegalArgumentException {
		String[] components = color.split(",");
		// returns the color
		return new Color(
				Integer.parseInt(components[0]),
				Integer.parseInt(components[1]),
				Integer.parseInt(components[2]),
				Integer.parseInt(components[3]));
	}
	
	/**
	 * Converts a BufferedImage object to a string.
	 * @param image the image
	 * @return String
	 * @throws IOException if an IO error occurs
	 */
	protected String imageToString(BufferedImage image) throws IOException {
		return ImageUtilities.getBase64ImageString(image).replaceAll("\\s*", "");
	}
	
	/**
	 * Converts a string into a BufferedImage object.
	 * @param image the string image
	 * @return BufferedImage
	 * @throws IOException if an IO error occurs; usually if the image data is corrupt
	 */
	protected BufferedImage imageFromString(String image) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(image.getBytes());
		BufferedInputStream bis = new BufferedInputStream(bais);
		Base64InputStream b64is = new Base64InputStream(bis);
		return ImageIO.read(b64is);
	}
	
	/**
	 * Converts a Font object to a string.
	 * @param font the font
	 * @return String
	 */
	protected String fontToString(Font font) {
		StringBuilder sb = new StringBuilder();
		sb.append(font.getFamily()).append(",")
		  .append(font.getStyle()).append(",")
		  .append(font.getSize());
		return sb.toString();
	}
	
	/**
	 * Converts a string into a Font object.
	 * @param font the string font
	 * @return Font
	 * @throws NumberFormatException if the style or size is not an integer
	 * @throws IndexOutOfBoundsException if the family, style, or size is not present
	 */
	protected Font fontFromString(String font) throws NumberFormatException, IndexOutOfBoundsException {
		String[] components = font.split(",");
		// return the font
		return new Font(
				components[0],
				Integer.parseInt(components[1]),
				Integer.parseInt(components[2]));
	}
	
	/**
	 * Converts a Rectangle object to a string.
	 * @param rectangle the rectangle
	 * @return String
	 */
	protected String rectangleToString(Rectangle rectangle) {
		StringBuilder sb = new StringBuilder();
		sb.append(rectangle.x).append(",")
		  .append(rectangle.y).append(",")
		  .append(rectangle.width).append(",")
		  .append(rectangle.height);
		return sb.toString();
	}
	
	/**
	 * Converts a string into a Rectangle object.
	 * @param rectangle the string rectangle
	 * @return Rectangle
	 * @throws NumberFormatException if the x, y, width, or height are not integers
	 * @throws IndexOutOfBoundsException if x, y, width or height is missing
	 */
	protected Rectangle rectangleFromString(String rectangle) throws NumberFormatException, IndexOutOfBoundsException {
		String[] components = rectangle.split(",");
		// return the font
		return new Rectangle(
				Integer.parseInt(components[0]),
				Integer.parseInt(components[1]),
				Integer.parseInt(components[2]),
				Integer.parseInt(components[3]));
	}

	/**
	 * Converts a Dimension object to a string.
	 * @param size the size
	 * @return String
	 */
	protected String dimensionToString(Dimension size) {
		StringBuilder sb = new StringBuilder();
		sb.append(size.width).append(",")
		  .append(size.height);
		return sb.toString();
	}
	
	/**
	 * Converts a string into a Dimension object.
	 * @param size the string size
	 * @return Dimension
	 * @throws NumberFormatException if the x, y, width, or height are not integers
	 * @throws IndexOutOfBoundsException if width or height is missing
	 */
	protected Dimension dimensionFromString(String size) throws NumberFormatException, IndexOutOfBoundsException {
		String[] components = size.split(",");
		// return the size
		return new Dimension(Integer.parseInt(components[0]), Integer.parseInt(components[1]));
	}
	
	/**
	 * Converts a GraphicsDevice to a string.
	 * @param device the device
	 * @return String
	 */
	protected String deviceToString(GraphicsDevice device) {
		return device.getIDstring();
	}
	
	/**
	 * Converts the given string to a GraphicsDevice object.
	 * <p>
	 * Returns null if no matching graphics device found.
	 * @param device the device id
	 * @return GraphicsDevice
	 */
	protected GraphicsDevice deviceFromString(String device) {
		return WindowUtilities.getScreenDeviceForId(device);
	}
}

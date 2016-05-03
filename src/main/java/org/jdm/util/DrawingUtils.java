package org.jdm.util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

public class DrawingUtils {

	public static Color colorFromString(String colorName) {

		Color color;
		try {

			// lookup the color
			Field field = Class.forName("java.awt.Color").getField(colorName);
			color = (Color) field.get(null);

		} catch (Exception e) {

			// null if not found
			color = null;
		}

		// return the color
		return color;
	}

	public static Cursor getHiddenCursor() {

		// transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// new blank cursor.
		return Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
	}
}

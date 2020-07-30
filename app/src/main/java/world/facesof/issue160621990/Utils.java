package world.facesof.issue160621990;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

class Utils {
	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	}

	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		return px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	}
}

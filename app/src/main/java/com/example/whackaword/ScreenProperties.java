package com.example.whackaword;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * The ScreenProperties class is responsible for managing the properties of the screen.
 * It contains a class variable for whether the screen is small
 */
public class ScreenProperties
{
    public static boolean smallScreen;

    /**
     * Setter for whether the screen is small
     */
    public static void setSmallScreenTo(boolean smallScreen)
    {
        ScreenProperties.smallScreen = smallScreen;
    }

    /**
     * Returns true if the screen is small,
     * i.e. has a width of less than 1000dp.
     * Otherwise, returns false
     */
    public static boolean isScreenSmall(Context aContext)
    {
        DisplayMetrics displayMetrics = aContext.getResources().getDisplayMetrics();
        // Gets the display metrics (such as screen density, width and height) of the Android device

        float screenWidthInDP = displayMetrics.widthPixels / displayMetrics.density;
        // DP refers to density-independent pixels

        return screenWidthInDP < 1000;
    }

}

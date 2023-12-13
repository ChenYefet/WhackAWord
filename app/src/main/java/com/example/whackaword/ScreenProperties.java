package com.example.whackaword;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * The ScreenProperties class is responsible for managing the properties of the screen
 *
 * The ScreenProperties class contains a class variable for
 * whether the screen is small,
 * as well as a constant class variable for
 * the width of large screens
 * in dp (density-independent pixels)
 */
public class ScreenProperties
{
    private static final int LARGE_SCREEN_WIDTH = 1200;
    public static boolean screenIsSmall;

    /**
     * Setter for whether the screen is small
     */
    public static void setScreenIsSmallTo(boolean isScreenSmall)
    {
        ScreenProperties.screenIsSmall = isScreenSmall;
    }

    /**
     * Returns true if the screen is small,
     * i.e. has a width less than LARGE_SCREEN_WIDTH.
     * Otherwise, returns false
     */
    public static boolean isScreenSmall(Context aContext)
    {
        DisplayMetrics displayMetrics = aContext.getResources().getDisplayMetrics();
        // Gets the display metrics (such as screen density, width and height) of the Android device

        float screenWidthInDP = displayMetrics.widthPixels / displayMetrics.density;

        return screenWidthInDP < LARGE_SCREEN_WIDTH;
    }

}

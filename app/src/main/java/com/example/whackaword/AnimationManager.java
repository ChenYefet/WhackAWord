package com.example.whackaword;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * The AnimationManager class is responsible for managing animations
 * in the Whack-A-Word game
 *
 * The AnimationManager class contains three class variables:
 *
 * firstCardIsAboutToPopUp, which records whether the first card is about to pop up
 *
 * numberOfPopUpTimes, which records the number of times cards pop up
 *
 * popUpBeingManaged, which keeps track of
 * which pop-up is currently being managed.
 * This is necessary in order to limit the duration of pop-ups
 */
public class AnimationManager extends DisplayManager
{
    public static boolean firstCardIsAboutToPopUp;
    public static int numberOfPopUpTimes;
    public static int popUpBeingManaged;

    /**
     * Initialises the animation properties
     */
    public static void initialiseAnimationProperties()
    {
        AnimationManager.firstCardIsAboutToPopUp = true;
        AnimationManager.numberOfPopUpTimes = 0;
        AnimationManager.popUpBeingManaged = 1;
    }

    /**
     * Causes each card set for display to pop up
     */
    public static void cardsPopUp(WhackAWordActivity aWhackAWordActivity)
    {
        if (AnimationManager.firstCardIsAboutToPopUp)
        {
            DisplayManager.displayFoodItemsOnCards(aWhackAWordActivity);
            AnimationManager.firstCardIsAboutToPopUp = false;
        }

        for (FoodItem foodItem : Collections.mapOfFoodItemsToTheirFoodCards.keySet())
        {
            FoodCard foodCard = Collections.mapOfFoodItemsToTheirFoodCards.get(foodItem);
            FrameLayout foodCardFrameLayout = aWhackAWordActivity.findViewById(foodCard.getID());
            ObjectAnimator animation = ObjectAnimator.ofFloat(foodCardFrameLayout, "translationY", AnimationManager.getUpwardsTranslation(aWhackAWordActivity));

            animation.setDuration(500);
            // The animation lasts for half a second (500 milliseconds)

            animation.setStartDelay(1200);
            // Delays starting the animation after start() is called by one and a fifth seconds (1200 milliseconds)

            if (AnimationManager.firstCardIsAboutToPopUp)
            {
                animation.start();
            }
            else
            {
                new Handler().postDelayed(animation::start, 250);
            }
            // Delays calling for the start of the animation by a quarter of a second (250 milliseconds)
            // in order to synchronise better with the audio for the correct food item,
            // apart from when the first card is about to pop up,
            // since it synchronises better without a delay in that instance

        }

        AudioManager.playPopUpSoundEffect(aWhackAWordActivity);

        AnimationManager.numberOfPopUpTimes++;

        Collections.mapOfPopUpTimesToWhetherACardHasBeenTappedOnTime.put(AnimationManager.numberOfPopUpTimes, false);
        // The value of the map (false) is changed to true if a card is tapped on time
        // (via the setClickListenerForFoodCard method in the TapManager class)

        AnimationManager.limitPopUpDuration(aWhackAWordActivity);
    }

    /**
     * Causes each card on display to hide,
     * and clears their click listeners
     */
    public static void hideCards(WhackAWordActivity aWhackAWordActivity)
    {
        float amountTranslatedFromInitialPosition = 0;
        // 'Initial position' refers to the position of the card before runtime

        for (FoodItem foodItem : Collections.mapOfFoodItemsToTheirFoodCards.keySet())
        {
            FoodCard foodCard = Collections.mapOfFoodItemsToTheirFoodCards.get(foodItem);
            FrameLayout foodCardFrameLayout = aWhackAWordActivity.findViewById(foodCard.getID());
            ObjectAnimator animation = ObjectAnimator.ofFloat(foodCardFrameLayout, "translationY", amountTranslatedFromInitialPosition);

            animation.setDuration(500); // The animation lasts for half a second (500 milliseconds)

            animation.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    DisplayManager.displayFoodItemsOnCards(aWhackAWordActivity);
                }
                // When the animation has ended, the cards have gone into their holes,
                // so the onAnimationEnd method ensures that
                // when the food cards change their food items,
                // this change happens out of sight of the user

            });

            new Handler().postDelayed(animation::start, 400);

        }

        AudioManager.playAudioConcurrently(aWhackAWordActivity, R.raw.hide_cards);

        Collections.availableFoodCards = new ArrayList<>(Collections.foodCards);

        TapManager.clearClickListeners();
    }

    /**
     * Causes the sky to change colour every six seconds
     * (as set out in changing_sky.xml
     * which is set as the background of the sky_view View element
     * in activity_whack_a_word.xml)
     */
    public static void animateSky(WhackAWordActivity aWhackAWordActivity)
    {
        View skyView = aWhackAWordActivity.findViewById(R.id.sky_view);
        AnimationDrawable animationDrawable = (AnimationDrawable) skyView.getBackground();

        animationDrawable.setEnterFadeDuration(2500);
        // It takes two and a half seconds (2500 milliseconds) for the current sky colours to fully fade in

        animationDrawable.setExitFadeDuration(2500);
        // It takes two and a half seconds (2500 milliseconds) for the current sky colours to fully fade out

        animationDrawable.start();
    }

    /**
     * Helper method that limits the cards' pop-up duration to eight seconds (8000 milliseconds)
     */
    private static void limitPopUpDuration(WhackAWordActivity aWhackAWordActivity)
    {
        new Handler().postDelayed(() ->
        {
            boolean aCardHasBeenTappedOnTime = Boolean.TRUE.equals(Collections.mapOfPopUpTimesToWhetherACardHasBeenTappedOnTime.get(AnimationManager.popUpBeingManaged));
            // Boolean.TRUE.equals() checks that what is in the brackets is equal to true,
            // and returns false if it is either null or false

            if (!aCardHasBeenTappedOnTime)
            {
                WhackAWordActivity.tryAgain(aWhackAWordActivity);
            }

            AnimationManager.popUpBeingManaged++;
            // popUpBeingManaged is incremented within the postDelayed method
            // (and not outside of it)
            // so that while each pop-up is being managed,
            // popUpBeingManaged would not be further incremented
            // but would have to wait until this point
            // within the postDelayed method
            // to be incremented,
            // ensuring that you are always managing the appropriate pop-up

        }, 8000);

    }

    /**
     * Helper method that returns the amount
     * in density-independent pixels (dp)
     * that a card needs to translate upwards from its initial (hidden) position
     * in order for it to pop up
     */
    private static float getUpwardsTranslation(Context aContext)
    {
        float amountTranslatedFromInitialPositionInPixels, amountTranslatedFromInitialPositionInDP;

        DisplayMetrics displayMetrics = aContext.getResources().getDisplayMetrics();
        // Gets the display metrics (such as screen density, width and height) of the Android device

        if (ScreenProperties.screenIsSmall)
        {
            amountTranslatedFromInitialPositionInPixels = -178; // For small screens
        }
        else
        {
            amountTranslatedFromInitialPositionInPixels = -278; // For large screens
        }
        // Determines the translation amount based on screen width,
        // since screens of width 1200dp and above have a different layout,
        // as shown in app/src/main/res/layout-w1200dp/activity_whack_a_word.xml

        amountTranslatedFromInitialPositionInDP = amountTranslatedFromInitialPositionInPixels * displayMetrics.density;
        // DP refers to density-independent pixels

        return amountTranslatedFromInitialPositionInDP;
    }

}
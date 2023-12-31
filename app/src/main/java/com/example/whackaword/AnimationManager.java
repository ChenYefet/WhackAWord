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
import java.util.concurrent.CountDownLatch;

/**
 * The AnimationManager class is responsible for managing animations
 * in the Whack-A-Word game
 *
 * It contains nine constant class variables
 * for managing time- and space-related animation properties,
 * such as durations, delays, and translations
 *
 * It also contains three other class variables:
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
    private static final int POP_UP_ANIMATION_DURATION = 500;
    private static final int HIDE_CARDS_ANIMATION_DURATION = 500;
    // The animation for cards popping up or hiding lasts for half a second (500 milliseconds)

    private static final int POP_UP_ANIMATION_DELAY = 1450;
    public static final int HIDE_CARDS_ANIMATION_DELAY_WHEN_CORRECT_FOOD_CARD_IS_TAPPED = 400;
    // The delay before starting the pop-up animation (after the start() method is called)
    // is one and nine twentieths seconds (1450 milliseconds),
    // while the delay before starting the hide cards animation (after the start() method is called)
    // is two fifths of a second (400 milliseconds).
    // The latter delay is only implemented whenever a correct food card is tapped
    // allowing it to appear above the ground and changing colours
    // for the duration of the delay

    private static final float POP_UP_TRANSLATION_FOR_SMALL_SCREENS = -178;
    private static final float POP_UP_TRANSLATION_FOR_LARGE_SCREENS = -278;
    // These values refer to pixels that are to be
    // converted to dp (density-independent pixels) via the getUpwardsTranslation method

    private static final int POP_UP_DURATION_LIMIT = 8000;
    // The duration limit for cards to remain popped up is eight seconds (8000 milliseconds)

    private static final int SKY_FADE_IN_DURATION = 2500;
    private static final int SKY_FADE_OUT_DURATION = 2500;
    // It takes two and a half seconds (2500 milliseconds) for
    // the current sky colours to fully fade in or out

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
     * while playing the pop-up sound effect
     * and placing a duration limit for cards to remain popped up.
     * After cards have popped up, ensures firstCardIsAboutToPopUp is false
     */
    public static void cardsPopUp(WhackAWordActivity aWhackAWordActivity)
    {
        if (AnimationManager.firstCardIsAboutToPopUp)
        {
            DisplayManager.displayFoodItemsOnCards(aWhackAWordActivity);
        }

        for (FoodItem foodItem : Collections.mapOfFoodItemsToTheirFoodCards.keySet())
        {
            AnimationManager.startPopUpAnimation(aWhackAWordActivity, foodItem);
        }

        TapManager.correctFoodCardWasJustTapped = false;

        SoundEffectsManager.playPopUpSoundEffect();

        AnimationManager.numberOfPopUpTimes++;

        Collections.mapOfPopUpTimesToWhetherACardHasBeenTappedOnTime.put(AnimationManager.numberOfPopUpTimes, false);
        // The value of the map (false) is changed to true if a card is tapped on time
        // (via the setClickListenerForFoodCard method in the TapManager class)

        AnimationManager.limitPopUpDuration(aWhackAWordActivity);
    }

    /**
     * Causes each card on display to hide
     * while playing the hide cards sound effect,
     * and clears their click listeners
     */
    public static void hideCards(WhackAWordActivity aWhackAWordActivity)
    {
        CountDownLatch countDownLatch = new CountDownLatch(Collections.mapOfFoodItemsToTheirFoodCards.size());

        for (FoodItem foodItem : Collections.mapOfFoodItemsToTheirFoodCards.keySet())
        {
            AnimationManager.startHideCardsAnimation(aWhackAWordActivity, foodItem, countDownLatch);
        }

        SoundEffectsManager.playHideCardsSoundEffect();

        Collections.availableFoodCards = new ArrayList<>(Collections.foodCards);

        TapManager.clearClickListeners();
    }

    /**
     * Causes the sky to change colour every six seconds
     * (as set out in background_sky_changing.xml
     * which is set as the background of the sky_view View element
     * in activity_whack_a_word.xml)
     */
    public static void animateSky(WhackAWordActivity aWhackAWordActivity)
    {
        View skyView = aWhackAWordActivity.findViewById(R.id.sky_view);
        AnimationDrawable animationDrawable = (AnimationDrawable) skyView.getBackground();

        animationDrawable.setEnterFadeDuration(SKY_FADE_IN_DURATION);
        animationDrawable.setExitFadeDuration(SKY_FADE_OUT_DURATION);

        animationDrawable.start();
    }

    /**
     * Helper method that limits the cards' pop-up duration to POP_UP_DURATION_LIMIT milliseconds
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

        }, POP_UP_DURATION_LIMIT);

    }

    /**
     * Helper method that starts the pop-up animation
     * after having set its duration to POP_UP_ANIMATION_DURATION milliseconds
     * and after a delay of POP_UP_ANIMATION_DELAY milliseconds.
     * Sets firstCardIsAboutToPopUp to false upon the end of the animation
     */
    private static void startPopUpAnimation(WhackAWordActivity aWhackAWordActivity, FoodItem foodItem)
    {
        float upwardsTranslation = AnimationManager.getUpwardsTranslation(aWhackAWordActivity);

        ObjectAnimator popUpAnimation = AnimationManager.createCardTranslation(aWhackAWordActivity, foodItem, upwardsTranslation, POP_UP_ANIMATION_DURATION, POP_UP_ANIMATION_DELAY);

        popUpAnimation.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                AnimationManager.firstCardIsAboutToPopUp = false;
            }
        });

        popUpAnimation.start();
    }

    /**
     * Helper method that starts the hide cards animation
     * after having set its duration to HIDE_CARDS_ANIMATION_DURATION milliseconds
     * and after a delay of HIDE_CARDS_ANIMATION_DELAY_WHEN_CORRECT_FOOD_CARD_IS_TAPPED milliseconds
     * upon the tap of a correct food card.
     * While the cards are hidden, displays food items on them,
     * using a CountDownLatch to ensure that this happens only after the last card has been hidden
     */
    private static void startHideCardsAnimation(WhackAWordActivity aWhackAWordActivity, FoodItem foodItem, CountDownLatch countDownLatch)
    {
        float amountTranslatedFromInitialPosition = 0;
        // 'Initial position' refers to the position of the card before runtime

        int startDelay = TapManager.correctFoodCardWasJustTapped ? HIDE_CARDS_ANIMATION_DELAY_WHEN_CORRECT_FOOD_CARD_IS_TAPPED : 0;

        ObjectAnimator hideCardsAnimation = AnimationManager.createCardTranslation(aWhackAWordActivity, foodItem, amountTranslatedFromInitialPosition, HIDE_CARDS_ANIMATION_DURATION, startDelay);

        hideCardsAnimation.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                countDownLatch.countDown();

                if (countDownLatch.getCount() == 0) // I.e. If there are no more cards to be hidden
                {
                    DisplayManager.displayFoodItemsOnCards(aWhackAWordActivity);
                }

            }
            // When the animations have ended, the cards have gone into their holes,
            // so the onAnimationEnd method ensures that
            // when the food cards change their food items,
            // this change happens out of sight of the user

        });

        hideCardsAnimation.start();
    }

    /**
     * Creates and returns an ObjectAnimator for vertically translating a food card.
     * The animation moves the card to its specified final position
     * relative to its initial position (that was defined before runtime).
     * Additional parameters control the duration of the animation
     * and the delay before it starts
     */
    private static ObjectAnimator createCardTranslation(WhackAWordActivity aWhackAWordActivity, FoodItem foodItem, float finalPositionRelativeToInitialPosition, int duration, int startDelay)
    {
        FoodCard foodCard = Collections.mapOfFoodItemsToTheirFoodCards.get(foodItem);
        FrameLayout foodCardFrameLayout = aWhackAWordActivity.findViewById(foodCard.getID());
        ObjectAnimator cardTranslation = ObjectAnimator.ofFloat(foodCardFrameLayout, "translationY", finalPositionRelativeToInitialPosition);

        cardTranslation.setDuration(duration);
        cardTranslation.setStartDelay(startDelay);

        return cardTranslation;
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
            amountTranslatedFromInitialPositionInPixels = POP_UP_TRANSLATION_FOR_SMALL_SCREENS;
        }
        else
        {
            amountTranslatedFromInitialPositionInPixels = POP_UP_TRANSLATION_FOR_LARGE_SCREENS;
        }
        // Determines the translation amount based on screen width,
        // since screens of width 1200dp and above have a different layout,
        // as shown in app/src/main/res/layout-w1200dp/activity_whack_a_word.xml

        amountTranslatedFromInitialPositionInDP = amountTranslatedFromInitialPositionInPixels * displayMetrics.density;
        // DP refers to density-independent pixels

        return amountTranslatedFromInitialPositionInDP;
    }

}
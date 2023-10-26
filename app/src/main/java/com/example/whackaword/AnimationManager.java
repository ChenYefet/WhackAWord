package com.example.whackaword;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * The AnimationManager class is responsible for managing animations
 * that enhance user experience and provide visual feedback
 * in the Whack-A-Word game
 */
public class AnimationManager extends DisplayManager
{
    /**
     * Causes each card set for display to pop up
     */
    public static void cardsPopUp(WhackAWordActivity aWhackAWordActivity, Map<FoodItem, FoodCard> mapOfFoodItemsToTheirFoodCards, boolean firstCardIsAboutToPopUp)
    {
        for (FoodItem foodItem : mapOfFoodItemsToTheirFoodCards.keySet())
        {
            FoodCard foodCard = mapOfFoodItemsToTheirFoodCards.get(foodItem);
            FrameLayout foodCardFrameLayout = aWhackAWordActivity.findViewById(foodCard.getID());
            ObjectAnimator animation = ObjectAnimator.ofFloat(foodCardFrameLayout, "translationY", AnimationManager.getUpwardsTranslation(aWhackAWordActivity));

            animation.setDuration(500); // The animation lasts for half a second (500 milliseconds)
            animation.setStartDelay(500); // Delays starting the animation after start() is called by half a second (500 milliseconds)

            if (firstCardIsAboutToPopUp)
            {
                animation.start();
            }
            else
            {
                new Handler().postDelayed(animation::start, 250);
            }
            // Delays calling for the start of the animation by a quarter of a second (250 milliseconds)
            // in order to synchronise better with the audio,
            // apart from when the first card is about to pop up,
            // since it synchronises better without a delay in that instance

        }

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
                    DisplayManager.displayFoodItemsOnCards(aWhackAWordActivity, Collections.mapOfFoodItemsToTheirFoodCards);
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
     * Displays an animated tick
     * if anAudioID corresponds to the tick sound
     */
    public static void displayAnimatedTickDuringTickAudio(WhackAWordActivity aWhackAWordActivity, int anAudioID)
    {
        if (anAudioID == R.raw.correct)
        {
            AnimationManager.displayAnimatedTick(aWhackAWordActivity);
        }
    }

    /**
     * Causes the card colour to change continuously
     * for as long as the card is up
     */
    public static void continuouslyChangeCardColour(Context aContext, FrameLayout aCard)
    {
        Drawable originalDrawable = aCard.getBackground();

        AnimationDrawable animationDrawable = new AnimationDrawable();

        if (ScreenProperties.smallScreen)
        {
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(aContext, R.drawable.first_colour_of_animation_of_correctly_tapped_card_for_small_screens)), 100);
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(aContext, R.drawable.second_colour_of_animation_of_correctly_tapped_card_for_small_screens)), 100);
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(aContext, R.drawable.third_colour_of_animation_of_correctly_tapped_card_for_small_screens)), 100);
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(aContext, R.drawable.fourth_colour_of_animation_of_correctly_tapped_card_for_small_screens)), 100);
        }
        else
        {
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(aContext, R.drawable.first_colour_of_animation_of_correctly_tapped_card)), 100);
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(aContext, R.drawable.second_colour_of_animation_of_correctly_tapped_card)), 100);
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(aContext, R.drawable.third_colour_of_animation_of_correctly_tapped_card)), 100);
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(aContext, R.drawable.fourth_colour_of_animation_of_correctly_tapped_card)), 100);
        }
        // The colour changes every tenth of a second (100 milliseconds)

        // After the final tap of the game, the cards remain up for longer
        // than after previous taps of the game, so ...

        if (LevelProperties.userWins())

        // ... if this is the final tap of the game ...

        {
            new Handler().postDelayed(() -> aCard.setBackground(originalDrawable), 2000);
            // ... delay setting the card's background back to normal
            // by two seconds (2000 milliseconds),
            // so that it would be animated for all the time that it is up ...

        }
        else
        {
            new Handler().postDelayed(() -> aCard.setBackground(originalDrawable), 1000);
            // ... otherwise delay setting the card's background back to normal
            // by only one second (1000 milliseconds),
            // so that not only would it be animated for all the time that it is up,
            // but the next card coming out of that hole
            // would not show an animated background
            // for any amount of time

        }

        aCard.setBackground(animationDrawable);

        animationDrawable.start();
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
     * Hides cards if the user has won and the last of the audio has played
     */
    public static void hideCardsIfUserHasWonAndAudioHasCompleted(WhackAWordActivity aWhackAWordActivity)
    {
        if (LevelProperties.userWins() && AudioManager.audioQueue.isEmpty())
        {
            AnimationManager.hideCards(aWhackAWordActivity);
        }
    }

    /**
     * Helper method that displays and animates a tick
     * by scaling, rotating, and translating it
     */
    private static void displayAnimatedTick(WhackAWordActivity aWhackAWordActivity)
    {
        ImageView tick = aWhackAWordActivity.findViewById(R.id.tick);
        tick.setVisibility(View.VISIBLE);

        tick.setRotation(0);
        // Sets the tick's rotational position to 0 degrees
        // because the ObjectAnimator doesn't reset the property to its original state
        // for subsequent animations.
        // Without this, the tick is only animated on its first appearance,
        // and just appears and disappears without an animation on subsequent appearances

        tick.setTranslationY(0);
        // Sets the tick's vertical position to its initial vertical position
        // because the ObjectAnimator doesn't reset the property to its original state
        // for subsequent animations.
        // Without this, the tick is only animated on its first appearance,
        // and just appears and disappears without an animation on subsequent appearances

        tick.setScaleX(0);
        // Sets the tick's horizontal size to 0

        tick.setScaleY(0);
        // Sets the tick's vertical size to 0

        // Note that the tick's horizontal and vertical sizes are 0 after being animated
        // due to the horizontalShrinkage and verticalShrinkage animations,
        // so there is no need to reset its horizontal and vertical sizes to 0
        // after the tick has been animated.
        // Nevertheless, it is necessary
        // to set the horizontal and vertical sizes of the tick to 0
        // before the first call of this method
        // so that the tick is horizontally and vertically stretched
        // upon the first time this method is called

        ObjectAnimator horizontalStretch = ObjectAnimator.ofFloat(tick, "scaleX", 1);
        ObjectAnimator verticalStretch = ObjectAnimator.ofFloat(tick, "scaleY", 1);
        ObjectAnimator horizontalShrinkage = ObjectAnimator.ofFloat(tick, "scaleX", 0);
        ObjectAnimator verticalShrinkage = ObjectAnimator.ofFloat(tick, "scaleY", 0);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(tick, "rotation", 3600);
        ObjectAnimator translation = ObjectAnimator.ofFloat(tick, "translationY", 1000);

        AnimatorSet firstTwoAnimations = new AnimatorSet();
        AnimatorSet lastFourAnimations = new AnimatorSet();
        AnimatorSet wholeAnimation = new AnimatorSet();

        firstTwoAnimations.playTogether(horizontalStretch, verticalStretch);
        firstTwoAnimations.setDuration(500);
        lastFourAnimations.playTogether(rotation, translation, horizontalShrinkage, verticalShrinkage);
        lastFourAnimations.setDuration(1000); // The animations last for one second (1000 milliseconds)
        lastFourAnimations.setStartDelay(500);

        wholeAnimation.playSequentially(firstTwoAnimations, lastFourAnimations);
        wholeAnimation.start();
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

        if (ScreenProperties.smallScreen)
        {
            amountTranslatedFromInitialPositionInPixels = -178; // For small screens
        }
        else
        {
            amountTranslatedFromInitialPositionInPixels = -278; // For large screens
        }
        // Determines the translation amount based on screen width,
        // since screens of width 1000dp and above have a different layout,
        // as shown in app/src/main/res/layout-w1000dp/activity_whack_a_word.xml

        amountTranslatedFromInitialPositionInDP = amountTranslatedFromInitialPositionInPixels * displayMetrics.density;
        // DP refers to density-independent pixels

        return amountTranslatedFromInitialPositionInDP;
    }

}
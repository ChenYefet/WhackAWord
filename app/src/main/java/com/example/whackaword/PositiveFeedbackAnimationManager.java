package com.example.whackaword;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import java.util.Objects;

/**
 * The PositiveFeedbackAnimationManager class is responsible for
 * managing animations that provide positive feedback
 * in the Whack-A-Word game
 *
 * It contains two constant class variables:
 * one for the duration of the card colours,
 * and one for the degrees in a rotation (360),
 * used for the rotation of the tick
 */
public class PositiveFeedbackAnimationManager extends AnimationManager
{
    private static final int CARD_COLOUR_DURATION = 100;
    // The card colour changes every tenth of a second (100 milliseconds)

    public static final int DEGREES_IN_A_ROTATION = 360;

    /**
     * Conveys positive feedback by
     * playing a tick sound,
     * displaying an animated tick,
     * and continuously changing the colour of aFoodCard
     */
    public static void conveyPositiveFeedback(WhackAWordActivity aWhackAWordActivity, FoodCard aFoodCard)
    {
        AudioManager.playAudioSequentially(aWhackAWordActivity, R.raw.correct);
        PositiveFeedbackAnimationManager.displayAnimatedTick(aWhackAWordActivity);
        PositiveFeedbackAnimationManager.continuouslyChangeCardColour(aWhackAWordActivity, aFoodCard);
    }

    /**
     * Causes the card colour to change continuously
     * for as long as the card is up
     */
    private static void continuouslyChangeCardColour(WhackAWordActivity aWhackAWordActivity, FoodCard aFoodCard)
    {
        FrameLayout foodCardFrameLayout = aWhackAWordActivity.findViewById(aFoodCard.getID());
        Drawable originalDrawable = foodCardFrameLayout.getBackground();
        AnimationDrawable animationDrawable = new AnimationDrawable();

        animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(aWhackAWordActivity, R.drawable.first_colour_of_animation_of_correctly_tapped_card)), CARD_COLOUR_DURATION);
        animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(aWhackAWordActivity, R.drawable.second_colour_of_animation_of_correctly_tapped_card)), CARD_COLOUR_DURATION);
        animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(aWhackAWordActivity, R.drawable.third_colour_of_animation_of_correctly_tapped_card)), CARD_COLOUR_DURATION);
        animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(aWhackAWordActivity, R.drawable.fourth_colour_of_animation_of_correctly_tapped_card)), CARD_COLOUR_DURATION);

        int delayForNormalBackground = 1000;
        int delayForNormalBackgroundAfterFinalTap = 4500;

        // After the final tap of the game, the cards remain up for longer
        // than after previous taps of the game, so ...

        if (LevelProperties.userWins())

        // ... if this is the final tap of the game ...

        {
            new Handler().postDelayed(() -> foodCardFrameLayout.setBackground(originalDrawable), delayForNormalBackgroundAfterFinalTap);
            // ... delay setting the card's background back to normal
            // by four and a half seconds (4500 milliseconds),
            // so that it would be animated for all the time that it is up ...

        }
        else
        {
            new Handler().postDelayed(() -> foodCardFrameLayout.setBackground(originalDrawable), delayForNormalBackground);
            // ... otherwise delay setting the card's background back to normal
            // by only one second (1000 milliseconds),
            // so that not only would it be animated for all the time that it is up,
            // but the next card coming out of that hole
            // would not show an animated background
            // for any amount of time

        }

        foodCardFrameLayout.setBackground(animationDrawable);

        animationDrawable.start();
    }

    /**
     * Displays and animates a tick
     * by scaling, rotating, and translating it
     */
    private static void displayAnimatedTick(WhackAWordActivity aWhackAWordActivity)
    {
        ImageView tick = aWhackAWordActivity.findViewById(R.id.tick);
        int originalSize = 1;
        int numberOfRotations = 10;
        int translationDistanceInPixels = 1000;

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

        ObjectAnimator horizontalStretch = ObjectAnimator.ofFloat(tick, "scaleX", originalSize);
        ObjectAnimator verticalStretch = ObjectAnimator.ofFloat(tick, "scaleY", originalSize);
        ObjectAnimator horizontalShrinkage = ObjectAnimator.ofFloat(tick, "scaleX", 0);
        ObjectAnimator verticalShrinkage = ObjectAnimator.ofFloat(tick, "scaleY", 0);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(tick, "rotation", DEGREES_IN_A_ROTATION * numberOfRotations);
        ObjectAnimator translation = ObjectAnimator.ofFloat(tick, "translationY", translationDistanceInPixels);

        AnimatorSet firstTwoAnimations = new AnimatorSet();
        AnimatorSet lastFourAnimations = new AnimatorSet();
        AnimatorSet wholeAnimation = new AnimatorSet();

        int durationOfFirstTwoAnimations = 500;
        // The first two animations last for half a second (500 milliseconds)

        int durationOfLastFourAnimations = 1000;
        // The last four animations last for one second (1000 milliseconds)

        int durationOfEnlargedTick = 500;
        // An enlarged tick is shown for half a second (500 milliseconds)
        // at the conclusion of the first two animations,
        // right before the last four animations start playing,
        // due to the delay of this duration
        // that is set on the last four animations
        // via the setStartDelay method

        firstTwoAnimations.playTogether(horizontalStretch, verticalStretch);
        firstTwoAnimations.setDuration(durationOfFirstTwoAnimations);
        lastFourAnimations.playTogether(rotation, translation, horizontalShrinkage, verticalShrinkage);
        lastFourAnimations.setDuration(durationOfLastFourAnimations);
        lastFourAnimations.setStartDelay(durationOfEnlargedTick);

        wholeAnimation.playSequentially(firstTwoAnimations, lastFourAnimations);
        wholeAnimation.start();
    }

}

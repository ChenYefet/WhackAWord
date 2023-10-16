package com.example.whackaword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * An object of the WhackAWordActivity class represents a game called Whack-A-Word
 * with the following description:
 *
 * A vocabulary card with a flashcard image of a food item
 * pops up from one of a series of holes in the ground.
 * The user hears the name of the food item then must tap it,
 * after which they receive some positive feedback – a tick with a sound effect.
 * If they don't tap it,
 * it retreats into its hole and pops up again in a random one.
 * After each round of three successful taps,
 * the number of cards that pop up simultaneously
 * increases by one.
 * Each card that pops up must have a different image
 * to the other cards that pop up at the same time
 * so that the user always chooses between different food items,
 * and the user should always be tasked to tap a different food card
 * to all previous food cards that they have correctly tapped
 * during the game
 * so that the game is more challenging and fun.
 * At the end of the third round, the user wins
 *
 * @author Chen Yefet
 */
public class WhackAWordActivity extends AppCompatActivity
{
    private static final int LAST_LEVEL = 3;
    private static final int REQUIRED_NUMBER_OF_SUCCESSFUL_TAPS_PER_LEVEL = 3;

    private static final int NUMBER_OF_CARDS_TO_DISPLAY_FOR_LEVEL_1 = 1;
    private static final int NUMBER_OF_CARDS_TO_DISPLAY_FOR_LEVEL_2 = 2;
    private static final int NUMBER_OF_CARDS_TO_DISPLAY_FOR_LEVEL_3 = 3;
    // The above three constants exist despite them equating to their corresponding level
    // since this may change in the future,
    // e.g. it is easier to update the code if we decide that level 3 should display four cards
    // instead of three,
    // or that a future level 4 should display five cards instead of four,
    // so it is best for the numberOfCardsToDisplay instance variable
    // to be independent of the current level
    // and be assigned one of these constant class variables instead

    private static Set<FoodItem> foodItems;
    // A set of all the food items that exist within the game

    private static Set<FoodCard> foodCards;
    // A set of all the food cards that exist within the game, one for each of the existing holes

    private static boolean firstCardIsAboutToPopUp;

    private List<FoodItem> availableFoodItems;
    // A list of food items that are currently available to be played (it varies).
    // When a food item is set for display on a food card,
    // that food item becomes unavailable to other food cards
    // in order to avoid multiple cards bearing the same food item.
    // Since retrieving a random element of this collection requires indexing,
    // it is a list instead of a set

    private List<FoodCard> availableFoodCards;
    // A list of food cards that are currently available to be played (it varies).
    // When a food card is set to display a food item,
    // that food card becomes unavailable to other food items
    // in order to avoid multiple food items being set to display on the same food card.
    // Since retrieving a random element of this collection requires indexing,
    // it is a list instead of a set

    // The availableFoodCards list is filled within the hideCards method
    // (in addition to the onCreate method)
    // because food cards become available when cards are hidden.
    // The availableFoodItems list is not filled within the hideCards method
    // since those unavailable food items that are not on the list
    // may need to be displayed again,
    // e.g. when an incorrect card has been tapped;
    // it is filled after a correct card has been clicked instead
    // i.e. right before playing Whack-A-Word again

    private Map<FoodItem, FoodCard> mapOfFoodItemsSelectedForDisplayToTheirFoodCards;
    // A map of food items that are set for display
    // to food cards upon which they are set to be displayed.
    // Note that there are currently no situations
    // in which two food cards contain the same food item.
    // If this were not the case,
    // i.e. if two different food cards could contain the same food item,
    // a map of food items to a list of food cards
    // rather than to a single food card
    // would be more appropriate

    private Set<FoodItem> correctlyTappedFoodItems;
    // A set of food items that have previously been correctly tapped.
    // A user should only need to tap
    // a food item that hasn't yet been correctly tapped

    private Set<FrameLayout> frameLayoutsWithClickListeners;
    // Each frame layout is effectively a card with an image on it.
    // This set is necessary to clear all click listeners at once
    // with the clearClickListeners() method

    private int currentLevel;
    private int numberOfCardsToDisplay;
    private int countOfSuccessfulTaps;

    private FoodItem correctFoodItem;
    // The food item that needs to be tapped

    private Queue<Integer> audioQueue;
    // A queue used to store audio IDs for playback in a sequential manner (rather than concurrent)

    private MediaPlayer mediaPlayer;
    // A media player that maintains a reference to the currently playing MediaPlayer.
    // This is necessary to control the playback of audio
    // across different method calls

    /**
     * This is the method that gets called when the activity is created.
     * It sets up the initial state of the activity,
     * including its layout and variables,
     * animates the sky,
     * and calls the whackAWord method that starts the game.
     *
     * savedInstanceState either contains the activity's previously saved state
     * or is null if the activity has never existed before.
     * It could include the contents of user interface widgets,
     * the values of instance variables,
     * or any other relevant data that the user would need
     * the next time the activity is created,
     * for example after a user rotates the device from portrait to landscape mode
     * (in which case the Activity object would be destroyed and recreated),
     * although the setRequestedOrientation method ensures that this does not happen
     * and that the game is always played in landscape mode
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Passes savedInstanceState to AppCompatActivity.
        // This is called before the rest of the onCreate() method
        // to ensure proper setup and initialization of the activity,
        // e.g. toolbar, theme, backward compatibility via the AndroidX library

        this.setContentView(R.layout.activity_whack_a_word);
        // Sets up the layout of the activity

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Sets the screen orientation to landscape mode
        // and causes it to remain so regardless of the device's physical orientation

        WhackAWordActivity.fillFoodItemsSet();
        WhackAWordActivity.fillFoodCardsSet();

        WhackAWordActivity.firstCardIsAboutToPopUp = true;

        this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards = new HashMap<>();
        this.correctlyTappedFoodItems = new HashSet<>();
        this.frameLayoutsWithClickListeners = new HashSet<>();
        this.audioQueue = new LinkedList<>();

        this.availableFoodItems = new ArrayList<>(WhackAWordActivity.foodItems);
        // Fills availableFoodItems with all the food items in foodItems

        this.availableFoodCards = new ArrayList<>(WhackAWordActivity.foodCards);
        // Fills availableFoodCards with all the food cards in foodCards

        this.currentLevel = 1;
        this.countOfSuccessfulTaps = 0;
        this.numberOfCardsToDisplay = NUMBER_OF_CARDS_TO_DISPLAY_FOR_LEVEL_1;

        this.animateSky();
        this.whackAWord();
    }

    /**
     * Plays Whack-A-Word:
     *
     * Causes food cards to pop up,
     * plays the correct audio,
     * and sets click listeners for correct and incorrect food cards
     */
    private void whackAWord()
    {
        this.selectFoodCardsToBeDisplayed(this.numberOfCardsToDisplay, true);

        if (WhackAWordActivity.firstCardIsAboutToPopUp)
        {
            this.displayFoodItemsOnCards();
        }

        this.cardsPopUp();
        WhackAWordActivity.firstCardIsAboutToPopUp = false;

        Set<FoodItem> foodItemsOnDisplay = this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.keySet();
        // The food items would be 'on' display (and not just set 'for' display)
        // by the end of the animations that were started in the cardsPopUp method

        for (FoodItem foodItemOnDisplay : foodItemsOnDisplay)
        {

            if (!this.correctlyTappedFoodItems.contains(foodItemOnDisplay))
            {
                this.correctFoodItem = foodItemOnDisplay;
                break;
            }

        }
        // Sets the correct food item to one of the food items on display
        // that hasn't yet been correctly tapped

        int correctFoodItemAudioID = this.correctFoodItem.getAudioID();
        FoodCard correctFoodCard = this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.get(this.correctFoodItem);

        this.playAudio(correctFoodItemAudioID);

        this.setOnClickListenerForFoodCard(correctFoodCard, true);

        if (this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.size() > 1)

        // I.e. if there are any incorrect food items that are set for display
        // in addition to the one correct food item ...

        {

            for (FoodItem foodItem : this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.keySet())
            {

                if (foodItem != this.correctFoodItem)
                {
                    FoodCard incorrectFoodCard = this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.get(foodItem);

                    this.setOnClickListenerForFoodCard(incorrectFoodCard, false);
                    // ... set a click listener for each of their corresponding food cards

                }

            }

        }

    }

    /**
     * When the correct food card is tapped,
     * plays the tick sound,
     * displays an animated tick,
     * continuously changes the card colour for the correct food card,
     * and checks whether the user has reached the next level or has won.
     * If the user hasn't won, continues playing the game
     *
     * Hides cards when an incorrect food card is tapped,
     * then displays random cards again with the same food items,
     * setting click listeners for the correct and incorrect food cards
     */
    private void setOnClickListenerForFoodCard(FoodCard foodCard, boolean isCorrectFoodCard)
    {
        FrameLayout frameLayout = this.findViewById(foodCard.getID());
        frameLayout.setOnClickListener(v ->
        {
            if (isCorrectFoodCard)
            {
                this.countOfSuccessfulTaps++;
                this.correctlyTappedFoodItems.add(foodCard.getFoodItem());

                this.continuouslyChangeCardColour(this.findViewById(foodCard.getID()));
                this.playAudio(R.raw.correct);
                this.manageLevel();

                if (this.countOfSuccessfulTaps == REQUIRED_NUMBER_OF_SUCCESSFUL_TAPS_PER_LEVEL && this.currentLevel == LAST_LEVEL)
                {
                    // You win, well done!
                }
                else
                {
                    this.continuePlaying();
                }

            }
            else
            {
                this.hideCards();

                this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.replaceAll(((foodItem, hiddenFoodCard) -> null));
                // Keeps all foodItem keys in the map while setting all their hiddenFoodCard values to null
                // since those food items need to be displayed again
                // in food cards which are not yet determined

                this.selectFoodCardsToBeDisplayed(this.numberOfCardsToDisplay, false);
                this.cardsPopUp();

                FoodCard correctFoodCard = this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.get(this.correctFoodItem);

                this.setOnClickListenerForFoodCard(correctFoodCard, true);

                for (FoodItem foodItem : this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.keySet())
                {

                    if (foodItem != this.correctFoodItem)
                    {
                        FoodCard incorrectFoodCard = this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.get(foodItem);

                        this.setOnClickListenerForFoodCard(incorrectFoodCard, false);
                    }

                }

            }

        });

        this.frameLayoutsWithClickListeners.add(frameLayout);
    }

    /**
     * If new food items are to be displayed,
     * selects a number of food cards to be displayed
     * equal to numberOfCards,
     * and ensures that the food items to be displayed
     * are a different combination to the ones that were already displayed.
     * Otherwise, selects the same food items that were most recently displayed
     * to be displayed again on random food cards
     */
    private void selectFoodCardsToBeDisplayed(int numberOfCards, boolean newFoodItemsAreToBeDisplayed)
    {
        if (newFoodItemsAreToBeDisplayed)
        {
            this.selectNewFoodCardsToBeDisplayed(numberOfCards);
        }
        else
        {
            this.selectSameFoodCardsToBeDisplayed();
        }
    }

    /**
     * Helper method that selects
     * a number of food cards equal to numberOfCards
     * to be displayed,
     * and assigns each one an appropriate food item
     */
    private void selectNewFoodCardsToBeDisplayed(int numberOfCards)
    {
        for (int cardCount = 1; cardCount <= numberOfCards; cardCount++)
        {
            FoodItem foodItemToBeDisplayed = this.selectAppropriateFoodItemToBeDisplayed(cardCount, numberOfCards);
            FoodCard foodCardToBeDisplayed = this.getAvailableFoodCard();

            foodCardToBeDisplayed.setFoodItem(foodItemToBeDisplayed);

            this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.put(foodItemToBeDisplayed, foodCardToBeDisplayed);
            this.availableFoodItems.remove(foodItemToBeDisplayed);
            this.availableFoodCards.remove(foodCardToBeDisplayed);
        }
    }

    /**
     * Helper method that selects
     * the same food items that were most recently displayed on food cards
     * to be displayed again on random food cards
     */
    private void selectSameFoodCardsToBeDisplayed()
    {
        for (FoodItem foodItemToBeDisplayed : this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.keySet())

        // Note that food items that are set to be displayed again
        // would have been kept in mapOfFoodItemsSelectedForDisplayToTheirFoodCards.
        // Their previous corresponding food cards
        // would have been removed from the map (i.e. set to null)
        // via the setOnClickListenerForFoodCard method
        // so that they could be replaced by random food cards

        {
            FoodCard foodCardToBeDisplayed = this.getAvailableFoodCard();
            foodCardToBeDisplayed.setFoodItem(foodItemToBeDisplayed);

            this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.put(foodItemToBeDisplayed, foodCardToBeDisplayed);
            this.availableFoodCards.remove(foodCardToBeDisplayed);
        }

    }

    /**
     * Plays the audio with ID audioID
     * after all other audio in front of it in the audio queue
     * have been played
     */
    private void playAudio(int audioID)
    {
        this.audioQueue.add(audioID);
        this.playAudioInSequence();
    }

    /**
     * Plays in sequence all the audio files whose IDs are in the audio queue:
     *
     * Plays the audio file whose ID is at the front of the queue
     * and sets a completion listener to ensure that,
     * upon completion of the audio,
     * the method recursively calls itself
     * to both play the next audio file in the queue and call itself recursively,
     * ensuring that all audio files are played in sequence
     *
     * Displays an animated tick if the tick sound is being played
     *
     * Hides all cards upon audio completion if the user has won
     *
     * If there is audio currently playing, does nothing.
     * This does not cause a problem of skipped audio files
     * if those audio files are in the audio queue
     * and the currently playing audio was playing due to the calling of this method,
     * since this method would be set to be called again recursively
     * upon completion of the audio
     */
    private void playAudioInSequence()
    {
        if (this.mediaPlayer != null && this.mediaPlayer.isPlaying())
        {
            return;
        }

        if (!this.audioQueue.isEmpty())
        {
            int audioID = this.audioQueue.poll();
            this.mediaPlayer = MediaPlayer.create(this, audioID);
            this.mediaPlayer.setOnCompletionListener(mp ->
            {
                this.playAudioInSequence();
                this.checkForAudioCompletionAfterWin();
            });

            this.mediaPlayer.start();

            if (audioID == R.raw.correct)
            {
                this.displayAnimatedTick();
            }

        }

    }

    /**
     * Causes each card set for display to pop up
     */
    private void cardsPopUp()
    {
        for (FoodItem foodItem : this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.keySet())
        {
            FoodCard foodCard = this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.get(foodItem);
            FrameLayout frameLayout = this.findViewById(foodCard.getID());
            ObjectAnimator animation = ObjectAnimator.ofFloat(frameLayout, "translationY", this.getUpwardsTranslation());

            animation.setDuration(500); // The animation lasts for half a second (500 milliseconds)
            animation.setStartDelay(500); // Delays starting the animation after start() is called by half a second (500 milliseconds)

            if (WhackAWordActivity.firstCardIsAboutToPopUp)
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
    private void hideCards()
    {
        float amountTranslatedFromInitialPosition = 0;
        // 'Initial position' refers to the position of the card before runtime

        for (FoodItem foodItem : this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.keySet())
        {
            FoodCard foodCard = this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.get(foodItem);
            FrameLayout frameLayout = this.findViewById(foodCard.getID());
            ObjectAnimator animation = ObjectAnimator.ofFloat(frameLayout, "translationY", amountTranslatedFromInitialPosition);

            animation.setDuration(500); // The animation lasts for half a second (500 milliseconds)

            animation.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    WhackAWordActivity.this.displayFoodItemsOnCards();
                }
                // When the animation has ended, the cards have gone into their holes,
                // so this method ensures that when the food cards change their food items,
                // this change happens out of sight of the user

            });

            animation.start();

        }

        this.availableFoodCards = new ArrayList<>(WhackAWordActivity.foodCards);

        this.clearClickListeners();
    }

    /**
     * Clears click listeners from each card
     */
    private void clearClickListeners()
    {
        for (FrameLayout frameLayout: this.frameLayoutsWithClickListeners)
        {
            frameLayout.setOnClickListener(null);
        }

        this.frameLayoutsWithClickListeners = new HashSet<>();
    }

    /**
     * Displays a tick and animates it
     * by rotating, translating and scaling it
     */
    private void displayAnimatedTick()
    {
        ImageView tick = this.findViewById(R.id.tick);
        tick.setVisibility(View.VISIBLE);

        tick.setRotation(0);
        // Resets the tick's rotational position to 0 degrees
        // because the ObjectAnimator doesn't reset the property to its original state
        // for subsequent animations.
        // Without this, the tick is only animated on its first appearance,
        // and just appears and disappears without an animation on subsequent appearances

        tick.setTranslationY(0);
        // Resets the tick's vertical position to its initial vertical position
        // because the ObjectAnimator doesn't reset the property to its original state
        // for subsequent animations.
        // Without this, the tick is only animated on its first appearance,
        // and just appears and disappears without an animation on subsequent appearances

        tick.setScaleX(1);
        // Resets the tick's horizontal size to its initial horizontal size
        // because the ObjectAnimator doesn't reset the property to its original state
        // for subsequent animations.
        // Without this, the tick is only animated on its first appearance,
        // and just appears and disappears without an animation on subsequent appearances

        tick.setScaleY(1);
        // Resets tick's vertical size to its initial vertical size
        // because the ObjectAnimator doesn't reset the property to its original state
        // for subsequent animations.
        // Without this, the tick is only animated on its first appearance,
        // and just appears and disappears without an animation on subsequent appearances

        ObjectAnimator rotation = ObjectAnimator.ofFloat(tick, "rotation", 3600);
        ObjectAnimator translation = ObjectAnimator.ofFloat(tick, "translationY", 1000);
        ObjectAnimator horizontalScaling = ObjectAnimator.ofFloat(tick, "scaleX", 0);
        ObjectAnimator verticalScaling = ObjectAnimator.ofFloat(tick, "scaleY", 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotation, translation, horizontalScaling, verticalScaling);
        animatorSet.setDuration(1000); // The animations last for one second (1000 milliseconds)

        animatorSet.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                tick.setVisibility(View.INVISIBLE);
            }
        });

        new Handler().postDelayed(animatorSet::start, 50);
        // The animations are delayed for a twentieth of a second (50 milliseconds)
        // so that the tick is discernible beforehand

    }

    /**
     * Helper method that returns the amount
     * in density-independent pixels (dp)
     * that a card needs to translate upwards from its initial (hidden) position
     * in order for it to pop up
     */
    private float getUpwardsTranslation()
    {
        float amountTranslatedFromInitialPositionInPixels, amountTranslatedFromInitialPositionInDP;

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        // Gets the display metrics (such as screen density, width and height) of the Android device

        if (smallScreen())
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

    /**
     * Helper method that returns true if the screen width is less than 1000dp
     */
    private boolean smallScreen()
    {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        // Gets the display metrics (such as screen density, width and height) of the Android device

        float screenWidthInDP = displayMetrics.widthPixels / displayMetrics.density;
        // DP refers to density-independent pixels

        return screenWidthInDP < 1000;
    }

    /**
     * Helper method that causes food cards to display food items
     */
    private void displayFoodItemsOnCards()
    {
        for (FoodItem foodItem : this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.keySet())
        {
            FoodCard foodCard = this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.get(foodItem);
            ImageView foodCardImageView = this.findViewById(foodCard.getImageViewID());
            foodCardImageView.setImageResource(foodItem.getImageID());
        }
    }

    /**
     * Helper method that returns a food item that is appropriate to be displayed,
     * i.e. a food item that allows for all of the food cards
     * (an amount equal to numberOfCardsToBeDisplayed)
     * that are set for display
     * to display different food items,
     * including at least one that hasn't yet been correctly tapped
     */
    private FoodItem selectAppropriateFoodItemToBeDisplayed(int cardCount, int numberOfCardsToBeDisplayed)
    {
        FoodItem appropriateFoodItem = this.getAvailableFoodItem();

        if (cardCount == numberOfCardsToBeDisplayed)

        // I.e. if the current count of cards has reached the number of cards to be displayed ...

        {
            Set<FoodItem> foodItemsSelectedForDisplay = this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards.keySet();
            Set<FoodItem> foodItemsSelectedForDisplayThatHaveNotYetBeenCorrectlyTapped = new HashSet<>(foodItemsSelectedForDisplay);
            foodItemsSelectedForDisplayThatHaveNotYetBeenCorrectlyTapped.removeAll(this.correctlyTappedFoodItems);

            if (foodItemsSelectedForDisplayThatHaveNotYetBeenCorrectlyTapped.size() == 0)

            // ... and all the food items so far selected to be displayed
            // have previously been correctly tapped ...

            {

                while (this.correctlyTappedFoodItems.contains(appropriateFoodItem))
                {
                    appropriateFoodItem = this.getAvailableFoodItem();
                    // ... ensure that the last food item selected to be displayed
                    // is one which hasn't previously been correctly tapped
                    // (to ensure that the game is more challenging and fun)

                }

            }

            // Note that calling keySet() on an empty map outputs an empty set,
            // so no exception would be thrown since the map has been initialised
            // as an empty map

        }

        return appropriateFoodItem;

    }

    /**
     * Helper method that returns an available food card,
     * selected at random from a list of available food cards
     */
    private FoodCard getAvailableFoodCard()
    {
        Random random = new Random();
        int randomIndex = random.nextInt(this.availableFoodCards.size());
        return this.availableFoodCards.get(randomIndex);
    }

    /**
     * Helper method that returns an available food item,
     * selected at random from a list of available food items
     */
    private FoodItem getAvailableFoodItem()
    {
        Random random = new Random();
        int randomIndex = random.nextInt(this.availableFoodItems.size());
        return this.availableFoodItems.get(randomIndex);
    }

    /**
     * Helper method that hides cards and plays Whack-A-Word again
     * if the user hasn't won
     */
    private void continuePlaying()
    {
        this.hideCards();
        this.availableFoodItems = new ArrayList<>(WhackAWordActivity.foodItems);
        this.mapOfFoodItemsSelectedForDisplayToTheirFoodCards = new HashMap<>();
        this.whackAWord();
    }

    /**
     * Helper method that increments the current level,
     * resets the count of successful taps,
     * and determines the number of cards to display
     * upon reaching the next level
     */
    public void manageLevel()
    {
        if (this.countOfSuccessfulTaps == REQUIRED_NUMBER_OF_SUCCESSFUL_TAPS_PER_LEVEL &&
                this.currentLevel < LAST_LEVEL)

        // I.e. if the user has reached the next level

        {
            this.currentLevel++;
            this.countOfSuccessfulTaps = 0;

            if (this.currentLevel == 2)
            {
                this.numberOfCardsToDisplay = NUMBER_OF_CARDS_TO_DISPLAY_FOR_LEVEL_2;
            }
            else // There are currently only three levels
            {
                this.numberOfCardsToDisplay = NUMBER_OF_CARDS_TO_DISPLAY_FOR_LEVEL_3;
            }

        }

    }

    /**
     * Helper method that hides cards
     * if the last of the audio has played and the user has won
     */
    private void checkForAudioCompletionAfterWin()
    {
        if (this.audioQueue.isEmpty() &&
                this.countOfSuccessfulTaps == REQUIRED_NUMBER_OF_SUCCESSFUL_TAPS_PER_LEVEL &&
                this.currentLevel == LAST_LEVEL)
        {
            this.hideCards();
        }
    }

    /**
     * Causes the card colour to change continuously
     * for as long as the card is up
     */
    private void continuouslyChangeCardColour(FrameLayout aCard)
    {
        Drawable originalDrawable = aCard.getBackground();

        AnimationDrawable animationDrawable = new AnimationDrawable();

        if (smallScreen())
        {
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.first_colour_of_animation_of_correctly_tapped_card_for_small_screens)), 100);
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.second_colour_of_animation_of_correctly_tapped_card_for_small_screens)), 100);
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.third_colour_of_animation_of_correctly_tapped_card_for_small_screens)), 100);
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.fourth_colour_of_animation_of_correctly_tapped_card_for_small_screens)), 100);
        }
        else
        {
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.first_colour_of_animation_of_correctly_tapped_card)), 100);
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.second_colour_of_animation_of_correctly_tapped_card)), 100);
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.third_colour_of_animation_of_correctly_tapped_card)), 100);
            animationDrawable.addFrame(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.fourth_colour_of_animation_of_correctly_tapped_card)), 100);
        }
        // The colour changes every tenth of a second (100 milliseconds)

        // After the final tap of the game, the cards remain up for longer
        // than after previous taps of the game, so ...

        if (this.countOfSuccessfulTaps == REQUIRED_NUMBER_OF_SUCCESSFUL_TAPS_PER_LEVEL &&
                this.currentLevel == LAST_LEVEL)

        // ... if this is the final tap of the game ...

        {
            new Handler().postDelayed(() -> aCard.setBackground(originalDrawable), 2000);
            // ... delay setting the card's background back to normal
            // by two seconds (2000 milliseconds),
            // so that it would be animated for all the time that it is up ...

        }
        else
        {
            new Handler().postDelayed(() -> aCard.setBackground(originalDrawable), 500);
            // ... otherwise delay setting the card's background back to normal
            // by only half a second (500 milliseconds),
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
    private void animateSky()
    {
        View skyView = this.findViewById(R.id.sky_view);
        AnimationDrawable animationDrawable = (AnimationDrawable) skyView.getBackground();

        animationDrawable.setEnterFadeDuration(2500);
        // It takes two and a half seconds (2500 milliseconds) for the current sky colours to fully fade in

        animationDrawable.setExitFadeDuration(2500);
        // It takes two and a half seconds (2500 milliseconds) for the current sky colours to fully fade out

        animationDrawable.start();
    }

    /**
     * Fills foodCards with all the food cards that exist within the game
     */
    private static void fillFoodCardsSet()
    {
        FoodCard foodCard1 = new FoodCard(R.id.card1, R.id.variable_food_item_for_card_1);
        FoodCard foodCard2 = new FoodCard(R.id.card2, R.id.variable_food_item_for_card_2);
        FoodCard foodCard3 = new FoodCard(R.id.card3, R.id.variable_food_item_for_card_3);
        FoodCard foodCard4 = new FoodCard(R.id.card4, R.id.variable_food_item_for_card_4);
        FoodCard foodCard5 = new FoodCard(R.id.card5, R.id.variable_food_item_for_card_5);

        WhackAWordActivity.foodCards = new HashSet<>();
        WhackAWordActivity.foodCards.add(foodCard1);
        WhackAWordActivity.foodCards.add(foodCard2);
        WhackAWordActivity.foodCards.add(foodCard3);
        WhackAWordActivity.foodCards.add(foodCard4);
        WhackAWordActivity.foodCards.add(foodCard5);
    }

    /**
     * Fills foodItems with all the food items that exist within the game
     */
    private static void fillFoodItemsSet()
    {
        FoodItem apple = new FoodItem("Apple", R.raw.fc_apple, R.drawable.fc_apple);
        FoodItem banana = new FoodItem("Banana", R.raw.fc_banana, R.drawable.fc_banana);
        FoodItem bread = new FoodItem("Bread", R.raw.fc_bread, R.drawable.fc_bread);
        FoodItem cake = new FoodItem("Cake", R.raw.fc_cake, R.drawable.fc_cake);
        FoodItem carrot = new FoodItem("Carrot", R.raw.fc_carrot, R.drawable.fc_carrot);
        FoodItem egg = new FoodItem("Egg", R.raw.fc_egg, R.drawable.fc_egg);
        FoodItem orange = new FoodItem("Orange", R.raw.fc_orange, R.drawable.fc_orange);
        FoodItem potato = new FoodItem("Potato", R.raw.fc_potato, R.drawable.fc_potato);
        FoodItem tomato = new FoodItem("Tomato", R.raw.fc_tomato, R.drawable.fc_tomato);

        WhackAWordActivity.foodItems = new HashSet<>();
        WhackAWordActivity.foodItems.add(apple);
        WhackAWordActivity.foodItems.add(banana);
        WhackAWordActivity.foodItems.add(bread);
        WhackAWordActivity.foodItems.add(cake);
        WhackAWordActivity.foodItems.add(carrot);
        WhackAWordActivity.foodItems.add(egg);
        WhackAWordActivity.foodItems.add(orange);
        WhackAWordActivity.foodItems.add(potato);
        WhackAWordActivity.foodItems.add(tomato);
    }

}
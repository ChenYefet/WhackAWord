package com.example.whackaword;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
 * after which they hear it again
 * and receive some positive feedback – a tick with a sound effect.
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
 * At the end of the third round, the user wins.
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

    private Map<FoodItem, FoodCard> foodItemsSelectedForDisplayOnFoodCards;
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

        this.foodItemsSelectedForDisplayOnFoodCards = new HashMap<>();

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
        this.displayFoodCards(this.numberOfCardsToDisplay);

        Set<FoodItem> foodItemsOnDisplay = this.foodItemsSelectedForDisplayOnFoodCards.keySet();
        // The food items are currently 'on' display (and not just set 'for' display)
        // since the displayFoodCards method has been called

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
        FoodCard correctFoodCard = this.foodItemsSelectedForDisplayOnFoodCards.get(this.correctFoodItem);

        this.playAudio(correctFoodItemAudioID);

        this.setOnClickListenerForCorrectFoodCard(correctFoodCard, correctFoodItemAudioID);

        if (this.foodItemsSelectedForDisplayOnFoodCards.size() > 1)

        // I.e. if there are any incorrect food items that are set for display
        // in addition to the one correct food item ...

        {

            for (FoodItem foodItem : this.foodItemsSelectedForDisplayOnFoodCards.keySet())
            {

                if (foodItem != this.correctFoodItem)
                {
                    FoodCard incorrectFoodCard = this.foodItemsSelectedForDisplayOnFoodCards.get(foodItem);

                    this.setOnClickListenerForIncorrectFoodCard(incorrectFoodCard);
                    // ... set a click listener for each of their corresponding food cards

                }

            }

        }

    }

    /**
     * Displays a number of food cards equal to numberOfCards.
     * Each food card is set an image of a different food item,
     * including at least one that hasn't yet been correctly tapped
     */
    private void displayFoodCards(int numberOfCards)
    {
        for (int i = 0; i < numberOfCards; i++)
        {
            FoodCard foodCard = this.getAvailableFoodCard();
            FoodItem foodItem = this.getAvailableFoodItem();

            if (i == numberOfCards - 1)

            // I.e. if this is the last iteration of this for loop ...

            {
                Set<FoodItem> foodItemsSelectedForDisplay = this.foodItemsSelectedForDisplayOnFoodCards.keySet();
                Set<FoodItem> foodItemsSelectedForDisplayThatHaveNotPreviouslyBeenCorrectlyTapped = new HashSet<>(foodItemsSelectedForDisplay);
                foodItemsSelectedForDisplayThatHaveNotPreviouslyBeenCorrectlyTapped.removeAll(this.correctlyTappedFoodItems);

                if (foodItemsSelectedForDisplayThatHaveNotPreviouslyBeenCorrectlyTapped.size() == 0)

                // ... and all the food items so far selected to be displayed
                // have previously been correctly tapped

                {

                    while (this.correctlyTappedFoodItems.contains(foodItem))
                    {
                        foodItem = this.getAvailableFoodItem();
                        // ... ensure that the last food item selected to be displayed
                        // is one which hasn't previously been correctly tapped
                        // (to ensure that the game is more challenging and fun)

                    }

                }

                // Note that calling keySet() on an empty map outputs an empty set,
                // so no exception would be thrown since the map has been initialised
                // as an empty map

            }

            foodCard.setFoodItem(foodItem);

            this.foodItemsSelectedForDisplayOnFoodCards.put(foodItem, foodCard);
            this.availableFoodItems.remove(foodItem);
            this.availableFoodCards.remove(foodCard);

            ImageView foodCardImageView = this.findViewById(foodCard.getImageViewID());
            foodCardImageView.setImageResource(foodItem.getImageID());
        }

        this.cardsPopUp();
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
     * ensuring that all audio files are played in sequence.
     *
     * Displays tick if the tick sound is being played.
     *
     * Hides all cards upon audio completion if the user has won.
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

                if (this.audioQueue.isEmpty() &&
                        this.countOfSuccessfulTaps == REQUIRED_NUMBER_OF_SUCCESSFUL_TAPS_PER_LEVEL &&
                        this.currentLevel == LAST_LEVEL)

                // I.e. if the last of the audio has played and you have won

                {
                    this.hideCards();
                }

            });

            this.mediaPlayer.start();

            if (audioID == R.raw.correct)
            {
                this.displayTick();
            }

        }

    }

    /**
     * Sets a click listener for the correct food card:
     *
     * When the correct food card is tapped,
     * plays the audio for the correct food item
     * and plays the tick sound.
     * If you haven't won,
     * hides all cards and plays Whack-A-Word again
     * with the number of cards to display dependent on the current level
     */
    private void setOnClickListenerForCorrectFoodCard(FoodCard correctFoodCard, int correctFoodItemAudioID)
    {
        FrameLayout frameLayout = this.findViewById(correctFoodCard.getID());
        frameLayout.setOnClickListener(v ->
        {
            this.countOfSuccessfulTaps++;
            this.correctlyTappedFoodItems.add(correctFoodCard.getFoodItem());

            this.playAudio(correctFoodItemAudioID);
            this.playAudio(R.raw.correct);

            if (this.countOfSuccessfulTaps == REQUIRED_NUMBER_OF_SUCCESSFUL_TAPS_PER_LEVEL &&
                    this.currentLevel < LAST_LEVEL)

            // I.e. if you have reached the next level

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

            if (this.countOfSuccessfulTaps == REQUIRED_NUMBER_OF_SUCCESSFUL_TAPS_PER_LEVEL &&
                    this.currentLevel == LAST_LEVEL)
            {
                // You win, well done!
            }
            else
            {
                this.hideCards();
                this.availableFoodItems = new ArrayList<>(WhackAWordActivity.foodItems);
                this.foodItemsSelectedForDisplayOnFoodCards = new HashMap<>();
                this.whackAWord();
            }
        });

        this.frameLayoutsWithClickListeners.add(frameLayout);
    }

    /**
     * Sets a click listener for an incorrect food card:
     *
     * Hides cards then displays random cards again with the same food items,
     * setting click listeners for the correct and incorrect food cards
     */
    private void setOnClickListenerForIncorrectFoodCard(FoodCard incorrectFoodCard)
    {
        FrameLayout frameLayout = this.findViewById(incorrectFoodCard.getID());
        frameLayout.setOnClickListener(v ->
        {
            this.hideCards();

            this.foodItemsSelectedForDisplayOnFoodCards.replaceAll(((foodItem, foodCard) -> null));
            // Keeps all foodItem keys in the map while setting all their foodCard values to null
            // since those food items need to be displayed again
            // in food cards which are not yet determined

            this.displayFoodCardsAgain();

            int correctFoodItemAudioID = this.correctFoodItem.getAudioID();
            FoodCard correctFoodCard = this.foodItemsSelectedForDisplayOnFoodCards.get(this.correctFoodItem);

            this.setOnClickListenerForCorrectFoodCard(correctFoodCard, correctFoodItemAudioID);

            for (FoodItem foodItem : this.foodItemsSelectedForDisplayOnFoodCards.keySet())
            {

                if (foodItem != this.correctFoodItem)
                {
                    FoodCard foodCard = this.foodItemsSelectedForDisplayOnFoodCards.get(foodItem);

                    this.setOnClickListenerForIncorrectFoodCard(foodCard);
                }

            }

        });

        this.frameLayoutsWithClickListeners.add(frameLayout);
    }

    /**
     * Displays in random food cards
     * the same food items that were already displayed on food cards
     */
    private void displayFoodCardsAgain()
    {
        for (FoodItem foodItem : this.foodItemsSelectedForDisplayOnFoodCards.keySet())

        // Note that food items have been kept in the map
        // even after the cards have been hidden via the hideCards method
        // since they are set to be displayed again.
        // Their previous corresponding food cards have been removed from the map (i.e. set to null)
        // within the click listener for incorrect cards
        // since they are set to be replaced by random food cards

        {
            FoodCard foodCard = this.getAvailableFoodCard();
            foodCard.setFoodItem(foodItem);

            this.foodItemsSelectedForDisplayOnFoodCards.put(foodItem, foodCard);
            this.availableFoodCards.remove(foodCard);

            ImageView foodCardImageView = this.findViewById(foodCard.getImageViewID());
            foodCardImageView.setImageResource(foodItem.getImageID());
        }

        this.cardsPopUp();
    }

    /**
     * Displays a tick for one second
     */
    private void displayTick()
    {
        ImageView tick = this.findViewById(R.id.tick);
        tick.setVisibility(View.VISIBLE);

        new Handler().postDelayed((Runnable) () -> tick.setVisibility(View.INVISIBLE), 1000);
    }

    /**
     * Causes each card set for display to pop up
     */
    private void cardsPopUp()
    {

        float shiftAmount = -500;

        for (FoodItem foodItem : this.foodItemsSelectedForDisplayOnFoodCards.keySet())
        {
            FoodCard foodCard = this.foodItemsSelectedForDisplayOnFoodCards.get(foodItem);
            FrameLayout frameLayout = this.findViewById(foodCard.getID());
            ObjectAnimator animation = ObjectAnimator.ofFloat(frameLayout, "translationY", shiftAmount);
            animation.setDuration(500);
            animation.start();
        }
    }

    /**
     * Causes each card on display to hide
     * and clears their click listeners
     */
    private void hideCards()
    {

        float shiftAmount = 1;

        for (FoodItem foodItem : this.foodItemsSelectedForDisplayOnFoodCards.keySet())
        {
            FoodCard foodCard = this.foodItemsSelectedForDisplayOnFoodCards.get(foodItem);
            FrameLayout frameLayout = this.findViewById(foodCard.getID());
            ObjectAnimator animation = ObjectAnimator.ofFloat(frameLayout, "translationY", shiftAmount);
            animation.setDuration(500);
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
     * Fills foodCards with all the food cards that exist within the game
     */
    private static void fillFoodCardsSet()
    {
        FoodCard foodCard1 = new FoodCard(R.id.card1, R.id.image_view_for_card_1);
        FoodCard foodCard2 = new FoodCard(R.id.card2, R.id.image_view_for_card_2);
        FoodCard foodCard3 = new FoodCard(R.id.card3, R.id.image_view_for_card_3);
        FoodCard foodCard4 = new FoodCard(R.id.card4, R.id.image_view_for_card_4);
        FoodCard foodCard5 = new FoodCard(R.id.card5, R.id.image_view_for_card_5);

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
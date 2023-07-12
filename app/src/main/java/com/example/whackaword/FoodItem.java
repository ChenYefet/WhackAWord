package com.example.whackaword;

import androidx.annotation.NonNull;

/**
 * An object of the FoodItem class represents
 * a vocabulary item with the characteristics of a food item
 */
public class FoodItem extends VocabularyItem
{
    private final int imageID;

    /**
     * Constructor for objects of the class FoodItem
     * with arguments for all of its superclass' and its own instance variables
     */
    public FoodItem(String aName, String aDefinition, int anAudioID, int anImageID)
    {
        super(aName, aDefinition, anAudioID);
        this.imageID = anImageID;
    }

    /**
     * Constructor for objects of the class FoodItem
     * without an argument for its superclass' definition instance variable
     */
    public FoodItem(String aName, int anAudioID, int anImageID)
    {
        super(aName, "", anAudioID);
        this.imageID = anImageID;
    }

    /**
     * Getter for the image ID of the food item
     */
    public int getImageID()
    {
        return this.imageID;
    }

    /**
     * Returns a string representation of the food item
     */
    @NonNull
    @Override
    public String toString()
    {
        return super.toString() + ", which is a food item with image ID " + this.getImageID();
    }
}

package com.example.whackaword;

import androidx.annotation.NonNull;

/**
 * The abstract class VocabularyItem serves as a template for creating
 * concrete subclasses of vocabulary items, such as the FoodItem class
 */
public abstract class VocabularyItem
{
    private final String name;
    private String definition; // May be useful in the future
    private final int audioID;

    /**
     * Constructor for objects of the abstract class VocabularyItem
     */
    public VocabularyItem(String aName, String aDefinition, int anAudioID)
    {
        this.name = aName;
        this.definition = aDefinition;
        this.audioID = anAudioID;
    }

    /**
     * Getter for the name of the vocabulary item
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Getter for the definition of the vocabulary item
     */
    public String getDefinition()
    {
        return this.definition;
    }

    /**
     * Setter for the definition of the vocabulary item
     */
    public void setDefinition(String aDefinition)
    {
        this.definition = aDefinition;
    }

    /**
     * Getter for the audio ID of the vocabulary item
     */
    public int getAudioID()
    {
        return this.audioID;
    }

    /**
     * Returns true if the vocabulary item is the same object as anObject,
     * or if the vocabulary item has the same name and definition as anObject
     * if anObject is of type VocabularyItem;
     * otherwise, returns false
     */
    @Override
    public boolean equals(Object anObject)
    {
        if (this == anObject)
        {
            return true;
        }

        if (anObject instanceof VocabularyItem)
        {
            VocabularyItem vocabularyItem = (VocabularyItem) anObject;
            return this.getName().equals(vocabularyItem.getName()) && this.getDefinition().equals(vocabularyItem.getDefinition());
        }

        return false;
    }

    /**
     * Returns a string representation of the vocabulary item
     */
    @NonNull
    @Override
    public String toString()
    {
        return "An instance of class " + this.getClass().getName() +
                " representing the following word or phrase: '" + this.getName() + "'";
    }
}
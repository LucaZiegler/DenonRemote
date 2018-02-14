package com.brin.denonremotefree.BrinObj;

/**
 * Created by Luca on 20.07.2016.
 */
public class Preference
{
    private int type;
    private int def;
    private int ind;
    private String title;
    private String diaTitle;
    private String description;
    private String storeTag;
    private String[] diaItems;
    private boolean available;

    public Preference(int ind, int type, String title)
    {
        // DIVIDER
        this.type = type;
        this.title = title;
        this.ind = ind;
    }

    public Preference(int ind, int type, String title, String description, int defVal, String storeTag, boolean available)
    {
        // SLIDER / CHECK
        this.ind = ind;
        this.type = type;
        this.title = title;
        this.description = description;
        this.def = defVal;
        this.storeTag = storeTag;
        this.available = available;
    }

    public Preference(int ind, int type, String title, String diaTitle, String description, int defVal, String storeTag, boolean available, String[] diaItems)
    {
        // DIALOG
        this.ind = ind;
        this.type = type;
        this.title = title;
        this.diaTitle = diaTitle;
        this.description = description;
        this.def = defVal;
        this.storeTag = storeTag;
        this.available = available;
        this.diaItems = diaItems;
    }

    public int getType()
    {
        return type;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public String getStoreTag()
    {
        return storeTag;
    }

    public boolean isAvailable()
    {
        return available;
    }

    public String getDiaTitle()
    {
        return diaTitle;
    }

    public void setDiaTitle(String diaTitle)
    {
        this.diaTitle = diaTitle;
    }

    public int getDef()
    {
        return def;
    }

    public void setDef(int def)
    {
        this.def = def;
    }

    public String[] getDiaItems()
    {
        return diaItems;
    }

    public void setDiaItems(String[] diaItems)
    {
        this.diaItems = diaItems;
    }

    public int getInd()
    {
        return ind;
    }
}

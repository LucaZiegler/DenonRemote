package com.brin.denonremotefree.BrinObj;

/**
 * Created by Luca on 30.08.2016.
 */

public class PlayerItem
{
    private int position;
    private int icon;
    private String title;
    private boolean selected;

    public PlayerItem(int position, int icon, String title, boolean selected)
    {
        this.position = position;
        this.icon = icon;
        this.title = title;
        this.selected = selected;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public int getIcon()
    {
        return icon;
    }

    public void setIcon(int icon)
    {
        this.icon = icon;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
}
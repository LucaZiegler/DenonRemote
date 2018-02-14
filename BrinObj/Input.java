package com.brin.denonremotefree.BrinObj;

/**
 * Created by Luca on 06.12.2016.
 */

public class Input
{
    private String name;
    private String nameNew = null;
    public String com;
    public int id;
    public int iconResSmall;
    public int iconResBig;
    public boolean active = false;

    public String getName()
    {
        return nameNew == null ? name : nameNew;
    }

    public void setNameNew(String nameNew)
    {
        this.nameNew = nameNew;
    }

    public Input(String com, String name, int id, int iconResSmall, int iconResBig)
    {
        this.name = name;
        this.com = com;
        this.id = id;
        this.iconResSmall = iconResSmall;
        this.iconResBig = iconResBig;
    }
}

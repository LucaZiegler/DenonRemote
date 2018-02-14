package com.brin.denonremotefree.BrinObj;

import android.content.Context;

/**
 * Created by Luca on 16.06.2016.
 */
public class AddableElement implements com.brin.denonremotefree.Interface.AddableElement
{
    public AddableElement(String elementTitle,String elementDesc,int elementType,int elementWidth,int elementIcon)
    {
        this.elementTitle = elementTitle;
        this.elementDesc = elementDesc;
        this.elementType = elementType;
        this.elementWidth = elementWidth;
        this.elementIcon = elementIcon;
    }
    public AddableElement(String elementTitle,String elementDesc,boolean elementZoneCompatible,int elementZone,int elementType,int elementWidth,int elementIcon)
    {
        this.elementTitle = elementTitle;
        this.elementDesc = elementDesc;
        this.elementZoneCompatible = elementZoneCompatible;
        this.elementZone = elementZone;
        this.elementType = elementType;
        this.elementWidth = elementWidth;
        this.elementIcon = elementIcon;
    }
    private String elementTitle;
    private String elementDesc;
    private boolean elementZoneCompatible = false;
    private int elementZone = 1;

    public void setElementWidth(int elementWidth)
    {
        this.elementWidth = elementWidth;
    }

    private int elementType;
    private int elementWidth;
    private int elementIcon;

    @Override
    public AddableElement getAddableElement(Context c)
    {
        return this;
    }

    public String getElementTitle()
    {
        return elementTitle;
    }

    public String getElementDesc()
    {
        return elementDesc;
    }

    public boolean isElementZoneCompatible()
    {
        return elementZoneCompatible;
    }

    public int getElementZone()
    {
        return elementZone;
    }

    public int getElementType()
    {
        return elementType;
    }

    public int getElementWidth()
    {
        return elementWidth;
    }

    public int getElementIcon()
    {
        return elementIcon;
    }

}


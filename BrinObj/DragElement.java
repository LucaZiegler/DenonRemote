package com.brin.denonremotefree.BrinObj;

import android.widget.FrameLayout;

/**
 * Created by Luca on 09.06.2016.
 */
public class DragElement
{
    public DragElement(int type)
    {
        elementType = type;
    }

    public int elementType;
    public int elementZone;
    //public int elementPosX;
    //public int elementPosY;
    public FrameLayout.LayoutParams elementParams = null;
    public int elementRow;
    public String elementTitle;
    public int elementZoneSupport = -1;
}

package com.brin.denonremotefree.HomeControl.Elements.Config;

import android.content.Context;

import com.brin.denonremotefree.BrinObj.AddableElement;
import com.brin.denonremotefree.BrinObj.BrinDashFragment;
import com.brin.denonremotefree.HomeControl.Elements.FavoriteElement;
import com.brin.denonremotefree.HomeControl.Elements.InputElement;
import com.brin.denonremotefree.HomeControl.Elements.NavElement;
import com.brin.denonremotefree.HomeControl.Elements.PlayerElement;
import com.brin.denonremotefree.HomeControl.Elements.PowerElement;
import com.brin.denonremotefree.HomeControl.Elements.QuickSelectElement;
import com.brin.denonremotefree.HomeControl.Elements.SoundModeElement;
import com.brin.denonremotefree.HomeControl.Elements.TunerElement;
import com.brin.denonremotefree.HomeControl.Elements.VolumeElement;

import java.util.ArrayList;
import java.util.UnknownFormatFlagsException;

/**
 * Created by Luca on 21.06.2016.
 */
public class PrefsElements
{
    public static final int TYPE_EMPTY = 0, TYPE_VOLUME = 1, TYPE_PW = 2, TYPE_SOUND_MODE = 3, TYPE_PLAYER = 4, TYPE_FAV = 5, TYPE_INP = 6, TYPE_TUNER = 7, TYPE_NAV = 8, TYPE_QUICK = 9;

    public static ArrayList<AddableElement> returnList1(Context c)
    {
        ArrayList<AddableElement> l = new ArrayList<>();
        l.add(new PowerElement().getAddableElement(c));
        l.add(new InputElement().getAddableElement(c));
        l.add(new VolumeElement().getAddableElement(c));
        l.add(new SoundModeElement().getAddableElement(c));
        l.add(new PlayerElement().getAddableElement(c));
        l.add(new FavoriteElement().getAddableElement(c));
        l.add(new TunerElement().getAddableElement(c));
        l.add(new NavElement().getAddableElement(c));
        l.add(new QuickSelectElement().getAddableElement(c));
        return l;
    }
    /*
    public static ArrayList<AddableElement> returnList1(Context c)
    {
        ArrayList<AddableElement> l = new ArrayList<>();
        l.add(PowerElement.getAddableElement(c));
        l.add(InputElement.getAddableElement(c));
        l.add(VolumeElement.getAddableElement(c));
        l.add(SoundModeElement.getAddableElement(c));
        l.add(PlayerElement.getAddableElement(c));
        l.add(FavoriteElement.getAddableElement(c));
        l.add(TunerElement.getAddableElement(c));
        l.add(NavElement.getAddableElement(c));
        l.add(QuickSelectElement.getAddableElement(c));
        return l;
    }*/

    public static ArrayList<AddableElement> returnList2()
    {
        ArrayList<AddableElement> l = new ArrayList<>();

        return l;
    }

    public static ArrayList<AddableElement> returnList3()
    {
        ArrayList<AddableElement> l = new ArrayList<>();

        return l;
    }

    public static AddableElement returnAddableByType(Context c,int t)
    {
        switch (t)
        {
            case TYPE_VOLUME:
                return new VolumeElement().getAddableElement(c);
            case TYPE_PW:
                return new PowerElement().getAddableElement(c);
            case TYPE_SOUND_MODE:
                return new SoundModeElement().getAddableElement(c);
            case TYPE_PLAYER:
                return new PlayerElement().getAddableElement(c);
            case TYPE_FAV:
                return new FavoriteElement().getAddableElement(c);
            case TYPE_INP:
                return new InputElement().getAddableElement(c);
            case TYPE_TUNER:
                return new TunerElement().getAddableElement(c);
            case TYPE_NAV:
                return new NavElement().getAddableElement(c);
            case TYPE_QUICK:
                return new QuickSelectElement().getAddableElement(c);
            default:
                throw new UnknownFormatFlagsException("Unknown fragment type: "+t);
        }
    }

    public static BrinDashFragment returnFragmentByType(int t)
    {
        switch (t)
        {
            case TYPE_VOLUME:
                return new VolumeElement();
            case TYPE_PW:
                return new PowerElement();
            case TYPE_SOUND_MODE:
                return new SoundModeElement();
            case TYPE_PLAYER:
                return new PlayerElement();
            case TYPE_FAV:
                return new FavoriteElement();
            case TYPE_INP:
                return new InputElement();
            case TYPE_TUNER:
                return new TunerElement();
            case TYPE_NAV:
                return new NavElement();
            case TYPE_QUICK:
                return new QuickSelectElement();
            default:
                throw new UnknownFormatFlagsException("Unknown fragment type: "+t);
        }
    }

    public static int rowWidth()
    {
        return 2;
    }

    public static String intentData()
    {
        return "KEJUHbg4fn";
    }
    public static String intentDataAdd()
    {
        return "cerkjnl§khj74";
    }
}

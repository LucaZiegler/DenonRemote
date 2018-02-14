package com.brin.denonremotefree.BrinObj;

/**
 * Created by Luca on 04.07.2016.
 */
public class HomeTab
{
    private String tabName;
    private int tabId;
    private boolean genDef = false;
    private int genZone = 0;

    public HomeTab(int id,String label)
    {
        tabName = label;
        tabId = id;
    }

    public String getTabName()
    {
        return tabName;
    }

    public void setTabName(String tabName)
    {
        this.tabName = tabName;
    }

    public int getTabId()
    {
        return tabId;
    }

    public void setTabId(int tabId)
    {
        this.tabId = tabId;
    }

    public boolean isGenDef()
    {
        return genDef;
    }

    public void setGenDef(boolean genDef)
    {
        this.genDef = genDef;
    }

    public int getGenZone()
    {
        return genZone;
    }

    public void setGenZone(int genZone)
    {
        this.genZone = genZone;
    }
}

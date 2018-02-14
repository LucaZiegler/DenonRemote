package com.brin.denonremotefree.BrinObj;

/**
 * Created by Luca on 15.05.2016.
 */
public class ReceiverStored
{
    public String receiverHostName;
    public String receiverIp;
    public String receiverAltName = null;
    public int receiverManufacturer = -1;
    public boolean receiverPrimary = false;
    public boolean deviceDemo = false;
    public String routerMac = null;
    public long storedTime;
    public int receiverZones;

    public String toJson()
    {
        return ReceiverTools.ReceiverStoredToJson(this);
    }
}

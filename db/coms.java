package com.brin.denonremotefree.db;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Luca on 13.12.2015.
 */
public class coms
{
    public static String serviceID = "com.brin.denonremotefree.SERVICE", altcs = "UA-59925624-1";
    public static String netPagePrev = "NS9Y", netPageNext = "NS9X", netAddFav = "NSFV MEM";
    public static String curUp = "NS90", curDown = "NS91", curLeft = "NS92", curRight = "NS93", curEnter = "NS94", curRemEnt = "MNENT", curRemReturn = "MNRTN", curRemOption = "MNOPT", curInfo = "MNINF", curMenuOn = "MNMEN ON", curMenuOff = "MNMEN OFF", comPageDn = "NS9X", comPageUp = "NS9Y";
    public static String curRemLeft = "MNCLT", curRemRight = "MNCRT", curRemDn = "MNCDN", curRemUp = "MNCUP";
    public static String mediaSkipBack = "NS9E", mediaSkipFor = "NS9D", mediaPlay = "NS9A", mediaPause = "NS94", mediaStop = "NS9C", mediaRandomOn = "NS9K", mediaRandomOff = "NS9M", mediaRepeatOff = "NS9J", mediaRepeatAll = "NS9I", mediaRepeatOne = "NS9H";
    public static String muteOff = "MUOFF", muteOn = "MUON";
    public static String bass = "PSBAS%20", treble = "STRE%20", dialog = "PSDIL%20", dimension = "PSDIM%20", lfe = "PSLFE%20", basssync = "PSBSC%20";
    public static String devicePowerOff = "PWSTANDBY", devicePowerOn = "PWON";

    public static String tunerPresetUp = "TPANUP", tunerPresetDn = "TPANDOWN";
    public static String tunerFreqUp = "TFANUP", tunerFreqDn = "TFANDOWN";
    public static String tunerModeAuto = "TMANAUTO", tunerModeManual = "TMANMANUAL";
    public static String tunerBand1 = "TFAN100000", tunerBand2 = "TFAN010000";
    //public static String tunerBand1 = "TMANAM", tunerBand2 = "TMANFM";
    public static String tunerFreqStatus = "TFAN?", tunerTPStatus = "TPAN?", tunerModeStatus = "TMAN?";

    public static String
            inputLastfm = "LASTFM", inputServer = "SERVER", inputFlickr = "FLICKR", input4 = "BD", input5 = "AUX1", input6 = "AUX2", input7 = "SMPLAY", input8 = "TV", inputTuner = "TUNER", input10 = "CD", input11 = "PHONO",
            inputIradio = "IRADIO", inputNet = "NET", input14 = "USB/IPOD", inputSpotify = "SPOTIFY", inputFav = "FAVORITES", inputPandora = "PANDORA", inputSirius = "SIRIUSXM", inputRhapsody = "RHAPSODY",inputGetRenames = "SSFUN ?";

    public static ArrayList<String> alAssigns()
    {
        return new ArrayList<>(Arrays.asList("SSPAAMOD 91C", "SSPAAMOD NOR", "SSPAAMOD ZO2", "SSPAAMOD ZO3", "SSPAAMOD ZOM", "SSPAAMOD BIA", "SSPAAMOD FRB", "SSPAAMOD 2CH"));
    }
}

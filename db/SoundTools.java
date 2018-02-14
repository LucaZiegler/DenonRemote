package com.brin.denonremotefree.db;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Luca on 17.01.2016.
 */
public class SoundTools {

    public static ArrayList<String> soundNames()
    {
        ArrayList<String> al = new ArrayList<>();
        al.addAll(Arrays.asList(
                "direct",
                "stereo",
                "pure direct",
                "standard",
                "multi ch stereo",
                "rock arena",
                "jazz club",
                "mono movie",
                "video game",
                "matrix",
                "virtual",
                "dts surround",
                "dolby digital",
                "dts neo:x game",
                "dts neo:x music",
                "dolby digital atmos",
                "dolby digital SURROUND",
                "dolby digital DIGITAL",
                "dolby digital D+DS",
                "dolby digital D+NEO:X C",
                "dolby digital D+NEO:X M",
                "dolby digital D+NEO:X G",
                "dolby digital D+",
                "dolby digital D+ +DS",
                "dolby digital HD",
                "dolby digital HD+DS",
                "dts SURROUND",
                "dts ES DSCRT6.1",
                "dts ES MTRX6.1",
                "dts96/24",
                "dts96 ES MTRX",
                "dts HD",
                "dts HD MSTR",
                "dts EXPRESS",
                "dts ES 8CH DSCRT",
                "MSMULTI CH IN",
                "MSM CH IN+DS",
                "MSMULTI CH IN 7.1",
                "AURO 3D",
                "AURO 2D SURRound"
        ));
        return al;
    }
    public static ArrayList<String> soundComs()
    {
        ArrayList<String> al = new ArrayList<>();
        al.addAll(Arrays.asList(
                "DIRECT",
                "STEREO",
                "PURE DIRECT",
                "STANDARD",
                "MCH STEREO",
                "ROCK ARENA",
                "JAZZ CLUB",
                "MONO MOVIE",
                "VIDEO GAME",
                "MATRIX",
                "VIRTUAL",
                "DTS SURROUND",
                "DOLBY DIGITAL",
                "GAME",
                "MUSIC",
                "DOLBY ATMOS",
                "DOLBY SURROUND",
                "DOLBY DIGITAL",
                "DOLBY D+DS",
                "DOLBY D+NEO:X C",
                "DOLBY D+NEO:X M",
                "DOLBY D+NEO:X G",
                "DOLBY D+",
                "DOLBY D+ +DS",
                "DOLBY HD",
                "DOLBY HD+DS",
                "DTS SURROUND",
                "DTS ES DSCRT6.1",
                "DTS ES MTRX6.1",
                "DTS96/24",
                "DTS96 ES MTRX",
                "DTS HD",
                "DTS HD MSTR",
                "DTS EXPRESS",
                "DTS ES 8CH DSCRT",
                "MULTI CH IN",
                "M CH IN+DS",
                "MULTI CH IN 7.1",
                "AURO3D",
                "AURO2DSURR"
        ));
        return al;
    }

}

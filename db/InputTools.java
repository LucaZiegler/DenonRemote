package com.brin.denonremotefree.db;

import android.content.Context;

import com.brin.denonremotefree.BrinObj.Input;
import com.brin.denonremotefree.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Server on 30.06.2015.
 */
public class InputTools {
    public static int getInputImage(String i){
        i = i.toLowerCase();

        if (i.contains("sat") || i.contains("cbl")) {
            return R.drawable.inp_sat_a;
        }

        if (i.contains("dvd")) {
            return R.drawable.inp_dvd_a;
        }

        if (i.contains("game")) {
            return R.drawable.inp_game_a;
        }

        if (i.contains("bd")) {
            return R.drawable.inp_bd_a;
        }

        if (i.contains("aux")) {
            return R.drawable.inp_aux_a;
        }

        if (i.contains("favorites")) {
            return R.drawable.inp_fav_a;
        }

        if (i.contains("server")) {
            return R.drawable.inp_mserver_a;
        }

        if (i.contains("tv")) {
            return R.drawable.inp_tv_a;
        }

        if (i.contains("tuner")) {
            return R.drawable.inp_tuner_a;
        }

        if (i.contains("cd")) {
            return R.drawable.inp_cd_a;
        }

        if (i.contains("phono_100_white")) {
            return R.drawable.inp_phono_a;
        }

        if (i.contains("internet radio") || i.contains("iradio")) {
            return R.drawable.inp_iradio_a;
        }

        if (i.contains("net")) {
            return R.drawable.inp_net_a;
        }

        if (i.contains("usb") || i.contains("dock")) {
            return R.drawable.inp_usb_a;
        }

        if (i.contains("spotify")) {
            return R.drawable.inp_spotify_a;
        }
        if (i.contains("mplay")) {
            return R.drawable.inp_mplayer_a;
        }
        if (i.contains("rhapsody")) {
            return R.drawable.inp_net_a;
        }
        if (i.contains("napster")) {
            return R.drawable.inp_net_a;
        }
        if (i.contains("pandora")) {
            return R.drawable.inp_net_a;
        }
        if (i.contains("last.fm")) {
            return R.drawable.inp_net_a;
        }
        if (i.contains("bluetooth") || i.contains("bt")) {
            return R.drawable.inp_bt_a;
        }
        if (i.contains("hdradio")) {
            return R.drawable.inp_tuner_a;
        }
        if (i.contains("sirius")) {
            return R.drawable.inp_net_a;
        }
        if (i.contains("digitalin") || i.contains("optical")) {
            return R.drawable.inp_optical_a;
        }
        if (i.contains("airplay")) {
            return R.drawable.inp_airplay_a;
        }
        if (i.contains("source")) {
            return R.drawable.inp_aux_a;
        }
        return R.drawable.inp_net_a;
    }
    public static Integer getInpIdFromReceiver(String input){
        input=input.toLowerCase();
        if (input.contains("cbl")) {
            return 1;
        }

        if (input.contains("dvd")) {
            return 2;
        }

        if (input.contains("game")) {
            return 3;
        }

        if (input.contains("bd")) {
            return 4;
        }

        if (input.contains("aux1")) {
            return 5;
        }

        if (input.contains("favorites")) {
            return 6;
        }

        if (input.contains("server")) {
            return 7;
        }

        if (input.contains("tv")) {
            return 8;
        }

        if (input.contains("tuner")) {
            return 9;
        }

        if (input.contains("cd")) {
            return 10;
        }

        if (input.contains("net")) {
            return 11;
        }

        if (input.contains("internet radio") || input.contains("iradio")) {
            return 12;
        }

        if (input.contains("phono")) {
            return 13;
        }

        if (input.contains("usb/ipod")) {
            return 14;
        }

        if (input.contains("spotify")) {
            return 15;
        }
        if (input.contains("aux2")) {
            return 16;
        }
        if (input.contains("play")) {
            return 17;
        }
        if (input.contains("rhapsody")) {
            return 18;
        }
        if (input.contains("napster")) {
            return 19;
        }
        if (input.contains("pandora")) {
            return 20;
        }
        if (input.contains("last.fm")) {
            return 21;
        }
        if (input.contains("bt")) {
            return 22;
        }
        if (input.contains("tooth")) {
            return 22;
        }
        return -1;
    }
    public static ArrayList<String> inputNames()
    {
        ArrayList<String> al = new ArrayList<>();
        al.addAll(Arrays.asList(
                "cbl/sat",
                "dvd",
                "game",
                "blu/ray",
                "aux-1",
                "favorites",
                "media server",
                "tv-audio",
                "tuner",
                "cd",
                "network",
                "internet radio",
                "phono",
                "ipod/usb",
                "spotify",
                "aux-2",
                "media-player",
                "rhapsody",
                "napster",
                "pandora",
                "lastfm",
                "bluetooth"
        ));
        return al;
    }
    public static ArrayList<String> inputComs()
    {
        ArrayList<String> al = new ArrayList<>();
        al.addAll(Arrays.asList(
                "SAT/CBL",
                "DVD",
                "GAME",
                "BD",
                "AUX1",
                "FAVORITES",
                "SERVER",
                "TV",
                "TUNER",
                "CD",
                "NET",
                "IRADIO",
                "PHONO",
                "USB/IPOD",
                "SPOTIFY",
                "AUX2",
                "MPLAY",
                "RHAPSODY",
                "NAPSTER",
                "PANDORA",
                "LASTFM",
                "BT"
        ));
        return al;
    }

    public static ArrayList<Input> inputList()
    {
        return new ArrayList<>(Arrays.asList(
                new Input("SAT/CBL", "cbl/sat",1,R.drawable.inp_sat_100_white,R.drawable.inp_aux_a),
                new Input("DVD", "dvd",2,R.drawable.dvd_100_white,R.drawable.inp_aux_a),
                new Input("GAME", "game",3,R.drawable.game_100_white,R.drawable.inp_aux_a),
                new Input("BD", "blu/ray",4,R.drawable.bd_100_white,R.drawable.inp_aux_a),
                new Input("AUX1", "aux-1",5,R.drawable.hdmi_100_white,R.drawable.inp_aux_a),
                new Input("FAVORITES", "favorites",6,R.drawable.like_100_white,R.drawable.inp_aux_a),
                new Input("SERVER", "media server",7,R.drawable.mserver_100_white,R.drawable.inp_aux_a),
                new Input("TV", "tv-audio",8,R.drawable.tv_100_white,R.drawable.inp_aux_a),
                new Input("TUNER", "tuner",9,R.drawable.tuner_100_white,R.drawable.inp_aux_a),
                new Input("CD", "cd",10,R.drawable.cd_100_white,R.drawable.inp_aux_a),
                new Input("NET", "network",11,R.drawable.net_100_white,R.drawable.inp_aux_a),
                new Input("IRADIO", "internet radio",12,R.drawable.iradio_100_white,R.drawable.inp_aux_a),
                new Input("PHONO", "phono",13,R.drawable.phono_100_white,R.drawable.inp_aux_a),
                new Input("USB/IPOD", "ipod/usb",14,R.drawable.usb_100_white,R.drawable.inp_aux_a),
                new Input("SPOTIFY", "spotify",15,R.drawable.spotify_100_white,R.drawable.inp_aux_a),
                new Input("AUX2", "aux-2",16,R.drawable.hdmi_100_white,R.drawable.inp_aux_a),
                new Input("MPLAY", "media-player",17,R.drawable.mplayer_100_white,R.drawable.inp_aux_a),
                new Input("RHAPSODY", "rhapsody",18,R.drawable.net_100_white,R.drawable.inp_aux_a),
                new Input("NAPSTER", "napster",19,R.drawable.net_100_white,R.drawable.inp_aux_a),
                new Input("PANDORA", "pandora",20,R.drawable.net_100_white,R.drawable.inp_aux_a),
                new Input("LASTFM", "lastfm",21,R.drawable.net_100_white,R.drawable.inp_aux_a),
                new Input("BT", "bluetooth",22,R.drawable.bt_100_white,R.drawable.inp_aux_a)
        ));
    }

    public static ArrayList<Integer> inputIconsSmall()
    {
        ArrayList<Integer> al = new ArrayList<>();
        al.addAll(Arrays.asList(
                R.drawable.inp_sat_100_white,
                R.drawable.dvd_100_white,
                R.drawable.game_100_white,
                R.drawable.bd_100_white,
                R.drawable.hdmi_100_white,
                R.drawable.like_100_white,
                R.drawable.mserver_100_white,
                R.drawable.tv_100_white,
                R.drawable.tuner_100_white,
                R.drawable.cd_100_white,
                R.drawable.net_100_white,
                R.drawable.iradio_100_white,
                R.drawable.phono_100_white,
                R.drawable.usb_100_white,
                R.drawable.spotify_100_white,
                R.drawable.hdmi_100_white,
                R.drawable.mplayer_100_white,
                R.drawable.net_100_white,
                R.drawable.net_100_white,
                R.drawable.net_100_white,
                R.drawable.net_100_white,
                R.drawable.bt_100_white
        ));
        return al;
    }

    public static String loadInputId(Context c,int inputId)
    {
        return c.getResources().getStringArray(R.array.input_coms)[inputId].toUpperCase();
    }
    public static boolean validInputName(String l)
    {
        ArrayList<String> inputs = new ArrayList<>();
        inputs.addAll(Arrays.asList("sat/cbl","sat", "cbl", "dvd", "game", "bd", "aux", "favorites", "server", "tv", "tuner", "cd", "phono_100_white", "iradio", "net", "usb/ipod", "dock", "spotify", "mplay", "rhapsody", "napster", "pandora", "lastfm", "bluetooth", "bt", "hdradio", "sirius", "digitalin", "optical", "airplay", "source","digitalin","irp","flickr","ipod","M-XPORT","BLUETOOTH"));
        for (int i = 0;i < inputs.size(); i++)
        {
            if(l.toLowerCase().contains(inputs.get(i).toLowerCase()))
                return true;
        }
        return false;
    }
    public String prepareHttpInputRename(final Integer inpId,String renameTo){
        renameTo=renameTo.replaceAll(" " , "+");

        String switchRen1="off";
        String switchRen2="off";
        String switchRen3="off";
        String switchRen4="off";
        String switchRen5="off";
        String switchRen6="off";
        String switchRen7="off";
        String switchRen8="off";
        String switchRen9="off";
        String switchRen10="off";

        if(inpId==1){
            switchRen1="on";
        }
        if(inpId==2){
            switchRen2="on";
        }
        if(inpId==4){
            switchRen3="on";
        }
        if(inpId==3){
            switchRen4="on";
        }
        if(inpId==5){
            switchRen5="on";
        }
        if(inpId==16){
            switchRen6="on";
        }
        if(inpId==17){
            switchRen7="on";
        }
        if(inpId==10){
            switchRen8="on";
        }
        if(inpId==8){
            switchRen9="on";
        }
        if(inpId==13){
            switchRen10="on";
        }

        return "setPureDirectOn=OFF&setSetupLock=OFF&setFuncRenameDefault=off"+

                "&textFuncRenameSATCBL="+renameTo+
                "&setFuncRenameSATCBL="+switchRen1+

                "&textFuncRenameDVD="+renameTo+
                "&setFuncRenameDVD="+switchRen2+

                "&textFuncRenameBD="+renameTo+
                "&setFuncRenameBD="+switchRen3+

                "&textFuncRenameGAME="+renameTo+
                "&setFuncRenameGAME="+switchRen4+

                "&textFuncRenameAUX1="+renameTo+
                "&setFuncRenameAUX1="+switchRen5+

                "&textFuncRenameAUX2="+renameTo+
                "&setFuncRenameAUX2="+switchRen6+

                "&textFuncRenameMPLAY="+renameTo+
                "&setFuncRenameMPLAY="+switchRen7+

                "&textFuncRenameCD="+renameTo+
                "&setFuncRenameCD="+switchRen8+

                "&textFuncRenameTV="+renameTo+
                "&setFuncRenameTV="+switchRen9+

                "&textFuncRenamePHONO="+renameTo+
                "&setFuncRenamePHONO="+switchRen10;
    }

    private static ArrayList<Integer> getInputImageList()
    {
        ArrayList<Integer> l = new ArrayList<>();
        l.addAll(Arrays.asList(
                R.drawable.inp_sat_a,
                R.drawable.inp_dvd_a,
                R.drawable.inp_game_a,
                R.drawable.inp_bd_a,
                R.drawable.inp_aux_a,
                R.drawable.inp_fav_a,
                R.drawable.inp_mserver_a,
                R.drawable.inp_tv_a,
                R.drawable.inp_tuner_a,
                R.drawable.inp_cd_a,
                R.drawable.inp_net_a,
                R.drawable.inp_iradio_a,
                R.drawable.inp_phono_a,
                R.drawable.inp_usb_a,
                R.drawable.inp_spotify_a,
                R.drawable.inp_aux_a,
                R.drawable.inp_mplayer_a,
                R.drawable.inp_net_a,
                R.drawable.inp_net_a,
                R.drawable.inp_net_a,
                R.drawable.inp_net_a,
                R.drawable.inp_bt_a,
                R.drawable.inp_tuner_a,
                R.drawable.inp_net_a,
                R.drawable.inp_optical_a,
                R.drawable.inp_airplay_a,
                R.drawable.inp_aux_a
        ));
        return l;
    }

    public static int getInputImageFromId(int inpId)
    {
        return getInputImageList().get(inpId - 1);
    }
}

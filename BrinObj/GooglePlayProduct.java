package com.brin.denonremotefree.BrinObj;

/**
 * Created by Luca on 12.10.2016.
 */

public class GooglePlayProduct
{
    public String title = "";
    public boolean bought = false;
    public String price = "";
    public String id = "";

    public GooglePlayProduct(String title,boolean bought,String price,String id)
    {
        this.title = title;
        this.bought = bought;
        this.price = price;
        this.id = id;
    }
}

package com.punuo.sys.app.agedcare.model;

import androidx.annotation.DrawableRes;

/**
 * Created by han.chen.
 * Date on 2021/2/19.
 **/
public class MenuItem {

    public int id;
    @DrawableRes
    public int drawable;

    public MenuItem(int id, @DrawableRes int drawable) {
        this.id = id;
        this.drawable = drawable;
    }
}

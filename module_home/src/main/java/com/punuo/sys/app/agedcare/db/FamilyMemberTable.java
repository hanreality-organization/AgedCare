package com.punuo.sys.app.agedcare.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by han.chen.
 * Date on 2021/2/22.
 **/
@Database(name = FamilyMemberTable.NAME, version = FamilyMemberTable.VERSION)
public class FamilyMemberTable {
    public static final String NAME = "member";
    public static final int VERSION = 1;
}

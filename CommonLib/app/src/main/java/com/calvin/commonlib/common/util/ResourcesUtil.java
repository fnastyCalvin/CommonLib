package com.calvin.commonlib.common.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;

/**
 * Created by jiangtao on 2016/4/13 15:49.
 */
public class ResourcesUtil {
    private ResourcesUtil() {
        //no instance
    }

    public static final String LAYTOUT="layout";
    public static final String DRAWABLE="drawable";
    public static final String MIPMAP="mipmap";
    public static final String MENU="menu";
    public static final String RAW="raw";
    public static final String ANIM="anim";
    public static final String STRING="string";
    public static final String STYLE="style";
    public static final String STYLEABLE="styleable";
    public static final String INTEGER="integer";
    public static final String ID="id";
    public static final String DIMEN="dimen";
    public static final String COLOR="color";
    public static final String BOOL="bool";
    public static final String ATTR="attr";



    public static int getResourceId(Context context, String name, String type){
        Resources resources=null;
        PackageManager pm=context.getPackageManager();
        try {
            resources=context.getResources();
            return resources.getIdentifier(name, type, context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

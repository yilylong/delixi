package com.delixi.price;

/**
 * Created by long on 2016/2/23.
 */
public class StringUtils {
    public static boolean isEmpty(String string){
        if(string==null){
            return true;
        }
        if(string.isEmpty()){
            return true;
        }
        return false;
    }
}

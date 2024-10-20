package com.xiangyueEducation.uploaderCloud.Utils.BaseUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayUtilsJiang {

    public static <T>  T[] createArray(T... params){
        return params;
    }


    public static String[] reverseArray(String[] array) {
        List<String> list = Arrays.asList(array);
        Collections.reverse(list);
        return list.toArray(new String[0]);
    }
}

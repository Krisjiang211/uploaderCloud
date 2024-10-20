package com.xiangyueEducation.uploaderCloud.Utils.BaseUtils;

import java.util.HashMap;
import java.util.Map;

public class MapUtilsJiang {

    /**
     * Description: 合并多个map
     *
     * @Author: Jiang
     * @param: Map[]
     * @return: HashMap<String,String>
     */

    public static <K,V> Map<K,V> getAMap(){
        Map<K, V> map = new HashMap<>();
        return map;
    }

    public static HashMap<String,String> concatMap(Map<String,String>... maps){
        HashMap<String,String> result = new HashMap<>();
        for (Map<String,String> map : maps) {
            result.putAll(map);
        }
        return result;
    }

}

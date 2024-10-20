package com.xiangyueEducation.uploaderCloud.Utils.BaseUtils;


import org.junit.Test;
import com.xiangyueEducation.uploaderCloud.Utils.DateUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class BaseUtilsJiang {


    //某个字符是否在数组中?    模式:遍历
    public Boolean isInArray(String obj,String[] stringArray){
        for (String item:stringArray){
            if (item.equals(obj)){
                return true;
            }
        }
        return false;
    }

    //类中对象全部属性转为map
    public <T> HashMap<String,String> objectToMap(T object){
        HashMap<String,String> result = new HashMap<>();

        Class<?> clazz = object.getClass(); // 获取类
        Field[] fields = clazz.getDeclaredFields(); // 获取所有字段
        for(Field item:fields){
            item.setAccessible(true);
            try {
                if (item.get(object) != null){
                    //如果是Date类型那么就要转换一下
                    if(item.getType()== Date.class){
                        result.put(item.getName(), DateUtils.formatDateToString((Date)item.get(object)));
                        continue;
                    }
                    result.put(item.getName(),item.get(object).toString());
                }
            } catch (IllegalAccessException e) {
                System.out.println("Error Occurred: " + e);
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public <T> HashMap<String,String> objectToMap(T object,String[] excludeProperties){
        HashMap<String, String> result = new HashMap<>();

        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for(Field item:fields){
            item.setAccessible(true);
            try {
                if (item.get(object)!=null){
                    if (!isInArray(item.getName(),excludeProperties)){
                        //如果是Date类型那么就要转换一下
                        if(item.getType()== Date.class){
                            result.put(item.getName(), DateUtils.formatDateToString((Date)item.get(object)));
                            continue;
                        }
                        result.put(item.getName(),item.get(object).toString());
                    }
                }
            } catch (IllegalAccessException e) {
                System.out.println("Error Occurred: " + e);
                throw new RuntimeException(e);
            }
        }
        return result;
    }


    public <T> void  showAllPropertiesConsole(T object){
        Map map=objectToMap(object);
        System.out.println("********如下是这个对象的所有属性********");
        for (Object obj : map.entrySet()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) obj;
            String key = entry.getKey(); // 获取键
            String value = entry.getValue(); // 获取值
            System.out.println("\""+key+"\""+":"+"\""+value+"\"");
        }
    }

    public <T> void  showPartPropertiesConsole(T object,String[] excludeProperties){
        Map map=objectToMap(object,excludeProperties);
        System.out.println("********如下是这个对象的所有属性********");
        for (Object obj : map.entrySet()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) obj;
            String key = entry.getKey(); // 获取键
            String value = entry.getValue(); // 获取值
            System.out.println("\""+key+"\""+":"+"\""+value+"\"");
        }
    }



}

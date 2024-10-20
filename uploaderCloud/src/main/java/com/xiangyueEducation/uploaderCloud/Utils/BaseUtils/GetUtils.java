package com.xiangyueEducation.uploaderCloud.Utils.BaseUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public class GetUtils {

    public static<T> QueryWrapper<T> queryWrapper(T t){
        QueryWrapper<T> wrapper = new QueryWrapper<>(t);
        return wrapper;

    }
}

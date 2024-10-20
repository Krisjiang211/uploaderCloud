package com.xiangyueEducation.uploaderCloud.Controller;


import com.xiangyueEducation.uploaderCloud.Service.funPackage.SearchService;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("search")
public class SearchController {


    @Autowired
    private SearchService searchService;


    @GetMapping("space/file/name")
    public Result searchSelfSpaceFileName(@RequestHeader("token") String token,
                                          @RequestParam("keyWord") String keyWord,
                                          @RequestParam("currentPage") Integer currentPage,
                                          @RequestParam(value = "pageSize",required = false) Integer pageSize){

        return searchService.searchSelfSpaceFileName(token, keyWord,currentPage,pageSize);
    }

    /**
     * 获取搜索关键字的文件的数量
     * @param token
     * @param keyWord
     * @return
     */

    @GetMapping("space/file/num")
    public Result searchSelfSpaceFileNum(@RequestHeader("token") String token,@RequestParam("keyWord") String keyWord){

        return searchService.searchSelfSpaceFileNum(token, keyWord);
    }



}

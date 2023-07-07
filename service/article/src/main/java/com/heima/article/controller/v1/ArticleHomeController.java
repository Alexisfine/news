package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.heima.common.constants.ArticleConstants.*;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleHomeController {
    @Autowired
    private ApArticleService apArticleService;

    /**
     * Load home page
     * @param dto
     * @return
     */
    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto dto) {
        return apArticleService.loadArticleList(dto, LOADTYPE_LOAD_MORE);
    }

    /**
     * Load more
     * @param dto
     * @return
     */
    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto dto) {
        return apArticleService.loadArticleList(dto, LOADTYPE_LOAD_MORE);
    }

    /**
     * Load new
     * @param dto
     * @return
     */
    @PostMapping("/loadnew")
    public ResponseResult loadLatest(@RequestBody ArticleHomeDto dto) {
        return apArticleService.loadArticleList(dto, LOADTYPE_LOAD_NEW);
    }
}

package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

import java.lang.reflect.InvocationTargetException;

public interface ApArticleService extends IService<ApArticle> {
    /**
     * load article list
     * @param dto
     * @param type 1: load more 2: load latest
     * @return
     */
    ResponseResult loadArticleList(ArticleHomeDto dto, Short type);

    /**
     * save app end article
     * @param dto
     * @return
     */
    ResponseResult saveArticle(ArticleDto dto);
}

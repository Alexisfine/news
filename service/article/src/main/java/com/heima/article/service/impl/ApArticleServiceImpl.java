package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.heima.common.constants.ArticleConstants.*;

@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    private static final short MAX_PAGE = 50;

    /**
     * load article list
     *
     * @param dto
     * @param type 1: load more 2: load latest
     * @return
     */
    @Override
    public ResponseResult loadArticleList(ArticleHomeDto dto, Short type) {
        // validate page size
        Integer size = dto.getSize();
        if (size == null || size == 0) size = 10;
        size = Math.min(size, MAX_PAGE);

        // validate type
        if (!type.equals(LOADTYPE_LOAD_MORE) && !type.equals(LOADTYPE_LOAD_NEW)) {
            type = LOADTYPE_LOAD_MORE;
        }

        // validate tag
        if (StringUtils.isBlank(dto.getTag())) {
            dto.setTag(DEFAULT_TAG);
        }

        // validate time
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }

        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }

        // query
        List<ApArticle> articleList = apArticleMapper.loadArticleList(dto, type);

        return ResponseResult.okResult(articleList);
    }

    /**
     * save app end article
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);

        if (dto.getId() == null) {
            // if article doesn't exist, add article
            save(apArticle);

            ApArticleConfig config  = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(config);

            ApArticleContent content = new ApArticleContent();
            content.setContent(dto.getContent());
            content.setArticleId(apArticle.getId());
            apArticleContentMapper.insert(content);

        } else {
            // otherwise, update article
            updateById(apArticle);
            System.out.println(dto);
            ApArticleContent content = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery()
                    .eq(ApArticleContent::getArticleId, dto.getId()));
            content.setContent(dto.getContent());
            apArticleContentMapper.updateById(content);

        }

        return ResponseResult.okResult(apArticle.getId());
    }
}

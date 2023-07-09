package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.heima.apis.article.IArticleClient;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {
    @Autowired
    private WmNewsMapper wmNewsMapper;
    @Autowired
    private GreenTextScan greenTextScan;
    @Autowired
    private GreenImageScan greenImageScan;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private IArticleClient iArticleClient;
    @Autowired
    private WmChannelMapper wmChannelMapper;
    @Autowired
    private WmUserMapper wmUserMapper;

    /**
     * wm article scan
     *
     * @param id
     */
    @Override
    public void autoScanWmNews(Integer id) {
        // search article
        WmNews news = wmNewsMapper.selectById(id);
        if (news == null) throw new RuntimeException("WmNewsAutoScanServiceImpl-news does not exist");


        if (news.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            Map<String, Object> textAndImages = handleTextAndImages(news);
            // scan and verify article
            boolean isTextScan = handleTextScan((String) textAndImages.get("content"), news);

            if (!isTextScan) {
                return;
            }

            // verify image
            boolean isImageScan = handleImageScan((List<String>) textAndImages.get("images"), news);
            if (!isImageScan) {
                return;
            }

            // save/update article info
            ResponseResult result = saveAppArticle(news);
            if (!result.getCode().equals(200)) {
                throw new RuntimeException();
            }
            news.setArticleId((Long) result.getData());
            updateNews(news, (short) 9, "Review success");
        }


    }

    /**
     * save news article
     * @param news
     */
    private ResponseResult saveAppArticle(WmNews news) {
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(news, articleDto);

        articleDto.setLayout(news.getType());

        WmChannel channel = wmChannelMapper.selectById(news.getChannelId());
        if (channel != null) articleDto.setChannelName(channel.getName());

        articleDto.setAuthorId(news.getUserId().longValue());

        WmUser wmUser = wmUserMapper.selectById(news.getUserId());
        if (wmUser != null) {
            articleDto.setAuthorName(wmUser.getName());
        }

        if (news.getArticleId() != null) {
            articleDto.setId(news.getArticleId());
        }

        articleDto.setCreatedTime(new Date());

        ResponseResult result = iArticleClient.saveArticle(articleDto);
        return result;
    }

    private boolean handleImageScan(List<String> images, WmNews news) {
        boolean flag = true;
        if (images == null || images.size() == 0) return flag;

        // download images
        images = images.stream().distinct().collect(Collectors.toList());

        List<byte[]> imageList = new ArrayList<>();
        for (String image : images) {
            byte[] bytes = fileStorageService.downLoadFile(image);
            imageList.add(bytes);
        }

        try {
            Map map = greenImageScan.imageScan(imageList);
            if (map != null) {
                if (map.get("suggestion").equals("block")) {
                    updateNews(news, (short) 2, "article image contains illegal content");
                    flag = false;

                } else if (map.get("suggestion").equals("review")) {
                    updateNews(news, (short) 3, "article image is under review");
                    flag = false;
                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    /**
     *
     * @param content
     * @param news
     * @return
     */
    private boolean handleTextScan(String content, WmNews news) {
        boolean flag = true;

        if (news.getTitle().length() + content.length() == 0) return flag;

        try {
            Map map = greenTextScan.greeTextScan(news.getTitle() + "-" + content);
            if (map != null) {
                if (map.get("suggestion").equals("block")) {
                    updateNews(news, (short) 2, "article contains illegal content");
                    flag = false;

                } else if (map.get("suggestion").equals("review")) {
                    updateNews(news, (short) 3, "article is under review");
                    flag = false;
                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    private void updateNews(WmNews news, short status, String reason) {
        news.setStatus(status);
        news.setReason(reason);
        wmNewsMapper.updateById(news);
    }

    /**
     * get text and images from news
     * get cover image
     * @param news
     * @return
     */
    private Map<String, Object> handleTextAndImages(WmNews news) {
        StringBuilder sb = new StringBuilder();
        List<String> images = new ArrayList<>();

        if (StringUtils.isNotBlank(news.getContent())) {
            List<Map> maps = JSONArray.parseArray(news.getContent(), Map.class);
            for (Map map : maps) {
                if (map.get("type").equals("text")) {
                    sb.append(map.get("value"));
                }

                if (map.get("type").equals("image")) {
                    images.add((String) map.get("value"));
                }
            }
        }

        // get cover image
        if (StringUtils.isNotBlank(news.getImages())) {
            String[] split = news.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("content", new String(sb));
        resMap.put("images", images);
        return resMap;
    }
}

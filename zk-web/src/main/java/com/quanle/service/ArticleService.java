package com.quanle.service;

import com.quanle.pojo.Article;
import org.springframework.data.domain.Page;

/**
 * @author quanle
 */
public interface ArticleService {
    /**
     * 分页查询文章
     *
     * @param pageNo   页码
     * @param pageSize 每页显示条数
     * @return
     */
    Page<Article> findByPage(Integer pageNo, Integer pageSize);
}

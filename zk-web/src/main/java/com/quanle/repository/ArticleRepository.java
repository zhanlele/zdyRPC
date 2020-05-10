package com.quanle.repository;

import com.quanle.pojo.Article;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author quanle
 */
public interface ArticleRepository extends JpaRepository<Article, Integer> {
}

package com.quanle.controller;

import com.quanle.pojo.Article;
import com.quanle.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 文章控制器
 *
 * @author quanle
 */
@Controller
@RequestMapping("")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 默认跳转index页面
     *
     * @return
     */
    @RequestMapping("/")
    public String toIndex() {
        return "forward:index";
    }

    /**
     * 文章首页
     *
     * @param pageNo   页码 默认第一页
     * @param pageSize 每页显示条数 默认3
     * @param model    页码显示
     * @return
     */
    @RequestMapping("/index")
    public String index(@RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                        @RequestParam(name = "pageSize", required = false, defaultValue = "3") Integer pageSize,
                        Model model) {
        pageNo = pageNo == null ? 1 : pageNo;
        pageSize = pageSize == null ? 3 : pageSize;
        Page<Article> articlePage = articleService.findByPage(pageNo, pageSize);
        List<Article> articleList = articlePage.getContent();
        model.addAttribute("articleList", articleList);
        model.addAttribute("total", articlePage.getTotalElements());
        model.addAttribute("pages", articlePage.getTotalPages());
        model.addAttribute("currentPage", articlePage.getPageable().getPageNumber() + 1);
        model.addAttribute("pageSize", pageSize);
        return "index";
    }
}

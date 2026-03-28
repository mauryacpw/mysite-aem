package com.mysite.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.Date;

@Model(
        adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ArticleDetailModel {

    @ValueMapValue
    private String articleImage;

    @ValueMapValue
    private String articleTitle;

    @ValueMapValue
    private String articleDescription;

    @ValueMapValue
    private Date articleDate;

    private String articlePath;

    public String getArticleImage() {
        return articleImage;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleDescription() {
        return articleDescription;
    }

    public Date getArticleDate() {
        return articleDate;
    }

    public String getArticlePath() {
        return articlePath;
    }

    public void setArticlePath(String articlePath) {
        this.articlePath = articlePath+".html";
    }

}
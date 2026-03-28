package com.mysite.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Model(
        adaptables = Resource.class,
        resourceType = "mysite/components/custom-components/article-card-container",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ArticleCardModel {

    private static final String ARTICLE_RESOURCE_TYPE =
            "mysite/components/custom-components/Article-details";

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    private String articleRootPath;

    @ValueMapValue
    private int maxArticles;

    private List<ArticleDetailModel> articleDetails = new ArrayList<>();

    @PostConstruct
    protected void init() {

        if (articleRootPath == null || articleRootPath.isEmpty()) {
            return;
        }

        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager == null) {
            return;
        }

        Page rootPage = pageManager.getPage(articleRootPath);

        if (rootPage == null) {
            return;
        }

        Iterator<Page> childPages = rootPage.listChildren();

        while (childPages.hasNext()) {

            Page childPage = childPages.next();

            Resource contentResource = childPage.getContentResource();

            if (contentResource == null) {
                continue;
            }

            Resource articleComponent = findArticleComponent(contentResource);

            if (articleComponent != null) {

                ArticleDetailModel article =
                        articleComponent.adaptTo(ArticleDetailModel.class);

                if (article != null) {
                    article.setArticlePath(childPage.getPath());
                    articleDetails.add(article);
                }
            }
        }

        articleDetails = articleDetails.stream()
                .sorted(
                        Comparator.comparing(
                                ArticleDetailModel::getArticleDate,
                                Comparator.nullsLast(Date::compareTo)
                        )
                )
                .limit(maxArticles > 0 ? maxArticles : articleDetails.size())
                .collect(Collectors.toList());
    }

    private Resource findArticleComponent(Resource resource) {

        if (resource.isResourceType(ARTICLE_RESOURCE_TYPE)) {
            return resource;
        }

        for (Resource child : resource.getChildren()) {

            Resource found = findArticleComponent(child);

            if (found != null) {
                return found;
            }
        }

        return null;
    }

    public List<ArticleDetailModel> getArticleDetails() {
        return articleDetails;
    }
}
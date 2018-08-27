package com.example.android.extraextranewsapp;

public class Article {
    private String sectionName;
    private String articleTitle;
    private String articleAuthor;
    private String webPubDate;
    private String articleUrl;

    /**
     * Create a new Article object.
     *
     * @param sectionName   is the category of the article, e.g. Sports.
     * @param articleTitle  is the title of the article (may be partial if exceeds 2 lines).
     * @param articleAuthor is the author of the article.
     * @param webPubDate    is the web publication date of the article.
     * @param articleUrl    is the url of the article.
     */

    public Article(String sectionName, String articleTitle, String articleAuthor, String webPubDate,
                   String articleUrl) {
        this.sectionName = sectionName;
        this.articleTitle = articleTitle;
        this.articleAuthor = articleAuthor;
        this.webPubDate = webPubDate;
        this.articleUrl = articleUrl;
    }

    // Get the section, title, author, date, and url of the article.
    public String getSectionName() {
        return sectionName;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleAuthor() {
        return articleAuthor;
    }

    public String getWebPubDate() {
        return webPubDate;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

}

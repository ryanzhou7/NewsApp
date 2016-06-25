package com.ryanzhou.company.newsapp.model;

/**
 * Created by ryanzhou on 6/24/16.
 */
public class Story {
    private final String webTitle;
    private final String webUrl;
    private final String sectionName;
    public static final String PARAM_SECTION_NAME = "sectionName";
    public static final String PARAM_WEB_TITLE = "webTitle";
    public static final String PARAM_WEB_URL = "webUrl";

    public Story(String title, String url, String section) {
        webTitle = title;
        webUrl = url;
        sectionName = section;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getSectionName() {
        return sectionName;
    }
}

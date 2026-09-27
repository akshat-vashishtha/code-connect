package com.codeconnect.curriculum.domain.model;

import java.util.List;

public class StoryContent {
    private String title;
    private String narrative;
    private String realWorldAnalogy;
    private List<String> socraticPrompts;

    public StoryContent() {}

    public StoryContent(String title, String narrative, String realWorldAnalogy, List<String> socraticPrompts) {
        this.title = title;
        this.narrative = narrative;
        this.realWorldAnalogy = realWorldAnalogy;
        this.socraticPrompts = socraticPrompts;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getNarrative() { return narrative; }
    public void setNarrative(String narrative) { this.narrative = narrative; }
    public String getRealWorldAnalogy() { return realWorldAnalogy; }
    public void setRealWorldAnalogy(String realWorldAnalogy) { this.realWorldAnalogy = realWorldAnalogy; }
    public List<String> getSocraticPrompts() { return socraticPrompts; }
    public void setSocraticPrompts(List<String> socraticPrompts) { this.socraticPrompts = socraticPrompts; }

    public static StoryContentBuilder builder() {
        return new StoryContentBuilder();
    }

    public static class StoryContentBuilder {
        private String title;
        private String narrative;
        private String realWorldAnalogy;
        private List<String> socraticPrompts;

        public StoryContentBuilder title(String title) { this.title = title; return this; }
        public StoryContentBuilder narrative(String narrative) { this.narrative = narrative; return this; }
        public StoryContentBuilder realWorldAnalogy(String realWorldAnalogy) { this.realWorldAnalogy = realWorldAnalogy; return this; }
        public StoryContentBuilder socraticPrompts(List<String> socraticPrompts) { this.socraticPrompts = socraticPrompts; return this; }

        public StoryContent build() {
            return new StoryContent(title, narrative, realWorldAnalogy, socraticPrompts);
        }
    }
}

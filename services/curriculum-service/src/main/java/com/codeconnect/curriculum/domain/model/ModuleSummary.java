package com.codeconnect.curriculum.domain.model;

public class ModuleSummary {
    private String id;
    private String title;
    private String slug;
    private Integer sequence;
    private Integer lessonCount;

    public ModuleSummary() {}

    public ModuleSummary(String id, String title, String slug, Integer sequence, Integer lessonCount) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.sequence = sequence;
        this.lessonCount = lessonCount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public Integer getSequence() { return sequence; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public Integer getLessonCount() { return lessonCount; }
    public void setLessonCount(Integer lessonCount) { this.lessonCount = lessonCount; }

    public static ModuleSummaryBuilder builder() {
        return new ModuleSummaryBuilder();
    }

    public static class ModuleSummaryBuilder {
        private String id;
        private String title;
        private String slug;
        private Integer sequence;
        private Integer lessonCount;

        public ModuleSummaryBuilder id(String id) { this.id = id; return this; }
        public ModuleSummaryBuilder title(String title) { this.title = title; return this; }
        public ModuleSummaryBuilder slug(String slug) { this.slug = slug; return this; }
        public ModuleSummaryBuilder sequence(Integer sequence) { this.sequence = sequence; return this; }
        public ModuleSummaryBuilder lessonCount(Integer lessonCount) { this.lessonCount = lessonCount; return this; }

        public ModuleSummary build() {
            return new ModuleSummary(id, title, slug, sequence, lessonCount);
        }
    }
}

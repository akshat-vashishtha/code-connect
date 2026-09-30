package com.codeconnect.curriculum.domain.valueobject;

public class TestCase {
    private String id;
    private String name;
    private String input;
    private String expectedOutput;
    private Boolean isHidden;

    public TestCase() {}

    public TestCase(String id, String name, String input, String expectedOutput, Boolean isHidden) {
        this.id = id;
        this.name = name;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.isHidden = isHidden;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    public Boolean getIsHidden() { return isHidden; }
    public void setIsHidden(Boolean isHidden) { this.isHidden = isHidden; }

    public static TestCaseBuilder builder() {
        return new TestCaseBuilder();
    }

    public static class TestCaseBuilder {
        private String id;
        private String name;
        private String input;
        private String expectedOutput;
        private Boolean isHidden;

        public TestCaseBuilder id(String id) { this.id = id; return this; }
        public TestCaseBuilder name(String name) { this.name = name; return this; }
        public TestCaseBuilder input(String input) { this.input = input; return this; }
        public TestCaseBuilder expectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; return this; }
        public TestCaseBuilder isHidden(Boolean isHidden) { this.isHidden = isHidden; return this; }

        public TestCase build() {
            return new TestCase(id, name, input, expectedOutput, isHidden);
        }
    }
}

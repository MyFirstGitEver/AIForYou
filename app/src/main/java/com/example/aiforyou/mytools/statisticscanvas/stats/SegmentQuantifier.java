package com.example.aiforyou.mytools.statisticscanvas.stats;

public class SegmentQuantifier {
    private final String categoryName;
    private final CategoryQuantifier[] quantifiers;

    public SegmentQuantifier(String categoryName, CategoryQuantifier[] quantifiers) {
        this.categoryName = categoryName;
        this.quantifiers = quantifiers;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public CategoryQuantifier[] getQuantifiers() {
        return quantifiers;
    }
}
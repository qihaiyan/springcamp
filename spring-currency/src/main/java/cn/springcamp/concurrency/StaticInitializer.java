package com.example;

import java.time.Year;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class StaticInitializer {
    public static final Year year = Year.of(2020);
    public static final Set<String> keywords;

    // 用静态初始化方法初始化集合对象 keywords
    static {
        // Creating mutable set
        Set<String> keywordsSet = new HashSet<>(); // Initializing state keywordsSet.add("java"); keywordsSet.add("concurrency");
        // Making set unmodifiable
        keywords = Collections.unmodifiableSet(keywordsSet);
    }
}
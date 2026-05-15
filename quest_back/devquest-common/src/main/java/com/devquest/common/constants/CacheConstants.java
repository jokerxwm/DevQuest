package com.devquest.common.constants;

public class CacheConstants {

    public static final String QUESTION_CACHE_PREFIX = "question:detail:";
    public static final String QUESTION_HOT_KEY = "question:hot";
    public static final String USER_CACHE_PREFIX = "user:info:";
    public static final String VOTE_PREFIX = "vote:";
    public static final String FAVORITE_PREFIX = "favorite:";
    public static final String TAG_HOT_KEY = "tag:hot";

    public static final long QUESTION_CACHE_TTL = 3600;
    public static final long USER_CACHE_TTL = 1800;

    private CacheConstants() {}
}

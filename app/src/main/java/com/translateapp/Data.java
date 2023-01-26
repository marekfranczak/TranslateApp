package com.translateapp;

import android.net.Uri;

public class Data {

    private static String originalText = "";
    private static String translateText = "";
    private static Uri uri = null;
    private static Languages languages;


    public static String getOriginalText() {
            return originalText;
    }

    public static void setOriginalText(String originalText) {
        Data.originalText = originalText;
    }

    public static String getTranslateText() {
        return translateText;
    }

    public static void setTranslateText(String translateText) {
        Data.translateText = translateText;
    }

    public static Uri getUri() {
        return uri;
    }

    public static void setUri(Uri uri) {
        Data.uri = uri;
    }

    public static Languages getLanguages() {
        return languages;
    }

    public static void setLanguages(Languages languages) {
        Data.languages = languages;
    }
}

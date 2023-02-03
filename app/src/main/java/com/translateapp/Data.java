package com.translateapp;

import android.net.Uri;

/**
 * The `Data` class is a static data storage class that holds information used throughout the application.
 * @author Marek Frańczak
 * @since 1.0.0
 */
public class Data {

    /**
     * The original text to translate.
     */
    private static String originalText = "";
    /**
     * The translated text.
     */
    private static String translateText = "";
    /**
     * The URI to the image where the text will be searched for.
     */
    private static Uri uri = null;
    /**
     * The target language to translate.
     */
    private static Languages languages;

    /**
     * Returns the original text.
     *
     * @return The original text.
     */
    public static String getOriginalText() {
            return originalText;
    }

    /**
     * Sets the original text.
     *
     * @param originalText The new original text.
     */
    public static void setOriginalText(String originalText) {
        Data.originalText = originalText;
    }

    /**
     * Returns the translated text.
     *
     * @return The translated text.
     */
    public static String getTranslateText() {
        return translateText;
    }

    /**
     * Sets the translated text.
     *
     * @param translateText The new translated text.
     */
    public static void setTranslateText(String translateText) {
        Data.translateText = translateText;
    }

    /**
     * Returns the URI of an photo file.
     *
     * @return The URI of an photo file.
     */
    public static Uri getUri() {
        return uri;
    }

    /**
     * Sets the URI of an photo file.
     *
     * @param uri The new URI of an photo file.
     */
    public static void setUri(Uri uri) {
        Data.uri = uri;
    }

    /**
     * Returns the target language to translate.
     *
     * @return The target language to translate.
     */
    public static Languages getLanguages() {
        return languages;
    }

    /**
     * Sets the target language to translate.
     *
     * @param languages The target language to translate.
     */
    public static void setLanguages(Languages languages) {
        Data.languages = languages;
    }
}

package com.translateapp;

public enum Languages {

    English("English", "en"),
    Esperanto("Esperanto", "eo"),
    French("French", "fr"),
    German("German", "de"),
    Italian("Italian", "it"),
    Japanese("Japanese", "ja"),
    Latin("Latin", "la"),
    Norwegian("Norwegian", "no"),
    Polish("Polish", "pl"),
    Spanish("Spanish", "es"),
    Swedish("Swedish", "sv"),
    Ukrainian("Ukrainian", "uk"),
    Zulu("Zulu", "zu");

    private String languageName;
    private String languageCode;

    Languages(String languageName, String languageCode) {
        this.languageName = languageName;
        this.languageCode = languageCode;
    }

    public String getLanguageName(){
        return languageName;
    }

    public String getLanguageCode(){
        return languageCode;
    }


}

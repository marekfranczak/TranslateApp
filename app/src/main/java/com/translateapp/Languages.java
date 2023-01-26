package com.translateapp;

public enum Languages {

    English ("en"),
    Esperanto("eo"),
    French("fr"),
    German("de"),
    Italian("it"),
    Japanese("ja"),
    Latin("la"),
    Norwegian("no"),
    Polish("pl"),
    Spanish("es"),
    Swedish("sv"),
    Ukrainian("uk"),
    Zulu("zu");

    private String languageCode;

    Languages(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageCode(){
        return languageCode;
    }


}

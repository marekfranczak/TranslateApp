package com.translateapp;

/**
 * This class is an enumeration of various languages supported by the application.
 * It contains the language code for each language that can be used to translate text.
 * The enum contains the following languages:
 *
 *     English ("en")
 *     Esperanto ("eo")
 *     French ("fr")
 *     German ("de")
 *     Italian ("it")
 *     Japanese ("ja")
 *     Latin ("la")
 *     Norwegian ("no")
 *     Polish ("pl")
 *     Spanish ("es")
 *     Swedish ("sv")
 *     Ukrainian ("uk")
 *     Zulu ("zu")
 * Note: The language codes are based on the ISO 639-1 standard.
 *
 * @author Marek Frańczak
 * @since 1.0.0
 */
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

    /**
     * This method returns the language code for the language represented by this enumeration.
     * @param languageCode The language codes are based on the ISO 639-1 standard.
     */
    Languages(String languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * This is a constructor method which sets the value of the language code for each language in the enumeration.
     * @return The language codes are based on the ISO 639-1 standard.
     */
    public String getLanguageCode(){
        return languageCode;
    }


}

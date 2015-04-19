package blake.com.flashtalk.data;

import java.util.Locale;

/**
 * Used in the Create Deck activity to choose the languages for hints and answers.
 */
public class LocaleSpinnerObject {
    private String displayLocale;
    private Locale valueLocale;

    public LocaleSpinnerObject(String displayLocale, Locale valueLocale){
        this.displayLocale = displayLocale;
        this.valueLocale = valueLocale;
    }

    public String getDisplay(){
        return displayLocale;
    }
    public Locale getValue(){
        return valueLocale;
    }
    @Override
    public String toString(){
        return displayLocale;
    }
}

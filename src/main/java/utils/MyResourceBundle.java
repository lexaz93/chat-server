package utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class MyResourceBundle {
    private final ResourceBundle bundle;
    private final static String BUNDLE_NAME = "localization";

    public MyResourceBundle(Locale locale) {
        bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }

    public String getValue(String key) {
        return  bundle.getString(key);
    }
}

package me.syncwrld.booter.libs.google.kyori.adventure.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public final class UTF8ResourceBundleControl extends ResourceBundle.Control {
  private static final UTF8ResourceBundleControl INSTANCE = new UTF8ResourceBundleControl();
  
  public static ResourceBundle.Control get() {
    return INSTANCE;
  }
  
  public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
    if (format.equals("java.properties")) {
      String bundle = toBundleName(baseName, locale);
      String resource = toResourceName(bundle, "properties");
      InputStream is = null;
      if (reload) {
        URL url = loader.getResource(resource);
        if (url != null) {
          URLConnection connection = url.openConnection();
          if (connection != null) {
            connection.setUseCaches(false);
            is = connection.getInputStream();
          } 
        } 
      } else {
        is = loader.getResourceAsStream(resource);
      } 
      if (is != null) {
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        try {
          PropertyResourceBundle propertyResourceBundle = new PropertyResourceBundle(isr);
          isr.close();
          return propertyResourceBundle;
        } catch (Throwable throwable) {
          try {
            isr.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } 
      return null;
    } 
    return super.newBundle(baseName, locale, format, loader, reload);
  }
}

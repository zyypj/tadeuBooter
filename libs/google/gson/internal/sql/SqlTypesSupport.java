package me.syncwrld.booter.libs.google.gson.internal.sql;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Date;
import me.syncwrld.booter.libs.google.gson.TypeAdapterFactory;
import me.syncwrld.booter.libs.google.gson.internal.bind.DefaultDateTypeAdapter;

public final class SqlTypesSupport {
  public static final boolean SUPPORTS_SQL_TYPES;
  
  public static final DefaultDateTypeAdapter.DateType<? extends Date> DATE_DATE_TYPE;
  
  public static final DefaultDateTypeAdapter.DateType<? extends Date> TIMESTAMP_DATE_TYPE;
  
  public static final TypeAdapterFactory DATE_FACTORY;
  
  public static final TypeAdapterFactory TIME_FACTORY;
  
  public static final TypeAdapterFactory TIMESTAMP_FACTORY;
  
  static {
    boolean sqlTypesSupport;
    try {
      Class.forName("java.sql.Date");
      sqlTypesSupport = true;
    } catch (ClassNotFoundException classNotFoundException) {
      sqlTypesSupport = false;
    } 
    SUPPORTS_SQL_TYPES = sqlTypesSupport;
    if (SUPPORTS_SQL_TYPES) {
      DATE_DATE_TYPE = (DefaultDateTypeAdapter.DateType)new DefaultDateTypeAdapter.DateType<Date>(Date.class) {
          protected Date deserialize(Date date) {
            return new Date(date.getTime());
          }
        };
      TIMESTAMP_DATE_TYPE = (DefaultDateTypeAdapter.DateType)new DefaultDateTypeAdapter.DateType<Timestamp>(Timestamp.class) {
          protected Timestamp deserialize(Date date) {
            return new Timestamp(date.getTime());
          }
        };
      DATE_FACTORY = SqlDateTypeAdapter.FACTORY;
      TIME_FACTORY = SqlTimeTypeAdapter.FACTORY;
      TIMESTAMP_FACTORY = SqlTimestampTypeAdapter.FACTORY;
    } else {
      DATE_DATE_TYPE = null;
      TIMESTAMP_DATE_TYPE = null;
      DATE_FACTORY = null;
      TIME_FACTORY = null;
      TIMESTAMP_FACTORY = null;
    } 
  }
}

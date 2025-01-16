package me.syncwrld.booter.libs.hikari;

import java.sql.SQLException;

public interface SQLExceptionOverride {
  public enum Override {
    CONTINUE_EVICT, DO_NOT_EVICT;
  }
  
  default Override adjudicate(SQLException sqlException) {
    return Override.CONTINUE_EVICT;
  }
}

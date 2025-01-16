package me.syncwrld.booter.libs.hikari;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import me.syncwrld.booter.libs.hikari.util.PropertyElf;
import me.syncwrld.booter.libs.javax.naming.Context;
import me.syncwrld.booter.libs.javax.naming.InitialContext;
import me.syncwrld.booter.libs.javax.naming.Name;
import me.syncwrld.booter.libs.javax.naming.NamingException;
import me.syncwrld.booter.libs.javax.naming.RefAddr;
import me.syncwrld.booter.libs.javax.naming.Reference;
import me.syncwrld.booter.libs.javax.naming.spi.ObjectFactory;
import me.syncwrld.booter.libs.javax.sql.DataSource;

public class HikariJNDIFactory implements ObjectFactory {
  public synchronized Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
    if (obj instanceof Reference && "me.syncwrld.booter.libs.javax.sql.DataSource".equals(((Reference)obj).getClassName())) {
      Reference ref = (Reference)obj;
      Set<String> hikariPropSet = PropertyElf.getPropertyNames(HikariConfig.class);
      Properties properties = new Properties();
      Enumeration<RefAddr> enumeration = ref.getAll();
      while (enumeration.hasMoreElements()) {
        RefAddr element = enumeration.nextElement();
        String type = element.getType();
        if (type.startsWith("dataSource.") || hikariPropSet.contains(type))
          properties.setProperty(type, element.getContent().toString()); 
      } 
      return createDataSource(properties, nameCtx);
    } 
    return null;
  }
  
  private DataSource createDataSource(Properties properties, Context context) throws NamingException {
    String jndiName = properties.getProperty("dataSourceJNDI");
    if (jndiName != null)
      return lookupJndiDataSource(properties, context, jndiName); 
    return new HikariDataSource(new HikariConfig(properties));
  }
  
  private DataSource lookupJndiDataSource(Properties properties, Context context, String jndiName) throws NamingException {
    if (context == null)
      throw new RuntimeException("JNDI context does not found for dataSourceJNDI : " + jndiName); 
    DataSource jndiDS = (DataSource)context.lookup(jndiName);
    if (jndiDS == null) {
      InitialContext initialContext = new InitialContext();
      jndiDS = (DataSource)initialContext.lookup(jndiName);
      initialContext.close();
    } 
    if (jndiDS != null) {
      HikariConfig config = new HikariConfig(properties);
      config.setDataSource(jndiDS);
      return new HikariDataSource(config);
    } 
    return null;
  }
}

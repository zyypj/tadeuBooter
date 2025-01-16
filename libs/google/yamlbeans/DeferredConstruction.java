package me.syncwrld.booter.libs.google.yamlbeans;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

class DeferredConstruction {
  private final Constructor constructor;
  
  private final String[] parameterNames;
  
  private final ParameterValue[] parameterValues;
  
  private final List<PropertyValue> propertyValues = new ArrayList<PropertyValue>(16);
  
  public DeferredConstruction(Constructor constructor, String[] parameterNames) {
    this.constructor = constructor;
    this.parameterNames = parameterNames;
    this.parameterValues = new ParameterValue[parameterNames.length];
  }
  
  public Object construct() throws InvocationTargetException {
    try {
      Object object, parameters[] = new Object[this.parameterValues.length];
      int i = 0;
      boolean missingParameter = false;
      for (ParameterValue parameter : this.parameterValues) {
        if (parameter == null) {
          missingParameter = true;
        } else {
          parameters[i++] = parameter.value;
        } 
      } 
      if (missingParameter) {
        try {
          object = this.constructor.getDeclaringClass().getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception ex) {
          throw new InvocationTargetException(new YamlException("Missing constructor property: " + this.parameterNames[i]));
        } 
      } else {
        object = this.constructor.newInstance(parameters);
      } 
      for (PropertyValue propertyValue : this.propertyValues) {
        if (propertyValue.value != null)
          propertyValue.property.set(object, propertyValue.value); 
      } 
      return object;
    } catch (Exception ex) {
      throw new InvocationTargetException(ex, "Error constructing instance of class: " + this.constructor
          .getDeclaringClass().getName());
    } 
  }
  
  public void storeProperty(Beans.Property property, Object value) {
    int index = 0;
    for (String name : this.parameterNames) {
      if (property.getName().equals(name)) {
        ParameterValue parameterValue = new ParameterValue();
        parameterValue.value = value;
        this.parameterValues[index] = parameterValue;
        return;
      } 
      index++;
    } 
    PropertyValue propertyValue = new PropertyValue();
    propertyValue.property = property;
    propertyValue.value = value;
    this.propertyValues.add(propertyValue);
  }
  
  public boolean hasParameter(String name) {
    for (String s : this.parameterNames) {
      if (s.equals(name))
        return true; 
    } 
    return false;
  }
  
  static class PropertyValue {
    Beans.Property property;
    
    Object value;
  }
  
  static class ParameterValue {
    Object value;
  }
}

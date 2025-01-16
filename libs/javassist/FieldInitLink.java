package me.syncwrld.booter.libs.javassist;

class FieldInitLink {
  FieldInitLink next;
  
  CtField field;
  
  CtField.Initializer init;
  
  FieldInitLink(CtField f, CtField.Initializer i) {
    this.next = null;
    this.field = f;
    this.init = i;
  }
}

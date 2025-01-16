package me.syncwrld.booter.libs.reflections.serializers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.syncwrld.booter.libs.reflections.Reflections;
import me.syncwrld.booter.libs.reflections.ReflectionsException;
import me.syncwrld.booter.libs.reflections.Store;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlSerializer implements Serializer {
  public Reflections read(InputStream inputStream) {
    try {
      Document document = (new SAXReader()).read(inputStream);
      Map<String, Map<String, Set<String>>> storeMap = (Map<String, Map<String, Set<String>>>)document.getRootElement().elements().stream().collect(Collectors.toMap(Node::getName, index -> (Map)index.elements().stream().collect(Collectors.toMap((), ()))));
      return new Reflections(new Store(storeMap));
    } catch (Exception e) {
      throw new ReflectionsException("could not read.", e);
    } 
  }
  
  public File save(Reflections reflections, String filename) {
    File file = Serializer.prepareFile(filename);
    try (FileOutputStream out = new FileOutputStream(file)) {
      (new XMLWriter(out, OutputFormat.createPrettyPrint()))
        .write(createDocument(reflections.getStore()));
    } catch (Exception e) {
      throw new ReflectionsException("could not save to file " + filename, e);
    } 
    return file;
  }
  
  private Document createDocument(Store store) {
    Document document = DocumentFactory.getInstance().createDocument();
    Element root = document.addElement("Reflections");
    store.forEach((index, map) -> {
          Element indexElement = root.addElement(index);
          map.forEach(());
        });
    return document;
  }
}

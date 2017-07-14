import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.media.Media;
import org.praisenter.slide.BasicSlide;
import org.praisenter.xml.XmlIO;

public class TextJaxb {
	@XmlRootElement(name = "c")
	@XmlAccessorType(XmlAccessType.NONE)
	@XmlSeeAlso(value = {
		T.class,
		ArrayList.class
	})
	public static class C {
		@XmlElement(name="entry")
		public Map<String, Object> items = new HashMap<>();
	}
	
	public static class T {
		@XmlElement(name="name")
		public String name;
	}
	
	public static void main(String[] args) throws JAXBException, IOException {
		C c = new C();
		T t = new T();
		t.name = "test2";
		ArrayList<T> array = new ArrayList<T>();
		array.add(t);
		
		c.items.put("test", t);
		c.items.put("array", array);
		
		String data = XmlIO.save(c);
		
		System.out.println(data);
		
		C c2 = XmlIO.read(data, C.class);
		
	}
}

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.xml.XmlIO;

public class TextJaxb {
	public static void main(String[] args) throws JAXBException, IOException {
		Temp t = new Temp();
		t.test.put("int", 1);
		t.test.put("double", 12.3);
		t.test.put("short", (short)1);
		t.test.put("byte", (byte)1);
		t.test.put("float", (float)1);
		t.test.put("string", "meh");
		t.test.put("uuid", UUID.randomUUID());
		t.test.put("man", new Man());
		
		Path path = Paths.get("C:\\Users\\wbittle\\Desktop\\test.xml");
		XmlIO.save(path, t);
		Temp temp = XmlIO.read(path, Temp.class);
		
		System.out.println("stop");
	}
	
	@XmlRootElement
	@XmlSeeAlso({
		Man.class
	})
	public static final class Temp {
		@XmlElement
		public HashMap<String, Object> test = new HashMap<>();
	}
	
	@XmlRootElement
	public static final class Man {
		@XmlElement
		public String name = "The";
	}
}

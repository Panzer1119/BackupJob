import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;

public class BackupTest {
    
    public static final void main(String[] args) throws Exception {
        System.out.println("xD");
        final File file = new File("src/test/resources/test.xml");
        final SAXBuilder saxBuilder = new SAXBuilder();
        final Document document = saxBuilder.build(file);
        final Element element_root = document.getRootElement();
        System.out.println(element_root);
        for (Element element : element_root.getChildren()) {
            System.out.println("    " + element);
        }
    }
    
}

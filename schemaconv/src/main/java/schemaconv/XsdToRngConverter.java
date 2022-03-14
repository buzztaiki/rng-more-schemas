package schemaconv;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.writer.relaxng.RELAXNGWriter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class XsdToRngConverter implements Converter {
    @Override
    public void convert(Path ipath, Path opath) {
        try {
            final GrammarLoader grammarLoader = new GrammarLoader();
            final Grammar grammar = grammarLoader.parse(new InputSource(Files.newInputStream(ipath)));

            try (OutputStream out = Files.newOutputStream(opath)) {

                @SuppressWarnings("deprecation") final org.apache.xml.serialize.OutputFormat outputFormat = new org.apache.xml.serialize.OutputFormat("xml", null, true);
                @SuppressWarnings("deprecation") final org.apache.xml.serialize.XMLSerializer serializer = new org.apache.xml.serialize.XMLSerializer(out, outputFormat);

                RELAXNGWriter writer = new RELAXNGWriter();
                writer.setDocumentHandler(serializer);
                writer.write(grammar);
            }
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

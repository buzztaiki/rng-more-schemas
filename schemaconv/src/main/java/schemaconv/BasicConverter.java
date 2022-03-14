package schemaconv;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.input.InputFailedException;
import com.thaiopensource.relaxng.input.InputFormat;
import com.thaiopensource.relaxng.output.LocalOutputDirectory;
import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.relaxng.output.OutputFailedException;
import com.thaiopensource.relaxng.output.OutputFormat;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;

public class BasicConverter implements Converter {

    @Override
    public void convert(Path ipath, Path opath) {
        final FileType itype = FileType.of(ipath);
        final FileType otype = FileType.of(opath);

        try {
            final InputFormat inputFormat = itype.getInputFormat();
            final ErrorHandlerImpl eh = new ErrorHandlerImpl();
            final SchemaCollection sc = inputFormat.load(ipath.toUri().toString(), new String[0], otype.name(), eh, null);
            final OutputFormat outputFormat = otype.getOutputFormat();
            final OutputDirectory outputDirectory = new LocalOutputDirectory(sc.getMainUri(), opath.toFile(), otype.name(), "UTF-8", 80, 2);
            outputFormat.output(sc, outputDirectory, new String[0], itype.name(), eh);
        } catch (SAXException | IOException | InputFailedException | OutputFailedException | InvalidParamsException e) {
            throw new RuntimeException(e);
        }
    }
}

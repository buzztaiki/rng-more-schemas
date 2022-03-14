package schemaconv;

import com.thaiopensource.relaxng.input.InputFormat;
import com.thaiopensource.relaxng.input.dtd.DtdInputFormat;
import com.thaiopensource.relaxng.input.parse.compact.CompactParseInputFormat;
import com.thaiopensource.relaxng.input.parse.sax.SAXParseInputFormat;
import com.thaiopensource.relaxng.output.OutputFormat;
import com.thaiopensource.relaxng.output.dtd.DtdOutputFormat;
import com.thaiopensource.relaxng.output.rnc.RncOutputFormat;
import com.thaiopensource.relaxng.output.rng.RngOutputFormat;
import com.thaiopensource.relaxng.output.xsd.XsdOutputFormat;

import java.nio.file.Path;
import java.util.Optional;

public enum FileType {
    RNC(new CompactParseInputFormat(), new RncOutputFormat()),
    RNG(new SAXParseInputFormat(), new RngOutputFormat()),
    DTD(new DtdInputFormat(), new DtdOutputFormat()),
    XSD(null, new XsdOutputFormat()) {
        @Override
        public Converter converter(FileType outputFormat) {
            return new XsdToRngConverter().andThen(RNG.converter(outputFormat));
        }
    };

    private final InputFormat inputFormat;
    private final OutputFormat outputFormat;

    FileType(InputFormat inputFormat, OutputFormat outputFormat) {
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
    }

    public static FileType of(Path path) throws IllegalArgumentException {
        final String fileName = path.getFileName().toString();
        final int i = fileName.lastIndexOf('.');
        if (i == -1) {
            throw new IllegalArgumentException("invalid file name: " + path);
        }

        return valueOf(fileName.substring(i + 1).toUpperCase());
    }

    public InputFormat getInputFormat() {
        return inputFormat;
    }

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public Converter converter(FileType outputFormat) {
        return new BasicConverter();
    }
}

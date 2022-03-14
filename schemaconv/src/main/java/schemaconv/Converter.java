package schemaconv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Converter {
    void convert(Path ipath, Path opath);

    default Converter andThen(Converter after) {
        return (ipath, opath) -> {
            try {
                final Path temp = Files.createTempFile("schemaconv-", ".rng");
                temp.toFile().deleteOnExit();
                convert(ipath, temp);
                after.convert(temp, opath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}

package schemaconv;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(final String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("usage: java -jar schemaconv.jar <input> <output>");
            System.exit(1);
        }

        final Path ipath = Paths.get(args[0]);
        final Path opath = Paths.get(args[1]);

        FileType.of(ipath).converter(FileType.of(opath)).convert(ipath, opath);
    }
}

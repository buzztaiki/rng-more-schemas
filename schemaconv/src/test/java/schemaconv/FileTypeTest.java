package schemaconv;

import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class FileTypeTest {
    @Test
    public void of() throws Exception {
        assertEquals(FileType.RNG, FileType.of(Paths.get("a.rng")));
        assertEquals(FileType.RNG, FileType.of(Paths.get(".rng")));
        assertEquals(FileType.RNG, FileType.of(Paths.get("A.RNG")));
    }

    @Test
    public void ofAllTypes() throws Exception {
        for (FileType ft :
                FileType.values()) {
            assertEquals(ft, FileType.of(Paths.get("a." + ft.name().toLowerCase())));
        }
    }

    @Test(expected = RuntimeException.class)
    public void ofUnknownType() throws Exception {
        FileType.of(Paths.get("a.zip"));
    }
}
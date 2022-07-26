package Props;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class DelayProperties {
    private final String config;
    public DelayProperties(String configPath){
        config = configPath;
    }
    public Properties build() throws IOException {
        if (!Files.exists(Paths.get(config))) {
            throw new IOException(config + " not found.");
        }
        final Properties cfg = new Properties();
        try (InputStream inputStream = new FileInputStream(config)) {
            cfg.load(inputStream);
        }
        return cfg;
    }
}

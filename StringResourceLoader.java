import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;

public class SecretsResourceLoader {
	
	private String secretsDirectory;
	private Map<String, String> keys;
	
	private static Logger logger = Logger.getLogger(SecretsResourceLoader.class); 

	
	public SecretsResourceLoader(String secretsDirectory) {
		this.secretsDirectory = secretsDirectory;
		File secretFolder = new File(secretsDirectory);
		if (secretFolder.exists()) {
			keys = new HashMap<>();
			this.loadAllFiles(keys);
		}
	}
	
	public String getSecret(String key) {
		return keys.get(key);
	}
	
	private void loadAllFiles(Map<String, String> keys){
		try(Stream<Path> paths = Files.walk(Paths.get(secretsDirectory))){
			paths
				.filter(Files::isRegularFile)
				.forEach(filePath -> {
					try {
						keys.put(filePath.getFileName().toString(), new String(FileCopyUtils.copyToByteArray(filePath.toFile()), StandardCharsets.UTF_8));
					} catch (IOException e) {
						logger.error(e);
					}
				});
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public String getSecretsDirectory() {
		return secretsDirectory;
	}

	public void setSecretsDirectory(String secretsDirectory) {
		this.secretsDirectory = secretsDirectory;
	}
}


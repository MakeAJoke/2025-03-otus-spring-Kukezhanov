package ru.otus.hw.datasource;

import org.springframework.stereotype.Component;
import ru.otus.hw.service.LocalizedIOService;

import java.io.InputStream;

@Component
public class ResourceFileDataSource implements FileDataSource {
    private final LocalizedIOService ioService;

    public ResourceFileDataSource(LocalizedIOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public InputStream getInputStream(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException(
                    ioService.getMessage("FileDataSource.fileNotFound.message", fileName)
            );
        }
        return inputStream;
    }
}

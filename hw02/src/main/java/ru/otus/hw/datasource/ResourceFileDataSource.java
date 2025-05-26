package ru.otus.hw.datasource;

import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class ResourceFileDataSource implements FileDataSource {

    @Override
    public InputStream getInputStream(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("file with questions not found");
        }
        return inputStream;
    }
}

package ru.otus.hw.datasource;

import java.io.InputStream;

public interface FileDataSource {
    InputStream getInputStream(String filename);
}

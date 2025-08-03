package ru.otus.hw.services.caches;

public interface CacheService<T> {

    public T get(long key);

    public void put(long key, T value);

}

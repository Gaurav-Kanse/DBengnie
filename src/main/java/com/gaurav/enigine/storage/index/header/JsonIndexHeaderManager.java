package com.gaurav.enigine.storage.index.header;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import lombok.SneakyThrows;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonIndexHeaderManager extends InMemoryIndexHeaderManager {

    private final Path path;
    private final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().serializeNulls().create();

    public JsonIndexHeaderManager(Path path) throws IOException {
        super(loadHeader(path));
        this.path = path;
        if (!Files.exists(path)) {
            this.write();
        }
    }

    private static Header loadHeader(Path path) throws IOException {
        if (!Files.exists(path)) {
            return new Header();
        }
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().serializeNulls().create();
        try (JsonReader jsonReader = new JsonReader(new FileReader(path.toFile()))) {
            Header header = gson.fromJson(jsonReader, Header.class);
            return header == null ? new Header() : header;
        }
    }

    private synchronized void write() throws IOException {
        try (FileWriter writer = new FileWriter(this.path.toFile())) {
            gson.toJson(this.header, writer);
            writer.flush();
        } catch (FileNotFoundException e) {
            if (!this.path.toFile().createNewFile()) {
                throw e;
            }
            this.write();
        }
    }

    @Override
    public synchronized void setRootOfIndex(int indexId, Location location) throws IOException {
        super.setRootOfIndex(indexId, location);
        this.write();
    }

    @Override
    public synchronized void setIndexBeginningInChunk(int indexId, Location location) throws IOException {
        super.setIndexBeginningInChunk(indexId, location);
        this.write();
    }

    @Override
    public synchronized void setNullBitmapLocation(int indexId, Location location) throws IOException {
        super.setNullBitmapLocation(indexId, location);
        this.write();
    }

    public static class SingletonFactory extends IndexHeaderManagerSingletonFactory {

        @Override
        @SneakyThrows
        public IndexHeaderManager create(Path path) {
            return new JsonIndexHeaderManager(path);
        }
    }

}

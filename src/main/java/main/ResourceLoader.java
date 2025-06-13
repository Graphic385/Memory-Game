package main;

import static com.raylib.Raylib.LoadImageFromMemory;
import static com.raylib.Raylib.LoadTextureFromImage;

import java.io.IOException;
import java.io.InputStream;

import com.raylib.Raylib.Texture;

public class ResourceLoader {
    public static InputStream loadResource(String path) {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(path);
    }

    public static Texture loadTexture(String imageType, String path) {
        try {
            byte[] data = ResourceLoader.loadResource(path).readAllBytes();
            return LoadTextureFromImage(LoadImageFromMemory(imageType, data, data.length));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package de.corneliusmay.silkspawners.plugin.locale;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocaleHandler {

    private static final String DEFAULT_MESSAGE = "§cNo value found for key {0} using locale {1}";

    private final Locale locale;

    @Getter
    private ResourceBundle resourceBundle;

    public LocaleHandler(Locale locale) {
        this.locale = locale;
        File localePath = new File(SilkSpawners.getInstance().getDataFolder() + "/locale");

        try {
            copyDefaultLocales();
            URL[] urls = {localePath.toURI().toURL()};
            ClassLoader loader = new URLClassLoader(urls);
            this.resourceBundle = ResourceBundle.getBundle("messages", locale, loader);
        } catch(MissingResourceException | URISyntaxException | IOException ex) {
            SilkSpawners.getInstance().getLog().error("Error loading locale file", ex);
            SilkSpawners.getInstance().getLog().warn("Disabling plugin due to missing locale file");
            SilkSpawners.getInstance().getPluginLoader().disablePlugin(SilkSpawners.getInstance());
        }
    }

    private void copyDefaultLocales() throws URISyntaxException, IOException {
        Path target = Paths.get(SilkSpawners.getInstance().getDataFolder() + "/locale");
        URI resource = getClass().getResource("").toURI();
        FileSystem fileSystem = FileSystems.newFileSystem(resource, Collections.<String, String>emptyMap());
        final Path jarPath = fileSystem.getPath("/locales");

        Files.walkFileTree(jarPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(target.resolve(jarPath.relativize(dir).toString()));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = target.resolve(jarPath.relativize(file).toString());
                if(Files.notExists(targetFile)) Files.copy(file, targetFile);
                return FileVisitResult.CONTINUE;
            }
        });
        fileSystem.close();
    }

    public String getMessage(String key, Object... args) {
        try {
            return getPrefix() + "§f " + MessageFormat.format(resourceBundle.getString(key), args);
        } catch (MissingResourceException ex) {
            return String.format(DEFAULT_MESSAGE, key, locale.toString());
        }
    }

    public static String getPrefix() {
        return SilkSpawners.getInstance().getPluginConfig().getPrefix();
    }
}

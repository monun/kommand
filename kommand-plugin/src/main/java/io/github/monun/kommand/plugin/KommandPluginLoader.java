package io.github.monun.kommand.plugin;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import kotlin.KotlinVersion;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.jetbrains.annotations.NotNull;


public class KommandPluginLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {

        // check kommand
        if (isRequire("io.github.monun.kommand.Kommand")) {
            var pluginMeta = classpathBuilder.getContext().getConfiguration();
            MavenLibraryResolver resolver = new MavenLibraryResolver();

            if (isReobf()) {
                resolver.addDependency(new Dependency(new DefaultArtifact("io.github.monun:kommand:" + pluginMeta.getVersion()), null));
            } else {
                resolver.addDependency(new Dependency(new DefaultArtifact("io.github.monun:kommand:" + pluginMeta.getVersion() + ":dev"), null));
            }

            // check kotlin is shadowed
            if (isRequire("kotlin.KotlinVersion")) {
                resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib:1.8.22"), null));
            }
            // check kotlin reflect is shadowed
            if (isRequire("kotlin.reflect.KClass")) {
                resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-reflect:1.8.22"), null));
            }

            classpathBuilder.addLibrary(resolver);
        }
    }

    private boolean isRequire(String name) {
        try {
            Class.forName(name, false, getClass().getClassLoader());
            return false;
        } catch (ClassNotFoundException ignored) {
            return true;
        }
    }

    private boolean isReobf() {
        try {
            var bukkitServer = Bukkit.getServer();
            var minecraftServer = bukkitServer.getClass().getMethod("getHandle").invoke(bukkitServer);
            var fieldServer = minecraftServer.getClass().getDeclaredFields()[0];

            System.out.println(fieldServer.getName());

            return fieldServer.getName().equals("SERVER");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}

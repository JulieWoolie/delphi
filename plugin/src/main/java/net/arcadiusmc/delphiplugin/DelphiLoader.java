package net.arcadiusmc.delphiplugin;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

public class DelphiLoader implements PluginLoader {

  @Override
  public void classloader(PluginClasspathBuilder classpathBuilder) {
    MavenLibraryResolver library = new MavenLibraryResolver();

    RemoteRepository repository = new RemoteRepository
        .Builder("central", "default", "https://repo1.maven.org/maven2/")
        .build();

    Dependency js = makeDependency("org.graalvm.js:js-language:24.1.2");

    library.addRepository(repository);
    library.addDependency(js);

    classpathBuilder.addLibrary(library);
  }

  private Dependency makeDependency(String coords) {
    Dependency dependency = new Dependency(new DefaultArtifact(coords), null);
    return dependency;
  }
}

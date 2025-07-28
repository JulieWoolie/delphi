package com.juliewoolie.delphiplugin;

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
        .Builder("central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR)
        .build();

    Dependency js = makeDependency("org.graalvm.js:js-language:24.1.2");
    Dependency tagsoup = makeDependency("org.ccil.cowan.tagsoup:tagsoup:1.2.1");

    library.addRepository(repository);
    library.addDependency(js);
    library.addDependency(tagsoup);

    classpathBuilder.addLibrary(library);
  }

  private Dependency makeDependency(String coords) {
    Dependency dependency = new Dependency(new DefaultArtifact(coords), null);
    return dependency;
  }
}

package technology.mainthread.apps.isitup.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import technology.mainthread.apps.isitup.IsItUpApp;
import technology.mainthread.apps.isitup.injection.graph.IsItUpGraph;
import technology.mainthread.apps.isitup.injection.module.DatabaseModule;
import technology.mainthread.apps.isitup.injection.module.IsItUpAppModule;
import technology.mainthread.apps.isitup.injection.module.NetworkModule;

@Singleton
@Component(modules = {IsItUpAppModule.class, NetworkModule.class, DatabaseModule.class})
public interface AppComponent extends IsItUpGraph {

    final class Initializer {
        public static AppComponent init(IsItUpApp app) {
            return DaggerAppComponent.builder()
                    .isItUpAppModule(new IsItUpAppModule(app))
                    .networkModule(new NetworkModule(app.getResources()))
                    .databaseModule(new DatabaseModule(app))
                    .build();
        }

        private Initializer() {
        } // No instances.
    }

}

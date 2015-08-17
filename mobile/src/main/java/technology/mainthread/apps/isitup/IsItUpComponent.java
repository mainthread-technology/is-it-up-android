package technology.mainthread.apps.isitup;

import javax.inject.Singleton;

import dagger.Component;
import technology.mainthread.apps.isitup.data.db.DatabaseModule;
import technology.mainthread.apps.isitup.data.network.NetworkModule;

@Singleton
@Component(modules = {IsItUpAppModule.class, NetworkModule.class, DatabaseModule.class})
public interface IsItUpComponent extends IsItUpGraph {

    final class Initializer {
        public static IsItUpComponent init(IsItUpApp app) {
            return DaggerIsItUpComponent.builder()
                    .isItUpAppModule(new IsItUpAppModule(app))
                    .networkModule(new NetworkModule(app.getResources()))
                    .databaseModule(new DatabaseModule(app))
                    .build();
        }

        private Initializer() {
        } // No instances.
    }

}

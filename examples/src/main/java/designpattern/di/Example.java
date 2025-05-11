package designpattern.di;

import com.google.inject.*;

public class Example {
    public static void main(String[] args) {
        // manual instantiation
        KlassA a = new KlassA(1);
        KlassB b = new KlassB(a);
        KlassC c = new KlassC(); // new KlassC(a, b);
        c.describe();

        // DI-based
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Provides
            @Singleton
            KlassA providesKlassA() {
                return new KlassA(1);
            }

            @Override
            protected void configure() {
                bind(Itf.class).to(KlassC.class);
                // bind(KlassA.class).toProvider(() -> new KlassA(1));
            }
        });

        // instantiate class
        KlassB kb = injector.getInstance(KlassB.class);

        // interfaces need to be bound to class
        Itf kc = injector.getInstance(KlassC.class);
        kc.describe();
    }
}

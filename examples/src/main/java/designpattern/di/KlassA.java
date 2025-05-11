package designpattern.di;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class KlassA {
    @Inject
    public KlassA(int i) {

    }
}

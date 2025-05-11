package designpattern.di;

import jakarta.inject.Inject;

public class KlassB {
    private KlassA ka;

    @Inject
    public KlassB(KlassA ka) {
        this.ka = ka;
    }

    @Override
    public String toString() {
        return super.toString() + " ka=" + ka;
    }
}

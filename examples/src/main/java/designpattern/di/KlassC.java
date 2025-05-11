package designpattern.di;

import com.google.inject.Inject;

public class KlassC implements Itf {
    @Inject private KlassA ka;
    @Inject private KlassB kb;

//    @Inject
//    public KlassC(KlassA ka, KlassB kb) {
//        this.ka = ka;
//        this.kb = kb;
//    }

    @Override
    public void describe() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return super.toString() + " ka=" + ka + " kb=" + kb;
    }
}

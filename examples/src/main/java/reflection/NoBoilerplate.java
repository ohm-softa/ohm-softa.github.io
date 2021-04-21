package reflection;

import lombok.*;

@Data(staticConstructor = "of")
public class NoBoilerplate {

    @NonNull @Getter @Setter(AccessLevel.PACKAGE)
    private String name;

    public static void main(String[] args) {
        NoBoilerplate nb = new NoBoilerplate("hans");

        nb.setName("Hans");
        System.out.println(nb);

        System.out.println(NoBoilerplate.of("muh"));
    }
}

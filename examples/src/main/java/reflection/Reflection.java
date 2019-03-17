package reflection;

import java.lang.reflect.*;
import java.util.Arrays;

public class Reflection {
	int a;

	public static void main(String[] args) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
		Class<String> klass1 = String.class;                // at compile time
		Class<? extends String> klass2 = "Hello, World!".getClass();  // at runtime
		Class<?> klass3 = Class.forName("java.lang.String");
		Class<String> klass4 = (Class<String>) new String().getClass();

		System.out.println(klass1.toString());
		System.out.println(klass2.toString());
		System.out.println(klass3.toString());
		System.out.println(klass4.toString());
		System.out.println(Reflection.class.getName());
		System.out.println(Reflection.class.getSimpleName());



		System.out.println(int.class.isPrimitive());
		System.out.println(String[].class.isArray());
		System.out.println(Comparable.class.isInterface());
		System.out.println((new Object() {}).getClass().isAnonymousClass());
		System.out.println(Deprecated.class.isAnnotation());

		System.out.println(String.class.getSuperclass());
		System.out.println(String.class.getMethod("charAt", int.class));
		System.out.println(String.class.getField("CASE_INSENSITIVE_ORDER"));
		System.out.println(String.class.getAnnotation(Deprecated.class));
		System.out.println(String.class.getAnnotationsByType(Deprecated.class));

		System.out.println(Arrays.toString(Reflection.class.getFields()));
		System.out.println(Arrays.toString(Reflection.class.getDeclaredFields()));

		Class<?> klass5 = "".getClass();
		String magic = (String) klass5.newInstance();

		Constructor<String> cons = (Constructor<String>) "".getClass().getConstructor(byte[].class);
		String crazy = cons.newInstance(new byte[] {1, 2, 3, 4});


		Imp imp = new Imp();

		// imp.guess(???);
		for (Field f : Imp.class.getDeclaredFields()) {
			f.setAccessible(true);
			if (Modifier.isPrivate(f.getModifiers())
					&& f.getType() == String.class) {
				System.out.println(f.get(imp));
			}
		}

		String wish = "Peter";
		for (Field f : Imp.class.getDeclaredFields()) {
			if (!f.isAccessible())
				f.setAccessible(true);

			if (Modifier.isPrivate(f.getModifiers()) && f.getType() == String.class) {
				f.set(imp, wish);
			}
		}
		System.out.println(imp.guess(wish));

		Weirdo weirdo = new Weirdo();
		for (Method m : weirdo.getClass().getDeclaredMethods()) {
			m.setAccessible(true);
			System.out.println(m);
			if (m.getReturnType() == boolean.class
					&& m.getParameterCount() == 1
					&& m.getParameterTypes()[0] == String.class) {
				System.out.println("meh" + m.invoke(weirdo, Weirdo.name));
			}
		}
	}
}

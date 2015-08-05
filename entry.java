import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

class entry {

public static void NSLog(String format, Object... args) {
	format += "\n";
	System.out.printf(format, args);
}
public static void main(String[] args) {
	ask();
}
public static void ask() {
	Set<SimpleEntry<Set<String>, Runnable>> poolRem =
		new HashSet<>();

	Set<SimpleEntry<Set<String>, Runnable>> poolAdd =
		new HashSet<>();

	SimpleEntry<Set<String>, Runnable> e1 =
		mkentry(mksel("123"), "1", "2", "3");

	SimpleEntry<Set<String>, Runnable> e2 =
		mkentry(mksel("abc"), "a", "b", "c");

	poolAdd.add(e1);
	poolAdd.add(e2);
	poolRem.add(e1);

	clear(poolAdd, "1");

	NSLog("onCompleted: add %s", poolAdd);
	NSLog("onCompleted: rem %s", poolRem);

	poolAdd.removeAll(poolRem);

	NSLog("onCompleted: add %s", poolAdd);
	NSLog("onCompleted: rem %s", poolRem);
}
public static void clear(Set<SimpleEntry<Set<String>, Runnable>> pool, final String code) {
	for (SimpleEntry<Set<String>, Runnable> e : pool) {
		Set<String> codes = e.getKey();
		Runnable sel = e.getValue();

		codes.remove(code);

		NSLog("clear: %s", codes);

		if (codes.isEmpty()) {
			NSLog("clear: isEmpty");
		}
	}
}
public static SimpleEntry<Set<String>, Runnable> mkentry(Runnable sel, String ... codes) {
	Set<String> set = new HashSet<>(Arrays.asList(codes));

	SimpleEntry<Set<String>, Runnable> e =
		new SimpleEntry<>(set, sel);

	return e;
}
public static Runnable mksel(final String text) {
	Runnable sel = new Runnable() {
		@Override
		public void run() {
			NSLog("mksel: " + text);
		}
	};

	return sel;
}
}

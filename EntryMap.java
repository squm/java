import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

class Entry {

Map<String, Runnable> poolRun;
Map<String, Set<String>> poolSet;

public static void NSLog(String format, Object... args) {
	format += "\n";
	System.out.printf(format, args);
}
public static void main(String[] args) {
	Entry e = new Entry();
	e.grok();
}
public void grok() {
	poolRun = new HashMap<>();
	poolSet = new HashMap<>();

	poolAdd(mksel("abc"), "a", "b", "c");
	poolAdd(mksel("123"), "1", "2", "3");

	NSLog("grok: %s", poolSet);

	poolRem("1");
	poolRem("2");
	poolRem("3");

	poolRem("a");
	poolRem("b");
	poolRem("c");
}
public void poolAdd(Runnable sel, String ... codes) {
	final String uuid = UUID.randomUUID().toString();

	Set<String> set = new HashSet<>(Arrays.asList(codes));

	poolSet.put(uuid, set);
	poolRun.put(uuid, sel);
}
public void poolRem(final String code) {
	Set<String> poolRem = new HashSet<>();

	for (Map.Entry<String, Set<String>> entry : poolSet.entrySet()) {
		final String uuid = entry.getKey();
		Set<String> codes = entry.getValue();

		codes.remove(code);

		if (codes.isEmpty()) {
			poolRem.add(uuid);
		}
	}
	
	for (String uuid : poolRem) {
		Runnable sel = poolRun.get(uuid);
		poolRun.remove(uuid);
		poolSet.remove(uuid);
		sel.run();
	}
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

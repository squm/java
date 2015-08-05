import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import rx.*;
import rx.functions.*;
import rx.subjects.*;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

class EntryRx {

Map<String, Set<String>> poolSet;
Map<String, Subscriber> poolSub;

public static void NSLog(String format, Object... args) {
	format += "\n";
	System.out.printf(format, args);
}
public static void main(String[] args) {
	EntryRx e = new EntryRx();
	e.grok();
}
public void grok() {
	poolSet = new HashMap<>();
	poolSub = new HashMap<>();

	Action1<String> selNext123 = new Action1<String>() {
		@Override
		public void call(String ret) {
			NSLog("grok selNext 123: %s", ret);
		}
	};
	Action1<String> selNextAbc = new Action1<String>() {
		@Override
		public void call(String ret) {
			NSLog("grok selNext Abc: %s", ret);
		}
	};

	Action1<Throwable> selError = new Action1<Throwable>() {
		@Override
		public void call(Throwable e) {
			e.printStackTrace();
			NSLog("grok selError");
		}
	};

	poolAddRx("1", "2", "3")
		.subscribe(selNext123, selError, mksel("onCompleted 123"))
	;

	poolAddRx("a", "b", "c")
		.subscribe(selNextAbc, selError, mksel("onCompleted abc"))
	;

	poolRemRx("1");
	poolRemRx("2");
	poolRemRx("3");

	poolRemRx("a");
	poolRemRx("b");
	poolRemRx("c");
}
public Observable poolAddRx(String ... codes) {
	// 05/08/15 14:14:00

	final String uuid = UUID.randomUUID().toString();

	Set<String> set = new HashSet<>(Arrays.asList(codes));

	Observable<String> obs = Observable.create(
		new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> sub) {
				NSLog("poolAddRx subscr: %s %s", uuid, set);

				poolSet.put(uuid, set);
				poolSub.put(uuid, sub);
			}
		}
	);

	return obs;
}
public void poolRemRx(final String code) {
	Set<String> poolRem = new HashSet<>();

	for (Map.Entry<String, Set<String>> entry : poolSet.entrySet()) {
		final String uuid = entry.getKey();
		Set<String> codes = entry.getValue();

		if (codes.remove(code)) {
			Subscriber sub = poolSub.get(uuid);
			sub.onNext(String.format("%s (%s)", uuid, code));
		}

		if (codes.isEmpty()) {
			poolRem.add(uuid);
		}
	}

	for (String uuid : poolRem) {
		Subscriber sub;
		sub = poolSub.get(uuid);
		sub.onCompleted();

		poolSet.remove(uuid);
		poolSub.remove(uuid);
	}
}
public static Action0 mksel(final String text) {
	Action0 sel = new Action0() {
		@Override
		public void call() {
			NSLog("mksel: %s", text);
		}
	};

	return sel;
}
}


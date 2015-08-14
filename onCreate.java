import java.util.AbstractMap;
import java.util.Collection;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.concurrent.SynchronousQueue;

import rx.*;
import rx.Observable;
import rx.functions.*;
import rx.subjects.*;
import rx.subscriptions.CompositeSubscription;
import rx.schedulers.Schedulers;

class onCreate {

private CompositeSubscription composite;
// private Queue<String> queue;
private SynchronousQueue<String> rendezvous;

public static void NSLog(String format, Object... args) {
	format += "\n";
	System.out.printf(format, args);
}
public static void sleep(final long time) {
	try { Thread.sleep(time); } catch (Exception e) { }
}
public static void sleep(final String text) {
	for (int i = 6; i-- > 1;) { NSLog("%s %d", text, i); sleep(123); }
}
public Observable obs(final String text) {
	Observable<String> obs = Observable.create(
		new Observable.OnSubscribe<String>() {
			@Override
			public void call(Subscriber<? super String> sub) {
				NSLog("obs: %s", text);
				sub.onNext(null);
			}
		}
	);

	return obs;
}
public static void main(String[] args) {
	onCreate o = new onCreate();

	o.composite = new CompositeSubscription();
	o.rendezvous = new SynchronousQueue<>();

	o.onCreate();
	o.onActivityResult();
}
public void onCreate() {
	Action1<String> sel = new Action1<String>() {
		@Override
		public void call(String ret) {
			sleep("onCreate: sleep");
		}
	};

	Action1<String> selQ = new Action1<String>() {
		@Override
		public void call(String ret) {
			NSLog("onCreate: queue");

			try {
				rendezvous.put("onCreate::selQ");
			}
			catch (Exception e) {
			}
		}
	};

	Observable o =
	obs("onCreate::obs")
		.subscribeOn(Schedulers.computation())
		.doOnNext(sel)
		.doOnNext(selQ)
	;

	Subscription sub = o.subscribe();
	composite.add(sub);
}
public void onActivityResult() {
	sleep(123 * 2);

	if (composite.hasSubscriptions()) {
	// if (false) {
		NSLog("onActivityResult wait");

		try {
			rendezvous.take();
		}
		catch (Exception e) {
		}
	}

	NSLog("onActivityResult");
}
}

package com.dungeonbuilder.utils.threads;

import java.util.ArrayList;
import java.util.List;

public abstract class TaskChain {

	private List<Runnable> runnables;

	public TaskChain() {
		this.runnables = new ArrayList<Runnable>();
	}

	public void addTask(Runnable runnable) {
		this.runnables.add(runnable);
	}

	public void start(long delayBetween) {
		this.runThenRunNext(0, delayBetween);
	}

	public void runThenRunNext(int i, long delayBetween) {
		if (i >= this.runnables.size()) {
			this.done();
			return;
		}
		Runnable runnable = this.runnables.get(i);
		runnable.run();
		Threads.runMainLater(() -> {
			this.runThenRunNext(i + 1, delayBetween);
		}, delayBetween);
	}

	public abstract void done();
}

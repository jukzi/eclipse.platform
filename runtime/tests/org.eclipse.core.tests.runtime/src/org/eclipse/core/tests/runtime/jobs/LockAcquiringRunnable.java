/*******************************************************************************
 * Copyright (c) 2003, 2015 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.runtime.jobs;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.core.runtime.jobs.ILock;

public class LockAcquiringRunnable implements Runnable {
	static class RandomOrder {
		ArrayList<LockAcquiringRunnable> allRunnables;
		AtomicInteger rounds = new AtomicInteger();

		RandomOrder(ArrayList<LockAcquiringRunnable> allRunnables) {
			this.allRunnables = allRunnables;
		}

		public void randomWait() {
			try {
				Random random = ThreadLocalRandom.current();
				Thread.sleep(0, random.nextInt(500));
			} catch (InterruptedException e) {
				// ignore
			}
		}

		public void roundCompleted() {
			rounds.incrementAndGet();
		}

		public void waitFor(int maxRounds) {
			while (rounds.get() < maxRounds) {
				Thread.yield();
			}
		}
	}

	private final ILock[] locks;
	private volatile boolean alive;
	private volatile RandomOrder rnd;
	/**
	 * This runnable will randomly acquire the given lock for
	 * random periods of time, in the given order
	 */
	public LockAcquiringRunnable(ILock[] locks) {
		this.locks = locks;
		this.alive = true;
	}

	public void stop() {
		alive = false;
	}

	@Override
	public void run() {
		while (alive) {
			rnd.randomWait();
			for (ILock lock : locks) {
				lock.acquire();
				rnd.randomWait();
			}
			//release all locks
			for (int i = locks.length; --i >= 0;) {
				locks[i].release();
			}
			rnd.roundCompleted();
		}
	}

	public void setRandomOrder(RandomOrder rnd) {
		this.rnd = rnd;
	}
}

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
import junit.framework.TestCase;
import org.eclipse.core.runtime.jobs.LockListener;
import org.eclipse.core.tests.runtime.jobs.LockAcquiringRunnable.RandomOrder;
import org.junit.Test;

/**
 * Tests implementation of ILock objects
 */
@SuppressWarnings("restriction")
public class OrderedLockTest2 extends TestCase {


	@Test
	public void testLockRequestDisappearence() {


		LockListener listener = new LockListener() {
			@Override
			public boolean aboutToWait(Thread lockOwner) {
				return true;
			}
		};

	}

	public void execute(ArrayList<LockAcquiringRunnable> allRunnables) {
		RandomOrder randomOrder = new RandomOrder(allRunnables);
		for (LockAcquiringRunnable lockAcquiringRunnable : allRunnables) {
			lockAcquiringRunnable.setRandomOrder(randomOrder);
		}

		randomOrder.waitFor(5);

	}
}

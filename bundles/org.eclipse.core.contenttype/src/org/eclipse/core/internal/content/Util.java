/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.content;

import java.util.*;

public class Util {
	public static String[] parseItems(String string) {
		return parseItems(string, ","); //$NON-NLS-1$
	}

	public static String[] parseItems(String string, String separator) {
		if (string == null)
			return new String[0];
		StringTokenizer tokenizer = new StringTokenizer(string, separator, true);
		if (!tokenizer.hasMoreTokens())
			return new String[] {string.trim()};
		String first = tokenizer.nextToken().trim();
		boolean wasSeparator = false;
		if (first.equals(separator)) {
			// leading separator
			first = ""; //$NON-NLS-1$
			wasSeparator = true;
		}
		// simple cases, do not create temporary list
		if (!tokenizer.hasMoreTokens())
			return wasSeparator ? /* two empty strings */new String[] {first, first} : /*single non-empty element  */new String[] {first};
		ArrayList items = new ArrayList();
		items.add(first);
		String current;
		do {
			current = tokenizer.nextToken().trim();
			boolean isSeparator = current.equals(separator);
			if (isSeparator) {
				if (wasSeparator)
					items.add(""); //$NON-NLS-1$
			} else
				items.add(current);
			wasSeparator = isSeparator;
		} while (tokenizer.hasMoreTokens());
		if (wasSeparator)
			// trailing separator
			items.add(""); //$NON-NLS-1$
		return (String[]) items.toArray(new String[items.size()]);
	}

	public static List parseItemsIntoList(String string) {
		return parseItemsIntoList(string, ","); //$NON-NLS-1$
	}

	public static List parseItemsIntoList(String string, String separator) {
		List items = new ArrayList(5);
		if (string == null)
			return items;
		StringTokenizer tokenizer = new StringTokenizer(string, separator, true);
		if (!tokenizer.hasMoreTokens()) {
			items.add(string.trim());
			return items;
		}
		String first = tokenizer.nextToken().trim();
		boolean wasSeparator = false;
		if (first.equals(separator)) {
			// leading separator
			first = ""; //$NON-NLS-1$
			wasSeparator = true;
		}
		items.add(first);
		if (!tokenizer.hasMoreTokens())
			return items;
		String current;
		do {
			current = tokenizer.nextToken().trim();
			boolean isSeparator = current.equals(separator);
			if (isSeparator) {
				if (wasSeparator)
					items.add(""); //$NON-NLS-1$
			} else
				items.add(current);
			wasSeparator = isSeparator;
		} while (tokenizer.hasMoreTokens());
		if (wasSeparator)
			// trailing separator
			items.add(""); //$NON-NLS-1$	
		return items;
	}

	public static String toListString(Object[] list) {
		return toListString(list, ","); //$NON-NLS-1$
	}

	public static String toListString(Object[] list, String separator) {
		if (list == null || list.length == 0)
			return null;
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < list.length; i++) {
			result.append(list[i]);
			result.append(separator);
		}
		// ignore last comma
		return result.substring(0, result.length() - 1);
	}
}

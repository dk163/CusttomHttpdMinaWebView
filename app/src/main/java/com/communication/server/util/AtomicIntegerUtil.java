package com.communication.server.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicIntegerUtil {

	private static final AtomicInteger mAtomicInteger = new AtomicInteger();

	public static int getIncrementID() {
		return mAtomicInteger.getAndIncrement();
	}
}

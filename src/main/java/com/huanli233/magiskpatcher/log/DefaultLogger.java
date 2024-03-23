package com.huanli233.magiskpatcher.log;

public class DefaultLogger implements ILogger {

	@Override
	public void debug(String s) {
		System.out.println(s);
	}

	@Override
	public void info(String s) {
		System.out.println(s);
	}

	@Override
	public void warn(String s) {
		System.out.println(s);
	}

	@Override
	public void err(String s) {
		System.err.println(s);
	}

}

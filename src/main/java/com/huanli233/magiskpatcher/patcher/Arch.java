package com.huanli233.magiskpatcher.patcher;

public enum Arch {
	
	ARM("armeabi-v7a"), ARM64("arm64-v8a"), X86("x86"), X86_64("x86_64");

	private final String value;
	
	Arch(String string) {
		this.value = string;
	}
	
	public String getValue() {
		return this.value;
	}
	
}

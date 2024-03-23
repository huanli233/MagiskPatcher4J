package com.huanli233.magiskpatcher.patcher;

public class PatchConfig {
	
	/**
	 * 保持验证
	 */
	private boolean keepVerify;
	
	/**
	 * 保持强制加密
	 */
	private boolean keepForceEncrypt;
	
	/**
	 * 修补vbmeta标志
	 */
	private boolean patchVbmetaFlag;
	
	/**
	 * recovery模式
	 */
	private boolean recoveryMode;
	
	/**
	 * 传统SAR
	 */
	private boolean legacySar;

	/**
	 * @return keepVerify
	 */
	public boolean isKeepVerify() {
		return keepVerify;
	}

	/**
	 * @param keepVerify 要设置的 keepVerify
	 */
	public PatchConfig setKeepVerify(boolean keepVerify) {
		this.keepVerify = keepVerify;
		return this;
	}

	/**
	 * @return keepForceEncrypt
	 */
	public boolean isKeepForceEncrypt() {
		return keepForceEncrypt;
	}

	/**
	 * @param keepForceEncrypt 要设置的 keepForceEncrypt
	 */
	public PatchConfig setKeepForceEncrypt(boolean keepForceEncrypt) {
		this.keepForceEncrypt = keepForceEncrypt;
		return this;
	}

	/**
	 * @return patchVbmetaFlag
	 */
	public boolean isPatchVbmetaFlag() {
		return patchVbmetaFlag;
	}

	/**
	 * @param patchVbmetaFlag 要设置的 patchVbmetaFlag
	 */
	public PatchConfig setPatchVbmetaFlag(boolean patchVbmetaFlag) {
		this.patchVbmetaFlag = patchVbmetaFlag;
		return this;
	}

	/**
	 * @return recoveryMode
	 */
	public boolean isRecoveryMode() {
		return recoveryMode;
	}

	/**
	 * @param recoveryMode 要设置的 recoveryMode
	 */
	public PatchConfig setRecoveryMode(boolean recoveryMode) {
		this.recoveryMode = recoveryMode;
		return this;
	}

	/**
	 * @return legacySar
	 */
	public boolean isLegacySar() {
		return legacySar;
	}

	/**
	 * @param legacySar 要设置的 legacySar
	 */
	public PatchConfig setLegacySar(boolean legacySar) {
		this.legacySar = legacySar;
		return this;
	}
	
}

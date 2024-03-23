package com.huanli233.magiskpatcher.patcher;

import java.io.File;

import com.huanli233.magiskpatcher.exception.InitializationFailedException;
import com.huanli233.magiskpatcher.log.ILogger;
import com.huanli233.magiskpatcher.utils.CmdUtil.ExecResult;
import com.huanli233.magiskpatcher.utils.CmdUtil;
import com.huanli233.magiskpatcher.utils.FileUtil;

public class MagiskPatcher {
	
	/**
	 * magiskboot文件
	 */
	public static final String BIN_FILE = "magiskboot.exe";
	
	/**
	 * 修补配置
	 */
	private PatchConfig config;
	
	/**
	 * Magisk包文件
	 */
	private File packageFile;
	
	/**
	 * 临时存储路径
	 */
	private File fileTempPath;
	
	/**
	 * 架构
	 */
	private Arch arch;
	
	/**
	 * logger
	 */
	private ILogger logger;
	
	/**
	 * @param fileTempPath magiskboot及其他文件的临时存储路径
	 * @throws InitializationFailedException 初始化失败
	 */
	public MagiskPatcher(File fileTempPath) throws InitializationFailedException {
		this.fileTempPath = fileTempPath;
		if (!fileTempPath.exists() && !fileTempPath.mkdirs()) {
			throw new InitializationFailedException("can not create directory " + fileTempPath.getAbsolutePath());
		}
		File magiskbootFile = new File(fileTempPath, BIN_FILE);
		if (!magiskbootFile.exists()) {
			FileUtil.writeInputStreamToFile(magiskbootFile, getClass().getResourceAsStream("/bin/" + BIN_FILE));
		} else if (!magiskbootFile.isFile()) {
			magiskbootFile.delete();
			FileUtil.writeInputStreamToFile(magiskbootFile, getClass().getResourceAsStream("/bin/" + BIN_FILE));
		}
		if (!magiskbootFile.exists() || !magiskbootFile.isFile()) {
			throw new InitializationFailedException("unable to initialize magiskboot");
		}
	}

	/**
	 * @return config
	 */
	public PatchConfig getConfig() {
		return config;
	}

	/**
	 * @param config 要设置的 config
	 */
	public void setConfig(PatchConfig config) {
		this.config = config;
	}

	/**
	 * @return packageFile
	 */
	public File getPackageFile() {
		return packageFile;
	}

	/**
	 * @param packageFile 要设置的 packageFile
	 */
	public void setPackageFile(File packageFile) {
		this.packageFile = packageFile;
	}
	
	/**
	 * @return logger
	 */
	public ILogger getLogger() {
		return logger;
	}

	/**
	 * @param logger 要设置的 logger
	 */
	public void setLogger(ILogger logger) {
		this.logger = logger;
	}

	/**
	 * @return arch
	 */
	public Arch getArch() {
		return arch;
	}

	/**
	 * @param arch 要设置的 arch
	 */
	public void setArch(Arch arch) {
		this.arch = arch;
	}
	
	/**
	 * 执行Magiskboot命令
	 * @return 命令结果
	 */
	private ExecResult execCmd(String... args) {
		return CmdUtil.runExecutable(new File(fileTempPath, BIN_FILE).getAbsoluteFile(), args);
	}

	String[] rmList = {};
	
	/**
	 * clean
	 */
	public void clean() {
		for (String path : rmList) {
			File file = new File(fileTempPath, path);
			file.delete();
		}
		execCmd("cleanup");
	}
	
}

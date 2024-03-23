package com.huanli233.magiskpatcher.patcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import com.huanli233.magiskpatcher.exception.InitializationFailedException;
import com.huanli233.magiskpatcher.log.DefaultLogger;
import com.huanli233.magiskpatcher.log.ILogger;
import com.huanli233.magiskpatcher.utils.CmdUtil.ExecResult;
import com.huanli233.magiskpatcher.utils.CmdUtil;
import com.huanli233.magiskpatcher.utils.FileUtil;
import com.huanli233.magiskpatcher.utils.MagiskApkUtil;

public class MagiskPatcher {
	
	/**
	 * magiskboot文件
	 */
	public static final String BIN_FILE = "magiskboot.exe";
	
	/**
	 * 修补配置
	 */
	private PatchConfig config = new PatchConfig();
	
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
		this.logger = new DefaultLogger();
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
		String[] cmd = new String[args.length + 1];
		cmd[0] = BIN_FILE;
		for (int i = 0; i < args.length; i++) {
			cmd[i + 1] = args[i];
		}
		return CmdUtil.runExecutable(fileTempPath.getAbsoluteFile(), cmd);
	}

	String[] rmList = {"magisk32", "magisk32.xz", "magisk64", "magisk64.xz", "magiskinit", "stub.apk"};
	
	/**
	 * clean
	 */
	public void clean() {
		for (String path : rmList) {
			File file = new File(fileTempPath, path);
			if (file.exists()) {
				file.delete();
			}
		}
		execCmd("cleanup");
	}
	
	/**
	 * 检查文件是否存在并且是否是文件
	 * @param file 要检查的文件
	 * @return
	 */
	private boolean isFile(File file) {
		
		return file.exists() && file.isFile();
	}
	
	/**
	 * 检查文件是否存在并且是否是文件
	 * @param str 要检查的文件名
	 * @return
	 */
	private boolean isFile(String str) {
		return isFile(new File(fileTempPath, str));
	}
	
	/**
	 * 读取指定文件sha1
	 * @param file
	 * @return sha1值
	 */
	private String sha1(File file) {
		try { 
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
	        FileInputStream fis = new FileInputStream(file);
	        byte[] byteArray = new byte[1024];
	        int bytesCount;
	        while ((bytesCount = fis.read(byteArray)) != -1) {
	            digest.update(byteArray, 0, bytesCount);
	        }
	        fis.close();
	        byte[] bytes = digest.digest();
	        StringBuilder sb = new StringBuilder();
	        for (byte aByte : bytes) {
	            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
	        }
	        return sb.toString();
		} catch (Exception e) {
			logger.err(e.toString());
		}
		return null;
    }
	
	/**
	 * 复制文件
	 * @param source 源文件
	 * @param dest 目标文件
	 */
	private void copyFile(File source, File dest) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (IOException e) {
			logger.err(e.toString());
		} finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	/**
	 * 删除文件
	 * @param file 目标文件
	 */
	private void rmFile(File file) {
		if (file.exists()) file.delete();
	}
	
	/**
	 * 删除文件
	 * @param file 目标文件
	 */
	private void rmFile(String str) {
		File file = new File(fileTempPath, str);
		rmFile(file);
	}
	
	/**
	 * 删除文件
	 * @param strs 目标文件
	 */
	private void rmFile(String... strs) {
		for (int i = 0; i < strs.length; i++) {
			File file = new File(fileTempPath, strs[i]);
			rmFile(file);
		}
	}
	
	private String grepProp(String key, String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileTempPath, file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(key)) {
                    return line.split("=")[1].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
	
	/**
	 * 修补boot文件
	 * @param bootimg 要修补的boot文件
	 * @param result 结果存储路径
	 */
	public int patch(File bootimg, File result) {
		
		if (arch == null) {
			logger.err("! Arch 未指定");
			return -3;
		}
		
		if (packageFile == null || !packageFile.exists() || !packageFile.isFile()) {
			logger.err("! Magisk APK 文件不存在");
			return -2;
		}
		
		logger.info("- 解包 Magisk APK");
		try {
			MagiskApkUtil.parseMagiskApk(packageFile.getAbsolutePath(), arch, fileTempPath);
		} catch (IOException e1) {
			logger.err("! 无法解包 Magisk APK");
			logger.err(e1.toString());
		}
		
		
		int rt;
		
		// 检查bootimg文件是否存在
		if (!bootimg.exists()) {
			logger.err("! boot 文件不存在");
			return -1;
		}
		
		// 解包boot
		logger.info("- 解包 boot 文件");
		rt = execCmd("unpack", bootimg.getAbsolutePath()).getExitCode();
		switch (rt) {
		case 0:
			break;
		case 1:
			logger.err("! 不支持或未知的镜像格式");
			return 1;
		case 2:
			logger.err("! ChromeOS 格式的镜像");
			logger.err("! 暂不支持");
			return 1;
		default:
			logger.err("! 无法解包 boot 文件");
			return 1;
		}
		
		// check ramdisk
		logger.info("- 检查 ramdisk 状态");
		int status;
		String skipBackup;
		if (isFile("ramdisk.cpio")) {
			status = execCmd("cpio", "ramdisk.cpio", "test").getExitCode();
			skipBackup = "";
		} else {
			status = 0;
		    skipBackup = "#";
		}
		
		String sha = "";
		switch (status & 3) {
		case 0:
			logger.info("- 检测到原始未修改的 boot 镜像");
			sha = sha1(bootimg);
			copyFile(bootimg, new File(fileTempPath, "stock_boot.img"));
			copyFile(new File(fileTempPath, "ramdisk.cpio"), new File(fileTempPath, "ramdisk.cpio.orig"));
			break;
		case 1:
			logger.info("- 检测到 Magisk 修补过的 boot 镜像");
			execCmd("cpio", "ramdisk.cpio", "extract .backup/.magisk config.orig", "restore");
			copyFile(new File(fileTempPath, "ramdisk.cpio"), new File(fileTempPath, "ramdisk.cpio.orig"));
			rmFile("stock_boot.img");
			break;
		case 2:
			logger.err("! boot 镜像被未知的程序修补");
			logger.err("! 请还原至原镜像");
			return 2;
		}
		
		// Sony device
		String init = "init";
		if (!((status & 4) == 0)) {
			init = "init.real";
		}
		
		if (isFile("config.orig")) {
			sha = grepProp("SHA1", "config.orig");
			rmFile("config.orig");
		}
		
		logger.info("- 修补 ramdisk");
		String skip32 = "#";
		String skip64 = "#";
		if (isFile("magisk64")) {
			execCmd("compress=xz", "magisk64", "magisk64.xz");
			skip64 = "";
		}
		if (isFile("magisk32")) {
			execCmd("compress=xz", "magisk32", "magisk32.xz");
			skip32 = "";
		}
		
		boolean stub = false;
		if (isFile("stub.apk")) {
			stub = true;
		}
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileTempPath, "config")))) {
            writer.write(
                    "KEEPVERITY=" + config.isKeepVerify() + "\n" +
                    "KEEPFORCEENCRYPT=" + config.isKeepForceEncrypt() + "\n" +
                    "RECOVERYMODE=" + config.isRecoveryMode() + "\n"
            );
            if (!sha.isEmpty()) {
                writer.write("SHA1=" + sha + "\n");
            }
        } catch (IOException e) {
            logger.err(e.toString());
            return 3;
        }
		
		rt = execCmd("cpio", "ramdisk.cpio",
	            String.format("add 0750 %s magiskinit", init),
	            "mkdir 0750 overlay.d",
	            "mkdir 0750 overlay.d/sbin",
	            String.format("%s add 0644 overlay.d/sbin/magisk32.xz magisk32.xz", skip32),
	            String.format("%s add 0644 overlay.d/sbin/magisk64.xz magisk64.xz", skip64),
	            stub ? "add 0644 overlay.d/sbin/stub.xz stub.xz" : "",
	            "patch",
	            String.format("%s backup ramdisk.cpio.orig", skipBackup),
	            "mkdir 000 .backup",
	            "add 000 .backup/.magisk config").getExitCode();
		if (rt != 0) {
			logger.err("! 无法修补 ramdisk");
			return 4;
		}
		
		rmFile("ramdisk.cpio.orig", "config", "magisk32.xz", "magisk64.xz", "stub.xz");
		
		for (String string : new String[]{"dtb", "kernel_dtb", "extra"}) {
			if (isFile(string)) {
				rt = execCmd("dtb", string, "test").getExitCode();
				if (rt != 0) {
					logger.err(String.format("! boot 镜像中的 %s 被旧的 Magisk 修补", string));
					logger.err("! 请使用原 boot 镜像重试");
					return 5;
				}
				rt = execCmd("dtb", string, "patch").getExitCode();
				if (rt == 0) {
					logger.info(String.format("- 修补 boot 镜像中 %s 的 fstab", string));
				}
			}
		}
		
		if (isFile("kernel")) {
			boolean patchedKernel = false;
			rt = execCmd("hexpatch", "kernel",
	                "49010054011440B93FA00F71E9000054010840B93FA00F7189000054001840B91FA00F7188010054",
	                "A1020054011440B93FA00F7140020054010840B93FA00F71E0010054001840B91FA00F7181010054").getExitCode();
			if (rt == 0) patchedKernel = true;
			rt = execCmd("hexpatch", "kernel", "821B8012", "E2FF8F12").getExitCode();
			if (rt == 0) patchedKernel = true;
			if (config.isLegacySar()) {
				rt = execCmd("hexpatch", "kernel",
	                    "736B69705F696E697472616D667300",
	                    "77616E745F696E697472616D667300").getExitCode();
				if (rt == 0) patchedKernel = true;
			}
			if (!patchedKernel) {
				rmFile("kernel");
			}
		}
		
		logger.info("- 打包 boot 镜像");
		rt = execCmd("repack", bootimg.getAbsolutePath()).getExitCode();
		if (rt != 0) {
			logger.err("! 无法打包 boot 镜像");
			return 6;
		}
		
		this.clean();
		logger.info("- 完成！");
		
		return 0;
	}
	
	/**
	 * 修补boot文件
	 * @param bootimg 要修补的boot文件
	 * @param result 结果存储路径
	 */
	public int patch(File bootimg, String result) {
		return patch(bootimg, new File(result));
	}
	
	/**
	 * 修补boot文件
	 * @param bootimg 要修补的boot文件
	 */
	public int patch(File bootimg) {
		return patch(bootimg, "");
	}
	
}

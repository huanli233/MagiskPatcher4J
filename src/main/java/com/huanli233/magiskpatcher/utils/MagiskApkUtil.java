package com.huanli233.magiskpatcher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.huanli233.magiskpatcher.patcher.Arch;

public class MagiskApkUtil {
	
	/**
	 * arch转32位
	 * @param arch arch
	 * @return 转换后结果
	 */
	private static Arch to32(Arch arch) {
		Arch result = arch;
		switch (arch) {
		case ARM64:
			result = Arch.ARM;
			break;
		case X86_64:
			result = Arch.X86;
			break;
		default:
			break;
		}
		return result;
	}

	static final List<Arch> arch64List = Arrays.asList(Arch.ARM64, Arch.X86_64);
	
	/**
	 * 解析Magisk APK并释放文件
	 * @param apkFilePath Magisk APK路径
	 * @param arch Arch
	 * @param saveDir 保存路径
	 * @throws IOException
	 */
	public static void parseMagiskApk(String apkFilePath, Arch arch, File saveDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(apkFilePath))) {
        	ZipEntry mEntry;
            while ((mEntry = zis.getNextEntry()) != null) {
                if ("stub.apk".equals(mEntry.getName())) {
                    saveFile(zis, new File(saveDir, "stub.apk"));
                }
                if (mEntry.getName().startsWith("lib/" + arch.getValue()) && mEntry.getName().contains("libmagiskinit.so")) {
                    saveFile(zis, new File(saveDir, "magiskinit"));
                }
            	Arch to32 = to32(arch);
            	if (mEntry.getName().startsWith("lib/" + to32.getValue()) && mEntry.getName().contains("libmagisk32.so")) {
            		saveFile(zis, new File(saveDir, "magisk32"));
				}
            	if (arch64List.contains(arch) && mEntry.getName().startsWith("lib/" + arch.getValue()) && mEntry.getName().contains("libmagisk64.so")) {
            		saveFile(zis, new File(saveDir, "magisk64"));
				}
            }
        }
    }
	
	/**
	 * 获取Magisk版本
	 * @param fname 文件路径
	 * @return
	 */
	public static String getMagiskApkVersion(String fname) {
        boolean validFlag = false;
        String magiskVerCode = "00000";

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fname))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith("util_functions.sh")) {
                    validFlag = true;
                    byte[] buffer = new byte[(int) entry.getSize()];
                    zis.read(buffer);
                    String content = new String(buffer);
                    String[] lines = content.split("\\r?\\n");
                    for (String line : lines) {
                        if (line.contains("MAGISK_VER_CODE")) {
                            magiskVerCode = line.split("=")[1];
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (!validFlag) {
            return null;
        }

        return magiskVerCode;
    }
	
	 private static void saveFile(ZipInputStream zis, File file) throws IOException {
	        try (FileOutputStream fos = new FileOutputStream(file)) {
	            byte[] buffer = new byte[1024];
	            int len;
	            while ((len = zis.read(buffer)) > 0) {
	                fos.write(buffer, 0, len);
	            }
	        }
	    }
	
}

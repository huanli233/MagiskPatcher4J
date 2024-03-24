package com.huanli233.magiskpatcher.tests;

import java.io.File;

import org.junit.Test;

import com.huanli233.magiskpatcher.exception.InitializationFailedException;
import com.huanli233.magiskpatcher.patcher.Arch;
import com.huanli233.magiskpatcher.patcher.MagiskPatcher;
import com.huanli233.magiskpatcher.patcher.PatchConfig;
import com.huanli233.magiskpatcher.utils.FileUtil;

public class PatchTest {

	@Test
	public void test() {
		if (!new File("boot.img").exists()) {
			FileUtil.writeInputStreamToFile(new File("boot.img"), PatchTest.class.getResourceAsStream("/boot.bin"));
		}
		try {
			MagiskPatcher patcher = new MagiskPatcher(new File("temp"));
			patcher.setArch(Arch.ARM);
			patcher.setConfig(new PatchConfig().setKeepForceEncrypt(true)
					.setKeepVerify(false)
					.setLegacySar(true)
					.setPatchVbmetaFlag(true)
					.setRecoveryMode(false));
			patcher.setPackageFile(new File("Magisk-v25.2.apk"));
			patcher.patch(new File("boot.img"), new File("boot_patched.img"));
		} catch (InitializationFailedException e) {
			e.printStackTrace();
		}
	}
	
}

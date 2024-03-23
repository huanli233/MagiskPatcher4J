# MagiskPatcher4J
`A lib to patch boot image in Java.`

# Thanks
[Magisk_patcher](https://github.com/affggh/Magisk_patcher)

# Usage

```java
MagiskPatcher patcher = new MagiskPatcher(new File("temp")); // 传入工作文件夹路径
patcher.setArch(Arch.ARM); // 设置架构
// 设置Patch配置
patcher.setConfig(new PatchConfig().setKeepForceEncrypt(true)
					.setKeepVerify(false)
					.setLegacySar(true)
					.setPatchVbmetaFlag(true)
					.setRecoveryMode(false));
patcher.setPackageFile(new File("Magisk-v25.2.apk")); // 设置Magisk APK路径
// 设置logger 实现ILogger 默认输出到System.out System.err
// patcher.setLogger(new ILogger() {
// 	 @Override
// 	 public void warn(String s) {
// 	 }
// 	 @Override
//	 public void info(String s) {
//	 }
//	 @Override
//	 public void err(String s) {
//	 }
//	 @Override
//	 public void debug(String s) {
//	 }
// });
patcher.patch(new File("boot.img"), new File("boot_patched.img")); // 被修补的boot与输出文件
```


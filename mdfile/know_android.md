# android常用知识总结

[toc]

## [android10 版本适配 - 存储](https://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650830137&idx=1&sn=11cefd20ed81bd3d05571a4fa9083415&chksm=80b7a1a7b7c028b16152bbb5ce029d1e41a5e2fe12fb2ca85e10c0854e3dd1fc0d7c869b27f0&scene=126&sessionid=1588902112&key=460806c790ad44b49fc6d1ceb0beaf13d7158ddb70260d134b8edb8ab13795e21ab663da16b7186511235f4737a9d37bbd79e566178b70456697ca0b64a2e1a452c733a39cb2311211a53a7489098833&ascene=1&uin=MjIwMDU4NjQ0MQ%3D%3D&devicetype=Windows+7&version=62080079&lang=zh_CN&exportkey=Aet%2FbXDhnuBHsmGGUUPVtNc%3D&pass_ticket=rwZq0uP9aluqO85V9FsqMpSxx7Ggp9kWw6EukYv3hitALPQsQdMhBypVa2HYPkqB)

android 10 引入 Scoped Storage 概念,通过添加外部存储访问限制来实现更好的文件管理;

- **内部储存**: /data 目录; 一般使用 getFilesDir() 和 getCacheDir() 方法获取本应用的内部储存路径,读写该路径下的文件不需要申请存储空间读写权限,且卸载应用时会自动删除;

- **外部储存**: /storage 或/mnt 目录; 一般使用 getExternalStorageDirectory()方法获取的路径来存取文件; 

外部路径也分为三部分:

- 特定目录(app-specific),使用getExternalFilesDir() 或 getExternalCacheDir() 方法访问; 无需权限,且卸载应用时会自动删除;
- 照片,视频,音频这类媒体文件, 使用mediastore 访问,访问其他应用的媒体文件时需要 `READ_EXTERNAL_STORAGE`权限;卸载不会自动删除;
- 其他目录(下载内容) 使用存储访问框架SAF(storage access framwork) 卸载不会自动删除 [saf-ref](https://developer.android.google.cn/guide/topics/providers/document-provider?hl=zh_cn)


![](https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwMp5uEteoGjY4cM1u8fjt2iaHpYojP1vo0Twev1OHWK3OJvFRuhIGOTbPZ7SibYzA3eILicwsUA244Fw/640?wx_fmt=png)

在android10中,即时拥有了储存空间的读写权限,也无法保证可以正常进行文件的读写操作;

####适配方法

- `android:requestLegacyExternalStorage="true"` 清单文件中设置 来请求旧的存储模式; 但是下一个版本会失效,强制使用外部储存限制; 
- 对于应用中涉及的文件操作,修改一下文件路径;
	- 以前使用Environment.getExternalStorageDirectory(),现在可使用getExternalFilesDir()方法(包括下载的安装包这类的文件),如果是缓存类文件,可以放到 getExternalCacheDir()路径下;
	- 多媒体文件可以使用mediastore 存至对应的媒体类型中(图片:MediaStore.Images, 视频: MediaStore.Video,音频:MediaStore.Audio);
	- 对于媒体资源的访问, 比如图片选择器这类的场景; 无法直接使用File,而应该使用Uri,否则会报错(`FileNotFoundException:open failed: Permission denied`);

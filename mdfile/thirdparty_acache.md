### 引言

这篇文章主要介绍 android存储文件的轻量级缓存框架 ACache; 
[原链接地址](https://github.com/yangfuhai/ASimpleCache)
库很小,小到只有一个文件,看到源码设计的很精美,决定写篇博客记录一下;

## ACache

代码很简洁,只摘要几个亮点以飨文章:

### 支持多进程下的存储

```

	//设置最大存储空间和最大存储数量;
	public static ACache get(File cacheDir, long max_zise, int max_count) {
        ACache manager = mInstanceMap.get(cacheDir.getAbsoluteFile() + myPid());
        if (manager == null) {
            manager = new ACache(cacheDir, max_zise, max_count);
            mInstanceMap.put(cacheDir.getAbsolutePath() + myPid(), manager);
        }
        return manager;
    }

	private static String myPid() {
        return "_" + android.os.Process.myPid();
    }

```

### 存 - 所有被存储的对象都通过 流FileOutputStream 存于本地

key: string的hashcode;

value: 所有的存储对象最后都转化为File 存于缓存目录;

```

	//存于本地路径,同时也是内存缓存中的key,代表文件系统中的路径;
	private File newFile(String key) {
            return new File(cacheDir, key.hashCode() + "");
        }

	//string 存储通过hashcode在存储目录中生成一个文件存储;成功后在内存中存储filekey和当前时间long;
	public void put(String key, String value) {
        File file = mCache.newFile(key);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file), 1024);
            out.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mCache.put(file);
        }
    }

	//cachemanager 的put方法
	private void put(File file) {
			//线程安全Integer;
            int curCacheCount = cacheCount.get();
            while (curCacheCount + 1 > countLimit) {
                long freedSize = removeNext();
                cacheSize.addAndGet(-freedSize);

                curCacheCount = cacheCount.addAndGet(-1);
            }
            cacheCount.addAndGet(1);

            long valueSize = calculateSize(file);
            long curCacheSize = cacheSize.get();
            //todo 若文件超大,这边就陷入死循环了,可以优化的地方,不过一般不会;
            while (curCacheSize + valueSize > sizeLimit) {
                long freedSize = removeNext();
                curCacheSize = cacheSize.addAndGet(-freedSize);
				//todo 此处可能remove多次,cacheCount应该也需要一起变化;
            }
            cacheSize.addAndGet(valueSize);

            Long currentTime = System.currentTimeMillis();
            file.setLastModified(currentTime);
			//线程安全的map;
            lastUsageDates.put(file, currentTime);
        }	

```
### 存 - 带有过期时间的存储

```

	//保存时间单位 s;
	public void put(String key, String value, int saveTime) {
        put(key, Utils.newStringWithDateInfo(saveTime, value));
    }

	//使用value 和 过期时间重新生成一个可解析时间的最终存储字符串;
	private static String newStringWithDateInfo(int second, String strInfo) {
            return createDateInfo(second) + strInfo;
        }

	//当前时间(不足13位前补0)+ - + 过期时间 + 分隔位(空格)
	private static String createDateInfo(int second) {
            String currentTime = System.currentTimeMillis() + "";
            while (currentTime.length() < 13) {
                currentTime = "0" + currentTime;
            }
            return currentTime + "-" + second + mSeparator;
        }

	//如果是存储的是数组(转化而来)
	//将时间标志信息和数据信息合并成一个数组,写入文件;
	private static byte[] newByteArrayWithDateInfo(int second, byte[] data2) {
            byte[] data1 = createDateInfo(second).getBytes();
            byte[] retdata = new byte[data1.length + data2.length];
            System.arraycopy(data1, 0, retdata, 0, data1.length);
            System.arraycopy(data2, 0, retdata, data1.length, data2.length);
            return retdata;
        }

```

### 取 - 通过key匹配存于本地的File对象,带有过期时间的取数据判断;

```


	//先通过hashcode匹配本地文件路径,从流中读取数据,如果数据存在时间位; 过期 return null,且调用remove方法; 没有过期则从数据中取出分隔位后的数据;
	public String getAsString(String key) {
        File file = mCache.get(key);
        if (!file.exists())
            return null;
        boolean removeFile = false;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            StringBuilder readStringBuilder = new StringBuilder();
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                readStringBuilder.append(currentLine);
            }
            if (!Utils.isDue(readStringBuilder.toString())) {
                return Utils.clearDateInfo(readStringBuilder.toString());
            } else {
                removeFile = true;
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (removeFile)
                remove(key);
        }
    }

	//cachemanager的remove方法,向缓存中加入一个对象,删除本地文件;
	private boolean remove(String key) {
            File image = get(key);
            return image.delete();
        }

	//cachemanager的get方法;
	private File get(String key) {
            File file = newFile(key);
            Long currentTime = System.currentTimeMillis();
            file.setLastModified(currentTime);
            lastUsageDates.put(file, currentTime);

            return file;
        }

	//判断时间是否过期工具类解析;
	//先使用从流中取出的存储数据,解析出时间数据,判断是否过期;
	private static boolean isDue(byte[] data) {
            String[] strs = getDateInfoFromDate(data);
            if (strs != null && strs.length == 2) {
                String saveTimeStr = strs[0];
                while (saveTimeStr.startsWith("0")) {
                    saveTimeStr = saveTimeStr.substring(1, saveTimeStr.length());
                }
                long saveTime = Long.valueOf(saveTimeStr);
                long deleteAfter = Long.valueOf(strs[1]);
                if (System.currentTimeMillis() > saveTime + deleteAfter * 1000) {
                    return true;
                }
            }
            return false;
        }

	//hasDateInfo 长度大于15,第13位是-,分隔位大于14位;
	//有时间数据,将前13位作为存储时间数据,14位到分隔位作为过期时间数据;
	private static String[] getDateInfoFromDate(byte[] data) {
            if (hasDateInfo(data)) {
                String saveDate = new String(copyOfRange(data, 0, 13));
                String deleteAfter = new String(copyOfRange(data, 14, indexOf(data, mSeparator)));
                return new String[]{saveDate, deleteAfter};
            }
            return null;
        }

	//如果直接取字节数据;
	public byte[] getAsBinary(String key) {
        RandomAccessFile RAFile = null;
        boolean removeFile = false;
        try {
            File file = mCache.get(key);
            if (!file.exists())
                return null;
            RAFile = new RandomAccessFile(file, "r");
            byte[] byteArray = new byte[(int) RAFile.length()];
            RAFile.read(byteArray);
            if (!Utils.isDue(byteArray)) {
				//判断有时间数据,则copyOfRange数据数组;
                return Utils.clearDateInfo(byteArray);
            } else {
                removeFile = true;
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (RAFile != null) {
                try {
                    RAFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (removeFile)
                remove(key);
        }
    }

```

### ACacheManager 设计

ACache的关键类;

```

		//可配置属性
		//原子类型 缓存的大小(文件);
		private final AtomicLong cacheSize;
		//原子类型 缓存的数量
        private final AtomicInteger cacheCount;
		//大小限制;
        private final long sizeLimit;
		//数量限制;
        private final int countLimit;
		//引用map; 初始化时将目录中原文件添加到内存map中;
        private final Map<File, Long> lastUsageDates = Collections.synchronizedMap(new HashMap<File, Long>());
		//缓存路径;
        protected File cacheDir;

```




### LRU(lastly recent use)算法实现的存储机制 


```
	
	//当检验到大小或数量大于设定值,则进行移出操作,返回删除的大小;
	private long removeNext() {
        if (lastUsageDates.isEmpty()) {
            return 0;
        }

        Long oldestUsage = null;
        File mostLongUsedFile = null;
        Set<Entry<File, Long>> entries = lastUsageDates.entrySet();
		//优先移出时间最早的数据;
        synchronized (lastUsageDates) {
            for (Entry<File, Long> entry : entries) {
                if (mostLongUsedFile == null) {
                    mostLongUsedFile = entry.getKey();
                    oldestUsage = entry.getValue();
                } else {
                    Long lastValueUsage = entry.getValue();
                    if (lastValueUsage < oldestUsage) {
                        oldestUsage = lastValueUsage;
                        mostLongUsedFile = entry.getKey();
                    }
                }
            }
        }

        long fileSize = calculateSize(mostLongUsedFile);
        if (mostLongUsedFile != null && mostLongUsedFile.delete()) {
            lastUsageDates.remove(mostLongUsedFile);
        }
        return fileSize;
    }

```

### 优缺点分析

优: 简单高效,只有一个文件; 设计精美;

缺: 只用于小文件一般性存储; put逻辑可以优化完善;

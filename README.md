Sysmonitor
==========
An APP which can monitor the system and inquire hardware information

<img src='mdimage/image01.png' height='300px'/>
<img src='mdimage/image02.png' height='300px'/>

### System monitor
(1) RAM usage
```java
public static float getUsedPercentValue(Context context) {
		String dir = "/proc/meminfo";
		try {
			FileReader fr = new FileReader(dir);
			BufferedReader br = new BufferedReader(fr, 2048);
			String memoryLine = br.readLine();
			String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
			br.close();
			long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
			long availableSize = getAvailableMemory(context) / 1024;
			float percent = (float) ((totalMemorySize - availableSize) / (float) totalMemorySize);
			return percent;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
```

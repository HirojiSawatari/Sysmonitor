package hg.crx.sysmonitor;

import hg.crx.sysmonitor.utils.CameraUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.os.PowerProfile;

public class FragmentPage2 extends Fragment{

	LinearLayout linearLayout1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		View view = inflater.inflate(R.layout.fragment_2,container, false);
		//初始化定位点资料卡容器布局
		linearLayout1 = (LinearLayout)view.findViewById(R.id.InfoTable);
		//初始化表
		getTable();
		return view;
	}	
	
	public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);
	}
	
	public void getTable() {
		TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		final String imei = tm.getDeviceId();
		final String num = tm.getLine1Number();
		final String sv = tm.getDeviceSoftwareVersion();
		final String nm = tm.getNetworkOperatorName(); 
		final String simn = tm.getSimSerialNumber();
		final String sid = tm.getSubscriberId();
		
		final FileFilter CPU_FILTER = new FileFilter(){
			@Override
			public boolean accept(File arg0) {
				// TODO 自动生成的方法存根
				String path = arg0.getName();
				if(path.startsWith("cpu")){
					for(int i=3; i < path.length(); i++){
						if(path.charAt(i) < '0' || path.charAt(i) > '9'){
							return false;
						}
					}
					return true;
				}
				return false;
			}
		};
		
		final LinearLayout pwTable = new LinearLayout(getActivity());
		pwTable.setOrientation(LinearLayout.VERTICAL);
		pwTable.setBackgroundColor(Color.WHITE);
		pwTable.setPadding(30, 30, 30, 30);
		LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams
				(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		margin.setMargins(40, 35, 40, 0);	//设置布局距离上下左右距离
		
		TextView p_model = new TextView(getActivity());
		p_model.setTextColor(Color.BLACK);
		p_model.setText("手机型号：" + android.os.Build.MODEL);
		pwTable.addView(p_model);
		
		TextView p_brand = new TextView(getActivity());
		p_brand.setTextColor(Color.BLACK);
		p_brand.setText("手机品牌：" + android.os.Build.BRAND);
		pwTable.addView(p_brand);
		
		TextView p_company = new TextView(getActivity());
		p_company.setTextColor(Color.BLACK);
		p_company.setText("制造商：" + android.os.Build.MANUFACTURER);
		pwTable.addView(p_company);
		
		TextView p_and = new TextView(getActivity());
		p_and.setTextColor(Color.BLACK);
		p_and.setText("Android版本号：" + android.os.Build.VERSION.RELEASE);
		pwTable.addView(p_and);
		
		TextView blank1 = new TextView(getActivity());
		blank1.setText("  ");
		pwTable.addView(blank1);
		
		TextView p_cpuname = new TextView(getActivity());
		p_cpuname.setTextColor(Color.BLACK);
		p_cpuname.setText("CPU型号：" + android.os.Build.HARDWARE);
		pwTable.addView(p_cpuname);
		
		TextView p_cputype = new TextView(getActivity());
		p_cputype.setTextColor(Color.BLACK);
		p_cputype.setText("CPU架构：" + android.os.Build.CPU_ABI);
		pwTable.addView(p_cputype);
		
		int cores;
		try{
			cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
		} catch(SecurityException e){
			cores = 0;
		} catch(NullPointerException e){
			cores = 0;
		}
		TextView p_cpucore = new TextView(getActivity());
		p_cpucore.setTextColor(Color.BLACK);
		p_cpucore.setText("CPU核心数：" + cores);
		pwTable.addView(p_cpucore);
		
		int maxRate1 = 0;
		double temp;
		String maxRate = "0";
		try {
			FileReader fr1 = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
			BufferedReader br1 = new BufferedReader(fr1);
			String text1 = br1.readLine();
			maxRate1 = Integer.parseInt(text1);
			temp = maxRate1 / 1000.0;
			maxRate = String.format("%.1f", temp);
			br1.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
		}
		TextView p_cpurate = new TextView(getActivity());
		p_cpurate.setTextColor(Color.BLACK);
		p_cpurate.setText("CPU最高频率：" + maxRate + "MHz");
		pwTable.addView(p_cpurate);
		
		int minRate1 = 0;
		String minRate = "0";
		try {
			FileReader fr2 = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
			BufferedReader br2 = new BufferedReader(fr2);
			String text2 = br2.readLine();
			minRate1 = Integer.parseInt(text2);
			temp = minRate1 / 1000.0;
			minRate = String.format("%.1f", temp);
			br2.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
		}
		TextView p_cpurate2 = new TextView(getActivity());
		p_cpurate2.setTextColor(Color.BLACK);
		p_cpurate2.setText("CPU最低频率：" + minRate + "MHz");
		pwTable.addView(p_cpurate2);
		
		String text4 = "0";
		try {
			FileReader fr4 = new FileReader("/sys/class/thermal/thermal_zone9/subsystem/thermal_zone9/temp");
			BufferedReader br4 = new BufferedReader(fr4);
			text4 = br4.readLine();
			br4.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
		}
		TextView p_temp = new TextView(getActivity());
		p_temp.setTextColor(Color.BLACK);
		p_temp.setText("CPU温度：" + text4 + "℃");
		pwTable.addView(p_temp);
		
		TextView blank2 = new TextView(getActivity());
		blank2.setText("  ");
		pwTable.addView(blank2);
		
		//TextView blank3 = new TextView(getActivity());
		//blank3.setText("  ");
		//pwTable.addView(blank3);
		
		int memorySize = 0;
		String ramSize = "0";
		try {
			FileReader fr3 = new FileReader("/proc/meminfo");
			BufferedReader br3 = new BufferedReader(fr3);
			String memoryLine = br3.readLine();
			String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
			memorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
			temp = memorySize / 1000000.0;
			ramSize = String.format("%.1f", temp);
			br3.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
		}
		TextView p_ram = new TextView(getActivity());
		p_ram.setTextColor(Color.BLACK);
		p_ram.setText("RAM大小：" + ramSize + "GB");
		pwTable.addView(p_ram);
		
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		long romSize1 = blockSize * totalBlocks;
		temp = romSize1 / 1000000000.0;
		String romSize = String.format("%.1f", temp);
		TextView p_rom = new TextView(getActivity());
		p_rom.setTextColor(Color.BLACK);
		p_rom.setText("ROM大小：" + romSize + "GB");
		pwTable.addView(p_rom);
		
		PowerProfile pp = new PowerProfile(getActivity());
		int batteryRate = (int) pp.getBatteryCapacity();
		TextView p_batteryrate = new TextView(getActivity());
		p_batteryrate.setTextColor(Color.BLACK);
		p_batteryrate.setText("电池容量：" + batteryRate + "mAh");
		pwTable.addView(p_batteryrate);
		
		TextView p_front = new TextView(getActivity());
		p_front.setTextColor(Color.BLACK);
		p_front.setText("前置摄像头像素：" + CameraUtils.getCameraPixels(CameraUtils.HasFrontCamera()));
		pwTable.addView(p_front);
		
		TextView p_back = new TextView(getActivity());
		p_back.setTextColor(Color.BLACK);
		p_back.setText("后置摄像头像素：" + CameraUtils.getCameraPixels(CameraUtils.HasBackCamera()));
		pwTable.addView(p_back);
		
		WindowManager windowManager = getActivity().getWindowManager();    
        Display display = windowManager.getDefaultDisplay();    
        int screenWidth = screenWidth = display.getWidth();    
        int screenHeight = screenHeight = display.getHeight();
        TextView p_screen = new TextView(getActivity());
        p_screen.setTextColor(Color.BLACK);
        p_screen.setText("屏幕分辨率：" + screenHeight + "×" + screenWidth);
		pwTable.addView(p_screen);
		
		TextView blank4 = new TextView(getActivity());
		blank4.setText("  ");
		pwTable.addView(blank4);
		
		TextView p_num = new TextView(getActivity());
		p_num.setTextColor(Color.BLACK);
		p_num.setText("本机号码：" + num);
		pwTable.addView(p_num);
		
		TextView p_nm = new TextView(getActivity());
		p_nm.setTextColor(Color.BLACK);
		p_nm.setText("运营商名称：" + nm);
		pwTable.addView(p_nm);
		
		TextView p_imei = new TextView(getActivity());
		p_imei.setTextColor(Color.BLACK);
		p_imei.setText("IMEI码：" + imei);
		pwTable.addView(p_imei);
		
		TextView p_sv = new TextView(getActivity());
		p_sv.setTextColor(Color.BLACK);
		p_sv.setText("设备软件版本号：" + sv);
		pwTable.addView(p_sv);
		
		TextView p_sim = new TextView(getActivity());
		p_sim.setTextColor(Color.BLACK);
		p_sim.setText("SIM卡编号：" + simn);
		pwTable.addView(p_sim);
		
		TextView p_uid = new TextView(getActivity());
		p_uid.setTextColor(Color.BLACK);
		p_uid.setText("用户识别码：" + sid);
		pwTable.addView(p_uid);
		
		WifiManager wifiMng = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfor = wifiMng.getConnectionInfo();
		TextView p_mac = new TextView(getActivity());
		p_mac.setTextColor(Color.BLACK);
		p_mac.setText("MAC地址：" + wifiInfor.getMacAddress());
		pwTable.addView(p_mac);
		
		linearLayout1.addView(pwTable, margin);
	}
	 
}
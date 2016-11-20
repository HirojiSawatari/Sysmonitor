package hg.crx.sysmonitor;

import hg.crx.sysmonitor.utils.RoundedRectProgressBar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class FragmentPage1 extends Fragment{

	private static ActivityManager myActivityManager; 
	private RoundedRectProgressBar bar;
	private ImageView needleView;  //ָ��ͼƬ
	private ImageView needleView2;  //ָ��ͼƬ
	private ImageView needleView3;  //ָ��ͼƬ
	private Timer timer;  //ʱ��
	float usage = 0;	//CPU������
	long total = 0;
    long idle = 0;
    private float batteryLevel;
    private float batteryScale;
    private StatFs stat;
    long totalBlocks;
    long availableBlocks;
	
	private float degree = 0.0f;  //��¼ָ����ת
	private float degree2 = 0.0f;
	private float degree3 = 0.0f;
	private int degree4 = 0;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {  //�����Ǳ���ָ��ת������
			//RAMʹ���ʶ�ȡ
			//�Ǳ��������172�ȣ�������Լ��������
			degree = 172 * getUsedPercentValue(getActivity());
			RotateAnimation animation = new RotateAnimation(degree, 
					degree, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			animation.setDuration(1000);
			animation.setFillAfter(true);
			needleView.startAnimation(animation);
			
			//CPU������
			//�ķ�֮��Բ
			degree2 = (float) (2.7 * getUsage());
			RotateAnimation animation2 = new RotateAnimation(degree2, 
					degree2, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			animation2.setDuration(1000);
			animation2.setFillAfter(true);
			needleView2.startAnimation(animation2);
			
			//��ص���
			IntentFilter intentFilter=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            //ע��������Ի�ȡ������Ϣ
            getActivity().registerReceiver(broadcastReceiver, intentFilter);
            
            //ROMʹ����
            stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
            degree4 = (int) ((totalBlocks - availableBlocks) * 100 / totalBlocks);
            bar.setProgress(degree4);
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		View view = inflater.inflate(R.layout.fragment_1,container, false);
		return view;		
	}	
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		myActivityManager =(ActivityManager)getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
		bar = (RoundedRectProgressBar) getActivity().findViewById(R.id.bar);
		needleView = (ImageView) getActivity().findViewById(R.id.needle);
		needleView2 = (ImageView) getActivity().findViewById(R.id.needle2);
		needleView3 = (ImageView) getActivity().findViewById(R.id.needle3);
		// ��ʼת��
		timer = new Timer();
		// ����ÿһ��ת��һ��
		timer.schedule(new NeedleTask(), 0, 1000);
	}
	
	private class NeedleTask extends TimerTask {
		@Override
		public void run() {
			handler.sendEmptyMessage(0);
		}
	}
	
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
	
	private static long getAvailableMemory(Context context) {
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		myActivityManager.getMemoryInfo(mi);
		return mi.availMem;
	}
	
	public float getUsage( ){
        readUsage( );
        return usage;
    }
	
	public void readUsage( ){
		try{
			BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( "/proc/stat" ) ), 1000 );
			String load = reader.readLine();
			reader.close();     
			String[] toks = load.split(" ");
			long currTotal = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]);
			long currIdle = Long.parseLong(toks[5]);
			this.usage =(currTotal - total) * 100.0f / (currTotal - total + currIdle - idle);
			this.total = currTotal;
			this.idle = currIdle;
		}
	    catch( IOException ex ){
	    	ex.printStackTrace();           
	    }  
	}
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //��ȡ��ǰ��������δ��ȡ������ֵ����Ĭ��Ϊ0
            batteryLevel=intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            //��ȡ����������δ��ȡ��������ֵ����Ĭ��Ϊ100
            batteryScale=intent.getIntExtra(BatteryManager.EXTRA_SCALE,100);
            //��ʾ����
            degree3 = (float) (2.7 * (batteryLevel * 100 / batteryScale));
			RotateAnimation animation3 = new RotateAnimation(degree3, 
					degree3, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			animation3.setDuration(1000);
			animation3.setFillAfter(true);
			needleView3.startAnimation(animation3);
        }
    };
}
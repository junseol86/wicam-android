package com.wicam.d_default_handong_bus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.a_common_utils.WicamColors;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BusYgrAdapter extends BaseAdapter { //-------------------------- 배달업체 리스트
	
	private LayoutInflater inflater;
	private List<BusYgrData> list;
	private Context context;
	private SimpleDateFormat formatter = new SimpleDateFormat("HHmm", Locale.KOREA);
	boolean past_now = false;
	boolean past_now_checked = false;
	
	public BusYgrAdapter(Context context, List<BusYgrData> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public BusYgrData getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}
	
	public List<BusYgrData> getLists(){
		   return list;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		View v = convertView;
		
		if (v == null) {
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.d_handong_bus_ygr_item, null);
			holder = new ViewHolder();
			
			holder.times = (LinearLayout)v.findViewById(R.id.times);
			holder.descriptions = (LinearLayout)v.findViewById(R.id.handong_bus_descriptions);
			holder.divider = (LinearLayout)v.findViewById(R.id.divider);
			holder.line = (RelativeLayout)v.findViewById(R.id.line);
			holder.s1rl = (RelativeLayout)v.findViewById(R.id.s1rl);
			holder.s2rl = (RelativeLayout)v.findViewById(R.id.s2rl);
			holder.s3rl = (RelativeLayout)v.findViewById(R.id.s3rl);
			holder.s4rl = (RelativeLayout)v.findViewById(R.id.s4rl);
			holder.s5rl = (RelativeLayout)v.findViewById(R.id.s5rl);
			holder.s1 = (TextView)v.findViewById(R.id.s1);
			holder.s2 = (TextView)v.findViewById(R.id.s2);
			holder.s3 = (TextView)v.findViewById(R.id.s3);
			holder.s4 = (TextView)v.findViewById(R.id.s4);
			holder.s5 = (TextView)v.findViewById(R.id.s5);
			holder.s123 = (TextView)v.findViewById(R.id.s123);
			holder.s45 = (TextView)v.findViewById(R.id.s45);
			
			v.setTag(holder);	
		}
		else {
			holder = (ViewHolder)v.getTag();
		}
		
		if (!getItem(position).getDivider().equalsIgnoreCase("1")) {
			holder.divider.setVisibility(View.GONE);
			holder.times.setVisibility(View.VISIBLE);
			
			if (getItem(position).getHide_line())
				holder.line.setVisibility(View.GONE);
			else
				holder.line.setVisibility(View.VISIBLE);
			
		}
		else {
			holder.divider.setVisibility(View.VISIBLE);
			holder.times.setVisibility(View.GONE);
		}
		
		Date date = new Date();
		String format = formatter.format(date);
		
		if (!getItem(position).getDivider().equalsIgnoreCase("1")) {
			holder.times.setBackgroundColor(position % 2 == 0 ? new WicamColors().BLACK : new WicamColors().DARK_GRAY);
			if (toInt(format) > toInt(getItem(position).getS1())) {
				holder.s1.setTextColor(new WicamColors().TEXT_SEMI_LIGHT);
			}
			else {
				holder.s1.setTextColor(new WicamColors().WHITE);
				past_now = true;
			}
			holder.s1.setText(convert(getItem(position).getS1()));
			
			if (toInt(format) > toInt(getItem(position).getS2())) {
				holder.s2.setTextColor(new WicamColors().TEXT_SEMI_LIGHT);
			}
			else {
				holder.s2.setTextColor(new WicamColors().WHITE);
				past_now = true;
				}
			holder.s2.setText(convert(getItem(position).getS2()));
			
			if (toInt(format) > toInt(getItem(position).getS3())) {
				holder.s3.setTextColor(new WicamColors().TEXT_SEMI_LIGHT);
			}
			else {
				holder.s3.setTextColor(new WicamColors().WHITE);
				past_now = true;
			}
			holder.s3.setText(convert(getItem(position).getS3()));
			
			if (toInt(format) > toInt(getItem(position).getS4())) {
				holder.s4.setTextColor(new WicamColors().TEXT_SEMI_LIGHT);
			}
			else {
				holder.s4.setTextColor(new WicamColors().WHITE);
				past_now = true;
				}
			holder.s4.setText(convert(getItem(position).getS4()));

			if (toInt(format) > toInt(getItem(position).getS5())) {
				holder.s5.setTextColor(new WicamColors().TEXT_SEMI_LIGHT);
			}
			else {
				holder.s5.setTextColor(new WicamColors().WHITE);
				past_now = true;
			}
			holder.s5.setText(convert(getItem(position).getS5()));

			holder.s123.setText(getItem(position).getS123());
			holder.s45.setText(getItem(position).getS45());

			holder.s1rl.setVisibility(Singleton.getOut_all_in() == 2 ? View.GONE : View.VISIBLE);
			holder.s2rl.setVisibility(Singleton.getOut_all_in() == 2 ? View.GONE : View.VISIBLE);
			holder.s4rl.setVisibility(Singleton.getOut_all_in() == 0 ? View.GONE : View.VISIBLE);
			holder.s5rl.setVisibility(Singleton.getOut_all_in() == 0 ? View.GONE : View.VISIBLE);

			holder.times.setVisibility((Singleton.getOut_all_in() == 0 && getItem(position).getS1().equalsIgnoreCase("") && getItem(position).getS2().equalsIgnoreCase(""))
					|| (Singleton.getOut_all_in() == 2 && getItem(position).getS4().equalsIgnoreCase("") && getItem(position).getS5().equalsIgnoreCase(""))
					? View.GONE : View.VISIBLE);
			holder.descriptions.setVisibility((Singleton.getOut_all_in() == 0 && getItem(position).getS1().equalsIgnoreCase("") && getItem(position).getS2().equalsIgnoreCase(""))
					|| (Singleton.getOut_all_in() == 2 && getItem(position).getS4().equalsIgnoreCase("") && getItem(position).getS5().equalsIgnoreCase(""))
					? View.GONE : View.VISIBLE);
			holder.s123.setVisibility(Singleton.getOut_all_in() == 0 || Singleton.getOut_all_in() == 2 ? View.GONE : View.VISIBLE);
			holder.s45.setVisibility(Singleton.getOut_all_in() == 0 || Singleton.getOut_all_in() == 2 ? View.GONE : View.VISIBLE);

			holder.line.setVisibility(getItem(position).getPast() ? View.VISIBLE : View.GONE);
			
		}
		
		return v;
	}
	
	public String convert(String str) {
		if (!isInt(str))
			return str;
		else {
			if (str.substring(0, str.length() - 2).equals("12") || str.substring(0, str.length() - 2).equals("24"))
				str = "12" + ":" + str.subSequence(str.length() - 2, str.length());
			else
				str = String.valueOf(Integer.parseInt(str.substring(0, str.length() - 2)) % 12) + ":" + str.subSequence(str.length() - 2, str.length());
			
			return str;
		}
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch(NumberFormatException e) {
			return false;
		} catch(NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	public int toInt(String str) {
		if (isInt(str))
			return Integer.parseInt(str.replace(":", ""));
		else
			return 0;
	}
	
	static class ViewHolder{
			LinearLayout times, descriptions, divider;
			RelativeLayout line, s1rl, s2rl, s3rl, s4rl, s5rl;
			TextView s1, s2, s3, s4, s5, s123, s45;
	   }
}
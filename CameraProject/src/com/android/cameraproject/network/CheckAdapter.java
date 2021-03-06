package com.android.cameraproject.network;

import java.util.List;

import com.android.cameraproject.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

// List adapter for SendActivity
public class CheckAdapter extends ArrayAdapter<CheckableItem>
{
	// private static final String TAG = "CheckAdapter";
	
	private Context context;
	private int layoutResourceId;
	private List<CheckableItem> items;

	public CheckAdapter(Context context, int textViewResourceId, List<CheckableItem> objects)
	{
		super(context, textViewResourceId, objects);
		
		this.context = context;
		layoutResourceId = textViewResourceId;
		items = objects;
		// Log.d(TAG, items.get(items.size()-1).getUsername() + "");
	}
	
	public List<CheckableItem> getItems()
	{
		return items;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		final ItemHolder holder = new ItemHolder();
		
		
		if(row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder.name = (TextView)row.findViewById(R.id.friendName);
			holder.box = (CheckBox)row.findViewById(R.id.isSelected);
			
			final CheckableItem item = items.get(position);
			
			holder.box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
				{
					
					// Log.d(TAG, item.getUsername() + "");
					
					item.setChecked(isChecked);
					
					// Log.d(TAG, item.isChecked() + "");
					
					holder.box.setTag(item);
				}
			});
			row.setTag(holder);
		}
		else
		{
			row = convertView;
			((ItemHolder)row.getTag()).box.setTag(items.get(position));
			holder.name = (TextView)row.findViewById(R.id.friendName);
			holder.box = (CheckBox)row.findViewById(R.id.isSelected);
		}
		CheckableItem item = items.get(position);
		holder.name.setText(item.getUsername());
		holder.box.setChecked(item.isChecked());
		
		return row;
		
	}
	static class ItemHolder
	{
		TextView name;
		CheckBox box;
	}
}

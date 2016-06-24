package com.code3apps.para.ff;

import java.util.List;

import com.code3apps.para.R;
import com.code3apps.para.beans.CheckListDetailBean;
import com.code3apps.para.utils.Const;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckDetailsListAdapter extends BaseAdapter {
    // for debug
    private static final String TAG = "CheckDetailsListAdapter";
    private static final boolean sEnableDebug = true;
    private static final String TAG_PREFIX = "FireFighter =>: ";

    private LayoutInflater mInflater = null;
    private List<CheckListDetailBean> mDetailBeans = null;

    public CheckDetailsListAdapter(Context context) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    public void setDetailBeans(List<CheckListDetailBean> pCheckListDetailBeans) {
        mDetailBeans = pCheckListDetailBeans;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        if (mDetailBeans != null) {
            return mDetailBeans.size();
        }
        return 0;
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return mDetailBeans.get(arg0);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        CheckListDetailBean detailBean;
        String strPre = "";
        String strInfo1 = "";
        String strInfo2 = "";
        String strInfo3 = "";

        // TODO Auto-generated method stub
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.chelist_detail_item, null);
            holder.precheckTxt = (TextView) convertView
                    .findViewById(R.id.precheck);

            // holder.nameLinView = (LinearLayout) convertView
            // .findViewById(R.id.name);
            holder.checkboxImg = (ImageView) convertView
                    .findViewById(R.id.chekbox_img);
            holder.nameTxt = (TextView) convertView.findViewById(R.id.txtname);

            holder.info1LinView = (LinearLayout) convertView
                    .findViewById(R.id.info1);
            holder.info1Txt = (TextView) convertView
                    .findViewById(R.id.txtinfo1);

            holder.info2LinView = (LinearLayout) convertView
                    .findViewById(R.id.info2);
            holder.info2Txt = (TextView) convertView
                    .findViewById(R.id.txtinfo2);

            holder.info3LinView = (LinearLayout) convertView
                    .findViewById(R.id.info3);
            holder.info3Txt = (TextView) convertView
                    .findViewById(R.id.txtinfo3);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        detailBean = mDetailBeans.get(position);
        // holder.reminderTxtView.setText(vcReminder.getNextDue() +" : " +
        // vcReminder.getVccine());
        strPre = detailBean.getPrecheck();

        // --- precheck ---
        if (strPre != null && strPre.length() > 0) {
            holder.precheckTxt.setVisibility(View.VISIBLE);
            holder.precheckTxt.setText(strPre);
        } else {
            // holder.precheckTxt.setVisibility(View.GONE);
        }

        // --- name ---
        if (detailBean.getChecked() == 0) {
            log("detailBean.getChecked() is false");
            holder.checkboxImg.setImageResource(R.drawable.box_blank);
        } else {
            log("detailBean.getChecked() is true");
            holder.checkboxImg.setImageResource(R.drawable.box_checked);
        }
        holder.nameTxt.setText(detailBean.getName());

        strInfo1 = detailBean.getInformation1();
        if (strInfo1 != null && strInfo1.length() > 0) {
            holder.info1LinView.setVisibility(View.VISIBLE);
            holder.info1Txt.setText(strInfo1);
        } else {
            holder.info1LinView.setVisibility(View.GONE);
        }

        strInfo2 = detailBean.getInformation2();
        if (strInfo2 != null && strInfo2.length() > 0) {
            holder.info2LinView.setVisibility(View.VISIBLE);
            holder.info2Txt.setText(strInfo2);
        } else {
            holder.info2LinView.setVisibility(View.GONE);
        }

        strInfo3 = detailBean.getInformation3();
        if (strInfo3 != null && strInfo3.length() > 0) {
            holder.info3LinView.setVisibility(View.VISIBLE);
            holder.info3Txt.setText(strInfo3);
        } else {
            holder.info3LinView.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        private TextView precheckTxt = null;

        // private LinearLayout nameLinView = null;
        private ImageView checkboxImg = null;
        private TextView nameTxt = null;

        private LinearLayout info1LinView = null;
        private TextView info1Txt = null;

        private LinearLayout info2LinView = null;
        private TextView info2Txt = null;

        private LinearLayout info3LinView = null;
        private TextView info3Txt = null;
    }

    // ---------------------------- for debug ---------------------------------
    private static void log(String tag, String msg) {
        if (sEnableDebug && Const.DEBUG_PHASE) {
            Log.d(tag, TAG_PREFIX + msg);
        }
    }

    private static void log(String msg) {
        if (sEnableDebug && Const.DEBUG_PHASE) {
            log(TAG, msg);
        }
    }
}

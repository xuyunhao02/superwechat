package cn.ucai.superwechat.test;

import android.widget.TextView;

import cn.ucai.superwechat.DemoHXSDKHelper;
import cn.ucai.superwechat.applib.controller.HXSDKHelper;
import cn.ucai.superwechat.domain.EMUser;

/**
 * Created by Administrator on 2016/5/24 0024.
 */
public class test {
    public static void setCurrentUserNick(TextView textView){
        EMUser user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
        if(textView != null){
             textView.setText(user.getNick());
        }
    }
}

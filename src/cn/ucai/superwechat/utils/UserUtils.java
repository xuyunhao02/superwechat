package cn.ucai.superwechat.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.easemob.util.HanziToPinyin;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.ucai.superwechat.Constant;
import cn.ucai.superwechat.DemoHXSDKHelper;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.applib.controller.HXSDKHelper;
import cn.ucai.superwechat.bean.Contact;
import cn.ucai.superwechat.bean.Group;
import cn.ucai.superwechat.bean.User;
import cn.ucai.superwechat.data.RequestManager;
import cn.ucai.superwechat.domain.EMUser;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static EMUser getUserInfo(String username){
        EMUser user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(username);
        if(user == null){
            user = new EMUser(username);
        }

        if(user != null){
            //demo没有这些数据，临时填充
        	if(TextUtils.isEmpty(user.getNick()))
        		user.setNick(username);
        }
        return user;
    }

	public static Contact getUserBeanInfo(String username) {
		Contact contact = SuperWeChatApplication.getInstance().getUserList().get(username);
		return contact;
	}

	/**
     * 设置用户头像
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	EMUser user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }
	public static void setUserHearder(String username, Contact user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getMUserNick())) {
			headerName = user.getMUserNick();
		} else {
			headerName = user.getMContactCname();
		}

		if (username.equals(Constant.NEW_FRIENDS_USERNAME)|| username.equals(Constant.GROUP_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.trim().substring(0, 1)).get(0).target.substring(0, 1)
					.toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}

	public static void setUserBeanAvatar(String userName, NetworkImageView imageView){
		Contact contact = getUserBeanInfo(userName);
		if(contact!=null&&contact.getMContactCname()!=null){
			setUserAvatar(getAvatarPath(userName),imageView);
		}
	}
	public static void setUserBeanAvatar(User user, NetworkImageView imageView){
		if(user!=null&&user.getMUserName()!=null){
			setUserAvatar(getAvatarPath(user.getMUserName()),imageView);
		}
	}

	/**
	 * 加载服务器群组头像
	 */
	public static void setGroupBeanAvatar(String mGroupHxid, NetworkImageView imageView) {
		if (mGroupHxid != null && !mGroupHxid.isEmpty()) {
			setGroupAvatar(getGroupPath(mGroupHxid), imageView);
		}
	}

	private static String getGroupPath(String hxid) {
		if (hxid == null || hxid.isEmpty()) return null;
		return I.REQUEST_DOWNLOAD_AVATAR_GROUP + hxid;
	}

	private static void setGroupAvatar(String url, NetworkImageView imageView) {
		if (url == null || url.isEmpty()) return;
		imageView.setDefaultImageResId(R.drawable.group_icon);
		imageView.setImageUrl(url, RequestManager.getImageLoader());
		imageView.setErrorImageResId(R.drawable.group_icon);
	}


	private static void setUserAvatar(String url, NetworkImageView imageView) {
		if (url == null || url.isEmpty())return;

		imageView.setDefaultImageResId(R.drawable.default_avatar);
		imageView.setImageUrl(url, RequestManager.getImageLoader());
		imageView.setErrorImageResId(R.drawable.default_avatar);

	}


	public static String getAvatarPath(String userName) {
		if(userName==null || userName.isEmpty())return null;
		return I.REQUEST_DOWNLOAD_AVATAR_USER + userName;
	}


	/**
     * 设置当前用户头像
     */
	public static void setCurrentUserAvatar(Context context, ImageView imageView) {
		EMUser user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if (user != null && user.getAvatar() != null) {
			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
		} else {
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}

	public static void setCurrentUserAvatar(NetworkImageView imageView) {
		User user = SuperWeChatApplication.getInstance().getUser();
		if (user != null) {
			setUserAvatar(getAvatarPath(user.getMUserName()), imageView);
		}
	}

    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	EMUser user = getUserInfo(username);
    	if(user != null){
    		textView.setText(user.getNick());
    	}else{
    		textView.setText(username);
    	}
    }
	public static void setUserBeanNick(String userName,TextView textView){
		Contact contact = getUserBeanInfo(userName);
		if(contact!=null){
			if(contact.getMUserNick()!=null){
				textView.setText(contact.getMUserNick());
			}else if (contact.getMContactCname()!=null){
				textView.setText(contact.getMContactCname());
			}
		}else {
			textView.setText(userName);
		}
	}


	/**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView){
		EMUser user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if(textView != null){
			textView.setText(user.getNick());
		}
	}
	public static void setCurrentUserBeanNick(TextView textView){
		User user = SuperWeChatApplication.getInstance().getUser();
		if(user != null&&user.getMUserNick()!=null&&textView!=null){
			textView.setText(user.getMUserNick());
		}
	}


    /**
     * 保存或更新某个用户
     */
	public static void saveUserInfo(EMUser newUser) {
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
	}



	//汉子转拼音
	public static String getPinYinFromHanZi(String hanzi) {
		String pinyin = "";

		for(int i=0;i<hanzi.length();i++){
			String s = hanzi.substring(i,i+1);
			pinyin = pinyin + HanziToPinyin.getInstance()
					.get(s).get(0).target.toLowerCase();
		}
		return pinyin;
	}

	public static Group getGroupBeanFromHXID(String hxid) {
		if (hxid != null && !hxid.isEmpty()) {
			ArrayList<Group> groupList = SuperWeChatApplication.getInstance().getGroupList();
			for (Group group : groupList) {
				if (group.getMGroupHxid().equals(hxid)) {
					return group;
				}
			}
		}
		return null;
	}

}

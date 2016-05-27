/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;

import java.io.File;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.Contact;
import cn.ucai.superwechat.bean.Group;
import cn.ucai.superwechat.bean.User;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.listener.OnSetAvatarListener;
import cn.ucai.superwechat.utils.ImageUtils;
import cn.ucai.superwechat.utils.Utils;

public class NewGroupActivity extends BaseActivity {
	private EditText groupNameEditText;
	private ProgressDialog progressDialog;
	private EditText introductionEditText;
	private CheckBox checkBox;
	private CheckBox memberCheckbox;
	private LinearLayout openInviteContainer;

	ImageView ivAvatar;
	NewGroupActivity mContext;
	OnSetAvatarListener mOnSetAvatarListener;
	String avatarName;//
	ProgressDialog pd;

	private static final int CREAT_NEW_GROUP = 100;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(cn.ucai.superwechat.R.layout.activity_new_group);
		mContext = this;
		initView();
		setListener();
	}

	private void setListener() {
		setOnCheckchangeListener();
		setSaveGroupClickListener();
		setGroupIconClickListener();

	}
//实例化上传群头像的；
	private void setGroupIconClickListener() {
		//嵌套一个监听事件；
		findViewById(R.id.group_avatar).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnSetAvatarListener = new OnSetAvatarListener(mContext, R.id.layout_new_group, getAvatarName(), I.AVATAR_TYPE_GROUP_PATH);
			}
		});

	}

	private String getAvatarName() {
		avatarName = System.currentTimeMillis() + "";
		return avatarName;
	}
	private void setSaveGroupClickListener() {
              findViewById(R.id.btnSaveGroup).setOnClickListener(new View.OnClickListener() {
				  @Override
				  public void onClick(View v) {
					  String str6 = getResources().getString(cn.ucai.superwechat.R.string.Group_name_cannot_be_empty);
					  String name = groupNameEditText.getText().toString();
					  if (TextUtils.isEmpty(name)) {
						  Intent intent = new Intent(mContext, AlertDialog.class);
						  intent.putExtra("msg", str6);
						  startActivity(intent);
					  } else {
						  // 进通讯录选人
						  startActivityForResult(new Intent(mContext, GroupPickContactsActivity.class).putExtra("groupName", name), CREAT_NEW_GROUP);
					  }

				  }
			  });

	}

	private void setOnCheckchangeListener() {
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					openInviteContainer.setVisibility(View.INVISIBLE);
				}else{
					openInviteContainer.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void initView() {
		groupNameEditText = (EditText) findViewById(cn.ucai.superwechat.R.id.edit_group_name);
		introductionEditText = (EditText) findViewById(cn.ucai.superwechat.R.id.edit_group_introduction);
		checkBox = (CheckBox) findViewById(cn.ucai.superwechat.R.id.cb_public);
		memberCheckbox = (CheckBox) findViewById(cn.ucai.superwechat.R.id.cb_member_inviter);
		openInviteContainer = (LinearLayout) findViewById(cn.ucai.superwechat.R.id.ll_open_invite);
		ivAvatar = (ImageView) findViewById(R.id.group_avatar);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != RESULT_OK) {
			return;
		}
		if (resultCode == RESULT_OK) {
			setProgressDialog();
			//创建群组
			//创建环信的群组，拿到HXID
			//创建远端的群组
			//选通讯录联系人
			createNewGroup(data);
		} else {

			mOnSetAvatarListener.setAvatar(requestCode, data, ivAvatar);
		}
	}

	private void createNewGroup(final Intent data) {
		final String st2 = getResources().getString(cn.ucai.superwechat.R.string.Failed_to_create_groups);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 调用sdk创建群组方法
				String groupName = groupNameEditText.getText().toString().trim();
				String desc = introductionEditText.getText().toString();
				Contact[] contacts = (Contact[]) data.getSerializableExtra("newmembers");
				String[] members = null;
				String[] memberIds = null;
				if (contacts != null) {
					members = new String[contacts.length];
					memberIds = new String[contacts.length];
					for (int i = 0; i < contacts.length; i++) {
						members[i] = contacts[i].getMContactCname() + ",";
						memberIds[i] = contacts[i].getMContactId() + ",";
					}
				}
				EMGroup emGroup;
				try {
					if(checkBox.isChecked()){
						//创建公开群，此种方式创建的群，可以自由加入
						//创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
						emGroup=	EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true,200);
					}else{
						//创建不公开群
						emGroup=EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(),200);
					}
					creatNewGroupAppServer(emGroup.getGroupId(), groupName, desc, contacts);
					//创建成功时
					String hxid = emGroup.getGroupId();
					//
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							finish();
						}
					});
				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}

			}
		}).start();

	}

	private void creatNewGroupAppServer(String hxid, String groupName, String desc, final Contact[] contacts) {
		User user = SuperWeChatApplication.getInstance().getUser();
		boolean isPublic = checkBox.isChecked();
		boolean isInvites = memberCheckbox.isChecked();

		//首先注册远端服务器账号，并上传头像----okhttp
		//注册环信的账号
		//如果环信注册失败，调用取消注册的方法，删除远端账号和图片
		File file = new File(ImageUtils.getAvatarPath(mContext, I.AVATAR_TYPE_GROUP_PATH),
				avatarName + I.AVATAR_SUFFIX_JPG);
		OkHttpUtils<Group> utils = new OkHttpUtils<Group>();
		utils.url(SuperWeChatApplication.SERVER_ROOT)
				.addParam(I.KEY_REQUEST,I.REQUEST_CREATE_GROUP)
				.addParam(I.Group.HX_ID, hxid)
				.addParam(I.Group.NAME, groupName)
				.addParam(I.Group.DESCRIPTION, desc)
				.addParam(I.Group.OWNER, user.getMUserName())
				.addParam(I.Group.IS_PUBLIC, isPublic + "")
				.addParam(I.Group.ALLOW_INVITES, isInvites + "")
				.addParam(I.User.USER_ID,user.getMUserName())
				.targetClass(Group.class)
				.addFile(file)
				.execute(new OkHttpUtils.OnCompleteListener<Group>() {
					@Override
					public void onSuccess(Group group) {
						if (group.isResult()) {
							if (contacts != null) {
								addGroupMembers(contacts);
							} else {
								SuperWeChatApplication.getInstance().getGroupList().add(group);
								progressDialog.dismiss();
								setResult(RESULT_OK);
								mContext.sendBroadcast(new Intent("update_group_list"));
								finish();
							}
						} else {
							Utils.showToast(mContext, Utils.getResourceString(mContext, group.getMsg()), Toast.LENGTH_SHORT);
                            pd.dismiss();

						}
					}

					@Override
					public void onError(String error) {
						pd.dismiss();
						Utils.showToast(mContext, error, Toast.LENGTH_SHORT);

					}
				});
	}

	private void addGroupMembers(Contact[] nembers) {


	}

	private void setProgressDialog() {
		String st1 = getResources().getString(cn.ucai.superwechat.R.string.Is_to_create_a_group_chat);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(st1);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	public void back(View view) {
		finish();
	}
}

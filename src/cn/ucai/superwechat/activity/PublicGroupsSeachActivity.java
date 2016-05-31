package cn.ucai.superwechat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.easemob.EMError;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Group;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.utils.Utils;

public class PublicGroupsSeachActivity extends BaseActivity{
    private RelativeLayout containerLayout;
    private EditText idET;
    private TextView nameText;
    public static Group searchedGroup;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        initView();
        searchedGroup = null;
    }

    private void initView() {
        setContentView(cn.ucai.superwechat.R.layout.activity_public_groups_search);
        containerLayout = (RelativeLayout) findViewById(cn.ucai.superwechat.R.id.rl_searched_group);
        idET = (EditText) findViewById(cn.ucai.superwechat.R.id.et_search_id);
        nameText = (TextView) findViewById(cn.ucai.superwechat.R.id.name);
    }

    /**
     * 搜索
     */
    public void searchGroup(View v){
        if(TextUtils.isEmpty(idET.getText())){
            return;
        }
        
        pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(cn.ucai.superwechat.R.string.searching));
        pd.setCancelable(false);
        pd.show();

        try {
            String path = new ApiParams()
                    .with(I.Group.HX_ID, idET.getText().toString())
                    .getRequestUrl(I.REQUEST_FIND_PUBLIC_GROUP_BY_HXID);
            executeRequest(new GsonRequest<Group>(path, Group.class,
                    responseFindPublicGroupListener(), errorListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {

            public void run() {
                try {
                    searchedGroup = EMGroupManager.getInstance().getGroupFromServer(idET.getText().toString());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            containerLayout.setVisibility(View.VISIBLE);
                            nameText.setText(searchedGroup.getMGroupName());
                        }
                    });
                    
                } catch (final EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            searchedGroup = null;
                            containerLayout.setVisibility(View.GONE);
                            if(e.getErrorCode() == EMError.GROUP_NOT_EXIST){
                                Toast.makeText(getApplicationContext(), getResources().getString(cn.ucai.superwechat.R.string.group_not_existed), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), getResources().getString(cn.ucai.superwechat.R.string.group_search_failed) + " : " + getString(cn.ucai.superwechat.R.string.connect_failuer_toast), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
        
    }

    private Response.Listener<Group> responseFindPublicGroupListener() {
        return new Response.Listener<Group>() {
            @Override
            public void onResponse(Group group) {
                if (group != null) {
                    containerLayout.setVisibility(View.VISIBLE);
                    nameText.setText(searchedGroup.getMGroupName());
                } else {
                    Utils.showToast(PublicGroupsSeachActivity.this, R.string.group_not_existed,
                            Toast.LENGTH_LONG);
                }
                pd.dismiss();
            }
        };
    }


    /**
     * 点击搜索到的群组进入群组信息页面
     */
    public void enterToDetails(View view){
        startActivity(new Intent(this, GroupSimpleDetailActivity.class));
    }
}

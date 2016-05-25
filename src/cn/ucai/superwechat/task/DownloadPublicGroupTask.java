package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.activity.BaseActivity;
import cn.ucai.superwechat.bean.Group;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by sks on 2016/5/23.
 */
public class DownloadPublicGroupTask extends BaseActivity{
    private static final String TAG = DownloadPublicGroupTask.class.getName();
    Context mcontext;
    String username;
    int page_id;
    int page_size;
    String path;

    public DownloadPublicGroupTask(Context mcontext, String username, int page_id, int page_size) {
        this.mcontext = mcontext;
        this.username = username;
        this.page_id = page_id;
        this.page_size = page_size;
        initPath();
    }

    private void initPath() {
        try {
            path = new ApiParams()
                    .with(I.User.USER_NAME, username)
                    .with(I.PAGE_ID, page_id+"")
                    .with(I.PAGE_SIZE, page_size+"")
                    .getRequestUrl(I.REQUEST_FIND_PUBLIC_GROUPS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void execute() {
        executeRequest(new GsonRequest<Group[]>(path,Group[].class,
                responseDownloadPublicGroupTaskListener(),errorListener()));
    }

    private Response.Listener<Group[]> responseDownloadPublicGroupTaskListener() {
        return new Response.Listener<Group[]>() {
            @Override
            public void onResponse(Group[] groups) {
                if (groups != null && groups.length > 1) {
                    ArrayList<Group> groupList =
                            SuperWeChatApplication.getInstance().getGroupList();
                    ArrayList<Group> list = Utils.array2List(groups);
                    for (Group g : list) {
                        if (!groupList.contains(g)) {
                            groupList.add(g);
                        }
                    }
                    mcontext.sendStickyBroadcast(new Intent("update_public_group"));
                }
            }
        };
    }


}

package com.example.wyf.classchat.util;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.example.wyf.classchat.bean.GroupBmob;
import com.example.wyf.classchat.bean.GroupMember;
import com.example.wyf.classchat.bean.NoticeBean;
import com.example.wyf.classchat.bean.NoticeDateBean;
import com.example.wyf.classchat.bean.NoticeDiscussBean;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.bean.ReaderBean;
import com.example.wyf.classchat.bean.RegisterHistory;
import com.example.wyf.classchat.bean.RegisterInfo;
import com.example.wyf.classchat.bean.RegisterStatus;
import com.example.wyf.classchat.listener.BmobCallback;
import com.example.wyf.classchat.file.bean.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteBatchListener;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * Created by Administrator on 2017/10/19.
 */

public class BmobUtils<T extends BmobObject> {

    private static BmobUtils bmobUtils = null;

    public  static synchronized BmobUtils getInstance() {
        if (bmobUtils == null) {
            bmobUtils = new BmobUtils();
        }
        return bmobUtils;
    }

    /**
     * 保存数据到Bmob
     *
     * @param t        需要保存的bean
     * @param callback
     */
    public void saveData(T t, final BmobCallback callback) {
        t.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    callback.success(objectId);
                } else {
                    callback.Error(e);
                }
            }
        });
    }

    /**
     * 删除Bmob的数据
     *
     * @param bean
     * @param callback
     */
    public void deleteDate(NoticeBean bean, final BmobCallback callback) {


        bean.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    callback.success(e);
                } else {
                    callback.Error(e);
                }
            }
        });

    }

    /**
     * 批量删除数据
     *
     * @param list
     * @param callback
     */
    public void deleteBatchData(ArrayList<BmobObject> list, final BmobCallback callback) {
        new BmobBatch().deleteBatch(list).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < o.size(); i++) {
                        BatchResult result = o.get(i);
                        BmobException ex = result.getError();
                        if (ex == null) {
                            callback.success(o);
                        } else {
                            callback.fail();

                        }
                    }

                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    callback.Error(e);
                }
            }
        });
    }

    /**
     * 批量更新数据
     *
     * @param list
     * @param callback
     */
    public void updateBatchData(ArrayList<BmobObject> list, final BmobCallback callback) {
        new BmobBatch().updateBatch(list).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < o.size(); i++) {
                        BatchResult result = o.get(i);
                        BmobException ex = result.getError();
                        if (ex == null) {
                            callback.success(o);
                        } else {
                            callback.fail();
                        }
                    }

                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    callback.Error(e);
                }
            }
        });
    }

    /**
     * 批量删除文件
     *
     * @param urls
     * @param callback
     */
    public void deleteBatchFile(String [] urls, final BmobCallback callback) {
        BmobFile.deleteBatch(urls, new DeleteBatchListener() {

            @Override
            public void done(String[] failUrls, BmobException e) {
                if(e==null){
                   Log.i("全部删除成功","");
                    callback.success(null);
                }else{
                    if(failUrls!=null){
//                        Log.i("删除失败个数：",failUrls.length+","+e.toString());
                    }else{
                        Log.i("全部文件删除失败：",e.getErrorCode()+","+e.toString());
                        callback.Error(e);
                    }
                }
            }
        });
    }

    /**
     * 更新数据
     *
     * @param t
     * @param objectId 要更新数据的objectId
     * @param callback
     */
    public void update(T t, String objectId, final BmobCallback callback) {

        t.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    callback.success(e);
                } else {
                    callback.Error(e);
                }
            }
        });
    }

    /**
     * 文件上传
     * @param fileInfo
     * @param callback
     */
    public void upLoadFiles(final ArrayList<FileInfo> fileInfo, final BmobCallback callback,final View view) {
        final String[] filePaths = new String[fileInfo.size()];

        for (int i = 0; i < fileInfo.size(); i++) {
            filePaths[i] = fileInfo.get(i).getPath();
        }

//批量上传是会依次上传文件夹里面的文件
        Bmob.uploadBatch(filePaths, new UploadBatchListener() {

            int i = 1;
            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                // TODO Auto-generated method stub
                Log.i("文件上传", "insertBatchDatasWithOne -onSuccess :" + urls.size() + "-----" + files + "----" + urls);

                int count =filePaths.length;

                while (count>0){

                    if (count==urls.size()){
                        fileInfo.get(count-1).setFile(files.get(count-1));
                        if (filePaths.length==urls.size()){
                            callback.success(fileInfo);
                        }
                    }
                    count--;
                }

            }

            @Override
            public void onError(int statuscode, String errormsg) {
                // TODO Auto-generated method stub
                callback.Error(null);
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total,
                                   int totalPercent) {
                // TODO Auto-generated method stub
                if (view!=null&&curIndex==total){
                    view.setClickable(true);
                }


            }
        });


    }

    /**
     * 批量插入数据
     *
     * @param files
     */
    public void insertBatch(ArrayList<BmobObject> files, final BmobCallback callback) {
        Log.i("测试insertBatch ","几次insertBatch");
        new BmobBatch().insertBatch(files).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if (e == null) {
                    for (int i = 0; i < o.size(); i++) {
                        BatchResult result = o.get(i);
                        BmobException ex = result.getError();
                        if (ex == null) {
                            Log.e("第" + i + "个数据批量添加成功：", result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt());
                            callback.success(i);
                        } else {
                            Log.e("第" + i + "个数据批量添加失败：", ex.getMessage() + "," + ex.getErrorCode());
                            callback.fail();
                        }
                    }
                } else {
                    Log.e("bmob数据批量添加失败", "失败：" + e.getMessage() + "," + e.getErrorCode());

                }
            }
        });
    }


    /**
     * 查询公告数据
     *
     * @param param    查询的参数
     * @param limit    查询数据上限
     * @param callback 查询回调
     */
    public void querryNoticeData(Map<String, String> param, int limit, final BmobCallback callback) {
        BmobQuery<NoticeBean> query = new BmobQuery<NoticeBean>();

        for (Map.Entry<String, String> entry : param.entrySet()) {
            query.addWhereEqualTo(entry.getKey(), entry.getValue());
        }
        query.setLimit(limit);
        query.findObjects(new FindListener<NoticeBean>() {
            @Override
            public void done(List<NoticeBean> object, BmobException e) {
                if (e == null) {
                    if (object.size() != 0) {
                        callback.success(object);
                    } else {
                        callback.fail();
                    }
                } else {
                    callback.Error(e);
                }
            }
        });
    }


    /**
     * 查询看公告的人
     *
     * @param params
     * @param limit
     * @param callback
     */
    public void querryReader(HashMap<String, String> params, int limit, final BmobCallback callback) {
        BmobQuery<ReaderBean> query = new BmobQuery<ReaderBean>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            query.addWhereEqualTo(entry.getKey(), entry.getValue());
        }
        query.setLimit(limit);
        query.findObjects(new FindListener<ReaderBean>() {
            @Override
            public void done(List<ReaderBean> object, BmobException e) {
                if (e == null) {
                    if (object.size() == 0) {
                        callback.fail();


                    } else {
                        callback.success(object);
                    }
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

    }

    /**
     * 查看阅读公告的总人数
     *
     * @param callback
     */
    public void querryCount(final BmobCallback callback) {
        BmobQuery<ReaderBean> query = new BmobQuery<ReaderBean>();
        query.setLimit(500);
        query.findObjects(new FindListener<ReaderBean>() {
            @Override
            public void done(final List<ReaderBean> object, BmobException e) {
                if (e == null) {

                    callback.success(object);
                } else {
                    callback.fail();
                }
            }
        });
    }

    /**
     * 查询公告讨论区的数据
     *
     * @param params
     * @param limit
     * @param callback
     */
    public void querryNoticeDiscussData(HashMap<String, String> params, int limit, final BmobCallback callback) {
        BmobQuery<NoticeDiscussBean> query = new BmobQuery<NoticeDiscussBean>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            query.addWhereEqualTo(entry.getKey(), entry.getValue());
        }
        query.setLimit(limit);
        query.findObjects(new FindListener<NoticeDiscussBean>() {
            @Override
            public void done(List<NoticeDiscussBean> object, BmobException e) {
                if (e == null) {
                    if (object.size() == 0) {
                        callback.fail();

                    } else {
                        callback.success(object);
                    }
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }
    /**
     * 查询公告文件的数据
     *
     * @param params
     * @param limit
     * @param callback
     */
    public void querryNoticeFileInfos(HashMap<String, String> params, int limit, final BmobCallback callback) {
        BmobQuery<FileInfo> query = new BmobQuery<FileInfo>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            query.addWhereEqualTo(entry.getKey(), entry.getValue());
        }
        query.setLimit(limit);
        query.findObjects(new FindListener<FileInfo>() {
            @Override
            public void done(List<FileInfo> object, BmobException e) {
                if (e == null) {
                    if (object.size() == 0) {
                        callback.fail();

                    } else {
                        callback.success(object);
                    }
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //查询圈成员
    public void querryMenber(final String groupId, final String userId, final BmobCallback callback) {
        BmobQuery<GroupMember> query = new BmobQuery<GroupMember>();

        query.addWhereEqualTo("groupId", groupId);
        query.addWhereEqualTo("userId", userId);
        query.setLimit(1);
        //执行查询方法
        query.findObjects(new FindListener<GroupMember>() {
            @Override
            public void done(List<GroupMember> object, BmobException e) {
                if (e == null) {
                    if (object.size() == 0) {
                        callback.success(object);
                    }
                } else {
                    callback.Error(e);
                }
            }
        });
    }

    /**
     * 查询考勤状态
     *
     * @param param    参数
     * @param callback 回调
     */
    public void querryRegisterHistory(Map<String, String> param, final BmobCallback callback) {
        BmobQuery<RegisterHistory> query = new BmobQuery<>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            query.addWhereEqualTo(entry.getKey(), entry.getValue());
        }
        query.findObjects(new FindListener<RegisterHistory>() {
            @Override
            public void done(List<RegisterHistory> object, BmobException e) {
                if (e == null) {
                    if (object.size() != 0) {
                        callback.success(object);
                    } else {
                        callback.fail();
                    }
                } else {
                    callback.Error(e);
                }
            }
        });
    }

    public void querryRegisterStatus(Map<String, String> param, final BmobCallback callback) {
        BmobQuery<RegisterStatus> query = new BmobQuery<>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            query.addWhereEqualTo(entry.getKey(), entry.getValue());
        }
        query.findObjects(new FindListener<RegisterStatus>() {
            @Override
            public void done(List<RegisterStatus> object, BmobException e) {
                if (e == null) {
                    if (object.size() != 0) {
                        callback.success(object);
                    } else {
                        callback.fail();
                    }
                } else {
                    callback.Error(e);
                }
            }
        });
    }

    /**
     * 查询参与的考勤成员数据
     *
     * @param param
     * @param callback
     */
    public void querryRegisterInfo(Map<String, String> param, final BmobCallback callback) {
        BmobQuery<RegisterInfo> query = new BmobQuery<RegisterInfo>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            query.addWhereEqualTo(entry.getKey(), entry.getValue());
        }
        query.setLimit(500);
        query.findObjects(new FindListener<RegisterInfo>() {
            @Override
            public void done(final List<RegisterInfo> object, BmobException e) {
                if (e == null) {
                    if (object.size() > 0) {
                        callback.success(object);
                    } else {
                        callback.fail();
                    }
                } else {
                    Log.i("bmob 23", "RegisterFragment 失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    /**
     * 根据环信id查询数据
     * @param param
     * @param callback
     */
    public void querryPerson(Map<String, String> param, final BmobCallback callback) {
        BmobQuery<PersonBmob> query = new BmobQuery<>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            query.addWhereEqualTo(entry.getKey(), entry.getValue());
        }
        query.findObjects(new FindListener<PersonBmob>() {
            @Override
            public void done(List<PersonBmob> object, BmobException e) {
                if (e == null) {
                    if (object.size() > 0) {
                        callback.success(object);
                    } else {
                        callback.fail();
                    }
                } else {
                    callback.Error(e);
                }
            }
        });
    }

    /**
     * 根据环信群组id查询数据
     * @param param
     * @param callback
     */
    public void querryGroup(Map<String, String> param, final BmobCallback callback) {
        BmobQuery<GroupBmob> query = new BmobQuery<>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            query.addWhereEqualTo(entry.getKey(), entry.getValue());
        }
        //query.addWhereEqualTo("id", id);
        query.findObjects(new FindListener<GroupBmob>() {
            @Override
            public void done(List<GroupBmob> object, BmobException e) {
                if (e == null) {
                    if (object.size() > 0) {
                        callback.success(object);
                    } else {
                        callback.fail();
                    }
                } else {
                    callback.Error(e);
                }
            }
        });
    }

    public void querryNoticeDataBean(Map<String, String> param, int limit, final BmobCallback callback) {
        BmobQuery<NoticeDateBean> query = new BmobQuery();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            query.addWhereEqualTo(entry.getKey(), entry.getValue());
        }
        query.setLimit(limit);
        query.findObjects(new FindListener<NoticeDateBean>() {
            @Override
            public void done(List<NoticeDateBean> object, BmobException e) {
                if (e == null) {
                    if (object.size() > 0) {
                        callback.success(object);
                    } else {
                        callback.fail();
                    }


                }
            }
        });
    }

    /**
     * 查询公告文件
     * @param param
     * @param limit
     * @param callback
     */

    public void querryFileInfo(Map<String, String> param, int limit, final BmobCallback callback){
        BmobQuery<FileInfo> query = new BmobQuery<FileInfo>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            query.addWhereEqualTo(entry.getKey(), entry.getValue());
        }
        query.setLimit(limit);
        query.findObjects(new FindListener<FileInfo>() {
            @Override
            public void done(List<FileInfo> object, BmobException e) {
                if (e == null) {
                    if (object.size() > 0) {
                        callback.success(object);
                    } else {
                        callback.fail();
                    }


                }
            }
        });
    }

    public void downloadFile(BmobFile file, final BmobCallback callback){
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(Environment.getExternalStorageDirectory()+"/bmob/", file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
//                toast("开始下载...");
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e==null){
//                    toast("下载成功,保存路径:"+savePath);
                    Log.i("下载成功","保存路径:"+savePath);
                    callback.success(savePath);
                }else{
//                    toast("下载失败："+e.getErrorCode()+","+e.getMessage());
                    Log.i("下载失败",e.getErrorCode()+","+e.getMessage());
                    callback.fail();
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                Log.i("bmob","下载进度："+value+","+newworkSpeed);
            }

        });
    }
}

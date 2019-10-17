package com.example.wyf.classchat.constants;

/**
 * Created by WYF on 2017/9/29.
 */

public class Constants {
    public static final int GET_CONTACT_LIST_INFO = 101; //获取联系人列表详情
    public static final int GET_DETAIL_INFO_FROM_SERVER = 102; //获取联系人信息
    public static final int REFRESH_CONTACT_ON_DEL = 103; //删除时更新联系人列表
    public static final int REFRESH_CONTACT_NEW_INFO = 104; //回调更新联系人列表
    public static final int TAKE_PHOTO = 105; //拍照回调
    public static final int TAKE_ALBUM = 106; //相册选取图片的回调
    public static final int PICTURE_CROP = 107; //拍照剪裁图片后回调
    public static final int CROP_PHOTO_ALBUM = 108; //相册选取图片剪裁后回调
    public static final int GROUP_BACKGROUND = 109; //群设置背景
    public static final int REFRESH_PERSONAL_INFO = 110; //更新个人信息

    public static final int GET_MEMBERS_INFO = 155; //群成员详情
    public static final int REFRESH_GROUP_ADMIN=151318; //拿到群管理员
    public static final int HIDE_NOTICE_FILE_LIST = 159;
    public static final int NOTICE_FRAGMENT_FALG_UP_LOAD_FILE_TO_BMOB = 160; //从NoticeFragment跳到FileAdapter
    public static final int NOTICE_FILE_APP = 161; //公告上传文件的回调
    public static final int NOTICE_FILE_IMAGES = 162;
    public static final int NOTICE_FILE_FILES = 163;
    public static final int NOTICE_FILE_VIDEO = 164;
    public static final int NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB = 165; //从NoticeSeeFragment跳到FileAdapter
    public static final int REFRESH_NOTICE_SEE_FILE_LIST = 166; //重新获取Notice文件列表
    public static final int NOTICE_DELETE_DONE = 167;//在上传文件时公告被删除
    public static final int REFRESH_GROUP_ICON = 168;
    public static final int DISPLAY_NOTICE = 169;

    public static final String VIEW_INFO_EXTRA = "view_info_extra"; //过渡动画
    public static final String PROPNAME_SCREENLOCATION_LEFT = "propname_screenlocation_left";
    public static final String PROPNAME_SCREENLOCATION_TOP = "propname_screenlocation_top";
    public static final String PROPNAME_WIDTH = "propname_width";
    public static final String PROPNAME_HEIGHT = "propname_height";
    public static final String TRANSLATE_TITLE = "translate_title";//共享元素：欢迎页标题
    public static final String TRANSLATE_HEAD = "translate_head"; //共享元素：头像
    public static final String CHATTYPE_SINGLE = "chattype_single"; //会话类型
    public static final String CHATTYPE_GROUP = "chattype_group";
    public static final String LAST_ACCOUNT = "last_account"; //最后登录的账户
    public static final String USER_OBJECT_ID = "user_object_id";
    public static final String BG_CONTACT_INFO_UPDATE_DATE = "bg_contact_info_update_date"; //联系人详情页更新日期
    public static final String GROUP_ID = "group_id";
    public static final String GROUP_ADMIN_LIST = "group_admin_list";
}

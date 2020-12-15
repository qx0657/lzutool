package fun.qianxiao.lzutool.ui.personalinf.bean;

import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;

import java.util.Date;

import fun.qianxiao.lzutool.utils.MIMEUtils;

/**
 * Create by QianXiao
 * On 2020/10/12
 */
public class FileOrFolderItem {
    private String name;
    private boolean isFolder = false;
    private int folder_fid;
    /**
     * 文件夹是否共享
     */
    private boolean folder_share = false;
    private long file_size;
    private String file_content_type;
    private String file_mid;
    private Date file_upload_time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public int getFolder_fid() {
        return folder_fid;
    }

    public void setFolder_fid(int folder_fid) {
        this.folder_fid = folder_fid;
    }

    public boolean isFolder_share() {
        return folder_share;
    }

    public void setFolder_share(boolean folder_share) {
        this.folder_share = folder_share;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public String getFile_content_type() {
        if(TextUtils.isEmpty(file_content_type)){
            return MIMEUtils.getMIMEType(getName());
        }else{
            return file_content_type;
        }
    }

    public void setFile_content_type(String file_content_type) {
        this.file_content_type = file_content_type;
    }

    public String getFile_mid() {
        return file_mid;
    }

    public void setFile_mid(String file_mid) {
        this.file_mid = file_mid;
    }

    public Date getFile_upload_time() {
        return file_upload_time;
    }

    public void setFile_upload_time(Date file_upload_time) {
        this.file_upload_time = file_upload_time;
    }

    @Override
    public String toString() {
        return "FileOrFolderItem{" +
                "name='" + name + '\'' +
                ", isFolder=" + isFolder +
                ", folder_fid=" + folder_fid +
                ", folder_share=" + folder_share +
                ", file_size=" + file_size +
                ", file_content_type='" + file_content_type + '\'' +
                ", file_mid='" + file_mid + '\'' +
                ", file_upload_time=" + file_upload_time +
                '}';
    }
}

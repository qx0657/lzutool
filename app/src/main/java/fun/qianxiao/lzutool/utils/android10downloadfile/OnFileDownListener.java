package fun.qianxiao.lzutool.utils.android10downloadfile;

/**
 * Create by QianXiao
 * On 2020/10/4
 */
public interface OnFileDownListener {
    /**
     * @date :2019/12/16 0016
     * @author : gaoxiaoxiong
     * @description:
     * @param status status == -1 表示失败  status ==0 表示正在下载  status == 1 表示成功
     * @param object 返回成功后的对象参数
     * @param proGress 当前下载百分比
     * @param currentDownProGress 当前下载量
     * @param totalProGress 总的量大小
     **/
    void onFileDownStatus(int status, Object object, int proGress, long currentDownProGress, long totalProGress);
}

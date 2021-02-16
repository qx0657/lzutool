package fun.qianxiao.lzutool.ui.mail.bean;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fun.qianxiao.lzutool.utils.MyTimeUtils;

public class MailInfo {
    private String id;
    private int size;
    private String from;
    private String to;
    private String subject;
    private String summary;
    private Date sentDate;
    private Date receivedDate;
    private boolean flag_read;//已读
    private boolean flag_system;//系统
    private boolean flag_attached;//附件
    private boolean flag_replied;//回复
    private boolean flag_flagged;//红旗
    private boolean flag_deferHandle;//代办
    private boolean flag_top;//置顶
    private int priority;//优先级  5缓慢 3普通 1紧急

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public boolean isFlag_read() {
        return flag_read;
    }

    public void setFlag_read(boolean flag_read) {
        this.flag_read = flag_read;
    }

    public boolean isFlag_system() {
        return flag_system;
    }

    public void setFlag_system(boolean flag_system) {
        this.flag_system = flag_system;
    }

    public boolean isFlag_attached() {
        return flag_attached;
    }

    public void setFlag_attached(boolean flag_attached) {
        this.flag_attached = flag_attached;
    }

    public boolean isFlag_replied() {
        return flag_replied;
    }

    public void setFlag_replied(boolean flag_replied) {
        this.flag_replied = flag_replied;
    }

    public boolean isFlag_flagged() {
        return flag_flagged;
    }

    public void setFlag_flagged(boolean flag_flagged) {
        this.flag_flagged = flag_flagged;
    }

    public boolean isFlag_deferHandle() {
        return flag_deferHandle;
    }

    public void setFlag_deferHandle(boolean flag_deferHandle) {
        this.flag_deferHandle = flag_deferHandle;
    }

    public boolean isFlag_top() {
        return flag_top;
    }

    public void setFlag_top(boolean flag_top) {
        this.flag_top = flag_top;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return subject;
    }

    public String getAppropriateTimeDisplay(){
        return MyTimeUtils.getAppropriateDisplayedTime(receivedDate);
    }

    public String getFromDisplay(){
        Pattern pattern = Pattern.compile("\"(.*)\"(.*)");
        Matcher m = pattern.matcher(from);
        String str = "";
        if (m.find()) {
            str = m.group(1);
            return str;
        }else{
            return from;
        }
    }
}

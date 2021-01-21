package edu.cqu.rsmungshare.bean;

import java.io.Serializable;

public class MsgBean implements Serializable {
    private static final long serialVersionUID = 3702900967898815839L;
    private int seq;//消息序号
    private String type;//消息类型
    private String time;//时间  yyyy/MM/dd HH:mm:ss
    private String source;//消息源  ip
    private String target;//消息目标ip
    private int size;//消息大小  单位KB
    private String tvContent;//消息内容---文本   或者是图片名称、文件名称
    private byte[] image_data;//消息内容---图片
    private byte[] file_data;//消息内容---文件路径
    public MsgBean(int seq, String type, String time, String source, String target, int size, String tvContent,
                   byte[] image_data, byte[] file_data) {
        super();
        this.seq = seq;
        this.type = type;
        this.time = time;
        this.source = source;
        this.target = target;
        this.size = size;
        this.tvContent = tvContent;
        this.image_data = image_data;
        this.file_data = file_data;
    }
    public int getSeq() {
        return seq;
    }
    public void setSeq(int seq) {
        this.seq = seq;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getTarget() {
        return target;
    }
    public void setTarget(String target) {
        this.target = target;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public String getTvContent() {
        return tvContent;
    }
    public void setTvContent(String tvContent) {
        this.tvContent = tvContent;
    }
    public byte[] getImage_data() {
        return image_data;
    }
    public void setImage_data(byte[] image_data) {
        this.image_data = image_data;
    }
    public byte[] getFile_data() {
        return file_data;
    }
    public void setFile_data(byte[] file_data) {
        this.file_data = file_data;
    }
    @Override
    public String toString() {
        return seq + "," + type + "," + time + "," + source + "," + target
                + "," + size + "," + tvContent
                ;
    }
}

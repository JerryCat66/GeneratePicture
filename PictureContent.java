package com.hexway.linan.utils.picture;

import java.io.Serializable;

/**
 * Created by linanjs on 2017/2/16.
 * 卡片内容实体类
 */
public class PictureContent implements Serializable {
    public String headUrl;
    public String startAddress;
    public String endAddress;
    public String goodsInfo;
    public String carInfo;
    public String contact;
    public String phoneNum;

    public PictureContent(String headUrl, String startAddress, String endAddress, String goodsInfo, String carInfo, String contact, String phoneNum) {
        this.headUrl = headUrl;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.goodsInfo = goodsInfo;
        this.carInfo = carInfo;
        this.contact = contact;
        this.phoneNum = phoneNum;
    }
}

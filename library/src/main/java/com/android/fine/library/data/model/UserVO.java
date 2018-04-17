package com.android.fine.library.data.model;

/**
 * Created by tiansj on 2018/4/8.
 */

public class UserVO {

    private Long id = 0L;
    private String nickname = "";
    private String avatar = "";
    private Integer sex = 0;
    private String city = "";
    private String description = "";
    private String birthday;
    private int price = 30;
    private Integer onlineStatus = 1;
    private boolean isBlack = false;
    private Integer minutes = 0;
    private String answerRate = "";
    private Boolean isWhite = false;
    private String invitationSign = "";
    private Boolean isAttention = true;

    public Boolean getIsAttention() {
        return isAttention;
    }

    public void setIsAttention(Boolean attention) {
        isAttention = attention;
    }

    public String getInvitationSign() {
        return invitationSign;
    }

    public void setInvitationSign(String invitationSign) {
        this.invitationSign = invitationSign;
    }

    public Boolean getIsWhite() {
        return isWhite;
    }

    public void setIsWhite(Boolean white) {
        isWhite = white;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public boolean getIsBlack() {
        return isBlack;
    }

    public void setIsBlack(boolean blacklist) {
        isBlack = blacklist;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getAnswerRate() {
        return answerRate;
    }

    public void setAnswerRate(String answerRate) {
        this.answerRate = answerRate;
    }
}

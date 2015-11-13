package com.elex.dataObject;

public class FBShareParam {
	private String name;
	private  String caption;
	private String description;
	private String link;
	private String picture;
	private String inviteMsg; //玩家复制时的邀请信息
	private String shareMsg; //分享页面与链接一起显示的信息
	private String shareIntroduce; //可点的分享介绍
	private String shareDescribe;   //点开以后的分享描述
	
	
	
	public String getShareMsg() {
		return shareMsg;
	}
	public void setShareMsg(String shareMsg) {
		this.shareMsg = shareMsg;
	}
	public String getShareIntroduce() {
		return shareIntroduce;
	}
	public void setShareIntroduce(String shareIntroduce) {
		this.shareIntroduce = shareIntroduce;
	}
	public String getShareDescribe() {
		return shareDescribe;
	}
	public void setShareDescribe(String shareDescribe) {
		this.shareDescribe = shareDescribe;
	}
	public String getInviteMsg() {
		return inviteMsg;
	}
	public void setInviteMsg(String inviteMsg) {
		this.inviteMsg = inviteMsg;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	

}

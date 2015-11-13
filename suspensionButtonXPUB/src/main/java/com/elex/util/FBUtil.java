package com.elex.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.elex.InviteGener;
import com.elex.SuspensionButton;
import com.elex.dataObject.FBFriendPoj;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.login.LoginManager;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.ShareDialog;
import com.umeng.analytics.MobclickAgent;

// by gengyongjiang
public class FBUtil {
	private static String TAG="FBUtil";
    
    private static boolean _inFeedDialogue = false;
    
    public static String feedName ="";
    public static String feedCaption ="";
    public static String feedLinkDesc ="";
    public static String feedLink ="";
    public static String feedPicURL ="";
    public static int feedType = 0;
    public static String feedRef ="";
    public static int preRequest = 0;//登录前的预先请求
    

	
	public static boolean Login() {
        boolean flag = true;
        try {
        	Collection<String> permissions = Arrays.asList("public_profile", "user_friends", "publish_actions");
//        	Collection<String> permissions = Arrays.asList("public_profile", "user_friends");
            LoginManager.getInstance().logInWithPermissions(SuspensionButton.getInstance().getGameActivity(), permissions);
        }catch (Exception e){
            Log.d("fb", "Login exception"+e.getMessage());
            flag = false;
        }
        return flag;
	}
	

	
	public static void publishFeedDialog(String name,String caption, String link,
	                                         String linkDescription,String pictureUrl,final int fType,String ref){
	        feedName = name;
	        feedCaption = caption;
	        feedLinkDesc = linkDescription;
	        feedLink = link;
	        feedPicURL = pictureUrl;
	        feedType = fType;
	        feedRef = ref;
	        Log.d("fb","fb publishFeedDialog start");
	    	if(_inFeedDialogue){
				return;
			}
	    	if(!isLogin()){
	    		Log.d("fb","fb publishFeedDialog not login ");
	    		preRequest = 5;
	    		Login();
				return;
			}
	    	if(feedType==1 && !AccessToken.getCurrentAccessToken().getPermissions().contains("publish_actions")){//使用非shareDialog时需要此权限
	    		preRequest = 5;
	    		Log.d("fb","fb publishFeedDialog have no permission publish_actions ");
	    		Login();
	    		return;
	    	}
			Log.d("fb","fb publishFeedDialog ");
			_inFeedDialogue = true;
	        final Bundle params = new Bundle();
		    params.putString("name", name);
		    params.putString("caption", caption);
		    params.putString("description", linkDescription);
		    params.putString("link", link);
		    params.putString("ref", ref);
		    params.putString("picture", pictureUrl);
		    String keyType = Integer.toString(feedType);
		    Log.d("fb","fb push feed feedType="+keyType);
			//分享统计
    		MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), SuspensionButton.mAppId+"_fb_share");
		    SuspensionButton.getInstance().getGameActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					try
					{
					    if(fType==1 || fType==2){
					    	Log.d("fb","fb push feed feedType=");
							final GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(), "me/feed", params,
									HttpMethod.POST, new GraphRequest.Callback() {
										public void onCompleted(GraphResponse response) {
											_inFeedDialogue = false;
											try {
												Log.d("fb","fb push feed response"+response.toString());
												if(response!=null && response.getError()==null){
													if(fType==1){
														//nativeSetFeedDialogResult(REQUEST_SUCESS);
													}
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
											Log.d("fb","fb push feed end");
										}
									});
							request.executeAsync();
					    }else{
					    	Log.d("fb","fb execute ShareDialog");
					    	//
					    	ShareDialog shareDialog = new ShareDialog(SuspensionButton.getInstance().getGameActivity());
					    	shareDialog.registerCallback(
					        		SuspensionButton.getInstance().getCallbackManager(),
					                new FacebookCallback<ShareDialog.Result>() {
					                    @Override
					                    public void onCancel() {
					                        Log.i(TAG, "Canceled");
					                        _inFeedDialogue = false;
					                        SuspensionButton.getInstance().mListener.onFbShareCallback(null, null);
					                        SuspensionButton.getInstance().mXpubListener.onFbShareCallback(null, null);
					                    }
	
					                    @Override
					                    public void onError(FacebookException error) {
					                    	_inFeedDialogue = false;
					                        Log.e(TAG, String.format("Error: %s", error.toString()));
					                        SuspensionButton.getInstance().mListener.onFbShareCallback(null, error);
					                        SuspensionButton.getInstance().mXpubListener.onFbShareCallback(null, error);
					                    }
	
					                    @Override
					                    public void onSuccess(ShareDialog.Result result) {
					                    	_inFeedDialogue = false;
					                        Log.d(TAG, "share Success!"+result);
					                        Log.d(TAG,result.toString());
					                        SuspensionButton.getInstance().mListener.onFbShareCallback(result, null);
					                        SuspensionButton.getInstance().mXpubListener.onFbShareCallback(result, null);
					                        
					                    }
					                });	
					    	ShareLinkContent content = new ShareLinkContent.Builder()
			                .setContentDescription(feedLinkDesc)
					    	.setContentTitle(feedCaption)
					    	.setImageUrl(Uri.parse(feedPicURL))
					    	.setContentUrl(Uri.parse(feedLink))
					    	.setRef(feedRef)
			                .build();
							if (shareDialog.canShow(content)) {
								Log.d("fb","fb execute ShareDialog show");
								shareDialog.show(content);
							}	
					    }
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		    preRequest = 0;
	    }
	/**
	 * Logout From Facebook 
	 */
	public static void callFacebookLogout() {
		LoginManager.getInstance().logOut();
	}
	
	public static void getFriendsList() {
		Log.d("fb", " static getFriendsList");
		if (isLogin()) {
			makeFriendsRequest();
		}
	}
	
	public static void inviteFriends(String msg) {
		Log.d("fb", " call inviteFriends");
		if (isLogin()) {
			Log.d("fb", "makeInviteFriendsRequest");
			SuspensionButton.getInstance().showLoading();
			makeInviteFriendsRequest(msg);
		}else{//wx 实现登录跳转
			FBUtil.preRequest = 11;
			FBUtil.Login();
			return;
		}
	}
	
	public static void getAppRequestList() {
		Log.d("fb", " static getAppRequestList");
		if (isLogin()) {
			getAppRequestFriends();
		}
	}
	
	public static void requestForMeInfo() {
		Log.d("fb", " call requestForMeInfo");
		if (isLogin()) {
			Log.d("fb", "makeRequestMe");
			makeRequestMe();
		}
	}
	
	public static void appInvite(final String inviteLink) {
		Log.d("fb", " call appInvite");
		if (isLogin()) {
			SuspensionButton.getInstance().getGameActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.d("fb", "run appInvite");
					// TODO Auto-generated method stub
					if (AppInviteDialog.canShow()) {
					    AppInviteContent content = new AppInviteContent.Builder()
					                .setApplinkUrl(inviteLink)
					                .build();
					    AppInviteDialog.show(SuspensionButton.getInstance().getGameActivity(), content);
					}
				}
			});
		}else{
			Log.d("fb", "facebook not login");
		}
	}
	
	public static void showFansWall(String url){
		Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));  
        SuspensionButton.getInstance().getGameActivity().startActivity(it); 
	}
	
	private static void makeFriendsRequest() {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Log.d("fb","fb makeFriendsRequest");
		SuspensionButton.getInstance().getGameActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				GraphRequest request = GraphRequest.newMyInstallFriendsRequest(AccessToken.getCurrentAccessToken(),
						new GraphRequest.GraphJSONArrayCallback() {

							@Override
							public void onCompleted(JSONArray result, GraphResponse response) {
								ArrayList<FBFriendPoj> fbAllFriendPojs=new ArrayList<FBFriendPoj>();
								try {
									Log.d("fb","fb makeFriendsRequest response "+response.toString());
									if(response!=null && response.getError()!=null){
										Log.d("fb","fb makeFriendsRequest error ");
									}else if(result!=null){
										JSONArray dataArray=result.optJSONArray(0);
										for(int i=0;i<dataArray.length();i++){
											JSONObject obj=(JSONObject) dataArray.get(i);
											FBFriendPoj poj=new FBFriendPoj();
											poj.setUserId(obj.optString("id"));
											poj.setUserName(obj.optString("name"));
//											if(obj.optString("name").equals("Maimaiti Zsygxh")) 
												fbAllFriendPojs.add(poj);
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
//								sendFriendRequest(fbAllFriendPojs,"test invite","1");
							}
						});
				request.executeAsync();
			}
		});
		
	}
	
	public static void makeInviteFriendsRequest(final String msg) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		SuspensionButton.getInstance().getGameActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d("fb","fb makeInviteFriendsRequest run");
				GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(),
						new GraphRequest.GraphJSONArrayCallback() {//Callback

							@Override
							public void onCompleted(JSONArray result, GraphResponse response) {
//								ArrayList<FBFriendPoj> fbAllFriendPojs=new ArrayList<FBFriendPoj>();
//								try {
//									Log.d("fb","fb makeFriendsRequest response "+response.toString());
//									if(response!=null && response.getError()!=null){
//										Log.d("fb","fb makeFriendsRequest error ");
//									}else if(result!=null){
//										for(int i=0;i<result.length();i++){
//											JSONObject obj=(JSONObject) result.get(i);
//											FBFriendPoj poj=new FBFriendPoj();
//											poj.setUserId(obj.optString("id"));
//											poj.setUserName(obj.optString("name"));
//											//if(obj.optString("name").equals("Maimaiti Zsygxh")) 
//												fbAllFriendPojs.add(poj);
//										}
//									}
//								} catch (Exception e) {
//									e.printStackTrace();
//								}								
//								sendFriendRequest(fbAllFriendPojs,msg,"9");
								SuspensionButton.getInstance().removeLoading();
								if(result == null) {//获取好友失败打点
									MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), "get_friend_failed");
								}else{
									MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), "get_friend_success");
								}
								InviteGener.fbJAarry=result;
								SuspensionButton.getInstance().showBoardActivity("1");
							}
						});
				request.executeAsync();
			}
		});
	}
	
	private static void getAppRequestFriends() {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		SuspensionButton.getInstance().getGameActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d("fb","fb getAppRequestFriends run");
				GraphRequest request = GraphRequest.newAppRequestFriendsRequest(AccessToken.getCurrentAccessToken(),
						new GraphRequest.GraphJSONArrayCallback() {//Callback
							@Override
							public void onCompleted(JSONArray result, GraphResponse response) {
								try {
									Log.d("fb","fb getAppRequestFriends response "+response.toString());
									if(response!=null && response.getError()!=null){
										//todo
										Log.d("fb","fb AppRequestFriends error ");
									}else if(result!=null){
										JSONArray data = result;
										Log.d("fb","fb AppRequestFriends friends count "+data.length());
										if (data.length() > 0) {
											Log.d("fb","fb Invite friends data: "+data.toString());

										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
				request.executeAsync();
			}
		});
	}
	
	
	
	//1送礼物 2索取物品 都需要配置物品
	public static void sendFriendRequest(final ArrayList<FBFriendPoj> friends,final String msg,final String ftype) {
		SuspensionButton.getInstance().getGameActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(!isLogin()){
					Log.d("fb","facebook not login");
					return;
				}
				Log.d("fb","fb sendFriendRequest ftype="+ftype);
				List<String> ids = new ArrayList<String>();
				for (int i = 0; i < friends.size(); i++) {
					ids.add(friends.get(i).getUserId());
				}
				GameRequestDialog gameRequestDialog = new GameRequestDialog(SuspensionButton.getInstance().getGameActivity());
		        gameRequestDialog.registerCallback(
		        		SuspensionButton.getInstance().getCallbackManager(),
		                new FacebookCallback<GameRequestDialog.Result>() {
		                    @Override
		                    public void onCancel() {
		                        Log.d(TAG, "request Canceled");
		                        SuspensionButton.getInstance().mListener.onFbInviteCallback(null, null);
		                        SuspensionButton.getInstance().mXpubListener.onFbInviteCallback(null, null);
		                    }

		                    @Override
		                    public void onError(FacebookException error) {
		                        Log.d(TAG, String.format("request Error: %s", error.toString()));
		                        SuspensionButton.getInstance().mListener.onFbInviteCallback(null, error);
		                        SuspensionButton.getInstance().mXpubListener.onFbInviteCallback(null, error);
		                    }

		                    @Override
		                    public void onSuccess(GameRequestDialog.Result result) {
								final String reqId = result.getRequestId();
								SuspensionButton.getInstance().mListener.onFbInviteCallback(result, null);
								SuspensionButton.getInstance().mXpubListener.onFbInviteCallback(result, null);
								InviteGener.getInstance().removeInvitedFriends();
								Log.d("fb","fb friends invite request id= "+reqId);
								if (reqId != null && !reqId.equals("")) {
									//nativeSetRequestResult(REQUEST_SUCESS,reqId);
									//onRequstSuccess(reqId);
								}
		                    }
		                });		        
				String useIds = TextUtils.join(",", ids);
				if(ftype!=null && ftype.equals("1")){
					GameRequestContent content1 = new GameRequestContent.Builder()
	                .setMessage(msg)
	                .setObjectId("739067812873207")
					.setActionType(GameRequestContent.ActionType.SEND)
					.setTo(useIds)
	                .build();
					if (GameRequestDialog.canShow()) {
						gameRequestDialog.show(content1);
					}
					Log.d("fb","fb params send object_id=739067812873207");
				}else if(ftype!=null && ftype.equals("2")){
					GameRequestContent content2 = new GameRequestContent.Builder()
	                .setMessage(msg)
	                .setObjectId("426169294209352")
					.setActionType(GameRequestContent.ActionType.ASKFOR)
					.setTo(useIds)
	                .build();
					if (GameRequestDialog.canShow()) {
						gameRequestDialog.show( content2);
					}
					Log.d("fb","fb params askfor object_id=426169294209352");
				}else if(friends.size()<=0){
					GameRequestContent content3 = new GameRequestContent.Builder()
	                .setMessage(msg)
	                .build();
					if (GameRequestDialog.canShow()) {
						gameRequestDialog.show(content3);
					}
					Log.d("fb","fb filters =app_non_users");
				}else{
					GameRequestContent content4 = new GameRequestContent.Builder()
	                .setMessage(msg)
	                .setTo(useIds)
	                .build();
					if (GameRequestDialog.canShow()) {
						gameRequestDialog.show(content4);
					}
					Log.d("fb","fb else send");
				}

				Log.d("fb","fb send ids ="+ids);
				//showDialogWithoutNotificationBar("apprequests", params);
			}
		});		
	}

	public static boolean isLogin(){
		if(AccessToken.getCurrentAccessToken() == null){
			Log.d("fb", "fb not login");
			return false;
		}else if(AccessToken.getCurrentAccessToken().isExpired()){
			Log.d("fb", "fb token expired");
			return false;
		}
		return true;
	}

	public static boolean facebookHasGranted(final String msg){
		boolean flag = false;
		if(isLogin() && AccessToken.getCurrentAccessToken().getPermissions().contains(msg)){
			flag = true;
		}
		return flag;
	}
	
	//story分享
	
	//调用示例  FBUtilies::fbPostMethod("feed", "link", "http://cok.elex.com/opengraph/opengraph.php?id=treasure_help&feedback=treasure_help", "actions", "{ \"name\":\"hi test\", \"link\":\"http://cok.elex.com/opengraph/opengraph.php?id=common&feedback=call_for_help\"}", "cal_for_hp");
	public static void facebookPostMethod(final String method,final String objectType,final String objectValue,final String actions,final String actionsValue,final String ref){
		Log.d(TAG, "fb facebookPfacebookPostMethodostAction");
    	if(!isLogin()){
    		Log.d(TAG,"fb facebookPostMethod not login ");
			return;
		}
        final Bundle params = new Bundle();
	    params.putString(objectType, objectValue);
	    if(actions!=null && !actions.equals("")){
	    	params.putString(actions, actionsValue);
	    }
	    params.putString("fb:explicitly_shared", "true");
	    params.putString("caption", "Clash of Kings");
	    params.putString("ref", ref);
	    SuspensionButton.getInstance().getGameActivity().runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				try
				{
			    	String methodLink = "me/";
			    	methodLink = methodLink + method;
					Log.d(TAG,"fb facebookPostMethod  method="+methodLink);
					Log.d(TAG,"fb facebookPostMethod  objectType="+objectType);
					Log.d(TAG,"fb facebookPostMethod  objectValue="+objectValue);
					final GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(), methodLink, params,
							HttpMethod.POST, new GraphRequest.Callback() {
								public void onCompleted(GraphResponse response) {
									try {
										Log.d(TAG,"fb facebookPostMethod response"+response.toString());
									} catch (Exception e) {
										e.printStackTrace();
									}
									Log.d(TAG,"fb facebookPostMethod  end");
								}
							});
					request.executeAsync();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public static void showToast(final String msg){
		SuspensionButton.getInstance().getGameActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(SuspensionButton.getInstance().getGameActivity(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public static void showToast(final int resId){
		final String msg=SuspensionButton.getInstance().getGameActivity().getString(resId);
		SuspensionButton.getInstance().getGameActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(SuspensionButton.getInstance().getGameActivity(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}
    
	public  static void rePublishFeedDialog(){
		Log.d("fb", "fb rePublishFeedDialog execute feed");
		publishFeedDialog(feedName,feedCaption,feedLink,feedLinkDesc,feedPicURL,feedType,feedRef);
	}
			

    
	private static void makeRequestMe() {
		SuspensionButton.getInstance().getGameActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d("fb","fb makeRequestMe run");
				GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
						new GraphRequest.GraphJSONObjectCallback() {//Callback
							@Override
	                        public void onCompleted(JSONObject me,  GraphResponse response) {
	                            if (response.getError() != null) {
	                      
	                            }else{
	                            	Log.d("fb","fb makeRequestMe id"+me.optString("id"));
	                            	//nativeSetFBUID(me.optString("id"));
	                            }
	                        }
						});
				request.executeAsync();
			}
		});
	}
	
	
	
	
}
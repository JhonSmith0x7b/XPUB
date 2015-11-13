package com.elex.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.elex.InviteGener;
import com.elex.ShinBoardActivity;
import com.elex.ShinBoardGener;
import com.elex.SuspensionButton;
import com.elex.dataObject.FBFriendPoj;
import com.elex.dataObject.FBShareParam;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.ShareDialog;

public class MyFBUtil {
	protected static final String TAG = "fb";
	public static int preLoginRquest =0;
	public static boolean Login() {
        boolean flag = true;
        try {
        	Collection<String> permissions = Arrays.asList("public_profile", "user_friends", "publish_actions");
//        	Collection<String> permissions = Arrays.asList("public_profile", "user_friends");
            LoginManager.getInstance().logInWithPermissions(ShinBoardGener.getInstance().getActivity(), permissions);
        }catch (Exception e){
            Log.d(TAG, "Login exception"+e.getMessage());
            flag = false;
        }
        return flag;
	}
	public static int inviteFriends(String msg) {
		Log.d(TAG, " call inviteFriends");
		if (isLogin()) {
			Log.d(TAG, "makeInviteFriendsRequest");
			makeInviteFriendsRequest(msg);
			return 1;
		}else{//wx 实现登录跳转
			preLoginRquest=1;
			MyFBUtil.Login();
			return 0;
		}
	}
	public static boolean isLogin(){
		if(AccessToken.getCurrentAccessToken() == null){
			Log.d(TAG, "fb not login");
			return false;
		}else if(AccessToken.getCurrentAccessToken().isExpired()){
			Log.d(TAG, "fb token expired");
			return false;
		}
		return true;
	}
	public static void makeInviteFriendsRequest(final String msg) {
		GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(),
				new GraphRequest.GraphJSONArrayCallback() {

					@Override
					public void onCompleted(JSONArray result,
							GraphResponse response) {
						InviteGener.fbJAarry=result;
						ShinBoardGener.getInstance().showInviteF();;
					}
			
		});
		request.executeAsync();
	}
	public static void sendFriendRequest(ArrayList<FBFriendPoj> friends,final String msg,String ftype) {
		GameRequestDialog gameRequestDialog = new GameRequestDialog(ShinBoardGener.getInstance().getActivity());
		gameRequestDialog.registerCallback(ShinBoardActivity.getCallbackManager(), new FacebookCallback<GameRequestDialog.Result>(){

			@Override
			public void onSuccess(GameRequestDialog.Result result) {
				InviteGener.getInstance().removeInvitedFriends();
				ShinBoardGener.getInstance().onFbInviteCallback(result, null);
				ShinBoardGener.getInstance().showBoard("1");
			}

			@Override
			public void onCancel() {
				ShinBoardGener.getInstance().showBoard("1");		
			}

			@Override
			public void onError(FacebookException error) {
				ShinBoardGener.getInstance().onFbInviteCallback(null, error);
				ShinBoardGener.getInstance().showBoard("1");
			}
			
		});
		List<String> ids = new ArrayList<String>();
		for (int i = 0; i < friends.size(); i++) {
			ids.add(friends.get(i).getUserId());
		}
		String useIds = TextUtils.join(",", ids);
		GameRequestContent content = new GameRequestContent.Builder()
		.setMessage(msg)
		.setTo(useIds)
		.build();
		if(GameRequestDialog.canShow()){
			gameRequestDialog.show(content);
		}
	}
	public static void publicFeed(FBShareParam shareParams,String shortLink){
		ShareLinkContent content;
		try{
			content = new ShareLinkContent.Builder()
			.setContentTitle(shareParams.getCaption())
			.setContentUrl(Uri.parse(shortLink))
			.setImageUrl(Uri.parse(shareParams.getPicture()))
			.setContentDescription(shareParams.getDescription())
			.build();
		}catch(Exception e){
			return;
		}
		ShareDialog sd= new ShareDialog(ShinBoardGener.getInstance().getActivity());
		sd.registerCallback(ShinBoardActivity.getCallbackManager(), new FacebookCallback<ShareDialog.Result>(){

			@Override
			public void onSuccess(com.facebook.share.Sharer.Result result) {
				ShinBoardGener.getInstance().onFbShareCallback(result, null);
			}

			@Override
			public void onCancel() {
				ShinBoardGener.getInstance().onFbShareCallback(null, null);
			}

			@Override
			public void onError(FacebookException error) {
				ShinBoardGener.getInstance().onFbShareCallback(null, error);
			}
		});
		if(sd.canShow(content)){
			sd.show(content);
		}
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
}

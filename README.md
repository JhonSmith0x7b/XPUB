#悬浮窗前端文档
####基础：
悬浮窗是基于FacebookSDK的社交增强插件。  
主体逻辑将放在第三部分介绍,你可以[直接进入第三部分](#three)。前两部分会对接入的SDK有一个引导性的介绍。

###一、悬浮窗与FacebookSDK
悬浮窗目前用到FacebookSDK的Login、Invite、Share、Graph API，如果你对此不了解的话，推荐前往官方网站去完成教程，并可在此处查看相关所有文档⬇️  
[Facebook Developer](https://developers.facebook.com/)  

下面会提供Login相关功能集成举例，以让你对于FacebookSDK的交互有一个直观的体验  
####FacebookSDK Login 集成
######通过Graph API实现登陆  
Facebook有提供登陆按钮，可是不够灵活，用Graph API是个好选择，而且并不会花去太多时间。 
######初始化FacbookSDK

	FacebookSdk.sdkInitialize(mContext);
*初始化应该与悬浮窗初始化同时进行，目前初始化放在SuspensionButton.java的onCreate(Bundle bundle)中。*   
######创建callbackManager
Facebook的登陆是使用异步来做的，所以我们需要一个回调机制来实现逻辑。  
一个 创建/获取 callbackManager的静态方法

	public static CallbackManager getCallbackManager(){
		if(callbackManager==null){
			callbackManager=CallbackManager.Factory.create();
		}
		return callbackManager;
	}

*callbackManager并不需要太多逻辑，只是为了匹配到facebookCallback（）*  

######注册callback  

	private void setLoginCallbackManger(){
		LoginManager.getInstance().registerCallback(getCallbackManager(), new FacebookCallback<LoginResult>(){

			@Override
			public void onSuccess(LoginResult result) {
				Toast.makeText(ShinBoardActivity.this, getResources().getString(R.string.loginresultsuccess), Toast.LENGTH_SHORT).show();
				if(MyFBUtil.preLoginRquest==1){
					ShinBoardGener.getInstance().showBoard("1");
				}
			}

			@Override
			public void onCancel() {
				Toast.makeText(ShinBoardActivity.this, getResources().getString(R.string.loginresultcancel), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError(FacebookException error) {
				Toast.makeText(ShinBoardActivity.this, getResources().getString(R.string.loginresulterror), Toast.LENGTH_SHORT).show();
			}
			
		});
	}

*LoginManager会有一个静态的对象，在之后登陆的时候我们也会用到它，所以你可以在登陆之前的任意位置注册这个callback，悬浮窗则放在了主要功能Activity（ShinBoardActivity.java）初始化的时候注册它*  
  
######重写用到facebook功能Activity的onActivityResult()方法

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getCallbackManager().onActivityResult(requestCode, resultCode, data);
	}
*facebook的功能会开启他自己的activity，所以需要在回到原activity的时候，调用callbackManager*  

######登陆方法

	public static boolean Login() {
        boolean flag = true;
        try {
        	Collection<String> permissions = Arrays.asList("public_profile", "user_friends", "publish_actions");          LoginManager.getInstance().logInWithPermissions(ShinBoardGener.getInstance().getActivity(), permissions);
        }catch (Exception e){
            Log.d(TAG, "Login exception"+e.getMessage());
            flag = false;
        }
        return flag;
	}
	
*你只需要调用logInWithPermissions()就可以完成登陆了，记得在上面注册callback的时候写好所需的逻辑，悬浮窗选择新建一个工具类去存放它(MyFBUtil.java)*  

***
至此你已经明白如何集成facebookSDK的登陆了，还有一些功能需要你对着官方文档和代码进行了解，有关facebookSDK的主要逻辑已经写在了MyFBUtil.java类中。  
###二、悬浮窗与第三方SDK
为了实现功能，悬浮窗除了FacebookSDK外还接入了一些SDK，现在将进行列举，以引导你通过官方文档与代码去了解它们：  

* umeng     [umeng 开发文档](http://dev.umeng.com/)

	悬浮窗使用umeng打点，生成数据以便进行统计，使用举例

        MobclickAgent.onEvent(SuspensionButton.getInstance().getGameActivity(), SuspensionButton.mAppId+"_xpub_invite");

* testIn    [testIn 帮助中心](http://my.testin.cn/help)

	悬浮窗使用testIn进行崩溃检测
	
* branch.io  [branch io 开发文档](https://start.branch.io/)

	悬浮窗的分享功能使用了branch.io，这是一个功能强大的插件，能生成可追踪记录的短链接，且简单易用，附上悬浮窗上的branch.io初始化方法，在SuspensionButton.java中。
	
		public void onStart(){
		//branch 初始化
		//    	branch = Branch.getInstance(getGameActivity(),"key_live_kkbKm4xMUHCrvMbMDgJnUphjyDf0tPVA");
    	branch = Branch.getInstance(getGameActivity().getApplicationContext());
		branch.initSession(new Branch.BranchReferralInitListener() {
			@Override
			public void onInitFinished(JSONObject referringParams, BranchError error) {
				if (error == null) {
					// params are the deep linked params associated with the link that the user clicked before showing up
					Log.i("BranchConfigTest", "deep link data: " + referringParams.toString());
					from_xpub_user_id = referringParams.optString("xpub_user_id", "");	
					from_scancode=referringParams.optString("scancode","");
				}
			}
		}, ((Activity)mContext).getIntent().getData(), (Activity)mContext);
    	}
    	
<!-- -->
有关第三方SDK的一些配置，可以参考要提供给CP的悬浮窗接入文档。
###[三、悬浮窗主体](id:three)
这一部分将选取主要功能进行比较详细的介绍，但不会逐条逐句去解释。
#####协议
协议的作用是规定前台与后台交互时的数据格式。悬浮窗中有许多网络交互的存在，这样的设置可以让很多改动从服务端进行而不是频繁更换客户端。  
先列出悬浮窗目前所有数据协议：
<pre>
请求方式 POST

参数中需指明功能类型（以参数mod，act定义）,以及该功能类型必须的其他参数

如获取玩家活动数据,需传参mod=user   act=tasks a=appid c=uid d=roleid

注意，appid，uid，roleid这三个参数能够标识出某游戏中的唯一角色。

user_tasks   获取玩家活动数据。进入领奖中心面板时请求。返回数据为user_tasks

需要参数：a,c,d

返回值：{“user_tasks”:[   

{

“id”:1,

“award”:0,       // 0  1  2    领奖按钮   置灰（不可点）/可点击/显示已领，置灰      

“goal”:5,             //任务目标值

“process”:3     //进度    

                         //goal和proecess这两个值仅用于客户端显示进度达成情况   与领奖按钮无关

}

]

event_award  传参id     领某任务的奖励。点击领奖按钮时请求 ，   返回数据为event_award

需要参数：a,c,d,taskid

返回值：

{"status":0,"msg":"抱歉，領獎功能暫時還未上線。在此之前，獎品由GM統一發放。"}

{“status”:-1,“msg”:”领奖失败，。。。”}

{“status”:0,“msg”:”领奖成功…”,”user_tasks”:”"}

{"status":-2, "msg":"usertask not in accomplishmenst "}


如果领奖成功。活动数据也需要传回,有user_tasks节点

fb_login  上报fb登录数据 mod=fb   act=login

需要参数a,b,c,d,k

fb_like 上报fb邀请数据mod=fb   act=like

需要参数a,b,c,d,i

fb_invite 上报fb邀请数据mod=fb   act=invite

需要参数a,b,c,d,r

fb_share上报fb分享数据mod=fb   act=share

需要参数a,b,c,d,p

app_start上报应用启动数据mod=app aact=start

需要参数 a(appid),b(uid),c(roleid),pkgname，deviceid

cpb_click上报cpb点击mod=cpb act=click

需要参数a,b,c,deviceid




参数示例及含义罗列

{

“mod”:”user”,

“act”:”tasks”，

"a": "appid",

"b": "fbid",

"c": "uid",

"d": "roldid",

"k": "accessToken",

"i": "isFan",

"p": "postId",

"r": "reqid",

"t": "to_fbids"

}
</pre>
目前因为有公共请求类（Globals.java）的存在，使请求后台十分直观，比如以下这次请求:

	private void doFbLike(Handler handler){
		likeHandler = handler;
		List<NameValuePair> params =  new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mod","fb"));
		params.add(new BasicNameValuePair("act","like"));
		params.add(new BasicNameValuePair("a",SuspensionButton.mAppId));
		params.add(new BasicNameValuePair("b",SuspensionButton.mFbId));
		params.add(new BasicNameValuePair("c",SuspensionButton.mUid));
		params.add(new BasicNameValuePair("d",SuspensionButton.mRoleId));
		params.add(new BasicNameValuePair("i","1"));
		Globals.getInstance().sendRequest(params, new OnFbLike() , new OnFbLikeError() );
	}

这是用户点击facebook的点赞按钮后，所触发的请求。这时候就应该去查找上面的协议，fb_like，需要提供 a, b, c ,d ,i参数，分别对应用户的appid，fbid，uid，roleid，i标示。  
这就是协议的使用方法，至于下面的发送请求，你可以在代码里去查看。
#####所有类及资源文件的简单介绍
这部分并不会详细去说，因为它只是让你知道这些类都在发挥什么作用。  

*  `AsyncFetch.java`  通过AsyncTask实现所有图片的缓存
*  `AwardFragment.java` 领奖页面的Fragment类
*  `AwardGener.java` 领奖页面的主要逻辑类
*  `BackgroundKiller.java` 通过ActivityManager去清理内存的类
*  `InviteFragment.java`  邀请页的Fragment类
*  `InviteGener.java`  邀请页面的主要逻辑类
*  `NoticeWindowActivity.java`  公告的Activity页面。目前公告已经整合进统一的activity，但仍保留了这个类，是为了以后如果有单独需要公告的需求时再使用
*  `NoticeWindowGener.java` 公告页面的主要逻辑类，这个类是通用在两种公告页面的(Activity,Fragment)
*  `ScreenShoter.java`  提供了截屏分享的方法
*  `ShareGener.java`   分享页面的主要逻辑类
*  `SharePageFragment.java`  分享页面的Fragment类
*  `ShinBoardActivity.java`  将悬浮窗所有Fragment整合的Activity
*  `ShinBoardGener.java`	ShinBoardActivity的主要逻辑类
*  `SuspensionButton.java`	悬浮窗主类
*  `WebViewFragment.java`	打开网页或者粉丝页的Fragment类
*  `Globals.java`	公共网络请求类
*  `MyFBUtil.java`	facebook工具类
*  `XpubUtils.java`	提供一些工具方法
*  `fb_likebtn.xml`	facebook点赞按钮的布局文件
*  `invite_main_portrait.xml`	邀请页面的主布局
*  `invited_grid_portrait.xml`	邀请页面中所包含的gridView的布局
*  `myfacebooklike_view_portrait.xml`	在领奖页面点击点赞任务时会弹出的popup的布局
*  `mysharepagedescribe_view_portrait.xml`	在分享页面点击详情时弹出的popup的布局
*  `notice_window_portrait.xml`		公告窗口的布局
*  `quest_grid_portrait_gener.xml`	领奖页面中gridView的布局
*  `quest_main_portrait.xml`	领奖页面主布局
*  `share_page_portrait.xml`	分享页面的主布局
*  `sharepage_gridview_layout.xml`	分享页面点击复制链接的时候会弹出的popup中gridview的布局
*  `sharepage_popup_layout.xml`	  分享页面点击复制链接的时候会弹出的popup的布局
*  `webview_main_portrait.xml`	webview的布局

<!---->
有些未介绍的类已经被新内容所替代。
#####UI实现方式说明
首先在资源文件中设定基本的布局，然后在逻辑类中去详细修改它。资源文件定义简单快捷，代码定义自由度更高，均衡这两种方式。  
举例：  
	<pre>
	\<Button 
        android:id="@+id/aboveButton"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_alignParentTop="true"
        android:gravity="center"
        android:textColor="#FFFFFF"
        android:textSize="20sp"
        android:text="@string/noticeboard_invitetitle"
        />
        </pre>
在领奖页面我们需要一个回游戏按钮，目前的设计是在游戏顶部，可是根据不同屏幕高度无法很好地匹配。比较好的方式是十分之一屏幕的高度，可这个是布局文件做不到的，于是使用代码进行修改：  

	aboveButton = (Button) rl.findViewById(R.id.aboveButton);
		RelativeLayout.LayoutParams rlParams = (LayoutParams) aboveButton.getLayoutParams();
		rlParams.width=dm.widthPixels;
		rlParams.height=dm.heightPixels/10;

在悬浮窗的页面中大量使用了这种生成方式，以让布局文件有更广的适用范围。当然，更好的布局是根据不同屏幕去设置不同的布局文件。

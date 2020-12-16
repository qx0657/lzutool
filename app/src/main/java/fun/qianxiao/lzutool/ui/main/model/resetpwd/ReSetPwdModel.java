package fun.qianxiao.lzutool.ui.main.model.resetpwd;

import com.blankj.utilcode.util.JsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.security.interfaces.RSAPublicKey;

import fun.qianxiao.lzutool.ui.main.model.ecardservices.EcardServicesModel;
import fun.qianxiao.lzutool.utils.MyOkhttpUtils;
import fun.qianxiao.lzutool.utils.MyRSACrypt;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 重设密码
 */
public class ReSetPwdModel {
    private OkHttpClient okHttpClient = MyOkhttpUtils.getUnsafeOkHttpClientBuilder()
            .build();


    /**
     * 修改账户密码(个人工作台)
     * @param mylzucookie
     * @param oldpwd
     * @param newpwd
     * @param observer
     */
    public void reSetPwd(String mylzucookie, String oldpwd, String newpwd, Observer<Boolean> observer){
        //LogUtils.i(mylzucookie);
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            String res = okHttpClient.newCall(new Request.Builder()
                    .addHeader("User-Agent","Mozilla/5.0 (iPod; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5")
                    .addHeader("Cookie",mylzucookie)
                    .url("http://my.lzu.edu.cn/setPassWord?t="+System.currentTimeMillis())
                    .post(new FormBody.Builder()
                            .add("nowPsd",oldpwd)
                            .add("psd",newpwd)
                            .add("confirmPsd",newpwd)
                            .build())
                    .build())
                    .execute().body().string();
            //LogUtils.i(res);
            JSONObject jsonObject;
            try{
                jsonObject = new JSONObject(res);
                if(jsonObject.getInt("state")==1){
                    emitter.onNext(true);
                }else{
                    emitter.onError(new Throwable(jsonObject.optString("msg")));
                }
            }catch (JSONException e){
                res = okHttpClient.newCall(new Request.Builder()
                        .addHeader("User-Agent","Mozilla/5.0 (iPod; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5")
                        .addHeader("Cookie",mylzucookie)
                        .url("http://my.lzu.edu.cn/setPassWord?t="+System.currentTimeMillis())
                        .post(new FormBody.Builder()
                                .add("nowPsd",oldpwd)
                                .add("psd",newpwd)
                                .add("confirmPsd",newpwd)
                                .build())
                        .build())
                        .execute().body().string();
                //LogUtils.i(res);
                jsonObject = new JSONObject(res);
                if(jsonObject.getInt("state")==1){
                    emitter.onNext(true);
                }else{
                    emitter.onError(new Throwable(jsonObject.optString("msg")));
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 修改支付密码
     * @param ecardcookie
     * @param newpdw
     * @param observer
     */
    public void reSetPayPwd(String ecardcookie, String newpdw, Observer<Boolean> observer){
        new EcardServicesModel().getRSAPublicKey(ecardcookie, new Observer<RSAPublicKey>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull RSAPublicKey rsaPublicKey) {
                Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                    String res = okHttpClient.newCall(new Request.Builder()
                            .addHeader("Cookie",ecardcookie)
                            .url("https://ecard.lzu.edu.cn/securityCenter/thirdStep")
                            .post(new FormBody.Builder()
                                    .add("pwdType","2")
                                    .add("oldPassword","")
                                    .add("newPassword",MyRSACrypt.encrypt(rsaPublicKey,newpdw))
                                    .add("isDefaultPWD","1")
                                    .build())
                            .build())
                            .execute().body().string();
                    if(res.contains("设置密保")){
                        emitter.onNext(true);
                    }else{
                        //LogUtils.i(res);
                        Document doc = Jsoup.parse(res);
                        Element element = doc.select("div.result-info").first();
                        if(element!=null){
                            String errorinfo = element.text();
                            emitter.onError(new Throwable(errorinfo));
                        }else{
                            emitter.onError(new Throwable("支付密码修改失败"));
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}

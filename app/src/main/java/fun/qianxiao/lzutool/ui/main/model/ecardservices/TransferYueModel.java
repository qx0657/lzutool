package fun.qianxiao.lzutool.ui.main.model.ecardservices;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fun.qianxiao.lzutool.utils.Des3Encrypt;
import fun.qianxiao.lzutool.utils.MyOkhttpUtils;
import fun.qianxiao.lzutool.utils.MyTimeUtils;
import fun.qianxiao.lzutool.utils.SignUtils;
import fun.qianxiao.lzutool.utils.Xml2JsonUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 余额互转
 */
public class TransferYueModel {
    private OkHttpClient okHttpClient = MyOkhttpUtils.getUnsafeOkHttpClientBuilder()
            .cookieJar(new CookieJar() {
                private List<Cookie> cache = new ArrayList<>();

                @Override
                public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                    cache.addAll(list);
                }

                @NotNull
                @Override
                public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                    return cache;
                }
            })
            .build();

    /**
     * 获取随机数
     * @param observer
     */
    private void getRandom(Observer<String> observer){
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            Map<String, String> map = new HashMap<>();
            map.put("Time", MyTimeUtils.getNowString());
            map.put("Sign", SignUtils.sign(map));
            FormBody.Builder postdata = new FormBody.Builder();
            for (String key : map.keySet()) {
                postdata.add(key, map.get(key));
            }
            String res = okHttpClient.newCall(new Request.Builder()
                    .url("https://gateway.lzu.edu.cn:9000/easytong-app/easytong_app/GetRandomNumber")
                    .post(postdata.build())
                    .build())
                    .execute().body().string();
            JSONObject jsonObject = Xml2JsonUtils.xml2json(res).getJSONObject("EasyTong");
            if(jsonObject.optString("Code").equals("1")){
                String random = jsonObject.getString("Random");
                emitter.onNext(random);
            }else{
                emitter.onError(new Throwable("随机数获取失败"));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 根据校园卡号获取用户AccNum
     * @param cardid
     * @return
     * @throws Throwable
     */
    public String getAccNum(String cardid) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("Percode", cardid);
        map.put("Time", MyTimeUtils.getNowString());
        map.put("Sign", SignUtils.sign(map));
        FormBody.Builder postdata = new FormBody.Builder();
        for (String key : map.keySet()) {
            postdata.add(key, map.get(key));
        }
        String res = okHttpClient.newCall(new Request.Builder()
                .url("http://app.lzu.edu.cn/easytong_app/GetAccInfoByPercode")
                .post(postdata.build())
                .build())
                .execute().body().string();
        JSONObject jsonObject = Xml2JsonUtils.xml2json(res).getJSONObject("EasyTong");
        if(jsonObject.getString("Code").equals("1")){
            return jsonObject.getString("AccNum");
        }else{
            throw new Exception(jsonObject.optString("Msg"));
        }
    }

    /**
     * 根据用户AccNum获取校园卡CardAccNum
     * @param accnum
     * @return
     * @throws Throwable
     */
    public String getCardAccNum(String accnum) throws Exception{
        Map<String, String> map = new HashMap<>();
        map.put("AccNum", accnum);
        map.put("CardStatus", "2");
        map.put("Time", MyTimeUtils.getNowString());
        map.put("Sign", SignUtils.sign(map));
        FormBody.Builder postdata = new FormBody.Builder();
        for (String key : map.keySet()) {
            postdata.add(key, map.get(key));
        }
        String res = okHttpClient.newCall(new Request.Builder()
                .url("https://appservice.lzu.edu.cn/easytong_app/easytong-app/easytong_app/GetAccCardInfoForDev")
                .post(postdata.build())
                .build())
                .execute().body().string();
        JSONObject jsonObject = Xml2JsonUtils.xml2json(res).getJSONObject("EasyTong");
        if(jsonObject.getString("Code").equals("1")){
            return jsonObject.getJSONObject("Table").getString("CardAccNum");
        }else{
            throw new Exception(jsonObject.optString("Msg"));
        }
    }

    /**
     * 从校园卡转入电子账户
     * @param accnum
     * @param cardAccnum
     * @param paypwd
     * @param money
     * @param observer
     */
    public void transferYueToEcard(String accnum,String cardAccnum,String paypwd,String money,Observer<Boolean> observer){
        getRandom(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String random) {
                LogUtils.i(random);
                Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("OutAccNum", accnum);
                    map.put("OutCardAccNum", cardAccnum);
                    map.put("OutEWalletNum", "1");
                    map.put("InAccNum", accnum);
                    map.put("InCardAccNum", "0");
                    map.put("InEWalletNum", "9");
                    map.put("MonTrans", money);
                    map.put("Password", Des3Encrypt.encrypt(paypwd,random));
                    map.put("IMEI", "imei");
                    map.put("Code", "code");
                    map.put("Time", MyTimeUtils.getNowString());
                    map.put("Sign", SignUtils.sign(map));
                    FormBody.Builder postdata = new FormBody.Builder();
                    for (String key : map.keySet()) {
                        postdata.add(key, map.get(key));
                    }
                    String res = okHttpClient.newCall(new Request.Builder()
                            .url("https://gateway.lzu.edu.cn:9000/easytong-app/easytong_app/CardTransfer")
                            .post(postdata.build())
                            .build())
                            .execute().body().string();
                    LogUtils.i(res);
                    JSONObject jsonObject = Xml2JsonUtils.xml2json(res).getJSONObject("EasyTong");
                    if(jsonObject.optString("Code").equals("1")){
                        emitter.onNext(true);
                    }else{
                        emitter.onError(new Throwable(jsonObject.optString("Msg")));
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                observer.onError(e);
            }

            @Override
            public void onComplete() {

            }
        });

    }

    /**
     * 余额互转
     * @param fromCardid 转出校园卡号
     * @param isFromEWallet 是否从电子账户转出
     * @param toCardid 转入校园卡号
     * @param isToEWallet 是否转入电子账户
     * @param paypwd 转出卡支付密码
     * @param money 转出金额
     * @param observer
     */
    public void transferYueToOtherCard(String fromCardid,boolean isFromEWallet,
                                       String toCardid,boolean isToEWallet,
                                       String paypwd,String money,Observer<Boolean> observer){
        getRandom(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String random) {
                Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                    String fromAccnum = getAccNum(fromCardid);
                    String fromCardAccnum = getCardAccNum(fromAccnum);
                    String toAccnum = getAccNum(toCardid);
                    String toCardAccnum = getCardAccNum(toAccnum);
                    LogUtils.i("Random:"+random,
                            "fromAccnum:"+fromAccnum,
                            "fromCardAccnum:"+fromCardAccnum,
                            "toAccnum:"+toAccnum,
                            "toCardAccnum:"+toCardAccnum);
                    Map<String, String> map = new HashMap<>();
                    map.put("OutAccNum", fromAccnum);
                    map.put("OutCardAccNum", isFromEWallet?"0":fromCardAccnum);
                    map.put("OutEWalletNum", isFromEWallet?"9":"1");
                    map.put("InAccNum", toAccnum);
                    map.put("InCardAccNum", isToEWallet?"0":toCardAccnum);
                    map.put("InEWalletNum", isToEWallet?"9":"1");
                    map.put("MonTrans", money);
                    map.put("Password", Des3Encrypt.encrypt(paypwd,random));
                    map.put("IMEI", "imei");
                    map.put("Code", "code");
                    map.put("Time", MyTimeUtils.getNowString());
                    map.put("Sign", SignUtils.sign(map));
                    //LogUtils.i(map.toString());
                    FormBody.Builder postdata = new FormBody.Builder();
                    for (String key : map.keySet()) {
                        postdata.add(key, map.get(key));
                    }
                    String res = okHttpClient.newCall(new Request.Builder()
                            .url("https://gateway.lzu.edu.cn:9000/easytong-app/easytong_app/CardTransfer")
                            .post(postdata.build())
                            .build())
                            .execute().body().string();
                    //LogUtils.i(res);
                    JSONObject jsonObject = Xml2JsonUtils.xml2json(res).getJSONObject("EasyTong");
                    if(jsonObject.optString("Code").equals("1")){
                        emitter.onNext(true);
                    }else{
                        emitter.onError(new Throwable(jsonObject.optString("Msg")));
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                observer.onError(e);
            }

            @Override
            public void onComplete() {

            }
        });

    }


}

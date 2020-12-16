package fun.qianxiao.lzutool.ui.main.model.ecardservices;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class EcardServicesModel {
    private OkHttpClient okHttpClient = MyOkhttpUtils.getUnsafeOkHttpClientBuilder()
            .build();
    /**
     * 获取RSA公钥
     * @param zhyktcookie
     * @param observer
     */
    public void getRSAPublicKey(String zhyktcookie,Observer<RSAPublicKey> observer){
        Observable.create((ObservableOnSubscribe<RSAPublicKey>) emitter -> {
            String res = okHttpClient.newCall(new Request.Builder()
                    .addHeader("Cookie",zhyktcookie)
                    .url("https://ecard.lzu.edu.cn/publiccombo/keyPair")
                    .post(RequestBody.create("".getBytes()))
                    .build())
                    .execute().body().string();
            JSONObject jsonObject = new JSONObject(res);
            if(jsonObject.getString("ajaxState").equals("3")){
                JSONObject publicKeyMap = jsonObject.getJSONObject("publicKeyMap");
                String exponent = publicKeyMap.getString("exponent");
                String modulus = publicKeyMap.getString("modulus");
                if(!TextUtils.isEmpty(exponent)&&!TextUtils.isEmpty(modulus)){
                    emitter.onNext(MyRSACrypt.getPublicKey(modulus, exponent));
                }else {
                    emitter.onError(new Throwable("获取秘钥对失败"));
                }
            }else{
                emitter.onError(new Throwable("获取秘钥对失败("+jsonObject.optString("msg")+")"));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 检查密码是否正确
     * @param zhyktcookie
     * @param pwd
     * @param observer
     */
    public void checkPaypwd(String zhyktcookie, String pwd, Observer<Boolean> observer){
        getRSAPublicKey(zhyktcookie, new Observer<RSAPublicKey>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull RSAPublicKey rsaPublicKey) {
                Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                    String pwd_c = MyRSACrypt.encrypt(rsaPublicKey, pwd);
                    //LogUtils.i(pwd_c);
                    String res = okHttpClient.newCall(new Request.Builder()
                            .addHeader("Cookie",zhyktcookie)
                            .url("https://ecard.lzu.edu.cn/publiccombo/checkpaypwd")
                            .post(new FormBody.Builder()
                                    .add("paypassword",pwd_c)
                                    .build())
                            .build())
                            .execute().body().string();
                    JSONObject jsonObject = new JSONObject(res);
                    //LogUtils.i(jsonObject.toString());
                    if(jsonObject.getString("ajaxState").equals("3")){
                        emitter.onNext(true);
                    }else{
                        emitter.onError(new Throwable(jsonObject.optString("msg")));
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
     * 获取交电费的token
     * @param zhyktcookie
     * @param observer
     */
    private void getElectricityPayToken(String zhyktcookie, Observer<String> observer){
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            String res = okHttpClient.newCall(new Request.Builder()
                    .addHeader("Cookie",zhyktcookie)
                    .url("https://ecard.lzu.edu.cn/payFee/showItemListPayPage")
                    .post(new FormBody.Builder()
                            .add("itemNum","2")
                            .build())
                    .build())
                    .execute().body().string();
            Document doc = Jsoup.parse(res);
            String token = doc.select("input#token").first().val();
            emitter.onNext(token);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 使用电子账户缴费宿舍电费
     * @param zhyktcookie
     * @param isUseYue 是否使用校园卡余额
     * @param cardAccNUm 使用使用校园卡余额则需要校园卡CardAccNum
     * @param pwd 校园卡支付密码
     * @param paymoney 缴费金额
     * @param dorminfo 宿舍基础信息json 包含了 areano buildingno floorno roomno
     * @param observer
     */
    public void useEcardPayForElectricity(String zhyktcookie, boolean isUseYue, String cardAccNUm, String pwd, String paymoney, JSONObject dorminfo, Observer<Boolean> observer){
        //LogUtils.i("isUseYue:"+isUseYue);
        getElectricityPayToken(zhyktcookie, new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String token) {
                //LogUtils.i(token);
                getRSAPublicKey(zhyktcookie, new Observer<RSAPublicKey>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull RSAPublicKey rsaPublicKey) {
                        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                            String res = okHttpClient.newCall(new Request.Builder()
                                    .addHeader("Cookie",zhyktcookie)
                                    .url("https://ecard.lzu.edu.cn/payFee/payItemlist")
                                    .post(new FormBody.Builder()
                                            .add("pwd",MyRSACrypt.encrypt(rsaPublicKey, pwd))
                                            .add("itemNum","2")
                                            .add("eWalletId",isUseYue?"1":"9")
                                            .add("typeNum","3")
                                            .add("itemName","学生宿舍电费")
                                            .add("payMenoy",paymoney)
                                            .add("ewalletMenoy","0.00")//已测试不可为空 可为0.00
                                            .add("eWalletName",isUseYue?"校园卡":"电子账户")
                                            .add("areano",dorminfo.optString("areano"))
                                            .add("buildingno",dorminfo.optString("buildingno"))
                                            .add("floorno",dorminfo.optString("floorno"))
                                            .add("roomno",dorminfo.optString("roomno"))
                                            .add("buildingname","公寓")//已测试 不用填写具体真实楼名
                                            .add("cardaccNum",isUseYue?cardAccNUm:"0")
                                            .add("token",token)
                                            .build())
                                    .build())
                                    .execute().body().string();
                            //LogUtils.i(res);
                            Document doc = Jsoup.parse(res);
                            String reslut = doc.select("div.result-info").first().text();
                            //LogUtils.i(reslut);
                            if(reslut.contains("缴费成功")){
                                emitter.onNext(true);
                            }else{
                                emitter.onError(new Throwable(reslut));
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
     * 电费查询
     * @param zhyktcookie
     * @param areano
     * @param buildingno
     * @param floorno
     * @param roomno
     * @param observer
     */
    public void queryDormBlance(String zhyktcookie,String areano, String buildingno, String floorno, String roomno, Observer<String> observer){
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            String res = okHttpClient.newCall(new Request.Builder()
                    .addHeader("Cookie",zhyktcookie)
                    .url("https://ecard.lzu.edu.cn/payFee/getBalance?t="+System.currentTimeMillis())
                    .post(new FormBody.Builder()
                            .add("data","{\"itemNum\":\"2\",\"areano\":\""+areano+"\",\"buildingno\":\""+buildingno+"\",\"floorno\":\""+floorno+"\",\"roomno\":\""+roomno+"\"}")
                            .build())
                    .build())
                    .execute().body().string();
            LogUtils.i(res);
            JSONObject jsonObject = new JSONObject(res);
            if (jsonObject.getString("ajaxState").equals("3")&&jsonObject.getJSONObject("feeDate").has("balance")){
                String blance = jsonObject.getJSONObject("feeDate").getString("balance");
                emitter.onNext(blance);
            }else{
                emitter.onError(new Throwable("电费查询失败"));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}

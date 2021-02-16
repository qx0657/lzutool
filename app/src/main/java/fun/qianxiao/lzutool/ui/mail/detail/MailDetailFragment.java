package fun.qianxiao.lzutool.ui.mail.detail;

import android.annotation.SuppressLint;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;


import java.util.Iterator;
import java.util.Map;

import fun.qianxiao.lzutool.R;
import fun.qianxiao.lzutool.base.BaseDateBadingFeagment;
import fun.qianxiao.lzutool.databinding.FragmentMailDetailBinding;
import fun.qianxiao.lzutool.ui.mail.ILzuMailView;
import fun.qianxiao.lzutool.ui.mail.bean.MailInfo;
import fun.qianxiao.lzutool.utils.MyCookieUtils;
import fun.qianxiao.lzutool.utils.MySpUtils;

public class MailDetailFragment extends BaseDateBadingFeagment<FragmentMailDetailBinding> {
    private MailInfo mailInfo;
    private ILzuMailView iLzuMailView;

    public MailDetailFragment(ILzuMailView iLzuMailView, MailInfo mailInfo) {
        this.iLzuMailView = iLzuMailView;
        this.mailInfo = mailInfo;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mail_detail;
    }

    @Override
    protected void initViewModel() {
        binding.setMailInfo(mailInfo);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initData() {
        //支持javascript
        binding.webview.getSettings().setJavaScriptEnabled(true);
        //设置可以支持缩放
        binding.webview.getSettings().setSupportZoom(true);
        //设置出现缩放工具
        binding.webview.getSettings().setBuiltInZoomControls(true   );
        //扩大比例的缩放
        binding.webview.getSettings().setUseWideViewPort(true);
        binding.webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        binding.webview.getSettings().setLoadWithOverviewMode(true);
        binding.webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        binding.webview.getSettings().setGeolocationEnabled(true);
        binding.webview.getSettings().setDomStorageEnabled(true);

        CookieManager cookieManager = CookieManager.getInstance();

        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(null);
            cookieManager.flush();
        } else {
            cookieManager.removeSessionCookie();
            CookieSyncManager.getInstance().sync();
        }
        String url = "https://mail.lzu.edu.cn/coremail/XT5/jsp/viewMailHTML.jsp?mid="+mailInfo.getId()+"&mailCipherPassword=&partId=&isSearch=&priority=&supportSMIME=&striptTrs=true&mboxa=&iframeId=&isAuditMail=false&sspurl=false";
        String hostURL = "https://mail.lzu.edu.cn";
        String coolie_mail = MySpUtils.getString("coolie_mail");
        Map<String,String> coolie_mail_map = MyCookieUtils.cookieStr2map(coolie_mail);
        Iterator<String> iterator = coolie_mail_map.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            cookieManager.setCookie(hostURL, key+"="+coolie_mail_map.get(key)+"; Path=/;");
        }
        cookieManager.flush();
        binding.webview.loadUrl(url);
        binding.webview.requestFocus();
        binding.webview.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
    }

    @Override
    public boolean onBackPressed() {
        iLzuMailView.back();
        return true;
    }
}

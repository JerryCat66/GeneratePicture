package com.hexway.linan.utils.picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.hexway.linan.R;

/**
 * Created by linanjs on 2017/2/15.
 * 显示卡片内容控件
 */
public class GeneratePictureView extends FrameLayout {
    WebView webView = null;

    public GeneratePictureView(Context context) {
        super(context);
        init(context);
    }

    public GeneratePictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GeneratePictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            WebView.enableSlowWholeDocumentDraw();
        }
        LayoutInflater.from(context).inflate(R.layout.layout_webiew, this);
        webView = (WebView) findViewById(R.id.detaile_webview);
        webView.setHorizontalScrollbarOverlay(false);
        webView.setVerticalScrollbarOverlay(false);

        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);// 屏幕自适应网页
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    public WebView getWebView() {
        return webView;
    }

    private boolean isFirstLoad = false;

    /**
     * 图片内容，其中data为整个内容数据拼接成html
     *
     * @param headUrl
     * @param startAddress
     * @param endAddress
     * @param goodsInfo
     * @param carInfo
     * @param contact
     * @param phoneNum
     */
    public void init(final String headUrl, final String startAddress, final String endAddress, final String goodsInfo, final String carInfo, final String contact, final String phoneNum) {
        if (Build.VERSION.SDK_INT >= 21) {
            isFirstLoad = true;
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress == 100) {
                        if (isFirstLoad) {
                            isFirstLoad = false;
                            showData(headUrl, startAddress, endAddress, goodsInfo, carInfo, contact, phoneNum);
                        }
                    }
                }
            });
            webView.loadUrl("file:///android_asset/generate_pic.html");
        } else {
            isFirstLoad = true;
            webView.setVisibility(View.GONE);
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress == 100) {
                        if (!isFirstLoad) {
                            webView.setVisibility(View.VISIBLE);
                            showData(headUrl, startAddress, endAddress, goodsInfo, carInfo, contact, phoneNum);
                        }
                    }
                }
            });
            webView.loadUrl("file:///android_asset/generate_pic.html");
        }
    }

    /**
     * 显示数据
     */
    public void showData(String headUrl, String startAddress, String endAddress, String goodsInfo, String carInfo, String contact, String phoneNum) {
        if (headUrl == null) {
            headUrl = "";
        }
        if (startAddress == null) {
            startAddress = "";
        }
        if (endAddress == null) {
            endAddress = "";
        }
        if (goodsInfo == null) {
            goodsInfo = "";
        }
        if (carInfo == null) {
            carInfo = "";
        }
        if (contact == null) {
            contact = "";
        }
        if (phoneNum == null) {
            phoneNum = "";
        }
        String data = "";
        data += "<div class=\"top\">" +
                "<p style=\"font-size:22px\"><strong>" + startAddress + "</strong>" + "<img style=\"margin:0 10px 0px 10px\" src='../android_asset/icon_good_list_arrow.png'/>" + "<strong>" + endAddress + "</strong>" + "</p>"
                + "<br/>\n"
                + "<p style=\"\">" + goodsInfo + "/" + carInfo + "</p>"
                + "<br/>\n"
                + "<p style=\"font-size:16px\">" + "发布时间:2017-7-20 21:05" + "</p>"
                + "</div>"
                + "<div class=\"middle\">"
               /* + "<p class=\"imageStyle\">" + "<img src=" + headUrl + " />" + "</p>"*/
                + "<p class=\"imageStyle\">" + "<img src='../android_asset/linan_qr_code.png' />" + "</p>"
                + "<p style=\"text-align:center;font-size:14px;color:#444444\">" + "点头像就能扫二维码？" + "</p>"
                + "</div>"
                + "<div class=\"bottom\">"
                + "<img style=\"float:left;width:120px;height:60px;text-align:center;margin:0px 10px 0px 0px;\" src='../android_asset/linan_logo.png' />"
                + "<p style=\"text-align:center;padding:10px 0px 10px 10px;\"><strong>" + "联系人:" + contact + "</strong></p>"
                + "<p style=\"color:black;text-align:center\">" + phoneNum + "</p>"
                + "</div>";
        webView.loadUrl("javascript:changeContent(\"" + data.replace("\n", "\\n").replace("\"", "\\\"").replace("'", "\\'") + "\")");
        if (Build.VERSION.SDK_INT < 21) {
            webView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    webView.reload();
                }
            }, 500);//防止低配手机显示不了图片
        }
    }

    /**
     * 截屏
     *
     * @return
     */
    public Bitmap getScreen() {
        Bitmap bmp = Bitmap.createBitmap(webView.getWidth(), 1, Bitmap.Config.ARGB_8888);
        int rowBytes = bmp.getRowBytes();
        bmp = null;

        if (rowBytes * webView.getHeight() >= getAvailMemory()) {
            return null;
        }
        bmp = Bitmap.createBitmap(webView.getWidth(), webView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
       /* Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        RectF rectF = new RectF(rect);
        float roundPx = 6;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bmp, rect, rect, paint);*/
        webView.draw(canvas);
        return bmp;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        webView.setVisibility(View.GONE);
        this.removeView(webView);
        webView.destroy();
        webView = null;
    }

    private long getAvailMemory() {// 获取android当前可用内存大小
        return Runtime.getRuntime().maxMemory();
    }
}

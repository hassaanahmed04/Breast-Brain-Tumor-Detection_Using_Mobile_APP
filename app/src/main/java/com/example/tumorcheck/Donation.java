package com.example.tumorcheck;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Donation extends AppCompatActivity {
    String postData = "";
    private WebView mWebView;

    private final String Jazz_MerchantID      = "MC58962";
    private final String Jazz_Password        = "zd32t6a059";
    private final String Jazz_IntegritySalt   = "0uv5tx45tu";

    private static final String paymentReturnUrl="http://www.softwand.net";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        mWebView = (WebView) findViewById(R.id.donation_payment_webview);
        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new MyWebViewClient());
        webSettings.setDomStorageEnabled(true);
        mWebView.addJavascriptInterface(new FormDataInterface(), "FORMOUT");

        Intent intentData = getIntent();
        String price = intentData.getStringExtra("price");
        System.out.println("Softwand: price_before : " +price);

        String[] values = price.split("\\.");
        price = values[0];
        price = price + "00";
        System.out.println("Softwand: price : " +price);

        Date Date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddkkmmss");
        String DateString = dateFormat.format(Date);
        System.out.println("Softwand: DateString : " +DateString);

        // Convert Date to Calendar
        Calendar c = Calendar.getInstance();
        c.setTime(Date);
        c.add(Calendar.HOUR, 1);

        // Convert calendar back to Date
        Date currentDateHourPlusOne = c.getTime();
        String expiryDateString = dateFormat.format(currentDateHourPlusOne);
        System.out.println("TumorCheck: expiryDateString : " +expiryDateString);

        String TransactionIdString = "T" + DateString;
        System.out.println("TumorCheck: TransactionIdString : " +TransactionIdString);


        String pp_MerchantID = Jazz_MerchantID;
        String pp_Password = Jazz_Password;
        String IntegritySalt = Jazz_IntegritySalt;
        String pp_ReturnURL = paymentReturnUrl;
        String pp_Amount = price;
        String pp_TxnDateTime = DateString;
        String pp_TxnExpiryDateTime = expiryDateString;
        String pp_TxnRefNo = TransactionIdString;
        String pp_Version = "1.1";
        String pp_TxnType = "";
        String pp_Language = "EN";
        String pp_SubMerchantID = "";
        String pp_BankID = "TBANK";
        String pp_ProductID = "DON";
        String pp_TxnCurrency = "PKR";
        String pp_BillReference = "billRef";
        String pp_Description = "Description of transaction";
        String pp_SecureHash = "";
        String pp_mpf_1 = "1";
        String pp_mpf_2 = "2";
        String pp_mpf_3 = "3";
        String pp_mpf_4 = "4";
        String pp_mpf_5 = "5";

        //sortedString = "cwu55225t6&1000&TBANK&billRef&Description of transaction&EN&MC10487&z740xw7fu0&RETL&http://localhost/jazzcash_part_3/order_placed.php&PKR&20201223202501&20201223212501&T20201223202501&1.1&1&2&3&4&5";
        //pp_SecureHash = php_hash_hmac(sortedString, IntegritySalt);

        String sortedString = "";
        sortedString += IntegritySalt + "&";
        sortedString += pp_Amount + "&";
        sortedString += pp_BankID + "&";
        sortedString += pp_BillReference + "&";
        sortedString += pp_Description + "&";
        sortedString += pp_Language + "&";
        sortedString += pp_MerchantID + "&";
        sortedString += pp_Password + "&";
        sortedString += pp_ProductID + "&";
        sortedString += pp_ReturnURL + "&";
        //sortedString += pp_SubMerchantID + "&";
        sortedString += pp_TxnCurrency + "&";
        sortedString += pp_TxnDateTime + "&";
        sortedString += pp_TxnExpiryDateTime + "&";
        //sortedString += pp_TxnType + "&";
        sortedString += pp_TxnRefNo + "&";
        sortedString += pp_Version + "&";
        sortedString += pp_mpf_1 + "&";
        sortedString += pp_mpf_2 + "&";
        sortedString += pp_mpf_3 + "&";
        sortedString += pp_mpf_4 + "&";
        sortedString += pp_mpf_5;

        pp_SecureHash = php_hash_hmac(sortedString, IntegritySalt);
        System.out.println("Softwand: sortedString : " +sortedString);
        System.out.println("Softwand: pp_SecureHash : " +pp_SecureHash);

        try {
            postData += URLEncoder.encode("pp_Version", "UTF-8")
                    + "=" + URLEncoder.encode(pp_Version, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_TxnType", "UTF-8")
                    + "=" + pp_TxnType + "&";
            postData += URLEncoder.encode("pp_Language", "UTF-8")
                    + "=" + URLEncoder.encode(pp_Language, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_MerchantID", "UTF-8")
                    + "=" + URLEncoder.encode(pp_MerchantID, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_SubMerchantID", "UTF-8")
                    + "=" + pp_SubMerchantID + "&";
            postData += URLEncoder.encode("pp_Password", "UTF-8")
                    + "=" + URLEncoder.encode(pp_Password, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_BankID", "UTF-8")
                    + "=" + URLEncoder.encode(pp_BankID, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_ProductID", "UTF-8")
                    + "=" + URLEncoder.encode(pp_ProductID, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_TxnRefNo", "UTF-8")
                    + "=" + URLEncoder.encode(pp_TxnRefNo, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_Amount", "UTF-8")
                    + "=" + URLEncoder.encode(pp_Amount, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_TxnCurrency", "UTF-8")
                    + "=" + URLEncoder.encode(pp_TxnCurrency, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_TxnDateTime", "UTF-8")
                    + "=" + URLEncoder.encode(pp_TxnDateTime, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_BillReference", "UTF-8")
                    + "=" + URLEncoder.encode(pp_BillReference, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_Description", "UTF-8")
                    + "=" + URLEncoder.encode(pp_Description, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_TxnExpiryDateTime", "UTF-8")
                    + "=" + URLEncoder.encode(pp_TxnExpiryDateTime, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_ReturnURL", "UTF-8")
                    + "=" + URLEncoder.encode(pp_ReturnURL, "UTF-8") + "&";
            postData += URLEncoder.encode("pp_SecureHash", "UTF-8")
                    + "=" + pp_SecureHash + "&";
            postData += URLEncoder.encode("ppmpf_1", "UTF-8")
                    + "=" + URLEncoder.encode(pp_mpf_1, "UTF-8") + "&";
            postData += URLEncoder.encode("ppmpf_2", "UTF-8")
                    + "=" + URLEncoder.encode(pp_mpf_2, "UTF-8") + "&";
            postData += URLEncoder.encode("ppmpf_3", "UTF-8")
                    + "=" + URLEncoder.encode(pp_mpf_3, "UTF-8") + "&";
            postData += URLEncoder.encode("ppmpf_4", "UTF-8")
                    + "=" + URLEncoder.encode(pp_mpf_4, "UTF-8") + "&";
            postData += URLEncoder.encode("ppmpf_5", "UTF-8")
                    + "=" + URLEncoder.encode(pp_mpf_5, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("Softwand: postData : " +postData);

        mWebView.postUrl("https://sandbox.jazzcash.com.pk/CustomerPortal/transactionmanagement/merchantform/", postData.getBytes());
    }

    private class MyWebViewClient extends WebViewClient {
        private final String jsCode ="" + "function parseForm(form){"+
                "var values='';"+
                "for(var i=0 ; i< form.elements.length; i++){"+
                "   values+=form.elements[i].name+'='+form.elements[i].value+'&'"+
                "}"+
                "var url=form.action;"+
                "console.log('parse form fired');"+
                "window.FORMOUT.processFormData(url,values);"+
                "   }"+
                "for(var i=0 ; i< document.forms.length ; i++){"+
                "   parseForm(document.forms[i]);"+
                "};";

        //private static final String DEBUG_TAG = "CustomWebClient";

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if(url.equals(paymentReturnUrl)){
                System.out.println("Softwand: return url cancelling");
                view.stopLoading();
                return;
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //Log.d(DEBUG_TAG, "Url: "+url);
            if(url.equals(paymentReturnUrl)){
                return;
            }
            view.loadUrl("javascript:(function() { " + jsCode + "})()");

            super.onPageFinished(view, url);
        }
    }

    private class FormDataInterface {
        @JavascriptInterface
        public void processFormData(String url, String formData) {
            Intent i = new Intent(Donation.this, MainActivity.class);

            System.out.println("Softwand: Url:" + url + " form data " + formData);
            if (url.equals(paymentReturnUrl)) {
                String[] values = formData.split("&");
                for (String pair : values) {
                    String[] nameValue = pair.split("=");
                    if (nameValue.length == 2) {
                        System.out.println("Softwand: Name:" + nameValue[0] + " value:" + nameValue[1]);
                        i.putExtra(nameValue[0], nameValue[1]);
                    }
                }

                setResult(RESULT_OK, i);
                finish();

                return;
            }
        }
    }

    public static String php_hash_hmac(String data, String secret) {
        String returnString = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] res = sha256_HMAC.doFinal(data.getBytes());
            returnString = bytesToHex(res);

        }catch (Exception e){
            e.printStackTrace();
        }

        return returnString;
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
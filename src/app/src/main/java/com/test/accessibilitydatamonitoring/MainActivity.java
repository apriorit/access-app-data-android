package com.test.accessibilitydatamonitoring;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.test.accessibilitydatamonitoring.MyAccessibilityService.urls;

public class MainActivity extends Activity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.urls);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    public void clearUrls(View view) {
        urls.clear();
        displayUrls();
    }

    private void displayUrls() {
        textView.setText(getReadableUrlsInfo(urls));
    }

    @Override
    protected void onResume() {
        super.onResume();
        toastIfAccessibilityDisabled();
        displayUrls();
    }

    private void toastIfAccessibilityDisabled() {
        if (!isAccessibilityEnabled(this, MyAccessibilityService.class)) {
            Toast.makeText(this, "Enable accessibility in Settings -> Accessibility -> " + getString(R.string.accessibility_label), Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isAccessibilityEnabled(Context context, Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }

    private String getReadableUrlsInfo(List<UrlInfo> urls) {
        String result = "";
        if (urls.isEmpty()) {
            result = "NO ANY URLS...";
        } else {
            for (UrlInfo url : urls) {
                result += new SimpleDateFormat("hh:mm:ss.SSS", Locale.getDefault()).format(url.time) + "  " + url.name
                        + "\r\n"
                        + "\r\n";
            }
        }

        return result;
    }
}

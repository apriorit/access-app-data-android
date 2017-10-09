package com.test.accessibilitydatamonitoring;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.util.Patterns;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    public static final String TAG = "MONITORING";
    public static List<UrlInfo> urls = new ArrayList<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        CharSequence eventPackageName = event.getPackageName();
        if (eventPackageName == null) {
            return;
        }


        printEventInfo(event);
        printNodeTreeDepth(event.getSource());

        int eventType = event.getEventType();
        if (eventPackageName.equals("org.mozilla.firefox")) {
            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
                    || eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                searchFirefoxUrlBreadth(event.getSource());
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    void printEventInfo(AccessibilityEvent event) {
        Log.d(TAG, "EventType=" + AccessibilityEvent.eventTypeToString(event.getEventType()) + ", package=" + event.getPackageName());
    }

    private static void printNodeTreeDepth(AccessibilityNodeInfo nodeInfo) {
        depthFirstSearchNodeProcessing(nodeInfo, 0);
    }

    public static void depthFirstSearchNodeProcessing(AccessibilityNodeInfo nodeInfo, int depth) {
        if (nodeInfo == null) {
            return;
        }

        Log.d(TAG, depth + "__" + nodeInfo.getClassName() + ", " + nodeInfo.getViewIdResourceName() + ", " + nodeInfo.getText());

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            depthFirstSearchNodeProcessing(nodeInfo.getChild(i), depth + 1);
        }
    }

    private void searchFirefoxUrlBreadth(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> unvisited = new ArrayList<>();
        unvisited.add(nodeInfo);

        while (!unvisited.isEmpty()) {
            AccessibilityNodeInfo node = unvisited.remove(0);

            if (node == null) {
                continue;
            }

            if (firefoxUrlFound(node)) {
                return;
            }

            for (int i = 0; i < node.getChildCount(); i++) {
                unvisited.add(node.getChild(i));
            }
        }
    }

    private boolean firefoxUrlFound(AccessibilityNodeInfo nodeInfo) {
        if ("android.widget.TextView".equals(nodeInfo.getClassName())
                && "org.mozilla.firefox:id/url_bar_title".equals(nodeInfo.getViewIdResourceName())
                && nodeInfo.getText() != null) {
            saveUrl(nodeInfo.getText().toString());
            return true;
        }
        return false;
    }

    private void saveUrl(String visibleUrl) {
        // skip if url is not valid
        if (!Patterns.WEB_URL.matcher(visibleUrl).matches()) {
            return;
        }

        // skip if already saved
        String lastAccessedUrl = urls.isEmpty() ? null : urls.get(urls.size() - 1).name;
        if (!visibleUrl.equals(lastAccessedUrl)) {
            urls.add(new UrlInfo(System.currentTimeMillis(), visibleUrl));
            Log.d(TAG, "Firefox url added:" + visibleUrl);
        }
    }
}

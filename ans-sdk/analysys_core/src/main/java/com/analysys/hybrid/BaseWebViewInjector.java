package com.analysys.hybrid;

/**
 * @Copyright © 2020 Analysys Inc. All rights reserved.
 * @Description:
 * @Create: 2020/5/20 6:31 PM
 * @author: huchangqing
 */
public class BaseWebViewInjector {

    public boolean isHybrid(int hashCode) {
        return true;
    }

    public void onVisualDomList(int hashCode, String info) {
    }

    public void onProperty(int hashCode, String info) {
    }

    public void AnalysysAgentTrack(int hashCode, String eventId, String eventInfo, String extraEditInfo) {
    }

    public String getEventList(int hashCode) {
        return null;
    }

    public String getProperty(Object webView, String info) {
        return null;
    }

    public void notifyInject(int hashCode) {
    }

    public void clearHybrid(int hashCode) {
    }
}

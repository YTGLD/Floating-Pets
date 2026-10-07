package com.ytgld.floating_pets;

public class HandlerClient {
    private static boolean showRenderWarped = false;
    private static boolean showRenderLight = false;
    public static boolean isShowRenderWarped() {
        return showRenderWarped;
    }
    public static void setShowRenderWarped(boolean showRenderWarped) {
        HandlerClient.showRenderWarped = showRenderWarped;
    }

    public static boolean isShowRenderLight() {
        return showRenderLight;
    }

    public static void setShowRenderLight(boolean showRenderLight) {
        HandlerClient.showRenderLight = showRenderLight;
    }
}

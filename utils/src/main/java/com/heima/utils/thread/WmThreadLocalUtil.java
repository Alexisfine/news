package com.heima.utils.thread;

import com.heima.model.wemedia.pojos.WmUser;

public class WmThreadLocalUtil {
    private static final ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    // add user into thread
    public static void setUser(WmUser user) {
        WM_USER_THREAD_LOCAL.set(user);
    }

    // get user
    public static WmUser getUser() {
        return WM_USER_THREAD_LOCAL.get();
    }

    // remove user from thread
    public static void clear() {
        WM_USER_THREAD_LOCAL.remove();
    }

}

package com.vplib.vortex.vplib;

/**
 * @author Simon Dräger
 * @version 11.3.18
 */

public interface ProgressCallback {
    void onProgress(int percent);
    void onComplete();
}

package com.saaei12092021.office.util;

import android.content.IntentSender;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static com.google.android.play.core.install.model.InstallStatus.DOWNLOADED;

/**
 * A simple implementation of the Android In-App Update API.
 * <p>
 * <div class="special reference">
 * <h3>In-App Updates</h3>
 * <p>For more information about In-App Updates you can check the official
 * <a href="https://developer.android.com/guide/app-bundle/in-app-updates">documentation</a>
 * </p>
 * </div>
 */
public class InAppUpdateManager implements LifecycleObserver {
//
//    /**
//     * Callback methods where update events are reported.
//     */
//    public interface InAppUpdateHandler {
//        /**
//         * On update error.
//         *
//         * @param code  the code
//         * @param error the error
//         */
//        void onInAppUpdateError(int code, Throwable error);
//
//
//        /**
//         * Monitoring the update state of the flexible downloads.
//         * For immediate updates, Google Play takes care of downloading and installing the update for you.
//         *
//         * @param status the status
//         */
//        void onInAppUpdateStatus(AppConstants.InstallStat status);
//
//
//        /**
//         * Monitoring the update availability.
//         *
//         * @param isAvailable the Boolean value for availability.
//         */
//        void onInAppUpdateAvailable(boolean isAvailable);
//    }
//
//    // Region member variable declarations
//    private AppCompatActivity activity;
//    private AppUpdateManager appUpdateManager;
//    private AppConstants.UpdateMode mode = AppConstants.UpdateMode.FLEXIBLE;
//    private InAppUpdateHandler handler;
//    private Snackbar snackbar;
//    private long noOfDaysToShowNextUpdateFlow = 0;
//
//    // Application Specific Component
//    private KeyStorePref keyStorePref;
//
//    private static final String TAG = "InAppUpdateManager";
//
//
//    private InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
//        @Override
//        public void onStateUpdate(InstallState installState) {
//            // Show module progress, log state, or install the update.
//            if (installState.installStatus() == DOWNLOADED) {
//                Log.d(TAG, "onStateUpdate: installStateUpdatedListener : DOWNLOADED");
//                // After the update is downloaded, show a notification
//                // and request user confirmation to restart the app.
//                popupSnackbarForUserConfirmation();
//                reportInstallStatus(AppConstants.InstallStat.DOWNLOADED);
//            } else if (installState.installStatus() == InstallStatus.INSTALLED) {
//                Log.d(TAG, "onStateUpdate: installStateUpdatedListener : INSTALLED");
//                appUpdateManager.unregisterListener(installStateUpdatedListener);
//                reportInstallStatus(AppConstants.InstallStat.INSTALLED);
//            }
//        }
//    };
//    // End region
//
//
//    // Region Constructor
//
//    /**
//     * Creates a private constructor because we are making it Singleton.
//     *
//     * @param activity the activity
//     */
//    public InAppUpdateManager(AppCompatActivity activity) {
//        this.activity = activity;
//        init();
//    }
//    // End Region
//
//
//    private void init() {
//        appUpdateManager = AppUpdateManagerFactory.create(this.activity);
//        activity.getLifecycle().addObserver(this);
//        keyStorePref = KeyStorePref.getInstance(activity);
//    }
//
//
//    // Region Setters
//
//    /**
//     * Set the callback handler
//     *
//     * @param handler the handler
//     */
//    public void handler(InAppUpdateHandler handler) {
//        this.handler = handler;
//    }
//
//    /**
//     * Set the Update Mode
//     *
//     * @param mode the UpdateMode
//     */
//    @SuppressWarnings("unused")
//    public void mode(AppConstants.UpdateMode mode) {
//        this.mode = mode;
//    }
//
//    // End Region
//
//    // Region Lifecycle
//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    public void onResume() {
//        checkOnResumeState();
//    }
//
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    public void onDestroy() {
//        unregisterListener();
//    }
//
//    // End Region
//
//    // Region Methods
//
//    /**
//     * Check for update availability. If there will be an update available
//     * will start the update process with the selected {@link AppConstants.UpdateMode}.
//     */
//    public void checkForAppUpdate() {
//        checkForUpdate();
//    }
//
//    //endregion
//
//    // Region Private Methods
//
//    /**
//     * Check for update availability. If there will be an update available
//     * will start the update process with the selected {@link AppConstants.UpdateMode}.
//     * will return if failure happens while reporting status
//     */
//    private void checkForUpdate() {
//        // Checks that the platform will allow the specified type of update.
//        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
//                Log.d(TAG, "updateChecker() : UPDATE_AVAILABLE");
//                // Report the update availability callback
//                reportUpdateInfo(true);
//
//                // Fetch Firebase Remote Config Values and Check if update has to be shown for version number
//                if (fetchFirebaseAndCheck()) {
//                    // Request the update while checking the update mode
//                    if (this.mode == AppConstants.UpdateMode.FLEXIBLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
//                        // Check if last shown dialog was some days ago coming from firebase config
//                        if (getLastShownDayOfInAppUpdate()) {
//                            Log.d(TAG, "updateChecker() : UPDATE_AVAILABLE : FLEXIBLE MODE");
//                            // Start a FLEXIBLE update.
//                            startAppUpdateFlexible(appUpdateInfo);
//                        } else {
//                            reportUpdateInfo(false);
//                        }
//                    } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                        Log.d(TAG, "updateChecker() : UPDATE_AVAILABLE : IMMEDIATE MODE");
//                        // Start an IMMEDIATE update.
//                        startAppUpdateImmediate(appUpdateInfo);
//                    }
//                } else {
//                    reportUpdateInfo(false);
//                }
//
//            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
//                Log.d(TAG, "updateChecker() : UPDATE_NOT_AVAILABLE");
//
//                keyStorePref.putLong(KEY_APP_UPDATE_LAST_SHOWN_DAY, 0);
//
//                Log.d(TAG, "updateChecker() : UPDATE_NOT_AVAILABLE_CALLBACK");
//
//                // Report the update availability callback
//                reportUpdateInfo(false);
//            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
//                Log.d(TAG, "updateChecker() : DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS");
//                if (mode == AppConstants.UpdateMode.IMMEDIATE) {
//                    Log.d(TAG, "updateChecker() : DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS : IMMEDIATE");
//                    startAppUpdateImmediate(appUpdateInfo);
//                } else {
//                    Log.d(TAG, "updateChecker() : DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS : FLEXIBLE");
//                    if (appUpdateInfo.installStatus() == DOWNLOADED) {
//                        Log.d(TAG, "updateChecker() : DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS :  FLEXIBLE : DOWNLOADED");
//                        popupSnackbarForUserConfirmation();
//                    }
//                }
//            }
//        });
//
//        appUpdateManager.getAppUpdateInfo().addOnFailureListener(e -> {
//            Log.i(TAG, "Failure" + e.getMessage());
//            reportUpdateInfo(false);
//        });
//
//    }
//
//    /**
//     * Checks that the update is not stalled during 'onResume()'.
//     * However, you should execute this check at all app entry points.
//     */
//    private void checkOnResumeState() {
//        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
//            Log.d(TAG, "onResume : onSuccess : " + appUpdateInfo.installStatus());
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
//                Log.d(TAG, "onResume : onSuccess : DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS");
//                if (mode == AppConstants.UpdateMode.IMMEDIATE) {
//                    Log.d(TAG, "onResume : onSuccess : IMMEDIATE");
//                    startAppUpdateImmediate(appUpdateInfo);
//                } else {
//                    Log.d(TAG, "onResume : onSuccess : FLEXIBLE");
//                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
//                        Log.d(TAG, "onResume : onSuccess : FLEXIBLE : DOWNLOADED");
//                        popupSnackbarForUserConfirmation();
//                    }
//                }
//            }
//        });
//    }
//
//    /**
//     * Starts an Immediate Update.
//     * report Update error if found any
//     *
//     * @param appUpdateInfo the {@link AppUpdateInfo}
//     */
//    private void startAppUpdateImmediate(AppUpdateInfo appUpdateInfo) {
//        try {
//            appUpdateManager.startUpdateFlowForResult(
//                    appUpdateInfo,
//                    AppUpdateType.IMMEDIATE,
//                    // The current activity making the update request.
//                    activity,
//                    // Include a request code to later monitor this update request.
//                    AppConstants.REQ_CODE_VERSION_UPDATE_IMMEDIATE);
//        } catch (IntentSender.SendIntentException e) {
//            Log.e(TAG, "error in startAppUpdateImmediate", e);
//            reportUpdateError(AppConstants.UPDATE_ERROR_START_APP_UPDATE_IMMEDIATE, e);
//        }
//    }
//
//    /**
//     * Starts an Flexible Update.
//     * report Update error if found any
//     *
//     * @param appUpdateInfo the {@link AppUpdateInfo}
//     */
//    private void startAppUpdateFlexible(AppUpdateInfo appUpdateInfo) {
//        appUpdateManager.registerListener(installStateUpdatedListener);
//
//        try {
//            appUpdateManager.startUpdateFlowForResult(
//                    appUpdateInfo,
//                    AppUpdateType.FLEXIBLE,
//                    // The current activity making the update request.
//                    activity,
//                    // Include a request code to later monitor this update request.
//                    AppConstants.REQ_CODE_VERSION_UPDATE_FLEXIBLE);
//        } catch (IntentSender.SendIntentException e) {
//            Log.e(TAG, "error in startAppUpdateFlexible", e);
//            reportUpdateError(AppConstants.UPDATE_ERROR_START_APP_UPDATE_FLEXIBLE, e);
//        }
//    }
//
//    /**
//     * Displays the snackbar notification and call to action.
//     * Needed only for Flexible app update
//     */
//    private void popupSnackbarForUserConfirmation() {
//        Log.d(TAG, "setupSnackbar");
//        if (snackbar != null && snackbar.isShownOrQueued()) {
//            snackbar.dismiss();
//        }
//        snackbar = Snackbar.make(activity.findViewById(android.R.id.content),
//                activity.getString(R.string.update_downloaded),
//                Snackbar.LENGTH_INDEFINITE);
//
//        snackbar.setAction(activity.getString(R.string.restart), view -> {
//            // Triggers the completion of the update of the app for the flexible flow.
//            appUpdateManager.completeUpdate();
//        });
//        snackbar.show();
//    }
//
//    /**
//     * Unregister {@link #installStateUpdatedListener}
//     */
//    private void unregisterListener() {
//        if (appUpdateManager != null && installStateUpdatedListener != null)
//            appUpdateManager.unregisterListener(installStateUpdatedListener);
//    }
//
//    /**
//     * callback for update error
//     *
//     * @param errorCode the {@link AppConstants}
//     * @param error     the error Throwable
//     */
//    private void reportUpdateError(int errorCode, Throwable error) {
//        if (handler != null) {
//            handler.onInAppUpdateError(errorCode, error);
//        }
//    }
//
//    /**
//     * callback for update Install Status {@link AppConstants.InstallStat}
//     *
//     * @param installStatus the {@link AppConstants.InstallStat}
//     */
//    private void reportInstallStatus(AppConstants.InstallStat installStatus) {
//        if (handler != null) {
//            handler.onInAppUpdateStatus(installStatus);
//        }
//    }
//
//    /**
//     * callback for report app update availability
//     *
//     * @param isAvailable the availability
//     */
//    private void reportUpdateInfo(boolean isAvailable) {
//        if (handler != null) {
//            handler.onInAppUpdateAvailable(isAvailable);
//        }
//    }
//
//
//    /**
//     * method to fetch firebase config values
//     *
//     * @return whether to check for update or not
//     */
//    private boolean fetchFirebaseAndCheck() {
//        Log.d(TAG, "getFirebaseConfig: ");
//        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//        if (!TextUtils.isEmpty(firebaseRemoteConfig.getString(KEY_FIREBASE_REMOTE_CONFIG_UPDATE_TYPE))) {
//            String[] arrUpdateType = firebaseRemoteConfig.getString(KEY_FIREBASE_REMOTE_CONFIG_UPDATE_TYPE).split("#");
//            Log.d(TAG, "getFirebaseConfig: updateVersion" + arrUpdateType[0] + " updateType " + arrUpdateType[1] + "Current App version" + CommonUtils.getVersionCode(activity));
//            int versionNumber = Integer.parseInt(arrUpdateType[0]);
//            this.noOfDaysToShowNextUpdateFlow = Long.parseLong(arrUpdateType[2]);
//            if (versionNumber > CommonUtils.getVersionCode(activity)) {
//                if (TextUtils.equals(arrUpdateType[1], "FLEXIBLE")) {
//                    this.mode = AppConstants.UpdateMode.FLEXIBLE;
//                } else {
//                    this.mode = AppConstants.UpdateMode.IMMEDIATE;
//                }
//                return true;
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }
//
//    /**
//     * method to check if last shown FLEXIBLE dialog is greater than number on firebase.
//     *
//     * @return whether to show the dialog or not.
//     */
//    private boolean getLastShownDayOfInAppUpdate() {
//        if (keyStorePref.getLong(KEY_APP_UPDATE_LAST_SHOWN_DAY) != 0) {
//            long msDiff = Calendar.getInstance().getTimeInMillis() - keyStorePref.getLong(KEY_APP_UPDATE_LAST_SHOWN_DAY);
//            long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
//            return daysDiff > this.noOfDaysToShowNextUpdateFlow;
//        } else {
//            return true;
//        }
//    }
//
//
}

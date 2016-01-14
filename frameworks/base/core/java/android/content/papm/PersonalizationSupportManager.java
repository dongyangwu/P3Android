package android.content.papm;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wdy on 15-6-8.
 */
public class PersonalizationSupportManager {
    private static final String TAG = "PersonalizationSupportManager";
    private final Context mContext;
    private final IPersonalizationSupportManager mService;
    private final Handler mHandler;

    public static int YOUTH = 1;
    public static int MIDLIFE = 2;
    public static int OLD = 3;

    public static boolean status = true;

    public PersonalizationSupportManager(Context context, IPersonalizationSupportManager service, Handler handler) {
        mContext = context;
        mService = service;
        mHandler = handler;
    }

    public void setStatus(boolean psm_status) {
        try {
            mService.setStatus(psm_status);
        } catch (RemoteException e) {
        }
    }

    public boolean getStatus() {
        try {
            return mService.getStatus();
        } catch (RemoteException e) {
        }
        return false;
    }

    public List<String> getPersonalizationSignals() {
        try {
            return mService.getPersonalizationSignals();
        } catch (RemoteException e) {
        }
        return null;
    }

    public void setPersonaeProfile(String[] personae, int[] value) {
        try {
            mService.setPersonaeProfile(personae, value);
        } catch (RemoteException e) {
        }
    }

    public boolean cleanOldPersonalizationSignals() {
        try {
            return mService.cleanOldPersonalizationSignals();
        } catch (RemoteException e) {
            return false;
        }
    }

    public int getTopPersonaeValue() {
        try {
            return mService.getTopPersonaeValue();
        } catch (RemoteException e) {
        }
        return -1;
    }

    public String getTopPersonae() {
        try {
            return mService.getTopPersonae();
        } catch (RemoteException e) {
        }
        return null;
    }

    public List<String> getPersonaeByDescOrder() {
        try {
            return mService.getPersonaeByDescOrder();
        } catch (RemoteException e) {
        }
        return null;
    }

    public int[] getPersonaeValueByDescOrder() {
        try {
            return mService.getPersonaeValueByDescOrder();
        } catch (RemoteException e) {
        }
        return null;
    }

    public int getUserProbablyAge() {
        try {
            return mService.getUserProbablyAge();
        } catch (RemoteException e) {
        }
        return -1;
    }
}

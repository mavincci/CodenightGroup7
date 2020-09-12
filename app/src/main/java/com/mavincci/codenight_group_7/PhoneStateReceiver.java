package com.mavincci.codenight_group_7;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class PhoneStateReceiver extends BroadcastReceiver {
    private String[] blockingNumber = {"251999","810","251984953628","251941420279","25194508849", "251938415869"};

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            final String numberCall = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);;

            //reject call if number is matched to our blocking number
            if (numberCall.equals(blockingNumber)) {
                disconnectPhoneItelephony(context);
            }
        }
        //blocking sms for matched number
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            Object messages[] = (Object[]) bundle.get("pdus");
            SmsMessage smsMessage[] = new SmsMessage[messages.length];

            for (int n = 0; n < messages.length; n++) {
                smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
            }

            final String numberSms = smsMessage[0].getOriginatingAddress();
            final String messageSms = smsMessage[0].getDisplayMessageBody();

            //long dateTimeSms = smsMessage[0].getTimestampMillis();
            // Toast.makeText(context,numberSms,Toast.LENGTH_LONG).show();
            Toast.makeText(context, numberSms+":-SMS-blocked", Toast.LENGTH_LONG).show();
            //block sms if number is matched to our blocking number
            if (numberSms.equals(blockingNumber)) {
                abortBroadcast();

            }
        }
    }


    // Keep this method as it is @SuppressWarnings({ "rawtypes", "unchecked" })
    private void disconnectPhoneItelephony(Context context) {
        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class < ? > telephonyClass;
            Class < ? > telephonyStubClass;
            Class < ? > serviceManagerClass;
            Class < ? > serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);
        } catch (Exception e) {
            e.printStackTrace();}
    }
}

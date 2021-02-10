package com.reactnativeandroidsmsverificationapi;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class AndroidSmsVerificationApiModule extends ReactContextBaseJavaModule {

  private int phoneNumberRequestCode = 420;
  private int userConsentRequestCode = 69;
  private Promise promise;
  private static ReactApplicationContext reactContext;


  private final ActivityEventListener activityEventListener = new ActivityEventListener() {
    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
      handleOnActivityResult(activity, requestCode, resultCode, data);
    }
    @Override
    public void onNewIntent(Intent intent) {

    }
  };

  private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
        Bundle extras = intent.getExtras();
        Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
        switch (smsRetrieverStatus.getStatusCode()) {
          case CommonStatusCodes.SUCCESS:
            try {
              String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
              if (message != null) {
                AndroidSmsVerificationApiModule.sendEvent(Constant.SMS_RECEIVED, message);
              } else {
                Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                getCurrentActivity().startActivityForResult(consentIntent, userConsentRequestCode);
              }
            } catch (ActivityNotFoundException e) {
              // TODO: handle error
              e.printStackTrace();
            }
            break;
          case CommonStatusCodes.TIMEOUT:
            AndroidSmsVerificationApiModule.sendEvent(Constant.SMS_ERROR, CommonStatusCodes.TIMEOUT);
            break;
        }
      }
    }
  };

  AndroidSmsVerificationApiModule(ReactApplicationContext context) {
    super(context);
    this.reactContext = context;
    context.addActivityEventListener(activityEventListener);
    IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
    getReactApplicationContext().registerReceiver(smsVerificationReceiver, intentFilter, SmsRetriever.SEND_PERMISSION, null);
  }

  private void handleOnActivityResult (Activity activity, int requestCode, int resultCode, Intent data) {
    if (requestCode == userConsentRequestCode) {
      if (resultCode == Activity.RESULT_OK) {
        String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
        AndroidSmsVerificationApiModule.sendEvent(Constant.SMS_RECEIVED, message);
      } else {
        AndroidSmsVerificationApiModule.sendEvent(Constant.SMS_ERROR, "Unable to retrieve SMS");
      }
    }
    if (requestCode == phoneNumberRequestCode && promise != null) {
      if (resultCode == Activity.RESULT_OK) {
        Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
        promise.resolve(credential.getId());
      } else {
        promise.reject(String.valueOf(resultCode), "Unable to retrieve phone number");
      }
    }
  }

  public static void sendEvent (String eventName, Object data) {
    if (reactContext == null) {
      return;
    }
    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, data);
  }

  @NonNull
  @Override
  public String getName() {
    return "AndroidSmsVerificationApi";
  }

  @ReactMethod
  public void multiply (int a, int b, Promise promise) {
    promise.resolve(a*b+5);
  }

  @ReactMethod
  public void requestPhoneNumber (int phoneNumberRequestCode, Promise promise) {
    this.promise = promise;
    this.phoneNumberRequestCode = phoneNumberRequestCode;
    HintRequest request = new HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build();
    PendingIntent intent = Credentials.getClient(getReactApplicationContext()).getHintPickerIntent(request);
    try {
      getCurrentActivity().startIntentSenderForResult(intent.getIntentSender(), phoneNumberRequestCode, null, 0, 0, 0);
    } catch (Exception e) {
      promise.reject(e);
    }
  }

  @ReactMethod
  public void getAppSignatures (Promise promise) {
    AppSignatureHelper helper = new AppSignatureHelper(getReactApplicationContext());
    WritableNativeArray array = new WritableNativeArray();
    ArrayList<String> signatures = helper.getAppSignatures();
    for (String signature : signatures) {
      array.pushString(signature);
    }
    promise.resolve(array);
  }

  @ReactMethod
  public void startSmsRetriever (Promise promise) {
    SmsRetrieverClient client = SmsRetriever.getClient(getReactApplicationContext());
    Task<Void> task = client.startSmsRetriever();
    task.addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void aVoid) {
        promise.resolve(true);
      }
    });
    task.addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        promise.reject(e);
      }
    });
  }

  @ReactMethod
  public void startSmsUserConsent (String senderPhoneNumber, int userConsentRequestCode, Promise promise) {
    this.userConsentRequestCode = userConsentRequestCode;
    SmsRetrieverClient client = SmsRetriever.getClient(getReactApplicationContext());
    Task<Void> task;
    if (senderPhoneNumber == null) {
      task = client.startSmsUserConsent(null);
    } else {
      task = client.startSmsUserConsent(senderPhoneNumber);
    }
    task.addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void aVoid) {
        promise.resolve(true);
      }
    });
    task.addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        promise.reject(e);
      }
    });
  }

}

package com.reactnativeandroidsmsverificationapi;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class AndroidSmsVerificationApiModule extends ReactContextBaseJavaModule {

  private ActivityEventListener activityEventListener = new ActivityEventListener() {
    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
      handleOnActivityResult(activity, requestCode, resultCode, data);
    }
    @Override
    public void onNewIntent(Intent intent) {

    }
  };

  private int phoneNumberRequestCode = 420;
  private Promise promise;
  private static ReactApplicationContext reactContext;

  AndroidSmsVerificationApiModule(ReactApplicationContext context) {
    super(context);
    this.reactContext = context;
    context.addActivityEventListener(activityEventListener);
  }

  private void handleOnActivityResult (Activity activity, int requestCode, int resultCode, Intent data) {
    if (promise == null || requestCode != phoneNumberRequestCode) {
      return;
    }
    if (resultCode == Activity.RESULT_OK) {
      Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
      promise.resolve(credential.getId());
    } else {
      promise.reject(String.valueOf(resultCode), "Unable to retrieve phone number");
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
}

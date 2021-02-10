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
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;

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

  AndroidSmsVerificationApiModule(ReactApplicationContext context) {
    super(context);
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
}

package com.reactnativeandroidsmsverificationapi;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class AndroidSmsVerificationApiModule extends ReactContextBaseJavaModule {

  AndroidSmsVerificationApiModule(ReactApplicationContext context) {
    super(context);
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
}

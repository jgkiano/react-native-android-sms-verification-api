package com.reactnativeandroidsmsverificationapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SMSRetrieverBroadcastReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
      Bundle extras = intent.getExtras();
      Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
      switch(status.getStatusCode()) {
        case CommonStatusCodes.SUCCESS:
          String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
          AndroidSmsVerificationApiModule.sendEvent(Constant.SMS_RECEIVED, message);
          break;
        case CommonStatusCodes.TIMEOUT:
          AndroidSmsVerificationApiModule.sendEvent(Constant.SMS_ERROR, CommonStatusCodes.TIMEOUT);
          break;
      }
    }
  }
}

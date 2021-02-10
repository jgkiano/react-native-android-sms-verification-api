import { NativeModules, NativeEventEmitter } from 'react-native';

type AndroidSmsVerificationApiType = {
  multiply(a: number, b: number): Promise<number>;
  requestPhoneNumber(requestCode: number): Promise<string>;
  getAppSignatures(): Promise<string[]>;
  startSmsRetriever(): Promise<boolean>;
  startSmsUserConsent(
    senderPhoneNumber: string | null,
    userConsentRequestCode: number
  ): Promise<boolean>;
};

type Callback = (error: Error | null, message: string | null) => any;

const EmitterMessages = {
  SMS_RECEIVED: 'SMS_RECEIVED',
  SMS_ERROR: 'SMS_ERROR',
};

const AndroidSmsVerificationApi: AndroidSmsVerificationApiType =
  NativeModules.AndroidSmsVerificationApi;

export const requestPhoneNumber = (requestCode?: number) => {
  return AndroidSmsVerificationApi.requestPhoneNumber(requestCode || 420);
};

export const receiveVerificationSMS = (callback: Callback) => {
  const eventEmitter = new NativeEventEmitter(
    NativeModules.AndroidSmsVerificationApi
  );
  const onSuccess = (message: string) => {
    callback(null, message);
    removeListeners();
  };
  const onError = (error: string) => {
    callback(Error(error), null);
    removeListeners();
  };
  const removeListeners = () => {
    eventEmitter.removeAllListeners(EmitterMessages.SMS_RECEIVED);
    eventEmitter.removeAllListeners(EmitterMessages.SMS_ERROR);
  };
  eventEmitter.addListener(EmitterMessages.SMS_RECEIVED, onSuccess);
  eventEmitter.addListener(EmitterMessages.SMS_ERROR, onError);
};

export const getAppSignatures = () => {
  return AndroidSmsVerificationApi.getAppSignatures();
};

export const startSmsRetriever = () => {
  return AndroidSmsVerificationApi.startSmsRetriever();
};

export const startSmsUserConsent = (
  senderPhoneNumber?: string,
  userConsentRequestCode?: number
) => {
  return AndroidSmsVerificationApi.startSmsUserConsent(
    senderPhoneNumber || null,
    userConsentRequestCode || 69
  );
};

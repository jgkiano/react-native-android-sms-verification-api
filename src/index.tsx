import { NativeModules, NativeEventEmitter } from 'react-native';

type AndroidSmsVerificationApiType = {
  multiply(a: number, b: number): Promise<number>;
  requestPhoneNumber(requestCode: number): Promise<string>;
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

let cb: Callback | null = null;

const AndroidSmsVerificationApi: AndroidSmsVerificationApiType =
  NativeModules.AndroidSmsVerificationApi;
const eventEmitter = new NativeEventEmitter(
  NativeModules.AndroidSmsVerificationApi
);
const onMessageSuccess = (message: string) => {
  if (typeof cb === 'function') {
    cb(null, message);
  }
};
const onMessageError = (error: string) => {
  if (typeof cb === 'function') {
    cb(Error(error), null);
  }
};
const startListeners = () => {
  removeAllListeners()
  eventEmitter.addListener(EmitterMessages.SMS_RECEIVED, onMessageSuccess);
  eventEmitter.addListener(EmitterMessages.SMS_ERROR, onMessageError);
};

export const removeAllListeners = () => {
  eventEmitter.removeAllListeners(EmitterMessages.SMS_RECEIVED);
  eventEmitter.removeAllListeners(EmitterMessages.SMS_ERROR);
};

export const requestPhoneNumber = (requestCode?: number) => {
  return AndroidSmsVerificationApi.requestPhoneNumber(requestCode || 420);
};

export const receiveVerificationSMS = (callback: Callback) => {
  cb = callback;
  startListeners();
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

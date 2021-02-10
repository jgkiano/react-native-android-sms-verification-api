import { NativeModules, NativeEventEmitter } from 'react-native';

type AndroidSmsVerificationApiType = {
  multiply(a: number, b: number): Promise<number>;
  requestPhoneNumber(requestCode: number): Promise<string>;
  getAppSignatures(): Promise<string[]>;
  startSmsRetriever(): Promise<boolean>;
};

const EmitterMessages = {
  SMS_RECEIVED: 'SMS_RECEIVED',
  SMS_ERROR: 'SMS_ERROR',
};

const AndroidSmsVerificationApi: AndroidSmsVerificationApiType =
  NativeModules.AndroidSmsVerificationApi;

export const requestPhoneNumber = (requestCode?: number) => {
  return AndroidSmsVerificationApi.requestPhoneNumber(requestCode || 420);
};

export const SMSRetriever = {
  getAppSignatures() {
    return AndroidSmsVerificationApi.getAppSignatures();
  },
  receiveVerificationSMS(): Promise<string> {
    return new Promise(async (resolve, reject) => {
      try {
        const eventEmitter = new NativeEventEmitter(
          NativeModules.AndroidSmsVerificationApi
        );
        const onSuccess = (message: string) => {
          resolve(message);
          removeListeners();
        };
        const onError = (message: any) => {
          reject(Error(message));
          removeListeners();
        };
        const removeListeners = () => {
          eventEmitter.removeAllListeners(EmitterMessages.SMS_RECEIVED);
          eventEmitter.removeAllListeners(EmitterMessages.SMS_ERROR);
        };
        eventEmitter.addListener(EmitterMessages.SMS_RECEIVED, onSuccess);
        eventEmitter.addListener(EmitterMessages.SMS_ERROR, onError);
      } catch (error) {
        reject(error);
      }
    });
  },
  startSmsRetriever() {
    return AndroidSmsVerificationApi.startSmsRetriever();
  },
};

import { NativeModules } from 'react-native';

type AndroidSmsVerificationApiType = {
  multiply(a: number, b: number): Promise<number>;
  requestPhoneNumber(requestCode: number): Promise<string>;
};

const AndroidSmsVerificationApi: AndroidSmsVerificationApiType =
  NativeModules.AndroidSmsVerificationApi;

export const requestPhoneNumber = (requestCode?: number) => {
  return AndroidSmsVerificationApi.requestPhoneNumber(requestCode || 420);
};

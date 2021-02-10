import { NativeModules } from 'react-native';

type AndroidSmsVerificationApiType = {
  multiply(a: number, b: number): Promise<number>;
};

const { AndroidSmsVerificationApi } = NativeModules;

export default AndroidSmsVerificationApi as AndroidSmsVerificationApiType;

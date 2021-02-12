# react-native-android-sms-verification-api

A wrapper for the Android SMS Verification API. Includes both [SMS Retriever](https://developers.google.com/identity/sms-retriever/overview) for Zero-tap SMS verification and [SMS User Consent](https://developers.google.com/identity/sms-retriever/user-consent/overview) for the One-tap SMS verification

![Preview](https://user-images.githubusercontent.com/9453250/107610908-ccdc3100-6c53-11eb-90b8-2cd2148f9a25.png)

## Installation

```sh
yarn add react-native-android-sms-verification-api
```

## Usage

### Prerequisites

This library target android devices >= SDK 19. Make sure you're targeting at-least the same by modifying your `android/build.gradle` file

```gradle
ext {
  minSdkVersion = 19
  ..//
}
```

### Requesting the user's phone number

```js
import { requestPhoneNumber } from 'react-native-android-sms-verification-api';

try {
  // presents a modal enabling the user to select their phone number. Requires a physical device, it won't work on an emulator
  const phoneNumber = await requestPhoneNumber();
} catch (error) {
  console.log(`${e.code} : ${e.message}`);
}
```

### Using the User Consent API

```js
import {
  receiveVerificationSMS,
  startSmsUserConsent,
  requestPhoneNumber,
  removeAllListeners,
} from 'react-native-android-sms-verification-api';

// 1. Define a callback that'll receive the message or any errors that occurs
receiveVerificationSMS((error, message) => {
  if (error) {
    // handle error
  } else {
    // parse the message to obtain the verification code
  }
});

// 2. Get the user's phone number
const phoneNumber = await requestPhoneNumber();

// 3. Set's up listeners for the incoming message. !Do this before sending the sms!
await startSmsUserConsent();

// 4. Your custom logic for sending a verification code
await sendVerificationCode(phoneNumber);

// 5. Make sure to remove the listeners after you've successfully retrieved the verification code

removeAllListeners();
```

### Using the SMS Retriever API

The SMS Retriever API requires you to include an 11-character hash string that identifies your app within the SMS body. See message requirements [here](https://developers.google.com/identity/sms-retriever/verify#1_construct_a_verification_message)

You can retrive the has by following these [steps.](https://developers.google.com/identity/sms-retriever/verify#computing_your_apps_hash_string) Or by using the `getAppSignatures` function that's available within the library.

The `getAppSignatures` function uses the `AppSignatureHelper` [java class](https://github.com/googlearchive/android-credentials/blob/master/sms-verification/android/app/src/main/java/com/google/samples/smartlock/sms_verify/AppSignatureHelper.java) and **is not suposed to be included in your application.** For this reason the `getAppSignatures` function is only available on a separate branch of the library.

To get started

1. Include the library version with the signature helper
   `yarn add https://github.com/jgkiano/react-native-android-sms-verification-api.git#with-signature-helper`

2. Change the minSdkVersion of your app to 19 but modifying the `android/build.gradle` file

```gradle
ext {
  minSdkVersion = 19
  ..//
}
```

3. Retrive your app's signature

```js
import { getAppSignatures } from 'react-native-android-sms-verification-api';

const [signature] = await getAppSignatures();
console.log(signature); // e.g FA+9qCX9VSu
```

4. Once you retrive your app's signature, include it in the SMS your server sends to your users e.g

```
Your verification code is: 123ABC78


FA+9qCX9VSu
```

5. Finally remove this version of the library in your project and include the offical release

`yarn remove react-native-android-sms-verification-api && yarn add react-native-android-sms-verification-api`

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

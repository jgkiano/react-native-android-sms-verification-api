# react-native-android-sms-verification-api

A wrapper for the Android SMS Verification API. Includes both [SMS Retriever](https://developers.google.com/identity/sms-retriever/overview) for Zero-tap SMS verification and [SMS User Consent](https://developers.google.com/identity/sms-retriever/user-consent/overview) for the One-tap SMS verification

![Preview](https://user-images.githubusercontent.com/9453250/107610908-ccdc3100-6c53-11eb-90b8-2cd2148f9a25.png)

## Installation

```sh
yarn install react-native-android-sms-verification-api
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
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

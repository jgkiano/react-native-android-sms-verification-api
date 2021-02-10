import * as React from 'react';

import { StyleSheet, View, Button } from 'react-native';
import {
  requestPhoneNumber,
  getAppSignatures,
  startSmsRetriever,
  receiveVerificationSMS,
  startSmsUserConsent,
} from 'react-native-android-sms-verification-api';

export default function App() {
  receiveVerificationSMS((error, message) => {
    console.log(error);
    console.log(message);
  });

  const handleOnRequestPhoneNumber = () => {
    requestPhoneNumber()
      .then(console.log)
      .catch((e) => console.log(`${e.code} : ${e.message}`));
  };

  const handleOnGetSignatures = () => {
    getAppSignatures().then(console.log).catch(console.log);
  };

  const handleOnStartMessageListener = () => {
    startSmsRetriever().then(console.log).catch(console.log);
  };

  const handleOnStartUserConsentMessageListener = () => {
    startSmsUserConsent().then(console.log).catch(console.log);
  };

  return (
    <View style={styles.container}>
      <Button
        onPress={handleOnRequestPhoneNumber}
        title="Request phone number"
      />
      <Button onPress={handleOnGetSignatures} title="Get app signatures" />
      <Button
        onPress={handleOnStartMessageListener}
        title="Start message listener"
      />
      <Button
        onPress={handleOnStartUserConsentMessageListener}
        title="Start user consent message listener"
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});

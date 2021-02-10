import * as React from 'react';

import { StyleSheet, View, Button } from 'react-native';
import {
  requestPhoneNumber,
  SMSRetriever,
} from 'react-native-android-sms-verification-api';

export default function App() {
  const handleOnRequestPhoneNumber = () => {
    requestPhoneNumber()
      .then(console.log)
      .catch((e) => console.log(`${e.code} : ${e.message}`));
  };

  const handleOnGetSignatures = () => {
    SMSRetriever.getAppSignatures().then(console.log).catch(console.log);
  };

  const handleOnStartMessageListener = () => {
    SMSRetriever.startSmsRetriever()
      .then(() => SMSRetriever.receiveVerificationSMS())
      .then(console.log)
      .catch(console.log);
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

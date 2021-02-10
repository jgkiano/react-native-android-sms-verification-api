import * as React from 'react';

import { StyleSheet, View, Button } from 'react-native';
import { requestPhoneNumber } from 'react-native-android-sms-verification-api';

export default function App() {
  const handleButtonPress = () => {
    requestPhoneNumber()
      .then(console.log)
      .catch((e) => console.log(`${e.code} : ${e.message}`));
  };

  return (
    <View style={styles.container}>
      <Button onPress={handleButtonPress} title="Press me" />
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

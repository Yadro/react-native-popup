import React, {Component, PropTypes } from 'react';
import {
  requireNativeComponent,
  NativeModules,
  View,
  Button,
  StyleSheet,
  ToastAndroid
} from 'react-native';

/*var iface = {
 name: 'PhotoView',
 propTypes: {
 src: PropTypes.string,
 value: PropTypes.number,
 borderRadius: PropTypes.number,
 resizeMode: PropTypes.oneOf(['cover', 'contain', 'stretch']),
 ...View.propTypes // include the default view properties
 },
 };*/

/*module.exports = {
 PhotoView: requireNativeComponent('RCTPhotoView', iface),
 Threshold: NativeModules.Threshold,
 readThresholdSave: NativeModules.Threshold.readThresholdSave,
 }*/

export default class Example extends Component {

  showInput = () => {
    NativeModules.PopupMenu.showInput('Title', 'content', ['ok'], 'input', 'placeholder', (e) => {
      console.log(e);
      ToastAndroid.show(JSON.stringify(e), ToastAndroid.SHORT);
    });
  };

  showChoice = () => {
    NativeModules.PopupMenu.showChoice('Title', 'This is content', ['one', 'two', 'three', 'four', 'five', 'six'], (e) => {
      console.log(e);
      ToastAndroid.show(JSON.stringify(e), ToastAndroid.SHORT);
    });
  };


  showMultiChoice = () => {
    NativeModules.PopupMenu.showMultiChoice('Title', 'content', ['cancel', 'ok'], ['one', 'two'], (e) => {
      console.log(e);
      ToastAndroid.show(JSON.stringify(e), ToastAndroid.SHORT);
    });
  };

  showRadio = () => {
    NativeModules.PopupMenu.showRadio('Title', 'content', ['ok'], {text: 'checkbox', value: true}, ['one', 'two'], (e) => {
      console.log(e);
      ToastAndroid.show(JSON.stringify(e), ToastAndroid.SHORT);
    });
  };

  showDialog = () => {
    NativeModules.PopupMenu.showDialog('Title', 'content', ['cancel', 'ok'], {text: 'checkbox', value: true}, false, (e) => {
      console.log(e);
      ToastAndroid.show(JSON.stringify(e), ToastAndroid.SHORT);
    });
  };

  render() {
    return <View>
      <Button style={styles.button} title='Input' onPress={this.showInput}/>
      <Button style={styles.button} title='Choice' onPress={this.showChoice}/>
      <Button style={styles.button} title='MultiChoice' onPress={this.showMultiChoice}/>
      <Button style={styles.button} title='Radio' onPress={this.showRadio}/>
      <Button style={styles.button} title='Checkbox' onPress={this.showDialog}/>
    </View>
  }
}

const styles = StyleSheet.create({
  button: {
    margin: 10,
  }
});

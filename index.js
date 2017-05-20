'use strict'
import {NativeModules,Platform} from 'react-native'

const shareLocal = NativeModules.RNShareLocal

export function shareMessage(option){
  if(option instanceof Object && option.length == undefined){
    if(Platform.OS === 'ios'){
      return shareLocal.message(option.text,option.image,(error)=>{
        if(option.callback)option.callback(error);
      });
    }else if (Platform.OS === 'android') {
      return shareLocal.message(option.winTitle,option.subject,option.text,option.component,(error)=>{
        if(option.callback)option.callback(error);
      });
    }
  }
}

export function shareLink(option){
  if(option instanceof Object && option.length == undefined){
    if(Platform.OS === 'ios'){
      return shareLocal.link(option.title,option.link,option.icon,(error)=>{
        if(option.callback)option.callback(error);
      });
    }else if (Platform.OS === 'android') {
      return shareLocal.link(option.winTitle,option.subject,option.message,option.component,(error)=>{
        if(option.callback)option.callback(error);
      });
    }
  }
}

export function sharePictures(option){
  if(option instanceof Object && option.length == undefined){
    return shareLocal.pictures(option.imagesUrl,(error)=>{
      if(option.callback)option.callback(error);
    });
  }
}

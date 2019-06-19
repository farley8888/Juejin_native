document.addEventListener("plusready", function () {
  var _BARCODE = 'Myplugin',
    B = window.plus.bridge;
  var myPlugin = { //启动登录
    plgLogin: function (param, successCallback) {
      var success = typeof successCallback !== 'function' ? null : function (args) {
          successCallback(args);
        },
        fail = null;
      callbackID = B.callbackId(success, fail);

      return B.exec(_BARCODE, "pluginLogin", [callbackID, param]);
    },
    //第三方授权登录 way=baidu、dingding
    authorLogin: function (way, successCallback, errorCallback) {
      var success = typeof successCallback !== 'function' ? null : function (args) {
          successCallback(args);
        },
        fail = typeof errorCallback !== 'function' ? null : function (code) {
          errorCallback(code);
        };
      callbackID = B.callbackId(success, fail);

      return B.exec(_BARCODE, "authorLogin", [callbackID, way]);
    },
    /**
     *
     * @param {String} way 方法 dingding qzone
     * @param {Object} param param.
     * @param {Function} callback 分享回调
     */
    shareTo: function (way, param, callback) {
      var success = typeof callback !== 'function' ? null : function (args) {
          callback(args);
        },
        fail = null;
      callbackID = B.callbackId(success, fail);

      return B.exec(_BARCODE, "shareTo", [callbackID, way, param]);
    },//登录结果
    loginResult: function (action, result, successCallback, errorCallback) {
      result = JSON.stringify(result)
      successCallback = successCallback ? successCallback : null
      errorCallback = errorCallback ? errorCallback : null
      var success = typeof successCallback !== 'function' ? null : function (args) {
          successCallback(args);
        },
        fail = typeof errorCallback !== 'function' ? null : function (code) {
          errorCallback(code);
        };
      callbackID = B.callbackId(success, fail);

      return B.exec(_BARCODE, "loginResult", [callbackID, action, result]);
    }, //下一个页面
    plgNextPage: function (from, to, callback) {
      var success = typeof callback !== 'function' ? null : function (args) {
          callback(args);
        },
        fail = null;
      callbackID = B.callbackId(success, fail);
      return B.exec(_BARCODE, "vueGoNext", [callbackID, from, to]);
    }, //返回 param nullable
    plgBack: function (from, to, param) {
      var success = null,
        fail = null;
      callbackID = B.callbackId(success, fail);
      return B.exec(_BARCODE, "vueGoBack", [callbackID, from, to, param]);
    }, //分享回调
    shareCallback: function (way, params, from) {
      var success = null,
        fail = null;
      callbackID = B.callbackId(success, fail);
      return B.exec(_BARCODE, "vueShareCallback", [callbackID, [way, params, from]]);
    }, //跳转到原生页面 fromVue=task, toNative=home、mine
    goNativeView: function (fromVue, toNative) {
      var success = null,
        fail = null;
      callbackID = B.callbackId(success, fail);
      return B.exec(_BARCODE, "vueJumpto", [callbackID, fromVue, toNative]);
    }, //登出
    loginOut: function (userName) {
      var success = null,
        fail = null;
      callbackID = B.callbackId(success, fail);
      return B.exec(_BARCODE, "vueLoginOut", [callbackID, userName]);
    }, //清除缓存
    cleanCache: function (userName) {
      var success = null,
        fail = null;
      callbackID = B.callbackId(success, fail);
      return B.exec(_BARCODE, "cleanCache", [callbackID, userName]);
    }, //显示隐藏底部tabBar 1显示 0隐藏
    showHideTabBar: function (show) {
      var success = null,
        fail = null;
      callbackID = B.callbackId(success, fail);
      return B.exec(_BARCODE, "showHideTabBar", [callbackID, show]);
    }
  };
  window.plus.myplugin = myPlugin;
}, true);

webpackJsonp([55],{"+l2M":function(n,t,o){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var i=s(o("mvHQ")),e=s(o("Dd8w")),a=o("NYxO");function s(n){return n&&n.__esModule?n:{default:n}}t.default={computed:(0,a.mapState)({commonState:function(n){return n.common}}),data:function(){return this.origin=this.$config.currentApi+"/v1/passport/captcha?timestamp=",{mobile:"",captcha_code:"",code:"",showCaptureCode:!1,buttonText:"获取验证码",timestamp:(new Date).getTime(),isBrower:!1,protocolBol:!1,couldSendMsg:!0,loginBtn:!0,showType:!0,showPhone:!1}},methods:(0,e.default)({},(0,a.mapMutations)(["common_login_bol","common_login_box","common_index_column","common_set_userInfo","common_protocol_bol","common_get_channel","common_get_recondChannel"]),{closeFn:function(){this.$router.go(-1)},closePhone:function(){this.showPhone=!1,this.$config.source_style<6&&!this.$utils.isWx()&&this.closeFn()},setInput:function(){this.mobile&&(this.mobile=this.mobile.replace(/[^0-9]/g,""),this.mobile.length>11&&(this.mobile=this.mobile.substr(0,11))),this.code&&(this.code=this.code.replace(/[^0-9]/g,""),this.code.length>11&&(this.code=this.code.substr(0,4)))},phoneLogin:function(){this.showPhone=!0},wechatLogin:function(){this.$config.source_style<6?this.$utils.wxLogin():this.appLogin()},getCode:function(){var n=this;if(this.couldSendMsg&&"获取验证码"===this.buttonText)if(setTimeout(function(){n.couldSendMsg=!0},2e3),this.$utils.isReg("PHONE_NUMBER",this.mobile))if(this.showCaptureCode&&""===this.captcha_code)this.$toast({message:"请输入图形验证码",position:"middle",duration:1500});else{var t={};t.mobile=this.mobile,this.showCaptureCode&&(t.captcha_code=this.captcha_code),this.couldSendMsg=!1,this.$api.get("SMSCODE",t,function(t){n.$toast({message:t.msg,duration:1500}),n.numTxt=60,n.buttonText="重新发送("+n.numTxt+"s)",n.tiemr=setInterval(function(){n.numTxt--,n.buttonText="重新发送("+n.numTxt+"s)",n.numTxt<=0&&(clearInterval(n.tiemr),n.couldSendMsg=!0,n.buttonText="获取验证码")},1e3)},function(t){"请输入图形验证码"===t.msg&&(n.showCaptureCode=!0,n.couldSendMsg=!0),"请输入图形验证码"!==t.msg&&"图形验证码不正确"!==t.msg&&"手机验证码不正确"!==t.msg||(n.timestamp=(new Date).getTime(),n.couldSendMsg=!0)})}else this.$toast({message:"请输入正确手机号",position:"middle",duration:1500})},login:function(){var n=this;if(this.loginBtn)if(this.loginBtn=!1,setTimeout(function(){n.loginBtn=!0}),this.$utils.isReg("PHONE_NUMBER",this.mobile))if(this.captcha_codeBol&&""===this.captcha_code)this.$toast({message:"请输入图形验证码",position:"middle",duration:1500});else if(""===this.code)this.$toast({message:"请输入短信验证码",position:"middle",duration:1500});else{var t=this.$utils.registerInfo();t.account=this.mobile,t.code=this.code,this.$utils.cache("inviteCodeUser")&&(t.invitation=this.$utils.cache("inviteCodeUser")),this.showCaptureCode&&(t.captcha_code=this.captcha_code),this.commonState.pushInfo.clientid&&null!==this.commonState.pushInfo.clientid&&"null"!==this.commonState.pushInfo.clientid&&(t.getui_cid=this.commonState.pushInfo.clientid),this.$api.post("LOGIN",t,function(t){n.$utils.cache("uid",t.data.uid),n.$utils.cache("user_token",t.data.user_token),n.$utils.cache("nickname",t.data.nickname),n.$utils.cache("first_login",t.data.first_login),n.$utils.cache("inviteCode",t.data.invitation),n.$utils.cache("user_icon",t.data.avatar),n.$utils.cache("user_city",t.data.city),n.$utils.cache("userInfo",(0,i.default)(t.data)),n.setShowGift(),clearInterval(n.tiemr),n.numTxt=0,n.buttonText="获取验证码",n.common_index_column(!0),n.common_set_userInfo(t.data),n.common_login_bol(!0),n.getChannel(),n.$utils.cache("inviteCodeUser",null),n.$emit("closeFn"),n.mobile="",n.code="",n.captcha_code="",n.$toast({message:t.msg,position:"middle",duration:3e3}),n.loginBtn=!0,window.plus.myplugin.loginResult("登录结果",(0,i.default)(t.data),null,null)},function(t){n.loginBtn=!0,"请输入图形验证码"===t.msg&&(n.showCaptureCode=!0),"请输入图形验证码"!==t.msg&&"图形验证码不正确"!==t.msg&&"手机验证码不正确"!==t.msg||(n.timestamp=(new Date).getTime())})}else this.$toast({message:"请输入正确手机号",position:"middle",duration:1500})},setShowGift:function(){},getChannel:function(){var n=this;this.$api.get("MY_CHANNEL",{},function(t){n.common_get_channel(t.data,"my_channel")}),this.$api.get("RECOND_CHANNEL",{},function(t){n.common_get_recondChannel(t.data,"recond_channel")})},appLogin:function(){var n=this;function t(){window.plus.oauth.getServices(function(t){for(var o={},e=0;e<t.length;e++)"weixin"===t[e].id&&(o=t[e]);window.plus.runtime.isApplicationExist({pname:"com.tencent.mm",action:"weixin://"})?o.authorize(function(t){var o=n.$utils.registerInfo(),e={type:2,code:t.code,os:o.os,channel:o.channel,useragent:o.useragent};n.commonState.pushInfo&&n.commonState.pushInfo.clientid&&null!==n.commonState.pushInfo.clientid&&"null"!==n.commonState.pushInfo.clientid&&(e.getui_cid=n.commonState.pushInfo.clientid),console.log((0,i.default)(e)),n.$api.post("WECHAT_LOGIN",e,function(t){n.$utils.cache("uid",t.data.uid),n.$utils.cache("user_token",t.data.user_token),n.$utils.cache("nickname",t.data.nickname),n.$utils.cache("first_login",t.data.first_login),n.$utils.cache("user_icon",t.data.avatar),n.$utils.cache("inviteCode",t.data.invitation),n.$utils.cache("userInfo",(0,i.default)(t.data)),n.common_index_column(!0),n.common_login_bol(!0),n.common_set_userInfo(t.data),n.setShowGift(),n.getChannel(),n.$toast({message:"登录成功",position:"middle",duration:1500,icon:"icon icon-success"}),n.$emit("closeFn"),n.back(),window.plus.myplugin.loginResult("登录结果",(0,i.default)(t.data),null,null)},function(t){0!==t.code&&(n.$toast({message:"登录失败",position:"middle",duration:1500}),n.$emit("closeFn"))})},function(t){n.closeFn("closeFn")},{scope:"snsapi_userinfo",state:"authorize test"}):(n.$toast("请安装微信"),n.closeFn())})}6!==this.$config.source_style&&7!==this.$config.source_style||(window.plus?t():document.addEventListener("plusready",function(){t()}))}}),watch:{"commonState.login_box":function(n){this.showCaptureCode=!1,this.captcha_code="",this.showType=!0,this.$config.source_style<6&&!this.$utils.isWx()&&(this.showPhone=!0,this.isBrower=!0,this.showType=!1)}},beforeUpdate:function(){this.setInput()},mounted:function(){}}},"0qTl":function(n,t,o){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var i=o("+l2M"),e=o.n(i);for(var a in i)"default"!==a&&function(n){o.d(t,n,function(){return i[n]})}(a);var s=o("ZTjP"),c=!1;var d=function(n){c||o("f3d6")},l=o("VU/8")(e.a,s.a,!1,d,"data-v-55d3dac1",null);l.options.__file="src/components/personal-center/native_login/index.vue",t.default=l.exports},"5IzT":function(n,t,o){var i=o("kxFB");(n.exports=o("FZ+f")(!0)).push([n.i,'\n#loginBox[data-v-55d3dac1]{position:absolute;position:fixed;width:100vw;height:100vh;-webkit-transform:translateZ(0);transform:translateZ(0);content:"viewport-units-buggyfill; width: 100vw; height: 100vh"\n}\n#loginBox[data-v-55d3dac1],#loginBox #login[data-v-55d3dac1]{-webkit-box-sizing:border-box;box-sizing:border-box\n}\n#loginBox #login[data-v-55d3dac1]{height:100%;position:relative;background:#fff\n}\n#loginBox #login .header-nav[data-v-55d3dac1]{height:11.733333vw;-webkit-box-sizing:border-box;box-sizing:border-box;border-bottom:1px solid #f5f5f5;content:"viewport-units-buggyfill; height: 11.733333vw"\n}\n#loginBox #login .header-nav .backIcon[data-v-55d3dac1]{width:2.666667vw;height:4.666667vw;padding:2.666667vw 5.333333vw;content:"viewport-units-buggyfill; width: 2.666667vw; height: 4.666667vw; padding: 2.666667vw 5.333333vw"\n}\n#loginBox #login .logo[data-v-55d3dac1]{top:36vw;display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-pack:center;-ms-flex-pack:center;justify-content:center;content:"viewport-units-buggyfill; top: 36vw"\n}\n#loginBox #login .logo .iconSize[data-v-55d3dac1]{width:24vw;height:24vw;-webkit-box-shadow:0 .266667vw .533333vw 0 #ff7600;box-shadow:0 .266667vw .533333vw 0 #ff7600;border-radius:4vw;overflow:hidden;content:"viewport-units-buggyfill; width: 24vw; height: 24vw; -webkit-box-shadow: 0 0.266667vw 0.533333vw 0 #FF7600; box-shadow: 0 0.266667vw 0.533333vw 0 #FF7600; border-radius: 4vw"\n}\n#loginBox #login .logoText[data-v-55d3dac1]{top:65.333333vw;white-space:nowrap;color:#666;font-size:3.733333vw;content:"viewport-units-buggyfill; top: 65.333333vw; font-size: 3.733333vw"\n}\n#loginBox #login .loginButton[data-v-55d3dac1]{width:71.066667vw;height:14vw;font-size:4.8vw;color:#fff;overflow:hidden;-webkit-box-sizing:border-box;box-sizing:border-box;content:"viewport-units-buggyfill; width: 71.066667vw; height: 14vw; font-size: 4.8vw"\n}\n#loginBox #login .loginButton .leftIcon[data-v-55d3dac1]{height:14vw;width:14vw;content:"viewport-units-buggyfill; height: 14vw; width: 14vw"\n}\n#loginBox #login .loginButton .leftIcon .iconSize[data-v-55d3dac1]{width:8vw;height:6.4vw;content:"viewport-units-buggyfill; width: 8vw; height: 6.4vw"\n}\n#loginBox #login .wechat[data-v-55d3dac1]{background:#24b637;border-radius:4vw;top:82.933333vw;content:"viewport-units-buggyfill; border-radius: 4vw; top: 82.933333vw"\n}\n#loginBox #login .phone[data-v-55d3dac1]{background:#fff;-webkit-box-shadow:-.266667vw .266667vw .533333vw 0 #e0e0e0,.266667vw .266667vw .533333vw 0 #e0e0e0;box-shadow:-.266667vw .266667vw .533333vw 0 #e0e0e0,.266667vw .266667vw .533333vw 0 #e0e0e0;border-radius:4vw;top:105.733333vw;color:#999;content:"viewport-units-buggyfill; -webkit-box-shadow: -0.266667vw 0.266667vw 0.533333vw 0 #E0E0E0, 0.266667vw 0.266667vw 0.533333vw 0 #E0E0E0; box-shadow: -0.266667vw 0.266667vw 0.533333vw 0 #E0E0E0, 0.266667vw 0.266667vw 0.533333vw 0 #E0E0E0; border-radius: 4vw; top: 105.733333vw"\n}\n#loginBox #login .phone .leftIcon .iconSize[data-v-55d3dac1]{width:4.266667vw;height:6.4vw;content:"viewport-units-buggyfill; width: 4.266667vw; height: 6.4vw"\n}\n#loginBox #login .phone.isBrower[data-v-55d3dac1]{top:82.933333vw;content:"viewport-units-buggyfill; top: 82.933333vw"\n}\n#loginBox .maskBox[data-v-55d3dac1]{background:none\n}\n#loginBox .maskBox.active[data-v-55d3dac1]{background:rgba(0,0,0,.5)\n}\n#loginBox .phoneLogin[data-v-55d3dac1]{width:79.466667vw;text-align:center;background:#fff;font-size:4.8vw;content:"viewport-units-buggyfill; width: 79.466667vw; font-size: 4.8vw"\n}\n#loginBox .phoneLogin .close[data-v-55d3dac1]{position:absolute;right:1.333333vw;top:1.333333vw;width:8vw;height:8vw;background:url('+i(o("Nb7f"))+') no-repeat 50%;background-size:3.466667vw 3.466667vw;min-width:0;content:"viewport-units-buggyfill; right: 1.333333vw; top: 1.333333vw; width: 8vw; height: 8vw; background-size: 3.466667vw 3.466667vw"\n}\n#loginBox .phoneLogin .close img[data-v-55d3dac1]{height:100%\n}\n#loginBox .phoneLogin .title[data-v-55d3dac1]{padding:5.333333vw 0;color:#333;content:"viewport-units-buggyfill; padding: 5.333333vw 0"\n}\n#loginBox .phoneLogin .inputBox[data-v-55d3dac1]{border:1px solid #979797;background:#fff;border-radius:5.6vw;height:11.066667vw;width:57.066667vw;overflow:hidden;margin:0 auto 2.666667vw;font-size:3.733333vw;color:#666;content:"viewport-units-buggyfill; border-radius: 5.6vw; height: 11.066667vw; width: 57.066667vw; margin-bottom: 2.666667vw; font-size: 3.733333vw"\n}\n#loginBox .phoneLogin .inputBox input[data-v-55d3dac1]{font-size:3.733333vw;color:#666;border:none;outline:none;-webkit-box-flex:1;-ms-flex:1;flex:1;padding:0 2.666667vw;min-width:0;display:block;height:8vw;content:"viewport-units-buggyfill; font-size: 3.733333vw; padding: 0 2.666667vw; height: 8vw"\n}\n#loginBox .phoneLogin .inputBox .codeButton[data-v-55d3dac1]{width:26.666667vw;height:5.866667vw;border-left:1px solid #979797;content:"viewport-units-buggyfill; width: 26.666667vw; height: 5.866667vw"\n}\n#loginBox .phoneLogin .inputBox .codeButton .imageCode[data-v-55d3dac1]{width:26.666667vw;height:8.266667vw;content:"viewport-units-buggyfill; width: 26.666667vw; height: 8.266667vw"\n}\n#loginBox .phoneLogin .inputBox .codeButton.active[data-v-55d3dac1]{color:#ff3000\n}\n#loginBox .phoneLogin .loginButton[data-v-55d3dac1]{background-image:linear-gradient(-119deg,orange,#ff6c00);border-radius:3.466667vw;width:57.066667vw;height:10.266667vw;margin:0 auto;color:#fff;font-size:4.266667vw;content:"viewport-units-buggyfill; border-radius: 3.466667vw; width: 57.066667vw; height: 10.266667vw; font-size: 4.266667vw"\n}\n#loginBox .phoneLogin .tip[data-v-55d3dac1]{font-size:3.2vw;white-space:nowrap;height:13.333333vw;content:"viewport-units-buggyfill; font-size: 3.2vw; height: 13.333333vw"\n}\n#loginBox .phoneLogin .tip>div[data-v-55d3dac1]{padding:2.666667vw 0;content:"viewport-units-buggyfill; padding: 2.666667vw 0"\n}\n#loginBox .phoneLogin .tip .agreeText[data-v-55d3dac1]{color:orange\n}',"",{version:3,sources:["/Users/apple/Documents/Gits/mobile2_juejinlian_native/src/components/personal-center/native_login/index.vue","/Users/apple/Documents/Gits/mobile2_juejinlian_native/src/components/personal-center/native_login/<no source>"],names:[],mappings:";AAoZA,2BAEE,kBAAkB,eACH,YACH,aACC,gCACb,wBAA+B,+DC1ZjC;CD0jBC;AAtKD,6DACE,8BAAA,qBAAsB;CA8ErB;AA/EH,kCAQI,YAAY,kBAEM,eACF;CAoEjB;AA/EH,8CAaM,mBAAY,8BACZ,sBAAsB,gCACU,uDCnatC;CDyaK;AArBL,wDAiBQ,iBAAW,kBACC,8BACM,yGCva1B;CDwaO;AApBP,wCAuBM,SAAU,oBACV,oBAAA,aAAa,wBACb,qBAAA,uBAAuB,6CC7a7B;CDqbK;AAjCL,kDA2BQ,WAAY,YACC,mDACb,2CAA+B,kBACZ,gBACH,uLCnbxB;CDobO;AAhCP,4CAmCM,gBAAU,mBACS,WACR,qBACI,2EC1brB;CD2bK;AAvCL,+CAyCM,kBAAY,YACC,gBACE,WACD,gBACE,8BAChB,sBAAsB,sFClc5B;CD2cK;AAvDL,yDAgDQ,YAAa,WACD,6DCrcpB;CD0cO;AAtDP,mEAmDU,UAAW,aACC,6DCxctB;CDycS;AArDT,0CA0DM,mBAAmB,kBAEA,gBACT,wECjdhB;CDkdK;AA9DL,yCAgEM,gBAAmB,oGACnB,4FAAyD,kBACtC,iBACT,WACC,2RCxdjB;CDkeK;AA9EL,6DAuEU,iBAAW,aACC,oEC5dtB;CD6dS;AAzET,kDA4EQ,gBAAU,oDChelB;CDieO;AA7EP,oCAiFI,eAAgB;CACjB;AAlFH,2CAoFI,yBAAgC;CACjC;AArFH,uCAwFI,kBAAY,kBACM,gBACF,gBACD,wEC/enB;CDyjBG;AArKH,8CA6FM,kBAAkB,iBACP,eACF,UACE,WACC,uDAC+D,sCACjD,YACd,uICxflB;CD4fK;AAxGL,kDAsGQ,WAAY;CACb;AAvGP,8CA0GM,qBAAe,WACJ,yDC/fjB;CDggBK;AA5GL,iDA8GM,yBAAyB,gBACT,oBACG,mBACP,kBACA,gBAEI,yBACG,qBACJ,WACJ,mJC3gBjB;CDoiBK;AAhJL,uDAyHQ,qBAAe,WACJ,YACC,aACC,mBACb,WAAA,OAAO,qBACQ,YACH,cACE,WACF,6FCrhBpB;CDshBO;AAlIP,6DAoIQ,kBAAY,kBACA,8BACkB,0EC1hBtC;CDkiBO;AA9IP,wEAwIU,kBAAY,kBACA,0EC7hBtB;CD8hBS;AA1IT,oEA4IU,aAAc;CACf;AA7IT,oDAkJM,yDAAoE,yBACjD,kBACP,mBACA,cACE,WACH,qBACI,6HC5iBrB;CD6iBK;AAzJL,4CA2JM,gBAAe,mBACI,mBACN,yECjjBnB;CDwjBK;AApKL,gDA+JQ,qBAAe,yDCnjBvB;CDojBO;AAhKP,uDAkKQ,YACF;CAAC",file:"index.vue",sourcesContent:["\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n#loginBox {\n  box-sizing: border-box;\n  position: absolute;\n  position: fixed;\n  width: 100vw;\n  height: 100vh;\n  transform: translate3d(0, 0, 0);\n  #login {\n    height: 100%;\n    box-sizing: border-box;\n    position: relative;\n    background: #fff;\n    .header-nav {\n      height: 88px;\n      box-sizing: border-box;\n      border-bottom: 1px solid #f5f5f5;\n      .backIcon {\n        width: 20px;\n        height: 35px;\n        padding: 20px 40px;\n      }\n    }\n    .logo{\n      top: 270px;\n      display: flex;\n      justify-content: center;\n      .iconSize {\n        width: 180px;\n        height: 180px;\n        box-shadow: 0 2px 4px 0 #FF7600;\n        border-radius: 30px;\n        overflow: hidden;\n      }\n    }\n    .logoText {\n      top: 490px;\n      white-space: nowrap;\n      color: #666;\n      font-size: 28px;\n    }\n    .loginButton {\n      width: 533px;\n      height: 105px;\n      font-size: 36px;\n      color: #FFFFFF;\n      overflow: hidden;\n      box-sizing: border-box;\n      .leftIcon {\n        height: 105px;\n        width: 105px;\n        .iconSize {\n          width: 60px;\n          height: 48px;\n        }\n      }\n    }\n    .wechat{\n      // background-image: linear-gradient(55deg, #FF8B29 0%, #FFA900 100%);\n      background: #24b637;\n      // box-shadow: 0 2px 4px 0 #FF7000;\n      border-radius: 30px;\n      top: 622px;\n    }\n    .phone {\n      background: #FFFFFF;\n      box-shadow: -2px 2px 4px 0 #E0E0E0, 2px 2px 4px 0 #E0E0E0;\n      border-radius: 30px;\n      top: 793px;\n      color: #999;\n      .leftIcon {\n        .iconSize {\n          width: 32px;\n          height: 48px;\n        }\n      }\n      &.isBrower {\n        top: 622px;\n      }\n    }\n  }\n  .maskBox {\n    background: none;\n  }\n  .maskBox.active {\n    background: rgba($color: #000000, $alpha: .5)\n  }\n  // 手机登录\n  .phoneLogin {\n    width: 596px;\n    text-align: center;\n    background: #fff;\n    font-size: 36px;\n    .close {\n      position: absolute;\n      right: 10px;\n      top: 10px;\n      width: 60px;\n      height: 60px;\n      background: url('../../../assets/img/task/close_gray.png') no-repeat center;\n      background-size: 26px 26px;\n      min-width: 0;\n      img {\n        height: 100%;\n      }\n    }\n    .title {\n      padding: 40px 0;\n      color: #333;\n    }\n    .inputBox {\n      border: 1px solid #979797;\n      background: #fff;\n      border-radius: 42px;\n      height: 83px;\n      width: 428px;\n      margin: 0 auto;\n      overflow: hidden;\n      margin-bottom: 20px;\n      font-size: 28px;\n      color: #666;\n      input {\n        font-size: 28px;\n        color: #666;\n        border: none;\n        outline: none;\n        flex: 1;\n        padding: 0 20px;\n        min-width: 0;\n        display: block;\n        height: 60px;\n      }\n      .codeButton {\n        width: 200px;\n        height: 44px;\n        border-left: 1px solid #979797;\n        .imageCode {\n          width: 200px;\n          height: 62px;\n        }\n        &.active{\n          color: #ff3000;\n        }\n      }\n      \n    }\n    .loginButton {\n      background-image: linear-gradient(-119deg, #FFA500 0%, #FF6C00 100%);\n      border-radius: 26px;\n      width: 428px;\n      height: 77px;\n      margin: 0 auto;\n      color: #fff;\n      font-size: 32px;\n    }\n    .tip {\n      font-size: 24px;\n      white-space: nowrap;\n      height: 100px;\n      >div{\n        padding: 20px 0;\n      }\n      .agreeText {\n        color:#FFA500\n      }\n    }\n  }\n}\n",null],sourceRoot:""}])},ZTjP:function(n,t,o){"use strict";var i=function(){var n=this,t=n.$createElement,i=n._self._c||t;return i("div",{attrs:{id:"loginBox"}},[n.showType?i("div",{attrs:{id:"login"}},[i("div",{staticClass:"header-nav flex-row jusB"},[i("div",{staticClass:"backIcon iconSize flex-row",on:{click:n.closeFn}},[i("img",{attrs:{src:o("1Eg8"),alt:"返回"}})])]),n._v(" "),n._m(0),n._v(" "),i("div",{staticClass:"logoText posC"},[n._v("掘金宝，精选热门资讯")]),n._v(" "),i("div",{staticClass:"loginButton posC phone flex-row",class:{isBrower:n.isBrower},on:{click:n.phoneLogin}},[n._m(1),n._v(" "),i("div",{staticClass:"text"},[n._v("手机号登录  ")])]),n._v(" "),i("div",{staticClass:"loginButton posC flex-row wechat",on:{click:n.wechatLogin}},[n._m(2),n._v(" "),i("div",{staticClass:"text"},[n._v("微信一键登录")])])]):n._e(),n._v(" "),n.showPhone?i("div",{staticClass:"maskBox active"},[i("div",{staticClass:"phoneLogin"},[i("div",{staticClass:"close iconSize",on:{click:n.closePhone}}),n._v(" "),i("div",{staticClass:"title"},[n._v("登录/注册")]),n._v(" "),i("div",{staticClass:"inputBox flex-row"},[i("input",{directives:[{name:"model",rawName:"v-model",value:n.mobile,expression:"mobile"}],attrs:{type:"number",pattern:"\\d*",placeholder:"请输入11位手机号码"},domProps:{value:n.mobile},on:{input:function(t){t.target.composing||(n.mobile=t.target.value)}}})]),n._v(" "),n.showCaptureCode?i("div",{staticClass:"inputBox flex-row"},[i("input",{directives:[{name:"model",rawName:"v-model",value:n.captcha_code,expression:"captcha_code"}],staticClass:"flex1",attrs:{type:"text",maxlength:"4",placeholder:"请输入图形验证码"},domProps:{value:n.captcha_code},on:{input:function(t){t.target.composing||(n.captcha_code=t.target.value)}}}),n._v(" "),i("div",{staticClass:"codeButton borderNone flex-row"},[i("div",{staticClass:"imageCode iconSize"},[i("img",{attrs:{src:n.origin+n.timestamp,alt:"图形验证码"},on:{click:function(t){n.timestamp=(new Date).getTime()}}})])])]):n._e(),n._v(" "),i("div",{staticClass:"inputBox flex-row"},[i("input",{directives:[{name:"model",rawName:"v-model",value:n.code,expression:"code"}],staticClass:"flex1",attrs:{maxlength:"4",placeholder:"请输入验证码",pattern:"\\d*",type:"number"},domProps:{value:n.code},on:{input:function(t){t.target.composing||(n.code=t.target.value)}}}),n._v(" "),i("div",{staticClass:"codeButton flex-row",class:{active:11===n.mobile.length&&"获取验证码"===n.buttonText},on:{click:n.getCode}},[n._v(n._s(n.buttonText))])]),n._v(" "),i("div",{staticClass:"loginButton flex-row",on:{click:n.login}},[n._v("登录/注册")]),n._v(" "),i("div",{staticClass:"tip flex-row"},[i("div",{staticClass:"text"},[n._v("注册即代表同意")]),n._v(" "),i("div",{staticClass:"agreeText",on:{click:function(t){t.stopPropagation(),n.$store.commit("common_protocol_bol",!0)}}},[n._v("《掘金宝用户协议》")])])])]):n._e()])},e=[function(){var n=this.$createElement,t=this._self._c||n;return t("div",{staticClass:"logo posC"},[t("div",{staticClass:"iconSize"},[t("img",{attrs:{src:o("dLd/"),alt:"logo"}})])])},function(){var n=this.$createElement,t=this._self._c||n;return t("div",{staticClass:"leftIcon flex-row"},[t("div",{staticClass:"iconSize"},[t("img",{attrs:{src:o("bwGQ"),alt:"手机登录图标"}})])])},function(){var n=this.$createElement,t=this._self._c||n;return t("div",{staticClass:"leftIcon flex-row"},[t("div",{staticClass:"iconSize"},[t("img",{attrs:{src:o("QK+l"),alt:"微信登录图标"}})])])}];i._withStripped=!0;var a={render:i,staticRenderFns:e};t.a=a},f3d6:function(n,t,o){var i=o("5IzT");"string"==typeof i&&(i=[[n.i,i,""]]),i.locals&&(n.exports=i.locals);o("rjj0")("a7fde514",i,!1,{})}});
//# sourceMappingURL=55.7a6f7401f2d52b1feb1e.js.map
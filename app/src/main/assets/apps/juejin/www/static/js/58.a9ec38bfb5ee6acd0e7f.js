webpackJsonp([58],{Tf0X:function(n,e,t){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var o=c(t("Dd8w")),i=c(t("MSFs")),s=c(t("LvM6")),r=c(t("t3UM")),a=c(t("ko1y")),h=t("NYxO");function c(n){return n&&n.__esModule?n:{default:n}}e.default={components:{HeaderNav:i.default,carShare:s.default,shareBox:r.default,ShareCommon:a.default},computed:{commonState:function(){return this.$store.state.common}},data:function(){return{msg:"",download:!1,showCarShare:!1,showBox:!1,current:null,appShare:{count:0},showConfirm:!1,article:{},fatherParams:{}}},methods:(0,o.default)({},(0,h.mapMutations)(["common_login_box"]),{toPage:function(n){this.$router.push(n)},shareActions:function(n){var e=this,t={title:this.fatherParams.title,content:this.fatherParams.desc,thumbs:[this.fatherParams.img_url],href:this.fatherParams.shareLink},o="qq";"cancel"!==n?("friend"===n?(o="weixin",t.type="web",t.extra={scene:"WXSceneTimeline"}):"wechat"===n?(o="weixin",t.type="web",t.extra={scene:"WXSceneSession"}):"qq"===n&&(o="qq"),console.log(t),this.commonState.shares[o].send(t,function(){e.showBox=!1,"qq"===o&&e.$toast({message:"分享成功",duration:1500})},function(n){e.showBox=!1,-2===n.code?e.$toast({message:"取消分享",duration:1500}):e.$toast({message:"分享失败",duration:1500})})):this.showBox=!1},closeShare:function(){this.showConfirm=!1},setShare:function(){this.$config.source_style>=6?this.headerShare():this.$utils.isWx()?this.showConfirm=!0:this.$utils.downLoadExtra()},headerShare:function(){this.showBox=!0},shareSuccess:function(){this.showBox=!1},getContent:function(){var n=this,e={aid:this.$route.params.id};this.$api.get("DETAIL",e,function(e){n.article={title:e.data.title,desc:e.data.describe,img_url:e.data.img_url.length>0?e.data.img_url[0]:"https://jjlmobile.oss-cn-shenzhen.aliyuncs.com/vsn1_1/share_logo.png",url:e.data.url},n.appShare.path=n.$route.path,n.showCarShare=!0,n.fatherParams={title:n.article.title,desc:n.article.desc,img_url:n.article.img_url?n.article.img_url:"https://jjlmobile.oss-cn-shenzhen.aliyuncs.com/vsn1_1/share_logo.png"},document.title=n.$utils.splitStr(n.fatherParams.title,14),n.msg=document.title;var t={path:n.$route.path,inviteCode:n.commonState.userInfo.invitation};localStorage.getItem("domain")&&(n.$config.currentWebUrl=localStorage.getItem("domain")),n.$config.source_style>=6?n.shareLink=n.$config.currentWebUrl+"?"+n.$qs.stringify(t):n.shareLink=location.origin+"?"+n.$qs.stringify(t),n.fatherParams.shareLink=n.shareLink,n.showCarShare=!0})}}),created:function(){this.getContent(),this.$config.source_style>=6?this.download=!1:this.$utils.isWx()?this.$route.query.inviteCode?this.download=!0:this.download=!1:this.download=!0},mounted:function(){}}},V5wv:function(n,e,t){var o=t("xDi2");"string"==typeof o&&(o=[[n.i,o,""]]),o.locals&&(n.exports=o.locals);t("rjj0")("560d400c",o,!1,{})},ghKo:function(n,e,t){"use strict";var o=function(){var n=this,e=n.$createElement,t=n._self._c||e;return t("div",{staticClass:"scrollBox"},[t("header-nav",{attrs:{msg:n.msg},on:{headerShare:n.headerShare}}),n._v(" "),n.article.url?t("div",{staticClass:"hasHeader",attrs:{id:"browser"}},[t("div",{staticClass:"iframe"},[t("iframe",{ref:"iframe",attrs:{frameborder:"0",scrolling:"no",src:n.article.url}})])]):n._e(),n._v(" "),t("div",{staticClass:"share-group f24",class:{isIos:n.$utils.isIos()}},[t("div",{staticClass:"sharebutton",on:{click:function(e){n.toPage("/AwardFeedback")}}},[n._v("用户反馈")]),n._v(" "),t("div",{staticClass:"sharebutton active",on:{click:function(e){n.setShare()}}},[n._v(n._s(n.download?"下载掘金宝APP":"分享好友（可锁粉）"))])]),n._v(" "),n.showCarShare?t("carShare",{attrs:{appShare:n.appShare,fatherParams:n.fatherParams},on:{shareSuccess:n.shareSuccess}}):n._e(),n._v(" "),n.showBox?t("share-box",{on:{shareAction:n.shareActions,shareSuccess:n.shareSuccess}}):n._e(),n._v(" "),n.showConfirm?t("ShareCommon",{on:{closeShare:n.closeShare,getArc:n.getArc}}):n._e()],1)};o._withStripped=!0;var i={render:o,staticRenderFns:[]};e.a=i},iMdH:function(n,e,t){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var o=t("Tf0X"),i=t.n(o);for(var s in o)"default"!==s&&function(n){t.d(e,n,function(){return o[n]})}(s);var r=t("ghKo"),a=!1;var h=function(n){a||t("V5wv")},c=t("VU/8")(i.a,r.a,!1,h,"data-v-2095b1d8",null);c.options.__file="src/components/common/browser.vue",e.default=c.exports},xDi2:function(n,e,t){(n.exports=t("FZ+f")(!0)).push([n.i,'\n.share-group[data-v-2095b1d8]{position:fixed;bottom:0;width:100%;display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-pack:justify;-ms-flex-pack:justify;justify-content:space-between;padding:0 4vw;-webkit-box-sizing:border-box;box-sizing:border-box;-webkit-box-align:center;-ms-flex-align:center;align-items:center;height:13.333333vw;border-top:1px solid #eee;background:#fff;font-size:3.733333vw;content:"viewport-units-buggyfill; padding: 0 4vw; height: 13.333333vw; font-size: 3.733333vw"\n}\n.share-group.isIos .sharebutton[data-v-2095b1d8]{padding-top:2.666667vw;content:"viewport-units-buggyfill; padding-top: 2.666667vw"\n}\n.share-group .sharebutton[data-v-2095b1d8]{text-align:center;padding:2.933333vw 0 1.866667vw;line-height:4.533333vw;color:#ff7f18;border:1px solid #ff7f18;width:32vw;line-height:5.333333vw;height:10.666667vw;-webkit-box-sizing:border-box;box-sizing:border-box;border-radius:9.333333vw;content:"viewport-units-buggyfill; padding: 2.933333vw 0 1.866667vw; line-height: 4.533333vw; width: 32vw; line-height: 5.333333vw; height: 10.666667vw; border-radius: 9.333333vw"\n}\n.share-group .sharebutton.active[data-v-2095b1d8]{background:#f38d32;color:#fff;width:48vw;content:"viewport-units-buggyfill; width: 48vw"\n}\n#browser[data-v-2095b1d8]{height:100vh;-webkit-box-sizing:border-box;box-sizing:border-box;overflow:hidden;content:"viewport-units-buggyfill; height: 100vh"\n}\n#browser .hasHeader .iframe[data-v-2095b1d8]{height:calc(100vh - 11.73333vw);content:"viewport-units-buggyfill; height: calc(100vh - 11.733333vw)"\n}\n#browser .iframe[data-v-2095b1d8]{width:100%;height:100%;overflow:auto!important;-webkit-overflow-scrolling:touch;-webkit-box-sizing:border-box;box-sizing:border-box;padding-bottom:13.333333vw;content:"viewport-units-buggyfill; padding-bottom: 13.333333vw"\n}\n#browser iframe[data-v-2095b1d8]{display:block;width:100%\n}',"",{version:3,sources:["/Users/apple/Documents/Gits/mobile2_juejinlian_native/src/components/common/browser.vue","/Users/apple/Documents/Gits/mobile2_juejinlian_native/src/components/common/<no source>"],names:[],mappings:";AAwLA,8BACE,eAAe,SACN,WACE,oBACX,oBAAA,aAAa,yBACb,sBAAA,8BAA8B,cACf,8BACf,sBAAsB,yBACtB,sBAAA,mBAAmB,mBACN,0BACa,gBACV,qBACD,8FCpMjB;CD2NC;AAnCD,iDAeM,uBAAiB,2DCvMvB;CDwMK;AAhBL,2CAmBI,kBAAkB,gCACE,uBACH,cACH,yBACW,WACb,uBACK,mBACL,8BACZ,sBAAsB,yBACH,mLCpNvB;CD0NG;AAlCH,kDA8BM,mBAAmB,WACR,WACC,+CCxNlB;CDyNK;AAGL,0BACE,aAAa,8BACb,sBAAsB,gBACN,iDC/NlB;CDmPC;AAvBD,6CAMM,gCAA0B,qEClOhC;CDmOK;AAPL,kCAUI,WAAW,YACC,wBACY,iCACS,8BACjC,sBAAsB,2BACD,+DC3OzB;CD4OG;AAhBH,iCAkBI,cAAc,UACH;CAGZ",file:"browser.vue",sourcesContent:["\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n.share-group {\n  position: fixed;\n  bottom: 0;\n  width: 100%;\n  display: flex;\n  justify-content: space-between;\n  padding: 0 30px;\n  box-sizing: border-box;\n  align-items: center;\n  height: 100px;\n  border-top: 1px solid #eee;\n  background: #fff;\n  font-size: 28px;\n  &.isIos {\n    .sharebutton {\n      padding-top: 20px;\n    }\n  }\n  .sharebutton {\n    text-align: center;\n    padding: 22px 0 14px;\n    line-height: 34px;\n    color: #FF7F18;\n    border: 1px solid #FF7F18;\n    width: 240px;\n    line-height: 40px;\n    height: 80px;\n    box-sizing: border-box;\n    border-radius: 70px;\n    &.active {\n      background: #F38D32;\n      color: #fff;\n      width: 360px;\n    }\n  }\n}\n#browser {\n  height: 100vh;\n  box-sizing: border-box;\n  overflow: hidden;\n  .hasHeader {\n    .iframe {\n      height: calc(100vh - 88px);\n    }\n  }\n  .iframe {\n    width: 100%;\n    height: 100%;\n    overflow: auto!important;\n    -webkit-overflow-scrolling: touch;\n    box-sizing: border-box;\n    padding-bottom: 100px;\n  }\n  iframe {\n    display: block;\n    width: 100%;\n    // height: 200000px;\n    // user-select: none;\n  }\n}\n",null],sourceRoot:""}])}});
//# sourceMappingURL=58.a9ec38bfb5ee6acd0e7f.js.map
webpackJsonp([27],{"2UOE":function(t,n,i){"use strict";Object.defineProperty(n,"__esModule",{value:!0});var e=i("3oEC"),a=i.n(e);for(var o in e)"default"!==o&&function(t){i.d(n,t,function(){return e[t]})}(o);var s=i("qdlT"),l=!1;var r=function(t){l||i("NeYh")},c=i("C7Lr")(a.a,s.a,!1,r,"data-v-e2099038",null);c.options.__file="src/components/personal-center/activity/detail.vue",n.default=c.exports},"3oEC":function(t,n,i){"use strict";Object.defineProperty(n,"__esModule",{value:!0});var e=r(i("IHPB")),a=r(i("MSFs")),o=r(i("95HW")),s=r(i("ZK+p")),l=i("R4Sj");function r(t){return t&&t.__esModule?t:{default:t}}n.default={computed:(0,l.mapState)({commonState:function(t){return t.common}}),components:{HeaderNav:a.default,PromptBox:o.default,NotPass:s.default},data:function(){return{imgList:[],details:{},content:"",msg:"",showFlag:!1,showPass:!1,previewImgList:[],fileList:[]}},methods:{deleteImage:function(t){this.fileList.splice(t,1),this.previewImgList.splice(t,1)},reEdit:function(){this.$set(this.details,"audit_status",0),this.setTitle(),this.showPass=!1},uploadFile:function(t){this.$utils.isImg(t)?t.target.files&&(this.fileList=[].concat((0,e.default)(this.fileList),(0,e.default)(t.target.files)).splice(0,3),this.previewImgList=[].concat((0,e.default)(this.previewImgList),(0,e.default)(this.$utils.getPreviewList(t.target.files))).splice(0,3),this.$previewRefresh()):this.$toast({message:"请上传图片",position:"middle",duration:1500})},confirmFn:function(){return this.previewImgList.length<3?this.$toast({message:"请上传三张应用市场好评截图",duration:1500}):this.content&&""!==this.content.trim()?void(this.content.trim().length<5?this.$toast({message:"截图备注不得小于五个字符"}):this.showFlag=!0):this.$toast({message:"请输入截图备注说明",duration:1500})},submit:function(){var t=this,n={activity_id:this.$route.params.id,content:this.content.trim()};this.$indicator.open({text:"提交中，请稍后"}),setTimeout(function(){t.$indicator.close()},3e3),this.$utils.uploaderMuti(this.$utils.cache("user_token"),"activity",this.fileList).then(function(i){t.$indicator.close(),n.picture_urls=i.join(","),t.$api.post("ACTIVITY_APPLY",n,function(i){t.details={audit_status:1,content:n.content,picture_urls:t.previewImgList},t.$previewRefresh(),t.showFlag=!1,t.$toast({message:"上传成功",duration:1500})})}).catch(function(){t.$toast("上传失败"),t.$indicator.close()})},setTitle:function(){0===this.details.audit_status?this.msg="上传截图":1===this.details.audit_status?this.msg="审核状态":3===this.details.audit_status?(this.msg="审核结果",this.showPass=!0):2===this.details.audit_status&&(this.msg="审核结果"),this.msg&&(document.title=this.msg)},getDetail:function(t){var n=this,i={activity_id:this.$route.params.id};this.$api.get("ACTIVITY_DETAIL",i,function(i){n.details=i.data,n.$previewRefresh(),"function"==typeof t&&t(i.data)})}},mounted:function(){var t=this;this.commonState.login_bol||this.$router.replace("/"),this.getDetail(function(n){t.setTitle()})}}},Ctw3:function(t,n){t.exports="data:image/png;base64,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"},EEp6:function(t,n,i){var e=i("Hb0h");"string"==typeof e&&(e=[[t.i,e,""]]),e.locals&&(t.exports=e.locals);i("FIqI")("33de3e7a",e,!1,{})},Hb0h:function(t,n,i){var e=i("L4zZ");(t.exports=i("UTlt")(!0)).push([t.i,'\n.passContent[data-v-7884f2c4]{width:75.866667vw;height:95.866667vw;content:"viewport-units-buggyfill; width: 75.866667vw; height: 95.866667vw"\n}\n.passContent .title[data-v-7884f2c4]{padding:6.666667vw 0 2.933333vw;font-size:4.266667vw;color:#ff4112;text-align:center;content:"viewport-units-buggyfill; padding: 6.666667vw 0 2.933333vw; font-size: 4.266667vw"\n}\n.passContent .p-content[data-v-7884f2c4]{font-size:3.733333vw;color:#666;line-height:5.066667vw;text-align:center;content:"viewport-units-buggyfill; font-size: 3.733333vw; line-height: 5.066667vw"\n}\n.passContent .c-content[data-v-7884f2c4]{background:#f9f9f9;border:1px solid #f4a82b;border-radius:1.333333vw;width:58.933333vw;margin:5.6vw auto 9.333333vw;line-height:1.5;text-align:justify;-webkit-box-sizing:border-box;box-sizing:border-box;height:38.666667vw;overflow-y:auto;-webkit-overflow-scrolling:touch;padding:1.333333vw;content:"viewport-units-buggyfill; border-radius: 1.333333vw; width: 58.933333vw; margin: 5.6vw auto 9.333333vw; height: 38.666667vw; padding: 1.333333vw"\n}\n.passContent .c-button[data-v-7884f2c4]{background:#f4a82b;border-radius:5.333333vw;width:50.666667vw;line-height:10.666667vw;height:10.666667vw;text-align:center;font-size:4.4vw;margin:0 auto;color:#fff;content:"viewport-units-buggyfill; border-radius: 5.333333vw; width: 50.666667vw; line-height: 10.666667vw; height: 10.666667vw; font-size: 4.4vw"\n}\n.passContent .close[data-v-7884f2c4]{width:8vw;height:8vw;position:absolute;right:2.666667vw;top:2.666667vw;background:url('+e(i("RO5J"))+') no-repeat 50%;background-size:4.266667vw 4.266667vw;content:"viewport-units-buggyfill; width: 8vw; height: 8vw; right: 2.666667vw; top: 2.666667vw; background-size: 4.266667vw 4.266667vw"\n}',"",{version:3,sources:["/Users/apple/Documents/Gits/mobile2_juejinlian/src/components/personal-center/activity/not-pass.vue","/Users/apple/Documents/Gits/mobile2_juejinlian/src/components/personal-center/activity/<no source>"],names:[],mappings:";AAiCA,8BACE,kBAAY,mBACC,2ECnCf;CDkFC;AAjDD,qCAII,gCAAoB,qBACL,cACD,kBACI,2FCxCtB;CDyCG;AARH,yCAUI,qBAAe,WACD,uBACG,kBACC,kFC9CtB;CD+CG;AAdH,yCAgBI,mBAAmB,yBACM,yBACN,kBACP,6BACU,gBACN,mBACG,8BACnB,sBAAsB,mBACT,gBACG,iCACiB,mBACpB,0JC5DjB;CD6DG;AA5BH,wCA8BI,mBAAmB,yBACA,kBACP,wBACK,mBACL,kBACM,gBACH,cACD,WACA,kJCvElB;CDwEG;AAvCH,qCAyCI,UAAW,WACC,kBACM,iBACP,eACF,uDAC8D,sCAC7C,uIChF9B;CDiFG",file:"not-pass.vue",sourcesContent:["\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n.passContent {\n  width: 569px;\n  height: 719px;\n  .title {\n    padding: 50px 0 22px;\n    font-size: 32px;\n    color: #FF4112;\n    text-align: center;\n  }\n  .p-content {\n    font-size: 28px;\n    color: #666666;\n    line-height: 38px;\n    text-align: center;\n  }\n  .c-content {\n    background: #F9F9F9;\n    border: 1px solid #F4A82B;\n    border-radius: 10px;\n    width: 442px;\n    margin: 42px auto 70px;\n    line-height: 1.5;\n    text-align: justify;\n    box-sizing: border-box;\n    height: 290px;\n    overflow-y: auto;\n    -webkit-overflow-scrolling: touch;\n    padding: 10px;\n  }\n  .c-button {\n    background: #F4A82B;\n    border-radius: 40px;\n    width: 380px;\n    line-height: 80px;\n    height: 80px;\n    text-align: center;\n    font-size: 33px;\n    margin: 0 auto;\n    color: #FFFFFF;\n  }\n  .close {\n    width: 60px;\n    height: 60px;\n    position: absolute;\n    right: 20px;\n    top: 20px;\n    background: url('../../../assets/img/share/close.png') no-repeat center;\n    background-size: 32px 32px;\n  }\n}\n",null],sourceRoot:""}])},"Hdo/":function(t,n){t.exports="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEIAAABCCAYAAADjVADoAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyBpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMC1jMDYwIDYxLjEzNDc3NywgMjAxMC8wMi8xMi0xNzozMjowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNSBXaW5kb3dzIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOjM3NzY4NjZCNDIzMDExRTk5QUMzRTIxMkU2QzlDMTdCIiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOjM3NzY4NjZDNDIzMDExRTk5QUMzRTIxMkU2QzlDMTdCIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6Mzc3Njg2Njk0MjMwMTFFOTlBQzNFMjEyRTZDOUMxN0IiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6Mzc3Njg2NkE0MjMwMTFFOTlBQzNFMjEyRTZDOUMxN0IiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz42Cv13AAAHn0lEQVR42uxcbUiUWRQ+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"},NeYh:function(t,n,i){var e=i("xaLF");"string"==typeof e&&(e=[[t.i,e,""]]),e.locals&&(t.exports=e.locals);i("FIqI")("93752b2a",e,!1,{})},QhQR:function(t,n,i){"use strict";Object.defineProperty(n,"__esModule",{value:!0}),n.default={data:function(){return{}},props:{details:{type:Object}},methods:{reEdit:function(){this.$emit("reEdit",!0)},close:function(){this.$emit("reEdit",!1)}}}},"Y/IJ":function(t,n,i){"use strict";var e=function(){var t=this,n=t.$createElement,i=t._self._c||n;return i("div",{staticClass:"maskBox",staticStyle:{"z-index":"5"}},[i("div",{staticClass:"passContent"},[i("div",{staticClass:"close",on:{click:function(n){return t.close()}}}),t._v(" "),i("div",{staticClass:"title"},[t._v("截图未通过审核")]),t._v(" "),t._m(0),t._v(" "),i("div",{staticClass:"c-content"},[t._v(t._s(t.details.audit_result))]),t._v(" "),i("div",{staticClass:"c-button",on:{click:t.reEdit}},[t._v("好的")])])])};e._withStripped=!0;var a={render:e,staticRenderFns:[function(){var t=this.$createElement,n=this._self._c||t;return n("div",{staticClass:"p-content"},[this._v("很抱歉，你的截图未通过审核，原因"),n("br"),this._v("如下，请编辑后重新提交：")])}]};n.a=a},"ZK+p":function(t,n,i){"use strict";Object.defineProperty(n,"__esModule",{value:!0});var e=i("QhQR"),a=i.n(e);for(var o in e)"default"!==o&&function(t){i.d(n,t,function(){return e[t]})}(o);var s=i("Y/IJ"),l=!1;var r=function(t){l||i("EEp6")},c=i("C7Lr")(a.a,s.a,!1,r,"data-v-7884f2c4",null);c.options.__file="src/components/personal-center/activity/not-pass.vue",n.default=c.exports},kc2w:function(t,n){t.exports="data:image/png;base64,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"},qdlT:function(t,n,i){"use strict";var e=function(){var t=this,n=t.$createElement,i=t._self._c||n;return i("div",{staticClass:"scrollBox"},[t.$utils.showNavBar()?i("header-nav",{attrs:{msg:t.msg}}):t._e(),t._v(" "),i("div",{class:{hasHeader:t.$utils.showNavBar()},attrs:{id:"activityDetail"}},[t.details&&0!==t.details.audit_status?1===t.details.audit_status||3===t.details.audit_status?i("div",[i("div",{staticClass:"a-title"},[t._v("您已提交截图，"),t.$route.query.endTime?i("span",[t._v(t._s(t.$utils.timestampToStr(new Date(t.$route.query.endTime.replace(/-/g,"/")),"Y-M-D")))]):t._e(),t._v("审核完毕。")]),t._v(" "),i("div",{staticClass:"a-user"},[i("div",{staticClass:"avatar iconSize"},[i("img",{attrs:{src:t.commonState.userInfo.avatar,alt:"头像"}})]),t._v(" "),i("div",{staticClass:"nickname"},[t._v(t._s(t.$utils.splitStr(t.commonState.userInfo.nickname,8)))])]),t._v(" "),i("div",{staticClass:"rect"}),t._v(" "),i("div",{staticClass:"second-title"},[t._v("提交截图")]),t._v(" "),i("div",{staticClass:"img-list second border-1px"},t._l(t.details.picture_urls,function(t,n){return i("div",{key:n,staticClass:"iconSize"},[i("div",{staticClass:"imgWrapper"},[i("img",{attrs:{src:t,preview:"1",alt:"图片"}})])])}),0),t._v(" "),i("div",{staticClass:"second-title"},[t._v("备注信息")]),t._v(" "),i("div",{staticClass:"remark"},[t._v(t._s(t.details.content))])]):2===t.details.audit_status?i("div",{staticClass:"overflow"},[t._m(1),t._v(" "),i("div",{staticClass:"o-title"},[t._v("你的截图通过审核")]),t._v(" "),t._m(2),t._v(" "),i("div",{staticClass:"o-third o-tip"},[t._v("希望你继续参与下次平台活动！")])]):t._e():i("div",[i("div",{staticClass:"activity-title"},[t._v("请上传3张应用市场好评截图")]),t._v(" "),i("div",{staticClass:"img-list"},[t._l(t.previewImgList,function(n,e){return i("div",{key:e,staticClass:"iconSize"},[i("div",{staticClass:"imgWrapper"},[i("img",{attrs:{src:n,preview:"1",alt:"图片"}})]),t._v(" "),i("div",{staticClass:"deleteIcon",on:{click:function(n){return t.deleteImage(e)}}})])}),t._v(" "),t.previewImgList.length<3?i("div",{staticClass:"upload"},[t._m(0),t._v(" "),i("div",{staticClass:"tips"},[t._v(t._s(t.previewImgList.length>0?t.previewImgList.length+" / 3":"添加图片"))]),t._v(" "),i("label",{staticClass:"labelContent",attrs:{for:"inputFile"}},[i("input",{staticClass:"uploadFile",attrs:{id:"inputFile",multiple:"multiple",accept:"image/*",type:"file"},on:{change:t.uploadFile}})])]):t._e()],2),t._v(" "),i("div",{staticClass:"rect"}),t._v(" "),i("div",{staticClass:"inputText"},[i("div",{staticClass:"activity-title"},[t._v("截图备注说明（必填）")]),t._v(" "),i("div",{staticClass:"activity-content"},[i("textarea",{directives:[{name:"model",rawName:"v-model",value:t.content,expression:"content"}],staticClass:"activity-content",attrs:{placeholder:"请详细描述这是哪几个应用市场的截图，以便审核通过，5-100字",maxlength:"100"},domProps:{value:t.content},on:{input:function(n){n.target.composing||(t.content=n.target.value)}}}),t._v(" "),i("span",{staticClass:"count"},[t._v(t._s(t.content.length)+" / 100")])]),t._v(" "),i("div",{staticClass:"activity-button",class:{active:t.content.length>0},on:{click:t.confirmFn}},[t._v("提交")])])])]),t._v(" "),i("prompt-box",{attrs:{dropBol:t.showFlag,rightTxt:"提交",contentTxt:"是否提交截图进行审核"},on:{leftBtn:function(n){t.showFlag=!1},rightBtn:t.submit}}),t._v(" "),t.showPass?i("not-pass",{attrs:{details:t.details},on:{reEdit:t.reEdit}}):t._e()],1)},a=[function(){var t=this.$createElement,n=this._self._c||t;return n("div",{staticClass:"photo"},[n("img",{attrs:{src:i("Ctw3"),alt:"上传"}})])},function(){var t=this.$createElement,n=this._self._c||t;return n("div",{staticClass:"success-icon iconSize"},[n("img",{attrs:{src:i("kc2w"),alt:"审核成功"}})])},function(){var t=this.$createElement,n=this._self._c||t;return n("div",{staticClass:"o-sec-title o-tip"},[this._v("系统已将本次活动奖励发放到你的掘金宝账户"),n("br"),this._v("里，请及时登录掘金宝APP查看。")])}];e._withStripped=!0;var o={render:e,staticRenderFns:a};n.a=o},xaLF:function(t,n,i){var e=i("L4zZ");(t.exports=i("UTlt")(!0)).push([t.i,'\n#activityDetail[data-v-e2099038]{overflow:auto;height:100vh;-webkit-overflow-scrolling:touch;content:"viewport-units-buggyfill; height: 100vh"\n}\n#activityDetail .overflow[data-v-e2099038]{overflow:hidden\n}\n#activityDetail .overflow .success-icon[data-v-e2099038]{width:16.533333vw;height:16.533333vw;margin:6.666667vw auto;content:"viewport-units-buggyfill; width: 16.533333vw; height: 16.533333vw; margin: 6.666667vw auto"\n}\n#activityDetail .overflow .o-title[data-v-e2099038]{font-size:4.266667vw;text-align:center;line-height:6vw;color:#333;content:"viewport-units-buggyfill; font-size: 4.266667vw; line-height: 6vw"\n}\n#activityDetail .overflow .o-tip[data-v-e2099038]{font-size:3.733333vw;color:#666;line-height:5.333333vw;text-align:center;content:"viewport-units-buggyfill; font-size: 3.733333vw; line-height: 5.333333vw"\n}\n#activityDetail .overflow .o-sec-title[data-v-e2099038]{padding:4vw;content:"viewport-units-buggyfill; padding: 4vw"\n}\n#activityDetail .activity-title[data-v-e2099038]{padding:4vw 4vw 6.666667vw;font-size:4.266667vw;color:#333;content:"viewport-units-buggyfill; padding: 4vw 4vw 6.666667vw; font-size: 4.266667vw"\n}\n#activityDetail .img-list[data-v-e2099038]{padding:0 4vw 4vw;display:-webkit-box;display:-ms-flexbox;display:flex;-ms-flex-flow:nowrap;flex-flow:nowrap;content:"viewport-units-buggyfill; padding: 0 4vw 4vw"\n}\n#activityDetail .img-list .iconSize[data-v-e2099038]{background:#d8d8d8;width:17.733333vw;height:17.733333vw;margin-right:4vw;position:relative;content:"viewport-units-buggyfill; width: 17.733333vw; height: 17.733333vw; margin-right: 4vw"\n}\n#activityDetail .img-list .iconSize .deleteIcon[data-v-e2099038]{width:5.333333vw;height:5.333333vw;background:url('+e(i("Hdo/"))+') no-repeat 50%;background-size:100%;right:-2.666667vw;top:-2.666667vw;position:absolute;cursor:pointer;content:"viewport-units-buggyfill; width: 5.333333vw; height: 5.333333vw; right: -2.666667vw; top: -2.666667vw"\n}\n#activityDetail .img-list .iconSize .imgWrapper[data-v-e2099038]{position:relative;width:100%;height:100%;overflow:hidden\n}\n#activityDetail .img-list .iconSize .imgWrapper>img[data-v-e2099038]{width:100%;position:absolute;top:50%;-webkit-transform:translateY(-50%);transform:translateY(-50%);height:auto\n}\n#activityDetail .rect[data-v-e2099038]{height:2.666667vw;background:#f5f5f5;content:"viewport-units-buggyfill; height: 2.666667vw"\n}\n#activityDetail .upload[data-v-e2099038]{width:17.733333vw;height:17.733333vw;display:-webkit-box;display:-ms-flexbox;display:flex;-webkit-box-orient:vertical;-webkit-box-direction:normal;-ms-flex-flow:column wrap;flex-flow:column wrap;-webkit-box-align:center;-ms-flex-align:center;align-items:center;-webkit-box-pack:center;-ms-flex-pack:center;justify-content:center;border:1px dashed #999;position:relative;content:"viewport-units-buggyfill; width: 17.733333vw; height: 17.733333vw"\n}\n#activityDetail .upload label[data-v-e2099038]{display:block;position:absolute;width:100%;height:100%;left:0;top:0\n}\n#activityDetail .upload .photo[data-v-e2099038]{width:6.533333vw;height:5.733333vw;content:"viewport-units-buggyfill; width: 6.533333vw; height: 5.733333vw"\n}\n#activityDetail .upload .photo img[data-v-e2099038]{width:100%;height:100%\n}\n#activityDetail .upload .tips[data-v-e2099038]{font-size:3.2vw;margin-top:2.666667vw;color:#999;content:"viewport-units-buggyfill; font-size: 3.2vw; margin-top: 2.666667vw"\n}\n#activityDetail .upload input[type=file][data-v-e2099038]{width:0;height:0;overflow:hidden;visibility:hidden\n}\n#activityDetail .activity-content[data-v-e2099038]{position:relative;width:92vw;margin:0 auto;content:"viewport-units-buggyfill; width: 92vw"\n}\n#activityDetail .activity-content textarea[data-v-e2099038]{width:92vw;height:38.933333vw;border-radius:1.333333vw;display:block;-webkit-box-sizing:border-box;box-sizing:border-box;padding:2.666667vw 2.666667vw 6.666667vw;-webkit-user-select:text;-moz-user-select:text;-ms-user-select:text;user-select:text;line-height:1.5;font-size:3.733333vw;color:#333;content:"viewport-units-buggyfill; width: 92vw; height: 38.933333vw; border-radius: 1.333333vw; padding: 2.666667vw 2.666667vw 6.666667vw; font-size: 3.733333vw"\n}\n#activityDetail .activity-content .count[data-v-e2099038]{position:absolute;color:#333;height:4vw;font-size:3.733333vw;line-height:4vw;bottom:2.666667vw;right:2.666667vw;z-index:1;content:"viewport-units-buggyfill; height: 4vw; font-size: 3.733333vw; line-height: 4vw; bottom: 2.666667vw; right: 2.666667vw"\n}\n#activityDetail .activity-button[data-v-e2099038]{margin:14.8vw auto 6.666667vw;width:62.8vw;height:10.666667vw;line-height:10.666667vw;text-align:center;background:#f4a82b;border-radius:5.333333vw;color:#fff;content:"viewport-units-buggyfill; margin: 14.8vw auto 6.666667vw; width: 62.8vw; height: 10.666667vw; line-height: 10.666667vw; border-radius: 5.333333vw"\n}\n#activityDetail .a-title[data-v-e2099038]{font-size:3.733333vw;color:#ff7f18;line-height:9.333333vw;height:9.333333vw;text-align:center;background:#fceac5;content:"viewport-units-buggyfill; font-size: 3.733333vw; line-height: 9.333333vw; height: 9.333333vw"\n}\n#activityDetail .a-user[data-v-e2099038]{padding:4vw;text-align:center;content:"viewport-units-buggyfill; padding: 4vw"\n}\n#activityDetail .avatar[data-v-e2099038]{width:13.333333vw;height:13.333333vw;border-radius:50%;margin:0 auto;overflow:hidden;content:"viewport-units-buggyfill; width: 13.333333vw; height: 13.333333vw"\n}\n#activityDetail .nickname[data-v-e2099038]{padding-top:3.2vw;color:#666;line-height:5.333333vw;content:"viewport-units-buggyfill; padding-top: 3.2vw; line-height: 5.333333vw"\n}\n#activityDetail .second-title[data-v-e2099038]{padding:4vw 4vw 2.666667vw;line-height:6vw;font-size:4.266667vw;content:"viewport-units-buggyfill; padding: 4vw 4vw 2.666667vw; line-height: 6vw; font-size: 4.266667vw"\n}\n#activityDetail .remark[data-v-e2099038]{border:1px solid #999;border-radius:1.333333vw;color:#666;font-size:3.733333vw;min-height:17.733333vw;width:92vw;margin:0 auto;line-height:1.5;padding:1.333333vw 2.666667vw;-webkit-box-sizing:border-box;box-sizing:border-box;content:"viewport-units-buggyfill; border-radius: 1.333333vw; font-size: 3.733333vw; min-height: 17.733333vw; width: 92vw; padding: 1.333333vw 2.666667vw"\n}',"",{version:3,sources:["/Users/apple/Documents/Gits/mobile2_juejinlian/src/components/personal-center/activity/detail.vue","/Users/apple/Documents/Gits/mobile2_juejinlian/src/components/personal-center/activity/<no source>"],names:[],mappings:";AA2NA,iCACE,cAAc,aACD,iCACoB,iDC9NnC;CDuZC;AA5LD,2CAKI,eAAgB;CAqBjB;AA1BH,yDAOM,kBAAY,mBACC,uBACI,oGCpOvB;CDqOK;AAVL,oDAYM,qBAAe,kBACG,gBACD,WAEnB,2EC3OJ;CD2OK;AAhBL,kDAkBM,qBAAe,WACD,uBACG,kBACC,kFChPxB;CDiPK;AAtBL,wDAwBM,YAAa,gDCnPnB;CDoPK;AAzBL,iDA4BI,2BAAuB,qBACR,WACD,sFCzPlB;CD0PG;AA/BH,2CAiCI,kBAAoB,oBACpB,oBAAA,aAAa,qBACb,iBAAiB,sDC9PrB;CD6RG;AAlEH,qDAqCM,mBAAmB,kBACP,mBACC,iBACK,kBACA,8FCpQxB;CD4RK;AAjEL,iEA2CQ,iBAAW,kBACC,uDAC+D,qBACtD,kBACT,gBACF,kBACQ,eACH,+GC7QvB;CD8QO;AAnDP,iEAqDQ,kBAAkB,WACP,YACC,eACI;CAQjB;AAhEP,qEA0DU,WAAW,kBACO,QACV,mCACR,2BAA2B,WACf;CACb;AA/DT,uCAoEI,kBAAY,mBACO,sDChSvB;CDiSG;AAtEH,yCAwEI,kBAAY,mBACC,oBACb,oBAAA,aAAa,4BACb,6BAAA,0BAAA,sBAAsB,yBACtB,sBAAA,mBAAmB,wBACnB,qBAAA,uBAAuB,uBACA,kBACL,2EC1StB;CDuUG;AA5GH,+CAiFM,cAAc,kBACI,WACP,YACC,OACL,KACD;CACP;AAvFL,gDAyFM,iBAAW,kBACC,yECrTlB;CD0TK;AA/FL,oDA4FQ,WAAW,WACC;CACb;AA9FP,+CAiGM,gBAAe,sBACC,WACL,4EC9TjB;CD+TK;AApGL,0DAuGM,QAAQ,SACC,gBACO,iBACE;CACnB;AA3GL,mDA8GI,kBAAkB,WACN,cACE,+CC3UlB;CDkWG;AAvIH,4DAkHM,WAAY,mBACC,yBACM,cACL,8BACd,sBAAsB,yCACC,yBACvB,sBAAA,qBAAA,iBAAiB,gBACD,qBACD,WACJ,iKCtVjB;CDuVK;AA5HL,0DA8HM,kBAAkB,WACP,WACC,qBACG,gBACE,kBACL,iBACD,UACD,+HChWhB;CDiWK;AAtIL,kDAyII,8BAAuB,aACX,mBACA,wBACK,kBACC,mBACC,yBACA,WACR,2JC3Wf;CD4WG;AAjJH,0CAoJI,qBAAe,cACD,uBACG,kBACL,kBACM,mBACC,sGCpXvB;CDqXG;AA1JH,yCA4JI,YAAa,kBACK,gDCxXtB;CDyXG;AA9JH,yCAgKI,kBAAY,mBACC,kBACK,cACJ,gBACE,2EC/XpB;CDgYG;AArKH,2CAuKI,kBAAiB,WACN,uBACM,+ECpYrB;CDqYG;AA1KH,+CA4KI,2BAAuB,gBACN,qBACF,wGCzYnB;CD0YG;AA/KH,yCAiLI,sBAAyB,yBACN,WACR,qBACI,uBACE,WACL,cACE,gBACE,8BACE,8BAClB,sBAAsB,0JCrZ1B;CDsZG",file:"detail.vue",sourcesContent:["\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n#activityDetail {\n  overflow: auto;\n  height: 100vh;\n  -webkit-overflow-scrolling: touch;\n  .overflow {\n    overflow: hidden;\n    .success-icon {\n      width: 124px;\n      height: 124px;\n      margin: 50px auto;\n    }\n    .o-title {\n      font-size: 32px;\n      text-align: center;\n      line-height: 45px;\n      color: #333333\n    }\n    .o-tip {\n      font-size: 28px;\n      color: #666666;\n      line-height: 40px;\n      text-align: center;\n    }\n    .o-sec-title {\n      padding: 30px;\n    }\n  }\n  .activity-title {\n    padding: 30px 30px 50px;\n    font-size: 32px;\n    color: #333333;\n  }\n  .img-list {\n    padding: 0 30px 30px;\n    display: flex;\n    flex-flow: nowrap;\n    .iconSize {\n      background: #D8D8D8;\n      width: 133px;\n      height: 133px;\n      margin-right: 30px;\n      position: relative;\n      .deleteIcon {\n        width: 40px;\n        height: 40px;\n        background:  url('../../../assets/img/personal/close.png') no-repeat center;\n        background-size: 100%;\n        right: -20px;\n        top: -20px;\n        position: absolute;\n        cursor: pointer;\n      }\n      .imgWrapper {\n        position: relative;\n        width: 100%;\n        height: 100%;\n        overflow: hidden;\n        >img {\n          width: 100%;\n          position: absolute;\n          top: 50%;\n          transform: translateY(-50%);\n          height: auto;\n        }\n      }\n    }\n  }\n  .rect {\n    height: 20px;\n    background: #f5f5f5;\n  }\n  .upload {\n    width: 133px;\n    height: 133px;\n    display: flex;\n    flex-flow: column wrap;\n    align-items: center;\n    justify-content: center;\n    border: 1px dashed #999;\n    position: relative;\n    label {\n      display: block;\n      position: absolute;\n      width: 100%;\n      height: 100%;\n      left: 0;\n      top: 0;\n    }\n    .photo {\n      width: 49px;\n      height: 43px;\n      img {\n        width: 100%;\n        height: 100%;\n      }\n    }\n    .tips {\n      font-size: 24px;\n      margin-top: 20px;\n      color: #999;\n    }\n    input[type=file] {\n      // display: none;\n      width: 0;\n      height: 0;\n      overflow: hidden;\n      visibility: hidden;\n    }\n  }\n  .activity-content {\n    position: relative;\n    width: 690px;\n    margin: 0 auto;\n    textarea {\n      width: 690px;\n      height: 292px;\n      border-radius: 10px;\n      display: block;\n      box-sizing: border-box;\n      padding: 20px 20px 50px;\n      user-select: text;\n      line-height: 1.5;\n      font-size: 28px;\n      color: #333;\n    }\n    .count {\n      position: absolute;\n      color: #333;\n      height: 30px;\n      font-size: 28px;\n      line-height: 30px;\n      bottom: 20px;\n      right: 20px;\n      z-index: 2;\n    }\n  }\n  .activity-button {\n    margin: 111px auto 50px;\n    width: 471px;\n    height: 80px;\n    line-height: 80px;\n    text-align: center;\n    background: #F4A82B;\n    border-radius: 40px;\n    color: #fff;\n  }\n  // 状态2\n  .a-title {\n    font-size: 28px;\n    color: #FF7F18;\n    line-height: 70px;\n    height: 70px;\n    text-align: center;\n    background: #FCEAC5;\n  }\n  .a-user {\n    padding: 30px;\n    text-align: center;\n  }\n  .avatar {\n    width: 100px;\n    height: 100px;\n    border-radius: 50%;\n    margin: 0 auto;\n    overflow: hidden;\n  }\n  .nickname {\n    padding-top: 24px;\n    color: #666;\n    line-height: 40px;\n  }\n  .second-title {\n    padding: 30px 30px 20px;\n    line-height: 45px;\n    font-size: 32px;\n  }\n  .remark {\n    border: 1px solid #999999;\n    border-radius: 10px;\n    color: #666;\n    font-size: 28px;\n    min-height: 133px;\n    width: 690px;\n    margin: 0 auto;\n    line-height: 1.5;\n    padding: 10px 20px;\n    box-sizing: border-box;\n  }\n}\n",null],sourceRoot:""}])}});
//# sourceMappingURL=27.fecbc2ab290616df6e1d.js.map
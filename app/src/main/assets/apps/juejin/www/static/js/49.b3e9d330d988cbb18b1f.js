webpackJsonp([49],{TSIU:function(n,e,t){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=t("jny3"),s=t.n(i);for(var o in i)"default"!==o&&function(n){t.d(e,n,function(){return i[n]})}(o);var a=t("v/uh"),l=!1;var c=function(n){l||t("n8XR")},r=t("VU/8")(s.a,a.a,!1,c,"data-v-1dbe126b",null);r.options.__file="src/components/task/upload.vue",e.default=r.exports},jny3:function(n,e,t){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i,s=t("TQvf"),o=(i=s)&&i.__esModule?i:{default:i};e.default={data:function(){return{file:null,imgFile:"",fileName:"",url:"",copyText:""}},methods:{getFile:function(n){var e=n.srcElement?n.srcElement:n.target;this.file=e.files[0],this.fileName=this.file.name},upload:function(){var n=this;this.file&&this.$utils.uploader(this.$utils.cache("user_token"),"vsn1_1",this.file,this.file.name.split(".")[0]).then(function(e){n.imgFile=e.url,n.url=e.url})},copyMy:function(){var n=this;if(this.copyText=this.url,o.default.isSupported()){var e=new o.default(".clipbtn");e.on("success",function(t){n.$toast({message:"复制成功,请发送给好友",position:"middle",duration:1500,iconClass:"icon icon-success"}),e.destroy()}),e.on("error",function(t){n.$toast({message:"复制链接失败了",position:"middle",duration:1500,iconClass:"icon icon-success"}),e.destroy()})}}}}},kKdd:function(n,e,t){(n.exports=t("FZ+f")(!0)).push([n.i,'\n#chooseFile[data-v-1dbe126b]{display:none\n}\n.item[data-v-1dbe126b]{display:-webkit-box;display:-ms-flexbox;-webkit-box-align:center;-ms-flex-align:center;height:10.666667vw;border-bottom:1px solid #eee;display:flex;align-items:center;padding:0 4vw;content:"viewport-units-buggyfill; height: 10.666667vw; padding: 0 4vw"\n}',"",{version:3,sources:["/Users/apple/Documents/Gits/mobile2_juejinlian_native/src/components/task/upload.vue","/Users/apple/Documents/Gits/mobile2_juejinlian_native/src/components/task/<no source>"],names:[],mappings:";AAiEA,6BACE,YAAa;CACd;AACD,uBACE,oBAAA,oBAAa,yBACb,sBAAmB,mBACP,6BACgB,aACf,mBACM,cACJ,uEC3EjB;CD4EC",file:"upload.vue",sourcesContent:["\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n#chooseFile {\n  display: none;\n}\n.item {\n  display: flex;\n  align-items: center;\n  height: 80px;\n  border-bottom: 1px solid#eee;\n  display: flex;\n  align-items: center;\n  padding: 0 30px;\n}\n",null],sourceRoot:""}])},n8XR:function(n,e,t){var i=t("kKdd");"string"==typeof i&&(i=[[n.i,i,""]]),i.locals&&(n.exports=i.locals);t("rjj0")("28867448",i,!1,{})},"v/uh":function(n,e,t){"use strict";var i=function(){var n=this,e=n.$createElement,t=n._self._c||e;return t("div",{attrs:{id:"upload"}},[t("label",{staticClass:"item",attrs:{for:"chooseFile"}},[n._v("选择文件"+n._s(n.fileName))]),n._v(" "),t("input",{staticClass:"upload",attrs:{id:"chooseFile",type:"file"},on:{change:n.getFile}}),n._v(" "),t("div",{staticClass:"item",on:{click:n.upload}},[n._v("上传文件")]),n._v(" "),n.url?t("div",{staticClass:"item"},[n._v(n._s(n.url))]):n._e(),n._v(" "),n.url?t("div",{staticClass:"item clipbtn",attrs:{"data-clipboard-text":n.copyText},on:{click:n.copyMy}},[n._v("点我复制")]):n._e()])};i._withStripped=!0;var s={render:i,staticRenderFns:[]};e.a=s}});
//# sourceMappingURL=49.b3e9d330d988cbb18b1f.js.map
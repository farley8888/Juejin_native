webpackJsonp([55],{"2gAH":function(n,t,i){"use strict";var e=function(){var n=this,t=n.$createElement,i=n._self._c||t;return i("div",{attrs:{id:"videoList"}},[n.videoNav.length>0?i("div",{ref:"videoChannel",staticClass:"prevent",staticStyle:{"z-index":"12"},attrs:{id:"videoChannel"}},[i("div",{staticClass:"v-video-channelbox noScrollBar prevent"},n._l(n.videoNav,function(t,e){return i("div",{key:e,staticClass:"prevent",class:n.activeIndex===e?"active":"",on:{click:function(i){return n.switchVideo(t,e)}}},[n._v("\n        "+n._s(t.ch)+"\n      ")])}),0)]):n._e(),n._v(" "),i("div",{ref:"wrapper",staticClass:"wrapper",class:{isBrowser:n.$config.source_style<6}},[i("div",{ref:"bg_fff",staticClass:"bg-fff"},[i("transition",{attrs:{name:"topTxt"}},[n.topBol?i("div",{staticClass:"text-center lh66 topTxt"},[n._v("\n          "+n._s(0!==n.num?"为您推荐"+n.num+"条更新":"暂无更新，请休息会再来~")+"\n        ")]):n._e()]),n._v(" "),i("video-list",{ref:"muAdRooot",attrs:{loading:n.loading,videoList:n.list},on:{digg_status:n.digg_status,deleteAd:n.deleteAd,videoDigg:n.videoDigg,upDate:n.upDate}})],1)])])};e._withStripped=!0;var o={render:e,staticRenderFns:[]};t.a=o},BHEb:function(n,t,i){var e=i("j5gy");"string"==typeof e&&(e=[[n.i,e,""]]),e.locals&&(n.exports=e.locals);i("FIqI")("6b6ea579",e,!1,{})},GGU1:function(n,t,i){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var e=i("MH2t"),o=i.n(e);for(var a in e)"default"!==a&&function(n){i.d(t,n,function(){return e[n]})}(a);var s=i("2gAH"),l=!1;var r=function(n){l||i("BHEb")},d=i("C7Lr")(o.a,s.a,!1,r,"data-v-73bcda1a",null);d.options.__file="src/components/indexList/videoList.vue",t.default=d.exports},MH2t:function(n,t,i){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var e=d(i("IHPB")),o=d(i("HzJ8")),a=d(i("4YfN")),s=d(i("/QXl")),l=d(i("soxv")),r=i("R4Sj");function d(n){return n&&n.__esModule?n:{default:n}}t.default={name:"videoCommon",props:{columnid:{type:Number,default:0},swichId:{type:Number,default:0}},data:function(){return{loading:0,topList:[],page:1,popupVisible:!1,topBol:!1,num:0,indexGray:null,timer:null,downBol:!1,lasttime:0,videoNav:[],activeIndex:0,list:[]}},methods:(0,a.default)({},(0,r.mapMutations)(["setVideoPage","comment_video_list"]),{setBrowserNums:function(n){var t={ad_id:n,type:0,uuid:this.$store.state.common.uuid};this.$api.get("AdvTOTAL",t,function(n){})},scrollFn:function(){var n=this.$refs.muAdRooot.$el.querySelectorAll(".mutiAdList");if(n&&n.length>0){var t=!0,i=!1,e=void 0;try{for(var a,s=(0,o.default)(n);!(t=(a=s.next()).done);t=!0){var l=a.value;this.$utils.setObj(l,this.setBrowser)&&l.getBoundingClientRect().top-window.outerHeight<0&&(this.setBrowser[l.dataset.id]=!0,console.log(this.setBrowser),this.setBrowserNums(l.dataset.id))}}catch(n){i=!0,e=n}finally{try{!t&&s.return&&s.return()}finally{if(i)throw e}}}},deleteAd:function(n){this.list.splice(n,1)},initScroll:function(){var n=this;this.$nextTick(function(){n.$refs.videoChannel&&(n.videoScroll?n.videoScroll.refresh():n.videoScroll=new s.default(n.$refs.videoChannel,{click:!0,scrollY:!0,scrollX:!0,bounceTime:100,mouseWheel:{speed:10,invert:!1,easeTime:300}}))})},switchVideo:function(n,t){var i=this;this.page=1,this.timer=setTimeout(function(){i.timer&&(clearTimeout(i.timer),i.timer=null),i.activeIndex=t,i.getVideoList()},100)},videoDigg:function(n){this.$set(this.list[n],"video_like_count",this.list[n].video_like_count?++this.list[n].video_like_count:1),this.$set(this.list[n],"is_fabulous",1)},digg_status:function(n){var t=n.index,i=n.value;this.$set(this.list[t],"digg_status",i)},loadData:function(){var n=this;this.$nextTick(function(){n.scroll?n.$nextTick(function(){n.scroll.refresh()}):(n.scroll=new s.default(n.$refs.wrapper,{click:!0,probeType:3,pullDownRefresh:{threshold:100,stop:0},pullUpLoad:!0,mouseWheel:{speed:10,invert:!1,easeTime:300},bounceTime:400,scrollbar:{fade:!0,interactive:!1}}),n.scroll.on("pullingDown",function(){n.downList()}),n.scroll.on("pullingUp",function(){n.upList()}),n.scroll.on("scroll",function(t){n.scrollFn(),t.y<80&&t.y>-80&&n.$emit("topscroll",t.y)}))})},refresh:function(){this.scroll&&this.scroll.scrollTo(0,0,200),this.downList()},downList:function(){var n=this;0!==this.videoNav.length?(this.num&&this.list.splice(this.num,1),this.getVideoList(function(t){clearTimeout(n.timer),n.topBol=!0,n.list=[].concat((0,e.default)(t),(0,e.default)(n.list)),n.$nextTick(function(){n.$store.commit("common_set_dblickLoading",0),n.scroll.finishPullDown(),n.scroll.refresh(),n.$indicator.close(),n.scroll.openPullUp(),n.timer=setTimeout(function(){n.topBol=!1},2e3),n.num=t.length,n.scroll.openPullUp()})})):this.getCatLogList()},upList:function(){var n=this;if(this.downBol)setTimeout(function(){n.downBol=!0},800);else{this.loading=1,this.$emit("downFn",!0);this.columnid,this.getVideoList(function(t){n.loading=0,n.list=[].concat((0,e.default)(n.list),(0,e.default)(t)),n.$emit("downFn",!1),0===n.list.length&&n.scroll.closePullUp(),n.$nextTick(function(){n.scroll.finishPullUp(),n.scroll.refresh(),n.downBol=!1})})}},getVideoList:function(n){var t=this,i=this.videoNav[this.activeIndex];var e={category:i.en,page:this.page,ad_position:3};this.share.videoPage[e.category]&&(e.page=this.share.videoPage[e.category],this.page=e.page),this.$api.get("VIDEO_LISTS",e,function(i){var o=i.data.data;t.page++,t.page>=i.data.last_page?t.setVideoPage({name:e.category,page:1}):t.setVideoPage({name:e.category,page:t.page}),"function"==typeof n?n&&n(o):(t.list=o,t.$nextTick(function(){t.loadData(),t.scroll&&t.scroll.scrollTo(0,0,1e3)}))},function(){t.downBol=!1})},getCatLogList:function(){var n=this;this.videoNav.length<=1&&this.$api.get("CATEGORY",{},function(t){var i=[],e=!0,a=!1,s=void 0;try{for(var l,r=(0,o.default)(t.data);!(e=(l=r.next()).done);e=!0){var d=l.value;"关注"!==d.name&&"直播"!==d.name&&i.push(d)}}catch(n){a=!0,s=n}finally{try{!e&&r.return&&r.return()}finally{if(a)throw s}}n.videoNav=i,n.loadData()})},getCatModelData:function(){var n=this;this.videoNav.length<=1&&this.$api.get("CATEGORY",{},function(t){var i=[],e=!0,a=!1,s=void 0;try{for(var l,r=(0,o.default)(t.data);!(e=(l=r.next()).done);e=!0){var d=l.value;"关注"!==d.name&&"直播"!==d.name&&i.push(d)}}catch(n){a=!0,s=n}finally{try{!e&&r.return&&r.return()}finally{if(a)throw s}}n.videoNav=i,n.getVideoList(),n.loadData()})},initData:function(){var n=this;0===this.videoNav.length&&this.$api.get("CATEGORY",{},function(t){n.videoNav=t.data,n.getVideoList()})},upDate:function(n){this.indexGray=n},isAttention:function(){var n=[],t=[];for(var i in this.$utils.cache("add_attention")&&(n=JSON.parse(this.$utils.cache("add_attention"))||[]),this.$utils.cache("cancel_attention")&&(t=JSON.parse(this.$utils.cache("cancel_attention"))),n)for(var e in this.list)n[i]===this.list[e].mid&&(this.list[e].is_follow=1);for(var o in t)for(var a in this.list)t[o]===this.list[a].mid&&(this.list[a].is_follow=1);this.$utils.cache("add_attention",null),this.$utils.cache("cancel_attention",null)}}),watch:{videoNav:function(n){this.$emit("getVideoList",n)},swichId:function(n){this.switchVideo(this.videoNav[n],n)},"commonState.dbClick":function(){1*this.commonState.columnid==44&&this.downList()}},activated:function(){this.loadData()},computed:(0,r.mapState)({tempList:function(n){return n.comment.videoList},share:function(n){return n.share},commonState:function(n){return n.common}}),mounted:function(){var n=this;this.timer=setTimeout(function(){n.initData(),n.activeIndex=0},500)},destroyed:function(){this.timer&&clearTimeout(this.timer)},deactivated:function(){this.timer&&clearTimeout(this.timer)},components:{videoList:l.default}}},j5gy:function(n,t,i){(n.exports=i("UTlt")(!0)).push([n.i,'\n#videoList[data-v-73bcda1a]{overflow:hidden;position:relative!important;width:100vw;content:"viewport-units-buggyfill; width: 100vw"\n}\n#videoList #videoChannel[data-v-73bcda1a]{height:12vw;-webkit-box-sizing:border-box;box-sizing:border-box;background:#fff;position:fixed;z-index:2;left:0;right:0;width:100vw;line-height:12vw;overflow-y:hidden;overflow-x:auto;-webkit-overflow-scrolling:touch;content:"viewport-units-buggyfill; height: 12vw; width: 100vw; line-height: 12vw"\n}\n#videoList #videoChannel .v-video-channelbox[data-v-73bcda1a]{white-space:nowrap;padding:0 1.6vw;width:auto;min-width:600vw;content:"viewport-units-buggyfill; padding: 0 1.6vw; min-width: 600vw"\n}\n#videoList #videoChannel .v-video-channelbox>div[data-v-73bcda1a]{display:inline-block;font-size:4.266667vw;padding:0 1.333333vw;margin:0 1.333333vw;background:#fff;height:5.333333vw;line-height:5.333333vw;content:"viewport-units-buggyfill; font-size: 4.266667vw; padding: 0 1.333333vw; margin: 0 1.333333vw; height: 5.333333vw; line-height: 5.333333vw"\n}\n#videoList #videoChannel .v-video-channelbox>div.active[data-v-73bcda1a]{background:#b2b2b2;color:#fff;border-radius:.666667vw;content:"viewport-units-buggyfill; border-radius: 0.666667vw"\n}\n#videoList .wrapper[data-v-73bcda1a]{height:calc(100vh - 37.6vw);margin-top:12vw;position:relative;content:"viewport-units-buggyfill; height: calc(100vh - 37.6vw); margin-top: 12vw"\n}\n#videoList .wrapper.isBrowser[data-v-73bcda1a]{height:calc(100vh - 50.93333vw);content:"viewport-units-buggyfill; height: calc(100vh - 50.933333vw)"\n}\n#videoList .wrapper .topTxt[data-v-73bcda1a]{background:#fceac5;height:8.8vw;overflow:hidden;color:#fa0;content:"viewport-units-buggyfill; height: 8.8vw"\n}\n#videoList .wrapper .listBox[data-v-73bcda1a]{position:absolute;left:0;top:0\n}\n#videoList .testContent[data-v-73bcda1a]{position:absolute;height:106.666667vw;left:0;right:0;top:40vw;bottom:0;color:#fff;max-width:100%;z-index:1;background:rgba(255,0,0,.4);overflow:hidden;content:"viewport-units-buggyfill; height: 106.666667vw; top: 40vw"\n}\n#videoList .testContent .childEl[data-v-73bcda1a]{white-space:nowrap;min-width:266.666667vw;background:#fa0;line-height:12vw;content:"viewport-units-buggyfill; min-width: 266.666667vw; line-height: 12vw"\n}',"",{version:3,sources:["/Users/apple/Documents/Gits/mobile2_juejinlian/src/components/indexList/videoList.vue","/Users/apple/Documents/Gits/mobile2_juejinlian/src/components/indexList/<no source>"],names:[],mappings:";AAiZA,4BACE,gBAAgB,4BACY,YAChB,gDCpZd;CD8dC;AA7ED,0CAKI,YAAY,8BACZ,sBAAsB,gBACN,eACD,UACJ,OACJ,QACC,YACI,iBACK,kBACC,gBACF,iCACiB,iFCjarC;CDubG;AAtCH,8DAkBM,mBAAmB,gBACJ,WACJ,gBACM,sECtavB;CDsbK;AArCL,kEAuBQ,qBAAqB,qBACN,qBACA,oBACD,gBACC,kBACH,uBACK,mJC9azB;CD+aO;AA9BP,yEAgCQ,mBAAmB,WACR,wBACO,6DCnb1B;CDobO;AAnCP,qCA2CI,4BAA2B,gBACX,kBACE,kFC9btB;CD0cG;AAzDH,+CAyCM,gCAA2B,qEC1bjC;CD2bK;AA1CL,6CA+CM,mBAAmB,aACP,gBACI,WACL,iDCncjB;CDocK;AAnDL,8CAqDM,kBAAkB,OACX,KACD;CACP;AAxDL,yCA2DI,kBAAkB,oBACL,OACN,QACC,SACE,SACD,WACE,eACI,UACJ,4BACkB,gBACb,mECtdpB;CD6dG;AA5EH,kDAuEM,mBAAmB,uBACF,gBACD,iBACC,8EC3dvB;CD4dK",file:"videoList.vue",sourcesContent:["\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n#videoList{\n  overflow: hidden;\n  position: relative!important;\n  width: 750px;\n  #videoChannel{\n    height: 90px;\n    box-sizing: border-box;\n    background: #fff;\n    position: fixed;\n    z-index: 20;\n    left: 0;\n    right: 0;\n    width: 750px;\n    line-height: 90px;\n    overflow-y: hidden;\n    overflow-x: auto;\n    -webkit-overflow-scrolling: touch;\n    .v-video-channelbox{\n      white-space: nowrap;\n      padding: 0 12px;\n      width: auto;\n      min-width: 4500px;\n      &>div{\n        display: inline-block;\n        font-size: 32px;\n        padding: 0 10px;\n        margin: 0 10px;\n        background:#fff;\n        height: 40px;\n        line-height: 40px;\n      }\n      &>div.active{\n        background: #b2b2b2;\n        color: #fff;\n        border-radius: 5px;\n      }\n     \n    }\n  }\n  .wrapper{\n    &.isBrowser {\n      height: calc(100vh - 382px);\n    }\n    height: calc(100vh - 282px);\n    margin-top: 90px;\n    position: relative;\n    .topTxt{\n      background: #FCEAC5;\n      height: 66px;\n      overflow: hidden;\n      color: #fa0;\n    }\n    .listBox{\n      position: absolute;\n      left: 0;\n      top: 0;\n    }\n  }\n  .testContent {\n    position: absolute;\n    height: 800px;\n    left: 0;\n    right: 0;\n    top: 300px;\n    bottom: 0;\n    color: #fff;\n    max-width: 100%;\n    z-index: 10;\n    background: rgba($color: #f00, $alpha: .4);\n    overflow: hidden;\n    .childEl {\n      white-space: nowrap;\n      min-width: 2000px;\n      background: #fa0;\n      line-height: 90px;\n    }\n  }\n}\n",null],sourceRoot:""}])}});
//# sourceMappingURL=55.06fcc1aea373b92d4b1a.js.map
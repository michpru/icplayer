(function(e){var a={validateBoolean:function(a){return"True"===a},validateInteger:function(b){var d={isValid:!1,value:NaN};if(a.isStringEmpty(b))return d;b=parseInt(b,10);if(isNaN(b))return d;d.isValid=!0;d.value=b;return d},validatePositiveInteger:function(a){a=this.validateInteger(a);return!a.isValid?a:1>a.value?{isValid:!1}:a},validateIntegerInRange:function(a,d,c){a=this.validateInteger(a);if(!a.isValid)return a;c||(c=0);return a.value<c||a.value>d?{isValid:!1}:a},validateFloat:function(b,d){var c=
{isValid:!1,value:NaN,parsedValue:NaN};if(a.isStringEmpty(b))return c;var g=parseFloat(b);if(isNaN(g))return c;d||(d=2);c.isValid=!0;c.value=g.toFixed(d);c.parsedValue=parseFloat(c.value);return c},validateFloatInRange:function(a,d,c,g){g||(g=2);a=this.validateFloat(a,g);if(!a.isValid)return a;c||(c=0);c=parseFloat(c.toFixed(g));d=parseFloat(d.toFixed(g));return a.value<c||a.value>d?{isValid:!1}:a},validateOption:function(a,d){for(var c in a)if(Object.prototype.hasOwnProperty.call(a,c)&&c===d)return a[c];
return a[a.DEFAULT]},validateColor:function(a,d){d||(d="#FFFFFF");if(!a)return{isValid:!1,color:d};var c=a.match(/#[0-9a-fA-F]+/),g;return(g=(g=!c||null===c||1!=c.length)||7!=c[0].length)?{isValid:!1,color:d}:{isValid:!0,color:a}},isStringEmpty:function(a){return void 0===a||""===a},isStringWithPrefixEmpty:function(b,d){return a.isStringEmpty(b)?!0:b===d},isArrayElementEmpty:function(b){if(!b)return!0;for(var d in b)if(Object.prototype.hasOwnProperty.call(b,d)&&!a.isStringEmpty(b[d]))return!1;return!0},
isArrayEmpty:function(b){return!b?!0:1===b.length&&a.isArrayElementEmpty(b[0])},removeDuplicatesFromArray:function(a){if(0===a.length)return[];for(var d=[],c=0;c<a.length-1;c++)a[c+1]!==a[c]&&d.push(a[c]);d.push(a[a.length-1]);return d}};e.ModelValidationUtils=a})(window);(function(e){e.DOMOperationsUtils={getOuterDimensions:function(a){a=$(a);return{border:{top:parseInt(a.css("border-top-width"),10),bottom:parseInt(a.css("border-bottom-width"),10),left:parseInt(a.css("border-left-width"),10),right:parseInt(a.css("border-right-width"),10)},margin:{top:parseInt(a.css("margin-top"),10),bottom:parseInt(a.css("margin-bottom"),10),left:parseInt(a.css("margin-left"),10),right:parseInt(a.css("margin-right"),10)},padding:{top:parseInt(a.css("padding-top"),10),bottom:parseInt(a.css("padding-bottom"),
10),left:parseInt(a.css("padding-left"),10),right:parseInt(a.css("padding-right"),10)}}},calculateOuterDistances:function(a){var b=a.border.top+a.border.bottom,b=b+(a.margin.top+a.margin.bottom),b=b+(a.padding.top+a.padding.bottom),d=a.border.left+a.border.right,d=d+(a.margin.left+a.margin.right),d=d+(a.padding.left+a.padding.right);return{vertical:b,horizontal:d}},calculateReducedSize:function(a,b){var d=DOMOperationsUtils.getOuterDimensions(b),d=DOMOperationsUtils.calculateOuterDistances(d);return{width:$(a).width()-
d.horizontal,height:$(a).height()-d.vertical}},setReducedSize:function(a,b){var d=DOMOperationsUtils.calculateReducedSize(a,b);$(b).css({width:d.width+"px",height:d.height+"px"});return d},showErrorMessage:function(a,b,d){$(a).html(b[d])},getResourceFullPath:function(a,b){return!a||!b?void 0:a.getStaticFilesPath()+b}}})(window);(function(e){function a(b){this._actualState=a._internal.STATE.START;this.correctCSS=b.correct||"";this.wrongCSS=b.wrong||"";this.showAnswersCSS=b.showAnswers||"";this.blockCSS=b.block||""}a._internal={STATE:{START:0,BLOCK:1,WORK:2,CORRECT:3,WRONG:4,SHOW_ANSWERS:5},changeStateToStart:function(){this._actualState=a._internal.STATE.START},changeStateToBlock:function(){this._actualState=a._internal.STATE.BLOCK},changeStateToWork:function(){this._actualState=a._internal.STATE.WORK},changeStateToCorrect:function(){this._actualState=
a._internal.STATE.CORRECT},changeStateToWrong:function(){this._actualState=a._internal.STATE.WRONG},changeStateToShowAnswers:function(){this._actualState=a._internal.STATE.SHOW_ANSWERS},checkStartState:function(){this.onBlock();this.setCssOnBlock();a._internal.changeStateToBlock.call(this)},showAnswersStartState:function(){this.notifyEdit();a._internal.showAnswersWorkState.call(this)},resetStartState:function(){},uncheckBlockState:function(){this.onUnblock();this.setCssOnUnblock();a._internal.changeStateToStart.call(this)},
resetBlockState:function(){a._internal.uncheckBlockState.call(this)},showAnswersBlockState:function(){a._internal.uncheckBlockState.call(this);a._internal.showAnswersStartState.call(this)},resetWorkState:function(){this.onReset();a._internal.changeStateToStart.call(this)},checkWorkState:function(){this.isCorrect()?(this.setCssOnCorrect(),this.onCorrect(),a._internal.changeStateToCorrect.call(this)):(this.setCssOnWrong(),this.onWrong(),a._internal.changeStateToWrong.call(this))},showAnswersWorkState:function(){this.onShowAnswers();
this.setCssOnShowAnswers();a._internal.changeStateToShowAnswers.call(this)},resetShowAnswersState:function(){a._internal.hideAnswersShowAnswersState.call(this);a._internal.resetWorkState.call(this)},hideAnswersShowAnswersState:function(){this.setCssOnHideAnswers();this.onHideAnswers();a._internal.changeStateToWork.call(this)},checkShowAnswersState:function(){a._internal.hideAnswersShowAnswersState.call(this);a._internal.checkWorkState.call(this)},uncheckCorrectState:function(){this.setCssOnUnCorrect();
this.onUnCorrect();a._internal.changeStateToWork.call(this)},resetCorrectState:function(){a._internal.uncheckCorrectState.call(this);a._internal.resetWorkState.call(this)},showAnswersCorrectState:function(){a._internal.uncheckCorrectState.call(this);a._internal.showAnswersWorkState.call(this)},resetWrongState:function(){a._internal.uncheckWrongState.call(this);a._internal.resetWorkState.call(this)},uncheckWrongState:function(){this.setCssOnUnWrong();this.onUnWrong();a._internal.changeStateToWork.call(this)},
showAnswersWrongState:function(){a._internal.uncheckWrongState.call(this);a._internal.showAnswersWorkState.call(this)}};a.prototype.reset=function(){switch(this._actualState){case a._internal.STATE.START:a._internal.resetStartState.call(this);break;case a._internal.STATE.BLOCK:a._internal.resetBlockState.call(this);break;case a._internal.STATE.WORK:a._internal.resetWorkState.call(this);break;case a._internal.STATE.WRONG:a._internal.resetWrongState.call(this);break;case a._internal.STATE.CORRECT:a._internal.resetCorrectState.call(this);
break;case a._internal.STATE.SHOW_ANSWERS:a._internal.resetShowAnswersState.call(this)}};a.prototype.check=function(b){switch(this._actualState){case a._internal.STATE.START:a._internal.checkStartState.call(this);break;case a._internal.STATE.BLOCK:a._internal.uncheckBlockState.call(this);break;case a._internal.STATE.WORK:a._internal.checkWorkState.call(this);break;case a._internal.STATE.WRONG:b||a._internal.uncheckWrongState.call(this);break;case a._internal.STATE.CORRECT:b||a._internal.uncheckCorrectState.call(this);
break;case a._internal.STATE.SHOW_ANSWERS:a._internal.checkShowAnswersState.call(this)}};a.prototype.showAnswers=function(){switch(this._actualState){case a._internal.STATE.START:a._internal.showAnswersStartState.call(this);break;case a._internal.STATE.BLOCK:a._internal.showAnswersBlockState.call(this);break;case a._internal.STATE.WORK:a._internal.showAnswersWorkState.call(this);break;case a._internal.STATE.WRONG:a._internal.showAnswersWrongState.call(this);break;case a._internal.STATE.CORRECT:a._internal.showAnswersCorrectState.call(this)}};
a.prototype.hideAnswers=function(){switch(this._actualState){case a._internal.STATE.SHOW_ANSWERS:a._internal.hideAnswersShowAnswersState.call(this)}};a.prototype.notifyEdit=function(){a._internal.changeStateToWork.call(this)};a.prototype.onReset=function(){throw Error("onReset is  abstract method.");};a.prototype.onBlock=function(){throw Error("onBlock is  abstract method.");};a.prototype.onUnblock=function(){throw Error("onUnblock is  abstract method.");};a.prototype.isCorrect=function(){throw Error("isCorrect is  abstract method.");
};a.prototype.onWrong=function(){throw Error("onWrong is  abstract method.");};a.prototype.onUnWrong=function(){throw Error("onUnWrong is  abstract method.");};a.prototype.onCorrect=function(){throw Error("onCorrect is  abstract method.");};a.prototype.onUnCorrect=function(){throw Error("onUnCorrect is  abstract method.");};a.prototype.onShowAnswers=function(){throw Error("onShowAnswers is  abstract method.");};a.prototype.onHideAnswers=function(){throw Error("onHideAnswers is  abstract method.");
};a.prototype.addCssClass=function(){throw Error("addCssClass is  abstract method.");};a.prototype.removeCssClass=function(){throw Error("removeCssClass is  abstract method.");};a.prototype.setCssOnCorrect=function(){this.addCssClass(this.correctCSS)};a.prototype.setCssOnWrong=function(){this.addCssClass(this.wrongCSS)};a.prototype.setCssOnShowAnswers=function(){this.addCssClass(this.showAnswersCSS)};a.prototype.setCssOnHideAnswers=function(){this.removeCssClass(this.showAnswersCSS)};a.prototype.setCssOnUnCorrect=
function(){this.removeCssClass(this.correctCSS)};a.prototype.setCssOnUnWrong=function(){this.removeCssClass(this.wrongCSS)};a.prototype.setCssOnBlock=function(){this.addCssClass(this.blockCSS)};a.prototype.setCssOnUnblock=function(){this.removeCssClass(this.blockCSS)};e.StatefullAddonObject=e.StatefullAddonObject||a})(window);(function(e){function a(a,b){this.message=a;this.name=b}function b(b,c,d,e){if(void 0===b[c])throw new a(d,e);return!0}function d(a,b,c,d){if(typeof a[b]!==d)throw new TypeError(c);return!0}function c(a,b){StatefullAddonObject.call(this,b);c._internal.validateConfiguration(a);this.addonID=a.addonID;this.objectID=a.objectID;this.source=a.source||"";this.value=a.value||"";this.showAnswersValue=a.showAnswersValue||"";this.type=a.type||"string";this._isClickable=!0;this.droppedElement;this.createView=
a.createView||c.prototype.createView;this.connectEvents=a.connectEvents||c._internal.connectEvents;this.setValue=a.setValue||c.prototype.setValue;this.setViewValue=a.setViewValue||c.prototype.setViewValue;this.helper=a.helper||c.prototype.helper;this.makeGapEmpty=a.makeGapEmpty||c.prototype.makeGapEmpty;this.fillGap=a.fillGap||c.prototype.fillGap;this.cursorAt=a.cursorAt||c.prototype.cursorAt;this.getDroppedElement=c.prototype.getDroppedElement;this.eventBus=a.eventBus;this.getSelectedItem=a.getSelectedItem;
this.$view=this.createView.call(this);this.connectEvents(this)}c.prototype=Object.create(StatefullAddonObject.prototype);c.constructor=c;c._internal={validateConfiguration:function(a){b(a,"eventBus","EventBus attribute object is undefined in configuration object","UndefinedEventBusError");d(a,"eventBus","EventBus attribute should be a event bus object","function");b(a,"getSelectedItem","Get Selected Item function is undefined in configuration object","UndefinedGetSelectedItemError");d(a,"getSelectedItem",
"Get Selected Item should be a function","function");b(a,"addonID","AddonID attribute is undefined in configuration object","UndefinedAddonIDError");d(a,"addonID","AddonID attribute should be a string type in configuration object","string");b(a,"objectID","objectID attribute is undefined in configuration object","UndefinedObjectIDError");d(a,"objectID","objectID attribute should be a string type in configuration object","string");void 0!==a.createView&&d(a,"createView","Create view have to be a function",
"function");void 0!==a.setValue&&d(a,"setValue","Set value have to be a function","function");void 0!==a.setViewValue&&d(a,"setViewValue","Set view value have to be a function","function");void 0!==a.makeGapEmpty&&d(a,"makeGapEmpty","Make gap empty have to be a function","function");void 0!==a.connectEvents&&d(a,"connectEvents","Fill gap have to be a function","function");void 0!==a.fillGap&&d(a,"fillGap","Fill gap have to be a function","function")},isSelectedItemEmpty:function(a){return"Empty"===
a.type},getSelectedItemWrapper:function(){return c._internal.validateSelectedItemData(this.getSelectedItem.call(this))},getClickHandler:function(){return function(a){this.stopPropagationAndPreventDefault(a);this.clickDispatcher(a)}.bind(this)},getDropHandler:function(){return function(a,b){this.stopPropagationAndPreventDefault(a);this.dropHandler(a,b);var c=b.helper[0];$(c).addClass("ic_sourceListItem-selected");this.droppedElement=c}.bind(this)},getStartDraggingHandler:function(){return function(a,
b){this.wrapperStartDraggingHandler(a,b)}.bind(this)},getStopDraggingHandler:function(){return function(a,b){this.wrapperStopDraggingHandler(a,b)}.bind(this)},validateSelectedItemData:function(a){if(null===a.value||null===a.item||void 0===a.type||void 0===a.value||void 0===a.item)a.type="Empty";return a},getDraggableEventsData:function(){return{source:this.addonID,item:this.source,value:this.value,type:"string"}},connectEvents:function(){this.bindClickHandler();this.bindDropHandler()}};c.prototype.setValue=
function(a){this.value=a};c.prototype.setViewValue=function(a){this.$view.html(a)};c.prototype.helper=function(){return function(){return $(this.droppedElement)}.bind(this)};c.prototype.makeGapEmpty=function(){this.notifyEdit();this.setValue.call(this,"");this.setViewValue.call(this,"");this.setSource.call(this,"");this.destroyDraggableProperty()};c.prototype.createView=function(){var a=$("<span></span>");a.attr("id",this.objectID);a.addClass("ui-draggable");a.addClass("ui-widget-content");return a};
c.prototype.fillGap=function(a){this.notifyEdit();this.setValue.call(this,a.value);this.setViewValue.call(this,a.value);this.setSource.call(this,a.item);this.sendItemConsumedEvent();this.bindDraggableHandler()};c.prototype.cursorAt=function(){return{}};c.prototype.getValue=function(){return this.value};c.prototype.getView=function(){return this.$view};c.prototype.getAddonID=function(){return this.addonID};c.prototype.getObjectID=function(){return this.objectID};c.prototype.getSource=function(){return this.source};
c.prototype.getDroppedElement=function(){return $("<div>").append($(this.droppedElement).clone()).html()};c.prototype.setDroppedElement=function(a){this.droppedElement=a};c.prototype.setSource=function(a){this.source=a};c.prototype.addCssClass=function(a){this.$view.addClass(a)};c.prototype.removeCssClass=function(a){this.$view.removeClass(a)};c.prototype.bindClickHandler=function(){this.$view.click(c._internal.getClickHandler.call(this))};c.prototype.bindDropHandler=function(){this.$view.droppable({drop:c._internal.getDropHandler.call(this)})};
c.prototype.bindDraggableHandler=function(){this.$view.draggable({revert:!1,helper:this.helper(),cursorAt:this.cursorAt(),start:c._internal.getStartDraggingHandler.call(this),stop:c._internal.getStopDraggingHandler.call(this)})};c.prototype.clickHandler=function(){var a=c._internal.getSelectedItemWrapper.call(this);if(!c._internal.isSelectedItemEmpty(a)){var b=a.item.substr(0,a.item.lastIndexOf("-")),b=$("#_icplayer").find("#"+b).children().filter(function(){return $(this).text()==a.value}),b=$(b[0]);
b.css("display","inline-block");b.addClass("ic_sourceListItem-selected");this.droppedElement=$("<div>").append(b.clone()).html()}this.sendItemReturnedEvent();c._internal.isSelectedItemEmpty(a)?this.makeGapEmpty.call(this):this.fillGap.call(this,a)};c.prototype.clickDispatcher=function(a){this._isClickable?this.clickHandler.call(this,a):this._isClickable=!0};c.prototype.dropHandler=function(a,b){this.setDroppedElement(b.draggable[0]);this.sendItemReturnedEvent();this.fillGap.call(this,c._internal.getSelectedItemWrapper.call(this))};
c.prototype.startDraggingHandler=function(a,b){this.setViewValue.call(this,"");this.sendItemReturnedEvent();this.sendItemDraggedEvent();b.helper.zIndex(100);this.$view.removeClass("gapFilled")};c.prototype.wrapperStartDraggingHandler=function(a,b){this._isClickable=!1;this.startDraggingHandler(a,b)};c.prototype.wrapperStopDraggingHandler=function(a,b){this._isClickable=!0;this.stopDraggingHandler(a,b)};c.prototype.stopDraggingHandler=function(a,b){b.helper[0].remove();this.sendItemStoppedEvent();
this.makeGapEmpty.call(this)};c.prototype.destroyDraggableProperty=function(){this.$view.draggable("destroy")};c.prototype.stopPropagationAndPreventDefault=function(a){a.stopPropagation();a.preventDefault()};c.prototype.sendItemDraggedEvent=function(){this.sendEvent("itemDragged",c._internal.getDraggableEventsData.call(this))};c.prototype.sendItemStoppedEvent=function(){this.sendEvent("itemStopped",c._internal.getDraggableEventsData.call(this))};c.prototype.sendItemReturnedEvent=function(){this.sendEvent("ItemReturned",
c._internal.getDraggableEventsData.call(this))};c.prototype.sendItemConsumedEvent=function(){this.sendEvent("ItemConsumed",c._internal.getDraggableEventsData.call(this))};c.prototype.sendEvent=function(a,b){this.eventBus.sendEvent(a,b)};c.prototype.lock=function(){this.$view.unbind("click");this.$view.draggable("disable");this.$view.droppable("disable")};c.prototype.unlock=function(){this.bindClickHandler();this.$view.draggable("enable");this.$view.droppable("enable")};c.prototype.onReset=function(){this.setValue.call(this,
"");this.setViewValue.call(this,"");this.setSource.call(this,"");this.destroyDraggableProperty()};c.prototype.onBlock=function(){this.lock()};c.prototype.onUnblock=function(){this.unlock()};c.prototype.isCorrect=function(){return this.value===this.showAnswersValue};c.prototype.onWrong=function(){this.lock()};c.prototype.onUnWrong=function(){this.unlock()};c.prototype.onCorrect=function(){this.lock()};c.prototype.onUnCorrect=function(){this.unlock()};c.prototype.onShowAnswers=function(){this.lock();
this.setViewValue(this.showAnswersValue)};c.prototype.onHideAnswers=function(){this.unlock();this.setViewValue(this.value)};c.prototype.setState=function(a,b,c){this.setValue.call(this,a);this.setViewValue.call(this,a);this.setSource.call(this,b);this.droppedElement=c;this.bindDraggableHandler();this.notifyEdit()};e.DraggableDroppableObject=e.DraggableDroppableObject||c})(window);(function(e){e.Serialization={serialize:function(a){if(a){var b="";$.each(a,function(a,c){var e=typeof c;if("object"===e&&$.isArray(c)){var e="array",f="";$.each(c,function(a){f+=this+"-"+typeof c[a]+","});c=f=f.slice(0,-1)}b+="["+a+":"+e+":"+c+"]"});return b}},deserialize:function(a){if(a){var b={};if(a=a.match(/[\w]+:[\w-]+:[\w,.\- ]+/g)){for(var d=0;d<a.length;d++){var c=a[d].split(":");b[c[0]]=this.convert(c[2],c[1])}return b}}},convert:function(a,b){if(a&&b)switch(b){case "string":return a;case "number":return this.isInteger(a)?
parseInt(a):parseFloat(a);case "boolean":return"true"==a;case "array":return this.convertArray(a);default:return"This type of value is unrecognized."}},convertArray:function(a){for(var a=a.split(","),b=[],d=0;d<a.length;d++){var c=a[d].split("-");b.push(this.convert(c[0].trim(),c[1]))}return b},isInteger:function(a){return 0===a%1&&null!=a}}})(window);(function(e){e.Watermark={defaultOptions:{size:100,opacity:1,color:"#000000"},validateOptions:function(a){if(!a)return $.extend({},this.defaultOptions);var b=$.extend({},a);ModelValidationUtils.validatePositiveInteger(a.size).isValid||(b.size=this.defaultOptions.size);ModelValidationUtils.validateFloatInRange(a.opacity,1,0,2).isValid||(b.opacity=this.defaultOptions.opacity);ModelValidationUtils.validateColor(a.color,this.defaultOptions.color).isValid||(b.color=this.defaultOptions.color);return b},
draw:function(a,b){var d=$(a),c=$(document.createElement("canvas"));d.html(c);b=this.validateOptions(b);c.attr({width:b.size,height:b.size});c.rotateCanvas({x:b.size/2,y:b.size/2,angle:90}).drawArc({strokeWidth:1,strokeStyle:b.color,fillStyle:b.color,x:b.size/2,y:b.size/2,radius:b.size/2-1,opacity:b.opacity}).drawLine({strokeWidth:1,strokeStyle:"#FFF",fillStyle:"#FFF",rounded:!0,x1:b.size/2,y1:0.17*b.size,x2:b.size-0.2*b.size,y2:b.size-0.33*b.size,x3:0.2*b.size,y3:b.size-0.33*b.size,x4:b.size/2,y4:0.17*
b.size,opacity:b.opacity})}}})(window);(function(e){e.Helpers={splitLines:function(a){return a.split(/[\n\r]+/)},alertErrorMessage:function(a,b){var d=b+"\n\n";a.name&&(d+="["+a.name+"] ");d+=a.message?a.message:a;alert(d)}}})(window);
(function(e){if(!e.fn.style){var a=function(a){return a.replace(/[-[\]{}()*+?.,\\^$|#\s]/g,"\\$&")};CSSStyleDeclaration.prototype.getPropertyValue||(CSSStyleDeclaration.prototype.getPropertyValue=function(a){return this.getAttribute(a)},CSSStyleDeclaration.prototype.setProperty=function(b,d,c){this.setAttribute(b,d);c="undefined"!=typeof c?c:"";if(""!=c){var e=RegExp(a(b)+"\\s*:\\s*"+a(d)+"(\\s*;)?","gmi");this.cssText=this.cssText.replace(e,b+": "+d+" !"+c+";")}},CSSStyleDeclaration.prototype.removeProperty=
function(a){return this.removeAttribute(a)},CSSStyleDeclaration.prototype.getPropertyPriority=function(b){return RegExp(a(b)+"\\s*:\\s*[^\\s]*\\s*!important(\\s*;)?","gmi").test(this.cssText)?"important":""});e.fn.style=function(a,d,c){if(typeof this.get(0)=="undefined")return this;var e=this.get(0).style;if(typeof a!="undefined"){if(typeof d!="undefined"){e.setProperty(a,d,typeof c!="undefined"?c:"");return this}return e.getPropertyValue(a)}return e}}})(jQuery);(function(e){var a={};e.MobileUtils={isMobileUserAgent:function(a){return void 0===a||!a?!1:a.match(/Android/i)||a.match(/BlackBerry/i)||a.match(/iPhone|iPad|iPod/i)||a.match(/IEMobile/i)||a.match(/Opera Mini/i)?!0:!1},isAndroidWebBrowser:function(a){return void 0===a||!a?!1:a.indexOf("Mozilla/5.0">-1&&-1<a.indexOf("Android ")&&-1<a.indexOf("AppleWebKit"))&&!(-1<a.indexOf("Chrome"))},getAndroidVersion:function(a){if(void 0===a||!a)return"";var a=a.toString(),d=a.indexOf("Android")+7;if(-1===d)return"";
var c=a.indexOf(";",d);return a.substring(d,c).trim()},isSafariMobile:function(a){return void 0===a||!a?!1:a.match(/iPhone|iPad|iPod/i)?!0:!1},isWindowsMobile:function(a){return void 0===a||!a?!1:a.msPointerEnabled?!0:!1},isEventSupported:function(b){var d={select:"input",change:"input",submit:"form",reset:"form",error:"img",load:"img",abort:"img",unload:"win",resize:"win"},c=b.replace(/^on/,"");if(a[c])return a[c];var d="win"==d[c]?e:document.createElement(d[c]||"div"),b="on"+c,g=b in d;g||(d.setAttribute(b,
"return;"),g="function"==typeof d[b]);return a[c]=g}}})(window);(function(e){e.Commands={dispatch:function(a,b,d,c){var e,f;for(f in a)Object.prototype.hasOwnProperty.call(a,f)&&f.toLowerCase()===b&&a[f]&&(e=a[f].call(c,d));return e}};e.CommandsQueueFactory={create:function(a){return new e.CommandsQueue(a)}};e.CommandsQueueTask=function(a,b){this.name=a;this.params=b};e.CommandsQueue=function(a){this.module=a;this.queue=[]};e.CommandsQueue.prototype.addTask=function(a,b){var d=new e.CommandsQueueTask(a,b);this.queue.push(d)};e.CommandsQueue.prototype.getTask=
function(){return this.isQueueEmpty()?null:this.queue.splice(0,1)[0]};e.CommandsQueue.prototype.getAllTasks=function(){return this.queue};e.CommandsQueue.prototype.executeTask=function(){var a=this.getTask();a&&this.module.executeCommand(a.name.toLowerCase(),a.params)};e.CommandsQueue.prototype.executeAllTasks=function(){for(;!this.isQueueEmpty();)this.executeTask()};e.CommandsQueue.prototype.getTasksCount=function(){return this.queue.length};e.CommandsQueue.prototype.isQueueEmpty=function(){return 0===
this.queue.length}})(window);(function(e){e.ImageViewer={validateSound:function(a){var b=[];if(a&&$.isArray(a))for(var d=0;d<a.length;d++){var c=ModelValidationUtils.isStringWithPrefixEmpty(a[d]["MP3 sound"],"/file/"),e=ModelValidationUtils.isStringWithPrefixEmpty(a[d]["AAC sound"],"/file/"),f=ModelValidationUtils.isStringWithPrefixEmpty(a[d]["OGG sound"],"/file/");b.push({AAC:e?"":a[d]["AAC sound"],OGG:f?"":a[d]["OGG sound"],MP3:c?"":a[d]["MP3 sound"],isEmpty:c&&e&&f})}return{sounds:b}},loadSounds:function(a,b){var d=[];if(!buzz.isSupported()||
!a||!b)return d;for(var c=0;c<b;c++)c>a.length-1||a[c].isEmpty?d[c]=null:(d[c]=""!==a[c].MP3&&buzz.isMP3Supported()?new buzz.sound(a[c].MP3):""!==a[c].OGG&&buzz.isOGGSupported()?new buzz.sound(a[c].OGG):new buzz.sound(a[c].AAC),d[c].load());return d},convertFramesList:function(a,b,d){if(ModelValidationUtils.isStringEmpty(a))return{isError:!0,errorCode:"FL01"};var c=a.match(/[0-9a-zA-Z,-]+/);if(null===c||a.length!==c[0].length)return{isError:!0,errorCode:"FL02"};for(var c=[],a=a.split(","),e=0;e<a.length;e++){if(ModelValidationUtils.isStringEmpty(a[e]))return{isError:!0,
errorCode:"FL04"};if(-1!==a[e].search("-")){var f=a[e].split("-")[1],h=ModelValidationUtils.validateIntegerInRange(f,d,b);if(!h.isValid)return{isError:!0,errorCode:"FL05"};var i=a[e].split("-")[0],f=ModelValidationUtils.validateIntegerInRange(i,f.value,b);if(!f.isValid||f.value>h.value)return{isError:!0,errorCode:"FL05"};for(f=f.value;f<=h.value;f++)c.push(f)}else{h=ModelValidationUtils.validateIntegerInRange(a[e],d,b);if(!h.isValid)return{isError:!0,errorCode:"FL03"};c.push(h.value)}}c.sort();c=
ModelValidationUtils.removeDuplicatesFromArray(c);return{isError:!1,list:c}}}})(window);(function(e){e.StringUtils={replaceAll:function(a,b,d){b=b.replace(/([.?*+^$[\]\\(){}|-])/g,"\\$1");return a.replace(RegExp(b,"g"),d)},startsWith:function(a,b){return a.match("^"+b)==b},endsWith:function(a,b){return-1!==a.indexOf(b,a.length-b.length)}}})(window);(function(e){e.TextParserProxy=function(a){if(null==a)return null;this.parser=a};e.TextParserProxy.prototype.parse=function(a){a=this.parser.parse(a);return StringUtils.replaceAll(a,"href='#'",'href="javascript:void(0)"')};e.TextParserProxy.prototype.connectLinks=function(a){return this.parser.connectLinks(a)};e.TextParserProxy.prototype.parseGaps=function(a,b){"undefined"==typeof b&&(b={isCaseSensitive:!1});return this.parser.parseGaps(a,b)}})(window);(function(e){e.Internationalization={EASTERN_ARABIC:"ea",WESTERN_ARABIC:"wa",PERSO_ARABIC:"pa",getNumericals:function(a){var b={};b[e.Internationalization.WESTERN_ARABIC]={"0":"0",1:"1",2:"2",3:"3",4:"4",5:"5",6:"6",7:"7",8:"8",9:"9"};b[e.Internationalization.EASTERN_ARABIC]={"0":"&#x0660",1:"&#x0661",2:"&#x0662",3:"&#x0663",4:"&#x0664",5:"&#x0665",6:"&#x0666",7:"&#x0667",8:"&#x0668",9:"&#x0669"};b[e.Internationalization.PERSO_ARABIC]={"0":"&#x06F0",1:"&#x06F1",2:"&#x06F2",3:"&#x06F3",4:"&#x06F4",
5:"&#x06F5",6:"&#x06F6",7:"&#x06F7",8:"&#x06F8",9:"&#x06F9"};return a in b?b[a]:b[e.Internationalization.WESTERN_ARABIC]},translate:function(a,b){for(var d=a.toString(),c="",g=e.Internationalization.getNumericals(b),f=0;f<d.length;f++)c+=g[+d.charAt(f)];return c}}})(window);(function(e){e.LoadedPromise=function(a,b){var d=[];$.each(b,function(b){var e=new $.Deferred,f=e.promise();a[b+"LoadedDeferred"]=e;a[b+"Loaded"]=f;d.push(f);a.getLoadedPromise=function(){var a=new $.Deferred,b=function(c,d){d==c.length?a.resolve():$.when(c[d]).then(function(){b(c,d+1)})};b(d,0);return a.promise()}})}})(window);(function(e){var a=function(){};a._internals={};a._internals.clicks=[];a._internals.callbacks=[];a._internals.getUserAgent=function(){return navigator.userAgent};a._internals.getCurrentTime=function(){return(new Date).getTime()};a._internals.getTargetCallbacks=function(b){return a._internals.callbacks.filter(function(a){return a.target==b}).map(function(a){return a.callback})};a._internals.removeTargetCallbacks=function(b){a._internals.callbacks=a._internals.callbacks.filter(function(a){return a.target!=
b})};a._internals.addCallback=function(b,d){a._internals.callbacks.push({target:b,callback:d})};a._internals.getLastTargetClick=function(b){for(var d=a._internals.clicks,c=d.length-1;0<=c;c-=1)if(d[c].target==b)return d[c];return null};a._internals.removeTargetClicks=function(b){a._internals.clicks=a._internals.clicks.filter(function(a){return a.target!=b})};a._internals.addClick=function(b,d){a._internals.removeTargetClicks(b);a._internals.clicks.push({target:b,time:d})};a._internals.doubleTapHandler=
function(b){var d=this,c=a._internals.getCurrentTime(),e=a._internals.getLastTargetClick(b.target);null!==e&&e.time+300>=c?(a._internals.removeTargetClicks(b.target),a._internals.getTargetCallbacks(d).forEach(function(a){a.call(d,b)})):a._internals.addClick(b.target,c)};a._internals.getStartEvent=function(b){var d=b||a._internals.getUserAgent(),b=/chrome/i.exec(d),d=/android/i.exec(d);return"ontouchstart"in e&&(!b||d)?"touchstart":"mousedown"};a.on=function(b,d){var c=b[0],e=a._internals.getStartEvent();
a._internals.addCallback(c,d);b.on(e,a._internals.doubleTapHandler)};a.off=function(b){var d=b[0],c=a._internals.getStartEvent();b.off(c,a._internals.doubleTapHandler);a._internals.removeTargetCallbacks(d);a._internals.removeTargetClicks(d)};e.EventsUtils={};e.EventsUtils.DoubleTap=a})(window);
/**
 * Player Addons Commons library
 * @version 0.378
 * Components:
 * - Model Validation Utils
 * - DOM Operations
 * - Statefull Addon Object
 * - Draggable Droppable Object
 * - States Serialization
 * - Watermark
 * - Commands
 * - Image Viewer (partial)
 * - Helpers
 * - Mobile Utils
 * - String Utils
 * - Internationalization
 * - Text Parser Proxy
 * - Loaded Promise
 * - Events Utils
 */
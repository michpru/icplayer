(function(g){var a={validateBoolean:function(b){return"True"===b},validateInteger:function(b){var a={isValid:!1,value:NaN};if(!b)return a;b=parseInt(b,10);if(isNaN(b))return a;a.isValid=!0;a.value=b;return a},validatePositiveInteger:function(b){b=this.validateInteger(b);return!b.isValid?b:1>b.value?{isValid:!1}:b},validateIntegerInRange:function(b,a,c){b=this.validateInteger(b);if(!b.isValid)return b;c||(c=0);return b.value<c||b.value>a?{isValid:!1}:b},validateFloat:function(a,d){var c={isValid:!1,
value:NaN};if(!a)return c;var e=parseFloat(a);if(isNaN(e))return c;d||(d=2);c.isValid=!0;c.value=e.toFixed(d);return c},validateFloatInRange:function(a,d,c,e){e||(e=2);a=this.validateFloat(a,e);if(!a.isValid)return a;c||(c=0);c=c.toFixed(e);d=d.toFixed(e);return a.value<c||a.value>d?{isValid:!1}:a},validateOption:function(a,d){for(var c in a)if(Object.prototype.hasOwnProperty.call(a,c)&&c===d)return a[c];return a[a.DEFAULT]},validateColor:function(a,d){d||(d="#FFFFFF");if(!a)return{isValid:!1,color:d};
var c=a.match(/#[0-9a-fA-F]+/),e;return(e=(e=!c||null===c||1!=c.length)||7!=c[0].length)?{isValid:!1,color:d}:{isValid:!0,color:a}},isStringEmpty:function(a){return void 0===a||""===a},isStringWithPrefixEmpty:function(b,d){return a.isStringEmpty(b)?!0:b===d},isArrayElementEmpty:function(b){if(!b)return!0;for(var d in b)if(Object.prototype.hasOwnProperty.call(b,d)&&!a.isStringEmpty(b[d]))return!1;return!0},isArrayEmpty:function(b){return!b?!0:1===b.length&&a.isArrayElementEmpty(b[0])},removeDuplicatesFromArray:function(a){if(0===
a.length)return[];for(var d=[],c=0;c<a.length-1;c++)a[c+1]!==a[c]&&d.push(a[c]);d.push(a[a.length-1]);return d}};g.ModelValidationUtils=a})(window);(function(g){g.LoadingScreen={screens:[],create:function(a){if(a){var a=$(a),b=a.width(),d=a.height(),c=Math.floor(1E3*Math.random())+"-"+Math.floor(1E3*Math.random()),e=$(document.createElement("img"));e.attr({src:"http://www.lorepo.com/media/images/loading.gif",alt:"Loading...",id:c,position:"absolute",display:"none",border:0,padding:0,margin:0});a.append(e);e.show();e.css({top:(d-e.height())/2+"px",left:(b-e.width())/2+"px"}).hide();this.screens.push({id:c,$element:e,counter:0});return c}},getScreen:function(a){for(var b=
0,d=this.screens.length;b<d;b++)if(this.screens[b].id===a)return b;return-1},show:function(a){a=this.getScreen(a);-1!==a&&(this.screens[a].counter++,this.screens[a].$element.show())},hide:function(a){a=this.getScreen(a);-1!==a&&(0<this.screens[a].counter&&this.screens[a].counter--,0===this.screens[a].counter&&this.screens[a].$element.hide())}}})(window);(function(g){g.DOMOperationsUtils={getOuterDimensions:function(a){a=$(a);return{border:{top:parseInt(a.css("border-top-width"),10),bottom:parseInt(a.css("border-bottom-width"),10),left:parseInt(a.css("border-left-width"),10),right:parseInt(a.css("border-right-width"),10)},margin:{top:parseInt(a.css("margin-top"),10),bottom:parseInt(a.css("margin-bottom"),10),left:parseInt(a.css("margin-left"),10),right:parseInt(a.css("margin-right"),10)},padding:{top:parseInt(a.css("padding-top"),10),bottom:parseInt(a.css("padding-bottom"),
10),left:parseInt(a.css("padding-left"),10),right:parseInt(a.css("padding-right"),10)}}},calculateOuterDistances:function(a){var b=a.border.top+a.border.bottom,b=b+(a.margin.top+a.margin.bottom),b=b+(a.padding.top+a.padding.bottom),d=a.border.left+a.border.right,d=d+(a.margin.left+a.margin.right),d=d+(a.padding.left+a.padding.right);return{vertical:b,horizontal:d}},calculateReducedSize:function(a,b){var d=DOMOperationsUtils.getOuterDimensions(b),d=DOMOperationsUtils.calculateOuterDistances(d);return{width:$(a).width()-
d.horizontal,height:$(a).height()-d.vertical}},setReducedSize:function(a,b){var d=DOMOperationsUtils.calculateReducedSize(a,b);$(b).css({width:d.width+"px",height:d.height+"px"});return d},showErrorMessage:function(a,b,d){$(a).html(b[d])},getResourceFullPath:function(a,b){return!a||!b?void 0:a.getStaticFilesPath()+b}}})(window);(function(g){g.Serialization={serialize:function(a){if(a){var b="";$.each(a,function(a,c){var e=typeof c;if("object"===e&&$.isArray(c)){var e="array",f="";$.each(c,function(a){f+=this+"-"+typeof c[a]+","});c=f=f.slice(0,-1)}b+="["+a+":"+e+":"+c+"]"});return b}},deserialize:function(a){if(a){var b={};if(a=a.match(/[\w]+:[\w-]+:[\w,.\- ]+/g)){for(var d=0;d<a.length;d++){var c=a[d].split(":");b[c[0]]=this.convert(c[2],c[1])}return b}}},convert:function(a,b){if(a&&b)switch(b){case "string":return a;case "number":return this.isInteger(a)?
parseInt(a):parseFloat(a);case "boolean":return"true"==a;case "array":return this.convertArray(a);default:return"This type of value is unrecognized."}},convertArray:function(a){for(var a=a.split(","),b=[],d=0;d<a.length;d++){var c=a[d].split("-");b.push(this.convert(c[0].trim(),c[1]))}return b},isInteger:function(a){return 0===a%1&&null!=a}}})(window);(function(g){g.Watermark={defaultOptions:{size:100,opacity:1,color:"#000000"},validateOptions:function(a){if(!a)return $.extend({},this.defaultOptions);var b=$.extend({},a);ModelValidationUtils.validatePositiveInteger(a.size).isValid||(b.size=this.defaultOptions.size);ModelValidationUtils.validateFloatInRange(a.opacity,1,0,2).isValid||(b.opacity=this.defaultOptions.opacity);ModelValidationUtils.validateColor(a.color,this.defaultOptions.color).isValid||(b.color=this.defaultOptions.color);return b},
draw:function(a,b){var d=$(a),c=$(document.createElement("canvas"));d.html(c);b=this.validateOptions(b);c.attr({width:b.size,height:b.size});c.rotateCanvas({x:b.size/2,y:b.size/2,angle:90}).drawArc({strokeWidth:1,strokeStyle:b.color,fillStyle:b.color,x:b.size/2,y:b.size/2,radius:b.size/2-1,opacity:b.opacity}).drawLine({strokeWidth:1,strokeStyle:"#FFF",fillStyle:"#FFF",rounded:!0,x1:b.size/2,y1:0.17*b.size,x2:b.size-0.2*b.size,y2:b.size-0.33*b.size,x3:0.2*b.size,y3:b.size-0.33*b.size,x4:b.size/2,y4:0.17*
b.size,opacity:b.opacity})}}})(window);(function(g){g.Helpers={splitLines:function(a){return a.split(/[\n\r]+/)}}})(window);(function(g){g.Commands={dispatch:function(a,b,d,c){var e,f;for(f in a)Object.prototype.hasOwnProperty.call(a,f)&&f.toLowerCase()===b&&a[f]&&(e=a[f].call(c,d));return e}}})(window);(function(g){g.ImageViewer={validateSound:function(a){var b=[];if(a&&$.isArray(a))for(var d=0;d<a.length;d++){var c=ModelValidationUtils.isStringWithPrefixEmpty(a[d]["MP3 sound"],"/file/"),e=ModelValidationUtils.isStringWithPrefixEmpty(a[d]["AAC sound"],"/file/"),f=ModelValidationUtils.isStringWithPrefixEmpty(a[d]["OGG sound"],"/file/");b.push({AAC:e?"":a[d]["AAC sound"],OGG:f?"":a[d]["OGG sound"],MP3:c?"":a[d]["MP3 sound"],isEmpty:c&&e&&f})}return{sounds:b}},loadSounds:function(a,b){var d=[];if(!buzz.isSupported()||
!a||!b)return d;for(var c=0;c<b;c++)c>a.length-1||a[c].isEmpty?d[c]=null:(d[c]=""!==a[c].MP3&&buzz.isMP3Supported()?new buzz.sound(a[c].MP3):""!==a[c].OGG&&buzz.isOGGSupported()?new buzz.sound(a[c].OGG):new buzz.sound(a[c].AAC),d[c].load());return d},convertFramesList:function(a,b,d){if(ModelValidationUtils.isStringEmpty(a))return{isError:!0,errorCode:"FL01"};var c=a.match(/[0-9a-zA-Z,-]+/);if(null===c||a.length!==c[0].length)return{isError:!0,errorCode:"FL02"};for(var c=[],a=a.split(","),e=0;e<a.length;e++){if(ModelValidationUtils.isStringEmpty(a[e]))return{isError:!0,
errorCode:"FL04"};if(-1!==a[e].search("-")){var f=a[e].split("-")[1],g=ModelValidationUtils.validateIntegerInRange(f,d,b);if(!g.isValid)return{isError:!0,errorCode:"FL05"};var h=a[e].split("-")[0],f=ModelValidationUtils.validateIntegerInRange(h,f.value,b);if(!f.isValid||f.value>g.value)return{isError:!0,errorCode:"FL05"};for(f=f.value;f<=g.value;f++)c.push(f)}else{g=ModelValidationUtils.validateIntegerInRange(a[e],d,b);if(!g.isValid)return{isError:!0,errorCode:"FL03"};c.push(g.value)}}c.sort();c=
ModelValidationUtils.removeDuplicatesFromArray(c);return{isError:!1,list:c}}}})(window);
/**
 * Lorepo Addons Commons library
 * @version 1.6.4
 * Components:
 * - Model Validation Utils
 * - Loading Screen
 * - DOM Operations
 * - States Serialization
 * - Watermark
 * - Commands
 * - Image Viewer (partial)
 * - Helpers
 */
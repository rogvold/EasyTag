var CopinyWidget = {};

CopinyWidget.gId = function(e) {
    return document.getElementById(e)
}

CopinyWidget.hasClass = function(e, c) {
    var eC = e.className;
    return (eC.length > 0 && (eC == c ||
        new RegExp("(^|\\s)" + c + "(\\s|$)").test(eC)));
};

CopinyWidget.addClass = function(e, c) {
  if (!this.hasClass(e, c)) {
      e.className += ' '+c;
  }
}

CopinyWidget.removeClass = function(e, c) {
    e.className = e.className.replace(new RegExp("(^|\\s+)" + c + "(\\s+|$)"), '');
};

CopinyWidget.getCookie = function(cn) {
    var c=document.cookie;
    if (c.length>0) {
        var cs=c.indexOf(cn + "=");
        if (cs!=-1) {
            cs=cs + cn.length+1;
            var ce=c.indexOf(";",cs);
            if (ce==-1) ce=c.length;
            return c.substring(cs,ce);
        }
    }
    return false;
}

function initCopinyWidget(options)
{
    options.community = parseInt(options.community);
    if (options.community == 0) {
        return false;
    }
    CopinyWidget.info = {};
    CopinyWidget.community = options.community;
    CopinyWidget.text    = options.text;
    CopinyWidget.hostcommunity    = options.hostcommunity?options.hostcommunity:0;
    CopinyWidget.newWindow    = options.newwindow?options.newwindow:0;
    CopinyWidget.cache    = options.cache?options.cache:0;
    CopinyWidget.ssl     = (options.use_ssl || (document.location.protocol == 'https:'));
    CopinyWidget.proto   = CopinyWidget.ssl?'https':'http';
    if(options.testmode) {
        CopinyWidget.mainhost=(options.testmode==1)?'idea.test':'crowd2community.com';
    } else {
        CopinyWidget.mainhost= 'copiny.com';
    }
    CopinyWidget.host = 'widget.'+CopinyWidget.mainhost;
    CopinyWidget.tabPosition = options.position?options.position:'right';
    CopinyWidget.title   = options.title?options.title:'Оставить отзыв';
    CopinyWidget.color   = options.color?options.color:'#000000';
    CopinyWidget.tabBorder = options.border?options.border:'#ffffff';
    CopinyWidget.type    = options.type?options.type:'question';
    if (options.integration) {
        CopinyWidget.integration = options.integration;
    }
    if(CopinyWidget.cache!=0)
        var css   = "a.widget-tab {top: 25%; right:0; position:fixed; background: "+CopinyWidget.color+" 6px 11px no-repeat; color: #ffffff; display: block; width:27px; border: solid 2px #ffffff;  z-index:100000; box-shadow: 0px 0px 5px #333;}\n\
        a.widget-tab:hover {width: 32px} \n";
    else
        var css   = "a.widget-tab {top: 25%; right:0; position:fixed; background: url(\""+CopinyWidget.proto+"://"+CopinyWidget.host+"/static/images/text.png\") "+CopinyWidget.color+" 6px 11px no-repeat; color: #ffffff; display: block; height: 145px; width:27px; border: solid 2px #ffffff; z-index:100000; box-shadow: 0px 0px 5px #333;}\n\
                    a.widget-tab:hover {width: 32px} \n";
     if (window.operamini) {
         css = css+"noindex:-o-prefocus, a.widget-tab { position: absolute; }\n";
     }
     css = css+"\
                a.widget-tab img{padding:3px;margin-left:4px;border:0;background: transparent;} \n\
     * html a.widget-tab {position:absolute;  }\n\
        .widget-img{width:16px;}\n\
     a.widget-tab-left{left:0; border-left:none; border-radius: 0 3px 3px 0;  -moz-border-radius: 0 3px 3px 0; -o-border-radius: 0 3px 3px 0; -webkit-border-radius: 0 3px 3px 0;} \n\
     a.widget-tab-left:hover{width: 32px; background-position: 11px 11px; } \n\
     a.widget-tab-left:hover img{margin-left:9px;} \n\
     a.widget-tab-right{border-right:none; border-radius: 3px 0 0 3px;  -moz-border-radius: 3px 0 0 3px; -o-border-radius: 3px 0 0 3px; -webkit-border-radius: 3px 0 0 3px;} \n\
    #copiny-wrapper {width: 100%;\n  height: 100%;\n  top: 0;\n  left: 0;\n  z-index: 1000000;\n  position: absolute;}\n\
    #copiny-overlay {text-align: center; position: absolute; left: 0;top: 0;\n z-index: 1;\n margin: 0;\n padding: 0;\n height: 100%;\n width: 100%;\n background-color: #000;\n filter:progid:DXImageTransform.Microsoft.Alpha(opacity=40);\n -moz-opacity: 0.4;\n -khtml-opacity: 0.4;\n opacity: 0.4;\n}\n* html #copiny-overlay {\n position: absolute;\n height: expression(document.body.scrollHeight > document.body.offsetHeight ? document.body.scrollHeight : document.body.offsetHeight + \"px\");\n}\n\
    #copiny-popup-container {\n position:relative; z-index:2; margin: 0 auto; padding:0; width:643px; border:none;}\n\
    #copiny-popup-iframe {background: transparent; position: static; margin: 0; padding:0; border: none;} \
    #copiny-popup-iframe.loading {background: transparent url(\""+CopinyWidget.proto+"://"+CopinyWidget.host+"/static/images/loading.png\") no-repeat 409px 9px; } \
    .widget_on embed, .widget_on select, .widget_on object, .widget_on iframe.youtube-player { visibility: hidden; }\n\
    #copiny-popup-container .copiny-popup-close {position: absolute; width:26px; height:28px; background:url("+CopinyWidget.proto+"://static."+CopinyWidget.mainhost+"/images/close-button.png) 0 0 no-repeat; cursor:pointer; top: 20px; left: 573px; }\n\
    #copiny-popup-container .copiny-popup-close:active	{background-position:0 -62px;}\n\
    #copiny-popup-container .copiny-popup-close:hover	{background-position:0 -30px;}";
    var head  = document.getElementsByTagName('head')[0];
    var style = document.createElement('style');
    style.type = 'text/css';
    if(style.styleSheet) {
      style.styleSheet.cssText = css;
    } else {
      var rules = document.createTextNode(css);
      style.appendChild(rules);
    }
    head.appendChild(style);

    var wrap = document.createElement('div');
    wrap.id = 'copiny-wrapper';
    wrap.style.display = 'none';
    wrap.innerHTML = '<div id="copiny-popup-container">\n\n\
    <a href="#" class="copiny-popup-close" onclick="CopinyWidget.hide(); return false;"></a>\
    <iframe id="copiny-popup-iframe" class="loading" allowtransparency="true" src="'+CopinyWidget.blankUrl()+'" scrolling="no" frameborder="0" style="width: 640px; height: 600px; background-color: transparent" ></iframe></div>\n\
    <div id="copiny-overlay"></div>';
    document.body.insertBefore(wrap, document.body.getElementsByTagName('*')[0]);
    CopinyWidget.initJSON();
}

CopinyWidget.showTab = function() {
    var tab = document.createElement('a');
    tab.className = "widget-tab widget-tab-"+this.tabPosition;
    tab.id = "copiny-widget-tab";
    tab.href = "#";
    tab.title = this.title;
    if((CopinyWidget.newWindow==0)||(CopinyWidget.hostcommunity==0))
        tab.onclick = function() {CopinyWidget.show();return false;};
    else if((CopinyWidget.newWindow==1)&(CopinyWidget.hostcommunity!=0)) {
        tab.href =CopinyWidget.hostcommunity;
        tab.target ='_blank';
    }
    /*tab.style.backgroundImage = 'url('+this.proto+'://'+this.host+'/static/images/text.png)';
    tab.style.backgroundColor = this.color;
    tab.style.backgroundPosition = "6px 11px";
    tab.style.backgroundRepeat = 'no-repeat';
    \""+CopinyWidget.proto+"://"+CopinyWidget.host+"/image.php?text="+CopinyWidget.cache+"\"
    */
    tab.style.borderColor = this.tabBorder;
    document.body.insertBefore(tab, document.body.getElementsByTagName('*')[0]);
    if(CopinyWidget.cache!=0){
        var img = document.createElement('img');
        //img.css('margin-right:10px');
        img.className='widget-img';
        img.src = CopinyWidget.proto+"://"+CopinyWidget.host+"/image.php?text="+CopinyWidget.cache;
        tab.appendChild(img);
    }

    // Фиксим баг для моб. устройств с Сафари и position: fixed
    var tabPosition = this.tabPosition;
    var isIPhone = /iPhone/.test(navigator.userAgent);
    var isIPod = /iPod/.test(navigator.userAgent);
    var isIPad = /iPad/.test(navigator.userAgent);
    if (isIPhone || isIPod || isIPad) {
        var fixedElement = function () {
            var left = 0;
            if (tabPosition == 'right') {
                left = window.pageXOffset + window.innerWidth - 35;
            }
            document.getElementById('copiny-widget-tab').style.left = left + 'px';
        };
        fixedElement();
        window.onscroll = fixedElement;
    }
}

CopinyWidget.getDocHeight = function() {
    var D = document;
    return Math.max(
        Math.max(D.body.scrollHeight, D.documentElement.scrollHeight),
        Math.max(D.body.offsetHeight, D.documentElement.offsetHeight),
        Math.max(D.body.clientHeight, D.documentElement.clientHeight)
    );
}

CopinyWidget.show = function()
{
    var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
    var clientHeight = window.innerHeight || document.documentElement.clientHeight;
    var iframes = document.getElementsByTagName('iframe');
    for (var i = 0; i < iframes.length; ++i) {
      var iframe = iframes[i];
      if (iframe.src.search(/http:\/\/www\.youtube\.com\//i) != -1
        && !this.hasClass(iframe, 'youtube-player')) {
            this.addClass(iframe, 'youtube-player');
        }
    }
    this.gId('copiny-overlay').style.height = this.getDocHeight()+"px";
    this.gId('copiny-wrapper').style.display = 'block';
    this.gId('copiny-popup-container').style.top = scrollTop+(clientHeight*0.1)+"px";
    var src = this.proto+'://'+this.host+'/?commid='+this.community+'&type='+this.type;
    if (this.integration && this.getCookie(this.integration)) {
        src += '&'+escape(this.integration)+'='+this.getCookie(this.integration);
    }
    src += '&ainfo='+encodeURIComponent(this.getAddInfo());
    var iframe = this.gId('copiny-popup-iframe');
    if (iframe.addEventListener) {
      iframe.addEventListener("load", this.loaded, false);
      window.addEventListener("message", CopinyWidget.receiveMessage, false);
    } else if (iframe.attachEvent) {
      iframe.attachEvent("onload", this.loaded);
      window.attachEvent("onmessage", CopinyWidget.receiveMessage);
    }
    this.gId('copiny-popup-iframe').src = src;
    this.addClass(document.getElementsByTagName('html')[0], 'widget_on');
}

CopinyWidget.loaded = function()
{
    this.gId('copiny-popup-iframe').className = '';
}

CopinyWidget.hide = function()
{
    var iframe = this.gId('copiny-popup-iframe');
    if (iframe.addEventListener) {
      iframe.removeEventListener("load", this.loaded, false);
      window.removeEventListener("message", CopinyWidget.receiveMessage, false);
    } else if (iframe.attachEvent) {
      iframe.detachEvent("onload", this.loaded);
      window.detachEvent("onmessage", CopinyWidget.receiveMessage);
    }
    this.gId('copiny-wrapper').style.display='none';
    iframe.className= 'loading';
    iframe.src = this.blankUrl();
    this.removeClass(document.getElementsByTagName('html')[0], 'widget_on');
}

CopinyWidget.loaded = function()
{
    CopinyWidget.gId('copiny-popup-iframe').className = '';
}

CopinyWidget.blankUrl = function()
{
    return 'about:blank';//this.proto+'://'+this.host+'/static/main/images/blank.gif';
}

CopinyWidget.addInfo = function(param, value)
{
    var key = param.toString();
    key.replace(/(^\s+)|(\s+$)/g, "");
    if (key.length > 0) {
        this.info[key] = value;
    }
}

CopinyWidget.getAddInfo = function()
{
    this.info.loc = document.location.href;
    this.info.ref = document.referrer;
    this.info.scr = {"x": screen.width, "y": screen.height};
    this.info.flash = this.getFlash();
    return this.JSON.stringify(this.info);
}

CopinyWidget.initJSON = function()
{
    if(!this.JSON)this.JSON={};
    (function(){function g(b){return b<10?"0"+b:b}function l(b){j.lastIndex=0;return j.test(b)?'"'+b.replace(j,function(h){var c=m[h];return typeof c==="string"?c:"\\u"+("0000"+h.charCodeAt(0).toString(16)).slice(-4)})+'"':'"'+b+'"'}function k(b,h){var c,f,i=d,e,a=h[b];if(a&&typeof a==="object"&&typeof a.toJSON==="function")a=a.toJSON(b);switch(typeof a){case "string":return l(a);case "number":return isFinite(a)?String(a):"null";case "boolean":case "null":return String(a);case "object":if(!a)return"null";e=
    [];if(Object.prototype.toString.apply(a)==="[object Array]"){f=a.length;for(c=0;c<f;c+=1)e[c]=k(c,a)||"null";f=e.length===0?"[]":d?"[\n"+d+e.join(",\n"+d)+"\n"+i+"]":"["+e.join(",")+"]";d=i;return f}for(c in a)if(Object.hasOwnProperty.call(a,c))if(f=k(c,a))e.push(l(c)+(d?": ":":")+f);f=e.length===0?"{}":d?"{\n"+d+e.join(",\n"+d)+"\n"+i+"}":"{"+e.join(",")+"}";d=i;return f}}if(typeof Date.prototype.toJSON!=="function"){Date.prototype.toJSON=function(){return isFinite(this.valueOf())?this.getUTCFullYear()+
    "-"+g(this.getUTCMonth()+1)+"-"+g(this.getUTCDate())+"T"+g(this.getUTCHours())+":"+g(this.getUTCMinutes())+":"+g(this.getUTCSeconds())+"Z":null};String.prototype.toJSON=Number.prototype.toJSON=Boolean.prototype.toJSON=function(){return this.valueOf()}}var j=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,d,m={"\u0008":"\\b","\t":"\\t","\n":"\\n","\u000c":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"};if(typeof CopinyWidget.JSON.stringify!==
    "function")CopinyWidget.JSON.stringify=function(b){d="";return k("",{"":b})}})();
}

CopinyWidget.getFlash = function (){
   try{try{var a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");try{a.AllowScriptAccess="always"}catch(b){return"6,0,0"}}catch(c){}return(new ActiveXObject("ShockwaveFlash.ShockwaveFlash")).GetVariable("$version").replace(/\D+/g,",").match(/^,?(.+),?$/)[1]}catch(d){try{if(navigator.mimeTypes["application/x-shockwave-flash"].enabledPlugin)return(navigator.plugins["Shockwave Flash 2.0"]||navigator.plugins["Shockwave Flash"]).description.replace(/\D+/g,",").match(/^,?(.+),?$/)[1]}catch(e){}}return"0,0,0"
}
CopinyWidget.receiveMessage = function(event)
{
  if (event.origin !== (CopinyWidget.proto+"://"+CopinyWidget.host))
    return;
  var data = event.data.split(' ', 2);
  if (data[0] != 'copiny')
      return
  var height = parseInt(data[1])+180;
  //alert(height);

  CopinyWidget.gId('copiny-popup-iframe').height = height+'px';
  CopinyWidget.gId('copiny-popup-iframe').style.height = height+'px';
}
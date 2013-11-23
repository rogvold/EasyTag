/*
 * Title                   : Hotspotter Editor App
 * Author                  : Basm , aka z-B
 * Version                 : 1.3.2
 * Last Modified           : 9 April 2013
 * CodeCanyon Profile      : http://codecanyon.net/user/z-B
 * License                 : http://codecanyon.net/licenses/regular_extended
 */

/*
 * Drawing object , responsible for drawing spots on image
 */
var Draw= {
    $edImg: null,
    edImg_b: {t:0 , b:0 , l:0 , r:0} ,
    parentOffset: {x:0 , y:0},
    mouseCoord: {x: 0, y: 0},
    isDraw: false,
    type: 'red-spot' , //default spot for Draw
    
    init: function($img) {
        this.$edImg= $img;
        
        this.$edImg.unbind('.draw');
        $(document).unbind('.draw');
        
        function imgLoaded() {
            //setTimeout because firefox get wrong parent
            //offsets when calling 'Draw.getConstraints' directly after image load
            //event fired for local images readed with FileReader HTML 5 API 
            //TESTED in Firefox 16.0.2 & 17.0
            setTimeout(function() {
                Draw.$edImg.bind('mousedown.draw', $.proxy(Draw.mousedown, Draw));
                $(document).bind('mousemove.draw' , $.proxy(Draw.start, Draw));
                $(document).bind('mouseup.draw' , $.proxy(Draw.mouseup, Draw));
            }, 100);
        }
        
        //note that we don't bind load event then check for complete
        //instead we make the load function seperate then check for complete
        //because in the first case when loading a cached image, load event
        //will fire, also when u check for '.complete' it will be true which
        //will also fire load and make a duplicate
        if (this.$edImg[0].complete) {
            imgLoaded();
        }else{
            this.$edImg.bind('load.draw', imgLoaded);
        }
    },
    
    getConstraints: function() {
        this.edImg_b.t = this.$edImg.offset().top;
        this.edImg_b.b = this.$edImg.offset().top + this.$edImg.height();
        this.edImg_b.l = this.$edImg.offset().left;
        this.edImg_b.r = this.$edImg.offset().left + this.$edImg.width();
        
        this.parentOffset.x= this.$edImg.parent().offset().left;
        this.parentOffset.y= this.$edImg.parent().offset().top;
    },
    
    mousedown: function(e) {
        //update constraints in case user scroll image to get accurate parent offset
        this.getConstraints();
        
        if (e.which == 1) {
            //sniper-spot
            if (this.type == 'sniper-spot') {
                //check boundries
                if ((e.pageX < (this.parentOffset.x + this.$edImg.width() - 11)) && (e.pageY < (this.parentOffset.y + this.$edImg.height() - 11))) {
                    //position spot & make center of spot in the point of mouse click
                    Editor.newSpot(new Spot(this.type, e.pageX - this.parentOffset.x - 11, e.pageY - this.parentOffset.y - 11));
                }
                
                return false;
            }
            
            //img-spot
            if (this.type == 'img-spot') {
                Editor.newSpot(new Spot(this.type, e.pageX - this.parentOffset.x , e.pageY - this.parentOffset.y));
                $('#img-spot-dialog').dialog('open');
                
                return false;
            }
            
            //other kinds of spots , start drawing with mousemove
            this.isDraw= true;
            this.mouseCoord.x= e.pageX;
            this.mouseCoord.y= e.pageY;
        }
        
        return false;
    },
    
    start:function(e) {
        if (!this.isDraw) {
            return;
        }
    
        if (!this.curSpot) {
            this.curSpot= new Spot(this.type, this.mouseCoord.x - this.parentOffset.x , this.mouseCoord.y - this.parentOffset.y);
        }
    
        //10 refer to min(width/height)
        var h = 10,
            w= 10;
        
        //check boundries
        if (e.pageX > this.edImg_b.l) {
            //check if width is larger than min
            if (((e.pageX - this.mouseCoord.x) > 10)) {
                w= e.pageX -this.mouseCoord.x;
            }
            
            //if mouse got out of image make spot width reach image boundry
            if (e.pageX > this.edImg_b.r) {
                w= this.$edImg.width() - parseFloat(this.curSpot.$edSpot.css('left'));
            }
            
            this.curSpot.$edSpot.outerWidth(w);
        }
        
        if (e.pageY > this.edImg_b.t) {
        
            if (((e.pageY - this.mouseCoord.y) > 10)) {
                h= e.pageY - this.mouseCoord.y;
            }
            
            if (e.pageY > this.edImg_b.b) {
                h= this.$edImg.height() - parseFloat(this.curSpot.$edSpot.css('top'));
            }
            
            this.curSpot.$edSpot.outerHeight(h);
        }
    },
    
    mouseup: function(e) {
        this.isDraw= false;
            if (this.curSpot) {
                Editor.newSpot(this.curSpot);
                Editor.updateSpotDim();
                Editor.updateSpotPos();
                
                this.curSpot= null;
            }
    }
    
};

//========================== Spot Object ==========================
/*
 * Hold Spot options , reference to spot dom element both in editor &
 * preview
 */
var Spot= function(spotClass, x, y) {
    this.spotClass= spotClass;
    
    //create edited spot
    if (spotClass == 'sniper-spot') {
        //sniper spots r wrapped in another div to be easily identified when selected
        //& prevent style break
        var $orignSpot= $('<div/>').addClass(spotClass);
        
        this.$edSpot= $('<div/>').css('position', 'absolute')
                                 .css('left',x)
                                 .css('top',y)
                                 .data('spotObj', this)
                                 .append($orignSpot)
                                 .appendTo(Editor.$edDiv);
    }else{
        this.$edSpot= $('<div/>').addClass(spotClass)
                                 .css('position', 'absolute')
                                 .css('left',x)
                                 .css('top',y)
                                 .data('spotObj', this)
                                 .appendTo(Editor.$edDiv);
        
        //make invisible spots visible during editing
        if (spotClass == 'invisible-spot') {
            this.$edSpot.addClass('invisible-spot-ed');
        }
        if (spotClass == 'onhover-spot') {
            this.$edSpot.addClass('onhover-spot-ed');
        }
    }
    
    
    //create preview spot
    this.$hsWrap= $('<div/>').addClass('hs-wrap'),
    this.$ttWrap= $('<div/>').addClass('tt-wrap');
    
    this.$prevSpot= $('<div/>').addClass(spotClass).appendTo(this.$hsWrap);
    
    //create tooltip
    this.$tooltip= $('<div/>').appendTo(this.$ttWrap);
    
    this.$ttWrap.appendTo(this.$hsWrap);
    
    //add preview spot to dom element
    Editor.$prevDiv.append(this.$hsWrap);
    
    if (spotClass == 'img-spot') {
        var $firstImg = $('<img/>');
        
        this.$edSpot.append($firstImg).append('<img/>');
        
        //avoid using .show() as it will use 'inline' instead of 'block'
        //which will cause additional height to be added to image
        $firstImg.css('display', 'block');
        
        this.$prevSpot.append('<img/>').append('<img/>');
    }
    
    
    //Spot Object Properties
    
    //set default options
    this.coord= [x,y];
    
    if (spotClass == 'sniper-spot' || spotClass == 'img-spot') {
        this.dim= null;
    }else{
        this.dim= [0,0,0];
    }
    
    this.spLink= '';     //spot link
    this.spLinknWin= false; //whether to open link in new win or no
    this.ttContent= 'content'; // tooltip content
    /*
     * Options object contain options that's specified
     * in option bar , when option bar changes this object 
     * will be updated
     */
    this.options= {
        spType     : 'General',
        spAnim     : null,
        spActiveon :'hover',
        spName     : '',
    
        ttType     : 'tip',
        ttAnim     : 'goin',
        ttDir      : 'top',
        ttWidth    : '',
        ttPos      : '',
        ttColor    : 'black'
    };
    
    //adjust tooltip position for sniper spots to appear exactly over spot
    if (this.spotClass == 'sniper-spot') {
        this.options.ttPos= -18;
    }
};

/*
 * Add options to preview spot & tooltip in preparation
 * of preview
 */
Spot.prototype.flushOptions= function() {
    //clear dynamically additions by the plugin
    this.$hsWrap.removeAttr('style');   //hs-wrap
    this.$prevSpot.removeAttr('style'); 
    this.$prevSpot.attr('class', this.spotClass); //reset class because Aim add 'hs-flatten' class to spots
    
    this.$ttWrap.attr('class', 'tt-wrap'); //reset tt-wrap class
    this.$ttWrap.removeAttr('style');     //tt-wrap
    this.$tooltip.removeAttr('style').removeData();    //tooltip
    
    //coordinates
    this.$prevSpot.attr('data-coord', this.coord.join(','));
    
    //dimensions
    if (this.dim !== null) {
        this.$prevSpot.attr('data-dim', this.dim.join(','));
    }
    
    if (this.options.spType === 'Aim') {
        this.$prevSpot.attr('data-handler', 'Aim');
        
        //Animation is default for Aim so put handleropts attr only if it false
        //to avoid cluttering markup
        if (this.options.spAnim == 'false') {
            this.$prevSpot.attr('data-handleropts', this.options.spAnim);
        }else{
            this.$prevSpot.removeAttr('data-handleropts');
        }
    }else{
        this.$prevSpot.removeAttr('data-handler');
        this.$prevSpot.removeAttr('data-handleropts');
    }
    
    this.$prevSpot.attr('data-activeon', this.options.spActiveon);
    
    if (this.options.spName) {
        this.$prevSpot.attr('data-name', $.trim(this.options.spName));
    }else{
        this.$prevSpot.removeAttr('data-name');
    }
    
    //add link to spot if available
    var $spParent= this.$prevSpot.parent(),
        $link;
    if (this.spLink) {
         //check if we have add link previously
        if($spParent[0] != this.$hsWrap[0]) {
            $link= $spParent.attr('href', this.spLink);
        }else{
            $link= $('<a/>').attr('href', this.spLink)
                            .prependTo(this.$hsWrap)
                            .append(this.$prevSpot);
        }
        
        if (this.spLinknWin) {
            $link.attr('target', '_blank');
        }else{
            $link.removeAttr('target');
        }
    }else{
        if($spParent[0] != this.$hsWrap[0]) {
            this.$prevSpot.prependTo(this.$hsWrap); //add spot to hs-wrap
            $spParent.remove(); //remove link
        }
    }
    
    this.$tooltip.removeClass();
    
    if (this.options.spType === 'Aim') {
        this.$tooltip.addClass('aim-tooltip');
    }else{
        this.$tooltip.addClass(this.options.ttType + '-tooltip');
    }
    
    if (!this.options.ttAnim || this.options.ttAnim == 'none') {
        this.$tooltip.removeAttr('data-anim');
    }else{
        this.$tooltip.attr('data-anim', this.options.ttAnim);
    }
    
    if (this.options.ttDir) {
        this.$tooltip.attr('data-dir', this.options.ttDir);
    }else{
        this.$tooltip.removeAttr('data-dir');
    }
    
    //ttWidth != null or != ''
    if (this.options.ttWidth) {
        this.$tooltip.attr('data-width', parseInt(this.options.ttWidth));
    }else{
        this.$tooltip.removeAttr('data-width');
    }
    
    //ttPos != null or != ''
    if (this.options.ttPos) {
        this.$tooltip.attr('data-pos', parseInt(this.options.ttPos));
    }else{
        this.$tooltip.removeAttr('data-pos');
    }
    
    if (this.options.ttColor == 'white') {
        this.$tooltip.addClass(this.options.ttColor);
    }
    
    //save tooltip content
    if (this.ttContent) {
        if (this.options.spType == 'Aim') {
            this.$tooltip.html('');
            
            //wrap content in 'tt-content' div to avoid content reflow with animation
            $('<div/>').addClass('tt-content')
                       .html(this.ttContent)
                       .appendTo(this.$tooltip);
        }else{
            this.$tooltip.html(this.ttContent);
        }
    }else{
        this.$tooltip.html('');
    }
    
    //clear styles from img-spot imgs
    if (this.spotClass == 'img-spot') {
        this.$prevSpot.find('img').removeAttr('style');
    }
};

/*
 * select spot in editor
 */
Spot.prototype.select= function() {
    //adjust glass & shadow spots to be contained within select border as they don't have border by default
    if (this.spotClass == 'glass-spot' || this.spotClass == 'shadow-spot') {
        var w= this.$edSpot.width(),
            h= this.$edSpot.height();
            
            this.$edSpot.width(w-4); //4 refer to select border-radius * 2
            this.$edSpot.height(h-4);
    }
    
    if (this.spotClass == 'img-spot') {
        var $spotImg = this.$edSpot.find('> img').eq(0),
            spotImgLeft = this.$edSpot.position().left,
            spotImgW = $spotImg.width(),
            spotImgTop = this.$edSpot.position().top,
            spotImgH = $spotImg.height(),
            edImgW = Editor.$edImg.width(),
            edImgH = Editor.$edImg.height();			
            
            
        if (Math.round(spotImgLeft + spotImgW) == edImgW) {
            this.$edSpot.css('left', spotImgLeft - 4);
        }

        if (Math.round(spotImgTop + spotImgH) == edImgH) {
            this.$edSpot.css('top', spotImgTop - 4);
        }
    }
    
    this.$edSpot.addClass('selected');
	
    $('#spotLeft').attr('value', this.$edSpot.css('left')); 
    $('#spotTop').attr('value', this.$edSpot.css('top'));
    $('#spotWidth').attr('value', this.$edSpot.css('width')); 
    $('#spotHeight').attr('value', this.$edSpot.css('height'));
    $('#spotContent').attr('value', this.ttContent);
};

/*
 * unselect spot
 */
Spot.prototype.unselect= function() {
    //reverse what we do in selecting
    if (this.spotClass == 'glass-spot' || this.spotClass == 'shadow-spot'){
        var w= this.$edSpot.width(),
            h= this.$edSpot.height();			            
            this.$edSpot.width(w+4);
            this.$edSpot.height(h+4);
    }
    
    //align img spot to bottom and/or left corner if it adajcent to it
    //to adjust for removal of the selection border
    //Note we don't do that for top/left because it take 0 in any or both of them
    //if it adjacent to any of them and so preview image do the same
    if (this.spotClass == 'img-spot') {
        var $spotImg = this.$edSpot.find('> img').eq(0),
            spotImgLeft = this.$edSpot.position().left,
            spotImgW = $spotImg.width(),
            spotImgTop = this.$edSpot.position().top,
            spotImgH = $spotImg.height(),
            edImgW = Editor.$edImg.width(),
            edImgH = Editor.$edImg.height();
            
            
        if (Math.round(spotImgLeft + spotImgW + 4) == edImgW) {
            this.$edSpot.css('left', spotImgLeft + 4);
            Editor.updateSpotPos();
        }

        if (Math.round(spotImgTop + spotImgH + 4) == edImgH) {
            this.$edSpot.css('top', spotImgTop + 4);
            Editor.updateSpotPos();
        }
    }
    
    this.$edSpot.removeClass('selected');
};

/*
 * Delete spot from both editor & preview
 * spot object it self will be deleted from Editor object
 */
Spot.prototype.del= function() {
    this.$edSpot.remove();
    this.$hsWrap.remove(); //remove hs-wrap 'Spot + tooltip'
};

//========================== Editor Object ==========================
/*
 * Editor: static object that manage editing
 */
var Editor= {
    
    isPreview: false,
    $edDiv: null, //ref to editing div
    $prevDiv:null,//ref to preview div 
    $edImg: null, //ref to image dom in editor
    $prevImg: null, //ref to image dom in preview
    curSpot: null, //ref to current spot object that's being edited
    optionBar: {}, //refs to option bar dom elements
    spotPool: [], //array of created spots
    
    
    /*
     * Initialzation function use on dom ready to cache used dom elements like
     * options bar elements & edited image , bind change event to options bar
     */
    init: function() {
        this.$edDiv                 = $('.edit-area');
        this.$prevDiv               = $('.hs-area');
        
        this.$edImg                 = $('#ed-img');
        this.$prevImg               = $('#prev-img');
        
        this.isLocalFile            = false; //flag to indicate whether loaded image is local or not
        this.localFile              = null;  //File object , populated when user choose local file in new image dialog
        
        this.$optionFrm             = $('#option-frm');
        
        this.optionBar.spType       =  $('#sp-type');
        this.optionBar.spAnim       =  $('#sp-anim');
        this.optionBar.spActiveon   =  $('#sp-activeon');
        this.optionBar.spBradius    =  $('#sp-bradius');
        this.optionBar.spName       =  $('#sp-name');
        
        this.optionBar.ttType       =  $('#tt-type');
        this.optionBar.ttAnim       =  $('#tt-anim');
        this.optionBar.ttDir        =  $('#tt-dir');
        this.optionBar.ttWidth      =  $('#tt-width');
        this.optionBar.ttPos        =  $('#tt-pos');
        this.optionBar.ttColor      =  $('#tt-color');
        
        //================ bind main controls ================
        //new image
        $('#nimg-btn').bind('click', function() {
            $('#new-img-dialog').dialog('open');
        });
        
        //preview & end preview buttons
        $('#prev-btn , #end-prev-btn').bind('click', $.proxy(Editor.togglePreview, Editor));
        
        //get code button
        $('#gcode-btn').bind('click', $.proxy(Editor.getCode, Editor));
        
        $('#load-code-btn').bind('click', function () {
            $('#load-code-dialog').dialog('open');
        });
        
        //bind Hotkeys
        this.bindHotKeys();
        
        //================ bind secondary controls ================ 
        //bind left bar options
        $('.spot-bar button').bind('click', function() {
            Draw.type= $(this).attr('id').slice(0,-4);
            Editor.focusSpotBtn(Draw.type);
        });
        
        //bind center bar options
        $('#clone-btn').bind('click', $.proxy(Editor.cloneSpot, Editor));
        $('#del-btn').bind('click', $.proxy(Editor.delSpot, Editor));
        $('#link-btn').bind('click', function() {
            $('#sp-link-dialog').dialog('open');
        });
        
        //bind right bar options
        this.$optionFrm.bind('change', $.proxy(Editor.optionChange, Editor));
        this.$optionFrm.bind('submit', function(e) {
            //prevent default if user press Enter
            e.preventDefault();
        });
        $('#tt-content-btn').bind('click', function() {
            $('#tt-content').val(Editor.curSpot.ttContent);
            $('#tt-content-dialog').dialog('open');
        });
        
        //remove formatting check box
        $('#remove-formatting').bind('click', function () {
            var markup= $('#gcode-txt').val();
                
            if ($(this).is(':checked')) {
                $('#gcode-txt').val(Editor.removeFormatting(markup));
            }else{
                $('#gcode-txt').val(style_html(markup));
            }
        });
        
        //================ make dialogs ================
        //prepare new image dialog
        if (!window.File || !window.FileList || !window.FileReader) {
            $('.html5-local-file').hide();
        }
        
        $('#new-i-d-tabs').tabs();
        $('#new-img-dialog').dialog({
            autoOpen: false,
            modal: true,
            title: 'New Image',
            resizable: false,            
            show: 'drop',
            hide: 'drop',
            buttons: {
                'Open': $.proxy(Editor.newImage, Editor),
                'Cancel': function() {
                    $(this).dialog('close');
                }
            },
            open: function() {
                $('#img-url-txt , #img-file-txt').val('');
            },
            close: function () {
                Editor.$edDiv.focus();
            }
        });
        $('#img-file-txt').bind('change', function(e) {
            Editor.localFile= e.target.files[0];
        });
        
        $('#gcode-dialog').dialog({
            autoOpen: false,
            modal: true,
            title: 'Get code',
            resizable: false,
            width: 630,
            show: 'drop',
            hide: 'drop',
            buttons: {
                'OK': function() {    
                    $(this).dialog('close');
                }
            },
            close: function() {
                $('#gcode-txt').val('');
                $(this).find('.ui-widget').hide();
                Editor.$edDiv.focus();
            }
        });
        
        $('#load-code-dialog').dialog({
            autoOpen: false,
            modal: true,
            title: 'Load code',
            resizable: false,
            width: 630,
            show: 'drop',
            hide: 'drop',
            buttons: {
                'Load': function () {
                    Editor.loadCode();
                    $(this).dialog('close');
                },
                'Close': function () {
                    $(this).dialog('close');
                }
            },
            close: function() {
                Editor.$edDiv.focus();
            }
        });
        
        //select all code when focusing textarea in get code dialog
        $('#gcode-txt').bind('focus', function() {
            var $txt= $(this);
            $txt.select();
            
            //fix chrome issue
            $txt.bind('mouseup', function() {
                $txt.unbind("mouseup");
                return false;
            });
        });
        
        $('#img-spot-dialog').dialog({
            autoOpen: false,
            modal: true,
            title: 'Choose Images',
            resizable: false,
            width: 380,
            height: 200,
            show: 'drop',
            hide: 'drop',
            buttons: {
                'Save': $.proxy(Editor.imgSpotSave, Editor),
                'Cancel': function() {
                    $(this).dialog('close');
                }
            },
            close: function() {
                //both save & cancel call close so if we saved images we set flag
                //in the newly created spot to avoid deleting the spot as happen when
                //cancel or use 'x' button on dialog
                if (!Editor.curSpot.$edSpot.data('img-spot-save')) {
                    Editor.delSpot();
                }
                
                $('#imgspot-1 , #imgspot-2').val('');
                
                Editor.$edDiv.focus();
            }
        });
        
        $('#sp-link-dialog').dialog({
            autoOpen: false,
            modal: true,
            title: 'Spot Link',
            resizable: false,
            show: 'drop',
            hide: 'drop',
            buttons: {
                'Save': function() {
                    Editor.curSpot.spLink= $('#sp-link').val();
                    Editor.curSpot.spLinknWin= $('#sp-link-n-win').is(':checked');
                    $(this).dialog('close');
                },
                'Cancel': function() {
                    $(this).dialog('close');
                }
            },
            open: function() {
              $('#sp-link').val(Editor.curSpot.spLink);
                    
                    if (Editor.curSpot.spLinknWin) {
                        $('#sp-link-n-win').prop('checked', true);
                    }else {
                        $('#sp-link-n-win').prop('checked', false);
                    }  
            },
            close: function () {
                Editor.$edDiv.focus();
            }
        });
        
        $('#tt-content-dialog').dialog({
            autoOpen: false,
            modal: true,
            resizable: false,
            width: 430,
            title: 'Tooltip Content',
            show: 'drop',
            hide: 'drop',
            buttons: {
                'Save': $.proxy(Editor.ttContentSave, Editor),
                'Cancel': function() {
                    $(this).dialog('close');
                }
            },
            open: function() {
                $('#tt-content').val(Editor.curSpot.ttContent);
            },
            close: function () {
                Editor.$edDiv.focus();
            }
        });
        
        $('#error-dialog').dialog({
            autoOpen: false,
            modal: true,
            title: 'Error',
            resizable: false,
            draggable: false,
            show: 'shake',
            hide: 'fade',
            buttons: {
                'OK': function() {
                    $(this).dialog('close');
                }
            }
        });
        
        //======================================================================
        
        $('button').button(); //make buttons
        //add spinner to b-radius
        $('#sp-bradius').spinner({
            create: function() {
                $(this).parent().removeClass('ui-widget-content');
            },
            stop: function() {
                Editor.curSpot.$edSpot.css('border-radius', parseInt($('#sp-bradius').val()));
            }
        }); 
        
        //tooltips
        $(document).tooltip({
            position: {
                my: "center top",
                at: "left bottom+10",
                using: function(position) {
                    $(this).css(position);
                    $(this).css('font-size', '12px');
                }
            }
        });
		
		//alert($('#spotX').attr('value'))
		
    },
    
    /*
     * Get image for drawing hotspots , either from URL or localfile using
     * HTML5 FileReader API if available
     * The function is bind to click event of new image dialog
     */
    newImage: function() {
        var imgURL= $('#img-url-txt').val();
        
        if (!imgURL && !Editor.localFile) {
            this.showError('Choose Image');
            return;
        }
        
        //set image src
        if ($('#img-url').css('display') == 'block') {
            Editor.initNewImage(imgURL);
            Editor.isLocalFile= false;
        }else{ 
            //file browse
            if (this.localFile) {
                Editor.initNewImage(Editor.localFile);
                Editor.isLocalFile= true;
            }
        }
        
        $('#new-img-dialog').dialog('close');
    },
    
    /*
     * Load new image in Editor, remove previous image & it's spots
     * init Draw object, adjust options panel
     */
    initNewImage: function (img) {
        //remove spots if there's any
        if (Editor.spotPool.length > 0) {
            var i= Editor.spotPool.length - 1;
            do {
                Editor.spotPool[i].del();
                delete Editor.spotPool[i];
            }while(i--);
            
            Editor.spotPool= [];
        }
        
        this.$edImg.removeAttr('src');
        
        //set image src
        if (typeof img == 'string') {
            this.$edImg.attr('src', img);
            this.$prevImg.attr('src', img);
        }else{ 
            //local file
            var fread= new FileReader();
                
            fread.onload= function(e) {
                Editor.$edImg.attr('src', e.target.result);
                Editor.$prevImg.attr('src', e.target.result);
            }
                
            fread.readAsDataURL(img);
        }
        
        //start drawing when image loaded
        Draw.init(Editor.$edImg);
        
        //remove responsive attr
        Editor.$prevImg.removeAttr('data-imgdim');
        
        //show responsive option
        $('.img-resp-container').show();
        $('.spot-options').hide();
        
        //focus red-spot as it's default
        Editor.focusSpotBtn('red-spot');
        Draw.type= 'red-spot';
    },
    
    /*
     * Save spot data , start/end preview
     * Bind to click event of preview & end preview button
     */
    togglePreview: function() {
        if (this.inPreview) {
            this.inPreview= false;
            
            $('#end-prev-btn').hide();
            
            window.location.hash= ''; //empty hash to allow for testing linked spots
            $(window).unbind('.hotspotter'); //remove hashchange handler & load
            
            var i= this.spotPool.length - 1,
                cur;
            
            //reset Aim handler tooltip position in dom in case user changed tt type
            if (i > -1) {
                do {
                    cur= this.spotPool[i];

                    cur.$prevSpot.removeData();

                    if (cur.options.spType == 'Aim') {
                        cur.$tooltip.appendTo(Editor.spotPool[i].$ttWrap);
                        cur.$tooltip.stop();

                        //clear Aim handler additional markup & data 
                        cur.$prevSpot.parents('.hs-area').removeData()
                                     .find('.hs-aim-rect , .hs-aim-pillar').remove();

                        //unbind before hide to prevent undefined object error
                        //when leave event try to access removed data 'areaData'
                        cur.$hsWrap.unbind();
                    }
                }while(i--);
            }

            $('#prev-overlay').hide();
            this.$prevDiv.parent().hide();
            Editor.$edDiv.focus();
        } else {
            /*if (this.spotPool.length == 0) { 
                this.showError('There is no spots');
                return; 
            } */
            this.inPreview= true;
            
            $('#end-prev-btn').show();
            
            //if img-spot was at bottom and/or right corner move image to
            //align with any/both of them to alleviate selection border removal
            if (this.curSpot && this.curSpot.spotClass == 'img-spot') {
                var $spotImg = this.curSpot.$edSpot.find('> img').eq(0),
                spotImgLeft = this.curSpot.$edSpot.position().left,
                spotImgW = $spotImg.width(),
                spotImgTop = this.curSpot.$edSpot.position().top,
                spotImgH = $spotImg.height(),
                edImgW = Editor.$edImg.width(),
                edImgH = Editor.$edImg.height();
            
            
                if (Math.round(spotImgLeft + spotImgW + 4) == edImgW) {
                    this.curSpot.coord[0] = spotImgLeft + 4;
                }

                if (Math.round(spotImgTop + spotImgH + 4) == edImgH) {
                    this.curSpot.coord[1] = spotImgTop + 4;
                }
            }
            
            //ensure options r saved , in case user used hotkey to enter
            //preview because options form 'change' event won't fire in this case
            if (this.curSpot) {
                this._saveOptions();
            }
            
            var i= this.spotPool.length - 1;
			
            if (i > -1){
                do {
                    //save options
                    this.spotPool[i].flushOptions();
                }while(i--);
            }
            
            var winW= $(window).width(),
                winH= $(window).height(),
                prevW= this.$prevDiv.parent().width(),
                prevH= this.$prevDiv.parent().height();
            
            //position preview image in center
            this.$prevDiv.parent().css('left', (winW/2) - (prevW/2));
            this.$prevDiv.parent().css('top', (winH/2) - (prevH/2));

            $('#prev-overlay').show();
            this.$prevDiv.parent().show();
            
            this.$prevDiv.hotspotter();
        }
    },
    
    /*
     * Get generated code & apply responsive option if checked 
     * Assigned to get code dialog 'open' handler
     */
    getCode: function() {
        //if there's no spots return
        if (!this.spotPool.length) { 
            this.showError('There is no spots');
            return;
        }
        
        var dialogH= 220; //specifiy height of dialog , to make dialog scale if we have notices 
        
        //check if user want image to be repsonsive
        if(true){//if ($('#img-responsive').is(':checked')) {
            var imgW= this.$edImg.width(),
                imgH= this.$edImg.height();
                
            this.$prevImg.attr('data-imgdim', [imgW, imgH].join(','));
        }else{
            this.$prevImg.removeAttr('data-imgdim');
        }
        
        //remove image id , we won't need to return image id because we have
        //already cached image dom in this.$prevImg
        this.$prevImg.removeAttr('id');
        
        //save current spot options
        if (this.curSpot) {
            this._saveOptions();
        }
        
        //save each spot options to markup
        var i= this.spotPool.length-1;
        do {
            
            this.spotPool[i].flushOptions();
            
        }while(i--);
        
        if (this.curSpot && this.curSpot.spotClass == 'img-spot') {
            var $spotImg = this.curSpot.$edSpot.find('> img').eq(0),
            spotImgLeft = this.curSpot.$edSpot.position().left,
            spotImgW = $spotImg.width(),
            spotImgTop = this.curSpot.$edSpot.position().top,
            spotImgH = $spotImg.height(),
            edImgW = Editor.$edImg.width(),
            edImgH = Editor.$edImg.height();
            
            
            if (Math.round(spotImgLeft + spotImgW + 4) == edImgW) {
                this.curSpot.coord[0] = spotImgLeft + 4;
            }

            if (Math.round(spotImgTop + spotImgH + 4) == edImgH) {
                this.curSpot.coord[1] = spotImgTop + 4;
            }
        }
        
        //if the image used is local , show local image notice & change img
        //src attr to put 'your image' placeholder & get code & change src attr
        //to it's original value
        var markup;
        
        if (this.isLocalFile) {
            var orignal_src= this.$prevImg.attr('src');
            
            $('#local-file-notice').show();
            
            this.$prevImg.attr('src', 'your image URL');
            markup= this.$prevDiv.parent().html();
            this.$prevImg.attr('src', orignal_src);
            dialogH= 320;
        }else{
            $('#local-file-notice').hide();
            markup= this.$prevDiv.parent().html();
        }
        
        if ($('#remove-formatting').is(':checked')) {
            markup = Editor.removeFormatting(markup);
        }else{
            markup = style_html(markup); //Beautify code
        }
        
        //show it
        $('#gcode-txt').val(markup);
        //$('#gcode-dialog').dialog('option')['height']= dialogH;
        //$('#gcode-dialog').dialog('open');
    },
    
    loadCode: function () {
        //make document fragment of the code inserted
        var $docFrag = $($('#load-code-txt').val()),
            $hsArea = $docFrag.filter('.hs-area').eq(0),
            $img = $hsArea.find('> img');
                
        if (!$hsArea.length) {
            Editor.showError("There's no div Tag with class .hs-area");
            return;
        }
        
        Editor.initNewImage($img.attr('src'));
        
        if ($img.attr('data-imgdim')) {
            $('#img-responsive').prop('checked', true);
        }
        
        
        $hsArea.find('.hs-wrap')
               .each(function () {
                   var $hsWrap = $(this),
                       $spot = $hsWrap.find('div[data-coord]'),
                       $tooltip = $hsWrap.find('.tt-wrap > div'),
                       spType = $spot.attr('class'),
                       spCoord = $spot.attr('data-coord').split(','),
                       spDim = $spot.attr('data-dim'),
                       spActiveon = $spot.attr('data-activeon'),
                       spHandler = $spot.attr('data-handler'),
                       spHdOpts = $spot.attr('data-handleropts'),
                       spName = $spot.attr('data-name'),
                       ttWidth = $tooltip.attr('data-width'),
                       ttDir = $tooltip.attr('data-dir'),
                       ttAnim = $tooltip.attr('data-anim'),
                       ttPos = $tooltip.attr('data-pos'),
                       spotObj;
                       
                   spotObj= new Spot(spType, spCoord[0], spCoord[1]);
                   
                   spotObj.$edSpot.css('left', spCoord[0] + 'px');
                   spotObj.$edSpot.css('top', spCoord[1] + 'px');
                   
                   if (spType == 'img-spot') {
                       var img1URL= $spot.find('> img').eq(0).attr('src'),
                           img2URL= $spot.find('> img').eq(1).attr('src');
                       
                       Editor.loadImgSpot(spotObj, img1URL, img2URL);
                   }
                   
                   if (spDim) {
                       var dimArr= spDim.split(',');
                       
                       spotObj.dim= [dimArr[0], dimArr[1], dimArr[2]];
                       
                       spotObj.$edSpot.width(dimArr[0]);
                       spotObj.$edSpot.height(dimArr[1]);
                       
                       if (dimArr[2]) {
                           spotObj.$edSpot.css('border-radius', dimArr[2] + 'px');
                       }
                   }
                   
                   spotObj.options.spActiveon = spActiveon;
                   spotObj.options.spType = spHandler ? spHandler : 'General';
                   spotObj.options.spAnim = spHdOpts ? 'false' : null;
                   
                   var $spotParent= $spot.parent();
                   
                   if ($spotParent.is('a')) {
                       spotObj.spLink = $spotParent.attr('href');
                       
                       if ($spotParent.attr('target')) {
                           spotObj.spLinknWin = true;
                       }
                   }
                   
                   spotObj.options.spName= spName;
                   
                   //Add tooltip options
                   if ($tooltip.hasClass('tip-tooltip')) {
                       
                       spotObj.options.ttType = 'tip';
                       
                   }else if ($tooltip.hasClass('bubble-tooltip')) {
                       
                       spotObj.options.ttType = 'bubble';
                   
                   }else if ($tooltip.hasClass('aim-tooltip')) {
                       spotObj.options.ttType = 'aim';
                   
                   }
                   
                   if (spotObj.options.ttType == 'aim') {
                       spotObj.ttContent = $tooltip.find('.tt-content').html();
                   }else{
                       spotObj.ttContent = $tooltip.html();
                   }
                   
                   spotObj.options.ttWidth = ttWidth || '';
                   spotObj.options.ttDir = ttDir || 'top';
                   spotObj.options.ttAnim = ttAnim || 'goin';
                   spotObj.options.ttPos= ttPos || '';
                   
                   if ($tooltip.hasClass('white')) {
                       spotObj.options.ttColor = 'white';
                   }else{
                       spotObj.options.ttColor = 'black';
                   }
                   
                   Editor.spotPool.push(spotObj);
        
                   //select when mouse down
                   spotObj.$edSpot.bind('mousedown', function() {
                       Editor.toggleSelect($(this).data('spotObj'));
                   });

                   if (spotObj.spotClass != 'sniper-spot' && spotObj.spotClass != 'img-spot') {
                       spotObj.$edSpot.resizable({
                           containment: Editor.$edImg,
                           stop: $.proxy(Editor.updateSpotDim, Editor)
                       });
                   }

                   spotObj.$edSpot.draggable({
                       containment: Editor.$edImg,
                       stop: $.proxy(Editor.updateSpotPos, Editor)
                   });
               });
                                    
    },
    
    /*
     * Delete current selected spot & remove it from spotPool
     * bind to click event of delete button
     */
    delSpot: function() {
        if (!this.curSpot) { return; }
        
        this.curSpot.del();
        
        //delete spot object from undoStack
        var i= this.spotPool.length - 1;
        do {
            if (this.spotPool[i] === this.curSpot) {
                //remove from stack
                this.spotPool.splice(i,1);
                delete this.curSpot;
                break;
            }
        }while(i--);
        
        this.curSpot= null;
        this.hideOptionBar();
    },
    
    /*
     * Clone all current spot data in new spot
     * Bind to click event of clone button
     */
    cloneSpot: function() {
        if (!this.curSpot) { return; }
        
        var imgDim= {w: this.$edImg.width(), h: this.$edImg.height()},
            spotDim= {w:this.curSpot.$edSpot.width(), h:this.curSpot.$edSpot.height()},
            spotPos= {x: parseFloat(this.curSpot.$edSpot.css('left')), y: parseFloat(this.curSpot.$edSpot.css('top'))},
            newSpotPos= {};
        
        this._saveOptions();
        
        newSpotPos.x= ((spotPos.x + spotDim.w + 10) < imgDim.w) ? spotPos.x + 10 : spotPos.x - 10;
        newSpotPos.y= ((spotPos.y + spotDim.h + 10) < imgDim.h) ? spotPos.y + 10 : spotPos.y - 10;
        
        //make clone
        var newSpotObj= new Spot(this.curSpot.spotClass, newSpotPos.x, newSpotPos.y);
        
        //copy options
        newSpotObj.options= $.extend({}, this.curSpot.options);
        
        //copy dimensions
        if (this.curSpot.spotClass != 'sniper-spot' && this.curSpot.spotClass != 'img-spot') {
            newSpotObj.$edSpot.width(spotDim.w);
            newSpotObj.$edSpot.height(spotDim.h);
            
            newSpotObj.dim= [spotDim.w, spotDim.h, this.curSpot.dim[2]];
            newSpotObj.$edSpot.css('border-radius', this.curSpot.dim[2]);
        }
        
        //copy tooltip contents
        newSpotObj.ttContent= this.curSpot.ttContent;
        
        //copy link
        newSpotObj.spLink= this.curSpot.spLink;
        newSpotObj.spLinknWin= this.curSpot.spLinknWin;
        
        //copy img src in case of img-spot
        if (this.curSpot.spotClass == 'img-spot') {
            var img1= this.curSpot.$edSpot.find('img').eq(0).attr('src'),
                img2= this.curSpot.$edSpot.find('img').eq(1).attr('src');
                
            newSpotObj.$edSpot.find('img').eq(0).attr('src', img1);
            newSpotObj.$prevSpot.find('img').eq(0).attr('src', img1);
                
            newSpotObj.$edSpot.find('img').eq(1).attr('src', img2);
            newSpotObj.$prevSpot.find('img').eq(1).attr('src', img2);
        }
        
        //add new spot to spotPool & select it
        this.newSpot(newSpotObj);
    },
    
    /*
     * Save images from dialog to current img-spot , used only with img-spot
     */
    imgSpotSave: function() {
        var img1URL = $('#imgspot-1').val(),
            img2URL = $('#imgspot-2').val(),
            $mainSpotImg = this.curSpot.$edSpot.find('img').eq(0);
        
        if (!Editor.loadImgSpot(Editor.curSpot, img1URL, img2URL, false)) {
            this.showError('One of images or both are not set');
            return;
        }
        
        //reset text fields
        $('#imgspot-1').val('');
        $('#imgspot-2').val('');
        
        //tell dialog close function that it's called after save to avoid deleting spot
        Editor.curSpot.$edSpot.data('img-spot-save', true);
        
        $('#img-spot-dialog').dialog('close');
    },
    
    /*
     * Load images into image spot & check boundaries to assure
     * that the images will be put inside the picture
     * boundary checking is done when first creating the image spot
     * it won't be checked again when loading code
     */
    loadImgSpot: function (spotObj, img1URL, img2URL, skipBoundaryCheck) {
        var $mainSpotImg= spotObj.$edSpot.find('img').eq(0);
        
        if (!img1URL || !img2URL) {
            return false;
        }
        
        $mainSpotImg.attr('src', img1URL);
        spotObj.$edSpot.find('img').eq(1).attr('src', img2URL);
        
        spotObj.$prevSpot.find('img').eq(0).attr('src', img1URL);
        spotObj.$prevSpot.find('img').eq(1).attr('src', img2URL);
        
        if (!skipBoundaryCheck) {
            //put newly loaded img inside the boundaries of the edited image
            $mainSpotImg.bind('load.boundaryCheck', function () {
                var $this = $(this),
                imgLeft = spotObj.$edSpot.position().left,
                imgW = $this.width(),
                imgTop = spotObj.$edSpot.position().top,
                imgH = $this.height(),
                edImgW = Editor.$edImg.width(),
                edImgH = Editor.$edImg.height();
                
                if (imgLeft + imgW + 4 > edImgW) {
                    var x= parseFloat(spotObj.$edSpot.css('left')),
                        y= parseFloat(spotObj.$edSpot.css('top'));
                        
                    spotObj.$edSpot.css('left', edImgW - imgW - 4);
                    spotObj.coord= [x, y];
                }
            
                if (imgTop + imgH + 4 > edImgH) {
                    var x= parseFloat(spotObj.$edSpot.css('left')),
                        y= parseFloat(spotObj.$edSpot.css('top'));
                    
                    spotObj.$edSpot.css('top', edImgH - imgH - 4);
                    spotObj.coord= [x, y];
                }
            });
            
            if ($mainSpotImg[0].complete) {
                $mainSpotImg.trigger('load.boundaryCheck');
            }
        }
        
        return true;
    },
    
    /*
     * Save tooltip contents , attached to tooltip content dialog save button
     */
    ttContentSave: function() {
        this.curSpot.ttContent= $('#tt-content').val();
        $('#tt-content-dialog').dialog('close');
    },
    
    /*
     * Show error dialog with assigned txt
     * 
     * @param txt text to put in error dialog
     */
    showError: function(txt) {
        $('#error-txt').text(txt);
        $('#error-dialog').dialog('open');
    },
    
    /*
     * Make the current selected spot focused , called when getting new image
     * to make red-spot as default also whenever you select a new spot
     */
    focusSpotBtn: function(spot) {
        //blur others
        $('.spot-bar button').removeClass('btn-focus');
        
        //focus target
        $('#' + spot + '-btn').addClass('btn-focus');
    },
    
    /*
     * Create new spot , get spot object & save it in spotPool & select it
     * Apply draggable & resizable if applicable to current spot type
     * 
     * @param spot Spot Object 
     */
    newSpot: function(spot) {
        this.spotPool.push(spot);
        
        //select when mouse down
        spot.$edSpot.bind('mousedown', function() {
            Editor.toggleSelect($(this).data('spotObj'));
        });
        
        if (spot.spotClass != 'sniper-spot' && spot.spotClass != 'img-spot') {
            spot.$edSpot.resizable({
                containment: Editor.$edImg,
                stop: $.proxy(Editor.updateSpotDim, Editor)
            });
        }
        
        spot.$edSpot.draggable({
            containment: Editor.$edImg,
            stop: $.proxy(Editor.updateSpotPos, Editor)
        });
        
        //ensure spot animation is checked for aim effect
        this.optionBar['spAnim'].prop('checked', true);
        
        this.toggleSelect(spot);
    },
    /*
     * Use to select spot & update options bar with it's properties & 
     * save last selected spot data because when selecting new spots ,
     * the spot options form change event will not be triggered
     * 
     * @param spot Spot object
     */
    toggleSelect: function(spot) {
        //hide previous selected spot if there were one
        if (this.curSpot) {          
            //save options , we do this because change event won't fire
            //if the user selected another spot so we save prev spot options
            this._saveOptions();
            
            this.curSpot.unselect();
        }
        
        this.curSpot= spot;
        
        spot.select();
        
        this._updateOptions();
        this.adjustOptionsBar();
        this.showOptionBar();
    },
    
    /*
     * Update options bar with current selected spot options
     * 
     * @private
     */
    _updateOptions: function() {
        var options= this.curSpot.options;
        
        for (var i in this.optionBar) {
            if (i == 'spBradius') {
                if (this.curSpot.dim) {
                    this.optionBar['spBradius'].val(this.curSpot.dim[2]);
                }
                continue;
            }
            
            //if the option= null that means it's disabled 
            //because it's incompatible so we don't update it
            if (options[i] === null) { continue; }
            
            if (i == 'spAnim') {
                if (options['spAnim'] === 'true') {
                    this.optionBar['spAnim'].prop('checked', true);
                }else{
                    this.optionBar['spAnim'].prop('checked', false);
                }
            }
            
            this.optionBar[i].val(options[i]);
        }
        
        //get spot link if any
        $('#sp-link').val(this.curSpot.spLink);
        if (this.curSpot.spLinknWin) {
            $('#sp-link-n-win').prop('checked', true);
        }else{
            $('#sp-link-n-win').prop('checked', false);
        }
    },
    
    /*
     * Save options from options bar to selected spot object
     * 
     * @private
     */
    _saveOptions: function() {
        var options= this.curSpot.options;
        
        for (var i in this.optionBar) {
            if (i == 'spBradius') {
                if (this.isEnabled('spBradius')) {
                    this.curSpot.dim[2]= parseInt(this.optionBar['spBradius'].val());
                }
                continue;
            }
            
            if (!this.isEnabled(i)) {
                options[i]= null;
                continue;
            }
            
            if (i == 'spAnim') {
                options[i]= this.optionBar[i].is(':checked').toString();
                continue;
            }
            
            options[i]= this.optionBar[i].val();
        }
    },
    
    /*
     * Update current spot obj dimensions , called whenever there is a change
     * to spot dimension
     */
    updateSpotDim: function() {
        //take outer Dimension to account for borders , that's usefull for borderless
        //spots like glass & shadow because when this function is called on any of them
        //they will be in selection mode 'ie: have selection blue border' and we need to make the
        //visible selected spot exactly like the one shown in preview so we take outer Dim
        //then the plugin will also use outer Dim to set because of spots with borders like
        //red spots
        //note that borderless spots will be resized in editor to be smaller when selected
        //because of the border added , then will return to it's normal dim when unselected
        var w= this.curSpot.$edSpot.outerWidth(),
            h= this.curSpot.$edSpot.outerHeight();
        
        if (this.curSpot.spotClass == 'glass-spot' || this.curSpot.spotClass == 'shadow-spot') {
            w= this.curSpot.$edSpot.outerWidth();
            h= this.curSpot.$edSpot.outerHeight();
        }else{
            w= this.curSpot.$edSpot.width();
            h= this.curSpot.$edSpot.height();
        }
            
        this.curSpot.dim[0]= w;
        this.curSpot.dim[1]= h;
		
        $('#spotWidth').attr('value', w + 'px'); 
        $('#spotHeight').attr('value', h + 'px');		
		
    },
    
    /*
     * Update current spot obj position , called whenever there is a change
     * to spot position
     */
    updateSpotPos: function() {
        var x= parseFloat(this.curSpot.$edSpot.css('left')),
            y= parseFloat(this.curSpot.$edSpot.css('top'));
            
        this.curSpot.coord= [x, y];
		
		$('#spotLeft').attr('value', x + 'px'); 
		$('#spotTop').attr('value', y + 'px');
    },
    
    /*
     * View the supplied option in spot options panel
     * 
     * @param name Option name
     */
    _enableOption: function (name) {
        this.optionBar[name].parents('tr').css('display','');
    },
    
    /*
     * Hide the supplied option in spot options panel
     * 
     * @param name Option name
     */
    _disableOption: function (name) {
        this.optionBar[name].parents('tr').css('display', 'none');
    },
    
    /*
     * Check whether the supplied option is available in spot option panel
     * 
     * @param name Option name
     */
    isEnabled: function(name) {
        if (this.optionBar[name].parents('tr').css('display') == 'none') {
            return false;
        }else{
            return true;
        }
    },
    
    /*
     * Show spot option bar
     */
    showOptionBar: function() {
        $('.spot-options').show();
    },
    
    /*
     * Hide spot option bar
     */
    hideOptionBar: function() {
        $('.spot-options').hide();
    },
    
    /*
     * Remove incompatible options , called when there's change in option bar
     */
    adjustOptionsBar: function() {
        if (this.optionBar['spType'].val() == 'General') {
            this._disableOption('spAnim');
            
            this._enableOption('ttType');
            this._enableOption('ttAnim');
            this._enableOption('ttWidth');
            this._enableOption('ttPos');
            if (this.optionBar['ttType'].val() == 'tip') {
                this._enableOption('ttDir');
                this._enableOption('ttColor');
            }else{
                this._disableOption('ttDir');
                this._disableOption('ttColor');
            }
        }
        
        if (this.optionBar['spType'].val() == 'Aim') {
            this._enableOption('spAnim');
            
            this._disableOption('ttType');
            this._disableOption('ttAnim');
            
            this._enableOption('ttDir');
            if (this.optionBar['ttDir'].val() == 'right' || this.optionBar['ttDir'].val() == 'left') {
                this._enableOption('ttWidth');
            }else{
                this._disableOption('ttWidth');
            }
            
            this._disableOption('ttPos');
            this._disableOption('ttColor');
        }
        
        if (this.curSpot.spotClass == 'sniper-spot' || this.curSpot.spotClass == 'img-spot' || this.curSpot.spotClass == 'invisible-spot') {
            this._disableOption('spBradius');
        }else{
            this._enableOption('spBradius');
        }
        
        if (this.curSpot.spotClass == 'invisible-spot' || this.curSpot.spotClass == 'onhover-spot') {
            this.optionBar['spActiveon'].find('option').eq(1).attr('disabled', ''); //disable click
        }else{
            this.optionBar['spActiveon'].find('option').eq(1).removeAttr('disabled');
        }
    },
    
    /*
     * Called when option bar form changes to remove incompatible options
     * & save current spot data 
     */
    optionChange: function () {
        this.adjustOptionsBar();
        this._saveOptions(); //
    },
    
    /*
     * Remove new lines '\n' & space between HTML tags,
     * mainly used to be able to include the generated code
     * in a wordpress post , as wordpress WYSIWYG editor will
     * automatically add <p> tags & <br> if the code were formatted
     */
    removeFormatting: function (markup) {
        var result;
        
        result = markup.replace(/\r|\n/gm, '');
        result = result.replace(/>\s+</gm, '><');
        
        return result;
    },
    
    bindHotKeys: function () {
        //Trigger first button 'action button' in dialogs when user press Enter 
        //& avoid doing that when user are foucsing a textarea , select , button
        //or input with type button
        $(document).delegate('.ui-dialog', 'keyup', function(e) {
            var tagName= e.target.tagName.toLowerCase();
            
            tagName= (tagName == 'input' && e.target.type == 'button') ? 'button' : tagName;
            
            if (e.which == $.ui.keyCode.ENTER && tagName != 'textarea' && tagName != 'select' && tagName != 'button') {
                $(this).find('button').eq(0).trigger('click');
                return false;
            }
        });
        
        //preview: ctrl + alt
        $(document).bind('keydown', function (e) {
            if (e.ctrlKey && e.altKey) {
                Editor.togglePreview();
            }
        });
        
        Editor.$edDiv.bind('click', function () {
            $(this).focus();
        });
        
        //==============================
        //move spot with arrow keys
        Editor.$edDiv.bind('keydown', function (e) {
            if (Editor.curSpot) {
                var val;
            
                switch (e.which) {
                    case $.ui.keyCode.UP:
                        val= parseFloat(Editor.curSpot.$edSpot.css('top'));
                    
                        if (val > 0) {
                            Editor.curSpot.$edSpot.css('top', val - 1);
                            Editor.updateSpotPos();
                        }
                        
                        e.preventDefault();
                        break;
                    
                    //note on DOWN & RIGHT we can't depend on browser return
                    //values for css 'right, bottom', in chrome it returns auto
                    //so we calculate boundries with image dimesions
                    case $.ui.keyCode.DOWN:
                        var imgH, spotHeight;
                        
                        val = parseFloat(Editor.curSpot.$edSpot.css('top'));
                        imgH = Editor.$edImg.height(); 
                        spotHeight = Editor.curSpot.$edSpot.outerHeight();
                        
                        if (val + spotHeight  < imgH) {
                            Editor.curSpot.$edSpot.css('top', val + 1);
                            Editor.updateSpotPos();
                        }
                    
                        e.preventDefault();
                        break;
                    
                    case $.ui.keyCode.RIGHT:
                        var imgW, spotWidth;
                        
                        val= parseFloat(Editor.curSpot.$edSpot.css('left'));
                        imgW = Editor.$edImg.width(); 
                        spotWidth = Editor.curSpot.$edSpot.outerWidth();
                        
                        if (val + spotWidth  < imgW) {
                            Editor.curSpot.$edSpot.css('left', val + 1);
                            Editor.updateSpotPos();
                        }
                    
                        e.preventDefault();
                        break;
                    
                    case $.ui.keyCode.LEFT:
                        val= parseFloat(Editor.curSpot.$edSpot.css('left'));
                    
                        if (val > 0) {
                            Editor.curSpot.$edSpot.css('left', val - 1);
                            Editor.updateSpotPos();
                        }
                    
                        e.preventDefault();
                        break;
                }
            }
        });
        
        //bind delete: del , clone: ctrl + c
        Editor.$edDiv.bind('keyup', function (e) {
            if (e.which == $.ui.keyCode.DELETE && Editor.curSpot) {
                Editor.delSpot();
                
                e.preventDefault();
            }
            
            if (e.ctrlKey && e.which == 67 && Editor.curSpot) {
                Editor.cloneSpot();
                
                e.preventDefault();
            }
        });

    }
};

$(document).ready($.proxy(Editor.init, Editor));
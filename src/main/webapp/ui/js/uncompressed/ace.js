if(! ('ace' in window) ) window['ace'] = {}
jQuery(function($) {
	//at some places we try to use 'tap' event instead of 'click' if jquery mobile plugin is available
	window['ace'].click_event = $.fn.tap ? "tap" : "click";
});

jQuery(function($) {
	//ace.click_event defined in ace-elements.js
	ace.handle_side_menu(jQuery);

	ace.enable_search_ahead(jQuery);	

	ace.widget_boxes(jQuery);
	
	/**
	//make sidebar scrollbar when it is fixed and some parts of it is out of view
	//>> you should include jquery-ui and slimscroll javascript files in your file
	//>> you can call this function when sidebar is clicked to be fixed
	$('.nav-list').slimScroll({
		height: '400px',
		distance:0,
		size : '6px'
	});
	*/
});



ace.handle_side_menu = function($) {
	$('#menu-toggler').on(ace.click_event, function() {
		$('#sidebar').toggleClass('display');
		$(this).toggleClass('display');
		return false;
	});
	//mini
	var $minimized = $('#sidebar').hasClass('menu-min');
	$('#sidebar-collapse').on(ace.click_event, function(){
		$minimized = $('#sidebar').hasClass('menu-min');
		ace.settings.sidebar_collapsed(!$minimized);//@ ace-extra.js
	});

	var touch = "ontouchend" in document;
	//opening submenu
	$('.nav-list').on(ace.click_event, function(e){
		//check to see if we have clicked on an element which is inside a .dropdown-toggle element?!
		//if so, it means we should toggle a submenu
		var link_element = $(e.target).closest('a');
		if(!link_element || link_element.length == 0) return;//if not clicked inside a link element
		
		$minimized = $('#sidebar').hasClass('menu-min');
		
		if(! link_element.hasClass('dropdown-toggle') ) {//it doesn't have a submenu return
			//just one thing before we return
			//if sidebar is collapsed(minimized) and we click on a first level menu item
			//and the click is on the icon, not on the menu text then let's cancel event and cancel navigation
			//Good for touch devices, that when the icon is tapped to see the menu text, navigation is cancelled
			//navigation is only done when menu text is tapped
			if($minimized && ace.click_event == "tap" &&
				link_element.get(0).parentNode.parentNode == this /*.nav-list*/ )//i.e. only level-1 links
			{
					var text = link_element.find('.menu-text').get(0);
					if( e.target != text && !$.contains(text , e.target) )//not clicking on the text or its children
					  return false;
			}

			return;
		}
		//
		var sub = link_element.next().get(0);

		//if we are opening this submenu, close all other submenus except the ".active" one
		if(! $(sub).is(':visible') ) {//if not open and visible, let's open it and make it visible
		  var parent_ul = $(sub.parentNode).closest('ul');
		  if($minimized && parent_ul.hasClass('nav-list')) return;
		  
		  parent_ul.find('> .open > .submenu').each(function(){
			//close all other open submenus except for the active one
			if(this != sub && !$(this.parentNode).hasClass('active')) {
				$(this).slideUp(200).parent().removeClass('open');
				
				//uncomment the following line to close all submenus on deeper levels when closing a submenu
				//$(this).find('.open > .submenu').slideUp(0).parent().removeClass('open');
			}
		  });
		} else {
			//uncomment the following line to close all submenus on deeper levels when closing a submenu
			//$(sub).find('.open > .submenu').slideUp(0).parent().removeClass('open');
		}

		if($minimized && $(sub.parentNode.parentNode).hasClass('nav-list')) return false;

		$(sub).slideToggle(200).parent().toggleClass('open');
		return false;
	 })
}



ace.widget_boxes = function($) {
	$(document).on('hide.bs.collapse show.bs.collapse', function (ev) {
		var hidden_id = ev.target.getAttribute('id')
		$('[href*="#'+ hidden_id+'"]').find('[class*="icon-"]').each(function(){
			var $icon = $(this)

			var $match
			var $icon_down = null
			var $icon_up = null
			if( ($icon_down = $icon.attr('data-icon-show')) ) {
				$icon_up = $icon.attr('data-icon-hide')
			}
			else if( $match = $icon.attr('class').match(/icon\-(.*)\-(up|down)/) ) {
				$icon_down = 'icon-'+$match[1]+'-down'
				$icon_up = 'icon-'+$match[1]+'-up'
			}

			if($icon_down) {
				if(ev.type == 'show') $icon.removeClass($icon_down).addClass($icon_up)
					else $icon.removeClass($icon_up).addClass($icon_down)
					
				return false;//ignore other icons that match, one is enough
			}

		});
	})


	$(document).on('click.ace.widget', '[data-action]', function (ev) {
		ev.preventDefault();

		var $this = $(this);
		var $action = $this.data('action');
		var $box = $this.closest('.widget-box');

		if($box.hasClass('ui-sortable-helper')) return;

		if($action == 'collapse') {
			var event_name = $box.hasClass('collapsed') ? 'show' : 'hide';
			var event_complete_name = event_name == 'show' ? 'shown' : 'hidden';

			
			var event
			$box.trigger(event = $.Event(event_name+'.ace.widget'))
			if (event.isDefaultPrevented()) return
		
			var $body = $box.find('.widget-body');
			var $icon = $this.find('[class*=icon-]').eq(0);
			var $match = $icon.attr('class').match(/icon\-(.*)\-(up|down)/);
			var $icon_down = 'icon-'+$match[1]+'-down';
			var $icon_up = 'icon-'+$match[1]+'-up';
			
			var $body_inner = $body.find('.widget-body-inner')
			if($body_inner.length == 0) {
				$body = $body.wrapInner('<div class="widget-body-inner"></div>').find(':first-child').eq(0);
			} else $body = $body_inner.eq(0);


			var expandSpeed   = 300;
			var collapseSpeed = 200;

			if( event_name == 'show' ) {
				if($icon) $icon.addClass($icon_up).removeClass($icon_down);
				$box.removeClass('collapsed');
				$body.slideUp(0 , function(){$body.slideDown(expandSpeed, function(){$box.trigger(event = $.Event(event_complete_name+'.ace.widget'))})});
			}
			else {
				if($icon) $icon.addClass($icon_down).removeClass($icon_up);
				$body.slideUp(collapseSpeed, function(){$box.addClass('collapsed');$box.trigger(event = $.Event(event_complete_name+'.ace.widget'))});
			}

			
		}
		else if($action == 'close') {
			var event
			$box.trigger(event = $.Event('close.ace.widget'))
			if (event.isDefaultPrevented()) return
			
			var closeSpeed = parseInt($this.data('close-speed')) || 300;
			$box.hide(closeSpeed , function(){$box.trigger(event = $.Event('closed.ace.widget'));$box.remove();});
		}
		else if($action == 'reload') {
			var event
			$box.trigger(event = $.Event('reload.ace.widget'))
			if (event.isDefaultPrevented()) return

			$this.blur();

			var $remove = false;
			if($box.css('position') == 'static') {$remove = true; $box.addClass('position-relative');}
			$box.append('<div class="widget-box-overlay"><i class="icon-spinner icon-spin icon-2x white"></i></div>');
			
			$box.one('reloaded.ace.widget', function() {
				$box.find('.widget-box-overlay').remove();
				if($remove) $box.removeClass('position-relative');
			});

		}
		else if($action == 'settings') {
			var event = $.Event('settings.ace.widget')
			$box.trigger(event)
		}

	});
}




//search box's dropdown autocomplete
ace.enable_search_ahead = function($) {
	ace.variable_US_STATES = ["Alabama","Alaska","Arizona","Arkansas","California","Colorado","Connecticut","Delaware","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Dakota","North Carolina","Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"]
	
	try {
		$('#nav-search-input').typeahead({
			source: ace.variable_US_STATES,
			updater:function (item) {
				$('#nav-search-input').focus();
				return item;
			}
		});
	} catch(e) {}
}


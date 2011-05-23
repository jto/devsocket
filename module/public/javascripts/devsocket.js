$(function(){
    var socket = new WebSocket('ws://localhost:9000/devsocket/');
    socket.onmessage = function(e){
		$('link[href^=' + e.data + ']').each(function(){
			var $this = $(this),
				h = $this.attr('href');
			h = h.replace(/(&|%5C?)forceReload=\d+/,'')
			var h2 = h + ( h.indexOf('?') >= 0 ? '&' : '?' ) + 'forceReload=' + new Date().getTime()
			$this.attr('href', h2)
		})
    }; 
    socket.onerror = function(e){
        console.error(e)
    }; 
    socket.onclose = function(){
        console.log('closed')
    }  
})
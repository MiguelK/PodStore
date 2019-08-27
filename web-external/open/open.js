console.clear();



function fetch(){

    $.urlParam = function(name){
        var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
        if (results==null) {
            return null;
        }
        return decodeURI(results[1]) || 0;
    }

    var term = $.urlParam('pid');

    var $results = $('<div class="results" />').appendTo(document.body);


    $.ajax({
        url: 'https://itunes.apple.com/lookup',
        crossDomain: true,
        dataType: 'jsonp',
        data: {
            id: term,
            entity: 'podcast',
            limit: 2,
            explicit: 'No'
        },
        method: 'GET',
        success: function(data){
            console.log(data);

            $results.empty();

            $.each(data.results,function(i,result){
                if ( i > 23 ) { return false; }

                var hires = result.artworkUrl100.replace('100x100','580x580');
                var pid = result.collectionId;
                var podCastName = result.collectionName;

                var link = 'https://qw7xh.app.goo.gl?link=http://www.podsapp.se?pid%3D' + pid + '&isi=1209200428&ibi=com.app.Pods&st=S%C3%A5%20funkar%20det&sd=@Pods&si=https://is3-ssl.mzstatic.com/image/thumb/Podcasts123/v4/1b/9f/21/1b9f2156-e11c-c341-3ba7-fac9fa3ec328/mza_1327199418451956232.jpg/600x600bb.jpg';
                $results[0].insertAdjacentHTML('beforeend','<a class="result" href="'+link+'" target="_blank"><img src="'+hires+'" onerror="src='+result.artworkUrl100+'" /> ' +
                    '<div class="result__text"> <span class="artist-name">'+result.collectionName+'</span> <span class="track-name">'+result.collectionName+'</span></div>' +
                    '<h1 style="color: black"><br>' + podCastName + '</h1><p><button onclick="window.location.href = link" class="w3-button w3-red w3-round-xxlarge">Open in Pods</button></p>');
            });
        },
        error: function(e){
            console.log(e);
        }
    });
}

$input.on('blur keydown',function(){
    if ( !event.keyCode || event.keyCode == 13 ) {
        fetch($input.val());
    }
});

fetch($input.val());
function getRandomSize(min, max) {
  return Math.round(Math.random() * (max - min) + min);
}

var allImages = "";

/*for (var i = 0; i < 25; i++) {
  var width = getRandomSize(200, 400);
  var height =  getRandomSize(200, 400);
  allImages += '<img src="https://placekitten.com/'+width+'/'+height+'" alt="pretty kitty">';
 with="' + width + 'px" height="' + height + 'px"
}*/

for (var i = 0; i < 100; i++) {
    var width = getRandomSize(100, 400);
    var height =  getRandomSize(100, 400);

    var name = "podcast-1.jpg";
    var link = "https://qw7xh.app.goo.gl/Q300vAsLlvaInDls1";
    allImages += '<a href="' + link + '"> <img src="http://pods.one/Podcast-Wall/images/' + name + '" alt="pretty kitty"  "></a>';
}

$('#photos').append(allImages);
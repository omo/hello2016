

(function() {
  var containerDiv = null;

  function addOneFrame() {
    var iframe = document.createElement('iframe');
    iframe.src = 'http://example.com/test';
    iframe.style = 'width: 10px; height: 10px;';
    containerDiv.appendChild(iframe);
  }

  function addButtonClicked() {
    console.log('Hello!');
    for (var i = 0; i < 10; ++i) {
      addOneFrame();
    }
  }

  function init() {
    document.getElementById('addButton').onclick = addButtonClicked;
    containerDiv = document.getElementById('containerDiv');
  }

  window.addEventListener('load', init);
})();

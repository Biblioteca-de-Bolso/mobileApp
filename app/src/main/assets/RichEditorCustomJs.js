RE.removeBackgroundColor = function() {
    RE.restorerange();
    document.execCommand("styleWithCSS", null, true);
    document.execCommand('backColor', false, 'transparent');
    document.execCommand("styleWithCSS", null, false);
}
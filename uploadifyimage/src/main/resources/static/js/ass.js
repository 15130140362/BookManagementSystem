$(function () {
    $("#chooseImage").uploadify({
        'swf': 'uploadify/uploadify.swf',
        'uploader': '/librarian/addBook',
        'queueID': 'fileQueue',
        'fileSizeLimit': '50MB',
        'buttonText': 'Select Image',
        'simUploadLimit': 1,
        'method':'get',
        'queueSizeLimit': 1,
        'auto':true,
        'fileExt': '*.jpg;*.gif;*.jpeg;*.png',
        onComplete: function (event, queueID, fileObj, response, data) {},
        onSelect: function (){},
        onError: function (event, queueID, fileObj) {
            alert("文件:" + fileObj.name + "上传失败");
        }
    });
});
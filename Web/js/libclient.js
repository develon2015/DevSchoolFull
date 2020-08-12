function formatDate(unixtime, callback) {
    var options = {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long',
        hour: 'numeric',
        minute: 'numeric',
        // second: 'numeric',
        hour12: false,
    };
    if (callback) callback(options);
    var date = new Date(unixtime);
    return Intl.DateTimeFormat("zh-CN", options).format(date);
};

function copyText(text) {
    var textarea = document.createElement("textarea");//创建input对象
    var currentFocus = document.activeElement;//当前获得焦点的元素
    document.body.appendChild(textarea);//添加元素
    textarea.value = text;
    textarea.focus();
    if (textarea.setSelectionRange)
        textarea.setSelectionRange(0, textarea.value.length);//获取光标起始位置到结束位置
    else
        textarea.select();
    try {
        var flag = document.execCommand("copy");//执行复制
    } catch(error) {
        var flag = false;
    }
    document.body.removeChild(textarea);//删除元素
    currentFocus.focus();
    return flag;
}

export {
    formatDate,
    copyText,
};
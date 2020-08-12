const child_process = require('child_process');
const worker = require('worker_threads');

function getData() {
    return new Promise((resolve, reject) => {
        // 这里开启了一个新的工作线程，工作耗时2s
        const th = new worker.Worker(__filename);
        th.once('message', data => {
            resolve(data);
        })
    });
}

async function asyncTask() {
    var data = await getData();
    console.log('主线程', worker.threadId, ': 很明显，工作线程计算完毕了，结果是多少呢，我已经返回给调用者了');
    return data
}

function main() {
    console.log('主线程', worker.threadId, ': 开始你的表演show');
    asyncTask().then(data => {
        console.log('主线程', worker.threadId, ': 我当然知道结果是', data);
    });
    console.log('主线程', worker.threadId, ': 你慢慢来，别着急哈！');
}

if (worker.isMainThread) {
    main();
} else {
    console.log('工作线程', worker.threadId, ': 开始堵塞');
    child_process.execSync(`sleep 2`);
    console.log('工作线程', worker.threadId, ': 我好了');
    worker.parentPort.postMessage(2020)
}
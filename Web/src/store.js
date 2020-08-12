// import Vue from 'vue';
// import Vuex from 'vuex';

Vue.use(Vuex);
export default new Vuex.Store({
    state: {
        /** 登录用户 */
        user: null,
        /** 登录状态 */
        isLogged: false,
        /** 待上传文件 */
        uploadFiles: [],
    }, // end of state
    mutations: {
        loginStatus(state, isLogged) {
            state.isLogged = isLogged;
        },
        saveUser(state, user) {
            state.user = user;
        },
        uploadFiles(state, uploadFile) {
            var index = 1;
            state.uploadFiles.forEach(it => {
                const f = it.file;
                if (uploadFile.name == f.name && uploadFile.size == f.size) {
                    throw '此文件已存在';
                }
                index = it.index >= index? it.index+1 : index;
            });
            var fileWrapper = {
                index,
                file: uploadFile,
                uploading: true,
                progress: 0,
            };
            state.uploadFiles.push(fileWrapper);
            var form = new FormData();
            form.append('auth',
                new Blob([localStorage.getItem('auth')],
                    { type: "application/json", },
                ),
            );
            form.append('file', uploadFile); // or fileWrapper.file
            api.auth().post('resource/upload', form, {
                headers: {
                        // "Content-Type": undefined,
                },
                // 上传进度事件
                onUploadProgress: progressEvent => {
                    console.log({loaded: progressEvent.loaded, total: progressEvent.total});
                    let complete = (progressEvent.loaded / progressEvent.total * 100 | 0);
                    if (complete < 100) {
                        fileWrapper.progress = complete;
                    } else {
                        fileWrapper.progress = 99;
                    }
                },
            }).then(res => {
                setTimeout(() => {
                    fileWrapper.progress = 100;
                }, 2000);
               console.dir(res.data);
            }).catch(err => {
                console.dir(err);
                fileWrapper.progress = -1;
            });
            console.log('上传文件');
            console.dir(uploadFile);
        }, // end of uploadFiles
        finish(state, index) {
            state.uploadFiles.forEach((it, i) => {
                if (it.index == index) {
                    state.uploadFiles.splice(i, 1);
                    return;
                }
            });
        },
    }, // end of mutations
});
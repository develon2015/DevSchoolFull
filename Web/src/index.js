// import Vue from 'vue';
import router from './router';
import store from './store';
import './mixins';
import axios from 'axios';
import App from './page/App.vue';
import { formatDate, copyText, } from '../js/libclient';
window.copyText = copyText;
window.formatDate = formatDate;

// use Buefy
Vue.use(Buefy);
// config axios instance
var host = 'localhost';
var port = 8080;
if (true) {
    host = location.host;
    port = location.port == '' ? 80 : location.port;
}
const api = {
    api: axios.create({
        baseURL: `http://${ host }:${ port }/api`,
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
    }),
    post(...varargs) {
        return this.api.post(...varargs);
    },
    get(...varargs) {
        return this.api.get(...varargs);
    },
    auth_api: axios.create({
        baseURL: `http://${ host }:${ port }/api`,
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
    }),
    auth() {
        let _this = this;
        let api = this.auth_api;
        return {
            post(...varargs) {
                if (JSON.parse(localStorage.getItem('auth')) == null) {
                    _this.requireLogin();
                    return Promise.reject('未认证');
                }
                return api.post(...varargs);
            },
        };
    }, // end of auth()
    requireLogin: _.debounce(() => { // 弹出登录提示框，需要防抖动
            if (!!!window.app) return;
            window.app.$buefy.dialog.confirm({
                title: '需要登录',
                message: '当前操作需要登录, 是否前往登录？',
                confirmText: '登录',
                cancelText: '取消',
                type: 'is-danger',
                hasIcon: true,
                onConfirm: () => window.app.$router.push({ name: '登录' })
                    .catch(error => { }),
            });
        }, 2000, {
            leading: true,
            trailing: false,
    }),
};
// 测试API
const test = axios.create({
    baseURL: 'http://127.0.0.1:8080/test',
    headers: {
        'Content-Type': 'application/json;charset=UTF-8',
    },
});
// export into global scoped
window.api = api;
window.test = test;
// app inst
let app = new Vue({
    router,
    store,
    el: '#app',
    render: h => h(App),
});
window.app = app;
// 配置拦截器
api.auth_api.interceptors.request.use(config => {
        console.dir(config);
        // api.requireLogin(); // can also check login in here
        if (config.data instanceof FormData) {
            config.data.append('auth',
                new Blob([localStorage.getItem('auth')], { type: "application/json", })
            );
        } else {
            config.data['auth'] = app.getAuth();
            console.log('请求数据');
            console.dir(config.data);
        }
        return config;
    }, error => {
        console.log('请求错误');
        console.dir(error);
        return Promise.reject(error);
});
const authFailed = _.debounce(() => { // 弹出登录提示框，需要防抖动
        if (!!!window.app) return;
        window.app.$buefy.dialog.confirm({
            title: '登录失效',
            message: '当前登录已失效, 是否重新登录？',
            confirmText: '登录',
            cancelText: '取消',
            type: 'is-danger',
            hasIcon: true,
            onConfirm: () => window.app.$router.push({ name: '登录' })
                .catch(error => { }),
        });
    }, 2000, {
        leading: true,
        trailing: false,
});
api.auth_api.interceptors.response.use(res => {
        return res;
    }, error => {
        console.dir(error);
        if (error.response && error.response.status == 401) {
            app.setAuth(null);
            app.refresh();
            authFailed();
        }
        return Promise.reject(error);
});
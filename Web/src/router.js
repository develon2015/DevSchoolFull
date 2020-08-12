// import Vue from 'vue';
// import Router from 'vue-router';
import Index from './page/Index.vue';
import Test from "./page/Test.vue";
import Login from "./page/Login.vue";
import Timer from "./page/Timer.vue";
import Space from "./page/Space.vue";
import Upload from "./page/Upload.vue";
import Play from "./page/Play.vue";
import Live from "./page/Live.vue";
import SearchFile from "./page/SearchFile.vue";
import SearchLive from "./page/SearchLive.vue";
import Order from "./page/Order.vue";

Vue.use(Router);
const router = new Router({
    mode: 'hash',
    routes: [
        {
            name: '首页',
            path: '/',
            component: Index,
        },
        {
            name: '预订直播',
            path: '/order',
            component: Order,
        },
        {
            name: '搜索直播',
            path: '/search/live',
            component: SearchLive,
        },
        {
            name: '搜索资源',
            path: '/search/file',
            component: SearchFile,
        },
        {
            name: '个人中心',
            path: '/space',
            component: Space,
        },
        {
            name: '上传教学资源',
            path: '/share',
            component: Upload,
        },
        {
            name: '在线教学直播间',
            path: `/live/:id(\\d+)`,
            component: Live,
        },
        {
            name: '视频播放',
            path: `/play/:video(.+)`,
            component: Play,
        },
        {
            name: '测试',
            path: '/test',
            component: Test,
        },
        {
            name: '登录',
            path: '/login',
            component: Login,
        },
        {
            name: '计时',
            path: '/timer',
            component: Timer,
        },
    ],
});

export default router;
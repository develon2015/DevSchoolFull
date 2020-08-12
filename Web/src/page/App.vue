<template>
    <div class="app">
        <header>
            <b-navbar :spaced="false" :mobile-burger="true" :shadow="false" :close-on-click="true" type="is-danger">
                <template slot="brand">
                    <b-navbar-item tag="router-link" to="/">
                        <img :src="icon" class="d-title"><span class="d-title">&nbsp;DevSchool</span>
                    </b-navbar-item>
                </template>

                <template slot="start">
                    <b-navbar-item tag="router-link" to="/">
                        <span>首页</span>
                    </b-navbar-item>

                    <b-navbar-item tag="router-link" to="/search/file">
                        <div class="search-icon">
                            <b-icon type="is-warning" pack="fa" icon="folder"></b-icon>&nbsp;&nbsp;
                        </div>
                        <span>搜索资源</span>
                    </b-navbar-item>

                    <b-navbar-item tag="router-link" to="/search/live">
                        <div class="live-icon">
                            <b-icon type="is-warning" pack="fa" icon="video"></b-icon>&nbsp;&nbsp;&nbsp;
                        </div>
                        <span>搜索直播</span>
                    </b-navbar-item>

                    <b-navbar-dropdown v-if="false" label="菜单" :hoverable="false" :boxed="true">
                        <b-navbar-item v-for="(route, index) in computed_routes" :key="index" tag="router-link" :to="route">
                            <span>{{ route.name }}</span>
                        </b-navbar-item>
                    </b-navbar-dropdown>

                    <b-navbar-dropdown label="更多" :hoverable="false" :boxed="true">
                        <b-navbar-item> <span @click="howLive">如何推流？</span> </b-navbar-item>

                        <b-navbar-item> <span @click="agreement">用户协议</span> </b-navbar-item>

                        <b-navbar-item> <span @click="about">关于作者</span> </b-navbar-item>
                    </b-navbar-dropdown>
                </template>

               <template slot="end">
                    <b-navbar-item v-if="!isLogged">
                        <b-button type="is-success" size="is-medium" @click="login">登录</b-button>
                        &nbsp;
                        <b-button type="is-success" size="is-medium" @click="register">注册</b-button>
                    </b-navbar-item>

                    <b-navbar-item v-if="isLogged" tag="router-link" to="/space">
                        <b-tooltip label="进入个人中心" position="is-bottom" type="is-success" size="is-large">
                            <!-- <b-icon pack="fas" icon="user" size="" type="is-success"></b-icon> -->
                            <img style="transform: scale(1.5)" :src="require(`../assets/user.svg`).default" alt="">
                        </b-tooltip>
                    </b-navbar-item>

                    <b-navbar-dropdown :right="true" v-if="isLogged" :label="user.nickname" :hoverable="false" :boxed="true">
                        <b-navbar-item tag="router-link" to="/space">
                            <b-icon icon="home" type="is-success"></b-icon>&nbsp;&nbsp;个人中心
                        </b-navbar-item>
                        <b-navbar-item @click="myLive">
                            <b-icon pack="fas" class="icon-my-live" icon="tv" type="is-success"></b-icon>&nbsp;&nbsp;我的直播间
                        </b-navbar-item>
                        <b-navbar-item tag="router-link" to="/order">
                            <b-icon icon="video" type="is-success"></b-icon>&nbsp;&nbsp;发起教学直播
                        </b-navbar-item>
                        <b-navbar-item tag="router-link" to="/share">
                            <b-icon icon="upload" type="is-success"></b-icon>&nbsp;&nbsp;上传教学资源
                        </b-navbar-item>
                        <b-navbar-item @click="exit">
                            <b-icon icon="logout" type="is-success"></b-icon>&nbsp;&nbsp;退出登录
                        </b-navbar-item>
                    </b-navbar-dropdown>
                </template>
            </b-navbar>
        </header>

        <div class="router">
            <router-view></router-view>
        </div>
    </div>
</template>

<script>
    import icon from 'file-loader?name=[name].[ext]!../favicon.ico';
    import 'file-loader?name=fonts/[name].[ext]!../fonts/materialdesignicons-webfont.woff2';
    import 'file-loader?name=webfonts/[name].[ext]!../webfonts/fa-solid-900.woff2';

    export default {
        data() {
            return {
                icon,
                routes: [],
            };
        },
        computed: {
            computed_routes() {
                const r = [];
                this.routes.forEach(it => {
                    r.push(it);
                });
                return r;
            },
        },
        methods: {
            howLive() {
                this.$buefy.dialog.confirm({
                    title: '推流教程',
                    message: `
1. <a href='https://obsproject.com/'>下载并安装开源软件：OBS</a><br>
2. 预订直播，获取推流密钥<br>
3. 打开OBS，使用推流地址和推流密钥开始推流<br>
<br>
推流地址：<br>
rtmp://${ location.hostname }/live`,
                    confirmText: '复制推流地址',
                    cancelText: '了解',
                    type: 'is-success',
                    onConfirm: () => {
                        copyText(`rtmp://${ location.hostname }/live`);
                    },
                });
            },
            agreement() {
                this.$buefy.dialog.alert({
                    title: '用户协议',
                    message: `
DevSchool 作为一个UGC教学互动平台，一直鼓励用户的积极创作、共享知识、传播文化，我们尊重和鼓励用户创作的内容，
认识到保护知识产权对 DevSchool 生存与发展的重要性，承诺将保护知识产权作为 DevSchool 运营的基本原则之一。
基于该项基本原则，您应当确认在 DevSchool 上发表的全部原创内容，除另有约定外，著作权归用户本人所有。<br>
您在使用本服务时须遵守法律法规，不得利用本服务从事违法违规行为。<br>`,
                    confirmText: '确定',
                    type: 'is-success',
                });
            },
            about() {
                this.$buefy.dialog.alert({
                    title: '关于作者',
                    message: `
开发者：龙勇<br>
中南民族大学<br>
2020年5月`,
                    confirmText: '确定',
                    type: 'is-success',
                });
            },
            register() {
                this.$router.push({ name: '登录', query: { type: 'register' }})
                    .catch(error => { });
            },
            login() {
                this.$router.push({ name: '登录' })
                    .catch(error => { });
            },
            exit() {
                api.post('user/logout', {
                    action: 'logout',
                    auth: this.getAuth(),
                }).then(res => {
                    this.$buefy.toast.open({
                        message: '已退出登录',
                        type: 'is-success',
                        position: 'is-bottom',
                    });
                }).catch(err => {
                    console.dir(err);
                }).finally(() => {
                    this.setAuth(null);
                    this.$store.commit('saveUser', null);
                    this.$store.commit('loginStatus', false);
                });
            },
            myLive() {
                this.$router.push({ path: `/live/${ this.getAuth().uid }`, });
            },
        }, // end of methods
        mounted() {
            this.routes = this.$router.options.routes;
            window.addEventListener('storage', event => {
                switch(event.key) {
                    case 'auth': { // 用户身份发送变化
                        this.refresh();
                        break;
                    }
                }
            });
            this.refresh(); // 获取用户数据
        },
    }
</script>

<style scoped>
    header {
        padding: 10px 0;
        background: #ff3860;
        min-height: 81px;
    }
    .navbar-item {
        font-size: 20px;
    }
    .navbar-dropdown {
        transition: all 2s;
        background: #63b175;
    }
    /* 弹出菜单项 */
    .navbar-item-dropdown::before {
        content: "\1F449 ";
        display: none;
    }
    .d-title {
        font-style: italic;
        font-weight: bold;
        user-select: none;
    }
    .search-icon {
        font-size: 16px;
    }
    .live-icon {
        font-size: 16px;
    }
    .icon-my-live {
        font-size: 12px;
    }
</style>

<style>
    html {
        background: whitesmoke;
    }
    .flex {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }
</style>
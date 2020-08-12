/**
 * 公共mixins
 */
// import Vue from 'vue';
const mixins = [
    {
        computed: {
            ...Vuex.mapState(['isLogged', 'user']),
        },
        mounted() {
            // 修改标题
            try {
                if (this.$route && this.$route.name === '首页')
                    document.title = 'DevSchool - 计算机学科教学互动网站';
                else
                    document.title = this.$route.name;
            } catch(e) {}
        },
        methods: {
            /** 从本地存储获取身份验证信息 */
            getAuth() {
                const auth = localStorage.getItem('auth');
                return JSON.parse(auth);
            },
            /** 更新auth */
            setAuth(auth) {
                localStorage.setItem('auth', JSON.stringify(auth));
                this.refresh(); // 需要在App.vue中使用事件处理其它标签页导致的auth变化
            },
            /** 刷新用户身份信息, App.vue被挂载时应调用该方法，以及setAuth()之后 */
            refresh() {
                if (this.getAuth() != null) {
                    // 检查用户身份是否有效
                    api.post(`user/self`, this.getAuth()).then(res => {
                        const user = res.data[0]; // 用户隐私数据
                        this.$store.commit('saveUser', user);
                        this.$store.commit('loginStatus', true);
                    }).catch(err => {
                        this.$store.commit('saveUser', null);
                        this.$store.commit('loginStatus', false);
                        if (err.response && err.response.data.error == '未认证用户') {
                            console.log('请重新登录');
                            localStorage.removeItem('auth');
                        }
                        console.dir(err);
                    });
                } else {
                    this.$store.commit('saveUser', null);
                    this.$store.commit('loginStatus', false);
                }
            },
        },
        filters: {
            filesize(size) {
                const KB = 1000;
                if (size < KB)
                    return `${ size } Byte`;
                if (size < KB * KB)
                    return `${ Math.round(size / KB) } KB`;
                if (size < 1024 * 1024 * 1024)
                    return `${ (size / KB / KB).toFixed(2) } MB`;
                return `${ (size / KB / KB / KB).toFixed(2) } GB`;
            },
            showDate(value) {
                return formatDate(value, options => {
                    delete options.weekday;
                    delete options.hour;
                    delete options.minute;
                });
            },
        },
    },
];
mixins.forEach(it => {
    Vue.mixin(it);
});
export default mixins;
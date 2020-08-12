<template>
    <main class="flex">
        <div class="div-login">
            <div class="div-login-bg"></div>
            <div class="content flex" :is="getTempByType">
                <!-- <div :is="getTempByType"></div> -->
            </div>
        </div>
    </main>
</template>

<script>
    import axios from 'axios';
    import LoginComp from '../component/Login.vue';
    import RegisterComp from '../component/Register.vue';

    export default {
        components: { LoginComp, RegisterComp, },
        data() {
            return {
                type: '',
            };
        },
        computed: {
            getTempByType() {
                switch(this.type) {
                    case 'register': return 'register-comp';
                    default: return 'login-comp';
                }
            },
        },
        watch: {
            ['$route.query'](it) {
                this.type = it.type || 'login';
            },
        },
        mounted() {
            this.type = this.$route.query.type || 'login';
        },
    }
</script>

<style scoped>
    main {
        min-width: 100vw;
        min-height: calc(100vh - 81px);
        background: url('../assets/bg_login.jpg');
        background-repeat: no-repeat;
        background-attachment: fixed;
        background-size: 100% 100%;
    }
    .div-login {
        width: 800px;
        max-width: 90vw;
        height: 48vh;
        min-height: 400px;
        background: inherit;
        position: relative;
        top: -100px;
        top: -10vh;
        overflow: hidden;
        box-shadow: 12px 12px 22px 1px #63b17591;
        border-radius: 40px 0 40px 0;
    }
    .div-login-bg {
        position: absolute;
        top: -80px;
        bottom: -80px;
        left: -80px;
        right: -80px;
        filter: blur(50px);
        background: inherit;
    }
    .content {
        position: absolute;
        width: 100%;
        height: 100%;
    }
</style>
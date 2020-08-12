<template>
    <div>
        <h1>
            <b-icon pack="fas" icon="user" size="is-small" type="is-success"></b-icon>
            &nbsp;登录
        </h1>

        <b-field label="邮箱"
            :type="email.type"
            :message="email.message">
            <b-input ref="email" type="email" maxlength="30" @input="inputEmail" v-model="email.value"></b-input>
        </b-field>

        <b-field label="密码"
            :type="passwd.type"
            :message="passwd.message">
            <b-input type="password" maxlength="20" password-reveal @input="inputPasswd" v-model="passwd.value"></b-input>
        </b-field>

        <div class="login-button">
            <b-button :type="loginBtn.type" size="is-medium" expanded rounded v-html="loginBtn.value" @click="login"></b-button>
        </div>

        <b-loading :is-full-page="true" :active.sync="isLoading" :can-cancel="false">
            <b-icon pack="fas" icon="sync-alt" size="is-large" custom-class="fa-spin"></b-icon>
        </b-loading>
    </div>
</template>

<script>
    import axios from 'axios';

    export default {
        data() {
            return {
                email: {
                    // value: 'develon@qq.com',
                    value: '',
                    type: 'is-info',
                    message: '',
                },
                passwd: {
                    // value: 'pppppppp',
                    value: '',
                    type: 'is-info',
                    message: '',
                },
                loginBtn: {
                    value: `${ '&nbsp;'.repeat(27) } 确认 ${ '&nbsp;'.repeat(27) }`,
                    type: 'is-dark',
                },
                loginDebounce: null,
                isLoading: false,
            };
        },
        filters: {
            a() {
                console.log('ooo');
            }
        },
        computed: {
            canLogin() {
                if (this.email.type == 'is-success' && this.passwd.type == 'is-success') {
                    return true;
                }
                this.login.type = 'is-dark';
                return false;
            }
        },
        methods: {
            inputEmail(value) {
                if (value.match(/.*@.+\..{2,}/)) {
                    this.email.type = 'is-success';
                    this.email.message = '格式正确';
                } else {
                    this.email.type = 'is-danger';
                    this.email.message = '请输入正确格式的邮箱';
                }
                this.checkLogin();
            },
            inputPasswd(value) {
                if (value.length >= 8) {
                    this.passwd.type = 'is-success';
                    this.passwd.message = '格式正确';
                } else {
                    this.passwd.type = 'is-danger';
                    this.passwd.message = '请输入至少8位数密码';
                }
                this.checkLogin();
            },
            checkLogin() {
                if (this.canLogin) {
                    this.loginBtn.type = 'is-success';
                } else {
                    this.loginBtn.type = 'is-dark';
                }
            },
            login() {
                console.log('登录');
                this.loginDebounce = this.loginDebounce || _.debounce(this.loginTask, 1200, {
                    leading: true,
                    trailing: false,
                });
                this.loginDebounce();
            },
            loginTask() {
                if (!this.canLogin) {
                    this.$buefy.toast.open({
                        message: '&nbsp;&nbsp;请输入正确的登录信息！',
                        type: 'is-danger',
                        position: 'is-bottom',
                        queue: false,
                    })
                    return;
                }
                api.post('user/login', {
                    email: this.email.value,
                    password: this.passwd.value,
                }).then(res => {
                    console.log(res.data);
                    this.setAuth(res.data);
                    // this.refresh();
                    this.$buefy.toast.open({
                        message: '登录成功',
                        type: 'is-success',
                        position: 'is-bottom',
                        queue: false,
                    });
                    this.$router.push({ name: '个人中心', });
                }).catch(err => {
                    console.dir(err);
                    this.$buefy.toast.open({
                        message: err.response == null? '请检查网络' : err.response.data.message,
                        type: 'is-danger',
                        position: 'is-bottom',
                        queue: false,
                    });
                }).finally(() => {
                    this.isLoading = false;
                });
                this.isLoading = true;
            },
        },
        mounted() {
            document.title = '登录';
            this.$refs.email.focus();
            // 自动填充从注册页导航至登录页的email
            this.email.value = this.$route.params.email || this.email.value;
            this.inputEmail(this.email.value);
            this.inputPasswd(this.passwd.value);
        },
    }
</script>

<style scoped>
    h1 {
        color: #63b175;
    }
    div.login-button {
        margin-top: 10px;
    }
    input {
        background: red;
    }
</style>

<template>
    <div>
        <h1>
            <b-icon
                pack="fas"
                icon="user"
                size="is-small"
                type="is-success">
            </b-icon>
            &nbsp;注册
        </h1>
        <span class="setup">第  {{ setup }} / 3  步</span>

        <div v-show="setup == 1">
            <b-field label="昵称" :type="name.type" :message="name.message">
                <b-input ref="username" type="username" maxlength="30" @input="inputName" v-model="name.value" :loading="name.loading"></b-input>
            </b-field>

            <b-field label="密码" :type="passwd.type" :message="passwd.message">
                <b-input type="password" maxlength="20" password-reveal @input="inputPasswd" v-model="passwd.value"></b-input>
            </b-field>

            <b-field label="" :type="passwd2.type" :message="passwd2.message">
            <b-input type="password" maxlength="20" password-reveal @input="inputPasswd2" v-model="passwd2.value"></b-input>
            </b-field>
        </div>

        <div v-show="setup == 2">
            <b-field label="邮箱"
                :type="email.type"
                :message="email.message">
                <b-input ref="email" type="email" maxlength="30" @input="inputEmail" v-model="email.value"></b-input>
            </b-field>
        </div>

        <div v-show="setup == 3">
            <h1></h1>
            <h1>注册成功, <router-link :to="{ name: '登录', params: {email: this.email.value}, }">立即登录</router-link></h1>
        </div>

        <div class="next-button" v-show="setup < 3">
            <b-button :type="nextBtn.type" size="is-medium" expanded rounded v-html="nextBtn.value" @click="next"></b-button>
        </div>

        <div v-show="false">
            {{ canSetup2 }}
            {{ canSetup3 }}
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
                name: {
                    value: '',
                    type: 'is-danger',
                    message: '',
                    loading: false,
                },
                email: {
                    // value: 'develon@qq.com',
                    value: '',
                    type: 'is-info',
                    message: '',
                },
                passwd: {
                    // value: 'aaaaaaaa',
                    value: '',
                    type: 'is-info',
                    message: '',
                },
                passwd2: {
                    // value: 'aaaaaaaa',
                    value: '',
                    type: 'is-info',
                    message: '',
                },
                nextBtn: {
                    value: `${ '&nbsp;'.repeat(27) } 下一步 ${ '&nbsp;'.repeat(27) }`,
                    type: 'is-dark',
                },
                nameDebounce: null,
                isLoading: false,
                setup: 1,
                serialNumber: -1,
            };
        },
        filters: {
        },
        computed: {
            canSetup2() {
                if (this.setup == 1) {
                    if (this.checkNext() && !this.name.loading && this.name.type == 'is-success') {
                        this.nextBtn.type = 'is-success';
                        return true;
                    } else {
                        this.nextBtn.type = 'is-dark';
                    }
                }
                return false;
            },
            canSetup3() {
                if (this.setup == 2) {
                    if (this.email.type == 'is-success') {
                        this.nextBtn.type = 'is-success';
                        return true;
                    } else {
                        this.nextBtn.type = 'is-dark';
                    }
                }
                return false;
            },
        },
        methods: {
            inputName(value) {
                if (value.length == 0) {
                    this.name.type = 'is-danger';
                    this.name.message = '请输入一个昵称';
                    return;
                }
                this.nameDebounce = this.nameDebounce || _.debounce(this.chekcName, 500, {
                    leading: false,
                    trailing: true,
                });
                this.nameDebounce();
            },
            chekcName() {
                var name = this.name.value;
                if (name.length == 0)
                    return;
                console.log('检查可用性', name);
                this.name.loading = true;
                api.get('user/check/' + name).then(res => {
                    if (name == this.name.value) {
                        if (res.data.available) {
                            this.name.type = 'is-success';
                            this.name.message = '昵称可以使用';
                        } else {
                            this.name.type = 'is-danger';
                            this.name.message = '昵称已被注册';
                        }
                    }
                }).catch(err => {
                    if (err.isAxiosError)
                        this.name.type = 'is-danger';
                        this.name.message = '网络错误';
                }).finally(() => {
                    this.name.loading = false;
                });
            },
            inputPasswd(value) {
                if (value.length < 8) {
                    this.passwd.type = 'is-danger';
                    this.passwd.message = '请输入至少8位数密码';
                } else {
                    this.passwd.type = 'is-success';
                    this.passwd.message = '格式正确';
                }
                this.checkNext();
            },
            inputPasswd2(value) {
                this.checkNext();
            },
            inputEmail(value) {
                if (value.match(/.*@.+\..{2,}/)) {
                    this.email.type = 'is-success';
                    this.email.message = '格式正确';
                } else {
                    this.email.type = 'is-danger';
                    this.email.message = '请输入正确格式的邮箱';
                }
            },
            /** 检查密码2是否合格 */
            checkNext() {
                if (this.passwd2.value.length < 8) {
                    this.passwd2.type = 'is-danger';
                    this.passwd2.message = '请重复输入一次密码';
                    return false;
                }
                if (this.passwd.value == this.passwd2.value) {
                    this.passwd2.type = 'is-success';
                    this.passwd2.message = '密码一致';
                    return true;
                } else {
                    this.passwd2.type = 'is-danger';
                    this.passwd2.message = '密码不一致';
                    return false;
                }
            },
            next() {
                if (this.canSetup2) {
                    this.setup = 2;
                    return;
                }
                if (this.canSetup3) {
                    // 请求验证码
                    this.isLoading = true;
                    api.post('verification_code/user_register', {
                        email: this.email.value,
                    }).then(res => {
                        console.dir(res.data);
                        this.serialNumber = res.data.serialNumber;
                        this.$buefy.dialog.prompt({
                            title: '邮箱验证',
                            hasIcon: true,
                            type: 'is-success',
                            message: `验证码已发送至邮箱，请在两分钟内输入验证码：`,
                            inputAttrs: {
                                type: 'number',
                                placeholder: '6位数字验证码',
                                maxlength: 6,
                                max: 999999,
                            },
                            trapFocus: true,
                            canCancel: true,
                            cancelText: '返回重试',
                            confirmText: '确认',
                            onConfirm: value => {
                                this.isLoading = true;
                                api.post('user/register', {
                                    nickname: this.name.value,
                                    password: this.passwd.value,
                                    email: this.email.value,
                                    serialNumber: this.serialNumber,
                                    verificationCode: value,
                                }).then(res => {
                                    if (res.data.message == 'ok') { // 验证码正确，注册成功
                                        this.setup = 3;
                                    }
                                }).catch(err => {
                                    this.$buefy.dialog.alert({
                                        title: err.response.data.error,
                                        message: err.response.data.message,
                                        type: 'is-danger',
                                        hasIcon: true,
                                        icon: 'times-circle',
                                        iconPack: 'fa',
                                        ariaRole: 'alertdialog',
                                        ariaModal: true,
                                    });
                                }).finally(() => {
                                    this.isLoading = false;
                                });
                            },
                        });
                        // this.setup = 3;
                    }).catch(err => {
                        console.dir(err);
                        if (err.response) {
                            if (err.response.data.error == '请求过快') {
                                this.$buefy.toast.open({
                                    message: `请求过快, ${ err.response.data.message }`,
                                    type: 'is-danger',
                                    position: 'is-bottom',
                                    queue: false,
                                });
                            } else {
                                this.$buefy.dialog.alert({
                                    title: err.response.data.error,
                                    message: err.response.data.error == '邮箱已注册'?
                                        `此邮箱已注册用户(${ err.response.data.message })，忘记密码可以找回`
                                        : err.response.data.message,
                                    type: 'is-danger',
                                    hasIcon: true,
                                    icon: 'times-circle',
                                    iconPack: 'fa',
                                    ariaRole: 'alertdialog',
                                    ariaModal: true,
                                    confirmText: '确认',
                                });
                            }
                        }
                    }).finally(() => {
                        this.isLoading = false;
                    });
                }
            },
        },
        mounted() {
            document.title = '注册';
            this.$refs.email.focus();
            this.inputName(this.name.value);
            this.inputPasswd(this.passwd.value);
            this.inputPasswd2(this.passwd2.value);
            this.inputEmail(this.email.value);
        },
    }
</script>

<style scoped>
    h1 {
        color: #63b175;
    }
    div.next-button {
        margin-top: 10px;
    }
    input {
        background: red;
    }
    .setup {
        color: #63b175
    }
</style>
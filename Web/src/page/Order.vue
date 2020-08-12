<template>
    <main class="flex">
        <div class="div-container">
            <div class="div-container-bg"></div>
            <div class="content flex">
                <div class="flex" v-show="living">
                    <h2>当前直播计划</h2>
                    <button class="button is-warning" @click="getStatus">
                        <b-icon icon="refresh" custom-class="fa-spin"></b-icon>
                        <span>刷新</span>
                    </button>
                    <br><br>
                    <b-table :data="live" :columns="columns" :sticky-header="true" height="200">
                        <template slot-scope="props">
                            <b-table-column class="flex2">{{ props.row.title }}</b-table-column>

                            <b-table-column class="flex2">{{ props.row.time }}</b-table-column>

                            <b-table-column class="flex2">{{ props.row.category }}</b-table-column>

                            <b-table-column class="flex2">{{ props.row.status | status }}</b-table-column>

                            <b-table-column class="flex2">{{ props.row.key }}</b-table-column>

                            <b-table-column class="flex2 flex3">
                                <b-tooltip label="复制推流密钥，主播唯一凭证" position="is-bottom" type="is-success" size="is-large">
                                    <button class="button is-success" @click="copy">
                                        <b-icon pack="fas" icon="clipboard"></b-icon><span>复制</span>
                                    </button>
                                </b-tooltip>
                            </b-table-column>

                            <b-table-column class="flex2 flex3">
                                <b-tooltip label="结束本次直播，请先断开推流" position="is-left" type="is-danger" size="is-large">
                                    <button class="button is-danger" @click="done">
                                        <b-icon icon="delete"></b-icon><span>结束</span>
                                    </button>
                                </b-tooltip>
                            </b-table-column>
                        </template>
                    </b-table>
                    <div>前往 "更多 >> 如何推流" 查看推流教程</div>
                </div>

                <div class="flex" v-show="!living">
                    <h2> 预订直播</h2>

                    <b-field class="fix" label="直播时间">
                        <b-timepicker v-model="input.time" inline></b-timepicker>
                    </b-field>

                    <b-field label="直播日期" class="user-input">
                        <b-datepicker v-model="input.time"
                            placeholder="选择日期"
                            :month-names='["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"]'
                            :day-names='["日", "一", "二", "三", "四", "五", "六"]'
                            >
                        </b-datepicker>
                    </b-field>
                    
                    <b-field label="直播分类" class="user-input">
                        <category :value="input.category" @input="handleInputCategory"></category>
                    </b-field>

                    <b-field label="直播标题" class="user-input">
                        <b-input v-model="input.title" @input="type = input.title.trim() != '' ? `is-success` : `is-dark`"></b-input>
                    </b-field>

                    <div class="div-button">
                        <b-button :type="type" size="is-medium" expanded rounded @click="order">确定</b-button>
                    </div>
                </div>
            </div>
        </div>

        <b-loading :is-full-page="true" :active.sync="loadding" :can-cancel="false">
            <b-icon pack="fas" icon="sync-alt" size="is-large" custom-class="fa-spin"></b-icon>
        </b-loading>
    </main>
</template>

<script>
    import Category from '../component/Category.vue';

    export default {
        components: { Category },
        data() {
            return {
                type: 'is-dark',
                loadding: false,
                id: this.getAuth().uid,
                living: false,
                live: [{
                    title: '',
                    time: '',
                    category: '',
                    key: '',
                    status: '',
                }],
                columns: [
                    { field: 'title', label: '直播标题', },
                    { field: 'time', label: '直播时间', centered: true, },
                    { field: 'category', label: '分类', centered: true, },
                    { field: 'status', label: '状态', centered: true, },
                    { field: 'key', label: '推流密钥', centered: true, },
                    { field: '', label: '复制', centered: true, },
                    { field: '', label: '结束', centered: true, },
                ],
                input: {
                    time: new Date(),
                    category: '未分类',
                    title: '',
                },
            };
        },
        watch: {
            'input.category'(v) {
                console.log(v);
            },
        },
        filters: {
            status(value) {
                switch(value) {
                    case 'pending': return '等待中';
                    case 'awaiting': return '直播断开';
                    case 'living': return '直播中';
                    case 'done': return '直播完成';
                    default: return '未知状态';
                }
            },
        },
        methods: {
            done() {
                this.loadding = true;
                api.auth().post('live/done', {}).then(data => {
                    this.getStatus();
                }).catch(error => {
                    console.dir(error);
                }).finally(() => {
                    this.loadding = false;
                });
            },
            getStatus() {
                const vm = this;
                vm.loadding = true;
                api.get('live/visit/' + vm.id).then(res => {
                        vm.live[0].title = res.data.title;
                        vm.live[0].time = formatDate(new Date(res.data.liveTime));
                        vm.live[0].category = res.data.category;
                        vm.live[0].key = `${ res.data.index }?md5=${ res.data.md5 }`;
                        vm.live[0].status = res.data.status;
                        switch (res.data.status) {
                            case 'living': { // 直播出现状况需要用户刷新网页
                                vm.living = true;
                                break;
                            }
                            case 'awaiting': {
                                vm.living = true;
                                break;
                            }
                            case 'pending': {
                                vm.living = true;
                                vm.msg = `${ vm.title }<br>预计于 ${ formatDate(new Date(res.data.liveTime)) } 开始直播`;
                                break;
                            }
                            case 'done': {
                                vm.living = false;
                                break;
                            }
                            default: {
                                vm.living = false;
                            }
                        }
                    }).catch(error => {
                        console.dir(error);
                        if (error.response) {
                            vm.living = false;
                        }
                    }).finally(() => {
                        vm.loadding = false;
                    });
            }, // end of getStatus()
            handleInputCategory(value) {
                this.input.category = value;
            },
            order() {
                if (this.type != 'is-success') return;
                this.loadding = true;
                api.auth().post('live/order', {
                    "title": this.input.title,
                    "time": formatDate(this.input.time).toString().replace(/(年|月)/g, '-').replace(/(星期.|日)/g, ''),
                    "category": this.input.category,
                }).then(res => {
                    this.getStatus();
                }).catch(error => {
                    console.log(error);
                }).finally(() => {
                    this.loadding = false;
                });
            },
            copy() {
                if (copyText(this.live[0]['key'])) {
                    this.$buefy.toast.open({
                        message: '复制成功，立即开始推流吧！',
                        type: 'is-success',
                        position: 'is-bottom',
                        queue: false,
                    });
                }
            },
        },
        mounted() {
            this.getStatus();
        },
    }
</script>

<style scoped>
    main {
        min-width: 100vw;
        min-height: calc(100vh - 81px);
        background: url('../assets/bg_upload.jpg');
        background-repeat: no-repeat;
        background-attachment: fixed;
        background-size: 100% 100%;
    }
    .div-container {
        width: 100vw;
        height: calc(100vh - 81px);
        background: inherit;
        position: relative;
        overflow: hidden;
        min-height: 800px;
    }
    .div-container-bg {
        position: absolute;
        top: -80px;
        bottom: -80px;
        left: -80px;
        right: -80px;
        filter: blur(30px);
        background: inherit;
    }
    .content {
        position: absolute;
        width: 100%;
        max-width: 100%;
        height: calc(100vh - 81px);
        height: 80vh;
    }
    h2 {
        color: #63b175;
    }
    .fix {
        text-align: center;
    }
    .div-button {
        min-width: 400px;
        max-width: 80vw;
    }
    .user-input {
        width: 360px;
    }
    td {
        /* background: #63b175; */
        height: 80px;
    }
    .flex2 {
        transform: translateY(20px);
    }
    .flex3 {
        transform: translateY(14px);
    }
</style>
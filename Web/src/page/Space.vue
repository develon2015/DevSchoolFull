<template>
    <main>
        <div class="content div-search flex">
            <span v-if="loadding">加载中...</span>
            <div v-else class="flex">
                <h2> 我的资源</h2>
                <div>
                    <button class="button is-warning" @click="refreshSpace">
                        <b-icon icon="refresh" custom-class="fa-spin"></b-icon>
                        <span>刷新</span>
                    </button>&nbsp;&nbsp;&nbsp;
                    <button class="button is-success" @click="$router.push('/share')">
                        <b-icon icon="upload"></b-icon>
                        <span>上传</span>
                    </button>
                </div>
            </div>
        </div>
        <div v-if="display" class="flex">
             <b-table :data="item" :columns="columns" :sticky-header="false">
                <template slot-scope="props">
                    <b-table-column>{{ props.row[0] }}</b-table-column>

                    <b-table-column>{{ props.row[1]}}</b-table-column>

                    <b-table-column>{{ props.row[2] }}</b-table-column>

                    <b-table-column>{{ props.row[3] | filesize }}</b-table-column>

                    <b-table-column>{{ props.row[4] | showDate }}</b-table-column>

                    <!-- <b-table-column>{{ props.row[5] }}</b-table-column> -->

                    <b-table-column>
                        <button class="button is-light" @click="copyLink(props.row[5])">
                            <b-icon icon="link"></b-icon><span>复制</span>
                        </button>
                    </b-table-column>

                    <b-table-column>
                        <button class="button is-success" @click="download(props.row[5])">
                            <b-icon icon="download"></b-icon><span>下载</span>
                        </button>
                    </b-table-column>

                    <b-table-column>
                        <button v-if="props.row[2].match(/video\/.*/)" class="button is-warning" @click="video(props.row[5])">
                            <b-icon icon="eye"></b-icon><span>播放</span>
                        </button>
                        <div v-else>暂不支持</div>
                    </b-table-column>

                    <b-table-column>
                        <span>{{ props.row[6] | status }}</span>
                    </b-table-column>

                    <b-table-column>
                        <button v-if="props.row[6] == 0" class="button is-dark" @click="changeStatus(props.row[7], 1)">
                            <b-icon icon="key"></b-icon><span>设为私有</span>
                        </button>

                        <button v-else class="button is-primary" @click="changeStatus(props.row[7], 0)">
                            <b-icon icon="cloud"></b-icon><span>设为公开</span>
                        </button>

                        <button class="button is-danger" @click="deleteFile(props.row)">
                            <b-icon icon="delete"></b-icon><span>删除</span>
                        </button>
                    </b-table-column>
                </template>
            </b-table>

            <div v-if="item.length == 0">您还没有任何资源，<router-link to="/share">立即上传</router-link></div>
        </div>

        <b-loading :is-full-page="true" :active.sync="loadding" :can-cancel="false">
            <b-icon pack="fas" icon="sync-alt" size="is-large" custom-class="fa-spin"></b-icon>
        </b-loading>
    </main>
</template>

<script>
    const data = {
        display: false,
        keyword: '',
        item: [],
        columns: [
            { label: '文件名', },
            { label: '后缀', centered: true, },
            { label: '类型', centered: true, },
            { label: '大小', centered: true, },
            { label: '上传时间', centered: true, },
            { label: '链接', centered: true, },
            { label: '下载', centered: true, },
            { label: '在线预览', centered: true, },
            { label: '状态', centered: true, },
            { label: '操作', centered: true, },
        ],
        loadding: true,
    };
    export default {
        data() {
            return data;
        },
        filters: {
            status(code) {
                switch (code) {
                    case 0: return '公开';
                    case 1: return '私有';
                }
                return '未知';
            },
        },
        methods: {
            search(event) {
                if (this.keyword != '' && event.key == 'Enter') {
                    this.loadding = true;
                    api.get('resource/search/' + this.keyword).then(res => {
                        this.display = true;
                        this.item = res.data;
                    }).catch(err => {
                        console.log('失败');
                    }).finally(() => {
                        this.loadding = false;
                    });
                }
            },
            copyLink(url) {
                if (copyText(`${ location.protocol }//${ location.host }${ url }`)) {
                    this.$buefy.toast.open({
                        message: '链接地址已复制！',
                        type: 'is-success',
                        position: 'is-bottom',
                        queue: false,
                    });
                }
            },
            download(url) {
                window.open(url, '_blank');
            },
            video(url) {
                this.$router.push({ path: '/play' + url });
            },
            refreshSpace() {
                this.loadding = true;
                api.auth().post('resource/mine', {}).then(res => {
                    this.display = true;
                    this.item = res.data;
                    console.dir(res);
                }).catch(error => {
                    console.log(error);
                }).finally(() => {
                    this.loadding = false;
                });
            },
            deleteFile(file) {
                this.$buefy.dialog.confirm({
                    title: '确认删除',
                    message: `确认删除文件${ file[0] }？`,
                    confirmText: '删除',
                    cancelText: '取消',
                    type: 'is-danger',
                    hasIcon: true,
                    onConfirm: () => {
                        this.changeStatus(file[7], -1);
                    },
                });
            },
            changeStatus(index, status) {
                this.loadding = true;
                api.auth().post('resource/status', {
                    index,
                    status,
                }).then(res => {
                    this.refreshSpace();
                }).catch(error => {}).finally(() => {
                    this.loadding = false;
                });
            },
        },
        mounted() {
            setTimeout(() => {
                this.refreshSpace();
            }, 200);
        },
    }
</script>

<style scoped>
    main {
        min-width: 100vw;
        min-height: calc(100vh - 81px);
        background: url('../assets/bg_index.jpg');
        background-attachment: fixed;
        background-repeat: no-repeat;
        background-size: 100% 100%;
        background: white;
    }
    .div-search {
        padding-top: 40px;
    }
    .field-search {
        width: 600px;
        max-width: 80vw;
    }
    h2 {
        color: #63b175;
    }
</style>
<template>
    <main>
        <div class="div-search flex">
            <div class="content flex">
                <h2>搜索教学资源</h2>
            </div>
            <b-field class="field-search">
                <b-input ref="input" autofocus v-model="keyword" @keypress.native="search" placeholder="输入关键字..." type="search" icon="magnify" rounded></b-input>
            </b-field>
        </div>
        <div v-if="display" class="flex">
             <b-table :data="item" :columns="columns" :sticky-header="false">
                <template slot-scope="props">
                    <b-table-column>{{ props.row[0] }}</b-table-column>

                    <b-table-column>{{ props.row[1]}}</b-table-column>

                    <b-table-column>{{ props.row[2] }}</b-table-column>

                    <b-table-column>{{ props.row[3] | filesize }}</b-table-column>

                    <b-table-column>{{ props.row[4] | showDate }}</b-table-column>

                    <b-table-column>{{ props.row[5] }}</b-table-column>

                    <b-table-column>
                        <button class="button is-light" @click="copyLink(props.row[6])">
                            <b-icon icon="link"></b-icon><span>复制</span>
                        </button>
                    </b-table-column>

                    <b-table-column>
                        <button class="button is-success" @click="download(props.row[6])">
                            <b-icon icon="download"></b-icon><span>下载</span>
                        </button>
                    </b-table-column>

                    <b-table-column>
                        <button v-if="props.row[2].match(/video\/.*/)" class="button is-warning" @click="video(props.row[6])">
                            <b-icon icon="eye"></b-icon><span>播放</span>
                        </button>
                        <div v-else>暂不支持</div>
                    </b-table-column>
                </template>
            </b-table>

            <div v-if="item.length == 0">抱歉，没有查找到资源</div>
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
            { label: '分享者', centered: true, },
            { label: '链接', centered: true, },
            { label: '下载', centered: true, },
            { label: '在线预览', centered: true, },
        ],
        loadding: false,
    };
    export default {
        data() {
            return data;
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
        },
        mounted() {
            this.$refs.input.focus();
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
        padding: 40px 0;
    }
    .field-search {
        width: 600px;
        max-width: 80vw;
    }
    h2 {
        color: #63b175;
    }
</style>
<template>
    <main>
        <div class="div-search flex">
            <div class="content flex">
                <h2>分类搜索直播</h2>
            </div>
            <div style="padding-bottom: 20px">
                <category :value="category" @input="handleInputCategory"></category>
            </div>
            <b-field class="field-search">
                <span style="width: 100%">
                    <b-input ref="input" autofocus v-model="keyword" @keypress.native="search" placeholder="输入关键字..." type="search" icon="magnify" rounded></b-input>
                </span>
            </b-field>
        </div>
        <div v-if="display" class="flex">
             <b-table :data="item" :columns="columns" :sticky-header="false">
                <template slot-scope="props">
                    <b-table-column>{{ props.row[1]}}</b-table-column>

                    <b-table-column>{{ props.row[2] }}</b-table-column>

                    <b-table-column>{{ props.row[3] | showDate }}</b-table-column>

                    <b-table-column>{{ props.row[4] }}</b-table-column>

                    <b-table-column>{{ props.row[5] | status }}</b-table-column>

                    <b-table-column>
                        <button class="button is-success" @click="join(props.row[0])">
                            <b-icon pack="fas" size="is-small" icon="tv"></b-icon><span>进入直播间</span>
                        </button>
                    </b-table-column>
                </template>
            </b-table>

            <div v-if="item.length == 0">抱歉，没有查找到直播</div>
        </div>

        <b-loading :is-full-page="true" :active.sync="loadding" :can-cancel="false">
            <b-icon pack="fas" icon="sync-alt" size="is-large" custom-class="fa-spin"></b-icon>
        </b-loading>
    </main>
</template>

<script>
    import Category from '../component/Category.vue';

    const data = {
        display: false,
        category: '全部分类',
        keyword: '',
        item: [],
        columns: [
            { label: '直播间标题', },
            { label: '分类', centered: true, },
            { label: '直播时间', centered: true, },
            { label: '主播', centered: true, },
            { label: '直播间状态', centered: true, },
            { label: '操作', centered: true, },
        ],
        loadding: false,
    };
    export default {
        components: { Category },
        data() {
            return data;
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
            showDate(value) {
                return formatDate(value);
            }
        },
        methods: {
            handleInputCategory(value) {
                this.category = value;
            },
            search(event) {
                if (this.keyword != '' && event.key == 'Enter') {
                    this.loadding = true;
                    api.get(`live/search/${ this.category }/${ this.keyword }`).then(res => {
                        console.log(res);
                        this.display = true;
                        this.item = res.data;
                    }).catch(err => {
                        console.log('失败');
                    }).finally(() => {
                        this.loadding = false;
                    });
                }
            },
            join(uid) {
                this.$router.push({ path: '/live/' + uid });
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
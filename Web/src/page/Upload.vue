<template>
    <main class="flex">
        <div class="div-container">
            <div class="div-container-bg"></div>
            <div class="content flex">
                <b-upload @input="input" del="dropFiles2" multiple drag-drop v-model="value">
                    <div class="dev-upload flex">
                        <div class="flex">
                                <b-icon icon="upload" size="is-large"></b-icon>
                                <span>单击上传或拖放文件至此处</span>
                        </div>
                    </div>
                </b-upload>

                <div class="uploading flex" v-if="uploadFiles.length > 0">
                    正在上传：
                    <b-table :data="uploadFiles" :columns="columns" :sticky-header="true" height="300">
                        <template slot-scope="props">
                            <b-table-column>{{ props.row.index }}</b-table-column>

                            <b-table-column>{{ props.row.file.name }}</b-table-column>

                            <b-table-column>{{ props.row.file.type || "未知" }}</b-table-column>

                            <b-table-column>{{ props.row.file.size | filesize }}</b-table-column>

                            <b-table-column>
                                <b-progress v-show="props.row.progress >=0 && props.row.progress < 100" class="up-progress" type="is-success" :value="props.row.progress" show-value format="percent"></b-progress>

                                <button v-show="props.row.progress >= 100" class="button is-success" @click="finish(props.row.index)">
                                    <b-icon icon="check"></b-icon><span>完成</span>
                                </button>

                                <button v-show="props.row.progress < 0" class="button is-danger" @click="retry(props.row.index, props.row.file)">
                                    <b-icon icon="cancel"></b-icon><span>重试</span>
                                </button>
                            </b-table-column>
                        </template>
                    </b-table>
                </div>

                <div>上传文件将被公开，您可以在个人中心设为私有</div>
            </div>
        </div>
    </main>
</template>

<script>
    import axios from 'axios';

    export default {
        data() {
            return {
                value: [],
                columns: [
                    { field: 'index', label: '序号', },
                    { field: 'file.name', label: '文件', centered: true, },
                    { field: 'file.type', label: '类型', centered: true, },
                    { field: 'file.size', label: '大小', centered: true, },
                    { field: 'progress', label: '上传进度', centered: true, },
                ],
            };
        },
        computed: {
            ...Vuex.mapState(['uploadFiles']),
        },
        methods: {
            deleteDropFile(index) {
                this.dropFiles.splice(index, 1)
            },
            input(value) {
                value.forEach(it => {
                    try {
                        this.$store.commit('uploadFiles', it);
                    } catch(e) {}
                });
                this.value = []; // 清空当前输入
            },
            finish(index) {
                this.$store.commit('finish', index);
            },
            retry(index, file) {
                console.log('删除', index);
                try {
                    this.finish(index);
                } catch(e) {
                    console.dir(e);
                }
                try {
                    this.$store.commit('uploadFiles', file);
                } catch(e) {
                    console.dir(e);
                }
                // this.value = []; // 清空当前输入
            },
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
        width: 800px;
        max-width: 90vw;
        height: 68vh;
        min-height: 600px;
        background: inherit;
        position: relative;
        /* top: -100px; */
        top: -6vh;
        overflow: hidden;
        box-shadow: 12px 12px 22px 1px #63b17591;
        border-radius: 40px 0 40px 0;
    }
    .div-container-bg {
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
    .dev-upload {
        width: 80vw;
        max-width: 700px;
        height: 200px;
        max-height: 28vh;
    }
    .uploading {
        width: 100%;
        padding-top: 20px;
        height: 340px;
        overflow: hidden
    }
    .up-progress {
        width: 160px;
        display: inline-block;
    }
</style>
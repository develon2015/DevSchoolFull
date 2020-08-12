<template>
    <main class="flex">
        <div class="div-container flex">
            <div class="div-container-bg"></div>
            <div class="content flex">
                <video-player v-if="start" :src="video" :type="type"></video-player>
                <div class="live-title" v-if="start">正在直播：《{{ title }}》</div>
            </div>
            <div v-show="!living" class="flex content">
                <div class="live-hint" v-html="msg"></div>
            </div>
        </div>

        <div class="div-container flex">
            <div class="div-container-bg"></div>
            <div class="content flex">
            </div>
        </div>

        <b-loading :is-full-page="true" :active.sync="loadding" :can-cancel="false">
            <b-icon pack="fas" icon="sync-alt" size="is-large" custom-class="fa-spin"></b-icon>
        </b-loading>
    </main>
</template>

<script>
    import VideoPlayer from '../component/VideoPlayer.vue';

    export default {
        components: { VideoPlayer, },
        data() {
            return {
                title: '',
                start: false,
                living: false,
                id: -1,
                index: -1,
                video: '',
                type: "application/x-mpegURL",
                leave: false,
                msg: '加载中...',
                loadding: true,
            };
        },
        mounted() {
            this.id = this.$route.params['id'];
            let vm = this;
            (function loop() {
                api.get('live/visit/' + vm.id).then(res => {
                    vm.index = res.data.index;
                    vm.video = `http://localhost/hls/${ vm.index }/index.m3u8`;
                    vm.video = `/hls/${ vm.index }/index.m3u8`;
                    vm.title = res.data.title;
                    switch (res.data.status) {
                        case 'living': { // 直播出现状况需要用户刷新网页
                            setTimeout(() => {
                                vm.living = true;
                                vm.start = true;
                            }, 30);
                            break;
                        }
                        case 'awaiting': {
                            vm.living = false;
                            vm.msg = '主播休息中';
                            setTimeout(() => { // 自动刷新
                                if (!vm.leave) loop();
                            }, 2000);
                            break;
                        }
                        case 'pending': {
                            vm.living = false;
                            vm.msg = `《${ vm.title }》<br>预计于 ${ formatDate(new Date(res.data.liveTime)) } 开始直播`;
                            setTimeout(() => { // 自动刷新
                                if (!vm.leave) loop();
                            }, 2000);
                            break;
                        }
                        case 'done': {
                            vm.living = false;
                            vm.msg = '直播已结束';
                            break;
                        }
                        default: {
                            vm.living = false;
                            vm.msg = '暂无直播';
                        }
                    }
                }).catch(error => {
                    console.dir(error);
                    if (error.response) {
                        vm.living = false;
                        vm.msg = error.response.data.error;
                    }
                }).finally(() => {
                    vm.loadding = false;
                });
            })();
        },
        beforeDestroy() {
            this.leave = true;
        }
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
        /* padding-top: 86px; */
        /* box-shadow: 12px 12px 22px 1px #63b17591; */
        /* border-radius: 40px 0 40px 0; */
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
        width: 1200px;
        max-width: 100%;
        height: calc(100vh - 81px);
        height: 80vh;
    }
    .live-title {
        font-size: 26px;
        font-weight: bold;
        color: #63b175;
        color: green;
    }
    .live-hint {
        font-size: 28px;
        color: green;
        font-weight: bold;
        text-align: center;
    }
</style>
<template>
    <main class="flex">
        <div class="div-container flex">
            <div class="div-container-bg"></div>
            <div class="content">
                <video-player v-if="start" :src="video" :type="type"></video-player>
            </div>
        </div>
        <div class="div-container flex">
            <div class="div-container-bg"></div>
            <div class="content">
            </div>
        </div>
    </main>
</template>

<script>
    import axios from 'axios';
    import VideoPlayer from '../component/VideoPlayer.vue';

    export default {
        components: { VideoPlayer, },
        data() {
            return {
                start: false, // 组件被挂载后再渲染视频播放器
                video: '',
                type: '',
            };
        },
        mounted() {
            this.video = this.$route.params['video'];
            console.log('视频地址：', this.video);
            // this.type = 'video/x-matroska';
            this.type = 'video/webm';
            console.log('视频类型：', this.type);
            this.start = true;
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
</style>

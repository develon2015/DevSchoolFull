<template>
    <main>
        <div class="index-info">
            {{ title | typing(i) }}<span class="typing" v-show="flash">&nbsp;|</span><br>
            专注于计算机科学领域<br>
            一个自由的教学互动平台<br>
            立即分享您的资源，开始您的教学直播<br>
        </div>
    </main>
</template>

<script>
    export default {
        data() {
            return {
                title: 'DevSchool',
                flash: true,
                i: 1,
            };
        },
        filters: {
            typing(value, i) {
                return value.substr(0, i);
            },
        },
        mounted() {
            const vm = this;
            setInterval(() => {
                vm.flash = !vm.flash;
            }, 100);
            setInterval(() => {
                if (vm.i > vm.title.length + 2)
                    vm.i = 0;
                else
                    vm.i ++ ;
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
    }
    .index-info {
        line-height: 68px;
        font-size: 24px;
        color: #63b175;
        padding-top: 20px;
        padding-left: 20px;
        font-weight: bold;
        user-select: none;
    }
    .typing {
        color: black;
        transition: all;
    }
</style>
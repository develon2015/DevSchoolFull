<template>
    <div class="timer">
        {{ timer }} : {{ millis }}
    </div>
</template>

<script>
    export default {
        data() {
            return {
                timer: 0,
                millis: 0,
            };
        },
        mounted() {
            let vm = this;
            (function _() {
                if (vm.millis >= 99)
                    vm.millis = 0;
                vm.millis += 1;
                setTimeout(_, 10);
            })();
            (function _() {
                vm.timer += 1;
                setTimeout(_, 1000);
            })();
        },
    }
</script>

<style scoped>
    .timer {
        font-size: 80px;
    }
</style>
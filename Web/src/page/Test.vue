<template>
    <main>
        <b-button @click="test" class="aaa">测试</b-button>
        <b-button @click="test" class="aaa">测试</b-button>
        <div v-if="true" v-for="(i, index) in icon" :key="index">
            {{ i }}:&nbsp;
            <b-icon :icon="i"></b-icon>
            <span v-for="(p, index) in pack" :key="index">
                {{ p }}:&nbsp;
                <b-icon :pack="p" :icon="i"></b-icon>
            </span>
        </div>
    </main>
</template>

<script>
    export default {
        data() {
            return {
                pack: ['mdi', 'fa', 'fas', 'far', 'fad', 'fal'],
                icon: ['check', 'music', 'video', 'cancel', 'live', 'logout', 'eye', 'no'],
            };
        },
        methods: {
            test() {
                console.log('ok');
                this.$router.push({
                    path: '/play',
                    querys: {
                        video: "kjk"
                    },
                });
            },
        },
    }
</script>

<style scoped>
    .aaa::after {
        content: '\EA79',
    }
</style>
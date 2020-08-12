<template>
    <span>
        <b-select :value="value" placeholder="未分类" expanded @input="handleInput">
            <optgroup v-for="(cg, index) in cgs" :key="index" :label="index">
                    <option v-for="(c, index) in cg" :key="index" :value="c">{{ c }}</option>
            </optgroup>
        </b-select>
    </span>
</template>

<script>
    export default {
        props: {
            value: {
                type: String,
                default: '未分类',
            },
        },
        data() {
            return {
                cgs: {
                    '其它': ['未分类'],
                    '计算机科学技术': ['计算机科学技术基础学科','自动机理论','可计算性理论','计算机可靠性理论','算法理论','数据结构','数据安全与计算机安全'],
                    '人工智能': ['人工智能理论','自然语言处理','机器翻译','模式识别','算机感知','计算机神经网络','知识工程'],
                    '计算机系统结构': ['计算机系统设计','并行处理','分布式处理系统','计算机网络','计算机运行测试与性能评价'],
                    '计算机软件': ['软件理论','操作系统与操作环境','程序设计语言','编译系统','数据库','软件开发环境与开发技术','软件工程'],
                    '计算机工程': ['计算机元器件','计算机处理器技术','计算机存储技术','计算机外围设备','计算机制造与检测','计算机高密度组装技术'],
                    '计算机应用': ['语言文字信息处理','计算机仿真','计算机图形学','计算机图像处理','计算机辅助设计','计算机过程控制','计算机信息管理系统','计算机决策支持系统'],
                },
            };
        },
        methods: {
            handleInput(value) {
                this.$emit('input', value);
            },
        },
        mounted() {
            if (this.value == '全部分类') {
                this.cgs['其它'].push('全部分类');
            }
        },
    }
</script>

<style scoped>
</style>
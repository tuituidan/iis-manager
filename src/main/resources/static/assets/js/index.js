/*
 * @author tuituidan
 * @date 2020/12/11
 */
new Vue({
    el: '#app',
    data() {
        return {
            keywords: '',
            searchDatas: [],
            datas: [],
        }
    },
    mounted() {
        this.init();
    },
    methods: {
        init() {
            this.$http.get('/api/v1/site')
                .then(res => {
                    this.searchDatas = this.datas = res.data;
                    this.searchHandler();
                })
                .catch(err => {
                    console.error(err);
                    this.$notice.err('发生错误');
                })
        },
        searchHandler() {
            if (!this.keywords) {
                this.searchDatas = this.datas;
                return;
            }
            this.searchDatas = this.datas.filter(item => item.siteName.toLocaleLowerCase()
                .includes(this.keywords.toLocaleLowerCase()))
        },
        siteState(id, state) {
            this.$http.patch(`/api/v1/site/${id}/actions/${state}`)
                .then(() => {
                    this.$notice.suc('执行成功');
                    this.init();
                })
                .catch(err => {
                    console.error(err);
                    this.$notice.err('发生错误');
                })
        },
        apppoolState(id, state) {
            this.$http.patch(`/api/v1/apppool/${id}/actions/${state}`)
                .then(() => {
                    this.$notice.suc('执行成功');
                    this.init();
                })
                .catch(err => {
                    console.error(err);
                    this.$notice.err('发生错误');
                })
        },
        showFile(row) {
            this.$refs.showFileDialog.open(row);
        }
    }
});

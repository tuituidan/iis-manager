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
            columns: [
                {
                    title: '网站',
                    key: 'siteName'
                },
                {
                    title: '绑定',
                    key: 'bindings',
                    slot: 'bindings-slot'
                },
                {
                    title: '网站状态',
                    slot: 'site-state',
                    key: 'siteState'
                },
                {
                    title: '应用池状态',
                    slot: 'apppool-state',
                    key: 'apppoolState',
                },
                {
                    title: '操作',
                    slot: 'action',
                    width: 380,
                    align: 'center'
                }
            ],
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
        }
    }
});

/*
 * @author tuituidan
 * @date 2020/12/11
 */
new Vue({
    el: '#app',
    data() {
        return {
            datas: [],
            columns: [
                {
                    title: '网站',
                    key: 'siteName'
                },
                {
                    title: '绑定',
                    key: 'bindings'
                },
                {
                    title: '网站状态',
                    key: 'siteState',
                },
                {
                    title: '应用池状态',
                    key: 'apppoolState',
                },
                {
                    title: '操作',
                    slot: 'action',
                    width: 500,
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
                    this.datas = res.data;
                })
                .catch(err => {
                    console.error(err);
                    this.$notice.err(err.response.data);
                })
        },
        siteState(id, state){
            this.$http.get(`/api/v1/site/${id}/actions/${state}`)
                .then(() => {
                    this.$notice.suc('执行成功');
                })
                .catch(err => {
                    console.error(err);
                    this.$notice.err(err.response.data);
                })
        },
        apppoolState(id, state){
            this.$http.get(`/api/v1/apppool/${id}/actions/${state}`)
                .then(() => {
                    this.$notice.suc('执行成功');
                })
                .catch(err => {
                    console.error(err);
                    this.$notice.err(err.response.data);
                })
        }
    }
});

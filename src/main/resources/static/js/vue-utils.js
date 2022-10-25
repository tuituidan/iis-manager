/*
 * @author tuituidan
 * @date 2020/12/27
 */
const CustomPlugin = {};
CustomPlugin.install = Vue => {
    Vue.prototype.$http = axios.create({
        timeout: 800000
    });
    Vue.prototype.$notice = {
        suc(msg) {
            Vue.prototype.$Notice.success({
                title: '成功',
                duration: 2,
                desc: msg
            });
        },
        err(msg) {
            Vue.prototype.$Notice.error({
                title: '错误',
                duration: 5,
                desc: msg
            });
        },
        warn(msg) {
            Vue.prototype.$Notice.warning({
                title: '警告',
                duration: 3,
                desc: msg
            });
        },
        info(msg) {
            Vue.prototype.$Notice.info({
                title: '提示',
                duration: 2,
                desc: msg
            });
        }
    };
};
Vue.use(CustomPlugin);

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
            Vue.prototype.$notify.success({
                title: '成功',
                message: msg
            });
        },
        err(msg) {
            Vue.prototype.$notify.error({
                title: '错误',
                message: msg
            });
        },
        warn(msg) {
            Vue.prototype.$notify.warning({
                title: '警告',
                message: msg
            });
        },
        info(msg) {
            Vue.prototype.$notify.info({
                title: '提示',
                message: msg
            });
        }
    };
};
Vue.use(CustomPlugin);

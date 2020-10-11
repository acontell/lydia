new Vue({
    el: '#app',
    data() {
        return {
            message: null,
            loading: true,
            errored: false
        }
    },
    mounted() {
        axios
            .get('https://api.coindesk.com/v1/bpi/currentprice.json')
            .then(response => {
                this.message = response.data
            })
            .catch(error => {
                console.log(error)
                this.errored = true
            })
            .finally(() => this.loading = false)
    }
})

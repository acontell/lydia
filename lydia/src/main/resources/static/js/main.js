Vue.component('trade-balance', {
    data() {
        return {
            result: null,
            loading: true,
            errored: false
        }
    },
    template: `
    <section v-if="errored">
        <p>We're sorry, we're not able to retrieve this information at the moment, please try back later</p>
    </section>

    <section v-else>
        <div v-if="loading">
            <div class="spinner-border text-info" role="status">
                <span class="sr-only">Loading...</span>
            </div>
        </div>

        <div v-else>
            <div class="card border-dark mb-3" style="max-width: 30rem;">
                <div class="card-header">Trade Balance</div>
                <div class="card-body text-dark">
                    <table class="table">
                      <tbody>
                        <tr>
                          <th scope="row">Combined balance of all currencies:</th>
                          <td>{{ result.eb | currencyDecimal }}</td>
                        </tr>
                      </tbody>
                    </table>
                </div>
            </div>
        </div>

    </section>
    `,
    filters: {
        currencyDecimal(value) {
            return (+value).toFixed(2) + " €"
        }
    },
    mounted() {
        axios
            .get('/api/trade-balance')
            .then(response => this.result = response.data.result)
            .catch(error => {
                console.log(error)
                this.errored = true
            })
            .finally(() => this.loading = false)
    }
})

new Vue({el: '#app'})

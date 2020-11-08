import Vue from 'vue';
import Retriever from './logic/Retriever'

const NUMBER_FORMATTER = new Intl.NumberFormat("es-ES", {style: "currency", currency: "EUR"})

Vue.component('trade-balance', {
    data() {
        return {
            balance: null,
            deposits: null,
            withdraws: null,
            tradesHistory: null,
            loading: true,
            errored: false
        }
    },
    computed: {
        gainsLossesObject() {

            return {
                'text-success': (this.balance.eb - (this.deposits.total + this.withdraws.total)) > 0,
                'text-danger': (this.balance.eb - (this.deposits.total + this.withdraws.total)) < 0
            }
        }
    },
    methods: {
        sortOrdersAsc(orders) {

            return _.orderBy(orders, 'time', 'asc');
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
                <div class="card-header"><b>Trade Balance</b></div>
                <div class="card-body text-dark">
                    <table class="table">
                      <tbody>
                        <tr>
                          <th scope="row">Total deposited:</th>
                          <td>{{ deposits.total | currencyDecimal }}</td>
                        </tr>
                        <tr>
                          <th scope="row">Total withdrawn:</th>
                          <td>{{ withdraws.total | currencyDecimal }}</td>
                        </tr>
                        <tr>
                          <th scope="row">Currently invested:</th>
                          <td>{{ deposits.total + withdraws.total | currencyDecimal }}</td>
                        </tr>
                        <tr>
                          <th scope="row">Currently valued:</th>
                          <td>{{ balance.eb | currencyDecimal }}</td>
                        </tr>
                        <tr>
                          <th scope="row">Gains/losses:</th>
                          <td v-bind:class="gainsLossesObject">{{ balance.eb - (deposits.total + withdraws.total) | currencyDecimal }}</td>
                        </tr>
                      </tbody>
                    </table>
                </div>
            </div>
            <div v-for="(orders, crypto) in tradesHistory">
                <div class="card border-dark mb-3" style="max-width: 75rem;">
                    <div class="card-header"><b>{{ crypto }}</b></div>
                    <div class="card-body text-dark">
                        <table class="table">
                          <thead>
                          <tr>
                              <th scope="row">Order</th>
                              <th scope="row">Date</th>
                              <th scope="row">Amount</th>
                              <th scope="row">Price</th>
                              <th scope="row">Euros</th>
                              <th scope="row">Fees</th>
                           </tr>
                          </thead>
                          <tbody>
                            <tr v-for="order in sortOrdersAsc(orders)">
                              <td v-bind:class="{ 'text-success': order.type === 'sell', 'text-danger': order.type === 'buy' }">{{ order.type | toUpperCase }}</td>
                              <td>{{ order.time | toDate }}</td>
                              <td>{{ order.vol }}</td>
                              <td>{{ order.price }}</td>
                              <td v-bind:class="{ 'text-success': order.type === 'sell', 'text-danger': order.type === 'buy' }">{{ order.cost | toEuros }}</td>
                              <td class="text-danger">{{ order.fee | toEuros }}</td>
                            </tr>
                          </tbody>
                        </table>                    
                    </div>
                </div>
            </div>
        </div>

    </section>
    `,
    filters: {
        currencyDecimal(value) {

            return (+value).toFixed(2) + " €"
        },
        toDate(value) {

            return new Date(value * 1000).toJSON().slice(0, 10).split('-').reverse().join('-')
        },
        toUpperCase(value) {

            return value.toUpperCase()
        },
        toEuros(value) {

            return NUMBER_FORMATTER.format(value);
        }
    },
    mounted() {

        let retriever = new Retriever();

        retriever.tradeBalance()
            .then(response => this.balance = response.data.result)
            .then(l => retriever.deposits())
            .then(deposits => this.deposits = deposits)
            .then(l => retriever.withdraws())
            .then(withdraws => this.withdraws = withdraws)
            .then(l => retriever.tradesHistory())
            .then(tradesHistory => this.tradesHistory = tradesHistory)
            .then(l => retriever.tickers(Object.keys(this.tradesHistory)))
            .then(console.log)
            .catch(error => {
                console.log(error)
                this.errored = true
            })
            .finally(() => this.loading = false)
    }
})

new Vue({el: '#app'})

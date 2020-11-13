import Vue from 'vue';
import Composer from './logic/Composer'

const NUMBER_FORMATTER = new Intl.NumberFormat("es-ES", {style: "currency", currency: "EUR"})

Vue.component('trade-balance', {
    data() {
        return {
            balance: null,
            deposits: null,
            withdraws: null,
            tradesHistory: null,
            gainsLosses: null,
            loading: true,
            errored: false
        }
    },
    computed: {
        gainsLossesObject() {

            return {
                'text-success': this.gainsLosses > 0,
                'text-danger': this.gainsLosses < 0
            }
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
                <div class="card-header"><b>Balance</b></div>
                <div class="card-body text-dark">
                    <table class="table">
                      <tbody>
                        <tr>
                          <th scope="row">Total deposited:</th>
                          <td>{{ deposits.total | toEuros }}</td>
                        </tr>
                        <tr>
                          <th scope="row">Total withdrawn:</th>
                          <td>{{ withdraws.total | toEuros }}</td>
                        </tr>
                        <tr>
                          <th scope="row">Currently invested:</th>
                          <td>{{ deposits.total + withdraws.total | toEuros }}</td>
                        </tr>
                        <tr>
                          <th scope="row">Currently valued:</th>
                          <td>{{ balance.eb | toEuros }}</td>
                        </tr>
                        <tr>
                          <th scope="row">Gains/losses:</th>
                          <td v-bind:class="gainsLossesObject">{{ gainsLosses | toEuros }}</td>
                        </tr>
                      </tbody>
                    </table>
                </div>
            </div>
            <div v-for="(orders, crypto) in tradesHistory">
                <div class="row">
                  <div class="col-sm-6">
                    <div class="card">
                      <div class="card-body">
                        <h5 class="card-title"><b>{{ crypto }}</b></h5>
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
                            <tr v-for="order in orders.trades">
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
                  <div class="col-sm-6">
                    <div class="card">
                      <div class="card-body">
                        <h5 class="card-title"><b>{{ crypto }}</b> - Summary</h5>
                        <table class="table">
                            <tbody>
                                <tr>
                                    <td><b>Gross Profit</b>: {{ orders.sumUp.gainsLosses | toEuros }}</td>
                                </tr>
                                <tr>
                                    <td><b>Total Fees:</b> {{ orders.sumUp.totalFees | toEuros }}</td>
                                </tr>
                                <tr>
                                    <td><b>Profit after Fees:</b> {{ orders.sumUp.gainsLosses - orders.sumUp.totalFees | toEuros }}</td>
                                </tr>
                                <tr>
                                    <td><b>Actual Invested:</b> {{ orders.sumUp.moneySpent | toEuros }}</td>
                                </tr>
                                <tr>
                                    <td><b>Volume:</b> {{ orders.sumUp.currentAmount }}</td>
                                </tr>
                                <tr>
                                    <td><b>Average Price:</b> {{ orders.sumUp.averagePrice }}</td>
                                </tr>
                                <tr>
                                    <td><b>Actual Price:</b> {{ orders.sumUp.actualPrice }}</td>
                                </tr>
                                <tr>
                                    <td><b>Actual Value:</b> {{ orders.sumUp.currentValue | toEuros }}</td>
                                </tr>
                                <tr>
                                    <td><b>Actual Gains/Losses:</b> <span v-bind:class="{ 'text-success': orders.sumUp.gainLoss > 0, 'text-danger': orders.sumUp.gainLoss <= 0 }">{{ orders.sumUp.gainLoss | toEuros }}</span></td>
                                </tr>
                                <tr>
                                    <td><b>Actual Gains/Losses Percentage:</b> <span v-bind:class="{ 'text-success': orders.sumUp.gainLossPercentage > 0, 'text-danger': orders.sumUp.gainLossPercentage <= 0 }">{{ orders.sumUp.gainLossPercentage | toPercentage }}</span></td>
                                </tr>
                            </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </div>
                <br />
            </div>
        </div>

    </section>
    `,
    filters: {
        toDate(value) {

            return new Date(value * 1000).toJSON().slice(0, 10).split('-').reverse().join('-')
        },
        toUpperCase(value) {

            return value.toUpperCase()
        },
        toEuros(value) {

            return NUMBER_FORMATTER.format(value);
        },
        toPercentage(value) {

            return (+value).toFixed(2) + " %"
        }
    },
    mounted() {

        let composer = new Composer();

        composer.compose()
            .then(sumUp => {
                this.tradesHistory = sumUp.trades
                this.balance = sumUp.balance
                this.deposits = sumUp.deposits
                this.withdraws = sumUp.withdraws
                this.gainsLosses = sumUp.gainsLosses
            })
            .catch(error => {
                console.log(error)
                this.errored = true
            })
            .finally(() => this.loading = false)
    }
})

new Vue({el: '#app'})

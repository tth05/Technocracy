package net.cydhra.technocracy.foundation.conduits.query

import net.cydhra.technocracy.foundation.conduits.transit.TransitSink

/**
 * An interface for a strategy for routing through a network. The implementor is ought to be stateless, or, if a an
 * implementor might use multiple instances using different state for configuration, it should not store state about
 * the network and the routes it handles. All route-specific state shall be stored in the [NetworkQuery] instances
 * generated by the strategy.
 */
interface RoutingStrategy<T : NetworkQuery> {

    /**
     * Query the network from a providing transit sink. The strategy calculates route information and return them in
     * form of a query, that can be used for routing as long as it is valid.
     *
     * @param providerSink the provider of output that shall be routed through the conduit network
     *
     * @return a [NetworkQuery] that stores state about the route. It is used by [provideTargetSink] to determine
     * routing targets
     *
     * @see [provideTargetSink]
     */
    fun query(providerSink: TransitSink): T

    /**
     * Depending on strategy and configuration a routing target can change between transfers, so a [NetworkQuery]
     * should ask the strategy for the target to use each time.
     *
     * @param route the [NetworkQuery] that stores all state of the current routing
     *
     * @return a transit sink where to route the output to
     */
    fun provideTargetSink(route: T): TransitSink
}
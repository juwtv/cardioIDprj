package com.example.testescomunicacao.communication

class CommunicationManager(
    //private val aidlStrategy: AidlCommunicationStrategy,
    //private val boundServiceStrategy: BoundServiceCommunicationStrategy,
    private val broadcastReceiverStrategy: BroadcastReceiverCommunicationStrategy,
    //private val contentProviderStrategy: ContentProviderCommunicationStrategy,
    //private val intentStrategy: IntentCommunicationStrategy,
    //private val socketStrategy: SocketCommunicationStrategy
) : CommunicationStrategy {

    override fun sendHeartRate(value: String, strategyType: String) {
        when (strategyType) {
            //CommunicationStrategy.AIDL -> aidlStrategy.sendHeartRate(value, CommunicationStrategy.AIDL)
            //CommunicationStrategy.BOUND_SERVICE -> boundServiceStrategy.sendHeartRate(value, CommunicationStrategy.BOUND_SERVICE)
            CommunicationStrategy.BROADCAST_RECEIVER -> broadcastReceiverStrategy.sendHeartRate(value, CommunicationStrategy.BROADCAST_RECEIVER)
            //CommunicationStrategy.CONTENT_PROVIDER -> contentProviderStrategy.sendHeartRate(value, CommunicationStrategy.CONTENT_PROVIDER)
            //CommunicationStrategy.INTENT -> intentStrategy.sendHeartRate(value, CommunicationStrategy.INTENT)
            //CommunicationStrategy.SOCKET -> socketStrategy.sendHeartRate(value, CommunicationStrategy.SOCKET)
            else -> throw IllegalArgumentException("Invalid strategy type")
        }
    }

    override fun sendHandsOn(handsOn: Int, strategyType: String) {
        when (strategyType) {
            //CommunicationStrategy.AIDL -> aidlStrategy.sendHandsOn(handsOn, CommunicationStrategy.AIDL)
            //CommunicationStrategy.BOUND_SERVICE -> boundServiceStrategy.sendHandsOn(handsOn, CommunicationStrategy.BOUND_SERVICE)
            CommunicationStrategy.BROADCAST_RECEIVER -> broadcastReceiverStrategy.sendHandsOn(handsOn, CommunicationStrategy.BROADCAST_RECEIVER)
            //CommunicationStrategy.CONTENT_PROVIDER -> contentProviderStrategy.sendHandsOn(handsOn, CommunicationStrategy.CONTENT_PROVIDER)
            //CommunicationStrategy.INTENT -> intentStrategy.sendHandsOn(handsOn, CommunicationStrategy.INTENT)
            //CommunicationStrategy.SOCKET -> socketStrategy.sendHandsOn(handsOn, CommunicationStrategy.SOCKET)
            else -> throw IllegalArgumentException("Invalid strategy type")
        }
    }
}

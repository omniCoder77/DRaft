package com.ethyllium.state

/**
 * Represents a collection of machines in a distributed system. This is static and does not change during runtime.
 * @property machineAddresses List of addresses of the machines.
 */
class Machine private constructor(val machineAddresses: List<String>){

    companion object {

        private var instance: Machine? = null

        /**
         * Factory method to create a Machine instance.
         * @param machineAddresses List of addresses of the machines.
         * @return A new Machine instance.
         */
        fun create(machineAddresses: List<String>): Machine {
            if (instance != null) {
                throw IllegalStateException("Machine instance already created.")
            }
            instance = Machine(machineAddresses)
            return instance!!
        }

        /**
         * Gets the singleton Machine instance.
         * @return The Machine instance.
         * @throws IllegalStateException if the instance has not been created yet.
         */
        fun getInstance(): Machine {
            if (instance == null) {
                throw IllegalStateException("Machine instance not initialized. Call create() first.")
            }
            return instance!!
        }
    }

    /**
     * Gets the size of the cluster.
     * @return The number of machines in the cluster.
     */
    fun clusterSize(): Int = machineAddresses.size

    /**
     * Calculates the quorum size required for consensus.
     * @return The number of machines required for a quorum.
     */
    fun quorumSize(): Int = (machineAddresses.size / 2) + 1
}
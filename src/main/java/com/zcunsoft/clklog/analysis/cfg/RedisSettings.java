package com.zcunsoft.clklog.analysis.cfg;

import java.util.List;

/**
 * Redis配置.
 */
public class RedisSettings {

    /**
     * Database index used by the connection factory.
     */
    private int database = 0;

    /**
     * Redis server host.
     */
    private String host = "localhost";

    /**
     * Login password of the redis server.
     */
    private String password;

    /**
     * Redis server port.
     */
    private int port = 6379;

    /**
     * Connection timeout in milliseconds.
     */
    private int timeout;

    /**
     * The pool.
     */
    private Pool pool;

    /**
     * The sentinel.
     */
    private Sentinel sentinel;

    /**
     * The cluster.
     */
    private Cluster cluster;

    /**
     * Gets the database.
     *
     * @return the database
     */
    public int getDatabase() {
        return this.database;
    }

    /**
     * Sets the database.
     *
     * @param database the new database
     */
    public void setDatabase(int database) {
        this.database = database;
    }

    /**
     * Gets the host.
     *
     * @return the host
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Sets the host.
     *
     * @param host the new host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Sets the port.
     *
     * @param port the new port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets the timeout.
     *
     * @param timeout the new timeout
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets the timeout.
     *
     * @return the timeout
     */
    public int getTimeout() {
        return this.timeout;
    }

    /**
     * Gets the sentinel.
     *
     * @return the sentinel
     */
    public Sentinel getSentinel() {
        return this.sentinel;
    }

    /**
     * Sets the sentinel.
     *
     * @param sentinel the new sentinel
     */
    public void setSentinel(Sentinel sentinel) {
        this.sentinel = sentinel;
    }

    /**
     * Gets the pool.
     *
     * @return the pool
     */
    public Pool getPool() {
        return this.pool;
    }

    /**
     * Sets the pool.
     *
     * @param pool the new pool
     */
    public void setPool(Pool pool) {
        this.pool = pool;
    }

    /**
     * Gets the cluster.
     *
     * @return the cluster
     */
    public Cluster getCluster() {
        return this.cluster;
    }

    /**
     * Sets the cluster.
     *
     * @param cluster the new cluster
     */
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    /**
     * Pool properties.
     */
    public static class Pool {

        /**
         * Max number of "idle" connections in the pool. Use a negative value to
         * indicate an unlimited number of idle connections.
         */
        private int maxIdle = 3;

        /**
         * Target for the minimum number of idle connections to maintain in the pool.
         * This setting only has an effect if it is positive.
         */
        private int minIdle = 0;

        /**
         * Max number of connections that can be allocated by the pool at a given time.
         * Use a negative value for no limit.
         */
        private int maxActive = 3;

        /**
         * Maximum amount of time (in milliseconds) a connection allocation should block
         * before throwing an exception when the pool is exhausted. Use a negative value
         * to block indefinitely.
         */
        private int maxWait = -1;

        /**
         * Gets the max idle.
         *
         * @return the max idle
         */
        public int getMaxIdle() {
            return this.maxIdle;
        }

        /**
         * Sets the max idle.
         *
         * @param maxIdle the new max idle
         */
        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        /**
         * Gets the min idle.
         *
         * @return the min idle
         */
        public int getMinIdle() {
            return this.minIdle;
        }

        /**
         * Sets the min idle.
         *
         * @param minIdle the new min idle
         */
        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        /**
         * Gets the max active.
         *
         * @return the max active
         */
        public int getMaxActive() {
            return this.maxActive;
        }

        /**
         * Sets the max active.
         *
         * @param maxActive the new max active
         */
        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
        }

        /**
         * Gets the max wait.
         *
         * @return the max wait
         */
        public int getMaxWait() {
            return this.maxWait;
        }

        /**
         * Sets the max wait.
         *
         * @param maxWait the new max wait
         */
        public void setMaxWait(int maxWait) {
            this.maxWait = maxWait;
        }

    }

    /**
     * Cluster properties.
     */
    public static class Cluster {

        /**
         * Comma-separated list of "host:port" pairs to bootstrap from. This represents
         * an "initial" list of cluster nodes and is required to have at least one
         * entry.
         */
        private List<String> nodes;

        /**
         * Maximum number of redirects to follow when executing commands across the
         * cluster.
         */
        private Integer maxRedirects;

        /**
         * Gets the nodes.
         *
         * @return the nodes
         */
        public List<String> getNodes() {
            return this.nodes;
        }

        /**
         * Sets the nodes.
         *
         * @param nodes the new nodes
         */
        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

        /**
         * Gets the max redirects.
         *
         * @return the max redirects
         */
        public Integer getMaxRedirects() {
            return this.maxRedirects;
        }

        /**
         * Sets the max redirects.
         *
         * @param maxRedirects the new max redirects
         */
        public void setMaxRedirects(Integer maxRedirects) {
            this.maxRedirects = maxRedirects;
        }

    }

    /**
     * Redis sentinel properties.
     */
    public static class Sentinel {

        /**
         * Name of Redis server.
         */
        private String master;

        /**
         * Comma-separated list of host:port pairs.
         */
        private String nodes;

        /**
         * Gets the master.
         *
         * @return the master
         */
        public String getMaster() {
            return this.master;
        }

        /**
         * Sets the master.
         *
         * @param master the new master
         */
        public void setMaster(String master) {
            this.master = master;
        }

        /**
         * Gets the nodes.
         *
         * @return the nodes
         */
        public String getNodes() {
            return this.nodes;
        }

        /**
         * Sets the nodes.
         *
         * @param nodes the new nodes
         */
        public void setNodes(String nodes) {
            this.nodes = nodes;
        }

    }
}
